package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThreeDAnimatedIcon(
    emoji: String? = null,
    vectorIcon: ImageVector? = null,
    accentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    isSelected: Boolean = false,
    animationDelayMs: Int = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "3d_icon_anim_$animationDelayMs")

    // Smooth floating movement on Y axis
    val floatY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = animationDelayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    // 3D Tilt around X axis
    val rotationX by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, delayMillis = animationDelayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotationX"
    )

    // 3D Tilt around Y axis
    val rotationY by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = animationDelayMs + 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotationY"
    )

    // Breathing pulse scale
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, delayMillis = animationDelayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scalePulse"
    )

    // Dynamic glow pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, delayMillis = animationDelayMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val density = LocalDensity.current.density

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // 1. Ambient 3D Neon Backlight Orb
        Box(
            modifier = Modifier
                .size(size * 0.85f)
                .graphicsLayer {
                    translationY = floatY * 0.5f
                    scaleX = if (isSelected) scalePulse * 1.25f else scalePulse
                    scaleY = if (isSelected) scalePulse * 1.25f else scalePulse
                    alpha = glowAlpha
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.7f),
                            accentColor.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // 2. 3D Rotating Glass Platform & Depth Base
        Box(
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    this.cameraDistance = 16f * density
                    this.rotationX = rotationX
                    this.rotationY = rotationY
                    this.translationY = floatY
                    this.scaleX = if (isSelected) 1.15f else 1.0f
                    this.scaleY = if (isSelected) 1.15f else 1.0f
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                }
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.25f),
                            Color(0xFF130E26),
                            accentColor.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accentColor,
                            accentColor.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.6f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // 3. 3D Depth Cast Shadow (Layer 1 - Dark Offset)
            if (emoji != null) {
                Text(
                    text = emoji,
                    fontSize = (size.value * 0.55f).sp,
                    modifier = Modifier
                        .offset(x = 2.dp, y = 3.dp)
                        .graphicsLayer {
                            alpha = 0.35f
                        }
                )
            } else if (vectorIcon != null) {
                Icon(
                    imageVector = vectorIcon,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(size * 0.55f)
                        .offset(x = 2.dp, y = 3.dp)
                )
            }

            // 4. Main 3D Foreground Icon Layer
            if (emoji != null) {
                Text(
                    text = emoji,
                    fontSize = (size.value * 0.55f).sp,
                    modifier = Modifier.graphicsLayer {
                        shadowElevation = 8f
                    }
                )
            } else if (vectorIcon != null) {
                Icon(
                    imageVector = vectorIcon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(size * 0.55f)
                )
            }
        }
    }
}
