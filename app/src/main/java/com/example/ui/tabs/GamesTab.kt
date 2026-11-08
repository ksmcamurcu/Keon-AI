package com.example.ui.tabs

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.components.KeonChatbotComponent
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

enum class SelectedGame {
    NONE,
    MINECRAFT_VOXEL,
    SNAKE,
    PUZZLE2048,
    TIC_TAC_TOE,
    MEMORY_MATCH,
    BRICK_BREAKER,
    FLAPPY_KEON,
    SPACE_SHOOTER,
    SPEED_MATH,
    COLOR_REFLEX,
    PONG_AIR_HOCKEY,
    DINO_RUNNER,
    PACMAN_RETRO
}

data class WebPlayableGame(
    val id: String,
    val title: String,
    val emoji: String,
    val category: String,
    val description: String,
    val webUrl: String,
    val accentColor: Color
)

data class PlayStoreGameItem(
    val id: String,
    val packageName: String,
    val title: String,
    val developer: String,
    val emoji: String,
    val category: String,
    val rating: String,
    val downloads: String,
    val description: String,
    val accentColor: Color
)

val samplePlayStoreGames = listOf(
    PlayStoreGameItem("minecraft", "com.mojang.minecraftpe", "Minecraft", "Mojang", "🟩", "Macera", "4.6", "50M+", "Sonsuz blok dünyaları keşfet ve hayalini inşa et.", Color(0xFF4CAF50)),
    PlayStoreGameItem("pubg", "com.tencent.ig", "PUBG MOBILE", "Tencent Games", "⚔️", "Aksiyon", "4.3", "500M+", "Devasa 100 kişilik Battle Royale arenasının lider oyunu.", NeonCyan),
    PlayStoreGameItem("subway", "com.kiloo.subwaysurf", "Subway Surfers", "SYBO Games", "🏃", "Casual", "4.5", "1B+", "Tren hatlarında bekçiden kaç, altınları topla!", NeonGreen),
    PlayStoreGameItem("brawl", "com.supercell.brawlstars", "Brawl Stars", "Supercell", "💥", "Aksiyon", "4.5", "100M+", "Hızlı 3v3 çok oyunculu tempolu savaşlar.", NeonMagenta),
    PlayStoreGameItem("roblox", "com.roblox.client", "Roblox", "Roblox Corporation", "🌐", "RPG", "4.4", "500M+", "Milyonlarca oyuncunun oluşturduğu sanal evrenler.", Color(0xFFFF9800)),
    PlayStoreGameItem("candy", "com.king.candycrushsaga", "Candy Crush Saga", "King", "🍬", "Bulmaca", "4.6", "1B+", "Lezzetli şekerleri eşleştir, 10.000+ seviyeyi geç.", Color(0xFFFF4081)),
    PlayStoreGameItem("clash_royale", "com.supercell.clashroyale", "Clash Royale", "Supercell", "👑", "Strateji", "4.3", "500M+", "Gerçek zamanlı kart savaşları ve kule yıkma.", NeonBlue),
    PlayStoreGameItem("clash_clans", "com.supercell.clashofclans", "Clash of Clans", "Supercell", "🏰", "Strateji", "4.5", "500M+", "Köyünü kur, ordunu eğit ve klan savaşlarına katıl.", Color(0xFF81C784)),
    PlayStoreGameItem("fifa", "com.ea.gp.fifamobile", "EA SPORTS FC™ Mobile", "ELECTRONIC ARTS", "⚽", "Spor", "4.2", "100M+", "Lisanslı takımlar, süper yıldızlar ve gerçek futbol.", NeonGreen),
    PlayStoreGameItem("efootball", "jp.konami.pesam", "eFootball™ 2024", "KONAMI", "⚽", "Spor", "4.2", "100M+", "Konsol kalitesinde gerçekçi PES futbol simülasyonu.", NeonCyan),
    PlayStoreGameItem("asphalt", "com.gameloft.android.ANMP.GloftA9HM", "Asphalt 9: Legends", "Gameloft", "🏎️", "Yarış", "4.4", "100M+", "Ferrari, Porsche arabalarıyla çılgın drift yarışları.", Color(0xFFFF5252)),
    PlayStoreGameItem("genshin", "com.miHoYo.GenshinImpact", "Genshin Impact", "COGNOSPHERE", "🗡️", "RPG", "4.3", "500M+", "Devasa açık dünya fantezi RPG fırtınası.", NeonMagenta),
    PlayStoreGameItem("mobile_legends", "com.mobile.legends", "Mobile Legends: Bang Bang", "Moonton", "⚔️", "MOBA", "4.2", "500M+", "5v5 epik klasik MOBA arena mücadelesi.", NeonBlue),
    PlayStoreGameItem("free_fire", "com.dts.freefireth", "Free Fire", "Garena", "🔫", "Aksiyon", "4.2", "1B+", "10 dakikalık tempolu hayatta kalma savaşı.", Color(0xFFFF9800)),
    PlayStoreGameItem("stumble", "com.kitkagames.fallbuddies", "Stumble Guys", "Scopely", "🤸", "Party", "4.3", "100M+", "32 kişilik eğlenceli nakavt parkur yarışları.", NeonCyan),
    PlayStoreGameItem("cod", "com.activision.callofduty.shooter", "Call of Duty®: Mobile", "Activision", "🎖️", "FPS", "4.4", "100M+", "Efsanevi COD haritaları ve multiplayer modlar.", Color(0xFFFF5722)),
    PlayStoreGameItem("hayday", "com.supercell.hayday", "Hay Day", "Supercell", "🌾", "Simülasyon", "4.4", "100M+", "En sevimli çiftlik ve ticaret oyunu.", Color(0xFF8BC34A)),
    PlayStoreGameItem("amongus", "com.innersloth.spacemafia", "Among Us", "Innersloth", "🛸", "Parti", "4.1", "500M+", "Uzay gemisinde hain kim? Dedektiflik yap!", NeonCyan),
    PlayStoreGameItem("headball2", "com.masomo.headball2", "Kafa Topu 2 (Head Ball 2)", "Masomo", "⚽", "Spor", "4.2", "100M+", "Birebir tempolu online kafa futbolu heyecanı.", NeonGreen),
    PlayStoreGameItem("okey101", "com.peakgames.mobile.okey.plus", "101 Okey Plus", "Zynga / Peak", "🀄", "Masa", "4.3", "50M+", "Türkiye'nin en popüler canlı 101 Okey oyunu.", Color(0xFFFFC107)),
    PlayStoreGameItem("monopoly", "com.scopely.monopolygo", "Monopoly GO!", "Scopely", "🎩", "Masa", "4.5", "100M+", "Zar at, şehirler satın al ve imparatorluğunu kur.", Color(0xFFE91E63)),
    PlayStoreGameItem("royalmatch", "com.marmalade.royalmatch", "Royal Match", "Dream Games", "👑", "Bulmaca", "4.6", "100M+", "Kralın sarayını yenilemek için bulmacaları çöz.", Color(0xFF3F51B5))
)

@Composable
fun GamesTab(
    onOpenVoiceAssistant: () -> Unit = {}
) {
    var activeGame by remember { mutableStateOf(SelectedGame.NONE) }
    var isFullScreenMode by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (activeGame == SelectedGame.NONE) {
            // Games Hub / Menu View
            GamesHubMenu(
                onSelectGame = { game ->
                    activeGame = game
                    isFullScreenMode = true
                }
            )
        } else {
            // Full Screen Native Game Experience
            FullScreenGameContainer(
                activeGame = activeGame,
                isFullScreen = isFullScreenMode,
                onToggleFullScreen = { isFullScreenMode = !isFullScreenMode },
                onBack = { activeGame = SelectedGame.NONE },
                onOpenVoiceAssistant = onOpenVoiceAssistant
            )
        }
    }
}

