package com.example.data.ai

enum class LeapKnowledgeCategory(val displayName: String, val shortCode: String) {
    ALL("All Categories", "ALL"),
    PROCEDURES("LEAP Procedures & Placement", "PROC"),
    DOCUMENTS("Required Documents & Forms", "DOCS"),
    ACTION_PLAN("Action Plan Guidelines", "PLAN"),
    LOGS_REPORTS("Daily Logs & Weekly Reports", "LOGS"),
    SUPERVISOR_EVAL("Supervisor Assessment (Form D)", "FORMD"),
    SELF_EVAL("Self-Evaluation (Form D2)", "FORMD2"),
    CHECKLIST("Submission Checklist & Dissertation", "CHK"),
    DEADLINES("Internship Deadlines & Milestones", "DLINE")
}

data class LeapFaqItem(
    val id: String,
    val category: LeapKnowledgeCategory,
    val question: String,
    val answer: String,
    val officialDocumentRef: String,
    val keyTakeaways: List<String>,
    val tags: List<String>
)

object LeapKnowledgeBase {

    val faqItems: List<LeapFaqItem> = listOf(
        // --- Category 1: PROCEDURES ---
        LeapFaqItem(
            id = "faq_proc_01",
            category = LeapKnowledgeCategory.PROCEDURES,
            question = "What is the Limkokwing LEAP Program and its core objective?",
            answer = "The Limkokwing Educational Advancement Program (LEAP) is a mandatory 12-week industrial internship module designed for penultimate and final-year undergraduate students at Limkokwing University of Creative Technology Sierra Leone. Its core objective is to bridge academic theory with real-world industry practice, enabling students to gain hands-on professional competence, industry networking, and work readiness within certified corporate, governmental, or NGO host organizations.",
            officialDocumentRef = "LEAP Student Handbook & Internship Charter Section 1.1",
            keyTakeaways = listOf(
                "12 weeks minimum duration (equivalent to 480 working hours)",
                "Compulsory credit-bearing module for graduation eligibility",
                "Monitored jointly by Academic Coordinators and Industry Supervisors"
            ),
            tags = listOf("leap", "objective", "internship", "overview", "limkokwing", "hours", "duration")
        ),
        LeapFaqItem(
            id = "faq_proc_02",
            category = LeapKnowledgeCategory.PROCEDURES,
            question = "What is the official step-by-step LEAP workflow from placement to graduation?",
            answer = "The standardized LEAP workflow spans 6 continuous phases:\n1. Placement & Acceptance: Student receives placement, company signs Form A2 (Acceptance), and submits Form A3 (Commencement) to LEAP Coordinator.\n2. Action Plan: Formulated by student in Weeks 1-2, signed by supervisor, and stamped with official company seal.\n3. Continuous Logging: Daily activity entries logged in the Room DB mobile log book and synthesized into Weekly Reports every Friday.\n4. Mid-Term Review: Week 6 Form B progress audit conducted by the Visiting Academic Coordinator.\n5. Final Assessment: Industry Supervisor completes Form D (18 criteria), while student submits Form D2 (Self-Evaluation).\n6. Report Binding & Defense: Student compiles the final typed dissertation report and satisfies all 8 items on the Submission Checklist.",
            officialDocumentRef = "LEAP Administrative Procedure Manual Phase 1-6",
            keyTakeaways = listOf(
                "Phase 1: Acceptance & Placement (Form A2/A3)",
                "Phase 2: Action Plan Approval (Weeks 1-2)",
                "Phase 3: Continuous Log Book & Weekly Reports",
                "Phase 4: Mid-Term Form B Review",
                "Phase 5: Form D & Form D2 Final Evaluations",
                "Phase 6: Final Dissertation Report & Checklist Clearance"
            ),
            tags = listOf("workflow", "procedure", "steps", "process", "phases", "lifecycle")
        ),
        LeapFaqItem(
            id = "faq_proc_03",
            category = LeapKnowledgeCategory.PROCEDURES,
            question = "What are the required working hours and attendance regulations?",
            answer = "Students must adhere to standard host organization working hours (typically 8:00 AM to 5:00 PM, Monday through Friday), completing a minimum of 40 hours per week over the 12-week tenure. Any absenteeism due to medical emergency must be supported by an authorized medical certificate and reported to both the Workplace Supervisor and Academic Coordinator within 24 hours.",
            officialDocumentRef = "LEAP Code of Conduct & Working Hours Regulation Section 3.2",
            keyTakeaways = listOf(
                "40 hours per week minimum full-time attendance",
                "Attendance evaluated under Form D Item #1 & #3",
                "Medical absence requires documentation within 24 hours"
            ),
            tags = listOf("working hours", "attendance", "punctuality", "absence", "regulations")
        ),
        LeapFaqItem(
            id = "faq_proc_04",
            category = LeapKnowledgeCategory.PROCEDURES,
            question = "Can a student change host organizations midway through the internship?",
            answer = "Host organization transfers are strictly prohibited unless exceptional extenuating circumstances exist (e.g., company closure, safety hazards, or verified breach of learning charter). Transfers require formal written petition submitted to Dr. Fatmata Sesay (LEAP Coordinator) and formal approval from the Academic Registry. Changing organizations without prior written authorization results in an immediate failing grade ('F').",
            officialDocumentRef = "LEAP Transfer Policy Article 4.5",
            keyTakeaways = listOf(
                "Mid-term transfers require formal LEAP Coordinator approval",
                "Unauthorized change of workplace leads to automatic failure",
                "Transferred students must re-submit Form A2, A3, and Action Plan"
            ),
            tags = listOf("transfer", "change company", "host organization", "policy", "coordinator approval")
        ),

        // --- Category 2: REQUIRED DOCUMENTS ---
        LeapFaqItem(
            id = "faq_docs_01",
            category = LeapKnowledgeCategory.DOCUMENTS,
            question = "What is the complete list of mandatory LEAP forms and documents?",
            answer = "The official LEAP document package consists of the following standardized instruments:\n• Form A2: Company Acceptance & Placement Confirmation\n• Form A3: Student Commencement Verification Form\n• Action Plan Form: 12-Week Task & Milestone Matrix with Company Stamp\n• Daily Log Book: Continuous activity records with reflection logs\n• Weekly Reports: Synthesized weekly progress submissions (Weeks 1–12)\n• Form B: Mid-Term Academic Coordinator Visitation & Progress Form\n• Form D: Industrial Workplace Supervisor Assessment (18 criteria)\n• Form D2: Student Self-Evaluation Form (Parts 1–3)\n• Final Internship Report: Formatted dissertation document in hardbound/PDF copy.",
            officialDocumentRef = "LEAP Document Inventory Standard Catalogue",
            keyTakeaways = listOf(
                "Total of 9 core administrative and academic instruments",
                "Digital counterparts in LEAP Manager synchronize directly with Room DB",
                "Missing any form delays final degree conferral"
            ),
            tags = listOf("forms", "documents", "form a2", "form a3", "form b", "form d", "form d2", "inventory")
        ),
        LeapFaqItem(
            id = "faq_docs_02",
            category = LeapKnowledgeCategory.DOCUMENTS,
            question = "What is Form A2 and what information must it contain?",
            answer = "Form A2 is the Host Organization Acceptance Letter. It verifies that the registered corporate entity has agreed to place the student intern. It must clearly state the student's full name, assigned department, appointed workplace supervisor, supervisor's official designation, corporate contact email/phone, official physical address, and must be signed by an authorized Human Resources or Department Head with the official company stamp.",
            officialDocumentRef = "LEAP Form A2 Specification Standard",
            keyTakeaways = listOf(
                "Verifies formal industrial placement offer",
                "Designates primary workplace supervisor and contact details",
                "Must bear official company stamp or seal"
            ),
            tags = listOf("form a2", "acceptance letter", "host organization", "stamp", "placement verification")
        ),
        LeapFaqItem(
            id = "faq_docs_03",
            category = LeapKnowledgeCategory.DOCUMENTS,
            question = "What is Form A3 (Commencement of Internship Verification)?",
            answer = "Form A3 is the official confirmation that the student has physically reported to duty on Day 1. It records the exact start date, student's assigned workspace/extension, host organization emergency contact, and confirms that workplace orientation and health/safety briefings were conducted.",
            officialDocumentRef = "LEAP Form A3 Placement Protocol",
            keyTakeaways = listOf(
                "Must be filed within 5 business days of reporting to duty",
                "Locks in commencement date on the academic record",
                "Confirms completion of safety and workplace orientation"
            ),
            tags = listOf("form a3", "commencement", "reporting", "day 1", "orientation")
        ),
        LeapFaqItem(
            id = "faq_docs_04",
            category = LeapKnowledgeCategory.DOCUMENTS,
            question = "What is Form B (Mid-Term Review Form)?",
            answer = "Form B is the Mid-Term Supervisory Visitation and Progress Review instrument. Conducted around Week 6, it captures the Academic Coordinator's assessment during site visits or virtual audits, checking student attendance, quality of log books, mentor satisfaction, and resolving any learning disparity.",
            officialDocumentRef = "LEAP Form B Visitation Standard",
            keyTakeaways = listOf(
                "Completed in Week 6 of the 12-week cycle",
                "Joint review between Academic Coordinator and Industry Mentor",
                "Early warning mechanism for at-risk students"
            ),
            tags = listOf("form b", "mid-term", "visitation", "review", "week 6")
        ),

        // --- Category 3: ACTION PLAN ---
        LeapFaqItem(
            id = "faq_plan_01",
            category = LeapKnowledgeCategory.ACTION_PLAN,
            question = "What is the LEAP Action Plan and when is it due?",
            answer = "The Action Plan is the strategic road map for the student's 12-week internship. It breaks down the overall learning objectives into concrete weekly and monthly deliverables across technical development, problem solving, teamwork, and system maintenance. The Action Plan must be drafted during Week 1 and finalized with supervisor approval and company stamp no later than Friday of Week 2.",
            officialDocumentRef = "LEAP Action Plan Formulation Framework Section 2.1",
            keyTakeaways = listOf(
                "Strict submission deadline: Friday of Week 2",
                "Maps tasks against 'What will I do', 'By When', and 'Achieved status'",
                "Requires student signature, supervisor signature, and company stamp"
            ),
            tags = listOf("action plan", "deadlines", "week 2", "tasks", "milestones", "stamp")
        ),
        LeapFaqItem(
            id = "faq_plan_02",
            category = LeapKnowledgeCategory.ACTION_PLAN,
            question = "What mandatory components must be included in each Action Plan task entry?",
            answer = "Each task entry in the LEAP Action Plan must clearly define:\n1. Task/Deliverable Description: Concrete technical or business objective (e.g., 'Configure internal LAN subnet and migrate SQLite tables').\n2. Target Completion Date ('By When'): Specific week number or calendar date.\n3. Competency Dimension: Alignment with IT, Software Engineering, Multimedia, or Business Computing learning domains.\n4. Achievement & Verification: Marked as achieved upon completion and verified by workplace supervisor digital sign-off.",
            officialDocumentRef = "LEAP Action Plan Scoring Matrix",
            keyTakeaways = listOf(
                "Must be specific, measurable, and relevant to degree specialization",
                "Progressively updated throughout the 12-week period",
                "Forms the baseline against which Form D Item #4 is evaluated"
            ),
            tags = listOf("action plan", "task description", "by when", "competency", "verification")
        ),

        // --- Category 4: DAILY LOGS & WEEKLY REPORTS ---
        LeapFaqItem(
            id = "faq_logs_01",
            category = LeapKnowledgeCategory.LOGS_REPORTS,
            question = "What specific fields are required for each Daily Log Book entry?",
            answer = "To ensure rigorous academic documentation and avoid generic notes, every daily entry in the LEAP Log Book must capture 7 mandatory fields:\n1. Date & Day of the Week\n2. Task/Activity Performed: Detailed operational narrative.\n3. Tools & Methods Used: Programming languages, frameworks, hardware, or methodologies (e.g., Kotlin, Room DB, Figma, Wireshark).\n4. People Worked With: Colleagues, department team, or supervisor.\n5. Skills Learned / Enhanced: Technical or soft skills acquired.\n6. Challenges Encountered: Operational, coding, or environmental hurdles.\n7. Student Reflection & Solution: Self-assessment of how challenges were resolved.",
            officialDocumentRef = "LEAP Daily Log Recording Standard Operating Procedure",
            keyTakeaways = listOf(
                "7 mandatory fields per daily log entry",
                "Must record tools/software used and challenges overcome",
                "Forms the empirical basis for dissertation chapters"
            ),
            tags = listOf("daily log", "logbook", "fields", "tools", "challenges", "reflection")
        ),
        LeapFaqItem(
            id = "faq_logs_02",
            category = LeapKnowledgeCategory.LOGS_REPORTS,
            question = "How are Weekly Reports synthesized and submitted?",
            answer = "At the end of each working week (every Friday by 5:00 PM), students synthesize their 5 daily log entries into a comprehensive Weekly Report. The report summarizes key weekly accomplishments, skills mastered, major technical problems resolved, and personal reflection. Workplace supervisors review the submission in their supervisor portal, assign a 1–5 star rating, and record written constructive feedback.",
            officialDocumentRef = "LEAP Weekly Reporting Regulation Article 5.1",
            keyTakeaways = listOf(
                "Weekly deadline: Every Friday by 5:00 PM",
                "Synthesizes 5 daily entries into executive weekly summary",
                "Supervisors provide feedback, approvals, and star ratings"
            ),
            tags = listOf("weekly report", "synthesis", "friday deadline", "supervisor feedback", "rating")
        ),
        LeapFaqItem(
            id = "faq_logs_03",
            category = LeapKnowledgeCategory.LOGS_REPORTS,
            question = "How does offline caching work for daily logs in areas with unstable internet?",
            answer = "The LEAP Internship Manager is architected with an offline-first Room SQLite database. Students can write, edit, and record daily logs and weekly reports without any active cellular or Wi-Fi connection. All entries are queued with an 'isSynced = false' state. Once network connectivity is detected or the user taps 'Sync Now', data automatically synchronizes with the Limkokwing cloud server without data loss.",
            officialDocumentRef = "LEAP Technical Architecture & Sierra Leone Low-Bandwidth Specification",
            keyTakeaways = listOf(
                "Full offline operation supported via Room Database",
                "Automatic queueing and timestamp preservation",
                "Prompted synchronization upon network recovery"
            ),
            tags = listOf("offline", "caching", "room db", "sync", "connectivity", "sierra leone")
        ),

        // --- Category 5: SUPERVISOR ASSESSMENT (FORM D) ---
        LeapFaqItem(
            id = "faq_formd_01",
            category = LeapKnowledgeCategory.SUPERVISOR_EVAL,
            question = "What is Form D and what percentage of the final grade does it represent?",
            answer = "Form D is the official Industrial Supervisor Assessment Instrument. It constitutes 50% of the overall LEAP academic grade. The industrial supervisor assesses the student across 18 distinct professional and technical criteria at the conclusion of Week 11 or 12, providing quantitative ratings (1–5 scale) and qualitative feedback on strengths and academic recommendations.",
            officialDocumentRef = "LEAP Academic Grading Scheme Section 6.2",
            keyTakeaways = listOf(
                "Contributes 50% of overall LEAP module grade",
                "Completed exclusively by the workplace supervisor",
                "Evaluates 18 standardized criteria across 4 categories"
            ),
            tags = listOf("form d", "supervisor assessment", "grading", "50 percent", "weighting")
        ),
        LeapFaqItem(
            id = "faq_formd_02",
            category = LeapKnowledgeCategory.SUPERVISOR_EVAL,
            question = "What are the 18 evaluation criteria assessed in Form D?",
            answer = "Form D evaluates students across 4 main domains with 18 criteria:\n1. Work Performance & Competence:\n • Technical Knowledge & Skill Application\n • Quality & Accuracy of Work\n • Volume of Output & Timeliness\n • Problem-Solving & Critical Thinking\n • Understanding of Organizational Objectives\n2. Work Habits & Discipline:\n • Attendance & Reliability\n • Punctuality\n • Safety & Workplace Compliance\n • Initiative & Self-Drive\n • Adaptability to New Tasks\n3. Interpersonal & Communication Skills:\n • Oral Communication\n • Written Communication & Reporting\n • Teamwork & Collaboration\n • Relationship with Supervisors & Colleagues\n • Customer/Client Service Orientation\n4. Professionalism & Ethical Conduct:\n • Ethical Integrity & Confidentiality\n • Professional Appearance & Demeanor\n • Receptiveness to Constructive Feedback.",
            officialDocumentRef = "LEAP Form D 18-Criteria Assessment Rubric",
            keyTakeaways = listOf(
                "Category A: Work Performance (5 criteria)",
                "Category B: Work Habits & Discipline (5 criteria)",
                "Category C: Communication & Teamwork (5 criteria)",
                "Category D: Professionalism & Ethics (3 criteria)"
            ),
            tags = listOf("form d", "18 criteria", "evaluation criteria", "rubric", "rating")
        ),
        LeapFaqItem(
            id = "faq_formd_03",
            category = LeapKnowledgeCategory.SUPERVISOR_EVAL,
            question = "What rating scale is used in Form D assessments?",
            answer = "Form D utilizes a 5-point Likert rating scale:\n• 5 = Superior / Outstanding (Exceptional mastery, exceeds professional standards)\n• 4 = Above Average / Exceeds Expectations (High competence, minimal guidance required)\n• 3 = Average / Satisfactory (Meets standard workplace expectations)\n• 2 = Below Average / Needs Improvement (Inconsistent performance, requires supervision)\n• 1 = Unsatisfactory / Poor (Fails to meet basic expectations).",
            officialDocumentRef = "LEAP Form D Likert Scoring Guide",
            keyTakeaways = listOf(
                "5 = Superior, 4 = Above Average, 3 = Satisfactory, 2 = Needs Improvement, 1 = Unsatisfactory",
                "Scores below 3.0 trigger academic review by Coordinator",
                "Maximum raw score is 90 points (18 items × 5 points)"
            ),
            tags = listOf("rating scale", "1 to 5", "likert", "scoring", "superior", "satisfactory")
        ),

        // --- Category 6: SELF-EVALUATION (FORM D2) ---
        LeapFaqItem(
            id = "faq_formd2_01",
            category = LeapKnowledgeCategory.SELF_EVAL,
            question = "What is Form D2 (Student Self-Evaluation) and why is it mandatory?",
            answer = "Form D2 is the comprehensive Student Self-Evaluation Instrument. It requires students to critically reflect upon their entire 12-week learning trajectory, analyze how their university coursework applied to industrial problems, and evaluate workplace support. Completing Form D2 is mandatory for graduation clearance and carries a 10% weighting towards academic evaluation.",
            officialDocumentRef = "LEAP Student Self-Evaluation Charter Section 7.1",
            keyTakeaways = listOf(
                "Mandatory reflection component completed by the student",
                "Carries 10% weight in academic assessment",
                "Consists of 3 detailed structured parts"
            ),
            tags = listOf("form d2", "self-evaluation", "reflection", "mandatory", "student evaluation")
        ),
        LeapFaqItem(
            id = "faq_formd2_02",
            category = LeapKnowledgeCategory.SELF_EVAL,
            question = "What are the 3 parts of Form D2 and what must be reported in each?",
            answer = "Form D2 consists of 3 distinct sections:\n• Part 1: Learning Goals & Experience (Evaluation of whether initial Action Plan goals were achieved, technical skills acquired, and real-world tools mastered).\n• Part 2: Work Environment & Support (Evaluation of supervision quality, company resources, safety protocols, and LEAP Directorate advisory assistance).\n• Part 3: Strengths, Weaknesses & Career Influence (Critical self-appraisal of personal strengths, professional weaknesses identified, how the internship influenced career aspirations, and recommendations for future interns).",
            officialDocumentRef = "LEAP Form D2 Tripartite Reflection Structure",
            keyTakeaways = listOf(
                "Part 1: Goals, Experience & Tool Mastery",
                "Part 2: Work Environment & Mentorship Quality",
                "Part 3: Self-Appraisal & Career Trajectory Impact"
            ),
            tags = listOf("form d2", "part 1", "part 2", "part 3", "goals", "strengths", "weaknesses")
        ),

        // --- Category 7: SUBMISSION CHECKLIST & FINAL REPORT ---
        LeapFaqItem(
            id = "faq_chk_01",
            category = LeapKnowledgeCategory.CHECKLIST,
            question = "What are the 8 mandatory items on the LEAP Final Submission Checklist?",
            answer = "Before a student is cleared for LEAP graduation defense, the following 8 items must be verified by both Student and Supervisor:\n1. Form D (Supervisor Evaluation Form - Signed & Stamped)\n2. Form D2 (Student Self-Evaluation Form - Fully Completed)\n3. Typed Daily Log Book (Weeks 1 to 12 - Complete Entries)\n4. Approved Action Plan Form (With Company Stamp & Signatures)\n5. Form A2 (Company Acceptance Letter)\n6. Form A3 (Commencement Verification Form)\n7. Form B (Mid-Term Review Form - Signed by Coordinator)\n8. Final Typed Internship Report (Dissertation format with formal binding).",
            officialDocumentRef = "LEAP 8-Item Submission Verification Standard",
            keyTakeaways = listOf(
                "8 non-negotiable items required for clearance",
                "Requires dual check-off (Student verification + Supervisor sign-off)",
                "Forms the primary audit trail for academic degree verification"
            ),
            tags = listOf("checklist", "8 items", "submission", "clearance", "verification", "graduation")
        ),
        LeapFaqItem(
            id = "faq_chk_02",
            category = LeapKnowledgeCategory.CHECKLIST,
            question = "What are the formatting specifications for the Final Typed Internship Report?",
            answer = "The Final Typed Internship Report must adhere to Limkokwing University dissertation formatting guidelines:\n• Length: 25 to 40 pages (excluding appendices).\n• Typography: Times New Roman or Arial, 12pt, 1.5 line spacing, 1-inch margins.\n• Chapter Structure: Chapter 1: Host Organization Background; Chapter 2: Technical Duties & Action Plan Execution; Chapter 3: Technical Problem Solving & Methodologies; Chapter 4: Reflections & Skill Gaps; Chapter 5: Conclusion & Recommendations.\n• Cover: Standard Limkokwing University Black hardbound cover with gold embossed lettering (for physical submission) or standardized PDF template (for digital submission).",
            officialDocumentRef = "Limkokwing Dissertation & Internship Report Writing Guide",
            keyTakeaways = listOf(
                "25-40 pages typed with 1.5 line spacing",
                "5 standard academic chapters",
                "Uploaded as certified PDF or hardbound copy"
            ),
            tags = listOf("internship report", "formatting", "dissertation", "chapters", "pages", "binding")
        ),
        LeapFaqItem(
            id = "faq_chk_03",
            category = LeapKnowledgeCategory.CHECKLIST,
            question = "What is the difference between paper-based LEAP submissions vs the Digital LEAP Manager?",
            answer = "The traditional paper process required students to physically carry printed logbooks and carbon-copy assessment forms between remote workplace sites and the Hill Station campus in Freetown, creating high risk of lost records, unverified signatures, and delayed grading. The Digital LEAP Manager introduces real-time Room DB local caching, verified cryptographic digital sign-offs, automated timeline tracking, and AI-assisted guideline recommendations, drastically reducing paperwork latency.",
            officialDocumentRef = "LEAP Digital Transformation Whitepaper Limkokwing SL",
            keyTakeaways = listOf(
                "Eliminates lost paper logbooks and manual travel burdens",
                "Provides tamper-proof digital sign-offs and instant coordinator audit",
                "Maintains offline accessibility during power and internet outages"
            ),
            tags = listOf("paper vs digital", "transformation", "dissertation context", "advantages", "benefits")
        ),

        // --- Category 8: DEADLINES & MILESTONES ---
        LeapFaqItem(
            id = "faq_dline_01",
            category = LeapKnowledgeCategory.DEADLINES,
            question = "What is the complete 12-week timeline and deadline schedule?",
            answer = "The standardized 12-week LEAP semester schedule:\n• Week 1: Report to workplace, complete Form A2 & A3 submission.\n• Week 2: Action Plan finalized, signed by supervisor, stamped, and uploaded.\n• Weeks 1–12: Continuous daily log entries and Weekly Reports due every Friday at 5:00 PM.\n• Week 6: Mid-Term Form B Review and Coordinator visitation.\n• Week 10: First draft of Final Internship Report submitted for supervisor review.\n• Week 11: Form D (Supervisor Evaluation) and Form D2 (Self-Evaluation) completed.\n• Week 12: Final Internship Report uploaded and all 8 Checklist items cleared.\n• Week 13: Academic Board defense and grade moderation.",
            officialDocumentRef = "LEAP Academic Calendar & Milestone Roadmap",
            keyTakeaways = listOf(
                "Week 1: Form A2/A3 reporting",
                "Week 2: Action Plan sign-off deadline",
                "Week 6: Mid-term Form B audit",
                "Week 11: Form D and Form D2 evaluations",
                "Week 12: Final Report and Checklist closure"
            ),
            tags = listOf("deadlines", "timeline", "milestones", "schedule", "week 1", "week 2", "week 6", "week 11", "week 12")
        ),
        LeapFaqItem(
            id = "faq_dline_02",
            category = LeapKnowledgeCategory.DEADLINES,
            question = "What penalties apply if a student misses report or form deadlines?",
            answer = "Submitting weekly reports past the Friday 5:00 PM deadline incurs a 5% grade deduction per day of delay. Unsubmitted Action Plans by Week 3 result in a formal academic warning issued by the LEAP Coordinator. Failure to submit Form D or Form D2 by Week 12 results in an 'Incomplete' (INC) grade, requiring the student to re-register for the module in the subsequent academic session.",
            officialDocumentRef = "LEAP Academic Disciplinary Policy Section 8.3",
            keyTakeaways = listOf(
                "5% per day deduction for late weekly report submissions",
                "Formal warning for Action Plan delay past Week 2",
                "INC grade for missing final Form D/D2 evaluations"
            ),
            tags = listOf("penalties", "late submission", "deadline", "disciplinary", "incomplete")
        ),
        LeapFaqItem(
            id = "faq_dline_03",
            category = LeapKnowledgeCategory.DEADLINES,
            question = "Does the AI Assistant assign or alter official academic grades?",
            answer = "No. The AI LEAP Assistant is strictly an advisory and informative instrument. It does NOT assign, moderate, or modify official grades. Official grades are determined solely by: 1) The Industrial Workplace Supervisor (Form D - 50%), 2) The Academic Coordinator / Visiting Faculty (Form B & Log Books - 20%), and 3) The Academic Defense Review Committee (Final Dissertation Report - 30%).",
            officialDocumentRef = "LEAP AI Ethics & Academic Integrity Policy Article 1.3",
            keyTakeaways = listOf(
                "AI is purely advisory and does not grade students",
                "Form D Supervisor Evaluation = 50%",
                "Log Books & Form B Review = 20%",
                "Final Dissertation Report & Defense = 30%"
            ),
            tags = listOf("grading", "ai ethics", "advisory", "weighting", "academic board")
        )
    )

