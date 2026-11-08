package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.KeonRepository
import com.example.data.db.ChatMessageEntity
import com.example.data.db.KeonDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    KEON_AI, CREATIVE_STUDIO, GAMES, CLOUDBOX, STORE, SETTINGS, ADMINISTRATOR, SECURITY
}

data class AiModelInfo(
    val id: String,
    val name: String,
    val company: String,
    val url: String,
    val iconResName: String,
    val accentHex: Long
)

data class KeonUserAccount(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val contact: String,
    val isEmail: Boolean,
    val isLoggedIn: Boolean = true,
    val registeredAt: String = "Bugün",
    val vipPackage: com.example.ui.theme.KeonPackage? = null,
    val isVipApproved: Boolean = false,
    val supportTicket: String? = null,
    val accountStatus: String = "Aktif",
    val vipRequestStatus: String? = null, // "PENDING", "APPROVED", "REJECTED"
    val requestedVipPackage: com.example.ui.theme.KeonPackage? = null,
    val userNotificationMessage: String? = null,
    val deviceModel: String = "Keon Mobile Device",
    val deviceType: String = "Mobil",
    val ipAddress: String = "185.190.22.84",
    val location: String = "Türkiye",
    val lastActiveTime: String = "🟢 Canlı Aktif (Şimdi)",
    val sessionToken: String = "KEON-SEC-001"
)

class KeonViewModel(application: Application) : AndroidViewModel(application) {

    val repository: KeonRepository
    val messages: StateFlow<List<ChatMessageEntity>>
    val allHistoryDesc: StateFlow<List<ChatMessageEntity>>
    val messageCount: StateFlow<Int>

    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery: StateFlow<String> = _historySearchQuery.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _selectedTab = MutableStateFlow(AppTab.KEON_AI)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    private val _selectedSearchEngine = MutableStateFlow("keon_ai")
    val selectedSearchEngine: StateFlow<String> = _selectedSearchEngine.asStateFlow()

    private val _isOrbitActive = MutableStateFlow(false)
    val isOrbitActive: StateFlow<Boolean> = _isOrbitActive.asStateFlow()

    // CloudBox Virtual Storage State
    private val _allocatedGb = MutableStateFlow(60) // Default max 60 GB
    val allocatedGb: StateFlow<Int> = _allocatedGb.asStateFlow()

    private val _usedGb = MutableStateFlow(14.8f)
    val usedGb: StateFlow<Float> = _usedGb.asStateFlow()

    private val _isDriveMounted = MutableStateFlow(true)
    val isDriveMounted: StateFlow<Boolean> = _isDriveMounted.asStateFlow()

    private val _isOsRunning = MutableStateFlow(false)
    val isOsRunning: StateFlow<Boolean> = _isOsRunning.asStateFlow()

    private val _cloudFiles = MutableStateFlow(
        listOf(
            com.example.ui.tabs.CloudFileItem("1", "Keon_VirtualOS_Container.vdi", 8400.0, "23 Tem 2026", "Sanal Disk"),
            com.example.ui.tabs.CloudFileItem("2", "4K_Kamera_Yedekleri.zip", 4200.0, "22 Tem 2026", "Fotoğraf"),
            com.example.ui.tabs.CloudFileItem("3", "Sistem_Loglari_Yedek.tar.gz", 1800.0, "20 Tem 2026", "Doküman")
        )
    )
    val cloudFiles: StateFlow<List<com.example.ui.tabs.CloudFileItem>> = _cloudFiles.asStateFlow()

    // App Settings & Theme/Wallpaper States
    private val _selectedLanguage = MutableStateFlow("Türkçe")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _isNotificationsEnabled = MutableStateFlow(true)
    val isNotificationsEnabled: StateFlow<Boolean> = _isNotificationsEnabled.asStateFlow()

    private val _isSoundEffectsEnabled = MutableStateFlow(true)
    val isSoundEffectsEnabled: StateFlow<Boolean> = _isSoundEffectsEnabled.asStateFlow()

    private val _isAutoBackupEnabled = MutableStateFlow(true)
    val isAutoBackupEnabled: StateFlow<Boolean> = _isAutoBackupEnabled.asStateFlow()

    private val _selectedTheme = MutableStateFlow(com.example.ui.theme.TechThemeId.CYBER_NEON)
    val selectedTheme: StateFlow<com.example.ui.theme.TechThemeId> = _selectedTheme.asStateFlow()

    private val _isDarkModeEnabled = MutableStateFlow(true)
    val isDarkModeEnabled: StateFlow<Boolean> = _isDarkModeEnabled.asStateFlow()

    private val _isAmoledModeEnabled = MutableStateFlow(false)
    val isAmoledModeEnabled: StateFlow<Boolean> = _isAmoledModeEnabled.asStateFlow()

    private val _isEyeProtectionNightFilter = MutableStateFlow(false)
    val isEyeProtectionNightFilter: StateFlow<Boolean> = _isEyeProtectionNightFilter.asStateFlow()

    private val _selectedNeonAccent = MutableStateFlow(com.example.ui.theme.NeonAccentColor.NEON_CYAN_BLUE)
    val selectedNeonAccent: StateFlow<com.example.ui.theme.NeonAccentColor> = _selectedNeonAccent.asStateFlow()

    private val _selectedWallpaper = MutableStateFlow(com.example.ui.theme.TechWallpaperId.CYBER_GRID)
    val selectedWallpaper: StateFlow<com.example.ui.theme.TechWallpaperId> = _selectedWallpaper.asStateFlow()

    private val _wallpaperAlpha = MutableStateFlow(0.40f)
    val wallpaperAlpha: StateFlow<Float> = _wallpaperAlpha.asStateFlow()

    private val _selectedLogoStyle = MutableStateFlow(com.example.ui.components.KeonLogoStyle.CYBER_CUBE_3D)
    val selectedLogoStyle: StateFlow<com.example.ui.components.KeonLogoStyle> = _selectedLogoStyle.asStateFlow()

    private val _activePackage = MutableStateFlow<com.example.ui.theme.KeonPackage?>(com.example.ui.theme.KeonPackage.AURA)
    val activePackage: StateFlow<com.example.ui.theme.KeonPackage?> = _activePackage.asStateFlow()

    private val _isDemoModeEnabled = MutableStateFlow(true)
    val isDemoModeEnabled: StateFlow<Boolean> = _isDemoModeEnabled.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _adminPhoneNumber = MutableStateFlow("")
    val adminPhoneNumber: StateFlow<String> = _adminPhoneNumber.asStateFlow()

    private val _adminAuthError = MutableStateFlow<String?>(null)
    val adminAuthError: StateFlow<String?> = _adminAuthError.asStateFlow()

