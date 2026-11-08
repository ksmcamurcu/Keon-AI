package com.example.ui.tabs

import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.pulsatingNeonGlow
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.FlameNeonOrange
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AndroidDownloader
import com.example.util.DetectedHardwareClass
import com.example.util.OnboardScanResult
import com.example.util.OnboardScanStep
import com.example.util.OnboardScanningUtility
import com.example.util.UnlockedInterfaceComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * TÜBİTAK Otonom Sistemler & Robotik Araç Türleri
 */
enum class TubitakVehicleType(
    val title: String,
    val subtitle: String,
    val iconName: String,
    val defaultCode: String,
    val accentColor: Color,
    val hardwareClass: DetectedHardwareClass,
    val capabilities: List<String>
) {
    UAV_AIRPLANE(
        title = "İnsansız Savaş Uçağı (İHA/SİHA)",
        subtitle = "TÜBİTAK SAGE / Baykar Entegrasyonu",
        iconName = "✈️",
        defaultCode = "KEON-TUBITAK-UAV-X9",
        accentColor = NeonCyan,
        hardwareClass = DetectedHardwareClass.DRONE_UAV,
        capabilities = listOf("Otonom Uçuş & Rota Takibi", "Güdümlü Hedef Kitleme", "Radar & LiDAR Haritalama", "Güvenli Telemetri Şifreleme")
    ),
    ROBOT_DOG(
        title = "4 Ayaklı Robot Köpek (Quadruped)",
        subtitle = "TÜBİTAK BİLGEM / Arazi & Savunma Keşif",
        iconName = "🐕",
        defaultCode = "KEON-TUBITAK-DOG-Q4",
        accentColor = FlameNeonOrange,
        hardwareClass = DetectedHardwareClass.ROBOT_DOG,
        capabilities = listOf("Zorlu Arazi Adaptasyonu", "Engel Aşma & Zıplama", "Termal Gece Görüşü", "Çevresel Gaz / Radyasyon Taraması")
    ),
    HUMANOID_ROBOT(
        title = "İnsansı Robot (Humanoid)",
        subtitle = "TÜBİTAK MAM / Biyomekatronik Ünite",
        iconName = "🤖",
        defaultCode = "KEON-TUBITAK-HUMAN-H1",
        accentColor = NeonMagenta,
        hardwareClass = DetectedHardwareClass.HUMANOID,
        capabilities = listOf("İki Ayaklı Denge & Yürüyüş", "Hassas Parmaklı Nesne Tutma", "Sesli & Görsel Yüz Tanıma", "Taktik Görev İcrası")
    ),
    DRONE_QUAD(
        title = "Taktik Sürü Dronu (FPV/Swarm)",
        subtitle = "TÜBİTAK Savunma Sürü Zekası",
        iconName = "🛸",
        defaultCode = "KEON-TUBITAK-DRONE-S8",
        accentColor = NeonGreen,
        hardwareClass = DetectedHardwareClass.DRONE_UAV,
        capabilities = listOf("Sürü İçi Çapraz İletişim", "Kapalı Alan Keşif Uçuşu", "Hızlı Dinamik Kaçış Manevrası", "Canlı 4K Şifreli Yayın")
    )
}

