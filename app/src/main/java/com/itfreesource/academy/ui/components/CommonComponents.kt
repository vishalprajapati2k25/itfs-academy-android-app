package com.itfreesource.academy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.UserProgress
import com.itfreesource.academy.ui.theme.*

@Composable
fun AppTopBar(
    progress: UserProgress,
    currentCourseTitle: String,
    currentCourseEmoji: String,
    onCourseClick: () -> Unit,
    onSecurityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight
    val bg = if (isLight) SlateCardBg else ObsidianCardBg
    val borderCol = if (isLight) SlateBorder else ObsidianBorder

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg)
            .border(1.dp, borderCol)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Active Track Selector
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onCourseClick() }
                .background(if (isLight) SlateElevatedBg else ObsidianElevatedBg)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(text = currentCourseEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (currentCourseTitle.length > 14) currentCourseTitle.take(12) + "…" else currentCourseTitle,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
            )
        }

        // Stats & Readiness Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Readiness Score Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandIndigo.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(text = "🎯", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${progress.interviewReadinessScore}% Ready",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = BrandIndigo
                )
            }

            // Streak Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(WarningAmber.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(text = "🔥", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${progress.streakDays}d",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = WarningAmber
                )
            }

            // Security Armor Shield
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SuccessEmerald.copy(alpha = 0.15f))
                    .clickable { onSecurityClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "DRM Content Armor Active",
                    tint = SuccessEmerald,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun CodeBlock(
    code: String,
    language: String = "python",
    modifier: Modifier = Modifier
) {
    Card(
        backgroundColor = Color(0xFF0F141C),
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
                Text(
                    text = "🔒 SCREEN PROTECTED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ObsidianTextMuted
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = code.trim(),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
fun CompanyBadge(
    company: String,
    modifier: Modifier = Modifier
) {
    val (bg, textColor) = when {
        company.contains("Meta", ignoreCase = true) -> Pair(Color(0xFF0668E1).copy(alpha = 0.15f), Color(0xFF3B82F6))
        company.contains("Google", ignoreCase = true) -> Pair(SuccessEmerald.copy(alpha = 0.15f), SuccessEmerald)
        company.contains("Amazon", ignoreCase = true) -> Pair(WarningAmber.copy(alpha = 0.15f), WarningAmber)
        company.contains("Netflix", ignoreCase = true) -> Pair(ErrorRose.copy(alpha = 0.15f), ErrorRose)
        else -> Pair(AccentViolet.copy(alpha = 0.15f), AccentViolet)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = company,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun RoleLevelBadge(
    roleLevel: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = roleLevel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = ObsidianTextSecondary
        )
    }
}
