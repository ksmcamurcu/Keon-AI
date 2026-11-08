package com.example.ui.components

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.sin

/**
 * Interactive Real-time Voice Waveform Visualizer for User Spoken Commands & Keon AI Responses.
 *
 * Visualizes microphone audio amplitude (rmsDb / decibel levels) with glowing bars,
 * fluid neon sine waves, frequency spectrum ripples, and status decibel indicators.
 */
@Composable
fun VoiceWaveformVisualizer(
    isListening: Boolean,
    isSpeaking: Boolean,
    audioLevel: Float, // Normalized amplitude 0.0f .. 1.0f
    modifier: Modifier = Modifier,
    barsCount: Int = 28,
    activeColorStart: Color = NeonCyan,
    activeColorEnd: Color = NeonMagenta,
    idleColor: Color = Color.Gray.copy(alpha = 0.35f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformPhase")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseAnim"
    )

    val wavePhase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseAnim2"
    )

    // Animated smoothed audio level
    val smoothedLevel = remember { Animatable(0.1f) }
    LaunchedEffect(audioLevel, isListening, isSpeaking) {
        val target = when {
            isListening -> audioLevel.coerceIn(0.15f, 1.0f)
            isSpeaking -> (0.4f + audioLevel * 0.5f).coerceIn(0.3f, 0.95f)
            else -> 0.06f
        }
        smoothedLevel.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0B24),
                        Color(0xFF070514)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        if (isListening) NeonMagenta else if (isSpeaking) NeonGreen else NeonCyan.copy(alpha = 0.4f),
                        if (isListening) NeonCyan else if (isSpeaking) NeonBlue else NeonMagenta.copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                when {
                                    isListening -> NeonMagenta
                                    isSpeaking -> NeonGreen
                                    else -> NeonCyan
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            isListening -> "🎙️ SES GİRİŞİ (CANLI DALGA)"
                            isSpeaking -> "🔊 AI SES ÇIKIŞI"
                            else -> "⚡ SES SPEKTRUMU"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isListening -> NeonMagenta
                            isSpeaking -> NeonGreen
                            else -> NeonCyan
                        },
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = if (isListening) {
                        "${(smoothedLevel.value * 100).toInt()}% • ${(smoothedLevel.value * 60).toInt()} dB"
                    } else if (isSpeaking) {
                        "TTS Aktif"
                    } else {
                        "Hazır"
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Canvas Waveform & Frequency Bars
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val centerY = height / 2f
                    val level = smoothedLevel.value

                    // 1. DUAL FLUID CONTINUOUS SINE WAVES
                    if (isListening || isSpeaking) {
                        val path1 = Path()
                        val path2 = Path()

                        val step = 4f
                        var x = 0f
                        path1.moveTo(0f, centerY)
                        path2.moveTo(0f, centerY)

                        while (x <= width) {
                            val normX = x / width
                            // Envelope window (hanning curve) so waves taper at ends
                            val envelope = sin(normX * Math.PI).toFloat()

                            val freq1 = 3.5f
                            val amp1 = (height * 0.38f) * level * envelope
                            val y1 = centerY + (sin(normX * freq1 * 2 * Math.PI + phase) * amp1).toFloat()

                            val freq2 = 5.2f
                            val amp2 = (height * 0.28f) * level * envelope
                            val y2 = centerY + (sin(normX * freq2 * 2 * Math.PI + wavePhase2 + 1.2) * amp2).toFloat()

                            path1.lineTo(x, y1)
                            path2.lineTo(x, y2)
                            x += step
                        }

                        // Draw background glowing fluid wave paths
                        drawPath(
                            path = path2,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    activeColorEnd.copy(alpha = 0.5f),
                                    activeColorStart.copy(alpha = 0.5f)
                                )
                            ),
                            style = Stroke(width = 2.5f)
                        )

                        drawPath(
                            path = path1,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    activeColorStart.copy(alpha = 0.85f),
                                    activeColorEnd.copy(alpha = 0.85f)
                                )
                            ),
                            style = Stroke(width = 3.0f)
                        )
                    }

                    // 2. EQUALIZER SPECTRUM BARS
                    val barSpacing = width / barsCount
                    val barWidth = (barSpacing * 0.55f).coerceAtLeast(3f)

                    for (i in 0 until barsCount) {
                        val normX = i.toFloat() / (barsCount - 1)
                        val x = i * barSpacing + (barSpacing - barWidth) / 2f

                        // Bell curve center weight
                        val centerWeight = sin(normX * Math.PI).toFloat()

                        // Dynamic harmonic offset based on index
                        val harmonic = sin(normX * 12f + phase * 2f).toFloat() * 0.35f

                        val barHeight = if (isListening || isSpeaking) {
                            val dynamicFactor = (centerWeight * 0.65f + harmonic * 0.35f + 0.3f).coerceIn(0.1f, 1.2f)
                            (height * 0.85f * level * dynamicFactor).coerceIn(4f, height - 4f)
                        } else {
                            // Subtle idle breathing bars
                            4f + sin(normX * 6f + phase).toFloat() * 2f
                        }

                        val top = centerY - barHeight / 2f

                        val barBrush = if (isListening) {
                            Brush.verticalGradient(
                                listOf(NeonMagenta, NeonCyan, Color(0xFF8A2BE2)),
                                startY = top,
                                endY = top + barHeight
                            )
                        } else if (isSpeaking) {
                            Brush.verticalGradient(
                                listOf(NeonGreen, NeonCyan, Color(0xFF00B4D8)),
                                startY = top,
                                endY = top + barHeight
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(idleColor, idleColor.copy(alpha = 0.15f)),
                                startY = top,
                                endY = top + barHeight
                            )
                        }

                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(x, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                        )
                    }
                }
            }
        }
    }
}
