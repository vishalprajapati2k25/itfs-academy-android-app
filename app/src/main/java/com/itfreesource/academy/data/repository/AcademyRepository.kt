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
 * AcademyRepository — Reactive repository coordinating:
 * - Career Streams (SDET, Developer, Security, Programming)
 * - Resume Learning / Continue where left off persistence
 * - Step-by-step concept tutorials
 * - MCQs validation
 * - FAANG Long Answer interview preparation
 * - Readiness scoring and bookmarks
 */
class AcademyRepository(context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val secureStore = EncryptedDataStore(context)

    // User Progress with Resume Learning persistence
    private val _userProgress = MutableStateFlow(
        UserProgress(
            interviewReadinessScore = secureStore.getInt("interview_readiness", 78),
            conceptsMasteredCount = secureStore.getInt("concepts_mastered", 14),
            mcqsSolvedCount = secureStore.getInt("mcqs_solved", 42),
            longAnswersReviewedCount = secureStore.getInt("long_answers_reviewed", 19),
            streakDays = secureStore.getInt("user_streak", 5),
            activeCourseId = secureStore.getSecureString("active_course_id", "sdet"),
            lastActiveLessonId = secureStore.getSecureString("last_lesson_id", "sdet_playwright_l1"),
            lastActiveLessonTitle = secureStore.getSecureString("last_lesson_title", "Playwright Network Mocking & Custom Fixtures"),
            lastActiveStreamTitle = secureStore.getSecureString("last_stream_title", "SDET & Automation"),
            lastActiveSubTopicTitle = secureStore.getSecureString("last_subtopic_title", "Playwright E2E Mastery"),
            lastActiveStepNumber = secureStore.getInt("last_step_num", 2)
        )
    )
    val userProgress: StateFlow<UserProgress> = _userProgress.asStateFlow()

    // Career Streams (SDET, Developer, Security, Programming)
    private val _careerStreams = MutableStateFlow<List<CareerStream>>(emptyList())
    val careerStreams: StateFlow<List<CareerStream>> = _careerStreams.asStateFlow()

    // Selected Career Stream for Drill-Down
    private val _selectedStream = MutableStateFlow<CareerStream?>(null)
    val selectedStream: StateFlow<CareerStream?> = _selectedStream.asStateFlow()

    // Courses List
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    // Curriculum
    private val _curriculum = MutableStateFlow<List<CourseSection>>(emptyList())
    val curriculum: StateFlow<List<CourseSection>> = _curriculum.asStateFlow()

    // Current Topic Content (Concepts + MCQs + Long Answers)
    private val _activeTopicContent = MutableStateFlow<TopicContent?>(null)
    val activeTopicContent: StateFlow<TopicContent?> = _activeTopicContent.asStateFlow()

    init {
        AcademyApiClient.init(context)
        loadInitialData()
    }

    private fun loadInitialData() {
        scope.launch {
            val streams = AcademyApiClient.fetchCareerStreams()
            _careerStreams.value = streams

            val savedEnrolled = secureStore.getSecureString("enrolled_stream_ids", "sdet")
            if (savedEnrolled.isNotBlank()) {
                val set = savedEnrolled.split(",").filter { it.isNotBlank() }.toSet()
                _userProgress.value = _userProgress.value.copy(enrolledStreamIds = set)
            }

            val list = AcademyApiClient.fetchCourses()
            _courses.value = list
            loadCurriculum(_userProgress.value.activeCourseId)
        }
    }

    fun selectCareerStream(stream: CareerStream?) {
        _selectedStream.value = stream
        if (stream != null) {
            selectCourse(stream.id)
        }
    }

    fun selectCourse(courseId: String) {
        val curr = _userProgress.value
        if (curr.activeCourseId != courseId) {
            _userProgress.value = curr.copy(activeCourseId = courseId)
            secureStore.putSecureString("active_course_id", courseId)
            scope.launch {
                loadCurriculum(courseId)
            }
        }
    }

    private suspend fun loadCurriculum(courseId: String) {
        val cur = AcademyApiClient.fetchCurriculum(courseId)
        _curriculum.value = cur
    }

    suspend fun loadTopicContent(lessonId: String): TopicContent {
        val content = AcademyApiClient.fetchTopicContent(lessonId)
        _activeTopicContent.value = content

        // Update & persist "Continue Where You Left Off" state
        val stream = _careerStreams.value.find { s -> s.subTopics.any { it.lessonId == lessonId } }
        val subTopic = stream?.subTopics?.find { it.lessonId == lessonId }
        val streamTitle = stream?.title ?: "SDET & Automation"
        val subTitle = subTopic?.title ?: content.title

        val curr = _userProgress.value
        _userProgress.value = curr.copy(
            lastActiveLessonId = lessonId,
            lastActiveLessonTitle = content.title,
            lastActiveStreamTitle = streamTitle,
            lastActiveSubTopicTitle = subTitle
        )
        secureStore.putSecureString("last_lesson_id", lessonId)
        secureStore.putSecureString("last_lesson_title", content.title)
        secureStore.putSecureString("last_stream_title", streamTitle)
        secureStore.putSecureString("last_subtopic_title", subTitle)

        return content
    }

    fun toggleCareerStreamEnrollment(streamId: String) {
        val current = _userProgress.value
        val updated = current.enrolledStreamIds.toMutableSet()
        val isEnrolling = !updated.contains(streamId)
        if (isEnrolling) {
            updated.add(streamId)
        } else {
            updated.remove(streamId)
        }
        _userProgress.value = current.copy(enrolledStreamIds = updated)
        secureStore.putSecureString("enrolled_stream_ids", updated.joinToString(","))
    }

    fun getStreamProgressPercent(stream: CareerStream): Float {
        val current = _userProgress.value
        val totalSubTopics = stream.subTopics.size.coerceAtLeast(1)
        val completedSubTopics = stream.subTopics.count { sub ->
            current.completedLessonIds.contains(sub.lessonId)
        }
        val dynamicRatio = completedSubTopics.toFloat() / totalSubTopics.toFloat()
        val mappedRatio = current.streamProgressMap[stream.id] ?: 0f
        return maxOf(dynamicRatio, mappedRatio).coerceIn(0f, 1f)
    }

    fun toggleSubscription(courseId: String) {
        val updated = _courses.value.map {
            if (it.id == courseId) it.copy(isSubscribed = !it.isSubscribed) else it
        }
        _courses.value = updated

        val curr = _userProgress.value
        val updatedEnrolled = curr.enrolledCourseIds.toMutableSet()
        if (updatedEnrolled.contains(courseId)) {
            updatedEnrolled.remove(courseId)
        } else {
            updatedEnrolled.add(courseId)
        }
        _userProgress.value = curr.copy(enrolledCourseIds = updatedEnrolled)
    }

    fun submitMcq(question: Question, selectedIndex: Int): QuizSubmissionResult {
        val isCorrect = selectedIndex == question.correctOptionIndex
        val current = _userProgress.value

        if (isCorrect) {
            val newScore = (current.interviewReadinessScore + 1).coerceAtMost(100)
            val newSolved = current.mcqsSolvedCount + 1
            _userProgress.value = current.copy(
                interviewReadinessScore = newScore,
                mcqsSolvedCount = newSolved
            )
            secureStore.putInt("interview_readiness", newScore)
            secureStore.putInt("mcqs_solved", newSolved)

            return QuizSubmissionResult(
                isCorrect = true,
                userScore = 100,
                xpEarned = 15,
                explanation = question.explanationHint
            )
        } else {
            val reason = question.distractorRationale[selectedIndex]
                ?: "Incorrect choice. Review the core concept steps above."
            return QuizSubmissionResult(
                isCorrect = false,
                userScore = 0,
                xpEarned = 0,
                explanation = "$reason\n\nCorrect approach: ${question.explanationHint}"
            )
        }
    }

    fun markConceptStepCompleted(lessonId: String, stepNumber: Int) {
        val current = _userProgress.value
        val newMastered = current.conceptsMasteredCount + 1
        val newScore = (current.interviewReadinessScore + 1).coerceAtMost(100)
        _userProgress.value = current.copy(
            conceptsMasteredCount = newMastered,
            interviewReadinessScore = newScore,
            lastActiveStepNumber = stepNumber
        )
        secureStore.putInt("concepts_mastered", newMastered)
        secureStore.putInt("interview_readiness", newScore)
        secureStore.putInt("last_step_num", stepNumber)
    }

    fun markLongAnswerReviewed(questionId: String) {
        val current = _userProgress.value
        val newReviewed = current.longAnswersReviewedCount + 1
        val newScore = (current.interviewReadinessScore + 2).coerceAtMost(100)
        _userProgress.value = current.copy(
            longAnswersReviewedCount = newReviewed,
            interviewReadinessScore = newScore
        )
        secureStore.putInt("long_answers_reviewed", newReviewed)
        secureStore.putInt("interview_readiness", newScore)
    }

    fun toggleBookmark(questionId: String) {
        val current = _userProgress.value
        val updated = current.bookmarkedQuestionIds.toMutableSet()
        if (updated.contains(questionId)) {
            updated.remove(questionId)
        } else {
            updated.add(questionId)
        }
        _userProgress.value = current.copy(bookmarkedQuestionIds = updated)
    }
}
