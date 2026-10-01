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
    val activeCourseId: String = "agentic-engineering",
    val bookmarkedQuestionIds: Set<String> = emptySet(),
    val completedLessonIds: Set<String> = setOf("agentic-engineering_l1")
)

data class TrackSummary(
    val courseId: String,
    val title: String,
    val iconEmoji: String,
    val progressPercent: Float,
    val nextTopicTitle: String
)
