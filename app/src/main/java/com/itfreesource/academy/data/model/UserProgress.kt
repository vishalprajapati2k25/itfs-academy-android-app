package com.itfreesource.academy.data.model

/**
 * UserProgress — Student interview readiness, mastery metrics, and streak tracking.
 */
data class UserProgress(
    val interviewReadinessScore: Int = 68, // Out of 100%
    val conceptsMasteredCount: Int = 14,
    val mcqsSolvedCount: Int = 42,
    val longAnswersReviewedCount: Int = 19,
    val streakDays: Int = 5,
    val totalStudyMinutes: Int = 320,
    val activeCourseId: String = "sdet",
    val lastActiveLessonId: String? = "sdet_playwright_l1",
    val lastActiveLessonTitle: String = "Playwright Network Mocking & Custom Fixtures",
    val lastActiveStreamTitle: String = "SDET & Automation",
    val lastActiveSubTopicTitle: String = "Playwright E2E Mastery",
    val lastActiveStepNumber: Int = 2,
    val enrolledStreamIds: Set<String> = setOf("sdet"),
    val enrolledCourseIds: Set<String> = setOf("playwright"),
    val streamProgressMap: Map<String, Float> = mapOf("sdet" to 0.40f, "developer" to 0.25f, "security" to 0.15f, "programming" to 0.10f),
    val bookmarkedQuestionIds: Set<String> = emptySet(),
    val completedLessonIds: Set<String> = setOf("sdet_playwright_l1")
)

data class TrackSummary(
    val courseId: String,
    val title: String,
    val iconEmoji: String,
    val progressPercent: Float,
    val nextTopicTitle: String
)
