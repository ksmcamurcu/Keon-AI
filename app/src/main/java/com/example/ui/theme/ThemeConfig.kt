package com.example.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R

enum class TechThemeId(
    val displayName: String,
    val description: String,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color
) {
    CYBER_NEON(
        displayName = "Siber Neon",
        description = "Mavi & Siyan Yüksek Teknoloji",
        primary = Color(0xFF00FFFF),
        secondary = Color(0xFFFF00FF),
        tertiary = Color(0xFF00FF88),
        background = Color(0xFF0A0A1F),
        surface = Color(0xCC121226),
        surfaceVariant = Color(0xFF1E1E38)
    ),
    AMOLED_PITCH_BLACK(
        displayName = "🌑 AMOLED Saf Siyah",
        description = "Zifiri Saf Siyah Karanlık Arayüz (%0 Pil Tüketimi)",
        primary = Color(0xFF00FFFF),
        secondary = Color(0xFF00FF88),
        tertiary = Color(0xFFFF00CC),
        background = Color(0xFF000000),
        surface = Color(0xFF09090D),
        surfaceVariant = Color(0xFF12121A)
    ),
    MIDNIGHT_DARK(
        displayName = "🌌 Gece Yarısı Karanlık",
        description = "Göz Dinlendiren Koyu Grafit & Gece Arayüzü",
        primary = Color(0xFF38BDF8),
        secondary = Color(0xFF818CF8),
        tertiary = Color(0xFF34D399),
        background = Color(0xFF04060A),
        surface = Color(0xFF0B0E17),
        surfaceVariant = Color(0xFF131826)
    ),
    STEALTH_CARBON(
        displayName = "🕶️ Mat Karbon Karanlık",
        description = "Mat Karbon & Yansıma Önleyici Koyu Arayüz",
        primary = Color(0xFFE2E8F0),
        secondary = Color(0xFF94A3B8),
        tertiary = Color(0xFF38BDF8),
        background = Color(0xFF08090C),
        surface = Color(0xFF101217),
        surfaceVariant = Color(0xFF191C24)
    ),
    QUANTUM_MATRIX(
        displayName = "Kuantum Matrix",
        description = "Siber Yeşil Matris & Biotek",
        primary = Color(0xFF00FF88),
        secondary = Color(0xFF00FFFF),
        tertiary = Color(0xFFAAFF00),
        background = Color(0xFF051A0E),
        surface = Color(0xCC0B2616),
        surfaceVariant = Color(0xFF133B23)
    ),
    SOLAR_FLARE(
        displayName = "Güneş Fırtınası",
        description = "Hiper Turuncu & Altın Işık",
        primary = Color(0xFFFF7700),
        secondary = Color(0xFFFFCC00),
        tertiary = Color(0xFF00FFFF),
        background = Color(0xFF1A0803),
        surface = Color(0xCC260E06),
        surfaceVariant = Color(0xFF3B170B)
    ),
    AURORA_HOLOGRAM(
        displayName = "Aurora Hologram",
        description = "Kozmik Mor & Pembe Enerji",
        primary = Color(0xFFB055FF),
        secondary = Color(0xFFFF33CC),
        tertiary = Color(0xFF00FFFF),
        background = Color(0xFF120824),
        surface = Color(0xCC1B0E33),
        surfaceVariant = Color(0xFF2C164A)
    ),
    OBSIDIAN_VOID(
        displayName = "Obsidyen Buz",
        description = "Derin Siyah & Kristal Beyaz",
        primary = Color(0xFFE0F7FA),
        secondary = Color(0xFF80DEEA),
        tertiary = Color(0xFF00E5FF),
        background = Color(0xFF05050A),
        surface = Color(0xCC10101A),
        surfaceVariant = Color(0xFF1A1A2A)
    ),
    PHOENIX_EMERALD(
        displayName = "🔥 Zümrüdü Anka Efsanevi",
        description = "Zümrüt Yeşili & Altın Alev Matrisi",
        primary = Color(0xFF00FF88),
        secondary = Color(0xFFFFD700),
        tertiary = Color(0xFF00E5FF),
        background = Color(0xFF031A0F),
        surface = Color(0xCC092A1A),
        surfaceVariant = Color(0xFF123E28)
    ),
    AURA_HOLOGRAM_PRO(
        displayName = "🔮 Aura VIP Hologram",
        description = "Mor Işıma & Elektrik Turkuaz Aura",
        primary = Color(0xFFD000FF),
        secondary = Color(0xFF00FFFF),
        tertiary = Color(0xFFFF00CC),
        background = Color(0xFF150428),
        surface = Color(0xCC240940),
        surfaceVariant = Color(0xFF35125A)
    ),
    FLAME_CORE(
        displayName = "🔥 Alev Çekirdeği",
        description = "Neon Mor & Alev Turuncusu Yüksek Zeka Çekirdeği",
        primary = Color(0xFFFF6600),
        secondary = Color(0xFFD000FF),
        tertiary = Color(0xFFFF3D00),
        background = Color(0xFF10041B),
        surface = Color(0xCC200835),
        surfaceVariant = Color(0xFF2E0C4C)
    )
}

