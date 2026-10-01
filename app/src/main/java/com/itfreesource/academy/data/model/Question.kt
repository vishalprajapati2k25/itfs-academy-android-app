package com.itfreesource.academy.data.model

import java.security.MessageDigest

/**
 * ConceptStep — Bite-sized, step-by-step deep dive into an architectural concept.
 */
data class ConceptStep(
    val stepNumber: Int,
    val title: String,
    val summary: String,
    val detailedExplanation: String,
    val codeSnippet: String? = null,
    val codeLanguage: String = "python",
    val architecturalDiagram: String? = null,
    val keyTakeaway: String
)

/**
 * Question — Multiple Choice Question for quick concept validation.
 */
data class Question(
    val id: String,
    val lessonId: String,
    val prompt: String,
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val options: List<String>,
    val correctAnswerHash: String, // SHA-256 hash (DRM security)
    val correctOptionIndex: Int = 0,
    val explanationHint: String = "",
    val distractorRationale: Map<Int, String> = emptyMap(),
    val difficulty: String = "Medium",
    val companyTag: String = "Google"
) {
    fun isCorrectAnswer(input: String): Boolean {
        val clean = input.trim().lowercase()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(clean.toByteArray(Charsets.UTF_8))
        val hex = digest.joinToString("") { String.format("%02x", it) }
        return hex == correctAnswerHash.lowercase()
    }
}

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    CODE_ANALYSIS
}

/**
 * InterviewQuestion — FAANG-style Long Answer format.
 * Teaches students how to structure answers for L5/L6/Staff+ interviews.
 */
data class InterviewQuestion(
    val id: String,
    val lessonId: String,
    val title: String,
    val targetCompany: String,
    val roleLevel: String, // e.g. "Senior / Staff Engineer (L5-L6)"
    val problemStatement: String,
    val timeEstimateMinutes: Int = 15,
    val keyTalkingPoints: List<String>,
    val modelAnswer: String,
    val codeSolution: String? = null,
    val codeLanguage: String = "python",
    val followUpQuestions: List<String> = emptyList(),
    val isBookmarked: Boolean = false
)

/**
 * TopicContent — Complete modular payload for a learning topic:
 * 1. Step-by-step concept breakdown
 * 2. Rapid-fire MCQs
 * 3. Deep FAANG Long Answer interview questions
 */
data class TopicContent(
    val lessonId: String,
    val title: String,
    val overview: String,
    val conceptSteps: List<ConceptStep>,
    val mcqs: List<Question>,
    val interviewQuestions: List<InterviewQuestion>
)

data class QuizSubmissionResult(
    val isCorrect: Boolean,
    val userScore: Int,
    val xpEarned: Int,
    val explanation: String
)