@Composable
fun TubitakDefenseSection(
    isAdmin: Boolean,
    adminPhoneNumber: String?,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val downloader = remember { AndroidDownloader(context) }
    val onboardScanner = remember { OnboardScanningUtility(context) }
    val scrollState = rememberScrollState()

    var selectedVehicle by remember { mutableStateOf(TubitakVehicleType.UAV_AIRPLANE) }
    var activeTelemetryState by remember { mutableStateOf(false) }
    var enginePower by remember { mutableFloatStateOf(75f) }
    var altitudeMeters by remember { mutableIntStateOf(1450) }
    var speedKmH by remember { mutableIntStateOf(320) }
    var batteryPercentage by remember { mutableIntStateOf(94) }
    var isEmergencyLocked by remember { mutableStateOf(false) }

    // Robot Köpek Denge ve Duruş
    var dogPostureState by remember { mutableStateOf("Tetikte (Patrol)") }
    var dogTerrainState by remember { mutableStateOf("Asfalt / Düz Zemin") }
    var dogGasLevelPpm by remember { mutableIntStateOf(12) }

    // İnsansı Robot Durumları
    var humanoidGestureState by remember { mutableStateOf("Hazır Duruş (Standby)") }
    var humanoidGripStrength by remember { mutableFloatStateOf(60f) }
    var humanoidBalanceZmp by remember { mutableFloatStateOf(98.5f) }

    // İHA / Dron Özel Kontroller
    var isRthActive by remember { mutableStateOf(false) }
    var targetMissileLocked by remember { mutableStateOf(false) }
    var pitchAngle by remember { mutableFloatStateOf(4.5f) }
    var rollAngle by remember { mutableFloatStateOf(-1.2f) }

    // .kdf (Keon Defense Format) İndirme ve Dışa Aktarma Durumu
    var isDownloadingKdf by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var lastExportedFileName by remember { mutableStateOf<String?>(null) }
    var lastExportedFilePath by remember { mutableStateOf<String?>(null) }

    // Onboard Tarayıcı & Donanım Teşhis / Kilit Açma Durumu
    var isScanningOnboard by remember { mutableStateOf(false) }
    var scanStepMessage by remember { mutableStateOf<String?>(null) }
    var scanProgressFraction by remember { mutableFloatStateOf(0f) }
    var onboardScanResult by remember { mutableStateOf<OnboardScanResult?>(null) }
    var unlockedComponentsList by remember { mutableStateOf<List<UnlockedInterfaceComponent>>(emptyList()) }
    var availableKdfFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var selectedKdfFile by remember { mutableStateOf<File?>(null) }

    // Yerel Donanım ve Sensör Profili
    val localHardwareProfile = remember { onboardScanner.probeLocalDeviceHardware() }

    // Mevcut .kdf dosyalarını yükle
    LaunchedEffect(Unit) {
        availableKdfFiles = onboardScanner.findAvailableKdfFiles()
        if (availableKdfFiles.isNotEmpty()) {
            selectedKdfFile = availableKdfFiles.first()
        }
    }

    // Canlı Simülasyon Döngüsü (Telemetri Açıkken)
    LaunchedEffect(activeTelemetryState) {
        if (activeTelemetryState) {
            while (activeTelemetryState) {
                delay(1000)
                when (selectedVehicle.hardwareClass) {
                    DetectedHardwareClass.DRONE_UAV -> {
                        altitudeMeters = (altitudeMeters + (-15..20).random()).coerceIn(500, 10000)
                        speedKmH = (speedKmH + (-5..10).random()).coerceIn(150, 850)
                        pitchAngle = (pitchAngle + (-10..10).random() / 10f).coerceIn(-25f, 25f)
                        rollAngle = (rollAngle + (-8..8).random() / 10f).coerceIn(-30f, 30f)
                    }
                    DetectedHardwareClass.ROBOT_DOG -> {
                        speedKmH = (speedKmH + (-1..2).random()).coerceIn(0, 35)
                        dogGasLevelPpm = (dogGasLevelPpm + (-1..2).random()).coerceIn(5, 50)
                    }
                    DetectedHardwareClass.HUMANOID -> {
                        speedKmH = (speedKmH + (-1..1).random()).coerceIn(0, 15)
                        humanoidBalanceZmp = (98f + (-5..5).random() / 10f).coerceIn(90f, 100f)
                    }
                    else -> {}
                }
                batteryPercentage = (batteryPercentage - 1).coerceAtLeast(10)
            }
        }
    }

    // Onboard Scanner'ı çalıştıran ve donanım tipini tespit edip arayüz bileşenlerini açan fonksiyon
    fun startOnboardScan(fileToScan: File?) {
        val targetFile = fileToScan ?: onboardScanner.generateSamplePackage(selectedVehicle.hardwareClass)
        isScanningOnboard = true
        scanStepMessage = "Şifreli savunma paketi doğrulanıyor..."
        scanProgressFraction = 0.1f

        coroutineScope.launch {
            onboardScanner.executeOnboardScanFlow(targetFile).collect { step ->
                when (step) {
                    is OnboardScanStep.Scanning -> {
                        scanStepMessage = step.message
                        scanProgressFraction = step.stepNumber / step.totalSteps.toFloat()
                    }
                    is OnboardScanStep.Decrypting -> {
                        scanStepMessage = "🔓 ${step.algorithm} çözülüyor..."
                        scanProgressFraction = step.progress
                    }
                    is OnboardScanStep.HardwareBinding -> {
                        scanStepMessage = "🔗 Donanım Tanındı: ${step.hardwareClass.titleTr} (${step.assignedProtocol})"
                    }
                    is OnboardScanStep.Completed -> {
                        isScanningOnboard = false
                        onboardScanResult = step.result
                        unlockedComponentsList = step.result.unlockedComponents

                        // Otomatik olarak eşleşen araç sekmesini seç
                        when (step.result.hardwareClass) {
                            DetectedHardwareClass.DRONE_UAV -> {
                                if (selectedVehicle != TubitakVehicleType.UAV_AIRPLANE && selectedVehicle != TubitakVehicleType.DRONE_QUAD) {
                                    selectedVehicle = TubitakVehicleType.UAV_AIRPLANE
                                }
                            }
                            DetectedHardwareClass.ROBOT_DOG -> {
                                selectedVehicle = TubitakVehicleType.ROBOT_DOG
                            }
                            DetectedHardwareClass.HUMANOID -> {
                                selectedVehicle = TubitakVehicleType.HUMANOID_ROBOT
                            }
                            else -> {}
                        }

                        Toast.makeText(
                            context,
                            "🎯 ${step.result.hardwareClass.titleTr} TESPİT EDİLDİ & ARAYÜZ KİLİDİ AÇILDI!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is OnboardScanStep.Failed -> {
                        isScanningOnboard = false
                        Toast.makeText(context, "Hata: ${step.errorReason}", Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }

    // .kdf Formatında Dosya Oluşturma ve Telefona İndirme Fonksiyonu
    fun exportAndDownloadKdfFile() {
        isDownloadingKdf = true
        downloadProgress = 0f
        coroutineScope.launch {
            for (i in 1..10) {
                delay(100)
                downloadProgress = i / 10f
            }

            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "TUBITAK_${selectedVehicle.name}_$timestamp.kdf"

                // KDF Özel Şifreli JSON Yapısı
                val kdfPayload = JSONObject().apply {
                    put("format", "KEON_DEFENSE_FORMAT_V2")
                    put("creator", "TÜBİTAK Otonom Sistemler & Robotik Güvenlik Merkezi")
                    put("authorized_admin", adminPhoneNumber ?: "Administrator (+905016370015)")
                    put("export_timestamp", timestamp)
                    put("target_vehicle_type", selectedVehicle.name)
                    put("vehicle_title", selectedVehicle.title)
                    put("vehicle_code", selectedVehicle.defaultCode)
                    put("encryption_level", "AES-256-GCM / TÜBİTAK Kripto")
                    put("telemetry_specs", JSONObject().apply {
                        put("engine_power", enginePower)
                        put("altitude_meters", altitudeMeters)
                        put("speed_kmh", speedKmH)
                        put("battery_level", batteryPercentage)
                        put("safety_lock", isEmergencyLocked)
                    })
                    put("hardware_auto_adapter", JSONObject().apply {
                        put("action_on_new_device", "AUTO_DETECT_AND_ADAPT")
                        put("supported_controllers", listOf("PX4", "ArduPilot", "ROS2", "CAN-Bus", "Micro-ROS", "STM32F4", "NVIDIA Jetson"))
                        put("auto_payload_scan", true)
                        put("flight_modes_available", listOf("MISSION", "AUTO_LAND", "FOLLOW_ME", "TERRAIN_SCAN", "COMBAT_READY"))
                    })
                    put("mission_code_snippet", """
                        // TÜBİTAK OTOMATİK DONANIM VE CİHAZ ADAPTASYON MOTORU
                        function onDeviceLoaded(deviceEnv) {
                            var detectedHw = deviceEnv.detectHardwareArchitecture();
                            if (detectedHw.isUAV()) {
                                deviceEnv.bindFlightTelemetry(3200, "CAN_BUS_0");
                                deviceEnv.setSafetyProtocol("TUBITAK_SAGE_V4");
                            } else if (detectedHw.isQuadruped()) {
                                deviceEnv.bindBipedalKinematics("INVERSE_KINEMATICS_4LEG");
                            } else if (detectedHw.isHumanoid()) {
                                deviceEnv.bindBipedalBalance("ZERO_MOMENT_POINT");
                            }
                            return "SYSTEM_OPTIMIZED_FOR_" + detectedHw.name;
                        }
                    """.trimIndent())
                }

                // Public Downloads klasörüne kaydet (.kdf uzantılı)
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val kdfFile = File(downloadsDir, fileName)
                kdfFile.writeText(kdfPayload.toString(4))

                // Dahili app dosyalarına da kopyala
                val internalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val internalFile = File(internalDir, fileName)
                internalFile.writeText(kdfPayload.toString(4))

                lastExportedFileName = fileName
                lastExportedFilePath = kdfFile.absolutePath

                // Listeyi güncelle
                availableKdfFiles = onboardScanner.findAvailableKdfFiles()
                selectedKdfFile = kdfFile

                isDownloadingKdf = false
                Toast.makeText(
                    context,
                    "✅ $fileName dosyası başarıyla İndirilenler klasörüne indirildi!",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                isDownloadingKdf = false
                Toast.makeText(context, "Hata: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Yetki Kontrolü: Yalnızca Administrator yetkisi olanlar kullanabilir
    if (!isAdmin) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF06030C))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(2.dp, Color(0xFFFF0055))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF0055).copy(alpha = 0.15f))
                            .border(2.dp, Color(0xFFFF0055), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Kilitli",
                            tint = Color(0xFFFF0055),
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Text(
                        text = "🔒 TÜBİTAK ÖZEL SAVUNMA BÖLMESİ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Bu bölme yalnızca yetkili EDMINASTÖR (Yönetici) kullanıcıları içindir.\nİnsansız Uçak, Robot Köpek, İnsansı Robot ve Dron kontrolü yüksek güvenlik kapsamındadır.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF14081E),
                        border = BorderStroke(1.dp, Color(0xFFFF0055).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFF0055), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Giriş için Edminastör panelinden yetki alınmalıdır.",
                                fontSize = 11.sp,
                                color = Color(0xFFFF88AA)
                            )
                        }
                    }

                    if (onBack != null) {
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0055)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Geri Dön", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        return
    }

    // YÖNETİCİ ARAYÜZÜ (TÜBİTAK SAVUNMA & ROBOTİK ONBOARD KOMUTA MERKEZİ)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF04060F))
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. ÜST BAŞLIK VE TÜBİTAK BRİFİNG KARTI
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(2.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonMagenta, FlameNeonOrange)))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF001F3F),
                                Color(0xFF1B032E),
                                Color(0xFF2E0C00)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (onBack != null) {
                                IconButton(onClick = onBack) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Geri",
                                        tint = Color.White
                                    )
                                }
                            }
                            Text(
                                text = "🇹🇷 TÜBİTAK ONBOARD SAVUNMA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonGreen.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, NeonGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(NeonGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ONBOARD AKTİF",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                )
                            }
                        }
                    }

                    Text(
                        text = "Şifreli .KDF paketlerini çözen, hedef donanımı (Dron, Robot Köpek, İnsansı Robot) otomatik algılayan ve arayüz bileşenlerinin kilidini açan Onboard Tarama Motoru.",
                        fontSize = 11.sp,
                        color = Color(0xFFB0C4DE),
                        lineHeight = 16.sp
                    )

                    // Yetkili Admin & Sensör Bilgisi
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "👑 Operatör: ${adminPhoneNumber ?: "+905016370015 (Kasım Çamurcu)"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Text(
                            text = "Sensörler: ${localHardwareProfile.availableSensors.size} Aktif",
                            fontSize = 10.sp,
                            color = NeonGreen
                        )
                    }
                }
            }
        }

        // =========================================================================
        // 2. ONBOARD TARAMA VE ŞİFRELİ DOSYA ÇÖZÜMLEME MOTORU (ANA YENİ YETENEK)
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070C1A)),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(NeonCyan, FlameNeonOrange)))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "🔍 ONBOARD TARAYICI & ŞİFRE ÇÖZÜCÜ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Şifreli .KDF Dosyasını Oku • Donanımı Tanı • Kilidi Aç",
                                fontSize = 10.sp,
                                color = NeonCyan
                            )
                        }
                    }

                    if (onboardScanResult != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NeonGreen.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, NeonGreen)
                        ) {
                            Text(
                                text = "KİLİT AÇILDI",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "İçe aktarılan şifreli savunma paketini tarayarak araç tipini (Dron, Robot Köpek, İnsansı Robot) otomatik olarak teşhis edin ve ilgili kontrol arayüzlerini anında aktifleştirin:",
                    fontSize = 11.sp,
                    color = Color(0xFFC8D3DF)
                )

                // Dosya Seçimi / Örnek Paket Seçici
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Buton 1: Dron Paketi Tara
                    OutlinedButton(
                        onClick = {
                            val pkg = onboardScanner.generateSamplePackage(DetectedHardwareClass.DRONE_UAV)
                            startOnboardScan(pkg)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("✈️ Dron/İHA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            Text("Paketi Çöz", fontSize = 9.sp, color = TextSecondary)
                        }
                    }

                    // Buton 2: Robot Köpek Paketi Tara
                    OutlinedButton(
                        onClick = {
                            val pkg = onboardScanner.generateSamplePackage(DetectedHardwareClass.ROBOT_DOG)
                            startOnboardScan(pkg)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, FlameNeonOrange.copy(alpha = 0.6f))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🐕 Robot Köpek", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FlameNeonOrange)
                            Text("Paketi Çöz", fontSize = 9.sp, color = TextSecondary)
                        }
                    }

                    // Buton 3: İnsansı Robot Paketi Tara
                    OutlinedButton(
                        onClick = {
                            val pkg = onboardScanner.generateSamplePackage(DetectedHardwareClass.HUMANOID)
                            startOnboardScan(pkg)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.6f))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🤖 İnsansı", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonMagenta)
                            Text("Paketi Çöz", fontSize = 9.sp, color = TextSecondary)
                        }
                    }
                }

                // Canlı Tarama İlerleme Göstergesi
                if (isScanningOnboard) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF030712), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = NeonCyan,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = scanStepMessage ?: "Onboard tarama sürüyor...",
                                    fontSize = 10.sp,
                                    color = NeonCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "${(scanProgressFraction * 100).toInt()}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        LinearProgressIndicator(
                            progress = { scanProgressFraction },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = NeonCyan,
                            trackColor = Color(0xFF1E293B)
                        )
                    }
                }

                // Tarama Sonucu & Teşhis Edilen Donanım Kartı
                onboardScanResult?.let { res ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0D1426),
                        border = BorderStroke(1.dp, res.hardwareClass.let {
                            when (it) {
                                DetectedHardwareClass.DRONE_UAV -> NeonCyan
                                DetectedHardwareClass.ROBOT_DOG -> FlameNeonOrange
                                DetectedHardwareClass.HUMANOID -> NeonMagenta
                                else -> NeonGreen
                            }
                        }),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎯 TESPİT EDİLEN DONANIM:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = NeonGreen.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "AES-256 ŞİFRE ÇÖZÜLDÜ",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonGreen,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = res.hardwareClass.titleTr,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Protokol Standardı:", fontSize = 10.sp, color = TextSecondary)
                                Text(res.hardwareClass.protocolStandard, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = NeonCyan)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Doğrulanan Görev Kodu:", fontSize = 10.sp, color = TextSecondary)
                                Text(res.missionCode, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = FlameNeonOrange)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Yetkili Merkez:", fontSize = 10.sp, color = TextSecondary)
                                Text(res.creatorAuthority, fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 3. KİLİDİ AÇILAN DONANIM ARAYÜZ BİLEŞENLERİ (UNLOCKED INTERFACE COMPONENTS)
        // =========================================================================
        if (unlockedComponentsList.isNotEmpty()) {
            Text(
                text = "🔓 KİLİDİ AÇILAN DONANIM BİLEŞENLERİ (${unlockedComponentsList.size} BİLEŞEN)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NeonGreen
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                unlockedComponentsList.forEach { comp ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreen.copy(alpha = 0.15f))
                                    .border(1.dp, NeonGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(comp.iconEmoji, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = comp.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF1E293B)
                                    ) {
                                        Text(
                                            text = comp.category,
                                            fontSize = 8.sp,
                                            color = NeonCyan,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = comp.description,
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    lineHeight = 14.sp
                                )
                            }
                            Icon(
                                Icons.Default.LockOpen,
                                contentDescription = "Açık",
                                tint = NeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 4. DONANIMA ÖZEL ETKİLEŞİMLİ KOMUTA & TELEMETRİ KONTROL PANELİ
        // =========================================================================
        Text(
            text = "🎮 AKTİF DONANIM KONTROL ARAYÜZÜ",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Araç Seçim Sekmesi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TubitakVehicleType.values().forEach { vehicle ->
                val isSelected = selectedVehicle == vehicle
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .clickable {
                            selectedVehicle = vehicle
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) vehicle.accentColor.copy(alpha = 0.2f) else DarkSurface
                    ),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) vehicle.accentColor else Color.Gray.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(vehicle.iconName, fontSize = 20.sp)
                            if (isSelected) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = vehicle.accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = vehicle.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = vehicle.subtitle,
                            fontSize = 8.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Ana Komuta Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.5.dp, selectedVehicle.accentColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Üst Başlık & Canlı Telemetri Aç/Kapa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${selectedVehicle.iconName} ${selectedVehicle.title}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "KOD: ${selectedVehicle.defaultCode}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = selectedVehicle.accentColor
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (activeTelemetryState) "CANLI YAYIN" else "BEKLEMEDE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTelemetryState) NeonGreen else TextSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = activeTelemetryState,
                            onCheckedChange = { activeTelemetryState = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = selectedVehicle.accentColor
                            )
                        )
                    }
                }

                // Canlı Göstergeler (Hız, İrtifa/Menzil, Güç)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hız
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF090A17))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("HIZ", fontSize = 9.sp, color = TextSecondary)
                            Text("$speedKmH km/s", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                    }

                    // İrtifa / Menzil
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF090A17))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (selectedVehicle.hardwareClass == DetectedHardwareClass.DRONE_UAV) "İRTİFA" else "MENZİL",
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                            Text("$altitudeMeters m", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonMagenta)
                        }
                    }

                    // Batarya
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF090A17))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("BATARYA", fontSize = 9.sp, color = TextSecondary)
                            Text("%$batteryPercentage", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                        }
                    }
                }

                // =========================================================================
                // DONANIMA ÖZEL KİLİDİ AÇILMIŞ ETKİLEŞİMLİ MODÜLLER
                // =========================================================================
                when (selectedVehicle.hardwareClass) {
                    DetectedHardwareClass.DRONE_UAV -> {
                        // 1. DRON & UÇAK KONTROL PANELİ
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Uçuş HUD Göstergesi
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF021226),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("PITCH (YUNUSLAMA)", fontSize = 9.sp, color = TextSecondary)
                                        Text("${String.format(Locale.US, "%.1f", pitchAngle)}°", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("ROLL (YATTIŞ)", fontSize = 9.sp, color = TextSecondary)
                                        Text("${String.format(Locale.US, "%.1f", rollAngle)}°", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("ÜSSE DÖNÜŞ (RTH)", fontSize = 9.sp, color = TextSecondary)
                                        Text(if (isRthActive) "OTONOM DÖNÜŞ" else "MANUEL ROTA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isRthActive) NeonGreen else TextSecondary)
                                    }
                                }
                            }

                            // Gaz / Türbin Kolu
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Türbin İtki Vektör Gücü:", fontSize = 11.sp, color = TextSecondary)
                                    Text("${enginePower.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = selectedVehicle.accentColor)
                                }
                                Slider(
                                    value = enginePower,
                                    onValueChange = { enginePower = it },
                                    valueRange = 0f..100f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = selectedVehicle.accentColor,
                                        activeTrackColor = selectedVehicle.accentColor
                                    )
                                )
                            }

                            // Taktik Hedef Kilidi & RTH Butonları
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { isRthActive = !isRthActive },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isRthActive) NeonGreen else Color(0xFF1E293B)
                                    )
                                ) {
                                    Icon(Icons.Default.Navigation, contentDescription = null, tint = if (isRthActive) Color.Black else Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isRthActive) "RTH İptal" else "Otonom RTH",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isRthActive) Color.Black else Color.White
                                    )
                                }

                                Button(
                                    onClick = { targetMissileLocked = !targetMissileLocked },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (targetMissileLocked) Color(0xFFFF0055) else Color(0xFF1E293B)
                                    )
                                ) {
                                    Icon(Icons.Default.GpsFixed, contentDescription = null, tint = if (targetMissileLocked) Color.White else NeonCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (targetMissileLocked) "Hedef Kilitlendi" else "Hedef Kilidi Aç",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    DetectedHardwareClass.ROBOT_DOG -> {
                        // 2. 4 AYAKLI ROBOT KÖPEK KONTROL PANELİ
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Taktik Yürüyüş Duruşu
                            Text("🐕 4 Ayaklı Yürüyüş & Kinematik Sekansı:", fontSize = 11.sp, color = TextSecondary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Tetikte (Patrol)", "Pusu / Çök", "Koşu (Sprint)", "Engel Atlama").forEach { posture ->
                                    val isCur = dogPostureState == posture
                                    OutlinedButton(
                                        onClick = { dogPostureState = posture },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, if (isCur) FlameNeonOrange else Color.Gray.copy(alpha = 0.3f)),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isCur) FlameNeonOrange.copy(alpha = 0.2f) else Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = posture.split(" ").first(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCur) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }

                            // Arazi Adaptasyon Modu
                            Text("⛰️ Arazi Uyumluluk Katmanı:", fontSize = 11.sp, color = TextSecondary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Asfalt / Düz Zemin", "Engebeli / Taşlık", "Merdiven Çıkışı").forEach { terrain ->
                                    val isCur = dogTerrainState == terrain
                                    OutlinedButton(
                                        onClick = { dogTerrainState = terrain },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, if (isCur) NeonCyan else Color.Gray.copy(alpha = 0.3f)),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isCur) NeonCyan.copy(alpha = 0.2f) else Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = terrain.split(" ").first(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCur) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }

                            // Gaz / KBRN Taraması Göstergesi
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1F1105),
                                border = BorderStroke(1.dp, FlameNeonOrange.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Thermostat, contentDescription = null, tint = FlameNeonOrange, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("KBRN / Gaz Taraması:", fontSize = 10.sp, color = TextSecondary)
                                    }
                                    Text(
                                        text = "$dogGasLevelPpm PPM (GÜVENLİ SEVİYE)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonGreen
                                    )
                                }
                            }
                        }
                    }

                    DetectedHardwareClass.HUMANOID -> {
                        // 3. İNSANSI ROBOT (HUMANOID) KONTROL PANELİ
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Görev Modu
                            Text("🤖 Biyomekanik Denge & Görev Modu:", fontSize = 11.sp, color = TextSecondary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Hazır Duruş", "Nesne Taşı", "Keşif Yürüyüşü", "Savunma Modu").forEach { gesture ->
                                    val isCur = humanoidGestureState == gesture
                                    OutlinedButton(
                                        onClick = { humanoidGestureState = gesture },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, if (isCur) NeonMagenta else Color.Gray.copy(alpha = 0.3f)),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isCur) NeonMagenta.copy(alpha = 0.2f) else Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = gesture.split(" ").first(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCur) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }

                            // 5 Parmaklı Tutucu (Gripper) Kavrama Gücü
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("5 Parmaklı Tutucu (Gripper) Basıncı:", fontSize = 11.sp, color = TextSecondary)
                                    Text("${humanoidGripStrength.toInt()} N (Hassas Tutuş)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonMagenta)
                                }
                                Slider(
                                    value = humanoidGripStrength,
                                    onValueChange = { humanoidGripStrength = it },
                                    valueRange = 0f..150f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonMagenta,
                                        activeTrackColor = NeonMagenta
                                    )
                                )
                            }

                            // ZMP Denge Göstergesi
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E0729),
                                border = BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ZMP İki Ayak Denge İndeksi:", fontSize = 10.sp, color = TextSecondary)
                                    }
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", humanoidBalanceZmp)}% (DENGELİ)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonGreen
                                    )
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }

        // =========================================================================
        // 5. .KDF (KEON DEFENSE FORMAT) FARKLI FORMATTA İNDİRME BÖLMESİ
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1023)),
            border = BorderStroke(1.5.dp, FlameNeonOrange)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = FlameNeonOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "📥 TELEFONA ÖZEL FORMATTA İNDİR (.KDF)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Keon Defense Format • Cihazlar arası akıllı transfer",
                                fontSize = 10.sp,
                                color = FlameNeonOrange
                            )
                        }
                    }
                }

                Text(
                    text = "Bu dosya telefona özel '.kdf' formatında indirilir. Dosyayı Bluetooth, Wi-Fi Direct veya USB ile başka bir cihaza (PC, Laptop, Robotik Kontrol Kartı, Tablet) attığınızda, onboard tarayıcı donanımı otomatik tespit eder ve arayüzü hazırlar.",
                    fontSize = 11.sp,
                    color = Color(0xFFD3D3D3),
                    lineHeight = 15.sp
                )

                if (isDownloadingKdf) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Özel Format Paketleniyor & İndiriliyor...", fontSize = 10.sp, color = FlameNeonOrange)
                            Text("${(downloadProgress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        LinearProgressIndicator(
                            progress = { downloadProgress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = FlameNeonOrange,
                            trackColor = Color.DarkGray
                        )
                    }
                }

                Button(
                    onClick = { exportAndDownloadKdfFile() },
                    enabled = !isDownloadingKdf,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FlameNeonOrange)
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${selectedVehicle.title} .KDF Dosyasını İndir",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                if (lastExportedFileName != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonGreen.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "✅ Telefona Kaydedildi: $lastExportedFileName",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                            Text(
                                text = "Konum: İndirilenler (Downloads) / $lastExportedFileName",
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
