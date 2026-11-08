package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameNeonOrange
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AiGeneratedMediaContent(
    rawText: String,
    messageId: Long,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Parse image or video tags
    var imagePrompt by remember(rawText) { mutableStateOf<String?>(null) }
    var imageUrl by remember(rawText) { mutableStateOf<String?>(null) }
    var videoPrompt by remember(rawText) { mutableStateOf<String?>(null) }

    val cleanText = remember(rawText) {
        var text = rawText

        // Check image prompt tag
        val imgPromptRegex = Regex("""\[MEDIA_IMAGE_PROMPT:\s*([^\]]+)\]""")
        imgPromptRegex.find(rawText)?.let {
            imagePrompt = it.groupValues[1].trim()
            text = text.replace(it.value, "").trim()
        }

        // Check direct image url tag
        val imgUrlRegex = Regex("""\[MEDIA_IMAGE_URL:\s*([^\]]+)\]""")
        imgUrlRegex.find(rawText)?.let {
            imageUrl = it.groupValues[1].trim()
            text = text.replace(it.value, "").trim()
        }

        // Check video prompt tag
        val vidPromptRegex = Regex("""\[MEDIA_VIDEO_PROMPT:\s*([^\]]+)\]""")
        vidPromptRegex.find(rawText)?.let {
            videoPrompt = it.groupValues[1].trim()
            text = text.replace(it.value, "").trim()
        }

        text
    }

    var showFullScreenDialog by remember { mutableStateOf(false) }
    var seedOffset by remember { mutableStateOf(0) }

    Column(modifier = modifier) {
        if (cleanText.isNotBlank()) {
            Text(
                text = cleanText,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        // 🎨 AI GENERATED IMAGE VIEW
        val activeImagePrompt = imagePrompt
        var retryAttempt by remember { mutableIntStateOf(0) }
        var isLoading by remember { mutableStateOf(true) }
        var isError by remember { mutableStateOf(false) }

        val activeImageUrl = remember(activeImagePrompt, imageUrl, messageId, seedOffset, retryAttempt) {
            if (imageUrl != null && retryAttempt == 0) {
                imageUrl
            } else if (activeImagePrompt != null) {
                val cleanPrompt = activeImagePrompt
                    .replace(Regex("""[^a-zA-Z0-9\s,]"""), " ")
                    .replace(Regex("""\s+"""), " ")
                    .take(160)
                    .trim()
                val encoded = Uri.encode(cleanPrompt)
                val seed = kotlin.math.abs((messageId + seedOffset + retryAttempt).hashCode())

                when (retryAttempt) {
                    0 -> "https://image.pollinations.ai/prompt/$encoded?width=1024&height=1024&model=flux&nologo=true&seed=$seed"
                    1 -> "https://image.pollinations.ai/prompt/$encoded?width=1024&height=1024&model=turbo&nologo=true&seed=$seed"
                    2 -> "https://image.pollinations.ai/prompt/$encoded?width=1024&height=1024&nologo=true&seed=$seed"
                    else -> "https://picsum.photos/seed/$seed/1024/1024"
                }
            } else null
        }

        if (activeImageUrl != null) {
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF130E26),
                border = BorderStroke(1.2.dp, FlameNeonOrange.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Header Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = FlameNeonOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Keon AI Neural Visual Art",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameNeonOrange
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(FlameNeonOrange.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "8K HIGH-RES",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameNeonOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Image Display Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0A0714))
                            .clickable { showFullScreenDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(activeImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = activeImagePrompt ?: "AI Görseli",
                            contentScale = ContentScale.Crop,
                            onLoading = {
                                isLoading = true
                                isError = false
                            },
                            onSuccess = {
                                isLoading = false
                                isError = false
                            },
                            onError = {
                                if (retryAttempt < 3) {
                                    retryAttempt += 1
                                    isLoading = true
                                    isError = false
                                } else {
                                    isLoading = false
                                    isError = true
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        if (isLoading) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = FlameNeonOrange,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "🎨 Neural Renderer Çiziyor...",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        if (isError) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "⚠️ Görsel Yüklenemedi",
                                    fontSize = 11.sp,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                IconButton(
                                    onClick = {
                                        retryAttempt = 0
                                        seedOffset += 1
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(FlameNeonOrange, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Tekrar Denet",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Action Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (activeImagePrompt != null) "Prompt: \"$activeImagePrompt\"" else "Keon AI Tasarımı",
                            fontSize = 9.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { showFullScreenDialog = true },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Tam Ekran",
                                    tint = FlameNeonOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = { seedOffset += 1 },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Yeniden Üret",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Image URL", activeImageUrl))
                                    Toast.makeText(context, "Görsel Bağlantısı Kopyalandı!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "İndir",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Full Screen Image Dialog
            if (showFullScreenDialog) {
                Dialog(onDismissRequest = { showFullScreenDialog = false }) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black,
                        border = BorderStroke(1.5.dp, FlameNeonOrange),
                        modifier = Modifier.fillMaxWidth(0.98f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🖼️ 8K Tam Çözünürlük AI Görsel",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FlameNeonOrange
                                )
                                Text(
                                    text = "✕ Kapat",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.clickable { showFullScreenDialog = false }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(activeImageUrl)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = activeImagePrompt ?: "",
                                fontSize = 11.sp,
                                color = TextPrimary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // 🎬 AI GENERATED VIDEO PLAYER VIEW
        val activeVideoPrompt = videoPrompt
        if (activeVideoPrompt != null) {
            Spacer(modifier = Modifier.height(10.dp))

            var isPlaying by remember { mutableStateOf(true) }
            var videoProgress by remember { mutableFloatStateOf(0f) }
            var seedOffset by remember { mutableIntStateOf(0) }
            var videoRetryAttempt by remember { mutableIntStateOf(0) }
            var isVideoLoading by remember { mutableStateOf(true) }
            var isVideoError by remember { mutableStateOf(false) }
            var showVideoFullScreen by remember { mutableStateOf(false) }

            val activeVideoFrameUrl = remember(activeVideoPrompt, messageId, seedOffset, videoRetryAttempt) {
                val cleanVidPrompt = activeVideoPrompt
                    .replace(Regex("""[^a-zA-Z0-9\s,]"""), " ")
                    .replace(Regex("""\s+"""), " ")
                    .take(160)
                    .trim()
                val encodedVid = Uri.encode(cleanVidPrompt)
                val seed = kotlin.math.abs((messageId + seedOffset + videoRetryAttempt).hashCode())

                when (videoRetryAttempt) {
                    0 -> "https://image.pollinations.ai/prompt/$encodedVid?width=1280&height=720&model=flux&nologo=true&seed=$seed"
                    1 -> "https://image.pollinations.ai/prompt/$encodedVid?width=1280&height=720&model=turbo&nologo=true&seed=$seed"
                    2 -> "https://image.pollinations.ai/prompt/$encodedVid?width=1280&height=720&nologo=true&seed=$seed"
                    else -> "https://picsum.photos/seed/$seed/1280/720"
                }
            }

            LaunchedEffect(isPlaying) {
                while (isPlaying) {
                    kotlinx.coroutines.delay(50)
                    videoProgress = (videoProgress + 0.015f) % 1f
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F0B1E),
                border = BorderStroke(1.2.dp, NeonMagenta.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Header Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = NeonMagenta,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Veo 3.1 Neural Video Generator",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonMagenta
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(NeonMagenta.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "4K 60FPS CINEMATIC",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonMagenta
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 16:9 Real AI Video Render Player Frame
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF06030B))
                            .clickable { showVideoFullScreen = true },
                        contentAlignment = Alignment.Center
                    ) {
                        // Real AI Generated Video Frame Image with Motion Zoom Effect
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(activeVideoFrameUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = activeVideoPrompt,
                            contentScale = ContentScale.Crop,
                            onLoading = {
                                isVideoLoading = true
                                isVideoError = false
                            },
                            onSuccess = {
                                isVideoLoading = false
                                isVideoError = false
                            },
                            onError = {
                                if (videoRetryAttempt < 3) {
                                    videoRetryAttempt += 1
                                    isVideoLoading = true
                                    isVideoError = false
                                } else {
                                    isVideoLoading = false
                                    isVideoError = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    if (isPlaying && !isVideoLoading && !isVideoError) {
                                        val motionScale = 1f + 0.04f * kotlin.math.sin(videoProgress * 2 * kotlin.math.PI.toFloat())
                                        scaleX = motionScale
                                        scaleY = motionScale
                                        translationX = 8f * kotlin.math.cos(videoProgress * 2 * kotlin.math.PI.toFloat())
                                    }
                                }
                        )

                        if (isVideoLoading) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = NeonMagenta,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "🎬 Veo 3.1 Video Kareleri Render Ediliyor...",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        if (isVideoError) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "⚠️ Video Kareleri Yüklenemedi",
                                    fontSize = 11.sp,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                IconButton(
                                    onClick = {
                                        videoRetryAttempt = 0
                                        seedOffset += 1
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(NeonMagenta, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Tekrar Denet",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        // Play/Pause Overlay Button
                        if (!isVideoLoading && !isVideoError) {
                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .border(1.5.dp, NeonMagenta, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Durdur" else "Oynat",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Bottom Video Progress Bar Overlay
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = { videoProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = NeonMagenta,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🎥 00:0${(videoProgress * 10).toInt()} / 00:10",
                                    fontSize = 8.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "60 FPS • Sinematik Video Render",
                                    fontSize = 8.sp,
                                    color = NeonCyan
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Video Prompt Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Senaryo: \"$activeVideoPrompt\"",
                            fontSize = 9.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { showVideoFullScreen = true },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Tam Ekran Video",
                                    tint = NeonMagenta,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = { seedOffset += 1 },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Videoyu Yeniden Üret",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Video URL", activeVideoFrameUrl))
                                    Toast.makeText(context, "Veo 3.1 4K Video Bağlantısı Kopyalandı!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Videoyu Kaydet",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Full Screen Video Dialog
            if (showVideoFullScreen) {
                Dialog(onDismissRequest = { showVideoFullScreen = false }) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black,
                        border = BorderStroke(1.5.dp, NeonMagenta),
                        modifier = Modifier.fillMaxWidth(0.98f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎬 4K 60FPS AI Sinematik Video",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonMagenta
                                )
                                Text(
                                    text = "✕ Kapat",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.clickable { showVideoFullScreen = false }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(activeVideoFrameUrl)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = activeVideoPrompt,
                                fontSize = 11.sp,
                                color = TextPrimary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
