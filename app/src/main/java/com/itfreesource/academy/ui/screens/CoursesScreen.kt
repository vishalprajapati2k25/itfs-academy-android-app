package com.itfreesource.academy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.Course
import com.itfreesource.academy.data.model.CourseCategory
import com.itfreesource.academy.ui.components.DuolingoButton
import com.itfreesource.academy.ui.components.DuoButtonVariant
import com.itfreesource.academy.ui.theme.*

@Composable
fun CoursesScreen(
    courses: List<Course>,
    activeCourseId: String,
    onToggleSubscribe: (String) -> Unit,
    onSelectActiveCourse: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CourseCategory?>(null) }

    val filteredCourses = courses.filter { course ->
        val matchesSearch = searchQuery.isBlank() ||
                course.title.contains(searchQuery, ignoreCase = true) ||
                course.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || course.category == selectedCategory
        matchesSearch && matchesCategory
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isLight) DuoGrayBackground else DuoDarkBackground),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        item {
            Text(
                text = "ACADEMY CATALOG",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = DuoBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Subscribe to Courses",
                style = MaterialTheme.typography.h2,
                color = if (isLight) DuoDarkText else DuoDarkTextPrimary
            )
            Text(
                text = "Select what you want to master in Duolingo-style micro-quests.",
                fontSize = 14.sp,
                color = if (isLight) DuoGrayText else DuoDarkTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Search Input Field
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search courses (e.g. Python, Agentic, AppSec...)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = DuoGrayText) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = if (isLight) DuoWhite else DuoDarkSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, if (isLight) DuoGrayBorder else DuoDarkBorder, RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    CategoryChip(
                        title = "All",
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                items(CourseCategory.values()) { cat ->
                    CategoryChip(
                        title = cat.displayName,
                        isSelected = selectedCategory == cat,
                        onClick = { selectedCategory = if (selectedCategory == cat) null else cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Course Cards
        items(filteredCourses) { course ->
            val isActive = course.id == activeCourseId

            Card(
                backgroundColor = if (isLight) DuoWhite else DuoDarkSurface,
                shape = RoundedCornerShape(20.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        width = if (isActive) 2.5.dp else 1.5.dp,
                        color = if (isActive) DuoGreen else if (isLight) DuoGrayBorder else DuoDarkBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = course.iconEmoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = course.title,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = if (isLight) DuoDarkText else DuoDarkTextPrimary
                                )
                                Text(
                                    text = course.category.displayName,
                                    fontSize = 12.sp,
                                    color = DuoBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DuoGreenLight)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DuoGreenDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = course.description,
                        fontSize = 13.sp,
                        color = if (isLight) DuoGrayText else DuoDarkTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "🎯 ${course.levelsCount} Lessons",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLight) DuoDarkText else DuoDarkTextPrimary
                        )
                        Text(
                            text = "⚡ +${course.xpReward} XP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DuoOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Subscribe / Unsubscribe Toggle Button
                        DuolingoButton(
                            text = if (course.isSubscribed) "Subscribed" else "Subscribe",
                            onClick = { onToggleSubscribe(course.id) },
                            variant = if (course.isSubscribed) DuoButtonVariant.OUTLINE else DuoButtonVariant.SECONDARY,
                            height = 44.dp,
                            modifier = Modifier.weight(1f)
                        )

                        // Set Active Quest Button
                        if (!isActive) {
                            DuolingoButton(
                                text = "Learn",
                                onClick = {
                                    if (!course.isSubscribed) {
                                        onToggleSubscribe(course.id)
                                    }
                                    onSelectActiveCourse(course.id)
                                },
                                variant = DuoButtonVariant.PRIMARY,
                                height = 44.dp,
                                modifier = Modifier.weight(0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isLight = MaterialTheme.colors.isLight
    val chipBg = when {
        isSelected -> DuoBlue
        isLight -> DuoWhite
        else -> DuoDarkSurface
    }
    val textCol = when {
        isSelected -> DuoWhite
        isLight -> DuoDarkText
        else -> DuoDarkTextPrimary
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(chipBg)
            .border(1.5.dp, if (isSelected) DuoBlue else if (isLight) DuoGrayBorder else DuoDarkBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textCol
        )
    }
}