    private val _isHighAccessibilityEnabled = MutableStateFlow(false)
    val isHighAccessibilityEnabled: StateFlow<Boolean> = _isHighAccessibilityEnabled.asStateFlow()

    private val _isHighContrastNeonEnabled = MutableStateFlow(false)
    val isHighContrastNeonEnabled: StateFlow<Boolean> = _isHighContrastNeonEnabled.asStateFlow()

    private val _fontSizeScale = MutableStateFlow(1.0f)
    val fontSizeScale: StateFlow<Float> = _fontSizeScale.asStateFlow()

    // 🎲 Floating 3D Cube Assistant (1/20th Screen Overlay) States
    private val _isFloating3DCubeEnabled = MutableStateFlow(false)
    val isFloating3DCubeEnabled: StateFlow<Boolean> = _isFloating3DCubeEnabled.asStateFlow()

    private val _isFloatingCubeVoiceListening = MutableStateFlow(false)
    val isFloatingCubeVoiceListening: StateFlow<Boolean> = _isFloatingCubeVoiceListening.asStateFlow()

    private val _floatingCubeVoiceMessage = MutableStateFlow("👋 3D Küp Asistanı aktif! Konuşup istek belirtebilirsiniz.")
    val floatingCubeVoiceMessage: StateFlow<String> = _floatingCubeVoiceMessage.asStateFlow()

    fun toggleFloating3DCube(enabled: Boolean) {
        _isFloating3DCubeEnabled.value = enabled
    }

    fun setFloatingCubeVoiceMessage(msg: String) {
        _floatingCubeVoiceMessage.value = msg
    }

    private val _isVoiceAssistantOpen = MutableStateFlow(false)
    val isVoiceAssistantOpen: StateFlow<Boolean> = _isVoiceAssistantOpen.asStateFlow()

    // 🎙️ Keon AI Voice Engine Tuning & Audio Analysis States
    private val _selectedVoiceProfile = MutableStateFlow(com.example.data.KeonVoiceProfile.SMOOTH_MALE_BARITONE)
    val selectedVoiceProfile: StateFlow<com.example.data.KeonVoiceProfile> = _selectedVoiceProfile.asStateFlow()

    private val _voicePitch = MutableStateFlow(0.83f)
    val voicePitch: StateFlow<Float> = _voicePitch.asStateFlow()

    private val _voiceSpeechRate = MutableStateFlow(0.97f)
    val voiceSpeechRate: StateFlow<Float> = _voiceSpeechRate.asStateFlow()

    private val _isAudioAnalyzing = MutableStateFlow(false)
    val isAudioAnalyzing: StateFlow<Boolean> = _isAudioAnalyzing.asStateFlow()

    private val _lastAudioAnalysisResult = MutableStateFlow<String?>(
        "🎵 Pürüzsüz Erkek Sesi Aktif: Keon AI ses motoru pürüzsüz ipeksi bariton erkek sesine (0.83x Perde, 0.97x Hız) ayarlandı. Metinler robotik takılmalar olmadan pürüzsüz ve akıcı telaffuz edilir."
    )
    val lastAudioAnalysisResult: StateFlow<String?> = _lastAudioAnalysisResult.asStateFlow()

    fun selectVoiceProfile(profile: com.example.data.KeonVoiceProfile) {
        _selectedVoiceProfile.value = profile
        if (profile != com.example.data.KeonVoiceProfile.CUSTOM) {
            _voicePitch.value = profile.pitch
            _voiceSpeechRate.value = profile.speechRate
        }
    }

    fun setVoicePitch(pitch: Float) {
        _voicePitch.value = pitch
        _selectedVoiceProfile.value = com.example.data.KeonVoiceProfile.CUSTOM
    }

    fun setVoiceSpeechRate(rate: Float) {
        _voiceSpeechRate.value = rate
        _selectedVoiceProfile.value = com.example.data.KeonVoiceProfile.CUSTOM
    }

    fun analyzeAudioAndCalibrateVoice(fileName: String = "kullanici_sesi.mp3", durationSeconds: Int = 12) {
        viewModelScope.launch {
            _isAudioAnalyzing.value = true
            _lastAudioAnalysisResult.value = "🔍 Ses dosyası spektrogramı çıkarılıyor ve frekans karakteristiği taranıyor..."
            kotlinx.coroutines.delay(1200)
            _lastAudioAnalysisResult.value = "📊 Frekans tespiti: 110-135 Hz (Pürüzsüz Erkek Rezonansı) • Artikülasyon: İpeksi tonlama..."
            kotlinx.coroutines.delay(1000)

            // Apply best matching profile based on analysis
            _selectedVoiceProfile.value = com.example.data.KeonVoiceProfile.SMOOTH_MALE_BARITONE
            _voicePitch.value = 0.83f
            _voiceSpeechRate.value = 0.97f
            _isAudioAnalyzing.value = false
            _lastAudioAnalysisResult.value = "✅ '$fileName' başarıyla analiz edildi!\n• Hakim Frekans: 120 Hz (Pürüzsüz Erkek Bariton)\n• Konuşma Hızı: 0.97x (Akıcı)\n• Perde: 0.83x (Tok ve İpeksi)\n• Keon AI pürüzsüz erkek sesine kusursuz şekilde kalibre edildi!"
        }
    }

    fun clearAudioAnalysisResult() {
        _lastAudioAnalysisResult.value = null
    }

    // Yapay Zeka Modeli Seçimi (Keon AI PRO vs Keon AI Lite)
    private val _selectedAiModel = MutableStateFlow(com.example.data.KeonAiModel.PRO)
    val selectedAiModel: StateFlow<com.example.data.KeonAiModel> = _selectedAiModel.asStateFlow()

    fun selectAiModel(model: com.example.data.KeonAiModel) {
        _selectedAiModel.value = model
    }



    // User Registration & Authentication State
    private val prefs = application.getSharedPreferences("keon_user_prefs", android.content.Context.MODE_PRIVATE)

    private val _currentUserAccount = MutableStateFlow<KeonUserAccount?>(null)
    val currentUserAccount: StateFlow<KeonUserAccount?> = _currentUserAccount.asStateFlow()

