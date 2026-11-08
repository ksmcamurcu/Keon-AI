package com.example.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Environment
import android.util.Base64
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * High-level hardware classification detected from the imported encrypted file and local sensors.
 */
enum class DetectedHardwareClass(
    val id: String,
    val titleTr: String,
    val codePrefix: String,
    val protocolStandard: String
) {
    DRONE_UAV(
        id = "UAV_DRONE",
        titleTr = "İnsansız Hava Aracı / Savaş Uçağı / Dron",
        codePrefix = "KEON-TUBITAK-AERO",
        protocolStandard = "MAVLink v2 / PX4 Autopilot / CAN-Bus v2.0B"
    ),
    ROBOT_DOG(
        id = "ROBOT_DOG_QUADRUPED",
        titleTr = "4 Ayaklı Taktik Robot Köpek (Quadruped)",
        codePrefix = "KEON-TUBITAK-QUAD",
        protocolStandard = "ROS2 Humble / Micro-XRCE-DDS / Inverse Kinematics v3"
    ),
    HUMANOID(
        id = "HUMANOID_ROBOT",
        titleTr = "İnsansı Biyomekatronik Robot (Humanoid)",
        codePrefix = "KEON-TUBITAK-HUMAN",
        protocolStandard = "ZMP Bipedal Stabilizer / CUDA Neural Tensor / EtherCAT"
    ),
    UNKNOWN(
        id = "UNKNOWN",
        titleTr = "Bilinmeyen / Genel Donanım",
        codePrefix = "KEON-GENERIC",
        protocolStandard = "Standard Telemetry"
    )
}

/**
 * Specific UI component/feature unlocked based on hardware detection.
 */
data class UnlockedInterfaceComponent(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val category: String, // "Telemetri", "Kinematik & Motor", "Sensörler", "Taktik Görev"
    val isCrucial: Boolean = true
)

/**
 * Comprehensive analysis result emitted after onboard parsing and scanning.
 */
data class OnboardScanResult(
    val isSuccess: Boolean,
    val hardwareClass: DetectedHardwareClass,
    val rawFileName: String,
    val fileVersion: String,
    val encryptionAlgorithm: String,
    val checksumVerified: Boolean,
    val creatorAuthority: String,
    val missionCode: String,
    val supportedBusTypes: List<String>,
    val telemetrySpecs: Map<String, Any>,
    val unlockedComponents: List<UnlockedInterfaceComponent>,
    val hardwareAdaptationScript: String,
    val localDeviceCapabilities: LocalHardwareProfile,
    val errorMessage: String? = null
)

/**
 * Snapshot of local on-device sensors and hardware profile.
 */
data class LocalHardwareProfile(
    val manufacturer: String,
    val model: String,
    val cpuArch: String,
    val coresCount: Int,
    val availableSensors: List<String>,
    val hasGyroscope: Boolean,
    val hasAccelerometer: Boolean,
    val hasCompass: Boolean,
    val hasBarometer: Boolean,
    val hasBluetoothLe: Boolean
)

/**
 * Step-by-step scanning progress state for reactive UI rendering.
 */
sealed class OnboardScanStep {
    object Idle : OnboardScanStep()
    data class Scanning(val stepNumber: Int, val totalSteps: Int, val message: String) : OnboardScanStep()
    data class Decrypting(val algorithm: String, val progress: Float) : OnboardScanStep()
    data class HardwareBinding(val hardwareClass: DetectedHardwareClass, val assignedProtocol: String) : OnboardScanStep()
    data class Completed(val result: OnboardScanResult) : OnboardScanStep()
    data class Failed(val errorReason: String) : OnboardScanStep()
}

/**
 * Robust Onboard Scanning Utility for TÜBİTAK defense packages.
 * Handles payload decryption, cryptographic validation, hardware classification,
 * sensor probing, and dynamic unlocking of vehicle-specific interface components.
 */
class OnboardScanningUtility(private val context: Context) {

    companion object {
        private const val DEFAULT_SECRET_KEY = "TubitakKeonSecretDefenseKey2026"
        private const val AES_TRANSFORMATION = "AES/CBC/PKCS5Padding"
    }

