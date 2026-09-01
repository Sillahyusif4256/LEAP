package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Data model representing a required LEAP Internship milestone with completion metrics.
 */
data class LeapMilestone(
    val id: String,
    val code: String,
    val title: String,
    val category: MilestoneCategory,
    val completionPercentage: Float, // 0.0f to 1.0f
    val weightPercentage: Int, // e.g. 15%
    val targetWeek: String,
    val status: MilestoneStatus,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val artifacts: List<String>,
    val description: String,
    val actionRoute: String? = null,
    val actionLabel: String? = null
)

enum class MilestoneCategory(val displayName: String) {
    ALL("All Milestones"),
    ONBOARDING("Placement & Plan"),
    CONTINUOUS("Continuous Logs"),
    EVALUATION("Assessments"),
    FINAL_SUBMISSION("Final Clearance")
}

enum class MilestoneStatus(val label: String, val badgeColor: Color, val textColor: Color) {
    COMPLETED("Completed", Color(0xFFDCFCE7), Color(0xFF15803D)),
    IN_PROGRESS("In Progress", Color(0xFFDBEAFE), Color(0xFF1D4ED8)),
    PENDING("Pending", Color(0xFFFEF3C7), Color(0xFF92400E)),
    ATTENTION("Needs Action", Color(0xFFFEE2E2), Color(0xFFB91C1C))
}

enum class ChartDisplayMode {
    MULTI_BAR,
    RADIAL_RINGS,
    WEEKLY_TIMELINE
}

/**
 * Default sample LEAP internship milestones computed from student records.
 */
