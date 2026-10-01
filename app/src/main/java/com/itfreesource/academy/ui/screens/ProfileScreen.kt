package com.itfreesource.academy.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.UserProgress
import com.itfreesource.academy.ui.components.DuolingoButton
import com.itfreesource.academy.ui.components.DuoButtonVariant
import com.itfreesource.academy.ui.theme.*

@Composable
fun ProfileScreen(
    progress: UserProgress,
    onOpenSecurityAudit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLight = MaterialTheme.colors.isLight

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isLight) DuoGrayBackground else DuoDarkBackground),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        // User Avatar Header
        item {
            Card(
                backgroundColor = if (isLight) DuoWhite else DuoDarkSurface,
                shape = RoundedCornerShape(20.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.5.dp, if (isLight) DuoGrayBorder else DuoDarkBorder, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(DuoBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚡", fontSize = 36.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Academy Scholar",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = if (isLight) DuoDarkText else DuoDarkTextPrimary
                    )
                    Text(
                        text = "student@itfreesource.com",
                        fontSize = 13.sp,
                        color = DuoGrayText
                    )
                }
            }
        }

        // Stats Grid
        item {
            Text(
                text = "STATISTICS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = DuoBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    icon = "🔥",
                    value = "${progress.streakDays}",
                    label = "Day Streak",
                    textColor = DuoOrange,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "⚡",
                    value = "${progress.totalXp}",
                    label = "Total XP",
                    textColor = DuoGoldDark,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    icon = "💎",
                    value = "${progress.brainGems}",
                    label = "Brain Gems",
                    textColor = DuoBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "❤️",
                    value = "${progress.hearts} / ${progress.maxHearts}",
                    label = "Hearts",
                    textColor = DuoRed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Security Armor & Anti-Screen Scraping Section
        item {
            Text(
                text = "CONTENT ARMOR & DRM",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = DuoGreen,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                backgroundColor = if (isLight) DuoWhite else DuoDarkSurface,
                shape = RoundedCornerShape(18.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenSecurityAudit() }
                    .border(1.5.dp, DuoGreen.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DuoGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Security Active",
                                tint = DuoGreenDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Anti-Scraping Shield Active",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = if (isLight) DuoDarkText else DuoDarkTextPrimary
                            )
                            Text(
                                text = "FLAG_SECURE • Anti-Tamper • DRM",
                                fontSize = 12.sp,
                                color = DuoGreenDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "AUDIT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = DuoGreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Achievements / Badges
        item {
            Text(
                text = "ACADEMY BADGES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = DuoPurple,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val badges = listOf(
                Pair("🤖 Agent Pioneer", "Built first autonomous multi-agent cognitive loop"),
                Pair("🛡️ OWASP Guardian", "Scored 100% on AppSec injection drills"),
                Pair("🐍 CPython Virtuoso", "Mastered PyMalloc memory allocator internals"),
                Pair("💎 Diamond League", "Promoted to top competitive student tier")
            )

            badges.forEach { (title, desc) ->
                Card(
                    backgroundColor = if (isLight) DuoWhite else DuoDarkSurface,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, if (isLight) DuoGrayBorder else DuoDarkBorder, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎖️", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isLight) DuoDarkText else DuoDarkTextPrimary
                            )
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = DuoGrayText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Play Store Compliant Privacy Policy Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://academy.itfreesource.com/apps/itfs-academy/privacy.html")
                        )
                        context.startActivity(browserIntent)
                    }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Academy Privacy Policy & Data Safety",
                    fontSize = 13.sp,
                    color = DuoBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Open Privacy Policy",
                    tint = DuoBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: String,
    value: String,
    label: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight
    Card(
        backgroundColor = if (isLight) DuoWhite else DuoDarkSurface,
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        modifier = modifier.border(1.dp, if (isLight) DuoGrayBorder else DuoDarkBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = value,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = textColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = DuoGrayText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
