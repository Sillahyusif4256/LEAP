package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LeapTopAppBar
import com.example.ui.components.PaperVsDigitalBadge
import com.example.ui.components.StatusChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel

@Composable
fun StudentProfileScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val student by viewModel.currentStudent.collectAsState()

    val studentName = student?.name ?: "Mohamed Kamara"
    val studentId = student?.studentIdCode ?: "LKW-SL-DEMO001"
    val programme = student?.programme ?: "BSc (Hons) Information Technology"
    val email = student?.email ?: "student@leap.demo"
    val phone = student?.phone ?: "+232 78 450123"
    val orgName = "Tech Solutions SL Ltd."
    val dept = student?.department ?: "Software Engineering"
    val supervisor = "Ing. David Koroma"
    val supervisorDesig = "Head of Software Engineering"
    val startDate = student?.commencementDate ?: "12-Jan-2026"
    val endDate = student?.completionDate ?: "05-Apr-2026"

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Student Profile",
                subtitle = "Limkokwing University Academic Record",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LeapNavyPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(LeapGoldAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MK",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = LeapNavyPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = studentName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Student ID: $studentId",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = LeapGoldAccent
                            )
                        )
                        Text(
                            text = programme,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notice: University Verified Info Lock
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Verified Placement",
                        tint = LeapBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "University Verified Placement Information. Modifiable solely by the LEAP Coordinator Office.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF1E3A8A),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Academic & Contact Details",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            ProfileFieldCard(icon = Icons.Default.Badge, label = "Student Identification", value = studentId)
            ProfileFieldCard(icon = Icons.Default.School, label = "Programme of Study", value = programme)
            ProfileFieldCard(icon = Icons.Default.Email, label = "Institutional Email", value = email)
            ProfileFieldCard(icon = Icons.Default.Phone, label = "Telephone Contact", value = phone)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Host Placement & Supervision Details",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            ProfileFieldCard(icon = Icons.Default.Business, label = "Host Organization", value = orgName)
            ProfileFieldCard(icon = Icons.Default.CorporateFare, label = "Assigned Department", value = dept)
            ProfileFieldCard(icon = Icons.Default.Person, label = "Workplace Supervisor", value = supervisor)
            ProfileFieldCard(icon = Icons.Default.Work, label = "Supervisor Designation", value = supervisorDesig)
            ProfileFieldCard(icon = Icons.Default.CalendarMonth, label = "Internship Commencement Date", value = startDate)
            ProfileFieldCard(icon = Icons.Default.Event, label = "Internship Completion Date", value = endDate)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InternshipPlacementScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val student by viewModel.currentStudent.collectAsState()
    val internship by viewModel.currentInternship.collectAsState()

    val orgName = "Tech Solutions SL Ltd."
    val dept = internship?.department ?: "Software Engineering & Cloud Systems"
    val role = internship?.positionRole ?: "Junior Mobile Software Engineer Intern"
    val supervisor = "Ing. David Koroma"
    val startDate = internship?.startDate ?: "12-Jan-2026"
    val endDate = internship?.endDate ?: "05-Apr-2026"
    val status = internship?.status ?: "Active"

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Internship Placement",
                subtitle = "Host Organization & Assignment Profile",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Placement Status Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Placement Status",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        StatusChip(status = status)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Official placement sanctioned under Limkokwing University LEAP Internship Framework 2025/2026.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Organization Profile",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LeapBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = orgName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Software Development & Enterprise Systems",
                                style = MaterialTheme.typography.bodySmall.copy(color = LeapCyan)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Address: 14 Wilkinson Road, Freetown, Sierra Leone\nEmail: contact@techsolutions.sl\nPhone: +232 76 991204\nAccredited Host: Approved Partner since 2021",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF475569), lineHeight = 20.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Placement Assignment Details",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            ProfileFieldCard(icon = Icons.Default.Work, label = "Designated Role / Position", value = role)
            ProfileFieldCard(icon = Icons.Default.CorporateFare, label = "Host Department", value = dept)
            ProfileFieldCard(icon = Icons.Default.SupervisorAccount, label = "Workplace Supervisor", value = supervisor)
            ProfileFieldCard(icon = Icons.Default.DateRange, label = "Internship Start Date", value = startDate)
            ProfileFieldCard(icon = Icons.Default.EventAvailable, label = "Internship End Date", value = endDate)

            Spacer(modifier = Modifier.height(16.dp))

            PaperVsDigitalBadge(
                paperProcess = "Form A2 physical posting letters delivered by students by hand.",
                digitalProcess = "Immediate digital placement registration with verified host profile and supervisor linking."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ActionPlanScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val actionPlan by viewModel.actionPlan.collectAsState()
    val tasks by viewModel.actionPlanTasks.collectAsState()
    val authState by viewModel.authState.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskDesc by remember { mutableStateOf("") }
    var newTaskByWhen by remember { mutableStateOf("Week 10") }

    val isSupervisor = authState.selectedRole == "SUPERVISOR"

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "LEAP Action Plan",
                subtitle = "Student Self-Evaluation Task Schedule",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = LeapNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_action_task")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Action Plan Academic Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Semester Action Plan",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        StatusChip(status = actionPlan?.approvalStatus ?: "Approved")
                    }
                    Text(
                        text = "Academic Year: ${actionPlan?.academicYear ?: "2025/2026"} | ${actionPlan?.semester ?: "Semester 2"}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "The Action Plan outlines target tasks, deadlines ('By When'), and achievement status. It forms the formal contract between Student, Host, and University.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF475569), fontSize = 11.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tasks Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Proposed Tasks & Milestones",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LeapNavyPrimary
                    )
                )
                Text(
                    text = "${tasks.count { it.isAchieved }}/${tasks.size} Achieved",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = StatusActive
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (tasks.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No action plan tasks added yet. Tap + to add proposed milestones.",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                    )
                }
            } else {
                tasks.forEach { task ->
                    ActionPlanTaskCard(
                        task = task,
                        isSupervisor = isSupervisor,
                        onToggleAchieved = { viewModel.toggleTaskAchieved(task) },
                        onApproveTask = { viewModel.approveActionPlanTask(task) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Digital Signatures & Stamp Section
            Text(
                text = "Digital Verification & Signatures",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Student Signature
                    SignatureRow(
                        label = "Student Signature",
                        signee = actionPlan?.studentSignature ?: "Mohamed Kamara",
                        isSigned = actionPlan?.isStudentSigned ?: true,
                        date = actionPlan?.studentSignatureDate ?: "15-Jan-2026"
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Supervisor Signature
                    SignatureRow(
                        label = "Supervisor Signature",
                        signee = actionPlan?.supervisorSignature ?: "Ing. David Koroma",
                        isSigned = actionPlan?.isSupervisorSigned ?: true,
                        date = actionPlan?.supervisorSignatureDate ?: "18-Jan-2026"
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Company Stamp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Company Digital Stamp",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                            )
                            Text(
                                text = actionPlan?.companyStampText ?: "OFFICIALLY VERIFIED - TECH SOLUTIONS SL",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LeapNavyPrimary
                                )
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = "STAMPED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF166534),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (isSupervisor && actionPlan?.approvalStatus != "Approved") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.approveActionPlan() },
                            modifier = Modifier.fillMaxWidth().testTag("button_approve_action_plan")
                        ) {
                            Text("Sign & Approve Action Plan")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Add Action Plan Task") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTaskDesc,
                        onValueChange = { newTaskDesc = it },
                        label = { Text("Task: What will I do") },
                        modifier = Modifier.fillMaxWidth().testTag("input_action_task_desc")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newTaskByWhen,
                        onValueChange = { newTaskByWhen = it },
                        label = { Text("By When (e.g. Week 6, 20-Feb-2026)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_action_task_by_when")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskDesc.isNotBlank()) {
                            viewModel.addActionPlanTask(newTaskDesc, newTaskByWhen)
                            newTaskDesc = ""
                            showAddTaskDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_action_task")
                ) {
                    Text("Add Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ActionPlanTaskCard(
    task: com.example.data.local.entities.ActionPlanTaskEntity,
    isSupervisor: Boolean,
    onToggleAchieved: () -> Unit,
    onApproveTask: () -> Unit
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isAchieved,
                    onCheckedChange = { onToggleAchieved() },
                    modifier = Modifier.testTag("checkbox_task_achieved_${task.id}")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.taskDescription,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (task.isAchieved) Color(0xFF64748B) else Color(0xFF0F172A)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target: ${task.byWhen}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = LeapCyan,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        if (task.isAchieved) {
                            Text(
                                text = "✓ Achieved",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = StatusActive,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
            if (task.supervisorNotes.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "Supervisor Note: ${task.supervisorNotes}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SignatureRow(
    label: String,
    signee: String,
    isSigned: Boolean,
    date: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
            )
            Text(
                text = signee,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Text(
                text = "Date: $date",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            )
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isSigned) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
        ) {
            Text(
                text = if (isSigned) "DIGITALLY SIGNED" else "PENDING",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSigned) Color(0xFF166534) else Color(0xFF92400E),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun ProfileFieldCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LeapCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                )
            }
        }
    }
}
