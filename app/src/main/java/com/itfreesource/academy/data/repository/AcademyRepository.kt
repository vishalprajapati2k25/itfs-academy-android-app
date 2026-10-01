package com.itfreesource.academy.data.repository

import android.content.Context
import com.itfreesource.academy.data.api.AcademyApiClient
import com.itfreesource.academy.data.model.*
import com.itfreesource.academy.security.EncryptedDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AcademyRepository — Central reactive repository orchestrating:
 * - Student progress (streaks, XP, hearts, gems)
 * - Course subscriptions & catalog
 * - Dynamic quest paths & interactive questions
 * - Encrypted local vault sync
 */
class AcademyRepository(context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val secureStore = EncryptedDataStore(context)

    // User Progress StateFlow
    private val _userProgress = MutableStateFlow(
        UserProgress(
            streakDays = secureStore.getInt("user_streak", 3),
            totalXp = secureStore.getInt("user_xp", 340),
            brainGems = secureStore.getInt("user_gems", 85),
            hearts = secureStore.getInt("user_hearts", 5),
            activeCourseId = secureStore.getSecureString("active_course_id", "agentic-engineering")
        )
    )
    val userProgress: StateFlow<UserProgress> = _userProgress.asStateFlow()

    // Courses List StateFlow
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    // Active Curriculum StateFlow
    private val _curriculum = MutableStateFlow<List<CourseSection>>(emptyList())
    val curriculum: StateFlow<List<CourseSection>> = _curriculum.asStateFlow()

    // Current Lesson Questions StateFlow
    private val _activeQuestions = MutableStateFlow<List<Question>>(emptyList())
    val activeQuestions: StateFlow<List<Question>> = _activeQuestions.asStateFlow()

    // Leaderboard StateFlow
    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    init {
        AcademyApiClient.init(context)
        loadInitialData()
    }

    private fun loadInitialData() {
        scope.launch {
            val fetchedCourses = AcademyApiClient.fetchCourses()
            _courses.value = fetchedCourses
            val activeId = _userProgress.value.activeCourseId
            loadCurriculum(activeId)
            loadLeaderboard()
        }
    }

    fun refreshAll() {
        scope.launch {
            val fetchedCourses = AcademyApiClient.fetchCourses()
            _courses.value = fetchedCourses
            loadCurriculum(_userProgress.value.activeCourseId)
        }
    }

    fun selectActiveCourse(courseId: String) {
        val current = _userProgress.value
        if (current.activeCourseId != courseId) {
            _userProgress.value = current.copy(activeCourseId = courseId)
            secureStore.putSecureString("active_course_id", courseId)
            scope.launch {
                loadCurriculum(courseId)
            }
        }
    }

    private suspend fun loadCurriculum(courseId: String) {
        val curr = AcademyApiClient.fetchCurriculum(courseId)
        _curriculum.value = curr
    }

    suspend fun loadQuestionsForLesson(lessonId: String): List<Question> {
        val qs = AcademyApiClient.fetchQuestions(lessonId)
        _activeQuestions.value = qs
        return qs
    }

    fun toggleSubscription(courseId: String) {
        val updated = _courses.value.map { course ->
            if (course.id == courseId) {
                val newSub = !course.isSubscribed
                course.copy(isSubscribed = newSub)
            } else {
                course
            }
        }
        _courses.value = updated
    }

    fun evaluateAnswer(question: Question, input: String): QuizSubmissionResult {
        val isCorrect = question.isCorrectAnswer(input)
        val current = _userProgress.value

        return if (isCorrect) {
            val xpGain = 15
            val gemsGain = 3
            val newXp = current.totalXp + xpGain
            val newGems = current.brainGems + gemsGain
            _userProgress.value = current.copy(totalXp = newXp, brainGems = newGems)
            secureStore.putInt("user_xp", newXp)
            secureStore.putInt("user_gems", newGems)

            QuizSubmissionResult(
                isCorrect = true,
                userScore = 100,
                xpEarned = xpGain,
                gemsEarned = gemsGain,
                heartsRemaining = current.hearts,
                newStreak = current.streakDays,
                explanation = question.explanationHint.ifEmpty { "Spot on! Verified by Academy Edge." }
            )
        } else {
            val newHearts = (current.hearts - 1).coerceAtLeast(0)
            _userProgress.value = current.copy(hearts = newHearts)
            secureStore.putInt("user_hearts", newHearts)

            QuizSubmissionResult(
                isCorrect = false,
                userScore = 0,
                xpEarned = 0,
                gemsEarned = 0,
                heartsRemaining = newHearts,
                newStreak = current.streakDays,
                explanation = "Incorrect. ${question.explanationHint}"
            )
        }
    }

    fun completeLessonNode(lessonId: String, starsEarned: Int) {
        val current = _userProgress.value
        val streak = current.streakDays + 1
        _userProgress.value = current.copy(streakDays = streak)
        secureStore.putInt("user_streak", streak)

        // Mark node as completed in local curriculum
        val updatedCurriculum = _curriculum.value.map { section ->
            val updatedLessons = section.lessons.mapIndexed { idx, lesson ->
                if (lesson.id == lessonId) {
                    lesson.copy(status = NodeStatus.COMPLETED, stars = starsEarned)
                } else if (idx > 0 && section.lessons[idx - 1].id == lessonId && lesson.status == NodeStatus.LOCKED) {
                    lesson.copy(status = NodeStatus.AVAILABLE)
                } else {
                    lesson
                }
            }
            section.copy(lessons = updatedLessons)
        }
        _curriculum.value = updatedCurriculum
    }

    fun refillHeartsWithGems(): Boolean {
        val current = _userProgress.value
        if (current.brainGems >= 20 && current.hearts < current.maxHearts) {
            val newGems = current.brainGems - 20
            _userProgress.value = current.copy(hearts = current.maxHearts, brainGems = newGems)
            secureStore.putInt("user_hearts", current.maxHearts)
            secureStore.putInt("user_gems", newGems)
            return true
        }
        return false
    }

    private fun loadLeaderboard() {
        _leaderboard.value = listOf(
            LeaderboardEntry(1, "Vishal P. (Maintainer)", "👑", 1420, 24),
            LeaderboardEntry(2, "Alex_SecOps", "🛡️", 980, 15),
            LeaderboardEntry(3, "You (Student)", "⚡", _userProgress.value.totalXp, _userProgress.value.streakDays, isCurrentUser = true),
            LeaderboardEntry(4, "PlaywrightNinja", "🎭", 310, 5),
            LeaderboardEntry(5, "AgentSmith_AI", "🤖", 270, 4),
            LeaderboardEntry(6, "Pythonista42", "🐍", 190, 2),
            LeaderboardEntry(7, "CodeAuditor", "🔍", 140, 1)
        )
    }
}