object LeapMilestoneData {
    val defaultMilestones = listOf(
        LeapMilestone(
            id = "m1_placement",
            code = "MS-01",
            title = "Placement Verification (Form A2/A3)",
            category = MilestoneCategory.ONBOARDING,
            completionPercentage = 1.0f,
            weightPercentage = 10,
            targetWeek = "Week 1",
            status = MilestoneStatus.COMPLETED,
            icon = Icons.Default.VerifiedUser,
            primaryColor = Color(0xFF10B981), // Emerald
            secondaryColor = Color(0xFF6EE7B7),
            artifacts = listOf("Form A2 Student Acceptance", "Form A3 Host Organization Verification"),
            description = "Host organization confirmation, supervisor contact details, and student declaration approved by LEAP Coordinator.",
            actionRoute = "internship_placement",
            actionLabel = "View Placement"
        ),
        LeapMilestone(
            id = "m2_action_plan",
            code = "MS-02",
            title = "Action Plan & Company Stamp",
            category = MilestoneCategory.ONBOARDING,
            completionPercentage = 1.0f,
            weightPercentage = 15,
            targetWeek = "Week 2",
            status = MilestoneStatus.COMPLETED,
            icon = Icons.Default.Description,
            primaryColor = Color(0xFF0284C7), // Sky Blue
            secondaryColor = Color(0xFF7DD3FC),
            artifacts = listOf("Action Plan Objectives Table", "Supervisor Signature", "Official Company Stamp"),
            description = "Formulation of 'What will I do', 'By When', and 'Achieved' milestones verified by industrial supervisor.",
            actionRoute = "action_plan",
            actionLabel = "View Action Plan"
        ),
        LeapMilestone(
            id = "m3_daily_logs",
            code = "MS-03",
            title = "Daily Log Books (7 Fields)",
            category = MilestoneCategory.CONTINUOUS,
            completionPercentage = 0.67f,
            weightPercentage = 20,
            targetWeek = "Weeks 1-12",
            status = MilestoneStatus.IN_PROGRESS,
            icon = Icons.Default.MenuBook,
            primaryColor = Color(0xFF8B5CF6), // Purple
            secondaryColor = Color(0xFFC4B5FD),
            artifacts = listOf("40 Daily Entries (8 Weeks)", "Tools & Methods Logged", "Supervisor Review Stamps"),
            description = "Daily documentation of tasks, skills, tools, collaboration, challenges, and self-reflections across all 12 weeks.",
            actionRoute = "daily_log_book",
            actionLabel = "Log Today's Work"
        ),
        LeapMilestone(
            id = "m4_weekly_reports",
            code = "MS-04",
            title = "Weekly Synthesis Reports",
            category = MilestoneCategory.CONTINUOUS,
            completionPercentage = 0.67f,
            weightPercentage = 15,
            targetWeek = "Fridays (W1-12)",
            status = MilestoneStatus.IN_PROGRESS,
            icon = Icons.Default.CalendarViewWeek,
            primaryColor = Color(0xFF06B6D4), // Cyan
            secondaryColor = Color(0xFF67E8F9),
            artifacts = listOf("8 of 12 Weekly Reports Submitted", "Supervisor Feedback Comments", "Weekly Grade Ratings"),
            description = "Weekly synthesis of accomplishments, competencies acquired, obstacles overcome, and supervisor ratings.",
            actionRoute = "weekly_reports",
            actionLabel = "Open Weekly Reports"
        ),
        LeapMilestone(
            id = "m5_midterm",
            code = "MS-05",
            title = "Mid-Term Form B Review",
            category = MilestoneCategory.EVALUATION,
            completionPercentage = 1.0f,
            weightPercentage = 10,
            targetWeek = "Week 6",
            status = MilestoneStatus.COMPLETED,
            icon = Icons.Default.FactCheck,
            primaryColor = Color(0xFFF59E0B), // Amber
            secondaryColor = Color(0xFFFDE68A),
            artifacts = listOf("Form B Mid-Term Evaluation", "Coordinator On-site / Virtual Check"),
            description = "Mid-way progress appraisal conducted with workplace mentor and LEAP academic coordinator.",
            actionRoute = "submission_checklist",
            actionLabel = "View Form B"
        ),
        LeapMilestone(
            id = "m6_form_d_d2",
            code = "MS-06",
            title = "Form D Assessment & Form D2 Self-Eval",
            category = MilestoneCategory.EVALUATION,
            completionPercentage = 0.50f,
            weightPercentage = 15,
            targetWeek = "Week 11",
            status = MilestoneStatus.IN_PROGRESS,
            icon = Icons.Default.AssignmentInd,
            primaryColor = Color(0xFFEC4899), // Pink
            secondaryColor = Color(0xFFFBCFE8),
            artifacts = listOf("Form D2 (Parts 1-3 Self-Evaluation) Done", "Form D 18-Criteria Supervisor Rating Pending"),
            description = "Student 3-part self-evaluation and final industrial supervisor evaluation across 18 performance criteria.",
            actionRoute = "self_evaluation",
            actionLabel = "Complete Form D2"
        ),
        LeapMilestone(
            id = "m7_final_report",
            code = "MS-07",
            title = "Typed Report & 8-Item Checklist",
            category = MilestoneCategory.FINAL_SUBMISSION,
            completionPercentage = 0.875f,
            weightPercentage = 15,
            targetWeek = "Week 12",
            status = MilestoneStatus.IN_PROGRESS,
            icon = Icons.Default.Checklist,
            primaryColor = Color(0xFF3B82F6), // Blue
            secondaryColor = Color(0xFF93C5FD),
            artifacts = listOf("7 of 8 Checklist Documents Verified", "Dissertation PDF Draft Compiled"),
            description = "Official 8-item LEAP final submission package, bound report according to Limkokwing dissertation formatting guidelines.",
            actionRoute = "submission_checklist",
            actionLabel = "Open 8-Item Checklist"
        )
    )
}

/**
 * Visual Progress Tracking Dashboard Component (Recharts-inspired design).
 * Displays multi-bar progress charts, radial milestone completion rings,
 * category filter tabs, interactive tooltip inspections, and milestone detail cards.
 */
