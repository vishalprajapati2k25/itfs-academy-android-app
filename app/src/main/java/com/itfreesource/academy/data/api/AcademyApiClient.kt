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
 * Delivers step-by-step concept tutorials, MCQs, and FAANG Long Answer interview questions.
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
        // Generates the comprehensive 3-pillar learning payload: Concepts, MCQs, Long Answer Interview
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

    // ------------------------------------------------------------------------
    // HIGH-YIELD ACADEMY DATA SEED (Extracted from Real Academy Markdown Bank)
    // ------------------------------------------------------------------------
    fun getFallbackCourses(): List<Course> {
        return listOf(
            Course(
                id = "agentic-engineering",
                title = "Agentic AI & Multi-Agent Swarms",
                category = CourseCategory.AI_AGENTIC,
                iconEmoji = "🤖",
                description = "Master autonomous reasoning loops, tool calling schemas, long-term memory, state graphs, and halting condition guardrails.",
                levelsCount = 7,
                xpReward = 300,
                isSubscribed = true,
                progressPercent = 0.45f,
                difficulty = "Staff / L6 Architecture",
                targetCompanies = listOf("OpenAI", "Google DeepMind", "Anthropic", "Meta"),
                interviewWeight = "Highest Frequency (2026 Hot Topic)"
            ),
            Course(
                id = "python",
                title = "CPython Internals & Low-Level Architecture",
                category = CourseCategory.BACKEND_LANGUAGES,
                iconEmoji = "🐍",
                description = "Virtual machine ceval.c execution stack, PyMalloc Arenas/Pools/Blocks, bytecodes, GIL concurrency, and descriptor protocols.",
                levelsCount = 8,
                xpReward = 280,
                isSubscribed = true,
                progressPercent = 0.70f,
                difficulty = "Senior / L5 Engineering",
                targetCompanies = listOf("Google", "Meta", "Uber", "Netflix"),
                interviewWeight = "Must-Know Systems"
            ),
            Course(
                id = "appsec",
                title = "Application Security & DevSecOps",
                category = CourseCategory.SECURITY,
                iconEmoji = "🛡️",
                description = "Threat modeling architectures, zero-trust tokens, OWASP Top 10 exploits, SSRF bypasses, and defensive cryptographic guardrails.",
                levelsCount = 8,
                xpReward = 260,
                isSubscribed = true,
                progressPercent = 0.30f,
                difficulty = "Staff Security Architect",
                targetCompanies = listOf("CrowdStrike", "Cloudflare", "Palantir", "Amazon"),
                interviewWeight = "High Yield"
            ),
            Course(
                id = "playwright",
                title = "Industrial Playwright & QE Architecture",
                category = CourseCategory.QUALITY_ENGINEERING,
                iconEmoji = "🎭",
                description = "Flaky test eradication, browser context isolation, parallel workers grid, trace viewer diagnostics, and enterprise CI test pyramids.",
                levelsCount = 6,
                xpReward = 220,
                isSubscribed = false,
                progressPercent = 0.0f,
                difficulty = "Lead / Senior SDET",
                targetCompanies = listOf("Microsoft", "Amazon", "Salesforce", "Atlassian"),
                interviewWeight = "Senior Automation"
            ),
            Course(
                id = "job-search",
                title = "FAANG System Design & Bar Raiser",
                category = CourseCategory.CAREER_NAVIGATION,
                iconEmoji = "💼",
                description = "Distributed consensus, back-of-the-envelope estimation, trade-off matrices, behavioral STAR mastery, and executive level negotiation.",
                levelsCount = 6,
                xpReward = 200,
                isSubscribed = false,
                progressPercent = 0.0f,
                difficulty = "Principal / Director",
                targetCompanies = listOf("Google", "Meta", "Amazon", "Apple", "Netflix"),
                interviewWeight = "Executive Filter"
            )
        )
    }

    fun getFallbackCurriculum(courseId: String): List<CourseSection> {
        return when (courseId) {
            "python" -> listOf(
                CourseSection(
                    sectionId = "py_sec_1",
                    courseId = "python",
                    title = "Unit 1: CPython VM & Memory Model",
                    description = "Understand low-level PyObject, PyMalloc allocators, and execution loops.",
                    order = 1,
                    lessons = listOf(
                        LessonNode(
                            id = "py_l1",
                            sectionId = "py_sec_1",
                            title = "Python Fundamentals & CPython Lifecycle",
                            subtitle = "Tokenization, AST, ceval.c stack, and PyVarObject",
                            order = 1,
                            estMinutes = 15,
                            difficulty = "Senior",
                            interviewTopicsCount = 2,
                            mcqCount = 3,
                            isCompleted = true
                        ),
                        LessonNode(
                            id = "py_l2",
                            sectionId = "py_sec_1",
                            title = "Data Structures & Memory Representations",
                            subtitle = "Lists O(1) resizing, Hash Tables, and Collision resolution",
                            order = 2,
                            estMinutes = 20,
                            difficulty = "Staff",
                            interviewTopicsCount = 3,
                            mcqCount = 3,
                            isCompleted = false
                        ),
                        LessonNode(
                            id = "py_l3",
                            sectionId = "py_sec_1",
                            title = "PyMalloc Allocator: Arenas, Pools & Blocks",
                            subtitle = "Small object allocation lifecycle (<= 512 bytes)",
                            order = 3,
                            estMinutes = 18,
                            difficulty = "Senior",
                            interviewTopicsCount = 2,
                            mcqCount = 3,
                            isCompleted = false
                        )
                    )
                ),
                CourseSection(
                    sectionId = "py_sec_2",
                    courseId = "python",
                    title = "Unit 2: Concurrency & AsyncIO Internals",
                    description = "Event loop mechanics, cooperative multitasking, and GIL circumvention.",
                    order = 2,
                    lessons = listOf(
                        LessonNode(
                            id = "py_l4",
                            sectionId = "py_sec_2",
                            title = "The GIL & Multi-Threaded CPU Bottlenecks",
                            subtitle = "Thread switching, I/O multiplexing vs multiprocessing",
                            order = 1,
                            estMinutes = 15,
                            difficulty = "Senior",
                            interviewTopicsCount = 2,
                            mcqCount = 3,
                            isCompleted = false
                        ),
                        LessonNode(
                            id = "py_l5",
                            sectionId = "py_sec_2",
                            title = "AsyncIO Event Loop Architecture",
                            subtitle = "Epoll/kqueue selectors, Task futures, and Coroutine generators",
                            order = 2,
                            estMinutes = 25,
                            difficulty = "Staff",
                            interviewTopicsCount = 3,
                            mcqCount = 3,
                            isCompleted = false
                        )
                    )
                )
            )
            "agentic-engineering" -> listOf(
                CourseSection(
                    sectionId = "ai_sec_1",
                    courseId = "agentic-engineering",
                    title = "Unit 1: The Agentic Mindset & Decision Matrix",
                    description = "Transitioning from casual prompting to deterministic autonomous agent loops.",
                    order = 1,
                    lessons = listOf(
                        LessonNode(
                            id = "ai_l1",
                            sectionId = "ai_sec_1",
                            title = "Casual Prompting vs. Agentic Engineering",
                            subtitle = "Deterministic vs probabilistic decision matrices",
                            order = 1,
                            estMinutes = 15,
                            difficulty = "Staff",
                            interviewTopicsCount = 2,
                            mcqCount = 3,
                            isCompleted = true
                        ),
                        LessonNode(
                            id = "ai_l2",
                            sectionId = "ai_sec_1",
                            title = "The Perception-Action-Observation Loop (ReAct)",
                            subtitle = "Tool call schemas, sandboxes, and structured outputs",
                            order = 2,
                            estMinutes = 18,
                            difficulty = "Senior",
                            interviewTopicsCount = 2,
                            mcqCount = 3,
                            isCompleted = false
                        ),
                        LessonNode(
                            id = "ai_l3",
                            sectionId = "ai_sec_1",
                            title = "Halting Conditions, Safety & Death Loops",
                            subtitle = "Recursion depth guards and CI/CD guardrails",
                            order = 3,
                            estMinutes = 20,
                            difficulty = "Staff",
                            interviewTopicsCount = 3,
                            mcqCount = 3,
                            isCompleted = false
                        )
                    )
                )
            )
            else -> listOf(
                CourseSection(
                    sectionId = "${courseId}_sec_1",
                    courseId = courseId,
                    title = "Unit 1: Foundations & Architecture",
                    description = "Core mental models and production failure modes.",
                    order = 1,
                    lessons = listOf(
                        LessonNode(
                            id = "${courseId}_l1",
                            sectionId = "${courseId}_sec_1",
                            title = "Architecture Principles & System Boundaries",
                            subtitle = "High-frequency interview questions and design trade-offs",
                            order = 1,
                            estMinutes = 15,
                            difficulty = "Senior",
                            interviewTopicsCount = 2,
                            mcqCount = 3,
                            isCompleted = false
                        )
                    )
                )
            )
        }
    }

    fun getFallbackTopicContent(lessonId: String): TopicContent {
        if (lessonId.startsWith("py")) {
            return TopicContent(
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
                        prompt = "Which CPython memory allocator hierarchy handles small objects of size <= 512 bytes?",
                        options = listOf("PyMalloc", "Jemalloc", "Glibc Malloc", "TCMalloc"),
                        correctAnswerHash = "7f7f02b1154c1fbc9c0953a6a125740fc5040e34b9d09c6ebfa9b7ce2b800ca8",
                        correctOptionIndex = 0,
                        explanationHint = "PyMalloc manages Arenas (256KB) and Pools (4KB) specifically for fast micro-allocations.",
                        distractorRationale = mapOf(
                            1 to "Jemalloc is used by Rust and FreeBSD, but is not Python's default small-object allocator.",
                            2 to "Glibc Malloc is the standard OS allocator used only for objects > 512 bytes.",
                            3 to "TCMalloc is Google's thread-caching allocator, not built into upstream CPython."
                        ),
                        difficulty = "Medium",
                        companyTag = "Google"
                    ),
                    Question(
                        id = "py_mcq_2",
                        lessonId = lessonId,
                        prompt = "Why does string concatenation in a loop (e.g. `s += char`) exhibit O(N^2) time complexity?",
                        options = listOf(
                            "Strings are immutable, requiring allocating a new buffer and copying all preceding characters on every turn",
                            "The GIL locks execution on every string byte access",
                            "Python hashes every string before each concatenation",
                            "CPython VM garbage collector runs synchronously after every operator call"
                        ),
                        correctAnswerHash = "e9ec9562725514f7724a73752e50cf6ef0d892015fa1d5982e56cb6e3f225028",
                        correctOptionIndex = 0,
                        explanationHint = "Strings are PyASCIIObject/PyCompactUnicode immutable buffers. Changing them requires reallocating and copying preceding characters.",
                        difficulty = "Senior",
                        companyTag = "Meta"
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
                    ),
                    InterviewQuestion(
                        id = "py_iq_2",
                        lessonId = lessonId,
                        title = "Explain PyMalloc Allocator and Memory Fragmentation",
                        targetCompany = "Google / Uber",
                        roleLevel = "Staff Systems Engineer (L6)",
                        problemStatement = "A high-throughput backend service processes millions of small JSON dictionaries per minute. Profiling reveals high resident memory (RSS) that never returns to the operating system even after processing finishes. Explain why this happens under CPython's PyMalloc and how you mitigate it.",
                        timeEstimateMinutes = 20,
                        keyTalkingPoints = listOf(
                            "PyMalloc manages memory in 256KB Arenas, 4KB Pools, and <= 512B Blocks.",
                            "An Arena can only be released back to the OS via free() if EVERY SINGLE POOL inside it is completely empty.",
                            "A single surviving 32-byte object in an Arena pins the entire 256KB arena in RSS.",
                            "Mitigation: multiprocessing pool termination, batch chunking, or custom allocators (Jemalloc via LD_PRELOAD)."
                        ),
                        modelAnswer = """
### Root Cause: PyMalloc Arena Pinning & OS Fragmentation

1. **Arena Release Constraint**:
   - CPython allocates memory in 256KB Arenas. An Arena is only surrendered to the OS via `free()` when **all 64 Pools (4KB each) within it are 100% unoccupied**.
   - If a long-lived object (such as a cached session token or global registry entry) resides in a pool, that single 64-byte allocation prevents the entire 256KB arena from returning to the OS.

2. **Remediation Engineering**:
   - **Worker Recycling**: Use Gunicorn/Uvicorn `--max-requests 5000` to periodically recycle OS worker processes, returning pinned RSS to the host.
   - **Subprocess Isolation**: Offload heavy JSON parsing batches to `multiprocessing.Pool` workers that terminate upon task completion.
   - **System Allocator Swap**: Preload **Jemalloc** (`LD_PRELOAD=/usr/lib/libjemalloc.so`) which provides superior aggressive background arena purging compared to standard glibc.
                        """.trimIndent(),
                        followUpQuestions = listOf(
                            "What is the difference between generational garbage collection (gc module) and reference counting in this scenario?",
                            "How does Python 3.12+ immortal objects PEP 683 impact multi-process copy-on-write memory sharing?"
                        )
                    )
                )
            )
        } else {
            // Agentic AI Track
            return TopicContent(
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
                    ),
                    ConceptStep(
                        stepNumber = 2,
                        title = "Step 2: The ReAct Operational Loop",
                        summary = "Perceive -> Reason -> Act -> Observe execution cycle.",
                        detailedExplanation = "In the ReAct pattern:\n\n- **Thought**: The model generates internal chain-of-thought planning.\n- **Action**: The model outputs a structured tool call JSON matching an exact schema.\n- **Observation**: The system executes the tool in a sandbox and returns the stdout/stderr into the context window as high-priority feedback.",
                        keyTakeaway = "Tool schemas must declare strict required parameters and clear error responses so the model can recover gracefully."
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
                            "Enforce ephemeral containerized sandboxes (Docker / gVisor) for all tool execution.",
                            "Automated verification loop: compile, run unit tests, and check lints before human review.",
                            "Stateful graph orchestration with checkpoints for backtracking upon failed approaches."
                        ),
                        modelAnswer = """
### System Design Blueprint:

1. **Subagent Specialization**:
   - **Research Agent**: Read-only tools (grep, AST inspection, log parsing).
   - **Coder Agent**: Focused diff application.
   - **Verification Agent**: Test suite runner with zero git commit privileges.

2. **Deterministic Sandbox Boundary**:
   - Tools execute inside ephemeral microVMs (e.g. Firecracker or Docker).
   - Network egress is restricted to internal dependencies to prevent data exfiltration.

3. **Self-Correction & Backtracking**:
   - Maintain a directed acyclic state graph (DAG). If a proposed code edit breaks regression tests, the orchestrator rolls back the working tree and instructs the planner to formulate an alternative hypothesis.
                        """.trimIndent(),
                        followUpQuestions = listOf(
                            "How do you handle context window degradation as tool observations accumulate over long trajectories?",
                            "How do you secure LLM tool calling against indirect prompt injection embedded in external web search results?"
                        )
                    )
                )
            )
        }
    }
}
