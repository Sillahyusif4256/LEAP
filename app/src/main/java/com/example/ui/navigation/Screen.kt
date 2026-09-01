package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Splash : Screen("splash", "LEAP Portal")
    object Login : Screen("login", "Sign In")
    object RoleSelection : Screen("role_selection", "Demo Role Selector")

    // Student Routes
    object StudentDashboard : Screen("student_dashboard", "Student Dashboard")
    object StudentProfile : Screen("student_profile", "Student Profile")
    object InternshipApplication : Screen("internship_application", "Internship Application")
    object InternshipPlacement : Screen("internship_placement", "Placement Details")

    object ActionPlan : Screen("action_plan", "LEAP Action Plan")
    object DailyLogBook : Screen("daily_log_book", "Daily Log Book")
    object WeeklyReports : Screen("weekly_reports", "Weekly Reports")
    object StudentSelfEvaluation : Screen("self_evaluation", "Form D2 Self-Evaluation")
    object SubmissionChecklist : Screen("submission_checklist", "Submission Checklist")
    object InternshipReport : Screen("internship_report", "Internship Report")
    object DocumentScanner : Screen("document_scanner", "Scan Signed Form")
    object ScannedDocumentsList : Screen("scanned_documents", "Scanned Documents Archive")
    object Notifications : Screen("notifications", "Notifications")

    // AI Features
    object LeapAiAssistant : Screen("ai_assistant", "LEAP AI Assistant")
    object AiInternshipMatch : Screen("ai_internship_match", "AI Placement Match")
    object AiProgressInsight : Screen("ai_progress_insight", "AI Progress Insights")

    // Supervisor Routes
    object SupervisorDashboard : Screen("supervisor_dashboard", "Supervisor Dashboard")
    object FormDAssessment : Screen("form_d_assessment", "Form D Assessment")
    object SupervisorFeedback : Screen("supervisor_feedback", "Supervisor Feedback")

    // Coordinator & Admin Routes
    object CoordinatorDashboard : Screen("coordinator_dashboard", "Coordinator Dashboard")
    object StudentManagement : Screen("student_management", "Student Records")
    object OrganizationManagement : Screen("organization_management", "Host Organizations")
    object DissertationComparison : Screen("dissertation_comparison", "Paper vs Digital Process")
    object Settings : Screen("settings", "System Settings")
}
