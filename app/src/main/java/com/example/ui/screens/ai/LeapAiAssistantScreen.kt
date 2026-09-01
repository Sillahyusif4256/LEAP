package com.example.ui.screens.ai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ChatMessage
import com.example.data.ai.LeapFaqItem
import com.example.data.ai.LeapKnowledgeBase
import com.example.data.ai.LeapKnowledgeCategory
import com.example.data.ai.MessageSender
import com.example.ui.components.LeapTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel
import kotlinx.coroutines.launch

enum class AssistantTopicCategory(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    ALL("All Questions", Icons.Default.AutoAwesome),
    INTERNSHIP_REQUIREMENTS("Internship Requirements", Icons.Default.Assignment),
    COMPANY_FEEDBACK("Company Feedback & Advice", Icons.Default.RateReview),
    LOGBOOK_FORMS("Logbooks & Form D/D2", Icons.Default.MenuBook),
    MILESTONES("Milestones & Deadlines", Icons.Default.Schedule)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeapAiAssistantScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val messages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val pendingSync by viewModel.pendingSyncCount.collectAsState()
    val supervisorFeedbacks by viewModel.supervisorFeedbacks.collectAsState()

    var selectedAssistantTab by remember { mutableStateOf(0) } // 0: Chat, 1: Feedback Analyzer Tool, 2: Knowledge Base
    var selectedTopicCategory by remember { mutableStateOf(AssistantTopicCategory.ALL) }
    var inputPrompt by remember { mutableStateOf("") }
    var kbSearchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<LeapKnowledgeCategory?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val topicQueries = remember(selectedTopicCategory) {
        when (selectedTopicCategory) {
            AssistantTopicCategory.ALL -> listOf(
                "What are the 7 required fields for daily log entries?",
                "How do I interpret supervisor feedback on initiative?",
                "Explain the 10 Form D supervisor evaluation criteria",
                "What are the 8 mandatory submission checklist items?",
                "How do I reach the 400-hour requirement?"
            )
            AssistantTopicCategory.INTERNSHIP_REQUIREMENTS -> listOf(
                "What are the 8 mandatory submission checklist documents?",
                "Explain the 12-week and 400-hour requirement",
                "What is the difference between Form A2 and Form A3?",
                "When is the Visiting Lecturer Form B appraisal conducted?",
                "What is required for the final internship dissertation binder?"
            )
            AssistantTopicCategory.COMPANY_FEEDBACK -> listOf(
                "How do I interpret supervisor remarks about code documentation?",
                "What should I do if my supervisor rated my communication 3 out of 5?",
                "How to ask my workplace supervisor for regular weekly feedback?",
                "Draft a polite message asking for a weekly report review",
                "How to handle constructive criticism during sprint reviews?"
            )
            AssistantTopicCategory.LOGBOOK_FORMS -> listOf(
                "What are the 7 required fields in each daily log entry?",
                "How is Form D graded across the 10 performance dimensions?",
                "What are the 7 parts of Form D2 student self-evaluation?",
                "Does my physical logbook need a company seal or stamp?",
                "Can I submit digital scans of signed LEAP forms?"
            )
            AssistantTopicCategory.MILESTONES -> listOf(
                "When is the Action Plan (Form A2/A3) due?",
                "When is the mid-term progress assessment?",
                "What are the consequences of late weekly report submissions?",
                "When must the final Form D evaluation be submitted?"
            )
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty() && selectedAssistantTab == 0) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Chat History", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to clear your current conversation with the LEAP Gemini Assistant?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearChat()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "LEAP Gemini AI",
                subtitle = "Internship Requirements & Feedback Advisor",
                showBackButton = true,
                onBackClick = onBack,
                isOffline = isOffline,
                isSyncing = isSyncing,
                pendingSyncCount = pendingSync
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Mode Tabs (Dedicated AI Chat vs Feedback Analyzer vs Knowledge Base)
            TabRow(
                selectedTabIndex = selectedAssistantTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = LeapNavyPrimary
            ) {
                Tab(
                    selected = selectedAssistantTab == 0,
                    onClick = { selectedAssistantTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (selectedAssistantTab == 0) LeapGoldAccent else MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gemini Chat", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_ai_chat")
                )
                Tab(
                    selected = selectedAssistantTab == 1,
                    onClick = { selectedAssistantTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Feedback Analyzer", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_feedback_analyzer")
                )
                Tab(
                    selected = selectedAssistantTab == 2,
                    onClick = { selectedAssistantTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Knowledge Base", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_knowledge_base")
                )
            }

            // Gemini Status & Model Badge Bar
            Surface(
                color = if (isOffline) Color(0xFFFEF3C7) else Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, if (isOffline) Color(0xFFFDE68A) else Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) Color(0xFFD97706) else Color(0xFF16A34A))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOffline) "Offline Engine • LEAP Curated Knowledge Base Active" else "Connected to Gemini 3.5 Flash • Official LEAP Academic Advisor",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isOffline) Color(0xFF92400E) else Color(0xFF166534),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    if (selectedAssistantTab == 0) {
                        IconButton(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.size(24.dp).testTag("btn_clear_chat")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear Chat",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            when (selectedAssistantTab) {
                0 -> {
                    // Chat Interface
                    GeminiDedicatedChatView(
                        messages = messages,
                        isThinking = isThinking,
                        selectedCategory = selectedTopicCategory,
                        onSelectCategory = { selectedTopicCategory = it },
                        suggestedQueries = topicQueries,
                        inputPrompt = inputPrompt,
                        onInputPromptChange = { inputPrompt = it },
                        onSendMessage = { text ->
                            viewModel.askAiAssistant(text)
                            inputPrompt = ""
                        },
                        listState = listState
                    )
                }
                1 -> {
                    // Dedicated Feedback Analyzer Tool
                    SupervisorFeedbackAnalyzerView(
                        feedbacks = supervisorFeedbacks,
                        onAnalyze = { comment, rating ->
                            viewModel.analyzeSupervisorFeedback(comment, rating)
                            selectedAssistantTab = 0
                        },
                        onAskDirectQuestion = { q ->
                            viewModel.askAiAssistant(q)
                            selectedAssistantTab = 0
                        }
                    )
                }
                2 -> {
                    // Knowledge Base Directory View
                    KnowledgeBaseDirectoryView(
                        searchQuery = kbSearchQuery,
                        onSearchQueryChange = { kbSearchQuery = it },
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it },
                        onAskInChat = { question ->
                            selectedAssistantTab = 0
                            viewModel.askAiAssistant(question)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GeminiDedicatedChatView(
    messages: List<ChatMessage>,
    isThinking: Boolean,
    selectedCategory: AssistantTopicCategory,
    onSelectCategory: (AssistantTopicCategory) -> Unit,
    suggestedQueries: List<String>,
    inputPrompt: String,
    onInputPromptChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Topic Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(AssistantTopicCategory.values()) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectCategory(category) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(category.icon, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(category.title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LeapNavyPrimary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = LeapGoldAccent
                    ),
                    modifier = Modifier.testTag("filter_topic_${category.name}")
                )
            }
        }

        // Suggested Prompt Carousel
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(suggestedQueries) { query ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            onSendMessage(query)
                        }
                        .testTag("quick_prompt_${query.take(15)}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = LeapNavyPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = query,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)

        // Chat Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages) { msg ->
                GeminiChatBubble(message = msg)
            }

            if (isThinking) {
                item {
                    GeminiThinkingCard()
                }
            }
        }

        // Chat Input Composer Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = inputPrompt,
                    onValueChange = onInputPromptChange,
                    placeholder = {
                        Text(
                            text = when (selectedCategory) {
                                AssistantTopicCategory.INTERNSHIP_REQUIREMENTS -> "Ask about Form D, checklists, 400h rule..."
                                AssistantTopicCategory.COMPANY_FEEDBACK -> "Ask how to interpret supervisor comments/ratings..."
                                AssistantTopicCategory.LOGBOOK_FORMS -> "Ask about daily log fields or self-evaluations..."
                                else -> "Ask about internship guidelines or company feedback..."
                            },
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_assistant_input"),
                    maxLines = 4,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LeapNavyPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputPrompt.isNotBlank()) {
                            val query = inputPrompt
                            onSendMessage(query)
                        }
                    },
                    enabled = inputPrompt.isNotBlank() && !isThinking,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (inputPrompt.isNotBlank() && !isThinking) LeapNavyPrimary else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("ai_assistant_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputPrompt.isNotBlank() && !isThinking) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GeminiChatBubble(message: ChatMessage) {
    val context = LocalContext.current
    val isUser = message.sender == MessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(LeapNavyPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Gemini AI",
                    tint = LeapGoldAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            if (!isUser) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = "LEAP Gemini Advisor",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LeapNavyPrimary,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = "AI Verified",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = LeapBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = if (isUser) 18.dp else 4.dp,
                    topEnd = 18.dp,
                    bottomStart = 18.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                color = if (isUser) LeapNavyPrimary else MaterialTheme.colorScheme.surface,
                border = if (!isUser) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                shadowElevation = if (!isUser) 1.dp else 0.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 21.sp,
                            fontSize = 13.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message.timestamp,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isUser) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )

                        if (!isUser) {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("LEAP AI Response", message.text)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Copied answer to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy response",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GeminiThinkingCard() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(LeapNavyPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI",
                tint = LeapGoldAccent,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = LeapGoldAccent,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Gemini is analyzing LEAP requirements & feedback...",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun SupervisorFeedbackAnalyzerView(
    feedbacks: List<com.example.data.local.entities.SupervisorFeedbackEntity>,
    onAnalyze: (String, Int) -> Unit,
    onAskDirectQuestion: (String) -> Unit
) {
    var customFeedbackText by remember { mutableStateOf("") }
    var selectedRating by remember { mutableStateOf(4) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LeapNavyPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Analytics, contentDescription = null, tint = LeapGoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Supervisor Feedback Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Paste workplace supervisor comments or review existing feedback to generate actionable growth strategies, key strengths, and professional response scripts.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 16.sp)
                    )
                }
            }
        }

        // Interactive Feedback Input Tool
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Analyze Workplace Feedback",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LeapNavyPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Supervisor Rating:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..5).forEach { star ->
                            val isSelected = star <= selectedRating
                            IconButton(
                                onClick = { selectedRating = star },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Rating $star",
                                    tint = if (isSelected) LeapGoldAccent else Color(0xFF94A3B8),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "$selectedRating / 5 Stars",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LeapNavyPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customFeedbackText,
                        onValueChange = { customFeedbackText = it },
                        label = { Text("Supervisor Comments / Remarks") },
                        placeholder = { Text("e.g. Great progress on backend API integration, but needs to document code and improve daily standup participation...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_feedback_analysis"),
                        minLines = 3,
                        maxLines = 6,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (customFeedbackText.isNotBlank()) {
                                onAnalyze(customFeedbackText, selectedRating)
                            }
                        },
                        enabled = customFeedbackText.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_run_feedback_analysis")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = LeapGoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate AI Feedback Action Plan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Recent Supervisor Feedback from Database
        item {
            Text(
                text = "Recent Supervisor Feedback on Record",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = LeapNavyPrimary
                )
            )
        }

        if (feedbacks.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No saved supervisor feedback found yet. You can paste custom feedback above for instant AI analysis!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        } else {
            items(feedbacks) { fb ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Week ${fb.weekNumber} Feedback • ${fb.targetType}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LeapNavyPrimary
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = fb.status,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF166534),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "\"${fb.content}\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onAnalyze(fb.content, 4)
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = LeapNavyPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyze with Gemini", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KnowledgeBaseDirectoryView(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: LeapKnowledgeCategory?,
    onSelectCategory: (LeapKnowledgeCategory?) -> Unit,
    onAskInChat: (String) -> Unit
) {
    val items: List<LeapFaqItem> = remember(searchQuery, selectedCategory) {
        val filteredByCategory = if (selectedCategory == null) {
            LeapKnowledgeBase.faqList
        } else {
            LeapKnowledgeBase.getByCategory(selectedCategory)
        }

        if (searchQuery.isBlank()) {
            filteredByCategory
        } else {
            val q = searchQuery.lowercase()
            filteredByCategory.filter { item ->
                item.question.lowercase().contains(q) ||
                item.answer.lowercase().contains(q) ||
                item.tags.any { tag -> tag.lowercase().contains(q) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search LEAP procedures, forms, rubrics...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("kb_search_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onSelectCategory(null) },
                    label = { Text("All (${LeapKnowledgeBase.faqList.size})", fontSize = 11.sp) }
                )
            }
            items(LeapKnowledgeCategory.values()) { cat ->
                val count = LeapKnowledgeBase.getByCategory(cat).size
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onSelectCategory(if (selectedCategory == cat) null else cat) },
                    label = { Text("${cat.displayName.take(18)} ($count)", fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Showing ${items.size} verified LEAP knowledge item(s)",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Items List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(items = items, key = { it.id }) { item ->
                KnowledgeBaseCard(item = item, onAskInChat = onAskInChat)
            }
        }
    }
}

@Composable
private fun KnowledgeBaseCard(
    item: LeapFaqItem,
    onAskInChat: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("kb_card_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = item.category.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.question,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.answer,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                ),
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onAskInChat(item.question) },
                        modifier = Modifier.testTag("ask_ai_btn_${item.id}")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = LeapNavyPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ask in Gemini Chat", fontSize = 11.sp, color = LeapNavyPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
