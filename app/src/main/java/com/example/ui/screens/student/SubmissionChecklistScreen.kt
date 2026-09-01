package com.example.ui.screens.student

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.LeapTopAppBar
import com.example.ui.components.PaperVsDigitalBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel

@Composable
fun SubmissionChecklistScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit,
    onNavigateToScanner: (String?) -> Unit = {},
    onNavigateToArchive: () -> Unit = {}
) {
    val checklist by viewModel.checklist.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val scannedDocs by viewModel.scannedDocuments.collectAsStateWithLifecycle()

    val isSupervisor = authState.selectedRole == "SUPERVISOR" || authState.selectedRole == "COORDINATOR"

    val c = checklist
    val studentCompletedCount = listOfNotNull(
        c?.formDStudentCheck,
        c?.selfEvalStudentCheck,
        c?.logBookStudentCheck,
        c?.actionPlanStudentCheck,
        c?.formA2StudentCheck,
        c?.formA3StudentCheck,
        c?.formBStudentCheck,
        c?.reportStudentCheck
    ).count { it }

    val supervisorVerifiedCount = listOfNotNull(
        c?.formDSupervisorCheck,
        c?.selfEvalSupervisorCheck,
        c?.logBookSupervisorCheck,
        c?.actionPlanSupervisorCheck,
        c?.formA2SupervisorCheck,
        c?.formA3SupervisorCheck,
        c?.formBSupervisorCheck,
        c?.reportSupervisorCheck
    ).count { it }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Submission Checklist",
                subtitle = "Official 8-Item LEAP Final Compliance",
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
            // Overall Progress Summary Card
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
                                text = "LEAP Final Checklist",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Submission Status: ${if (studentCompletedCount == 8) "Completed" else "In Progress"}",
                                style = MaterialTheme.typography.bodySmall.copy(color = LeapGoldAccent)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = LeapGoldAccent
                        ) {
                            Text(
                                text = "$studentCompletedCount/8",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = LeapNavyPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { studentCompletedCount / 8f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = LeapGoldAccent,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Student Checklist: $studentCompletedCount of 8 items",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCBD5E1))
                        )
                        Text(
                            text = "Supervisor Verified: $supervisorVerifiedCount of 8",
                            style = MaterialTheme.typography.labelSmall.copy(color = LeapGoldLight)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Document Scanner Hero Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, LeapNavyPrimary.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(LeapNavyPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DocumentScanner,
                                contentDescription = null,
                                tint = LeapNavyPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Scan Signed Forms",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${scannedDocs.size} documents archived in device",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onNavigateToArchive,
                            modifier = Modifier.testTag("btn_checklist_open_archive")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderSpecial,
                                contentDescription = "View Archive",
                                tint = LeapNavyPrimary
                            )
                        }

                        Button(
                            onClick = { onNavigateToScanner(null) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LeapNavyPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_checklist_camera_scan")
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Official LEAP Required Documents",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
            Text(
                text = "Both student and workplace supervisor/coordinator verify each requirement before clearance.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ChecklistRowItem(
                itemNumber = 1,
                title = "1. Form D Evaluation",
                subtitle = "Official Supervisor Assessment (18 criteria scale)",
                formCode = "FORM_D",
                hasScannedDoc = scannedDocs.any { it.documentType == "FORM_D" },
                isStudentChecked = c?.formDStudentCheck ?: true,
                isSupervisorChecked = c?.formDSupervisorCheck ?: true,
                isSupervisorMode = isSupervisor,
                onToggleStudent = { viewModel.toggleChecklistItem("formD", false) },
                onToggleSupervisor = { viewModel.toggleChecklistItem("formD", true) },
                onScanClick = { onNavigateToScanner("FORM_D") }
            )

            ChecklistRowItem(
                itemNumber = 2,
                title = "2. Self-Evaluation Form (Form D2)",
                subtitle = "Parts 1, 2 & 3 student reflective evaluation",
                formCode = "FORM_D2",
                hasScannedDoc = scannedDocs.any { it.documentType == "FORM_D2" },
                isStudentChecked = c?.selfEvalStudentCheck ?: true,
                isSupervisorChecked = c?.selfEvalSupervisorCheck ?: true,
                isSupervisorMode = isSupervisor,
                onToggleStudent = { viewModel.toggleChecklistItem("selfEval", false) },
                onToggleSupervisor = { viewModel.toggleChecklistItem("selfEval", true) },
                onScanClick = { onNavigateToScanner("FORM_D2") }
            )

            ChecklistRowItem(
                itemNumber = 3,
                title = "3. Daily Log Sheets (Weeks 1-12)",
                subtitle = "Complete daily activities with 7 required fields",
                formCode = "LOGBOOK_SHEET",
                hasScannedDoc = scannedDocs.any { it.documentType == "LOGBOOK_SHEET" },
                isStudentChecked = c?.logBookStudentCheck ?: true,
                isSupervisorChecked = c?.logBookSupervisorCheck ?: true,
                isSupervisorMode = isSupervisor,
                onToggleStudent = { viewModel.toggleChecklistItem("logBook", false) },
                onToggleSupervisor = { viewModel.toggleChecklistItem("logBook", true) },
                onScanClick = { onNavigateToScanner("LOGBOOK_SHEET") }
            )

            ChecklistRowItem(
                itemNumber = 4,
                title = "4. LEAP Action Plan (Form A)",
                subtitle = "Approved milestones with company official stamp",
                formCode = "ACTION_PLAN",
                hasScannedDoc = scannedDocs.any { it.documentType == "ACTION_PLAN" },
                isStudentChecked = c?.actionPlanStudentCheck ?: true,
                isSupervisorChecked = c?.actionPlanSupervisorCheck ?: true,
                isSupervisorMode = isSupervisor,
                onToggleStudent = { viewModel.toggleChecklistItem("actionPlan", false) },
                onToggleSupervisor = { viewModel.toggleChecklistItem("actionPlan", true) },
                onScanClick = { onNavigateToScanner("ACTION_PLAN") }
            )

            ChecklistRowItem(
                itemNumber = 5,
                title = "5. Form A2",
                subtitle = "Host Organization Acceptance & Registration",
                formCode = "FORM_A2",
                hasScannedDoc = scannedDocs.any { it.documentType == "FORM_A2" },
                isStudentChecked = c?.formA2StudentCheck ?: true,
                isSupervisorChecked = c?.formA2SupervisorCheck ?: true,
                isSupervisorMode = isSupervisor,
                onToggleStudent = { viewModel.toggleChecklistItem("formA2", false) },
                onToggleSupervisor = { viewModel.toggleChecklistItem("formA2", true) },
                onScanClick = { onNavigateToScanner("FORM_A2") }
            )

            ChecklistRowItem(
                itemNumber = 6,
                title = "6. Form A3",
                subtitle = "Student Commencement Confirmation letter",
                formCode = "FORM_A3",
                hasScannedDoc = scannedDocs.any { it.documentType == "FORM_A3" },
                isStudentChecked = c?.formA3StudentCheck ?: true,
                isSupervisorChecked = c?.formA3SupervisorCheck ?: true,
                isSupervisorMode = isSupervisor,
                onToggleStudent = { viewModel.toggleChecklistItem("formA3", false) },
                onToggleSupervisor = { viewModel.toggleChecklistItem("formA3", true) },
                onScanClick = { onNavigateToScanner("FORM_A3") }
            )

            ChecklistRowItem(
                itemNumber = 7,
                title = "7. Form B",
                subtitle = "Mid-Term Internship Progress Report",
                formCode = "FORM_B",
                hasScannedDoc = scannedDocs.any { it.documentType == "FORM_B" },
                isStudentChecked = c?.formBStudentCheck ?: true,
                isSupervisorChecked = c?.formBSupervisorCheck ?: true,
                isSupervisorMode = isSupervisor,
                onToggleStudent = { viewModel.toggleChecklistItem("formB", false) },
                onToggleSupervisor = { viewModel.toggleChecklistItem("formB", true) },
                onScanClick = { onNavigateToScanner("FORM_B") }
            )

            ChecklistRowItem(
                itemNumber = 8,
                title = "8. Final Internship Report (Typed)",
                subtitle = "Bound academic dissertation format document",
                formCode = "REPORT_CLEARANCE",
                hasScannedDoc = scannedDocs.any { it.documentType == "REPORT_CLEARANCE" },
                isStudentChecked = c?.reportStudentCheck ?: false,
                isSupervisorChecked = c?.reportSupervisorCheck ?: false,
                isSupervisorMode = isSupervisor,
                onToggleStudent = { viewModel.toggleChecklistItem("report", false) },
                onToggleSupervisor = { viewModel.toggleChecklistItem("report", true) },
                onScanClick = { onNavigateToScanner("REPORT_CLEARANCE") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaperVsDigitalBadge(
                paperProcess = "Physical paper checklist sheet signed with ballpoint pens and submitted inside cardboard binder.",
                digitalProcess = "Dual-stakeholder verified digital checklist with auditable timestamps and real-time coordinator view."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ChecklistRowItem(
    itemNumber: Int,
    title: String,
    subtitle: String,
    formCode: String,
    hasScannedDoc: Boolean,
    isStudentChecked: Boolean,
    isSupervisorChecked: Boolean,
    isSupervisorMode: Boolean,
    onToggleStudent: () -> Unit,
    onToggleSupervisor: () -> Unit,
    onScanClick: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LeapNavyPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (hasScannedDoc) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFF93C5FD))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = LeapNavyPrimary,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "SCANNED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LeapNavyPrimary,
                                        fontSize = 8.sp
                                    )
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isStudentChecked && isSupervisorChecked) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = if (isStudentChecked && isSupervisorChecked) "VERIFIED" else "PENDING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isStudentChecked && isSupervisorChecked) Color(0xFF166534) else Color(0xFF92400E),
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Student Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onToggleStudent() }
                ) {
                    Checkbox(
                        checked = isStudentChecked,
                        onCheckedChange = { onToggleStudent() },
                        modifier = Modifier.testTag("checklist_student_check_$itemNumber")
                    )
                    Text(
                        text = "Student Check",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isStudentChecked) Color(0xFF166534) else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Scan Action Button
                OutlinedButton(
                    onClick = onScanClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_item_scan_$itemNumber")
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (hasScannedDoc) "Re-scan" else "Scan", fontSize = 11.sp)
                }

                // Supervisor Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = isSupervisorMode) { onToggleSupervisor() }
                ) {
                    Checkbox(
                        checked = isSupervisorChecked,
                        onCheckedChange = { if (isSupervisorMode) onToggleSupervisor() },
                        enabled = isSupervisorMode,
                        modifier = Modifier.testTag("checklist_supervisor_check_$itemNumber")
                    )
                    Text(
                        text = "Supervisor",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSupervisorChecked) LeapBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