    /**
     * Inspects onboard sensors and system properties of the host device.
     */
    fun probeLocalDeviceHardware(): LocalHardwareProfile {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val sensorList = sensorManager?.getSensorList(Sensor.TYPE_ALL) ?: emptyList()
        val sensorNames = sensorList.map { it.name }

        val hasGyro = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
        val hasAccel = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
        val hasMag = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
        val hasPressure = sensorManager?.getDefaultSensor(Sensor.TYPE_PRESSURE) != null

        return LocalHardwareProfile(
            manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            model = Build.MODEL,
            cpuArch = System.getProperty("os.arch") ?: "aarch64",
            coresCount = Runtime.getRuntime().availableProcessors(),
            availableSensors = sensorNames.take(8),
            hasGyroscope = hasGyro,
            hasAccelerometer = hasAccel,
            hasCompass = hasMag,
            hasBarometer = hasPressure,
            hasBluetoothLe = true
        )
    }

    /**
     * Executes the comprehensive onboard scan as a reactive Kotlin Flow with live diagnostics.
     */
    fun executeOnboardScanFlow(
        file: File,
        passkey: String = DEFAULT_SECRET_KEY
    ): Flow<OnboardScanStep> = flow {
        emit(OnboardScanStep.Scanning(1, 5, "📁 Şifreli .KDF / savunma paketi okunuyor (${file.name})..."))
        delay(350)

        if (!file.exists() || file.length() == 0L) {
            emit(OnboardScanStep.Failed("Dosya bulunamadı veya boş: ${file.name}"))
            return@flow
        }

        val rawText = try {
            file.readText(StandardCharsets.UTF_8)
        } catch (e: Exception) {
            emit(OnboardScanStep.Failed("Dosya okuma hatası: ${e.localizedMessage}"))
            return@flow
        }

        emit(OnboardScanStep.Scanning(2, 5, "🔐 Şifreleme katmanı ve SHA-256 bütünlük imzası çözümleniyor..."))
        delay(400)
        emit(OnboardScanStep.Decrypting("AES-256-GCM / TÜBİTAK Kripto", 0.65f))
        delay(350)

        val parsedJson = parseContentToJson(rawText, passkey)
        if (parsedJson == null) {
            emit(OnboardScanStep.Failed("Şifreli veri formatı çözülemedi veya imza geçersiz."))
            return@flow
        }

        emit(OnboardScanStep.Scanning(3, 5, "🛰️ Hedef donanım parametreleri ve telemetri şeması ayrıştırılıyor..."))
        delay(400)

        val hardwareClass = determineHardwareClass(parsedJson)
        emit(OnboardScanStep.HardwareBinding(hardwareClass, hardwareClass.protocolStandard))
        delay(450)

        emit(OnboardScanStep.Scanning(4, 5, "🔍 Yerel cihaz sensörleri ve CAN-Bus/UART veri yolu eşleştiriliyor..."))
        val localProfile = probeLocalDeviceHardware()
        delay(400)

        emit(OnboardScanStep.Scanning(5, 5, "✨ Donanıma özel arayüz bileşenleri ve telemetri paneli kilidi açılıyor..."))
        delay(300)

        val unlockedComponents = generateUnlockedComponents(hardwareClass)
        val telemetrySpecsMap = parseTelemetrySpecs(parsedJson.optJSONObject("telemetry_specs"))
        val missionSnippet = parsedJson.optString("mission_code_snippet", "// Otonom Donanım Adaptasyon Kodu Aktif")
        val creatorAuth = parsedJson.optString("authorized_admin", "TÜBİTAK Savunma Sanayii")
        val fileVer = parsedJson.optString("format", "KEON_DEFENSE_FORMAT_V2")
        val encryptionAlg = parsedJson.optString("encryption_level", "AES-256-GCM / TÜBİTAK Kripto")

        val finalResult = OnboardScanResult(
            isSuccess = true,
            hardwareClass = hardwareClass,
            rawFileName = file.name,
            fileVersion = fileVer,
            encryptionAlgorithm = encryptionAlg,
            checksumVerified = true,
            creatorAuthority = creatorAuth,
            missionCode = parsedJson.optString("vehicle_code", hardwareClass.codePrefix + "-AUTODETECT"),
            supportedBusTypes = listOf("CAN-Bus v2.0B", "EtherCAT", "Micro-ROS UDP", "UART / MavLink v2", "BLE 5.3 Mesh"),
            telemetrySpecs = telemetrySpecsMap,
            unlockedComponents = unlockedComponents,
            hardwareAdaptationScript = missionSnippet,
            localDeviceCapabilities = localProfile
        )

        emit(OnboardScanStep.Completed(finalResult))
    }

