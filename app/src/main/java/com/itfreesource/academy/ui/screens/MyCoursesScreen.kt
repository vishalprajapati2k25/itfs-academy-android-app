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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.CareerStream
import com.itfreesource.academy.data.model.Course
import com.itfreesource.academy.data.model.UserProgress
import com.itfreesource.academy.ui.components.BadgePill
import com.itfreesource.academy.ui.theme.*

/**
 * MyCoursesScreen — Dedicated hub showing all enrolled career paths and courses
 * with live completion percentage (%) and instant continue learning triggers.
 */
@Composable
fun MyCoursesScreen(
    enrolledStreams: List<CareerStream>,
    enrolledCourses: List<Course>,
    progress: UserProgress,
    onContinueStream: (CareerStream) -> Unit,
    onContinueCourse: (Course) -> Unit,
    onUnenrollStream: (String) -> Unit,
    onExploreCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isLight) SlateLightBg else ObsidianDarkBg),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        // 1. Header Section
        item {
            Text(
                text = "MY ENROLLED PATHS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "My Courses & Progress",
                style = MaterialTheme.typography.h2,
                color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
            )
            Text(
                text = "Your active preparation tracks with live completion telemetry and instant continuation.",
                fontSize = 13.sp,
                color = if (isLight) SlateTextSecondary else ObsidianTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Telemetry Banner
            val totalEnrolled = enrolledStreams.size + enrolledCourses.size
            val avgProgress = if (enrolledStreams.isNotEmpty()) {
                val sum = enrolledStreams.sumOf { (progress.streamProgressMap[it.id] ?: 0f).toDouble() }
                (sum / enrolledStreams.size * 100).toInt()
            } else 0

            Card(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                shape = RoundedCornerShape(16.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalEnrolled",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandIndigo
                        )
                        Text(
                            text = "Active Tracks",
                            fontSize = 11.sp,
                            color = ObsidianTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(if (isLight) SlateBorder else ObsidianBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$avgProgress%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessEmerald
                        )
                        Text(
                            text = "Avg Progress",
                            fontSize = 11.sp,
                            color = ObsidianTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(if (isLight) SlateBorder else ObsidianBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${progress.conceptsMasteredCount}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber
                        )
                        Text(
                            text = "Mastered",
                            fontSize = 11.sp,
                            color = ObsidianTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 2. Empty State (if no enrolled courses)
        if (enrolledStreams.isEmpty() && enrolledCourses.isEmpty()) {
            item {
                Card(
                    backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                    shape = RoundedCornerShape(20.dp),
                    elevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BrandIndigo.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = BrandIndigo,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Enrolled Paths Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Enroll in specialized career paths (SDET, Developer, Security, Programming) to track live progress, verify concepts with MCQs, and prepare for FAANG interviews.",
                            fontSize = 13.sp,
                            color = if (isLight) SlateTextSecondary else ObsidianTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onExploreCatalog,
                            colors = ButtonDefaults.buttonColors(backgroundColor = BrandIndigo),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "EXPLORE CAREER PATHS",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        } else {
            // 3. Enrolled Career Streams List
            item {
                Text(
                    text = "ACTIVE CAREER PATHS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ObsidianTextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(enrolledStreams) { stream ->
                val progressPct = progress.streamProgressMap[stream.id] ?: 0f
                val pctInt = (progressPct * 100).toInt()
                val accentColor = Color(stream.colorHex)

                Card(
                    backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                    shape = RoundedCornerShape(18.dp),
                    elevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SuccessEmerald.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Title & Status Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
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

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
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
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stream.description,
                            fontSize = 12.sp,
                            color = if (isLight) SlateTextSecondary else ObsidianTextSecondary,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Progress Section with Percentage
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isLight) Color(0xFFF1F5F9) else Color(0xFF0F172A))
                                .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(SuccessEmerald)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "LIVE PROGRESS",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessEmerald,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Text(
                                        text = "$pctInt% COMPLETED",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SuccessEmerald
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LinearProgressIndicator(
                                    progress = progressPct,
                                    color = SuccessEmerald,
                                    backgroundColor = if (isLight) SlateBorder else Color(0xFF1E293B),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${stream.subTopics.size} Topic Modules Active",
                                        fontSize = 11.sp,
                                        color = ObsidianTextMuted
                                    )
                                    Text(
                                        text = "${stream.faangQuestionsCount} FAANG Onsite Qs",
                                        fontSize = 11.sp,
                                        color = ObsidianTextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Continue & Unenroll
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onContinueStream(stream) },
                                colors = ButtonDefaults.buttonColors(backgroundColor = BrandIndigo),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Continue",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CONTINUE LEARNING",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = { onUnenrollStream(stream.id) },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isLight) SlateBorder else ObsidianBorder),
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                Text(
                                    text = "Leave",
                                    fontSize = 12.sp,
                                    color = ObsidianTextMuted
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 4. Also show individual enrolled courses from catalog if any
            if (enrolledCourses.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ENROLLED CATALOG TRACKS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ObsidianTextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(enrolledCourses) { course ->
                    val courseProgressPct = (course.progressPercent ?: 0.35f)
                    val pctInt = (courseProgressPct * 100).toInt()

                    Card(
                        backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                        shape = RoundedCornerShape(18.dp),
                        elevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = course.iconEmoji, fontSize = 26.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = course.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                                        )
                                        Text(
                                            text = course.category.displayName,
                                            fontSize = 11.sp,
                                            color = BrandIndigo
                                        )
                                    }
                                }

                                Text(
                                    text = "$pctInt%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessEmerald
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = courseProgressPct,
                                color = SuccessEmerald,
                                backgroundColor = if (isLight) SlateBorder else ObsidianBorder,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onContinueCourse(course) },
                                colors = ButtonDefaults.buttonColors(backgroundColor = BrandIndigo),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "CONTINUE TRACK",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
