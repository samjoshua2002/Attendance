package com.example.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
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
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isInitializing) {
                LoadingSplash()
            } else {
                Crossfade(targetState = currentUser, label = "UserTransition") { user ->
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
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
    }
}

@Composable
fun WelcomeOnboardingScreen(viewModel: SkillskapesViewModel) {
    var showAuth by remember { mutableStateOf(false) }

    AnimatedContent(targetState = showAuth, label = "AuthFlow") { isAuth ->
        if (isAuth) {
            LoginRegisterScreen(viewModel)
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(40.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "SKILLSKAPES",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Minimal coordination system for modern teams.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Light,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(64.dp))
                Button(
                    onClick = { showAuth = true },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Text("GET STARTED", fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }
        }
    }
}

@Composable
fun MonoCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        content()
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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                if (isSignUp) "ACCOUNT" else "WELCOME",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                if (isSignUp) {
                    MonoTextField(value = name, onValueChange = { name = it }, label = "Full Name")
                }
                MonoTextField(value = email, onValueChange = { email = it }, label = "Email")
                MonoTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    isPassword = true,
                    showPassword = showPassword,
                    onTogglePassword = { showPassword = !showPassword }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (isSignUp) {
                            viewModel.register(name, email, password, "Developer", "av_logo_1", { }, { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
                        } else {
                            viewModel.login(email, password, { }, { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
                        }
                    },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("CONTINUE", fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }
            
            TextButton(
                onClick = { isSignUp = !isSignUp },
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(
                    if (isSignUp) "BACK TO LOGIN" else "CREATE ACCOUNT",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DemoChip("SAM") { email = "samjoshua.skillskapes@gmail.com"; password = "password123" }
                DemoChip("PRIYANKA") { email = "ceo@skillskapes.com"; password = "password123" }
            }
        }
    }
}

@Composable
fun DemoChip(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MonoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    Column {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            letterSpacing = 1.sp
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                { IconButton(onClick = { onTogglePassword?.invoke() }) {
                    Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                } }
            } else null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppWorkspace(viewModel: SkillskapesViewModel, user: Employee) {
    val isSuper = user.isSuperAdmin
    val isBoss = user.role == "Boss"
    var currentTab by remember { mutableStateOf(if (isBoss) "analytics" else if (isSuper) "home" else "portal") }
    var selectedChatUserId by remember { mutableStateOf<String?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val activeEmployees by viewModel.activeEmployees.collectAsStateWithLifecycle()
    val statsMap by viewModel.employeeStatistics.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedRecord by viewModel.selectedDayRecord.collectAsStateWithLifecycle()

    val tabs = remember(isSuper, isBoss) {
        val list = mutableListOf<String>()
        if (isSuper && !isBoss) list.add("home")
        list.add("portal")
        list.add("schedule")
        list.add("chat")
        if (isBoss) list.add("analytics")
        if (isSuper) list.add("superadmin")
        list
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface, drawerShape = RoundedCornerShape(0.dp)) {
                Text(
                    "SKILLSKAPES",
                    modifier = Modifier.padding(32.dp),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                tabs.forEach { tab ->
                    NavigationDrawerItem(
                        label = { Text(tab.uppercase(), fontWeight = FontWeight.Bold) },
                        selected = currentTab == tab,
                        onClick = { currentTab = tab; scope.launch { drawerState.close() } },
                        icon = { Icon(getIconForTab(tab), null) },
                        shape = RoundedCornerShape(0.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text("LOGOUT", fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = { viewModel.logout() },
                    shape = RoundedCornerShape(0.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(currentTab.uppercase(), fontWeight = FontWeight.Black, letterSpacing = 2.sp, style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, null) } },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = {
                MonoBottomNavBar(
                    currentTab = currentTab,
                    tabs = tabs.filter { it != "superadmin" },
                    onTabSelect = { currentTab = it }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (currentTab) {
                    "portal" -> EmployeePortalView(user, statsMap)
                    "home" -> BossFloorDashboard(viewModel, selectedRecord, selectedDate, activeEmployees, isSuper)
                    "schedule" -> WeeklyScheduleView(viewModel)
                    "chat" -> ChatCoordinator(viewModel, user, activeEmployees, selectedChatUserId) { selectedChatUserId = it }
                    "superadmin" -> SuperAdminView(viewModel, activeEmployees)
                    "analytics" -> AnalyticsDashboard(activeEmployees, statsMap, viewModel)
                }
            }
        }
    }
}

fun getIconForTab(tab: String): ImageVector = when(tab) {
    "home" -> Icons.Default.GridView
    "portal" -> Icons.Default.AccountCircle
    "schedule" -> Icons.Default.DateRange
    "chat" -> Icons.Default.ChatBubbleOutline
    "superadmin" -> Icons.Default.Security
    "analytics" -> Icons.Default.BarChart
    else -> Icons.Default.Star
}

@Composable
fun MonoBottomNavBar(currentTab: String, tabs: List<String>, onTabSelect: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(72.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = currentTab == tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onTabSelect(tab) }
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        getIconForTab(tab),
                        null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 4.dp else 0.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun EmployeePortalView(user: Employee, statsMap: Map<String, EmployeeStats>) {
    val stats = statsMap[user.id] ?: EmployeeStats(0, 0, 0)
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(32.dp), verticalArrangement = Arrangement.spacedBy(40.dp)) {
        item {
            Column {
                Text("HELLO,", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text(user.name.uppercase(), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                MonoMetric("OFFICE", "${stats.inOfficeDays}", Modifier.weight(1f))
                MonoMetric("WFH", "${stats.wfhDays}", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MonoMetric(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black)
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(MaterialTheme.colorScheme.primary))
    }
}

@Composable
fun BossFloorDashboard(viewModel: SkillskapesViewModel, record: AttendanceRecord?, date: String, employees: List<Employee>, isSuper: Boolean) {
    var showAbsentDialog by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
        item {
            Column {
                Text("FLOOR PLAN", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text(date, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SeatMonoNode("01", record?.p1, Modifier.weight(1f), employees, isSuper) { viewModel.updateSeating(date, it, record?.p2, record?.p3) }
                SeatMonoNode("02", record?.p2, Modifier.weight(1f), employees, isSuper) { viewModel.updateSeating(date, record?.p1, it, record?.p3) }
                SeatMonoNode("CR", record?.p3, Modifier.weight(1f), employees, isSuper) { viewModel.updateSeating(date, record?.p1, record?.p2, it) }
            }
        }
        if (isSuper) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { showAbsentDialog = true },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("ABSENTEES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.Gray)
                            Text(record?.absent ?: "NONE", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.Add, null)
                    }
                }
            }
        }
    }

    if (showAbsentDialog) {
        var absentText by remember { mutableStateOf(record?.absent ?: "") }
        AlertDialog(
            onDismissRequest = { showAbsentDialog = false },
            shape = RoundedCornerShape(0.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("MARK ABSENT", fontWeight = FontWeight.Black) },
            text = {
                OutlinedTextField(
                    value = absentText, onValueChange = { absentText = it },
                    label = { Text("Names") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                )
            },
            confirmButton = {
                Button(onClick = { viewModel.updateSeating(date, record?.p1, record?.p2, record?.p3, absentText); showAbsentDialog = false }, shape = RoundedCornerShape(0.dp)) {
                    Text("SAVE")
                }
            }
        )
    }
}

@Composable
fun SeatMonoNode(label: String, person: String?, modifier: Modifier, employees: List<Employee>, isSuper: Boolean, onAssign: (String?) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .border(2.dp, if (person != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                .clickable(enabled = isSuper) { showMenu = true },
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
            if (isSuper) {
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = { Text("VACANT") }, onClick = { onAssign(null); showMenu = false })
                    employees.forEach { emp ->
                        DropdownMenuItem(text = { Text(emp.name) }, onClick = { onAssign(emp.name); showMenu = false })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(person ?: "VACANT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun WeeklyScheduleView(viewModel: SkillskapesViewModel) {
    val timetable by viewModel.allAttendanceRecords.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val employees by viewModel.activeEmployees.collectAsStateWithLifecycle()
    val isSuper = user?.isSuperAdmin == true
    var searchQuery by remember { mutableStateOf("") }

    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.US) }
    val filteredTimetable = remember(timetable, searchQuery) {
        timetable.filter { record ->
            searchQuery.isBlank() || 
            record.date.contains(searchQuery) || 
            record.dayOfWeek.contains(searchQuery, ignoreCase = true) ||
            record.p1?.contains(searchQuery, ignoreCase = true) == true ||
            record.p2?.contains(searchQuery, ignoreCase = true) == true ||
            record.p3?.contains(searchQuery, ignoreCase = true) == true ||
            record.absent?.contains(searchQuery, ignoreCase = true) == true
        }.sortedByDescending { 
            try { sdf.parse(it.date)?.time ?: 0L } catch(e: Exception) { 0L }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        item { 
            Text("TIMETABLE", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
            Spacer(modifier = Modifier.height(24.dp))
            MonoSearchField(value = searchQuery, onValueChange = { searchQuery = it })
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        items(filteredTimetable) { record ->
            MonoScheduleItem(record, employees, isSuper, viewModel)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
        }
    }
}

@Composable
fun MonoSearchField(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("SEARCH...", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(0.dp)
    )
}

@Composable
fun MonoScheduleItem(record: AttendanceRecord, employees: List<Employee>, isSuper: Boolean, viewModel: SkillskapesViewModel) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(record.date, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                Text(record.dayOfWeek.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
            if (record.dayOfWeek == "Sunday") {
                Text("HOLIDAY", fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelSmall)
            } else if (isSuper) {
                IconButton(onClick = { viewModel.selectDate(record.date) }) {
                    Icon(Icons.Default.MoreVert, null)
                }
            }
        }
        if (record.dayOfWeek != "Sunday") {
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MonoSeatingPill(record.p1, "01", record.date, employees, isSuper, viewModel)
                MonoSeatingPill(record.p2, "02", record.date, employees, isSuper, viewModel)
                MonoSeatingPill(record.p3, "CR", record.date, employees, isSuper, viewModel)
            }
            if (!record.absent.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("ABSENT: ${record.absent.uppercase()}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.Gray)
            }
        }
    }
}

@Composable
fun MonoSeatingPill(name: String?, node: String, date: String, employees: List<Employee>, isSuper: Boolean, viewModel: SkillskapesViewModel) {
    var showMenu by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.clickable(enabled = isSuper) { showMenu = true },
        color = if (name != null) MaterialTheme.colorScheme.primary else Color.Transparent,
        border = if (name == null) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(node, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = if (name != null) MaterialTheme.colorScheme.onPrimary else Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(name?.uppercase() ?: "VACANT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (name != null) MaterialTheme.colorScheme.onPrimary else Color.Gray)
            
            if (isSuper) {
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = { Text("VACANT") }, onClick = { 
                        viewModel.allAttendanceRecords.value.find { it.date == date }?.let { rec ->
                            when(node) {
                                "01" -> viewModel.updateSeating(date, null, rec.p2, rec.p3)
                                "02" -> viewModel.updateSeating(date, rec.p1, null, rec.p3)
                                "CR" -> viewModel.updateSeating(date, rec.p1, rec.p2, null)
                            }
                        }
                        showMenu = false 
                    })
                    employees.forEach { emp ->
                        DropdownMenuItem(text = { Text(emp.name) }, onClick = { 
                            viewModel.allAttendanceRecords.value.find { it.date == date }?.let { rec ->
                                when(node) {
                                    "01" -> viewModel.updateSeating(date, emp.name, rec.p2, rec.p3)
                                    "02" -> viewModel.updateSeating(date, rec.p1, emp.name, rec.p3)
                                    "CR" -> viewModel.updateSeating(date, rec.p1, rec.p2, emp.name)
                                }
                            }
                            showMenu = false 
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ChatCoordinator(viewModel: SkillskapesViewModel, currentUser: Employee, employees: List<Employee>, selectedUserId: String?, onUserSelected: (String?) -> Unit) {
    if (selectedUserId == null) ChatListView(employees, currentUser, onUserSelected)
    else {
        val target = employees.find { it.id == selectedUserId }
        if (target != null) ChatDetailView(viewModel, currentUser, target) { onUserSelected(null) }
    }
}

@Composable
fun ChatListView(employees: List<Employee>, currentUser: Employee, onSelect: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp)) {
        item { 
            Text("MESSAGES", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
            Spacer(modifier = Modifier.height(32.dp))
        }
        items(employees.filter { it.id != currentUser.id }) { emp ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(emp.id) }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                    Text(emp.name.take(1).uppercase(), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(emp.name.uppercase(), fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyLarge)
                    Text(emp.role.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailView(viewModel: SkillskapesViewModel, currentUser: Employee, targetUser: Employee, onBack: () -> Unit) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val chatMessages = messages.filter { (it.senderId == currentUser.id && it.receiverId == targetUser.id) || (it.senderId == targetUser.id && it.receiverId == currentUser.id) }
    var text by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(targetUser.name.uppercase(), fontWeight = FontWeight.Black, letterSpacing = 1.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Surface(border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                Row(modifier = Modifier.padding(16.dp).navigationBarsPadding().imePadding(), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = text, onValueChange = { text = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("TYPE MESSAGE...", style = MaterialTheme.typography.labelSmall) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Button(
                        onClick = { if (text.isNotBlank()) { viewModel.sendChatMessage(targetUser.id, text); text = "" } },
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text("SEND")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            items(chatMessages) { msg ->
                val isMe = msg.senderId == currentUser.id
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    Surface(
                        color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(0.dp),
                        border = if (!isMe) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                    ) {
                        Text(
                            msg.messageText, 
                            modifier = Modifier.padding(16.dp), 
                            color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        if (isMe) "YOU" else targetUser.name.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 4.dp),
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun SuperAdminView(viewModel: SkillskapesViewModel, employees: List<Employee>) {
    val timetable by viewModel.allAttendanceRecords.collectAsStateWithLifecycle()
    var dateQuery by remember { mutableStateOf("") }
    var selectedEditRecord by remember { mutableStateOf<AttendanceRecord?>(null) }

    val filteredLedger = remember(timetable, dateQuery) {
        timetable.filter { it.date.contains(dateQuery) || it.dayOfWeek.contains(dateQuery, ignoreCase = true) }
            .sortedByDescending { 
                try { SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(it.date)?.time ?: 0L } catch(e: Exception) { 0L }
            }
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(32.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
        item { Text("ADMIN", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black) }
        
        item {
            Button(
                onClick = { viewModel.generateNextWeekPlan() }, 
                modifier = Modifier.fillMaxWidth().height(56.dp), 
                shape = RoundedCornerShape(0.dp)
            ) {
                Text("DEPLOY NEW CYCLE", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }
        }

        item {
            MonoSearchField(value = dateQuery, onValueChange = { dateQuery = it })
        }

        val chunkedLedger = filteredLedger.chunked(4)
        items(chunkedLedger) { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { record ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .clickable { selectedEditRecord = record },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(record.date.take(5), fontWeight = FontWeight.Black, fontSize = 12.sp)
                            Text(record.dayOfWeek.take(3).uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }
                repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }

        item { Text("TEAM AUTHORITY", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black) }
        items(employees) { emp ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(emp.name.uppercase(), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Switch(checked = emp.isSuperAdmin, onCheckedChange = { viewModel.assignSuperAdmin(emp.id, it) })
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
        }
    }

    if (selectedEditRecord != null) {
        AdminDateEditDialog(
            record = selectedEditRecord!!,
            employees = employees,
            onDismiss = { selectedEditRecord = null },
            onSave = { p1, p2, p3, abs ->
                viewModel.updateSeating(selectedEditRecord!!.date, p1, p2, p3, abs)
                selectedEditRecord = null
            },
            onDelete = {
                viewModel.removeAttendanceForDate(selectedEditRecord!!.date)
                selectedEditRecord = null
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDateEditDialog(
    record: AttendanceRecord,
    employees: List<Employee>,
    onDismiss: () -> Unit,
    onSave: (String?, String?, String?, String?) -> Unit,
    onDelete: () -> Unit
) {
    var p1 by remember { mutableStateOf(record.p1) }
    var p2 by remember { mutableStateOf(record.p2) }
    var p3 by remember { mutableStateOf(record.p3) }
    var absents by remember { mutableStateOf(record.absent?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()) }

    val officeMembers = listOfNotNull(p1, p2, p3)
    val availableForAbsent = employees.filter { it.role != "Boss" && !officeMembers.contains(it.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(record.date, fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AdminSeatPicker("NODE 01", p1, employees) { p1 = it }
                AdminSeatPicker("NODE 02", p2, employees) { p2 = it }
                AdminSeatPicker("CREATIVE", p3, employees) { p3 = it }
                
                Text("MARK ABSENT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableForAbsent.forEach { emp ->
                        val isMarked = absents.contains(emp.name)
                        FilterChip(
                            selected = isMarked,
                            onClick = { absents = if (isMarked) absents - emp.name else absents + emp.name },
                            label = { Text(emp.name.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            shape = RoundedCornerShape(0.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(p1, p2, p3, absents.joinToString(",")) }, shape = RoundedCornerShape(0.dp)) { Text("SAVE") }
        },
        dismissButton = {
            TextButton(onClick = onDelete) { Text("DELETE", color = Color.Red) }
        }
    )
}

@Composable
fun AdminSeatPicker(label: String, current: String?, employees: List<Employee>, onSelect: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(current?.uppercase() ?: "VACANT", fontWeight = FontWeight.Bold)
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("VACANT") }, onClick = { onSelect(null); expanded = false })
                employees.forEach { emp ->
                    DropdownMenuItem(text = { Text(emp.name.uppercase()) }, onClick = { onSelect(emp.name); expanded = false })
                }
            }
        }
    }
}

@Composable
fun AnalyticsDashboard(employees: List<Employee>, statsMap: Map<String, EmployeeStats>, viewModel: SkillskapesViewModel) {
    val analytics by viewModel.bossAnalytics.collectAsStateWithLifecycle()
    val today by viewModel.todayStatus.collectAsStateWithLifecycle()
    val nonBosses = employees.filter { it.role != "Boss" }
    var selectedEmployeeId by remember { mutableStateOf<String?>(null) }

    val completedDays = analytics["completed_working_days"] ?: 0
    val totalPresence = analytics["total_presence"] ?: 0

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(32.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
        item { Text("ANALYTICS", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black) }
        
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                MonoMetric("COMPLETED DAYS", "$completedDays", Modifier.weight(1f))
                MonoMetric("TOTAL PRESENCE", "$totalPresence", Modifier.weight(1f))
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AnalyticsStatusSection("IN OFFICE TODAY", today["office"] ?: emptyList(), employees) { selectedEmployeeId = it }
                AnalyticsStatusSection("WFH TODAY", today["wfh"] ?: emptyList(), employees) { selectedEmployeeId = it }
                AnalyticsStatusSection("ABSENT TODAY", today["absent"] ?: emptyList(), employees) { selectedEmployeeId = it }
            }
        }
        
        item { Text("PERFORMANCE LEDGER", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black) }
        
        items(nonBosses) { emp ->
            val stats = statsMap[emp.id] ?: EmployeeStats(0, 0, 0)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedEmployeeId = emp.id }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(emp.name.uppercase(), fontWeight = FontWeight.Black)
                    Text(emp.role.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Text("${stats.attendanceRate.toInt()}%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
        }
    }

    if (selectedEmployeeId != null) {
        val emp = employees.find { it.id == selectedEmployeeId }
        val stats = statsMap[selectedEmployeeId] ?: EmployeeStats(0, 0, 0)
        if (emp != null) {
            IndividualActivityDialog(emp, stats) { selectedEmployeeId = null }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalyticsStatusSection(title: String, names: List<String>, allEmps: List<Employee>, onNameClick: (String) -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        if (names.isEmpty()) {
            Text("NONE", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                names.forEach { name ->
                    Surface(
                        modifier = Modifier.clickable { allEmps.find { it.name == name }?.let { onNameClick(it.id) } },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(name.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun IndividualActivityDialog(emp: Employee, stats: EmployeeStats, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(emp.name.uppercase(), fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                MonoMetric("OFFICE DAYS", "${stats.inOfficeDays}", Modifier.fillMaxWidth())
                MonoMetric("WFH DAYS", "${stats.wfhDays}", Modifier.fillMaxWidth())
                MonoMetric("ABSENT DAYS", "${stats.absentDays}", Modifier.fillMaxWidth())
                Text("ATTENDANCE RATE: ${stats.attendanceRate.toInt()}%", fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelLarge)
            }
        },
        confirmButton = { Button(onClick = onDismiss, shape = RoundedCornerShape(0.dp)) { Text("CLOSE") } }
    )
}

@Composable fun ProfileView(u: Employee, s: Map<String, EmployeeStats>) {}
@Composable fun MetricPill(l: String, v: String, c: Color, m: Modifier) {}
@Composable fun SeatCard(l: String, p: String?, m: Modifier, e: List<Employee>, a: (String?) -> Unit) {}
@Composable fun MeetingCard(m: Meeting, d: () -> Unit) {}
@Composable fun LeaveApprovalCard(r: LeaveRequest, a: () -> Unit, re: () -> Unit) {}
@Composable fun StatsBarCard(e: Employee, s: EmployeeStats) {}