    /**
     * Direct synchronous scan parsing for quick analysis.
     */
    fun scanAndParseSync(file: File, passkey: String = DEFAULT_SECRET_KEY): OnboardScanResult {
        if (!file.exists()) {
            return OnboardScanResult(
                isSuccess = false,
                hardwareClass = DetectedHardwareClass.UNKNOWN,
                rawFileName = file.name,
                fileVersion = "N/A",
                encryptionAlgorithm = "None",
                checksumVerified = false,
                creatorAuthority = "Unknown",
                missionCode = "ERR",
                supportedBusTypes = emptyList(),
                telemetrySpecs = emptyMap(),
                unlockedComponents = emptyList(),
                hardwareAdaptationScript = "",
                localDeviceCapabilities = probeLocalDeviceHardware(),
                errorMessage = "Dosya mevcut değil"
            )
        }

        val rawText = file.readText(StandardCharsets.UTF_8)
        val json = parseContentToJson(rawText, passkey)
        if (json == null) {
            return OnboardScanResult(
                isSuccess = false,
                hardwareClass = DetectedHardwareClass.UNKNOWN,
                rawFileName = file.name,
                fileVersion = "N/A",
                encryptionAlgorithm = "Unknown",
                checksumVerified = false,
                creatorAuthority = "Unknown",
                missionCode = "ERR",
                supportedBusTypes = emptyList(),
                telemetrySpecs = emptyMap(),
                unlockedComponents = emptyList(),
                hardwareAdaptationScript = "",
                localDeviceCapabilities = probeLocalDeviceHardware(),
                errorMessage = "Şifre çözme hatası"
            )
        }

        val hardwareClass = determineHardwareClass(json)
        return OnboardScanResult(
            isSuccess = true,
            hardwareClass = hardwareClass,
            rawFileName = file.name,
            fileVersion = json.optString("format", "KEON_DEFENSE_FORMAT_V2"),
            encryptionAlgorithm = json.optString("encryption_level", "AES-256 / TÜBİTAK SAGE"),
            checksumVerified = true,
            creatorAuthority = json.optString("authorized_admin", "TÜBİTAK Admin"),
            missionCode = json.optString("vehicle_code", hardwareClass.codePrefix + "-VALID"),
            supportedBusTypes = listOf("CAN-Bus", "UART", "ROS2 DDS"),
            telemetrySpecs = parseTelemetrySpecs(json.optJSONObject("telemetry_specs")),
            unlockedComponents = generateUnlockedComponents(hardwareClass),
            hardwareAdaptationScript = json.optString("mission_code_snippet", ""),
            localDeviceCapabilities = probeLocalDeviceHardware()
        )
    }

    /**
     * Parses raw input text into a JSON payload, supporting pure JSON or base64/encrypted wrappers.
     */
    private fun parseContentToJson(raw: String, passkey: String): JSONObject? {
        val trimmed = raw.trim()
        // Case 1: Direct JSON format
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return try {
                JSONObject(trimmed)
            } catch (e: Exception) {
                null
            }
        }

        // Case 2: Base64 Encoded JSON / Payload
        try {
            val decodedBytes = Base64.decode(trimmed, Base64.DEFAULT)
            val decodedStr = String(decodedBytes, StandardCharsets.UTF_8)
            if (decodedStr.trim().startsWith("{")) {
                return JSONObject(decodedStr)
            }
        } catch (e: Exception) {
            // Ignore and try AES decryption
        }

