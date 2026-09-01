package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.local.entities.SelfEvaluationEntity
import com.example.ui.components.LeapTopAppBar
import com.example.ui.components.PaperVsDigitalBadge
import com.example.ui.components.RatingBar5
import com.example.ui.components.StatusChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel

@Composable
fun StudentSelfEvaluationScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val selfEvaluation by viewModel.selfEvaluation.collectAsState()

    var selectedPart by remember { mutableStateOf(1) }

    // Part 1 States (1-5 ratings)
    var goalAchievement by remember { mutableStateOf(selfEvaluation?.q1AchievedGoals ?: 5) }
    var academicRelevance by remember { mutableStateOf(selfEvaluation?.q2RelatedFieldTraining ?: 5) }
    var applicationOfKnowledge by remember { mutableStateOf(selfEvaluation?.q3CompletedResponsibilities ?: 4) }
    var skillsDevelopment by remember { mutableStateOf(selfEvaluation?.q4AdequateWorkload ?: 5) }
    var problemSolving by remember { mutableStateOf(selfEvaluation?.q5MetExpectation ?: 4) }
    var technicalCompetence by remember { mutableStateOf(selfEvaluation?.q6AcademicPreparation ?: 5) }
    var communicationSkills by remember { mutableStateOf(selfEvaluation?.q7UsefulExperience ?: 4) }
    var overallSatisfaction by remember { mutableStateOf(if (selfEvaluation?.q8ConsiderPermanentEmployment == false) 2 else 5) }

    // Part 2 States (1-5 ratings)
    var workEnvironment by remember { mutableStateOf(selfEvaluation?.envOrgStructure ?: 5) }
    var supportAndFeedback by remember { mutableStateOf(selfEvaluation?.supFromSupervisor ?: 5) }
    var opportunityToBeCreative by remember { mutableStateOf(selfEvaluation?.creatConsiderIdeas ?: 4) }
    var interactionWithOthers by remember { mutableStateOf(selfEvaluation?.interactTeamProject ?: 5) }
    var leapOfficeSupport by remember { mutableStateOf(selfEvaluation?.offInfoProvided ?: 4) }

    // Part 3 States (Text & Overall)
    var specificTasks by remember { mutableStateOf(selfEvaluation?.specificTasksAssigned ?: "Architected Android mobile MVVM screens, integrated Room SQLite database for offline operations, participated in daily stand-ups and code reviews.") }
    var challenges by remember { mutableStateOf(selfEvaluation?.challengesOrLimitations ?: "Adapting to high-concurrency database queries and initial unfamiliarity with Jetpack Compose state reconciliation.") }
    var impactOfChallenges by remember { mutableStateOf(selfEvaluation?.impactOfChallenges ?: "Enhanced problem-solving perseverance, deepened debugging skill with Android Profiler, and strengthened documentation habits.") }
    var keySkillsGained by remember { mutableStateOf(selfEvaluation?.keySkillsGained ?: "Jetpack Compose, Room persistence, Git feature branches, REST API integration, professional workplace communication.") }
    var careerInfluence by remember { mutableStateOf(selfEvaluation?.influenceOnCareerPlans ?: "Solidified my dedication to becoming a Lead Mobile Software Engineer in Sierra Leone's growing fintech sector.") }
    var whatToChange by remember { mutableStateOf(selfEvaluation?.whatWouldChangeAndWhy ?: "I would allocate more time during Week 1 to deepen automated unit testing practices.") }
    var overallEvaluation by remember { mutableStateOf(selfEvaluation?.overallEvaluation ?: "Superior") }
    var additionalComments by remember { mutableStateOf(selfEvaluation?.additionalComments ?: "The LEAP internship experience at Tech Solutions SL has exceeded my expectations and bridged academic theory with industrial practice.") }

    var showSuccessSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Form D2: Self-Evaluation",
                subtitle = "Student Internship Assessment",
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
                            text = "LEAP Form D2 (Official)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        StatusChip(status = if (selfEvaluation?.isSubmitted == true) "Submitted" else "Draft")
                    }
                    Text(
                        text = "The Student Self-Evaluation Form must be completed prior to final dissertation submission and verified by the LEAP Coordinator.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Part Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1 to "Part 1: Goals", 2 to "Part 2: Org & Office", 3 to "Part 3: Reflection").forEach { (partNum, label) ->
                    val isSelected = selectedPart == partNum
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) LeapNavyPrimary else Color(0xFFE2E8F0),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedPart = partNum }
                            .testTag("part_tab_$partNum")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF475569)
                            ),
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedPart) {
                1 -> {
                    Text(
                        text = "Part 1: Learning Goals & Overall Experience",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LeapNavyPrimary)
                    )
                    Text(
                        text = "Rate each statement on a 1 (Strongly Disagree) to 5 (Strongly Agree) scale:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    EvaluationRatingItem(label = "1. Achievement of initial learning goals", rating = goalAchievement, onRating = { goalAchievement = it })
                    EvaluationRatingItem(label = "2. Relevance of internship to academic studies", rating = academicRelevance, onRating = { academicRelevance = it })
                    EvaluationRatingItem(label = "3. Opportunity to apply classroom theoretical knowledge", rating = applicationOfKnowledge, onRating = { applicationOfKnowledge = it })
                    EvaluationRatingItem(label = "4. Development of professional & career skills", rating = skillsDevelopment, onRating = { skillsDevelopment = it })
                    EvaluationRatingItem(label = "5. Enhancement of analytical & problem-solving abilities", rating = problemSolving, onRating = { problemSolving = it })
                    EvaluationRatingItem(label = "6. Growth in practical technical competence", rating = technicalCompetence, onRating = { technicalCompetence = it })
                    EvaluationRatingItem(label = "7. Improvement in interpersonal & communication skills", rating = communicationSkills, onRating = { communicationSkills = it })
                    EvaluationRatingItem(label = "8. Overall personal internship satisfaction", rating = overallSatisfaction, onRating = { overallSatisfaction = it })
                }
                2 -> {
                    Text(
                        text = "Part 2: Work Environment & Institutional Support",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LeapNavyPrimary)
                    )
                    Text(
                        text = "Rate each area from 1 (Unsatisfactory) to 5 (Outstanding):",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    EvaluationRatingItem(label = "1. Host Workplace Environment & Culture", rating = workEnvironment, onRating = { workEnvironment = it })
                    EvaluationRatingItem(label = "2. Supervisor Guidance, Support & Constructive Feedback", rating = supportAndFeedback, onRating = { supportAndFeedback = it })
                    EvaluationRatingItem(label = "3. Opportunity to Innovate & Express Creativity", rating = opportunityToBeCreative, onRating = { opportunityToBeCreative = it })
                    EvaluationRatingItem(label = "4. Professional Interaction with Colleagues & Clients", rating = interactionWithOthers, onRating = { interactionWithOthers = it })
                    EvaluationRatingItem(label = "5. Administrative Support from Limkokwing LEAP Office", rating = leapOfficeSupport, onRating = { leapOfficeSupport = it })
                }
                3 -> {
                    Text(
                        text = "Part 3: Detailed Self-Reflective Assessment",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LeapNavyPrimary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = specificTasks,
                        onValueChange = { specificTasks = it },
                        label = { Text("Specific Tasks & Responsibilities Performed") },
                        modifier = Modifier.fillMaxWidth().testTag("input_formd2_tasks"),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = challenges,
                        onValueChange = { challenges = it },
                        label = { Text("Challenges / Limitations Faced") },
                        modifier = Modifier.fillMaxWidth().testTag("input_formd2_challenges"),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = impactOfChallenges,
                        onValueChange = { impactOfChallenges = it },
                        label = { Text("Impact of Challenges on Your Growth") },
                        modifier = Modifier.fillMaxWidth().testTag("input_formd2_impact"),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = keySkillsGained,
                        onValueChange = { keySkillsGained = it },
                        label = { Text("Key Technical & Professional Skills Gained") },
                        modifier = Modifier.fillMaxWidth().testTag("input_formd2_skills"),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = careerInfluence,
                        onValueChange = { careerInfluence = it },
                        label = { Text("Influence on Thinking & Future Career Plans") },
                        modifier = Modifier.fillMaxWidth().testTag("input_formd2_career"),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = whatToChange,
                        onValueChange = { whatToChange = it },
                        label = { Text("What Would You Change and Why?") },
                        modifier = Modifier.fillMaxWidth().testTag("input_formd2_change"),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Overall Evaluation Selector
                    Text(
                        text = "Overall Self-Evaluation Grade",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Superior", "Excellent", "Satisfactory", "Unsatisfactory").forEach { grade ->
                            val isSel = overallEvaluation == grade
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) LeapBlue else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { overallEvaluation = grade }
                                    .testTag("grade_btn_$grade")
                            ) {
                                Text(
                                    text = grade,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color(0xFF334155),
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = additionalComments,
                        onValueChange = { additionalComments = it },
                        label = { Text("Additional Comments") },
                        modifier = Modifier.fillMaxWidth().testTag("input_formd2_comments"),
                        minLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save & Submit Button
            Button(
                onClick = {
                    val updated = (selfEvaluation ?: SelfEvaluationEntity(studentId = 1)).copy(
                        q1AchievedGoals = goalAchievement,
                        q2RelatedFieldTraining = academicRelevance,
                        q3CompletedResponsibilities = applicationOfKnowledge,
                        q4AdequateWorkload = skillsDevelopment,
                        q5MetExpectation = problemSolving,
                        q6AcademicPreparation = technicalCompetence,
                        q7UsefulExperience = communicationSkills,
                        q8ConsiderPermanentEmployment = overallSatisfaction >= 3,
                        envOrgStructure = workEnvironment,
                        supFromSupervisor = supportAndFeedback,
                        creatConsiderIdeas = opportunityToBeCreative,
                        interactTeamProject = interactionWithOthers,
                        offInfoProvided = leapOfficeSupport,
                        specificTasksAssigned = specificTasks,
                        challengesOrLimitations = challenges,
                        impactOfChallenges = impactOfChallenges,
                        keySkillsGained = keySkillsGained,
                        influenceOnCareerPlans = careerInfluence,
                        whatWouldChangeAndWhy = whatToChange,
                        overallEvaluation = overallEvaluation,
                        additionalComments = additionalComments,
                        isSubmitted = true,
                        submittedDate = "Today"
                    )
                    viewModel.saveSelfEvaluation(updated)
                    showSuccessSnackbar = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("button_save_formd2")
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save & Submit Form D2 Self-Evaluation", fontWeight = FontWeight.Bold)
            }

            if (showSuccessSnackbar) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ Form D2 Self-Evaluation successfully saved and locked for LEAP coordinator audit.",
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
                paperProcess = "Form D2 paper forms filled by hand with physical stapling onto the final bound internship report.",
                digitalProcess = "Multi-part digital self-evaluation with automated Likert scale computation and coordinator archiving."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EvaluationRatingItem(
    label: String,
    rating: Int,
    onRating: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0F172A)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            RatingBar5(
                rating = rating,
                onRatingSelected = onRating
            )
        }
    }
}
