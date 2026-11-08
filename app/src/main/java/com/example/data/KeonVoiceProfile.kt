package com.example.data

/**
 * Voice Tone & Timber Profile for Keon AI Voice Engine.
 * Configures TTS Pitch, Speech Rate, Voice Gender and Audio Smoothing.
 */
enum class KeonVoiceProfile(
    val id: String,
    val title: String,
    val description: String,
    val pitch: Float,
    val speechRate: Float,
    val badge: String,
    val tag: String,
    val isMaleVoice: Boolean = true
) {
    SMOOTH_MALE_BARITONE(
        id = "smooth_male_baritone",
        title = "🎙️ Pürüzsüz & Karizmatik Erkek Sesi (İpeksi Bariton)",
        description = "Özel filtrelenmiş pürüzsüz erkek tonu, tok rezonans ve akıcı, doğal artikülasyon.",
        pitch = 0.83f,
        speechRate = 0.97f,
        badge = "ÖNERİLEN • ERKEK SESİ",
        tag = "🔊 İpeksi Erkek Sesi",
        isMaleVoice = true
    ),
    DEEP_WARM_MASCULINE(
        id = "deep_warm_masculine",
        title = "🎙️ Derin & Tok Erkek Sesi (Kalın Tını)",
        description = "Daha düşük perde (derin bas rezonansı), sakin tempo ve net erkek tonlaması.",
        pitch = 0.78f,
        speechRate = 0.93f,
        badge = "DERİN BAS",
        tag = "🔊 Derin Erkek Sesi",
        isMaleVoice = true
    ),
    BALANCED_CYBER(
        id = "balanced_cyber",
        title = "🎙️ Keon Standart Erkek (Dengeli)",
        description = "Doğal Türkçe artikülasyon, dengeli frekans ve akıcı erkek konuşma temposu.",
        pitch = 0.90f,
        speechRate = 1.02f,
        badge = "DENGELİ",
        tag = "🎯 Dengeli",
        isMaleVoice = true
    ),
    SOFT_CALM_FEMININE(
        id = "soft_calm_feminine",
        title = "🎙️ İnce, Zarif & Dingin Ses (Kadın)",
        description = "Hafif yüksek perde, yumuşak artikülasyon ve huzurlu ritim.",
        pitch = 1.25f,
        speechRate = 1.0f,
        badge = "KADIN SESİ",
        tag = "🌸 Zarif & Dingin",
        isMaleVoice = false
    ),
    CYBER_SYNTH_AI(
        id = "cyber_synth_ai",
        title = "⚡ Siber Sentetik Asistan (Fütüristik)",
        description = "Dinamik yapay zeka tonu, hafif tiz ve hızlı teknolojik tepki.",
        pitch = 1.12f,
        speechRate = 1.15f,
        badge = "FÜTÜRİSTİK",
        tag = "🤖 Siber AI",
        isMaleVoice = false
    ),
    CUSTOM(
        id = "custom",
        title = "🎛️ Özel Ayarlanmış Frekans & Perde",
        description = "Kullanıcı veya ses analizine göre özel belirlenen perde ve konuşma hızı.",
        pitch = 0.85f,
        speechRate = 1.0f,
        badge = "ÖZEL",
        tag = "🎚️ Manuel",
        isMaleVoice = true
    );

    companion object {
        fun fromId(id: String): KeonVoiceProfile {
            return values().firstOrNull { it.id == id } ?: SMOOTH_MALE_BARITONE
        }

        /**
         * Cleans and normalizes text for crystal-clear, smooth TTS speech without robotic glitches.
         */
        fun cleanTextForSmoothSpeech(rawText: String): String {
            if (rawText.isBlank()) return ""
            var cleaned = rawText
                // Remove code blocks and inline code
                .replace(Regex("```[\\s\\S]*?```"), "")
                .replace(Regex("`[^`]*`"), "")
                // Remove markdown bold, italic, headers, bullet points
                .replace(Regex("[#*_~`\\[\\]\\(\\)>]"), " ")
                // Remove URLs
                .replace(Regex("https?://\\S+"), "")
                // Remove emojis and special non-letter symbols
                .replace(Regex("[\\p{So}\\p{Cn}\\p{Sk}]"), " ")
                // Remove excessive dashes, equals, underscores
                .replace(Regex("[-=_]{2,}"), " ")
                // Replace multiple spaces/newlines with a single space
                .replace(Regex("\\s+"), " ")
                .trim()

            return cleaned
        }
    }
}

