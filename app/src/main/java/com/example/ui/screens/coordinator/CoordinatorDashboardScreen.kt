package com.example.ui.screens.coordinator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.StudentEntity
import com.example.ui.components.LeapTopAppBar
import com.example.ui.components.PaperVsDigitalBadge
import com.example.ui.components.StatusChip
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel

@Composable
fun CoordinatorDashboardScreen(
    viewModel: LeapViewModel,
    onNavigate: (String) -> Unit,
    onSwitchRole: () -> Unit
) {
    val allStudents by viewModel.allStudents.collectAsState()
    val allOrgs by viewModel.allOrganizations.collectAsState()
    val allApplications by viewModel.allApplications.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val pendingSync by viewModel.pendingSyncCount.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    var showBroadcastModal by remember { mutableStateOf(false) }
    var broadcastTitle by remember { mutableStateOf("Form D Submission Deadline Alert") }
    var broadcastMessage by remember { mutableStateOf("All Week 12 Final Reports and Supervisor Form D Assessments must be finalized by Friday at 5:00 PM.") }
    var selectedApplicationToView by remember { mutableStateOf<com.example.data.local.entities.InternshipApplicationEntity?>(null) }

    var selectedFilter by remember { mutableStateOf("All") }

    val filteredStudents = when (selectedFilter) {
        "Active" -> allStudents.filter { it.internshipStatus == "Active" }
        "At Risk" -> allStudents.filter { it.progressPercentage < 50 }
        "Completed" -> allStudents.filter { it.internshipStatus == "Completed" }
        else -> allStudents
    }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "LEAP Directorate",
                subtitle = "Limkokwing University • Coordinator Portal",
                roleName = "COORDINATOR",
                isOffline = isOffline,
                pendingSyncCount = pendingSync,
                onSyncClick = { viewModel.syncPendingData {} },
                unreadNotificationCount = notifications.count { !it.isRead },
                onNotificationClick = { onNavigate(Screen.Notifications.route) },
                onRoleSwitchClick = onSwitchRole
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Coordinator Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LeapNavyPrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Dr. Fatmata Sesay",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "LEAP Director • Academic Directorate",
                                    style = MaterialTheme.typography.bodySmall.copy(color = LeapGoldAccent)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = LeapGoldAccent
                            ) {
                                Text(
                                    text = "COORDINATOR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = LeapNavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CohortStatItem("Cohort Size", "${allStudents.size}", LeapCyan)
                            CohortStatItem("Host Orgs", "${allOrgs.size}", LeapGoldAccent)
                            CohortStatItem("Active", "${allStudents.count { it.internshipStatus == "Active" }}", StatusActive)
                            CohortStatItem("Attention", "1", StatusAlert)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // AI Progress Insights & Broadcast Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onNavigate(Screen.AiProgressInsight.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = LeapBlue),
                        modifier = Modifier.weight(1f).height(44.dp).testTag("btn_coord_ai_insights")
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Progress Insights", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showBroadcastModal = true },
                        modifier = Modifier.weight(1f).height(44.dp).testTag("btn_coord_broadcast")
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Send Broadcast", fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Nav Modules
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onNavigate(Screen.StudentManagement.route) },
                        modifier = Modifier.weight(1f).testTag("btn_nav_students")
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("All Students", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onNavigate(Screen.OrganizationManagement.route) },
                        modifier = Modifier.weight(1f).testTag("btn_nav_orgs")
                    ) {
                        Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Host Partners", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onNavigate(Screen.DissertationComparison.route) },
                        modifier = Modifier.weight(1f).testTag("btn_nav_dissertation_compare")
                    ) {
                        Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Paper vs Dig", fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Pending Internship Applications Review Section
            if (allApplications.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LeapCyan.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AppRegistration,
                                        contentDescription = null,
                                        tint = LeapCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Incoming Internship Applications (${allApplications.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = LeapNavyPrimary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFE0F2FE)
                                ) {
                                    Text(
                                        text = "Multi-Step Forms",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0369A1),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Students have completed multi-step applications capturing contact info, preferred industry sectors, and attached CVs.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            allApplications.forEach { app ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { selectedApplicationToView = app },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = app.fullName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = LeapNavyPrimary
                                            )
                                            Text(
                                                text = "${app.programme} • CGPA: ${app.cgpa}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "Sector: ${app.primarySector}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = LeapBlue
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            StatusChip(status = app.submissionStatus)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "View CV Details",
                                                fontSize = 10.sp,
                                                color = LeapCyan,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Cohort Monitoring Header & Filters
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LEAP Cohort Monitoring",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LeapNavyPrimary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All", "Active", "At Risk", "Completed").forEach { filter ->
                        val isSel = selectedFilter == filter
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) LeapNavyPrimary else Color(0xFFE2E8F0),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedFilter = filter }
                        ) {
                            Text(
                                text = filter,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) Color.White else Color(0xFF475569),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(filteredStudents) { student ->
                CoordinatorStudentRowCard(
                    student = student,
                    onInspect = {
                        viewModel.setSelectedStudentId(student.id)
                        onNavigate(Screen.StudentDashboard.route)
                    },
                    onAiInspect = {
                        viewModel.generateProgressInsight(student)
                        onNavigate(Screen.AiProgressInsight.route)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                PaperVsDigitalBadge(
                    paperProcess = "Coordinator manually searches through paper filing cabinets in Freetown campus to track 100+ students.",
                    digitalProcess = "Real-time institution-wide dashboard with automated compliance metrics and early warning flags."
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showBroadcastModal) {
        AlertDialog(
            onDismissRequest = { showBroadcastModal = false },
            title = { Text("Send Broadcast Notification") },
            text = {
                Column {
                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Alert Title") },
                        modifier = Modifier.fillMaxWidth().testTag("input_broadcast_title")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = broadcastMessage,
                        onValueChange = { broadcastMessage = it },
                        label = { Text("Message Body") },
                        modifier = Modifier.fillMaxWidth().testTag("input_broadcast_body"),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendBroadcastNotification(broadcastTitle, broadcastMessage, "STUDENT")
                        showBroadcastModal = false
                    },
                    modifier = Modifier.testTag("confirm_send_broadcast")
                ) {
                    Text("Broadcast Alert")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Application Review Dialog for Coordinator
    selectedApplicationToView?.let { app ->
        AlertDialog(
            onDismissRequest = { selectedApplicationToView = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AppRegistration, contentDescription = null, tint = LeapNavyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Application: ${app.applicationRefNumber}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = app.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LeapNavyPrimary)
                            Text(text = "${app.programme} • ID: ${app.studentIdNumber}", fontSize = 12.sp)
                            Text(text = "Email: ${app.email} • Phone: ${app.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "CGPA: ${app.cgpa} • Year: ${app.currentYearSemester}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Text("Placement Preferences", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LeapNavyPrimary)
                    Text("• Primary Sector: ${app.primarySector}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("• Target Role: ${app.preferredRoleDepartment}", fontSize = 12.sp)
                    Text("• Location & Mode: ${app.preferredLocation} (${app.preferredWorkMode})", fontSize = 12.sp)

                    HorizontalDivider()

                    Text("CV & Technical Profile", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LeapNavyPrimary)
                    Text("• Attached PDF: ${app.cvFileName} (${app.cvFileSize})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = LeapCyan)
                    Text("• Skills: ${app.technicalSkills}", fontSize = 11.sp)
                    Text("• Summary: ${app.professionalSummary}", fontSize = 11.sp, maxLines = 3)
                    Text("• Referee: ${app.academicRefereeName} (${app.academicRefereeContact})", fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveApplicationDraft(app.copy(submissionStatus = "Approved")) {
                            selectedApplicationToView = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LeapNavyPrimary)
                ) {
                    Text("Approve & Endorse")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedApplicationToView = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun CohortStatItem(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = valueColor
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun CoordinatorStudentRowCard(
    student: StudentEntity,
    onInspect: () -> Unit,
    onAiInspect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LeapNavyPrimary
                        )
                    )
                    Text(
                        text = "${student.studentIdCode} • ${student.programme}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    )
                }
                StatusChip(status = student.internshipStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress: ${student.progressPercentage}% (Week ${student.currentWeek}/12)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (student.progressPercentage < 50) StatusAlert else StatusActive,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onAiInspect,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("AI Audit", fontSize = 10.sp)
                    }
                    Button(
                        onClick = onInspect,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("View Record", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StudentManagementScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val allStudents by viewModel.allStudents.collectAsState()
    val allOrgs by viewModel.allOrganizations.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddStudentDialog by remember { mutableStateOf(false) }

    var newName by remember { mutableStateOf("") }
    var newCode by remember { mutableStateOf("LKW-SL-2026-") }
    var newProgramme by remember { mutableStateOf("BSc (Hons) Information Technology") }
    var newDept by remember { mutableStateOf("Software Engineering") }

    val filtered = allStudents.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.studentIdCode.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Student Directory",
                subtitle = "Active Cohort Placement Records",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddStudentDialog = true },
                containerColor = LeapNavyPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Student")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search by student name or ID code") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("search_students_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(filtered) { student ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(student.name, fontWeight = FontWeight.Bold, color = LeapNavyPrimary)
                            StatusChip(status = student.internshipStatus)
                        }
                        Text("${student.studentIdCode} • ${student.programme}", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Email: ${student.email} • Tel: ${student.phone}", fontSize = 10.sp, color = Color(0xFF475569))
                        Text("Dates: ${student.commencementDate} to ${student.completionDate}", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showAddStudentDialog) {
        AlertDialog(
            onDismissRequest = { showAddStudentDialog = false },
            title = { Text("Register New LEAP Student") },
            text = {
                Column {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Full Name") })
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newCode, onValueChange = { newCode = it }, label = { Text("Student ID Code") })
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newProgramme, onValueChange = { newProgramme = it }, label = { Text("Programme") })
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newDept, onValueChange = { newDept = it }, label = { Text("Assigned Department") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.addStudent(newName, newCode, newProgramme, 1L, newDept)
                            newName = ""
                            showAddStudentDialog = false
                        }
                    }
                ) {
                    Text("Save Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStudentDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun OrganizationManagementScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val allOrgs by viewModel.allOrganizations.collectAsState()

    var showAddOrgModal by remember { mutableStateOf(false) }
    var orgName by remember { mutableStateOf("") }
    var orgIndustry by remember { mutableStateOf("") }
    var orgAddress by remember { mutableStateOf("") }
    var contactPerson by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("+232 ") }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Host Organizations",
                subtitle = "Accredited LEAP Industry Partners",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddOrgModal = true },
                containerColor = LeapNavyPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.AddBusiness, contentDescription = "Add Partner")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            items(allOrgs) { org ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(org.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LeapNavyPrimary))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (org.isApproved) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = if (org.isApproved) "ACCREDITED" else "PENDING",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (org.isApproved) Color(0xFF166534) else Color(0xFF92400E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(org.industry, style = MaterialTheme.typography.bodySmall.copy(color = LeapCyan, fontWeight = FontWeight.Medium))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Address: ${org.address}", fontSize = 11.sp, color = Color(0xFF475569))
                        Text("Contact: ${org.contactPerson} • ${org.contactEmail} • ${org.contactPhone}", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showAddOrgModal) {
        AlertDialog(
            onDismissRequest = { showAddOrgModal = false },
            title = { Text("Register Host Organization") },
            text = {
                Column {
                    OutlinedTextField(value = orgName, onValueChange = { orgName = it }, label = { Text("Organization Name") })
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = orgIndustry, onValueChange = { orgIndustry = it }, label = { Text("Industry Sector") })
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = orgAddress, onValueChange = { orgAddress = it }, label = { Text("Physical Address in Freetown") })
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = contactPerson, onValueChange = { contactPerson = it }, label = { Text("Contact Person / HR Lead") })
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = contactEmail, onValueChange = { contactEmail = it }, label = { Text("Contact Email") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (orgName.isNotBlank()) {
                            viewModel.addOrganization(orgName, orgIndustry, orgAddress, contactPerson, contactEmail, contactPhone)
                            orgName = ""
                            showAddOrgModal = false
                        }
                    }
                ) {
                    Text("Add Organization")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddOrgModal = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun DissertationComparisonScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Dissertation Comparison",
                subtitle = "Paper-Based vs AI Digital LEAP System",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LeapNavyPrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Limkokwing University LEAP Modernization",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "This prototype was developed to empirically address the documented limitations of paper-based internship administration in Sierra Leone higher education institutions.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 18.sp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Systemic Comparison Matrix",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LeapNavyPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            val comparisons = listOf(
                Triple("Daily Log Book", "Handwritten notebook entries; risk of physical loss; delay in review until end of term.", "Digitized mobile entries; daily structured reflection; offline Room DB caching; real-time supervisor visibility."),
                Triple("Weekly Reports", "Physical printed reports; cumbersome travel to university for submission.", "Instant in-app compilation from daily logs; supervisor digital feedback and 1-5 rating."),
                Triple("Form D Assessment", "Paper forms completed manually; prone to arithmetic errors; physical ink stamps required.", "Exact 18-criteria digitized scoring; automated average calculation; digital sign-off and verification."),
                Triple("Form D2 Self-Evaluation", "Paper checklist stapled to final binding; limited academic feedback loop.", "Structured 3-part digital evaluation with Likert scale calculation and instant coordinator audit."),
                Triple("Submission Checklist", "Manual ticking on loose paper sheet; frequent missing document disputes.", "Dual-stakeholder verified 8-item checklist with immutable audit timestamps."),
                Triple("Placement Matching", "Ad-hoc manual placement based on student informal networking.", "AI-Assisted recommendation matching student IT skills with accredited host organizations."),
                Triple("Cohort Monitoring", "Filing cabinets; zero real-time visibility into at-risk students.", "Automated AI progress risk detection ('On Track', 'Needs Attention', 'At Risk') and instant broadcast alerts.")
            )

            items(comparisons) { (domain, paper, digital) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = domain,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LeapNavyPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Existing Paper Process", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFDC2626), fontWeight = FontWeight.Bold))
                                Text(paper, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF475569), fontSize = 11.sp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Digital LEAP System", style = MaterialTheme.typography.labelSmall.copy(color = StatusActive, fontWeight = FontWeight.Bold))
                                Text(digital, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF0F172A), fontSize = 11.sp, fontWeight = FontWeight.Medium))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
