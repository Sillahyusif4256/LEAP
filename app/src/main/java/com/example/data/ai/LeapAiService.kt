package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

data class ChatMessage(
    val sender: MessageSender,
    val text: String,
    val timestamp: String = "Just now",
    val isGeminiPowered: Boolean = true
)

enum class MessageSender {
    USER, AI, SYSTEM
}

data class InternshipMatchResult(
    val organizationName: String,
    val department: String,
    val matchScore: Int, // e.g. 94%
    val reason: String,
    val requiredSkills: List<String>,
    val industry: String,
    val location: String = "Freetown, Sierra Leone"
)

data class StudentProgressInsight(
    val studentName: String,
    val studentCode: String,
    val riskStatus: RiskLevel,
    val progressSummary: String,
    val missingRequirements: List<String>,
    val suggestedFollowUp: String
)

enum class RiskLevel {
    ON_TRACK, NEEDS_ATTENTION, AT_RISK
}

interface LeapAiService {
    suspend fun askLeapAssistant(question: String, history: List<ChatMessage> = emptyList()): String
    suspend fun analyzeSupervisorFeedback(supervisorComment: String, rating: Int): String
    suspend fun getInternshipMatches(
        programme: String,
        skills: String,
        interests: String,
        department: String,
        location: String,
        experience: String
    ): List<InternshipMatchResult>
    suspend fun generateProgressInsights(studentName: String, progressPct: Int, currentWeek: Int, hasMissingDocs: Boolean): StudentProgressInsight
}

class LeapAiServiceImpl : LeapAiService {

    private val systemInstructionText = """
        You are the official Limkokwing University LEAP (Limkokwing Enterprise Advancement Programme) AI Academic & Industry Advisor for university internship programs.
        
        Your core responsibilities:
        1. Answer student questions accurately regarding internship requirements, the 12-week (400-hour) requirement, the 7 required daily logbook fields (Date, Day, Activities, Tools/Methods, Collaborators, Skills Acquired, Challenges & Reflection), weekly reports, and the 8 mandatory submission checklist documents (Form A2, Form A3, Action Plan, Form B visiting lecturer appraisal, Form D supervisor evaluation with 10 performance dimensions, Form D2 self-appraisal, physical logbook with company stamp, and final dissertation report).
        2. Help students interpret workplace supervisor feedback and company ratings constructively. Offer actionable improvement strategies for soft skills, technical delivery, communication, punctuality, and professional etiquette.
        3. Formulate SMART weekly goals, provide guidance on approaching workplace supervisors for reviews, and clarify grading and submission deadlines.
        
        Response guidelines:
        - Provide helpful, well-structured, clear answers with concise bullet points where suitable.
        - Adopt an encouraging, professional academic advisor tone.
        - Mention relevant LEAP forms (Form D, Form D2, Form B, Action Plan) and policies when relevant.
    """.trimIndent()

