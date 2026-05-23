package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SkillskapesViewModel(private val repository: Repository) : ViewModel() {

    private val _isInitializing = MutableStateFlow(true)
    val isInitializing: StateFlow<Boolean> = _isInitializing.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                // DB is populated by AppDatabaseCallback onOpen/onCreate
                // We ensure it's finished by checking count
                var count = repository.getEmployeeCount()
                if (count == 0) {
                    SeedData.INITIAL_EMPLOYEES.forEach { emp -> repository.insertEmployee(emp) }
                    SeedData.INITIAL_MEETINGS.forEach { meeting -> repository.insertMeeting(meeting) }
                    SeedData.parseSeedAttendance().forEach { record -> repository.forceUpdateAttendance(record) }
                }
                // Auto-sync local verified data to Firestore
                syncDataToFirebaseFirestore()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isInitializing.value = false
            }
        }
    }

    // Authentication State
    private val _currentUser = MutableStateFlow<Employee?>(null)
    val currentUser: StateFlow<Employee?> = _currentUser.asStateFlow()

    // Firestore Sync State
    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _selectedDate = MutableStateFlow(getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Flow that triggers when selected date changes, fetching or generating that record
    val selectedDayRecord: StateFlow<AttendanceRecord?> = _selectedDate
        .flatMapLatest { dateStr ->
            flow {
                emit(repository.getOrCreateAttendanceForDate(dateStr))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeEmployees: StateFlow<List<Employee>> = repository.activeEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEmployees: StateFlow<List<Employee>> = repository.allEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaveRequests: StateFlow<List<LeaveRequest>> = repository.allLeaveRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val meetings: StateFlow<List<Meeting>> = repository.allMeetings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendanceRecords: StateFlow<List<AttendanceRecord>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessage>> = repository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userNotifications: StateFlow<List<AppNotification>> = _currentUser
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(emptyList())
            } else {
                repository.getNotificationsForUser(user.id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamically calculate statistical records for all registered individuals!
    val employeeStatistics: StateFlow<Map<String, EmployeeStats>> = combine(
        repository.allAttendance,
        allEmployees
    ) { records, employees ->
        val statsMap = mutableMapOf<String, EmployeeStats>()
        
        // Initialize stats for each known employee
        employees.forEach { emp ->
            statsMap[emp.id] = EmployeeStats(0, 0, 0)
        }

        records.forEach { record ->
            val p1 = record.p1?.lowercase()?.trim() ?: ""
            val p2 = record.p2?.lowercase()?.trim() ?: ""
            val p3 = record.p3?.lowercase()?.trim() ?: ""
            val absents = record.absent?.lowercase()?.split(",")?.map { it.trim() } ?: emptyList()

            employees.forEach { emp ->
                val empId = emp.id.lowercase().trim()
                val empName = emp.name.lowercase().trim()
                
                // Matches either ID or exact display name
                val isPresent = (p1 == empId || p2 == empId || p3 == empId) ||
                                (p1 == empName || p2 == empName || p3 == empName)

                val isAbsent = absents.contains(empId) || 
                               absents.contains(empName) ||
                               (record.note?.lowercase()?.contains(empId) == true) ||
                               (record.note?.lowercase()?.contains(empName) == true)

                val current = statsMap[emp.id] ?: EmployeeStats(0, 0, 0)
                if (isPresent) {
                    statsMap[emp.id] = current.copy(inOfficeDays = current.inOfficeDays + 1)
                } else if (isAbsent) {
                    statsMap[emp.id] = current.copy(absentDays = current.absentDays + 1)
                } else {
                    statsMap[emp.id] = current.copy(wfhDays = current.wfhDays + 1)
                }
            }
        }
        statsMap
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun removeAttendanceForDate(date: String) {
        viewModelScope.launch {
            repository.removeAttendanceForDate(date)
            syncDataToFirebaseFirestore()
        }
    }

    fun assignSuperAdmin(id: String, isSuper: Boolean) {
        viewModelScope.launch {
            repository.assignSuperAdmin(id, isSuper)
            syncDataToFirebaseFirestore()
        }
    }

    fun generateNextWeekPlan() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val calendar = Calendar.getInstance()
            
            // Start from the latest date in the database or today
            val allAttendance = repository.allAttendance.first()
            val latestRecord = allAttendance.maxByOrNull { 
                try { sdf.parse(it.date)?.time ?: 0L } catch(e: Exception) { 0L }
            }
            
            val startDate = latestRecord?.let { 
                try { sdf.parse(it.date) } catch(e: Exception) { null }
            } ?: calendar.time

            calendar.time = startDate

            // Generate next 7 days in database, skipping Sundays
            for (i in 1..7) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    continue // Skip all Sundays
                }
                val targetDateStr = sdf.format(calendar.time)
                repository.getOrCreateAttendanceForDate(targetDateStr)
            }

            // Put a dynamic alert
            val bossLabel = _currentUser.value?.name ?: "Superadmin"
            repository.insertNotification(
                AppNotification(
                    employeeId = "all",
                    title = "New Timetable Released 📅",
                    message = "The next rotation block has been scheduled by $bossLabel. Check your assigned seats!"
                )
            )
            syncDataToFirebaseFirestore()
        }
    }
    fun login(emailParam: String, passwordParam: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val trimmedEmail = emailParam.trim()
            // Query repository directly to avoid waiting for StateFlow propagation
            val allEmps = repository.allEmployees.first()
            val user = allEmps.find { 
                it.email.equals(trimmedEmail, ignoreCase = true) || it.id.equals(trimmedEmail, ignoreCase = true)
            }
            if (user == null) {
                onError("No user account matching credentials found.")
            } else if (user.password == passwordParam) {
                _currentUser.value = user
                onSuccess()
                syncDataToFirebaseFirestore() // Auto-sync on login
            } else {
                onError("Incorrect password entered. Try again.")
            }
        }
    }

    fun register(name: String, email: String, passwordStr: String, role: String, avatar: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            if (name.isBlank() || email.isBlank() || passwordStr.isBlank()) {
                onError("Please fill out all credential inputs.")
                return@launch
            }
            val emailExists = allEmployees.value.any { it.email.equals(email.trim(), ignoreCase = true) }
            if (emailExists) {
                onError("Email domain is already registered.")
                return@launch
            }
            val cleanId = name.trim().lowercase().replace("\\s+".toRegex(), "_")
            val employee = Employee(
                id = cleanId,
                name = name.trim(),
                role = role, // "Developer", "Designer", or "Boss"
                email = email.trim(),
                isActive = true,
                password = passwordStr,
                avatarUri = avatar
            )
            repository.insertEmployee(employee)
            _currentUser.value = employee
            
            // Welcome notification
            repository.insertNotification(
                AppNotification(
                    employeeId = cleanId,
                    title = "Welcome to Skillskapes! 🎨",
                    message = "Let's check upcoming rotating timetables or message the team in communication board!"
                )
            )
            onSuccess()
            syncDataToFirebaseFirestore() // Auto-sync on registration
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateProfile(name: String, email: String, avatar: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val updated = user.copy(
                name = name.trim(),
                email = email.trim(),
                avatarUri = avatar
            )
            repository.insertEmployee(updated)
            _currentUser.value = updated
            syncDataToFirebaseFirestore() // Auto-sync on profile update
        }
    }

    // Invite new employees via email
    fun inviteEmployee(name: String, email: String, role: String) {
        viewModelScope.launch {
            val cleanId = name.trim().lowercase().replace("\\s+".toRegex(), "_")
            val invited = Employee(
                id = cleanId,
                name = name.trim(),
                role = role,
                email = email.trim(),
                isActive = true,
                password = "password123", // initial default password
                avatarUri = "av_logo_" + (1..6).random()
            )
            repository.insertEmployee(invited)

            // Dynamic system broadcast notification
            repository.insertNotification(
                AppNotification(
                    employeeId = "all",
                    title = "New Team Invitee! 🚀",
                    message = "${name.trim()} has been invited as '$role'. Credentials pre-populated with password: password123."
                )
            )
            syncDataToFirebaseFirestore() // Auto-sync on invitation
        }
    }

    // --- Communication Chat Messaging ---
    fun sendChatMessage(receiverId: String?, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val msg = ChatMessage(
                senderId = user.id,
                senderName = user.name,
                senderRole = user.role,
                receiverId = receiverId,
                messageText = text.trim()
            )
            repository.insertChatMessage(msg)
        }
    }

    fun clearChatMessages() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }

    // --- Notifications Feed Actions ---
    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.markNotificationsRead(user.id)
        }
    }

    // --- Attendance & Seating Management ---
    fun updateSeating(p1: String?, p2: String?, p3: String?) {
        viewModelScope.launch {
            val currentDate = _selectedDate.value
            val existing = selectedDayRecord.value
            val dayOfWeek = existing?.dayOfWeek ?: "Monday"
            val absents = existing?.absent
            val notes = existing?.note

            val updatedRecord = AttendanceRecord(
                date = currentDate,
                dayOfWeek = dayOfWeek,
                p1 = if (p1.isNullOrBlank() || p1 == "Vacant") null else p1,
                p2 = if (p2.isNullOrBlank() || p2 == "Vacant") null else p2,
                p3 = if (p3.isNullOrBlank() || p3 == "Vacant") null else p3,
                absent = absents,
                note = notes
            )
            repository.forceUpdateAttendance(updatedRecord)

            // Alert notification to employees
            val bossLabel = _currentUser.value?.name ?: "Boss"
            repository.insertNotification(
                AppNotification(
                    employeeId = "all",
                    title = "Timetable Changed ⚠️",
                    message = "Active workspace assignments for $currentDate were changed by Boss $bossLabel."
                )
            )
            syncDataToFirebaseFirestore()
        }
    }


    fun applyLeave(date: String, reason: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val leaveRequest = LeaveRequest(
                employeeId = user.id,
                employeeName = user.name,
                date = date,
                reason = reason,
                status = "PENDING"
            )
            repository.insertLeaveRequest(leaveRequest)

            // Alert Bosses
            repository.insertNotification(
                AppNotification(
                    employeeId = "all", // broad request
                    title = "New Leave Submitted 📝",
                    message = "${user.name} requested leave leave on $date. Approval pending."
                )
            )
        }
    }

    fun approveLeave(requestId: Int, status: String, date: String, employeeId: String) {
        viewModelScope.launch {
            repository.updateLeaveStatus(requestId, status, date, employeeId)

            // Target personal notification to user
            val bossLabel = _currentUser.value?.name ?: "Boss"
            repository.insertNotification(
                AppNotification(
                    employeeId = employeeId,
                    title = "Leave Request $status! ✅",
                    message = "Your leave request for $date has been evaluated as $status by Boss $bossLabel."
                )
            )
        }
    }

    fun scheduleMeeting(title: String, date: String, time: String, gmeetLink: String, notes: String?) {
        viewModelScope.launch {
            val creator = _currentUser.value?.name ?: "Boss"
            val meeting = Meeting(
                title = title,
                date = date,
                time = time,
                gmeetLink = gmeetLink,
                createdBy = "Boss $creator",
                notes = notes
            )
            repository.insertMeeting(meeting)

            // Broad broadcast alert
            repository.insertNotification(
                AppNotification(
                    employeeId = "all",
                    title = "New Meeting Synced 🎬",
                    message = "Sync meeting '$title' is scheduled on $date at $time by $creator. Link: $gmeetLink."
                )
            )
        }
    }

    fun deleteMeeting(meetingId: Int) {
        viewModelScope.launch {
            repository.deleteMeeting(meetingId)
        }
    }

    fun removeEmployee(id: String) {
        viewModelScope.launch {
            repository.deactivateEmployee(id)
        }
    }

    // --- Firebase Cloud Firestore Sync Interface ---
    fun syncDataToFirebaseFirestore(
        emps: List<Employee>? = null,
        attendance: List<AttendanceRecord>? = null,
        meets: List<Meeting>? = null,
        chats: List<ChatMessage>? = null
    ) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                
                // Use provided data or fetch current snapshot from repository flows
                val finalEmps = emps ?: repository.allEmployees.first()
                val finalAttendance = attendance ?: repository.allAttendance.first()
                val finalMeets = meets ?: repository.allMeetings.first()
                val finalChats = chats ?: repository.allChatMessages.first()
                
                // 1. Sync Employees
                finalEmps.forEach { emp ->
                    val empMap = mapOf(
                        "id" to emp.id,
                        "name" to emp.name,
                        "role" to emp.role,
                        "email" to emp.email,
                        "isActive" to emp.isActive,
                        "password" to emp.password,
                        "avatarUri" to (emp.avatarUri ?: "av_logo_1")
                    )
                    db.collection("employees").document(emp.id).set(empMap)
                }
                
                // 2. Sync Attendance (Timetable collection as requested)
                finalAttendance.forEach { record ->
                    val safeDateId = record.date.replace("/", "-")
                    val recMap = mapOf(
                        "date" to record.date,
                        "day" to record.dayOfWeek,
                        "p1" to (record.p1 ?: "Vacant"),
                        "p2" to (record.p2 ?: "Vacant"),
                        "p3" to (record.p3 ?: "Vacant"),
                        "absent" to (record.absent ?: "None")
                    )
                    db.collection("timetable").document(safeDateId).set(recMap)
                }
                
                // 3. Sync Meetings
                finalMeets.forEach { meeting ->
                    val docId = if (meeting.id == 0) UUID.randomUUID().toString() else meeting.id.toString()
                    val meetMap = mapOf(
                        "id" to meeting.id,
                        "title" to meeting.title,
                        "date" to meeting.date,
                        "time" to meeting.time,
                        "gmeetLink" to meeting.gmeetLink,
                        "createdBy" to meeting.createdBy,
                        "notes" to (meeting.notes ?: "")
                    )
                    db.collection("meetings").document(docId).set(meetMap)
                }
                
                // 4. Sync Chat Messages
                finalChats.forEach { message ->
                    val docId = if (message.id == 0) UUID.randomUUID().toString() else message.id.toString()
                    val msgMap = mapOf(
                        "id" to message.id,
                        "senderId" to message.senderId,
                        "senderName" to message.senderName,
                        "senderRole" to message.senderRole,
                        "receiverId" to (message.receiverId ?: ""),
                        "messageText" to message.messageText,
                        "timestamp" to message.timestamp
                    )
                    db.collection("chat_messages").document(docId).set(msgMap)
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetSyncState() {
        _syncState.value = SyncState("IDLE", 0f, "")
    }

    fun getTodayDateString(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date())
    }
}

data class EmployeeStats(
    val inOfficeDays: Int,
    val wfhDays: Int,
    val absentDays: Int
) {
    val totalWorkingDays: Int get() = inOfficeDays + wfhDays
    val attendanceRate: Float get() = if (totalWorkingDays == 0) 0f else (inOfficeDays.toFloat() / totalWorkingDays) * 100
}

data class SyncState(
    val status: String = "IDLE", // "IDLE", "SYNCING", "SUCCESS", "ERROR"
    val progress: Float = 0f,
    val details: String = ""
)

class SkillskapesViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SkillskapesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SkillskapesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
