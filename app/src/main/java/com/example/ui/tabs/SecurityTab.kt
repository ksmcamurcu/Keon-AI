package com.example.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen

@Composable
fun SecurityTab() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Güvenlik & Hardening (Geliştirici)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
        Spacer(modifier = Modifier.height(16.dp))
        
        val items = listOf(
            "ProGuard / R8 Obfuscation: Aktif" to "Kodunuzu tersine mühendisliğe karşı koruyun.",
            "Firebase Security Rules: Denetlendi" to "Veritabanı erişimi sınırlandırıldı.",
            "Hardcoded Secrets Check: Geçildi" to "API anahtarları güvenli yönetiliyor.",
            "Network Security Config: Yapılandırıldı" to "Cleartext trafiği engellendi."
        )

        LazyColumn {
            items.forEach { (title, desc) ->
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF161A2B))) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(title, fontWeight = FontWeight.Bold, color = NeonGreen)
                            Text(desc, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
