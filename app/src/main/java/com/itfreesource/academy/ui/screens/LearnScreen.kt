package com.itfreesource.academy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.CareerStream
import com.itfreesource.academy.data.model.StreamSubTopic
import com.itfreesource.academy.data.model.UserProgress
import com.itfreesource.academy.ui.components.BadgePill
import com.itfreesource.academy.ui.components.CareerStreamTile
import com.itfreesource.academy.ui.components.ContinueLearningCard
import com.itfreesource.academy.ui.theme.*

@Composable
fun LearnScreen(
    careerStreams: List<CareerStream>,
    selectedStream: CareerStream?,
    progress: UserProgress,
    onSelectStream: (CareerStream) -> Unit,
    onBackToStreams: () -> Unit,
    onOpenSubTopic: (StreamSubTopic) -> Unit,
    onResumeLearning: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isLight) SlateLightBg else ObsidianDarkBg),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // SCENARIO A: STREAM DETAIL VIEW (Inside SDET, Developer, Security, Programming)
        if (selectedStream != null) {
            item {
                // Back Button to all Streams
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onBackToStreams() }
                        .padding(vertical = 6.dp, horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to streams",
                        tint = BrandIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "All Career Streams",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandIndigo
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stream Hero Header Card
                val accentColor = Color(selectedStream.colorHex)
                Card(
                    backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                    shape = RoundedCornerShape(20.dp),
                    elevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(accentColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = selectedStream.iconEmoji, fontSize = 26.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = selectedStream.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                                    )
                                    Text(
                                        text = selectedStream.tagline,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = accentColor
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = selectedStream.description,
                            fontSize = 13.sp,
                            color = if (isLight) SlateTextSecondary else ObsidianTextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BadgePill(text = "🎯 ${selectedStream.readinessPercent}% Stream Readiness", highlight = true)
                            BadgePill(text = "📚 ${selectedStream.modulesCount} Modules")
                            BadgePill(text = "💼 ${selectedStream.faangQuestionsCount} FAANG Qs")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section title
                Text(
                    text = "SELECT TOPIC TO PREPARE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ObsidianTextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Sub-topics list
            items(selectedStream.subTopics) { subTopic ->
                SubTopicCard(
                    subTopic = subTopic,
                    accentColor = Color(selectedStream.colorHex),
                    onClick = { onOpenSubTopic(subTopic) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        } else {
            // SCENARIO B: HOME STREAMS VIEW (Top tiles + Continue where you left off)

            // 1. Resume Learning / Continue where left off hero card
            if (progress.lastActiveLessonId != null) {
                item {
                    ContinueLearningCard(
                        streamTitle = progress.lastActiveStreamTitle,
                        lessonTitle = progress.lastActiveLessonTitle,
                        subTopicTitle = progress.lastActiveSubTopicTitle,
                        stepNumber = progress.lastActiveStepNumber,
                        onResumeClick = onResumeLearning
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // 2. Section Header
            item {
                Column {
                    Text(
                        text = "ENGINEERING CAREER STREAMS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandIndigo,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose Your Target Role",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                    )
                    Text(
                        text = "Select your discipline to access curated topic breakdowns, interactive MCQs, and FAANG interview questions.",
                        fontSize = 13.sp,
                        color = if (isLight) SlateTextSecondary else ObsidianTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Career Stream Tiles
            items(careerStreams) { stream ->
                CareerStreamTile(
                    stream = stream,
                    onClick = { onSelectStream(stream) }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun SubTopicCard(
    subTopic: StreamSubTopic,
    accentColor: Color,
    onClick: () -> Unit
) {
    val isLight = MaterialTheme.colors.isLight

    Card(
        backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = subTopic.iconEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = subTopic.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                        )
                        Text(
                            text = subTopic.targetRole,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = accentColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${subTopic.estMinutes}m",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subTopic.description,
                fontSize = 12.sp,
                color = if (isLight) SlateTextSecondary else ObsidianTextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BadgePill(text = "📖 Concepts")
                    BadgePill(text = "🎯 ${subTopic.mcqCount} MCQs")
                    BadgePill(text = "💼 ${subTopic.faangQuestionsCount} FAANG Qs", highlight = true)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "START",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Start Topic",
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
