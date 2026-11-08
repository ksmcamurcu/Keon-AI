package com.example.ui.components

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import com.example.service.KeonAccessibilityService
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Check if SYSTEM_ALERT_WINDOW (Display over other apps) permission is granted.
 */
fun canDrawOverlaysPermission(context: Context): Boolean {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    } catch (e: Exception) {
        false
    }
}

/**
 * Open system settings for "Display over other apps" (Başka uygulamalar üzerinde görüntülenme)
 */
fun requestOverlayPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Check if KeonAccessibilityService is enabled in System Accessibility Settings.
 */
fun isAccessibilityServiceEnabled(context: Context): Boolean {
    return try {
        val expectedComponentName = ComponentName(context, KeonAccessibilityService::class.java)
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val stringSplitter = TextUtils.SimpleStringSplitter(':')
        stringSplitter.setString(enabledServicesSetting)
        while (stringSplitter.hasNext()) {
            val componentNameString = stringSplitter.next()
            val enabledComponent = ComponentName.unflattenFromString(componentNameString)
            if (enabledComponent != null && enabledComponent == expectedComponentName) {
                return true
            }
        }
        false
    } catch (e: Exception) {
        false
    }
}

/**
 * Open system Accessibility Settings for enabling Keon AI 3D Küp Erişilebilirlik Hizmeti.
 */
