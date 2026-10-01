package com.itfreesource.academy.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
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
import com.itfreesource.academy.ui.components.DuolingoButton
import com.itfreesource.academy.ui.components.DuoButtonVariant
import com.itfreesource.academy.ui.theme.*

@Composable
fun QuizPlayScreen(
    lessonNode: LessonNode,
    questions: List<Question>,
    hearts: Int,
    onAnswerSubmit: (Question, String) -> QuizSubmissionResult,
    onLessonFinished: (Int, Int) -> Unit, // stars, xp
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var scrambledTokensOrder by remember { mutableStateOf<List<String>>(emptyList()) }
    var submissionResult by remember { mutableStateOf<QuizSubmissionResult?>(null) }
    var isCheckingAnswer by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }
    var totalXpEarned by remember { mutableStateOf(0) }
    var wrongAnswersCount by remember { mutableStateOf(0) }

    val currentQuestion = questions.getOrNull(currentIndex)

    // Reset selection when question changes
    LaunchedEffect(currentIndex) {
        selectedOption = null
        submissionResult = null
        isCheckingAnswer = false
        currentQuestion?.let {
            if (it.type == QuestionType.CODE_SCRAMBLE) {
                scrambledTokensOrder = it.scrambleTokens.shuffled()
            }
        }
    }

    if (isCompleted || currentQuestion == null) {
        // Celebration Screen
        val stars = when (wrongAnswersCount) {
            0 -> 3
            1 -> 2
            else -> 1
        }
        LessonCelebrationView(
            stars = stars,
            totalXp = totalXpEarned + lessonNode.xpValue,
            onContinue = {
                onLessonFinished(stars, totalXpEarned + lessonNode.xpValue)
            }
        )
        return
    }

    val progressRatio = (currentIndex.toFloat()) / questions.size.coerceAtLeast(1)

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isLight) DuoWhite else DuoDarkSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onExit) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Quiz",
                        tint = if (isLight) DuoDarkText else DuoDarkTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = progressRatio,
                    color = DuoGreen,
                    backgroundColor = if (isLight) DuoGrayBorder else DuoDarkBorder,
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Hearts
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Hearts",
                        tint = DuoRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$hearts",
                        fontWeight = FontWeight.Black,
                        color = DuoRed,
                        fontSize = 15.sp
                    )
                }
            }
        },
        bottomBar = {
            // Check / Feedback bottom bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        when {
                            submissionResult?.isCorrect == true -> DuoGreenLight
                            submissionResult?.isCorrect == false -> DuoRedLight
                            else -> if (isLight) DuoWhite else DuoDarkSurface
                        }
                    )
                    .padding(16.dp)
            ) {
                if (submissionResult == null) {
                    DuolingoButton(
                        text = "Check",
                        enabled = selectedOption != null || currentQuestion.type == QuestionType.CODE_SCRAMBLE,
                        onClick = {
                            val answerToCheck = if (currentQuestion.type == QuestionType.CODE_SCRAMBLE) {
                                scrambledTokensOrder.joinToString("\n")
                            } else {
                                selectedOption ?: ""
                            }
                            val res = onAnswerSubmit(currentQuestion, answerToCheck)
                            submissionResult = res
                            if (res.isCorrect) {
                                totalXpEarned += res.xpEarned
                            } else {
                                wrongAnswersCount++
                            }
                        },
                        variant = DuoButtonVariant.PRIMARY,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    val correct = submissionResult!!.isCorrect
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (correct) "🎉 Amazing! Correct!" else "❌ Not quite right",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = if (correct) DuoGreenDark else DuoRedDark
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = submissionResult!!.explanation,
                            fontSize = 14.sp,
                            color = if (correct) DuoGreenDark else DuoRedDark
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DuolingoButton(
                            text = "Continue",
                            onClick = {
                                if (currentIndex + 1 < questions.size) {
                                    currentIndex++
                                } else {
                                    isCompleted = true
                                }
                            },
                            variant = if (correct) DuoButtonVariant.PRIMARY else DuoButtonVariant.DANGER,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    ) { paddingVals ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .background(if (isLight) DuoGrayBackground else DuoDarkBackground)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Question ${currentIndex + 1} of ${questions.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DuoBlue,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentQuestion.prompt,
                    style = MaterialTheme.typography.h3,
                    color = if (isLight) DuoDarkText else DuoDarkTextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Optional code snippet block
                if (!currentQuestion.codeSnippet.isNullOrBlank()) {
                    Card(
                        backgroundColor = Color(0xFF1E1E1E),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = currentQuestion.codeSnippet,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = Color(0xFFD4D4D4),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Render options according to QuestionType
            when (currentQuestion.type) {
                QuestionType.MULTIPLE_CHOICE, QuestionType.TRUE_FALSE, QuestionType.FILL_IN_BLANK -> {
                    items(currentQuestion.options.size) { optIdx ->
                        val optText = currentQuestion.options[optIdx]
                        val isSelected = selectedOption == optText

                        val cardBg = when {
                            isSelected && isLight -> DuoBlueLight
                            isSelected && !isLight -> DuoDarkCard
                            isLight -> DuoWhite
                            else -> DuoDarkSurface
                        }
                        val borderCol = if (isSelected) DuoBlue else if (isLight) DuoGrayBorder else DuoDarkBorder

                        Card(
                            backgroundColor = cardBg,
                            shape = RoundedCornerShape(14.dp),
                            elevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .border(2.dp, borderCol, RoundedCornerShape(14.dp))
                                .clickable(enabled = submissionResult == null) {
                                    selectedOption = optText
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) DuoBlue else if (isLight) DuoGrayBackground else DuoDarkCard),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${optIdx + 1}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) DuoWhite else DuoGrayText
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = optText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isLight) DuoDarkText else DuoDarkTextPrimary
                                )
                            }
                        }
                    }
                }
                QuestionType.CODE_SCRAMBLE -> {
                    item {
                        Text(
                            text = "Arrange lines in correct logical order:",
                            fontSize = 13.sp,
                            color = DuoGrayText
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    items(scrambledTokensOrder.size) { tokenIdx ->
                        val token = scrambledTokensOrder[tokenIdx]
                        Card(
                            backgroundColor = if (isLight) DuoWhite else DuoDarkSurface,
                            shape = RoundedCornerShape(12.dp),
                            elevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.5.dp, DuoBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                text = token,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLight) DuoDarkText else DuoDarkTextPrimary,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonCelebrationView(
    stars: Int,
    totalXp: Int,
    onContinue: () -> Unit
) {
    val isLight = MaterialTheme.colors.isLight

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isLight) DuoWhite else DuoDarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "🏆", fontSize = 72.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Lesson Complete!",
                style = MaterialTheme.typography.h1,
                color = DuoGoldDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You're sharpening your engineering instincts.",
                fontSize = 15.sp,
                color = if (isLight) DuoGrayText else DuoDarkTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stars Row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(stars) {
                    Text(text = "⭐", fontSize = 36.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    backgroundColor = DuoOrangeLight,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 0.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "XP EARNED", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DuoOrangeDark)
                        Text(text = "+$totalXp", fontSize = 24.sp, fontWeight = FontWeight.Black, color = DuoOrangeDark)
                    }
                }
                Card(
                    backgroundColor = DuoBlueLight,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 0.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "STREAK", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DuoBlueDark)
                        Text(text = "+1 Day 🔥", fontSize = 20.sp, fontWeight = FontWeight.Black, color = DuoBlueDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            DuolingoButton(
                text = "Claim Rewards",
                onClick = onContinue,
                variant = DuoButtonVariant.GOLD,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
