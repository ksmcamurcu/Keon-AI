package com.example.ui.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.ui.components.canDrawOverlaysPermission
import com.example.ui.components.requestOverlayPermission
import com.example.ui.components.isAccessibilityServiceEnabled
import com.example.ui.components.requestAccessibilityPermission
import com.example.service.KeonFloatingCubeService
import com.example.ui.KeonUserAccount
import com.example.ui.components.RobotCaptchaCard
import com.example.widget.KeonChatWidgetProvider
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.components.KeonLogoStyle
import com.example.ui.components.KeonLogoView
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TechThemeId
import com.example.ui.theme.TechWallpaperId
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsTab(
    selectedLanguage: String,
    isNotificationsEnabled: Boolean,
    isSoundEffectsEnabled: Boolean,
    isAutoBackupEnabled: Boolean,
    allocatedGb: Int,
    isApiKeyConfigured: Boolean,
    selectedTheme: TechThemeId = TechThemeId.CYBER_NEON,
    selectedNeonAccent: com.example.ui.theme.NeonAccentColor = com.example.ui.theme.NeonAccentColor.NEON_CYAN_BLUE,
    activePackage: com.example.ui.theme.KeonPackage? = com.example.ui.theme.KeonPackage.AURA,
    selectedWallpaper: TechWallpaperId = TechWallpaperId.CYBER_GRID,
    wallpaperAlpha: Float = 0.40f,
    selectedLogoStyle: KeonLogoStyle = KeonLogoStyle.CYBER_CUBE_3D,
    isDemoModeEnabled: Boolean = true,
    isAdmin: Boolean = false,
    adminPhoneNumber: String = "",
    adminAuthError: String? = null,
    isHighAccessibilityEnabled: Boolean = false,
    isHighContrastNeonEnabled: Boolean = false,
    isDarkModeEnabled: Boolean = true,
    isAmoledModeEnabled: Boolean = false,
    isEyeProtectionNightFilter: Boolean = false,
    isFloating3DCubeEnabled: Boolean = false,
    fontSizeScale: Float = 1.0f,
    onLanguageSelect: (String) -> Unit,
    onToggleNotifications: () -> Unit,
    onToggleSoundEffects: () -> Unit,
    onToggleAutoBackup: () -> Unit,
    onSetAllocatedGb: (Int) -> Unit,
    onClearChatHistory: () -> Unit,
    onOptimizeStorage: () -> Unit,
    onToggleDarkMode: (Boolean) -> Unit = {},
    onToggleAmoledMode: (Boolean) -> Unit = {},
    onToggleEyeProtectionFilter: (Boolean) -> Unit = {},
    onSetDarkModePreset: (String) -> Unit = {},
    onSelectTheme: (TechThemeId) -> Unit = {},
    onSelectNeonAccent: (com.example.ui.theme.NeonAccentColor) -> Unit = {},
    onSelectPackage: (com.example.ui.theme.KeonPackage) -> Unit = {},
    onSelectWallpaper: (TechWallpaperId) -> Unit = {},
    onSetWallpaperAlpha: (Float) -> Unit = {},
    onSelectLogoStyle: (KeonLogoStyle) -> Unit = {},
    onToggleDemoMode: (Boolean) -> Unit = {},
    onVerifyAdminPhone: (String) -> Unit = {},
    onRevokeAdmin: () -> Unit = {},
    onToggleHighAccessibility: (Boolean) -> Unit = {},
    onToggleHighContrastNeon: (Boolean) -> Unit = {},
    onToggleFloating3DCube: (Boolean) -> Unit = {},
    onSetFontSizeScale: (Float) -> Unit = {},
    onOpenVoiceAssistant: (() -> Unit)? = null,
    selectedVoiceProfile: com.example.data.KeonVoiceProfile = com.example.data.KeonVoiceProfile.SMOOTH_MALE_BARITONE,
    voicePitch: Float = 0.83f,
    voiceSpeechRate: Float = 0.97f,
    isAudioAnalyzing: Boolean = false,
    lastAudioAnalysisResult: String? = null,
    onSelectVoiceProfile: (com.example.data.KeonVoiceProfile) -> Unit = {},
    onSetVoicePitch: (Float) -> Unit = {},
    onSetVoiceSpeechRate: (Float) -> Unit = {},
    onAnalyzeAudioAndCalibrateVoice: (String, Int) -> Unit = { _, _ -> },
    onClearAudioAnalysisResult: () -> Unit = {},
    currentUserAccount: KeonUserAccount? = null,
    userAuthError: String? = null,
    isVipApprovedByAdmin: Boolean = false,
    vipPackageError: String? = null,
    onRegisterUser: (name: String, contactInput: String, password: String, passwordConfirm: String, isEmail: Boolean) -> Boolean = { _, _, _, _, _ -> true },
    onLoginUser: (contactInput: String, password: String) -> Boolean = { _, _ -> true },
    onLogoutUser: () -> Unit = {},
    onClearAuthError: () -> Unit = {},
    onToggleVipApprovalByAdmin: (Boolean) -> Unit = {},
    onClearVipPackageError: () -> Unit = {},
    onRequestVipPackage: (com.example.ui.theme.KeonPackage) -> Unit = {},
    onClearUserNotification: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showOptimizedToast by remember { mutableStateOf(false) }
    var phoneInput by remember { mutableStateOf("") }

    val languagesList = listOf(
        "Türkçe" to "[TR]",
        "English" to "[EN]",
        "Deutsch" to "[DE]",
        "Español" to "[ES]",
        "Français" to "[FR]",
        "Русский" to "[RU]",
        "العربية" to "[AR]",
        "中文" to "[ZH]",
        "日本語" to "[JA]",
        "한국어" to "[KO]"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Section Header
        Text(
            text = "UYGULAMA AYARLARI",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // TELEFON BİLDİRİMİ / VIP TALEBİ BİLDİRİM BANNERI (Kullanıcı Telefonuna Gelen Bildirim)
        if (!currentUserAccount?.userNotificationMessage.isNullOrEmpty() || currentUserAccount?.vipRequestStatus == "REJECTED" || currentUserAccount?.vipRequestStatus == "APPROVED") {
            val isRejected = currentUserAccount?.vipRequestStatus == "REJECTED" || currentUserAccount?.userNotificationMessage?.contains("REDDEDİLDİ") == true
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isRejected) Color(0xFF330914) else Color(0xFF072B1A)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = if (isRejected) Color(0xFFFF3366) else NeonGreen
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isRejected) Icons.Default.Cancel else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isRejected) Color(0xFFFF3366) else NeonGreen,
                            modifier = Modifier.size(26.dp)
                        )
                        Column {
                            Text(
                                text = "📱 TELEFONUNUZA GELEN BİLDİRİM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isRejected) Color(0xFFFF88AA) else NeonGreen
                            )
                            Text(
                                text = currentUserAccount?.userNotificationMessage
                                    ?: if (isRejected) "📱 Telefonunuza gelen bildirim: ❌ VIP Paket Talebiniz Administrator Tarafından REDDEDİLDİ."
                                    else "📱 Telefonunuza gelen bildirim: 🎉 VIP Paket Talebiniz Administrator Tarafından ONAYLANDI!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = { onClearUserNotification() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 00. KULLANICI HESABI & KAYIT OLMA (E-POSTA / TELEFON)
        UserAccountSection(
            currentUserAccount = currentUserAccount,
            userAuthError = userAuthError,
            activePackage = activePackage,
            onRegisterUser = onRegisterUser,
            onLoginUser = onLoginUser,
            onLogoutUser = onLogoutUser,
            onClearAuthError = onClearAuthError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📱 TELEFON ANA EKRANI CHAT WIDGET'I (HOME SCREEN LAUNCHER WIDGET)
        val context = androidx.compose.ui.platform.LocalContext.current
        SettingsCategoryCard(
            title = "📱 Telefon Ana Ekranı Chat Widget'ı",
            icon = Icons.Default.Widgets
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Keon AI Chat Widget'ını doğrudan telefonunuzun ana ekranına (Launcher) ekleyebilirsiniz. Uygulamayı açmaya gerek kalmadan telefonunuzun ekranından tek tıkla yapay zekaya soru sorabilir veya sesli sohbet başlatabilirsiniz:",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                // Interactive Live Preview Box of the Home Screen Widget
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF120E29))
                        .border(1.5.dp, NeonCyan, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🤖 ", fontSize = 16.sp)
                                Text("KEON AI CHAT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🟢 CANLI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF181335))
                                    .border(1.dp, Color(0xFF3D326F), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Ana Ekran Widget", fontSize = 8.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Search/Input prompt box mock
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E193B))
                                .border(1.dp, NeonCyan, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💬 Sormak istediğin soruyu yaz veya dokun...",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text("Sor ➔", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Quick buttons row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF181335))
                                    .border(1.dp, Color(0xFF3D326F), RoundedCornerShape(8.dp))
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💡 Motivasyon", fontSize = 9.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF181335))
                                    .border(1.dp, Color(0xFF3D326F), RoundedCornerShape(8.dp))
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⚡ Özet Sun", fontSize = 9.sp, color = NeonMagenta, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E193B))
                                    .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎙️ Sesli Chat", fontSize = 9.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val pinned = KeonChatWidgetProvider.pinWidgetToHomeScreen(context)
                        if (pinned) {
                            android.widget.Toast.makeText(
                                context,
                                "📲 Telefon ana ekranına widget ekleme isteği gönderildi!",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else {
                            android.widget.Toast.makeText(
                                context,
                                "📌 Ana ekranınızda boş bir yere uzun basıp 'Widget'lar' menüsünden 'Keon AI Chat Widget'ı seçebilirsiniz.",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("📌 WİDGET'I TELEFON ANA EKRANINA EKLE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 0. DEMO MODU & ADMINISTRATOR AYARLARI
        SettingsCategoryCard(title = "Demo Modu & Administrator Ayarları", icon = Icons.Default.AdminPanelSettings) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Demo Modu",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isDemoModeEnabled) "Demo modu tüm kullanıcılar için aktif" else "Demo modu kapalı",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = isDemoModeEnabled,
                    onCheckedChange = { onToggleDemoMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NeonCyan
                    )
                )
            }

            if (isDemoModeEnabled) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFF1E1E2C),
                                    Color(0xFF2A1B3D)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(listOf(NeonCyan, Color(0xFFFF00CC))),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "👑 ADMINISTRATOR AYARLARI",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (!isAdmin) {
                            Text(
                                text = "Sadece yetkili Administrator erişebilir. Giriş yapmak için özel Administrator Kodunu giriniz:",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = { Text("Administrator Kodu (örn: +** *** *** ** ** ##### #####)", fontSize = 11.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedLabelColor = NeonCyan,
                                    cursorColor = NeonCyan,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (adminAuthError != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = adminAuthError,
                                    color = Color(0xFFFF3366),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { onVerifyAdminPhone(phoneInput) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "ADMINISTRATOR GİRİŞİ YAP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        } else {
                            // Active Admin view
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF00FF88).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF00FF88), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = null,
                                            tint = Color(0xFF00FF88),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "AKTİF TEK ADMINISTRATOR",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF00FF88)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Kayıtlı Tel: $adminPhoneNumber",
                                        fontSize = 11.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "⚡ Tüm sistem konsolu, VIP parametreler ve geliştirici ayrıcalıkları aktif.",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Administrator VIP Approval Control Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(DarkSurface, RoundedCornerShape(10.dp))
                                            .border(1.dp, if (isVipApprovedByAdmin) NeonGreen else NeonCyan, RoundedCornerShape(10.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "👑 Kayıtlı Kullanıcılara VIP Paket İzni",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = if (isVipApprovedByAdmin) "✅ VIP Geçiş İzni VERİLDİ" else "🔒 VIP Geçiş İzni BEKLEMEDE",
                                                fontSize = 10.sp,
                                                color = if (isVipApprovedByAdmin) NeonGreen else Color.Yellow,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Switch(
                                            checked = isVipApprovedByAdmin,
                                            onCheckedChange = { onToggleVipApprovalByAdmin(it) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.Black,
                                                checkedTrackColor = NeonGreen
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { onRevokeAdmin() },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, Color(0xFFFF3366)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "ADMINISTRATOR OTURUMUNU KAPAT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF3366)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 0.4. 🎲 EKRANIN 1/20'SİNİ KAPLAYAN 3D KÜP ASİSTANI (BAŞKA UYGULAMALAR ÜZERİNDE GÖRÜNTÜLENME & SESLİ SOHBET)
        SettingsCategoryCard(
            title = "🎲 1/20 Ekran 3D Küp Sesli Asistanı",
            icon = Icons.Default.Psychology
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Erişilebilirlik özelliği: Telefon ekranının sadece 20'de 1'ini kaplayan 3D Küp Asistanı ile başka herhangi bir uygulama açıkken ekran üzerinde sesli olarak sohbet edebilir ve istek belirtebilirsiniz.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                // Master Toggle Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF161233))
                        .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🎲 3D Küp Asistanını Aktif Et",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isFloating3DCubeEnabled) "🟢 3D Küp Ekran Üzerinde Canlı" else "⚪ 3D Küp Asistanı Kapalı",
                            fontSize = 10.sp,
                            color = if (isFloating3DCubeEnabled) NeonGreen else TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Switch(
                        checked = isFloating3DCubeEnabled,
                        onCheckedChange = { enabled ->
                            onToggleFloating3DCube(enabled)
                            if (enabled) {
                                if (!canDrawOverlaysPermission(context)) {
                                    requestOverlayPermission(context)
                                } else if (!isAccessibilityServiceEnabled(context)) {
                                    requestAccessibilityPermission(context)
                                }
                                KeonFloatingCubeService.startService(context)
                            } else {
                                KeonFloatingCubeService.stopService(context)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = NeonGreen
                        )
                    )
                }

                // Required System Permissions Box (Overlay)
                val hasOverlayPerm = canDrawOverlaysPermission(context)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (hasOverlayPerm) Color(0xFF072B1A) else Color(0xFF331309))
                        .border(1.dp, if (hasOverlayPerm) NeonGreen else Color(0xFFFF9900), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (hasOverlayPerm) Icons.Default.CheckCircle else Icons.Default.Security,
                                    contentDescription = null,
                                    tint = if (hasOverlayPerm) NeonGreen else Color(0xFFFF9900),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "1. Başka Uygulamalar Üzerinde Görüntülenme İzni",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasOverlayPerm) NeonGreen else Color(0xFFFF9900)
                                )
                            }
                        }

                        Text(
                            text = if (hasOverlayPerm) "✅ Sistem izinleri tanımlandı. Başka uygulamalar açıkken 3D küp görünür."
                            else "⚠️ 3D Küpün başka uygulamaların üstünde durabilmesi için 'Başka uygulamalar üzerinde görüntülenme' izni vermelisiniz.",
                            fontSize = 10.sp,
                            color = Color.White
                        )

                        if (!hasOverlayPerm) {
                            Button(
                                onClick = { requestOverlayPermission(context) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("📌 SİSTEM GÖRÜNTÜLENME İZNİ VER", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Accessibility Service Permission Box
                val hasAccessibPerm = isAccessibilityServiceEnabled(context)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (hasAccessibPerm) Color(0xFF072B1A) else Color(0xFF1E143B))
                        .border(1.dp, if (hasAccessibPerm) NeonGreen else NeonCyan, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (hasAccessibPerm) Icons.Default.CheckCircle else Icons.Default.AccessibilityNew,
                                    contentDescription = null,
                                    tint = if (hasAccessibPerm) NeonGreen else NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "2. Erişilebilirlik Hizmeti İzni (Keon AI 3D Küp)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasAccessibPerm) NeonGreen else NeonCyan
                                )
                            }
                        }

                        Text(
                            text = if (hasAccessibPerm) "✅ Erişilebilirlik Hizmeti aktif. Keon AI 3D Küp asistanınız otomatik çalışır."
                            else "ℹ️ Erişilebilirlik Hizmeti ile 3D Küp, sistem geneli erişilebilirlik asistanı olarak ekran üstünde kesintisiz çalışır.",
                            fontSize = 10.sp,
                            color = Color.White
                        )

                        Button(
                            onClick = { requestAccessibilityPermission(context) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = if (hasAccessibPerm) Color(0xFF16382B) else NeonCyan),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessibilityNew,
                                contentDescription = null,
                                tint = if (hasAccessibPerm) NeonGreen else Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (hasAccessibPerm) "⚙️ ERİŞİLEBİLİRLİK AYARLARINI AÇ (AKTİF)" else "⚡ ERİŞİLEBİLİRLİK İZNİ VER (AYARLARI AÇ)",
                                fontSize = 11.sp,
                                color = if (hasAccessibPerm) NeonGreen else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Voice Microphone Permission Status Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF141129))
                        .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("3. Sadece Sesli Konuşma & İstek İzni", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                        Text(
                            text = "3D Küp dokunulduğunda doğrudan sesli olarak sizi dinler, isteklerinizi alır ve sesli yanıt verir.",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Interactive Demo Action Box
                if (isFloating3DCubeEnabled) {
                    Button(
                        onClick = {
                            if (canDrawOverlaysPermission(context)) {
                                KeonFloatingCubeService.startService(context)
                                android.widget.Toast.makeText(
                                    context,
                                    "🎲 3D Küp Servisi Başlatıldı! Ekranınızda 1/20 boyutunda görünecektir.",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            } else {
                                requestOverlayPermission(context)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("⚡ 3D KÜPÜ EKRANA GETİR / YENİDEN BAŞLAT", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 0.4. KARANLIK ARAYÜZ VE GECE MODU AYARLARI
        SettingsCategoryCard(title = "🌙 Karanlık Arayüz & Gece Modu", icon = Icons.Default.DarkMode) {
            // Ana Karanlık Mod Switch'i
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Karanlık Arayüz Modu",
                            fontSize = (13 * fontSizeScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(if (isDarkModeEnabled) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .border(0.8.dp, if (isDarkModeEnabled) Color(0xFF00E5FF) else Color.Gray, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isDarkModeEnabled) "AKTİF" else "KAPALI",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkModeEnabled) Color(0xFF00E5FF) else Color.LightGray
                            )
                        }
                    }
                    Text(
                        text = "Göz yorgunluğunu önleyen derin karanlık arayüz ve gece teması.",
                        fontSize = (11 * fontSizeScale).sp,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = isDarkModeEnabled,
                    onCheckedChange = { onToggleDarkMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = Color(0xFF00E5FF)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AMOLED Saf Siyah Modu Switch'i
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🌑 AMOLED Saf Zifiri Siyah (%0 Pil)",
                            fontSize = (13 * fontSizeScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF00FF88).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .border(0.8.dp, Color(0xFF00FF88), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PİL TASARRUFU",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF88)
                            )
                        }
                    }
                    Text(
                        text = "OLED/AMOLED ekranlarda pikselleri tamamen kapatarak zifiri siyah arayüz ve maksimum pil tasarrufu sağlar.",
                        fontSize = (11 * fontSizeScale).sp,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = isAmoledModeEnabled || selectedTheme == TechThemeId.AMOLED_PITCH_BLACK,
                    onCheckedChange = { onToggleAmoledMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = Color(0xFF00FF88)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gece Göz Koruma & Karartma Filtresi Switch'i
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🛡️ Gece Göz Koruma & Karartma",
                        fontSize = (13 * fontSizeScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Mavi ışığı ve parlaklığı yumuşatarak gece kullanımında göz konforunu artırır.",
                        fontSize = (11 * fontSizeScale).sp,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = isEyeProtectionNightFilter,
                    onCheckedChange = { onToggleEyeProtectionFilter(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = Color(0xFFFFB300)
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Hızlı Karanlık Arayüz Stili Seçin:",
                fontSize = (12 * fontSizeScale).sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            // 4 Adet Hızlı Karanlık Arayüz Önayarı Butonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val darkPresets = listOf(
                    Triple("amoled", "🌑 AMOLED", TechThemeId.AMOLED_PITCH_BLACK),
                    Triple("midnight", "🌌 Gece", TechThemeId.MIDNIGHT_DARK),
                    Triple("stealth", "🕶️ Karbon", TechThemeId.STEALTH_CARBON),
                    Triple("neon", "⚡ Neon", TechThemeId.CYBER_NEON)
                )

                darkPresets.forEach { (presetKey, label, theme) ->
                    val isSelected = selectedTheme == theme
                    Button(
                        onClick = { onSetDarkModePreset(presetKey) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF00E5FF) else DarkSurface
                        ),
                        border = BorderStroke(1.dp, if (isSelected) Color.White else Color.Gray.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 2.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            color = if (isSelected) Color.Black else TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Karanlık Mod Bilgi Kartı
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF06060A), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Nightlight,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Aktif Karanlık Mod: ${selectedTheme.displayName}",
                            fontSize = (11 * fontSizeScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF)
                        )
                        Text(
                            text = "Karanlık arayüz tüm menülerde, sohbet ekranında ve oyunlarda kusursuz çalışır.",
                            fontSize = (10 * fontSizeScale).sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 0.5. YÜKSEK ERİŞİLEBİLİRLİK MODU & KONTRAST AYARLARI
        SettingsCategoryCard(title = "Yüksek Erişilebilirlik & Neon Kontrast", icon = Icons.Default.AccessibilityNew) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Yüksek Erişilebilirlik Modu",
                        fontSize = (13 * fontSizeScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Neon renklerin okunabilirliğini artırır, metin kontrastını güçlendirir.",
                        fontSize = (11 * fontSizeScale).sp,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = isHighAccessibilityEnabled,
                    onCheckedChange = { onToggleHighAccessibility(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NeonCyan
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Neon Koyu Kontrast Paneli",
                        fontSize = (13 * fontSizeScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Neon metinler arkasına mat koyu kontrast tabakası ekleyerek parlak renklerde %100 netlik sağlar.",
                        fontSize = (11 * fontSizeScale).sp,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = isHighContrastNeonEnabled,
                    onCheckedChange = { onToggleHighContrastNeon(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = Color(0xFF00FF88)
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Yazı Tipi Boyutu & Ölçeği",
                fontSize = (13 * fontSizeScale).sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val scales = listOf(
                    1.0f to "1.0x Standard",
                    1.15f to "1.15x Büyük",
                    1.30f to "1.30x Çok Büyük"
                )

                scales.forEach { (scale, label) ->
                    val isSelected = (fontSizeScale == scale)
                    Button(
                        onClick = { onSetFontSizeScale(scale) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) NeonCyan else DarkSurface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canlı Önizleme Kartı
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (isHighContrastNeonEnabled) Color(0xFF030308) else Color(0xFF161622),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = if (isHighAccessibilityEnabled) 2.dp else 1.dp,
                        color = if (isHighAccessibilityEnabled) NeonCyan else Color.Gray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CANLI KONTRAST & METİN ÖNİZLEMESİ",
                            fontSize = (11 * fontSizeScale).sp,
                            fontWeight = FontWeight.Black,
                            color = if (isHighAccessibilityEnabled) Color.White else NeonCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Örnek Neon Metin: Kuantum Çıkarım Motoru Hazır",
                        fontSize = (12 * fontSizeScale).sp,
                        fontWeight = if (isHighAccessibilityEnabled) FontWeight.Bold else FontWeight.Normal,
                        color = if (isHighContrastNeonEnabled) Color(0xFF00FF88) else Color(0xFF00E5FF)
                    )
                    Text(
                        text = "Göz yorulmasını engelleyen yüksek kontrast ve ayarlanabilir yazı boyutu aktif.",
                        fontSize = (10 * fontSizeScale).sp,
                        color = if (isHighAccessibilityEnabled) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 0. KEON AI PAKETLER & ABONELİKLER
        SettingsCategoryCard(title = "Keon AI Paketler & Abonelikler", icon = Icons.Default.AutoAwesome) {
            Text(
                text = "🌱 Ücretsiz Plan & VIP Paketler",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "İhtiyacınıza uygun Kuantum AI paketini seçin ve avantajların keyfini çıkarın.",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // User Registration & Admin Approval Status Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Registration status row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (currentUserAccount != null) Icons.Default.CheckCircle else Icons.Default.Person,
                        contentDescription = null,
                        tint = if (currentUserAccount != null) NeonGreen else Color.Yellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentUserAccount != null)
                            "1. Kayıt Durumu: KAYITLI HESAP (${currentUserAccount.name})"
                        else
                            "1. Kayıt Durumu: KAYITSIZ (VIP Geçiş için Kaydolun)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentUserAccount != null) NeonGreen else Color.Yellow
                    )
                }

                // Administrator permission status row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = if (isAdmin || isVipApprovedByAdmin) NeonGreen else Color(0xFFFF9900),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAdmin)
                            "2. Administrator İzni: TAM YETKİLİ (Admin Girişi)"
                        else if (isVipApprovedByAdmin)
                            "2. Administrator İzni: ONAYLANDI (VIP Kullanabilir)"
                        else
                            "2. Administrator İzni: BEKLEMEDE (Admin Onayı Gerekli)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAdmin || isVipApprovedByAdmin) NeonGreen else Color(0xFFFF9900)
                    )
                }
            }

            if (vipPackageError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF380812), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFFF3366), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = vipPackageError,
                                color = Color(0xFFFF6688),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { onClearVipPackageError() },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Kapat",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        if (!isAdmin && !isVipApprovedByAdmin) {
                            Button(
                                onClick = {
                                    onRequestVipPackage(com.example.ui.theme.KeonPackage.AURA)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "👑 Administrator'a VIP Talebi Gönder (Bildirim)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3 Packages List
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                com.example.ui.theme.KeonPackage.values().forEach { pkg ->
                    val isSelected = activePackage == pkg

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(pkg.primaryColor, pkg.secondaryColor)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelectPackage(pkg) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) pkg.primaryColor.copy(alpha = 0.18f) else DarkSurface
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Package Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                                    listOf(pkg.primaryColor, pkg.secondaryColor)
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = pkg.badge,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = pkg.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                Text(
                                    text = pkg.price,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = pkg.primaryColor
                                )
                            }

                            Text(
                                text = pkg.description,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 6.dp, bottom = 10.dp)
                            )

                            // Features List
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                pkg.features.forEach { feature ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "• ",
                                            color = pkg.primaryColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = feature,
                                            color = TextPrimary.copy(alpha = 0.9f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Activation Button
                            Button(
                                onClick = { onSelectPackage(pkg) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) pkg.primaryColor else pkg.primaryColor.copy(alpha = 0.25f)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isSelected) "✓ AKTİF PAKET" else "PAKETİ SEÇ VE ETKİNLEŞTİR",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else pkg.primaryColor
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. CANLI TEKNOLOJİ TEMALARI VE DUVAR KAĞITLARI
        SettingsCategoryCard(title = "Canlı Teknoloji Temaları & Duvar Kağıtları", icon = Icons.Default.Palette) {
            Text(
                text = "⚡ Neon Vurgu Rengi Özelleştirme",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Neon Accent Color Selector Options
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                com.example.ui.theme.NeonAccentColor.values().forEach { accent ->
                    val isSelected = selectedNeonAccent == accent
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) accent.primaryNeon.copy(alpha = 0.22f) else DarkSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) accent.primaryNeon else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectNeonAccent(accent) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Glowing Dual Color Circle
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                            listOf(accent.primaryNeon, accent.secondaryNeon)
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(1.2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = accent.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) accent.primaryNeon else TextPrimary
                                )
                                Text(
                                    text = accent.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Seçildi",
                                tint = accent.primaryNeon,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Uygulama Renk Teması Seçin",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Theme Options Row
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TechThemeId.values().forEach { theme ->
                    val isSelected = selectedTheme == theme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) theme.primary.copy(alpha = 0.25f) else DarkSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) theme.primary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectTheme(theme) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Color Dots
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(theme.primary, CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(theme.secondary, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = theme.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) theme.primary else TextPrimary
                                )
                                Text(
                                    text = theme.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = theme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Canlı Teknoloji Duvar Kağıdı",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Wallpaper Options
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TechWallpaperId.values().forEach { wallpaper ->
                    val isSelected = selectedWallpaper == wallpaper
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) selectedTheme.primary.copy(alpha = 0.2f) else DarkSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) selectedTheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectWallpaper(wallpaper) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = wallpaper.displayName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) selectedTheme.primary else TextPrimary
                            )
                            Text(
                                text = wallpaper.description,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = selectedTheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Wallpaper Opacity Slider
            Text(
                text = "Duvar Kağıdı Saydamlığı: ${(wallpaperAlpha * 100).toInt()}%",
                fontSize = 12.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )

            Slider(
                value = wallpaperAlpha,
                onValueChange = { onSetWallpaperAlpha(it) },
                valueRange = 0.1f..0.8f,
                colors = SliderDefaults.colors(
                    thumbColor = selectedTheme.primary,
                    activeTrackColor = selectedTheme.secondary,
                    inactiveTrackColor = DarkSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. KEON AI SIMGE VE LOGO VERSİYONLARI
        SettingsCategoryCard(title = "Keon AI Simge & Logo Versiyonları", icon = Icons.Default.AutoAwesome) {
            Text(
                text = "Ana Ekran & Başlık Logosunu Seçin:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                KeonLogoStyle.values().forEach { style ->
                    val isSelected = selectedLogoStyle == style
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) selectedTheme.primary.copy(alpha = 0.22f) else DarkSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) selectedTheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectLogoStyle(style) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Animated Live Preview of the logo style
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                KeonLogoView(
                                    logoStyle = style,
                                    size = 46.dp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = style.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) selectedTheme.primary else TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "[${style.tag}]",
                                        fontSize = 9.sp,
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = style.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 2
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Seçildi",
                                tint = selectedTheme.primary,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))


        // 1. DİL VE YAPAY ZEKA AYARLARI
        SettingsCategoryCard(title = "Yapay Zeka & Dil", icon = Icons.Default.Language) {
            // Select Language Row
            SettingsClickableRow(
                icon = Icons.Default.Language,
                title = "Uygulama & Öğretim Dili",
                subtitle = "Şu anki dil: $selectedLanguage",
                testTag = "language_setting_row",
                onClick = { showLanguageDialog = true }
            )

            // Voice Feedback / Sound Effects Toggle
            SettingsSwitchRow(
                icon = Icons.Default.VolumeUp,
                title = "Sesli Yanıt / Efektler",
                subtitle = "Keon AI etkileşim ve buton sesleri",
                checked = isSoundEffectsEnabled,
                testTag = "sound_effects_switch",
                onCheckedChange = { onToggleSoundEffects() }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. BİLDİRİM VE YEDEKLEME
        SettingsCategoryCard(title = "Bildirim & Bulut", icon = Icons.Default.Notifications) {
            SettingsSwitchRow(
                icon = Icons.Default.Notifications,
                title = "Uygulama Bildirimleri",
                subtitle = "Sanal depolama ve yapay zeka güncellemeleri",
                checked = isNotificationsEnabled,
                testTag = "notifications_switch",
                onCheckedChange = { onToggleNotifications() }
            )

            SettingsSwitchRow(
                icon = Icons.Default.Storage,
                title = "Otomatik Bulut Sync",
                subtitle = "60GB Sanal Drive ile arka plan eşitlemesi",
                checked = isAutoBackupEnabled,
                testTag = "auto_backup_switch",
                onCheckedChange = { onToggleAutoBackup() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Storage Limit Slider
            Text(
                text = "Sanal Disk Kapasite Sınırı: $allocatedGb GB",
                fontSize = 13.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )

            Slider(
                value = allocatedGb.toFloat(),
                onValueChange = { onSetAllocatedGb(it.toInt()) },
                valueRange = 10f..60f,
                steps = 9,
                colors = SliderDefaults.colors(
                    thumbColor = NeonCyan,
                    activeTrackColor = NeonBlue,
                    inactiveTrackColor = DarkSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("storage_limit_slider")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. VERİTABANI VE PERFORMANS
        SettingsCategoryCard(title = "Veritabanı & Temizlik", icon = Icons.Default.Delete) {
            // Storage Optimization
            SettingsClickableRow(
                icon = Icons.Default.Refresh,
                title = "Telefon Önbelleğini Temizle",
                subtitle = "Gereksiz geçici verileri silerek alan açar",
                testTag = "optimize_storage_row",
                onClick = {
                    onOptimizeStorage()
                    showOptimizedToast = true
                }
            )

            AnimatedVisibility(visible = showOptimizedToast) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(NeonGreen.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Önbellek temizlendi ve sanal alan optimize edildi!",
                        fontSize = 12.sp,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Clear Chat History
            SettingsClickableRow(
                icon = Icons.Default.Delete,
                title = "Sohbet Geçmişini Sıfırla",
                subtitle = "Yerel Room veritabanındaki tüm mesajları siler",
                titleColor = NeonMagenta,
                testTag = "clear_chat_row",
                onClick = { showClearConfirmDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. API DİAGNOSTİK VE HAKKINDA
        SettingsCategoryCard(title = "Sistem & Hakkında", icon = Icons.Default.Info) {
            // API Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isApiKeyConfigured) NeonGreen else NeonCyan,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Keon Engine Bağlantı Durumu",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isApiKeyConfigured) "Bulut Sunucularından Doğrulandı (Aktif)" else "Yerel Akıllı Motor Aktif (Offline Mode)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (isApiKeyConfigured) NeonGreen.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isApiKeyConfigured) "ONLINE" else "LOCAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isApiKeyConfigured) NeonGreen else NeonCyan
                    )
                }
            }

            // 🎙️ SESLİ ASİSTAN MODU, SES DOSYASI ANALİZİ VE SES KALİBRASYON AYARLARI
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF2A1B3D), Color(0xFF13111C))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(listOf(NeonMagenta, NeonCyan)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Sesli Asistan",
                                tint = NeonMagenta,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "🎙️ KEON AI SES MOTORU & SES ANALİZİ",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonMagenta
                                )
                                Text(
                                    text = "Ses dosyası spektrogram analizi ve TTS perde/hız optimizasyonu",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. SES DOSYASI ANALİZİ & OTOMATİK SES KALİBRASYON KARTI
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F0C24), RoundedCornerShape(12.dp))
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "🎵 Ses Dosyası Analizi & Eşitleme",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }

                                if (isAudioAnalyzing) {
                                    Text(
                                        text = "⚡ Analiz Ediliyor...",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFCC00)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Kullanıcı tarafından sağlanan ses kaydı frekans ve rezonans taramasından geçirilerek Keon AI'nın konuşma tonu, derinliği ve hızı birebir optimize edilir.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )

                            if (lastAudioAnalysisResult != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF071F18), RoundedCornerShape(8.dp))
                                        .border(0.8.dp, NeonGreen, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = lastAudioAnalysisResult,
                                        fontSize = 11.sp,
                                        color = NeonGreen,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onAnalyzeAudioAndCalibrateVoice("kullanici_sesi.mp3", 12)
                                    },
                                    enabled = !isAudioAnalyzing,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isAudioAnalyzing) "Analiz Ediliyor..." else "🎙️ Sesi Analiz Et & Uygula",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. SES TONU PROFİLİ SEÇİMİ
                    Text(
                        text = "Keon AI Ses Tonu Profili:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        com.example.data.KeonVoiceProfile.values().forEach { profile ->
                            val isSelected = selectedVoiceProfile == profile
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) NeonMagenta.copy(alpha = 0.22f) else DarkSurface)
                                    .border(
                                        width = if (isSelected) 1.2.dp else 0.5.dp,
                                        color = if (isSelected) NeonMagenta else Color.Gray.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onSelectVoiceProfile(profile) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = profile.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) NeonMagenta else TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (isSelected) NeonMagenta.copy(alpha = 0.3f) else Color.DarkGray,
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = profile.badge,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else Color.LightGray
                                            )
                                        }
                                    }
                                    Text(
                                        text = profile.description,
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Seçili",
                                        tint = NeonMagenta,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. İNCE SES AYARLARI (SLIDERLAR)
                    Text(
                        text = "🎚️ Ses Perdesi (Pitch): ${String.format(java.util.Locale.US, "%.2f", voicePitch)}x ${if (voicePitch < 0.9f) "(Tok Bariton)" else if (voicePitch > 1.1f) "(Tiz & İnce)" else "(Doğal)"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Slider(
                        value = voicePitch,
                        onValueChange = { onSetVoicePitch(it) },
                        valueRange = 0.6f..1.6f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = DarkSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "⚡ Konuşma Hızı (Rate): ${String.format(java.util.Locale.US, "%.2f", voiceSpeechRate)}x",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Slider(
                        value = voiceSpeechRate,
                        onValueChange = { onSetVoiceSpeechRate(it) },
                        valueRange = 0.6f..1.6f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonMagenta,
                            activeTrackColor = NeonMagenta,
                            inactiveTrackColor = DarkSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onOpenVoiceAssistant?.invoke() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SESLİ ASİSTAN MODUNU BAŞLAT & DENE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // App Version Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = NeonMagenta,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Keon AI Engine v2.5.0 Pro",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "VirtualBox Cloud Engine & Polyglot AI Suite",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Language Selection Modal Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = "Öğretim & Uygulama Dili Seçin",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    languagesList.forEach { (lang, flag) ->
                        val isSelected = selectedLanguage == lang
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable {
                                    onLanguageSelect(lang)
                                    showLanguageDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = lang,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) NeonCyan else TextPrimary
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Kapat", color = NeonCyan)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Clear Chat Confirm Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text("Sohbeti Sıfırla", color = NeonMagenta, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Tüm sohbet geçmişiniz kalıcı olarak silinecek. Emin misiniz?",
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearChatHistory()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
                ) {
                    Text("Evet, Sil", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("İptal", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun SettingsCategoryCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue
                )
            }
            content()
        }
    }
}

@Composable
fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    titleColor: Color = TextPrimary,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = titleColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = NeonCyan,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = DarkSurface
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
fun UserAccountSection(
    currentUserAccount: KeonUserAccount?,
    userAuthError: String?,
    activePackage: com.example.ui.theme.KeonPackage?,
    onRegisterUser: (name: String, contactInput: String, password: String, passwordConfirm: String, isEmail: Boolean) -> Boolean,
    onLoginUser: (contactInput: String, password: String) -> Boolean,
    onLogoutUser: () -> Unit,
    onClearAuthError: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isLoginMode by remember { mutableStateOf(false) } // false = Register, true = Login
    var isEmailType by remember { mutableStateOf(true) } // true = Email, false = Phone
    var nameInput by remember { mutableStateOf("") }
    var contactInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordConfirmInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isNotRobotChecked by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    SettingsCategoryCard(
        title = if (currentUserAccount != null) "Kullanıcı Profil Hesabı" else "Kullanıcı Kaydı & Giriş",
        icon = Icons.Default.AccountCircle
    ) {
        if (currentUserAccount != null) {
            // LOGGED IN VIEW
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF13102C), Color(0xFF1E1742))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(NeonCyan, NeonMagenta)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // User Avatar Initial Circle
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(listOf(NeonCyan, NeonBlue))
                                )
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUserAccount.name.take(1).uppercase(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUserAccount.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Onaylı Hesap",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (currentUserAccount.isEmail) Icons.Default.Email else Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentUserAccount.contact,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "📱 Cihaz: ${currentUserAccount.deviceModel} (${currentUserAccount.deviceType})",
                                fontSize = 11.sp,
                                color = NeonCyan,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "🟢 Canlı Oturum: ${currentUserAccount.sessionToken} • 📍 ${currentUserAccount.location}",
                                fontSize = 10.sp,
                                color = NeonGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Active Plan Badge
                        val planBadge = activePackage?.badge ?: "🌱 ÜCRETSİZ"
                        Box(
                            modifier = Modifier
                                .background(DarkSurface, RoundedCornerShape(10.dp))
                                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Aktif Paket: $planBadge",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }

                        OutlinedButton(
                            onClick = { onLogoutUser() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF4466)),
                            border = BorderStroke(1.dp, Color(0xFFFF4466).copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Çıkış Yap", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // REGISTRATION & LOGIN FORM
            Column(modifier = Modifier.fillMaxWidth()) {
                // Toggle Mode Selector (Register vs Login)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isLoginMode) NeonCyan else Color.Transparent)
                            .clickable {
                                isLoginMode = false
                                onClearAuthError()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✍️ KAYDOL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isLoginMode) Color.Black else TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isLoginMode) NeonCyan else Color.Transparent)
                            .clickable {
                                isLoginMode = true
                                onClearAuthError()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔑 GİRİŞ YAP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLoginMode) Color.Black else TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isLoginMode) {
                    // --- REGISTER FORM ---
                    // Contact Type Switcher: Email vs Phone
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                isEmailType = true
                                contactInput = ""
                                onClearAuthError()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                1.5.dp,
                                if (isEmailType) NeonCyan else Color.Gray.copy(alpha = 0.4f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isEmailType) NeonCyan.copy(alpha = 0.15f) else Color.Transparent
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = if (isEmailType) NeonCyan else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "E-Posta ile",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isEmailType) NeonCyan else TextSecondary
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                isEmailType = false
                                contactInput = ""
                                onClearAuthError()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                1.5.dp,
                                if (!isEmailType) NeonMagenta else Color.Gray.copy(alpha = 0.4f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (!isEmailType) NeonMagenta.copy(alpha = 0.15f) else Color.Transparent
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = if (!isEmailType) NeonMagenta else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Telefon ile",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isEmailType) NeonMagenta else TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Full Name Input
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Ad Soyad", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = NeonCyan,
                            cursorColor = NeonCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email or Phone Input
                    OutlinedTextField(
                        value = contactInput,
                        onValueChange = { contactInput = it },
                        label = {
                            Text(
                                if (isEmailType) "E-Posta Adresi (örn: ahmet@keon.ai)" else "Telefon Numarası (örn: 0555 123 45 67)",
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isEmailType) Icons.Default.Email else Icons.Default.Phone,
                                contentDescription = null,
                                tint = if (isEmailType) NeonCyan else NeonMagenta,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (isEmailType) KeyboardType.Email else KeyboardType.Phone
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isEmailType) NeonCyan else NeonMagenta,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = if (isEmailType) NeonCyan else NeonMagenta,
                            cursorColor = NeonCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password Input
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Şifre Belirleyin", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Şifre Göster/Gizle",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = NeonCyan,
                            cursorColor = NeonCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password Confirm Input
                    OutlinedTextField(
                        value = passwordConfirmInput,
                        onValueChange = { passwordConfirmInput = it },
                        label = { Text("Şifre Tekrarı", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = NeonCyan,
                            cursorColor = NeonCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // "Ben robot değilim" reCAPTCHA Verification Component
                    RobotCaptchaCard(
                        isVerified = isNotRobotChecked,
                        onVerifyChanged = { checked ->
                            isNotRobotChecked = checked
                            if (checked) {
                                statusMessage = "✅ Robot doğrulaması (reCAPTCHA) onaylandı."
                            } else {
                                statusMessage = null
                            }
                        }
                    )

                    if (userAuthError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "❌ $userAuthError",
                            color = Color(0xFFFF3366),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (statusMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = statusMessage!!,
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Register Action Button
                    Button(
                        onClick = {
                            if (!isNotRobotChecked) {
                                statusMessage = "❌ Lütfen kaydolmak için 'Ben robot değilim' kutusunu işaretleyiniz."
                                android.widget.Toast.makeText(
                                    context,
                                    "❌ Lütfen 'Ben robot değilim' kutusunu işaretleyiniz.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            val success = onRegisterUser(nameInput, contactInput, passwordInput, passwordConfirmInput, isEmailType)
                            if (success) {
                                statusMessage = "🎉 Tebrikler! Robot doğrulaması ile hesabınız başarıyla oluşturuldu."
                                android.widget.Toast.makeText(
                                    context,
                                    "🎉 Kaydınız başarıyla oluşturuldu!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "🚀 KAYDOL VE KEON AI'YA BAĞLAN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                } else {
                    // --- LOGIN FORM ---
                    OutlinedTextField(
                        value = contactInput,
                        onValueChange = { contactInput = it },
                        label = { Text("E-Posta Adresi veya Telefon Numarası", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = NeonCyan,
                            cursorColor = NeonCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Şifreniz", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Şifre Göster/Gizle",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = NeonCyan,
                            cursorColor = NeonCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    RobotCaptchaCard(
                        isVerified = isNotRobotChecked,
                        onVerifyChanged = { isNotRobotChecked = it },
                        title = "Giriş Güvenlik Doğrulaması (Robot Değilim)"
                    )

                    if (userAuthError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "❌ $userAuthError",
                            color = Color(0xFFFF3366),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (!isNotRobotChecked) {
                                android.widget.Toast.makeText(
                                    context,
                                    "❌ Lütfen giriş yapmak için 'Ben robot değilim' kutusunu işaretleyiniz.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            onLoginUser(contactInput, passwordInput)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "🔑 HESABIMA GİRİŞ YAP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
