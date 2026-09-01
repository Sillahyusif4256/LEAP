package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.entities.DailyLogEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LeapTopAppBar
import com.example.ui.components.PaperVsDigitalBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel

@Composable
fun DailyLogBookScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit,
    onNavigateToWeeklyReport: () -> Unit
) {
    val logs by viewModel.dailyLogs.collectAsState()
    val selectedWeek by viewModel.selectedWeek.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog form state
    var dateInput by remember { mutableStateOf("2026-03-09") }
    var dayOfWeekInput by remember { mutableStateOf("Monday") }
    var activityInput by remember { mutableStateOf("") }
    var toolsInput by remember { mutableStateOf("Android Studio, Jetpack Compose, Room DB") }
    var peopleInput by remember { mutableStateOf("Ing. David Koroma") }
    var skillsInput by remember { mutableStateOf("MVVM Architecture, Local Data Caching") }
    var challengesInput by remember { mutableStateOf("Offline state reconciliation") }
    var reflectionInput by remember { mutableStateOf("Local persistence is vital for reliable African enterprise apps.") }

    val weekLogs = logs.filter { it.weekNumber == selectedWeek }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Daily Log Book",
                subtitle = "Week $selectedWeek Entries | Grouped by Week",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = LeapNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_daily_log")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Log Entry")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Week Selector Tabs
            item {
                Text(
                    text = "Select Internship Week (1 to 12)",
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
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) LeapNavyPrimary else Color(0xFFE2E8F0),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.setSelectedWeek(week) }
                                .testTag("week_tab_$week")
                        ) {
                            Text(
                                text = "Week $week",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF475569)
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Generate Weekly Report Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Weekly Report Generation",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LeapNavyPrimary
                                )
                            )
                            Text(
                                text = "Synthesize Week $selectedWeek's ${weekLogs.size} logs into the official Weekly Report.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF475569),
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Button(
                            onClick = onNavigateToWeeklyReport,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("button_generate_weekly_report")
                        ) {
                            Text("View Report", fontSize = 11.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (weekLogs.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.EditNote,
                        title = "No Log Entries for Week $selectedWeek",
                        description = "Tap the + button below to log your daily tasks, tools, skills, challenges, and reflections.",
                        actionButtonText = "Add First Entry",
                        onActionClick = { showAddDialog = true }
                    )
                }
            } else {
                items(weekLogs) { log ->
                    DailyLogCard(
                        log = log,
                        onDelete = { viewModel.deleteDailyLog(log) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                PaperVsDigitalBadge(
                    paperProcess = "Handwritten daily entries in paper logbook notebooks stamped weekly by workplace mentor.",
                    digitalProcess = "Digitized daily structured logs with offline Room DB queuing and instant supervisor notification."
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add Daily Log Entry (Week $selectedWeek)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = dateInput,
                        onValueChange = { dateInput = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_log_date"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = activityInput,
                        onValueChange = { activityInput = it },
                        label = { Text("Task / Activity Performed *") },
                        modifier = Modifier.fillMaxWidth().testTag("input_log_activity"),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = toolsInput,
                        onValueChange = { toolsInput = it },
                        label = { Text("Tools / Methods Used") },
                        modifier = Modifier.fillMaxWidth().testTag("input_log_tools"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = peopleInput,
                        onValueChange = { peopleInput = it },
                        label = { Text("People Worked With") },
                        modifier = Modifier.fillMaxWidth().testTag("input_log_people"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = skillsInput,
                        onValueChange = { skillsInput = it },
                        label = { Text("Skills Learned") },
                        modifier = Modifier.fillMaxWidth().testTag("input_log_skills"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = reflectionInput,
                        onValueChange = { reflectionInput = it },
                        label = { Text("Student Reflection") },
                        modifier = Modifier.fillMaxWidth().testTag("input_log_reflection"),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (activityInput.isNotBlank()) {
                            viewModel.addDailyLog(
                                weekNumber = selectedWeek,
                                date = dateInput,
                                dayOfWeek = dayOfWeekInput,
                                activity = activityInput,
                                tools = toolsInput,
                                people = peopleInput,
                                skills = skillsInput,
                                challenges = challengesInput,
                                reflection = reflectionInput
                            )
                            activityInput = ""
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_save_daily_log")
                ) {
                    Text("Save Entry")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DailyLogCard(
    log: DailyLogEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = LeapNavyPrimary
                    ) {
                        Text(
                            text = log.dayOfWeek.take(3).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = log.date,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LeapNavyPrimary
                        )
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp).testTag("delete_log_${log.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = log.taskActivity,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0F172A)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LogDetailField(label = "Tools/Methods:", value = log.toolsMethods)
            LogDetailField(label = "Collaborators:", value = log.peopleWorkedWith)
            LogDetailField(label = "Skills Gained:", value = log.skillsLearned)
            if (log.challenges.isNotBlank()) {
                LogDetailField(label = "Challenges:", value = log.challenges)
            }

            if (log.reflection.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Reflection:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LeapBlue
                            )
                        )
                        Text(
                            text = log.reflection,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF475569),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LogDetailField(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label ",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF334155),
                fontSize = 11.sp
            )
        )
    }
}
