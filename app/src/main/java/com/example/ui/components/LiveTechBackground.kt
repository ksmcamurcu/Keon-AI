package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.ui.theme.TechThemeId
import com.example.ui.theme.TechWallpaperId
import kotlin.random.Random

private data class Particle(
    val initialX: Float,
    val initialY: Float,
    val radius: Float,
    val speedY: Float,
    val isPrimaryColor: Boolean
)

@Composable
fun LiveTechBackground(
    activeTheme: TechThemeId,
    activeWallpaper: TechWallpaperId,
    wallpaperAlpha: Float = 0.35f,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tech_anim")
    
    // Grid animation movement
    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grid_offset"
    )

    // Pulse animation for particle glow
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Generate random static particles
    val particles = remember {
        List(25) {
            Particle(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat(),
                radius = Random.nextFloat() * 4f + 2f,
                speedY = Random.nextFloat() * 0.3f + 0.1f,
                isPrimaryColor = Random.nextBoolean()
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(activeTheme.background)
    ) {
        // 1. Wallpaper Image Layer (if image resource exists)
        activeWallpaper.drawableRes?.let { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = "Teknoloji Duvar Kağıdı",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(wallpaperAlpha)
            )
        }

        // 2. Ambient Gradient Glow Overlay
        val gradientBrush = Brush.radialGradient(
            colors = listOf(
                activeTheme.primary.copy(alpha = 0.18f),
                activeTheme.secondary.copy(alpha = 0.08f),
                Color.Transparent
            ),
            center = Offset(300f, 400f),
            radius = 900f
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
        )

        // 3. Live Canvas Grid & Floating Particle Effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Draw Subtle Futuristic Grid Lines
            val gridStep = 80f
            var x = 0f
            while (x < width) {
                drawLine(
                    color = activeTheme.primary.copy(alpha = 0.06f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += gridStep
            }

            var y = (gridOffset % gridStep)
            while (y < height) {
                drawLine(
                    color = activeTheme.primary.copy(alpha = 0.05f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.2f
                )
                y += gridStep
            }

            // Draw Floating Particles
            particles.forEach { p ->
                val px = p.initialX * width
                val py = ((p.initialY * height - (gridOffset * p.speedY * 15f)) % height + height) % height
                val particleColor = if (p.isPrimaryColor) activeTheme.primary else activeTheme.secondary

                // Glowing Outer Aura
                drawCircle(
                    color = particleColor.copy(alpha = pulseAlpha * 0.3f),
                    radius = p.radius * 2.5f,
                    center = Offset(px, py)
                )

                // Solid Core
                drawCircle(
                    color = particleColor.copy(alpha = pulseAlpha * 0.9f),
                    radius = p.radius,
                    center = Offset(px, py)
                )
            }
        }
    }
}
