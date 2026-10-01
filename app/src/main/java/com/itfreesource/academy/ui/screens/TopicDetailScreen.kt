package com.itfreesource.academy.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.*
import com.itfreesource.academy.ui.components.CodeBlock
import com.itfreesource.academy.ui.components.CompanyBadge
import com.itfreesource.academy.ui.components.RoleLevelBadge
import com.itfreesource.academy.ui.theme.*

enum class TopicTab(val title: String, val icon: String) {
    CONCEPT("1. Concept Deep Dive", "📖"),
    MCQ("2. Practice MCQs", "🎯"),
    INTERVIEW("3. FAANG Long Answer", "💼")
}

@Composable
fun TopicDetailScreen(
    topicContent: TopicContent,
    onBack: () -> Unit,
    onVerifyMcq: (Question, Int) -> QuizSubmissionResult,
    onMarkStepComplete: (Int) -> Unit,
    onMarkInterviewReviewed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight
    var selectedTab by remember { mutableStateOf(TopicTab.CONCEPT) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isLight) SlateCardBg else ObsidianCardBg)
                    .border(1.dp, if (isLight) SlateBorder else ObsidianBorder)
            ) {
                // Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = topicContent.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                        )
                        Text(
                            text = "Step-by-Step Masterclass & Interview Prep",
                            fontSize = 11.sp,
                            color = ObsidianTextMuted
                        )
                    }
                }

                // 3-Pillar Tab Row
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    backgroundColor = Color.Transparent,
                    contentColor = BrandIndigo,
                    divider = {}
                ) {
                    TopicTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    text = "${tab.icon} ${tab.title}",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) BrandIndigo else ObsidianTextSecondary
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { paddingVals ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingVals)
                .background(if (isLight) SlateLightBg else ObsidianDarkBg)
        ) {
            when (selectedTab) {
                TopicTab.CONCEPT -> ConceptTabContent(
                    topic = topicContent,
                    onMarkStepComplete = onMarkStepComplete
                )
                TopicTab.MCQ -> McqTabContent(
                    mcqs = topicContent.mcqs,
                    onVerifyMcq = onVerifyMcq
                )
                TopicTab.INTERVIEW -> InterviewTabContent(
                    interviewQuestions = topicContent.interviewQuestions,
                    onMarkReviewed = onMarkInterviewReviewed
                )
            }
        }
    }
}

// ============================================================================
// TAB 1: STEP-BY-STEP CONCEPT DEEP DIVE
// ============================================================================