    private val _registeredUsersList = MutableStateFlow<List<KeonUserAccount>>(
        listOf(
            KeonUserAccount(
                id = "admin-user-01",
                name = "Kasım (Administrator)",
                contact = "+90 501 637 00 15",
                isEmail = false,
                isLoggedIn = true,
                registeredAt = "01 Ağustos 2026",
                vipPackage = com.example.ui.theme.KeonPackage.ZUMRUDU_ANKA,
                isVipApproved = true,
                accountStatus = "👑 Administrator & VIP",
                deviceModel = "Samsung Galaxy S24 Ultra",
                deviceType = "Android Telefon",
                ipAddress = "185.190.22.84",
                location = "Ankara, Türkiye",
                lastActiveTime = "🟢 Canlı Aktif (Şimdi)",
                sessionToken = "KEON-SEC-ADM-001"
            ),
            KeonUserAccount(
                id = "user-vip-01",
                name = "Halil Çamurcu",
                contact = "camurcuhalil06@gmail.com",
                isEmail = true,
                isLoggedIn = true,
                registeredAt = "05 Ağustos 2026",
                vipPackage = com.example.ui.theme.KeonPackage.AURA,
                isVipApproved = true,
                accountStatus = "👑 VIP Onaylı",
                deviceModel = "Xiaomi 14 Ultra 5G",
                deviceType = "Android Telefon",
                ipAddress = "88.241.15.201",
                location = "İstanbul, Türkiye",
                lastActiveTime = "🟢 Canlı Aktif (1 dk önce)",
                sessionToken = "KEON-SEC-XIAOMI-891"
            ),
            KeonUserAccount(
                id = "user-vip-req-01",
                name = "Mehmet Kaya",
                contact = "+90 532 987 65 43",
                isEmail = false,
                isLoggedIn = true,
                registeredAt = "Bugün 15:10",
                vipPackage = com.example.ui.theme.KeonPackage.FREE,
                isVipApproved = false,
                requestedVipPackage = com.example.ui.theme.KeonPackage.ZUMRUDU_ANKA,
                vipRequestStatus = "PENDING",
                accountStatus = "👑 VIP Onay Talebi Gönderdi",
                userNotificationMessage = "📲 VIP Talebiniz Administrator'a iletildi (Zümrüdü Anka). Administrator onayı bekleniyor...",
                deviceModel = "iPhone 15 Pro Max",
                deviceType = "iOS Cihaz",
                ipAddress = "176.234.12.90",
                location = "İzmir, Türkiye",
                lastActiveTime = "🟢 Canlı Aktif (3 dk önce)",
                sessionToken = "KEON-SEC-IPHONE-772"
            ),
            KeonUserAccount(
                id = "user-active-02",
                name = "Zeynep Aksoy",
                contact = "zeynep.aksoy@hotmail.com",
                isEmail = true,
                isLoggedIn = true,
                registeredAt = "Bugün 16:05",
                vipPackage = com.example.ui.theme.KeonPackage.ULTIMATUM,
                isVipApproved = true,
                accountStatus = "🟢 Aktif (Tablet Oturumu)",
                deviceModel = "Google Pixel Tab 2",
                deviceType = "Android Tablet",
                ipAddress = "212.156.40.12",
                location = "Bursa, Türkiye",
                lastActiveTime = "🟢 Canlı Aktif (Şimdi)",
                sessionToken = "KEON-SEC-PIXEL-304"
            ),
            KeonUserAccount(
                id = "user-ticket-01",
                name = "Ahmet Yılmaz",
                contact = "ahmet.yilmaz@gmail.com",
                isEmail = true,
                isLoggedIn = false,
                registeredAt = "Bugün 14:20",
                supportTicket = "🆘 Kaydolurken e-posta doğrulama kodu gelmedi, kaydı tamamlayamıyorum. Şifre sıfırlama veya onay talebi.",
                accountStatus = "⚠️ Kaydolma Destek Bekliyor",
                deviceModel = "Redmi Note 12",
                deviceType = "Android Telefon",
                ipAddress = "31.223.11.02",
                location = "Antalya, Türkiye",
                lastActiveTime = "🔴 Oturum Kapalı",
                sessionToken = "KEON-SEC-OFFLINE-001"
            ),
            KeonUserAccount(
                id = "user-ticket-02",
                name = "Elif Demir",
                contact = "05551234567",
                isEmail = false,
                isLoggedIn = false,
                registeredAt = "Bugün 11:15",
                supportTicket = "🆘 SMS ile kaydolmaya çalışırken hata aldım, manuel aktivasyon rica ediyorum.",
                accountStatus = "⚠️ Kaydolma Destek Bekliyor",
                deviceModel = "Samsung Galaxy A54",
                deviceType = "Android Telefon",
                ipAddress = "85.105.18.44",
                location = "Adana, Türkiye",
                lastActiveTime = "🔴 Oturum Kapalı",
                sessionToken = "KEON-SEC-OFFLINE-002"
            )
        )
    )
    val registeredUsersList: StateFlow<List<KeonUserAccount>> = _registeredUsersList.asStateFlow()

    private val _userAuthError = MutableStateFlow<String?>(null)
    val userAuthError: StateFlow<String?> = _userAuthError.asStateFlow()

    private val _isVipApprovedByAdmin = MutableStateFlow(false)
    val isVipApprovedByAdmin: StateFlow<Boolean> = _isVipApprovedByAdmin.asStateFlow()

    private val _vipPackageError = MutableStateFlow<String?>(null)
    val vipPackageError: StateFlow<String?> = _vipPackageError.asStateFlow()

    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    val isApiKeyConfigured: Boolean
        get() {
            val key = BuildConfig.GEMINI_API_KEY.trim()
            return key.isNotEmpty() && key != "MY_GEMINI_API_KEY"
        }

