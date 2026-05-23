package com.example.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class Repository(
    private val employeeDao: EmployeeDao,
    private val attendanceDao: AttendanceDao,
    private val leaveRequestDao: LeaveRequestDao,
    private val meetingDao: MeetingDao,
    private val chatMessageDao: ChatMessageDao,
    private val appNotificationDao: AppNotificationDao
) {
    val activeEmployees: Flow<List<Employee>> = employeeDao.getAllActiveEmployees()
    val allEmployees: Flow<List<Employee>> = employeeDao.getAllEmployees()
    val allAttendance: Flow<List<AttendanceRecord>> = attendanceDao.getAllAttendance()
    val allLeaveRequests: Flow<List<LeaveRequest>> = leaveRequestDao.getAllLeaveRequests()
    val allMeetings: Flow<List<Meeting>> = meetingDao.getAllMeetings()

    val allChatMessages: Flow<List<ChatMessage>> = chatMessageDao.getAllMessages()

    fun getNotificationsForUser(employeeId: String): Flow<List<AppNotification>> =
        appNotificationDao.getNotificationsForUser(employeeId)

    fun getLeaveRequestsForEmployee(employeeId: String): Flow<List<LeaveRequest>> =
        leaveRequestDao.getLeaveRequestsForEmployee(employeeId)

    fun getMeetingsByDate(date: String): Flow<List<Meeting>> =
        meetingDao.getMeetingsByDate(date)

    suspend fun getEmployeeCount(): Int = employeeDao.getEmployeeCount()

    suspend fun insertEmployee(employee: Employee) = employeeDao.insertEmployee(employee)
    suspend fun updateEmployee(employee: Employee) = employeeDao.updateEmployee(employee)
    suspend fun deactivateEmployee(employeeId: String) = employeeDao.deactivateEmployee(employeeId)
    suspend fun clearAllEmployees() = employeeDao.deleteAllEmployees()

    suspend fun insertMeeting(meeting: Meeting) = meetingDao.insertMeeting(meeting)
    suspend fun deleteMeeting(meetingId: Int) = meetingDao.deleteMeeting(meetingId)
    suspend fun clearAllMeetings() = meetingDao.deleteAllMeetings()

    suspend fun insertChatMessage(message: ChatMessage) = chatMessageDao.insertMessage(message)
    suspend fun clearChatHistory() = chatMessageDao.clearAllMessages()

    suspend fun insertNotification(notification: AppNotification) = appNotificationDao.insertNotification(notification)
    suspend fun markNotificationsRead(employeeId: String) = appNotificationDao.markAllAsRead(employeeId)

    suspend fun insertLeaveRequest(request: LeaveRequest) {
        leaveRequestDao.insertLeaveRequest(request)
        
        // When a leave request is submitted, we should automatically update or update upon approval
        // If it's eventually approved, we update the attendance record of that date.
        // Let's make an emergency apply leave immediately update scheduling!
    }

    suspend fun updateLeaveStatus(id: Int, status: String, date: String, employeeId: String) {
        leaveRequestDao.updateLeaveStatus(id, status)
        
        if (status == "APPROVED") {
            // Apply this leave to the attendance record for that date
            applyEmployeeLeaveToAttendance(date, employeeId)
        }
    }

    // High fidelity deterministic on-the-fly dynamic generator for empty date cells!
    suspend fun getOrCreateAttendanceForDate(dateStr: String): AttendanceRecord {
        val existing = attendanceDao.getAttendanceByDate(dateStr)
        if (existing != null) {
            return existing
        }

        // Generate deterministically based on date's hash or epoch days
        val records = generateDeterministicRecord(dateStr)
        attendanceDao.insertAttendance(records)
        return records
    }

    suspend fun forceUpdateAttendance(record: AttendanceRecord) {
        attendanceDao.insertAttendance(record)
    }

    suspend fun clearAllAttendance() = attendanceDao.deleteAllAttendance()

    suspend fun removeAttendanceForDate(date: String) {
        attendanceDao.deleteAttendanceByDate(date)
    }

    suspend fun assignSuperAdmin(employeeId: String, isSuper: Boolean) {
        val emps = employeeDao.getAllEmployees().first()
        val emp = emps.find { it.id == employeeId } ?: return
        employeeDao.updateEmployee(emp.copy(isSuperAdmin = isSuper))
    }

    private suspend fun applyEmployeeLeaveToAttendance(dateStr: String, employeeId: String) {
        val record = getOrCreateAttendanceForDate(dateStr)
        val empName = employeeId.replaceFirstChar { it.uppercase() }
        
        // Remove from whichever chair they occupied
        val newP1 = if (record.p1.equals(empName, ignoreCase = true)) null else record.p1
        val newP2 = if (record.p2.equals(empName, ignoreCase = true)) null else record.p2
        val newP3 = if (record.p3.equals(empName, ignoreCase = true)) null else record.p3
        
        // Add to absent list
        val currentAbsents = record.absent?.split(",")?.map { it.trim() }?.toMutableList() ?: mutableListOf()
        if (!currentAbsents.contains(empName)) {
            currentAbsents.add(empName)
        }
        val newAbsent = currentAbsents.filter { it.isNotBlank() }.joinToString(",")

        val updated = record.copy(
            p1 = newP1,
            p2 = newP2,
            p3 = newP3,
            absent = newAbsent,
            note = "$empName Emergency Leave"
        )
        attendanceDao.insertAttendance(updated)
    }

    private fun generateDeterministicRecord(dateStr: String): AttendanceRecord {
        // Try parsing the date or fallback to hash code
        val dateHash = Math.abs(dateStr.hashCode())
        val weekdays = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        
        var dayOfWeek = "Monday"
        try {
            val dateObj = SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateStr)
            if (dateObj != null) {
                dayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(dateObj)
            }
        } catch (e: Exception) {
            dayOfWeek = weekdays[dateHash % 7]
        }

        // Active Developers: Sam, Vaishu, Loki
        val devs = listOf("Sam", "Vaishu", "Loki")
        // Active Designers: Aathi, Naomi
        val designers = listOf("Aathi", "Naomi")

        // Determine who works from home today
        // Developer stay at home index: dateHash % 3
        val devHomeIndex = dateHash % 3
        val assignedDevs = devs.filterIndexed { index, _ -> index != devHomeIndex }

        // Designer stay at home index: dateHash % 2
        val designerHomeIndex = dateHash % 2
        val assignedDesigner = designers[1 - designerHomeIndex]
        val homeDesigner = designers[designerHomeIndex]
        val homeDev = devs[devHomeIndex]

        return AttendanceRecord(
            date = dateStr,
            dayOfWeek = dayOfWeek,
            p1 = assignedDevs.getOrNull(0),
            p2 = assignedDevs.getOrNull(1),
            p3 = assignedDesigner,
            absent = null,
            note = null
        )
    }
}
