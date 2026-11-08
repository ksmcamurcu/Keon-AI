package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.neonGlow
import com.example.ui.components.ThreeDAnimatedIcon
import com.example.ui.tabs.KeonStoreTab
import com.example.ui.theme.FlameNeonOrange

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiOrbitOverlay
import com.example.ui.components.CubeLogo3D
import com.example.ui.components.Floating3DCubeOverlay
import com.example.ui.components.KeonLogoStyle
import com.example.ui.components.KeonLogoView
import com.example.ui.components.LiveTechBackground
import com.example.ui.components.QuantumStartupOverlay
import com.example.ui.components.VoiceAssistantOverlay
import com.example.ui.components.neonGlow
import com.example.ui.components.pulsatingNeonGlow
import com.example.ui.tabs.AdministratorTab
import com.example.ui.tabs.CloudBoxTab
import com.example.ui.tabs.CreativeStudioTab
import com.example.ui.tabs.GamesTab
import com.example.ui.tabs.KeonChatTab
import com.example.ui.tabs.SearchTab
import com.example.ui.tabs.SecurityTab
import com.example.ui.tabs.SettingsTab
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant

import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun KeonMainScreen(
    viewModel: KeonViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val allHistoryDesc by viewModel.allHistoryDesc.collectAsStateWithLifecycle()
    val messageCount by viewModel.messageCount.collectAsStateWithLifecycle()
    val isTyping by viewModel.isTyping.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedSearchEngine by viewModel.selectedSearchEngine.collectAsStateWithLifecycle()
    val isOrbitActive by viewModel.isOrbitActive.collectAsStateWithLifecycle()

    val allocatedGb by viewModel.allocatedGb.collectAsStateWithLifecycle()
    val usedGb by viewModel.usedGb.collectAsStateWithLifecycle()
    val isDriveMounted by viewModel.isDriveMounted.collectAsStateWithLifecycle()
    val isOsRunning by viewModel.isOsRunning.collectAsStateWithLifecycle()
    val cloudFiles by viewModel.cloudFiles.collectAsStateWithLifecycle()

    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsStateWithLifecycle()
    val isSoundEffectsEnabled by viewModel.isSoundEffectsEnabled.collectAsStateWithLifecycle()
    val isAutoBackupEnabled by viewModel.isAutoBackupEnabled.collectAsStateWithLifecycle()

    val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()
    val selectedNeonAccent by viewModel.selectedNeonAccent.collectAsStateWithLifecycle()
    val activePackage by viewModel.activePackage.collectAsStateWithLifecycle()
    val selectedWallpaper by viewModel.selectedWallpaper.collectAsStateWithLifecycle()
    val wallpaperAlpha by viewModel.wallpaperAlpha.collectAsStateWithLifecycle()
    val selectedLogoStyle by viewModel.selectedLogoStyle.collectAsStateWithLifecycle()
    val isDemoModeEnabled by viewModel.isDemoModeEnabled.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val adminPhoneNumber by viewModel.adminPhoneNumber.collectAsStateWithLifecycle()
    val adminAuthError by viewModel.adminAuthError.collectAsStateWithLifecycle()
    val isHighAccessibilityEnabled by viewModel.isHighAccessibilityEnabled.collectAsStateWithLifecycle()
    val isHighContrastNeonEnabled by viewModel.isHighContrastNeonEnabled.collectAsStateWithLifecycle()
    val isDarkModeEnabled by viewModel.isDarkModeEnabled.collectAsStateWithLifecycle()
    val isAmoledModeEnabled by viewModel.isAmoledModeEnabled.collectAsStateWithLifecycle()
    val isEyeProtectionNightFilter by viewModel.isEyeProtectionNightFilter.collectAsStateWithLifecycle()
    val isFloating3DCubeEnabled by viewModel.isFloating3DCubeEnabled.collectAsStateWithLifecycle()
    val fontSizeScale by viewModel.fontSizeScale.collectAsStateWithLifecycle()
    val isVoiceAssistantOpen by viewModel.isVoiceAssistantOpen.collectAsStateWithLifecycle()
    val selectedVoiceProfile by viewModel.selectedVoiceProfile.collectAsStateWithLifecycle()
    val voicePitch by viewModel.voicePitch.collectAsStateWithLifecycle()
    val voiceSpeechRate by viewModel.voiceSpeechRate.collectAsStateWithLifecycle()
    val isAudioAnalyzing by viewModel.isAudioAnalyzing.collectAsStateWithLifecycle()
    val lastAudioAnalysisResult by viewModel.lastAudioAnalysisResult.collectAsStateWithLifecycle()
    val selectedAiModel by viewModel.selectedAiModel.collectAsStateWithLifecycle()
    val currentUserAccount by viewModel.currentUserAccount.collectAsStateWithLifecycle()
    val registeredUsersList by viewModel.registeredUsersList.collectAsStateWithLifecycle()
    val userAuthError by viewModel.userAuthError.collectAsStateWithLifecycle()
    val isVipApprovedByAdmin by viewModel.isVipApprovedByAdmin.collectAsStateWithLifecycle()
    val vipPackageError by viewModel.vipPackageError.collectAsStateWithLifecycle()

    var showInfoDialog by remember { mutableStateOf(false) }
    var showQuickThemeDialog by remember { mutableStateOf(false) }
    var showQuantumStartup by remember { mutableStateOf(true) }
    var isViewExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Technology Theme & Wallpaper Background
            LiveTechBackground(
                activeTheme = selectedTheme,
                activeWallpaper = selectedWallpaper,
                wallpaperAlpha = wallpaperAlpha
            )

            // Main Content Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile / Account Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkSurface)
                            .border(1.dp, if (currentUserAccount != null) NeonGreen else NeonCyan, RoundedCornerShape(20.dp))
                            .clickable { viewModel.selectTab(AppTab.SETTINGS) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = if (currentUserAccount != null) Icons.Default.Person else Icons.Default.AccountCircle,
                            contentDescription = "Hesap & Profil",
                            tint = if (currentUserAccount != null) NeonGreen else NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentUserAccount?.name?.take(10) ?: "Kaydol / Giriş",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.openVoiceAssistant() }) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Sesli Asistan Modu",
                                tint = NeonMagenta,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(onClick = {
                            viewModel.toggleDarkMode(!isDarkModeEnabled)
                        }) {
                            Icon(
                                imageVector = if (isDarkModeEnabled) Icons.Default.Nightlight else Icons.Default.DarkMode,
                                contentDescription = "Karanlık Arayüz & Gece Modu",
                                tint = if (isDarkModeEnabled) Color(0xFF00E5FF) else Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(onClick = { showQuantumStartup = true }) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Kuantum Animasyonu Göster",
                                tint = selectedTheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(onClick = { showQuickThemeDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Hızlı Tema & Duvar Kağıdı",
                                tint = selectedTheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(onClick = { showInfoDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Bilgi",
                                tint = selectedTheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Top Header Section & Logo
                if (isViewExpanded) {
                    // Header when section is opened (Icon stays full size 85.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            KeonLogoView(
                                logoStyle = selectedLogoStyle,
                                size = 85.dp,
                                onClick = { isViewExpanded = false }
                            )
                            Column {
                                Text(
                                    text = "KEON AI",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 3.sp,
                                    style = androidx.compose.ui.text.TextStyle(
                                        brush = Brush.linearGradient(
                                            listOf(NeonCyan, NeonMagenta, NeonGreen)
                                        )
                                    )
                                )
                                Text(
                                    text = when (selectedTab) {
                                        AppTab.KEON_AI -> "💬 Sohbet Bölümü"
                                        AppTab.CREATIVE_STUDIO -> "🚀 Üretim Stüdyosu (Müzik, App, Web)"
                                        AppTab.GAMES -> "🎮 Oyunlar Bölümü"
                                        AppTab.CLOUDBOX -> "☁️ 60GB Bulut"
                                        AppTab.STORE -> "🛍️ Keon Store Ekosistem & Paket Yöneticisi"
                                        AppTab.SETTINGS -> "⚙️ Sistem Ayarları"
                                        AppTab.ADMINISTRATOR -> "👑 Edminastör Paneli"
                                        AppTab.SECURITY -> "🛡️ Güvenlik Denetimi"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                        }
                    }
                } else {
                    // Full Logo & Header when viewing Main Menu
                    KeonLogoView(
                        logoStyle = selectedLogoStyle,
                        size = 85.dp,
                        onClick = { isViewExpanded = true }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "KEON AI",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp,
                        style = androidx.compose.ui.text.TextStyle(
                            brush = Brush.linearGradient(
                                listOf(NeonCyan, NeonMagenta, NeonGreen)
                            )
                        )
                    )

                    Text(
                        text = "KENDİ YAPAY ZEKA ASİSTANINIZ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 2.sp
                    )

                    activePackage?.let { pkg ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(listOf(pkg.primaryColor, pkg.secondaryColor)),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = pkg.badge,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3 Square Neon Cards Per Row Grid Layout
                    NeonGridDashboard(
                        selectedTab = selectedTab,
                        isViewExpanded = isViewExpanded,
                        isAdmin = isAdmin,
                        onSelectTab = { tab ->
                            viewModel.selectTab(tab)
                            isViewExpanded = true
                        },
                        onOpenVoiceAssistant = {
                            viewModel.openVoiceAssistant()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Active Section Detail View (Sohbet Bölümü, Oyunlar, Ayarlar, etc.)
                if (isViewExpanded) {
                    Box(modifier = Modifier.weight(1f)) {
                        when (selectedTab) {
                            AppTab.KEON_AI -> KeonChatTab(
                                messages = messages,
                                isTyping = isTyping,
                                isApiKeyConfigured = viewModel.isApiKeyConfigured,
                                allHistoryDesc = allHistoryDesc,
                                messageCount = messageCount,
                                selectedAiModel = selectedAiModel,
                                onSelectAiModel = { model -> viewModel.selectAiModel(model) },
                                onSendMessage = { text -> viewModel.sendMessage(text) },
                                onClearChat = { viewModel.clearChat() },
                                onDeleteMessage = { id -> viewModel.deleteMessageById(id) },
                                onOpenVoiceAssistant = { viewModel.openVoiceAssistant() }
                            )
                            AppTab.CREATIVE_STUDIO -> CreativeStudioTab(
                                repository = viewModel.repository
                            )
                            AppTab.GAMES -> GamesTab(
                                onOpenVoiceAssistant = { viewModel.openVoiceAssistant() }
                            )
                            AppTab.CLOUDBOX -> CloudBoxTab(
                                allocatedGb = allocatedGb,
                                usedGb = usedGb,
                                isMounted = isDriveMounted,
                                isOsRunning = isOsRunning,
                                filesList = cloudFiles,
                                onSetAllocatedGb = { gb -> viewModel.setAllocatedStorageGb(gb) },
                                onToggleMount = { viewModel.toggleDriveMount() },
                                onToggleOs = { viewModel.toggleVirtualOs() },
                                onAddFile = { name, sizeMb, cat -> viewModel.addCloudFile(name, sizeMb, cat) },
                                onDeleteFile = { id -> viewModel.deleteCloudFile(id) },
                                onOptimizeSpace = { viewModel.optimizePhoneStorage() }
                            )
                            AppTab.STORE -> KeonStoreTab(
                                onNavigateToTab = { tab -> viewModel.selectTab(tab) }
                            )
                            AppTab.SETTINGS -> SettingsTab(
                                isFloating3DCubeEnabled = isFloating3DCubeEnabled,
                                onToggleFloating3DCube = { enabled -> viewModel.toggleFloating3DCube(enabled) },
                                selectedLanguage = selectedLanguage,
                                isNotificationsEnabled = isNotificationsEnabled,
                                isSoundEffectsEnabled = isSoundEffectsEnabled,
                                isAutoBackupEnabled = isAutoBackupEnabled,
                                allocatedGb = allocatedGb,
                                isApiKeyConfigured = viewModel.isApiKeyConfigured,
                                selectedTheme = selectedTheme,
                                selectedNeonAccent = selectedNeonAccent,
                                activePackage = activePackage,
                                selectedWallpaper = selectedWallpaper,
                                wallpaperAlpha = wallpaperAlpha,
                                selectedLogoStyle = selectedLogoStyle,
                                isDemoModeEnabled = isDemoModeEnabled,
                                isAdmin = isAdmin,
                                adminPhoneNumber = adminPhoneNumber,
                                adminAuthError = adminAuthError,
                                isHighAccessibilityEnabled = isHighAccessibilityEnabled,
                                isHighContrastNeonEnabled = isHighContrastNeonEnabled,
                                isDarkModeEnabled = isDarkModeEnabled,
                                isAmoledModeEnabled = isAmoledModeEnabled,
                                isEyeProtectionNightFilter = isEyeProtectionNightFilter,
                                fontSizeScale = fontSizeScale,
                                onLanguageSelect = { lang -> viewModel.setSelectedLanguage(lang) },
                                onToggleNotifications = { viewModel.toggleNotifications() },
                                onToggleSoundEffects = { viewModel.toggleSoundEffects() },
                                onToggleAutoBackup = { viewModel.toggleAutoBackup() },
                                onSetAllocatedGb = { gb -> viewModel.setAllocatedStorageGb(gb) },
                                onClearChatHistory = { viewModel.clearChat() },
                                onOptimizeStorage = { viewModel.optimizePhoneStorage() },
                                onToggleDarkMode = { enabled -> viewModel.toggleDarkMode(enabled) },
                                onToggleAmoledMode = { enabled -> viewModel.toggleAmoledMode(enabled) },
                                onToggleEyeProtectionFilter = { enabled -> viewModel.toggleEyeProtectionFilter(enabled) },
                                onSetDarkModePreset = { preset -> viewModel.setDarkModePreset(preset) },
                                onSelectTheme = { theme -> viewModel.selectTheme(theme) },
                                onSelectNeonAccent = { accent -> viewModel.selectNeonAccent(accent) },
                                onSelectPackage = { pkg -> viewModel.selectPackage(pkg) },
                                onSelectWallpaper = { wallpaper -> viewModel.selectWallpaper(wallpaper) },
                                onSetWallpaperAlpha = { alpha -> viewModel.setWallpaperAlpha(alpha) },
                                onSelectLogoStyle = { style -> viewModel.selectLogoStyle(style) },
                                onToggleDemoMode = { enabled -> viewModel.toggleDemoMode(enabled) },
                                onVerifyAdminPhone = { phone -> viewModel.verifyAdminPhoneNumber(phone) },
                                onRevokeAdmin = { viewModel.revokeAdmin() },
                                onToggleHighAccessibility = { enabled -> viewModel.toggleHighAccessibility(enabled) },
                                onToggleHighContrastNeon = { enabled -> viewModel.toggleHighContrastNeon(enabled) },
                                onSetFontSizeScale = { scale -> viewModel.setFontSizeScale(scale) },
                                onOpenVoiceAssistant = { viewModel.openVoiceAssistant() },
                                selectedVoiceProfile = selectedVoiceProfile,
                                voicePitch = voicePitch,
                                voiceSpeechRate = voiceSpeechRate,
                                isAudioAnalyzing = isAudioAnalyzing,
                                lastAudioAnalysisResult = lastAudioAnalysisResult,
                                onSelectVoiceProfile = { profile -> viewModel.selectVoiceProfile(profile) },
                                onSetVoicePitch = { pitch -> viewModel.setVoicePitch(pitch) },
                                onSetVoiceSpeechRate = { rate -> viewModel.setVoiceSpeechRate(rate) },
                                onAnalyzeAudioAndCalibrateVoice = { name, duration -> viewModel.analyzeAudioAndCalibrateVoice(name, duration) },
                                onClearAudioAnalysisResult = { viewModel.clearAudioAnalysisResult() },
                                currentUserAccount = currentUserAccount,
                                userAuthError = userAuthError,
                                isVipApprovedByAdmin = isVipApprovedByAdmin,
                                vipPackageError = vipPackageError,
                                onRegisterUser = { name, contact, pass, passConfirm, isEmail ->
                                    viewModel.registerUser(name, contact, pass, passConfirm, isEmail)
                                },
                                onLoginUser = { contact, pass ->
                                    viewModel.loginUser(contact, pass)
                                },
                                onLogoutUser = { viewModel.logoutUser() },
                                onClearAuthError = { viewModel.clearUserAuthError() },
                                onToggleVipApprovalByAdmin = { approved -> viewModel.toggleVipApprovalByAdmin(approved) },
                                onClearVipPackageError = { viewModel.clearVipPackageError() },
                                onRequestVipPackage = { pkg -> viewModel.requestVipPackage(pkg) },
                                onClearUserNotification = { viewModel.clearUserNotificationMessage() }
                            )
                            AppTab.ADMINISTRATOR -> AdministratorTab(
                                isAdmin = isAdmin,
                                adminPhoneNumber = adminPhoneNumber,
                                currentUserAccount = currentUserAccount,
                                registeredUsersList = registeredUsersList,
                                isVipApprovedByAdmin = isVipApprovedByAdmin,
                                activePackage = activePackage,
                                allocatedGb = allocatedGb,
                                onToggleVipApprovalByAdmin = { approved -> viewModel.toggleVipApprovalByAdmin(approved) },
                                onSelectPackage = { pkg -> viewModel.selectPackage(pkg) },
                                onGrantVipToUser = { userId, pkg -> viewModel.grantVipToUser(userId, pkg) },
                                onRevokeVipFromUser = { userId -> viewModel.revokeVipFromUser(userId) },
                                onResolveSupportTicket = { userId, note -> viewModel.resolveSupportTicketAndRegister(userId, note) },
                                onAddUserByAdmin = { name, contact, isEmail, pkg -> viewModel.addOrRegisterUserByAdmin(name, contact, isEmail, pkg) },
                                onDeleteUserByAdmin = { userId -> viewModel.deleteUserByAdmin(userId) },
                                onApproveVipRequest = { userId -> viewModel.approveVipRequest(userId) },
                                onRejectVipRequest = { userId -> viewModel.rejectVipRequest(userId) },
                                onKickUserSession = { userId -> viewModel.kickUserSession(userId) },
                                onTerminateAllOtherSessions = { viewModel.terminateAllOtherSessions() },
                                onSendNotificationToUserSession = { userId, msg -> viewModel.sendNotificationToUserSession(userId, msg) },
                                onSetAllocatedGb = { gb -> viewModel.setAllocatedStorageGb(gb) },
                                onRevokeAdmin = { viewModel.revokeAdmin() }
                            )
                            AppTab.SECURITY -> SecurityTab()
                        }
                    }
                }
            }

            // AI Orbit Overlay triggered when cube is tapped
            AiOrbitOverlay(
                active = isOrbitActive,
                aiList = viewModel.otherAis
            )

            // Dynamic Quantum Startup Animation Overlay
            QuantumStartupOverlay(
                isVisible = showQuantumStartup,
                onDismiss = { showQuantumStartup = false },
                activePackage = activePackage
            )

            // Voice Assistant Mode Overlay
            VoiceAssistantOverlay(
                isVisible = isVoiceAssistantOpen,
                messages = messages,
                isTyping = isTyping,
                voicePitch = voicePitch,
                voiceSpeechRate = voiceSpeechRate,
                onSendMessage = { text -> viewModel.sendMessage(text) },
                onDismiss = { viewModel.closeVoiceAssistant() }
            )

            // 🎲 Floating 3D Cube Assistant Overlay (1/20th Screen)
            Floating3DCubeOverlay(
                isVisible = isFloating3DCubeEnabled,
                voicePitch = voicePitch,
                voiceSpeechRate = voiceSpeechRate,
                onClose = { viewModel.toggleFloating3DCube(false) }
            )

            // 🛡️ Night Eye Protection / Soft Dim Filter Overlay
            if (isEyeProtectionNightFilter) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2A1502).copy(alpha = 0.16f))
                )
            }

            // Info Dialog
            if (showInfoDialog) {
                AlertDialog(
                    onDismissRequest = { showInfoDialog = false },
                    title = {
                        Text(text = "Keon AI Bilgi & Yapılandırma", color = selectedTheme.primary, fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Column {
                            Text(
                                text = "Keon AI, Google'ın gelişmiş altyapısını ve canlı internet arama ağını (Google Search Grounding) kullanır.\n\n" +
                                        "• Bağlantı Durumu: ${if (viewModel.isApiKeyConfigured) "[AKTİF - Google Bulut & Canlı İnternet Erişimi]" else "[AKTİF - İnternet & Google Arama Bağlantılı]"}\n" +
                                        "• Motor: Keon Neural Core & Gemini 3.5 Engine\n" +
                                        "• İnternet Erişimi: Canlı Google Arama Altyapısı Aktif\n" +
                                        "• Yerel Veritabanı: Room Persistence Engine\n\n" +
                                        "Gelişmiş bilişsel özellikleri, canlı internet araması ve duygusal zekasıyla sorulan tüm sorulara yanıt verir.",
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showInfoDialog = false }) {
                            Text("Tamam", color = selectedTheme.primary)
                        }
                    },
                    containerColor = DarkSurface,
                    shape = RoundedCornerShape(20.dp)
                )
            }

            // Quick Theme Selector Dialog
            if (showQuickThemeDialog) {
                AlertDialog(
                    onDismissRequest = { showQuickThemeDialog = false },
                    title = {
                        Text(
                            text = "🎨 Hızlı Tema & Canlı Duvar Kağıdı",
                            color = selectedTheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // 🌙 Karanlık Mod Hızlı Seçim
                            Text(
                                text = "🌙 Karanlık Arayüz & AMOLED Modu:",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "amoled" to "🌑 AMOLED",
                                    "midnight" to "🌌 Gece",
                                    "stealth" to "🕶️ Karbon",
                                    "neon" to "⚡ Neon"
                                ).forEach { (preset, label) ->
                                    Button(
                                        onClick = { viewModel.setDarkModePreset(preset) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DarkSurfaceVariant
                                        ),
                                        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(vertical = 6.dp, horizontal = 2.dp)
                                    ) {
                                        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "🌱 Keon AI Paketleri (Ücretsiz & VIP):",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                com.example.ui.theme.KeonPackage.values().forEach { pkg ->
                                    val isPkgSel = activePackage == pkg
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                brush = Brush.linearGradient(listOf(pkg.primaryColor, pkg.secondaryColor)),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .border(
                                                width = if (isPkgSel) 2.5.dp else 0.dp,
                                                color = if (isPkgSel) Color.White else Color.Transparent,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { viewModel.selectPackage(pkg) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = pkg.badge,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }

                            if (vipPackageError != null) {
                                Text(
                                    text = vipPackageError!!,
                                    color = Color(0xFFFF6688),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "⚡ Neon Vurgu Rengi:",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                com.example.ui.theme.NeonAccentColor.values().forEach { accent ->
                                    val isAccSel = selectedNeonAccent == accent
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(
                                                brush = Brush.linearGradient(
                                                    listOf(accent.primaryNeon, accent.secondaryNeon)
                                                ),
                                                shape = CircleShape
                                            )
                                            .border(
                                                width = if (isAccSel) 2.5.dp else 0.5.dp,
                                                color = if (isAccSel) Color.White else Color.White.copy(alpha = 0.4f),
                                                shape = CircleShape
                                            )
                                            .clickable { viewModel.selectNeonAccent(accent) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isAccSel) {
                                            Text("✓", color = Color.White, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Tema Seçin:",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                com.example.ui.theme.TechThemeId.values().forEach { theme ->
                                    val isSel = selectedTheme == theme
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(theme.primary, RoundedCornerShape(10.dp))
                                            .border(
                                                width = if (isSel) 2.5.dp else 0.dp,
                                                color = if (isSel) Color.White else Color.Transparent,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { viewModel.selectTheme(theme) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSel) {
                                            Text("✓", color = Color.Black, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Duvar Kağıdı Seçin:",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            com.example.ui.theme.TechWallpaperId.values().forEach { wp ->
                                val isWpSel = selectedWallpaper == wp
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isWpSel) selectedTheme.primary.copy(alpha = 0.2f) else DarkSurfaceVariant)
                                        .clickable { viewModel.selectWallpaper(wp) }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = wp.displayName,
                                        fontSize = 12.sp,
                                        color = if (isWpSel) selectedTheme.primary else TextPrimary,
                                        fontWeight = if (isWpSel) FontWeight.Bold else FontWeight.Medium
                                    )
                                    if (isWpSel) {
                                        Text("✓", color = selectedTheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Keon AI Logo Simgesi:",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                KeonLogoStyle.values().forEach { style ->
                                    val isStyleSel = selectedLogoStyle == style
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isStyleSel) selectedTheme.primary.copy(alpha = 0.3f) else DarkSurfaceVariant)
                                            .border(
                                                width = if (isStyleSel) 2.dp else 0.dp,
                                                color = if (isStyleSel) selectedTheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { viewModel.selectLogoStyle(style) }
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        KeonLogoView(logoStyle = style, size = 40.dp)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showQuickThemeDialog = false }) {
                            Text("Uygula", color = selectedTheme.primary, fontWeight = FontWeight.Bold)
                        }
                    },
                    containerColor = DarkSurface,
                    shape = RoundedCornerShape(20.dp)
                )
            }

        }
    }
}

@Composable
fun NavTabButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activePrimary = androidx.compose.material3.MaterialTheme.colorScheme.primary
    val activeSecondary = androidx.compose.material3.MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .background(
                brush = if (selected) Brush.linearGradient(listOf(activePrimary, activeSecondary))
                else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.Black else TextSecondary
        )
    }
}


@Composable
fun NeonBackgroundCanvas() {
    val floatAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        floatAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val offsetFactor = floatAnim.value * 30f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .blur(60.dp)
    ) {
        val width = size.width
        val height = size.height

        // Cyan Circle
        drawCircle(
            color = NeonCyan.copy(alpha = 0.22f),
            radius = 160.dp.toPx(),
            center = Offset(width * 0.2f + offsetFactor, height * 0.2f - offsetFactor)
        )

        // Magenta Circle
        drawCircle(
            color = NeonMagenta.copy(alpha = 0.20f),
            radius = 140.dp.toPx(),
            center = Offset(width * 0.8f - offsetFactor, height * 0.7f + offsetFactor)
        )

        // Green Circle
        drawCircle(
            color = NeonGreen.copy(alpha = 0.18f),
            radius = 120.dp.toPx(),
            center = Offset(width * 0.5f + offsetFactor, height * 0.5f + offsetFactor)
        )
    }
}

private data class NeonGridCardData(
    val id: AppTab?,
    val title: String,
    val icon: String,
    val accentColor: Color,
    val description: String
)

@Composable
fun NeonGridDashboard(
    selectedTab: AppTab,
    isViewExpanded: Boolean,
    isAdmin: Boolean = false,
    onSelectTab: (AppTab) -> Unit,
    onOpenVoiceAssistant: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseCardItems = listOf(
        NeonGridCardData(
            id = AppTab.KEON_AI,
            title = "Sohbet",
            icon = "📜",
            accentColor = NeonCyan,
            description = "AI Asistan"
        ),
        NeonGridCardData(
            id = AppTab.GAMES,
            title = "Oyunlar",
            icon = "🕹️",
            accentColor = NeonMagenta,
            description = "Mini Oyunlar"
        ),
        NeonGridCardData(
            id = AppTab.SETTINGS,
            title = "Ayarlar",
            icon = "⚙️",
            accentColor = NeonGreen,
            description = "Sistem & Tema"
        ),
        NeonGridCardData(
            id = AppTab.CLOUDBOX,
            title = "60GB Bulut",
            icon = "☁️",
            accentColor = Color(0xFFFFD700),
            description = "Depolama"
        ),
        NeonGridCardData(
            id = AppTab.STORE,
            title = "Keon Store",
            icon = "🛍️",
            accentColor = NeonBlue,
            description = "Paket Yöneticisi"
        ),
        NeonGridCardData(
            id = null,
            title = "Sesli Asistan",
            icon = "🎙",
            accentColor = Color(0xFFFF007F),
            description = "Sesli Komut"
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth(0.96f)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val rows = baseCardItems.chunked(3)
        var globalIndex = 0
        for (rowItems in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (item in rowItems) {
                    val itemIndex = globalIndex++
                    val isSelected = item.id != null && item.id == selectedTab && isViewExpanded

                    val modifierWithGlow = if (isSelected || item.id == null) {
                        Modifier.pulsatingNeonGlow(
                            glowColor = item.accentColor,
                            minAlpha = if (isSelected) 0.45f else 0.3f,
                            maxAlpha = if (isSelected) 0.95f else 0.7f,
                            glowRadius = if (isSelected) 16.dp else 12.dp,
                            shape = RoundedCornerShape(16.dp),
                            borderWidth = if (isSelected) 2.2.dp else 1.4.dp
                        )
                    } else {
                        Modifier.neonGlow(
                            glowColor = item.accentColor.copy(alpha = 0.5f),
                            glowRadius = 10.dp,
                            shape = RoundedCornerShape(16.dp),
                            borderWidth = 1.2.dp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .then(modifierWithGlow)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        DarkSurface.copy(alpha = 0.95f),
                                        Color(0xFF0C091A)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                if (item.id != null) {
                                    onSelectTab(item.id)
                                } else {
                                    onOpenVoiceAssistant()
                                }
                            }
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            ThreeDAnimatedIcon(
                                emoji = item.icon,
                                accentColor = item.accentColor,
                                isSelected = isSelected,
                                animationDelayMs = itemIndex * 180,
                                size = 38.dp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = item.description,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = item.accentColor,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .background(item.accentColor, CircleShape)
                            )
                        }
                    }
                }
            }
        }

        // Full-width Administrator Card on a separate bottom row (occupies 3 button widths)
        if (isAdmin) {
            val isAdminSelected = selectedTab == AppTab.ADMINISTRATOR && isViewExpanded
            val adminAccent = Color(0xFFFF0055)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .neonGlow(
                        glowColor = if (isAdminSelected) adminAccent else adminAccent.copy(alpha = 0.5f),
                        glowRadius = if (isAdminSelected) 16.dp else 10.dp,
                        shape = RoundedCornerShape(16.dp),
                        borderWidth = if (isAdminSelected) 2.5.dp else 1.2.dp
                    )
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF2A081A),
                                DarkSurface.copy(alpha = 0.95f),
                                Color(0xFF2A081A)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        onSelectTab(AppTab.ADMINISTRATOR)
                    }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThreeDAnimatedIcon(
                        emoji = "👑",
                        accentColor = adminAccent,
                        isSelected = isAdminSelected,
                        animationDelayMs = 0,
                        size = 32.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "👑 Edminastör Paneli",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Kullanıcı Yönetimi, VIP İzinleri & Sistem Onayları",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = adminAccent
                        )
                    }
                }

                if (isAdminSelected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(adminAccent, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadKeonWebScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun openOrDownloadKeonWeb() {
        val packageName = "com.kasimcamurcu.keon.web"
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            try {
                val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                context.startActivity(marketIntent)
            } catch (e: Exception) {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                context.startActivity(webIntent)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090614))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    Brush.linearGradient(listOf(NeonMagenta, FlameNeonOrange)),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🐱", fontSize = 52.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "KEON WEB (LAKABI: KEDİ)",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = NeonMagenta
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(NeonCyan.copy(alpha = 0.15f))
                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = "ID: com.kasimcamurcu.keon.web",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.5.dp, NeonMagenta.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📲 Bağımsız Tarayıcı İndirme Paneli",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Keon AI içerisinden web tarayıcı tamamen kaldırılmıştır. Tüm tarayıcı özellikleri, Soğan (Tor) şifrelemesi, Dark Web (.onion) desteği ve KEDİ Hata Düzeltici 'Keon Web' bağımsız uygulamasına taşınmıştır.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = { openOrDownloadKeonWeb() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "KEON WEB'İ TELEFONA İNDİR / AÇ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
