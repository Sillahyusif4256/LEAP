package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ChatMessage
import com.example.data.ai.InternshipMatchResult
import com.example.data.ai.LeapFaqItem
import com.example.data.ai.LeapKnowledgeBase
import com.example.data.ai.LeapKnowledgeCategory
import com.example.data.ai.MessageSender
import com.example.data.ai.StudentProgressInsight
import com.example.data.local.entities.*
import com.example.data.repository.LeapRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

data class AuthState(
    val currentUser: UserEntity? = null,
    val isAuthenticated: Boolean = false,
    val selectedRole: String = "STUDENT",
    val error: String? = null
)

class LeapViewModel(private val repository: LeapRepository) : ViewModel() {

    // --- Theme Mode State ---
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    // --- Auth State ---
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // --- Active Selected Student ID ---
    private val _selectedStudentId = MutableStateFlow<Long>(1L)
    val selectedStudentId: StateFlow<Long> = _selectedStudentId.asStateFlow()

    // --- Selected Week for Log Filtering ---
    private val _selectedWeek = MutableStateFlow(8)
    val selectedWeek: StateFlow<Int> = _selectedWeek.asStateFlow()

    // --- AI Chat State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = MessageSender.AI,
                text = "Welcome to the Limkokwing LEAP AI Assistant! I can help you understand internship procedures, Form D supervisor assessments, Form D2 self-evaluations, daily log formatting (7 required fields), 8-item submission checklists, and critical internship deadlines.",
                timestamp = "Ready"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // --- AI Internship Matches ---
    private val _aiMatches = MutableStateFlow<List<InternshipMatchResult>>(emptyList())
    val aiMatches: StateFlow<List<InternshipMatchResult>> = _aiMatches.asStateFlow()
    val isMatching = MutableStateFlow(false)

    // --- AI Progress Insight ---
    private val _aiInsight = MutableStateFlow<StudentProgressInsight?>(null)
    val aiInsight: StateFlow<StudentProgressInsight?> = _aiInsight.asStateFlow()
    val isGeneratingInsight = MutableStateFlow(false)

    // --- Offline & Sync ---
    val isOfflineMode: StateFlow<Boolean> = repository.isOfflineMode
    val isSyncing: StateFlow<Boolean> = repository.isSyncing
    val pendingSyncCount: StateFlow<Int> = repository.pendingSyncCount

    private val _showSyncRestoredPrompt = MutableStateFlow(false)
    val showSyncRestoredPrompt: StateFlow<Boolean> = _showSyncRestoredPrompt.asStateFlow()

    fun dismissSyncPrompt() {
        _showSyncRestoredPrompt.value = false
    }