enum class NeonAccentColor(
    val id: String,
    val displayName: String,
    val description: String,
    val primaryNeon: Color,
    val secondaryNeon: Color,
    val tertiaryNeon: Color,
    val glowColor: Color
) {
    NEON_FLAME_PURPLE_ORANGE(
        id = "flame_purple_orange",
        displayName = "🔥 Alev Mor & Turuncu",
        description = "Neon Mor ve Alev Turuncusu Yansımalı Zeka Çekirdeği",
        primaryNeon = Color(0xFFFF6600),
        secondaryNeon = Color(0xFFD000FF),
        tertiaryNeon = Color(0xFFFF3D00),
        glowColor = Color(0xFFFF7700)
    ),
    NEON_CYAN_BLUE(
        id = "cyan_blue",
        displayName = "Siyan & Elektrik Mavi",
        description = "Mavi & Siyan yüksek teknoloji elektrik ikilisi",
        primaryNeon = Color(0xFF00FFFF),
        secondaryNeon = Color(0xFF0088FF),
        tertiaryNeon = Color(0xFF00FF88),
        glowColor = Color(0xFF00E5FF)
    ),
    NEON_PURPLE_MAGENTA(
        id = "purple_magenta",
        displayName = "Kozmik Mor & Pembe",
        description = "Ultraviyole siber neon ve magenta moru",
        primaryNeon = Color(0xFFD000FF),
        secondaryNeon = Color(0xFFFF00FF),
        tertiaryNeon = Color(0xFFFF33CC),
        glowColor = Color(0xFFE040FB)
    ),
    NEON_MATRIX_GREEN(
        id = "matrix_green",
        displayName = "Matris & Biyo Yeşil",
        description = "Kuantum matris yeşili ve biyoteknoloji",
        primaryNeon = Color(0xFF00FF88),
        secondaryNeon = Color(0xFF76FF03),
        tertiaryNeon = Color(0xFF00FFFF),
        glowColor = Color(0xFF39FF14)
    ),
    NEON_AMBER_GOLD(
        id = "amber_gold",
        displayName = "Kehribar & Plazma Turuncu",
        description = "Güneş fırtınası ve altın fütüristik plazma",
        primaryNeon = Color(0xFFFF7700),
        secondaryNeon = Color(0xFFFFCC00),
        tertiaryNeon = Color(0xFFFF3D00),
        glowColor = Color(0xFFFFAB00)
    ),
    NEON_LASER_RED(
        id = "laser_red",
        displayName = "Lazer Kırmızı & Alev",
        description = "Yüksek voltajlı kırmızı lazer ışıması",
        primaryNeon = Color(0xFFFF1744),
        secondaryNeon = Color(0xFFFF5252),
        tertiaryNeon = Color(0xFFFF0055),
        glowColor = Color(0xFFFF2A6D)
    ),
    NEON_ICE_CRYSTAL(
        id = "ice_crystal",
        displayName = "Buz & Kristal Turkuaz",
        description = "Kutupsal krio-dijital kristal ve berrak buz",
        primaryNeon = Color(0xFF00E5FF),
        secondaryNeon = Color(0xFF80DEEA),
        tertiaryNeon = Color(0xFFE0F7FA),
        glowColor = Color(0xFF18FFFF)
    )
}

