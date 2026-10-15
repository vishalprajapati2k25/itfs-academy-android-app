package com.itfreesource.academy.data.model

/**
 * CareerStream — Top-level engineering persona / work stream.
 * e.g., SDET, Developer, Security, Programming.
 */
data class CareerStream(
    val id: String,
    val title: String,
    val shortCode: String,
    val iconEmoji: String,
    val tagline: String,
    val description: String,
    val modulesCount: Int,
    val faangQuestionsCount: Int,
    val readinessPercent: Int,
    val colorHex: Long = 0xFF6366F1,
    val subTopics: List<StreamSubTopic>
)

/**
 * StreamSubTopic — Specific preparation subject within a career stream.
 * e.g., inside SDET: Playwright, Selenium, LLM Testing, Coding Questions, PseudoCodes.
 */
data class StreamSubTopic(
    val id: String,
    val streamId: String,
    val title: String,
    val iconEmoji: String,
    val description: String,
    val targetRole: String = "Senior / Staff",
    val lessonId: String,
    val estMinutes: Int = 15,
    val mcqCount: Int = 3,
    val faangQuestionsCount: Int = 2,
    val isCompleted: Boolean = false
)
