package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

val ColorPrimary = Color(0xFF6366F1) // Indigo 600 Accenting
val ColorGold = Color(0xFFF59E0B) // Gold accent for Boss profiles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillskapesApp(viewModel: SkillskapesViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    if (currentUser == null) {
        // Auth gate
        LoginRegisterScreen(viewModel)
    } else {
        // Authenticated workspace
        MainAppWorkspace(viewModel, currentUser!!)
    }
}

// --- SECURE AUTH SEGMENT (LOGIN & REGISTRATION) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRegisterScreen(viewModel: SkillskapesViewModel) {
    val context = LocalContext.current
    var isSignUp by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Developer") }
    var selectedAvatar by remember { mutableStateOf("av_logo_1") }
    var showPassword by remember { mutableStateOf(false) }

    val presetAvatars = listOf(
        "av_logo_1" to "🤖 Dev Core",
        "av_logo_2" to "🎨 UI Unicorn",
        "av_logo_3" to "⚡ Swift Coder",
        "av_logo_4" to "💅 Pixel Craft",
        "av_boss_1" to "👑 Tech Boss",
        "av_boss_2" to "💎 Lead Guru",
        "av_logo_sam" to "🚀 Sam Joshua"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Branding
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ColorPrimary.copy(alpha = 0.15f))
                        .border(1.5.dp, ColorPrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Logo",
                        tint = ColorPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Skillskapes",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "TEAM COORDINATION & SCHEDULING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Tabs toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!isSignUp) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { isSignUp = false }
                        .testTag("signin_tab_trigger"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSignUp) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { isSignUp = true }
                        .testTag("signup_tab_trigger"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Register", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Input fields Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isSignUp) {
                        // Full Name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Display Name") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Role Selection Row
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Desired Work Role", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOf("Developer", "Designer", "Boss").forEach { role ->
                                    val isSelected = selectedRole == role
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) ColorPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                            .border(1.dp, if (isSelected) ColorPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                                            .clickable { selectedRole = role }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = role,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Avatar Presets grid
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Select Avatar Halo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(presetAvatars) { (avId, desc) ->
                                    val isSelected = selectedAvatar == avId
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isSelected) ColorPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                                            .border(2.dp, if (isSelected) ColorPrimary else Color.Transparent, RoundedCornerShape(16.dp))
                                            .clickable { selectedAvatar = avId }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(ColorPrimary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(desc.take(2), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(desc.substring(3), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Email Address
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email address or Username") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
                    )

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password Input") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Info else Icons.Default.Lock,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
                    )

                    // Error trigger / Action Button
                    Button(
                        onClick = {
                            if (isSignUp) {
                                viewModel.register(
                                    name = name,
                                    email = email,
                                    passwordStr = password,
                                    role = selectedRole,
                                    avatar = selectedAvatar,
                                    onSuccess = {
                                        Toast.makeText(context, "Welcome aboard, $name!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, "Error: $err", Toast.LENGTH_LONG).show()
                                    }
                                )
                            } else {
                                viewModel.login(
                                    emailParam = email,
                                    passwordParam = password,
                                    onSuccess = {
                                        Toast.makeText(context, "Successfully logged in!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, "Error: $err", Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_submit_button")
                    ) {
                        Text(
                            text = if (isSignUp) "Register & Join Company" else "Sign In Securely",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Quick Tap Assist Panel
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Demo Fast Credentials Autofills",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimary
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    email = "priyanka@skillskapes.com"
                                    password = "priyanka123"
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                colors = variantButtonColorsDefault(isBoss = true),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text("Priyanka (Boss)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    email = "jojo@skillskapes.com"
                                    password = "jojo123"
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                colors = variantButtonColorsDefault(isBoss = true),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text("Jojo (Boss)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                email = "samjoshua.skillskapes@gmail.com"
                                password = "joshua123"
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth().height(36.dp).testTag("select_sam_login_demo")
                        ) {
                            Text("Sam Joshua (Employee Developer)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun variantButtonColorsDefault(isBoss: Boolean): ButtonColors {
    return if (isBoss) {
        ButtonDefaults.buttonColors(
            containerColor = ColorGold.copy(alpha = 0.15f),
            contentColor = ColorGold
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// --- SECURE AUTHENTICATED WORKSPACE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppWorkspace(viewModel: SkillskapesViewModel, user: Employee) {
    val context = LocalContext.current
    val isBoss = user.role == "Boss"

    var currentTab by remember { mutableStateOf(if (isBoss) "home" else "portal") }

    // State Collection
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedRecord by viewModel.selectedDayRecord.collectAsStateWithLifecycle()
    val activeEmployees by viewModel.activeEmployees.collectAsStateWithLifecycle()
    val leaveRequests by viewModel.leaveRequests.collectAsStateWithLifecycle()
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val statsMap by viewModel.employeeStatistics.collectAsStateWithLifecycle()
    val notifications by viewModel.userNotifications.collectAsStateWithLifecycle()

    var showMeetingDialog by remember { mutableStateOf(false) }
    var showSeatingEditDialog by remember { mutableStateOf(false) }

    val unreadNotificationsCount = remember(notifications) {
        notifications.count { !it.isRead }
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Skillskapes",
                                color = if (isBoss) ColorGold else ColorPrimary,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (isBoss) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ColorGold.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("CEO HUB", color = ColorGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = "HELLO, ${user.name.uppercase()}",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Avatar component
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (unreadNotificationsCount > 0 && !isBoss) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(StateAbsent)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "$unreadNotificationsCount alert",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Circular image preview
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isBoss) ColorGold else ColorPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (user.name.length >= 2) user.name.take(2).uppercase() else user.name.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                if (isBoss) {
                    // BOSS Tabs: Home (Floor), Leaves requests, Stats directory, DMs, Settings (invite, name edit)
                    NavigationBarItem(
                        selected = currentTab == "home",
                        onClick = { currentTab = "home" },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Floor", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "leaves",
                        onClick = { currentTab = "leaves" },
                        icon = { Icon(Icons.Default.DateRange, null) },
                        label = { Text("Leaves", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "stats",
                        onClick = { currentTab = "stats" },
                        icon = { Icon(Icons.Default.Star, null) },
                        label = { Text("Stats", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "chat",
                        onClick = { currentTab = "chat" },
                        icon = { Icon(Icons.Default.Email, null) },
                        label = { Text("Chat", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "settings",
                        onClick = { currentTab = "settings" },
                        icon = { Icon(Icons.Default.Settings, null) },
                        label = { Text("Settings", fontSize = 10.sp) }
                    )
                } else {
                    // EMPLOYEE Tabs
                    NavigationBarItem(
                        selected = currentTab == "portal",
                        onClick = { 
                            currentTab = "portal"
                            viewModel.markAllNotificationsAsRead()
                        },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Dashboard", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "leaves_employee",
                        onClick = { currentTab = "leaves_employee" },
                        icon = { Icon(Icons.Default.DateRange, null) },
                        label = { Text("My Leaves", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "chat",
                        onClick = { currentTab = "chat" },
                        icon = { Icon(Icons.Default.Email, null) },
                        label = { Text("Chat Board", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "settings",
                        onClick = { currentTab = "settings" },
                        icon = { Icon(Icons.Default.Person, null) },
                        label = { Text("My Settings", fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Determine active container view depending on current tab selection
            when (currentTab) {
                "portal" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            EmployeePortalView(viewModel, user, statsMap, notifications)
                        }
                    }
                }

                "leaves_employee" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            EmployeeLeavePlannerView(viewModel, user, leaveRequests)
                        }
                    }
                }

                "home" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            BossFloorDashboard(viewModel, selectedRecord, selectedDate, showMeetingDialog) {
                                showMeetingDialog = true
                            }
                        }

                        item {
                            MeetingScheduleHeader()
                        }

                        if (meetings.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No meetings currently scheduled for this sprint.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                            }
                        } else {
                            items(meetings) { meeting ->
                                MeetingCard(meeting) {
                                    viewModel.deleteMeeting(meeting.id)
                                    Toast.makeText(context, "Meeting deleted!", Toast.LENGTH_SHORT).show()
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }

                "leaves" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("Pending Employee Leave Requests", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        val pendingLeaves = leaveRequests.filter { it.status == "PENDING" }
                        if (pendingLeaves.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("All leave requests processed! No pending items.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(pendingLeaves) { req ->
                                LeaveApprovalCard(
                                    request = req,
                                    onApprove = {
                                        viewModel.approveLeave(req.id, "APPROVED", req.date, req.employeeId)
                                        Toast.makeText(context, "Request for ${req.employeeName} approved!", Toast.LENGTH_SHORT).show()
                                    },
                                    onReject = {
                                        viewModel.approveLeave(req.id, "REJECTED", req.date, req.employeeId)
                                        Toast.makeText(context, "Request for ${req.employeeName} denied.", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }

                "stats" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Workspace Coordination Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("In-Office seat capacity vs Work-From-Home logs.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }

                        items(activeEmployees) { emp ->
                            val empStat = statsMap[emp.id] ?: EmployeeStats(0, 0, 0)
                            StatsBarCard(employee = emp, stats = empStat)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                "chat" -> {
                    // Universal Group/Direct DMs should be statically sized to take full screen height and remain anchored
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        TeamChatView(viewModel, user, activeEmployees)
                    }
                }

                "settings" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            SystemSettingsView(viewModel, user, activeEmployees)
                        }
                    }
                }
            }
        }
    }

    // --- POPUP DIALOGS ---
    if (showMeetingDialog) {
        var meetTitle by remember { mutableStateOf("") }
        var meetDate by remember { mutableStateOf(selectedDate) }
        var meetTime by remember { mutableStateOf("10:15 AM") }
        var meetLink by remember { mutableStateOf("https://meet.google.com/abc-defg-hij") }
        var meetNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showMeetingDialog = false },
            title = { Text("Schedule Team Meeting") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = meetTitle,
                        onValueChange = { meetTitle = it },
                        label = { Text("Meeting Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = meetDate,
                        onValueChange = { meetDate = it },
                        label = { Text("Scheduled Date (dd/MM/yyyy)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = meetTime,
                        onValueChange = { meetTime = it },
                        label = { Text("Sprint Time slotted") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = meetLink,
                        onValueChange = { meetLink = it },
                        label = { Text("GMeet URL Link") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = meetNotes,
                        onValueChange = { meetNotes = it },
                        label = { Text("Agenda / Description notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (meetTitle.isNotBlank()) {
                            viewModel.scheduleMeeting(meetTitle, meetDate, meetTime, meetLink, meetNotes)
                            showMeetingDialog = false
                            Toast.makeText(context, "New team meeting scheduled!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) { Text("Commit Meeting") }
            },
            dismissButton = {
                TextButton(onClick = { showMeetingDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Seating Edit dialog
    if (showSeatingEditDialog) {
        selectedRecord?.let { record ->
            val devNames = activeEmployees.filter { it.role == "Developer" }.map { it.name }
            val desNames = activeEmployees.filter { it.role == "Designer" }.map { it.name }

            var selectedP1 by remember { mutableStateOf(record.p1 ?: "Vacant") }
            var selectedP2 by remember { mutableStateOf(record.p2 ?: "Vacant") }
            var selectedP3 by remember { mutableStateOf(record.p3 ?: "Vacant") }

            var expandedP1 by remember { mutableStateOf(false) }
            var expandedP2 by remember { mutableStateOf(false) }
            var expandedP3 by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showSeatingEditDialog = false },
                title = { Text("Assign Seats for $selectedDate") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            "Manually alter daily seat coordination. Max rule: 2 Developers + 1 Designer.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        // Seat 1
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Seat 1 (Developer)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Box {
                                OutlinedButton(
                                    onClick = { expandedP1 = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(selectedP1)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                                DropdownMenu(
                                    expanded = expandedP1,
                                    onDismissRequest = { expandedP1 = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Vacant") },
                                        onClick = {
                                            selectedP1 = "Vacant"
                                            expandedP1 = false
                                        }
                                    )
                                    devNames.forEach { name ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                selectedP1 = name
                                                expandedP1 = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Seat 2
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Seat 2 (Developer)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Box {
                                OutlinedButton(
                                    onClick = { expandedP2 = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(selectedP2)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                                DropdownMenu(
                                    expanded = expandedP2,
                                    onDismissRequest = { expandedP2 = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Vacant") },
                                        onClick = {
                                            selectedP2 = "Vacant"
                                            expandedP2 = false
                                        }
                                    )
                                    devNames.forEach { name ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                selectedP2 = name
                                                expandedP2 = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Seat 3
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Seat 3 (Designer)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Box {
                                OutlinedButton(
                                    onClick = { expandedP3 = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(selectedP3)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                                DropdownMenu(
                                    expanded = expandedP3,
                                    onDismissRequest = { expandedP3 = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Vacant") },
                                        onClick = {
                                            selectedP3 = "Vacant"
                                            expandedP3 = false
                                        }
                                    )
                                    desNames.forEach { name ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                selectedP3 = name
                                                expandedP3 = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateSeating(
                                p1 = if (selectedP1 == "Vacant") null else selectedP1,
                                p2 = if (selectedP2 == "Vacant") null else selectedP2,
                                p3 = if (selectedP3 == "Vacant") null else selectedP3
                            )
                            showSeatingEditDialog = false
                            Toast.makeText(context, "Attendance seating updated in database!", Toast.LENGTH_SHORT).show()
                        }
                    ) { Text("Save Seating Plan") }
                },
                dismissButton = {
                    TextButton(onClick = { showSeatingEditDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

// --- SUB-VIEW: INDIVIDUAL EMPLOYEE PORTAL ---
@Composable
fun EmployeePortalView(
    viewModel: SkillskapesViewModel,
    user: Employee,
    statsMap: Map<String, EmployeeStats>,
    notifications: List<AppNotification>
) {
    val myStats = remember(statsMap, user.id) {
        statsMap[user.id] ?: EmployeeStats(24, 18, 2) // realistic placeholders if empty
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Welcome and Overview
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ColorPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = ColorPrimary)
            }
            Column {
                Text(
                    text = "Welcome back, ${user.name}!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Role: ${user.role} | Active Workplace Node",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // Segment 1: COMPLETE ANALYTICS
        Text(
            text = "Personal Work Hours Analytics",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Came (Office)
            MetricPill(
                label = "IN-OFFICE DAYS",
                value = "${myStats.inOfficeDays} Days",
                color = StateInOffice,
                modifier = Modifier.weight(1f)
            )
            // WFH
            MetricPill(
                label = "WFH ROTATIONS",
                value = "${myStats.wfhDays} Days",
                color = StateWFH,
                modifier = Modifier.weight(1f)
            )
            // Leaves
            MetricPill(
                label = "DAYS OFF",
                value = "${myStats.absentDays} Days",
                color = StateAbsent,
                modifier = Modifier.weight(1f)
            )
        }

        // Attendance Percentage track
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Workspace Presence Rate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Comparison including leaves and WFH blocks", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                Text(
                    text = "${String.format(Locale.US, "%.1f", myStats.attendanceRate)}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = StateInOffice
                )
            }
        }

        // Segment 2: NEXT UPCOMING 7 DAYS SCHEDULE (FORECAST)
        Text(
            text = "Your Next rolling 7-Days Timetable Plan",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Generate next 7 days list dynamically
                val forecastList = remember {
                    val list = mutableListOf<String>()
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                    val cal = Calendar.getInstance()
                    for (i in 0..6) {
                        list.add(sdf.format(cal.time))
                        cal.add(Calendar.DAY_OF_YEAR, 1)
                    }
                    list
                }

                forecastList.forEach { dateStr ->
                    val dayName = remember(dateStr) {
                        try {
                            val d = SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateStr)
                            SimpleDateFormat("EEEE", Locale.US).format(d)
                        } catch (e: Exception) {
                            "Day"
                        }
                    }

                    // Seating check
                    val statusText = remember(dateStr, user.name) {
                        // Deterministic seating generator for previewing
                        val dateHash = Math.abs(dateStr.hashCode())
                        val devs = listOf("Sam", "Vaishu", "Loki", "Sam Joshua")
                        val devHomeIndex = dateHash % devs.size
                        
                        val nameStr = user.name.lowercase().trim()
                        if (nameStr.contains("sam joshua") || nameStr.contains("joshua")) {
                            if (devHomeIndex == 3) "🏡 Work From Home (WFH)" else "🪑 Slotted Chair (Seat ${if (devHomeIndex % 2 == 0) "Seat 1" else "Seat 2"})"
                        } else if (nameStr.contains("sam")) {
                            if (devHomeIndex == 0) "🏡 Work From Home (WFH)" else "🪑 Slotted Chair (Seat 1)"
                        } else if (nameStr.contains("vaishu")) {
                            if (devHomeIndex == 1) "🏡 Work From Home (WFH)" else "🪑 Slotted Chair (Seat 2)"
                        } else if (nameStr.contains("loki")) {
                            if (devHomeIndex == 2) "🏡 Work From Home (WFH)" else "🪑 Slotted Chair (Seat 1)"
                        } else {
                            if (dateHash % 2 == 0) "🪑 Slotted Chair 3 (Designer)" else "🏡 Work from Home (WFH)"
                        }
                    }

                    val isOfficeText = statusText.contains("Seat")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(dateStr, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(dayName, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isOfficeText) StateInOffice.copy(alpha = 0.08f) else StateWFH.copy(alpha = 0.08f))
                                .border(1.dp, if (isOfficeText) StateInOffice.copy(alpha = 0.15f) else StateWFH.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOfficeText) StateInOffice else StateWFH
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.06f))
                }
            }
        }

        // Segment 3: LIVE NOTIFICATION FEED & SYSTEM ALERTS
        Text(
            text = "Live Coordination Alert Tray",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No alerts currently received. Enjoy your sprint!",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                notifications.take(5).forEach { notif ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = notif.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorPrimary
                                )
                                Text(
                                    text = SimpleDateFormat("HH:mm", Locale.US).format(Date(notif.timestamp)),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notif.message,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-VIEW: PERSONAL EMPLOYEE LEAVE REQUESTS ---
@Composable
fun EmployeeLeavePlannerView(viewModel: SkillskapesViewModel, user: Employee, list: List<LeaveRequest>) {
    val context = LocalContext.current
    var leaveDate by remember { mutableStateOf("") }
    var leaveReason by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Request Time Off", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Submitting leaves notifies management and automatically vacates office chairs.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = leaveDate,
                    onValueChange = { leaveDate = it },
                    label = { Text("Target Leave Date (dd/MM/yyyy)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("leave_date_input")
                )
                OutlinedTextField(
                    value = leaveReason,
                    onValueChange = { leaveReason = it },
                    label = { Text("Reason for Absency") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("leave_reason_input")
                )
                Button(
                    onClick = {
                        if (leaveDate.isNotBlank() && leaveReason.isNotBlank()) {
                            viewModel.applyLeave(leaveDate, leaveReason)
                            leaveDate = ""
                            leaveReason = ""
                            Toast.makeText(context, "Leave request submitted to bosses!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please fill leave date & reason fields.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("submit_leave_button")
                ) {
                    Text("Submit Absence Request", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Text("Your Leave Request Desk", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        val myLeaves = list.filter { it.employeeId == user.id }
        if (myLeaves.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No Leave Requests booked yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        } else {
            myLeaves.forEach { leave ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(leave.date, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Reason: \"${leave.reason}\"", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        
                        val badgeColor = when (leave.status) {
                            "APPROVED" -> StateInOffice
                            "REJECTED" -> StateAbsent
                            else -> ColorGold
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(badgeColor.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                leave.status,
                                color = badgeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-VIEW: INTUITIVE GROUP CHAT & PRIVATE DMs ---
@Composable
fun TeamChatView(viewModel: SkillskapesViewModel, user: Employee, employees: List<Employee>) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    var isGroupChat by remember { mutableStateOf(true) }
    var selectedDMUserId by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }

    val dmTargetUser = remember(selectedDMUserId, employees) {
        employees.find { it.id == selectedDMUserId }
    }

    // Filtered messages
    val visibleMessages = remember(chatMessages, isGroupChat, selectedDMUserId, user.id) {
        if (isGroupChat) {
            chatMessages.filter { it.receiverId == null }
        } else {
            chatMessages.filter {
                (it.senderId == user.id && it.receiverId == selectedDMUserId) ||
                (it.senderId == selectedDMUserId && it.receiverId == user.id)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Toggle Room Selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isGroupChat) ColorPrimary else Color.Transparent)
                    .clickable { isGroupChat = true },
                contentAlignment = Alignment.Center
            ) {
                Text("Universal Group Chat", color = if (isGroupChat) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (!isGroupChat) ColorPrimary else Color.Transparent)
                    .clickable { 
                        isGroupChat = false
                        // Set standard DM user if none is selected
                        if (selectedDMUserId.isEmpty()) {
                            val initialDm = employees.find { it.id != user.id }
                            if (initialDm != null) selectedDMUserId = initialDm.id
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Private Direct DM", color = if (!isGroupChat) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Dropdown to select DM user if in DM view
        if (!isGroupChat) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Chatting with:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(dmTargetUser?.name ?: "Pick Team Mate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp))
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        employees.filter { it.id != user.id }.forEach { other ->
                            DropdownMenuItem(
                                text = { Text("${other.name} (${other.role})", fontSize = 12.sp) },
                                onClick = {
                                    selectedDMUserId = other.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Chat conversation card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (visibleMessages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                        Text(
                            text = if (isGroupChat) "Team group chat room. Say hello! 👋" else "No direct messages exchanges yet.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(visibleMessages) { msg ->
                        val isMe = msg.senderId == user.id
                        val senderLabel = if (isMe) "You" else msg.senderName
                        val alignLeft = !isMe

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (alignLeft) Alignment.Start else Alignment.End
                        ) {
                            Row(
                                horizontalArrangement = if (alignLeft) Arrangement.Start else Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    text = senderLabel,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (msg.senderRole == "Boss") ColorGold else ColorPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "• ${msg.senderRole}",
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = if (alignLeft) 4.dp else 12.dp,
                                            bottomEnd = if (alignLeft) 12.dp else 4.dp
                                        )
                                    )
                                    .background(
                                        if (isMe) {
                                            ColorPrimary.copy(alpha = 0.1f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (isMe) ColorPrimary.copy(alpha = 0.2f) else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = msg.messageText,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Send Text Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Write chat message...", fontSize = 12.sp) },
                shape = RoundedCornerShape(20.dp),
                textStyle = TextStyle(fontSize = 12.sp),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_text_input")
            )
            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendChatMessage(
                            receiverId = if (isGroupChat) null else selectedDMUserId,
                            text = messageText
                        )
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ColorPrimary)
                    .testTag("chat_send_button")
            ) {
                Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// --- SUB-VIEW: SYSTEM CONFIGURATION & COMPOSITION (SETTINGS) ---
@Composable
fun SystemSettingsView(viewModel: SkillskapesViewModel, user: Employee, employees: List<Employee>) {
    val context = LocalContext.current
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val isBoss = user.role == "Boss"

    var nameVal by remember { mutableStateOf(user.name) }
    var emailVal by remember { mutableStateOf(user.email) }

    // Employee Inviter values
    var invName by remember { mutableStateOf("") }
    var invEmail by remember { mutableStateOf("") }
    var invRole by remember { mutableStateOf("Developer") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Core Card 1: Profile customization edit
        Text("Your Profile Configuration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nameVal,
                    onValueChange = { nameVal = it },
                    label = { Text("Custom User Display Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
                )
                OutlinedTextField(
                    value = emailVal,
                    onValueChange = { emailVal = it },
                    label = { Text("Profile Work Email") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (nameVal.isNotBlank() && emailVal.isNotBlank()) {
                            viewModel.updateProfile(nameVal, emailVal, user.avatarUri ?: "av_logo_1")
                            Toast.makeText(context, "Profile credentials updated in database!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Text("Apply Profile Revisions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Core Card 2: INVITE NEW EMPLOYEES VIA MAIL
        if (isBoss) {
            Text("Invite New Member via Email Link", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "An active invite creates their in-office credentials instantly with default password 'password123'.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    OutlinedTextField(
                        value = invName,
                        onValueChange = { invName = it },
                        label = { Text("New Team Member Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("invite_name_input")
                    )

                    OutlinedTextField(
                        value = invEmail,
                        onValueChange = { invEmail = it },
                        label = { Text("New Team Member Email Address") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("invite_email_input")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Assign Work Role", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Developer", "Designer", "Boss").forEach { role ->
                                val isSelected = invRole == role
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ColorPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { invRole = role }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = role,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (invName.isNotBlank() && invEmail.isNotBlank()) {
                                viewModel.inviteEmployee(invName, invEmail, invRole)
                                Toast.makeText(context, "Employee $invName invited! Notifications broadcasts issued.", Toast.LENGTH_SHORT).show()
                                
                                // Reset
                                invName = ""
                                invEmail = ""
                            } else {
                                Toast.makeText(context, "Fill display name and email of invitee.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(40.dp).testTag("invite_submit_button")
                    ) {
                        Text("Active Invitation & Send Mail Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Connection detail Console with Cloud Firestore Sync Integration!
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Firebase Cloud Firestore Connector", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                Text(
                    "Initiate real-time cloud sync to automatically generate and populate structured collections in your Cloud Firestore database.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Local SQLite Schema", fontSize = 11.sp)
                    Text("v1 (AppDatabase.kt)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StateInOffice)
                }
                
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Firebase Project Space", fontSize = 11.sp)
                    Text("skillskapes.firebase.google", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorPrimary)
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Target collections tracker preview
                Text("Database Collections to Create:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val collections = listOf("employees", "attendance_records", "meetings", "chat_messages")
                    collections.forEach { col ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = col, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Indicator
                if (syncState.status == "SYNCING") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Uploading Cloud Data...", fontSize = 10.sp, color = ColorPrimary)
                            Text("${(syncState.progress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ColorPrimary)
                        }
                        LinearProgressIndicator(
                            progress = syncState.progress,
                            color = ColorPrimary,
                            trackColor = ColorPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth().clip(CircleShape).height(6.dp)
                        )
                    }
                }

                // Sync details log output
                if (syncState.details.isNotBlank()) {
                    val statusColor = when (syncState.status) {
                        "SUCCESS" -> StateInOffice
                        "ERROR" -> StateAbsent
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                    val bgCol = when (syncState.status) {
                        "SUCCESS" -> StateInOffice.copy(alpha = 0.08f)
                        "ERROR" -> StateAbsent.copy(alpha = 0.08f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgCol)
                            .border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = syncState.details,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = statusColor
                        )
                    }
                }

                // Trigger Button
                Button(
                    onClick = {
                        viewModel.syncDataToFirebaseFirestore()
                    },
                    enabled = syncState.status != "SYNCING",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (syncState.status == "SUCCESS") StateInOffice else ColorPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("firestore_sync_button")
                ) {
                    Icon(
                        if (syncState.status == "SUCCESS") Icons.Default.Done else Icons.Default.Refresh, 
                        null, 
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (syncState.status) {
                            "IDLE" -> "Sync now to Cloud Firestore"
                            "SYNCING" -> "Uploading Records..."
                            "SUCCESS" -> "Sync Completed Successfully!"
                            "ERROR" -> "Retry Cloud Firestore Sync"
                            else -> "Sync to Firestore"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (syncState.status == "SUCCESS" || syncState.status == "ERROR") {
                    TextButton(
                        onClick = { viewModel.resetSyncState() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Reset status panel", fontSize = 11.sp, color = ColorPrimary)
                    }
                }
            }
        }

        // Lougout action button
        Button(
            onClick = {
                viewModel.logout()
                Toast.makeText(context, "Safely logged out of profile.", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = StateAbsent.copy(alpha = 0.12f),
                contentColor = StateAbsent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("logout_button")
        ) {
            Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout & Switch Account", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

// --- CORE BOSS HOME SEGMENT VIEW (Attendance floors, calendars, planners) ---
@Composable
fun BossFloorDashboard(
    viewModel: SkillskapesViewModel,
    selectedRecord: AttendanceRecord?,
    selectedDate: String,
    showMeetingDialog: Boolean,
    onOpenMeetingCreator: () -> Unit
) {
    val context = LocalContext.current
    var isDatePickerVisible by remember { mutableStateOf(false) }
    var selectedP1 by remember { mutableStateOf("") }
    var selectedP2 by remember { mutableStateOf("") }
    var selectedP3 by remember { mutableStateOf("") }
    var showSeatingEditDialog by remember { mutableStateOf(false) }

    val occupiedCount = remember(selectedRecord) {
        listOfNotNull(selectedRecord?.p1, selectedRecord?.p2, selectedRecord?.p3).count { it.isNotBlank() && it != "Null" }
    }
    val badgeBg = if (occupiedCount == 3) Color(0xFFDCFCE7) else Color(0xFFFEF9C3)
    val badgeTextCol = if (occupiedCount == 3) Color(0xFF15803D) else Color(0xFF854D0E)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Workspace Seat Rotation plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Threshold constraint: 2 Developers + 1 Designer max",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$occupiedCount/3 OCCUPIED",
                        color = badgeTextCol,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Selector Wheel trigger
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Active Date: $selectedDate",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = { isDatePickerVisible = true },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier.height(28.dp).testTag("date_picker_trigger")
                ) {
                    Text("Select Day", fontSize = 10.sp)
                }
            }

            // Floor plan seating
            selectedRecord?.let { record ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chair 1
                    SeatCard(
                        chairLabel = "Seat 1 (Dev)",
                        occupantName = record.p1,
                        modifier = Modifier.weight(1f)
                    )
                    // Chair 2
                    SeatCard(
                        chairLabel = "Seat 2 (Dev)",
                        occupantName = record.p2,
                        modifier = Modifier.weight(1f)
                    )
                    // Chair 3
                    SeatCard(
                        chairLabel = "Seat 3 (Des)",
                        occupantName = record.p3,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Edit seat layout button
                Button(
                    onClick = { showSeatingEditDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .testTag("change_attendance_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Alter Daily Seating / Attendance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // WFH and Absent labels
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Active Absents", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(record.absent ?: "Nil", fontSize = 11.sp, color = StateAbsent)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Daily Work notes", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(record.note ?: "Deterministic Rotation schedule active", fontSize = 11.sp, maxLines = 1)
                    }
                }
            }

            // planner generator trigger button
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        viewModel.generateNextWeekPlan()
                        Toast.makeText(context, "Populated next 7 days seating arrangements in database!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary.copy(alpha = 0.1f), contentColor = ColorPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("generate_next_week_button")
                ) {
                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pre-populate next 7 days in Room", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOpenMeetingCreator,
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("schedule_meeting_trigger")
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Schedule New Team meeting", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (isDatePickerVisible) {
        var typedDate by remember { mutableStateOf(selectedDate) }
        AlertDialog(
            onDismissRequest = { isDatePickerVisible = false },
            title = { Text("Enter Target Coordination Date") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Format should be exact (dd/MM/yyyy). E.g., 23/05/2026", fontSize = 11.sp)
                    OutlinedTextField(
                        value = typedDate,
                        onValueChange = { typedDate = it },
                        label = { Text("dd/MM/yyyy") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (typedDate.contains("/")) {
                            viewModel.selectDate(typedDate.trim())
                            isDatePickerVisible = false
                        }
                    }
                ) { Text("Load Date") }
            },
            dismissButton = {
                TextButton(onClick = { isDatePickerVisible = false }) { Text("Cancel") }
            }
        )
    }

    if (showSeatingEditDialog) {
        selectedRecord?.let { record ->
            // Let boss alter
            val devNames = listOf("Sam", "Vaishu", "Loki", "Sam Joshua")
            val desNames = listOf("Aathi", "Naomi")

            var temp1 by remember { mutableStateOf(record.p1 ?: "Vacant") }
            var temp2 by remember { mutableStateOf(record.p2 ?: "Vacant") }
            var temp3 by remember { mutableStateOf(record.p3 ?: "Vacant") }

            AlertDialog(
                onDismissRequest = { showSeatingEditDialog = false },
                title = { Text("Alter seats plan: $selectedDate") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Seat 1 ocupant:")
                        OutlinedTextField(value = temp1, onValueChange = { temp1 = it }, modifier = Modifier.fillMaxWidth())

                        Text("Seat 2 ocupant:")
                        OutlinedTextField(value = temp2, onValueChange = { temp2 = it }, modifier = Modifier.fillMaxWidth())

                        Text("Seat 3 ocupant (Designer):")
                        OutlinedTextField(value = temp3, onValueChange = { temp3 = it }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.updateSeating(
                            if (temp1 == "Vacant") null else temp1,
                            if (temp2 == "Vacant") null else temp2,
                            if (temp3 == "Vacant") null else temp3
                        )
                        showSeatingEditDialog = false
                    }) { Text("Save changes") }
                }
            )
        }
    }
}

@Composable
fun MeetingScheduleHeader() {
    Column {
        Text(
            text = "Slotted Workspace Sync-ups",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Google Meet integrations with live approved attendance locks",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

// --- PRESERVED PREMIUM COMPONENT: GMeet Card ---
@Composable
fun MeetingCard(meeting: Meeting, onDelete: () -> Unit) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val containerBg = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.surface
    } else {
        primaryColor
    }
    val textColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else Color.White
    val subTextColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.8f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                        Text(
                            text = "GOOGLE MEET INTEGRATION",
                            color = subTextColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Delete Meeting",
                            tint = subTextColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = meeting.title,
                    color = textColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Time slot: ${meeting.time} • Synced by ${meeting.createdBy}",
                    color = subTextColor,
                    fontSize = 11.sp
                )

                if (!meeting.notes.isNullOrBlank()) {
                    Text(
                        text = "Agenda: ${meeting.notes}",
                        color = subTextColor,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meeting.gmeetLink))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No app available to handle GMeet URL", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else Color.White,
                        contentColor = if (isSystemInDarkTheme()) Color.White else primaryColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text(
                        text = "Launch Google Meet Room",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// --- PRESERVED PREMIUM COMPONENT: Leave Request Row for Approving ---
@Composable
fun LeaveApprovalCard(
    request: LeaveRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(request.employeeName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Requested Date: ${request.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StateAbsent.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("PENDING ACTIVE", color = StateAbsent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text("Reason: \"${request.reason}\"", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onReject, modifier = Modifier.testTag("reject_${request.id}")) {
                    Text("Deny", color = StateAbsent, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = StateInOffice),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("approve_${request.id}"),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Approve & Vacate Chair", fontSize = 11.sp)
                }
            }
        }
    }
}

// --- PRESERVED PREMIUM COMPONENT: SeatCard ---
@Composable
fun SeatCard(
    chairLabel: String,
    occupantName: String?,
    modifier: Modifier = Modifier
) {
    val isOccupied = occupantName != null && occupantName != "Null" && occupantName.isNotBlank()
    val bg = if (isOccupied) {
        if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else Color(0xFFF0F4F8)
    } else {
        if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f) else Color(0xFFF8FAFC)
    }
    val borderCol = if (isOccupied) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }
    
    Card(
        modifier = modifier.height(115.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderCol)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = chairLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            
            if (isOccupied) {
                val initials = remember(occupantName) {
                    val nameTrimmed = occupantName.trim()
                    if (nameTrimmed.length >= 2) {
                        nameTrimmed.substring(0, 2).uppercase()
                    } else {
                        nameTrimmed.take(1).uppercase()
                    }
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = occupantName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Vacant",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "Vacant Office",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

// --- PRESERVED PREMIUM COMPONENT: Stats progress bar card ---
@Composable
fun StatsBarCard(employee: Employee, stats: EmployeeStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(employee.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("${employee.role} • ${stats.inOfficeDays} Office Days", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${String.format(Locale.US, "%.0f", stats.attendanceRate)}% Presence",
                        color = StateInOffice,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text("${stats.wfhDays} WFH • ${stats.absentDays} Leaves", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }

            // indicator
            val total = (stats.inOfficeDays + stats.wfhDays + stats.absentDays).toFloat()
            if (total > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(maxOf(0.01f, stats.inOfficeDays.toFloat() / total))
                            .background(StateInOffice)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(maxOf(0.01f, stats.wfhDays.toFloat() / total))
                            .background(StateWFH)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(maxOf(0.01f, stats.absentDays.toFloat() / total))
                            .background(StateAbsent)
                    )
                }
            }
        }
    }
}

// Compact helper badge
@Composable
fun MetricPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = color)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
