package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.WeeklyReportEntity
import com.example.ui.components.LeapTopAppBar
import com.example.ui.components.PaperVsDigitalBadge
import com.example.ui.components.StatusChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel

@Composable
fun WeeklyReportsScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val weeklyReports by viewModel.weeklyReports.collectAsState()
    val dailyLogs by viewModel.dailyLogs.collectAsState()
    val selectedWeek by viewModel.selectedWeek.collectAsState()

    val currentReport = weeklyReports.find { it.weekNumber == selectedWeek }
    val weekDailyLogs = dailyLogs.filter { it.weekNumber == selectedWeek }

    var dateRangeInput by remember { mutableStateOf(currentReport?.dateRange ?: "Mar 02 - Mar 06, 2026") }
    var activitiesInput by remember {
        mutableStateOf(
            currentReport?.activitiesCompleted ?: weekDailyLogs.joinToString("\n• ") { it.taskActivity }.let { if (it.isNotBlank()) "• $it" else "" }
        )
    }
    var skillsInput by remember { mutableStateOf(currentReport?.skillsLearned ?: "Android Compose, Room DB, Git collaboration, Form validation") }
    var challengesInput by remember { mutableStateOf(currentReport?.challenges ?: "Low bandwidth network caching during field visits") }
    var reflectionInput by remember { mutableStateOf(currentReport?.reflection ?: "Offline-first architecture ensures full system accessibility across Sierra Leone.") }

    var showSubmitConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(currentReport) {
        if (currentReport != null) {
            dateRangeInput = currentReport.dateRange
            activitiesInput = currentReport.activitiesCompleted
            skillsInput = currentReport.skillsLearned
            challengesInput = currentReport.challenges
            reflectionInput = currentReport.reflection
        } else if (weekDailyLogs.isNotEmpty()) {
            activitiesInput = "• " + weekDailyLogs.joinToString("\n• ") { it.taskActivity }
        }
    }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Weekly Reports",
                subtitle = "Week $selectedWeek Academic Synthesis",
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
            // Week Tabs
            Text(
                text = "Select Report Week",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items((1..12).toList()) { week ->
                    val isSelected = week == selectedWeek
                    val hasReport = weeklyReports.any { it.weekNumber == week }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) LeapNavyPrimary else if (hasReport) Color(0xFFDCFCE7) else Color(0xFFE2E8F0),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.setSelectedWeek(week) }
                            .testTag("weekly_report_tab_$week")
                    ) {
                        Text(
                            text = if (hasReport) "Week $week ✓" else "Week $week",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else if (hasReport) Color(0xFF166534) else Color(0xFF475569)
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Report Header Card
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
                            text = "Week $selectedWeek Report",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        StatusChip(status = currentReport?.submissionStatus ?: "Draft")
                    }
                    if (currentReport?.submittedDate?.isNotBlank() == true) {
                        Text(
                            text = "Submitted: ${currentReport.submittedDate} | Reviewed: ${currentReport.reviewedDate.ifEmpty { "Pending" }}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Supervisor Feedback Banner if reviewed
            if (currentReport?.supervisorFeedback?.isNotBlank() == true) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Feedback, contentDescription = null, tint = LeapBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Supervisor Feedback & Evaluation",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LeapNavyPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentReport.supervisorFeedback,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF1E3A8A))
                        )
                        if (currentReport.feedbackRating > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Supervisor Rating: ${currentReport.feedbackRating} / 5 Stars",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LeapGoldAccent
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Input Fields
            OutlinedTextField(
                value = dateRangeInput,
                onValueChange = { dateRangeInput = it },
                label = { Text("Date Range (e.g. Mar 02 - Mar 06, 2026)") },
                modifier = Modifier.fillMaxWidth().testTag("input_report_date_range"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = activitiesInput,
                onValueChange = { activitiesInput = it },
                label = { Text("Activities Completed (Compiled from Daily Logs)") },
                modifier = Modifier.fillMaxWidth().testTag("input_report_activities"),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = skillsInput,
                onValueChange = { skillsInput = it },
                label = { Text("Skills Learned & Professional Competencies") },
                modifier = Modifier.fillMaxWidth().testTag("input_report_skills"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = challengesInput,
                onValueChange = { challengesInput = it },
                label = { Text("Challenges Encountered & Solutions Applied") },
                modifier = Modifier.fillMaxWidth().testTag("input_report_challenges"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = reflectionInput,
                onValueChange = { reflectionInput = it },
                label = { Text("Weekly Student Reflection") },
                modifier = Modifier.fillMaxWidth().testTag("input_report_reflection"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showSubmitConfirmation = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("button_submit_weekly_report")
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (currentReport == null) "Submit Week $selectedWeek Report" else "Update & Resubmit Report",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PaperVsDigitalBadge(
                paperProcess = "Weekly physical report booklet signatures submitted manually at end of term.",
                digitalProcess = "Immediate weekly report synthesis with supervisor remote grading and feedback logs."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showSubmitConfirmation) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirmation = false },
            title = { Text("Confirm Weekly Report Submission") },
            text = {
                Text("Are you sure you want to submit your Week $selectedWeek report for workplace supervisor review? Your supervisor will be notified immediately.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitWeeklyReport(
                            weekNumber = selectedWeek,
                            dateRange = dateRangeInput,
                            activities = activitiesInput,
                            skills = skillsInput,
                            challenges = challengesInput,
                            reflection = reflectionInput
                        )
                        showSubmitConfirmation = false
                    },
                    modifier = Modifier.testTag("confirm_submit_weekly_report")
                ) {
                    Text("Confirm Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
