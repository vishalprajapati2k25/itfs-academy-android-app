package com.itfreesource.academy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.LeaderboardEntry
import com.itfreesource.academy.ui.theme.*

@Composable
fun LeaderboardScreen(
    entries: List<LeaderboardEntry>,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isLight) DuoGrayBackground else DuoDarkBackground),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        // League Header Banner
        item {
            Card(
                backgroundColor = DuoPurple,
                shape = RoundedCornerShape(20.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "💎", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Diamond League",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = DuoWhite
                    )
                    Text(
                        text = "Top 3 advance to Master Tier • Resets in 3 days",
                        fontSize = 12.sp,
                        color = DuoWhite.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Leaderboard List
        items(entries) { entry ->
            val isUser = entry.isCurrentUser
            val cardBg = when {
                isUser && isLight -> DuoGreenLight
                isUser && !isLight -> DuoDarkCard
                isLight -> DuoWhite
                else -> DuoDarkSurface
            }
            val borderCol = if (isUser) DuoGreen else if (isLight) DuoGrayBorder else DuoDarkBorder

            Card(
                backgroundColor = cardBg,
                shape = RoundedCornerShape(16.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(if (isUser) 2.dp else 1.dp, borderCol, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Rank Indicator
                        val rankEmoji = when (entry.rank) {
                            1 -> "🥇"
                            2 -> "🥈"
                            3 -> "🥉"
                            else -> "${entry.rank}"
                        }
                        Text(
                            text = rankEmoji,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.width(32.dp),
                            color = if (isLight) DuoDarkText else DuoDarkTextPrimary
                        )

                        Text(text = entry.avatarEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = entry.username,
                                fontWeight = if (isUser) FontWeight.Black else FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isUser) DuoGreenDark else if (isLight) DuoDarkText else DuoDarkTextPrimary
                            )
                            Text(
                                text = "🔥 ${entry.streak} day streak",
                                fontSize = 11.sp,
                                color = DuoOrange
                            )
                        }
                    }

                    Text(
                        text = "${entry.xpPoints} XP",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = DuoBlue
                    )
                }
            }
        }
    }
}
