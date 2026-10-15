package com.itfreesource.academy.data.api

import android.content.Context
import com.itfreesource.academy.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * AcademyApiClient — REST API client connecting to ITFreeSource Academy Edge.
 * Delivers Career Streams (SDET, Developer, Security, Programming),
 * step-by-step concept tutorials, MCQs, and FAANG Long Answer interview questions.
 */
object AcademyApiClient {

    private const val PREFS_NAME = "itfs_academy_api_prefs"
    private const val PREF_KEY_BASE_URL = "custom_base_url"

    const val LIVE_ACADEMY_URL = "https://academy.itfreesource.com/api/v1"
    const val DEV_EMULATOR_URL = "http://10.0.2.2:8788/api/v1"

    var baseUrl: String = LIVE_ACADEMY_URL
    var isLiveConnected: Boolean = false
    var lastSyncStatus: String = "Edge Active (Cloudflare Serverless)"
    var lastSyncLatencyMs: Long = 0

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(PREF_KEY_BASE_URL, null)
        baseUrl = if (!saved.isNullOrBlank()) saved else LIVE_ACADEMY_URL
    }

    private fun openConnection(endpoint: String, method: String): HttpURLConnection {
        val fullUrl = "$baseUrl$endpoint"
        val url = URL(fullUrl)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = method
        conn.setRequestProperty("Accept", "application/json")
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("User-Agent", "ITFS-Academy-Pro/2.0.0 (Android; ARM64)")
        conn.connectTimeout = 4000
        conn.readTimeout = 4000
        return conn
    }

    suspend fun testConnection(): Pair<Boolean, Long> = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            val conn = openConnection("/courses", "GET")
            val code = conn.responseCode
            val latency = System.currentTimeMillis() - start
            lastSyncLatencyMs = latency
            if (code in 200..299) {
                isLiveConnected = true
                lastSyncStatus = "Live Cloudflare Edge ($latency ms)"
                Pair(true, latency)
            } else {
                isLiveConnected = false
                lastSyncStatus = "HTTP $code (Offline Fallback Engine Active)"
                Pair(false, latency)
            }
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - start
            isLiveConnected = false
            lastSyncStatus = "Offline Cache Active"
            Pair(false, latency)
        }
    }

    suspend fun fetchCareerStreams(): List<CareerStream> = withContext(Dispatchers.IO) {
        getFallbackCareerStreams()
    }

    suspend fun fetchCourses(): List<Course> = withContext(Dispatchers.IO) {
        try {
            val conn = openConnection("/courses", "GET")
            if (conn.responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val jsonStr = reader.readText()
                reader.close()
                val jsonArray = JSONArray(jsonStr)
                val list = mutableListOf<Course>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(parseCourseJson(obj))
                }
                isLiveConnected = true
                list
            } else {
                getFallbackCourses()
            }
        } catch (e: Exception) {
            getFallbackCourses()
        }
    }

    suspend fun fetchCurriculum(courseId: String): List<CourseSection> = withContext(Dispatchers.IO) {
        try {
            val conn = openConnection("/courses/$courseId/curriculum", "GET")
            if (conn.responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val jsonStr = reader.readText()
                reader.close()
                parseCurriculumJson(courseId, JSONArray(jsonStr))
            } else {
                getFallbackCurriculum(courseId)
            }
        } catch (e: Exception) {
            getFallbackCurriculum(courseId)
        }
    }

    suspend fun fetchTopicContent(lessonId: String): TopicContent = withContext(Dispatchers.IO) {
        getFallbackTopicContent(lessonId)
    }

    private fun parseCourseJson(obj: JSONObject): Course {
        val catStr = obj.optString("category", "BACKEND_LANGUAGES")
        val category = try { CourseCategory.valueOf(catStr) } catch (_: Exception) { CourseCategory.BACKEND_LANGUAGES }
        return Course(
            id = obj.getString("id"),
            title = obj.getString("title"),
            category = category,
            iconEmoji = obj.optString("iconEmoji", "🎓"),
            description = obj.optString("description", ""),
            levelsCount = obj.optInt("levelsCount", 8),
            xpReward = obj.optInt("xpReward", 150),
            isSubscribed = obj.optBoolean("isSubscribed", false),
            progressPercent = obj.optDouble("progressPercent", 0.0).toFloat(),
            difficulty = obj.optString("difficulty", "Senior / Staff Track"),
            targetCompanies = listOf("Google", "Meta", "Amazon", "Uber", "Apple"),
            interviewWeight = obj.optString("interviewWeight", "High Frequency")
        )
    }

    private fun parseCurriculumJson(courseId: String, array: JSONArray): List<CourseSection> {
        val sections = mutableListOf<CourseSection>()
        for (i in 0 until array.length()) {
            val sObj = array.getJSONObject(i)
            val secId = sObj.getString("sectionId")
            val lessonsArray = sObj.getJSONArray("lessons")
            val lessons = mutableListOf<LessonNode>()
            for (j in 0 until lessonsArray.length()) {
                val lObj = lessonsArray.getJSONObject(j)
                lessons.add(
                    LessonNode(
                        id = lObj.getString("id"),
                        sectionId = secId,
                        title = lObj.getString("title"),
                        subtitle = lObj.optString("subtitle", "Core Concept & FAANG Deep Dive"),
                        order = lObj.optInt("order", j + 1),
                        estMinutes = lObj.optInt("estMinutes", 15),
                        difficulty = lObj.optString("difficulty", "Medium"),
                        interviewTopicsCount = lObj.optInt("interviewTopicsCount", 2),
                        mcqCount = lObj.optInt("mcqCount", 3),
                        isCompleted = lObj.optBoolean("isCompleted", false)
                    )
                )
            }
            sections.add(
                CourseSection(
                    sectionId = secId,
                    courseId = courseId,
                    title = sObj.getString("title"),
                    description = sObj.optString("description", ""),
                    order = sObj.optInt("order", i + 1),
                    lessons = lessons
                )
            )
        }
        return sections
    }

    // ========================================================================
    // CAREER STREAMS SEED (SDET, Developer, Security, Programming)
    // ========================================================================
    fun getFallbackCareerStreams(): List<CareerStream> {
        return listOf(
            CareerStream(
                id = "sdet",
                title = "SDET & Test Architecture",
                shortCode = "SDET",
                iconEmoji = "🎯",
                tagline = "Playwright • Selenium • LLM Quality • Coding • PseudoCodes",
                description = "Master industrial E2E test frameworks, AI agent quality evaluation, LeetCode patterns for SDETs, and distributed test grid architecture.",
                modulesCount = 5,
                faangQuestionsCount = 42,
                readinessPercent = 78,
                colorHex = 0xFF6366F1, // BrandIndigo
                subTopics = listOf(
                    StreamSubTopic(
                        id = "sdet_playwright",
                        streamId = "sdet",
                        title = "Playwright E2E Mastery",
                        iconEmoji = "🎭",
                        description = "Network route mocking, isolated browser contexts, custom fixtures, auto-waiting, and trace diagnostics.",
                        targetRole = "Lead / Senior SDET",
                        lessonId = "sdet_playwright_l1",
                        estMinutes = 15,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "sdet_selenium",
                        streamId = "sdet",
                        title = "Selenium & Grid Architecture",
                        iconEmoji = "🌐",
                        description = "RemoteWebDriver protocols, StaleElement atomic retry strategies, dynamic waits, and high-concurrency Grid clusters.",
                        targetRole = "Staff SDET / Test Architect",
                        lessonId = "sdet_selenium_l1",
                        estMinutes = 20,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "sdet_llm_testing",
                        streamId = "sdet",
                        title = "LLM & GenAI Quality Engineering",
                        iconEmoji = "🤖",
                        description = "RAG evaluation benchmarks, hallucination detection heuristics, prompt injection defense, and non-deterministic assertions.",
                        targetRole = "AI Quality Engineer / Staff SDET",
                        lessonId = "sdet_llm_testing_l1",
                        estMinutes = 20,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "sdet_coding",
                        streamId = "sdet",
                        title = "Coding Questions for SDETs",
                        iconEmoji = "🧩",
                        description = "DOM tree BFS/DFS traversals, log file sliding window parsers, string manipulations, and test retry queue structures.",
                        targetRole = "Senior SDET / L5 Automation",
                        lessonId = "sdet_coding_l1",
                        estMinutes = 25,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "sdet_pseudocode",
                        streamId = "sdet",
                        title = "PseudoCodes & Test Architecture",
                        iconEmoji = "📝",
                        description = "Distributed test orchestrator algorithms, work-stealing schedulers, and flaky test quarantine state machines.",
                        targetRole = "Principal Test Architect",
                        lessonId = "sdet_pseudocode_l1",
                        estMinutes = 20,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    )
                )
            ),
            CareerStream(
                id = "developer",
                title = "Software Developer & Backend Systems",
                shortCode = "DEV",
                iconEmoji = "⚡",
                tagline = "Python Internals • Microservices • Caching • Event-Driven",
                description = "Deep dive into language runtime internals, distributed transactions (Saga), event-driven architectures, and high-throughput systems.",
                modulesCount = 4,
                faangQuestionsCount = 56,
                readinessPercent = 74,
                colorHex = 0xFF8B5CF6, // AccentViolet
                subTopics = listOf(
                    StreamSubTopic(
                        id = "dev_python",
                        streamId = "developer",
                        title = "CPython VM & PyMalloc Internals",
                        iconEmoji = "🐍",
                        description = "Stack evaluation in ceval.c, Arenas/Pools/Blocks allocator, GIL concurrency, and descriptor protocols.",
                        targetRole = "Senior Backend Engineer (L5)",
                        lessonId = "py_l1",
                        estMinutes = 20,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "dev_microservices",
                        streamId = "developer",
                        title = "Distributed Microservices & Saga",
                        iconEmoji = "🔄",
                        description = "2-Phase Commit vs Choreographed Saga, idempotent API keys, circuit breakers, and distributed tracing.",
                        targetRole = "Staff Engineer (L6)",
                        lessonId = "dev_microservices_l1",
                        estMinutes = 25,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "dev_system_design",
                        streamId = "developer",
                        title = "High-Scale System Design",
                        iconEmoji = "🏗️",
                        description = "Distributed rate limiters, global cache-aside patterns, database sharding, and back-of-the-envelope estimation.",
                        targetRole = "Staff / Principal Architect",
                        lessonId = "dev_system_design_l1",
                        estMinutes = 30,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "dev_event_driven",
                        streamId = "developer",
                        title = "Event-Driven Systems & Kafka",
                        iconEmoji = "📨",
                        description = "Partition assignment strategies, rebalancing protocols, exactly-once processing, and consumer lag mitigation.",
                        targetRole = "Senior Backend Engineer",
                        lessonId = "dev_event_driven_l1",
                        estMinutes = 20,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    )
                )
            ),
            CareerStream(
                id = "security",
                title = "Application Security & DevSecOps",
                shortCode = "SEC",
                iconEmoji = "🛡️",
                tagline = "OWASP Top 10 • Threat Modeling • Cloud IAM • Code Auditing",
                description = "Master production exploit vectors, zero-trust token signing, AWS/GCP KMS key architectures, and automated security gating.",
                modulesCount = 4,
                faangQuestionsCount = 38,
                readinessPercent = 62,
                colorHex = 0xFF10B981, // SuccessEmerald
                subTopics = listOf(
                    StreamSubTopic(
                        id = "sec_owasp",
                        streamId = "security",
                        title = "OWASP Top 10 & API Vulnerabilities",
                        iconEmoji = "🚨",
                        description = "Server-Side Request Forgery (SSRF), BOLA, SQL injection bypasses, and defensive middleware architecture.",
                        targetRole = "Staff AppSec Engineer",
                        lessonId = "sec_owasp_l1",
                        estMinutes = 20,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "sec_threat_model",
                        streamId = "security",
                        title = "Threat Modeling & Zero-Trust Tokens",
                        iconEmoji = "🔐",
                        description = "STRIDE diagrams, cryptographic key rotation, JWT signature forgery prevention, and mutual TLS (mTLS).",
                        targetRole = "Security Architect",
                        lessonId = "sec_threat_model_l1",
                        estMinutes = 25,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "sec_cloud_iam",
                        streamId = "security",
                        title = "Cloud Security & IAM Least Privilege",
                        iconEmoji = "☁️",
                        description = "Permission boundaries, cross-account assume-role chains, KMS envelope encryption, and container hardening.",
                        targetRole = "DevSecOps Lead",
                        lessonId = "sec_cloud_iam_l1",
                        estMinutes = 20,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "sec_code_review",
                        streamId = "security",
                        title = "Secure Code Review & Race Conditions",
                        iconEmoji = "🔍",
                        description = "Time-of-Check to Time-of-Use (TOCTOU) bugs, unsafe deserialization, timing side-channels, and SAST triage.",
                        targetRole = "Application Security Engineer",
                        lessonId = "sec_code_review_l1",
                        estMinutes = 20,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    )
                )
            ),
            CareerStream(
                id = "programming",
                title = "Core Programming & Algorithms",
                shortCode = "PROG",
                iconEmoji = "💻",
                tagline = "Data Structures • LeetCode Patterns • PseudoCodes • Big-O",
                description = "Master high-frequency FAANG coding interview algorithms, rigorous amortized complexity analysis, and algorithmic formulations.",
                modulesCount = 4,
                faangQuestionsCount = 50,
                readinessPercent = 82,
                colorHex = 0xFFF59E0B, // WarningAmber
                subTopics = listOf(
                    StreamSubTopic(
                        id = "prog_patterns",
                        streamId = "programming",
                        title = "High-Yield LeetCode Patterns",
                        iconEmoji = "🧩",
                        description = "Sliding Window, Two Pointers, Fast & Slow Pointers, Monotonic Stack, and Binary Search boundary logic.",
                        targetRole = "Software Engineer (L4-L6)",
                        lessonId = "prog_patterns_l1",
                        estMinutes = 25,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "prog_data_structures",
                        streamId = "programming",
                        title = "Advanced Data Structures Mastery",
                        iconEmoji = "🌲",
                        description = "Trie prefix trees, Min/Max Heaps for streaming medians, Disjoint Set Union (Union-Find), and Graph adjacency lists.",
                        targetRole = "Senior Software Engineer (L5)",
                        lessonId = "prog_data_structures_l1",
                        estMinutes = 25,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "prog_pseudocode",
                        streamId = "programming",
                        title = "Algorithmic PseudoCodes & Formulations",
                        iconEmoji = "📝",
                        description = "Translating ambiguous business requirements into mathematical step invariants and recurrence relations.",
                        targetRole = "Staff Software Engineer (L6)",
                        lessonId = "prog_pseudocode_l1",
                        estMinutes = 20,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    ),
                    StreamSubTopic(
                        id = "prog_big_o",
                        streamId = "programming",
                        title = "Big-O Time & Space Complexity Rigor",
                        iconEmoji = "⏱️",
                        description = "Amortized complexity, Master Theorem recursion trees, CPU L1/L2 cache locality, and memory overhead of pointers.",
                        targetRole = "Senior / Staff Engineer",
                        lessonId = "prog_big_o_l1",
                        estMinutes = 15,
                        mcqCount = 3,
                        faangQuestionsCount = 2
                    )
                )
            )
        )
    }

    // Fallback Course list
    fun getFallbackCourses(): List<Course> {
        return listOf(
            Course(
                id = "sdet",
                title = "SDET & Test Architecture",
                category = CourseCategory.QUALITY_ENGINEERING,
                iconEmoji = "🎯",
                description = "Master Playwright, Selenium, LLM Quality Engineering, Coding Interview Drills, and Test Runner PseudoCodes.",
                levelsCount = 5,
                xpReward = 350,
                isSubscribed = true,
                progressPercent = 0.78f,
                difficulty = "Lead / Senior SDET",
                targetCompanies = listOf("Google", "Microsoft", "Amazon", "Salesforce"),
                interviewWeight = "High Yield Automation"
            ),
            Course(
                id = "developer",
                title = "Software Developer & Backend Systems",
                category = CourseCategory.BACKEND_LANGUAGES,
                iconEmoji = "⚡",
                description = "CPython VM internals, Distributed Microservices, High-Scale System Design, and Event-Driven Kafka architecture.",
                levelsCount = 4,
                xpReward = 320,
                isSubscribed = true,
                progressPercent = 0.74f,
                difficulty = "Senior / L5 Engineering",
                targetCompanies = listOf("Google", "Meta", "Uber", "Netflix"),
                interviewWeight = "Must-Know Systems"
            ),
            Course(
                id = "security",
                title = "Application Security & DevSecOps",
                category = CourseCategory.SECURITY,
                iconEmoji = "🛡️",
                description = "OWASP Top 10 exploits, SSRF bypasses, Threat Modeling architectures, Zero-Trust tokens, and Secure Code Audits.",
                levelsCount = 4,
                xpReward = 300,
                isSubscribed = true,
                progressPercent = 0.62f,
                difficulty = "Staff Security Architect",
                targetCompanies = listOf("CrowdStrike", "Cloudflare", "Palantir", "Amazon"),
                interviewWeight = "Security Armor"
            ),
            Course(
                id = "programming",
                title = "Core Programming & Algorithms",
                category = CourseCategory.CAREER_NAVIGATION,
                iconEmoji = "💻",
                description = "High-frequency LeetCode algorithmic patterns, advanced data structures, pseudo-codes, and Big-O complexity rigor.",
                levelsCount = 4,
                xpReward = 280,
                isSubscribed = true,
                progressPercent = 0.82f,
                difficulty = "Staff / Bar Raiser",
                targetCompanies = listOf("Google", "Meta", "Apple", "Uber"),
                interviewWeight = "Coding Filter"
            )
        )
    }

    fun getFallbackCurriculum(courseId: String): List<CourseSection> {
        val stream = getFallbackCareerStreams().find { it.id == courseId } ?: getFallbackCareerStreams().first()
        return listOf(
            CourseSection(
                sectionId = "${stream.id}_sec_1",
                courseId = stream.id,
                title = "Curated Modules & Interview Deep Dives",
                description = "Step-by-step masterclass covering concepts, MCQs, and FAANG Long Answers.",
                order = 1,
                lessons = stream.subTopics.mapIndexed { idx, st ->
                    LessonNode(
                        id = st.lessonId,
                        sectionId = "${stream.id}_sec_1",
                        title = st.title,
                        subtitle = st.description,
                        order = idx + 1,
                        estMinutes = st.estMinutes,
                        difficulty = st.targetRole,
                        interviewTopicsCount = st.faangQuestionsCount,
                        mcqCount = st.mcqCount,
                        isCompleted = st.isCompleted
                    )
                }
            )
        )
    }

    // ========================================================================
    // HIGH-YIELD TOPIC CONTENT (Concepts, MCQs, FAANG Long Answers)
    // ========================================================================
    fun getFallbackTopicContent(lessonId: String): TopicContent {
        return when {
            // ----------------------------------------------------------------
            // SDET: PLAYWRIGHT
            // ----------------------------------------------------------------
            lessonId.startsWith("sdet_playwright") -> TopicContent(
                lessonId = lessonId,
                title = "Playwright Network Mocking, Fixtures & Flakiness Eradication",
                overview = "Master industrial-grade browser automation using Playwright: architecting isolated browser contexts, mock routing, auto-waiting mechanics, and trace diagnostics.",
                conceptSteps = listOf(
                    ConceptStep(
                        stepNumber = 1,
                        title = "Step 1: Browser Context Isolation vs Page Lifecycle",
                        summary = "Why separate BrowserContexts deliver 100x faster execution than spawning fresh browser instances.",
                        detailedExplanation = "Traditional frameworks launch a new browser process for every test, costing seconds in OS overhead. Playwright introduces **BrowserContext**:\n\n1. **Single Browser Process**: Chromium/WebKit boots once.\n2. **Multi-Tenant Contexts**: Each test receives an incognito-like, memory-isolated `BrowserContext` with its own cookies, localStorage, and cache.\n3. **Parallel Throughput**: 10+ workers can run concurrently in parallel on a single machine with zero session cross-contamination.",
                        architecturalDiagram = "Single Browser Process -> Worker 1 (Context A: Auth) | Worker 2 (Context B: Guest) | Worker 3 (Context C: Admin)",
                        keyTakeaway = "Use test.extend() fixtures to inject pre-authenticated storage states into isolated contexts."
                    ),
                    ConceptStep(
                        stepNumber = 2,
                        title = "Step 2: Network Routing & Payment Gateway Mocking",
                        summary = "Using page.route() to mock flaky third-party APIs with microsecond latency.",
                        detailedExplanation = "Real external APIs (Stripe, Twilio, Auth0) introduce test flakiness, rate limits, and network lag. Playwright intercepts HTTP requests at the protocol layer:\n\n- Intercepts requests before they hit the network wire.\n- Returns deterministic JSON mock payloads with custom HTTP status codes.\n- Enables testing edge cases like HTTP 504 Gateway Timeout or partial payload corruptions.",
                        codeSnippet = """
// Playwright TypeScript Route Interception
await page.route('**/api/v1/checkout', async (route) => {
  const postData = route.request().postDataJSON();
  if (postData.amount <= 0) {
    await route.fulfill({
      status: 422,
      contentType: 'application/json',
      body: JSON.stringify({ error: 'INVALID_AMOUNT' })
    });
  } else {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ transactionId: 'tx_mock_9921', status: 'SUCCESS' })
    });
  }
});
                        """.trimIndent(),
                        keyTakeaway = "Always mock third-party payment and SMS gateways to achieve 100% test determinism."
                    ),
                    ConceptStep(
                        stepNumber = 3,
                        title = "Step 3: Actionability Checks & Zero Sleep Policy",
                        summary = "Why Thread.sleep() or waitForTimeout() are banned in production test suites.",
                        detailedExplanation = "Playwright performs automatic actionability checks prior to executing clicks or inputs:\n\n1. Element is attached to the DOM.\n2. Element is visible (`display != none`, `opacity != 0`).\n3. Element is stable (not undergoing CSS transitions/animations).\n4. Element receives pointer events (not obscured by sticky banners or modals).\n\nIf any check fails, Playwright retries within the action timeout rather than failing immediately.",
                        keyTakeaway = "Never use hard sleeps. Rely strictly on Playwright's locator auto-waiting and web-first assertions."
                    )
                ),
                mcqs = listOf(
                    Question(
                        id = "pw_mcq_1",
                        lessonId = lessonId,
                        prompt = "Why does using `page.waitForTimeout(5000)` cause severe pipeline regressions in continuous integration?",
                        options = listOf(
                            "It injects non-deterministic idle latency that exponentially bloats pipeline execution and masks real race conditions",
                            "Playwright drops browser websocket connections if the event loop is idle for 5 seconds",
                            "Modern browsers automatically terminate execution when encountering arbitrary sleep commands",
                            "Chromium garbage collector purges the DOM tree if no user events occur during the timeout"
                        ),
                        correctAnswerHash = "b98dafa8f21e5f3bbd18f51950e932944cae97a51c4a04d3e5a528e1c66779ee",
                        correctOptionIndex = 0,
                        explanationHint = "Hard sleeps slow test suites and fail to wait for real DOM readiness. Use web-first assertions like `expect(locator).toBeVisible()`.",
                        difficulty = "Senior",
                        companyTag = "Microsoft"
                    )
                ),
                interviewQuestions = listOf(
                    InterviewQuestion(
                        id = "pw_iq_1",
                        lessonId = lessonId,
                        title = "Architect a Zero-Flake E2E Automation Pipeline for a Global Checkout",
                        targetCompany = "Google / Microsoft",
                        roleLevel = "Lead SDET / Principal Test Architect",
                        problemStatement = "Your company's checkout flow has an 18% test failure rate in CI due to network timeouts, third-party payment gateway lags, and dynamic DOM animations. Design a zero-flake testing architecture that scales across 100 parallel workers.",
                        timeEstimateMinutes = 20,
                        keyTalkingPoints = listOf(
                            "BrowserContext storage state reuse to eliminate redundant UI logins.",
                            "Protocol-level network mocking for external payment processors (Stripe/PayPal).",
                            "Web-first assertions replacing explicit timeouts.",
                            "Automated trace capture on first retry with CI artifact upload."
                        ),
                        modelAnswer = """
### Architectural Strategy:

1. **Authentication Storage State Matrix**:
   - Run a single global setup worker that logs in test personas and saves auth tokens as `userState.json`.
   - All 100 parallel test workers initialize their `BrowserContext` with this state, eliminating 100 redundant UI login sequences.

2. **Network Route Interception for Third Parties**:
   - Intercept requests to external payment providers via `page.route()`.
   - Provide deterministic mock responses for SUCCESS, INSUFFICIENT_FUNDS, and TIMEOUT scenarios.

3. **Trace Viewer Post-Mortem Debugging**:
   - Configure `trace: 'on-first-retry'`. Tests execute with zero tracing overhead on pass; if a failure occurs, Playwright reruns once with screencasts, DOM snapshots, and network waterfalls recorded for instant RCA.
                        """.trimIndent(),
                        followUpQuestions = listOf(
                            "How do you handle shard distribution across GitHub Actions matrix runners?",
                            "How do you ensure component isolation when testing micro-frontends?"
                        )
                    )
                )
            )

            // ----------------------------------------------------------------
            // SDET: SELENIUM & GRID ARCHITECTURE
            // ----------------------------------------------------------------
            lessonId.startsWith("sdet_selenium") -> TopicContent(
                lessonId = lessonId,
                title = "Selenium Grid Architecture & StaleElement Resolution",
                overview = "Industrial-grade WebDriver engineering: diagnosing StaleElementReferenceException, architecting custom ExpectedConditions, and scaling Kubernetes Selenium Grid hubs.",
                conceptSteps = listOf(
                    ConceptStep(
                        stepNumber = 1,
                        title = "Step 1: StaleElementReferenceException Deep Dive",
                        summary = "Why DOM re-renders detach previously located WebElement references.",
                        detailedExplanation = "A `WebElement` in Selenium is an internal pointer ID referencing an active node in the browser DOM tree. A `StaleElementReferenceException` triggers when:\n\n1. **DOM Node Replaced**: React, Vue, or Angular re-renders the element during a virtual DOM reconciliation.\n2. **Navigation Occurred**: The page refreshed or transitioned via client-side routing.\n\nEven if the element looks identical on screen, its internal memory address in the browser engine has changed.",
                        keyTakeaway = "Wrap element access in dynamic Proxy locators or explicit retry wrappers that re-query the DOM upon staleness."
                    )
                ),
                mcqs = listOf(
                    Question(
                        id = "sel_mcq_1",
                        lessonId = lessonId,
                        prompt = "What is the most robust way to handle StaleElementReferenceException in an enterprise test framework?",
                        options = listOf(
                            "Use dynamic By locator resolution on every interaction or wrap calls in an explicit FluentWait retry policy",
                            "Add Thread.sleep(2000) before clicking the element",
                            "Switch from ChromeDriver to FirefoxDriver",
                            "Increase the implicit wait timeout on the WebDriver instance"
                        ),
                        correctAnswerHash = "b98dafa8f21e5f3bbd18f51950e932944cae97a51c4a04d3e5a528e1c66779ee",
                        correctOptionIndex = 0,
                        explanationHint = "Implicit waits and sleeps do not resolve detached pointers; re-querying the locator with FluentWait retry guarantees fresh DOM reference.",
                        difficulty = "Senior",
                        companyTag = "Amazon"
                    )
                ),
                interviewQuestions = listOf(
                    InterviewQuestion(
                        id = "sel_iq_1",
                        lessonId = lessonId,
                        title = "Scale a Selenium Grid to 500 Concurrent Nodes on Kubernetes",
                        targetCompany = "Amazon / Salesforce",
                        roleLevel = "Staff SDET / Infrastructure Architect",
                        problemStatement = "Design an auto-scaling Selenium Grid on Kubernetes that handles 500 concurrent browser sessions during nightly test runs without browser pod starvation or memory leaks.",
                        timeEstimateMinutes = 20,
                        keyTalkingPoints = listOf(
                            "Event-driven architecture with KEDA auto-scaling based on queue depth.",
                            "Shared memory (/dev/shm) volume allocation to prevent Chromium crashes.",
                            "Session timeout and zombie container reaping.",
                            "Video and log streaming to S3/GCS."
                        ),
                        modelAnswer = """
### High-Scale Architecture:
1. **Queue-Based Autoscaling**: Deploy Selenium Event-Bus and Router. Use KEDA to monitor pending session requests in the Grid queue and scale Chrome/Firefox node pods dynamically.
2. **IPC Memory Fix**: Mount an `emptyDir` memory volume at `/dev/shm` (min 2GB) to eliminate Chromium renderer crashes during heavy DOM rendering.
3. **Graceful Node Drainage**: Configure pods with `node-drain-timeout` to ensure active tests finish before Kubernetes kills the container.
                        """.trimIndent(),
                        followUpQuestions = listOf(
                            "Why does Chromium crash if /dev/shm is limited to the default 64MB Docker size?"
                        )
                    )
                )
            )

            // ----------------------------------------------------------------
            // SDET: LLM & AI QUALITY ENGINEERING
            // ----------------------------------------------------------------
            lessonId.startsWith("sdet_llm_testing") -> TopicContent(
                lessonId = lessonId,
                title = "LLM & GenAI Quality Engineering: RAG & Prompt Safety",
                overview = "How modern Quality Engineers test non-deterministic AI systems: RAG retrieval benchmarks, hallucination detection heuristics, and red-teaming prompt injection defenses.",
                conceptSteps = listOf(
                    ConceptStep(
                        stepNumber = 1,
                        title = "Step 1: Testing Non-Deterministic Output",
                        summary = "Why exact string matching fails and how semantic similarity scoring works.",
                        detailedExplanation = "Traditional unit tests verify deterministic inputs: `f(x) == y`. Generative AI outputs vary across temperatures. QE teams evaluate models using:\n\n1. **Semantic Embedding Similarity**: Measuring cosine distance between output embeddings and ground truth.\n2. **LLM-as-a-Judge**: Utilizing a high-reasoning teacher model (e.g. Claude 3.5 Sonnet / GPT-4o) with strict evaluation rubrics.\n3. **Faithfulness & Context Relevance**: In RAG, verifying that 100% of claims made in the answer are grounded in retrieved context documents.",
                        keyTakeaway = "Measure RAG quality across 3 axes: Context Precision, Context Recall, and Answer Faithfulness."
                    )
                ),
                mcqs = listOf(
                    Question(
                        id = "llm_mcq_1",
                        lessonId = lessonId,
                        prompt = "In evaluating a RAG pipeline, what metric directly measures whether the model hallucinated information outside the retrieved documents?",
                        options = listOf(
                            "Answer Faithfulness / Groundedness",
                            "BLEU Score",
                            "Perplexity",
                            "Token Latency"
                        ),
                        correctAnswerHash = "b98dafa8f21e5f3bbd18f51950e932944cae97a51c4a04d3e5a528e1c66779ee",
                        correctOptionIndex = 0,
                        explanationHint = "Faithfulness measures the percentage of claims in the generated answer that can be inferred directly from the context documents.",
                        difficulty = "Staff",
                        companyTag = "Anthropic"
                    )
                ),
                interviewQuestions = listOf(
                    InterviewQuestion(
                        id = "llm_iq_1",
                        lessonId = lessonId,
                        title = "Design an Automated Regression & Safety Pipeline for an AI Agent",
                        targetCompany = "Google DeepMind / Meta",
                        roleLevel = "Staff AI Quality Engineer",
                        problemStatement = "Your company is deploying an autonomous customer service agent with tool-calling capabilities. Design an automated CI/CD evaluation test suite that prevents prompt injection attacks, tool misuse, and data leakage.",
                        timeEstimateMinutes = 25,
                        keyTalkingPoints = listOf(
                            "Golden test evaluation dataset with calibrated human baselines.",
                            "Automated red-teaming adversarial fuzzer generating prompt injections.",
                            "Deterministic tool schema parameter validation before API invocation.",
                            "Budget and recursion depth bounds."
                        ),
                        modelAnswer = """
### Multi-Tiered AI Quality Framework:
1. **Adversarial Fuzzing**: Run automated red-teaming test suites that attempt jailbreaks (e.g. DAN, base64 obfuscation, indirect injection).
2. **Schema & Argument Contract Tests**: Unit test the tool calling router with malformed and boundary arguments to ensure graceful recovery.
3. **Dual-Model Judge Consensus**: For high-risk outputs, run dual-model evaluation rubrics assessing Toxicity, Hallucination, and Safety compliance.
                        """.trimIndent(),
                        followUpQuestions = listOf(
                            "How do you handle statistical variance across repeated evaluation runs?"
                        )
                    )
                )
            )

            // ----------------------------------------------------------------
            // SDET: CODING QUESTIONS FOR SDETS
            // ----------------------------------------------------------------
            lessonId.startsWith("sdet_coding") -> TopicContent(
                lessonId = lessonId,
                title = "High-Yield Coding Drills for Senior SDETs",
                overview = "Master core algorithmic patterns frequently asked in Google, Meta, and Amazon SDET interviews: DOM tree traversals, test log parsing sliding windows, and retry queues.",
                conceptSteps = listOf(
                    ConceptStep(
                        stepNumber = 1,
                        title = "Step 1: DOM Hierarchy Traversal (BFS vs DFS)",
                        summary = "Navigating component trees to locate dynamic locators without brittle XPath.",
                        detailedExplanation = "SDET interviews frequently test tree algorithms using the browser DOM tree as the problem domain. When searching for elements:\n\n- **BFS (Breadth-First Search)**: Ideal for finding shallowest matching elements or inspecting sibling elements.\n- **DFS (Depth-First Search)**: Best for ancestor/descendant relationships and calculating component depth.",
                        keyTakeaway = "Tree problems in SDET interviews are LeetCode BFS/DFS problems mapped to DOM nodes."
                    )
                ),
                mcqs = listOf(
                    Question(
                        id = "cd_mcq_1",
                        lessonId = lessonId,
                        prompt = "What is the space complexity of Breadth-First Search (BFS) on a balanced DOM tree with N total nodes and branching factor B?",
                        options = listOf(
                            "O(W) where W is the maximum width of the tree (up to O(N) at the leaf level)",
                            "O(1) constant space",
                            "O(log N) always",
                            "O(N^2) quadratic space"
                        ),
                        correctAnswerHash = "b98dafa8f21e5f3bbd18f51950e932944cae97a51c4a04d3e5a528e1c66779ee",
                        correctOptionIndex = 0,
                        explanationHint = "BFS maintains a queue of all nodes at the current level. For a balanced tree, the bottom level contains approximately N/2 nodes.",
                        difficulty = "Medium",
                        companyTag = "Meta"
                    )
                ),
                interviewQuestions = listOf(
                    InterviewQuestion(
                        id = "cd_iq_1",
                        lessonId = lessonId,
                        title = "Find the Lowest Common Ancestor (LCA) of Two Elements in a DOM Tree",
                        targetCompany = "Google / Meta",
                        roleLevel = "Senior SDET (L5)",
                        problemStatement = "Given the root of an HTML DOM tree and two elements nodeA and nodeB, write an algorithm to find their lowest common ancestor element.",
                        timeEstimateMinutes = 20,
                        keyTalkingPoints = listOf(
                            "Bottom-up post-order traversal.",
                            "Base cases: root is null, root equals nodeA, or root equals nodeB.",
                            "If left and right subtrees both return non-null, current root is the LCA."
                        ),
                        modelAnswer = """
### Optimal Recursive LCA Algorithm:
```python
def find_lca(root, node_a, node_b):
    if not root or root == node_a or root == node_b:
        return root
    
    left = find_lca(root.left, node_a, node_b)
    right = find_lca(root.right, node_a, node_b)
    
    if left and right:
        return root  # node_a and node_b are in different subtrees
    return left if left else right
```
**Complexity**: O(N) time where N is nodes in DOM tree; O(H) call stack auxiliary space.
                        """.trimIndent(),
                        followUpQuestions = listOf(
                            "How would you optimize this if nodes have parent pointers (node.parent)?"
                        )
                    )
                )
            )

            // ----------------------------------------------------------------
            // SDET: PSEUDOCODES & TEST ARCHITECTURE
            // ----------------------------------------------------------------
            lessonId.startsWith("sdet_pseudocode") -> TopicContent(
                lessonId = lessonId,
                title = "PseudoCodes & Distributed Test Runner Architecture",
                overview = "Formulating industrial test orchestration algorithms: work-stealing schedulers, flaky test quarantine logic, and exponential backoff retry state machines.",
                conceptSteps = listOf(
                    ConceptStep(
                        stepNumber = 1,
                        title = "Step 1: Work-Stealing Distributed Test Scheduler",
                        summary = "Why static test partitioning creates worker stragglers and how work-stealing eliminates idle CI time.",
                        detailedExplanation = "Static partitioning splits 100 tests into 4 chunks of 25. If one chunk contains three long 2-minute tests, 3 workers finish in 30 seconds and sit idle while worker 4 finishes in 6 minutes.\n\n**Work-Stealing Architecture**:\n- Central queue maintains prioritized test items (historically slow tests scheduled first).\n- Workers pull tests dynamically via atomic deque operations upon completing previous runs.\n- Ensures near-100% CPU utilization across all CI nodes.",
                        keyTakeaway = "Always schedule longest-running tests first (LPT algorithm) with dynamic work stealing."
                    )
                ),
                mcqs = listOf(
                    Question(
                        id = "ps_mcq_1",
                        lessonId = lessonId,
                        prompt = "In a distributed test runner, why is Longest Processing Time (LPT) greedy scheduling optimal?",
                        options = listOf(
                            "Scheduling heavy tests first minimizes total makespan by filling trailing idle time with quick unit tests",
                            "It guarantees zero memory usage on worker nodes",
                            "It prevents Chromium browser from crashing",
                            "It eliminates the need for test retries"
                        ),
                        correctAnswerHash = "b98dafa8f21e5f3bbd18f51950e932944cae97a51c4a04d3e5a528e1c66779ee",
                        correctOptionIndex = 0,
                        explanationHint = "LPT scheduling prevents long stragglers from extending total CI pipeline completion time.",
                        difficulty = "Senior",
                        companyTag = "Uber"
                    )
                ),
                interviewQuestions = listOf(
                    InterviewQuestion(
                        id = "ps_iq_1",
                        lessonId = lessonId,
                        title = "Write PseudoCode for an Automated Flaky Test Quarantine System",
                        targetCompany = "Google / Uber",
                        roleLevel = "Staff Test Infrastructure Engineer",
                        problemStatement = "Write formal pseudocode for a production CI gate that evaluates test results, identifies flaky behavior (flips between pass and fail on identical commits), and automatically quarantines tests to prevent breaking master builds.",
                        timeEstimateMinutes = 20,
                        keyTalkingPoints = listOf(
                            "Statistical confidence window (e.g. last 100 runs).",
                            "Quarantine state transition: ACTIVE -> FLAKY_CANDIDATE -> QUARANTINED.",
                            "Automated Jira ticket generation with trace attachments."
                        ),
                        modelAnswer = """
### Flaky Quarantine Algorithm (PseudoCode):
```text
FUNCTION evaluate_test_result(test_id, commit_hash, status):
    history = db.get_recent_runs(test_id, limit=50)
    flip_count = calculate_state_transitions(history)
    
    IF flip_count >= FLAKE_THRESHOLD:
        IF test.status != 'QUARANTINED':
            test.status = 'QUARANTINED'
            alert_oncall_team(test_id, history)
            create_jira_issue(test_id, "Flaky rate exceeded 10%")
        RETURN 'NON_BLOCKING_WARNING'
    ELSE:
        RETURN status
```
                        """.trimIndent(),
                        followUpQuestions = listOf(
                            "How do you automatically de-quarantine a test once a developer patches the race condition?"
                        )
                    )
                )
            )

            // ----------------------------------------------------------------
            // FALLBACK TO CPYTHON (DEVELOPER)
            // ----------------------------------------------------------------
            lessonId.startsWith("py") -> TopicContent(
                lessonId = lessonId,
                title = "CPython Lifecycle, PyObject & Memory Architecture",
                overview = "A rigorous, step-by-step masterclass on how CPython translates source code into bytecode, executes it on a stack virtual machine, and manages small objects with the PyMalloc allocator.",
                conceptSteps = listOf(
                    ConceptStep(
                        stepNumber = 1,
                        title = "Step 1: The 4-Stage CPython Compilation Lifecycle",
                        summary = "Traces code execution from raw text characters to VM evaluation in ceval.c.",
                        detailedExplanation = "CPython executes Python scripts via a 4-stage pipeline:\n\n1. **Tokenization (Lexer)**: Scans characters into lexical tokens and maintains indentation stacks to emit INDENT/DEDENT tokens.\n2. **Parsing (PEG Parser)**: Validates formal grammar (Python 3.9+ uses PEG parser) to produce an Abstract Syntax Tree (AST).\n3. **Bytecode Compilation**: The Control Flow Graph (CFG) and peephole optimizer fold constants (e.g. 2 + 3 -> 5) and emit bytecode instructions (.pyc files in __pycache__).\n4. **Virtual Machine Execution**: The compiled bytecode is evaluated in `_PyEval_EvalFrameDefault` inside CPython's `ceval.c`. The VM is a stack-based evaluator.",
                        architecturalDiagram = "Source .py -> Tokenizer -> AST -> Bytecode (.pyc) -> CPython VM (ceval.c Stack Evaluation)",
                        keyTakeaway = "Python is compiled to bytecode before interpretation. Bytecode instructions manipulate value pointers on an evaluation frame stack."
                    ),
                    ConceptStep(
                        stepNumber = 2,
                        title = "Step 2: Low-Level PyObject & PyVarObject Structs",
                        summary = "Everything in Python is a C structure pointer containing reference counts and type descriptors.",
                        detailedExplanation = "In CPython, every object is rooted in the `PyObject` structure:\n\n- `ob_refcnt`: Reference counter (ssize_t) for memory tracking.\n- `ob_type`: Pointer to the PyTypeObject (e.g. &PyTuple_Type or &PyList_Type).\n\nFor variable-sized containers like lists and strings, `PyVarObject` adds `ob_size`.\n\n**Immutability vs Mutability**:\n- **Immutable (tuples, strings, ints)**: Memory allocated contiguously once. Modifying requires allocating a completely new object in memory.\n- **Mutable (lists, dicts)**: The struct holds a pointer to a dynamically resizable array of `PyObject*` pointers.",
                        codeSnippet = """
# Low-level memory identity check
a = (1, 2)
b = (1, 2)
print(a is b)  # False: Distinct heap allocations

# Small integer interning (-5 to 256)
x = 100
y = 100
print(x is y)  # True: CPython pre-allocates an array of small PyLongObject singletons
                        """.trimIndent(),
                        keyTakeaway = "Tuples are immutable contiguous arrays. Mutating strings or tuples in loops causes O(N^2) allocations."
                    ),
                    ConceptStep(
                        stepNumber = 3,
                        title = "Step 3: The PyMalloc Hierarchical Allocator",
                        summary = "Custom allocator optimized for small allocations (<= 512 bytes) to bypass kernel malloc syscalls.",
                        detailedExplanation = "Calling system `malloc()` for every small object creates massive OS context switch overhead and memory fragmentation. CPython implements **PyMalloc** for objects <= 512 bytes:\n\n- **Arenas (256 KB)**: Slices of memory obtained from the OS.\n- **Pools (4 KB)**: Subdivisions of arenas sized to match system virtual memory pages. Each pool holds blocks of a single uniform size class (multiples of 8 or 16 bytes).\n- **Blocks (8B to 512B)**: Discrete chunks where actual `PyObject` payloads reside.\n\nObjects larger than 512 bytes bypass PyMalloc and call standard system `malloc()` directly.",
                        architecturalDiagram = "OS Heap -> Arena (256KB) -> Pool (4KB, Page Size) -> Block (<= 512B Uniform Size Class)",
                        keyTakeaway = "PyMalloc eliminates OS kernel syscall overhead for high-frequency small object allocations."
                    )
                ),
                mcqs = listOf(
                    Question(
                        id = "py_mcq_1",
                        lessonId = lessonId,
                        prompt = "What is the primary architectural purpose of CPython's PyMalloc allocator?",
                        options = listOf(
                            "Avoid kernel malloc/free syscall overhead for objects <= 512 bytes",
                            "Compile Python bytecode into machine instructions",
                            "Prevent deadlocks in multi-threaded asyncio code",
                            "Automatically serialize Python objects into JSON"
                        ),
                        correctAnswerHash = "b98dafa8f21e5f3bbd18f51950e932944cae97a51c4a04d3e5a528e1c66779ee",
                        correctOptionIndex = 0,
                        explanationHint = "PyMalloc manages 256KB Arenas and 4KB Pools to batch small memory requests, bypassing OS kernel context switches.",
                        difficulty = "Senior",
                        companyTag = "Google"
                    )
                ),
                interviewQuestions = listOf(
                    InterviewQuestion(
                        id = "py_iq_1",
                        lessonId = lessonId,
                        title = "Reverse a Singly Linked List: Iterative vs Recursive Stack Analysis",
                        targetCompany = "Meta / Google",
                        roleLevel = "Senior Software Engineer (L5-L6)",
                        problemStatement = "Given the head of a singly linked list, reverse the list in-place and return its new head. Analyze the low-level stack frame implications of both iterative and recursive implementations in a managed runtime.",
                        timeEstimateMinutes = 15,
                        keyTalkingPoints = listOf(
                            "Iterative 3-pointer manipulation (prev, curr, next_node) achieves O(1) auxiliary space.",
                            "In-place pointer reversal avoids heap allocations and preserves object IDs.",
                            "Recursive traversal incurs O(N) auxiliary space on the call stack due to PyFrameObject allocation.",
                            "Risk of RecursionError when exceeding sys.getrecursionlimit() (default 1000)."
                        ),
                        modelAnswer = """
### Architectural Approach & Tradeoffs

1. **Iterative In-Place Reversal (Optimal)**:
   - We maintain three reference pointers: `prev` (None), `curr` (head), and `next_node` (temporary).
   - In each iteration, we store `curr.next` in `next_node`, reverse `curr.next = prev`, and advance `prev` and `curr`.
   - **Complexity**: O(N) time, O(1) space. Zero heap reallocations.

2. **Recursive Traversal (Stack-Heavy)**:
   - Recursively reaches the tail: `new_head = reverse(head.next)`.
   - On the return cascade: `head.next.next = head` and `head.next = None`.
   - **Complexity**: O(N) time, but O(N) auxiliary stack space. In CPython, each call pushes a complete `PyFrameObject` containing local namespaces and evaluation stack. If N > 1000, Python raises a `RecursionError`.

### Production Conclusion:
Always deploy the iterative 3-pointer solution in production systems to guarantee O(1) memory stability and eliminate stack-overflow vulnerability.
                        """.trimIndent(),
                        codeSolution = """
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

def reverse_list_iterative(head: ListNode) -> ListNode:
    prev = None
    curr = head
    while curr:
        next_node = curr.next
        curr.next = prev
        prev = curr
        curr = next_node
    return prev
                        """.trimIndent(),
                        followUpQuestions = listOf(
                            "How would you reverse a sub-portion of the linked list between positions m and n in a single pass?",
                            "How does Python's garbage collector handle circular references if a linked list node points back to an earlier ancestor?"
                        )
                    )
                )
            )

            // ----------------------------------------------------------------
            // DEFAULT (AGENTIC AI & DISTRIBUTED SYSTEMS)
            // ----------------------------------------------------------------
            else -> TopicContent(
                lessonId = lessonId,
                title = "The Agentic Mindset, Autonomous Loops & Guardrails",
                overview = "A masterclass on architecting production AI agents: distinguishing casual chatbot prompting from deterministic perception-action loops, tool calling schemas, and recursion depth guardrails.",
                conceptSteps = listOf(
                    ConceptStep(
                        stepNumber = 1,
                        title = "Step 1: Casual Chatbot Prompting vs. Agentic Engineering",
                        summary = "Why static forward-pass completions are obsolete compared to autonomous tool-using agents.",
                        detailedExplanation = "Non-Agentic AI takes a prompt and produces a single response without verification. In contrast, an **Agentic System** possesses:\n\n1. **Agency**: Autonomously proposes and invokes external tools (APIs, Shell commands, databases).\n2. **Observation**: Ingests tool execution results, parsing standard output and compiler errors.\n3. **Self-Correction**: Reasons about test failures and iteratively edits code until the goal is achieved.",
                        architecturalDiagram = "Human Goal -> Agent (Reason) -> Tool Call (Act) -> Sandbox Execution (Observe) -> Self-Correction Loop",
                        keyTakeaway = "An agentic engineer treats the LLM as a high-speed reasoning engine integrated with deterministic tools and automated feedback loops."
                    )
                ),
                mcqs = listOf(
                    Question(
                        id = "ai_mcq_1",
                        lessonId = lessonId,
                        prompt = "What guardrail is mandatory to prevent an autonomous coding agent from running in an infinite non-terminating loop?",
                        options = listOf("Maximum recursion depth / iteration limit", "Increasing LLM temperature", "Removing tool calling definitions", "Using streaming responses"),
                        correctAnswerHash = "b98dafa8f21e5f3bbd18f51950e932944cae97a51c4a04d3e5a528e1c66779ee",
                        correctOptionIndex = 0,
                        explanationHint = "Enforcing a hard turn limit (e.g. max 25 iterations) stops runaway resource exhaustion.",
                        difficulty = "Medium",
                        companyTag = "Anthropic"
                    )
                ),
                interviewQuestions = listOf(
                    InterviewQuestion(
                        id = "ai_iq_1",
                        lessonId = lessonId,
                        title = "Design a Resilient Tool-Calling Multi-Agent Orchestrator",
                        targetCompany = "Google DeepMind / OpenAI",
                        roleLevel = "Staff AI Systems Architect",
                        problemStatement = "Design an autonomous agent system that can diagnose and patch bugs in a distributed microservice codebase. How do you prevent hallucinations, ensure reproducible sandboxing, and prevent catastrophic regressions?",
                        timeEstimateMinutes = 20,
                        keyTalkingPoints = listOf(
                            "Decouple Planner/Architect subagent from Worker/Coder subagents.",
                            "Enforce ephemeral containerized sandboxes for all tool execution.",
                            "Automated verification loop: compile, run unit tests, and check lints before human review.",
                            "Stateful graph orchestration with checkpoints for backtracking upon failed approaches."
                        ),
                        modelAnswer = """
### System Design Blueprint:
1. **Subagent Specialization**: Planner (high reasoning), Coder (focused edits), Verifier (test runner).
2. **Ephemeral MicroVM Sandboxes**: Tools execute inside containerized environments with strict network egress controls.
3. **Rollback Graph**: Checkpointed states enable rolling back when an approach fails tests.
                        """.trimIndent(),
                        followUpQuestions = listOf(
                            "How do you handle context window degradation as tool observations accumulate over long trajectories?"
                        )
                    )
                )
            )
        }
    }
}
