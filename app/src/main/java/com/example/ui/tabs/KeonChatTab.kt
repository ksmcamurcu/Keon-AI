package com.example.ui.tabs

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.core.content.FileProvider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Surface
import com.example.data.KeonAiModel
import com.example.data.db.ChatMessageEntity
import com.example.ui.components.ModelPerformanceBenchmarkPanel
import com.example.ui.components.pulsatingNeonGlow
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun KeonChatTab(
    messages: List<ChatMessageEntity>,
    isTyping: Boolean,
    isApiKeyConfigured: Boolean,
    allHistoryDesc: List<ChatMessageEntity> = emptyList(),
    messageCount: Int = messages.size,
    selectedAiModel: KeonAiModel = KeonAiModel.PRO,
    onSelectAiModel: (KeonAiModel) -> Unit = {},
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onDeleteMessage: (Long) -> Unit = {},
    onOpenVoiceAssistant: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var micRmsDbLevel by remember { mutableFloatStateOf(0.15f) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var historySearchQuery by remember { mutableStateOf("") }

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

    fun startVoiceToText() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
            return
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
            }

            override fun onBeginningOfSpeech() {
                isListening = true
            }

            override fun onRmsChanged(rmsdB: Float) {
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
                    inputText = spokenText
                    onSendMessage(spokenText)
                    inputText = ""
                    Toast.makeText(context, "🎤 Keon AI'ya iletildi: \"$spokenText\"", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull()
                if (!spokenText.isNullOrBlank()) {
                    inputText = spokenText
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

    // Permission Request Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startVoiceToText()
        } else {
            Toast.makeText(context, "Mikrofon izni verilmedi. Sesli etkileşim kullanılamaz.", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestMicAndVoiceToText() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            startVoiceToText()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val suggestedPrompts = remember {
        listOf(
            "Merhaba Keon AI!",
            "Yapay zeka nedir?",
            "Kod yazmama yardım et",
            "Matematik hesabı yap",
            "Bana yaratıcı fikirler ver"
        )
    }

    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Cognitive Status & Emotion Header Bar
        var showCognitiveDialog by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(16.dp))
                .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                .clickable { showCognitiveDialog = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(if (isApiKeyConfigured) NeonGreen else NeonMagenta, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Keon AI Bilge Zeka Engine",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🔥 Alev Çekirdeği",
                            fontSize = 9.sp,
                            color = NeonAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "🔥 Alev Çekirdeği Yüksek Zeka • Derin Mantık & Karmaşık Analiz Engine",
                        fontSize = 10.sp,
                        color = NeonGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {


                IconButton(
                    onClick = { showHistoryDialog = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Room Geçmiş Konuşmalar",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (onOpenVoiceAssistant != null) {
                    IconButton(
                        onClick = onOpenVoiceAssistant,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Sesli Asistan Modu",
                            tint = NeonMagenta,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { showCognitiveDialog = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Zeka & Duygu Paneli",
                        tint = NeonBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Temizle",
                        tint = NeonMagenta.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 🧠 Yapay Zeka Seçim Paneli (Keon AI PRO vs Keon AI Lite) - Alev Çekirdeği Altında
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Keon AI PRO Card
            val isPro = selectedAiModel == KeonAiModel.PRO
            Surface(
                onClick = { onSelectAiModel(KeonAiModel.PRO) },
                shape = RoundedCornerShape(12.dp),
                color = if (isPro) com.example.ui.theme.FlameNeonOrange.copy(alpha = 0.22f) else DarkSurface,
                border = BorderStroke(
                    1.dp,
                    if (isPro) com.example.ui.theme.FlameNeonOrange else Color.White.copy(alpha = 0.15f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔥", fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Keon AI PRO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPro) com.example.ui.theme.FlameNeonOrange else TextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isPro) com.example.ui.theme.FlameNeonOrange else Color.Gray.copy(alpha = 0.3f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "HIGH THINKING",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPro) Color.Black else Color.White
                                )
                            }
                        }
                        Text(
                            text = "Derin Mantık Engine",
                            fontSize = 9.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }

            // Keon AI Lite Card
            val isLite = selectedAiModel == KeonAiModel.LITE
            Surface(
                onClick = { onSelectAiModel(KeonAiModel.LITE) },
                shape = RoundedCornerShape(12.dp),
                color = if (isLite) NeonCyan.copy(alpha = 0.22f) else DarkSurface,
                border = BorderStroke(
                    1.dp,
                    if (isLite) NeonCyan else Color.White.copy(alpha = 0.15f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🚀", fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Keon AI Lite",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLite) NeonCyan else TextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isLite) NeonCyan else Color.Gray.copy(alpha = 0.3f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "LOW LATENCY",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLite) Color.Black else Color.White
                                )
                            }
                        }
                        Text(
                            text = "Hızlı Yanıt Engine",
                            fontSize = 9.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // 📊 Model Hız & Yanıt Süresi Gösterge Paneli
        ModelPerformanceBenchmarkPanel(
            selectedModel = selectedAiModel
        )



        if (showHistoryDialog) {
            val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("tr")) }
            val filteredList = remember(historySearchQuery, allHistoryDesc) {
                if (historySearchQuery.isBlank()) {
                    allHistoryDesc
                } else {
                    allHistoryDesc.filter {
                        it.text.contains(historySearchQuery, ignoreCase = true) ||
                                it.sender.contains(historySearchQuery, ignoreCase = true)
                    }
                }
            }

            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showHistoryDialog = false },
                containerColor = DarkSurface,
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.History, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("📜 Room Veritabanı Geçmişi", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { showHistoryDialog = false }, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat", tint = TextSecondary)
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Room DB Stats Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("💾 Local SQLite / Room Engine", fontSize = 11.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                                    Text("Cihazda $messageCount adet konuşma kaydı saklanıyor.", fontSize = 11.sp, color = TextSecondary)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(NeonBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("${allHistoryDesc.size} Kayıt", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Export Action Buttons Row (TXT & JSON)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .clickable { exportChatAsTxt(context, filteredList) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("📄 TXT Dışa Aktar", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                    .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .clickable { exportChatAsJson(context, filteredList) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("📑 JSON Dışa Aktar", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Search Field for Room Database
                        OutlinedTextField(
                            value = historySearchQuery,
                            onValueChange = { historySearchQuery = it },
                            placeholder = { Text("Geçmiş mesajlarda ara...", fontSize = 12.sp, color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp)) },
                            trailingIcon = {
                                if (historySearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { historySearchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Temizle", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Saved Messages List
                        if (filteredList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (historySearchQuery.isEmpty()) "Henüz veritabanında saklanan sohbet yok." else "Aramanıza uygun mesaj bulunamadı.",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredList, key = { it.id }) { msg ->
                                    val dateStr = remember(msg.timestamp) {
                                        try {
                                            dateFormat.format(Date(msg.timestamp))
                                        } catch (e: Exception) {
                                            "Tarih Bilinmiyor"
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (msg.sender == "user") Color(0xFF1E293B) else Color(0xFF0F172A),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (msg.sender == "user") NeonCyan.copy(alpha = 0.3f) else NeonGreen.copy(alpha = 0.3f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = when (msg.sender) {
                                                        "user" -> "👤 Sen"
                                                        "ai" -> "🤖 Keon AI"
                                                        else -> "⚙️ Sistem"
                                                    },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (msg.sender == "user") NeonCyan else NeonGreen
                                                )

                                                Text(
                                                    text = dateStr,
                                                    fontSize = 10.sp,
                                                    color = TextSecondary
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = msg.text,
                                                fontSize = 12.sp,
                                                color = TextPrimary,
                                                maxLines = 6
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Copy Button
                                                IconButton(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                        val clip = ClipData.newPlainText("Keon AI Message", msg.text)
                                                        clipboard.setPrimaryClip(clip)
                                                        Toast.makeText(context, "Kopyalandı!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "Kopyala",
                                                        tint = NeonCyan,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                // Delete Single Item Button
                                                IconButton(
                                                    onClick = {
                                                        onDeleteMessage(msg.id)
                                                        Toast.makeText(context, "Mesaj veritabanından silindi.", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Room'dan Sil",
                                                        tint = NeonMagenta,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.TextButton(
                            onClick = {
                                onClearChat()
                                Toast.makeText(context, "Room veritabanı sohbet geçmişi temizlendi.", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("🧹 Tümünü Temizle", color = NeonMagenta, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        androidx.compose.material3.TextButton(
                            onClick = { showHistoryDialog = false }
                        ) {
                            Text("Kapat", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }

        if (showCognitiveDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showCognitiveDialog = false },
                containerColor = DarkSurface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🧠 Keon AI Bilişsel & Duygusal Panel", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("❤️ Duygusal Zeka & Empati", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonMagenta)
                                Text("Soru ve mesajlardaki hissi analiz eder; samimi, anlayışlı ve insani bir iletişim tonu belirler.", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("🧠 Mantıksal Düşünme Zinciri", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                Text("Yanıt vermeden önce soruyu zihninde parçalara böler, mantık bağlarını kurar ve doğruluğunu tartar.", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("📚 Adaptif Öğrenme & Hafıza", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                                Text("Sohbet geçmişinizdeki detayları öğrenir ve gelecek sorularda bu bağlamı hatırlar.", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFFFCC00).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("🎯 Saf Odak & Sonuç Çıkarma", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFCC00))
                                Text("Alakasız detayları eler; doğrudan sorulan soruya odaklanıp net ve kesin bir sonuç çıkarır.", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { showCognitiveDialog = false }) {
                        Text("Anladım", color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(DarkSurface.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(message = msg)
            }

            if (isTyping) {
                item {
                    TypingDotsIndicator()
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Suggested Prompts Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
        ) {
            items(suggestedPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .background(DarkSurface, RoundedCornerShape(20.dp))
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { onSendMessage(prompt) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = prompt,
                        fontSize = 12.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Listening Status Banner & Live Waveform (if active)
        AnimatedVisibility(visible = isListening) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                com.example.ui.components.VoiceWaveformVisualizer(
                    isListening = isListening,
                    isSpeaking = false,
                    audioLevel = micRmsDbLevel,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Input Area
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Sorunuzu yazın veya konuşun...", color = TextSecondary.copy(alpha = 0.6f), fontSize = 13.sp)
                },
                singleLine = false,
                maxLines = 4,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = NeonCyan.copy(alpha = 0.3f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                            focusManager.clearFocus()
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Microphone Voice-to-Text Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .pulsatingNeonGlow(
                        glowColor = if (isListening) NeonMagenta else NeonMagenta.copy(alpha = 0.5f),
                        minAlpha = if (isListening) 0.5f else 0.2f,
                        maxAlpha = if (isListening) 0.95f else 0.6f,
                        glowRadius = if (isListening) 16.dp else 10.dp,
                        shape = CircleShape,
                        borderWidth = 1.2.dp
                    )
                    .background(
                        brush = if (isListening) Brush.linearGradient(listOf(NeonMagenta, Color(0xFFFF0088)))
                        else Brush.linearGradient(listOf(DarkSurface, DarkSurface)),
                        shape = CircleShape
                    )
                    .clickable { requestMicAndVoiceToText() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Sesle Yaz (Voice to Text)",
                    tint = if (isListening) Color.White else NeonMagenta,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Send Button
            val isSendActive = inputText.isNotBlank() && !isTyping
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .then(
                        if (isSendActive) {
                            Modifier.pulsatingNeonGlow(
                                glowColor = NeonCyan,
                                minAlpha = 0.4f,
                                maxAlpha = 0.9f,
                                glowRadius = 14.dp,
                                shape = CircleShape,
                                borderWidth = 1.5.dp
                            )
                        } else Modifier
                    )
                    .background(
                        brush = if (isSendActive) Brush.linearGradient(listOf(NeonCyan, NeonBlue)) else Brush.linearGradient(listOf(DarkSurface, Color(0xFF1E1E38))),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSendActive) NeonCyan else Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                    .clickable(enabled = isSendActive) {
                        onSendMessage(inputText)
                        inputText = ""
                        focusManager.clearFocus()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Gönder",
                    tint = if (isSendActive) Color.Black else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessageEntity) {
    val context = LocalContext.current

    when (message.sender) {
        "user" -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(
                            brush = Brush.linearGradient(listOf(NeonCyan, NeonBlue)),
                            shape = RoundedCornerShape(18.dp, 18.dp, 2.dp, 18.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = message.text,
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        "ai" -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.92f)) {
                    val toneObj = if (message.emotionTone.isNotBlank()) {
                        com.example.data.EmotionalTone.fromCode(message.emotionTone)
                    } else {
                        com.example.data.EmotionalTone.CURIOSITY_THOUGHTFUL
                    }
                    val badgeColor = Color(toneObj.badgeColorHex)

                    // Cognitive & Emotional Tone Tags Bar
                    Row(
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(DarkSurface, RoundedCornerShape(8.dp))
                                .border(0.8.dp, badgeColor.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("${toneObj.emoji} ${toneObj.displayName}", fontSize = 9.sp, color = badgeColor, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .background(DarkSurface, RoundedCornerShape(8.dp))
                                .border(0.8.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("🧠 Çıkarım & Odak", fontSize = 9.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .background(DarkSurface, RoundedCornerShape(8.dp))
                                .border(0.8.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("📚 Hafıza", fontSize = 9.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(listOf(NeonMagenta, Color(0xFFFF0088))),
                                shape = RoundedCornerShape(18.dp, 18.dp, 18.dp, 2.dp)
                            )
                            .border(
                                1.dp,
                                NeonMagenta.copy(alpha = 0.6f),
                                RoundedCornerShape(18.dp, 18.dp, 18.dp, 2.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column {
                            com.example.ui.components.AiGeneratedMediaContent(
                                rawText = message.text,
                                messageId = message.id
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Keon AI • ${toneObj.emoji} ${toneObj.displayName}",
                                    fontSize = 10.sp,
                                    color = TextPrimary.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Bold
                                )

                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Kopyala",
                                    tint = TextPrimary.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable {
                                            val clipboard =
                                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Keon AI", message.text)
                                            clipboard.setPrimaryClip(clip)
                                            Toast
                                                .makeText(context, "Kopyalandı!", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
        "system" -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(NeonGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = message.text,
                        color = NeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun TypingDotsIndicator() {
    val dot1 = remember { Animatable(0f) }
    val dot2 = remember { Animatable(0f) }
    val dot3 = remember { Animatable(0f) }

    var thinkingPhase by remember { mutableStateOf("🧠 Soru ve Duygu Tespiti Yapılıyor...") }

    LaunchedEffect(Unit) {
        val animationSpec = infiniteRepeatable<Float>(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
        launch { dot1.animateTo(1f, animationSpec) }
        launch {
            kotlinx.coroutines.delay(150)
            dot2.animateTo(1f, animationSpec)
        }
        launch {
            kotlinx.coroutines.delay(300)
            dot3.animateTo(1f, animationSpec)
        }

        // Cycle through cognitive steps
        launch {
            kotlinx.coroutines.delay(800)
            thinkingPhase = "📚 Sohbet Hafızasından Bağlam Öğreniliyor..."
            kotlinx.coroutines.delay(1000)
            thinkingPhase = "🎯 Mantıksal Çıkarım & Saf Sonuç Üretiliyor..."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .background(DarkSurface, RoundedCornerShape(16.dp))
            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(y = (-4 * dot1.value).dp)
                            .background(NeonMagenta, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(y = (-4 * dot2.value).dp)
                            .background(NeonCyan, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(y = (-4 * dot3.value).dp)
                            .background(NeonGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Keon AI Düşünüyor",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "Keon AI Özgün Motoru",
                    fontSize = 10.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = thinkingPhase,
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

private fun exportChatAsTxt(context: Context, messages: List<ChatMessageEntity>) {
    if (messages.isEmpty()) {
        Toast.makeText(context, "Dışa aktarılacak mesaj bulunamadı.", Toast.LENGTH_SHORT).show()
        return
    }
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("tr"))
    val sb = StringBuilder()
    sb.append("===========================================\n")
    sb.append("           KEON AI SOHBET GEÇMİŞİ          \n")
    sb.append("   Dışa Aktarma Tarihi: ${dateFormat.format(Date())}\n")
    sb.append("   Toplam Mesaj Sayısı: ${messages.size}\n")
    sb.append("===========================================\n\n")

    messages.sortedBy { it.timestamp }.forEach { msg ->
        val timeStr = try { dateFormat.format(Date(msg.timestamp)) } catch (e: Exception) { "" }
        val senderTitle = when (msg.sender) {
            "user" -> "KULLANICI"
            "ai" -> "KEON AI"
            else -> "SİSTEM"
        }
        sb.append("[$timeStr] $senderTitle:\n")
        sb.append("${msg.text}\n")
        if (msg.emotionTone.isNotBlank()) {
            sb.append("(Duygu Tonu: ${msg.emotionTone})\n")
        }
        sb.append("-------------------------------------------\n")
    }

    shareChatFile(context, "keon_chat_history_${System.currentTimeMillis()}.txt", sb.toString(), "text/plain")
}

private fun exportChatAsJson(context: Context, messages: List<ChatMessageEntity>) {
    if (messages.isEmpty()) {
        Toast.makeText(context, "Dışa aktarılacak mesaj bulunamadı.", Toast.LENGTH_SHORT).show()
        return
    }
    val jsonArray = org.json.JSONArray()
    messages.sortedBy { it.timestamp }.forEach { msg ->
        val obj = org.json.JSONObject()
        obj.put("id", msg.id)
        obj.put("sender", msg.sender)
        obj.put("text", msg.text)
        obj.put("emotionTone", msg.emotionTone)
        obj.put("timestamp", msg.timestamp)
        jsonArray.put(obj)
    }

    shareChatFile(context, "keon_chat_history_${System.currentTimeMillis()}.json", jsonArray.toString(2), "application/json")
}

private fun shareChatFile(context: Context, fileName: String, content: String, mimeType: String) {
    try {
        val file = java.io.File(context.cacheDir, fileName)
        file.writeText(content)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Keon AI Sohbet Geçmişi Dışa Aktarımı")
            putExtra(Intent.EXTRA_TEXT, "Keon AI uygulamasından dışa aktarılan sohbet geçmişi.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Sohbet Geçmişini Dışa Aktar"))
    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Keon AI Sohbet Geçmişi")
            putExtra(Intent.EXTRA_TEXT, content)
        }
        context.startActivity(Intent.createChooser(intent, "Sohbet Geçmişini Paylaş"))
    }
}
