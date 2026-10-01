package com.itfreesource.academy.data.model

/**
 * CourseSection — Major thematic section along the Duolingo quest path.
 */
data class CourseSection(
    val sectionId: String,
    val courseId: String,
    val title: String,
    val description: String,
    val order: Int,
    val lessons: List<LessonNode>
)

/**
 * LessonNode — An interactive node along the winding learning trail.
 */
data class LessonNode(
    val id: String,
    val sectionId: String,
    val title: String,
    val order: Int,
    val nodeType: NodeType,
    val status: NodeStatus,
    val stars: Int = 0, // 0..3 stars
    val xpValue: Int = 20,
    val estMinutes: Int = 5
)

enum class NodeType {
    STANDARD_QUEST,   // Standard Duolingo-style micro-lessons
    SPEED_BLITZ,      // Rapid-fire timer challenge
    MYSTERY_CHEST,    // Milestone reward chest with gems
    BOSS_BATTLE       // Unit capstone exam with crown reward
}

enum class NodeStatus {
    LOCKED,      // Grayed out, inaccessible until preceding node passed
    AVAILABLE,   // Active glowing node, ready to play
    COMPLETED,   // Finished successfully with checkmark & stars
    MASTERED     // Crowned milestone
}
