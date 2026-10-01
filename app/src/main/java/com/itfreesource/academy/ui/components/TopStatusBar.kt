package com.itfreesource.academy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.UserProgress
import com.itfreesource.academy.ui.theme.*

@Composable
fun TopStatusBar(
    progress: UserProgress,
    courseTitle: String,
    courseEmoji: String,
    onCourseClick: () -> Unit,
    onSecurityShieldClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight
    val barBg = if (isLight) DuoWhite else DuoDarkSurface
    val borderCol = if (isLight) DuoGrayBorder else DuoDarkBorder

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(barBg)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Course Selector Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { onCourseClick() }
                .background(if (isLight) DuoGrayBackground else DuoDarkCard)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(text = courseEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (courseTitle.length > 12) courseTitle.take(10) + "…" else courseTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isLight) DuoDarkText else DuoDarkTextPrimary
            )
        }

        // Stats Row (Streak, Gems, Hearts, Armor Shield)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Streak
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔥", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${progress.streakDays}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = DuoOrange
                )
            }

            // Brain Gems
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💎", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${progress.brainGems}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = DuoBlue
                )
            }

            // Hearts
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "❤️", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${progress.hearts}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = DuoRed
                )
            }

            // DRM / Armor Shield Icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(DuoGreen.copy(alpha = 0.15f))
                    .clickable { onSecurityShieldClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "DRM & Screen Protection Active",
                    tint = DuoGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
