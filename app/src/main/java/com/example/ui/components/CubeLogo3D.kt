package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import kotlin.math.cos
import kotlin.math.sin

enum class KeonLogoStyle(
    val displayName: String,
    val description: String,
    val tag: String
) {
    CYBER_CUBE_3D(
        displayName = "3D Siber Küp",
        description = "3 Boyutlu Dönen Siber Küp & Işıklı K Harfi",
        tag = "3D Cube"
    ),
    QUANTUM_ORBIT_SPHERE(
        displayName = "Kuantum Yörünge Küresi",
        description = "Çift Dönen Parçacık Halkaları & Çekirdek K",
        tag = "Orbit Sphere"
    ),
    HOLOGRAPHIC_HEXAGON(
        displayName = "Holografik Altıgen Matris",
        description = "Dönerek Işıldayan Altıgen Çerçeve & Geometrik K",
        tag = "Hex Matrix"
    ),
    PLASMA_ENERGY_RING(
        displayName = "Plazma Enerji Halkası",
        description = "Işın Saçan Güneş Fırtınası Halkası & K Çekirdeği",
        tag = "Plasma Ring"
    ),
    MINIMAL_NEON_SHIELD(
        displayName = "Siber Neon Kalkan",
        description = "Köşeli Siber Kalkan Rozeti & Yüksek Kontrast K",
        tag = "Cyber Shield"
    )
}

@Composable
fun CubeLogo3D(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    KeonLogoView(
        logoStyle = KeonLogoStyle.CYBER_CUBE_3D,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun KeonLogoView(
    logoStyle: KeonLogoStyle,
    modifier: Modifier = Modifier,
    size: Dp = 90.dp,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Box(
        modifier = clickableModifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        when (logoStyle) {
            KeonLogoStyle.CYBER_CUBE_3D -> CyberCube3DLogo(size = size)
            KeonLogoStyle.QUANTUM_ORBIT_SPHERE -> QuantumOrbitSphereLogo(size = size)
            KeonLogoStyle.HOLOGRAPHIC_HEXAGON -> HolographicHexagonLogo(size = size)
            KeonLogoStyle.PLASMA_ENERGY_RING -> PlasmaEnergyRingLogo(size = size)
            KeonLogoStyle.MINIMAL_NEON_SHIELD -> MinimalNeonShieldLogo(size = size)
        }
    }
}

@Composable
private fun CyberCube3DLogo(size: Dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "CubeRotation")
    val rotationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationY"
    )

    val boxSize = size * 0.9f
    val fontSize = (size.value * 0.48f).sp

    Box(
        modifier = Modifier
            .size(boxSize)
            .graphicsLayer {
                this.rotationY = rotationY
                this.rotationX = -15f
                this.cameraDistance = 12f * density
            }
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.85f),
                        NeonMagenta.copy(alpha = 0.85f),
                        NeonGreen.copy(alpha = 0.7f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(NeonCyan, NeonMagenta)
                ),
                shape = RoundedCornerShape(18.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "K",
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier.graphicsLayer {
                this.rotationY = -rotationY / 4f
            }
        )
    }
}

