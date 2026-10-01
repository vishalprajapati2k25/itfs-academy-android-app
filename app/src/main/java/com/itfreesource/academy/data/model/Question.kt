package com.itfreesource.academy.data.model

import java.security.MessageDigest

/**
 * Question — Protected gamified question entity.
 * Uses SHA-256 hashed answers to prevent plain-text extraction from APKs or memory dumps.
 */
data class Question(
    val id: String,
    val lessonId: String,
    val prompt: String,
    val type: QuestionType,
    val options: List<String>,
    val correctAnswerHash: String, // SHA-256 hash
    val codeSnippet: String? = null,
    val explanationHint: String = "",
    val scrambleTokens: List<String> = emptyList() // For code reordering game mode
) {
    /**
     * Client-side evaluation against SHA-256 hash.
     */
    fun isCorrectAnswer(input: String): Boolean {
        val clean = input.trim().lowercase()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(clean.toByteArray(Charsets.UTF_8))
        val hex = digest.joinToString("") { String.format("%02x", it) }
        return hex == correctAnswerHash.lowercase()
    }
}

enum class QuestionType {
    MULTIPLE_CHOICE, // Standard card choice
    CODE_SCRAMBLE,   // Reorder code tokens/lines
    TRUE_FALSE,      // Binary concept check
    FILL_IN_BLANK    // Chip selection
}

data class QuizSubmissionResult(
    val isCorrect: Boolean,
    val userScore: Int,
    val xpEarned: Int,
    val gemsEarned: Int,
    val heartsRemaining: Int,
    val newStreak: Int,
    val explanation: String
)
