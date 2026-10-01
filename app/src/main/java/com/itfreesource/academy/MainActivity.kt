package com.itfreesource.academy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.LessonNode
import com.itfreesource.academy.data.model.TopicContent
import com.itfreesource.academy.data.repository.AcademyRepository
import com.itfreesource.academy.security.SecurityManager
import com.itfreesource.academy.ui.components.AppTopBar
import com.itfreesource.academy.ui.screens.*
import com.itfreesource.academy.ui.theme.*
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val icon: ImageVector) {
    LEARN("Learn", Icons.AutoMirrored.Filled.MenuBook),
    INTERVIEW("Interview", Icons.Default.BusinessCenter),
    TRACKS("Tracks", Icons.Default.ViewCarousel),
    PROFILE("Readiness", Icons.Default.Person)
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
            ITFSAcademyTheme(darkTheme = true) {
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

    val currentCourse = courses.find { it.id == userProgress.activeCourseId } ?: courses.firstOrNull()

    var currentTab by remember { mutableStateOf(MainTab.LEARN) }
    var activeTopicLesson by remember { mutableStateOf<LessonNode?>(null) }
    var activeTopicContent by remember { mutableStateOf<TopicContent?>(null) }
    var showSecurityAudit by remember { mutableStateOf(false) }

    // If active topic is open, render TopicDetailScreen (Concepts, MCQs, and Long Answers)
    if (activeTopicLesson != null && activeTopicContent != null) {
        TopicDetailScreen(
            topicContent = activeTopicContent!!,
            onBack = {
                activeTopicLesson = null
                activeTopicContent = null
            },
            onVerifyMcq = { q, selectedIdx ->
                repository.submitMcq(q, selectedIdx)
            },
            onMarkStepComplete = { stepNum ->
                repository.markConceptStepCompleted(activeTopicLesson!!.id, stepNum)
            },
            onMarkInterviewReviewed = { qId ->
                repository.markLongAnswerReviewed(qId)
            }
        )
        return
    }

    Scaffold(
        topBar = {
            AppTopBar(
                progress = userProgress,
                currentCourseTitle = currentCourse?.title ?: "Select Track",
                currentCourseEmoji = currentCourse?.iconEmoji ?: "🎓",
                onCourseClick = { currentTab = MainTab.TRACKS },
                onSecurityClick = { showSecurityAudit = true }
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                elevation = 8.dp
            ) {
                MainTab.values().forEach { tab ->
                    val selected = currentTab == tab
                    BottomNavigationItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontSize = 11.sp, fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal) },
                        selected = selected,
                        selectedContentColor = BrandIndigo,
                        unselectedContentColor = ObsidianTextMuted,
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
                    onOpenTopic = { lesson ->
                        scope.launch {
                            val content = repository.loadTopicContent(lesson.id)
                            activeTopicLesson = lesson
                            activeTopicContent = content
                        }
                    },
                    onOpenCatalog = {
                        currentTab = MainTab.TRACKS
                    }
                )
                MainTab.INTERVIEW -> InterviewPrepScreen(
                    onMarkReviewed = { qId ->
                        repository.markLongAnswerReviewed(qId)
                    }
                )
                MainTab.TRACKS -> CoursesScreen(
                    courses = courses,
                    activeCourseId = userProgress.activeCourseId,
                    onToggleSubscribe = { courseId ->
                        repository.toggleSubscription(courseId)
                    },
                    onSelectActiveCourse = { courseId ->
                        repository.selectCourse(courseId)
                        currentTab = MainTab.LEARN
                    }
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
