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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
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
            .background(if (isLight) SlateLightBg else ObsidianDarkBg),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        // User Header
        item {
            Card(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                shape = RoundedCornerShape(20.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(BrandIndigo.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👨‍💻", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Senior Software Engineer Candidate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                        )
                        Text(
                            text = "Preparing for L5/L6 & Staff+ Interviews",
                            fontSize = 12.sp,
                            color = BrandIndigo,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Interview Readiness Dashboard Card
        item {
            Text(
                text = "INTERVIEW READINESS DASHBOARD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                shape = RoundedCornerShape(18.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isLight) SlateBorder else ObsidianBorder, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Interview Preparedness",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                        )
                        Text(
                            text = "${progress.interviewReadinessScore}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = BrandIndigo
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = progress.interviewReadinessScore / 100f,
                        color = BrandIndigo,
                        backgroundColor = if (isLight) SlateBorder else ObsidianBorder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricBox(
                            value = "${progress.conceptsMasteredCount}",
                            label = "Concepts Mastered",
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            value = "${progress.mcqsSolvedCount}",
                            label = "MCQs Solved",
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            value = "${progress.longAnswersReviewedCount}",
                            label = "FAANG Q&As",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Content Armor & DRM Diagnostics
        item {
            Text(
                text = "CONTENT ARMOR & DRM SECURITY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SuccessEmerald,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
                shape = RoundedCornerShape(16.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SuccessEmerald.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable { onOpenSecurityAudit() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SuccessEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Security Active",
                                tint = SuccessEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Anti-Scraping Protection Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                            )
                            Text(
                                text = "FLAG_SECURE • Anti-Recompile • SHA-256 DRM",
                                fontSize = 11.sp,
                                color = SuccessEmerald,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Text(
                        text = "AUDIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy Policy Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                    text = "Academy Privacy Policy & Data Safety Disclosures",
                    fontSize = 12.sp,
                    color = BrandIndigo,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open Privacy Policy",
                    tint = BrandIndigo,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun MetricBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colors.isLight
    Card(
        backgroundColor = if (isLight) SlateElevatedBg else ObsidianElevatedBg,
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = ObsidianTextMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
