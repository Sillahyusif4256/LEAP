package com.example.data.repository

import com.example.data.ai.*
import com.example.data.local.dao.LeapDao
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeapRepository(
    private val dao: LeapDao,
    private val aiService: LeapAiService = LeapAiServiceImpl()
) {

    // Offline / Sync Simulation state
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode = _isOfflineMode.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _pendingSyncCount = MutableStateFlow(0)
    val pendingSyncCount = _pendingSyncCount.asStateFlow()

    fun setOfflineMode(offline: Boolean) {
        _isOfflineMode.value = offline
    }

    suspend fun simulateSync(): Int {
        _isSyncing.value = true
        kotlinx.coroutines.delay(1200) // Simulate fast network sync
        val syncedItems = 3
        _pendingSyncCount.value = 0
        _isSyncing.value = false
        return syncedItems
    }

    // --- Users & Auth ---
    suspend fun login(email: String, password: String): UserEntity? {
        val user = dao.getUserByEmail(email)
        return if (user != null && user.passwordHash == password) user else null
    }

    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()

    // --- Students ---
    fun getAllStudents(): Flow<List<StudentEntity>> = dao.getAllStudents()
    fun getStudentById(id: Long): Flow<StudentEntity?> = dao.getStudentById(id)
    fun getStudentByUserId(userId: Long): Flow<StudentEntity?> = dao.getStudentByUserId(userId)
    fun getStudentsBySupervisor(supervisorId: Long): Flow<List<StudentEntity>> = dao.getStudentsBySupervisor(supervisorId)
    fun getStudentsByOrganization(orgId: Long): Flow<List<StudentEntity>> = dao.getStudentsByOrganization(orgId)
    suspend fun insertStudent(student: StudentEntity): Long = dao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = dao.updateStudent(student)
    suspend fun deleteStudent(student: StudentEntity) = dao.deleteStudent(student)

    // --- Organizations ---
    fun getAllOrganizations(): Flow<List<OrganizationEntity>> = dao.getAllOrganizations()
    fun getOrganizationById(id: Long): Flow<OrganizationEntity?> = dao.getOrganizationById(id)
    suspend fun insertOrganization(org: OrganizationEntity): Long = dao.insertOrganization(org)
    suspend fun updateOrganization(org: OrganizationEntity) = dao.updateOrganization(org)

    // --- Supervisors ---
    fun getAllSupervisors(): Flow<List<SupervisorEntity>> = dao.getAllSupervisors()
    fun getSupervisorById(id: Long): Flow<SupervisorEntity?> = dao.getSupervisorById(id)
    fun getSupervisorByUserId(userId: Long): Flow<SupervisorEntity?> = dao.getSupervisorByUserId(userId)
    suspend fun insertSupervisor(supervisor: SupervisorEntity): Long = dao.insertSupervisor(supervisor)

    // --- Internships ---
    fun getInternshipByStudentId(studentId: Long): Flow<InternshipEntity?> = dao.getInternshipByStudentId(studentId)
    fun getAllInternships(): Flow<List<InternshipEntity>> = dao.getAllInternships()
    suspend fun insertInternship(internship: InternshipEntity): Long = dao.insertInternship(internship)
    suspend fun updateInternship(internship: InternshipEntity) = dao.updateInternship(internship)

    // --- Action Plan & Tasks ---
    fun getActionPlanByStudentId(studentId: Long): Flow<ActionPlanEntity?> = dao.getActionPlanByStudentId(studentId)
    fun getActionPlanTasksByStudent(studentId: Long): Flow<List<ActionPlanTaskEntity>> = dao.getActionPlanTasksByStudent(studentId)
    suspend fun insertActionPlan(plan: ActionPlanEntity): Long = dao.insertActionPlan(plan)
    suspend fun updateActionPlan(plan: ActionPlanEntity) = dao.updateActionPlan(plan)
    suspend fun insertActionPlanTask(task: ActionPlanTaskEntity): Long = dao.insertActionPlanTask(task)
    suspend fun updateActionPlanTask(task: ActionPlanTaskEntity) = dao.updateActionPlanTask(task)
    suspend fun deleteActionPlanTask(task: ActionPlanTaskEntity) = dao.deleteActionPlanTask(task)

    // --- Daily Logs ---
    fun getDailyLogsByStudent(studentId: Long): Flow<List<DailyLogEntity>> = dao.getDailyLogsByStudent(studentId)
    fun getDailyLogsByStudentAndWeek(studentId: Long, week: Int): Flow<List<DailyLogEntity>> = dao.getDailyLogsByStudentAndWeek(studentId, week)
    suspend fun insertDailyLog(log: DailyLogEntity): Long {
        val id = dao.insertDailyLog(log)
        if (_isOfflineMode.value) {
            _pendingSyncCount.value += 1
        }
        return id
    }
    suspend fun updateDailyLog(log: DailyLogEntity) = dao.updateDailyLog(log)
    suspend fun deleteDailyLog(log: DailyLogEntity) = dao.deleteDailyLog(log)

    // --- Weekly Reports ---
    fun getWeeklyReportsByStudent(studentId: Long): Flow<List<WeeklyReportEntity>> = dao.getWeeklyReportsByStudent(studentId)
    fun getWeeklyReportByStudentAndWeek(studentId: Long, week: Int): Flow<WeeklyReportEntity?> = dao.getWeeklyReportByStudentAndWeek(studentId, week)
    fun getSubmittedWeeklyReports(): Flow<List<WeeklyReportEntity>> = dao.getSubmittedWeeklyReports()
    suspend fun insertWeeklyReport(report: WeeklyReportEntity): Long = dao.insertWeeklyReport(report)
    suspend fun updateWeeklyReport(report: WeeklyReportEntity) = dao.updateWeeklyReport(report)

    // --- Supervisor Feedback ---
    fun getFeedbacksByStudent(studentId: Long): Flow<List<SupervisorFeedbackEntity>> = dao.getFeedbacksByStudent(studentId)
    suspend fun insertSupervisorFeedback(feedback: SupervisorFeedbackEntity): Long = dao.insertSupervisorFeedback(feedback)

    // --- Assessments (Form D) ---
    fun getAssessmentByStudentId(studentId: Long): Flow<AssessmentEntity?> = dao.getAssessmentByStudentId(studentId)
    fun getAllAssessments(): Flow<List<AssessmentEntity>> = dao.getAllAssessments()
    suspend fun insertAssessment(assessment: AssessmentEntity): Long = dao.insertAssessment(assessment)
    suspend fun updateAssessment(assessment: AssessmentEntity) = dao.updateAssessment(assessment)
    fun getAssessmentItems(assessmentId: Long): Flow<List<AssessmentItemEntity>> = dao.getAssessmentItems(assessmentId)
    suspend fun updateAssessmentItem(item: AssessmentItemEntity) = dao.updateAssessmentItem(item)

    // --- Self Evaluations (Form D2) ---
    fun getSelfEvaluationByStudentId(studentId: Long): Flow<SelfEvaluationEntity?> = dao.getSelfEvaluationByStudentId(studentId)
    suspend fun insertSelfEvaluation(selfEval: SelfEvaluationEntity): Long = dao.insertSelfEvaluation(selfEval)
    suspend fun updateSelfEvaluation(selfEval: SelfEvaluationEntity) = dao.updateSelfEvaluation(selfEval)

    // --- Submission Checklists ---
    fun getChecklistByStudentId(studentId: Long): Flow<SubmissionChecklistEntity?> = dao.getChecklistByStudentId(studentId)
    suspend fun insertChecklist(checklist: SubmissionChecklistEntity): Long = dao.insertChecklist(checklist)
    suspend fun updateChecklist(checklist: SubmissionChecklistEntity) = dao.updateChecklist(checklist)

    // --- Internship Reports ---
    fun getInternshipReportByStudentId(studentId: Long): Flow<InternshipReportEntity?> = dao.getInternshipReportByStudentId(studentId)
    suspend fun insertInternshipReport(report: InternshipReportEntity): Long = dao.insertInternshipReport(report)
    suspend fun updateInternshipReport(report: InternshipReportEntity) = dao.updateInternshipReport(report)

    // --- Notifications ---
    fun getNotificationsForUser(userId: Long, role: String): Flow<List<NotificationEntity>> = dao.getNotificationsForUser(userId, role)
    suspend fun insertNotification(notification: NotificationEntity): Long = dao.insertNotification(notification)
    suspend fun markNotificationAsRead(id: Long) = dao.markNotificationAsRead(id)
    suspend fun markAllNotificationsAsRead(userId: Long) = dao.markAllNotificationsAsRead(userId)

    // --- Scanned Documents ---
    fun getScannedDocumentsByStudent(studentId: Long): Flow<List<ScannedDocumentEntity>> = dao.getScannedDocumentsByStudent(studentId)
    fun getScannedDocumentsByType(studentId: Long, type: String): Flow<List<ScannedDocumentEntity>> = dao.getScannedDocumentsByType(studentId, type)
    fun getScannedDocumentById(id: Long): Flow<ScannedDocumentEntity?> = dao.getScannedDocumentById(id)
    suspend fun insertScannedDocument(document: ScannedDocumentEntity): Long {
        val id = dao.insertScannedDocument(document)
        if (_isOfflineMode.value) {
            _pendingSyncCount.value += 1
        }
        return id
    }
    suspend fun updateScannedDocument(document: ScannedDocumentEntity) = dao.updateScannedDocument(document)
    suspend fun deleteScannedDocument(document: ScannedDocumentEntity) = dao.deleteScannedDocument(document)
    suspend fun deleteScannedDocumentById(id: Long) = dao.deleteScannedDocumentById(id)

    // --- Internship Applications ---
    fun getApplicationsByStudent(studentId: Long): Flow<List<InternshipApplicationEntity>> = dao.getApplicationsByStudent(studentId)
    fun getLatestApplicationByStudent(studentId: Long): Flow<InternshipApplicationEntity?> = dao.getLatestApplicationByStudent(studentId)
    fun getApplicationById(id: Long): Flow<InternshipApplicationEntity?> = dao.getApplicationById(id)
    fun getAllApplications(): Flow<List<InternshipApplicationEntity>> = dao.getAllApplications()
    suspend fun insertApplication(app: InternshipApplicationEntity): Long {
        val id = dao.insertApplication(app)
        if (_isOfflineMode.value) {
            _pendingSyncCount.value += 1
        }
        return id
    }
    suspend fun updateApplication(app: InternshipApplicationEntity) = dao.updateApplication(app)
    suspend fun deleteApplication(app: InternshipApplicationEntity) = dao.deleteApplication(app)
    suspend fun deleteApplicationById(id: Long) = dao.deleteApplicationById(id)

    // --- AI Services ---

    suspend fun askAiAssistant(question: String, history: List<ChatMessage> = emptyList()): String = 
        aiService.askLeapAssistant(question, history)
        
    suspend fun analyzeSupervisorFeedback(supervisorComment: String, rating: Int): String =
        aiService.analyzeSupervisorFeedback(supervisorComment, rating)

    suspend fun getInternshipMatches(
        programme: String,
        skills: String,
        interests: String,
        department: String,
        location: String,
        experience: String
    ): List<InternshipMatchResult> = aiService.getInternshipMatches(programme, skills, interests, department, location, experience)
    suspend fun generateProgressInsights(studentName: String, progressPct: Int, currentWeek: Int, hasMissingDocs: Boolean): StudentProgressInsight =
        aiService.generateProgressInsights(studentName, progressPct, currentWeek, hasMissingDocs)
}