@Composable
fun FullScreenGameContainer(
    activeGame: SelectedGame,
    isFullScreen: Boolean,
    onToggleFullScreen: () -> Unit,
    onBack: () -> Unit,
    onOpenVoiceAssistant: () -> Unit
) {
    val gameTitle = when (activeGame) {
        SelectedGame.MINECRAFT_VOXEL -> "⛏️ Keon Craft: Voxel 2D"
        SelectedGame.SNAKE -> "🐍 Klasik Yılan Oyunu"
        SelectedGame.PUZZLE2048 -> "🧩 2048 Bulmaca"
        SelectedGame.TIC_TAC_TOE -> "❌⭕ Keon AI X-O-X"
        SelectedGame.MEMORY_MATCH -> "🃏 Hafıza Kartları"
        SelectedGame.BRICK_BREAKER -> "🚀 Tuğla Kırma"
        SelectedGame.FLAPPY_KEON -> "🐦 Flappy Keon Cube"
        SelectedGame.SPACE_SHOOTER -> "👾 Space Galaga"
        SelectedGame.SPEED_MATH -> "⚡ Hızlı Matematik"
        SelectedGame.COLOR_REFLEX -> "🎨 Renk Refleksi"
        SelectedGame.PONG_AIR_HOCKEY -> "🏓 Retro Pong"
        SelectedGame.DINO_RUNNER -> "🦖 T-Rex Dino Runner"
        SelectedGame.PACMAN_RETRO -> "🟡 Retro Pac-Maze"
        else -> "Oyun"
    }

    val gameAccent = when (activeGame) {
        SelectedGame.MINECRAFT_VOXEL -> Color(0xFF4CAF50)
        SelectedGame.SNAKE -> NeonGreen
        SelectedGame.PUZZLE2048 -> NeonCyan
        SelectedGame.TIC_TAC_TOE -> NeonMagenta
        SelectedGame.MEMORY_MATCH -> Color(0xFFFFCC00)
        SelectedGame.BRICK_BREAKER -> Color(0xFFFF5252)
        SelectedGame.FLAPPY_KEON -> NeonCyan
        SelectedGame.SPACE_SHOOTER -> NeonMagenta
        SelectedGame.SPEED_MATH -> Color(0xFFFFCC00)
        SelectedGame.COLOR_REFLEX -> NeonGreen
        SelectedGame.PONG_AIR_HOCKEY -> NeonBlue
        SelectedGame.DINO_RUNNER -> Color(0xFFFF9800)
        SelectedGame.PACMAN_RETRO -> Color(0xFFFFD700)
        else -> NeonCyan
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        // Minimal Floating Full-Screen Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .background(DarkSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, gameAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Oyun Menüsüne Dön",
                    tint = gameAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Title & FullScreen Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = gameTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Box(
                    modifier = Modifier
                        .background(gameAccent.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .border(0.8.dp, gameAccent.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🖥️ TAM EKRAN",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = gameAccent
                    )
                }
            }

            // Actions (Voice Assistant & Fullscreen Toggle)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onToggleFullScreen,
                    modifier = Modifier
                        .size(38.dp)
                        .background(DarkSurface, RoundedCornerShape(12.dp))
                        .border(1.dp, gameAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Tam Ekran Modu",
                        tint = gameAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onOpenVoiceAssistant,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF23083B), RoundedCornerShape(12.dp))
                        .border(1.2.dp, NeonMagenta, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Sesli Asistan",
                        tint = NeonMagenta,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Full Screen Game View
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeGame) {
                SelectedGame.MINECRAFT_VOXEL -> MinecraftVoxelGameView()
                SelectedGame.SNAKE -> SnakeGameView()
                SelectedGame.PUZZLE2048 -> Puzzle2048GameView()
                SelectedGame.TIC_TAC_TOE -> TicTacToeGameView()
                SelectedGame.MEMORY_MATCH -> MemoryMatchGameView()
                SelectedGame.BRICK_BREAKER -> BrickBreakerGameView()
                SelectedGame.FLAPPY_KEON -> FlappyKeonGameView()
                SelectedGame.SPACE_SHOOTER -> SpaceShooterGameView()
                SelectedGame.SPEED_MATH -> SpeedMathGameView()
                SelectedGame.COLOR_REFLEX -> ColorReflexGameView()
                SelectedGame.PONG_AIR_HOCKEY -> PongAirHockeyGameView()
                SelectedGame.DINO_RUNNER -> DinoRunnerGameView()
                SelectedGame.PACMAN_RETRO -> PacmanRetroGameView()
                else -> {}
            }
            // Chatbot Overlay
            Box(modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                KeonChatbotComponent(modifier = Modifier.width(300.dp))
            }
        }
    }
}

