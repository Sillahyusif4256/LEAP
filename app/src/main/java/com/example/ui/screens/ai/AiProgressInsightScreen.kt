package com.example.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.RiskLevel
import com.example.ui.components.LeapTopAppBar
import com.example.ui.theme.LeapBlue
import com.example.ui.theme.LeapGoldAccent
import com.example.ui.theme.LeapNavyPrimary
import com.example.ui.viewmodel.LeapViewModel

@Composable
fun AiProgressInsightScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val insight by viewModel.aiInsight.collectAsState()
    val isGenerating by viewModel.isGeneratingInsight.collectAsState()
    val student by viewModel.currentStudent.collectAsState()

    LaunchedEffect(student) {
        if (student != null && insight == null) {
            viewModel.generateProgressInsight(student!!)
        }
    }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "AI Progress Insights",
                subtitle = "Cohort Compliance Risk Analytics",
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
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LeapNavyPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LEAP Coordinator AI Early Warning System",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Monitors daily log cadence, weekly report submissions, supervisor feedbacks, and missing official forms across the student lifecycle.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 16.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isGenerating) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = LeapGoldAccent)
                }
            } else if (insight != null) {
                val ins = insight!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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
                                text = "Candidate: ${ins.studentName}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LeapNavyPrimary
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (ins.riskStatus) {
                                    RiskLevel.ON_TRACK -> Color(0xFFDCFCE7)
                                    RiskLevel.NEEDS_ATTENTION -> Color(0xFFFEF3C7)
                                    RiskLevel.AT_RISK -> Color(0xFFFEE2E2)
                                }
                            ) {
                                Text(
                                    text = when (ins.riskStatus) {
                                        RiskLevel.ON_TRACK -> "ON TRACK"
                                        RiskLevel.NEEDS_ATTENTION -> "NEEDS ATTENTION"
                                        RiskLevel.AT_RISK -> "AT RISK"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = when (ins.riskStatus) {
                                            RiskLevel.ON_TRACK -> Color(0xFF166534)
                                            RiskLevel.NEEDS_ATTENTION -> Color(0xFF92400E)
                                            RiskLevel.AT_RISK -> Color(0xFF991B1B)
                                        }
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "AI Lifecycle Analysis Summary:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                        )
                        Text(
                            text = ins.progressSummary,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF0F172A), lineHeight = 20.sp)
                        )

                        if (ins.missingRequirements.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Outstanding / Pending Requirements:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                            )
                            ins.missingRequirements.forEach { req ->
                                Text("• $req", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF991B1B)))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Recommended Coordinator Follow-up Action:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = LeapBlue)
                        )
                        Text(
                            text = ins.suggestedFollowUp,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF1E3A8A),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
