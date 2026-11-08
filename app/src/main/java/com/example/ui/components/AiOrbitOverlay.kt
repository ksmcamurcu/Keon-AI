package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AiModelInfo
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AiOrbitOverlay(
    active: Boolean,
    aiList: List<AiModelInfo>,
    modifier: Modifier = Modifier
) {
    if (!active) return

    val progress = remember { Animatable(0f) }

    LaunchedEffect(active) {
        if (active) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 4500, easing = FastOutSlowInEasing)
            )
        }
    }

    val currentProgress = progress.value
    if (currentProgress >= 1f) return

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val total = aiList.size
        val radiusMax = 160f // radius in dp
        val currentRadius = radiusMax * (1f - currentProgress * 0.8f)
        val scale = (1f - currentProgress * 0.7f).coerceAtLeast(0.2f)
        val alpha = if (currentProgress > 0.8f) (1f - currentProgress) / 0.2f else 1f

        aiList.forEachIndexed { index, model ->
            val angleOffset = (index.toFloat() / total) * 2 * Math.PI
            val rotationAngle = (currentProgress * 3 * Math.PI) + angleOffset

            val x = (currentRadius * cos(rotationAngle)).toFloat()
            val y = (currentRadius * sin(rotationAngle)).toFloat()

            Box(
                modifier = Modifier
                    .offset { IntOffset(x.dp.roundToPx(), y.dp.roundToPx()) }
                    .scale(scale)
                    .graphicsLayer { this.alpha = alpha }
                    .size(48.dp)
                    .background(Color(0xEE0A0A1A), CircleShape)
                    .border(
                        width = 2.dp,
                        color = Color(model.accentHex),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = model.name.take(2).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