@Composable
fun LeapMilestoneProgressDashboard(
    milestones: List<LeapMilestone> = LeapMilestoneData.defaultMilestones,
    currentWeek: Int = 8,
    totalWeeks: Int = 12,
    onNavigateToMilestone: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(MilestoneCategory.ALL) }
    var chartMode by remember { mutableStateOf(ChartDisplayMode.MULTI_BAR) }
    var selectedMilestoneId by remember { mutableStateOf<String?>(null) }

    val filteredMilestones = remember(selectedCategory, milestones) {
        if (selectedCategory == MilestoneCategory.ALL) {
            milestones
        } else {
            milestones.filter { it.category == selectedCategory }
        }
    }

    // Weighted Overall Completion Calculation
    val totalWeightedScore = remember(milestones) {
        val totalWeight = milestones.sumOf { it.weightPercentage }.coerceAtLeast(1)
        val earned = milestones.sumOf { (it.completionPercentage * it.weightPercentage).toDouble() }
        (earned / totalWeight * 100).toFloat()
    }

    val completedCount = remember(milestones) { milestones.count { it.status == MilestoneStatus.COMPLETED } }
    val inProgressCount = remember(milestones) { milestones.count { it.status == MilestoneStatus.IN_PROGRESS } }
    val totalCount = milestones.size

    val animatedOverallProgress by animateFloatAsState(
        targetValue = totalWeightedScore / 100f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "overall_progress_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("leap_milestone_progress_dashboard"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Title & Chart Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LeapNavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Milestone Completion Tracker",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    Text(
                        text = "LEAP Internship Milestones • Recharts Visualization Engine",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }

                // Chart Display Mode Toggle Pills
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ChartModeIconButton(
                            icon = Icons.Default.AlignHorizontalLeft,
                            title = "Bar Chart",
                            isSelected = chartMode == ChartDisplayMode.MULTI_BAR,
                            onClick = { chartMode = ChartDisplayMode.MULTI_BAR }
                        )
                        ChartModeIconButton(
                            icon = Icons.Default.DonutLarge,
                            title = "Radial Rings",
                            isSelected = chartMode == ChartDisplayMode.RADIAL_RINGS,
                            onClick = { chartMode = ChartDisplayMode.RADIAL_RINGS }
                        )
                        ChartModeIconButton(
                            icon = Icons.Default.Timeline,
                            title = "12-Week Timeline",
                            isSelected = chartMode == ChartDisplayMode.WEEKLY_TIMELINE,
                            onClick = { chartMode = ChartDisplayMode.WEEKLY_TIMELINE }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // KPI Overview Banner (4 Key Metrics)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiMetricCard(
                    title = "Overall Rate",
                    value = "${"%.1f".format(totalWeightedScore)}%",
                    subtitle = "Weighted Score",
                    color = LeapNavyPrimary,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = "Cleared",
                    value = "$completedCount / $totalCount",
                    subtitle = "Milestones Done",
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = "In Progress",
                    value = "$inProgressCount",
                    subtitle = "Active Tasks",
                    color = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = "Timeline",
                    value = "W$currentWeek/$totalWeeks",
                    subtitle = "Week Progress",
                    color = LeapOrange,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Chart Visualizer Container (Canvas / Recharts style)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    when (chartMode) {
                        ChartDisplayMode.MULTI_BAR -> {
                            RechartsStyleBarChart(
                                milestones = filteredMilestones,
                                selectedMilestoneId = selectedMilestoneId,
                                onSelectMilestone = { selectedMilestoneId = if (selectedMilestoneId == it) null else it }
                            )
                        }
                        ChartDisplayMode.RADIAL_RINGS -> {
                            RechartsStyleRadialRingsChart(
                                milestones = milestones,
                                overallProgress = animatedOverallProgress,
                                totalScore = totalWeightedScore,
                                currentWeek = currentWeek,
                                totalWeeks = totalWeeks
                            )
                        }
                        ChartDisplayMode.WEEKLY_TIMELINE -> {
                            WeeklyProgressTimelineChart(
                                currentWeek = currentWeek,
                                totalWeeks = totalWeeks,
                                milestones = milestones
                            )
                        }
                    }
                }
            }

            // Interactive Tooltip / Inspection Card if a bar is selected
            val activeSelectedMilestone = milestones.find { it.id == selectedMilestoneId }
            AnimatedVisibility(visible = activeSelectedMilestone != null) {
                activeSelectedMilestone?.let { ms ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ms.primaryColor.copy(alpha = 0.08f),
                        border = BorderStroke(1.5.dp, ms.primaryColor.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${ms.code}: ${ms.title}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = ms.status.badgeColor
                                    ) {
                                        Text(
                                            text = ms.status.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ms.status.textColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ms.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Target: ${ms.targetWeek} • Academic Weight: ${ms.weightPercentage}% of total LEAP credits",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ms.primaryColor,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            if (ms.actionRoute != null && ms.actionLabel != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { onNavigateToMilestone(ms.actionRoute) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ms.primaryColor),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_inspect_${ms.id}")
                                ) {
                                    Text(
                                        text = ms.actionLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Tabs
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(MilestoneCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    val count = if (category == MilestoneCategory.ALL) milestones.size else milestones.count { it.category == category }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = "${category.displayName} ($count)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LeapNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Milestone Cards List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredMilestones.forEach { milestone ->
                    MilestoneCardItem(
                        milestone = milestone,
                        isSelected = milestone.id == selectedMilestoneId,
                        onClick = {
                            selectedMilestoneId = if (selectedMilestoneId == milestone.id) null else milestone.id
                        },
                        onActionClick = {
                            if (milestone.actionRoute != null) {
                                onNavigateToMilestone(milestone.actionRoute)
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Recharts-style Interactive Multi-Bar Milestone Chart.
 * Renders target reference line (100%), animated bars, background track rails,
 * and completion percentage badges with tap selection.
 */
@Composable
private fun RechartsStyleBarChart(
    milestones: List<LeapMilestone>,
    selectedMilestoneId: String?,
    onSelectMilestone: (String) -> Unit
) {
    val barAnimations = milestones.map { ms ->
        animateFloatAsState(
            targetValue = ms.completionPercentage,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "bar_${ms.id}"
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Chart Header Legend & Target Reference Line
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MILESTONE COMPLETION BARS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Legend item: Completed
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("100% Target", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
                // Legend item: Actual
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(LeapNavyPrimary))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Actual %", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Canvas Multi-Bar Rendering
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height((milestones.size * 34).coerceAtLeast(140).dp)
                .pointerInput(milestones) {
                    detectTapGestures { offset ->
                        val barHeight = size.height / milestones.size
                        val index = (offset.y / barHeight).toInt().coerceIn(0, milestones.size - 1)
                        if (index in milestones.indices) {
                            onSelectMilestone(milestones[index].id)
                        }
                    }
                }
                .testTag("recharts_canvas_barchart")
        ) {
            val totalHeight = size.height
            val totalWidth = size.width
            val rowHeight = totalHeight / milestones.size
            val barThickness = rowHeight * 0.44f
            val labelWidth = 62.dp.toPx()
            val valueWidth = 42.dp.toPx()
            val chartAreaWidth = totalWidth - labelWidth - valueWidth

            // Draw dashed reference grid lines at 25%, 50%, 75%, 100%
            val gridSteps = listOf(0.25f, 0.5f, 0.75f, 1.0f)
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

            gridSteps.forEach { step ->
                val x = labelWidth + (step * chartAreaWidth)
                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(x, 0f),
                    end = Offset(x, totalHeight),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = if (step == 1.0f) null else pathEffect
                )
            }

            // Draw each milestone bar
            milestones.forEachIndexed { i, ms ->
                val animatedProgress = barAnimations[i].value
                val yCenter = (i * rowHeight) + (rowHeight / 2f)
                val isSelected = ms.id == selectedMilestoneId

                val barStartX = labelWidth
                val maxBarWidth = chartAreaWidth
                val currentBarWidth = maxBarWidth * animatedProgress

                // Background track rail
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.12f),
                    topLeft = Offset(barStartX, yCenter - (barThickness / 2f)),
                    size = Size(maxBarWidth, barThickness),
                    cornerRadius = CornerRadius(barThickness / 2f, barThickness / 2f)
                )

                // Filled Progress Bar with Gradient
                if (currentBarWidth > 0f) {
                    val brush = Brush.horizontalGradient(
                        colors = listOf(ms.primaryColor, ms.secondaryColor),
                        startX = barStartX,
                        endX = barStartX + currentBarWidth
                    )
                    drawRoundRect(
                        brush = brush,
                        topLeft = Offset(barStartX, yCenter - (barThickness / 2f)),
                        size = Size(currentBarWidth, barThickness),
                        cornerRadius = CornerRadius(barThickness / 2f, barThickness / 2f)
                    )
                }

                // Selection Ring
                if (isSelected) {
                    drawRoundRect(
                        color = ms.primaryColor,
                        topLeft = Offset(barStartX - 2f, yCenter - (barThickness / 2f) - 2f),
                        size = Size(maxBarWidth + 4f, barThickness + 4f),
                        cornerRadius = CornerRadius(barThickness / 2f + 2f, barThickness / 2f + 2f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }

        // Percentage Axis Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 62.dp, end = 42.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0%", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("25%", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("50%", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("75%", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("100%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        }
    }
}

/**
 * Recharts Radial Bar / Ring Chart Representation.
 * Displays concentric radial progress rings for core milestone categories.
 */
@Composable
private fun RechartsStyleRadialRingsChart(
    milestones: List<LeapMilestone>,
    overallProgress: Float,
    totalScore: Float,
    currentWeek: Int,
    totalWeeks: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Center Radial Ring Donut
        Box(
            modifier = Modifier
                .size(150.dp)
                .testTag("recharts_radial_ring_chart"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseStroke = 9.dp.toPx()

                // Ring 1: Background Track
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.12f),
                    radius = (size.minDimension / 2f) - baseStroke,
                    style = Stroke(width = baseStroke)
                )

                // Ring 1: Overall Progress Arc
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(LeapNavyPrimary, LeapCyan, Color(0xFF8B5CF6), LeapNavyPrimary)
                    ),
                    startAngle = -90f,
                    sweepAngle = overallProgress * 360f,
                    useCenter = false,
                    topLeft = Offset(baseStroke, baseStroke),
                    size = Size(size.width - (baseStroke * 2), size.height - (baseStroke * 2)),
                    style = Stroke(width = baseStroke, cap = StrokeCap.Round)
                )

                // Ring 2: Inner Timeline Ring (Week Progress)
                val innerStroke = 6.dp.toPx()
                val innerOffset = baseStroke + 12.dp.toPx()
                val weekRatio = (currentWeek.toFloat() / totalWeeks.toFloat()).coerceIn(0f, 1f)

                drawArc(
                    color = LeapOrange,
                    startAngle = -90f,
                    sweepAngle = weekRatio * 360f,
                    useCenter = false,
                    topLeft = Offset(innerOffset, innerOffset),
                    size = Size(size.width - (innerOffset * 2), size.height - (innerOffset * 2)),
                    style = Stroke(width = innerStroke, cap = StrokeCap.Round)
                )
            }

            // Center KPI Text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${"%.0f".format(totalScore)}%",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 22.sp
                    )
                )
                Text(
                    text = "COMPLETED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = Color(0xFF10B981),
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }

        // Legend Breakdown List
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 12.dp)
        ) {
            RadialLegendItem(
                color = LeapNavyPrimary,
                label = "Weighted Progress",
                value = "${"%.1f".format(totalScore)}%"
            )
            RadialLegendItem(
                color = LeapOrange,
                label = "Internship Timeline",
                value = "Week $currentWeek of $totalWeeks"
            )
            RadialLegendItem(
                color = Color(0xFF10B981),
                label = "Form A2/A3, Plan & B",
                value = "100% Cleared"
            )
            RadialLegendItem(
                color = Color(0xFF8B5CF6),
                label = "Daily Logs & Reports",
                value = "67% (Week 8/12)"
            )
            RadialLegendItem(
                color = Color(0xFF3B82F6),
                label = "8-Item Checklist",
                value = "7/8 Items Ready"
            )
        }
    }
}

@Composable
private fun RadialLegendItem(
    color: Color,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = color
                )
            )
        }
    }
}