@Composable
private fun QuantumOrbitSphereLogo(size: Dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "QuantumOrbit")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitRotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbitPulse"
    )

    val canvasSize = size * 0.95f
    val fontSize = (size.value * 0.42f).sp

    Box(modifier = Modifier.size(canvasSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.width / 2f - 4f

            // Background Outer Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.6f), NeonGreen.copy(alpha = 0.2f), Color.Transparent),
                    center = center,
                    radius = radius * pulse
                ),
                radius = radius * pulse,
                center = center
            )

            // Outer Orbit Ring
            rotate(rotation, center) {
                drawCircle(
                    color = NeonCyan,
                    radius = radius * 0.85f,
                    center = center,
                    style = Stroke(width = 2.5f)
                )
                // Electron Nodes
                for (i in 0 until 4) {
                    val angle = Math.toRadians((i * 90).toDouble())
                    val ex = center.x + (radius * 0.85f) * cos(angle).toFloat()
                    val ey = center.y + (radius * 0.85f) * sin(angle).toFloat()
                    drawCircle(color = Color.White, radius = 4f, center = Offset(ex, ey))
                }
            }

            // Inner Reverse Orbit Ring
            rotate(-rotation * 1.4f, center) {
                drawCircle(
                    color = NeonMagenta,
                    radius = radius * 0.65f,
                    center = center,
                    style = Stroke(width = 2f)
                )
                for (i in 0 until 3) {
                    val angle = Math.toRadians((i * 120).toDouble())
                    val ex = center.x + (radius * 0.65f) * cos(angle).toFloat()
                    val ey = center.y + (radius * 0.65f) * sin(angle).toFloat()
                    drawCircle(color = NeonGreen, radius = 4f, center = Offset(ex, ey))
                }
            }
        }

        // Center Core Button
        Box(
            modifier = Modifier
                .size(size * 0.5f)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonCyan, NeonBlue, Color(0xFF0A0A1F))
                    )
                )
                .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "K",
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
private fun HolographicHexagonLogo(size: Dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "HexRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hexRotation"
    )

    val boxSize = size * 0.92f
    val fontSize = (size.value * 0.44f).sp

    Box(modifier = Modifier.size(boxSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.width / 2f - 6f

            rotate(rotation, center) {
                val hexPath = Path()
                for (i in 0 until 6) {
                    val angle = Math.toRadians((i * 60).toDouble())
                    val x = center.x + radius * cos(angle).toFloat()
                    val y = center.y + radius * sin(angle).toFloat()
                    if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
                }
                hexPath.close()

                drawPath(
                    path = hexPath,
                    brush = Brush.sweepGradient(
                        colors = listOf(NeonGreen, NeonCyan, NeonMagenta, NeonGreen)
                    ),
                    style = Stroke(width = 3.5f)
                )
            }

            rotate(-rotation * 0.7f, center) {
                val innerHexPath = Path()
                for (i in 0 until 6) {
                    val angle = Math.toRadians((i * 60 + 30).toDouble())
                    val x = center.x + (radius * 0.75f) * cos(angle).toFloat()
                    val y = center.y + (radius * 0.75f) * sin(angle).toFloat()
                    if (i == 0) innerHexPath.moveTo(x, y) else innerHexPath.lineTo(x, y)
                }
                innerHexPath.close()

                drawPath(
                    path = innerHexPath,
                    color = NeonCyan.copy(alpha = 0.6f),
                    style = Stroke(width = 1.5f)
                )
            }
        }

        Text(
            text = "K",
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            color = NeonGreen
        )
    }
}

@Composable
private fun PlasmaEnergyRingLogo(size: Dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "PlasmaPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plasmaPulse"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "plasmaRotation"
    )

    val boxSize = size * 0.95f
    val fontSize = (size.value * 0.46f).sp

    Box(modifier = Modifier.size(boxSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val maxR = this.size.width / 2f - 4f

            // Radiating Energy Beams
            for (i in 0 until 12) {
                val angle = Math.toRadians((i * 30 + rotation).toDouble())
                val endX = center.x + (maxR * pulse) * cos(angle).toFloat()
                val endY = center.y + (maxR * pulse) * sin(angle).toFloat()
                drawLine(
                    color = if (i % 2 == 0) Color(0xFFFF7700).copy(alpha = 0.7f) else NeonMagenta.copy(alpha = 0.7f),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFCC00), Color(0xFFFF3300), Color.Transparent),
                    center = center,
                    radius = maxR * 0.7f
                ),
                radius = maxR * 0.6f * pulse,
                center = center
            )
        }

        Box(
            modifier = Modifier
                .size(size * 0.55f)
                .clip(CircleShape)
                .background(Color(0xFF1A0803))
                .border(2.dp, Color(0xFFFF7700), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "K",
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFCC00)
            )
        }
    }
}

@Composable
private fun MinimalNeonShieldLogo(size: Dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "ShieldPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shieldPulse"
    )

    val boxSize = size * 0.92f
    val fontSize = (size.value * 0.46f).sp

    Box(modifier = Modifier.size(boxSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Cyber Shield Path
            val shieldPath = Path().apply {
                moveTo(w * 0.5f, h * 0.05f)
                lineTo(w * 0.92f, h * 0.25f)
                lineTo(w * 0.92f, h * 0.65f)
                lineTo(w * 0.5f, h * 0.95f)
                lineTo(w * 0.08f, h * 0.65f)
                lineTo(w * 0.08f, h * 0.25f)
                close()
            }

            drawPath(
                path = shieldPath,
                brush = Brush.linearGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.4f), NeonMagenta.copy(alpha = 0.3f))
                )
            )

            drawPath(
                path = shieldPath,
                brush = Brush.linearGradient(
                    colors = listOf(NeonCyan, NeonGreen)
                ),
                style = Stroke(width = 3.5f)
            )
        }

        Text(
            text = "K",
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}
