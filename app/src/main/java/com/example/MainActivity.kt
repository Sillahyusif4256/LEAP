package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.repository.LeapRepository
import com.example.ui.components.GlobalSyncRestoredPrompt
import com.example.ui.navigation.Screen
import com.example.ui.screens.ai.*
import com.example.ui.screens.auth.*
import com.example.ui.screens.coordinator.*
import com.example.ui.screens.settings.*
import com.example.ui.screens.student.*
import com.example.ui.screens.supervisor.*
import com.example.ui.theme.LeapTheme
import com.example.ui.viewmodel.LeapViewModel
import com.example.ui.viewmodel.LeapViewModelFactory
import com.example.ui.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = LeapRepository(database.leapDao())
        val viewModelFactory = LeapViewModelFactory(repository)

        setContent {
            val viewModel: LeapViewModel = viewModel(factory = viewModelFactory)
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            LeapTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LeapAppNavigation(viewModel = viewModel)
                        GlobalSyncRestoredPrompt(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun LeapAppNavigation(viewModel: LeapViewModel) {
    val navController = rememberNavController()
    val authState by viewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // --- Auth Flows ---
        composable(Screen.Splash.route) {
            SplashScreen(
                onContinue = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        "SUPERVISOR" -> Screen.SupervisorDashboard.route
                        "COORDINATOR", "ADMIN" -> Screen.CoordinatorDashboard.route
                        else -> Screen.StudentDashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onQuickDemoSelect = {
                    navController.navigate(Screen.RoleSelection.route)
                }
            )
        }

        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                viewModel = viewModel,
                onRoleSelected = { role ->
                    val destination = when (role) {
                        "SUPERVISOR" -> Screen.SupervisorDashboard.route
                        "COORDINATOR", "ADMIN" -> Screen.CoordinatorDashboard.route
                        else -> Screen.StudentDashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // --- Student Destinations ---
        composable(Screen.StudentDashboard.route) {
            StudentDashboardScreen(
                viewModel = viewModel,
                onNavigate = { route -> navController.navigate(route) },
                onSwitchRole = { navController.navigate(Screen.RoleSelection.route) }
            )
        }

        composable(Screen.StudentProfile.route) {
            StudentProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.InternshipApplication.route) {
            InternshipApplicationScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.InternshipPlacement.route) {
            InternshipPlacementScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ActionPlan.route) {
            ActionPlanScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DailyLogBook.route) {
            DailyLogBookScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToWeeklyReport = { navController.navigate(Screen.WeeklyReports.route) }
            )
        }

        composable(Screen.WeeklyReports.route) {
            WeeklyReportsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.StudentSelfEvaluation.route) {
            StudentSelfEvaluationScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SubmissionChecklist.route) {
            SubmissionChecklistScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToScanner = { formType ->
                    navController.navigate("${Screen.DocumentScanner.route}?formType=${formType ?: ""}")
                },
                onNavigateToArchive = {
                    navController.navigate(Screen.ScannedDocumentsList.route)
                }
            )
        }

        composable(Screen.InternshipReport.route) {
            InternshipReportScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("${Screen.DocumentScanner.route}?formType={formType}") { backStackEntry ->
            val formType = backStackEntry.arguments?.getString("formType")
            DocumentScannerScreen(
                viewModel = viewModel,
                initialFormType = formType,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToArchive = {
                    navController.navigate(Screen.ScannedDocumentsList.route)
                }
            )
        }

        composable(Screen.DocumentScanner.route) {
            DocumentScannerScreen(
                viewModel = viewModel,
                initialFormType = null,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToArchive = {
                    navController.navigate(Screen.ScannedDocumentsList.route)
                }
            )
        }

        composable(Screen.ScannedDocumentsList.route) {
            ScannedDocumentsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToScanner = { formType ->
                    navController.navigate("${Screen.DocumentScanner.route}?formType=${formType ?: ""}")
                }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- AI Features ---
        composable(Screen.LeapAiAssistant.route) {
            LeapAiAssistantScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AiInternshipMatch.route) {
            AiInternshipMatchScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AiProgressInsight.route) {
            AiProgressInsightScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- Supervisor Destinations ---
        composable(Screen.SupervisorDashboard.route) {
            SupervisorDashboardScreen(
                viewModel = viewModel,
                onNavigate = { route -> navController.navigate(route) },
                onSwitchRole = { navController.navigate(Screen.RoleSelection.route) }
            )
        }

        composable(Screen.FormDAssessment.route) {
            FormDAssessmentScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SupervisorFeedback.route) {
            SupervisorFeedbackScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- Coordinator & Admin Destinations ---
        composable(Screen.CoordinatorDashboard.route) {
            CoordinatorDashboardScreen(
                viewModel = viewModel,
                onNavigate = { route -> navController.navigate(route) },
                onSwitchRole = { navController.navigate(Screen.RoleSelection.route) }
            )
        }

        composable(Screen.StudentManagement.route) {
            StudentManagementScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.OrganizationManagement.route) {
            OrganizationManagementScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DissertationComparison.route) {
            DissertationComparisonScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
