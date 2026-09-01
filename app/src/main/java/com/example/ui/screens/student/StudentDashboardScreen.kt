package com.example.ui.screens.student

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LeapMilestoneProgressDashboard
import com.example.ui.components.PaperVsDigitalBadge
import com.example.ui.components.StatusChip
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel

data class StudentDashboardCardItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val bubbleBg: Color,
    val bubbleTint: Color,
    val isHighlighted: Boolean = false,
    val badge: String? = null,
    val testTag: String
)

data class InternshipDeadlineItem(
    val id: String,
    val title: String,
    val category: String, // "Report", "Form", "Final", "Milestone"
    val dueDateText: String,
    val daysRemaining: Int,
    val urgencyText: String,
    val urgencyLevel: DeadlineUrgency, // URGENT, MEDIUM, SCHEDULED
    val description: String,
    val route: String,
    val actionLabel: String,
    val icon: ImageVector,
    val testTag: String
)

enum class DeadlineUrgency {
    URGENT,
    MEDIUM,
    SCHEDULED
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StudentDashboardScreen(
    viewModel: LeapViewModel,
    onNavigate: (String) -> Unit,
    onSwitchRole: () -> Unit
) {
    val student by viewModel.currentStudent.collectAsState()
    val currentApp by viewModel.currentApplication.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val pendingSync by viewModel.pendingSyncCount.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val studentName = student?.name ?: "Mohamed Kamara"
    val studentIdCode = student?.studentIdCode ?: "LKW-SL-DEMO001"
    val programme = student?.programme ?: "BSc (Hons) Information Technology"
    val orgName = "Tech Solutions SL Ltd."
    val supervisorName = "Ing. David Koroma"
    val supervisorRole = "Lead Enterprise Systems Architect"
    val placementRole = "Enterprise Cloud & Android Engineering Intern"
    val progress = student?.progressPercentage ?: 68
    val currentWeek = student?.currentWeek ?: 8
    val totalWeeks = student?.totalWeeks ?: 12
    val hoursLogged = 272
    val totalHoursRequired = 400

    val unreadNotifs = notifications.count { !it.isRead }

    // Deadline filter tab state
    var selectedDeadlineFilter by remember { mutableStateOf("All") }

    // List of real upcoming deadlines
    val upcomingDeadlines = remember {
        listOf(
            InternshipDeadlineItem(
                id = "d1",
                title = "Weekly Progress Report #8",
                category = "Reports",
                dueDateText = "Friday, 4 Sep 2026 • 5:00 PM",
                daysRemaining = 2,
                urgencyText = "Due in 2 days",
                urgencyLevel = DeadlineUrgency.URGENT,
                description = "Submit comprehensive reflection of sprint tasks, database optimizations, and team standups.",
                route = Screen.WeeklyReports.route,
                actionLabel = "Submit Report",
                icon = Icons.Default.CalendarViewWeek,
                testTag = "btn_deadline_report8"
            ),
            InternshipDeadlineItem(
                id = "d2",
                title = "Form D2 Student Self-Evaluation (Part 2)",
                category = "Forms",
                dueDateText = "Tuesday, 8 Sep 2026 • 11:59 PM",
                daysRemaining = 5,
                urgencyText = "Due in 5 days",
                urgencyLevel = DeadlineUrgency.MEDIUM,
                description = "Mandatory mid-placement self-appraisal covering 10 professional and technical domains.",
                route = Screen.StudentSelfEvaluation.route,
                actionLabel = "Complete Self-Evaluation",
                icon = Icons.Default.Assignment,
                testTag = "btn_deadline_form_d2"
            ),
            InternshipDeadlineItem(
                id = "d3",
                title = "Supervisor Form D Mid-Term Sign-Off",
                category = "Forms",
                dueDateText = "Friday, 11 Sep 2026 • 5:00 PM",
                daysRemaining = 9,
                urgencyText = "Due in 9 days",
                urgencyLevel = DeadlineUrgency.SCHEDULED,
                description = "Host supervisor assessment and digital endorsement of student workplace competencies.",
                route = Screen.SubmissionChecklist.route,
                actionLabel = "Check Sign-off Status",
                icon = Icons.Default.VerifiedUser,
                testTag = "btn_deadline_supervisor_form"
            ),
            InternshipDeadlineItem(
                id = "d4",
                title = "Final Typed Internship Report & Logbook Binder",
                category = "Final",
                dueDateText = "Friday, 25 Sep 2026 • 5:00 PM",
                daysRemaining = 23,
                urgencyText = "Week 12 Milestone",
                urgencyLevel = DeadlineUrgency.SCHEDULED,
                description = "Comprehensive dissertation-style industrial placement final submission with certified annexures.",
                route = Screen.InternshipReport.route,
                actionLabel = "View Report Draft",
                icon = Icons.Default.Article,
                testTag = "btn_deadline_final_report"
            )
        )
    }

    val filteredDeadlines = remember(selectedDeadlineFilter) {
        if (selectedDeadlineFilter == "All") {
            upcomingDeadlines
        } else {
            upcomingDeadlines.filter { it.category == selectedDeadlineFilter }
        }
    }

    val dashboardCards = listOf(
        StudentDashboardCardItem(
            title = "Daily Log Book",
            subtitle = "5 entries logged",
            icon = Icons.Default.MenuBook,
            route = Screen.DailyLogBook.route,
            bubbleBg = PurpleBubbleBg,
            bubbleTint = PurpleBubbleTint,
            testTag = "card_daily_logs"
        ),
        StudentDashboardCardItem(
            title = "Apply for Placement",
            subtitle = "Multi-Step CV Form",
            icon = Icons.Default.AppRegistration,
            route = Screen.InternshipApplication.route,
            bubbleBg = Color(0xFFE0F2FE),
            bubbleTint = Color(0xFF0284C7),
            badge = "Step Form",
            testTag = "card_internship_application"
        ),
        StudentDashboardCardItem(
            title = "Weekly Reports",
            subtitle = "Week 8 Pending",
            icon = Icons.Default.CalendarViewWeek,
            route = Screen.WeeklyReports.route,
            bubbleBg = BlueBubbleBg,
            bubbleTint = BlueBubbleTint,
            testTag = "card_weekly_reports"
        ),
        StudentDashboardCardItem(
            title = "AI LEAP Assistant",
            subtitle = "Regulations & Guide",
            icon = Icons.Default.SmartToy,
            route = Screen.LeapAiAssistant.route,
            bubbleBg = Color.White,
            bubbleTint = LeapNavyPrimary,
            isHighlighted = true,
            badge = "Advisory AI",
            testTag = "card_ai_assistant"
        ),
        StudentDashboardCardItem(
            title = "Submission List",
            subtitle = "7/8 items ready",
            icon = Icons.Default.Checklist,
            route = Screen.SubmissionChecklist.route,
            bubbleBg = GreenBubbleBg,
            bubbleTint = GreenBubbleTint,
            testTag = "card_submission_checklist"
        ),
        StudentDashboardCardItem(
            title = "Form D2 Self",
            subtitle = "Parts 1-3 Evaluation",
            icon = Icons.Default.Assignment,
            route = Screen.StudentSelfEvaluation.route,
            bubbleBg = OrangeBubbleBg,
            bubbleTint = OrangeBubbleTint,
            testTag = "card_self_evaluation"
        ),
        StudentDashboardCardItem(
            title = "Action Plan",
            subtitle = "Tasks & Milestones",
            icon = Icons.Default.Description,
            route = Screen.ActionPlan.route,
            bubbleBg = PinkBubbleBg,
            bubbleTint = PinkBubbleTint,
            testTag = "card_action_plan"
        ),
        StudentDashboardCardItem(
            title = "Internship Report",
            subtitle = "Final Typed PDF",
            icon = Icons.Default.Article,
            route = Screen.InternshipReport.route,
            bubbleBg = TealBubbleBg,
            bubbleTint = TealBubbleTint,
            testTag = "card_internship_report"
        ),
        StudentDashboardCardItem(
            title = "Scan Forms",
            subtitle = "Camera Doc Capture",
            icon = Icons.Default.DocumentScanner,
            route = Screen.DocumentScanner.route,
            bubbleBg = Color(0xFFFEF3C7),
            bubbleTint = Color(0xFFB45309),
            badge = "Camera",
            testTag = "card_document_scanner"
        ),
        StudentDashboardCardItem(
            title = "AI Matchmaking",
            subtitle = "Placement Analysis",
            icon = Icons.Default.Hub,
            route = Screen.AiInternshipMatch.route,
            bubbleBg = BlueBubbleBg,
            bubbleTint = LeapBlue,
            testTag = "card_ai_matchmaking"
        )
    )

    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { }
                            .testTag("nav_bottom_home")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Home",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onNavigate(Screen.DailyLogBook.route) }
                            .testTag("nav_bottom_tasks")
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = "Tasks",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Tasks",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onNavigate(Screen.Notifications.route) }
                            .testTag("nav_bottom_alerts")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifs > 0) {
                                    Badge(containerColor = LeapOrange) {
                                        Text(unreadNotifs.toString(), color = Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alerts",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Alerts",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onNavigate(Screen.StudentProfile.route) }
                            .testTag("nav_bottom_profile")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Header (Deep Royal Navy with Limkokwing Branding)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                color = LeapNavyPrimary,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    // Top row: App brand & offline status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "LEAP Manager",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                            Text(
                                text = "Limkokwing University • Industrial Placement",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Offline pill indicator
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .clickable { viewModel.syncPendingData {} }
                                    .testTag("offline_status_pill")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudQueue,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isOffline) "Offline" else "Online",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }

                            // Role Switcher
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .clickable { onSwitchRole() }
                                    .testTag("role_switch_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Switch Role",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Student Profile Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.22f))
                                .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "MK",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = studentName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "$programme • $studentIdCode",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = LeapOrange
                                ) {
                                    Text(
                                        text = "STUDENT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            letterSpacing = 0.5.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "WEEK $currentWeek OF $totalWeeks",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 1. MATERIAL 3 ACTIVE INTERNSHIPS SUMMARY CARD
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BusinessCenter,
                            contentDescription = null,
                            tint = LeapNavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Active Internship Summary",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = LeapNavyPrimary
                            )
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = "Active Placement",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Primary Active Internship M3 Card
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onNavigate(Screen.InternshipPlacement.route) }
                        .testTag("card_active_internship_summary"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Company & Role Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Company Logo Avatar
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(LeapNavyPrimary.copy(alpha = 0.08f))
                                        .border(1.dp, LeapNavyPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CorporateFare,
                                        contentDescription = null,
                                        tint = LeapNavyPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = orgName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = placementRole,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            color = LeapBlue,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Circular Progress Ring Indicator
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .testTag("circular_progress_indicator"),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeWidth = 5.dp.toPx()
                                    drawCircle(
                                        color = LeapPrimaryLight,
                                        style = Stroke(width = strokeWidth)
                                    )
                                    drawArc(
                                        color = LeapNavyPrimary,
                                        startAngle = -90f,
                                        sweepAngle = (progress / 100f) * 360f,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                                Text(
                                    text = "$progress%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        // Internship Meta Specs (Supervisor & Logistics)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PersonOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Supervisor: $supervisorName",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "On-Track (96% Attendance)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusActive
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Freetown Central • On-site & Hybrid",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "$hoursLogged / $totalHoursRequired hrs",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Linear progress indicator
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Timeline: Week $currentWeek of $totalWeeks",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LeapNavyPrimary
                                )
                                Text(
                                    text = "${totalWeeks - currentWeek} Weeks Remaining",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = LeapNavyPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Action Buttons inside Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { onNavigate(Screen.DailyLogBook.route) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_log_today_action"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = LeapNavyPrimary,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log Today's Work", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { onNavigate(Screen.InternshipPlacement.route) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_view_placement_details"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Placement Info", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // 3 Quick KPI Stat M3 Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(Screen.DailyLogBook.route) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = PurpleBubbleTint, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("38 / 40", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LeapNavyPrimary)
                            Text("Days Logged", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(Screen.WeeklyReports.route) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CalendarViewWeek, contentDescription = null, tint = BlueBubbleTint, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("7 / 12", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LeapNavyPrimary)
                            Text("Weekly Reports", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(Screen.SubmissionChecklist.route) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GreenBubbleTint, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("92%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = StatusActive)
                            Text("Supervisor Sign-off", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // 2. MATERIAL 3 UPCOMING DEADLINES SECTION
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationImportant,
                            contentDescription = null,
                            tint = LeapOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Upcoming Deadlines",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = LeapNavyPrimary
                            )
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = LeapOrange.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${upcomingDeadlines.size} Submissions",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LeapOrange,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Filter Category Chips (All, Reports, Forms, Final)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Reports", "Forms", "Final").forEach { filter ->
                        val isSelected = selectedDeadlineFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDeadlineFilter = filter },
                            label = { Text(filter, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LeapNavyPrimary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                // Deadline Cards List
                filteredDeadlines.forEach { deadline ->
                    Material3DeadlineCard(
                        deadline = deadline,
                        onActionClick = { onNavigate(deadline.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // 3. INTERNSHIP APPLICATION FAST-TRACK CARD
            // ==========================================
            if (currentApp != null || true) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onNavigate(Screen.InternshipApplication.route) }
                        .testTag("card_placement_application_banner"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, LeapCyan.copy(alpha = 0.35f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE0F2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.AppRegistration,
                                        contentDescription = null,
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Multi-Step Placement Application",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = LeapNavyPrimary
                                    )
                                    Text(
                                        text = "Digital CV & Multi-Sector Preferences",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            StatusChip(status = currentApp?.submissionStatus ?: "Submitted")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ref: ${currentApp?.applicationRefNumber ?: "APP-2026-SL-8921"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LeapNavyPrimary
                                )
                                Text(
                                    text = "Sector: ${currentApp?.primarySector ?: "Software & Cloud"}",
                                    fontSize = 11.sp,
                                    color = LeapBlue
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ==========================================
            // 4. ESSENTIAL MODULES GRID
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Placement Modules",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LeapNavyPrimary
                        )
                    )
                    Text(
                        text = "10 Tools Available",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                for (i in dashboardCards.indices step 2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SleekModuleGridCard(
                            item = dashboardCards[i],
                            onClick = { onNavigate(dashboardCards[i].route) },
                            modifier = Modifier.weight(1f)
                        )
                        if (i + 1 < dashboardCards.size) {
                            SleekModuleGridCard(
                                item = dashboardCards[i + 1],
                                onClick = { onNavigate(dashboardCards[i + 1].route) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recharts-inspired Visual Milestone Progress Tracking Dashboard Component
            LeapMilestoneProgressDashboard(
                currentWeek = currentWeek,
                totalWeeks = totalWeeks,
                onNavigateToMilestone = { route -> onNavigate(route) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dissertation Context Notice
            PaperVsDigitalBadge(
                paperProcess = "Paper logbooks & printed Form D assessment forms carried physically between workplace and campus.",
                digitalProcess = "Real-time mobile entry with Room DB local caching, supervisor digital sign-offs, and advisory AI.",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ----------------------------------------------------
// Material 3 Deadline Card Component
// ----------------------------------------------------
@Composable
fun Material3DeadlineCard(
    deadline: InternshipDeadlineItem,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerBg = when (deadline.urgencyLevel) {
        DeadlineUrgency.URGENT -> Color(0xFFFEF2F2)
        DeadlineUrgency.MEDIUM -> Color(0xFFF0F9FF)
        DeadlineUrgency.SCHEDULED -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when (deadline.urgencyLevel) {
        DeadlineUrgency.URGENT -> Color(0xFFFECACA)
        DeadlineUrgency.MEDIUM -> Color(0xFFBAE6FD)
        DeadlineUrgency.SCHEDULED -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    }

    val chipBg = when (deadline.urgencyLevel) {
        DeadlineUrgency.URGENT -> Color(0xFFFEE2E2)
        DeadlineUrgency.MEDIUM -> Color(0xFFE0F2FE)
        DeadlineUrgency.SCHEDULED -> MaterialTheme.colorScheme.surfaceVariant
    }

    val chipText = when (deadline.urgencyLevel) {
        DeadlineUrgency.URGENT -> Color(0xFFDC2626)
        DeadlineUrgency.MEDIUM -> Color(0xFF0284C7)
        DeadlineUrgency.SCHEDULED -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag(deadline.testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(chipBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = deadline.icon,
                            contentDescription = null,
                            tint = chipText,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = deadline.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = chipText,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = deadline.dueDateText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = chipText
                                )
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = chipBg
                ) {
                    Text(
                        text = deadline.urgencyText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = chipText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = deadline.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onActionClick,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = deadline.actionLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (deadline.urgencyLevel == DeadlineUrgency.URGENT) Color(0xFFDC2626) else LeapNavyPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (deadline.urgencyLevel == DeadlineUrgency.URGENT) Color(0xFFDC2626) else LeapNavyPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun SleekModuleGridCard(
    item: StudentDashboardCardItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerBg = if (item.isHighlighted) LeapPrimaryContainer else Color.White
    val borderColor = if (item.isHighlighted) Color(0xFFD8B4FE) else Color(0xFFF1F5F9)

    Card(
        modifier = modifier
            .height(118.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag(item.testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon bubble
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(item.bubbleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.bubbleTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (item.isHighlighted) LeapNavyDark else Color(0xFF1E293B)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            if (item.subtitle.isNotEmpty()) {
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = if (item.isHighlighted) LeapNavyPrimary else Color(0xFF64748B)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
