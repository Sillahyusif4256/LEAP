package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val passwordHash: String = "123456",
    val name: String,
    val role: String, // "STUDENT", "SUPERVISOR", "COORDINATOR", "ADMIN"
    val phone: String = "+232 76 000000",
    val designation: String = "",
    val department: String = "",
    val studentCode: String = ""
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val studentIdCode: String, // e.g. "LKW-SL-DEMO001"
    val name: String,
    val programme: String, // e.g. "BSc (Hons) Information Technology"
    val email: String,
    val phone: String,
    val organizationId: Long,
    val department: String,
    val supervisorId: Long,
    val commencementDate: String,
    val completionDate: String,
    val internshipStatus: String, // "Pending", "Approved", "Active", "Completed"
    val progressPercentage: Int, // e.g. 68
    val currentWeek: Int, // e.g. 8
    val totalWeeks: Int = 12
)

@Entity(tableName = "organizations")
data class OrganizationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val industry: String,
    val address: String,
    val city: String = "Freetown, Sierra Leone",
    val contactPerson: String,
    val contactEmail: String,
    val contactPhone: String,
    val isApproved: Boolean = true,
    val departmentsAvailable: String = "IT, Software Engineering, Multimedia, Telecoms"
)

@Entity(tableName = "supervisors")
data class SupervisorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val name: String,
    val designation: String, // e.g. "Senior Software Architect & Systems Lead"
    val organizationId: Long,
    val organizationName: String,
    val department: String,
    val email: String,
    val phone: String
)

@Entity(tableName = "internships")
data class InternshipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val organizationId: Long,
    val supervisorId: Long,
    val positionRole: String,
    val department: String,
    val startDate: String,
    val endDate: String,
    val status: String // "Pending", "Approved", "Active", "Completed"
)

@Entity(tableName = "action_plans")
data class ActionPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val supervisorId: Long,
    val academicYear: String = "2025/2026",
    val semester: String = "Semester 2",
    val studentSignature: String = "Mohamed Kamara (Digital Confirmation)",
    val isStudentSigned: Boolean = true,
    val studentSignatureDate: String = "15-Jan-2026",
    val supervisorSignature: String = "Ing. D. Koroma (Digital Approval)",
    val isSupervisorSigned: Boolean = true,
    val supervisorSignatureDate: String = "18-Jan-2026",
    val isCompanyStamped: Boolean = true,
    val companyStampText: String = "OFFICIALLY VERIFIED - TECH SOLUTIONS SL",
    val approvalStatus: String = "Approved" // "Draft", "Submitted", "Approved", "Requires Revision"
)

@Entity(
    tableName = "action_plan_tasks",
    indices = [Index("actionPlanId"), Index("studentId")]
)
data class ActionPlanTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actionPlanId: Long,
    val studentId: Long,
    val taskDescription: String, // "What will I do"
    val byWhen: String, // e.g. "Week 4", "15-Feb-2026"
    val isAchieved: Boolean = false,
    val supervisorNotes: String = "",
    val isApprovedBySupervisor: Boolean = true
)

@Entity(
    tableName = "daily_logs",
    indices = [Index("studentId"), Index("weekNumber")]
)
data class DailyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val weekNumber: Int,
    val date: String, // "2026-03-02"
    val dayOfWeek: String = "Monday",
    val taskActivity: String, // "Task/activity performed"
    val toolsMethods: String, // "Tools/methods used"
    val peopleWorkedWith: String, // "People worked with"
    val skillsLearned: String, // "Skills learned"
    val challenges: String, // "Challenges encountered"
    val reflection: String, // "Student reflection"
    val isSynced: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "weekly_reports",
    indices = [Index("studentId"), Index("weekNumber")]
)
data class WeeklyReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val weekNumber: Int,
    val dateRange: String, // "Feb 23 - Feb 27, 2026"
    val activitiesCompleted: String,
    val skillsLearned: String,
    val challenges: String,
    val reflection: String,
    val supervisorFeedback: String = "",
    val submissionStatus: String = "Draft", // "Draft", "Submitted", "Reviewed", "Requires Changes", "Approved"
    val submittedDate: String = "",
    val reviewedDate: String = "",
    val feedbackRating: Int = 0 // 1..5
)