    val faqList: List<LeapFaqItem> get() = faqItems

    fun getByCategory(category: LeapKnowledgeCategory): List<LeapFaqItem> {
        if (category == LeapKnowledgeCategory.ALL) return faqItems
        return faqItems.filter { it.category == category }
    }

    fun search(query: String, category: LeapKnowledgeCategory = LeapKnowledgeCategory.ALL): List<LeapFaqItem> {
        val q = query.trim().lowercase()
        return faqItems.filter { item ->
            val matchesCategory = category == LeapKnowledgeCategory.ALL || item.category == category
            val matchesQuery = if (q.isEmpty()) true else {
                item.question.lowercase().contains(q) ||
                item.answer.lowercase().contains(q) ||
                item.tags.any { it.lowercase().contains(q) } ||
                item.officialDocumentRef.lowercase().contains(q)
            }
            matchesCategory && matchesQuery
        }
    }

    fun findBestResponse(userQuery: String): String {
        val lower = userQuery.lowercase().trim()

        // 1. Direct tag/question match
        var bestItem: LeapFaqItem? = null
        var maxScore = 0

        for (item in faqItems) {
            var score = 0
            val qWords = item.question.lowercase().split(" ", "?", ",", ".", "-", "(", ")")
            val tags = item.tags.map { it.lowercase() }
            val queryWords = lower.split(" ", "?", ",", ".", "-", "(", ")").filter { it.length > 2 }

            for (w in queryWords) {
                if (tags.contains(w)) score += 5
                if (qWords.contains(w)) score += 3
                if (item.answer.lowercase().contains(w)) score += 1
            }

            if (score > maxScore) {
                maxScore = score
                bestItem = item
            }
        }

        if (bestItem != null && maxScore >= 4) {
            val takeaways = bestItem.keyTakeaways.joinToString("\n• ", prefix = "• ")
            return "${bestItem.answer}\n\n📌 **Key Takeaways & Regulations:**\n$takeaways\n\n📖 **Official Reference:** ${bestItem.officialDocumentRef}\n\n*[Advisory Note: Official university regulations supersede AI responses.]*"
        }

        // Generic greetings / fallback
        if (lower.contains("hello") || lower.contains("hi") || lower.contains("help")) {
            return "Hello! I am your AI LEAP Assistant for Limkokwing University Sierra Leone. I have access to the complete LEAP knowledge base. You can ask me about:\n\n• LEAP Procedures & 12-week workflows\n• Required Forms (Form A2, A3, B, D, D2)\n• Action Plan deadlines & stamping\n• Daily Log formatting (7 required fields)\n• Supervisor 18-criteria assessment (Form D)\n• Student Self-Evaluation (Form D2 Parts 1-3)\n• 8-Item Submission Checklist & Dissertation Report."
        }

        return "I could not locate an exact match for that specific inquiry in the LEAP Knowledge Base. To maintain academic compliance, please consult Dr. Fatmata Sesay at the Limkokwing LEAP Coordination Directorate (Freetown Campus) or refer to your LEAP Student Handbook."
    }
}
