package com.example.ui.screens.settings

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
import com.example.ui.components.ConnectionBadge
import com.example.ui.components.ConnectionStatus
import com.example.ui.components.LeapTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel
import com.example.ui.viewmodel.ThemeMode

@Composable
fun SettingsScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val pendingSync by viewModel.pendingSyncCount.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    var syncSuccessBanner by remember { mutableStateOf<String?>(null) }

    val currentStatus = when {
        isSyncing -> ConnectionStatus.SYNCING
        isOffline -> ConnectionStatus.OFFLINE
        else -> ConnectionStatus.ONLINE
    }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "System Settings",
                subtitle = "Appearance, Connectivity & Preferences",
                showBackButton = true,
                onBackClick = onBack,
                isOffline = isOffline,
                isSyncing = isSyncing,
                pendingSyncCount = pendingSync
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
            // Theme & Appearance Section
            Text(
                text = "Appearance & Display Theme",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose how the LEAP Internship Manager looks. Switch to dark mode for comfortable night use.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "App Theme Mode",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeModeOption(
                            title = "System",
                            icon = Icons.Default.SettingsBrightness,
                            isSelected = themeMode == ThemeMode.SYSTEM,
                            onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeModeOption(
                            title = "Light",
                            icon = Icons.Default.LightMode,
                            isSelected = themeMode == ThemeMode.LIGHT,
                            onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeModeOption(
                            title = "Dark",
                            icon = Icons.Default.DarkMode,
                            isSelected = themeMode == ThemeMode.DARK,
                            onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Offline & Low Bandwidth Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Connection & Sync Manager",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                ConnectionBadge(status = currentStatus, pendingCount = pendingSync)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Engineered specifically for low-connectivity environments in Sierra Leone, supporting offline Room DB caching.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Status Banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = when (currentStatus) {
                            ConnectionStatus.ONLINE -> Color(0xFFDCFCE7)
                            ConnectionStatus.OFFLINE -> Color(0xFFFEE2E2)
                            ConnectionStatus.SYNCING -> Color(0xFFDBEAFE)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (currentStatus) {
                                    ConnectionStatus.ONLINE -> Icons.Default.CloudDone
                                    ConnectionStatus.OFFLINE -> Icons.Default.CloudOff
                                    ConnectionStatus.SYNCING -> Icons.Default.Sync
                                },
                                contentDescription = null,
                                tint = when (currentStatus) {
                                    ConnectionStatus.ONLINE -> Color(0xFF15803D)
                                    ConnectionStatus.OFFLINE -> Color(0xFFB91C1C)
                                    ConnectionStatus.SYNCING -> Color(0xFF1D4ED8)
                                },
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = when (currentStatus) {
                                        ConnectionStatus.ONLINE -> "Connected to Central LEAP Server"
                                        ConnectionStatus.OFFLINE -> "Offline Mode Active (Room SQLite Active)"
                                        ConnectionStatus.SYNCING -> "Replicating Records..."
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = when (currentStatus) {
                                            ConnectionStatus.ONLINE -> Color(0xFF15803D)
                                            ConnectionStatus.OFFLINE -> Color(0xFFB91C1C)
                                            ConnectionStatus.SYNCING -> Color(0xFF1D4ED8)
                                        }
                                    )
                                )
                                Text(
                                    text = if (isOffline) "All log books, action plans, and weekly reports are stored securely on-device." else "Submissions immediately replicate to university database.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = when (currentStatus) {
                                            ConnectionStatus.ONLINE -> Color(0xFF166534)
                                            ConnectionStatus.OFFLINE -> Color(0xFF991B1B)
                                            ConnectionStatus.SYNCING -> Color(0xFF1E40AF)
                                        }
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Simulate Offline Mode",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Tests local queueing and data preservation when network drops.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Switch(
                            checked = isOffline,
                            onCheckedChange = { viewModel.toggleOfflineMode(it) },
                            modifier = Modifier.testTag("switch_offline_mode")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Pending Offline Sync Items",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "$pendingSync item(s) waiting for server replication",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = LeapCyan,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.syncPendingData { count ->
                                    syncSuccessBanner = "Successfully synchronized $count offline item(s) with university server!"
                                }
                            },
                            enabled = !isSyncing,
                            modifier = Modifier.testTag("button_sync_settings")
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Text("Sync Now", fontSize = 11.sp)
                            }
                        }
                    }

                    if (isOffline && pendingSync > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.triggerSimulatedConnectionRestored() },
                            modifier = Modifier.fillMaxWidth().testTag("simulate_restored_button")
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simulate Network Recovery Prompt", fontSize = 11.sp)
                        }
                    }

                    if (syncSuccessBanner != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFDCFCE7),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = syncSuccessBanner ?: "",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF166534),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Current Session & Role Switcher
            Text(
                text = "Demonstration Stakeholder Session",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Active User: ${authState.currentUser?.name ?: "Mohamed Kamara"}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Current Role: ${authState.selectedRole} • ${authState.currentUser?.email ?: "student@leap.demo"}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("STUDENT", "SUPERVISOR", "COORDINATOR", "ADMIN").forEach { role ->
                            val isCurrent = authState.selectedRole == role
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCurrent) LeapNavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.loginDemoUser(role) }
                            ) {
                                Text(
                                    text = role.take(4),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = onLogout,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        modifier = Modifier.fillMaxWidth().testTag("button_logout")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign Out to Login Screen")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Academic Dissertation Metadata
            Text(
                text = "Dissertation & Academic Credits",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LEAP Internship Manager (AI-Enabled)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Developed for Limkokwing University of Creative Technology, Sierra Leone as an academic dissertation prototype.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Target Architecture: MVVM with Kotlin Jetpack Compose & Material 3", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Local Persistence Engine: Android Jetpack Room SQLite Database", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Official LEAP Instruments: Form D (18-Scale), Form D2 (Parts 1-3), 8-Checklist, Action Plan", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• AI Governance Principle: Advisory assistance only; human academic supervisor retains grading authority", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ThemeModeOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) LeapNavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) BorderStroke(1.5.dp, LeapNavyPrimary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("theme_mode_$title")
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
