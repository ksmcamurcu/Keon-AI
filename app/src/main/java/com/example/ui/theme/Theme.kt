package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Composition locals for custom Neon Light & Dark Mode Aesthetics
val LocalNeonAccent = staticCompositionLocalOf { NeonAccentColor.NEON_CYAN_BLUE }
val LocalTechTheme = staticCompositionLocalOf { TechThemeId.CYBER_NEON }

val KeonShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun KeonTheme(
    activeTheme: TechThemeId = TechThemeId.CYBER_NEON,
    activeNeonAccent: NeonAccentColor = NeonAccentColor.NEON_CYAN_BLUE,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = activeNeonAccent.primaryNeon,
        onPrimary = Color.Black,
        secondary = activeNeonAccent.secondaryNeon,
        onSecondary = Color.White,
        tertiary = activeNeonAccent.tertiaryNeon,
        onTertiary = Color.Black,
        background = activeTheme.background,
        onBackground = TextPrimary,
        surface = activeTheme.surface,
        onSurface = TextPrimary,
        surfaceVariant = activeTheme.surfaceVariant,
        onSurfaceVariant = TextSecondary,
        outline = activeNeonAccent.primaryNeon.copy(alpha = 0.5f),
        outlineVariant = activeNeonAccent.glowColor.copy(alpha = 0.3f),
        error = NeonLaserRed,
        onError = Color.White
    )

    CompositionLocalProvider(
        LocalNeonAccent provides activeNeonAccent,
        LocalTechTheme provides activeTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = KeonShapes,
            content = content
        )
    }
}

/**
 * Custom Modifier Extension: Cyber Neon Outer Glow Shadow
 */
fun Modifier.neonGlow(
    color: Color = NeonCyan,
    borderRadius: Dp = 16.dp,
    blurRadius: Dp = 12.dp,
    alpha: Float = 0.5f
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = android.graphics.Color.TRANSPARENT
        frameworkPaint.setShadowLayer(
            blurRadius.toPx(),
            0f,
            0f,
            color.copy(alpha = alpha).toArgb()
        )
        canvas.drawRoundRect(
            0f,
            0f,
            size.width,
            size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}

/**
 * Flame Core (Alev Çekirdeği) Custom Brush & Gradient Utilities
 */
val FlameCorePurpleOrangeGradient = Brush.horizontalGradient(
    colors = listOf(
        FlameNeonOrange,
        FlameNeonPurple,
        FlameNeonAmberRed
    )
)

val FlameCoreGlowBrush = Brush.linearGradient(
    colors = listOf(
        FlameNeonPurple.copy(alpha = 0.8f),
        FlameNeonOrange.copy(alpha = 0.8f),
        FlameNeonAmberRed.copy(alpha = 0.8f)
    )
)

/**
 * Custom Modifier Extension: Alev Çekirdeği (Flame Core) Purple & Orange Glassmorphism
 */
fun Modifier.flameCoreGlassBackground(
    backgroundColor: Color = FlameDarkSurface,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.5.dp
): Modifier = this
    .background(backgroundColor, RoundedCornerShape(cornerRadius))
    .border(
        borderWidth,
        Brush.horizontalGradient(
            colors = listOf(
                FlameNeonOrange.copy(alpha = 0.9f),
                FlameNeonPurple.copy(alpha = 0.9f),
                FlameNeonAmberRed.copy(alpha = 0.9f)
            )
        ),
        RoundedCornerShape(cornerRadius)
    )

/**
 * Custom Modifier Extension: Cyber Glassmorphism Surface with Gradient Neon Border
 */
fun Modifier.cyberGlassBackground(
    backgroundColor: Color = DarkSurface,
    borderColor: Color = NeonCyan,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp
): Modifier = this
    .background(backgroundColor, RoundedCornerShape(cornerRadius))
    .border(
        borderWidth,
        Brush.horizontalGradient(
            colors = listOf(
                borderColor.copy(alpha = 0.8f),
                borderColor.copy(alpha = 0.2f),
                borderColor.copy(alpha = 0.8f)
            )
        ),
        RoundedCornerShape(cornerRadius)
    )


