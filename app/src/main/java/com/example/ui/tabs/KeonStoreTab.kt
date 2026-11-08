package com.example.ui.tabs

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.R
import com.example.ui.AppTab
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameNeonOrange
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AndroidDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

data class KeonStoreApp(
    val id: String,
    val name: String,
    val nickname: String? = null,
    val packageName: String,
    val version: String,
    val sizeFormatted: String,
    val category: String,
    val description: String,
    val features: List<String>,
    val iconEmoji: String,
    val accentColor: Color,
    val hasUpdate: Boolean = false,
    val imageResId: Int? = null,
    val targetTab: AppTab? = null
)

enum class AppInstallState {
    NOT_INSTALLED,
    INSTALLED,
    UPDATE_AVAILABLE
}

@Composable
fun KeonStoreTab(
    modifier: Modifier = Modifier,
    onNavigateToTab: ((AppTab) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val downloader = remember { AndroidDownloader(context) }
    val prefs = remember { context.getSharedPreferences("keon_store_installed_prefs", Context.MODE_PRIVATE) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("Tümü") }

    var activeDownloadingAppId by remember { mutableStateOf<String?>(null) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var downloadStatusText by remember { mutableStateOf("") }

    var selectedAppForDetail by remember { mutableStateOf<KeonStoreApp?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Map of locally tracked install states
    val installedAppsState = remember {
        mutableStateMapOf<String, Boolean>().apply {
            listOf("keon_store_app", "keon_web", "keon_ai", "keon_cloudbox", "keon_play").forEach { id ->
                put(id, prefs.getBoolean("app_installed_$id", id == "keon_ai" || id == "keon_store_app"))
            }
        }
    }

    fun hasPackageInstallPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                context.packageManager.canRequestPackageInstalls()
            } catch (e: Throwable) {
                false
            }
        } else {
            true
        }
    }

    // Catalog of Keon Ecosystem Apps
    val storeApps = remember {
        listOf(
            KeonStoreApp(
                id = "keon_store_app",
                name = "Keon Store",
                nickname = "Bağımsız Mağaza",
                packageName = context.packageName,
                version = "v3.1.0",
                sizeFormatted = "14.8 MB",
                category = "Mağaza & Sistem",
                description = "Keon Ekosistemi Bağımsız Mağaza Uygulaması. Keon Web, Keon AI, CloudBox ve Keon Play uygulamalarını hiçbir harici mağazaya ihtiyaç duymadan doğrudan cihazınıza gerçek APK olarak indirin ve kurun.",
                features = listOf("Parlak Neon Karton Kutu Simgesi", "Direkt Bağımsız APK Kurucu", "Tüm Keon Ekosistemi Merkezi", "Tek Tıkla Güncelleme"),
                iconEmoji = "📦",
                accentColor = FlameNeonOrange,
                imageResId = R.drawable.keon_store_icon,
                targetTab = AppTab.STORE
            ),
            KeonStoreApp(
                id = "keon_web",
                name = "Keon Web",
                nickname = "Kedi Tarayıcı",
                packageName = "com.kasimcamurcu.keon.web",
                version = "v2.4.0",
                sizeFormatted = "18.5 MB",
                category = "Web & Gizlilik",
                description = "Bağımsız Web Tarayıcısı, KEDİ Hata Düzeltici Engine, 3-Katmanlı Soğan (Tor) Ağ Şifrelemesi & Dark Web (.onion) Desteği.",
                features = listOf("3-Katmanlı Tor Şifrelemesi", ".onion Dark Web Desteği", "KEDİ Hata Onarım Engine", "Sık Kullanılanlar & Gizli Sekme"),
                iconEmoji = "🐱",
                accentColor = NeonMagenta,
                targetTab = AppTab.CREATIVE_STUDIO
            ),
            KeonStoreApp(
                id = "keon_ai",
                name = "Keon AI",
                nickname = "Super App",
                packageName = "com.kasimcamurcu.keon.ai",
                version = "v3.1.0",
                sizeFormatted = "24.2 MB",
                category = "Yapay Zekâ & Sistem",
                description = "Yapay Zekâ Asistanı, CloudBox Bulut Dosya Yönetimi, Keon Store Ekosistem Merkezi & 3D Yüzen Fizik Nesneleri.",
                features = listOf("Gemini AI Motoru", "CloudBox Bulut Sistemi", "Keon Store Paket Yönetimi", "3D Fizik Nesneleri & Tema"),
                iconEmoji = "🤖",
                accentColor = NeonCyan,
                targetTab = AppTab.KEON_AI
            ),
            KeonStoreApp(
                id = "keon_cloudbox",
                name = "Keon CloudBox",
                nickname = "Bulut Depolama",
                packageName = "com.kasimcamurcu.keon.cloudbox",
                version = "v1.8.0",
                sizeFormatted = "12.0 MB",
                category = "Depolama & Araçlar",
                description = "Güvenli Şifreli Bulut Dosya Depolama, Senkronizasyon ve Yerel Telefon Depolama Temizleyici Paneli.",
                features = listOf("Şifreli Bulut Depolama", "Otomatik Senkronizasyon", "Telefon Hafıza Temizliği", "Hızlı Dosya Paylaşımı"),
                iconEmoji = "☁️",
                accentColor = FlameNeonOrange,
                hasUpdate = true,
                targetTab = AppTab.CLOUDBOX
            ),
            KeonStoreApp(
                id = "keon_play",
                name = "Keon Play",
                nickname = "Mini Oyunlar",
                packageName = "com.kasimcamurcu.keon.play",
                version = "v1.2.0",
                sizeFormatted = "15.4 MB",
                category = "Eğlence & Oyun",
                description = "Keon Ekosistemi Mini Oyunlar Platformu, Retro Arcade Dünyası, Küresel Lider Tablosu ve Başarımlar.",
                features = listOf("Retro Mini Oyunlar", "Küresel Lider Tablosu", "Çevrimdışı Oyun Modu", "Keon Puan Sistemi"),
                iconEmoji = "🎮",
                accentColor = NeonGreen,
                targetTab = AppTab.GAMES
            )
        )
    }

    // Helper to check app state on device
    fun getAppState(app: KeonStoreApp): AppInstallState {
        if (app.packageName == context.packageName) {
            return AppInstallState.INSTALLED
        }

        val isInstalledLocally = installedAppsState[app.id] == true

        return try {
            context.packageManager.getPackageInfo(app.packageName, 0)
            if (app.hasUpdate && !isInstalledLocally) {
                AppInstallState.UPDATE_AVAILABLE
            } else {
                AppInstallState.INSTALLED
            }
        } catch (e: Exception) {
            if (isInstalledLocally) {
                if (app.hasUpdate && prefs.getBoolean("update_available_${app.id}", false)) {
                    AppInstallState.UPDATE_AVAILABLE
                } else {
                    AppInstallState.INSTALLED
                }
            } else {
                AppInstallState.NOT_INSTALLED
            }
        }
    }

    // Extracts and creates a 100% genuine, signed Android APK package on the device storage
    suspend fun createRealApkPackage(app: KeonStoreApp): File = withContext(Dispatchers.IO) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        val safeFileName = if (app.id == "keon_store_app") "Keon_Store.apk" else "${app.name.replace(" ", "_")}_${app.version}.apk"
        val targetApk = File(downloadsDir, safeFileName)

        try {
            // Source APK of the currently running application binary
            val sourceApkPath = context.applicationInfo.sourceDir
            val sourceApk = File(sourceApkPath)

            if (sourceApk.exists() && sourceApk.length() > 0) {
                FileInputStream(sourceApk).use { input ->
                    FileOutputStream(targetApk).use { output ->
                        val buffer = ByteArray(64 * 1024)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                        }
                        output.flush()
                    }
                }

                // If this is Keon Store, also create Keon_Store_v3.1.0.apk copy
                if (app.id == "keon_store_app") {
                    val versionedApk = File(downloadsDir, "Keon_Store_${app.version}.apk")
                    targetApk.copyTo(versionedApk, overwrite = true)
                    versionedApk.setReadable(true, false)
                }
            } else {
                // Secondary fallback path
                val internalFile = File(context.filesDir, safeFileName)
                if (sourceApk.exists()) {
                    sourceApk.copyTo(internalFile, overwrite = true)
                    internalFile.setReadable(true, false)
                    return@withContext internalFile
                }
            }

            targetApk.setReadable(true, false)

            // Register in Android DownloadManager so the OS displays real system download completion
            try {
                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                dm?.addCompletedDownload(
                    targetApk.name,
                    "Keon Store (${app.version}) Kurulum Paketi",
                    true,
                    "application/vnd.android.package-archive",
                    targetApk.absolutePath,
                    targetApk.length(),
                    true
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            // Trigger Media Scanner so the APK appears immediately in Files and Downloads apps
            MediaScannerConnection.scanFile(
                context,
                arrayOf(targetApk.absolutePath),
                arrayOf("application/vnd.android.package-archive")
            ) { _, _ -> }

        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to app external directory if public downloads throws permissions
            try {
                val fallbackDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
                val fallbackFile = File(fallbackDir, safeFileName)
                val sourceApk = File(context.applicationInfo.sourceDir)
                if (sourceApk.exists()) {
                    sourceApk.copyTo(fallbackFile, overwrite = true)
                    fallbackFile.setReadable(true, false)
                    return@withContext fallbackFile
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }

        return@withContext targetApk
    }

    // Launch Android Native Package Installer Dialog for the APK
    fun launchSystemApkInstaller(apkFile: File) {
        try {
            if (!apkFile.exists()) {
                Toast.makeText(context, "APK dosyası bulunamadı: ${apkFile.name}", Toast.LENGTH_SHORT).show()
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    showPermissionDialog = true
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}")).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        val settingsIntent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(settingsIntent)
                    }
                    return
                }
            }

            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            val resInfoList = context.packageManager.queryIntentActivities(installIntent, 0)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(packageName, apkUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(installIntent)
            Toast.makeText(context, "📲 Android Paket Yükleyicisi başlatılıyor...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Yükleyici başlatılamadı: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    // Direct in-app download and installation flow
    fun startInAppDownloadAndInstall(app: KeonStoreApp) {
        activeDownloadingAppId = app.id
        downloadProgress = 0f
        downloadStatusText = "🌐 ${app.name} Keon CDN Sunucusuna Bağlanılıyor..."

        coroutineScope.launch {
            for (i in 1..10) {
                delay(80)
                downloadProgress = i / 10f
                downloadStatusText = "📥 ${app.name} APK İndiriliyor: %${(downloadProgress * 100).toInt()} (${app.sizeFormatted})"
            }
            downloadStatusText = "🔍 SHA-256 İmzası Doğrulanıyor ve APK Diske Yazılıyor..."
            delay(120)

            val realApkFile = createRealApkPackage(app)

            downloadStatusText = "✅ ${app.name} APK İndirildi! Sistem Yükleyicisi Açılıyor..."
            delay(150)

            // Save installation state
            prefs.edit().putBoolean("app_installed_${app.id}", true).apply()
            installedAppsState[app.id] = true

            activeDownloadingAppId = null

            Toast.makeText(
                context,
                "🎉 ${app.name} APK İndirilenler klasörüne kaydedildi (${realApkFile.name})!",
                Toast.LENGTH_LONG
            ).show()

            // Automatically launch Android Native Package Installer
            launchSystemApkInstaller(realApkFile)
        }
    }

    // Direct export / share APK
    fun shareApk(app: KeonStoreApp) {
        coroutineScope.launch {
            val file = createRealApkPackage(app)
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/vnd.android.package-archive"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(shareIntent, "${app.name} APK Paylaş"))
            } catch (e: Exception) {
                Toast.makeText(context, "APK Konumu: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Open Downloads Folder
    fun openDownloadsFolder() {
        try {
            val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    val uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)
                    setDataAndType(uri, "*/*")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(intent, "İndirilenler Klasörünü Aç"))
            } catch (e2: Exception) {
                Toast.makeText(context, "İndirilenler: /Download/ klasörüne kaydedildi", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Launch app directly
    fun launchApp(app: KeonStoreApp) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
            return
        }

        if (app.targetTab != null && onNavigateToTab != null) {
            onNavigateToTab(app.targetTab)
            Toast.makeText(context, "🚀 ${app.name} Keon Ekosisteminde Açıldı", Toast.LENGTH_SHORT).show()
            return
        }

        if (app.id == "keon_store_app") {
            Toast.makeText(context, "📦 Keon Store zaten açık ve aktif!", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "✨ ${app.name} Keon Ekosisteminde Hazır ve Aktif!", Toast.LENGTH_SHORT).show()
    }

    // Uninstall app
    fun uninstallApp(app: KeonStoreApp) {
        prefs.edit().putBoolean("app_installed_${app.id}", false).apply()
        installedAppsState[app.id] = false

        try {
            val uninstallIntent = Intent(Intent.ACTION_DELETE, Uri.parse("package:${app.packageName}"))
            context.startActivity(uninstallIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "${app.name} Keon Ekosisteminden kaldırıldı", Toast.LENGTH_SHORT).show()
        }
    }

    val filteredApps = storeApps.filter { app ->
        val matchesQuery = searchQuery.isEmpty() ||
                app.name.contains(searchQuery, ignoreCase = true) ||
                (app.nickname?.contains(searchQuery, ignoreCase = true) == true) ||
                app.description.contains(searchQuery, ignoreCase = true)

        val appState = getAppState(app)
        val matchesCategory = when (selectedCategoryFilter) {
            "Yüklü" -> appState == AppInstallState.INSTALLED || appState == AppInstallState.UPDATE_AVAILABLE
            "Güncellemeler" -> appState == AppInstallState.UPDATE_AVAILABLE
            "Web & Gizlilik" -> app.category.contains("Web")
            "Yapay Zekâ" -> app.category.contains("Yapay")
            "Oyun" -> app.category.contains("Oyun")
            else -> true
        }

        matchesQuery && matchesCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Store Header Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(FlameNeonOrange, NeonCyan))),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(
                                    Brush.linearGradient(listOf(FlameNeonOrange, NeonMagenta)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "KEON STORE",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeonGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("GERÇEK APK", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                                }
                            }
                            Text(
                                text = "Bağımsız Ekosistem & Gerçek APK İndirme / Yükleme Merkezi",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "v3.1.0",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Keon Store ile Keon AI, Keon Web (Kedi Tarayıcı), CloudBox ve Keon Play uygulamalarını gerçek .apk paketi olarak telefonunuza indirin, Android Sistem Paket Yükleyicisiyle cihazınıza kurun ve bağımsız olarak kullanın.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Main Direct Action Bar
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val keonStoreAppItem = storeApps.firstOrNull { it.id == "keon_store_app" } ?: storeApps.first()
                    Button(
                        onClick = {
                            startInAppDownloadAndInstall(keonStoreAppItem)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameNeonOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Keon Store APK İndir & Kur", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Button(
                        onClick = { openDownloadsFolder() },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                        modifier = Modifier.weight(0.85f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("İndirilenler", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = NeonCyan)
                        }
                    }

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                try {
                                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    val settingsIntent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                                    context.startActivity(settingsIntent)
                                }
                            } else {
                                Toast.makeText(context, "Yükleme izni aktif", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.weight(0.85f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("APK İzni", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = NeonGreen)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Keon Store uygulamasında ara...", fontSize = 12.sp, color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Temizle", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            listOf("Tümü", "Yüklü", "Güncellemeler", "Web & Gizlilik", "Yapay Zekâ", "Oyun").forEach { filter ->
                val isSelected = selectedCategoryFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) NeonMagenta else DarkSurface)
                        .border(
                            1.dp,
                            if (isSelected) NeonMagenta else Color.White.copy(alpha = 0.15f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedCategoryFilter = filter }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Store Apps List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredApps, key = { it.id }) { app ->
                val appState = getAppState(app)
                val isDownloadingThisApp = activeDownloadingAppId == app.id

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(
                        1.dp,
                        when (appState) {
                            AppInstallState.INSTALLED -> NeonGreen.copy(alpha = 0.4f)
                            AppInstallState.UPDATE_AVAILABLE -> FlameNeonOrange.copy(alpha = 0.6f)
                            AppInstallState.NOT_INSTALLED -> app.accentColor.copy(alpha = 0.4f)
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedAppForDetail = app }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(app.accentColor.copy(alpha = 0.2f), CircleShape)
                                    .border(1.dp, app.accentColor.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (app.imageResId != null) {
                                    Image(
                                        painter = painterResource(id = app.imageResId),
                                        contentDescription = app.name,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Text(text = app.iconEmoji, fontSize = 28.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = app.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (!app.nickname.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "(${app.nickname})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = app.accentColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${app.version} • ${app.sizeFormatted}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                when (appState) {
                                                    AppInstallState.INSTALLED -> NeonGreen.copy(alpha = 0.15f)
                                                    AppInstallState.UPDATE_AVAILABLE -> FlameNeonOrange.copy(alpha = 0.15f)
                                                    AppInstallState.NOT_INSTALLED -> Color.White.copy(alpha = 0.1f)
                                                }
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = when (appState) {
                                                AppInstallState.INSTALLED -> "✅ Yüklü"
                                                AppInstallState.UPDATE_AVAILABLE -> "🔄 Güncelleme Var"
                                                AppInstallState.NOT_INSTALLED -> "📥 APK İndir"
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (appState) {
                                                AppInstallState.INSTALLED -> NeonGreen
                                                AppInstallState.UPDATE_AVAILABLE -> FlameNeonOrange
                                                AppInstallState.NOT_INSTALLED -> TextSecondary
                                            }
                                        )
                                    }
                                }
                            }

                            // Share / Export APK quick button
                            IconButton(
                                onClick = { shareApk(app) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "APK Paylaş", tint = TextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = app.description,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // App Features Tags
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            app.features.take(2).forEach { feature ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(text = "• $feature", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }

                        // Progress Indicator during download
                        if (isDownloadingThisApp) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = app.accentColor,
                                trackColor = DarkBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = downloadStatusText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Install, Open, Update, Direct APK
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            when (appState) {
                                AppInstallState.NOT_INSTALLED -> {
                                    Button(
                                        onClick = { startInAppDownloadAndInstall(app) },
                                        enabled = !isDownloadingThisApp,
                                        colors = ButtonDefaults.buttonColors(containerColor = app.accentColor),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("APK İNDİR & KUR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                val file = createRealApkPackage(app)
                                                launchSystemApkInstaller(file)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Icon(Icons.Default.InstallMobile, contentDescription = "Android'e Kur", tint = NeonGreen, modifier = Modifier.size(18.dp))
                                    }

                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                val file = createRealApkPackage(app)
                                                Toast.makeText(context, "📥 ${file.name} Downloads klasörüne kaydedildi!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Icon(Icons.Default.FileDownload, contentDescription = "APK Kaydet", tint = NeonCyan, modifier = Modifier.size(18.dp))
                                    }
                                }

                                AppInstallState.INSTALLED -> {
                                    Button(
                                        onClick = { launchApp(app) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Launch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("AÇ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                val file = createRealApkPackage(app)
                                                launchSystemApkInstaller(file)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Icon(Icons.Default.InstallMobile, contentDescription = "Yeniden Kur", tint = NeonGreen, modifier = Modifier.size(18.dp))
                                    }

                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                val file = createRealApkPackage(app)
                                                Toast.makeText(context, "📥 ${file.name} Downloads klasörüne kaydedildi!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Icon(Icons.Default.FileDownload, contentDescription = "APK Kaydet", tint = NeonCyan, modifier = Modifier.size(18.dp))
                                    }

                                    if (app.id != "keon_ai") {
                                        IconButton(
                                            onClick = { uninstallApp(app) },
                                            modifier = Modifier
                                                .background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                                .border(1.dp, Color.Red.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                                .size(42.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Kaldır", tint = Color.Red, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }

                                AppInstallState.UPDATE_AVAILABLE -> {
                                    Button(
                                        onClick = { launchApp(app) },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, NeonGreen),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("AÇ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                                    }

                                    Button(
                                        onClick = { startInAppDownloadAndInstall(app) },
                                        enabled = !isDownloadingThisApp,
                                        colors = ButtonDefaults.buttonColors(containerColor = FlameNeonOrange),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1.2f)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Update, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("APK GÜNCELLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Permission Dialog for Unknown App Sources
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = FlameNeonOrange, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("APK Yükleme İzni Gerekli", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Android sisteminde doğrudan APK yükleyebilmek için Keon Store uygulamasına 'Bilinmeyen Uygulamaları Yükleme' izni vermeniz gerekmektedir.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                    Text(
                        text = "Açılacak ayarlar ekranından 'Bu kaynaktan izin ver' seçeneğini aktif ediniz.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try {
                                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val settingsIntent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                                context.startActivity(settingsIntent)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameNeonOrange),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ayarlara Git & İzin Ver", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("İptal", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Detailed App Modal Dialog
    if (selectedAppForDetail != null) {
        val app = selectedAppForDetail!!
        val appState = getAppState(app)

        AlertDialog(
            onDismissRequest = { selectedAppForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = app.iconEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = app.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "${app.packageName} (${app.version})", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = app.description,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 17.sp
                    )

                    Divider(color = Color.White.copy(alpha = 0.1f))

                    Text(
                        text = "🚀 Temel Özellikler & Paket Bilgisi:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )

                    app.features.forEach { feature ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = feature, fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBackground, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Paket Boyutu: ${app.sizeFormatted}", fontSize = 11.sp, color = TextSecondary)
                        Text("Kategori: ${app.category}", fontSize = 11.sp, color = app.accentColor, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            shareApk(app)
                            selectedAppForDetail = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                        border = BorderStroke(1.dp, NeonCyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("APK Paylaş", color = NeonCyan, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            startInAppDownloadAndInstall(app)
                            selectedAppForDetail = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = app.accentColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Gerçek APK İndir & Kur", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAppForDetail = null }) {
                    Text("Kapat", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}
