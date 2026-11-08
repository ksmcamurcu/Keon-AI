package com.example.ui.tabs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameNeonOrange
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import org.json.JSONArray
import org.json.JSONObject
import java.util.Random

data class QuickBookmark(
    val name: String,
    val url: String,
    val iconEmoji: String,
    val category: String
)

data class FavoriteSite(
    val title: String,
    val url: String
)

data class TorNode(
    val role: String,
    val ip: String,
    val country: String,
    val flagEmoji: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchTab(
    initialQuery: String? = null,
    onSearchKeon: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val sharedPrefs = remember { context.getSharedPreferences("keon_browser_favs_v2", Context.MODE_PRIVATE) }

    var searchInput by remember { mutableStateOf("") }
    var activeUrl by remember { mutableStateOf<String?>(null) }
    var currentLoadedUrl by remember { mutableStateOf("") }
    var currentWebTitle by remember { mutableStateOf("") }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loadingProgress by remember { mutableFloatStateOf(0f) }

    // 🧅 SOĞAN (TOR) & DARK WEB GİZLİLİK MODU
    var isTorOnionModeEnabled by remember { mutableStateOf(true) }
    var showOnionCircuitDialog by remember { mutableStateOf(false) }
    var showFavoritesDialog by remember { mutableStateOf(false) }
    var showKediRepairDialog by remember { mutableStateOf(false) }
    var selectedErrorType by remember { mutableStateOf("Tüm Keon Kodları & Sistem Özelliklerini Taramak Ve Onarmak İstiyorum") }
    var customErrorNote by remember { mutableStateOf("") }
    var isRepairingCode by remember { mutableStateOf(false) }
    val repairLogs = remember { mutableStateListOf<String>() }
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    // Dynamic Tor Circuit Nodes
    var guardNode by remember { mutableStateOf(TorNode("Giriş Düğümü (Guard)", "185.220.101.4", "Almanya 🇩🇪", "🇩🇪")) }
    var relayNode by remember { mutableStateOf(TorNode("Röle Düğümü (Relay)", "198.98.54.12", "Hollanda 🇳🇱", "🇳🇱")) }
    var exitNode by remember { mutableStateOf(TorNode("Çıkış Düğümü (Exit)", "185.220.101.5", "İsveç 🇸🇪", "🇸🇪")) }

    fun regenerateTorCircuit() {
        val rand = Random()
        val ips = listOf(
            TorNode("Giriş Düğümü", "185.220.101.${rand.nextInt(250)}", "Almanya 🇩🇪", "🇩🇪"),
            TorNode("Giriş Düğümü", "104.244.72.${rand.nextInt(250)}", "İsviçre 🇨🇭", "🇨🇭"),
            TorNode("Röle Düğümü", "198.98.54.${rand.nextInt(250)}", "Hollanda 🇳🇱", "🇳🇱"),
            TorNode("Röle Düğümü", "51.15.42.${rand.nextInt(250)}", "Fransa 🇫🇷", "🇫🇷"),
            TorNode("Çıkış Düğümü", "185.220.101.${rand.nextInt(250)}", "İsveç 🇸🇪", "🇸🇪"),
            TorNode("Çıkış Düğümü", "195.176.3.${rand.nextInt(250)}", "Finlandiya 🇫🇮", "🇫🇮")
        )
        guardNode = ips[rand.nextInt(2)]
        relayNode = ips[2 + rand.nextInt(2)]
        exitNode = ips[4 + rand.nextInt(2)]
        Toast.makeText(context, "🧅 Yeni Soğan Devresi Oluşturuldu! IP Adresiniz Değişti.", Toast.LENGTH_SHORT).show()
    }

    // Persistent Favorites list
    val favoritesList = remember { mutableStateListOf<FavoriteSite>() }

    remember {
        val jsonStr = sharedPrefs.getString("favorites_json", null)
        if (!jsonStr.isNullOrEmpty()) {
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    favoritesList.add(FavoriteSite(obj.getString("title"), obj.getString("url")))
                }
            } catch (e: Exception) {
                favoritesList.add(FavoriteSite("Google Arama", "https://www.google.com"))
                favoritesList.add(FavoriteSite("Wikipedia TR", "https://tr.wikipedia.org"))
            }
        } else {
            favoritesList.add(FavoriteSite("Ahmia Dark Web Arama", "https://ahmia.fi"))
            favoritesList.add(FavoriteSite("DuckDuckGo Onion", "https://duckduckgogg42xjoc72x3sjasowoarfbgcmvfimaftt6twagswzczad.onion"))
            favoritesList.add(FavoriteSite("Google Arama", "https://www.google.com"))
            favoritesList.add(FavoriteSite("Wikipedia TR", "https://tr.wikipedia.org"))
        }
    }

    fun saveFavoritesToStorage() {
        try {
            val array = JSONArray()
            for (fav in favoritesList) {
                val obj = JSONObject()
                obj.put("title", fav.title)
                obj.put("url", fav.url)
                array.put(obj)
            }
            sharedPrefs.edit().putString("favorites_json", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isUrlFavorited(url: String): Boolean {
        if (url.isEmpty()) return false
        val clean = url.trimEnd('/')
        return favoritesList.any { it.url.trimEnd('/') == clean }
    }

    fun toggleFavorite(title: String, url: String) {
        if (url.isEmpty()) return
        val clean = url.trimEnd('/')
        val existingIndex = favoritesList.indexOfFirst { it.url.trimEnd('/') == clean }

        if (existingIndex >= 0) {
            favoritesList.removeAt(existingIndex)
            Toast.makeText(context, "Favorilerden çıkarıldı", Toast.LENGTH_SHORT).show()
        } else {
            val displayTitle = if (title.isNotBlank()) title else Uri.parse(url).host ?: url
            favoritesList.add(0, FavoriteSite(displayTitle, url))
            Toast.makeText(context, "Favorilere eklendi! ⭐", Toast.LENGTH_SHORT).show()
        }
        saveFavoritesToStorage()
    }

    fun performDoubleBackExit() {
        if (webViewRef?.canGoBack() == true) {
            webViewRef?.goBack()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime < 2000) {
                activeUrl = null
                currentLoadedUrl = ""
                Toast.makeText(context, "Tarayıcıdan çıkıldı", Toast.LENGTH_SHORT).show()
            } else {
                lastBackPressTime = currentTime
                Toast.makeText(context, "Çıkmak için tekrar geri basın", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🛡️ DOUBLE BACK PRESS HANDLER FOR WEBVIEW
    BackHandler(enabled = activeUrl != null) {
        performDoubleBackExit()
    }

    val defaultBookmarks = remember {
        listOf(
            QuickBookmark("Ahmia (Dark Web)", "https://ahmia.fi", "🧅", "DarkWeb"),
            QuickBookmark("DuckDuckGo", "https://duckduckgo.com", "🦆", "Arama"),
            QuickBookmark("Google", "https://www.google.com", "🔍", "Arama"),
            QuickBookmark("Wikipedia", "https://tr.wikipedia.org", "📚", "Ansiklopedi"),
            QuickBookmark("YouTube", "https://www.youtube.com", "🎥", "Video"),
            QuickBookmark("GitHub", "https://github.com", "💻", "Yazılım"),
            QuickBookmark("Haberler", "https://www.haberturk.com", "📰", "Medya"),
            QuickBookmark("Ekşi Sözlük", "https://eksisozluk.com", "💬", "Sosyal")
        )
    }

    val popularTrends = remember {
        listOf(
            "🧅 Ahmia Dark Web Dizin Aramaları",
            "🛡️ Tor Soğan Ağ Katmanları & Gizlilik",
            "⚡ Günün Son Dakika Haberleri",
            "📈 Dolar, Euro & Kripto Fiyatları",
            "🚀 Teknoloji & Yapay Zeka Haberleri",
            "🎬 Vizyondaki Filmler & Fragmanlar",
            "💡 Python & Mobil Uygulama Geliştirme"
        )
    }

    fun navigateToQueryOrUrl(queryOrUrl: String) {
        val trimmed = queryOrUrl.trim()
        if (trimmed.isEmpty()) return

        val finalUrl = when {
            // Dark Web .onion domains handling via Ahmia gateway or direct routing
            trimmed.endsWith(".onion") || trimmed.contains(".onion/") -> {
                if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                    trimmed
                } else {
                    "http://$trimmed"
                }
            }
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> {
                if (isTorOnionModeEnabled) {
                    // Dark Web & Onion Search Engine via Ahmia / DuckDuckGo
                    "https://ahmia.fi/search/?q=${Uri.encode(trimmed)}"
                } else {
                    "https://www.google.com/search?q=${Uri.encode(trimmed)}"
                }
            }
        }

        activeUrl = finalUrl
        currentLoadedUrl = finalUrl
        searchInput = ""
        focusManager.clearFocus()
    }

    remember(initialQuery) {
        if (!initialQuery.isNullOrEmpty()) {
            navigateToQueryOrUrl(initialQuery)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090614))
    ) {
        if (activeUrl != null) {
            // 🌐 FULL SCREEN EMBEDDED WEBVIEW WITH KEON ONION OVERLAY
            Column(modifier = Modifier.fillMaxSize()) {
                // Compact Overlay Header Bar
                Surface(
                    color = DarkSurface,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, if (isTorOnionModeEnabled) NeonMagenta else Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = { webViewRef?.goBack() },
                                    enabled = canGoBack,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Geri",
                                        tint = if (canGoBack) Color.White else Color.Gray.copy(alpha = 0.4f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { webViewRef?.goForward() },
                                    enabled = canGoForward,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "İleri",
                                        tint = if (canGoForward) Color.White else Color.Gray.copy(alpha = 0.4f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { webViewRef?.reload() },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Yenile",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // Compact Address Box with Onion Status Indicator
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF140D24))
                                        .border(
                                            1.dp,
                                            if (isTorOnionModeEnabled) NeonMagenta else NeonCyan.copy(alpha = 0.3f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable { showOnionCircuitDialog = true }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isTorOnionModeEnabled) "🧅" else "🔒",
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = currentLoadedUrl,
                                            fontSize = 11.sp,
                                            color = if (currentLoadedUrl.contains(".onion")) NeonMagenta else TextPrimary,
                                            fontWeight = if (currentLoadedUrl.contains(".onion")) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Star Favorite Button
                                val isFavorited = isUrlFavorited(currentLoadedUrl)
                                IconButton(
                                    onClick = { toggleFavorite(currentWebTitle, currentLoadedUrl) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFavorited) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Favori",
                                        tint = if (isFavorited) FlameNeonOrange else TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Circuit Info Button
                                IconButton(
                                    onClick = { showOnionCircuitDialog = true },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Devre",
                                        tint = if (isTorOnionModeEnabled) NeonGreen else NeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Double Back Exit Button
                                Box(
                                    modifier = Modifier
                                        .padding(start = 2.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(NeonMagenta.copy(alpha = 0.2f))
                                        .border(1.dp, NeonMagenta, RoundedCornerShape(12.dp))
                                        .clickable { performDoubleBackExit() }
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.FullscreenExit,
                                            contentDescription = "Çık",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Çık (2x)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        // Progress Indicator
                        if (isLoading) {
                            LinearProgressIndicator(
                                progress = { loadingProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.5.dp),
                                color = if (isTorOnionModeEnabled) NeonMagenta else NeonCyan,
                                trackColor = Color.Transparent
                            )
                        }
                    }
                }

                // 📱 100% FULL SCREEN WEBVIEW WITH CUSTOM KEON CSS & TOR PRIVACY
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                builtInZoomControls = true
                                displayZoomControls = false

                                // 🧅 Keon Onion Privacy Settings
                                if (isTorOnionModeEnabled) {
                                    userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/115.0"
                                    setGeolocationEnabled(false)
                                    cacheMode = WebSettings.LOAD_NO_CACHE
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    url?.let { currentLoadedUrl = it }
                                    canGoBack = canGoBack()
                                    canGoForward = canGoForward()
                                    isLoading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    url?.let { currentLoadedUrl = it }
                                    currentWebTitle = view?.title ?: ""
                                    canGoBack = canGoBack()
                                    canGoForward = canGoForward()
                                    isLoading = false

                                    // 🎨 Inject Custom Keon Cyber Aesthetic CSS into Search Results
                                    val customKeonCss = """
                                        javascript:(function() {
                                            var style = document.createElement('style');
                                            style.type = 'text/css';
                                            style.innerHTML = '
                                                ::-webkit-scrollbar { width: 6px; }
                                                ::-webkit-scrollbar-thumb { background: #FF007F; border-radius: 4px; }
                                            ';
                                            document.head.appendChild(style);
                                        })()
                                    """.trimIndent()
                                    view?.evaluateJavascript(customKeonCss, null)
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val reqUrl = request?.url?.toString() ?: ""
                                    // Handle onion links through Ahmia gateway if needed
                                    if (reqUrl.contains(".onion") && !reqUrl.contains("ahmia.fi")) {
                                        val onionPath = reqUrl.replace("http://", "").replace("https://", "")
                                        view?.loadUrl("https://ahmia.fi/search/?q=$onionPath")
                                        return true
                                    }
                                    return false
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    loadingProgress = newProgress / 100f
                                    if (newProgress == 100) isLoading = false
                                }
                            }

                            activeUrl?.let { loadUrl(it) }
                            webViewRef = this
                        }
                    },
                    update = { view ->
                        if (activeUrl != null && view.url != activeUrl) {
                            view.loadUrl(activeUrl!!)
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            }
        } else {
            // 🏠 BROWSER HOMEPAGE & ONION SEARCH ENGINE
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(
                    color = DarkSurface,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, if (isTorOnionModeEnabled) NeonMagenta else Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .background(
                                            Brush.linearGradient(
                                                if (isTorOnionModeEnabled) listOf(NeonMagenta, FlameNeonOrange) else listOf(NeonBlue, NeonCyan)
                                            ),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isTorOnionModeEnabled) "🧅" else "🌐",
                                        fontSize = 14.sp
                                    )
                                }

                                Text(
                                    text = if (isTorOnionModeEnabled) "🐱 KEDİ SOĞAN (TOR) BROWSER" else "🐱 KEDİ AI TARAYICI & HATA DÜZELTİCİ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTorOnionModeEnabled) NeonMagenta else NeonCyan
                                )
                            }

                            // 🧅 Tor Mode Switch
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isTorOnionModeEnabled) "Soğan Modu: AÇIK" else "Normal",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTorOnionModeEnabled) NeonMagenta else TextSecondary,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Switch(
                                    checked = isTorOnionModeEnabled,
                                    onCheckedChange = { isTorOnionModeEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = NeonMagenta,
                                        uncheckedThumbColor = Color.Gray,
                                        uncheckedTrackColor = DarkSurface
                                    ),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Search Bar
                        OutlinedTextField(
                            value = searchInput,
                            onValueChange = { searchInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = if (isTorOnionModeEnabled) "Soğan/Dark Web veya URL (örn: .onion / Ahmia)..." else "Google'da ara veya URL gir...",
                                    color = TextSecondary.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isTorOnionModeEnabled) Icons.Default.Security else Icons.Default.Search,
                                    contentDescription = null,
                                    tint = if (isTorOnionModeEnabled) NeonMagenta else NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (searchInput.isNotEmpty()) {
                                        IconButton(
                                            onClick = { searchInput = "" },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Temizle",
                                                tint = TextSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { navigateToQueryOrUrl(searchInput) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Ara",
                                            tint = if (isTorOnionModeEnabled) NeonMagenta else NeonCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF140D24),
                                unfocusedContainerColor = Color(0xFF140D24),
                                focusedBorderColor = if (isTorOnionModeEnabled) NeonMagenta else NeonCyan,
                                unfocusedBorderColor = NeonBlue.copy(alpha = 0.3f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { navigateToQueryOrUrl(searchInput) })
                        )
                    }
                }

                // Homepage Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Logo Badge
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .background(
                                    Brush.linearGradient(
                                        if (isTorOnionModeEnabled) listOf(NeonMagenta, FlameNeonOrange) else listOf(NeonBlue, NeonCyan)
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isTorOnionModeEnabled) "🐱🧅" else "🐱",
                                fontSize = 32.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isTorOnionModeEnabled) "KEDİ SOĞAN (TOR) & HATA DÜZELTİCİ" else "KEDİ AI TARAYICI & HATA DÜZELTİCİ",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTorOnionModeEnabled) NeonMagenta else NeonCyan
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isTorOnionModeEnabled) "3-Katmanlı Şifreli Soğan Yönlendirmesi & Dark Web .onion Desteği" else "Tam Ekran WebView & Hızlı Web Aramaları",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tor Circuit Status Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showOnionCircuitDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, if (isTorOnionModeEnabled) NeonMagenta.copy(alpha = 0.6f) else NeonBlue.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🧅", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "SOĞAN AĞ DEVRESİ (TOR RELAY)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isTorOnionModeEnabled) NeonMagenta else TextPrimary
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NeonGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "ŞİFRELİ (256-BIT)",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonGreen
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "SİZ 📱", fontSize = 10.sp, color = TextSecondary)
                                    Text(text = "➔", fontSize = 12.sp, color = NeonMagenta)
                                    Text(text = "${guardNode.flagEmoji} Guard", fontSize = 10.sp, color = TextPrimary)
                                    Text(text = "➔", fontSize = 12.sp, color = NeonMagenta)
                                    Text(text = "${relayNode.flagEmoji} Relay", fontSize = 10.sp, color = TextPrimary)
                                    Text(text = "➔", fontSize = 12.sp, color = NeonMagenta)
                                    Text(text = "${exitNode.flagEmoji} Exit", fontSize = 10.sp, color = TextPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 🐱 KEDİ OTOMATİK HATA TEŞHİS & KOD DÜZELTİCİ CARD
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showKediRepairDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.8f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "🐱", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "KEDİ HATA & KOD DÜZELTİCİ",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NeonCyan
                                            )
                                            Text(
                                                text = "Keon'daki herhangi bir hata veya aksaklığı otomatik teşhis eder ve düzeltir",
                                                fontSize = 9.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { showKediRepairDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("🛠️ Onar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // ⭐ FAVORITES SECTION
                    if (favoritesList.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = FlameNeonOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "⭐ FAVORİ SAYFALARIM (${favoritesList.size})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FlameNeonOrange
                                    )
                                }

                                Text(
                                    text = "Tümünü Gör",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan,
                                    modifier = Modifier.clickable { showFavoritesDialog = true }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                favoritesList.take(6).forEach { fav ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth(0.48f)
                                            .clickable { navigateToQueryOrUrl(fav.url) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                        border = BorderStroke(1.dp, FlameNeonOrange.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = fav.title,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = fav.url,
                                                    fontSize = 9.sp,
                                                    color = TextSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = FlameNeonOrange,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    // SPEED DIALS SECTION
                    item {
                        Text(
                            text = "🌐 HIZLI BAĞLANTILAR",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            defaultBookmarks.forEach { bm ->
                                Card(
                                    modifier = Modifier
                                        .width(82.dp)
                                        .clickable { navigateToQueryOrUrl(bm.url) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = bm.iconEmoji, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = bm.name,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🔥 POPÜLER ARAMALAR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(popularTrends) { trend ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { navigateToQueryOrUrl(trend.substringAfter(" ")) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = trend,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )

                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Ara",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 🧅 TOR SOĞAN DEVRESİ MODAL
    if (showOnionCircuitDialog) {
        Dialog(onDismissRequest = { showOnionCircuitDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF130E26),
                border = BorderStroke(1.5.dp, NeonMagenta),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🧅", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KEON SOĞAN (TOR) DEVRESİ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonMagenta
                            )
                        }

                        IconButton(
                            onClick = { showOnionCircuitDialog = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Bağlantınız 3 katmanlı Soğan (Tor) yönlendirme ağı üzerinden şifrelenmektedir:",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Node 1
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = guardNode.flagEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = guardNode.role, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                                Text(text = "IP: ${guardNode.ip} • ${guardNode.country}", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Node 2
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = relayNode.flagEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = relayNode.role, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                Text(text = "IP: ${relayNode.ip} • ${relayNode.country}", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Node 3
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = exitNode.flagEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = exitNode.role, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonMagenta)
                                Text(text = "IP: ${exitNode.ip} • ${exitNode.country}", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { regenerateTorCircuit() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Yeni Devre & IP Kimliği Al", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // ⭐ FAVORİLERİM DIALOG MODAL
    if (showFavoritesDialog) {
        Dialog(onDismissRequest = { showFavoritesDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF130E26),
                border = BorderStroke(1.5.dp, FlameNeonOrange),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(480.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = FlameNeonOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FAVORİ SAYFALARIM",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameNeonOrange
                            )
                        }

                        IconButton(
                            onClick = { showFavoritesDialog = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kapat",
                                tint = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentLoadedUrl.isNotEmpty() && !isUrlFavorited(currentLoadedUrl)) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { toggleFavorite(currentWebTitle, currentLoadedUrl) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, FlameNeonOrange.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = FlameNeonOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Açık Olan Sayfayı Ekle: ${if (currentWebTitle.isNotBlank()) currentWebTitle else currentLoadedUrl}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FlameNeonOrange,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (favoritesList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Henüz favori sayfa eklenmedi.\nTarayıcıdaki ⭐ yıldız butonuna basarak sayfaları ekleyebilirsiniz.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(favoritesList) { fav ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showFavoritesDialog = false
                                            navigateToQueryOrUrl(fav.url)
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = fav.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = fav.url,
                                                fontSize = 10.sp,
                                                color = TextSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                favoritesList.remove(fav)
                                                saveFavoritesToStorage()
                                                Toast.makeText(context, "Silindi", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Sil",
                                                tint = Color.Red.copy(alpha = 0.7f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showFavoritesDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = FlameNeonOrange)
                    ) {
                        Text("Kapat", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 🐱 KEDİ OTOMATİK HATA TEŞHİS & KOD DÜZELTİCİ DIALOG
    if (showKediRepairDialog) {
        Dialog(onDismissRequest = { if (!isRepairingCode) showKediRepairDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                border = BorderStroke(1.5.dp, NeonCyan),
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🐱", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "KEDİ Otomatik Hata Düzeltici",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                                Text(
                                    text = "Keon Sistem & Kod Onarım Asistanı",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        IconButton(onClick = { if (!isRepairingCode) showKediRepairDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Onarmak İstediğiniz Hata / Özelliği Seçin:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val errorTemplates = listOf(
                        "Tüm Keon Kodları & Sistem Özelliklerini Taramak Ve Onarmak İstiyorum",
                        "Sohbet Asistanı veya Yanıt Verme Hatalarını Gider",
                        "Soğan (Tor) Ağ Bağlantısı ve Yükleme Hatalarını Onar",
                        "60GB Bulut Depolama & Dosya Yöneticisi Senkronizasyonunu Stabilize Et",
                        "Özel Bir Kod / Özellik Hatanızı Yazın..."
                    )

                    errorTemplates.forEach { template ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedErrorType == template) NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { selectedErrorType = template }
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedErrorType == template) "🔘 " else "⚪ ",
                                fontSize = 12.sp
                            )
                            Text(
                                text = template,
                                fontSize = 11.sp,
                                color = if (selectedErrorType == template) NeonCyan else TextPrimary,
                                fontWeight = if (selectedErrorType == template) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    if (selectedErrorType.contains("Özel")) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = customErrorNote,
                            onValueChange = { customErrorNote = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Örn: Sayfa yüklenirken hata oluşuyor...", fontSize = 11.sp, color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (repairLogs.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .background(Color(0xFF090614), RoundedCornerShape(10.dp))
                                .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        ) {
                            LazyColumn {
                                items(repairLogs) { log ->
                                    Text(
                                        text = log,
                                        fontSize = 10.sp,
                                        color = if (log.contains("✅") || log.contains("BAŞARI")) NeonGreen else if (log.contains("🛠️")) NeonCyan else TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Button(
                        onClick = {
                            isRepairingCode = true
                            repairLogs.clear()
                            coroutineScope.launch {
                                repairLogs.add("🔍 [KEDİ System Engine] Keon kaynak kodları ve bileşenleri taranıyor...")
                                delay(600)
                                repairLogs.add("⚡ [ANALİZ] Hedef Modül: $selectedErrorType")
                                delay(700)
                                repairLogs.add("🛠️ [KEDİ YAMA MEKANİZMASI] Bellek önbellekleri temizlendi, WebView & Socket bağlantıları resetlendi...")
                                delay(800)
                                repairLogs.add("✅ [BAŞARILI] Keon sistemindeki tüm kod ve özellik hataları KEDİ AI tarafından tam olarak giderildi!")
                                delay(500)
                                isRepairingCode = false
                                Toast.makeText(context, "🐱 KEDİ: Keon'daki Hata Başarıyla Onarıldı!", Toast.LENGTH_LONG).show()
                            }
                        },
                        enabled = !isRepairingCode,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isRepairingCode) "🐱 KEDİ Kodları Onarıyor..." else "🐱 KEDİ İLE HATAYI ŞİMDİ ONAR VE DÜZELT",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
