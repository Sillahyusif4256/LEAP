package com.example.ui.screens.supervisor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.data.local.entities.AssessmentItemEntity
import com.example.ui.components.LeapTopAppBar
import com.example.ui.components.PaperVsDigitalBadge
import com.example.ui.components.RatingBar5
import com.example.ui.components.StatusChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel

@Composable
fun FormDAssessmentScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val assessment by viewModel.assessment.collectAsState()
    val assessmentItems by viewModel.assessmentItems.collectAsState()
    val student by viewModel.currentStudent.collectAsState()

    var strengthsInput by remember { mutableStateOf(assessment?.majorStrengths ?: "High proficiency in Jetpack Compose, rapid bug resolution, disciplined code documentation, and proactive daily communication.") }
    var recommendationsInput by remember { mutableStateOf(assessment?.academicWorkRecommendations ?: "Incorporate advanced cloud backend integration and automated UI instrumentation pipelines into final semester modules.") }
    var technicalSkillsInput by remember { mutableStateOf(assessment?.technicalSkillsGained ?: "Android Kotlin, Jetpack Compose, Room SQLite, Material 3, Git CI/CD workflow.") }
    var objectivesMet by remember { mutableStateOf(assessment?.wereInternshipObjectivesMet ?: true) }
    var commentsInput by remember { mutableStateOf(assessment?.otherComments ?: "Mohamed has proved to be an outstanding intern asset at Tech Solutions SL.") }

    var showSuccessMessage by remember { mutableStateOf(false) }

    // Calculate real-time average of 18 criteria
    val averageScore = if (assessmentItems.isNotEmpty()) {
        assessmentItems.map { it.rating }.average().toFloat()
    } else {
        4.6f
    }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Form D: Supervisor Assessment",
                subtitle = "Official 18-Criteria Evaluation Scale",
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
            // Header
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
                                text = "Candidate: ${student?.name ?: "Mohamed Kamara"}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "ID: ${student?.studentIdCode ?: "LKW-SL-DEMO001"} • ${student?.programme ?: "BSc IT"}",
                                style = MaterialTheme.typography.bodySmall.copy(color = LeapGoldAccent)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LeapGoldAccent
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = String.format("%.1f", averageScore),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = LeapNavyPrimary
                                    )
                                )
                                Text(
                                    text = "/ 5.0 Rating",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = LeapNavyPrimary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Official LEAP Form D completed by Workplace Supervisor (Ing. David Koroma) to evaluate performance across 18 core competency domains.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Clarification Notice (Strict Compliance Rule)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = "Official Grade Authority",
                        tint = Color(0xFF92400E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACADEMIC GOVERNANCE RULE: The supervisor holds sole responsibility for assigning official marks. AI features serve an advisory role only.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Official 18 Performance Criteria (1 to 5 Scale)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Text(
                text = "1: Unsatisfactory | 2: Below Average | 3: Average | 4: Above Average | 5: Superior",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 18 Criteria list
            assessmentItems.forEach { item ->
                FormDCriteriaCard(
                    item = item,
                    onRatingChanged = { newRating ->
                        viewModel.updateAssessmentItemRating(item, newRating)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Qualitative Feedback Section
            Text(
                text = "Supervisor Qualitative Assessment",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = strengthsInput,
                onValueChange = { strengthsInput = it },
                label = { Text("Major Strengths Demonstrated") },
                modifier = Modifier.fillMaxWidth().testTag("input_formd_strengths"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = technicalSkillsInput,
                onValueChange = { technicalSkillsInput = it },
                label = { Text("Technical Skills Gained & Demonstrated") },
                modifier = Modifier.fillMaxWidth().testTag("input_formd_technical"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = recommendationsInput,
                onValueChange = { recommendationsInput = it },
                label = { Text("Recommendations for Academic Coursework") },
                modifier = Modifier.fillMaxWidth().testTag("input_formd_recommendations"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Objectives Met Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Were Internship Objectives Fully Met?",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = if (objectivesMet) "Yes - Candidate satisfied all target competencies." else "No - Further remediation required.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                        )
                    }
                    Switch(
                        checked = objectivesMet,
                        onCheckedChange = { objectivesMet = it },
                        modifier = Modifier.testTag("switch_formd_objectives")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = commentsInput,
                onValueChange = { commentsInput = it },
                label = { Text("Other Remarks / Confidential Supervisor Comments") },
                modifier = Modifier.fillMaxWidth().testTag("input_formd_comments"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.submitAssessment(
                        overallRating = averageScore,
                        overallAssessment = "Superior overall performance with outstanding technical and behavioral competence.",
                        strengths = strengthsInput,
                        recommendations = recommendationsInput,
                        technicalSkills = technicalSkillsInput,
                        objectivesMet = objectivesMet,
                        comments = commentsInput
                    )
                    showSuccessMessage = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("button_submit_formd")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit & Lock Official Form D Assessment", fontWeight = FontWeight.Bold)
            }

            if (showSuccessMessage) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ Official Form D Assessment saved and transmitted to Limkokwing LEAP Directorate.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF166534),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PaperVsDigitalBadge(
                paperProcess = "Paper Form D completed by hand, physically stamped, and hand-delivered by students to university.",
                digitalProcess = "Digitized 18-criteria assessment with automated calculation, digital supervisor sign-off, and instant audit trail."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FormDCriteriaCard(
    item: AssessmentItemEntity,
    onRatingChanged: (Int) -> Unit
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
                Text(
                    text = item.criterionName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LeapNavyPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (item.rating) {
                        5 -> Color(0xFFDCFCE7)
                        4 -> Color(0xFFEFF6FF)
                        3 -> Color(0xFFFEF3C7)
                        else -> Color(0xFFFEE2E2)
                    }
                ) {
                    Text(
                        text = when (item.rating) {
                            5 -> "Superior (5)"
                            4 -> "Above Avg (4)"
                            3 -> "Average (3)"
                            2 -> "Below Avg (2)"
                            else -> "Unsatisfactory (1)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color(0xFF0F172A)
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            RatingBar5(
                rating = item.rating,
                onRatingSelected = onRatingChanged,
                modifier = Modifier.testTag("criteria_rating_${item.id}")
            )
        }
    }
}

@Composable
fun SupervisorFeedbackScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val feedbacks by viewModel.supervisorFeedbacks.collectAsState()

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Supervisor Feedback Log",
                subtitle = "Historical Feedback & Review Notes",
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
            Text(
                text = "Feedback History",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            feedbacks.forEach { feedback ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Week ${feedback.weekNumber} Feedback",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LeapNavyPrimary
                                )
                            )
                            StatusChip(status = feedback.status)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = feedback.content,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF334155))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Target: ${feedback.targetType} • Date: ${feedback.createdAt}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
