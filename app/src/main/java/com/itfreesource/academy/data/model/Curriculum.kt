package com.itfreesource.academy.data.model

/**
 * CourseSection — Thematic unit within an engineering track.
 */
data class CourseSection(
    val sectionId: String,
    val courseId: String,
    val title: String,
    val description: String,
    val order: Int,
    val lessons: List<LessonNode>
)

/**
 * LessonNode — Individual lesson module containing concept breakdown, MCQs, and interview Q&As.
 */
data class LessonNode(
    val id: String,
    val sectionId: String,
    val title: String,
    val subtitle: String = "Core Concepts & FAANG Questions",
    val order: Int,
    val estMinutes: Int = 15,
    val difficulty: String = "Medium",
    val interviewTopicsCount: Int = 2,
    val mcqCount: Int = 3,
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false
)