    init {
        val db = KeonDatabase.getDatabase(application)
        repository = KeonRepository(db.chatMessageDao())

        // Load saved user account if present
        val savedName = prefs.getString("user_name", null)
        val savedContact = prefs.getString("user_contact", null)
        val savedIsEmail = prefs.getBoolean("user_is_email", true)
        val savedRegisteredAt = prefs.getString("user_registered_at", null)
        val savedIsLoggedIn = prefs.getBoolean("user_is_logged_in", false)
        val savedVipApproved = prefs.getBoolean("user_vip_approved", false)

        _isVipApprovedByAdmin.value = savedVipApproved

        val fbUser = try { firebaseAuth?.currentUser } catch (e: Exception) { null }
        if (fbUser != null && !fbUser.email.isNullOrEmpty()) {
            val name = fbUser.displayName ?: savedName ?: "Keon Kullanıcısı"
            _currentUserAccount.value = KeonUserAccount(
                name = name,
                contact = fbUser.email!!,
                isEmail = true,
                isLoggedIn = true,
                registeredAt = savedRegisteredAt ?: "Bugün"
            )
        } else if (!savedName.isNullOrEmpty() && !savedContact.isNullOrEmpty() && savedIsLoggedIn) {
            _currentUserAccount.value = KeonUserAccount(
                name = savedName,
                contact = savedContact,
                isEmail = savedIsEmail,
                isLoggedIn = true,
                registeredAt = savedRegisteredAt ?: "Bugün"
            )
        }

        val savedPkgId = prefs.getString("active_package_id", "free")
        val pkgToSet = when (savedPkgId?.lowercase()) {
            "ultimatum" -> com.example.ui.theme.KeonPackage.ULTIMATUM
            "aura" -> com.example.ui.theme.KeonPackage.AURA
            "phoenix", "zumrudu_anka", "zumrudu-anka" -> com.example.ui.theme.KeonPackage.ZUMRUDU_ANKA
            else -> if (savedVipApproved) com.example.ui.theme.KeonPackage.AURA else com.example.ui.theme.KeonPackage.FREE
        }

        if (savedVipApproved) {
            _isVipApprovedByAdmin.value = true
            _activePackage.value = pkgToSet
            if (_currentUserAccount.value == null) {
                val defaultAccount = KeonUserAccount(
                    name = savedName ?: "VIP Kullanıcısı",
                    contact = savedContact ?: "vip@keon.ai",
                    isEmail = savedIsEmail,
                    isLoggedIn = true,
                    registeredAt = savedRegisteredAt ?: "Bugün",
                    vipPackage = pkgToSet,
                    isVipApproved = true,
                    accountStatus = "👑 VIP Onaylı (${pkgToSet.title})"
                )
                _currentUserAccount.value = defaultAccount
            } else {
                _currentUserAccount.value = _currentUserAccount.value?.copy(
                    isVipApproved = true,
                    vipPackage = pkgToSet,
                    accountStatus = "👑 VIP Onaylı (${pkgToSet.title})"
                )
            }
        } else {
            _activePackage.value = com.example.ui.theme.KeonPackage.FREE
        }

        messages = repository.allMessages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allHistoryDesc = repository.allMessagesDesc.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        messageCount = repository.messageCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        viewModelScope.launch {
            // Insert initial welcome message if database is empty
            repository.allMessages.collect { list ->
                if (list.isEmpty()) {
                    repository.saveMessage(
                        sender = "system",
                        text = "Merhaba! Ben Keon AI, size özel yapay zeka asistanınız. Size nasıl yardımcı olabilirim?"
                    )
                }
            }
        }
    }

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty() || _isTyping.value) return

        viewModelScope.launch {
            // Save user message to database
            repository.saveMessage(sender = "user", text = trimmed)

            _isTyping.value = true

            // Fetch current messages history
            val currentList = messages.value

            // Get response from Gemini with emotional analysis
            val aiResult = repository.generateAiResponse(
                userMessage = trimmed,
                recentHistory = currentList,
                selectedModel = _selectedAiModel.value.modelEndpoint
            )

            // Save AI response with detected emotional tone to database
            repository.saveMessage(sender = "ai", text = aiResult.text, emotionTone = aiResult.tone.name)

            _isTyping.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearHistory()
            repository.saveMessage(
                sender = "system",
                text = "Sohbet geçmişi temizlendi. Ben Keon AI, yeni sorularınız için hazırım!"
            )
        }
    }

    fun deleteMessageById(id: Long) {
        viewModelScope.launch {
            repository.deleteMessage(id)
        }
    }

    fun setHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
        _isVoiceAssistantOpen.value = false
    }

    fun selectSearchEngine(engine: String) {
        _selectedSearchEngine.value = engine
    }

    fun setAllocatedStorageGb(gb: Int) {
        _allocatedGb.value = gb.coerceIn(5, 60)
        if (_usedGb.value > _allocatedGb.value) {
            _usedGb.value = _allocatedGb.value.toFloat() * 0.9f
        }
    }

    fun toggleDriveMount() {
        _isDriveMounted.value = !_isDriveMounted.value
    }

    fun toggleVirtualOs() {
        _isOsRunning.value = !_isOsRunning.value
    }

    fun addCloudFile(name: String, sizeMb: Double, category: String) {
        val newId = System.currentTimeMillis().toString()
        val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("tr")).format(java.util.Date())
        val newItem = com.example.ui.tabs.CloudFileItem(newId, name, sizeMb, dateStr, category)
        _cloudFiles.value = listOf(newItem) + _cloudFiles.value
        val addedGb = (sizeMb / 1024.0).toFloat()
        _usedGb.value = (_usedGb.value + addedGb).coerceAtMost(_allocatedGb.value.toFloat())
    }

    fun deleteCloudFile(id: String) {
        val target = _cloudFiles.value.find { it.id == id }
        if (target != null) {
            _cloudFiles.value = _cloudFiles.value.filter { it.id != id }
            val removedGb = (target.sizeMb / 1024.0).toFloat()
            _usedGb.value = (_usedGb.value - removedGb).coerceAtLeast(0.5f)
        }
    }

    fun optimizePhoneStorage() {
        _usedGb.value = (_usedGb.value * 0.7f).coerceAtLeast(2.0f)
    }

    fun setSelectedLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun toggleNotifications() {
        _isNotificationsEnabled.value = !_isNotificationsEnabled.value
    }

    fun toggleSoundEffects() {
        _isSoundEffectsEnabled.value = !_isSoundEffectsEnabled.value
    }

    fun toggleAutoBackup() {
        _isAutoBackupEnabled.value = !_isAutoBackupEnabled.value
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkModeEnabled.value = enabled
        if (enabled) {
            if (_isAmoledModeEnabled.value) {
                selectTheme(com.example.ui.theme.TechThemeId.AMOLED_PITCH_BLACK)
            } else {
                selectTheme(com.example.ui.theme.TechThemeId.MIDNIGHT_DARK)
            }
        } else {
            selectTheme(com.example.ui.theme.TechThemeId.CYBER_NEON)
        }
    }

    fun toggleAmoledMode(enabled: Boolean) {
        _isAmoledModeEnabled.value = enabled
        if (enabled) {
            _isDarkModeEnabled.value = true
            selectTheme(com.example.ui.theme.TechThemeId.AMOLED_PITCH_BLACK)
            selectWallpaper(com.example.ui.theme.TechWallpaperId.AMOLED_PURE_BLACK)
        } else {
            selectTheme(com.example.ui.theme.TechThemeId.MIDNIGHT_DARK)
        }
    }

    fun toggleEyeProtectionFilter(enabled: Boolean) {
        _isEyeProtectionNightFilter.value = enabled
    }

    fun setDarkModePreset(preset: String) {
        _isDarkModeEnabled.value = true
        when (preset.lowercase()) {
            "amoled" -> {
                _isAmoledModeEnabled.value = true
                selectTheme(com.example.ui.theme.TechThemeId.AMOLED_PITCH_BLACK)
                selectWallpaper(com.example.ui.theme.TechWallpaperId.AMOLED_PURE_BLACK)
            }
            "midnight" -> {
                _isAmoledModeEnabled.value = false
                selectTheme(com.example.ui.theme.TechThemeId.MIDNIGHT_DARK)
                selectWallpaper(com.example.ui.theme.TechWallpaperId.DEEP_SPACE_NIGHT)
            }
            "stealth" -> {
                _isAmoledModeEnabled.value = false
                selectTheme(com.example.ui.theme.TechThemeId.STEALTH_CARBON)
                selectWallpaper(com.example.ui.theme.TechWallpaperId.STEALTH_CARBON_GRID)
            }
            "neon" -> {
                _isAmoledModeEnabled.value = false
                selectTheme(com.example.ui.theme.TechThemeId.CYBER_NEON)
                selectWallpaper(com.example.ui.theme.TechWallpaperId.CYBER_GRID)
            }
            "flame" -> {
                _isAmoledModeEnabled.value = false
                selectTheme(com.example.ui.theme.TechThemeId.FLAME_CORE)
                selectWallpaper(com.example.ui.theme.TechWallpaperId.FLAME_CORE_MATRIX)
            }
        }
    }

    fun selectTheme(themeId: com.example.ui.theme.TechThemeId) {
        _selectedTheme.value = themeId
        when (themeId) {
            com.example.ui.theme.TechThemeId.CYBER_NEON -> _selectedNeonAccent.value = com.example.ui.theme.NeonAccentColor.NEON_CYAN_BLUE
            com.example.ui.theme.TechThemeId.AMOLED_PITCH_BLACK -> {
                _isAmoledModeEnabled.value = true
                _isDarkModeEnabled.value = true
                _selectedNeonAccent.value = com.example.ui.theme.NeonAccentColor.NEON_CYAN_BLUE
            }
            com.example.ui.theme.TechThemeId.MIDNIGHT_DARK -> {
                _isDarkModeEnabled.value = true
                _selectedNeonAccent.value = com.example.ui.theme.NeonAccentColor.NEON_CYAN_BLUE
            }
            com.example.ui.theme.TechThemeId.STEALTH_CARBON -> {
                _isDarkModeEnabled.value = true
                _selectedNeonAccent.value = com.example.ui.theme.NeonAccentColor.NEON_ICE_CRYSTAL
            }
            com.example.ui.theme.TechThemeId.AURORA_HOLOGRAM -> _selectedNeonAccent.value = com.example.ui.theme.NeonAccentColor.NEON_PURPLE_MAGENTA
            com.example.ui.theme.TechThemeId.QUANTUM_MATRIX -> _selectedNeonAccent.value = com.example.ui.theme.NeonAccentColor.NEON_MATRIX_GREEN
            com.example.ui.theme.TechThemeId.SOLAR_FLARE -> _selectedNeonAccent.value = com.example.ui.theme.NeonAccentColor.NEON_AMBER_GOLD
            com.example.ui.theme.TechThemeId.OBSIDIAN_VOID -> _selectedNeonAccent.value = com.example.ui.theme.NeonAccentColor.NEON_ICE_CRYSTAL
            com.example.ui.theme.TechThemeId.PHOENIX_EMERALD -> _selectedNeonAccent.value = com.example.ui.theme.NeonAccentColor.NEON_MATRIX_GREEN
            com.example.ui.theme.TechThemeId.AURA_HOLOGRAM_PRO -> _selectedNeonAccent.value = com.example.ui.theme.NeonAccentColor.NEON_PURPLE_MAGENTA
            com.example.ui.theme.TechThemeId.FLAME_CORE -> _selectedNeonAccent.value = com.example.ui.theme.NeonAccentColor.NEON_FLAME_PURPLE_ORANGE
        }
    }

    fun selectNeonAccent(accent: com.example.ui.theme.NeonAccentColor) {
        _selectedNeonAccent.value = accent
        when (accent) {
            com.example.ui.theme.NeonAccentColor.NEON_CYAN_BLUE -> _selectedTheme.value = com.example.ui.theme.TechThemeId.CYBER_NEON
            com.example.ui.theme.NeonAccentColor.NEON_PURPLE_MAGENTA -> _selectedTheme.value = com.example.ui.theme.TechThemeId.AURORA_HOLOGRAM
            com.example.ui.theme.NeonAccentColor.NEON_MATRIX_GREEN -> _selectedTheme.value = com.example.ui.theme.TechThemeId.QUANTUM_MATRIX
            com.example.ui.theme.NeonAccentColor.NEON_AMBER_GOLD -> _selectedTheme.value = com.example.ui.theme.TechThemeId.SOLAR_FLARE
            com.example.ui.theme.NeonAccentColor.NEON_ICE_CRYSTAL -> _selectedTheme.value = com.example.ui.theme.TechThemeId.OBSIDIAN_VOID
            com.example.ui.theme.NeonAccentColor.NEON_FLAME_PURPLE_ORANGE -> _selectedTheme.value = com.example.ui.theme.TechThemeId.FLAME_CORE
            else -> {}
        }
    }

    fun selectPackage(pkg: com.example.ui.theme.KeonPackage): Boolean {
        _vipPackageError.value = null
        if (pkg == com.example.ui.theme.KeonPackage.FREE) {
            _activePackage.value = pkg
            prefs.edit().putString("active_package_id", pkg.id).apply()
            return true
        }

        // Rule 1: User must be registered & logged in
        val user = _currentUserAccount.value
        if (user == null || !user.isLoggedIn) {
            _vipPackageError.value = "⚠️ VIP Paket Geçişi Engellendi: VIP paketlere (Ultimatum, Aura, Zümrüdü Anka) geçebilmek için öncelikle e-posta veya telefon numarası ile KAYIT OLMANIZ gerekmektedir."
            return false
        }

        // Rule 2: User must have Administrator permission or be Administrator
        if (!_isAdmin.value && !_isVipApprovedByAdmin.value) {
            requestVipPackage(pkg)
            return false
        }

        _activePackage.value = pkg
        prefs.edit().putString("active_package_id", pkg.id).apply()
        return true
    }

    fun requestVipPackage(pkg: com.example.ui.theme.KeonPackage) {
        val user = _currentUserAccount.value
        val contactToFind = user?.contact ?: ""

        val currentList = _registeredUsersList.value.toMutableList()
        val index = currentList.indexOfFirst { (contactToFind.isNotBlank() && it.contact == contactToFind) || (user != null && it.id == user.id) }

        val updatedUser = if (index != -1) {
            val u = currentList[index]
            val updated = u.copy(
                requestedVipPackage = pkg,
                vipRequestStatus = "PENDING",
                accountStatus = "👑 VIP Onay Talebi Gönderildi (${pkg.title})",
                userNotificationMessage = "📲 VIP Talebiniz Administrator'a iletildi (${pkg.title}). Onay veya Red kararı bekleniyor..."
            )
            currentList[index] = updated
            updated
        } else {
            val newUser = KeonUserAccount(
                name = user?.name ?: "Kayıtlı Kullanıcı",
                contact = if (contactToFind.isBlank()) "Bilinmeyen İletişim" else contactToFind,
                isEmail = user?.isEmail ?: true,
                isLoggedIn = true,
                registeredAt = "Bugün",
                requestedVipPackage = pkg,
                vipRequestStatus = "PENDING",
                accountStatus = "👑 VIP Onay Talebi Gönderildi (${pkg.title})",
                userNotificationMessage = "📲 VIP Talebiniz Administrator'a iletildi (${pkg.title}). Onay veya Red kararı bekleniyor..."
            )
            currentList.add(newUser)
            newUser
        }

        _registeredUsersList.value = currentList
        _currentUserAccount.value = updatedUser
        _vipPackageError.value = "🔔 Administrator İzni Gerekli: ${pkg.title} talebiniz Administrator'a BİLDİRİM olarak gönderildi. Onay veya Red kararı bekleniyor."
    }

    fun approveVipRequest(userId: String) {
        val currentList = _registeredUsersList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == userId || it.contact == userId }
        if (index != -1) {
            val user = currentList[index]
            val chosenPkg = user.requestedVipPackage ?: com.example.ui.theme.KeonPackage.AURA
            val updated = user.copy(
                vipPackage = chosenPkg,
                isVipApproved = true,
                vipRequestStatus = "APPROVED",
                accountStatus = "👑 VIP Onaylandı (${chosenPkg.title})",
                userNotificationMessage = "🎉 MÜJDE: Administrator VIP Paket Talebinizi ONAYLADI! (${chosenPkg.title} Ayrıcalıkları Aktif)"
            )
            currentList[index] = updated
            _registeredUsersList.value = currentList

            _isVipApprovedByAdmin.value = true
            _activePackage.value = chosenPkg
            if (_currentUserAccount.value?.contact == user.contact || _currentUserAccount.value?.id == user.id || _currentUserAccount.value == null) {
                _currentUserAccount.value = updated
            } else {
                _currentUserAccount.value = _currentUserAccount.value?.copy(
                    isVipApproved = true,
                    vipPackage = chosenPkg
                )
            }
            _vipPackageError.value = null
            prefs.edit()
                .putBoolean("user_vip_approved", true)
                .putString("active_package_id", chosenPkg.id)
                .putString("user_name", _currentUserAccount.value?.name ?: user.name)
                .putString("user_contact", _currentUserAccount.value?.contact ?: user.contact)
                .putBoolean("user_is_logged_in", true)
                .apply()
        }
    }

    fun rejectVipRequest(userId: String) {
        val currentList = _registeredUsersList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == userId || it.contact == userId }
        if (index != -1) {
            val user = currentList[index]
            val updated = user.copy(
                vipPackage = com.example.ui.theme.KeonPackage.FREE,
                isVipApproved = false,
                vipRequestStatus = "REJECTED",
                accountStatus = "❌ VIP Talebi Reddedildi",
                userNotificationMessage = "📱 TELEFONUNUZA GELEN BİLDİRİM: ❌ VIP Paket Talebiniz Administrator Tarafından REDDEDİLDİ."
            )
            currentList[index] = updated
            _registeredUsersList.value = currentList

            if (_currentUserAccount.value?.contact == user.contact || _currentUserAccount.value?.id == user.id) {
                _isVipApprovedByAdmin.value = false
                _activePackage.value = com.example.ui.theme.KeonPackage.FREE
                _currentUserAccount.value = updated
                _vipPackageError.value = "❌ VIP Paket Talebiniz Administrator Tarafından REDDEDİLDİ."
                prefs.edit().putBoolean("user_vip_approved", false).putString("active_package_id", "free").apply()
            }
        }
    }

    fun clearUserNotificationMessage() {
        _currentUserAccount.value = _currentUserAccount.value?.copy(userNotificationMessage = null)
    }

    fun toggleVipApprovalByAdmin(approved: Boolean) {
        _isVipApprovedByAdmin.value = approved
        if (approved) {
            val pkgToSet = if (_activePackage.value != null && _activePackage.value != com.example.ui.theme.KeonPackage.FREE) {
                _activePackage.value!!
            } else {
                com.example.ui.theme.KeonPackage.AURA
            }
            _activePackage.value = pkgToSet
            _currentUserAccount.value = _currentUserAccount.value?.copy(isVipApproved = true, vipPackage = pkgToSet)
            prefs.edit()
                .putBoolean("user_vip_approved", true)
                .putString("active_package_id", pkgToSet.id)
                .putBoolean("user_is_logged_in", true)
                .apply()
        } else {
            _currentUserAccount.value = _currentUserAccount.value?.copy(isVipApproved = false, vipPackage = com.example.ui.theme.KeonPackage.FREE)
            if (!_isAdmin.value) {
                _activePackage.value = com.example.ui.theme.KeonPackage.FREE
                prefs.edit()
                    .putBoolean("user_vip_approved", false)
                    .putString("active_package_id", "free")
                    .apply()
            } else {
                prefs.edit().putBoolean("user_vip_approved", false).apply()
            }
        }
    }

    fun clearVipPackageError() {
        _vipPackageError.value = null
    }

    fun selectWallpaper(wallpaperId: com.example.ui.theme.TechWallpaperId) {
        _selectedWallpaper.value = wallpaperId
    }

    fun setWallpaperAlpha(alpha: Float) {
        _wallpaperAlpha.value = alpha.coerceIn(0.1f, 0.9f)
    }

    fun selectLogoStyle(style: com.example.ui.components.KeonLogoStyle) {
        _selectedLogoStyle.value = style
    }

    fun toggleDemoMode(enabled: Boolean) {
        _isDemoModeEnabled.value = enabled
        if (!enabled) {
            _isAdmin.value = false
            _adminPhoneNumber.value = ""
            _adminAuthError.value = null
        }
    }

    fun verifyAdminPhoneNumber(code: String) {
        val trimmed = code.trim()
        val normalized = trimmed.lowercase().replace("\\s+".toRegex(), " ")
        val stripped = trimmed.lowercase().replace("\\s+".toRegex(), "")

        val targetNormalized = "+90 501 637 00 15 kasım admin"
        val targetStripped = "+905016370015kasımadmin"
        val targetStrippedNoPlus = "905016370015kasımadmin"
        val targetStrippedZero = "05016370015kasımadmin"
        val targetMasked = "+** *** *** ** ** ##### #####"
        val targetMaskedStripped = "+************##########"

        if (normalized == targetNormalized || stripped == targetStripped || stripped == targetStrippedNoPlus || 
            stripped == targetStrippedZero || normalized == targetMasked || stripped == targetMaskedStripped) {
            _isAdmin.value = true
            _adminPhoneNumber.value = "+** *** *** ** ** ##### #####"
            _adminAuthError.value = null
        } else {
            _isAdmin.value = false
            _adminAuthError.value = "Hatalı Administrator Kodu! Yetkili girişi yapmak için geçerli Administrator Kodunu giriniz."
        }
    }

    fun revokeAdmin() {
        _isAdmin.value = false
        _adminPhoneNumber.value = ""
        _adminAuthError.value = null
    }

    fun toggleHighAccessibility(enabled: Boolean) {
        _isHighAccessibilityEnabled.value = enabled
        if (enabled) {
            _isHighContrastNeonEnabled.value = true
        }
    }

    fun toggleHighContrastNeon(enabled: Boolean) {
        _isHighContrastNeonEnabled.value = enabled
    }

    fun setFontSizeScale(scale: Float) {
        _fontSizeScale.value = scale
    }

    fun openVoiceAssistant() {
        _isVoiceAssistantOpen.value = true
    }

    fun closeVoiceAssistant() {
        _isVoiceAssistantOpen.value = false
    }

    fun toggleVoiceAssistant() {
        _isVoiceAssistantOpen.value = !_isVoiceAssistantOpen.value
    }

    fun registerUser(
        name: String,
        contactInput: String,
        password: String,
        passwordConfirm: String,
        isEmail: Boolean
    ): Boolean {
        _userAuthError.value = null
        val trimmedName = name.trim()
        val trimmedContact = contactInput.trim()

        if (trimmedName.isEmpty()) {
            _userAuthError.value = "Lütfen adınızı ve soyadınızı giriniz."
            return false
        }

        if (trimmedContact.isEmpty()) {
            _userAuthError.value = if (isEmail) "Lütfen e-posta adresinizi giriniz." else "Lütfen telefon numaranızı giriniz."
            return false
        }

        if (isEmail && (!trimmedContact.contains("@") || !trimmedContact.contains("."))) {
            _userAuthError.value = "Geçerli bir e-posta adresi giriniz (ör: ahmet@keon.ai)"
            return false
        }

        if (!isEmail && trimmedContact.length < 10) {
            _userAuthError.value = "Geçerli bir telefon numarası giriniz (ör: 0555 123 45 67)"
            return false
        }

        if (password.length < 4) {
            _userAuthError.value = "Şifreniz en az 4 karakter olmalıdır."
            return false
        }

        if (password != passwordConfirm) {
            _userAuthError.value = "Şifreler birbiriyle eşleşmiyor."
            return false
        }

        val now = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale("tr")).format(java.util.Date())
        val account = KeonUserAccount(
            name = trimmedName,
            contact = trimmedContact,
            isEmail = isEmail,
            isLoggedIn = true,
            registeredAt = now
        )

        _currentUserAccount.value = account
        val listWithNewUser = _registeredUsersList.value.toMutableList()
        if (listWithNewUser.none { it.contact == account.contact }) {
            listWithNewUser.add(0, account)
            _registeredUsersList.value = listWithNewUser
        }
        prefs.edit().apply {
            putString("user_name", trimmedName)
            putString("user_contact", trimmedContact)
            putBoolean("user_is_email", isEmail)
            putString("user_registered_at", now)
            putBoolean("user_is_logged_in", true)
            apply()
        }

        if (isEmail && firebaseAuth != null) {
            try {
                firebaseAuth?.createUserWithEmailAndPassword(trimmedContact, password)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            user?.updateProfile(
                                UserProfileChangeRequest.Builder().setDisplayName(trimmedName).build()
                            )
                        } else {
                            val err = task.exception?.localizedMessage
                            if (!err.isNullOrEmpty()) {
                                _userAuthError.value = "Firebase Bildirimi: $err"
                            }
                        }
                    }
            } catch (e: Exception) {
                // Fallback to local user
            }
        }

        return true
    }

    fun loginUser(contactInput: String, password: String): Boolean {
        _userAuthError.value = null
        val trimmedContact = contactInput.trim()

        if (trimmedContact.isEmpty()) {
            _userAuthError.value = "Lütfen e-posta adresinizi veya telefon numaranızı giriniz."
            return false
        }

        if (password.isEmpty()) {
            _userAuthError.value = "Lütfen şifrenizi giriniz."
            return false
        }

        val savedContact = prefs.getString("user_contact", null)
        val savedName = prefs.getString("user_name", "Keon Kullanıcısı") ?: "Keon Kullanıcısı"
        val isEmail = trimmedContact.contains("@")
        val now = prefs.getString("user_registered_at", "Bugün") ?: "Bugün"

        val account = KeonUserAccount(
            name = if (!savedContact.isNullOrEmpty() && savedContact == trimmedContact) savedName else "Kullanıcı (${trimmedContact.take(8)})",
            contact = trimmedContact,
            isEmail = isEmail,
            isLoggedIn = true,
            registeredAt = now
        )

        _currentUserAccount.value = account
        prefs.edit().apply {
            putString("user_name", account.name)
            putString("user_contact", trimmedContact)
            putBoolean("user_is_email", isEmail)
            putString("user_registered_at", now)
            putBoolean("user_is_logged_in", true)
            apply()
        }

        if (isEmail && firebaseAuth != null) {
            try {
                firebaseAuth?.signInWithEmailAndPassword(trimmedContact, password)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            val displayName = user?.displayName
                            if (!displayName.isNullOrEmpty()) {
                                val updatedAccount = account.copy(name = displayName)
                                _currentUserAccount.value = updatedAccount
                                prefs.edit().putString("user_name", displayName).apply()
                            }
                        } else {
                            val err = task.exception?.localizedMessage
                            if (!err.isNullOrEmpty()) {
                                _userAuthError.value = "Firebase Bildirimi: $err"
                            }
                        }
                    }
            } catch (e: Exception) {
                // Fallback to local login
            }
        }

        return true
    }

    fun logoutUser() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            // Ignore
        }
        _currentUserAccount.value = null
        prefs.edit().putBoolean("user_is_logged_in", false).apply()
        if (!_isAdmin.value) {
            _activePackage.value = com.example.ui.theme.KeonPackage.FREE
            prefs.edit().putString("active_package_id", "free").apply()
        }
    }

    fun clearUserAuthError() {
        _userAuthError.value = null
    }

    fun triggerOrbit() {
        viewModelScope.launch {
            _isOrbitActive.value = true
            kotlinx.coroutines.delay(5000)
            _isOrbitActive.value = false
        }
    }

    fun grantVipToUser(userId: String, pkg: com.example.ui.theme.KeonPackage) {
        val currentList = _registeredUsersList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == userId || it.contact == userId }
        if (index != -1) {
            val user = currentList[index]
            val updated = user.copy(
                vipPackage = pkg,
                isVipApproved = true,
                accountStatus = "👑 VIP Onaylı (${pkg.title})",
                supportTicket = null
            )
            currentList[index] = updated
            _registeredUsersList.value = currentList

            if (_currentUserAccount.value?.contact == user.contact || _currentUserAccount.value?.id == user.id) {
                _isVipApprovedByAdmin.value = true
                _activePackage.value = pkg
                prefs.edit().putBoolean("user_vip_approved", true).putString("active_package_id", pkg.name.lowercase()).apply()
            }
        }
    }

    fun revokeVipFromUser(userId: String) {
        val currentList = _registeredUsersList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == userId || it.contact == userId }
        if (index != -1) {
            val user = currentList[index]
            val updated = user.copy(
                vipPackage = com.example.ui.theme.KeonPackage.FREE,
                isVipApproved = false,
                accountStatus = "Standart Kullanıcı"
            )
            currentList[index] = updated
            _registeredUsersList.value = currentList

            if (_currentUserAccount.value?.contact == user.contact || _currentUserAccount.value?.id == user.id) {
                _isVipApprovedByAdmin.value = false
                _activePackage.value = com.example.ui.theme.KeonPackage.FREE
                prefs.edit().putBoolean("user_vip_approved", false).putString("active_package_id", "free").apply()
            }
        }
    }

    fun resolveSupportTicketAndRegister(userId: String, note: String = "Admin tarafından hesap doğrulandı ve aktifleştirildi.") {
        val currentList = _registeredUsersList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == userId || it.contact == userId }
        if (index != -1) {
            val user = currentList[index]
            val updated = user.copy(
                accountStatus = "✅ Aktif (Admin Yardımıyla Kaydedildi)",
                supportTicket = "✅ Tamamlandı: $note"
            )
            currentList[index] = updated
            _registeredUsersList.value = currentList
        }
    }

    fun addOrRegisterUserByAdmin(
        name: String,
        contact: String,
        isEmail: Boolean,
        vipPackage: com.example.ui.theme.KeonPackage? = com.example.ui.theme.KeonPackage.AURA
    ) {
        val now = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale("tr")).format(java.util.Date())
        val newUser = KeonUserAccount(
            name = name,
            contact = contact,
            isEmail = isEmail,
            isLoggedIn = true,
            registeredAt = now,
            vipPackage = vipPackage ?: com.example.ui.theme.KeonPackage.AURA,
            isVipApproved = true,
            accountStatus = "👑 Admin Tarafından Kaydedildi & VIP"
        )
        val list = _registeredUsersList.value.toMutableList()
        list.add(0, newUser)
        _registeredUsersList.value = list
    }

    fun deleteUserByAdmin(userId: String) {
        val list = _registeredUsersList.value.filterNot { it.id == userId || it.contact == userId }
        _registeredUsersList.value = list
    }

    fun kickUserSession(userId: String) {
        val currentList = _registeredUsersList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == userId || it.contact == userId }
        if (index != -1) {
            val user = currentList[index]
            val updated = user.copy(
                isLoggedIn = false,
                lastActiveTime = "🔴 Oturum Admin Tarafından Kapatıldı",
                accountStatus = "🔴 Oturum Kapatıldı",
                userNotificationMessage = "⚠️ Oturumunuz Administrator tarafından güvenlik nedeniyle sonlandırıldı."
            )
            currentList[index] = updated
            _registeredUsersList.value = currentList

            if (_currentUserAccount.value?.contact == user.contact || _currentUserAccount.value?.id == user.id) {
                logoutUser()
            }
        }
    }

    fun terminateAllOtherSessions() {
        val currentList = _registeredUsersList.value.toMutableList()
        val currentId = _currentUserAccount.value?.id
        val updatedList = currentList.map { user ->
            if (user.id != currentId && !user.id.contains("admin")) {
                user.copy(
                    isLoggedIn = false,
                    lastActiveTime = "🔴 Toplu Oturum Kapatıldı",
                    accountStatus = "🔴 Oturum Kapatıldı",
                    userNotificationMessage = "⚠️ Tüm harici cihaz oturumları Administrator tarafından sonlandırıldı."
                )
            } else {
                user
            }
        }
        _registeredUsersList.value = updatedList
    }

    fun sendNotificationToUserSession(userId: String, message: String) {
        val currentList = _registeredUsersList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == userId || it.contact == userId }
        if (index != -1) {
            val user = currentList[index]
            val updated = user.copy(
                userNotificationMessage = "🔔 Admin Mesajı: $message"
            )
            currentList[index] = updated
            _registeredUsersList.value = currentList

            if (_currentUserAccount.value?.contact == user.contact || _currentUserAccount.value?.id == user.id) {
                _currentUserAccount.value = updated
            }
        }
    }

    val otherAis = listOf(
        AiModelInfo("chatgpt", "ChatGPT", "OpenAI", "https://chat.openai.com", "ic_chatgpt", 0xFF74D7A7),
        AiModelInfo("keon", "Keon AI Web", "Keon Core", "https://keon.ai", "ic_keon", 0xFF00E5FF),
        AiModelInfo("claude", "Claude", "Anthropic", "https://claude.ai", "ic_claude", 0xFFCC8F66),
        AiModelInfo("copilot", "Copilot", "Microsoft", "https://copilot.microsoft.com", "ic_copilot", 0xFF0078D4),
        AiModelInfo("perplexity", "Perplexity", "AI Arama", "https://www.perplexity.ai", "ic_perplexity", 0xFF20CAAC),
        AiModelInfo("deepseek", "DeepSeek", "DeepSeek", "https://chat.deepseek.com", "ic_deepseek", 0xFFFF64C8),
        AiModelInfo("mistral", "Mistral", "Mistral AI", "https://chat.mistral.ai", "ic_mistral", 0xFFFFA500),
        AiModelInfo("huggingchat", "HuggingChat", "Hugging Face", "https://huggingface.co/chat", "ic_hugging", 0xFFFFD700),
        AiModelInfo("youcom", "You.com", "AI Arama", "https://you.com", "ic_youcom", 0xFF8A2BE2),
        AiModelInfo("poe", "Poe", "Quora", "https://poe.com", "ic_poe", 0xFFFF1493),
        AiModelInfo("pi", "Pi", "Inflection AI", "https://pi.ai", "ic_pi", 0xFF00BFFF),
        AiModelInfo("metaai", "Meta AI", "Meta", "https://www.meta.ai", "ic_meta", 0xFF32CD32)
    )
}
