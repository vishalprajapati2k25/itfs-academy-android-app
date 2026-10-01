package com.itfreesource.academy.data.model

/**
 * UserProgress — Encapsulates student streaks, XP, currency, and league status.
 */
data class UserProgress(
    val streakDays: Int = 1,
    val totalXp: Int = 120,
    val brainGems: Int = 45,
    val hearts: Int = 5,
    val maxHearts: Int = 5,
    val activeCourseId: String = "agentic-engineering",
    val currentLeague: String = "Diamond Tier",
    val leagueRank: Int = 3
)

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val avatarEmoji: String,
    val xpPoints: Int,
    val streak: Int,
    val isCurrentUser: Boolean = false
)

data class AcademyBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val unlockedDate: String = ""
)
