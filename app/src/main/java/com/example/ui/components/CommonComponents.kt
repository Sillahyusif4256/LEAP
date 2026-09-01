package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel

enum class ConnectionStatus {
    ONLINE, OFFLINE, SYNCING
}

@Composable
fun ConnectionBadge(
    status: ConnectionStatus,
    pendingCount: Int = 0,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon, label) = when (status) {
        ConnectionStatus.ONLINE -> Quadruple(
            Color(0xFFDCFCE7),
            Color(0xFF15803D),
            Icons.Default.CloudDone,
            "Online"
        )
        ConnectionStatus.OFFLINE -> Quadruple(
            Color(0xFFFEE2E2),
            Color(0xFFB91C1C),
            Icons.Default.CloudOff,
            if (pendingCount > 0) "Offline ($pendingCount)" else "Offline"
        )
        ConnectionStatus.SYNCING -> Quadruple(
            Color(0xFFDBEAFE),
            Color(0xFF1D4ED8),
            Icons.Default.Sync,
            "Syncing..."
        )
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("connection_status_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            if (status == ConnectionStatus.SYNCING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    color = textColor,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = textColor,
                    modifier = Modifier.size(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = textColor
                )
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeapTopAppBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    roleName: String = "STUDENT",
    isOffline: Boolean = false,
    isSyncing: Boolean = false,
    pendingSyncCount: Int = 0,
    onSyncClick: () -> Unit = {},
    unreadNotificationCount: Int = 0,
    onNotificationClick: () -> Unit = {},
    onRoleSwitchClick: () -> Unit = {}
) {
    val connectionStatus = when {
        isSyncing -> ConnectionStatus.SYNCING
        isOffline -> ConnectionStatus.OFFLINE
        else -> ConnectionStatus.ONLINE
    }

    Surface(
        color = LeapNavyPrimary,
        tonalElevation = 4.dp,
        shadowElevation = 3.dp,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Column {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("nav_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp, end = 8.dp)
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "LEAP",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    // Prominent Connection Badge
                    ConnectionBadge(
                        status = connectionStatus,
                        pendingCount = pendingSyncCount,
                        onClick = onSyncClick,
                        modifier = Modifier.padding(end = 6.dp)
                    )

                    // Role Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = when (roleName) {
                            "SUPERVISOR" -> Color(0xFF3B82F6)
                            "COORDINATOR" -> LeapGoldAccent
                            "ADMIN" -> Color(0xFFEC4899)
                            else -> LeapOrange
                        },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clickable { onRoleSwitchClick() }
                            .testTag("role_switch_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = roleName.take(4),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    // Notifications Bell
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier.testTag("notification_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationCount > 0) {
                                    Badge(containerColor = LeapOrange) {
                                        Text(unreadNotificationCount.toString(), color = Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    }
                }
            )

            // Prominent Full-Width Sync & Connectivity Ribbon
            AnimatedVisibility(visible = isOffline || pendingSyncCount > 0 || isSyncing) {
                Surface(
                    color = when {
                        isSyncing -> Color(0xFF1E3A8A)
                        isOffline -> Color(0xFF7F1D1D)
                        else -> Color(0xFF14532D)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = when {
                                    isSyncing -> Icons.Default.Sync
                                    isOffline -> Icons.Default.CloudOff
                                    else -> Icons.Default.CloudQueue
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when {
                                    isSyncing -> "Replicating local data with Limkokwing server..."
                                    isOffline -> "Offline Mode Active (Local Room SQLite DB)"
                                    else -> "Connection Restored • $pendingSyncCount items queued to sync"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (pendingSyncCount > 0 && !isSyncing) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                modifier = Modifier
                                    .clickable { onSyncClick() }
                                    .testTag("ribbon_sync_button")
                            ) {
                                Text(
                                    text = "Sync Now",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LeapNavyPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlobalSyncRestoredPrompt(viewModel: LeapViewModel) {
    val showPrompt by viewModel.showSyncRestoredPrompt.collectAsState()
    val pendingCount by viewModel.pendingSyncCount.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    if (showPrompt && pendingCount > 0) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSyncPrompt() },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Connection Restored",
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Internet Connection Restored",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Your device is back online. You have $pendingCount pending item(s) (daily logs, weekly reports, action plans) saved locally in Room SQLite database ready to replicate to the central LEAP server.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 Syncing preserves your exact submission timestamps and notifies your Workplace Supervisor.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.syncPendingData { }
                    },
                    enabled = !isSyncing,
                    modifier = Modifier.testTag("dialog_sync_now_button")
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text("Sync Now ($pendingCount items)")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissSyncPrompt() },
                    modifier = Modifier.testTag("dialog_sync_later_button")
                ) {
                    Text("Sync Later")
                }
            }
        )
    }
}

@Composable
fun PaperVsDigitalBadge(
    paperProcess: String,
    digitalProcess: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Digital Transformation",
                    tint = LeapGoldAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "DISSERTATION DIGITALIZATION CONTEXT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = LeapNavyPrimary,
                        letterSpacing = 0.5.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Existing Paper Workflow:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    )
                    Text(
                        text = paperProcess,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF475569),
                            fontSize = 11.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Digital LEAP Replacement:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = StatusActive
                        )
                    )
                    Text(
                        text = digitalProcess,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status.lowercase()) {
        "active", "approved", "completed", "checked", "on track", "superior" ->
            Triple(Color(0xFFDCFCE7), Color(0xFF166534), Icons.Default.CheckCircle)
        "pending", "submitted", "draft", "needs attention" ->
            Triple(Color(0xFFFEF3C7), Color(0xFF92400E), Icons.Default.Schedule)
        "requires changes", "at risk", "unsatisfactory", "requires revision" ->
            Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), Icons.Default.ErrorOutline)
        else -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), Icons.Default.Info)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun RatingBar5(
    rating: Int,
    onRatingSelected: (Int) -> Unit = {},
    isEditable: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..5).forEach { star ->
            IconButton(
                onClick = { if (isEditable) onRatingSelected(star) },
                enabled = isEditable,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("rating_star_$star")
            ) {
                Icon(
                    imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "$star Stars",
                    tint = if (star <= rating) LeapGoldAccent else Color(0xFFCBD5E1),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "($rating/5)",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector = Icons.Default.FolderOpen,
    title: String,
    description: String,
    actionButtonText: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        )
        if (actionButtonText != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onActionClick,
                modifier = Modifier.testTag("empty_state_action_button")
            ) {
                Text(actionButtonText)
            }
        }
    }
}
