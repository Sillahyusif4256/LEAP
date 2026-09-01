package com.example.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel

@Composable
fun LoginScreen(
    viewModel: LeapViewModel,
    onLoginSuccess: (String) -> Unit,
    onQuickDemoSelect: () -> Unit
) {
    var email by remember { mutableStateOf("student@leap.demo") }
    var password by remember { mutableStateOf("123456") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Header with University Brand
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.leap_hero_banner),
                contentDescription = "Limkokwing Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                LeapNavyDark.copy(alpha = 0.85f),
                                LeapNavyDark
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = LeapGoldAccent,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "DISSERTATION PROTOTYPE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "LEAP Internship Manager",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Limkokwing University of Creative Technology, Sierra Leone",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFCBD5E1)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Sign In to LEAP Portal",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Enter your credentials or choose a quick role below.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isLoading = true
                        viewModel.login(email, password) { success ->
                            isLoading = false
                            if (success) {
                                onLoginSuccess(viewModel.authState.value.selectedRole)
                            } else {
                                errorMessage = "Invalid login. Please select a demo account."
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Sign In", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onQuickDemoSelect,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("quick_role_select_button")
                ) {
                    Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select Quick Demo Role")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Demo Accounts Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = LeapGoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Demo Accounts (Password: 123456)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LeapNavyPrimary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                DemoAccountRow("Student:", "student@leap.demo", "Mohamed Kamara") {
                    email = "student@leap.demo"
                    password = "123456"
                }
                DemoAccountRow("Supervisor:", "supervisor@leap.demo", "Ing. David Koroma") {
                    email = "supervisor@leap.demo"
                    password = "123456"
                }
                DemoAccountRow("Coordinator:", "coordinator@leap.demo", "Dr. Fatmata Sesay") {
                    email = "coordinator@leap.demo"
                    password = "123456"
                }
                DemoAccountRow("Administrator:", "admin@leap.demo", "Registry ICT Admin") {
                    email = "admin@leap.demo"
                    password = "123456"
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DemoAccountRow(
    roleLabel: String,
    email: String,
    name: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() },
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$roleLabel $name",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A),
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 10.sp
                    )
                )
            }
            Text(
                text = "Use",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapCyan,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun RoleSelectionScreen(
    viewModel: LeapViewModel,
    onRoleSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select Demonstration Role",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = LeapNavyPrimary
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Switch between the 4 stakeholder perspectives defined in the LEAP dissertation system:",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        RoleCard(
            title = "1. Student Role",
            name = "Mohamed Kamara (LKW-SL-DEMO001)",
            description = "Daily logs, weekly reports, action plan formulation, Form D2 self-evaluation, and submission checklist.",
            icon = Icons.Default.School,
            badgeColor = Color(0xFFDCFCE7),
            testTag = "role_card_student"
        ) {
            viewModel.loginDemoUser("STUDENT")
            onRoleSelected("STUDENT")
        }

        Spacer(modifier = Modifier.height(12.dp))

        RoleCard(
            title = "2. Workplace Supervisor",
            name = "Ing. David Koroma (Tech Solutions SL)",
            description = "Monitor assigned interns, review daily logs, provide weekly feedback, and complete official Form D Assessment.",
            icon = Icons.Default.Work,
            badgeColor = Color(0xFFE0E7FF),
            testTag = "role_card_supervisor"
        ) {
            viewModel.loginDemoUser("SUPERVISOR")
            onRoleSelected("SUPERVISOR")
        }

        Spacer(modifier = Modifier.height(12.dp))

        RoleCard(
            title = "3. LEAP Coordinator",
            name = "Dr. Fatmata Sesay (Limkokwing Office)",
            description = "Institution-wide cohort tracking, document compliance verification, broadcast alerts, and AI progress insights.",
            icon = Icons.Default.AccountBalance,
            badgeColor = Color(0xFFFEF3C7),
            testTag = "role_card_coordinator"
        ) {
            viewModel.loginDemoUser("COORDINATOR")
            onRoleSelected("COORDINATOR")
        }

        Spacer(modifier = Modifier.height(12.dp))

        RoleCard(
            title = "4. Administrator",
            name = "Academic Systems Registry",
            description = "Host organization approvals, student placement records, supervisor assignments, and system audit logs.",
            icon = Icons.Default.AdminPanelSettings,
            badgeColor = Color(0xFFFCE7F3),
            testTag = "role_card_admin"
        ) {
            viewModel.loginDemoUser("ADMIN")
            onRoleSelected("ADMIN")
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    name: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = LeapNavyPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = LeapNavyPrimary
                    )
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = LeapCyan
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    ),
                    maxLines = 2
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8)
            )
        }
    }
}
