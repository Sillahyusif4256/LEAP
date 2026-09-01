package com.example.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LeapTopAppBar
import com.example.ui.theme.LeapCyan
import com.example.ui.theme.LeapGoldAccent
import com.example.ui.theme.LeapNavyPrimary
import com.example.ui.viewmodel.LeapViewModel

@Composable
fun AiInternshipMatchScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val matches by viewModel.aiMatches.collectAsState()
    val isMatching by viewModel.isMatching.collectAsState()

    var programme by remember { mutableStateOf("BSc (Hons) Information Technology") }
    var skills by remember { mutableStateOf("Kotlin, Android Jetpack Compose, Room SQLite, REST APIs, Git") }
    var interests by remember { mutableStateOf("Mobile Application Engineering, Enterprise Fintech, Offline Database Systems") }
    var department by remember { mutableStateOf("Software Engineering / ICT") }
    var location by remember { mutableStateOf("Freetown, Sierra Leone") }
    var experience by remember { mutableStateOf("Academic coursework, mobile lab prototypes, and university group projects.") }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "AI Placement Recommendation",
                subtitle = "Skill & Industry Matching Engine",
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Hub, contentDescription = null, tint = LeapGoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Smart Placement Matchmaker",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Calculates compatibility scores against accredited Sierra Leone organizations using your skills, career goals, and department preferences.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 16.sp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Input Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Your Candidate Profile Inputs",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LeapNavyPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = programme,
                            onValueChange = { programme = it },
                            label = { Text("Academic Programme") },
                            modifier = Modifier.fillMaxWidth().testTag("input_match_programme"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = skills,
                            onValueChange = { skills = it },
                            label = { Text("Key Technical & Professional Skills") },
                            modifier = Modifier.fillMaxWidth().testTag("input_match_skills"),
                            minLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = interests,
                            onValueChange = { interests = it },
                            label = { Text("Career Interests & Industry Aspirations") },
                            modifier = Modifier.fillMaxWidth().testTag("input_match_interests"),
                            minLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = department,
                            onValueChange = { department = it },
                            label = { Text("Preferred Department") },
                            modifier = Modifier.fillMaxWidth().testTag("input_match_dept"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Preferred Location") },
                            modifier = Modifier.fillMaxWidth().testTag("input_match_location"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.generateInternshipMatches(programme, skills, interests, department, location, experience)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("button_run_ai_match")
                        ) {
                            if (isMatching) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Smart Match Recommendations", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Results Section
            if (matches.isNotEmpty()) {
                item {
                    Text(
                        text = "Recommended Placement Matches (${matches.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LeapNavyPrimary
                        )
                    )
                    Text(
                        text = "Advisory ranking only. Official placements must be verified by the LEAP Directorate.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(matches) { match ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = match.organizationName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = LeapNavyPrimary
                                        )
                                    )
                                    Text(
                                        text = match.department,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = LeapCyan,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "${match.matchScore}% Match",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF166534)
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = match.reason,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF334155),
                                    lineHeight = 18.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Required Competencies:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                match.requiredSkills.forEach { skill ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFF1F5F9)
                                    ) {
                                        Text(
                                            text = skill,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF475569),
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "📍 ${match.location}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                            )
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
