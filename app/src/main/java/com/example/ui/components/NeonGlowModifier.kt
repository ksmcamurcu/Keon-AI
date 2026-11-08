package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neonGlow(
    glowColor: Color,
    glowRadius: Dp = 12.dp,
    shape: Shape = RoundedCornerShape(18.dp),
    borderWidth: Dp = 1.5.dp,
    alpha: Float = 0.6f
): Modifier {
    return this
        .drawBehind {
            val transparentColor = glowColor.copy(alpha = 0f).toArgb()
            val shadowColor = glowColor.copy(alpha = alpha).toArgb()

            drawIntoCanvas { canvas ->
                val paint = Paint()
                val frameworkPaint = paint.asFrameworkPaint()
                frameworkPaint.color = transparentColor
                frameworkPaint.setShadowLayer(
                    glowRadius.toPx().coerceAtLeast(1f),
                    0f,
                    0f,
                    shadowColor
                )

                canvas.drawRoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    radiusX = 18.dp.toPx(),
                    radiusY = 18.dp.toPx(),
                    paint = paint
                )
            }
        }
        .border(
            width = borderWidth,
            brush = Brush.linearGradient(
                colors = listOf(
                    glowColor,
                    glowColor.copy(alpha = 0.3f),
                    glowColor
                )
            ),
            shape = shape
        )
}

@Composable
fun Modifier.pulsatingNeonGlow(
    glowColor: Color,
    minAlpha: Float = 0.25f,
    maxAlpha: Float = 0.8f,
    glowRadius: Dp = 12.dp,
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 1.5.dp,
    durationMillis: Int = 2200
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "neonPulseTransition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonPulseAlpha"
    )
    val animatedRadius by infiniteTransition.animateFloat(
        initialValue = glowRadius.value * 0.5f,
        targetValue = glowRadius.value * 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonPulseRadius"
    )

    return this
        .drawBehind {
            val transparentColor = glowColor.copy(alpha = 0f).toArgb()
            val shadowColor = glowColor.copy(alpha = alpha).toArgb()

            drawIntoCanvas { canvas ->
                val paint = Paint()
                val frameworkPaint = paint.asFrameworkPaint()
                frameworkPaint.color = transparentColor
                frameworkPaint.setShadowLayer(
                    animatedRadius.dp.toPx().coerceAtLeast(1f),
                    0f,
                    0f,
                    shadowColor
                )

                canvas.drawRoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    radiusX = 16.dp.toPx(),
                    radiusY = 16.dp.toPx(),
                    paint = paint
                )
            }
        }
        .border(
            width = borderWidth,
            brush = Brush.linearGradient(
                colors = listOf(
                    glowColor.copy(alpha = alpha),
                    glowColor.copy(alpha = (alpha * 0.35f).coerceIn(0.1f, 1f)),
                    glowColor.copy(alpha = alpha)
                )
            ),
            shape = shape
        )
}

