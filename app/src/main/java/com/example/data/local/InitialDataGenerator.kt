package com.example.data.local

import com.example.data.local.dao.LeapDao
import com.example.data.local.entities.*

object InitialDataGenerator {

    val CRITERIA_NAMES = listOf(
        "Attendance",
        "Punctuality",
        "Dress Code",
        "Attitude",
        "Acceptance of Criticism",
        "Self-Motivation",
        "Ethical Behaviour",
        "Knowledge",
        "Verbal Communication",
        "Written Communication",
        "Technical Skills",
        "Meeting Deadlines",
        "Initiative",
        "Prioritization",
        "Quality of Work",
        "Interest in Assignment",
        "Company Rules and Regulations",
        "Teamwork"
    )

    suspend fun populateInitialData(dao: LeapDao) {
        // 1. Users
        val users = listOf(
            UserEntity(id = 1, email = "student@leap.demo", passwordHash = "123456", name = "Mohamed Kamara", role = "STUDENT", phone = "+232 78 450123", designation = "Intern", department = "Software Engineering", studentCode = "LKW-SL-DEMO001"),
            UserEntity(id = 2, email = "supervisor@leap.demo", passwordHash = "123456", name = "Ing. David Koroma", role = "SUPERVISOR", phone = "+232 76 991204", designation = "Head of Software Engineering", department = "Engineering & Solutions", studentCode = ""),
            UserEntity(id = 3, email = "coordinator@leap.demo", passwordHash = "123456", name = "Dr. Fatmata Sesay", role = "COORDINATOR", phone = "+232 30 551020", designation = "Director of LEAP & Industrial Relations", department = "LEAP Directorate", studentCode = ""),
            UserEntity(id = 4, email = "admin@leap.demo", passwordHash = "123456", name = "System Administrator", role = "ADMIN", phone = "+232 77 112233", designation = "Academic Systems Lead", department = "ICT Directorate", studentCode = ""),
            // Extra students
            UserEntity(id = 5, email = "aminata.b@leap.demo", passwordHash = "123456", name = "Aminata Bangura", role = "STUDENT", phone = "+232 79 334455", designation = "Intern", department = "Enterprise IT", studentCode = "LKW-SL-DEMO002"),
            UserEntity(id = 6, email = "joseph.c@leap.demo", passwordHash = "123456", name = "Joseph Conteh", role = "STUDENT", phone = "+232 76 556677", designation = "Intern", department = "Digital Banking", studentCode = "LKW-SL-DEMO003"),
            UserEntity(id = 7, email = "mariama.f@leap.demo", passwordHash = "123456", name = "Mariama Fofanah", role = "STUDENT", phone = "+232 88 778899", designation = "Intern", department = "Network Operations", studentCode = "LKW-SL-DEMO004"),
            UserEntity(id = 8, email = "samuel.m@leap.demo", passwordHash = "123456", name = "Samuel Mansaray", role = "STUDENT", phone = "+232 75 990011", designation = "Intern", department = "Database Systems", studentCode = "LKW-SL-DEMO005")
        )
        dao.insertUsers(users)

        // 2. Organizations
        val organizations = listOf(
            OrganizationEntity(id = 1, name = "Tech Solutions SL Ltd.", industry = "Information Technology & Software", address = "14 Wilkinson Road", city = "Freetown, Sierra Leone", contactPerson = "Ing. David Koroma", contactEmail = "d.koroma@techsolutions.sl", contactPhone = "+232 76 991204", isApproved = true, departmentsAvailable = "Software Engineering, Cloud Infrastructure, Mobile Dev"),
            OrganizationEntity(id = 2, name = "Orange Sierra Leone", industry = "Telecommunications & Digital Services", address = "25 Regent Road, Hill Station", city = "Freetown, Sierra Leone", contactPerson = "Hassan Bangura", contactEmail = "internships@orange.sl", contactPhone = "+232 79 000111", isApproved = true, departmentsAvailable = "Network Engineering, Fintech / Orange Money, Customer Experience"),
            OrganizationEntity(id = 3, name = "Sierra Leone Commercial Bank (SLCB)", industry = "Banking & Financial Technology", address = "29/31 Siaka Stevens Street", city = "Freetown, Sierra Leone", contactPerson = "Zainab Conteh", contactEmail = "hr@slcb.sl", contactPhone = "+232 22 222501", isApproved = true, departmentsAvailable = "Digital Channels, Core Banking, IT Security, Data Analytics"),
            OrganizationEntity(id = 4, name = "Africell Sierra Leone", industry = "Telecommunications & Mobile Money", address = "Pivot Street, Wilberforce", city = "Freetown, Sierra Leone", contactPerson = "Mariama Jalloh", contactEmail = "careers@africell.sl", contactPhone = "+232 88 123456", isApproved = true, departmentsAvailable = "VAS, NOC Operations, Billing Systems"),
            OrganizationEntity(id = 5, name = "National Revenue Authority (NRA)", industry = "Public Sector / Taxation Systems", address = "Modu Chim Building, Gloucester St", city = "Freetown, Sierra Leone", contactPerson = "Sahr Kelly", contactEmail = "training@nra.gov.sl", contactPhone = "+232 77 987654", isApproved = true, departmentsAvailable = "ASYCUDA World, Database Admin, ICT Infrastructure")
        )
        dao.insertOrganizations(organizations)

        // 3. Supervisors
        val supervisors = listOf(
            SupervisorEntity(id = 1, userId = 2, name = "Ing. David Koroma", designation = "Head of Software Engineering", organizationId = 1, organizationName = "Tech Solutions SL Ltd.", department = "Engineering & Solutions", email = "supervisor@leap.demo", phone = "+232 76 991204"),
            SupervisorEntity(id = 2, userId = 0, name = "Ing. Hassan Bangura", designation = "Core Network Lead", organizationId = 2, organizationName = "Orange Sierra Leone", department = "Enterprise IT", email = "hassan.b@orange.sl", phone = "+232 79 445566"),
            SupervisorEntity(id = 3, userId = 0, name = "Mrs. Zainab Conteh", designation = "Fintech Product Manager", organizationId = 3, organizationName = "Sierra Leone Commercial Bank", department = "Digital Banking", email = "z.conteh@slcb.sl", phone = "+232 78 889900")
        )
        dao.insertSupervisors(supervisors)

        // 4. Students
        val students = listOf(
            StudentEntity(id = 1, userId = 1, studentIdCode = "LKW-SL-DEMO001", name = "Mohamed Kamara", programme = "BSc (Hons) Information Technology", email = "student@leap.demo", phone = "+232 78 450123", organizationId = 1, department = "Software Engineering", supervisorId = 1, commencementDate = "12-Jan-2026", completionDate = "05-Apr-2026", internshipStatus = "Active", progressPercentage = 68, currentWeek = 8, totalWeeks = 12),
            StudentEntity(id = 2, userId = 5, studentIdCode = "LKW-SL-DEMO002", name = "Aminata Bangura", programme = "BSc (Hons) Software Engineering", email = "aminata.b@leap.demo", phone = "+232 79 334455", organizationId = 2, department = "Enterprise IT", supervisorId = 2, commencementDate = "05-Jan-2026", completionDate = "29-Mar-2026", internshipStatus = "Active", progressPercentage = 85, currentWeek = 10, totalWeeks = 12),
            StudentEntity(id = 3, userId = 6, studentIdCode = "LKW-SL-DEMO003", name = "Joseph Conteh", programme = "BSc (Hons) Multimedia & Digital Design", email = "joseph.c@leap.demo", phone = "+232 76 556677", organizationId = 3, department = "Digital Banking", supervisorId = 3, commencementDate = "19-Jan-2026", completionDate = "12-Apr-2026", internshipStatus = "Active", progressPercentage = 50, currentWeek = 6, totalWeeks = 12),
            StudentEntity(id = 4, userId = 7, studentIdCode = "LKW-SL-DEMO004", name = "Mariama Fofanah", programme = "BSc (Hons) Telecommunications", email = "mariama.f@leap.demo", phone = "+232 88 778899", organizationId = 4, department = "Network Operations", supervisorId = 1, commencementDate = "02-Feb-2026", completionDate = "26-Apr-2026", internshipStatus = "Active", progressPercentage = 30, currentWeek = 4, totalWeeks = 12),
            StudentEntity(id = 5, userId = 8, studentIdCode = "LKW-SL-DEMO005", name = "Samuel Mansaray", programme = "BSc (Hons) Information Systems", email = "samuel.m@leap.demo", phone = "+232 75 990011", organizationId = 5, department = "Database Systems", supervisorId = 1, commencementDate = "05-Jan-2026", completionDate = "29-Mar-2026", internshipStatus = "Active", progressPercentage = 95, currentWeek = 12, totalWeeks = 12)
        )
        dao.insertStudents(students)

        // 5. Internships
        val internships = listOf(
            InternshipEntity(id = 1, studentId = 1, organizationId = 1, supervisorId = 1, positionRole = "Junior Mobile Software Engineer Intern", department = "Software Engineering", startDate = "12-Jan-2026", endDate = "05-Apr-2026", status = "Active"),
            InternshipEntity(id = 2, studentId = 2, organizationId = 2, supervisorId = 2, positionRole = "Network & Cloud Engineering Intern", department = "Enterprise IT", startDate = "05-Jan-2026", endDate = "29-Mar-2026", status = "Active"),
            InternshipEntity(id = 3, studentId = 3, organizationId = 3, supervisorId = 3, positionRole = "UI/UX & Fintech Digital Media Intern", department = "Digital Banking", startDate = "19-Jan-2026", endDate = "12-Apr-2026", status = "Active"),
            InternshipEntity(id = 4, studentId = 4, organizationId = 4, supervisorId = 1, positionRole = "NOC Operations Trainee", department = "Network Operations", startDate = "02-Feb-2026", endDate = "26-Apr-2026", status = "Active"),
            InternshipEntity(id = 5, studentId = 5, organizationId = 5, supervisorId = 1, positionRole = "Database & Systems Support Intern", department = "Database Systems", startDate = "05-Jan-2026", endDate = "29-Mar-2026", status = "Active")
        )
        dao.insertInternships(internships)

        // 6. Action Plan & Tasks
        dao.insertActionPlan(
            ActionPlanEntity(
                id = 1,
                studentId = 1,
                supervisorId = 1,
                academicYear = "2025/2026",
                semester = "Semester 2",
                studentSignature = "Mohamed Kamara (Digital Confirmation)",
                isStudentSigned = true,
                studentSignatureDate = "15-Jan-2026",
                supervisorSignature = "Ing. David Koroma (Digital Approval)",
                isSupervisorSigned = true,
                supervisorSignatureDate = "18-Jan-2026",
                isCompanyStamped = true,
                companyStampText = "OFFICIALLY VERIFIED - TECH SOLUTIONS SL",
                approvalStatus = "Approved"
            )
        )

        val actionPlanTasks = listOf(
            ActionPlanTaskEntity(id = 1, actionPlanId = 1, studentId = 1, taskDescription = "Company orientation, IDE tooling setup, and review of client codebase architecture", byWhen = "Week 1", isAchieved = true, supervisorNotes = "Completed smoothly on schedule.", isApprovedBySupervisor = true),
            ActionPlanTaskEntity(id = 2, actionPlanId = 1, studentId = 1, taskDescription = "Design and implement offline-first caching module using SQLite Room database", byWhen = "Week 4", isAchieved = true, supervisorNotes = "Well executed schema with foreign keys.", isApprovedBySupervisor = true),
            ActionPlanTaskEntity(id = 3, actionPlanId = 1, studentId = 1, taskDescription = "Build responsive Jetpack Compose screens for internship reporting module", byWhen = "Week 7", isAchieved = true, supervisorNotes = "Clean UI adhering to M3 guidelines.", isApprovedBySupervisor = true),
            ActionPlanTaskEntity(id = 4, actionPlanId = 1, studentId = 1, taskDescription = "Conduct unit and automated integration tests for repository sync engine", byWhen = "Week 9", isAchieved = false, supervisorNotes = "In progress. Target next week.", isApprovedBySupervisor = true),
            ActionPlanTaskEntity(id = 5, actionPlanId = 1, studentId = 1, taskDescription = "Finalize comprehensive LEAP dissertation documentation and presentation slides", byWhen = "Week 12", isAchieved = false, supervisorNotes = "Pending completion of Week 11.", isApprovedBySupervisor = true)
        )
        dao.insertActionPlanTasks(actionPlanTasks)

        // 7. Daily Logs (for Week 7 & 8)
        val dailyLogs = listOf(
            DailyLogEntity(id = 1, studentId = 1, weekNumber = 8, date = "2026-03-02", dayOfWeek = "Monday", taskActivity = "Implemented local Room DB DAO queries and repository interfaces for LEAP offline logging.", toolsMethods = "Android Studio, Kotlin, Room KSP, Flow", peopleWorkedWith = "Ing. David Koroma (Supervisor)", skillsLearned = "Reactive SQLite transactions, StateFlow mapping", challenges = "Resolving conflicting schema versions during local cache testing", reflection = "Understood the critical importance of local persistence for low-bandwidth environments in Sierra Leone."),
            DailyLogEntity(id = 2, studentId = 1, weekNumber = 8, date = "2026-03-03", dayOfWeek = "Tuesday", taskActivity = "Designed Material 3 UI screens for student daily log entry and weekly report compilation.", toolsMethods = "Jetpack Compose, Material 3, Vector Assets", peopleWorkedWith = "UI/UX Designer Zainab", skillsLearned = "Compose Scaffold, LazyColumn layout optimization, accessibility touch targets", challenges = "Dynamic window insets handling for edge-to-edge rendering", reflection = "M3 design tokens greatly streamline maintaining visual consistency."),
            DailyLogEntity(id = 3, studentId = 1, weekNumber = 8, date = "2026-03-04", dayOfWeek = "Wednesday", taskActivity = "Created mock AI knowledge base and rule engine for LEAP regulations query assistant.", toolsMethods = "Kotlin DSL, Coroutines, Pattern Matcher", peopleWorkedWith = "Ing. David Koroma", skillsLearned = "Contextual prompt formulation, advisory disclaimer integration", challenges = "Ensuring AI responses never invent unofficial university regulations", reflection = "Strictly bounded knowledge retrieval is essential for academic integrity."),
            DailyLogEntity(id = 4, studentId = 1, weekNumber = 8, date = "2026-03-05", dayOfWeek = "Thursday", taskActivity = "Connected Form D assessment calculation engine and Form D2 self-evaluation survey forms.", toolsMethods = "StateFlow, MVVM ViewModel, Data Validation", peopleWorkedWith = "Intern Peer Aminata", skillsLearned = "Multi-step form state preservation across configuration changes", challenges = "Validating 18 distinct assessment metrics before enabling submission", reflection = "Automating validation reduces manual paperwork errors observed in paper Form D."),
            DailyLogEntity(id = 5, studentId = 1, weekNumber = 8, date = "2026-03-06", dayOfWeek = "Friday", taskActivity = "Weekly team sprint review, demonstration of LEAP prototype to department lead, weekly report submission.", toolsMethods = "Git, Code Review, Jira, Compose Preview", peopleWorkedWith = "Full Engineering Team (8 members)", skillsLearned = "Professional technical presentation, addressing supervisor feedback constructively", challenges = "None", reflection = "Constructive criticism from the team will help refine the final LEAP dissertation deliverable.")
        )
        dao.insertDailyLogs(dailyLogs)

        // 8. Weekly Reports
        val weeklyReports = listOf(
            WeeklyReportEntity(id = 1, studentId = 1, weekNumber = 6, dateRange = "Feb 16 - Feb 20, 2026", activitiesCompleted = "Completed student authentication flows and role-based navigation architecture.", skillsLearned = "Navigation Compose, sealed route objects, session state caching.", challenges = "Securing role boundaries between Students, Supervisors, and Coordinators.", reflection = "Role-based separation is crucial to prevent unauthorized grade alterations.", supervisorFeedback = "Excellent architecture. Clean separation of concerns.", submissionStatus = "Approved", submittedDate = "20-Feb-2026", reviewedDate = "22-Feb-2026", feedbackRating = 5),
            WeeklyReportEntity(id = 2, studentId = 1, weekNumber = 7, dateRange = "Feb 23 - Feb 27, 2026", activitiesCompleted = "Created digital Action Plan and Daily Log Book modules with weekly grouping.", skillsLearned = "Room Database foreign key modeling, KSP entity compilation.", challenges = "Ensuring smooth scrolling performance with dense historical log entries.", reflection = "Pagination and lazy loading improve user responsiveness.", supervisorFeedback = "Daily reflection entries are detailed and thoughtful. Keep it up.", submissionStatus = "Approved", submittedDate = "27-Feb-2026", reviewedDate = "01-Mar-2026", feedbackRating = 5),
            WeeklyReportEntity(id = 3, studentId = 1, weekNumber = 8, dateRange = "Mar 02 - Mar 06, 2026", activitiesCompleted = "Implemented Form D supervisor assessment, Form D2 student self-evaluation, and submission checklist.", skillsLearned = "Material 3 Form controls, 18-point rubric aggregation, digital verification.", challenges = "Syncing checklist completion percentage dynamically.", reflection = "Having real-time progress indicators significantly reduces student anxiety about missing graduation requirements.", supervisorFeedback = "Work is progressing exceptionally well. Approved for Week 8.", submissionStatus = "Submitted", submittedDate = "06-Mar-2026", reviewedDate = "", feedbackRating = 0)
        )
        dao.insertWeeklyReports(weeklyReports)

        // 9. Supervisor Feedbacks
        val feedbacks = listOf(
            SupervisorFeedbackEntity(id = 1, studentId = 1, supervisorId = 1, weekNumber = 6, targetType = "WEEKLY_REPORT", content = "Great job finalizing the role-based navigation model. Coding standard is exemplary.", status = "Approved", createdAt = "2026-02-22 10:15"),
            SupervisorFeedbackEntity(id = 2, studentId = 1, supervisorId = 1, weekNumber = 7, targetType = "ACTION_PLAN", content = "Action plan tasks 1, 2, and 3 verified. Please prioritize the unit tests before Week 10.", status = "Approved", createdAt = "2026-03-01 16:40"),
            SupervisorFeedbackEntity(id = 3, studentId = 1, supervisorId = 1, weekNumber = 8, targetType = "DAILY_LOG", content = "Reviewed Thursday log entry. The offline sync fallback architecture is well conceived.", status = "Approved", createdAt = "2026-03-06 17:00")
        )
        dao.insertSupervisorFeedbacks(feedbacks)

        // 10. Assessment (Form D)
        val assessmentId = dao.insertAssessment(
            AssessmentEntity(
                id = 1,
                studentId = 1,
                supervisorId = 1,
                overallPerformanceRating = 4.8f,
                overallAssessment = "Mohamed has demonstrated outstanding technical skill, reliability, and leadership throughout his placement at Tech Solutions SL.",
                otherComments = "Highly recommended for permanent employment as a Junior Mobile Software Engineer upon graduation.",
                majorStrengths = "Analytical problem-solving, rapid mastery of Jetpack Compose, punctual attendance, excellent collaboration.",
                academicWorkRecommendations = "Continue focusing on distributed systems and advanced mobile security practices.",
                technicalSkillsGained = "Android Jetpack Compose, Room Database, Kotlin Coroutines, Git Flow, API Integration.",
                wereInternshipObjectivesMet = true,
                supervisorName = "Ing. David Koroma",
                supervisorDesignation = "Head of Software Engineering",
                companyName = "Tech Solutions SL Ltd.",
                isSubmitted = true,
                submittedDate = "2026-03-08"
            )
        )

        // Form D items (18 criteria)
        val assessmentItems = CRITERIA_NAMES.mapIndexed { index, name ->
            AssessmentItemEntity(
                id = (index + 1).toLong(),
                assessmentId = assessmentId,
                criterionName = name,
                rating = if (index in listOf(0, 1, 6, 10, 14, 17)) 5 else 4,
                comments = "Consistently exceeds standard expectations."
            )
        }
        dao.insertAssessmentItems(assessmentItems)

        // 11. Self-Evaluation (Form D2)
        dao.insertSelfEvaluation(
            SelfEvaluationEntity(
                id = 1,
                studentId = 1,
                isSubmitted = true,
                submittedDate = "2026-03-08"
            )
        )

        // 12. Submission Checklist
        dao.insertChecklist(
            SubmissionChecklistEntity(
                id = 1,
                studentId = 1,
                lastUpdated = "10-Mar-2026"
            )
        )

        // 13. Internship Report
        dao.insertInternshipReport(
            InternshipReportEntity(
                id = 1,
                studentId = 1,
                title = "Final LEAP Internship Technical Report - Tech Solutions SL",
                fileName = "Mohamed_Kamara_LEAP_Report_Final.pdf",
                fileSize = "2.4 MB",
                uploadDate = "2026-03-09",
                status = "Submitted",
                supervisorFeedback = "Report well structured. Chapters 3 and 4 have thorough system diagrams.",
                coordinatorFeedback = "Formatting adheres strictly to Limkokwing academic guidelines.",
                version = 1
            )
        )

        // 14. Notifications
        val notifications = listOf(
            NotificationEntity(id = 1, userId = 1, role = "STUDENT", title = "Supervisor Feedback Received", message = "Ing. David Koroma approved your Week 8 daily logs and added feedback.", type = "FEEDBACK", timeAgo = "1h ago"),
            NotificationEntity(id = 2, userId = 1, role = "STUDENT", title = "Action Plan Approved", message = "Your digital LEAP Action Plan has been signed and approved by Tech Solutions SL.", type = "APPROVAL", timeAgo = "1d ago"),
            NotificationEntity(id = 3, userId = 1, role = "STUDENT", title = "Internship Report Reminder", message = "LEAP Final Report submission window is open. Ensure all 8 checklist items are complete.", type = "DEADLINE", timeAgo = "3d ago"),
            NotificationEntity(id = 4, userId = 2, role = "SUPERVISOR", title = "Weekly Report Submitted", message = "Mohamed Kamara submitted Week 8 report for your review.", type = "APPROVAL", timeAgo = "2h ago"),
            NotificationEntity(id = 5, userId = 3, role = "COORDINATOR", title = "Coordinator Briefing", message = "18 students currently on active LEAP placements across Freetown.", type = "ANNOUNCEMENT", timeAgo = "4h ago")
        )
        dao.insertNotifications(notifications)

        // 15. Sample Scanned Documents
        val sampleScans = listOf(
            ScannedDocumentEntity(
                id = 1,
                studentId = 1,
                documentType = "FORM_A2",
                title = "Signed Form A2 - Student Acceptance Letter",
                formCode = "Form A2",
                imageUri = "content://leap/scans/form_a2_kamara.jpg",
                capturedDate = "10-Jan-2026 11:15 AM",
                fileSize = "1.6 MB",
                pageCount = 1,
                filterApplied = "ENHANCED_BW",
                isSupervisorSigned = true,
                isCompanyStamped = true,
                verificationStatus = "VERIFIED",
                supervisorName = "Ing. David Koroma",
                companyName = "Tech Solutions SL Ltd.",
                notes = "Formal acceptance letter stamped by HR Director.",
                associatedChecklistItem = "formA2"
            ),
            ScannedDocumentEntity(
                id = 2,
                studentId = 1,
                documentType = "FORM_A3",
                title = "Signed Form A3 - Industrial Supervisor Appointment",
                formCode = "Form A3",
                imageUri = "content://leap/scans/form_a3_appointment.jpg",
                capturedDate = "12-Jan-2026 02:40 PM",
                fileSize = "1.4 MB",
                pageCount = 1,
                filterApplied = "ENHANCED_BW",
                isSupervisorSigned = true,
                isCompanyStamped = true,
                verificationStatus = "VERIFIED",
                supervisorName = "Ing. David Koroma",
                companyName = "Tech Solutions SL Ltd.",
                notes = "Supervisor appointment confirmed with official email & signature.",
                associatedChecklistItem = "formA3"
            ),
            ScannedDocumentEntity(
                id = 3,
                studentId = 1,
                documentType = "ACTION_PLAN",
                title = "Company Stamped Action Plan",
                formCode = "Action Plan",
                imageUri = "content://leap/scans/action_plan_stamped.jpg",
                capturedDate = "18-Jan-2026 09:20 AM",
                fileSize = "2.1 MB",
                pageCount = 2,
                filterApplied = "DOCUMENT_SHARP",
                isSupervisorSigned = true,
                isCompanyStamped = true,
                verificationStatus = "VERIFIED",
                supervisorName = "Ing. David Koroma",
                companyName = "Tech Solutions SL Ltd.",
                notes = "Detailed week 1-12 milestones signed with Tech Solutions company seal.",
                associatedChecklistItem = "actionPlan"
            ),
            ScannedDocumentEntity(
                id = 4,
                studentId = 1,
                documentType = "FORM_B",
                title = "Mid-Term Form B Progress Review",
                formCode = "Form B",
                imageUri = "content://leap/scans/form_b_midterm.jpg",
                capturedDate = "20-Feb-2026 04:00 PM",
                fileSize = "1.8 MB",
                pageCount = 1,
                filterApplied = "ENHANCED_BW",
                isSupervisorSigned = true,
                isCompanyStamped = true,
                verificationStatus = "VERIFIED",
                supervisorName = "Ing. David Koroma",
                companyName = "Tech Solutions SL Ltd.",
                notes = "Midterm on-site evaluation by LEAP academic coordinator.",
                associatedChecklistItem = "formB"
            )
        )
        dao.insertScannedDocuments(sampleScans)

        // 13. Internship Applications (Initial Sample)
        val sampleApplications = listOf(
            InternshipApplicationEntity(
                id = 1,
                studentId = 1,
                applicationRefNumber = "APP-LEAP-2026-0814",
                fullName = "Mohamed Kamara",
                studentIdNumber = "LKW-SL-DEMO001",
                email = "student@leap.demo",
                phone = "+232 78 450123",
                faculty = "Faculty of Information & Communication Technology",
                programme = "BSc (Hons) in Information Technology",
                currentYearSemester = "Year 3, Semester 2",
                cgpa = "3.82",
                address = "22 Sanders Street, Freetown",
                emergencyContactName = "Fatmata Kamara",
                emergencyContactPhone = "+232 76 112233",
                emergencyContactRelation = "Parent / Guardian",
                primarySector = "Software & Cloud Engineering",
                secondarySector = "Telecommunications & Digital Services",
                preferredRoleDepartment = "Mobile & Web Software Development",
                preferredLocation = "Freetown Central",
                preferredWorkMode = "On-site",
                internshipDurationWeeks = 12,
                preferredStartDate = "12-Jan-2026",
                hasOwnLaptop = true,
                willingnessToRelocate = false,
                professionalSummary = "Dedicated penultimate-year IT student with hands-on experience in Kotlin Android development, Room database architecture, and REST API integration. Passionate about building robust offline-first software solutions for African enterprise ecosystems.",
                technicalSkills = "Kotlin, Jetpack Compose, Java, Python, SQL / Room DB, Git, REST APIs, Linux",
                softSkills = "Problem Solving, Team Leadership, Agile Collaboration, Technical Writing, Fast Learner",
                educationHistory = "Limkokwing University of Creative Technology (BSc Hons IT, 2023 - Present) | Sierra Leone Grammar School (WASSCE, 2017 - 2023)",
                projectExperience = "1. LEAP Offline-First Android Sync Manager (Kotlin, Room, Jetpack Compose)\n2. Campus Student Portal Web Backend (Node.js, PostgreSQL)\n3. Local Retail Point-of-Sale App (Android)",
                certifications = "Google IT Support Professional Certificate, Cisco Networking Basics (CCNA 1)",
                cvFileName = "Mohamed_Kamara_LEAP_CV_2026.pdf",
                cvFileSize = "1.8 MB",
                cvLastModified = "Today, 10:15 AM",
                portfolioOrGithubUrl = "https://github.com/mohamedkamara-dev",
                linkedInUrl = "https://linkedin.com/in/mohamed-kamara-sl",
                academicRefereeName = "Dr. Fatmata Sesay",
                academicRefereeTitle = "Head of Computing, Limkokwing University SL",
                academicRefereeContact = "f.sesay@limkokwing.edu.sl",
                isDeclarationAccepted = true,
                submissionStatus = "Approved",
                submittedDate = "05-Jan-2026",
                reviewerNotes = "Exceptional academic background and strong technical portfolio. Directly matched with Tech Solutions SL Ltd."
            )
        )
        dao.insertApplications(sampleApplications)
    }
}