@Entity(
    tableName = "supervisor_feedbacks",
    indices = [Index("studentId"), Index("supervisorId")]
)
data class SupervisorFeedbackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val supervisorId: Long,
    val weekNumber: Int,
    val targetType: String, // "WEEKLY_REPORT", "DAILY_LOG", "ACTION_PLAN", "GENERAL"
    val content: String,
    val status: String = "Approved", // "Approved", "Changes Requested", "Remark"
    val createdAt: String = "2026-03-01 14:30"
)

@Entity(
    tableName = "assessments",
    indices = [Index("studentId")]
)
data class AssessmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val supervisorId: Long,
    val overallPerformanceRating: Float = 4.5f, // Out of 5.0
    val overallAssessment: String = "Outstanding diligence and technical aptitude demonstrated throughout the internship duration.",
    val otherComments: String = "Recommended for fast-track engineering hire upon graduation.",
    val majorStrengths: String = "Quick comprehension of complex architectures, punctual attendance, proactive team communication.",
    val academicWorkRecommendations: String = "Deepen distributed systems and microservices coursework in final year.",
    val technicalSkillsGained: String = "Kotlin Android, Room DB, REST APIs, Git Workflow, CI/CD deployment pipelines.",
    val wereInternshipObjectivesMet: Boolean = true,
    val supervisorName: String = "Ing. David Koroma",
    val supervisorDesignation: String = "Head of Engineering",
    val companyName: String = "Tech Solutions SL Ltd.",
    val isSubmitted: Boolean = true,
    val submittedDate: String = "2026-03-10"
)

@Entity(
    tableName = "assessment_items",
    indices = [Index("assessmentId")]
)
data class AssessmentItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assessmentId: Long,
    val criterionName: String,
    val rating: Int = 5, // 1 to 5
    val comments: String = ""
)

@Entity(tableName = "self_evaluations")
data class SelfEvaluationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    // Part 1: Learning Goals & Overall Experience (1 = Strongly Agree to 5 = Strongly Disagree)
    val q1AchievedGoals: Int = 1,
    val q2RelatedFieldTraining: Int = 1,
    val q3CompletedResponsibilities: Int = 1,
    val q4AdequateWorkload: Int = 2,
    val q5MetExpectation: Int = 1,
    val q6AcademicPreparation: Int = 2,
    val q7UsefulExperience: Int = 1,
    val q8ConsiderPermanentEmployment: Boolean = true,
    
    // Part 2: Work Environment & Support (1 = Outstanding to 5 = Unsatisfactory)
    // Work Environment
    val envOrgStructure: Int = 1,
    val envAccessMaterials: Int = 2,
    val envCollegiality: Int = 1,
    val envRespectInterns: Int = 1,
    // Support & Feedback
    val supFromSupervisor: Int = 1,
    val supFromEmployees: Int = 1,
    val supUnderstoodGoals: Int = 1,
    val supRegularFeedback: Int = 1,
    // Opportunity to be Creative
    val creatConsiderIdeas: Int = 1,
    val creatDemoSkills: Int = 1,
    // Interaction with Others
    val interactTeamProject: Int = 1,
    val interactQuestionsAnswered: Int = 1,
    val interactAccessMentors: Int = 1,
    // Support from Internship / LEAP Office
    val offInfoProvided: Int = 1,
    val offAdequateInteraction: Int = 2,
    val offVisitedIntern: Int = 1,

    // Part 3: Text fields
    val specificTasksAssigned: String = "Developed mobile UI modules, implemented SQLite Room database schemas, tested REST client integration, participated in daily stand-ups.",
    val challengesOrLimitations: String = "Occasional power outages and intermittent low-bandwidth network at the regional client sites.",
    val impactOfChallenges: String = "Required designing offline-first synchronization workflows to prevent workflow delays.",
    val keySkillsGained: String = "Jetpack Compose, Room local database architecture, version control branching, collaborative code review.",
    val influenceOnCareerPlans: String = "Solidified ambition to specialize as an enterprise mobile software engineer in West Africa.",
    val whatWouldChangeAndWhy: String = "Would arrange for cloud mock server setups earlier in the onboarding week.",
    val overallEvaluation: String = "Superior", // "Superior", "Excellent", "Satisfactory", "Unsatisfactory"
    val additionalComments: String = "Very appreciative of the mentorship provided by Tech Solutions SL and the Limkokwing LEAP coordination office.",
    val isSubmitted: Boolean = true,
    val submittedDate: String = "2026-03-08"
)