enum class KeonPackage(
    val id: String,
    val title: String,
    val badge: String,
    val price: String,
    val description: String,
    val features: List<String>,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    FREE(
        id = "free",
        title = "Ücretsiz Plan",
        badge = "🌱 ÜCRETSİZ",
        price = "₺0 / Ücretsiz",
        description = "Temel Keon AI Özellikleri, Standart Yanıt Hızı & Günlük AI Erişimi",
        features = listOf(
            "💬 Temel Keon AI Sohbet & Soru-Cevap",
            "⚡ Standart AI Yanıt Hızı",
            "🎨 Standart Neon Temalar & Arayüz",
            "🎮 Temel Mini Oyunlar",
            "☁️ 5 GB Temel Cloud Storage",
            "🎙️ Standart Sesli Asistan Erişimi"
        ),
        primaryColor = Color(0xFF00E5FF),
        secondaryColor = Color(0xFF3D5AFE)
    ),
    ULTIMATUM(
        id = "ultimatum",
        title = "Ultimatom Paketi",
        badge = "⚡ ULTIMATUM PRO",
        price = "₺149 / Ay",
        description = "Reklamsız VIP Deneyim, Ultra Gerçekçi Animasyonlar & Işık Hızında Açılış",
        features = listOf(
            "🚫 %100 Reklamsız Kesintisiz VIP Deneyim",
            "🎨 Ultra Gerçekçi Kuantum & Fizik Animasyonları",
            "⚡ Ultra Hızlı Yapay Zeka Yanıt Motoru",
            "🚀 Işık Hızında Anında Uygulama Açılışı",
            "🧠 Gelişmiş Derin Mantıksal Çıkarım Zinciri",
            "☁️ 100 GB CloudBox Yüksek Hızlı VIP Depolama"
        ),
        primaryColor = Color(0xFFFF3366),
        secondaryColor = Color(0xFFFF9900)
    ),
    AURA(
        id = "aura",
        title = "Aura Paketi",
        badge = "🔮 AURA VIP",
        price = "₺79 / Ay",
        description = "Reklamsız Deneyim, Holografik Siber Aura, 30+ Özel Tema, Arka Plan & Simge",
        features = listOf(
            "🚫 %100 Reklamsız Kesintisiz AURA VIP Deneyim",
            "🎨 +30 Ekstra Özel Siber & Holografik Tema Koleksiyonu",
            "🖼️ +30 Ekstra HD Siber Arka Plan & Canlı Izgara Duvar Kağıdı",
            "🔰 +30 Ekstra Özel Holografik VIP Simge Seti & Neon İkon Teması",
            "⚡ Ultra Gerçekçi Holografik Siber Aura Animasyonları",
            "🚀 Işık Hızında Anında Açılış & Süper Hızlı Cevaplar",
            "🔮 Holografik Siber Aura Işıma Efekti",
            "☁️ 50 GB CloudBox Şifreli Depolama",
            "🎙️ Sesli Asistan Kristal Gürültü Filtresi",
            "✨ Özel AURA VIP Kullanıcı Rozeti"
        ),
        primaryColor = Color(0xFFD000FF),
        secondaryColor = Color(0xFF00FFFF)
    ),
    ZUMRUDU_ANKA(
        id = "phoenix",
        title = "Zümrüdü Anka Paketi",
        badge = "🔥 ZÜMRÜDÜ ANKA EFSANEVİ",
        price = "₺199 / Ay",
        description = "Efsanevi Reklamsız VIP, 100+ Tema, Arka Plan & Simge Koleksiyonu",
        features = listOf(
            "🚫 %100 Reklamsız Sınırsız Efsanevi VIP Deneyim",
            "👑 +100 Ekstra Efsanevi Zümrüdü Anka Tema Koleksiyonu",
            "🖼️ +100 Ekstra VIP Arka Plan & Ultra HD Canlı Duvar Kağıdı Galerisi",
            "🔰 +100 Ekstra Efsanevi Simge Seti, 3D Logo Stili & VIP Rozetler",
            "🔥 Ultra Gerçekçi Zümrüdü Anka Alev & Parçacık Animasyonları",
            "🚀 En Yüksek Öncelikli Işık Hızında Açılış & Anında Cevap",
            "🔥 Sonsuz Kuantum Zeka & Bilişsel Hafıza",
            "☁️ Sınırsız CloudBox & Yüksek Hızlı Otomatik Yedekleme",
            "🎮 Özel Oyun İçi VIP Modeller & Hile Kodları",
            "👑 7/24 Öncelikli Birebir VIP Teknik Destek"
        ),
        primaryColor = Color(0xFF00FF88),
        secondaryColor = Color(0xFFFFD700)
    )
}