@Composable
private fun ConceptTabContent(
    topic: TopicContent,
    onMarkStepComplete: (Int) -> Unit
) {
    val isLight = MaterialTheme.colors.isLight

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        // Topic Overview Card
        item {
            Card(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                shape = RoundedCornerShape(16.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Architectural Overview",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = BrandIndigo
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = topic.overview,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = if (isLight) SlateTextSecondary else ObsidianTextSecondary
                    )
                }
            }
        }

        // Sequential Concept Steps
        itemsIndexed(topic.conceptSteps) { index, step ->
            var isCompleted by remember { mutableStateOf(false) }

            Card(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                shape = RoundedCornerShape(16.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = step.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                        )

                        if (isCompleted) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = SuccessEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Done",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessEmerald
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = step.summary,
                        fontSize = 13.sp,
                        color = ObsidianTextMuted,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = step.detailedExplanation,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = if (isLight) SlateTextSecondary else ObsidianTextSecondary
                    )

                    // Architectural Diagram
                    if (!step.architecturalDiagram.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            backgroundColor = Color(0xFF0F172A),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📐 SYSTEM TOPOLOGY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = step.architecturalDiagram,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }

                    // Code Snippet
                    if (!step.codeSnippet.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        CodeBlock(code = step.codeSnippet, language = step.codeLanguage)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Key Takeaway Callout Card
                    Card(
                        backgroundColor = BrandIndigo.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandIndigo.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚡", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Takeaway: ${step.keyTakeaway}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mark Complete Button
                    if (!isCompleted) {
                        Button(
                            onClick = {
                                isCompleted = true
                                onMarkStepComplete(step.stepNumber)
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = BrandIndigo),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "MARK STEP COMPLETE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 2: INSTANT MCQS (CONCEPT TEST)
// ============================================================================

@Composable
private fun McqTabContent(
    mcqs: List<Question>,
    onVerifyMcq: (Question, Int) -> QuizSubmissionResult
) {
    val isLight = MaterialTheme.colors.isLight

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        itemsIndexed(mcqs) { qIndex, question ->
            var selectedIdx by remember { mutableStateOf<Int?>(null) }
            var submission by remember { mutableStateOf<QuizSubmissionResult?>(null) }

            Card(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                shape = RoundedCornerShape(16.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompanyBadge(company = question.companyTag)
                        RoleLevelBadge(roleLevel = question.difficulty)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${qIndex + 1}. ${question.prompt}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Options list
                    question.options.forEachIndexed { optIdx, optText ->
                        val isSelected = selectedIdx == optIdx
                        val isCorrectOpt = submission != null && optIdx == question.correctOptionIndex
                        val isWrongSelected = submission != null && isSelected && !submission!!.isCorrect

                        val (cardBg, borderCol) = when {
                            isCorrectOpt -> Pair(SuccessEmeraldLight.copy(alpha = 0.2f), SuccessEmerald)
                            isWrongSelected -> Pair(ErrorRoseLight.copy(alpha = 0.2f), ErrorRose)
                            isSelected -> Pair(BrandIndigo.copy(alpha = 0.15f), BrandIndigo)
                            else -> Pair(
                                if (isLight) SlateElevatedBg else ObsidianElevatedBg,
                                if (isLight) SlateBorder else ObsidianBorder
                            )
                        }

                        Card(
                            backgroundColor = cardBg,
                            shape = RoundedCornerShape(12.dp),
                            elevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                                .clickable(enabled = submission == null) {
                                    selectedIdx = optIdx
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) BrandIndigo else Color(0xFF334155)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${('A' + optIdx)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = optText,
                                    fontSize = 14.sp,
                                    color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Check button or Feedback
                    if (submission == null) {
                        Button(
                            onClick = {
                                if (selectedIdx != null) {
                                    submission = onVerifyMcq(question, selectedIdx!!)
                                }
                            },
                            enabled = selectedIdx != null,
                            colors = ButtonDefaults.buttonColors(backgroundColor = BrandIndigo),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "CHECK ANSWER",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    } else {
                        val correct = submission!!.isCorrect
                        Card(
                            backgroundColor = if (correct) SuccessEmeraldLight.copy(alpha = 0.15f) else ErrorRoseLight.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if (correct) SuccessEmerald else ErrorRose, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (correct) "✓ Correct Answer!" else "✗ Common Interview Trap",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (correct) SuccessEmeraldDark else ErrorRose
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = submission!!.explanation,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp,
                                    color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 3: FAANG INTERVIEW LONG ANSWERS (CRACK THE INTERVIEW)
// ============================================================================

@Composable
private fun InterviewTabContent(
    interviewQuestions: List<InterviewQuestion>,
    onMarkReviewed: (String) -> Unit
) {
    val isLight = MaterialTheme.colors.isLight

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        items(interviewQuestions) { q ->
            var isRevealed by remember { mutableStateOf(false) }
            var isReviewed by remember { mutableStateOf(false) }

            Card(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                shape = RoundedCornerShape(16.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompanyBadge(company = q.targetCompany)
                        RoleLevelBadge(roleLevel = q.roleLevel)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = q.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        lineHeight = 24.sp,
                        color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = q.problemStatement,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = if (isLight) SlateTextSecondary else ObsidianTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Key Talking Points (Checklist before revealing answer)
                    Text(
                        text = "🎯 MUST-HIT TALKING POINTS IN INTERVIEW:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningAmber
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    q.keyTalkingPoints.forEach { point ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "•", color = WarningAmber, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = point,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Model Answer Accordion
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isRevealed) BrandIndigo.copy(alpha = 0.15f) else Color(0xFF1E293B))
                            .clickable { isRevealed = !isRevealed }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isRevealed) "HIDE MODEL ANSWER" else "REVEAL FAANG MODEL ANSWER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRevealed) BrandIndigo else Color.White
                        )
                        Icon(
                            imageVector = if (isRevealed) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = if (isRevealed) BrandIndigo else Color.White
                        )
                    }

                    AnimatedVisibility(visible = isRevealed) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            Card(
                                backgroundColor = Color(0xFF0F172A),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = q.modelAnswer,
                                        fontSize = 13.sp,
                                        lineHeight = 21.sp,
                                        color = Color(0xFFE2E8F0)
                                    )

                                    if (!q.codeSolution.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        CodeBlock(code = q.codeSolution, language = q.codeLanguage)
                                    }
                                }
                            }

                            // Follow up gotchas
                            if (q.followUpQuestions.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "💡 INTERVIEWER FOLLOW-UP PROMPTS:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                q.followUpQuestions.forEach { fq ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(text = "→", color = AccentCyan, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = fq,
                                            fontSize = 13.sp,
                                            lineHeight = 19.sp,
                                            color = if (isLight) SlateTextSecondary else ObsidianTextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Mark Reviewed Button
                            if (!isReviewed) {
                                Button(
                                    onClick = {
                                        isReviewed = true
                                        onMarkReviewed(q.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = SuccessEmerald),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "✓ MARK INTERVIEW Q&A REVIEWED (+2% Readiness)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            } else {
                                Text(
                                    text = "✓ Reviewed & added to interview readiness profile",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessEmerald
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
