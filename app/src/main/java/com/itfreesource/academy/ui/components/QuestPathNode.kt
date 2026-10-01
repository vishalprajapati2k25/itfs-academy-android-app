package com.itfreesource.academy.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.data.model.LessonNode
import com.itfreesource.academy.data.model.NodeStatus
import com.itfreesource.academy.data.model.NodeType
import com.itfreesource.academy.ui.theme.*

@Composable
fun QuestPathNode(
    node: LessonNode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    // Pulsing animation for available node
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (node.status == NodeStatus.AVAILABLE) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val (topColor, shadowColor, iconEmoji) = when (node.status) {
        NodeStatus.LOCKED -> Triple(Color(0xFFE5E5E5), Color(0xFFAFAFAF), "")
        NodeStatus.AVAILABLE -> when (node.nodeType) {
            NodeType.STANDARD_QUEST -> Triple(DuoGreen, DuoGreenDark, "⭐")
            NodeType.SPEED_BLITZ -> Triple(DuoOrange, DuoOrangeDark, "⚡")
            NodeType.MYSTERY_CHEST -> Triple(DuoPurple, DuoPurpleDark, "🎁")
            NodeType.BOSS_BATTLE -> Triple(DuoGold, DuoGoldDark, "👑")
        }
        NodeStatus.COMPLETED, NodeStatus.MASTERED -> when (node.nodeType) {
            NodeType.BOSS_BATTLE -> Triple(DuoGold, DuoGoldDark, "👑")
            NodeType.MYSTERY_CHEST -> Triple(DuoPurple, DuoPurpleDark, "💎")
            else -> Triple(DuoGreen, DuoGreenDark, "✓")
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .scale(scale)
                .clip(CircleShape)
                .background(shadowColor)
                .clickable(enabled = node.status != NodeStatus.LOCKED) { onClick() }
        ) {
            // Raised 3D tactile button surface
            Box(
                modifier = Modifier
                    .size(size)
                    .offset(y = (-4).dp)
                    .clip(CircleShape)
                    .background(topColor),
                contentAlignment = Alignment.Center
            ) {
                when (node.status) {
                    NodeStatus.LOCKED -> {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked Node",
                            tint = Color(0xFFAFAFAF),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    NodeStatus.AVAILABLE -> {
                        Text(text = iconEmoji, fontSize = 28.sp)
                    }
                    NodeStatus.COMPLETED, NodeStatus.MASTERED -> {
                        if (node.nodeType == NodeType.BOSS_BATTLE) {
                            Text(text = "👑", fontSize = 28.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed Node",
                                tint = DuoWhite,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Stars earned badge below completed node
        if (node.status == NodeStatus.COMPLETED && node.stars > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(node.stars) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = DuoGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        } else {
            Text(
                text = node.title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (node.status == NodeStatus.LOCKED) Color(0xFFAFAFAF) else DuoDarkText
            )
        }
    }
}
