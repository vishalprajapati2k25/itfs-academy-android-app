package com.itfreesource.academy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.LessonNode
import com.itfreesource.academy.data.repository.AcademyRepository
import com.itfreesource.academy.security.SecurityManager
import com.itfreesource.academy.ui.components.TopStatusBar
import com.itfreesource.academy.ui.screens.*
import com.itfreesource.academy.ui.theme.*
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val icon: ImageVector) {
    LEARN("Learn", Icons.Default.PlayArrow),
    COURSES("Courses", Icons.Default.List),
    LEADERBOARD("League", Icons.Default.EmojiEvents),
    PROFILE("Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: AcademyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --------------------------------------------------------------------
        // SECURITY ENFORCEMENT: Screen Capture & Screenshot DRM Shield
        // --------------------------------------------------------------------
        SecurityManager.applyScreenshotProtection(this, true)

        repository = AcademyRepository(applicationContext)

        setContent {
            ITFSAcademyTheme {
                MainAppHost(repository = repository)
            }
        }
    }
}

@Composable
fun MainAppHost(repository: AcademyRepository) {
    val scope = rememberCoroutineScope()
    val isLight = MaterialTheme.colors.isLight

    val userProgress by repository.userProgress.collectAsState()
    val courses by repository.courses.collectAsState()
    val curriculum by repository.curriculum.collectAsState()
    val leaderboard by repository.leaderboard.collectAsState()

    val currentCourse = courses.find { it.id == userProgress.activeCourseId } ?: courses.firstOrNull()

    var currentTab by remember { mutableStateOf(MainTab.LEARN) }
    var activeQuizLesson by remember { mutableStateOf<LessonNode?>(null) }
    var showSecurityAudit by remember { mutableStateOf(false) }

    // If active quiz is running, render full-screen QuizPlayScreen
    if (activeQuizLesson != null) {
        val lesson = activeQuizLesson!!
        var quizQuestions by remember { mutableStateOf(repository.activeQuestions.value) }

        LaunchedEffect(lesson.id) {
            quizQuestions = repository.loadQuestionsForLesson(lesson.id)
        }

        QuizPlayScreen(
            lessonNode = lesson,
            questions = quizQuestions,
            hearts = userProgress.hearts,
            onAnswerSubmit = { q, input ->
                repository.evaluateAnswer(q, input)
            },
            onLessonFinished = { stars, xp ->
                repository.completeLessonNode(lesson.id, stars)
                activeQuizLesson = null
            },
            onExit = {
                activeQuizLesson = null
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopStatusBar(
                progress = userProgress,
                courseTitle = currentCourse?.title ?: "Select Course",
                courseEmoji = currentCourse?.iconEmoji ?: "🎓",
                onCourseClick = { currentTab = MainTab.COURSES },
                onSecurityShieldClick = { showSecurityAudit = true }
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = if (isLight) DuoWhite else DuoDarkSurface,
                elevation = 8.dp
            ) {
                MainTab.values().forEach { tab ->
                    val selected = currentTab == tab
                    BottomNavigationItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontSize = 11.sp) },
                        selected = selected,
                        selectedContentColor = DuoGreen,
                        unselectedContentColor = DuoGrayText,
                        onClick = { currentTab = tab }
                    )
                }
            }
        }
    ) { paddingVals ->
        Box(modifier = Modifier.padding(paddingVals)) {
            when (currentTab) {
                MainTab.LEARN -> LearnScreen(
                    currentCourse = currentCourse,
                    curriculum = curriculum,
                    progress = userProgress,
                    onStartLesson = { lesson ->
                        activeQuizLesson = lesson
                    },
                    onRefillHearts = {
                        repository.refillHeartsWithGems()
                    },
                    onOpenCourseCatalog = {
                        currentTab = MainTab.COURSES
                    }
                )
                MainTab.COURSES -> CoursesScreen(
                    courses = courses,
                    activeCourseId = userProgress.activeCourseId,
                    onToggleSubscribe = { courseId ->
                        repository.toggleSubscription(courseId)
                    },
                    onSelectActiveCourse = { courseId ->
                        repository.selectActiveCourse(courseId)
                        currentTab = MainTab.LEARN
                    }
                )
                MainTab.LEADERBOARD -> LeaderboardScreen(
                    entries = leaderboard
                )
                MainTab.PROFILE -> ProfileScreen(
                    progress = userProgress,
                    onOpenSecurityAudit = { showSecurityAudit = true }
                )
            }
        }
    }

    if (showSecurityAudit) {
        SecurityAuditDialog(onDismiss = { showSecurityAudit = false })
    }
}