    // --- Repository Exposed Flows ---
    val allStudents: StateFlow<List<StudentEntity>> = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSupervisors: StateFlow<List<SupervisorEntity>> = repository.getAllSupervisors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrganizations: StateFlow<List<OrganizationEntity>> = repository.getAllOrganizations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentStudent: StateFlow<StudentEntity?> = _selectedStudentId
        .flatMapLatest { id -> repository.getStudentById(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentInternship: StateFlow<InternshipEntity?> = _selectedStudentId
        .flatMapLatest { id -> repository.getInternshipByStudentId(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val actionPlan: StateFlow<ActionPlanEntity?> = _selectedStudentId
        .flatMapLatest { id -> repository.getActionPlanByStudentId(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val actionPlanTasks: StateFlow<List<ActionPlanTaskEntity>> = _selectedStudentId
        .flatMapLatest { id -> repository.getActionPlanTasksByStudent(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyLogs: StateFlow<List<DailyLogEntity>> = _selectedStudentId
        .flatMapLatest { id -> repository.getDailyLogsByStudent(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyReports: StateFlow<List<WeeklyReportEntity>> = _selectedStudentId
        .flatMapLatest { id -> repository.getWeeklyReportsByStudent(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supervisorFeedbacks: StateFlow<List<SupervisorFeedbackEntity>> = _selectedStudentId
        .flatMapLatest { id -> repository.getFeedbacksByStudent(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assessment: StateFlow<AssessmentEntity?> = _selectedStudentId
        .flatMapLatest { id -> repository.getAssessmentByStudentId(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val assessmentItems: StateFlow<List<AssessmentItemEntity>> = assessment
        .flatMapLatest { ass ->
            if (ass != null) repository.getAssessmentItems(ass.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selfEvaluation: StateFlow<SelfEvaluationEntity?> = _selectedStudentId
        .flatMapLatest { id -> repository.getSelfEvaluationByStudentId(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val checklist: StateFlow<SubmissionChecklistEntity?> = _selectedStudentId
        .flatMapLatest { id -> repository.getChecklistByStudentId(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val internshipReport: StateFlow<InternshipReportEntity?> = _selectedStudentId
        .flatMapLatest { id -> repository.getInternshipReportByStudentId(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notifications: StateFlow<List<NotificationEntity>> = _authState
        .flatMapLatest { auth ->
            val userId = auth.currentUser?.id ?: 1L
            val role = auth.selectedRole
            repository.getNotificationsForUser(userId, role)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scannedDocuments: StateFlow<List<ScannedDocumentEntity>> = _selectedStudentId
        .flatMapLatest { id -> repository.getScannedDocumentsByStudent(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentApplication: StateFlow<InternshipApplicationEntity?> = _selectedStudentId
        .flatMapLatest { id -> repository.getLatestApplicationByStudent(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allApplications: StateFlow<List<InternshipApplicationEntity>> = repository.getAllApplications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        // Default login as Student Mohamed Kamara for initial seamless experience
        loginDemoUser("STUDENT")
    }

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(email.trim(), pass)
            if (user != null) {
                _authState.value = AuthState(
                    currentUser = user,
                    isAuthenticated = true,
                    selectedRole = user.role,
                    error = null
                )
                if (user.role == "STUDENT") {
                    _selectedStudentId.value = 1L
                }
                onResult(true)
            } else {
                _authState.value = _authState.value.copy(error = "Invalid credentials. Please use demo accounts.")
                onResult(false)
            }
        }
    }

    fun loginDemoUser(role: String) {
        val (email, name, desig, dept, code) = when (role) {
            "SUPERVISOR" -> listOf("supervisor@leap.demo", "Ing. David Koroma", "Head of Software Engineering", "Engineering", "")
            "COORDINATOR" -> listOf("coordinator@leap.demo", "Dr. Fatmata Sesay", "Director of LEAP Directorate", "Academic Registry", "")
            "ADMIN" -> listOf("admin@leap.demo", "System Administrator", "ICT Lead", "ICT Directorate", "")
            else -> listOf("student@leap.demo", "Mohamed Kamara", "Intern", "Software Engineering", "LKW-SL-DEMO001")
        }

        val user = UserEntity(
            id = if (role == "STUDENT") 1L else if (role == "SUPERVISOR") 2L else if (role == "COORDINATOR") 3L else 4L,
            email = email,
            name = name,
            role = role,
            designation = desig,
            department = dept,
            studentCode = code
        )

        _authState.value = AuthState(
            currentUser = user,
            isAuthenticated = true,
            selectedRole = role,
            error = null
        )
    }

    fun logout() {
        _authState.value = AuthState(currentUser = null, isAuthenticated = false, selectedRole = "STUDENT")
    }

    fun setSelectedStudentId(id: Long) {
        _selectedStudentId.value = id
    }

    fun setSelectedWeek(week: Int) {
        _selectedWeek.value = week
    }

    // --- Action Plan ---
    fun addActionPlanTask(taskDesc: String, byWhen: String) {
        viewModelScope.launch {
            val studentId = _selectedStudentId.value
            val currentPlan = actionPlan.value
            val planId = currentPlan?.id ?: 1L
            repository.insertActionPlanTask(
                ActionPlanTaskEntity(
                    actionPlanId = planId,
                    studentId = studentId,
                    taskDescription = taskDesc,
                    byWhen = byWhen,
                    isAchieved = false,
                    isApprovedBySupervisor = false
                )
            )
        }
    }

    fun toggleTaskAchieved(task: ActionPlanTaskEntity) {
        viewModelScope.launch {
            repository.updateActionPlanTask(task.copy(isAchieved = !task.isAchieved))
        }
    }

    fun approveActionPlanTask(task: ActionPlanTaskEntity) {
        viewModelScope.launch {
            repository.updateActionPlanTask(task.copy(isApprovedBySupervisor = true))
        }
    }

    fun approveActionPlan() {
        viewModelScope.launch {
            val plan = actionPlan.value ?: return@launch
            repository.updateActionPlan(
                plan.copy(
                    isSupervisorSigned = true,
                    isCompanyStamped = true,
                    approvalStatus = "Approved",
                    supervisorSignatureDate = "10-Mar-2026"
                )
            )
        }
    }

    // --- Daily Logs ---
    fun addDailyLog(
        weekNumber: Int,
        date: String,
        dayOfWeek: String,
        activity: String,
        tools: String,
        people: String,
        skills: String,
        challenges: String,
        reflection: String
    ) {
        viewModelScope.launch {
            repository.insertDailyLog(
                DailyLogEntity(
                    studentId = _selectedStudentId.value,
                    weekNumber = weekNumber,
                    date = date,
                    dayOfWeek = dayOfWeek,
                    taskActivity = activity,
                    toolsMethods = tools,
                    peopleWorkedWith = people,
                    skillsLearned = skills,
                    challenges = challenges,
                    reflection = reflection,
                    isSynced = !isOfflineMode.value
                )
            )
        }
    }

    fun deleteDailyLog(log: DailyLogEntity) {
        viewModelScope.launch {
            repository.deleteDailyLog(log)
        }
    }

    // --- Weekly Reports ---
    fun submitWeeklyReport(
        weekNumber: Int,
        dateRange: String,
        activities: String,
        skills: String,
        challenges: String,
        reflection: String
    ) {
        viewModelScope.launch {
            val existing = repository.getWeeklyReportByStudentAndWeek(_selectedStudentId.value, weekNumber).first()
            if (existing != null) {
                repository.updateWeeklyReport(
                    existing.copy(
                        dateRange = dateRange,
                        activitiesCompleted = activities,
                        skillsLearned = skills,
                        challenges = challenges,
                        reflection = reflection,
                        submissionStatus = "Submitted",
                        submittedDate = "Today"
                    )
                )
            } else {
                repository.insertWeeklyReport(
                    WeeklyReportEntity(
                        studentId = _selectedStudentId.value,
                        weekNumber = weekNumber,
                        dateRange = dateRange,
                        activitiesCompleted = activities,
                        skillsLearned = skills,
                        challenges = challenges,
                        reflection = reflection,
                        submissionStatus = "Submitted",
                        submittedDate = "Today"
                    )
                )
            }
        }
    }

    fun reviewWeeklyReport(report: WeeklyReportEntity, feedback: String, status: String, rating: Int) {
        viewModelScope.launch {
            repository.updateWeeklyReport(
                report.copy(
                    supervisorFeedback = feedback,
                    submissionStatus = status,
                    reviewedDate = "Today",
                    feedbackRating = rating
                )
            )
            repository.insertSupervisorFeedback(
                SupervisorFeedbackEntity(
                    studentId = report.studentId,
                    supervisorId = 1,
                    weekNumber = report.weekNumber,
                    targetType = "WEEKLY_REPORT",
                    content = feedback,
                    status = status
                )
            )
        }
    }

    // --- Form D Assessment ---
    fun updateAssessmentItemRating(item: AssessmentItemEntity, newRating: Int) {
        viewModelScope.launch {
            repository.updateAssessmentItem(item.copy(rating = newRating))
        }
    }

    fun submitAssessment(
        overallRating: Float,
        overallAssessment: String,
        strengths: String,
        recommendations: String,
        technicalSkills: String,
        objectivesMet: Boolean,
        comments: String
    ) {
        viewModelScope.launch {
            val existing = assessment.value
            if (existing != null) {
                repository.updateAssessment(
                    existing.copy(
                        overallPerformanceRating = overallRating,
                        overallAssessment = overallAssessment,
                        majorStrengths = strengths,
                        academicWorkRecommendations = recommendations,
                        technicalSkillsGained = technicalSkills,
                        wereInternshipObjectivesMet = objectivesMet,
                        otherComments = comments,
                        isSubmitted = true,
                        submittedDate = "Today"
                    )
                )
            }
        }
    }

    // --- Form D2 Self-Evaluation ---
    fun saveSelfEvaluation(eval: SelfEvaluationEntity) {
        viewModelScope.launch {
            repository.updateSelfEvaluation(eval.copy(isSubmitted = true, submittedDate = "Today"))
        }
    }

    // --- Submission Checklist ---
    fun toggleChecklistItem(itemKey: String, isSupervisor: Boolean) {
        viewModelScope.launch {
            val c = checklist.value ?: return@launch
            val updated = when (itemKey) {
                "formD" -> if (isSupervisor) c.copy(formDSupervisorCheck = !c.formDSupervisorCheck) else c.copy(formDStudentCheck = !c.formDStudentCheck)
                "selfEval" -> if (isSupervisor) c.copy(selfEvalSupervisorCheck = !c.selfEvalSupervisorCheck) else c.copy(selfEvalStudentCheck = !c.selfEvalStudentCheck)
                "logBook" -> if (isSupervisor) c.copy(logBookSupervisorCheck = !c.logBookSupervisorCheck) else c.copy(logBookStudentCheck = !c.logBookStudentCheck)
                "actionPlan" -> if (isSupervisor) c.copy(actionPlanSupervisorCheck = !c.actionPlanSupervisorCheck) else c.copy(actionPlanStudentCheck = !c.actionPlanStudentCheck)
                "formA2" -> if (isSupervisor) c.copy(formA2SupervisorCheck = !c.formA2SupervisorCheck) else c.copy(formA2StudentCheck = !c.formA2StudentCheck)
                "formA3" -> if (isSupervisor) c.copy(formA3SupervisorCheck = !c.formA3SupervisorCheck) else c.copy(formA3StudentCheck = !c.formA3StudentCheck)
                "formB" -> if (isSupervisor) c.copy(formBSupervisorCheck = !c.formBSupervisorCheck) else c.copy(formBStudentCheck = !c.formBStudentCheck)
                "report" -> if (isSupervisor) c.copy(reportSupervisorCheck = !c.reportSupervisorCheck) else c.copy(reportStudentCheck = !c.reportStudentCheck)
                else -> c
            }
            repository.updateChecklist(updated)
        }
    }

    // --- Internship Report ---
    fun uploadInternshipReport(title: String, fileName: String, fileSize: String) {
        viewModelScope.launch {
            val current = internshipReport.value
            if (current != null) {
                repository.updateInternshipReport(
                    current.copy(
                        title = title,
                        fileName = fileName,
                        fileSize = fileSize,
                        uploadDate = "Today",
                        status = "Submitted",
                        version = current.version + 1
                    )
                )
            } else {
                repository.insertInternshipReport(
                    InternshipReportEntity(
                        studentId = _selectedStudentId.value,
                        title = title,
                        fileName = fileName,
                        fileSize = fileSize,
                        uploadDate = "Today",
                        status = "Submitted"
                    )
                )
            }
        }
    }

    // --- Notifications ---
    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun sendBroadcastNotification(title: String, message: String, role: String) {
        viewModelScope.launch {
            repository.insertNotification(
                NotificationEntity(
                    userId = 0,
                    role = role,
                    title = title,
                    message = message,
                    type = "ANNOUNCEMENT",
                    timeAgo = "Just now"
                )
            )
        }
    }

    // --- AI Features ---
    fun askAiAssistant(question: String) {
        if (question.isBlank()) return
        val currentHistory = _chatMessages.value
        val userMsg = ChatMessage(sender = MessageSender.USER, text = question.trim())
        _chatMessages.value = currentHistory + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            val response = repository.askAiAssistant(question.trim(), currentHistory)
            _chatMessages.value = _chatMessages.value + ChatMessage(
                sender = MessageSender.AI,
                text = response,
                timestamp = "Just now",
                isGeminiPowered = true
            )
            _isAiThinking.value = false
        }
    }

    fun analyzeSupervisorFeedback(comment: String, rating: Int) {
        if (comment.isBlank()) return
        val promptText = "Analyze Supervisor Feedback (Rating: $rating/5): \"$comment\""
        val currentHistory = _chatMessages.value
        val userMsg = ChatMessage(sender = MessageSender.USER, text = promptText)
        _chatMessages.value = currentHistory + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            val response = repository.analyzeSupervisorFeedback(comment.trim(), rating)
            _chatMessages.value = _chatMessages.value + ChatMessage(
                sender = MessageSender.AI,
                text = response,
                timestamp = "Just now",
                isGeminiPowered = true
            )
            _isAiThinking.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = MessageSender.AI,
                text = "Chat history cleared. How can I help you today with your internship requirements or workplace feedback?",
                timestamp = "Ready",
                isGeminiPowered = true
            )
        )
    }

    fun generateInternshipMatches(
        programme: String,
        skills: String,
        interests: String,
        department: String,
        location: String,
        experience: String
    ) {
        isMatching.value = true
        viewModelScope.launch {
            val results = repository.getInternshipMatches(programme, skills, interests, department, location, experience)
            _aiMatches.value = results
            isMatching.value = false
        }
    }

    fun generateProgressInsight(student: StudentEntity) {
        isGeneratingInsight.value = true
        viewModelScope.launch {
            val hasMissing = student.progressPercentage < 50
            val insight = repository.generateProgressInsights(
                studentName = student.name,
                progressPct = student.progressPercentage,
                currentWeek = student.currentWeek,
                hasMissingDocs = hasMissing
            )
            _aiInsight.value = insight
            isGeneratingInsight.value = false
        }
    }

    // --- Offline & Sync Actions ---
    fun toggleOfflineMode(enabled: Boolean) {
        val wasOffline = isOfflineMode.value
        repository.setOfflineMode(enabled)
        if (wasOffline && !enabled && pendingSyncCount.value > 0) {
            _showSyncRestoredPrompt.value = true
        }
    }

    fun triggerSimulatedConnectionRestored() {
        if (isOfflineMode.value) {
            repository.setOfflineMode(false)
            if (pendingSyncCount.value > 0) {
                _showSyncRestoredPrompt.value = true
            }
        }
    }

    fun syncPendingData(onCompleted: (Int) -> Unit) {
        viewModelScope.launch {
            val synced = repository.simulateSync()
            _showSyncRestoredPrompt.value = false
            onCompleted(synced)
        }
    }

    // --- Scanned Documents Management ---
    fun saveScannedDocument(
        documentType: String,
        title: String,
        formCode: String,
        imageUri: String,
        pageCount: Int = 1,
        filterApplied: String = "ENHANCED_BW",
        isSupervisorSigned: Boolean = true,
        isCompanyStamped: Boolean = true,
        supervisorName: String = "Ing. David Koroma",
        companyName: String = "Tech Solutions SL Ltd.",
        notes: String = "",
        associatedChecklistItem: String = "",
        onCompleted: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val studentId = _selectedStudentId.value
            val doc = ScannedDocumentEntity(
                studentId = studentId,
                documentType = documentType,
                title = title,
                formCode = formCode,
                imageUri = imageUri,
                capturedDate = java.text.SimpleDateFormat("dd-MMM-yyyy hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
                fileSize = "${(1.2 + Math.random() * 1.5).toString().take(3)} MB",
                pageCount = pageCount,
                filterApplied = filterApplied,
                isSupervisorSigned = isSupervisorSigned,
                isCompanyStamped = isCompanyStamped,
                verificationStatus = "VERIFIED",
                supervisorName = supervisorName,
                companyName = companyName,
                notes = notes,
                associatedChecklistItem = associatedChecklistItem
            )
            val docId = repository.insertScannedDocument(doc)

            // Auto-update submission checklist if this document satisfies a checklist item
            val curChecklist = checklist.value
            if (curChecklist != null && associatedChecklistItem.isNotBlank()) {
                val updated = when (associatedChecklistItem) {
                    "formA2" -> curChecklist.copy(formA2StudentCheck = true, formA2SupervisorCheck = true, formA2Status = "Approved")
                    "formA3" -> curChecklist.copy(formA3StudentCheck = true, formA3SupervisorCheck = true, formA3Status = "Approved")
                    "actionPlan" -> curChecklist.copy(actionPlanStudentCheck = true, actionPlanSupervisorCheck = true, actionPlanStatus = "Approved")
                    "formB" -> curChecklist.copy(formBStudentCheck = true, formBSupervisorCheck = true, formBStatus = "Approved")
                    "formD" -> curChecklist.copy(formDStudentCheck = true, formDSupervisorCheck = true, formDStatus = "Approved")
                    "selfEval" -> curChecklist.copy(selfEvalStudentCheck = true, selfEvalSupervisorCheck = true, selfEvalStatus = "Approved")
                    "logBook" -> curChecklist.copy(logBookStudentCheck = true, logBookSupervisorCheck = true, logBookStatus = "Approved")
                    "report" -> curChecklist.copy(reportStudentCheck = true, reportStatus = "Submitted")
                    else -> curChecklist
                }
                repository.updateChecklist(updated)
            }

            // Also post notification
            repository.insertNotification(
                NotificationEntity(
                    userId = studentId,
                    role = "STUDENT",
                    title = "Document Scanned & Stored",
                    message = "$title ($formCode) successfully scanned, verified, and saved to your LEAP records.",
                    type = "APPROVAL",
                    timeAgo = "Just now"
                )
            )

            onCompleted(docId)
        }
    }

    fun deleteScannedDocument(docId: Long) {
        viewModelScope.launch {
            repository.deleteScannedDocumentById(docId)
        }
    }

    // --- Internship Application Actions ---
    fun saveApplicationDraft(application: InternshipApplicationEntity, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val appToSave = application.copy(
                submissionStatus = "Draft",
                timestamp = System.currentTimeMillis()
            )
            val id = repository.insertApplication(appToSave)
            onSaved(id)
        }
    }

    fun submitInternshipApplication(
        application: InternshipApplicationEntity,
        onSuccess: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val studentId = application.studentId
            val refCode = "APP-LEAP-2026-${(1000..9999).random()}"
            val submittedApp = application.copy(
                applicationRefNumber = if (application.applicationRefNumber.startsWith("APP-")) application.applicationRefNumber else refCode,
                submissionStatus = "Submitted",
                submittedDate = java.text.SimpleDateFormat("dd-MMM-yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                timestamp = System.currentTimeMillis()
            )
            val appId = repository.insertApplication(submittedApp)

            // Post notification to student
            repository.insertNotification(
                NotificationEntity(
                    userId = studentId,
                    role = "STUDENT",
                    title = "Internship Application Submitted",
                    message = "Your LEAP placement application (${submittedApp.applicationRefNumber}) has been received for ${submittedApp.primarySector}. Academic coordinator review is in progress.",
                    type = "APPROVAL",
                    timeAgo = "Just now"
                )
            )

            // Post notification to Coordinator
            repository.insertNotification(
                NotificationEntity(
                    userId = 3L, // Coordinator Dr Fatmata Sesay
                    role = "COORDINATOR",
                    title = "New Internship Application",
                    message = "${application.fullName} (${application.studentIdNumber}) submitted a placement application for ${application.primarySector}.",
                    type = "ANNOUNCEMENT",
                    timeAgo = "Just now"
                )
            )

            onSuccess(appId)
        }
    }

    fun deleteApplication(appId: Long) {
        viewModelScope.launch {
            repository.deleteApplicationById(appId)
        }
    }

    // --- Admin Additions ---

    fun addOrganization(name: String, industry: String, address: String, contactPerson: String, email: String, phone: String) {
        viewModelScope.launch {
            repository.insertOrganization(
                OrganizationEntity(
                    name = name,
                    industry = industry,
                    address = address,
                    contactPerson = contactPerson,
                    contactEmail = email,
                    contactPhone = phone
                )
            )
        }
    }

    fun addStudent(name: String, code: String, programme: String, orgId: Long, dept: String) {
        viewModelScope.launch {
            repository.insertStudent(
                StudentEntity(
                    userId = 0,
                    studentIdCode = code,
                    name = name,
                    programme = programme,
                    email = "${code.lowercase()}@limkokwing.edu.sl",
                    phone = "+232 78 000000",
                    organizationId = orgId,
                    department = dept,
                    supervisorId = 1,
                    commencementDate = "01-Feb-2026",
                    completionDate = "25-Apr-2026",
                    internshipStatus = "Active",
                    progressPercentage = 10,
                    currentWeek = 1
                )
            )
        }
    }
}

class LeapViewModelFactory(private val repository: LeapRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeapViewModel::class.java)) {
            return LeapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
