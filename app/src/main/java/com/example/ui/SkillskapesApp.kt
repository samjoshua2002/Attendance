package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun SkillskapesApp(viewModel: SkillskapesViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isInitializing by viewModel.isInitializing.collectAsStateWithLifecycle()

    SkillskapesTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            if (isInitializing) {
                LoadingSplash()
            } else {
                Crossfade(targetState = currentUser, label = "ScreenTransition") { user ->
                    if (user == null) {
                        WelcomeOnboardingScreen(viewModel)
                    } else {
                        MainAppWorkspace(viewModel, user)
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingSplash() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Initializing Skillskapes...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun WelcomeOnboardingScreen(viewModel: SkillskapesViewModel) {
    var showAuth by remember { mutableStateOf(false) }

    if (showAuth) {
        LoginRegisterScreen(viewModel)
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            AbstractModernBackground()

            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(140.dp).clip(RoundedCornerShape(32.dp)).background(Color.White.copy(alpha = 0.9f)).padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Star, null, tint = IndigoPrimary, modifier = Modifier.size(80.dp))
                }
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    "Skillskapes",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    "Harmonizing Team Dynamics",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(60.dp))
                Button(
                    onClick = { showAuth = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = IndigoPrimary),
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Text("Get Started", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AbstractModernBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val radius by infiniteTransition.animateFloat(
        initialValue = 300f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(IndigoPrimary, IndigoDark)
            )
        )
        drawCircle(
            color = IndigoLight.copy(alpha = 0.2f),
            radius = radius,
            center = Offset(size.width * 0.8f, size.height * 0.2f)
        )
        drawCircle(
            color = Color.Cyan.copy(alpha = 0.1f),
            radius = radius * 1.5f,
            center = Offset(size.width * 0.2f, size.height * 0.9f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRegisterScreen(viewModel: SkillskapesViewModel) {
    val context = LocalContext.current
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                if (isSignUp) "Create Account" else "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                if (isSignUp) "Join the Skillskapes ecosystem" else "Sign in to continue your coordination",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(40.dp))

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (isSignUp) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Display Name") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(if (showPassword) Icons.Default.Info else Icons.Default.Lock, null)
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (isSignUp) {
                                viewModel.register(name, email, password, "Developer", "av_logo_1",
                                    { Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show() },
                                    { Toast.makeText(context, it, Toast.LENGTH_LONG).show() })
                            } else {
                                viewModel.login(email, password,
                                    { Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show() },
                                    { Toast.makeText(context, it, Toast.LENGTH_LONG).show() })
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text(if (isSignUp) "Register" else "Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(onClick = { isSignUp = !isSignUp }) {
                Text(
                    if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Register",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            QuickDemoPanel(onFill = { e, p -> email = e; password = p })
        }
    }
}

@Composable
fun QuickDemoPanel(onFill: (String, String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Demo Access", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { onFill("samjoshua.skillskapes@gmail.com", "password123") }, label = { Text("Sam (Admin)") })
            AssistChip(onClick = { onFill("ceo@skillskapes.com", "password123") }, label = { Text("Priyanka (Boss)") })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppWorkspace(viewModel: SkillskapesViewModel, user: Employee) {
    val isBoss = user.role == "Boss"
    val isSuper = user.isSuperAdmin
    var currentTab by remember { mutableStateOf(if (isBoss) "home" else "portal") }
    var selectedChatUserId by remember { mutableStateOf<String?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val activeEmployees by viewModel.activeEmployees.collectAsStateWithLifecycle()
    val trackingEmployees = remember(activeEmployees) { activeEmployees.filter { it.role != "Boss" } }
    val statsMap by viewModel.employeeStatistics.collectAsStateWithLifecycle()
    val notifications by viewModel.userNotifications.collectAsStateWithLifecycle()
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedRecord by viewModel.selectedDayRecord.collectAsStateWithLifecycle()
    
    var showMeetingDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("SKILLSKAPES", modifier = Modifier.padding(24.dp), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                NavigationDrawerItem(
                    label = { Text("My Dashboard") },
                    selected = currentTab == "portal" || currentTab == "home",
                    onClick = { currentTab = if (isBoss) "home" else "portal"; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Messenger") },
                    selected = currentTab == "chat",
                    onClick = { currentTab = "chat"; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.AutoMirrored.Filled.Send, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                if (isSuper) {
                    NavigationDrawerItem(
                        label = { Text("Superadmin Panel") },
                        selected = currentTab == "superadmin",
                        onClick = { currentTab = "superadmin"; scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Default.Settings, null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = { viewModel.logout(); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(currentTab.uppercase(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    NavigationBarItem(
                        selected = currentTab == (if(isBoss) "home" else "portal"),
                        onClick = { currentTab = if(isBoss) "home" else "portal" },
                        icon = { Icon(if (isBoss) Icons.Default.DateRange else Icons.Default.Person, null) },
                        label = { Text(if (isBoss) "Floor" else "Dashboard") }
                    )
                    NavigationBarItem(
                        selected = currentTab == "chat",
                        onClick = { currentTab = "chat" },
                        icon = { Icon(Icons.Default.Email, null) },
                        label = { Text("Chat") }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (currentTab) {
                    "portal" -> EmployeePortalView(viewModel, user, statsMap, notifications)
                    "home" -> BossFloorDashboard(viewModel, selectedRecord, selectedDate, showMeetingDialog, { showMeetingDialog = true }, meetings)
                    "chat" -> ChatCoordinator(viewModel, user, activeEmployees, selectedChatUserId) { selectedChatUserId = it }
                    "superadmin" -> SuperAdminView(viewModel, user, activeEmployees)
                    "profile" -> ProfileView(viewModel, user, trackingEmployees)
                }
            }
        }
    }
}

@Composable
fun EmployeePortalView(viewModel: SkillskapesViewModel, user: Employee, statsMap: Map<String, EmployeeStats>, notifications: List<AppNotification>) {
    val stats = statsMap[user.id] ?: EmployeeStats(0, 0, 0)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(user.name.take(1), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Welcome back,", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            Text(user.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }
        }

        item {
            Text("Productivity Pulse", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricPill("Office", "${stats.inOfficeDays}", StateInOffice, Modifier.weight(1f))
                MetricPill("WFH", "${stats.wfhDays}", StateWFH, Modifier.weight(1f))
                MetricPill("Away", "${stats.absentDays}", StateAbsent, Modifier.weight(1f))
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Attendance Rate", style = MaterialTheme.typography.labelLarge)
                        Text("Past 30 days", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Text("${stats.attendanceRate.toInt()}%", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item {
            Text("Upcoming Seating", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        items(7) { i ->
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, i)
            val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(calendar.time)
            val dayName = SimpleDateFormat("EEEE", Locale.US).format(calendar.time)
            val isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY

            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSunday) StateAbsent.copy(alpha = 0.1f) else MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(dayName.take(3).uppercase(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = if (isSunday) StateAbsent else MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dateStr, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text(if (isSunday) "Official Holiday" else "Assigned Work Node", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSunday) Color.Transparent else StateWFH.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(if (isSunday) "🏖️" else "🏡 WFH", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = if (isSunday) Color.Unspecified else StateWFH)
                    }
                }
            }
        }
    }
}

@Composable
fun BossFloorDashboard(viewModel: SkillskapesViewModel, record: AttendanceRecord?, date: String, show: Boolean, onReq: () -> Unit, meetings: List<Meeting>) {
    val activeEmployees by viewModel.activeEmployees.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column {
                Text("Office Layout", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                Text(date, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ACTIVE NODES", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SeatCard("Node 01", record?.p1, Modifier.weight(1f), activeEmployees) { name -> 
                            viewModel.updateSeating(name, record?.p2, record?.p3) 
                        }
                        SeatCard("Node 02", record?.p2, Modifier.weight(1f), activeEmployees) { name -> 
                            viewModel.updateSeating(record?.p1, name, record?.p3) 
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SeatCard("Creative Node", record?.p3, Modifier.fillMaxWidth(0.6f), activeEmployees) { name -> 
                        viewModel.updateSeating(record?.p1, record?.p2, name) 
                    }
                }
            }
        }

        item {
            Text("System Feed", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        if (meetings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No active system alerts", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(meetings) { meeting ->
                ListItem(
                    headlineContent = { Text(meeting.title, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("Scheduled by ${meeting.createdBy}") },
                    trailingContent = { Text(meeting.time, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface)
                )
            }
        }
    }
}

@Composable
fun SeatCard(label: String, person: String?, modifier: Modifier, employees: List<Employee>, onAssign: (String?) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    Card(
        modifier = modifier.height(120.dp).clickable { showMenu = true },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (person == null) MaterialTheme.colorScheme.surfaceVariant else IndigoPrimary, contentColor = if (person == null) MaterialTheme.colorScheme.onSurfaceVariant else Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(person ?: "Vacant", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text("Vacant") }, onClick = { onAssign(null); showMenu = false })
                employees.forEach { emp ->
                    DropdownMenuItem(text = { Text(emp.name) }, onClick = { onAssign(emp.name); showMenu = false })
                }
            }
        }
    }
}

@Composable
fun ChatCoordinator(viewModel: SkillskapesViewModel, currentUser: Employee, employees: List<Employee>, selectedUserId: String?, onUserSelected: (String?) -> Unit) {
    if (selectedUserId == null) {
        ChatListView(employees, currentUser, onUserSelected)
    } else {
        val target = employees.find { it.id == selectedUserId }
        if (target != null) {
            ChatDetailView(viewModel, currentUser, target) { onUserSelected(null) }
        } else {
            onUserSelected(null)
        }
    }
}

@Composable
fun ChatListView(employees: List<Employee>, currentUser: Employee, onSelect: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text("Messenger", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        items(employees.filter { it.id != currentUser.id }) { emp ->
            ListItem(
                headlineContent = { Text(emp.name, fontWeight = FontWeight.Bold) },
                supportingContent = { Text("Active in ${emp.role}") },
                leadingContent = {
                    Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                        Text(emp.name.take(1), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                },
                modifier = Modifier.clickable { onSelect(emp.id) }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailView(viewModel: SkillskapesViewModel, currentUser: Employee, targetUser: Employee, onBack: () -> Unit) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val chatMessages = messages.filter { (it.senderId == currentUser.id && it.receiverId == targetUser.id) || (it.senderId == targetUser.id && it.receiverId == currentUser.id) }
    var text by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { _ -> }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(targetUser.name, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(modifier = Modifier.padding(12.dp).navigationBarsPadding().imePadding(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { launcher.launch("*/*") }) { Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary) }
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Write a message...") },
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    )
                    IconButton(onClick = { if (text.isNotBlank()) { viewModel.sendChatMessage(targetUser.id, text); text = "" } }) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            items(chatMessages) { msg ->
                val isMe = msg.senderId == currentUser.id
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                    Surface(
                        color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isMe) 20.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 20.dp
                        )
                    ) {
                        Text(msg.messageText, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun SuperAdminView(viewModel: SkillskapesViewModel, user: Employee, employees: List<Employee>) {
    val timetable by viewModel.allAttendanceRecords.collectAsStateWithLifecycle()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { 
            Text("Admin Center", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold) 
        }

        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Timetable Master", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.generateNextWeekPlan() }, 
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer, contentColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(Icons.Default.DateRange, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Deploy Next Cycle")
                    }
                }
            }
        }

        item { 
            Text("Seating Ledger", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) 
        }

        items(timetable.sortedByDescending { it.date }) { record ->
            ListItem(
                headlineContent = { Text(record.date, fontWeight = FontWeight.Bold) },
                supportingContent = { Text(record.dayOfWeek) },
                trailingContent = { 
                    IconButton(onClick = { viewModel.removeAttendanceForDate(record.date) }) {
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface)
            )
        }

        item { 
            Text("User Authority", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) 
        }

        items(employees) { emp ->
            ListItem(
                headlineContent = { Text(emp.name, fontWeight = FontWeight.Bold) },
                supportingContent = { Text(emp.role) },
                trailingContent = { 
                    Switch(
                        checked = emp.isSuperAdmin, 
                        onCheckedChange = { viewModel.assignSuperAdmin(emp.id, it) },
                        enabled = emp.id != user.id
                    ) 
                }
            )
        }
    }
}

@Composable
fun ProfileView(viewModel: SkillskapesViewModel, user: Employee, employees: List<Employee>) {
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Account Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Card(shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth())
                Button(onClick = { viewModel.updateProfile(name, email, user.avatarUri ?: "av_logo_1"); Toast.makeText(context, "Profile Saved", Toast.LENGTH_SHORT).show() }, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Save Changes")
                }
            }
        }
    }
}

@Composable
fun MetricPill(label: String, value: String, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable fun MeetingScheduleHeader() {}
@Composable fun MeetingCard(m: Meeting, d: () -> Unit) {}
@Composable fun LeaveApprovalCard(r: LeaveRequest, a: () -> Unit, re: () -> Unit) {}
@Composable fun StatsBarCard(e: Employee, s: EmployeeStats) {}