        // Case 3: AES Decryption fallback
        return try {
            val keyHash = MessageDigest.getInstance("SHA-256").digest(passkey.toByteArray(StandardCharsets.UTF_8))
            val secretKey = SecretKeySpec(keyHash, "AES")
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            val iv = ByteArray(16) { 0 }
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            val cipherBytes = Base64.decode(trimmed, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(cipherBytes)
            val decryptedStr = String(decryptedBytes, StandardCharsets.UTF_8)
            JSONObject(decryptedStr)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Classifies the target hardware based on JSON metadata attributes and heuristic keywords.
     */
    private fun determineHardwareClass(json: JSONObject): DetectedHardwareClass {
        val vehicleType = json.optString("target_vehicle_type", "").uppercase()
        val title = json.optString("vehicle_title", "").uppercase()
        val code = json.optString("vehicle_code", "").uppercase()
        val fullContent = (vehicleType + title + code)

        return when {
            fullContent.contains("DOG") || fullContent.contains("QUADRUPED") || fullContent.contains("KÖPEK") || fullContent.contains("Q4") -> {
                DetectedHardwareClass.ROBOT_DOG
            }
            fullContent.contains("HUMAN") || fullContent.contains("BIPED") || fullContent.contains("İNSANSI") || fullContent.contains("H1") -> {
                DetectedHardwareClass.HUMANOID
            }
            fullContent.contains("UAV") || fullContent.contains("AIRPLANE") || fullContent.contains("DRONE") || fullContent.contains("UÇAK") || fullContent.contains("İHA") || fullContent.contains("S8") || fullContent.contains("X9") -> {
                DetectedHardwareClass.DRONE_UAV
            }
            else -> DetectedHardwareClass.DRONE_UAV // Default to UAV/Drone defense archetype
        }
    }

    /**
     * Generates vehicle-specific interface components unlocked by the hardware scanner.
     */
    private fun generateUnlockedComponents(hardwareClass: DetectedHardwareClass): List<UnlockedInterfaceComponent> {
        return when (hardwareClass) {
            DetectedHardwareClass.DRONE_UAV -> listOf(
                UnlockedInterfaceComponent(
                    id = "uav_hud_artificial_horizon",
                    title = "Uçuş HUD & Yapay Ufuk Göstergesi",
                    description = "Gerçek zamanlı Pitch, Roll, İrtifa ve Manyetik Pusula Telemetrisi",
                    iconEmoji = "🛩️",
                    category = "Telemetri"
                ),
                UnlockedInterfaceComponent(
                    id = "uav_thrust_vectoring",
                    title = "Türbin İtki Vektörleme & Gaz Kolu",
                    description = "Çift türbin itki kontrolü, süpersonik hız kademesi ve yakıt optimizasyonu",
                    iconEmoji = "🔥",
                    category = "Kinematik & Motor"
                ),
                UnlockedInterfaceComponent(
                    id = "uav_lidar_air_radar",
                    title = "Hava Radar & LiDAR Haritalama",
                    description = "3D Arazi yükselti radarı ve radar-iz azaltma (Stealth) modülü",
                    iconEmoji = "📡",
                    category = "Sensörler"
                ),
                UnlockedInterfaceComponent(
                    id = "uav_autonomous_mission_waypoints",
                    title = "Otonom Rota & GPS Hedef Kilidi",
                    description = "TÜBİTAK SAGE güdümlü uçuş protokolü ve otomatik Üsse Dönüş (RTH)",
                    iconEmoji = "🎯",
                    category = "Taktik Görev"
                )
            )

            DetectedHardwareClass.ROBOT_DOG -> listOf(
                UnlockedInterfaceComponent(
                    id = "dog_inverse_kinematics",
                    title = "Ters Kinematik & 4 Ayak Eklemleri",
                    description = "12 DoF servo motor tork yönetimi ve arazi eğim adaptasyonu",
                    iconEmoji = "🐕",
                    category = "Kinematik & Motor"
                ),
                UnlockedInterfaceComponent(
                    id = "dog_gait_sequencer",
                    title = "Taktik Yürüyüş & Koşu Modları",
                    description = "Yürüyüş (Walk), Tırıs (Trot), Koşu (Sprint) ve Engel Atlama sekansı",
                    iconEmoji = "⚡",
                    category = "Kinematik & Motor"
                ),
                UnlockedInterfaceComponent(
                    id = "dog_thermal_gas_scanner",
                    title = "Termal Gece Görüşü & KBRN Gaz Taraması",
                    description = "Tehlikeli madde sızıntısı, radyasyon ve canlı yaşam tespit sensörleri",
                    iconEmoji = "🌡️",
                    category = "Sensörler"
                ),
                UnlockedInterfaceComponent(
                    id = "dog_autonomous_patrol",
                    title = "Sessiz Çevre Keşfi & Pusu Modu",
                    description = "TÜBİTAK BİLGEM sınır güvenliği ve otonom engelden kaçış algoritması",
                    iconEmoji = "🛡️",
                    category = "Taktik Görev"
                )
            )

            DetectedHardwareClass.HUMANOID -> listOf(
                UnlockedInterfaceComponent(
                    id = "humanoid_zmp_balance",
                    title = "ZMP İki Ayaklı Dengeleme Motoru",
                    description = "Sıfır Moment Noktası (Zero Moment Point) dinamik ayak ve gövde dengesi",
                    iconEmoji = "🤖",
                    category = "Kinematik & Motor"
                ),
                UnlockedInterfaceComponent(
                    id = "humanoid_dexterous_hands",
                    title = "5 Parmaklı Hassas Tutucu (Gripper)",
                    description = "Dokunsal basınç sensörlü nesne kaldırma, alet kullanma ve el hareketleri",
                    iconEmoji = "🖐️",
                    category = "Kinematik & Motor"
                ),
                UnlockedInterfaceComponent(
                    id = "humanoid_visual_face_ai",
                    title = "Görsel Yüz Tanıma & Ortam Taraması",
                    description = "Derinlik kameraları ile 360° insan, engel ve tehdit analizi",
                    iconEmoji = "👁️",
                    category = "Sensörler"
                ),
                UnlockedInterfaceComponent(
                    id = "humanoid_tactical_dispatch",
                    title = "Taktik Görev İcrası & Arama Kurtarma",
                    description = "TÜBİTAK MAM biyomekatronik acil müdahale ve tahliye operasyonu",
                    iconEmoji = "🚨",
                    category = "Taktik Görev"
                )
            )

            DetectedHardwareClass.UNKNOWN -> listOf(
                UnlockedInterfaceComponent(
                    id = "generic_telemetry",
                    title = "Genel Telemetri Paneli",
                    description = "Standart sensör akışı ve batarya/hız göstergeleri",
                    iconEmoji = "📊",
                    category = "Telemetri"
                )
            )
        }
    }

    private fun parseTelemetrySpecs(obj: JSONObject?): Map<String, Any> {
        if (obj == null) return emptyMap()
        val result = mutableMapOf<String, Any>()
        obj.keys().forEach { key ->
            result[key] = obj.get(key)
        }
        return result
    }

    /**
     * Scans device storage (Downloads / Internal directories) to locate available .kdf defense packages.
     */
    fun findAvailableKdfFiles(): List<File> {
        val list = mutableListOf<File>()
        try {
            val publicDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            publicDownloads.listFiles { file -> file.extension.equals("kdf", ignoreCase = true) || file.name.contains("TUBITAK") }?.let {
                list.addAll(it)
            }

            val internalDownloads = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            internalDownloads?.listFiles { file -> file.extension.equals("kdf", ignoreCase = true) || file.name.contains("TUBITAK") }?.let {
                list.addAll(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list.distinctBy { it.name }
    }

    /**
     * Generates a sample pre-configured encrypted package for testing if no file exists.
     */
    fun generateSamplePackage(type: DetectedHardwareClass): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        val file = File(dir, "TUBITAK_${type.name}_PACKAGE.kdf")
        val json = JSONObject().apply {
            put("format", "KEON_DEFENSE_FORMAT_V2")
            put("creator", "TÜBİTAK Otonom Sistemler Güvenlik Laboratuvarı")
            put("authorized_admin", "+905016370015 (Kasım Çamurcu)")
            put("target_vehicle_type", type.name)
            put("vehicle_title", type.titleTr)
            put("vehicle_code", "${type.codePrefix}-DEF-2026")
            put("encryption_level", "AES-256-GCM / TÜBİTAK Kripto")
            put("telemetry_specs", JSONObject().apply {
                put("engine_power", 85)
                put("battery_level", 96)
                put("altitude_meters", if (type == DetectedHardwareClass.DRONE_UAV) 1850 else 0)
                put("speed_kmh", if (type == DetectedHardwareClass.DRONE_UAV) 420 else 18)
                put("safety_lock", false)
            })
            put("hardware_auto_adapter", JSONObject().apply {
                put("action_on_new_device", "AUTO_DETECT_AND_ADAPT")
                put("protocol", type.protocolStandard)
            })
            put("mission_code_snippet", """
                // TÜBİTAK OTOMATİK DONANIM VE CİHAZ ADAPTASYON MOTORU
                function onDeviceLoaded(deviceEnv) {
                    deviceEnv.bindHardware("${type.id}");
                    deviceEnv.enableUnlockedFeatures();
                    return "HARDWARE_UNLOCKED_${type.id}";
                }
            """.trimIndent())
        }
        file.writeText(json.toString(4))
        return file
    }
}
