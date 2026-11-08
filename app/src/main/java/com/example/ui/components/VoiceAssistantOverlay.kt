package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.VolumeOff
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.db.ChatMessageEntity
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

// Helper function to launch app by spoken Turkish keyword
fun launchAppByKeyword(
    context: Context,
    keyword: String,
    onFeedback: (String) -> Unit
): Boolean {
    val kw = keyword.lowercase(Locale("tr", "TR"))
    try {
        when {
            kw.contains("kamera") -> {
                val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                onFeedback("📷 Kamera açılıyor")
                Toast.makeText(context, "📷 Kamera açılıyor", Toast.LENGTH_SHORT).show()
                return true
            }
            kw.contains("ayar") -> {
                val intent = Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                onFeedback("⚙️ Ayarlar açılıyor")
                Toast.makeText(context, "⚙️ Ayarlar açılıyor", Toast.LENGTH_SHORT).show()
                return true
            }
            kw.contains("galeri") || kw.contains("fotoğraf") -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    type = "image/*"
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                onFeedback("🖼️ Galeri açılıyor")
                Toast.makeText(context, "🖼️ Galeri açılıyor", Toast.LENGTH_SHORT).show()
                return true
            }
            kw.contains("whatsapp") -> {
                val intent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    onFeedback("💬 WhatsApp açılıyor")
                    Toast.makeText(context, "💬 WhatsApp açılıyor", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
            kw.contains("spotify") -> {
                val intent = context.packageManager.getLaunchIntentForPackage("com.spotify.music")
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    onFeedback("🎵 Spotify açılıyor")
                    Toast.makeText(context, "🎵 Spotify açılıyor", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
            kw.contains("hesap makine") || kw.contains("hesap") -> {
                val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.calculator")
                    ?: context.packageManager.getLaunchIntentForPackage("com.android.calculator2")
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    onFeedback("🧮 Hesap Makinesi açılıyor")
                    Toast.makeText(context, "🧮 Hesap Makinesi açılıyor", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
            kw.contains("harita") || kw.contains("haritalar") -> {
                val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.apps.maps")
                    ?: Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                onFeedback("🗺️ Haritalar açılıyor")
                Toast.makeText(context, "🗺️ Haritalar açılıyor", Toast.LENGTH_SHORT).show()
                return true
            }
            kw.contains("chrome") || kw.contains("tarayıcı") || kw.contains("internet") -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                onFeedback("🌐 Tarayıcı açılıyor")
                Toast.makeText(context, "🌐 Tarayıcı açılıyor", Toast.LENGTH_SHORT).show()
                return true
            }
            kw.contains("kişi") || kw.contains("rehber") -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                onFeedback("👥 Rehber açılıyor")
                Toast.makeText(context, "👥 Rehber açılıyor", Toast.LENGTH_SHORT).show()
                return true
            }
        }

        // Search installed apps by name label
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(0)
        for (pkg in packages) {
            val label = pm.getApplicationLabel(pkg).toString().lowercase(Locale("tr", "TR"))
            if (label.isNotBlank() && (label.contains(kw) || kw.contains(label))) {
                val launchIntent = pm.getLaunchIntentForPackage(pkg.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                    onFeedback("🚀 $label açılıyor")
                    Toast.makeText(context, "🚀 $label açılıyor", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
        }
    } catch (e: Exception) {
        // Ignore launch errors
    }
    return false
}

@Composable
fun VoiceAssistantOverlay(
    isVisible: Boolean,
    messages: List<ChatMessageEntity>,
    isTyping: Boolean,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit,
    voicePitch: Float = 0.83f,
    voiceSpeechRate: Float = 0.97f,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var isSpeakingByTts by remember { mutableStateOf(false) }
    var isTtsAudioEnabled by remember { mutableStateOf(true) }
    var lastRecognizedText by remember { mutableStateOf("") }
    var actionFeedbackText by remember { mutableStateOf("") }
    var lastSpokenAiResponseId by remember { mutableStateOf<Long?>(null) }
    var isFloatingGameHud by remember { mutableStateOf(false) }
    var micRmsDbLevel by remember { mutableFloatStateOf(0.15f) }

    // TextToSpeech Engine with dynamic pitch, smooth male voice calibration and AudioAttributes
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS && tts != null) {
                val trLocale = Locale("tr", "TR")
                val result = tts?.setLanguage(trLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                } else {
                    tts?.setLanguage(Locale.getDefault())
                    isTtsReady = true
                }

                // Select male Turkish voice if available on device
                try {
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
                } catch (_: Exception) {}

                // Set assistant audio attributes for smoother output
                try {
                    val audioAttributes = android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_ASSISTANT)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                    tts?.setAudioAttributes(audioAttributes)
                } catch (_: Exception) {}

                try {
                    tts?.setPitch(voicePitch)
                    tts?.setSpeechRate(voiceSpeechRate)
                } catch (_: Exception) {}
            }
        }
        ttsEngine = tts
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Reactively update pitch and speech rate when modified or calibrated from audio analysis
    LaunchedEffect(voicePitch, voiceSpeechRate, isTtsReady) {
        if (isTtsReady) {
            try {
                ttsEngine?.setPitch(voicePitch)
                ttsEngine?.setSpeechRate(voiceSpeechRate)
            } catch (_: Exception) {}
        }
    }

    fun speakText(text: String) {
        if (isTtsAudioEnabled && isTtsReady && text.isNotBlank()) {
            isSpeakingByTts = true
            try {
                ttsEngine?.setPitch(voicePitch)
                ttsEngine?.setSpeechRate(voiceSpeechRate)
            } catch (_: Exception) {}
            // Normalize and smooth text for flawless natural speech
            val smoothText = com.example.data.KeonVoiceProfile.cleanTextForSmoothSpeech(text)
            if (smoothText.isNotBlank()) {
                ttsEngine?.speak(smoothText, TextToSpeech.QUEUE_FLUSH, null, "KeonVoiceResponse")
            }
        }
    }

    // Process Voice Command logic for Phone Call, YouTube Search, and App Opening
    fun processVoiceCommand(spokenText: String) {
        lastRecognizedText = spokenText
        val lower = spokenText.lowercase(Locale("tr", "TR")).trim()

        // 1. PHONE CALL / CONTACT COMMANDS ("annemi ara", "ahmeti ara", "ara 0501...", "telefon et")
        if (lower.contains("ara") && (lower.contains("annemi") || lower.contains("telefon") || lower.contains("kişi") || lower.contains("rehber") || lower.contains("çağrı") || lower.contains("numara") || lower.startsWith("ara") || lower.endsWith("ara"))) {
            var target = lower
                .replace("telefon et", "")
                .replace("çağrı yap", "")
                .replace("telefonu ara", "")
                .replace("ara", "")
                .trim()

            if (target.isBlank() || lower.contains("annem")) {
                target = "Annem"
            }

            try {
                actionFeedbackText = "📞 Araniyor: $target"
                speakText("$target aranıyor")

                val isDigits = target.all { it.isDigit() || it == '+' || it == ' ' }
                val phoneUri = if (isDigits && target.length >= 3) Uri.parse("tel:$target") else Uri.parse("tel:")
                val callIntent = Intent(Intent.ACTION_DIAL, phoneUri)
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(callIntent)
                Toast.makeText(context, "📞 Araniyor: $target", Toast.LENGTH_SHORT).show()
                return
            } catch (e: Exception) {
                try {
                    val contactsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/"))
                    contactsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(contactsIntent)
                    Toast.makeText(context, "👤 Kişiler açılıyor...", Toast.LENGTH_SHORT).show()
                    return
                } catch (ex: Exception) {
                    Toast.makeText(context, "Telefon araması başlatılamadı.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 2. YOUTUBE SEARCH COMMANDS ("youtubede kedi videoları ara", "youtube'da müzik ara")
        if (lower.contains("youtube") || lower.contains("youtubede") || lower.contains("yutup") || lower.contains("yutupta")) {
            var query = lower
                .replace("youtube'da", "")
                .replace("youtubede", "")
                .replace("youtube", "")
                .replace("yutupta", "")
                .replace("yutup", "")
                .replace("ara", "")
                .trim()

            actionFeedbackText = "▶️ YouTube'da aranıyor: ${query.ifBlank { "YouTube" }}"
            speakText("YouTube'da ${query.ifBlank { "arama yapılıyor" }}")

            try {
                val intent = if (query.isNotBlank()) {
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(query)))
                } else {
                    context.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                        ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"))
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                Toast.makeText(context, "YouTube açılamadı.", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. OPEN APP COMMANDS ("whatsapp'ı aç", "kamerayı aç", "ayarları aç", "galeriyi aç", "hesap makinesini aç")
        if (lower.contains("aç") || lower.contains("başlat") || lower.contains("çalıştır")) {
            val appKeyword = lower
                .replace("uygulamasını", "")
                .replace("uygulamayı", "")
                .replace("aç", "")
                .replace("başlat", "")
                .replace("çalıştır", "")
                .trim()

            val isHandled = launchAppByKeyword(context, appKeyword) { feedback ->
                actionFeedbackText = feedback
                speakText(feedback)
            }
            if (isHandled) return
        }

        // 4. Default: AI Chat Response
        actionFeedbackText = ""
        onSendMessage(spokenText)
    }

    // Auto-Speak AI response when received in Voice Mode
    val latestAiMsg = remember(messages) { messages.lastOrNull { it.sender == "ai" } }
    LaunchedEffect(latestAiMsg) {
        if (latestAiMsg != null && latestAiMsg.id != lastSpokenAiResponseId && isTtsAudioEnabled && isTtsReady) {
            lastSpokenAiResponseId = latestAiMsg.id
            speakText(latestAiMsg.text)
        }
    }

    // Direct In-App Speech Recognizer Engine (No external Google popup)
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    DisposableEffect(context) {
        var sr: SpeechRecognizer? = null
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            sr = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer = sr
        }
        onDispose {
            sr?.stopListening()
            sr?.destroy()
        }
    }

    fun startListening() {
        if (isSpeakingByTts) {
            ttsEngine?.stop()
            isSpeakingByTts = false
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return
        }

        val sr = speechRecognizer ?: run {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                val created = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer = created
                created
            } else null
        }

        if (sr == null) {
            Toast.makeText(context, "Cihazınızda ses tanıma servisi bulunamadı.", Toast.LENGTH_SHORT).show()
            return
        }

        sr.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                actionFeedbackText = "Keon AI Dinliyor..."
            }

            override fun onBeginningOfSpeech() {
                isListening = true
            }

            override fun onRmsChanged(rmsdB: Float) {
                // rmsdB ranges usually between -2f and 10f in Android SpeechRecognizer
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.12f, 1.0f)
                micRmsDbLevel = normalized
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
                micRmsDbLevel = 0.1f
            }

            override fun onError(error: Int) {
                isListening = false
                micRmsDbLevel = 0.1f
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                micRmsDbLevel = 0.1f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull()
                if (!spokenText.isNullOrBlank()) {
                    processVoiceCommand(spokenText)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull()
                if (!spokenText.isNullOrBlank()) {
                    lastRecognizedText = spokenText
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            sr.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            isListening = false
            Toast.makeText(context, "Ses tanıma başlatılamadı: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // Record Audio Permission Request Launcher
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListening()
        } else {
            Toast.makeText(context, "Mikrofon izni reddedildi.", Toast.LENGTH_LONG).show()
        }
    }

    fun requestMicAndListen() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            startListening()
        } else {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Auto-trigger microphone on voice overlay launch
    LaunchedEffect(isVisible) {
        if (isVisible) {
            kotlinx.coroutines.delay(80) // Smooth 60fps entrance before trigger
            requestMicAndListen()
        }
    }

    // Pulse Animations for Voice Orb
    val pulseAnim = remember { Animatable(0f) }
    LaunchedEffect(isListening, isSpeakingByTts, isTyping) {
        val duration = if (isListening || isSpeakingByTts) 600 else 1800
        pulseAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val quickCommands = listOf(
        "📞 Annemi ara",
        "▶️ YouTube'da kedi videoları ara",
        "📷 Kamerayı aç",
        "⚙️ Ayarları aç",
        "💬 WhatsApp'ı aç",
        "🧮 Hesap Makinesini aç",
        "🤖 Günün motivasyonu nedir?"
    )

    // INSTANT ANIMATED FLOATING BOTTOM CARD / FLOATING GAME HUD (Does NOT cover or interrupt screen/game)
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        if (isFloatingGameHud) {
            // FLOATING GAME HUD WIDGET (100% NON-BLOCKING FOR GAMES & APPS)
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp, end = 16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Floating Mini Response / Status Card
                    if (isListening || isSpeakingByTts || isTyping || lastRecognizedText.isNotBlank() || actionFeedbackText.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFA120E29))
                                .border(1.dp, NeonCyan, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isListening) "🎙️ Keon Dinliyor..." else if (isTyping) "🤖 Düşünüyor..." else "💬 Keon Sesli Asistan",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                    IconButton(
                                        onClick = { isFloatingGameHud = false },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.OpenInFull,
                                            contentDescription = "Paneli Genişlet",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when {
                                        isListening && lastRecognizedText.isNotBlank() -> "\"$lastRecognizedText\""
                                        isListening -> "Lütfen sorunuzu söyleyin..."
                                        isTyping -> "Keon AI cevabı hazırlıyor..."
                                        actionFeedbackText.isNotBlank() -> actionFeedbackText
                                        else -> messages.lastOrNull { it.sender != "user" }?.text ?: "Oyun oynarken Keon'a soru sorabilirsiniz."
                                    },
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    maxLines = 4
                                )
                                if (isListening || isSpeakingByTts) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    VoiceWaveformVisualizer(
                                        isListening = isListening,
                                        isSpeaking = isSpeakingByTts,
                                        audioLevel = micRmsDbLevel,
                                        barsCount = 20,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // Floating Glowing Mic Orb Control Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Floating Glowing Mic Button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        listOf(Color(0xFF8A2BE2), Color(0xFF120E29))
                                    )
                                )
                                .border(2.dp, Brush.horizontalGradient(listOf(NeonMagenta, NeonCyan)), CircleShape)
                                .clickable { requestMicAndListen() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Sesli Asistan Dinle",
                                tint = if (isListening) NeonGreen else Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Expand Full Sheet Button
                        IconButton(
                            onClick = { isFloatingGameHud = false },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xCC181530), CircleShape)
                                .border(1.dp, NeonCyan, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInFull,
                                contentDescription = "Tam Paneli Aç",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Close Voice Assistant
                        IconButton(
                            onClick = {
                                ttsEngine?.stop()
                                onDismiss()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xCC181530), CircleShape)
                                .border(1.dp, Color.Red.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kapat",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // FULL BOTTOM SHEET OVERLAY (Translucent background, minimizes to Floating HUD on backdrop click)
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        ttsEngine?.stop()
                        onDismiss()
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(bottom = 16.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {} // prevent click through
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFF181530), Color(0xFF0D0A1C))
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(listOf(NeonMagenta, NeonCyan)),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Handle Bar
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(4.dp)
                                .background(Color.Gray.copy(alpha = 0.4f), CircleShape)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Compact Header Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (isListening) NeonMagenta else NeonGreen, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "🎙️ KEON SESLİ ASİSTAN",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonCyan,
                                    letterSpacing = 1.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Floating Game Mode Switcher Button
                                IconButton(
                                    onClick = { isFloatingGameHud = true },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Gamepad,
                                        contentDescription = "Yüzen Oyun Asistanı Modu",
                                        tint = NeonMagenta,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        isTtsAudioEnabled = !isTtsAudioEnabled
                                        if (!isTtsAudioEnabled && isSpeakingByTts) {
                                            ttsEngine?.stop()
                                            isSpeakingByTts = false
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isTtsAudioEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                        contentDescription = "Sesli Yanıt Toggles",
                                        tint = if (isTtsAudioEnabled) NeonCyan else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        ttsEngine?.stop()
                                        onDismiss()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Kapat",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Compact Orb & Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Compact Glowing Mic Orb
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .clickable { requestMicAndListen() },
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val radius = size.minDimension / 2f
                                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                                val pulseRadius = radius * (0.8f + 0.22f * pulseAnim.value)
                                val ringColor = when {
                                    isListening -> NeonMagenta
                                    isSpeakingByTts -> NeonGreen
                                    isTyping -> NeonCyan
                                    else -> NeonBlue
                                }

                                drawCircle(
                                    color = ringColor.copy(alpha = 0.25f),
                                    radius = pulseRadius,
                                    center = centerOffset
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                when {
                                                    isListening -> NeonMagenta
                                                    isSpeakingByTts -> NeonGreen
                                                    else -> NeonCyan
                                                },
                                                Color(0xFF0F0C29)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        brush = Brush.linearGradient(listOf(NeonCyan, NeonMagenta)),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Mikrofon",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            // Status text
                            Text(
                                text = when {
                                    isListening -> "🎙️ Dinleniyor... Konuşun"
                                    isSpeakingByTts -> "🔊 Keon Asistan Yanıtlıyor..."
                                    isTyping -> "🧠 Düşünüyor..."
                                    actionFeedbackText.isNotBlank() -> actionFeedbackText
                                    else -> "👆 Konuşmak için dokunun"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isListening -> NeonMagenta
                                    isSpeakingByTts -> NeonGreen
                                    else -> NeonCyan
                                }
                            )

                            if (lastRecognizedText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "\"$lastRecognizedText\"",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Real-time Voice Waveform Visualizer (Sound Wave)
                    VoiceWaveformVisualizer(
                        isListening = isListening,
                        isSpeaking = isSpeakingByTts,
                        audioLevel = micRmsDbLevel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // AI Response Box (if AI answered)
                    if (latestAiMsg != null && actionFeedbackText.isBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkSurface, RoundedCornerShape(12.dp))
                                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🤖 Keon AI",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (isSpeakingByTts) {
                                        Text("🔊 Okunuyor", fontSize = 9.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = latestAiMsg.text,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    lineHeight = 15.sp,
                                    maxLines = 3
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Command Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(quickCommands) { cmd ->
                            val cleanCmd = cmd.substringAfter(" ")
                            Box(
                                modifier = Modifier
                                    .background(DarkSurface, RoundedCornerShape(12.dp))
                                    .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        processVoiceCommand(cleanCmd)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = cmd,
                                    fontSize = 10.sp,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}
