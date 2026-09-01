package com.example

import com.example.data.ai.LeapAiServiceImpl
import com.example.data.ai.LeapKnowledgeBase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testKnowledgeBaseFaqCoverage() {
        val faqs = LeapKnowledgeBase.faqList
        assertTrue(faqs.isNotEmpty())
        
        val formDItem = LeapKnowledgeBase.findBestResponse("Form D evaluation criteria")
        assertTrue(formDItem.contains("Form D", ignoreCase = true) || formDItem.contains("assessment", ignoreCase = true))
        
        val logbookItem = LeapKnowledgeBase.findBestResponse("daily logbook fields")
        assertTrue(logbookItem.contains("log", ignoreCase = true) || logbookItem.contains("fields", ignoreCase = true))
    }

    @Test
    fun testAiAssistantFallbackResponse() = runBlocking {
        val service = LeapAiServiceImpl()
        val response = service.askLeapAssistant("What are the 8 checklist items?")
        assertTrue(response.isNotBlank())
    }

    @Test
    fun testSupervisorFeedbackAnalysis() = runBlocking {
        val service = LeapAiServiceImpl()
        val analysis = service.analyzeSupervisorFeedback("Great job on UI components but needs to improve test coverage.", 4)
        assertTrue(analysis.isNotBlank())
    }
}