@Entity(tableName = "submission_checklists")
data class SubmissionChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    // Item 1: Form D Evaluation
    val formDStudentCheck: Boolean = true,
    val formDSupervisorCheck: Boolean = true,
    val formDStatus: String = "Approved",
    val formDDate: String = "08-Mar-2026",
    // Item 2: Self-Evaluation Form (Form D2)
    val selfEvalStudentCheck: Boolean = true,
    val selfEvalSupervisorCheck: Boolean = true,
    val selfEvalStatus: String = "Approved",
    val selfEvalDate: String = "08-Mar-2026",
    // Item 3: Log Book (Typed)
    val logBookStudentCheck: Boolean = true,
    val logBookSupervisorCheck: Boolean = true,
    val logBookStatus: String = "Approved",
    val logBookDate: String = "06-Mar-2026",
    // Item 4: Action Plan Form
    val actionPlanStudentCheck: Boolean = true,
    val actionPlanSupervisorCheck: Boolean = true,
    val actionPlanStatus: String = "Approved",
    val actionPlanDate: String = "18-Jan-2026",
    // Item 5: Form A2 (Internship Acceptance Letter)
    val formA2StudentCheck: Boolean = true,
    val formA2SupervisorCheck: Boolean = true,
    val formA2Status: String = "Approved",
    val formA2Date: String = "10-Jan-2026",
    // Item 6: Form A3 (Supervisor Appointment Confirmation)
    val formA3StudentCheck: Boolean = true,
    val formA3SupervisorCheck: Boolean = true,
    val formA3Status: String = "Approved",
    val formA3Date: String = "12-Jan-2026",
    // Item 7: Form B (Monthly Progress Review)
    val formBStudentCheck: Boolean = true,
    val formBSupervisorCheck: Boolean = true,
    val formBStatus: String = "Approved",
    val formBDate: String = "20-Feb-2026",
    // Item 8: Internship Report (Typed)
    val reportStudentCheck: Boolean = true,
    val reportSupervisorCheck: Boolean = false,
    val reportStatus: String = "Submitted",
    val reportDate: String = "09-Mar-2026",
    val lastUpdated: String = "10-Mar-2026"
)

