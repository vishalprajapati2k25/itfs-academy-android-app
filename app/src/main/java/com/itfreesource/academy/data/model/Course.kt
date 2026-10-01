package com.itfreesource.academy.data.model

/**
 * Course — Professional Academy Engineering Track.
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
    val progressPercent: Float = 0.0f,
    val difficulty: String = "Intermediate",
    val targetCompanies: List<String> = listOf("Google", "Meta", "Amazon", "Uber"),
    val interviewWeight: String = "High Frequency"
)

enum class CourseCategory(val displayName: String) {
    AI_AGENTIC("AI & Agentic Systems"),
    SECURITY("Application Security & DevSecOps"),
    QUALITY_ENGINEERING("Quality Engineering & E2E"),
    BACKEND_LANGUAGES("Languages & Systems Internals"),
    CAREER_NAVIGATION("FAANG Staff+ System Design")
}
