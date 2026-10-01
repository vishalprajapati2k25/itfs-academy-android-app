package com.itfreesource.academy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itfreesource.academy.ui.theme.*

enum class DuoButtonVariant {
    PRIMARY,   // Green
    SECONDARY, // Blue
    OUTLINE,   // White/Card with gray border
    DANGER,    // Red
    GOLD       // Gold/Yellow
}

@Composable
fun DuolingoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: DuoButtonVariant = DuoButtonVariant.PRIMARY,
    enabled: Boolean = true,
    height: Dp = 52.dp,
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val (topColor, shadowColor, textColor) = when (variant) {
        DuoButtonVariant.PRIMARY -> Triple(
            if (enabled) DuoGreen else Color(0xFFE5E5E5),
            if (enabled) DuoGreenDark else Color(0xFFAFAFAF),
            if (enabled) DuoWhite else Color(0xFFAFAFAF)
        )
        DuoButtonVariant.SECONDARY -> Triple(
            if (enabled) DuoBlue else Color(0xFFE5E5E5),
            if (enabled) DuoBlueDark else Color(0xFFAFAFAF),
            if (enabled) DuoWhite else Color(0xFFAFAFAF)
        )
        DuoButtonVariant.OUTLINE -> Triple(
            if (MaterialTheme.colors.isLight) DuoWhite else DuoDarkCard,
            if (MaterialTheme.colors.isLight) DuoGrayBorder else DuoDarkBorder,
            if (MaterialTheme.colors.isLight) DuoDarkText else DuoDarkTextPrimary
        )
        DuoButtonVariant.DANGER -> Triple(
            if (enabled) DuoRed else Color(0xFFE5E5E5),
            if (enabled) DuoRedDark else Color(0xFFAFAFAF),
            if (enabled) DuoWhite else Color(0xFFAFAFAF)
        )
        DuoButtonVariant.GOLD -> Triple(
            if (enabled) DuoGold else Color(0xFFE5E5E5),
            if (enabled) DuoGoldDark else Color(0xFFAFAFAF),
            if (enabled) DuoWhite else Color(0xFFAFAFAF)
        )
    }

    val shadowHeight = if (isPressed || !enabled) 0.dp else 4.dp
    val topOffset = if (isPressed) 4.dp else 0.dp

    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(shadowColor, RoundedCornerShape(16.dp))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height - shadowHeight)
                .offset(y = topOffset)
                .background(topColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text.uppercase(),
                    color = textColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}
