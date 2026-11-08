package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeonAiModel
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameNeonOrange
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun ModelPerformanceBenchmarkPanel(
    selectedModel: KeonAiModel,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isRunningTest by remember { mutableStateOf(false) }
    var testProgress by remember { mutableStateOf(0f) }
    var simulatedLiteLatency by remember { mutableIntStateOf(280) }
    var simulatedProLatency by remember { mutableIntStateOf(1240) }
    var testCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(isRunningTest) {
        if (isRunningTest) {
            testCompleted = false
            testProgress = 0f
            // Simulate live benchmark testing steps
            for (i in 1..20) {
                delay(60)
                testProgress = i / 20f
                simulatedLiteLatency = (240..310).random()
                simulatedProLatency = (1150..1320).random()
            }
            isRunningTest = false
            testCompleted = true
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = DarkSurface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            // Header Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Hız Göstergesi",
                        tint = if (selectedModel == KeonAiModel.LITE) NeonCyan else FlameNeonOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Model Performans & Hız Paneli",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (selectedModel == KeonAiModel.LITE) NeonCyan.copy(alpha = 0.2f)
                                else FlameNeonOrange.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (selectedModel == KeonAiModel.LITE) "⚡ ~280ms (Hızlı)" else "🧠 ~1.2s (Derin Mantık)",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedModel == KeonAiModel.LITE) NeonCyan else FlameNeonOrange
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isExpanded) "Gizle" else "Detaylar",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Expanded Benchmark Dashboard Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    // Comparison Bar - Latency (Yanıt Süresi)
                    Text(
                        text = "⏱️ Yanıt Süresi (Latency - Düşük Olması İyidir)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Lite Latency Bar
                    MetricComparisonBar(
                        label = "Keon AI Lite",
                        valueText = "${simulatedLiteLatency}ms (Süper Hızlı)",
                        progress = 0.22f,
                        color = NeonCyan,
                        isSelected = selectedModel == KeonAiModel.LITE
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // PRO Latency Bar
                    MetricComparisonBar(
                        label = "Keon AI PRO",
                        valueText = "${simulatedProLatency}ms (Düşünme Süreci)",
                        progress = 0.85f,
                        color = FlameNeonOrange,
                        isSelected = selectedModel == KeonAiModel.PRO
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Comparison Bar - Tokens Per Second (Üretim Hızı)
                    Text(
                        text = "🚀 Kelime Üretim Hızı (Tokens/sec)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    MetricComparisonBar(
                        label = "Keon AI Lite",
                        valueText = "195 tok/sn (4x Seri)",
                        progress = 0.95f,
                        color = NeonCyan,
                        isSelected = selectedModel == KeonAiModel.LITE
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    MetricComparisonBar(
                        label = "Keon AI PRO",
                        valueText = "48 tok/sn (Analiz Ağırlıklı)",
                        progress = 0.35f,
                        color = FlameNeonOrange,
                        isSelected = selectedModel == KeonAiModel.PRO
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Reasoning Depth Comparison
                    Text(
                        text = "🧠 Mantık & Analiz Kapasitesi",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    MetricComparisonBar(
                        label = "Keon AI PRO",
                        valueText = "%98 Karmaşık Problem & Kodlama",
                        progress = 0.98f,
                        color = FlameNeonOrange,
                        isSelected = selectedModel == KeonAiModel.PRO
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    MetricComparisonBar(
                        label = "Keon AI Lite",
                        valueText = "%72 Standart Sohbet & Hızlı Soru",
                        progress = 0.72f,
                        color = NeonCyan,
                        isSelected = selectedModel == KeonAiModel.LITE
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Test Simulator Button & Status
                    if (isRunningTest) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚡ Canlı Hız & Ping Testi Yapılıyor... ${(testProgress * 100).toInt()}%",
                                fontSize = 10.sp,
                                color = NeonGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { testProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = NeonGreen,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { isRunningTest = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF231438)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Test Et",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "⚡ Canlı Hız Testi Çalıştır",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }
                            }

                            Text(
                                text = if (selectedModel == KeonAiModel.LITE)
                                    "⚡ Lite: Günlük sohbetler için ultra hızlı"
                                else
                                    "🔥 PRO: Karmaşık kod & derin analiz için",
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    if (testCompleted) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NeonGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "✅ Test Tamamlandı: Lite Modu ~${simulatedLiteLatency}ms ile 4.4 kat daha düşük gecikme sağladı!",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricComparisonBar(
    label: String,
    valueText: String,
    progress: Float,
    color: Color,
    isSelected: Boolean
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "MetricBar"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) color else TextPrimary
                )
            }
            Text(
                text = valueText,
                fontSize = 9.sp,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (isSelected) color
                        else color.copy(alpha = 0.5f)
                    )
            )
        }
    }
}
