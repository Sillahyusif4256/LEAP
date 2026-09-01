package com.example.ui.screens.student

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
import com.example.ui.components.LeapTopAppBar
import com.example.ui.components.PaperVsDigitalBadge
import com.example.ui.components.StatusChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel

@Composable
fun InternshipReportScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val report by viewModel.internshipReport.collectAsState()
    val authState by viewModel.authState.collectAsState()

    var showUploadModal by remember { mutableStateOf(false) }
    var reportTitle by remember { mutableStateOf(report?.title ?: "LEAP Internship Comprehensive Industrial Experience & Software Engineering Report") }
    var fileName by remember { mutableStateOf(report?.fileName ?: "Mohamed_Kamara_LEAP_Final_Report_2026.pdf") }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Internship Final Report",
                subtitle = "Academic Dissertation Documentation",
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
            // Status Header
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
                            text = "Dissertation Report Status",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        StatusChip(status = report?.status ?: "Submitted")
                    }
                    Text(
                        text = "Version ${report?.version ?: 1} | Uploaded: ${report?.uploadDate ?: "02-Mar-2026"}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Document Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "PDF File",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = report?.fileName ?: fileName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LeapNavyPrimary
                                )
                            )
                            Text(
                                text = "Size: ${report?.fileSize ?: "4.2 MB"} • Format: PDF/A Academic Standard",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Report Title:",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                    )
                    Text(
                        text = report?.title ?: reportTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showUploadModal = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_upload_new_report_version")
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload New Version / PDF")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Coordinator & Supervisor Review Comments
            Text(
                text = "Academic Review & Feedback",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "LEAP Directorate Reviewer (Dr. Fatmata Sesay):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LeapBlue
                        )
                    )
                    Text(
                        text = report?.coordinatorFeedback ?: "Report draft demonstrates robust technical depth in mobile software engineering and Room database architecture. Ensure executive summary includes organizational chart before final binding.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155), lineHeight = 18.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Workplace Supervisor (Ing. David Koroma):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = StatusActive
                        )
                    )
                    Text(
                        text = report?.supervisorFeedback ?: "Approved. Technical chapters accurately describe the industrial codebase contributions at Tech Solutions SL.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155), lineHeight = 18.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PaperVsDigitalBadge(
                paperProcess = "Three physically printed and comb-bound copies submitted across campus counters.",
                digitalProcess = "Encrypted digital PDF upload with automated version tracking and unified supervisor/coordinator annotation."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showUploadModal) {
        AlertDialog(
            onDismissRequest = { showUploadModal = false },
            title = { Text("Upload Internship Report") },
            text = {
                Column {
                    OutlinedTextField(
                        value = reportTitle,
                        onValueChange = { reportTitle = it },
                        label = { Text("Dissertation Report Title") },
                        modifier = Modifier.fillMaxWidth().testTag("input_report_modal_title")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("File Name (e.g., student_report.pdf)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_report_modal_filename")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.uploadInternshipReport(reportTitle, fileName, "4.5 MB")
                        showUploadModal = false
                    },
                    modifier = Modifier.testTag("confirm_upload_report")
                ) {
                    Text("Upload Document")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun NotificationsScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Notifications & Alerts",
                subtitle = "LEAP System Broadcasts & Deadlines",
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
                text = "Recent Notifications (${notifications.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            notifications.forEach { notif ->
                NotificationCard(
                    notification = notif,
                    onMarkRead = { viewModel.markNotificationAsRead(notif.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NotificationCard(
    notification: com.example.data.local.entities.NotificationEntity,
    onMarkRead: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else Color(0xFFEFF6FF)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (notification.type) {
                            "FEEDBACK" -> LeapBlue.copy(alpha = 0.2f)
                            "DEADLINE" -> StatusAlert.copy(alpha = 0.2f)
                            "ASSESSMENT" -> StatusActive.copy(alpha = 0.2f)
                            else -> LeapGoldAccent.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.type) {
                        "FEEDBACK" -> Icons.Default.Comment
                        "DEADLINE" -> Icons.Default.Timer
                        "ASSESSMENT" -> Icons.Default.Star
                        else -> Icons.Default.Campaign
                    },
                    contentDescription = null,
                    tint = LeapNavyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LeapNavyPrimary
                        )
                    )
                    Text(
                        text = notification.timeAgo,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF334155)
                    )
                )
            }
        }
    }
}
