package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LeapDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    // --- Students ---
    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: Long): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE userId = :userId LIMIT 1")
    fun getStudentByUserId(userId: Long): Flow<StudentEntity?>

    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE supervisorId = :supervisorId")
    fun getStudentsBySupervisor(supervisorId: Long): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE organizationId = :orgId")
    fun getStudentsByOrganization(orgId: Long): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    // --- Organizations ---
    @Query("SELECT * FROM organizations")
    fun getAllOrganizations(): Flow<List<OrganizationEntity>>

    @Query("SELECT * FROM organizations WHERE id = :id")
    fun getOrganizationById(id: Long): Flow<OrganizationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganization(org: OrganizationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganizations(orgs: List<OrganizationEntity>)

    @Update
    suspend fun updateOrganization(org: OrganizationEntity)

    // --- Supervisors ---
    @Query("SELECT * FROM supervisors")
    fun getAllSupervisors(): Flow<List<SupervisorEntity>>

    @Query("SELECT * FROM supervisors WHERE id = :id")
    fun getSupervisorById(id: Long): Flow<SupervisorEntity?>

    @Query("SELECT * FROM supervisors WHERE userId = :userId LIMIT 1")
    fun getSupervisorByUserId(userId: Long): Flow<SupervisorEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupervisor(supervisor: SupervisorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupervisors(supervisors: List<SupervisorEntity>)

    // --- Internships ---
    @Query("SELECT * FROM internships WHERE studentId = :studentId LIMIT 1")
    fun getInternshipByStudentId(studentId: Long): Flow<InternshipEntity?>

    @Query("SELECT * FROM internships")
    fun getAllInternships(): Flow<List<InternshipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInternship(internship: InternshipEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInternships(internships: List<InternshipEntity>)

    @Update
    suspend fun updateInternship(internship: InternshipEntity)

    // --- Action Plan & Tasks ---
    @Query("SELECT * FROM action_plans WHERE studentId = :studentId LIMIT 1")
    fun getActionPlanByStudentId(studentId: Long): Flow<ActionPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActionPlan(plan: ActionPlanEntity): Long

    @Update
    suspend fun updateActionPlan(plan: ActionPlanEntity)

    @Query("SELECT * FROM action_plan_tasks WHERE studentId = :studentId ORDER BY id ASC")
    fun getActionPlanTasksByStudent(studentId: Long): Flow<List<ActionPlanTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActionPlanTask(task: ActionPlanTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActionPlanTasks(tasks: List<ActionPlanTaskEntity>)

    @Update
    suspend fun updateActionPlanTask(task: ActionPlanTaskEntity)

    @Delete
    suspend fun deleteActionPlanTask(task: ActionPlanTaskEntity)

    // --- Daily Logs ---
    @Query("SELECT * FROM daily_logs WHERE studentId = :studentId ORDER BY date DESC, id DESC")
    fun getDailyLogsByStudent(studentId: Long): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE studentId = :studentId AND weekNumber = :weekNumber ORDER BY date ASC")
    fun getDailyLogsByStudentAndWeek(studentId: Long, weekNumber: Int): Flow<List<DailyLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLog(log: DailyLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLogs(logs: List<DailyLogEntity>)

    @Update
    suspend fun updateDailyLog(log: DailyLogEntity)

    @Delete
    suspend fun deleteDailyLog(log: DailyLogEntity)

    // --- Weekly Reports ---
    @Query("SELECT * FROM weekly_reports WHERE studentId = :studentId ORDER BY weekNumber DESC")
    fun getWeeklyReportsByStudent(studentId: Long): Flow<List<WeeklyReportEntity>>

    @Query("SELECT * FROM weekly_reports WHERE studentId = :studentId AND weekNumber = :weekNumber LIMIT 1")
    fun getWeeklyReportByStudentAndWeek(studentId: Long, weekNumber: Int): Flow<WeeklyReportEntity?>

    @Query("SELECT * FROM weekly_reports WHERE submissionStatus = 'Submitted' OR submissionStatus = 'Reviewed'")
    fun getSubmittedWeeklyReports(): Flow<List<WeeklyReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyReport(report: WeeklyReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyReports(reports: List<WeeklyReportEntity>)

    @Update
    suspend fun updateWeeklyReport(report: WeeklyReportEntity)

    // --- Supervisor Feedback ---
    @Query("SELECT * FROM supervisor_feedbacks WHERE studentId = :studentId ORDER BY id DESC")
    fun getFeedbacksByStudent(studentId: Long): Flow<List<SupervisorFeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupervisorFeedback(feedback: SupervisorFeedbackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupervisorFeedbacks(feedbacks: List<SupervisorFeedbackEntity>)

    // --- Assessments (Form D) ---
    @Query("SELECT * FROM assessments WHERE studentId = :studentId LIMIT 1")
    fun getAssessmentByStudentId(studentId: Long): Flow<AssessmentEntity?>

    @Query("SELECT * FROM assessments")
    fun getAllAssessments(): Flow<List<AssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentEntity): Long

    @Update
    suspend fun updateAssessment(assessment: AssessmentEntity)

    @Query("SELECT * FROM assessment_items WHERE assessmentId = :assessmentId")
    fun getAssessmentItems(assessmentId: Long): Flow<List<AssessmentItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessmentItems(items: List<AssessmentItemEntity>)

    @Update
    suspend fun updateAssessmentItem(item: AssessmentItemEntity)

    // --- Self Evaluations (Form D2) ---
    @Query("SELECT * FROM self_evaluations WHERE studentId = :studentId LIMIT 1")
    fun getSelfEvaluationByStudentId(studentId: Long): Flow<SelfEvaluationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelfEvaluation(selfEval: SelfEvaluationEntity): Long

    @Update
    suspend fun updateSelfEvaluation(selfEval: SelfEvaluationEntity)

    // --- Submission Checklists ---
    @Query("SELECT * FROM submission_checklists WHERE studentId = :studentId LIMIT 1")
    fun getChecklistByStudentId(studentId: Long): Flow<SubmissionChecklistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(checklist: SubmissionChecklistEntity): Long

    @Update
    suspend fun updateChecklist(checklist: SubmissionChecklistEntity)

    // --- Internship Reports ---
    @Query("SELECT * FROM internship_reports WHERE studentId = :studentId LIMIT 1")
    fun getInternshipReportByStudentId(studentId: Long): Flow<InternshipReportEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInternshipReport(report: InternshipReportEntity): Long

    @Update
    suspend fun updateInternshipReport(report: InternshipReportEntity)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE userId = :userId OR role = :role OR role = 'ALL' ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: Long, role: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsAsRead(userId: Long)

    // --- Scanned Documents ---
    @Query("SELECT * FROM scanned_documents WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getScannedDocumentsByStudent(studentId: Long): Flow<List<ScannedDocumentEntity>>

    @Query("SELECT * FROM scanned_documents WHERE studentId = :studentId AND documentType = :type ORDER BY timestamp DESC")
    fun getScannedDocumentsByType(studentId: Long, type: String): Flow<List<ScannedDocumentEntity>>

    @Query("SELECT * FROM scanned_documents WHERE id = :id LIMIT 1")
    fun getScannedDocumentById(id: Long): Flow<ScannedDocumentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScannedDocument(document: ScannedDocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScannedDocuments(documents: List<ScannedDocumentEntity>)

    @Update
    suspend fun updateScannedDocument(document: ScannedDocumentEntity)

    @Delete
    suspend fun deleteScannedDocument(document: ScannedDocumentEntity)

    @Query("DELETE FROM scanned_documents WHERE id = :id")
    suspend fun deleteScannedDocumentById(id: Long)

    // --- Internship Applications ---
    @Query("SELECT * FROM internship_applications WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getApplicationsByStudent(studentId: Long): Flow<List<InternshipApplicationEntity>>

    @Query("SELECT * FROM internship_applications WHERE studentId = :studentId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestApplicationByStudent(studentId: Long): Flow<InternshipApplicationEntity?>

    @Query("SELECT * FROM internship_applications WHERE id = :id LIMIT 1")
    fun getApplicationById(id: Long): Flow<InternshipApplicationEntity?>

    @Query("SELECT * FROM internship_applications ORDER BY timestamp DESC")
    fun getAllApplications(): Flow<List<InternshipApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: InternshipApplicationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplications(apps: List<InternshipApplicationEntity>)

    @Update
    suspend fun updateApplication(app: InternshipApplicationEntity)

    @Delete
    suspend fun deleteApplication(app: InternshipApplicationEntity)

    @Query("DELETE FROM internship_applications WHERE id = :id")
    suspend fun deleteApplicationById(id: Long)
}

