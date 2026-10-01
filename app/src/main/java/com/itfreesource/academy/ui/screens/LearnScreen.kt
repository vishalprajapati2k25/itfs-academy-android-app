package com.itfreesource.academy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.School
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
import com.itfreesource.academy.ui.components.DuolingoButton
import com.itfreesource.academy.ui.components.DuoButtonVariant
import com.itfreesource.academy.ui.components.QuestPathNode
import com.itfreesource.academy.ui.theme.*

@Composable
fun LearnScreen(
    currentCourse: Course?,
    curriculum: List<CourseSection>,
    progress: UserProgress,
    onStartLesson: (LessonNode) -> Unit,
    onRefillHearts: () -> Unit,
    onOpenCourseCatalog: () -> Unit,
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
                Text(text = "🎓", fontSize = 54.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome to ITFS Academy!",
                    style = MaterialTheme.typography.h2,
                    color = if (isLight) DuoDarkText else DuoDarkTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Choose your first industry course to begin your gamified learning journey.",
                    style = MaterialTheme.typography.body1,
                    color = if (isLight) DuoGrayText else DuoDarkTextSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))
                DuolingoButton(
                    text = "Explore Courses",
                    onClick = onOpenCourseCatalog,
                    variant = DuoButtonVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isLight) DuoGrayBackground else DuoDarkBackground),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Zero Hearts Warning Banner
        if (progress.hearts <= 0) {
            item {
                Card(
                    backgroundColor = DuoRedLight,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💔", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Out of Hearts!",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = DuoRedDark
                                )
                                Text(
                                    text = "Refill with 20 💎 Brain Gems to play",
                                    fontSize = 13.sp,
                                    color = DuoRedDark
                                )
                            }
                        }
                        DuolingoButton(
                            text = "Refill",
                            onClick = onRefillHearts,
                            variant = DuoButtonVariant.DANGER,
                            height = 40.dp
                        )
                    }
                }
            }
        }

        // Sections and Zig-Zag Nodes
        curriculum.forEach { section ->
            // Section Header Card
            item {
                Card(
                    backgroundColor = DuoGreen,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = section.title.uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = DuoWhite,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "${currentCourse.iconEmoji} ${currentCourse.title.take(16)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DuoGreenLight
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = section.description,
                            fontSize = 13.sp,
                            color = DuoWhite.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Zig-zag nodes along the winding trail
            itemsIndexed(section.lessons) { index, node ->
                // Calculate horizontal sinusoidal offset for winding trail
                val offsets = listOf(0.dp, 55.dp, 0.dp, (-55).dp)
                val currentOffset = offsets[index % offsets.size]

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = currentOffset)
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        QuestPathNode(
                            node = node,
                            onClick = {
                                if (progress.hearts > 0) {
                                    onStartLesson(node)
                                } else {
                                    onRefillHearts()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
