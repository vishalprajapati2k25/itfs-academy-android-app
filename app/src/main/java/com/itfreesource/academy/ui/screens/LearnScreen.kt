package com.itfreesource.academy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.Course
import com.itfreesource.academy.data.model.CourseSection
import com.itfreesource.academy.data.model.LessonNode
import com.itfreesource.academy.data.model.UserProgress
import com.itfreesource.academy.ui.components.CompanyBadge
import com.itfreesource.academy.ui.components.RoleLevelBadge
import com.itfreesource.academy.ui.theme.*

@Composable
fun LearnScreen(
    currentCourse: Course?,
    curriculum: List<CourseSection>,
    progress: UserProgress,
    onOpenTopic: (LessonNode) -> Unit,
    onOpenCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight

    if (currentCourse == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(text = "🎓", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Active Engineering Track",
                    style = MaterialTheme.typography.h2,
                    color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select a track to begin step-by-step concept learning and FAANG interview preparation.",
                    fontSize = 14.sp,
                    color = ObsidianTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onOpenCatalog,
                    colors = ButtonDefaults.buttonColors(backgroundColor = BrandIndigo),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("EXPLORE TRACKS", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isLight) SlateLightBg else ObsidianDarkBg),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        // Active Track Hero Banner
        item {
            Card(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                shape = RoundedCornerShape(20.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .border(1.dp, BrandIndigo.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = currentCourse.iconEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = currentCourse.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                                )
                                Text(
                                    text = currentCourse.category.displayName,
                                    fontSize = 12.sp,
                                    color = BrandIndigo,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = currentCourse.description,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = if (isLight) SlateTextSecondary else ObsidianTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Companies row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "TARGETED FOR:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ObsidianTextMuted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(currentCourse.targetCompanies) { comp ->
                                CompanyBadge(company = comp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Track Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Track Readiness",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ObsidianTextMuted
                        )
                        Text(
                            text = "${(currentCourse.progressPercent * 100).toInt()}% Complete",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = currentCourse.progressPercent,
                        color = BrandIndigo,
                        backgroundColor = if (isLight) SlateBorder else ObsidianBorder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }

        // Section & Topic Cards
        curriculum.forEach { section ->
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = section.title.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandIndigo,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = section.description,
                        fontSize = 13.sp,
                        color = ObsidianTextMuted
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(section.lessons) { lesson ->
                Card(
                    backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(16.dp))
                        .clickable { onOpenTopic(lesson) }
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (lesson.isCompleted) SuccessEmerald.copy(alpha = 0.15f) else BrandIndigo.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (lesson.isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = SuccessEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${lesson.order}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = BrandIndigo
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = lesson.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = lesson.subtitle,
                                    fontSize = 12.sp,
                                    color = ObsidianTextSecondary,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Modalities pill badges
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    BadgePill(text = "📖 Concepts")
                                    BadgePill(text = "🎯 ${lesson.mcqCount} MCQs")
                                    BadgePill(text = "💼 ${lesson.interviewTopicsCount} FAANG Qs", highlight = true)
                                }
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Topic",
                            tint = ObsidianTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgePill(text: String, highlight: Boolean = false) {
    val isLight = MaterialTheme.colors.isLight
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (highlight) WarningAmber.copy(alpha = 0.12f) else if (isLight) SlateElevatedBg else ObsidianElevatedBg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (highlight) WarningAmber else ObsidianTextSecondary
        )
    }
}
