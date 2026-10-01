package com.itfreesource.academy.data.model

/**
 * Course — High-level Academy Course representation.
 */
data class Course(
    val id: String,
    val title: String,
    val category: CourseCategory,
    val iconEmoji: String,
    val description: String,
    val levelsCount: Int,
    val xpReward: Int,
    val isSubscribed: Boolean = false,
    val progressPercent: Float = 0.0f
)

enum class CourseCategory(val displayName: String) {
    AI_AGENTIC("AI & Agentic Engineering"),
    SECURITY("Application Security & Hacking"),
    QUALITY_ENGINEERING("Quality Engineering & E2E"),
    BACKEND_LANGUAGES("Languages & Distributed Systems"),
    CAREER_NAVIGATION("FAANG & Career Navigation")
}