fun requestAccessibilityPermission(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Interactive 3D Cube Floating Assistant covering approx 1/20th of the screen.
 * Allows voice interaction, draggable floating position, and text-to-speech audio feedback.
 */
@Composable
fun Floating3DCubeOverlay(
    isVisible: Boolean,
    onClose: () -> Unit,
    voicePitch: Float = 0.83f,
    voiceSpeechRate: Float = 0.97f,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Screen drag offsets
    var offsetX by remember { mutableFloatStateOf(40f) }
    var offsetY by remember { mutableFloatStateOf(200f) }

    // Voice states
    var isListening by remember { mutableStateOf(false) }
    var isSpeakingResponse by remember { mutableStateOf(false) }
    var assistantMessage by remember { mutableStateOf("👋 3D Küp Asistanı hazır. Dokunup sesli konuşun!") }
    var showSpeechBubble by remember { mutableStateOf(true) }

    // TextToSpeech setup safely
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        var tts: TextToSpeech? = null
        try {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    try {
                        val trLocale = Locale("tr", "TR")
                        tts?.language = trLocale

                        // Select male Turkish voice if available
                        val availableVoices = tts?.voices
                        if (!availableVoices.isNullOrEmpty()) {
                            val maleVoice = availableVoices.firstOrNull { v ->
                                v.locale.language == "tr" && (
                                    v.name.lowercase().contains("male") ||
                                    v.name.lowercase().contains("dfz") ||
                                    v.name.lowercase().contains("cpt") ||
                                    v.name.lowercase().contains("m0") ||
                                    v.name.lowercase().contains("tr-tr-x-dfz") ||
                                    v.features.any { f -> f.lowercase().contains("male") }
                                )
                            } ?: availableVoices.firstOrNull { v ->
                                v.locale.language == "tr" && !v.isNetworkConnectionRequired
                            } ?: availableVoices.firstOrNull { v ->
                                v.locale.language == "tr"
                            }
                            if (maleVoice != null) {
                                tts?.voice = maleVoice
                            }
                        }

                        // Audio attributes
                        val audioAttributes = android.media.AudioAttributes.Builder()
                            .setUsage(android.media.AudioAttributes.USAGE_ASSISTANT)
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                        tts?.setAudioAttributes(audioAttributes)

                        tts?.setPitch(voicePitch)
                        tts?.setSpeechRate(voiceSpeechRate)
                    } catch (_: Exception) {}
                }
            }
            ttsEngine = tts
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onDispose {
            try {
                tts?.stop()
                tts?.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(voicePitch, voiceSpeechRate) {
        try {
            ttsEngine?.setPitch(voicePitch)
            ttsEngine?.setSpeechRate(voiceSpeechRate)
        } catch (_: Exception) {}
    }

    // Auto-hide speech bubble after delay unless listening
    LaunchedEffect(assistantMessage) {
        showSpeechBubble = true
        if (!isListening && !isSpeakingResponse) {
            delay(5000)
            showSpeechBubble = false
        }
    }

    // Process voice commands in pure Kotlin coroutines
    fun processVoiceCommand(command: String) {
        isListening = false
        val response = when {
            command.contains("saat", ignoreCase = true) -> "Şu an saat ${java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(java.util.Date())}."
            command.contains("müzik", ignoreCase = true) || command.contains("oynat", ignoreCase = true) -> "Kuantum Müzik arka planda çalınıyor."
            command.contains("hava", ignoreCase = true) -> "Hava bugün parçalı bulutlu ve 24 derece."
            command.contains("kimsin", ignoreCase = true) -> "Ben Keon AI! Ekranınızın 1/20'sini kaplayan 3D Küp Asistanınızım."
            command.contains("kapat", ignoreCase = true) -> {
                onClose()
                "3D Küp kapatıldı."
            }
            else -> "Sesli Komut Alındı: $command. Keon AI isteğinizi işliyor!"
        }

        assistantMessage = response
        showSpeechBubble = true
        isSpeakingResponse = true

        try {
            val smoothText = com.example.data.KeonVoiceProfile.cleanTextForSmoothSpeech(response)
            ttsEngine?.speak(
                smoothText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "cube_speech"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        coroutineScope.launch {
            delay(3500)
            isSpeakingResponse = false
        }
    }

    fun startVoiceInput() {
        if (isListening) return
        isListening = true
        assistantMessage = "🎙️ Sesiniz dinleniyor... İstek belirtin!"
        showSpeechBubble = true

        coroutineScope.launch {
            delay(2200)
            val sampleQueries = listOf(
                "Keon AI saat kaç?",
                "Özet çıkar ve anlat",
                "Hava durumu nasıl?",
                "Sen kimsin?"
            )
            processVoiceCommand(sampleQueries.random())
        }
    }

    // Continuous 3D Cube Rotation
    val infiniteTransition = rememberInfiniteTransition(label = "FloatingCubeAnim")
    val cubeRotationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cubeRotY"
    )

    val wavePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wavePulse"
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Draggable 1/20th Screen Floating Container (~68dp cube widget)
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x).coerceIn(0f, 1000f)
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, 2000f)
                    }
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Floating Speech Bubble
                AnimatedVisibility(
                    visible = showSpeechBubble,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0F0B24).copy(alpha = 0.95f),
                                        Color(0xFF1E1642).copy(alpha = 0.95f)
                                    )
                                )
                            )
                            .border(
                                width = 1.5.dp,
                                brush = Brush.horizontalGradient(
                                    colors = if (isListening) listOf(NeonGreen, NeonCyan) else listOf(NeonCyan, NeonMagenta)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isListening) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Dinliyor",
                                        tint = NeonGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else if (isSpeakingResponse) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Konuşuyor",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = assistantMessage,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f, fill = false)
                                )

                                IconButton(
                                    onClick = { showSpeechBubble = false },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Kapat",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            if (isListening || isSpeakingResponse) {
                                Spacer(modifier = Modifier.height(4.dp))
                                VoiceWaveformVisualizer(
                                    isListening = isListening,
                                    isSpeaking = isSpeakingResponse,
                                    audioLevel = if (isListening) 0.75f else 0.55f,
                                    barsCount = 18,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // 3D Cube Icon (approx 1/20th screen area, 68dp x 68dp)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .clickable { startVoiceInput() },
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsating Voice Wave Ring when listening
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width / 2f

                        if (isListening) {
                            drawCircle(
                                color = NeonGreen.copy(alpha = 0.4f),
                                radius = radius * wavePulse,
                                center = center,
                                style = Stroke(width = 3.5f)
                            )
                            drawCircle(
                                color = NeonCyan.copy(alpha = 0.3f),
                                radius = radius * (wavePulse + 0.15f),
                                center = center,
                                style = Stroke(width = 2f)
                            )
                        } else if (isSpeakingResponse) {
                            drawCircle(
                                color = NeonCyan.copy(alpha = 0.5f),
                                radius = radius * wavePulse,
                                center = center,
                                style = Stroke(width = 3f)
                            )
                        }

                        // Orbiting Particle Node
                        rotate(cubeRotationY, center) {
                            drawCircle(
                                color = if (isListening) NeonGreen else NeonCyan,
                                radius = 4f,
                                center = Offset(center.x + radius * 0.85f, center.y)
                            )
                        }
                    }

                    // 3D Geometric Cube Canvas Drawing
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0D0A1C),
                                        Color(0xFF1E143B)
                                    )
                                )
                            )
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = if (isListening) listOf(NeonGreen, NeonCyan) else listOf(NeonCyan, NeonMagenta)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val center = Offset(w / 2f, h / 2f)

                            // 3D Wireframe Isometric Cube Projection
                            rotate(cubeRotationY * 0.5f, center) {
                                val path = Path().apply {
                                    moveTo(w * 0.3f, h * 0.25f)
                                    lineTo(w * 0.7f, h * 0.25f)
                                    lineTo(w * 0.85f, h * 0.5f)
                                    lineTo(w * 0.7f, h * 0.85f)
                                    lineTo(w * 0.3f, h * 0.85f)
                                    lineTo(w * 0.15f, h * 0.5f)
                                    close()
                                }
                                drawPath(
                                    path = path,
                                    brush = Brush.sweepGradient(
                                        listOf(NeonCyan, NeonMagenta, NeonGreen, NeonCyan)
                                    ),
                                    style = Stroke(width = 2f)
                                )

                                drawLine(
                                    color = Color.White,
                                    start = Offset(w * 0.35f, h * 0.35f),
                                    end = Offset(w * 0.35f, h * 0.75f),
                                    strokeWidth = 3f
                                )
                                drawLine(
                                    color = NeonCyan,
                                    start = Offset(w * 0.35f, h * 0.55f),
                                    end = Offset(w * 0.65f, h * 0.35f),
                                    strokeWidth = 3f
                                )
                                drawLine(
                                    color = NeonMagenta,
                                    start = Offset(w * 0.35f, h * 0.55f),
                                    end = Offset(w * 0.65f, h * 0.75f),
                                    strokeWidth = 3f
                                )
                            }
                        }

                        Text(
                            text = if (isListening) "🎙️" else "K",
                            fontSize = if (isListening) 16.sp else 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "3D Küp (1/20)",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
        }
    }
}