enum class TechWallpaperId(
    val displayName: String,
    val description: String,
    @DrawableRes val drawableRes: Int?
) {
    CYBER_GRID(
        displayName = "3D Siber Izgara",
        description = "Mavi Devre Ağı Duvar Kağıdı",
        drawableRes = R.drawable.img_tech_wallpaper_cyber
    ),
    QUANTUM_MATRIX(
        displayName = "Kuantum Matris",
        description = "Yeşil Parçacık Duvar Kağıdı",
        drawableRes = R.drawable.img_tech_wallpaper_quantum
    ),
    AURORA_WAVE(
        displayName = "Aurora Hologram",
        description = "Mor Enerji Dalgası Duvar Kağıdı",
        drawableRes = R.drawable.img_tech_wallpaper_aurora
    ),
    AURA_NEON_WAVE(
        displayName = "🔮 Aura VIP 30+ Canlı Arka Plan",
        description = "Mor & Turkuaz Holografik Işıma Izgarası",
        drawableRes = null
    ),
    PHOENIX_FIRE_MATRIX(
        displayName = "🔥 Zümrüdü Anka 100+ Efsanevi Arka Plan",
        description = "Canlı Altın & Zümrüt Alev Parçacık Matrisi",
        drawableRes = null
    ),
    FLAME_CORE_MATRIX(
        displayName = "🔥 Alev Çekirdeği Mor & Turuncu Plazma",
        description = "Neon Mor & Alev Turuncusu Yüksek Zeka Izgarası",
        drawableRes = null
    ),
    CANVAS_LIVE_GRID(
        displayName = "Canlı Animasyonlu Izgara",
        description = "Arka planda süzülen dijital ışıklar",
        drawableRes = null
    ),
    MINIMAL_DARK(
        displayName = "Sade Uzay Gradyanı",
        description = "Soft ve göz yormayan arka plan",
        drawableRes = null
    ),
    AMOLED_PURE_BLACK(
        displayName = "🌑 Saf AMOLED Siyah",
        description = "Sıfır ışık, tam karanlık arayüz arka planı",
        drawableRes = null
    ),
    DEEP_SPACE_NIGHT(
        displayName = "🌌 Derin Gece Matrisi",
        description = "Göz dinlendiren karanlık gece ızgarası",
        drawableRes = null
    ),
    STEALTH_CARBON_GRID(
        displayName = "🕶️ Karbon Koyu Arayüz",
        description = "Mat dokulu şık karanlık arayüz arka planı",
        drawableRes = null
    )
}