@Entity(tableName = "internship_reports")
data class InternshipReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val title: String = "Final LEAP Internship Technical Report - Tech Solutions SL",
    val fileName: String = "Mohamed_Kamara_LEAP_Report_Final.pdf",
    val fileSize: String = "2.4 MB",
    val uploadDate: String = "2026-03-09",
    val status: String = "Submitted", // "Pending", "Submitted", "Under Review", "Changes Requested", "Approved"
    val supervisorFeedback: String = "Report well structured. Chapters 3 and 4 have thorough system diagrams.",
    val coordinatorFeedback: String = "Formatting adheres strictly to Limkokwing academic guidelines.",
    val version: Int = 1
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val role: String, // "STUDENT", "SUPERVISOR", "COORDINATOR", "ADMIN", "ALL"
    val title: String,
    val message: String,
    val type: String, // "FEEDBACK", "DEADLINE", "MISSING_DOC", "APPROVAL", "ASSESSMENT", "ANNOUNCEMENT"
    val timeAgo: String = "2h ago",
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "scanned_documents",
    indices = [Index("studentId"), Index("documentType")]
)
data class ScannedDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val documentType: String, // "FORM_A2", "FORM_A3", "ACTION_PLAN", "FORM_B", "FORM_D", "FORM_D2", "LOGBOOK_SHEET", "REPORT_CLEARANCE", "OTHER"
    val title: String,
    val formCode: String, // "Form A2", "Form D", etc.
    val imageUri: String, // File path or content URI string
    val thumbnailUri: String = "",
    val capturedDate: String,
    val fileSize: String = "1.4 MB",
    val pageCount: Int = 1,
    val filterApplied: String = "ENHANCED_BW", // "ORIGINAL", "ENHANCED_BW", "DOCUMENT_SHARP", "GRAYSCALE"
    val isSupervisorSigned: Boolean = true,
    val isCompanyStamped: Boolean = true,
    val verificationStatus: String = "VERIFIED", // "PENDING", "VERIFIED", "REQUIRES_RESAMPLE"
    val supervisorName: String = "Ing. David Koroma",
    val companyName: String = "Tech Solutions SL Ltd.",
    val notes: String = "",
    val associatedChecklistItem: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "internship_applications",
    indices = [Index("studentId")]
)
data class InternshipApplicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val applicationRefNumber: String = "APP-LEAP-2026-0814",
    
    // Step 1: Student Details & Academic Profile
    val fullName: String,
    val studentIdNumber: String,
    val email: String,
    val phone: String,
    val faculty: String = "Faculty of Information & Communication Technology",
    val programme: String = "BSc (Hons) in Information Technology",
    val currentYearSemester: String = "Year 3, Semester 2",
    val cgpa: String = "3.82",
    val address: String = "22 Sanders Street, Freetown",
    val emergencyContactName: String = "Fatmata Kamara",
    val emergencyContactPhone: String = "+232 76 112233",
    val emergencyContactRelation: String = "Parent / Guardian",
    
    // Step 2: Preferred Industry Sector & Placement Preferences
    val primarySector: String = "Software & Cloud Engineering",
    val secondarySector: String = "Telecommunications & Digital Services",
    val preferredRoleDepartment: String = "Mobile & Web Software Development",
    val preferredLocation: String = "Freetown Central",
    val preferredWorkMode: String = "On-site", // "On-site", "Hybrid", "Remote"
    val internshipDurationWeeks: Int = 12,
    val preferredStartDate: String = "15-Mar-2026",
    val hasOwnLaptop: Boolean = true,
    val willingnessToRelocate: Boolean = false,
    
    // Step 3: CV Information & Credentials
    val professionalSummary: String = "Dedicated penultimate-year IT student with hands-on experience in Kotlin Android development, REST API integration, and database management. Passionate about building robust software solutions for African enterprise ecosystems.",
    val technicalSkills: String = "Kotlin, Jetpack Compose, Java, Python, SQL / Room DB, Git, REST APIs, Linux",
    val softSkills: String = "Problem Solving, Team Leadership, Agile Collaboration, Technical Writing, Fast Learner",
    val educationHistory: String = "Limkokwing University of Creative Technology (BSc Hons IT, 2023 - Present) | Sierra Leone Grammar School (WASSCE, 2017 - 2023)",
    val projectExperience: String = "1. LEAP Offline-First Android Sync Manager (Kotlin, Room, Jetpack Compose)\n2. Campus Student Portal Web Backend (Node.js, PostgreSQL)",
    val certifications: String = "Google IT Support Professional Certificate, Cisco Networking Basics (CCNA 1)",
    val cvFileName: String = "Mohamed_Kamara_LEAP_CV_2026.pdf",
    val cvFileSize: String = "1.8 MB",
    val cvLastModified: String = "Today, 10:15 AM",
    val portfolioOrGithubUrl: String = "https://github.com/mohamedkamara-dev",
    val linkedInUrl: String = "https://linkedin.com/in/mohamed-kamara-sl",
    val academicRefereeName: String = "Dr. Fatmata Sesay",
    val academicRefereeTitle: String = "Head of Computing, Limkokwing University SL",
    val academicRefereeContact: String = "f.sesay@limkokwing.edu.sl",
    
    // Step 4: Submission & Verification
    val isDeclarationAccepted: Boolean = true,
    val submissionStatus: String = "Submitted", // "Draft", "Submitted", "Under Review", "Shortlisted", "Placement Matched", "Approved"
    val submittedDate: String = "2026-03-01",
    val reviewerNotes: String = "Strong profile matching Tech Solutions SL and Orange SL requirements.",
    val timestamp: Long = System.currentTimeMillis()
)