@Composable
fun GamesHubMenu(
    onSelectGame: (SelectedGame) -> Unit
) {
    val context = LocalContext.current
    var selectedCategoryTab by remember { mutableIntStateOf(0) } // 0: Yerel Tam Ekran Oyunlar, 1: Google Play Kataloğu
    var searchQuery by remember { mutableStateOf("") }
    var playStoreCategoryFilter by remember { mutableStateOf("Hepsi") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(20.dp))
                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Gamepad,
                        contentDescription = "Oyunlar",
                        tint = NeonGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keon AI Yerel Oyun Merkezi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Web olmadan doğrudan uygulama içine yazılmış %100 yerel ve tam ekran açılan oyunlar!",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgeChip("🖥️ Tam Ekran Modu", NeonGreen)
                    BadgeChip("⚡ %100 Yerel Kod", NeonCyan)
                    BadgeChip("📶 İnternetsiz Oyna", NeonMagenta)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Tab Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selectedCategoryTab == 0) NeonGreen.copy(alpha = 0.2f) else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        if (selectedCategoryTab == 0) 1.dp else 0.dp,
                        if (selectedCategoryTab == 0) NeonGreen else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedCategoryTab = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎮 Yerel Oyunlar (${SelectedGame.values().size - 1})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedCategoryTab == 0) NeonGreen else TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selectedCategoryTab == 1) NeonCyan.copy(alpha = 0.2f) else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        if (selectedCategoryTab == 1) 1.dp else 0.dp,
                        if (selectedCategoryTab == 1) NeonCyan else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedCategoryTab = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🛍️ Google Play Kataloğu",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedCategoryTab == 1) NeonCyan else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedCategoryTab == 0) {
            // TAB 0: 100% NATIVE FULL SCREEN GAMES
            Text(
                text = "⛏️ VOXEL SANDBOX & İNŞA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Minecraft Voxel Featured Card
            GameCardItem(
                emoji = "⛏️",
                title = "Keon Craft: Voxel Minecraft 2D",
                description = "Tam ekran Voxel Sandbox dünyası! Çimen, taş, kütük kır; kazma & kılıç üret; TNT patlat ve elmas madenciliği yap!",
                badge = "⚡ Tam Ekran Voxel Sandbox",
                accentColor = Color(0xFF4CAF50),
                onClick = { onSelectGame(SelectedGame.MINECRAFT_VOXEL) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameCardItem(
                emoji = "🐺",
                title = "Kurt Simülatörü 3D (Demo)",
                description = "Sonsuz ormanda bir kurt olarak hayatta kal!",
                badge = "3D Simülasyon",
                accentColor = Color(0xFF607D8B),
                onClick = { /* TODO: Launch Wolf Game */ }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "🕹️ ARCADE, RETRO & AKSİYON",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            GameCardItem(
                emoji = "🦖",
                title = "Chrome T-Rex Dino Runner",
                description = "Uygulama içi yerel sonsuz dinozor koşusu! Engellerin üstünden zıpla, gece/gündüz döngüsünde rekor kır.",
                badge = "Retro Runner",
                accentColor = Color(0xFFFF9800),
                onClick = { onSelectGame(SelectedGame.DINO_RUNNER) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameCardItem(
                emoji = "🟡",
                title = "Retro Pac-Maze Runner",
                description = "Labirentte Keon Pacman'i yönet, sarı neon yemleri topla ve hayaletlerden kaç.",
                badge = "Nostalji Arcade",
                accentColor = Color(0xFFFFD700),
                onClick = { onSelectGame(SelectedGame.PACMAN_RETRO) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameCardItem(
                emoji = "🐍",
                title = "Klasik Yılan Oyunu (Snake)",
                description = "Elmaları topla, yılanı büyüt ve kendi rekorunu kır. Akıcı dokunmatik ve yön butonları kontrolü.",
                badge = "Aksiyon & Klasik",
                accentColor = NeonGreen,
                onClick = { onSelectGame(SelectedGame.SNAKE) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameCardItem(
                emoji = "🐦",
                title = "Flappy Keon Cube",
                description = "Ekrana dokunarak Keon küpünü uçur, neon engellerin arasından geç ve rekor kır!",
                badge = "Refleks & Aksiyon",
                accentColor = NeonCyan,
                onClick = { onSelectGame(SelectedGame.FLAPPY_KEON) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameCardItem(
                emoji = "👾",
                title = "Space Galaga Shooter",
                description = "Siberpunk uzay gemini kontrol et, gelen uzaylı filolarını lazer atışlarıyla yok et!",
                badge = "Arcade Shooter",
                accentColor = NeonMagenta,
                onClick = { onSelectGame(SelectedGame.SPACE_SHOOTER) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameCardItem(
                emoji = "🚀",
                title = "Tuğla Kırma (Brick Breaker)",
                description = "Topu raketle sektirerek renkli tuğlaları kır. Hızını ayarla ve tüm blokları temizle.",
                badge = "Refleks & Arcade",
                accentColor = Color(0xFFFF5252),
                onClick = { onSelectGame(SelectedGame.BRICK_BREAKER) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameCardItem(
                emoji = "🏓",
                title = "Retro Pong & Hava Hokeyi",
                description = "Keon AI raketine karşı masa tenisi / hava hokeyi maçı yap, 5 sayı alan kazanır!",
                badge = "Spor & 1v1 AI",
                accentColor = NeonBlue,
                onClick = { onSelectGame(SelectedGame.PONG_AIR_HOCKEY) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "🧠 ZEKA, STRATEJİ & BULMACA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonMagenta,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            GameCardItem(
                emoji = "🧩",
                title = "2048 Sayı Bulmacası",
                description = "Aynı sayıları kaydırarak birleştir, 2048 karosuna ulaş ve zekanı test et.",
                badge = "Zeka & Strateji",
                accentColor = NeonCyan,
                onClick = { onSelectGame(SelectedGame.PUZZLE2048) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameCardItem(
                emoji = "❌⭕",
                title = "Keon AI X-O-X (Tic-Tac-Toe)",
                description = "Akıllı Keon AI motoruna karşı oyna veya arkadaşınla yan yana 2 kişilik oyna.",
                badge = "Yapay Zeka & 2 Kişilik",
                accentColor = NeonMagenta,
                onClick = { onSelectGame(SelectedGame.TIC_TAC_TOE) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameCardItem(
                emoji = "🃏",
                title = "Hafıza Kartları Oyunu",
                description = "Gizli emoji kartlarını eşleştir, görsel hafızanı güçlendir ve hamle sayını düşür.",
                badge = "Hafıza & Eğlence",
                accentColor = Color(0xFFFFCC00),
                onClick = { onSelectGame(SelectedGame.MEMORY_MATCH) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameCardItem(
                emoji = "⚡",
                title = "Zeka Fırtınası Hızlı Matematik",
                description = "Zamana karşı yarış! 5 saniyede hızlı zihinsel matematik sorularını çöz ve kombo yap.",
                badge = "Zeka & Hafıza",
                accentColor = Color(0xFFFFCC00),
                onClick = { onSelectGame(SelectedGame.SPEED_MATH) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameCardItem(
                emoji = "🎨",
                title = "Renk Refleksi & Hafıza (Simon Says)",
                description = "Rengarenk neon butonların yanış sırasını aklında tut, sırayla basarak hafızanı zorla.",
                badge = "Hafıza & Algı",
                accentColor = NeonGreen,
                onClick = { onSelectGame(SelectedGame.COLOR_REFLEX) }
            )

        } else {
            // TAB 1: GOOGLE PLAY STORE GAME CATALOG
            Column(modifier = Modifier.fillMaxWidth()) {
                // Direct Play Store Launcher Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F2027), RoundedCornerShape(16.dp))
                        .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = NeonGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google Play Store Oyun Portalı", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "Google Play Store'daki tüm popüler oyunları doğrudan cihazınızda açın, indirin ve oynayın.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { launchPlayStoreSearch(context, "games") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(" Google Play Oyunlar Mağazasını Aç", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Google Play Store'da oyun ara...", fontSize = 12.sp, color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Temizle", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (searchQuery.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { launchPlayStoreSearch(context, searchQuery) },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔍 Google Play Store'da '$searchQuery' Ara", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Chips Filter
                val categories = listOf("Hepsi", "Aksiyon", "Spor", "Yarış", "Strateji", "Bulmaca", "Masa", "RPG", "Casual")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        Box(
                            modifier = Modifier
                                .background(
                                    if (playStoreCategoryFilter == cat) NeonGreen.copy(alpha = 0.25f) else DarkSurface,
                                    RoundedCornerShape(20.dp)
                                )
                                .border(
                                    1.dp,
                                    if (playStoreCategoryFilter == cat) NeonGreen else TextSecondary.copy(alpha = 0.2f),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { playStoreCategoryFilter = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                color = if (playStoreCategoryFilter == cat) NeonGreen else TextSecondary,
                                fontWeight = if (playStoreCategoryFilter == cat) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Filtered Play Store Games
                val filteredPlayStoreList = remember(searchQuery, playStoreCategoryFilter) {
                    samplePlayStoreGames.filter { game ->
                        val matchesSearch = searchQuery.isBlank() ||
                                game.title.contains(searchQuery, ignoreCase = true) ||
                                game.developer.contains(searchQuery, ignoreCase = true) ||
                                game.category.contains(searchQuery, ignoreCase = true)
                        val matchesCategory = playStoreCategoryFilter == "Hepsi" ||
                                game.category.equals(playStoreCategoryFilter, ignoreCase = true)
                        matchesSearch && matchesCategory
                    }
                }

                Text(
                    text = "🏆 GOOGLE PLAY EN POPÜLER OYUNLAR (${filteredPlayStoreList.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                filteredPlayStoreList.forEach { game ->
                    PlayStoreGameCard(
                        game = game,
                        onOpenPlayStore = { launchPlayStorePackage(context, game.packageName) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun PlayStoreGameCard(
    game: PlayStoreGameItem,
    onOpenPlayStore: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenPlayStore() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, game.accentColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Emoji / Icon Box
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(DarkBackground, RoundedCornerShape(12.dp))
                            .border(1.dp, game.accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = game.emoji, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = game.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = game.developer,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(game.rating, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.width(10.dp))

                            Icon(Icons.Default.Download, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(game.downloads, fontSize = 11.sp, color = NeonCyan)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(game.accentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(0.8.dp, game.accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(game.category, fontSize = 10.sp, color = game.accentColor, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = game.description,
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onOpenPlayStore,
                    colors = ButtonDefaults.buttonColors(containerColor = game.accentColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📲 Google Play Store'da Aç / İndir", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

private fun launchPlayStorePackage(context: Context, packageName: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(webIntent)
    }
}

private fun launchPlayStoreSearch(context: Context, query: String) {
    val targetQuery = if (query.isBlank()) "games" else query
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=$targetQuery&c=apps")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=$targetQuery&c=apps")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(webIntent)
    }
}

@Composable
fun BadgeChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(DarkBackground, RoundedCornerShape(12.dp))
            .border(0.8.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun GameCardItem(
    emoji: String,
    title: String,
    description: String,
    badge: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(DarkBackground, RoundedCornerShape(14.dp))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = badge, fontSize = 9.sp, color = accentColor, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

// --------------------------------------------------------
// 1. SNAKE GAME IMPLEMENTATION
// --------------------------------------------------------
enum class Direction { UP, DOWN, LEFT, RIGHT }

@Composable
fun SnakeGameView() {
    val gridSize = 16
    var snake = remember { mutableStateListOf(Pair(5, 5), Pair(5, 6), Pair(5, 7)) }
    var food by remember { mutableStateOf(Pair(10, 10)) }
    var direction by remember { mutableStateOf(Direction.UP) }
    var score by remember { mutableIntStateOf(0) }
    var highScore by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    fun resetGame() {
        snake.clear()
        snake.addAll(listOf(Pair(8, 8), Pair(8, 9), Pair(8, 10)))
        direction = Direction.UP
        score = 0
        isGameOver = false
        isPaused = false
        food = Pair(Random.nextInt(gridSize), Random.nextInt(gridSize))
    }

    LaunchedEffect(isGameOver, isPaused) {
        while (!isGameOver && !isPaused) {
            delay(150)
            val head = snake.first()
            val newHead = when (direction) {
                Direction.UP -> Pair(head.first, (head.second - 1 + gridSize) % gridSize)
                Direction.DOWN -> Pair(head.first, (head.second + 1) % gridSize)
                Direction.LEFT -> Pair((head.first - 1 + gridSize) % gridSize, head.second)
                Direction.RIGHT -> Pair((head.first + 1) % gridSize, head.second)
            }

            // Check Collision with self
            if (snake.contains(newHead)) {
                isGameOver = true
                if (score > highScore) highScore = score
            } else {
                snake.add(0, newHead)
                if (newHead == food) {
                    score += 10
                    food = Pair(Random.nextInt(gridSize), Random.nextInt(gridSize))
                } else {
                    snake.removeAt(snake.size - 1)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Score Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Puan: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            Text("En Yüksek: $highScore", fontSize = 14.sp, color = TextSecondary)
            IconButton(onClick = { resetGame() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Yeniden Başlat", tint = NeonCyan)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Snake Canvas Grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(DarkBackground, RoundedCornerShape(16.dp))
                .border(2.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellSize = size.width / gridSize

                // Draw Food
                drawCircle(
                    color = Color.Red,
                    radius = cellSize / 2.2f,
                    center = Offset((food.first + 0.5f) * cellSize, (food.second + 0.5f) * cellSize)
                )

                // Draw Snake
                snake.forEachIndexed { index, seg ->
                    val color = if (index == 0) NeonGreen else NeonGreen.copy(alpha = 0.7f)
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(seg.first * cellSize + 1, seg.second * cellSize + 1),
                        size = Size(cellSize - 2, cellSize - 2),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
            }

            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("OYUN BİTTİ!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        Text("Toplam Puan: $score", fontSize = 16.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { resetGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                        ) {
                            Text("Tekrar Oyna", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // D-Pad Controls
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = { if (direction != Direction.DOWN) direction = Direction.UP },
                modifier = Modifier
                    .size(48.dp)
                    .background(DarkSurface, CircleShape)
                    .border(1.dp, NeonGreen, CircleShape)
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Yukarı", tint = NeonGreen)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(36.dp)) {
                IconButton(
                    onClick = { if (direction != Direction.RIGHT) direction = Direction.LEFT },
                    modifier = Modifier
                        .size(48.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.dp, NeonGreen, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Sol", tint = NeonGreen)
                }

                IconButton(
                    onClick = { if (direction != Direction.LEFT) direction = Direction.RIGHT },
                    modifier = Modifier
                        .size(48.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.dp, NeonGreen, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Sağ", tint = NeonGreen)
                }
            }

            IconButton(
                onClick = { if (direction != Direction.UP) direction = Direction.DOWN },
                modifier = Modifier
                    .size(48.dp)
                    .background(DarkSurface, CircleShape)
                    .border(1.dp, NeonGreen, CircleShape)
            ) {
                Icon(Icons.Default.ArrowDownward, contentDescription = "Aşağı", tint = NeonGreen)
            }
        }
    }
}

// --------------------------------------------------------
// 2. 2048 PUZZLE GAME IMPLEMENTATION
// --------------------------------------------------------
@Composable
fun Puzzle2048GameView() {
    var grid by remember { mutableStateOf(Array(4) { IntArray(4) }) }
    var score by remember { mutableIntStateOf(0) }
    var bestScore by remember { mutableIntStateOf(0) }

    fun spawnTile() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0..3) {
            for (c in 0..3) {
                if (grid[r][c] == 0) emptyCells.add(Pair(r, c))
            }
        }
        if (emptyCells.isNotEmpty()) {
            val (r, c) = emptyCells.random()
            grid[r][c] = if (Random.nextFloat() < 0.9f) 2 else 4
        }
    }

    fun initGame() {
        grid = Array(4) { IntArray(4) }
        score = 0
        spawnTile()
        spawnTile()
    }

    LaunchedEffect(Unit) {
        if (grid.all { row -> row.all { it == 0 } }) {
            initGame()
        }
    }

    fun slideAndCombine(row: IntArray): Pair<IntArray, Int> {
        val nonZero = row.filter { it != 0 }.toMutableList()
        var addedScore = 0
        val result = IntArray(4)
        var i = 0
        var targetIndex = 0

        while (i < nonZero.size) {
            if (i + 1 < nonZero.size && nonZero[i] == nonZero[i + 1]) {
                val merged = nonZero[i] * 2
                result[targetIndex] = merged
                addedScore += merged
                i += 2
            } else {
                result[targetIndex] = nonZero[i]
                i += 1
            }
            targetIndex++
        }
        return Pair(result, addedScore)
    }

    fun moveLeft() {
        val newGrid = Array(4) { IntArray(4) }
        var changed = false
        var turnScore = 0

        for (r in 0..3) {
            val (newRow, added) = slideAndCombine(grid[r])
            newGrid[r] = newRow
            turnScore += added
            if (!newRow.contentEquals(grid[r])) changed = true
        }

        if (changed) {
            grid = newGrid
            score += turnScore
            if (score > bestScore) bestScore = score
            spawnTile()
        }
    }

    fun moveRight() {
        val newGrid = Array(4) { IntArray(4) }
        var changed = false
        var turnScore = 0

        for (r in 0..3) {
            val reversed = grid[r].reversedArray()
            val (newRow, added) = slideAndCombine(reversed)
            val finalRow = newRow.reversedArray()
            newGrid[r] = finalRow
            turnScore += added
            if (!finalRow.contentEquals(grid[r])) changed = true
        }

        if (changed) {
            grid = newGrid
            score += turnScore
            if (score > bestScore) bestScore = score
            spawnTile()
        }
    }

    fun moveUp() {
        val newGrid = Array(4) { IntArray(4) }
        var changed = false
        var turnScore = 0

        for (c in 0..3) {
            val column = IntArray(4) { r -> grid[r][c] }
            val (newCol, added) = slideAndCombine(column)
            turnScore += added
            for (r in 0..3) {
                newGrid[r][c] = newCol[r]
                if (grid[r][c] != newCol[r]) changed = true
            }
        }

        if (changed) {
            grid = newGrid
            score += turnScore
            if (score > bestScore) bestScore = score
            spawnTile()
        }
    }

    fun moveDown() {
        val newGrid = Array(4) { IntArray(4) }
        var changed = false
        var turnScore = 0

        for (c in 0..3) {
            val column = IntArray(4) { r -> grid[r][c] }.reversedArray()
            val (newCol, added) = slideAndCombine(column)
            val finalCol = newCol.reversedArray()
            turnScore += added
            for (r in 0..3) {
                newGrid[r][c] = finalCol[r]
                if (grid[r][c] != finalCol[r]) changed = true
            }
        }

        if (changed) {
            grid = newGrid
            score += turnScore
            if (score > bestScore) bestScore = score
            spawnTile()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Skor: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            Text("En Yüksek: $bestScore", fontSize = 14.sp, color = TextSecondary)
            IconButton(onClick = { initGame() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Yeniden Başlat", tint = NeonCyan)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4x4 Grid Board
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(DarkSurface, RoundedCornerShape(16.dp))
                .border(2.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (r in 0..3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (c in 0..3) {
                            val value = grid[r][c]
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(
                                        getTileColor(value),
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (value > 0) {
                                    Text(
                                        text = "$value",
                                        fontSize = if (value > 1000) 18.sp else 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (value <= 4) TextPrimary else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Direction Control Buttons for 2048
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = { moveUp() },
                modifier = Modifier
                    .size(46.dp)
                    .background(DarkSurface, CircleShape)
                    .border(1.dp, NeonCyan, CircleShape)
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Yukarı", tint = NeonCyan)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                IconButton(
                    onClick = { moveLeft() },
                    modifier = Modifier
                        .size(46.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.dp, NeonCyan, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Sol", tint = NeonCyan)
                }

                IconButton(
                    onClick = { moveRight() },
                    modifier = Modifier
                        .size(46.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.dp, NeonCyan, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Sağ", tint = NeonCyan)
                }
            }

            IconButton(
                onClick = { moveDown() },
                modifier = Modifier
                    .size(46.dp)
                    .background(DarkSurface, CircleShape)
                    .border(1.dp, NeonCyan, CircleShape)
            ) {
                Icon(Icons.Default.ArrowDownward, contentDescription = "Aşağı", tint = NeonCyan)
            }
        }
    }
}

fun getTileColor(value: Int): Color {
    return when (value) {
        0 -> Color(0xFF1E293B)
        2 -> Color(0xFF334155)
        4 -> Color(0xFF475569)
        8 -> Color(0xFF0284C7)
        16 -> Color(0xFF0D9488)
        32 -> Color(0xFF16A34A)
        64 -> Color(0xFFCA8A04)
        128 -> Color(0xFFEA580C)
        256 -> Color(0xFFE11D48)
        512 -> Color(0xFF9333EA)
        1024 -> Color(0xFF4F46E5)
        2048 -> Color(0xFF00E5FF)
        else -> Color(0xFFFF0055)
    }
}

// --------------------------------------------------------
// 3. TIC TAC TOE (X-O-X) IMPLEMENTATION
// --------------------------------------------------------
@Composable
fun TicTacToeGameView() {
    var board by remember { mutableStateOf(Array(3) { Array(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var isVsAi by remember { mutableStateOf(true) }
    var winner by remember { mutableStateOf<String?>(null) }
    var xWins by remember { mutableIntStateOf(0) }
    var oWins by remember { mutableIntStateOf(0) }

    fun checkWinner(b: Array<Array<String>>): String? {
        for (i in 0..2) {
            if (b[i][0].isNotEmpty() && b[i][0] == b[i][1] && b[i][1] == b[i][2]) return b[i][0]
            if (b[0][i].isNotEmpty() && b[0][i] == b[1][i] && b[1][i] == b[2][i]) return b[0][i]
        }
        if (b[0][0].isNotEmpty() && b[0][0] == b[1][1] && b[1][1] == b[2][2]) return b[0][0]
        if (b[0][2].isNotEmpty() && b[0][2] == b[1][1] && b[1][1] == b[2][0]) return b[0][2]

        var isFull = true
        for (r in 0..2) {
            for (c in 0..2) {
                if (b[r][c].isEmpty()) isFull = false
            }
        }
        return if (isFull) "DRAW" else null
    }

    fun resetBoard() {
        board = Array(3) { Array(3) { "" } }
        currentPlayer = "X"
        winner = null
    }

    fun aiMove() {
        if (winner != null) return
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0..2) {
            for (c in 0..2) {
                if (board[r][c].isEmpty()) emptyCells.add(Pair(r, c))
            }
        }
        if (emptyCells.isNotEmpty()) {
            val (r, c) = emptyCells.random()
            val newBoard = Array(3) { row -> board[row].clone() }
            newBoard[r][c] = "O"
            board = newBoard
            val win = checkWinner(board)
            if (win != null) {
                winner = win
                if (win == "O") oWins++
            } else {
                currentPlayer = "X"
            }
        }
    }

    fun makeMove(r: Int, c: Int) {
        if (board[r][c].isNotEmpty() || winner != null) return

        val newBoard = Array(3) { row -> board[row].clone() }
        newBoard[r][c] = currentPlayer
        board = newBoard

        val win = checkWinner(board)
        if (win != null) {
            winner = win
            if (win == "X") xWins++
            else if (win == "O") oWins++
        } else {
            if (isVsAi && currentPlayer == "X") {
                currentPlayer = "O"
                aiMove()
            } else {
                currentPlayer = if (currentPlayer == "X") "O" else "X"
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mode Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { isVsAi = true; resetBoard() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVsAi) NeonMagenta else Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Keon AI'ya Karşı", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { isVsAi = false; resetBoard() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isVsAi) NeonCyan else Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("2 Kişilik Çevrimdışı", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Score Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("X (Siz): $xWins", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeonMagenta)
            Text("O (${if (isVsAi) "Keon AI" else "2. Oyuncu"}): $oWins", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            IconButton(onClick = { resetBoard() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Yeniden Başlat", tint = TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tic Tac Toe Board
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(DarkSurface, RoundedCornerShape(16.dp))
                .border(2.dp, NeonMagenta.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (r in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (c in 0..2) {
                            val mark = board[r][c]
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .background(DarkBackground, RoundedCornerShape(12.dp))
                                    .border(1.dp, NeonMagenta.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .clickable { makeMove(r, c) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mark,
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (mark == "X") NeonMagenta else NeonCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (winner != null) {
            val winText = when (winner) {
                "X" -> "🎉 X Kazandı!"
                "O" -> if (isVsAi) "🧠 Keon AI Kazandı!" else "🎉 O Kazandı!"
                else -> "🤝 Berabere Bitti!"
            }
            Text(
                text = winText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (winner == "X") NeonMagenta else NeonCyan
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { resetBoard() },
                colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
            ) {
                Text("Yeni Tur Başlat", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --------------------------------------------------------
// 4. MEMORY MATCH GAME IMPLEMENTATION
// --------------------------------------------------------
data class MemoryCard(
    val id: Int,
    val symbol: String,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun MemoryMatchGameView() {
    val symbols = listOf("🚀", "🛸", "🤖", "⭐", "🔮", "💎", "👾", "⚡")
    val cards = remember { mutableStateListOf<MemoryCard>() }
    var selectedIndexes = remember { mutableStateListOf<Int>() }
    var moves by remember { mutableIntStateOf(0) }
    var pairsMatched by remember { mutableIntStateOf(0) }

    fun initMemoryGame() {
        cards.clear()
        selectedIndexes.clear()
        moves = 0
        pairsMatched = 0

        val cardList = (symbols + symbols).shuffled().mapIndexed { index, sym ->
            MemoryCard(id = index, symbol = sym)
        }
        cards.addAll(cardList)
    }

    LaunchedEffect(Unit) {
        if (cards.isEmpty()) initMemoryGame()
    }

    fun onCardClick(index: Int) {
        if (selectedIndexes.size >= 2 || cards[index].isFaceUp || cards[index].isMatched) return

        cards[index] = cards[index].copy(isFaceUp = true)
        selectedIndexes.add(index)

        if (selectedIndexes.size == 2) {
            moves++
            val first = selectedIndexes[0]
            val second = selectedIndexes[1]

            if (cards[first].symbol == cards[second].symbol) {
                cards[first] = cards[first].copy(isMatched = true)
                cards[second] = cards[second].copy(isMatched = true)
                pairsMatched++
                selectedIndexes.clear()
            } else {
                // Flip back after delay
            }
        }
    }

    LaunchedEffect(selectedIndexes.size) {
        if (selectedIndexes.size == 2) {
            val first = selectedIndexes[0]
            val second = selectedIndexes[1]
            if (cards[first].symbol != cards[second].symbol) {
                delay(800)
                cards[first] = cards[first].copy(isFaceUp = false)
                cards[second] = cards[second].copy(isFaceUp = false)
            }
            selectedIndexes.clear()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hamle: $moves", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFCC00))
            Text("Eşleşen: $pairsMatched / 8", fontSize = 14.sp, color = TextSecondary)
            IconButton(onClick = { initMemoryGame() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Yeniden Başlat", tint = Color(0xFFFFCC00))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(cards) { index, card ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(
                            if (card.isFaceUp || card.isMatched) DarkSurfaceVariant else DarkSurface,
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            if (card.isMatched) NeonGreen else Color(0xFFFFCC00).copy(alpha = 0.4f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onCardClick(index) },
                    contentAlignment = Alignment.Center
                ) {
                    if (card.isFaceUp || card.isMatched) {
                        Text(text = card.symbol, fontSize = 28.sp)
                    } else {
                        Text(text = "❓", fontSize = 20.sp, color = TextSecondary)
                    }
                }
            }
        }

        if (pairsMatched == 8) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("🎉 TEBRİKLER! TÜM KARTLARI BULDUNUZ!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { initMemoryGame() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
            ) {
                Text("Yeniden Oyna", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --------------------------------------------------------
// 5. BRICK BREAKER GAME IMPLEMENTATION
// --------------------------------------------------------
@Composable
fun BrickBreakerGameView() {
    var paddleX by remember { mutableStateOf(150f) }
    var ballX by remember { mutableStateOf(150f) }
    var ballY by remember { mutableStateOf(300f) }
    var ballVx by remember { mutableStateOf(6f) }
    var ballVy by remember { mutableStateOf(-8f) }
    var score by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    val bricks = remember { mutableStateListOf<Pair<Float, Float>>() }

    fun initBricks() {
        bricks.clear()
        for (r in 0..3) {
            for (c in 0..5) {
                bricks.add(Pair(c * 55f + 10f, r * 25f + 20f))
            }
        }
        ballX = 150f
        ballY = 300f
        ballVx = 6f
        ballVy = -8f
        score = 0
        isGameOver = false
    }

    LaunchedEffect(Unit) {
        initBricks()
    }

    LaunchedEffect(isGameOver) {
        while (!isGameOver) {
            delay(16)
            ballX += ballVx
            ballY += ballVy

            // Left/Right Wall collision
            if (ballX <= 10f || ballX >= 320f) ballVx = -ballVx

            // Top Wall collision
            if (ballY <= 10f) ballVy = -ballVy

            // Paddle collision
            if (ballY >= 380f && ballY <= 395f && ballX >= paddleX - 10f && ballX <= paddleX + 70f) {
                ballVy = -kotlin.math.abs(ballVy)
            }

            // Bottom Wall collision (Game Over)
            if (ballY >= 440f) {
                isGameOver = true
            }

            // Brick collision
            val hitBrick = bricks.find { b ->
                ballX >= b.first && ballX <= b.first + 50f && ballY >= b.second && ballY <= b.second + 20f
            }
            if (hitBrick != null) {
                bricks.remove(hitBrick)
                ballVy = -ballVy
                score += 20
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Skor: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
            Text("Tuğlalar: ${bricks.size}", fontSize = 14.sp, color = TextSecondary)
            IconButton(onClick = { initBricks() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Yeniden Başlat", tint = Color(0xFFFF5252))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Canvas Game Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .background(DarkBackground, RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFFF5252).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        paddleX = (paddleX + dragAmount.x).coerceIn(10f, 270f)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw Bricks
                bricks.forEach { b ->
                    drawRoundRect(
                        color = Color(0xFFFF5252),
                        topLeft = Offset(b.first, b.second),
                        size = Size(48f, 18f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }

                // Draw Paddle
                drawRoundRect(
                    color = NeonCyan,
                    topLeft = Offset(paddleX, 390f),
                    size = Size(65f, 14f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                )

                // Draw Ball
                drawCircle(
                    color = Color.Yellow,
                    radius = 8f,
                    center = Offset(ballX, ballY)
                )
            }

            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("OYUN BİTTİ", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        Text("Skorunuz: $score", fontSize = 16.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { initBricks() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                        ) {
                            Text("Tekrar Başlat", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("👉 Raketi hareket ettirmek için ekranı sağa-sola kaydırın", fontSize = 11.sp, color = TextSecondary)
    }
}

// --------------------------------------------------------
// 6. FLAPPY KEON GAME IMPLEMENTATION
// --------------------------------------------------------
@Composable
fun FlappyKeonGameView() {
    var birdY by remember { mutableStateOf(200f) }
    var velocity by remember { mutableStateOf(0f) }
    var score by remember { mutableIntStateOf(0) }
    var highScore by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var isStarted by remember { mutableStateOf(false) }

    data class Pipe(var x: Float, val gapY: Float, var passed: Boolean = false)
    val pipes = remember { mutableStateListOf<Pipe>() }

    fun resetGame() {
        birdY = 200f
        velocity = 0f
        score = 0
        isGameOver = false
        isStarted = false
        pipes.clear()
        pipes.add(Pipe(300f, 180f))
        pipes.add(Pipe(500f, 240f))
    }

    LaunchedEffect(Unit) {
        resetGame()
    }

    LaunchedEffect(isStarted, isGameOver) {
        while (isStarted && !isGameOver) {
            delay(20)
            velocity += 0.7f // gravity
            birdY += velocity

            // Move pipes
            for (i in pipes.indices) {
                val p = pipes[i]
                p.x -= 4.5f

                // Check scoring
                if (!p.passed && p.x < 100f) {
                    p.passed = true
                    score += 1
                }
            }

            // Recycle pipes
            if (pipes.isNotEmpty() && pipes.first().x < -60f) {
                pipes.removeAt(0)
                val lastX = pipes.lastOrNull()?.x ?: 300f
                pipes.add(Pipe(lastX + 220f, Random.nextInt(120, 280).toFloat()))
            }

            // Check Collision
            if (birdY < 10f || birdY > 410f) {
                isGameOver = true
                if (score > highScore) highScore = score
            }

            val birdX = 120f
            pipes.forEach { p ->
                if (kotlin.math.abs(p.x - birdX) < 35f) {
                    if (birdY < p.gapY - 50f || birdY > p.gapY + 50f) {
                        isGameOver = true
                        if (score > highScore) highScore = score
                    }
                }
            }
        }
    }

    fun flap() {
        if (!isStarted) isStarted = true
        if (isGameOver) resetGame() else velocity = -11f
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Skor: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            Text("Rekor: $highScore", fontSize = 14.sp, color = TextSecondary)
            IconButton(onClick = { resetGame() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Yeniden Başlat", tint = NeonCyan)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp)
                .background(DarkBackground, RoundedCornerShape(16.dp))
                .border(2.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { flap() },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width

                // Draw Pipes
                pipes.forEach { p ->
                    // Top Pipe
                    drawRoundRect(
                        color = NeonGreen,
                        topLeft = Offset(p.x, 0f),
                        size = Size(50f, p.gapY - 50f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                    // Bottom Pipe
                    drawRoundRect(
                        color = NeonGreen,
                        topLeft = Offset(p.x, p.gapY + 50f),
                        size = Size(50f, 440f - (p.gapY + 50f)),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                }

                // Draw Bird (Keon Cube)
                drawRoundRect(
                    color = NeonMagenta,
                    topLeft = Offset(105f, birdY - 15f),
                    size = Size(30f, 30f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = Offset(125f, birdY - 5f)
                )
            }

            if (!isStarted && !isGameOver) {
                Text("👉 Zıplamak İçin Ekrana Dokun!", fontSize = 15.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
            }

            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.82f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("OYUN BİTTİ", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        Text("Skor: $score | En Yüksek: $highScore", fontSize = 14.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { flap() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("Tekrar Başlat", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------
// 7. SPACE SHOOTER (GALAGA) GAME IMPLEMENTATION
// --------------------------------------------------------
@Composable
fun SpaceShooterGameView() {
    var shipX by remember { mutableStateOf(160f) }
    var score by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    data class Bullet(var x: Float, var y: Float)
    data class Enemy(var x: Float, var y: Float, val speed: Float)

    val bullets = remember { mutableStateListOf<Bullet>() }
    val enemies = remember { mutableStateListOf<Enemy>() }

    fun resetGame() {
        shipX = 160f
        score = 0
        isGameOver = false
        bullets.clear()
        enemies.clear()
    }

    LaunchedEffect(isGameOver) {
        var tick = 0
        while (!isGameOver) {
            delay(20)
            tick++

            // Spawn enemies
            if (tick % 45 == 0) {
                enemies.add(Enemy(Random.nextInt(20, 300).toFloat(), 0f, Random.nextFloat() * 2f + 2f))
            }

            // Move Bullets
            for (i in bullets.indices.reversed()) {
                val b = bullets[i]
                b.y -= 12f
                if (b.y < 0) bullets.removeAt(i)
            }

            // Move Enemies
            for (i in enemies.indices.reversed()) {
                val e = enemies[i]
                e.y += e.speed

                // Collision with Ship
                if (e.y >= 380f && kotlin.math.abs(e.x - shipX) < 30f) {
                    isGameOver = true
                }

                if (e.y > 420f) {
                    enemies.removeAt(i)
                }
            }

            // Bullet - Enemy Collision
            for (bi in bullets.indices.reversed()) {
                val b = bullets.getOrNull(bi) ?: continue
                for (ei in enemies.indices.reversed()) {
                    val e = enemies.getOrNull(ei) ?: continue
                    if (kotlin.math.abs(b.x - e.x) < 25f && kotlin.math.abs(b.y - e.y) < 25f) {
                        bullets.removeAt(bi)
                        enemies.removeAt(ei)
                        score += 50
                        break
                    }
                }
            }
        }
    }

    fun fireBullet() {
        if (!isGameOver) {
            bullets.add(Bullet(shipX, 380f))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Uzay Skoru: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonMagenta)
            IconButton(onClick = { resetGame() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Sıfırla", tint = NeonMagenta)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(DarkBackground, RoundedCornerShape(16.dp))
                .border(2.dp, NeonMagenta.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        shipX = (shipX + dragAmount.x).coerceIn(20f, 300f)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw Bullets
                bullets.forEach { b ->
                    drawCircle(color = NeonCyan, radius = 4f, center = Offset(b.x, b.y))
                }

                // Draw Enemies
                enemies.forEach { e ->
                    drawRoundRect(
                        color = Color.Red,
                        topLeft = Offset(e.x - 12f, e.y - 12f),
                        size = Size(24f, 24f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }

                // Draw Ship
                drawRoundRect(
                    color = NeonMagenta,
                    topLeft = Offset(shipX - 20f, 380f),
                    size = Size(40f, 18f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                )
            }

            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("UZAY GEMİSİ DÜŞTÜ!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        Text("Toplanan Puan: $score", fontSize = 14.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { resetGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
                        ) {
                            Text("Yeni Görev Başlat", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { shipX = (shipX - 30f).coerceIn(20f, 300f) },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta)
            ) {
                Text("◄ Sol", color = NeonMagenta)
            }

            Button(
                onClick = { fireBullet() },
                colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
            ) {
                Text("🚀 ATEŞ ET", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { shipX = (shipX + 30f).coerceIn(20f, 300f) },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta)
            ) {
                Text("Sağ ►", color = NeonMagenta)
            }
        }
    }
}

// --------------------------------------------------------
// 8. SPEED MATH GAME IMPLEMENTATION
// --------------------------------------------------------
@Composable
fun SpeedMathGameView() {
    var num1 by remember { mutableIntStateOf(12) }
    var num2 by remember { mutableIntStateOf(8) }
    var isAddition by remember { mutableStateOf(true) }
    var displayedAnswer by remember { mutableIntStateOf(20) }
    var isCorrectAnswer by remember { mutableStateOf(true) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableStateOf(5f) }
    var isGameOver by remember { mutableStateOf(false) }

    fun generateQuestion() {
        num1 = Random.nextInt(2, 25)
        num2 = Random.nextInt(2, 25)
        isAddition = Random.nextBoolean()
        val realAns = if (isAddition) num1 + num2 else num1 * num2
        isCorrectAnswer = Random.nextBoolean()
        displayedAnswer = if (isCorrectAnswer) realAns else realAns + Random.nextInt(-5, 6).let { if (it == 0) 2 else it }
        timeLeft = 5f
    }

    fun startNewGame() {
        score = 0
        isGameOver = false
        generateQuestion()
    }

    LaunchedEffect(Unit) {
        generateQuestion()
    }

    LaunchedEffect(isGameOver, timeLeft) {
        while (!isGameOver && timeLeft > 0f) {
            delay(100)
            timeLeft -= 0.1f
            if (timeLeft <= 0f) {
                isGameOver = true
            }
        }
    }

    fun answer(userChoice: Boolean) {
        if (userChoice == isCorrectAnswer) {
            score += 10
            generateQuestion()
        } else {
            isGameOver = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Puan: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFCC00))
            Text("Kalan Süre: ${"%.1f".format(timeLeft)}s", fontSize = 14.sp, color = NeonCyan)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(DarkSurface, RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFFFCC00).copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (!isGameOver) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isAddition) "$num1 + $num2 = ?" else "$num1 × $num2 = ?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "= $displayedAnswer",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFCC00)
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ZAMAN BİTTİ VEYA YANLIŞ!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                    Text("Toplam Skorunuz: $score", fontSize = 16.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { startNewGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
                    ) {
                        Text("Yeniden Başla", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { answer(true) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("✓ DOĞRU", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { answer(false) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("✕ YANLIŞ", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --------------------------------------------------------
// 9. COLOR REFLEX (SIMON SAYS) GAME IMPLEMENTATION
// --------------------------------------------------------
@Composable
fun ColorReflexGameView() {
    val sequence = remember { mutableStateListOf<Int>() }
    var userStep by remember { mutableIntStateOf(0) }
    var activeHighlight by remember { mutableStateOf<Int?>(null) }
    var isCpuPlaying by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    fun playNextRound() {
        sequence.add(Random.nextInt(4))
        userStep = 0
        isCpuPlaying = true
    }

    fun startNewGame() {
        sequence.clear()
        score = 0
        isGameOver = false
        playNextRound()
    }

    LaunchedEffect(isCpuPlaying) {
        if (isCpuPlaying) {
            delay(500)
            sequence.forEach { colorIdx ->
                activeHighlight = colorIdx
                delay(400)
                activeHighlight = null
                delay(200)
            }
            isCpuPlaying = false
        }
    }

    fun onPadClick(idx: Int) {
        if (isCpuPlaying || isGameOver) return
        if (sequence.getOrNull(userStep) == idx) {
            userStep++
            if (userStep >= sequence.size) {
                score += 100
                playNextRound()
            }
        } else {
            isGameOver = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Puan: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            Text(if (isCpuPlaying) "🤖 Keon Gösteriyor..." else "👉 Sıra Sende!", fontSize = 13.sp, color = NeonCyan)
            IconButton(onClick = { startNewGame() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Yeniden Başlat", tint = NeonGreen)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val colors = listOf(NeonCyan, NeonMagenta, NeonGreen, Color(0xFFFFCC00))

        Column(
            modifier = Modifier.size(280.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in 0..1) {
                    val color = colors[i]
                    val isLit = activeHighlight == i
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (isLit) color else color.copy(alpha = 0.3f),
                                RoundedCornerShape(16.dp)
                            )
                            .border(2.dp, color, RoundedCornerShape(16.dp))
                            .clickable { onPadClick(i) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in 2..3) {
                    val color = colors[i]
                    val isLit = activeHighlight == i
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (isLit) color else color.copy(alpha = 0.3f),
                                RoundedCornerShape(16.dp)
                            )
                            .border(2.dp, color, RoundedCornerShape(16.dp))
                            .clickable { onPadClick(i) }
                    )
                }
            }
        }

        if (sequence.isEmpty() && !isGameOver) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { startNewGame() },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                Text("Oyunu Başlat", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        if (isGameOver) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("HATALI SIRA! OYUN BİTTİ", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { startNewGame() },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                Text("Tekrar Dene", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --------------------------------------------------------
// 10. RETRO PONG AIR HOCKEY GAME IMPLEMENTATION
// --------------------------------------------------------
@Composable
fun PongAirHockeyGameView() {
    var playerX by remember { mutableStateOf(130f) }
    var aiX by remember { mutableStateOf(130f) }
    var ballX by remember { mutableStateOf(150f) }
    var ballY by remember { mutableStateOf(200f) }
    var ballVx by remember { mutableStateOf(5f) }
    var ballVy by remember { mutableStateOf(6f) }
    var playerScore by remember { mutableIntStateOf(0) }
    var aiScore by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    fun resetBall() {
        ballX = 150f
        ballY = 200f
        ballVx = if (Random.nextBoolean()) 5f else -5f
        ballVy = if (Random.nextBoolean()) 6f else -6f
    }

    fun startNewGame() {
        playerScore = 0
        aiScore = 0
        isGameOver = false
        resetBall()
    }

    LaunchedEffect(isGameOver) {
        while (!isGameOver) {
            delay(16)
            ballX += ballVx
            ballY += ballVy

            // Smooth AI movement
            aiX += (ballX - (aiX + 35f)) * 0.12f

            // Wall collisions
            if (ballX <= 10f || ballX >= 290f) ballVx = -ballVx

            // Player Paddle Collision
            if (ballY >= 370f && ballY <= 385f && ballX >= playerX - 10f && ballX <= playerX + 70f) {
                ballVy = -kotlin.math.abs(ballVy)
            }

            // AI Paddle Collision
            if (ballY <= 30f && ballY >= 15f && ballX >= aiX - 10f && ballX <= aiX + 70f) {
                ballVy = kotlin.math.abs(ballVy)
            }

            // Goal Player Bottom
            if (ballY > 410f) {
                aiScore++
                if (aiScore >= 5) isGameOver = true else resetBall()
            }

            // Goal AI Top
            if (ballY < 5f) {
                playerScore++
                if (playerScore >= 5) isGameOver = true else resetBall()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sen: $playerScore", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            Text("VS", fontSize = 14.sp, color = TextSecondary)
            Text("Keon AI: $aiScore", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonMagenta)
            IconButton(onClick = { startNewGame() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Yeniden Başlat", tint = NeonCyan)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(410.dp)
                .background(DarkBackground, RoundedCornerShape(16.dp))
                .border(2.dp, NeonBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        playerX = (playerX + dragAmount.x).coerceIn(10f, 230f)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Center Line
                drawLine(
                    color = Color.White.copy(alpha = 0.2f),
                    start = Offset(0f, 205f),
                    end = Offset(size.width, 205f),
                    strokeWidth = 2f
                )

                // AI Paddle
                drawRoundRect(
                    color = NeonMagenta,
                    topLeft = Offset(aiX, 20f),
                    size = Size(65f, 12f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                )

                // Player Paddle
                drawRoundRect(
                    color = NeonCyan,
                    topLeft = Offset(playerX, 375f),
                    size = Size(65f, 12f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                )

                // Ball
                drawCircle(color = Color.Yellow, radius = 9f, center = Offset(ballX, ballY))
            }

            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (playerScore >= 5) "🏆 SİZ KAZANDINIZ!" else "🤖 KEON AI KAZANDI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (playerScore >= 5) NeonGreen else NeonMagenta
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { startNewGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("Yeni Maç Başlat", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 12. CHROME T-REX DINO RUNNER (%100 YEREL NATIVE COMPOSE)
// ----------------------------------------------------
@Composable
fun DinoRunnerGameView() {
    var isRunning by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var highScore by remember { mutableIntStateOf(0) }

    var dinoY by remember { mutableFloatStateOf(0f) }
    var dinoVelocityY by remember { mutableFloatStateOf(0f) }
    var isJumping by remember { mutableStateOf(false) }
    var isDucking by remember { mutableStateOf(false) }

    // Obstacles: X position, type (0 = Cactus small, 1 = Cactus big, 2 = Bird)
    val obstacles = remember { mutableStateListOf<Triple<Float, Int, Float>>() }
    var speed by remember { mutableFloatStateOf(6f) }
    var groundOffset by remember { mutableFloatStateOf(0f) }

    fun jump() {
        if (!isJumping && !isGameOver) {
            if (!isRunning) isRunning = true
            isJumping = true
            dinoVelocityY = -14f
        } else if (isGameOver) {
            // Restart
            obstacles.clear()
            score = 0
            speed = 6f
            dinoY = 0f
            dinoVelocityY = 0f
            isJumping = false
            isGameOver = false
            isRunning = true
        }
    }

    LaunchedEffect(isRunning, isGameOver) {
        if (isRunning && !isGameOver) {
            while (true) {
                delay(16) // ~60fps
                score += 1
                if (score > highScore) highScore = score

                // Increase speed gradually
                if (score % 250 == 0 && speed < 14f) {
                    speed += 0.5f
                }

                // Dino physics
                if (isJumping) {
                    dinoY += dinoVelocityY
                    dinoVelocityY += 0.85f // Gravity
                    if (dinoY >= 0f) {
                        dinoY = 0f
                        dinoVelocityY = 0f
                        isJumping = false
                    }
                }

                // Ground animation
                groundOffset = (groundOffset + speed) % 50f

                // Spawn obstacles
                if (obstacles.isEmpty() || (obstacles.last().first < 600f && Random.nextFloat() < 0.035f)) {
                    val type = if (score > 300 && Random.nextFloat() < 0.3f) 2 else Random.nextInt(2) // 2 is bird
                    val birdY = if (type == 2) (Random.nextInt(40) - 50).toFloat() else 0f
                    obstacles.add(Triple(800f + Random.nextInt(150), type, birdY))
                }

                // Move obstacles & collision
                val iterator = obstacles.listIterator()
                while (iterator.hasNext()) {
                    val obs = iterator.next()
                    val nextX = obs.first - speed
                    if (nextX < -60f) {
                        iterator.remove()
                    } else {
                        iterator.set(Triple(nextX, obs.second, obs.third))

                        // Check collision with Dino (Dino is at X ~ 60..100)
                        val dinoLeft = 60f
                        val dinoRight = 100f
                        val dinoBottom = 0f - dinoY
                        val dinoTop = (if (isDucking) 20f else 40f) - dinoY

                        val obsLeft = nextX
                        val obsRight = nextX + if (obs.second == 2) 35f else 25f
                        val obsBottom = obs.third
                        val obsTop = obs.third + if (obs.second == 2) 25f else 40f

                        // AABB collision
                        if (dinoRight > obsLeft && dinoLeft < obsRight && dinoBottom <= obsTop && dinoTop >= obsBottom) {
                            isGameOver = true
                            isRunning = false
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Score bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Puan: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
            Text("En Yüksek: $highScore", fontSize = 14.sp, color = TextSecondary)
            Text(if (isGameOver) "💥 Çarptı!" else if (!isRunning) "👆 Dokun & Başla" else "🦖 Koşuyor...", fontSize = 12.sp, color = NeonGreen)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Game Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFF141923), RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFFF9800).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectTapGestures {
                        jump()
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val groundY = size.height - 60f

                // Ground Line & bumps
                drawLine(
                    color = Color(0xFF607D8B),
                    start = Offset(0f, groundY),
                    end = Offset(size.width, groundY),
                    strokeWidth = 3f
                )

                // Ground details
                for (i in -1..20) {
                    val dotX = (i * 45f - groundOffset)
                    if (dotX in 0f..size.width) {
                        drawLine(
                            color = Color(0xFF455A64),
                            start = Offset(dotX, groundY + 8f),
                            end = Offset(dotX + 15f, groundY + 8f),
                            strokeWidth = 2f
                        )
                    }
                }

                // Dino (X = 70f, Ground = groundY)
                val dinoPosX = 70f
                val dinoPosY = groundY - 40f + dinoY

                // Draw Chrome T-Rex in Neon Style
                drawRoundRect(
                    color = if (isGameOver) Color(0xFFFF5252) else NeonGreen,
                    topLeft = Offset(dinoPosX, dinoPosY),
                    size = Size(32f, if (isDucking) 24f else 38f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                )
                // Dino Eye
                drawCircle(
                    color = Color.Black,
                    radius = 3f,
                    center = Offset(dinoPosX + 24f, dinoPosY + 8f)
                )
                // Dino Legs
                if (!isJumping && isRunning) {
                    val legStep = (score / 4) % 2 == 0
                    drawRect(
                        color = NeonGreen,
                        topLeft = Offset(dinoPosX + if (legStep) 6f else 20f, dinoPosY + 38f),
                        size = Size(5f, 10f)
                    )
                }

                // Obstacles
                obstacles.forEach { obs ->
                    val obsX = obs.first
                    val obsType = obs.second
                    val obsExtraY = obs.third

                    if (obsType == 2) {
                        // Flying Pterodactyl Bird
                        val birdY = groundY - 50f + obsExtraY
                        drawRoundRect(
                            color = Color(0xFFFF5252),
                            topLeft = Offset(obsX, birdY),
                            size = Size(32f, 20f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )
                        // Wing
                        val wingUp = (score / 6) % 2 == 0
                        drawRect(
                            color = Color(0xFFFF8A80),
                            topLeft = Offset(obsX + 10f, birdY + if (wingUp) -10f else 16f),
                            size = Size(12f, 8f)
                        )
                    } else {
                        // Cactus
                        val cactusHeight = if (obsType == 1) 48f else 36f
                        val cactusY = groundY - cactusHeight
                        drawRoundRect(
                            color = Color(0xFF4CAF50),
                            topLeft = Offset(obsX, cactusY),
                            size = Size(20f, cactusHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )
                        // Cactus side branches
                        drawRect(
                            color = Color(0xFF4CAF50),
                            topLeft = Offset(obsX - 6f, cactusY + 10f),
                            size = Size(6f, 12f)
                        )
                        drawRect(
                            color = Color(0xFF4CAF50),
                            topLeft = Offset(obsX + 20f, cactusY + 16f),
                            size = Size(6f, 12f)
                        )
                    }
                }
            }

            if (!isRunning && !isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🦖 ZIPLAMAK İÇİN EKRANA DOKUN", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                }
            }

            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💀 OYUN BİTTİ", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Skor: $score", fontSize = 16.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { jump() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                        ) {
                            Text("🔄 Yeniden Oyna", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Jump Button for easy tapping
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { jump() },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Zıpla", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("🚀 ZIPLA (JUMP)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// ----------------------------------------------------
// 13. RETRO PAC-MAZE RUNNER (%100 YEREL NATIVE COMPOSE)
// ----------------------------------------------------
@Composable
fun PacmanRetroGameView() {
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var isGameOver by remember { mutableStateOf(false) }
    var isGameWon by remember { mutableStateOf(false) }

    // Grid: 9 cols x 11 rows
    val gridWidth = 9
    val gridHeight = 11

    var pacmanX by remember { mutableIntStateOf(4) }
    var pacmanY by remember { mutableIntStateOf(8) }
    var pacDirection by remember { mutableStateOf(0) } // 0: Right, 1: Down, 2: Left, 3: Up

    // Ghosts (X, Y, color)
    var ghost1X by remember { mutableIntStateOf(2) }
    var ghost1Y by remember { mutableIntStateOf(3) }
    var ghost2X by remember { mutableIntStateOf(6) }
    var ghost2Y by remember { mutableIntStateOf(3) }

    // Dots board
    val dots = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(gridWidth * gridHeight) { index ->
                val x = index % gridWidth
                val y = index / gridWidth
                // Leave walls and center open
                val isWall = (x == 0 || x == gridWidth - 1 || y == 0 || y == gridHeight - 1) ||
                        (y == 2 && x in 2..6) || (y == 5 && (x in 1..2 || x in 6..7)) ||
                        (y == 7 && x in 3..5)
                add(!isWall)
            }
        }
    }

    fun isWallAt(x: Int, y: Int): Boolean {
        if (x < 0 || x >= gridWidth || y < 0 || y >= gridHeight) return true
        if (x == 0 || x == gridWidth - 1 || y == 0 || y == gridHeight - 1) return true
        if (y == 2 && x in 2..6) return true
        if (y == 5 && (x in 1..2 || x in 6..7)) return true
        if (y == 7 && x in 3..5) return true
        return false
    }

    fun restartGame() {
        score = 0
        lives = 3
        pacmanX = 4
        pacmanY = 8
        ghost1X = 2
        ghost1Y = 3
        ghost2X = 6
        ghost2Y = 3
        isGameOver = false
        isGameWon = false
        dots.clear()
        repeat(gridWidth * gridHeight) { index ->
            val x = index % gridWidth
            val y = index / gridWidth
            val wall = isWallAt(x, y)
            dots.add(!wall)
        }
    }

    fun movePacman(dx: Int, dy: Int, dir: Int) {
        if (isGameOver || isGameWon) return
        val targetX = pacmanX + dx
        val targetY = pacmanY + dy
        if (!isWallAt(targetX, targetY)) {
            pacmanX = targetX
            pacmanY = targetY
            pacDirection = dir

            // Eat dot
            val index = pacmanY * gridWidth + pacmanX
            if (index in 0 until dots.size && dots[index]) {
                dots[index] = false
                score += 10

                // Check win
                if (dots.none { it }) {
                    isGameWon = true
                }
            }
        }
    }

    // Ghost AI Movement Loop
    LaunchedEffect(isGameOver, isGameWon) {
        if (!isGameOver && !isGameWon) {
            while (true) {
                delay(320)
                // Move Ghost 1 towards Pacman
                val g1Dx = (pacmanX - ghost1X).coerceIn(-1, 1)
                val g1Dy = (pacmanY - ghost1Y).coerceIn(-1, 1)
                if (Random.nextBoolean() && !isWallAt(ghost1X + g1Dx, ghost1Y)) {
                    ghost1X += g1Dx
                } else if (!isWallAt(ghost1X, ghost1Y + g1Dy)) {
                    ghost1Y += g1Dy
                }

                // Move Ghost 2 randomly / chase
                val dirs = listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0)).shuffled()
                for (d in dirs) {
                    if (!isWallAt(ghost2X + d.first, ghost2Y + d.second)) {
                        ghost2X += d.first
                        ghost2Y += d.second
                        break
                    }
                }

                // Check Collision with Ghosts
                if ((pacmanX == ghost1X && pacmanY == ghost1Y) || (pacmanX == ghost2X && pacmanY == ghost2Y)) {
                    lives -= 1
                    if (lives <= 0) {
                        isGameOver = true
                    } else {
                        // Reset positions
                        pacmanX = 4
                        pacmanY = 8
                        ghost1X = 2
                        ghost1Y = 3
                        ghost2X = 6
                        ghost2Y = 3
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Puan: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
            Row {
                repeat(lives) {
                    Text("❤️", fontSize = 14.sp)
                }
            }
            IconButton(onClick = { restartGame() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Yeniden Başlat", tint = NeonCyan)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Maze Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(Color(0xFF060913), RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFF1E88E5), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellW = size.width / gridWidth
                val cellH = size.height / gridHeight

                // Draw Walls & Dots
                for (y in 0 until gridHeight) {
                    for (x in 0 until gridWidth) {
                        val cellLeft = x * cellW
                        val cellTop = y * cellH

                        if (isWallAt(x, y)) {
                            drawRoundRect(
                                color = Color(0xFF1565C0),
                                topLeft = Offset(cellLeft + 2f, cellTop + 2f),
                                size = Size(cellW - 4f, cellH - 4f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                            )
                        } else {
                            val dotIndex = y * gridWidth + x
                            if (dotIndex in 0 until dots.size && dots[dotIndex]) {
                                drawCircle(
                                    color = Color(0xFFFFE082),
                                    radius = 4f,
                                    center = Offset(cellLeft + cellW / 2, cellTop + cellH / 2)
                                )
                            }
                        }
                    }
                }

                // Draw Pac-Man
                val pacCenter = Offset(pacmanX * cellW + cellW / 2, pacmanY * cellH + cellH / 2)
                drawCircle(
                    color = Color(0xFFFFD700),
                    radius = cellW * 0.4f,
                    center = pacCenter
                )
                // Pac-Man Eye
                drawCircle(
                    color = Color.Black,
                    radius = 3f,
                    center = Offset(pacCenter.x + 2f, pacCenter.y - cellH * 0.15f)
                )

                // Ghost 1 (Red Blinky)
                val g1Center = Offset(ghost1X * cellW + cellW / 2, ghost1Y * cellH + cellH / 2)
                drawRoundRect(
                    color = Color(0xFFFF1744),
                    topLeft = Offset(g1Center.x - cellW * 0.38f, g1Center.y - cellH * 0.38f),
                    size = Size(cellW * 0.76f, cellH * 0.76f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                )

                // Ghost 2 (Cyan Inky)
                val g2Center = Offset(ghost2X * cellW + cellW / 2, ghost2Y * cellH + cellH / 2)
                drawRoundRect(
                    color = NeonCyan,
                    topLeft = Offset(g2Center.x - cellW * 0.38f, g2Center.y - cellH * 0.38f),
                    size = Size(cellW * 0.76f, cellH * 0.76f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                )
            }

            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💀 HAYALETLER YAKALADI!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF1744))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Skor: $score", fontSize = 14.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { restartGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                        ) {
                            Text("Tekrar Dene", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (isGameWon) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏆 TEBRİKLER! LABİRENTİ TEMİZLEDİNİZ", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Skor: $score", fontSize = 14.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { restartGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                        ) {
                            Text("Yeniden Başlat", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // D-Pad Touch Controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { movePacman(0, -1, 3) },
                modifier = Modifier
                    .size(46.dp)
                    .background(DarkSurface, CircleShape)
                    .border(1.dp, Color(0xFFFFD700), CircleShape)
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Yukarı", tint = Color(0xFFFFD700))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                IconButton(
                    onClick = { movePacman(-1, 0, 2) },
                    modifier = Modifier
                        .size(46.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.dp, Color(0xFFFFD700), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Sol", tint = Color(0xFFFFD700))
                }

                IconButton(
                    onClick = { movePacman(0, 1, 1) },
                    modifier = Modifier
                        .size(46.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.dp, Color(0xFFFFD700), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = "Aşağı", tint = Color(0xFFFFD700))
                }

                IconButton(
                    onClick = { movePacman(1, 0, 0) },
                    modifier = Modifier
                        .size(46.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.dp, Color(0xFFFFD700), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Sağ", tint = Color(0xFFFFD700))
                }
            }
        }
    }
}
