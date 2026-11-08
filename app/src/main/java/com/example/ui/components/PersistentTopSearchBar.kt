package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class SearchEngineItem(
    val id: String,
    val name: String,
    val emoji: String,
    val color: Color,
    val searchUrlTemplate: String
)

val integratedSearchEngines = listOf(
    SearchEngineItem("google", "Google", "🌐", NeonCyan, "https://www.google.com/search?q=%s"),
    SearchEngineItem("bing", "Bing", "🔍", NeonBlue, "https://www.bing.com/search?q=%s"),
    SearchEngineItem("duckduckgo", "DuckDuckGo", "🦆", Color(0xFFFF9800), "https://duckduckgo.com/?q=%s"),
    SearchEngineItem("yandex", "Yandex", "🔴", Color(0xFFFF3333), "https://yandex.com/search/?text=%s"),
    SearchEngineItem("wikipedia", "Vikipedi", "📚", Color(0xFFE0E0E0), "https://tr.wikipedia.org/w/index.php?search=%s"),
    SearchEngineItem("youtube", "YouTube", "📺", Color(0xFFFF0000), "https://www.youtube.com/results?search_query=%s"),
    SearchEngineItem("yahoo", "Yahoo", "🟣", Color(0xFF9C27B0), "https://search.yahoo.com/search?p=%s"),
    SearchEngineItem("keon_ai", "Keon AI", "🤖", NeonMagenta, "KEON_AI_INTERNAL")
)

@Composable
fun PersistentTopSearchBar(
    selectedEngineId: String,
    onSelectEngine: (String) -> Unit,
    onSearchKeonAi: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }

    fun executeSearch() {
        val query = searchQuery.trim()
        if (query.isEmpty()) {
            Toast.makeText(context, "Lütfen bir arama metni yazın", Toast.LENGTH_SHORT).show()
            return
        }

        onSearchKeonAi(query)
        searchQuery = ""
        focusManager.clearFocus()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .pulsatingNeonGlow(
                glowColor = NeonMagenta,
                minAlpha = 0.2f,
                maxAlpha = 0.75f,
                glowRadius = 10.dp,
                shape = RoundedCornerShape(16.dp),
                borderWidth = 1.2.dp
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Keon AI Engine Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonMagenta.copy(alpha = 0.15f))
                    .border(1.dp, NeonMagenta.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🤖", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Keon AI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonMagenta
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Keon AI'ya Sor veya Web'de Ara...",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(24.dp)
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
                            onClick = { executeSearch() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Keon Arama",
                                tint = NeonMagenta,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { executeSearch() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonMagenta,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
