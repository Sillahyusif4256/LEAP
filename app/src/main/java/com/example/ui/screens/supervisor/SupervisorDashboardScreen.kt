package com.example.ui.screens.supervisor

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
fun SupervisorDashboardScreen(
    viewModel: LeapViewModel,
    onNavigate: (String) -> Unit,
    onSwitchRole: () -> Unit
) {
    val allStudents by viewModel.allStudents.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val pendingSync by viewModel.pendingSyncCount.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val assignedStudents = allStudents.filter { it.supervisorId == 1L }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Workplace Supervisor",
                subtitle = "Tech Solutions SL Ltd. • Host Portal",
                roleName = "SUPERVISOR",
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
            // Supervisor Banner
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
                                    text = "Ing. David Koroma",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Head of Software Engineering • Tech Solutions SL",
                                    style = MaterialTheme.typography.bodySmall.copy(color = LeapGoldAccent)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = LeapBlue
                            ) {
                                Text(
                                    text = "SUPERVISOR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Assigned Interns", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8)))
                                Text("${assignedStudents.size} Students", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Reports Pending Review", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8)))
                                Text("1 Report", style = MaterialTheme.typography.titleMedium.copy(color = LeapGoldAccent, fontWeight = FontWeight.Bold))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Form D Evaluations", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8)))
                                Text("In Progress", style = MaterialTheme.typography.titleMedium.copy(color = StatusActive, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Supervised Internship Candidates",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LeapNavyPrimary
                    )
                )
                Text(
                    text = "Select an intern to evaluate Form D, sign off Action Plans, and grade weekly log books.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(assignedStudents) { student ->
                SupervisorStudentCard(
                    student = student,
                    onSelect = {
                        viewModel.setSelectedStudentId(student.id)
                    },
                    onFormDAssessment = {
                        viewModel.setSelectedStudentId(student.id)
                        onNavigate(Screen.FormDAssessment.route)
                    },
                    onReviewWeekly = {
                        viewModel.setSelectedStudentId(student.id)
                        onNavigate(Screen.WeeklyReports.route)
                    },
                    onActionPlan = {
                        viewModel.setSelectedStudentId(student.id)
                        onNavigate(Screen.ActionPlan.route)
                    },
                    onChecklist = {
                        viewModel.setSelectedStudentId(student.id)
                        onNavigate(Screen.SubmissionChecklist.route)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                PaperVsDigitalBadge(
                    paperProcess = "Supervisor manually scribbles on 3-page carbon copy assessment form at end of semester.",
                    digitalProcess = "Continuous in-app 18-criteria scoring, instant weekly report grading, and verified digital signatures."
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SupervisorStudentCard(
    student: StudentEntity,
    onSelect: () -> Unit,
    onFormDAssessment: () -> Unit,
    onReviewWeekly: () -> Unit,
    onActionPlan: () -> Unit,
    onChecklist: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .testTag("supervisor_student_card_${student.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(LeapGoldAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            fontWeight = FontWeight.Bold,
                            color = LeapNavyPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LeapNavyPrimary
                            )
                        )
                        Text(
                            text = "${student.programme} • ID: ${student.studentIdCode}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
                StatusChip(status = student.internshipStatus)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Internship Progress: Week ${student.currentWeek} of ${student.totalWeeks}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                )
                Text(
                    text = "${student.progressPercentage}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = StatusActive
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { student.progressPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = StatusActive,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onFormDAssessment,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.weight(1.2f).height(36.dp).testTag("btn_form_d_${student.id}")
                ) {
                    Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Form D (18-Scale)", fontSize = 10.sp)
                }

                OutlinedButton(
                    onClick = onReviewWeekly,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("btn_weekly_${student.id}")
                ) {
                    Text("Weekly Logs", fontSize = 10.sp)
                }

                OutlinedButton(
                    onClick = onActionPlan,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("btn_action_plan_${student.id}")
                ) {
                    Text("Action Plan", fontSize = 10.sp)
                }
            }
        }
    }
}