    override suspend fun askLeapAssistant(question: String, history: List<ChatMessage>): String {
        return withContext(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
                try {
                    val contentsList = mutableListOf<GeminiContent>()
                    
                    // Add recent conversation history for contextual multi-turn chat (limit to last 6 messages)
                    history.takeLast(6).forEach { msg ->
                        val role = if (msg.sender == MessageSender.USER) "user" else "model"
                        contentsList.add(GeminiContent(role = role, parts = listOf(GeminiPart(text = msg.text))))
                    }
                    
                    // Add current question
                    contentsList.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = question))))

                    val request = GeminiGenerateRequest(
                        contents = contentsList,
                        systemInstruction = GeminiContent(
                            parts = listOf(GeminiPart(text = systemInstructionText))
                        ),
                        generationConfig = GeminiGenerationConfig(
                            temperature = 0.7f,
                            topP = 0.95f,
                            maxOutputTokens = 1200
                        )
                    )

                    val response = GeminiRetrofitClient.apiService.generateContent(apiKey, request)
                    val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!replyText.isNullOrBlank()) {
                        return@withContext replyText.trim()
                    }
                } catch (e: Exception) {
                    Log.w("LeapAiService", "Gemini API call failed, falling back to local LEAP knowledge base: ${e.localizedMessage}")
                }
            }

            // High-reliability offline / curated knowledge-base fallback
            delay(350)
            LeapKnowledgeBase.findBestResponse(question)
        }
    }

    override suspend fun analyzeSupervisorFeedback(supervisorComment: String, rating: Int): String {
        val prompt = """
            Please analyze the following supervisor feedback received by an intern:
            - Supervisor Rating: $rating out of 5
            - Supervisor Remarks: "$supervisorComment"
            
            Provide a constructive 4-part breakdown for the student:
            1. Key Positive Strengths Noted
            2. Core Growth / Improvement Opportunities
            3. Actionable Next Steps to implement in the upcoming work week
            4. Suggested Professional Response or Clarification Questions for the Supervisor
        """.trimIndent()

        return askLeapAssistant(prompt)
    }

    override suspend fun getInternshipMatches(
        programme: String,
        skills: String,
        interests: String,
        department: String,
        location: String,
        experience: String
    ): List<InternshipMatchResult> {
        delay(400) // Fast recommendation compute
        val lowerProg = programme.lowercase()
        val lowerSkills = skills.lowercase()

        return if (lowerProg.contains("software") || lowerProg.contains("information technology") || lowerSkills.contains("kotlin") || lowerSkills.contains("code") || lowerSkills.contains("web")) {
            listOf(
                InternshipMatchResult(
                    organizationName = "Tech Solutions SL Ltd.",
                    department = "Software Engineering & Cloud Systems",
                    matchScore = 96,
                    reason = "High alignment with your programming profile, Kotlin/Android mobile interests, and software development coursework.",
                    requiredSkills = listOf("Kotlin / Java", "Room SQLite Database", "Git Version Control", "REST API integration"),
                    industry = "Software Development & ICT",
                    location = "14 Wilkinson Road, Freetown"
                ),
                InternshipMatchResult(
                    organizationName = "Orange Sierra Leone",
                    department = "Fintech & Digital Platforms (Orange Money)",
                    matchScore = 89,
                    reason = "Strong match for enterprise backend systems, transactional database management, and mobile API services.",
                    requiredSkills = listOf("System Integration", "SQL Databases", "API Testing", "Agile Methodology"),
                    industry = "Telecommunications & Digital Services",
                    location = "Hill Station, Freetown"
                ),
                InternshipMatchResult(
                    organizationName = "Sierra Leone Commercial Bank (SLCB)",
                    department = "Digital Banking & Core IT Security",
                    matchScore = 82,
                    reason = "Well suited for students interested in fintech security compliance and digital channel development.",
                    requiredSkills = listOf("Information Security", "Network Architecture", "Customer Channel Support"),
                    industry = "Banking & Financial Services",
                    location = "Siaka Stevens Street, Freetown"
                )
            )
        } else if (lowerProg.contains("multimedia") || lowerProg.contains("design") || lowerSkills.contains("ui") || lowerSkills.contains("graphics")) {
            listOf(
                InternshipMatchResult(
                    organizationName = "Sierra Leone Commercial Bank (SLCB)",
                    department = "Digital Banking UI/UX & Brand Media",
                    matchScore = 94,
                    reason = "Excellent match for creative multimedia design, customer journey wireframing, and digital marketing assets.",
                    requiredSkills = listOf("Figma / UI Design", "Adobe Creative Suite", "Mobile Prototyping", "User Research"),
                    industry = "Banking & Financial Services",
                    location = "Siaka Stevens Street, Freetown"
                ),
                InternshipMatchResult(
                    organizationName = "Africell Sierra Leone",
                    department = "Creative Brand & Digital VAS Media",
                    matchScore = 88,
                    reason = "Provides rich opportunities for commercial multimedia content creation and mobile visual campaigns.",
                    requiredSkills = listOf("Motion Graphics", "Video Editing", "Visual Branding", "Copywriting"),
                    industry = "Telecommunications & Media",
                    location = "Wilberforce, Freetown"
                )
            )
        } else {
            listOf(
                InternshipMatchResult(
                    organizationName = "National Revenue Authority (NRA)",
                    department = "ICT Infrastructure & Database Admin",
                    matchScore = 91,
                    reason = "Strong alignment with enterprise database systems, hardware infrastructure, and public sector data administration.",
                    requiredSkills = listOf("Database Administration", "Network Troubleshooting", "Hardware Maintenance", "Reporting"),
                    industry = "Public Sector Governance",
                    location = "Gloucester Street, Freetown"
                ),
                InternshipMatchResult(
                    organizationName = "Orange Sierra Leone",
                    department = "Network Operations Centre (NOC)",
                    matchScore = 85,
                    reason = "Practical exposure to live telecom infrastructure, fiber routing, and real-time monitoring tools.",
                    requiredSkills = listOf("TCP/IP Networking", "Linux Basics", "Network Monitoring Tools"),
                    industry = "Telecommunications",
                    location = "Hill Station, Freetown"
                )
            )
        }
    }

    override suspend fun generateProgressInsights(
        studentName: String,
        progressPct: Int,
        currentWeek: Int,
        hasMissingDocs: Boolean
    ): StudentProgressInsight {
        delay(300)
        return when {
            hasMissingDocs || progressPct < 40 && currentWeek >= 6 -> {
                StudentProgressInsight(
                    studentName = studentName,
                    studentCode = "LKW-SL",
                    riskStatus = RiskLevel.AT_RISK,
                    progressSummary = "$studentName is at Week $currentWeek with only $progressPct% progress completed. Crucial LEAP documentation is outstanding.",
                    missingRequirements = listOf("Form A2/A3 submission", "Action Plan supervisor sign-off", "Weekly Reports (Weeks 4-6)"),
                    suggestedFollowUp = "Issue an automated coordinator notification and schedule an on-site supervisor consultation at host organization."
                )
            }
            progressPct < 70 && currentWeek >= 8 -> {
                StudentProgressInsight(
                    studentName = studentName,
                    studentCode = "LKW-SL",
                    riskStatus = RiskLevel.NEEDS_ATTENTION,
                    progressSummary = "$studentName is progressing at $progressPct% in Week $currentWeek. Daily log cadence is active but weekly report approvals are pending review.",
                    missingRequirements = listOf("Supervisor sign-off on Week 7 Report", "Mid-term Form B verification"),
                    suggestedFollowUp = "Prompt the Workplace Supervisor to review submitted weekly reports."
                )
            }
            else -> {
                StudentProgressInsight(
                    studentName = studentName,
                    studentCode = "LKW-SL",
                    riskStatus = RiskLevel.ON_TRACK,
                    progressSummary = "$studentName is performing excellently with $progressPct% progress across Week $currentWeek. Daily logs, action plans, and weekly reports are thoroughly submitted and approved.",
                    missingRequirements = emptyList(),
                    suggestedFollowUp = "Maintain regular bi-weekly monitoring. Prepare for Final Form D evaluation and dissertation report submission."
                )
            }
        }
    }
}

