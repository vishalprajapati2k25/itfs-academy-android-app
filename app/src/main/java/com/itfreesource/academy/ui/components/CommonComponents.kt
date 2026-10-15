package com.itfreesource.academy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.CareerStream
import com.itfreesource.academy.data.model.UserProgress
import com.itfreesource.academy.ui.theme.*

@Composable
fun AppTopBar(
    progress: UserProgress,
    currentCourseTitle: String,
    currentCourseEmoji: String,
    onCourseClick: () -> Unit,
    onSecurityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight
    val bg = if (isLight) SlateCardBg else ObsidianCardBg
    val borderCol = if (isLight) SlateBorder else ObsidianBorder

    Surface(
        color = bg,
        elevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderCol)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Active Track / Stream Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onCourseClick() }
                        .background(if (isLight) SlateElevatedBg else ObsidianElevatedBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = currentCourseEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentCourseTitle.length > 16) currentCourseTitle.take(14) + "…" else currentCourseTitle,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                    )
                }

                // Stats & Readiness Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Readiness Score Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandIndigo.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(text = "🎯", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${progress.interviewReadinessScore}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = BrandIndigo
                        )
                    }

                    // Streak Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(WarningAmber.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(text = "🔥", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${progress.streakDays}d",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = WarningAmber
                        )
                    }

                    // DRM Security Shield Icon
                    IconButton(
                        onClick = onSecurityClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security Audit",
                            tint = SuccessEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * ContinueLearningCard — Prominent Hero card allowing candidate to resume exactly where they left off.
 */
@Composable
fun ContinueLearningCard(
    streamTitle: String,
    lessonTitle: String,
    subTopicTitle: String,
    stepNumber: Int,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight
    Card(
        backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
        shape = RoundedCornerShape(20.dp),
        elevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, BrandIndigo, RoundedCornerShape(20.dp))
            .clickable { onResumeClick() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⚡ RESUME LEARNING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningAmber,
                        letterSpacing = 1.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandIndigo.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = streamTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandIndigo
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = lessonTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$subTopicTitle • Concept Step $stepNumber in progress",
                fontSize = 12.sp,
                color = if (isLight) SlateTextSecondary else ObsidianTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onResumeClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = BrandIndigo),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Resume",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CONTINUE WHERE YOU LEFT OFF",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * CareerStreamTile — High-contrast, tactile career stream card.
 */
@Composable
fun CareerStreamTile(
    stream: CareerStream,
    isEnrolled: Boolean = false,
    progressPercent: Float = 0f,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight
    val accentColor = Color(stream.colorHex)

    Card(
        backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
        shape = RoundedCornerShape(18.dp),
        elevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isEnrolled) SuccessEmerald.copy(alpha = 0.6f) else if (isLight) SlateBorder else ObsidianBorder,
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stream.iconEmoji, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stream.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                        )
                        Text(
                            text = stream.shortCode,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = accentColor
                        )
                    }
                }

                if (isEnrolled) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(SuccessEmerald.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ENROLLED ✓",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = SuccessEmerald
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${stream.readinessPercent}% Ready",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = accentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stream.tagline,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stream.description,
                fontSize = 12.sp,
                color = if (isLight) SlateTextSecondary else ObsidianTextSecondary,
                maxLines = 2
            )

            if (isEnrolled) {
                Spacer(modifier = Modifier.height(12.dp))
                val pctInt = (progressPercent * 100).toInt()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE PROGRESS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessEmerald,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "$pctInt% COMPLETED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = progressPercent,
                    color = SuccessEmerald,
                    backgroundColor = if (isLight) SlateBorder else ObsidianBorder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BadgePill(text = "📚 ${stream.modulesCount} Topics")
                    BadgePill(text = "💼 ${stream.faangQuestionsCount} FAANG Qs", highlight = true)
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Explore",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun BadgePill(text: String, highlight: Boolean = false) {
    val isLight = MaterialTheme.colors.isLight
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (highlight) BrandIndigo.copy(alpha = 0.15f)
                else (if (isLight) SlateElevatedBg else ObsidianElevatedBg)
            )
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) BrandIndigo else (if (isLight) SlateTextSecondary else ObsidianTextSecondary)
        )
    }
}

@Composable
fun CodeBlock(
    code: String,
    language: String = "python",
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
                Text(
                    text = "🔒 SCREEN PROTECTED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ObsidianTextMuted
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = code.trim(),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
fun CompanyBadge(
    company: String,
    modifier: Modifier = Modifier
) {
    val (bg, textColor) = when {
        company.contains("Meta", ignoreCase = true) -> Pair(Color(0xFF0668E1).copy(alpha = 0.15f), Color(0xFF3B82F6))
        company.contains("Google", ignoreCase = true) -> Pair(SuccessEmerald.copy(alpha = 0.15f), SuccessEmerald)
        company.contains("Amazon", ignoreCase = true) -> Pair(WarningAmber.copy(alpha = 0.15f), WarningAmber)
        company.contains("Netflix", ignoreCase = true) -> Pair(ErrorRose.copy(alpha = 0.15f), ErrorRose)
        else -> Pair(AccentViolet.copy(alpha = 0.15f), AccentViolet)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = company,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun RoleLevelBadge(
    roleLevel: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = roleLevel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = ObsidianTextSecondary
        )
    }
}
