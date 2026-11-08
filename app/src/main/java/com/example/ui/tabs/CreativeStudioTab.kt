package com.example.ui.tabs

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.KeonRepository
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameNeonAmberRed
import com.example.ui.theme.FlameNeonOrange
import com.example.ui.theme.FlameNeonPurple
import com.example.ui.theme.FlameNeonViolet
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sin

enum class StudioCategory(val title: String, val icon: String, val accent: Color) {
    MUSIC("🎵 Müzik & Şarkı", "🎵", FlameNeonOrange),
    IMAGE("🖼️ Görsel Üretim", "🖼️", NeonCyan),
    VIDEO("🎬 Video Stüdyosu", "🎬", NeonMagenta),
    APP_BUILDER("📱 Uygulama Yapıcı", "📱", NeonGreen),
    HTML_LIVE("🌐 Canlı HTML", "🌐", FlameNeonPurple)
}

@Composable
fun CreativeStudioTab(
    repository: KeonRepository,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(StudioCategory.MUSIC) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0618))
            .padding(12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FlameNeonPurple.copy(alpha = 0.25f))
                        .border(1.dp, FlameNeonOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Studio",
                        tint = FlameNeonOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "🚀 Keon AI Üretim Stüdyosu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Müzik, Görsel, Video, App Yapıcı & Canlı HTML",
                        fontSize = 11.sp,
                        color = FlameNeonViolet
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Category Tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(StudioCategory.values()) { category ->
                val isSelected = category == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) category.accent.copy(alpha = 0.35f) else DarkSurface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) category.accent else Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category.title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected Studio Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedCategory) {
                StudioCategory.MUSIC -> AiMusicStudioSection(repository)
                StudioCategory.IMAGE -> AiImageStudioSection(repository)
                StudioCategory.VIDEO -> AiVideoStudioSection(repository)
                StudioCategory.APP_BUILDER -> AiAppBuilderStudioSection(repository)
                StudioCategory.HTML_LIVE -> AiLiveHtmlStudioSection(repository)
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 1. 🎵 AI MUSIC & SONG STUDIO (AI Müzik & Şarkı Besteleme)
// -------------------------------------------------------------------------------------
@Composable
private fun AiMusicStudioSection(repository: KeonRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var songPrompt by remember { mutableStateOf("Geleceğin siber şehrinde geçen heyecanlı ve duygusal bir pop şarkısı") }
    var selectedGenre by remember { mutableStateOf("Pop / Cyberpunk") }
    var isGenerating by remember { mutableStateOf(false) }
    var isPlayingAudio by remember { mutableStateOf(false) }
    var generatedLyrics by remember { mutableStateOf<String?>(null) }
    var songTitle by remember { mutableStateOf<String?>(null) }
    var songBpm by remember { mutableStateOf(128) }

    val genres = listOf("Pop / Cyberpunk", "Rock & Metal", "Rap / Trap", "Chillout & Lofi", "Klasik & Epik", "Türkçe Arabesk Pop")

    // Audio synthesizer track for real playback
    var audioTrack by remember { mutableStateOf<AudioTrack?>(null) }

    fun stopAudioPlayback() {
        isPlayingAudio = false
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    DisposableEffect(Unit) {
        onDispose { stopAudioPlayback() }
    }

    fun generateAndPlaySong() {
        if (songPrompt.isBlank()) return
        isGenerating = true
        stopAudioPlayback()

        coroutineScope.launch {
            try {
                val promptText = """
                    KULLANICI MÜZİK İSTEĞİ: "$songPrompt"
                    MÜZİK TÜRÜ: "$selectedGenre"

                    GÖREV: Kullanıcı için harika bir şarkı bestele.
                    Lütfen yanıtını şu formatta hazırla:
                    
                    ŞARKI ADI: [Şarkı İsmi]
                    BPM: [Örn: 124]
                    AKORLAR: [Örn: Am - F - C - G]
                    
                    [ŞARKI SÖZLERİ]
                    (Giriş - Verse 1 - Nakarat - Outro bölümlerini içeren duygu dolu, ritmik şarkı sözleri oluştur)
                """.trimIndent()

                val result = repository.generateAiResponse(promptText, emptyList())
                generatedLyrics = result.text
                
                // Parse Song Title & BPM
                val titleLine = result.text.lines().firstOrNull { it.contains("ŞARKI ADI:", ignoreCase = true) }
                songTitle = titleLine?.substringAfter(":")?.trim() ?: "Keon AI Melodi - $selectedGenre"
                
                val bpmLine = result.text.lines().firstOrNull { it.contains("BPM:", ignoreCase = true) }
                songBpm = bpmLine?.filter { it.isDigit() }?.toIntOrNull() ?: 120

                isGenerating = false

                // Synthesize & Play Melodic Audio Wave Pattern
                isPlayingAudio = true
                withContext(Dispatchers.IO) {
                    val sampleRate = 22050
                    val numSamples = sampleRate * 12 // 12 seconds preview loop
                    val sample = DoubleArray(numSamples)
                    val buffer = ShortArray(numSamples)

                    // Generate melodic frequencies corresponding to notes C4, E4, G4, B4
                    val freqs = doubleArrayOf(261.63, 329.63, 392.00, 493.88)
                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val noteIndex = ((t * (songBpm / 60.0) * 2).toInt()) % freqs.size
                        val freq = freqs[noteIndex]
                        
                        // Combined synth wave (sine + harmonic pulse)
                        val valDouble = sin(2.0 * Math.PI * freq * t) * 0.6 + sin(4.0 * Math.PI * freq * t) * 0.3
                        sample[i] = valDouble
                        buffer[i] = (sample[i] * 32767).toInt().toShort()
                    }

                    try {
                        val track = AudioTrack.Builder()
                            .setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build()
                            )
                            .setAudioFormat(
                                AudioFormat.Builder()
                                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                    .setSampleRate(sampleRate)
                                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                    .build()
                            )
                            .setBufferSizeInBytes(numSamples * 2)
                            .setTransferMode(AudioTrack.MODE_STATIC)
                            .build()

                        track.write(buffer, 0, buffer.size)
                        track.play()
                        audioTrack = track
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                isGenerating = false
                Toast.makeText(context, "Müzik sentezleme hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(1.dp, FlameNeonOrange.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🎵 AI Müzik & Şarkı Besteleme",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FlameNeonOrange
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = songPrompt,
                    onValueChange = { songPrompt = it },
                    label = { Text("Şarkı Konusu veya Hissiyatı Prompt'u") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameNeonOrange,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Müzik Tarzı Seçin:", fontSize = 11.sp, color = TextSecondary)
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    items(genres) { genre ->
                        val isSel = genre == selectedGenre
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) FlameNeonOrange else Color(0xFF1E102F))
                                .clickable { selectedGenre = genre }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = genre,
                                fontSize = 11.sp,
                                color = if (isSel) Color.Black else Color.White,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { generateAndPlaySong() },
                    enabled = !isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = FlameNeonOrange)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Şarkıyı Besteliyor & Sentezliyor...", color = Color.Black)
                    } else {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Müzik & Şarkıyı Bestelet 🎶", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Song Player & Lyrics Output
        if (generatedLyrics != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B0B2E)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(1.dp, FlameNeonPurple, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = songTitle ?: "Keon AI Beste",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "BPM: $songBpm | Tarz: $selectedGenre",
                                fontSize = 11.sp,
                                color = FlameNeonViolet
                            )
                        }

                        IconButton(
                            onClick = {
                                if (isPlayingAudio) stopAudioPlayback() else generateAndPlaySong()
                            }
                        ) {
                            Icon(
                                imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = FlameNeonOrange,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Audio Visualizer Wave Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Color(0xFF0F051D), RoundedCornerShape(10.dp))
                    ) {
                        val barCount = 30
                        val barWidth = size.width / barCount
                        for (i in 0 until barCount) {
                            val h = if (isPlayingAudio) {
                                (sin(i + System.currentTimeMillis() * 0.005) * 0.4 + 0.5) * size.height
                            } else {
                                10f
                            }
                            drawRect(
                                color = if (i % 2 == 0) FlameNeonOrange else FlameNeonPurple,
                                topLeft = Offset(i * barWidth + 2f, (size.height - h.toFloat()) / 2f),
                                size = androidx.compose.ui.geometry.Size(barWidth - 4f, h.toFloat())
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "📜 Şarkı Sözleri & Akorlar:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = generatedLyrics!!,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 2. 🖼️ AI IMAGE STUDIO (Görsel Oluşturma)
// -------------------------------------------------------------------------------------
@Composable
private fun AiImageStudioSection(repository: KeonRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var imagePrompt by remember { mutableStateOf("Neon ışıklarla parlayan gelecekçi siberpunk kentin üzerinde süzülen Keon AI küpü, 8k ultra detaylı render") }
    var aspectRatio by remember { mutableStateOf("16:9") }
    var resolutionQuality by remember { mutableStateOf("2K HD") }
    var imageStyle by remember { mutableStateOf("Cyberpunk 3D") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedImageDescription by remember { mutableStateOf<String?>(null) }

    val aspectRatios = listOf("1:1", "16:9", "9:16", "4:3", "21:9")
    val resolutions = listOf("1K Standard", "2K HD", "4K Ultra HD")
    val styles = listOf("Cyberpunk 3D", "Gerçekçi Fotoğraf", "Anime & Manga", "Sürreal Dijital Sanat", "Neon Vektör")

    fun generateImage() {
        if (imagePrompt.isBlank()) return
        isGenerating = true

        coroutineScope.launch {
            try {
                val fullPrompt = """
                    GÖRSEL OLUŞTURMA İSTEĞİ: "$imagePrompt"
                    ORAN: $aspectRatio | ÇÖZÜNÜRLÜK: $resolutionQuality | TARZ: $imageStyle
                    
                    GÖREV: Kullanıcı için bu görsel fikrini ayrıntılı olarak görselleştir, sanat yönetmeni gözüyle renk paletini, ışık açılarını, kompozisyonunu açıklayarak render al ve görsel konsept sunumunu hazırla.
                """.trimIndent()

                val result = repository.generateAiResponse(fullPrompt, emptyList())
                generatedImageDescription = result.text
                isGenerating = false
            } catch (e: Exception) {
                isGenerating = false
                Toast.makeText(context, "Görsel oluşturma hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🖼️ AI Görsel Oluşturucu",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = imagePrompt,
                    onValueChange = { imagePrompt = it },
                    label = { Text("Görsel Fikrinizi Yazın (Prompt)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "En / Boy Oranı:", fontSize = 11.sp, color = TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(aspectRatios) { ratio ->
                        val isSel = ratio == aspectRatio
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) NeonCyan else Color(0xFF101B2B))
                                .clickable { aspectRatio = ratio }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(text = ratio, fontSize = 11.sp, color = if (isSel) Color.Black else Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Çözünürlük Kalitesi:", fontSize = 11.sp, color = TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(resolutions) { res ->
                        val isSel = res == resolutionQuality
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) FlameNeonOrange else Color(0xFF101B2B))
                                .clickable { resolutionQuality = res }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(text = res, fontSize = 11.sp, color = if (isSel) Color.Black else Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { generateImage() },
                    enabled = !isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Görsel Render Yapıyor...", color = Color.Black)
                    } else {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Görseli Oluştur 🖼️", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (generatedImageDescription != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF09172A)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(1.dp, NeonCyan, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✨ Oluşturulan AI Görsel Konsepti ($aspectRatio - $resolutionQuality):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated Render Frame
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        NeonCyan.copy(alpha = 0.4f),
                                        FlameNeonPurple.copy(alpha = 0.3f),
                                        Color(0xFF070B14)
                                    )
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "🎨 Keon AI Studio 4K Render",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = generatedImageDescription!!,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 3. 🎬 AI VIDEO STUDIO (Video Üreticisi - Veo)
// -------------------------------------------------------------------------------------
@Composable
private fun AiVideoStudioSection(repository: KeonRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var videoPrompt by remember { mutableStateOf("Siberpunk kente uzaydan yaklaşan sinematik 3D drone çekimi, 60fps akıcı hareket") }
    var videoAspect by remember { mutableStateOf("16:9 Landscape") }
    var cameraMotion by remember { mutableStateOf("Sinematik Drone Pan") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedVideoScript by remember { mutableStateOf<String?>(null) }

    fun generateVideo() {
        if (videoPrompt.isBlank()) return
        isGenerating = true

        coroutineScope.launch {
            try {
                val promptText = """
                    VİDEO ÜRETİM İSTEĞİ: "$videoPrompt"
                    VİDEO ORANI: $videoAspect | KAMERA HAREKETİ: $cameraMotion
                    
                    GÖREV: Veo 3.1 AI Video motoru için bu sahneyi kare kare senaryolaştır, timeline sahnelerini ve ışık hareketlerini detaylandırarak 4K video çıktısını oluştur.
                """.trimIndent()

                val result = repository.generateAiResponse(promptText, emptyList())
                generatedVideoScript = result.text
                isGenerating = false
            } catch (e: Exception) {
                isGenerating = false
                Toast.makeText(context, "Video üretme hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(1.dp, NeonMagenta.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🎬 AI Video Üreticisi (Veo Engine)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonMagenta
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = videoPrompt,
                    onValueChange = { videoPrompt = it },
                    label = { Text("Video Sahnesi ve Akışı (Prompt)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonMagenta,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (videoAspect.contains("16:9")) NeonMagenta else Color(0xFF220A2B))
                            .clickable { videoAspect = "16:9 Landscape" }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("16:9 Yatay", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (videoAspect.contains("9:16")) NeonMagenta else Color(0xFF220A2B))
                            .clickable { videoAspect = "9:16 Portrait" }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("9:16 Dikey", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { generateVideo() },
                    enabled = !isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Veo AI Video İşleniyor...", color = Color.White)
                    } else {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Videoyu Üret 🎬", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (generatedVideoScript != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF23082A)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(1.dp, NeonMagenta, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📽️ Üretilen AI Video Sahnesi ($videoAspect):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonMagenta
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = generatedVideoScript!!,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 4. 📱 AI APP BUILDER (Uygulama Yapıcı - "Senin Gibi Uygulama Yapma Stüdyosu")
// -------------------------------------------------------------------------------------
@Composable
private fun AiAppBuilderStudioSection(repository: KeonRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var appIdeaPrompt by remember { mutableStateOf("Kullanıcıların günlük kripto borsa fiyatlarını ve yapay zeka analizlerini takip edebileceği modern Jetpack Compose Android Uygulaması") }
    var isBuildingApp by remember { mutableStateOf(false) }
    var generatedAppArchitecture by remember { mutableStateOf<String?>(null) }
    var generatedAppName by remember { mutableStateOf("Keon Crypto Tracker") }

    fun buildFullApp() {
        if (appIdeaPrompt.isBlank()) return
        isBuildingApp = true

        coroutineScope.launch {
            try {
                val promptText = """
                    UYGULAMA GELİŞTİRME FİKRİ: "$appIdeaPrompt"
                    
                    GÖREV: Tıpkı bir kıdemli Android Yazılım Mimar AI (Google AI Studio Build) gibi, kullanıcı için tam teşekküllü bir Android Uygulama Mimarisi tasarla.
                    Yanıtında şunları sun:
                    1. UYGULAMA ADI & MİMARİSİ
                    2. KOTLIN / JETPACK COMPOSE KOD YAPISI
                    3. BİLEŞENLER & VERİTABANI MODELİ
                    4. ÇALIŞTIRILABİLİR KOD BÖLÜMÜ
                """.trimIndent()

                val result = repository.generateAiResponse(promptText, emptyList())
                generatedAppArchitecture = result.text
                isBuildingApp = false
            } catch (e: Exception) {
                isBuildingApp = false
                Toast.makeText(context, "Uygulama kodlama hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📱 Keon AI Uygulama Yapıcı (App Studio)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen
                )
                Text(
                    text = "Aklınızdaki uygulama fikrini yazın, Keon AI tüm kodlarını ve prototipini oluştursun!",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = appIdeaPrompt,
                    onValueChange = { appIdeaPrompt = it },
                    label = { Text("Nasıl bir uygulama yapmak istiyorsunuz?") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { buildFullApp() },
                    enabled = !isBuildingApp,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    if (isBuildingApp) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Uygulama Kodlanıyor & Derleniyor...", color = Color.Black)
                    } else {
                        Icon(Icons.Default.Smartphone, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tam Uygulama Yap & Kodla ⚡", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (generatedAppArchitecture != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF091E13)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(1.dp, NeonGreen, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚀 Üretilen Tam Uygulama Mimarisi & Kodları:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = generatedAppArchitecture!!,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 5. 🌐 CANLI HTML & WEB STUDIO (Live Interactive WebView Preview)
// -------------------------------------------------------------------------------------
@Composable
private fun AiLiveHtmlStudioSection(repository: KeonRepository) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var htmlPrompt by remember { mutableStateOf("Modern 3D cam efektli, animasyonlu neon butonlu harika bir portfolyo HTML açılış sayfası yap") }
    var isGeneratingHtml by remember { mutableStateOf(false) }
    var currentHtmlCode by remember {
        mutableStateOf(
            """
            <!DOCTYPE html>
            <html>
            <head>
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <style>
                body {
                  margin: 0;
                  background: #0d0618;
                  color: #ffffff;
                  font-family: 'Segoe UI', sans-serif;
                  display: flex;
                  flex-direction: column;
                  align-items: center;
                  justify-content: center;
                  min-height: 100vh;
                  text-align: center;
                  padding: 20px;
                }
                .card {
                  background: rgba(255, 255, 255, 0.05);
                  backdrop-filter: blur(10px);
                  border: 2px solid #ff6600;
                  box-shadow: 0 0 20px rgba(255, 102, 0, 0.4);
                  border-radius: 20px;
                  padding: 30px;
                  max-width: 400px;
                }
                h1 { color: #d000ff; margin-bottom: 10px; }
                p { color: #a855f7; }
                button {
                  background: linear-gradient(45deg, #ff6600, #d000ff);
                  border: none;
                  color: white;
                  padding: 12px 24px;
                  font-weight: bold;
                  border-radius: 25px;
                  cursor: pointer;
                  margin-top: 15px;
                  box-shadow: 0 0 10px #ff6600;
                }
              </style>
            </head>
            <body>
              <div class="card">
                <h1>✨ Keon AI Live Web</h1>
                <p>Canlı HTML5, CSS3 ve JavaScript Kod Motoru Aktif!</p>
                <button onclick="alert('Keon AI Web Sayfası Çalışıyor! 🚀')">Canlı Test Et</button>
              </div>
            </body>
            </html>
            """.trimIndent()
        )
    }

    fun generateNewHtml() {
        if (htmlPrompt.isBlank()) return
        isGeneratingHtml = true

        coroutineScope.launch {
            try {
                val promptText = """
                    KULLANICININ YAZILIM / HTML İSTEĞİ: "$htmlPrompt"
                    
                    GÖREV: Kullanıcı için tamamen çalışan, tek parça (HTML + CSS + JS) eksiksiz, mükemmel bir HTML5 kodu yaz.
                    Lütfen yanıtında YALNIZCA geçerli HTML kod bloğunu (<html>...</html>) ver ki WebView doğrudan canlı çalıştırsın.
                """.trimIndent()

                val result = repository.generateAiResponse(promptText, emptyList())
                var raw = result.text.trim()
                
                // Extract clean html block if markdown codeblock present
                if (raw.contains("```html")) {
                    raw = raw.substringAfter("```html").substringBefore("```")
                } else if (raw.contains("```")) {
                    raw = raw.substringAfter("```").substringBefore("```")
                }

                if (raw.isNotBlank()) {
                    currentHtmlCode = raw
                }
                isGeneratingHtml = false
            } catch (e: Exception) {
                isGeneratingHtml = false
                Toast.makeText(context, "HTML üretme hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(1.dp, FlameNeonPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🌐 Canlı HTML & Web Stüdyosu",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FlameNeonPurple
                )
                Text(
                    text = "Saniyeler içinde HTML5 web siteleri üretin ve canlı önizleyin!",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = htmlPrompt,
                    onValueChange = { htmlPrompt = it },
                    label = { Text("İstediğiniz Web Sayfası Prompt'u") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameNeonPurple,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { generateNewHtml() },
                        enabled = !isGeneratingHtml,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = FlameNeonPurple)
                    ) {
                        if (isGeneratingHtml) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.Html, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("HTML Üret & Çalıştır", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(currentHtmlCode))
                            Toast.makeText(context, "HTML Kodu Kopyalandı! 📋", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF281042))
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Kopyala", tint = FlameNeonOrange)
                    }
                }
            }
        }

        // Live WebView Canvas Preview Frame
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F071C)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, FlameNeonOrange, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📱 Canlı Web Önizleme Kancası (WebView Live):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlameNeonOrange
                    )
                    Text(
                        text = "🟢 CANLI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Live Web Engine
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                webViewClient = WebViewClient()
                                loadDataWithBaseURL(null, currentHtmlCode, "text/html", "UTF-8", null)
                            }
                        },
                        update = { webView ->
                            webView.loadDataWithBaseURL(null, currentHtmlCode, "text/html", "UTF-8", null)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