/**
 * 12-Week Milestone Progression Timeline Chart.
 */
@Composable
private fun WeeklyProgressTimelineChart(
    currentWeek: Int,
    totalWeeks: Int,
    milestones: List<LeapMilestone>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "12-WEEK LEAP INTERNSHIP PROGRESSION",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.8.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Week Steps Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items((1..totalWeeks).toList()) { week ->
                val isCompleted = week < currentWeek
                val isCurrent = week == currentWeek
                val isUpcoming = week > currentWeek

                val (bgColor, borderColor, textColor) = when {
                    isCompleted -> Triple(Color(0xFFDCFCE7), Color(0xFF10B981), Color(0xFF15803D))
                    isCurrent -> Triple(LeapNavyPrimary, LeapGoldAccent, Color.White)
                    else -> Triple(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = bgColor,
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.width(52.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "W$week",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(
                            imageVector = when {
                                isCompleted -> Icons.Default.CheckCircle
                                isCurrent -> Icons.Default.PlayCircle
                                else -> Icons.Default.RadioButtonUnchecked
                            },
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (week) {
                                1 -> "A2/A3"
                                2 -> "Plan"
                                6 -> "Form B"
                                8 -> "Active"
                                11 -> "Form D"
                                12 -> "Report"
                                else -> "Logs"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = textColor,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Detailed Milestone Item Card.
 */
@Composable
private fun MilestoneCardItem(
    milestone: LeapMilestone,
    isSelected: Boolean,
    onClick: () -> Unit,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("milestone_card_${milestone.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) milestone.primaryColor else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(milestone.primaryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = milestone.icon,
                            contentDescription = milestone.title,
                            tint = milestone.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = milestone.code,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    color = milestone.primaryColor
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${milestone.targetWeek}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Text(
                            text = milestone.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Completion Rate Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = milestone.status.badgeColor
                ) {
                    Text(
                        text = "${(milestone.completionPercentage * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = milestone.status.textColor,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Linear Progress Track
            LinearProgressIndicator(
                progress = { milestone.completionPercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = milestone.primaryColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weight: ${milestone.weightPercentage}% of total grade",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )

                if (milestone.actionLabel != null) {
                    Text(
                        text = "${milestone.actionLabel} →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = milestone.primaryColor,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onActionClick() }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiMetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    fontSize = 13.sp
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ChartModeIconButton(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) LeapNavyPrimary else Color.Transparent,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
