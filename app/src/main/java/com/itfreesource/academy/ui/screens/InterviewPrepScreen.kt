package com.itfreesource.academy.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.InterviewQuestion
import com.itfreesource.academy.ui.components.CodeBlock
import com.itfreesource.academy.ui.components.CompanyBadge
import com.itfreesource.academy.ui.components.RoleLevelBadge
import com.itfreesource.academy.ui.theme.*

@Composable
fun InterviewPrepScreen(
    onMarkReviewed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight
    var selectedCompany by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val companies = listOf("All", "Google", "Meta", "Amazon", "Uber", "Netflix", "DeepMind", "CrowdStrike")

    // High-yield FAANG interview repository
    val allQuestions = remember {
        listOf(
            InterviewQuestion(
                id = "py_iq_1",
                lessonId = "python_l1",
                title = "Reverse a Singly Linked List: Stack Analysis in Managed Runtimes",
                targetCompany = "Meta / Google",
                roleLevel = "Senior / Staff (L5-L6)",
                problemStatement = "Given the head of a singly linked list, reverse it in-place and return the new head. Compare low-level frame allocations between iterative 3-pointer and recursive techniques.",
                timeEstimateMinutes = 15,
                keyTalkingPoints = listOf(
                    "Iterative pointer reassignment achieves O(1) auxiliary space without frame allocations.",
                    "Recursive solution consumes O(N) stack memory; risks RecursionError when list exceeds call stack limits.",
                    "In-place reversal preserves underlying node memory addresses."
                ),
                modelAnswer = "Deploy an iterative 3-pointer algorithm (prev, curr, next_node). In-place reassignment guarantees O(1) auxiliary memory and avoids PyFrameObject stack exhaustion.",
                codeSolution = """
def reverse_list(head):
    prev, curr = None, head
    while curr:
        nxt = curr.next
        curr.next = prev
        prev = curr
        curr = nxt
    return prev
                """.trimIndent(),
                followUpQuestions = listOf(
                    "How would you reverse only nodes between indices m and n in one pass?",
                    "How does garbage collection behave if a reversed node introduces an object cycle?"
                )
            ),
            InterviewQuestion(
                id = "py_iq_2",
                lessonId = "python_l3",
                title = "PyMalloc Allocator: Arena Pinning & Memory Fragmentation",
                targetCompany = "Google / Uber",
                roleLevel = "Staff Systems Engineer (L6)",
                problemStatement = "A service parsing high volumes of micro-JSON objects exhibits resident memory (RSS) that never returns to the host OS. Explain why CPython's PyMalloc causes this and how you mitigate it.",
                timeEstimateMinutes = 20,
                keyTalkingPoints = listOf(
                    "PyMalloc organizes small memory in 256KB Arenas and 4KB Pools.",
                    "An Arena is surrendered to the OS only when ALL 64 pools inside it are completely free.",
                    "A single surviving 32-byte object pins the entire 256KB Arena in memory.",
                    "Mitigate with periodic worker recycling (--max-requests) or preloading Jemalloc."
                ),
                modelAnswer = "PyMalloc arenas cannot be freed piecemeal. A surviving long-lived object pins an entire 256KB arena in RSS. Mitigate by isolating parsing in worker pools or recycling processes.",
                codeSolution = """
# Gunicorn configuration to prevent RSS fragmentation:
# max_requests = 10000
# max_requests_jitter = 1000
                """.trimIndent(),
                followUpQuestions = listOf(
                    "How does Jemalloc's background arena purging differ from standard glibc malloc?"
                )
            ),
            InterviewQuestion(
                id = "ai_iq_1",
                lessonId = "ai_l1",
                title = "Design a Self-Correcting Multi-Agent Coding Orchestrator",
                targetCompany = "DeepMind / OpenAI",
                roleLevel = "Staff AI Systems Architect",
                problemStatement = "Architect an autonomous coding agent system capable of patching bugs in large codebases while strictly preventing hallucinations and execution death-loops.",
                timeEstimateMinutes = 20,
                keyTalkingPoints = listOf(
                    "Specialized subagent roles: Planner (read-only), Coder (diffing), and Verifier (test suite).",
                    "Deterministic sandbox boundary with ephemeral microVMs.",
                    "Automated verification loop: compile, run tests, and self-correct with backtracking DAG."
                ),
                modelAnswer = "Decouple planning from execution. Enforce a hard recursion depth limit, run all tools in an isolated sandbox, and backtrack across state graph checkpoints upon test regression.",
                followUpQuestions = listOf(
                    "How do you safeguard against indirect prompt injection in external tool observations?"
                )
            ),
            InterviewQuestion(
                id = "sec_iq_1",
                lessonId = "appsec_l1",
                title = "Mitigating Server-Side Request Forgery (SSRF) in Cloud Native APIs",
                targetCompany = "Amazon / CrowdStrike",
                roleLevel = "Senior Security Engineer",
                problemStatement = "A microservice accepts a webhook callback URL from users. How do you engineer defense-in-depth against SSRF targeting the cloud instance metadata service (169.254.169.254) and internal VPC services?",
                timeEstimateMinutes = 15,
                keyTalkingPoints = listOf(
                    "Enforce strict DNS resolution before HTTP connect; block private/loopback RFC 1918 CIDRs.",
                    "Guard against DNS Rebinding by pinning the resolved IP for the actual socket connection.",
                    "Disable HTTP redirect following or re-verify each redirect target IP.",
                    "Enforce IMDSv2 with session tokens and hop-limit=1."
                ),
                modelAnswer = "Resolve DNS before connection, reject private IPv4/IPv6 ranges (including 169.254.169.254 metadata), pin the resolved IP to avoid DNS rebinding, and strictly enforce IMDSv2 with session tokens.",
                followUpQuestions = listOf(
                    "What happens if an attacker supplies a decimal or hexadecimal encoded IP (e.g. 0x7f000001)?"
                )
            )
        )
    }

    val filtered = allQuestions.filter { q ->
        val matchesCompany = selectedCompany == null || selectedCompany == "All" || q.targetCompany.contains(selectedCompany!!, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || q.title.contains(searchQuery, ignoreCase = true) || q.problemStatement.contains(searchQuery, ignoreCase = true)
        matchesCompany && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isLight) SlateLightBg else ObsidianDarkBg),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        item {
            Text(
                text = "FAANG INTERVIEW SIMULATOR",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Staff & Senior Interview Q&As",
                style = MaterialTheme.typography.h2,
                color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
            )
            Text(
                text = "Real high-frequency technical and architectural interview questions with model answers.",
                fontSize = 13.sp,
                color = ObsidianTextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search interview topics (e.g. PyMalloc, SSRF, Memory...)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = ObsidianTextMuted) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Company Filter Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(companies) { comp ->
                    val isSelected = (selectedCompany == null && comp == "All") || selectedCompany == comp
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) BrandIndigo else if (isLight) SlateCardBg else ObsidianCardBg)
                            .border(1.dp, if (isSelected) BrandIndigo else if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedCompany = if (comp == "All") null else comp }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = comp,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else ObsidianTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(filtered) { q ->
            var isExpanded by remember { mutableStateOf(false) }
            var isMarked by remember { mutableStateOf(false) }

            Card(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                shape = RoundedCornerShape(16.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompanyBadge(company = q.targetCompany)
                        RoleLevelBadge(roleLevel = q.roleLevel)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = q.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = q.problemStatement,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = if (isLight) SlateTextSecondary else ObsidianTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Talking Points
                    Text(
                        text = "KEY CONCEPTS TO HIT:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningAmber
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    q.keyTalkingPoints.forEach { pt ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(text = "•", color = WarningAmber, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = pt,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Expand Model Answer Accordion
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isExpanded) BrandIndigo.copy(alpha = 0.15f) else Color(0xFF1E293B))
                            .clickable { isExpanded = !isExpanded }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpanded) "COLLAPSE MODEL ANSWER" else "REVEAL FULL ARCHITECTURAL ANSWER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isExpanded) BrandIndigo else Color.White
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Answer",
                            tint = if (isExpanded) BrandIndigo else Color.White
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            Card(
                                backgroundColor = Color(0xFF0F172A),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = q.modelAnswer,
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp,
                                        color = Color(0xFFE2E8F0)
                                    )

                                    if (!q.codeSolution.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        CodeBlock(code = q.codeSolution, language = q.codeLanguage)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (!isMarked) {
                                Button(
                                    onClick = {
                                        isMarked = true
                                        onMarkReviewed(q.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = SuccessEmerald),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "✓ MARK REVIEWED (+2% Readiness)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            } else {
                                Text(
                                    text = "✓ Mastered & marked for revision",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessEmerald
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
