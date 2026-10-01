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
import com.itfreesource.academy.ui.components.CompanyBadge
import com.itfreesource.academy.ui.components.RoleLevelBadge
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

    val filtered = courses.filter { c ->
        val matchesCategory = selectedCategory == null || c.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() || c.title.contains(searchQuery, ignoreCase = true) || c.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isLight) SlateLightBg else ObsidianDarkBg),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        item {
            Text(
                text = "ENGINEERING TRACKS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Subscribe to Academy Tracks",
                style = MaterialTheme.typography.h2,
                color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
            )
            Text(
                text = "Curricula engineered for technical excellence and FAANG staff interview standards.",
                fontSize = 13.sp,
                color = ObsidianTextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search tracks (e.g. Python, Agentic, AppSec...)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = ObsidianTextMuted) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    val isAllSelected = selectedCategory == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isAllSelected) BrandIndigo else if (isLight) SlateCardBg else ObsidianCardBg)
                            .border(1.dp, if (isAllSelected) BrandIndigo else if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedCategory = null }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "All Tracks",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isAllSelected) Color.White else ObsidianTextSecondary
                        )
                    }
                }
                items(CourseCategory.values()) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) BrandIndigo else if (isLight) SlateCardBg else ObsidianCardBg)
                            .border(1.dp, if (isSelected) BrandIndigo else if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedCategory = if (selectedCategory == cat) null else cat }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat.displayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else ObsidianTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(filtered) { course ->
            val isActive = course.id == activeCourseId

            Card(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                shape = RoundedCornerShape(18.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        width = if (isActive) 1.5.dp else 1.dp,
                        color = if (isActive) BrandIndigo else if (isLight) SlateBorder else ObsidianBorder,
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = course.iconEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = course.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                                )
                                Text(
                                    text = course.category.displayName,
                                    fontSize = 12.sp,
                                    color = BrandIndigo,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BrandIndigo.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandIndigo
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = course.description,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = if (isLight) SlateTextSecondary else ObsidianTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Target Companies row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(course.targetCompanies) { comp ->
                                CompanyBadge(company = comp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Subscribe / Unsubscribe Toggle
                        Button(
                            onClick = { onToggleSubscribe(course.id) },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (course.isSubscribed) Color(0xFF1E293B) else BrandIndigo
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (course.isSubscribed) "SUBSCRIBED" else "SUBSCRIBE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Study Action
                        if (!isActive) {
                            OutlinedButton(
                                onClick = {
                                    if (!course.isSubscribed) onToggleSubscribe(course.id)
                                    onSelectActiveCourse(course.id)
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandIndigo),
                                modifier = Modifier.weight(0.9f)
                            ) {
                                Text(
                                    text = "STUDY TRACK",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandIndigo
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
