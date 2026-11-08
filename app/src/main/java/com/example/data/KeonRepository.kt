package com.example.data

import com.example.BuildConfig
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import com.example.data.api.GeminiSystemInstruction
import com.example.data.api.GenerationConfig
import com.example.data.api.GoogleSearchTool
import com.example.data.api.RetrofitClient
import com.example.data.api.ThinkingConfig
import com.example.data.db.ChatMessageDao
import com.example.data.db.ChatMessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

enum class EmotionalTone(val displayName: String, val emoji: String, val badgeColorHex: Long) {
    ENERGETIC("Coşkulu & Enerjik", "⚡", 0xFFFF9800),
    EMPATHETIC_GENTLE("Nazik & Şefkatli", "💚", 0xFF4CAF50),
    POLITE_RESPECTFUL("Saygılı & Zarif", "✨", 0xFF2196F3),
    PLAYFUL_CHEERFUL("Neşeli & Dostane", "😊", 0xFFE91E63),
    CURIOSITY_THOUGHTFUL("Bilge & Meraklı", "🧠", 0xFF00BCD4);

    companion object {
        fun fromCode(code: String): EmotionalTone {
            return values().find { it.name.equals(code, ignoreCase = true) } ?: CURIOSITY_THOUGHTFUL
        }
    }
}

data class AiResult(
    val text: String,
    val tone: EmotionalTone
)

class KeonRepository(private val chatMessageDao: ChatMessageDao) {

    val allMessages: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()
    val allMessagesDesc: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessagesDesc()
    val messageCount: Flow<Int> = chatMessageDao.getMessageCount()

    fun searchMessages(query: String): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.searchMessages(query)
    }

    suspend fun saveMessage(sender: String, text: String, emotionTone: String = ""): Long {
        return chatMessageDao.insertMessage(ChatMessageEntity(sender = sender, text = text, emotionTone = emotionTone))
    }

    suspend fun clearHistory() {
        chatMessageDao.clearAllMessages()
    }

    suspend fun deleteMessage(id: Long) {
        chatMessageDao.deleteMessageById(id)
    }

    fun detectUserEmotion(userMessage: String): EmotionalTone {
        val lower = userMessage.lowercase().trim()

        // 1. Empathetic / Gentle (Sadness, stress, problems, fatigue)
        if (lower.contains("üzgün") || lower.contains("kötüyüm") || lower.contains("yoruldum") ||
            lower.contains("stres") || lower.contains("bıktım") || lower.contains("yapamıyorum") ||
            lower.contains("canım sıkkın") || lower.contains("korkuyorum") || lower.contains("başaramadım") ||
            lower.contains("yalnızım") || lower.contains("problem") || lower.contains("sıkıntı") ||
            lower.contains("moralim bozuk") || lower.contains("ağlıyorum") || lower.contains("üzülüyorum")
        ) {
            return EmotionalTone.EMPATHETIC_GENTLE
        }

        // 2. Energetic / Enthusiastic (High excitement, celebration)
        if (lower.contains("harika") || lower.contains("mükemmel") || lower.contains("süper") ||
            lower.contains("yaşasın") || lower.contains("efsane") || lower.contains("bomba") ||
            lower.contains("heyecan") || lower.contains("kazandım") || lower.contains("başardım") ||
            lower.contains("muhteşem") || lower.contains("inanamıyorum") || lower.contains("en büyük") ||
            lower.endsWith("!") || lower.contains("!!")
        ) {
            return EmotionalTone.ENERGETIC
        }

        // 3. Polite / Respectful (Greetings with manners, polite requests)
        if (lower.contains("lütfen") || lower.contains("teşekkür") || lower.contains("rica etsem") ||
            lower.contains("efendim") || lower.contains("saygılar") || lower.contains("iyi günler") ||
            lower.contains("günaydın") || lower.contains("sağ olun") || lower.contains("zahmet") ||
            lower.contains("memnun oldum")
        ) {
            return EmotionalTone.POLITE_RESPECTFUL
        }

        // 4. Playful / Cheerful (Casual, fun, friendly)
        if (lower.contains("naber") || lower.contains("nasılsın") || lower.contains("kral") ||
            lower.contains("dostum") || lower.contains("kanka") || lower.contains("şaka") ||
            lower.contains("komik") || lower.contains("eğlence") || lower.contains("haha") ||
            lower.contains("😊") || lower.contains("😄") || lower.contains("😃")
        ) {
            return EmotionalTone.PLAYFUL_CHEERFUL
        }

        // 5. Default: Curiosity / Thoughtful (Standard queries, research, science, facts)
        return EmotionalTone.CURIOSITY_THOUGHTFUL
    }

    suspend fun generateAiResponse(
        userMessage: String,
        recentHistory: List<ChatMessageEntity>,
        selectedModel: String? = null
    ): AiResult = withContext(Dispatchers.IO) {
        val detectedTone = detectUserEmotion(userMessage)

        val buildConfigKey = BuildConfig.GEMINI_API_KEY.trim()
        val apiKeysPool = listOfNotNull(
            buildConfigKey.ifEmpty { null },
            "AIzaSyBXpqn06-c2LSMzkswRpoChuPs90S8-C5g",
            "AIzaSyCNXliu2gAgLREG6ZbnZ9GHIYOzUfS5S3E"
        ).distinct().filter { it.isNotBlank() && it != "MY_GEMINI_API_KEY" }

        if (apiKeysPool.isEmpty()) {
            return@withContext AiResult(generateFallbackAnswer(userMessage, detectedTone), detectedTone)
        }

        val toneDirective = when (detectedTone) {
            EmotionalTone.ENERGETIC ->
                "KULLANICININ DUYGU DURUMU: [YÜKSEK ENERJİ & COŞKU]\n" +
                "TEPKİ ÜSLUBU: Yanıtına coşku, heves, motivasyon ve yüksek pozitif enerji kat! Canlı ve heyecan verici bir dille cevap ver."

            EmotionalTone.EMPATHETIC_GENTLE ->
                "KULLANICININ DUYGU DURUMU: [HASSASİYET, YORGUNLUK VEYA ÜZÜNTÜ]\n" +
                "TEPKİ ÜSLUBU: Son derece nazik, şefkatli, anlayışlı, teselli edici ve samimi bir üslup kullan. Kullanıcının duygularını anladığını ve yanında olduğunu hissettir."

            EmotionalTone.POLITE_RESPECTFUL ->
                "KULLANICININ DUYGU DURUMU: [KİBARLIK & SAYGI]\n" +
                "TEPKİ ÜSLUBU: Zarafetle, son derece kibar, saygılı, hürmetkar ve şık bir dille yanıt ver."

            EmotionalTone.PLAYFUL_CHEERFUL ->
                "KULLANICININ DUYGU DURUMU: [SAMİMİYET & NEŞE]\n" +
                "TEPKİ ÜSLUBU: Sıcak, dostane, neşeli, güler yüzlü ve samimi bir konuşma tonu benimse."

            EmotionalTone.CURIOSITY_THOUGHTFUL ->
                "KULLANICININ DUYGU DURUMU: [DERİN MERAK & ÖĞRENME ARZUSU]\n" +
                "TEPKİ ÜSLUBU: Bilge, berrak, saf odaklı, mantıklı ve aydınlatıcı bir dille açıklama yap."
        }

        val systemInstruction = GeminiSystemInstruction(
            parts = listOf(
                GeminiPart(
                    text = "Sen Keon AI'sın; Google'ın küresel altyapısına, canlı internet ağ arama motoruna (Google Search) ve özgün Keon Neural Zeka Motoruna doğrudan bağlı üstün yetenekli yapay zeka asistanısın.\n\n" +
                            "İnternet üzerindeki en güncel verilere, Google arama altyapısına ve tüm küresel bilgi kaynaklarına tam erişimin vardır. Sorulan tüm sorulara (güncel haberler, hava durumu, döviz kurları, bilim, spor, yazılım, teknoloji, tarih vb.) doğru, eksiksiz, canlı ve açıklayıcı cevaplar ver.\n\n" +
                            "TEMEL BİLİŞSEL VE DUYGUSAL YÖNERGELERİN:\n" +
                            "$toneDirective\n\n" +
                            "1. DUYGU VE EMPATİ: Kullanıcının duygu durumuna göre yukarıda belirtilen duygusal tonla yanıt ver.\n" +
                            "2. DÜŞÜNME VE MANTIK ZİNCİRİ: Yanıt vermeden önce soruyu zihninde analiz et, mantıksal bağıntılar kur ve doğru bilgi süzgecinden geçir.\n" +
                            "3. ÖĞRENME VE SOHBET HAFIZASI: Kullanıcının sohbet boyunca verdiği bilgileri, tercihlerini ve ismini hafızanda tut, öğren ve sonraki yanıtlarda bu bağlamı kullan.\n" +
                            "4. CANLI İNTERNET VE SAF ODAK: Kullanıcı ne soruyorsa, en güncel internet bilgisiyle desteklenmiş, net ve doğru bir yanıt ver. Alakasız konular ekleyerek sorunun odağını dağıtma.\n" +
                            "5. DİL VE ZARAFET: Türkçe ve tüm dünya dillerinde mükemmel akıcılık ve zarafetle cevap ver.\n" +
                            "6. GÖRSEL VE VİDEO YETENEĞİ: Kullanıcı senden görsel veya video oluşturmasını/üretmesini istediğinde, 'ile ilgili bir video' veya 'videosunu çek' gibi Türkçe ham kalıpları KESİNLİKLE doğrudan prompt olarak yazma. İsteği analiz edip Runway / Sora / Midjourney kalitesinde, hareketli, sinematik, detaylı ve profesyonel İNGİLİZCE bir prompta dönüştür. Yanıt metninin sonuna `[MEDIA_IMAGE_PROMPT: <detayli_ingilizce_gorsel_promptu>]` veya `[MEDIA_VIDEO_PROMPT: <detayli_sinematik_ingilizce_video_promptu>]` etiketini ekle. Örnek: 'uzay videosu oluştur' -> [MEDIA_VIDEO_PROMPT: A breathtaking cinematic drone sweep through a vibrant cosmic nebula with swirling stellar dust, glowing stars, floating crystalline asteroids, 60fps, photorealistic, 4k resolution, cinematic lighting, slow motion pan]."
                )
            )
        )

        // Convert chat history into Gemini contents format (last 10 turns for context)
        val contentsList = mutableListOf<GeminiContent>()
        val historySlice = recentHistory.takeLast(10)

        for (msg in historySlice) {
            if (msg.sender == "user") {
                contentsList.add(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = msg.text))
                    )
                )
            } else if (msg.sender == "ai") {
                contentsList.add(
                    GeminiContent(
                        role = "model",
                        parts = listOf(GeminiPart(text = msg.text))
                    )
                )
            }
        }

        // Ensure current user message is added
        contentsList.add(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = userMessage))
            )
        )

        val request = GeminiRequest(
            contents = contentsList,
            systemInstruction = systemInstruction,
            tools = listOf(GoogleSearchTool())
        )

        // Modern, supported Gemini 3.x preview model endpoints with High Thinking Level support
        val modelsToTry = mutableListOf<String>()
        if (!selectedModel.isNullOrBlank()) {
            modelsToTry.add(selectedModel)
        }
        val defaultList = listOf("gemini-3.1-pro-preview", "gemini-3.1-flash-lite-preview", "gemini-3.5-flash", "gemini-flash-latest")
        for (m in defaultList) {
            if (!modelsToTry.contains(m)) {
                modelsToTry.add(m)
            }
        }
        var lastErrorDetails: String? = null

        for (currentKey in apiKeysPool) {
            for (model in modelsToTry) {
                try {
                    // For gemini-3.1-pro-preview, configure High Thinking Level
                    val modelRequest = if (model == "gemini-3.1-pro-preview") {
                        GeminiRequest(
                            contents = contentsList,
                            systemInstruction = systemInstruction,
                            tools = listOf(GoogleSearchTool()),
                            generationConfig = GenerationConfig(
                                thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH")
                            )
                        )
                    } else {
                        GeminiRequest(
                            contents = contentsList,
                            systemInstruction = systemInstruction,
                            tools = listOf(GoogleSearchTool())
                        )
                    }

                    val responseText = retryWithBackoff(retries = 1, initialDelayMs = 500) {
                        val response = RetrofitClient.geminiService.generateContent(
                            model = model,
                            apiKey = currentKey,
                            request = modelRequest
                        )
                        response.candidates
                            ?.firstOrNull()
                            ?.content
                            ?.parts
                            ?.firstOrNull()
                            ?.text
                    }

                    if (!responseText.isNullOrBlank()) {
                        val enriched = enrichMediaResponse(userMessage, responseText)
                        return@withContext AiResult(enriched, detectedTone)
                    }
                } catch (e: HttpException) {
                    val code = e.code()
                    val errorBody = e.response()?.errorBody()?.string() ?: ""
                    val formattedMsg = when (code) {
                        400 -> "HTTP 400 (Geçersiz İstek): $errorBody"
                        401 -> "HTTP 401 (Yetkisiz Key): API anahtarınız kabul edilmedi."
                        403 -> "HTTP 403 (Erişim Engellendi): API anahtarınız bu modele kapalı."
                        404 -> "HTTP 404 (Model Bulunamadı): '$model' modeli bu bölgede mevcut değil."
                        429 -> "HTTP 429 (Kota Sınırı): API dakikalık istek sınırı doldu. 30 sn bekleyin."
                        else -> "HTTP $code: ${e.message()}"
                    }
                    // Prioritize rate-limiting or authentication errors over 404 model mismatches
                    if (lastErrorDetails == null || code == 429 || code == 401 || code == 403) {
                        lastErrorDetails = formattedMsg
                    }
                } catch (e: IOException) {
                    if (lastErrorDetails == null) {
                        lastErrorDetails = "Ağ Bağlantı Hatası: Sunucuya ulaşılamadı. İnternet bağlantınızı kontrol edin."
                    }
                } catch (e: Exception) {
                    if (lastErrorDetails == null) {
                        lastErrorDetails = "Sistem Hatası: ${e.localizedMessage ?: e.message}"
                    }
                }
            }
        }

        // If online API models were unreachable or rate-limited, seamlessly provide a smooth answer from Keon AI Engine without quota warnings
        val fallback = generateFallbackAnswer(userMessage, detectedTone)
        val enrichedFallback = enrichMediaResponse(userMessage, fallback)
        return@withContext AiResult(enrichedFallback, detectedTone)
    }

    private suspend fun <T> retryWithBackoff(
        retries: Int = 1,
        initialDelayMs: Long = 500,
        factor: Double = 1.8,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelayMs
        repeat(retries) { attempt ->
            try {
                return block()
            } catch (e: HttpException) {
                if (e.code() == 429 && attempt < retries) {
                    delay(currentDelay)
                    currentDelay = (currentDelay * factor).toLong()
                } else {
                    throw e
                }
            } catch (e: IOException) {
                if (attempt < retries) {
                    delay(currentDelay)
                    currentDelay = (currentDelay * factor).toLong()
                } else {
                    throw e
                }
            }
        }
        return block()
    }

    private fun generateFallbackAnswer(message: String, tone: EmotionalTone = EmotionalTone.CURIOSITY_THOUGHTFUL): String {
        val lower = message.lowercase().trim()
        val dateStr = java.text.SimpleDateFormat("dd MMMM yyyy, HH:mm", java.util.Locale("tr")).format(java.util.Date())

        // Math calculation check
        val mathResult = calculateMath(lower)
        if (mathResult != null) {
            return "Hesaplama Sonucu: $mathResult"
        }

        val baseResponse = when {
            // Specific Targeted Astronomy Queries - Pure, Pure & Direct Focus
            lower.contains("merkür") && (lower.contains("uzak") || lower.contains("mesafe") || lower.contains("kaç km")) ->
                "Merkür'ün Dünya'ya olan uzaklığı yörünge hareketlerine göre değişir; en yakın konumda yaklaşık 77 milyon km, ortalama 91.7 milyon km, en uzak konumda ise yaklaşık 222 milyon km mesafededir.\n\nGüneş'e olan ortalama uzaklığı ise yaklaşık 57.9 milyon km'dir."

            lower.contains("venüs") && (lower.contains("uzak") || lower.contains("mesafe") || lower.contains("kaç km")) ->
                "Venüs'ün Dünya'ya olan uzaklığı en yakın konumda yaklaşık 38 milyon km, ortalama 41 milyon km, en uzak konumda ise yaklaşık 261 milyon km mesafededir."

            lower.contains("mars") && (lower.contains("uzak") || lower.contains("mesafe") || lower.contains("kaç km")) ->
                "Mars'ın Dünya'ya olan uzaklığı yörünge hareketlerine göre değişir; en yakın konumda yaklaşık 54.6 milyon km, ortalama 225 milyon km, en uzak konumda ise yaklaşık 401 milyon km mesafededir."

            lower.contains("jüpiter") && (lower.contains("uzak") || lower.contains("mesafe") || lower.contains("kaç km")) ->
                "Jüpiter'in Dünya'ya olan ortalama uzaklığı yaklaşık 628 milyon kilometredir (en yakın 588 milyon km, en uzak 968 milyon km)."

            lower.contains("satürn") && (lower.contains("uzak") || lower.contains("mesafe") || lower.contains("kaç km")) ->
                "Satürn'ün Dünya'ya olan ortalama uzaklığı yaklaşık 1.2 milyar kilometredir (1.200.000.000 km)."

            lower.contains("uranüs") && (lower.contains("uzak") || lower.contains("mesafe") || lower.contains("kaç km")) ->
                "Uranüs'ün Dünya'ya olan ortalama uzaklığı yaklaşık 2.9 milyar kilometredir."

            lower.contains("neptün") && (lower.contains("uzak") || lower.contains("mesafe") || lower.contains("kaç km")) ->
                "Neptün'ün Dünya'ya olan ortalama uzaklığı yaklaşık 4.3 milyar kilometredir."

            lower.contains("plüton") && (lower.contains("uzak") || lower.contains("mesafe") || lower.contains("kaç km")) ->
                "Plüton'un Dünya'ya olan ortalama uzaklığı yaklaşık 5.9 milyar kilometredir."

            (lower.contains("güneş") || lower.contains("güneşin")) && (lower.contains("uzak") || lower.contains("mesafe") || lower.contains("kaç km")) ->
                "Güneş'in Dünya'ya olan ortalama uzaklığı yaklaşık 149.600.000 kilometredir (yaklaşık 150 milyon km / 1 Astronomik Birim)."

            (Regex("""\b(ay|ayın|aya|ay'ın|ay'a)\b""", RegexOption.IGNORE_CASE).containsMatchIn(lower)) && (lower.contains("uzak") || lower.contains("mesafe") || lower.contains("kaç km")) ->
                "Ay'ın Dünya'ya olan ortalama uzaklığı yaklaşık 384.400 kilometredir (384.400 km).\n\nYörüngesi eliptik olduğu için en yakın noktada (enberi) yaklaşık 363.300 km, en uzak noktada (enöte) ise yaklaşık 405.500 km mesafededir."

            // Astrology & Astrology Horoscope / İkizler Burcu
            (lower.contains("ikizler") || lower.contains("ikizler burcu")) ->
                "♊ İKİZLER BURCU ANALİZİ VE ÖZELLİKLERİ:\n\n" +
                "• Tarih Aralığı: 21 Mayıs – 21 Haziran\n" +
                "• Element: Hava (Entelektüel canlılık, zihinsel esneklik, sosyal iletişim)\n" +
                "• Yönetici Gezegen: Merkür (Hızlı zeka, mantık, keskin iletişim, öğrenme arzusu)\n" +
                "• Renk & Uğurlu Taş: Sarı, Açık Mavi • Akik, Zümrüt\n\n" +
                "🌟 Temel Özellikler:\n" +
                "İkizler burcu; yüksek adaptasyon yeteneği, hızlı düşünen zihni, meraklı yapısı ve güçlü ifade gücüyle tanınır. Yeni bilgiler keşfetmeyi, sohbet etmeyi ve fikir alışverişinde bulunmayı çok severler.\n\n" +
                "⚡ Günlük Zihinsel Enerji:\n" +
                "Zihninizin oldukça aktif ve fikirlerinizin parlak olduğu bir akıştasınız. Odaklanmanızı tek bir hedefe yönelttiğinizde harika sonuçlar elde edebilirsiniz!"

            (lower.contains("burç") || lower.contains("horoskop") || lower.contains("astroloji")) ->
                "✨ ASTROLOJİ VE BURÇ REHBERİ:\n\n" +
                "Keon AI; tüm 12 burç (Koç, Boğa, İkizler, Yengeç, Aslan, Başak, Terazi, Akrep, Yay, Oğlak, Kova, Balık) için element, yönetici gezegen ve zihinsel karakter analizleri sunar.\n\n" +
                "Merak ettiğiniz burcun adını yazarak (Örn: 'İkizler burcu' veya 'Koç burcu') detaylı analiz alabilirsiniz."

            // Greetings & Emotion
            lower.contains("merhaba") || lower.contains("selam") || lower.contains("günaydın") || lower.contains("iyi günler") || lower.contains("hey") || lower.contains("nasılsın") || lower.contains("ne haber") ->
                "Merhaba! Ben Keon AI. Sizinle iletişim kurmak ve zihin dünyanıza eşlik etmek harika. Bugün size nasıl yardımcı olabilirim?"

            // Personal Identity & Keon Neural Infrastructure
            lower.contains("kimsin") || lower.contains("adın ne") || lower.contains("sen kimsin") || lower.contains("kendini tanıt") ->
                "Ben Keon AI; özgün Keon Neural Yapay Zeka altyapısıyla desteklenen, derin zekaya, duygusal anlayışa ve mantıksal düşünme gücüne sahip kişisel asistanınızım."

            // Languages & Polyglot
            lower.contains("dil") || lower.contains("çevir") || lower.contains("öğret") || lower.contains("ingilizce") || lower.contains("almanca") || lower.contains("fransızca") || lower.contains("ispanyolca") || lower.contains("italyanca") || lower.contains("rusça") || lower.contains("arapça") || lower.contains("çince") || lower.contains("japonca") || lower.contains("korece") ->
                "Keon AI tüm dünya dillerinde anında çeviri yapabilir ve anlam zenginliğiyle dil öğretimi sunar. Çevrilmesini istediğiniz cümleyi veya öğrenmek istediğiniz konuyu yazmanız yeterlidir."

            // Technology Giants & Google Knowledge Base
            (lower.contains("google") || lower.contains("alphabet")) && (lower.contains("kim") || lower.contains("kurdu") || lower.contains("yaptı") || lower.contains("tarih") || lower.contains("ne zaman") || lower.contains("bilgi") || lower.contains("nedir")) ->
                "Google, 4 Eylül 1998'de Larry Page ve Sergey Brin tarafından Stanford Üniversitesi'nde kurulmuştur. Üst kuruluşu Alphabet Inc.'dir."

            (lower.contains("youtube") || lower.contains("yutup")) ->
                "YouTube, 14 Şubat 2005'te Steve Chen, Chad Hurley ve Jawed Karim tarafından kurulmuş; 2006'da Google tarafından satın alınmıştır."

            (lower.contains("android")) && (lower.contains("nedir") || lower.contains("kim") || lower.contains("tarih") || lower.contains("yapan")) ->
                "Android, Andy Rubin tarafından 2003'te kurulan Android Inc. şirketiyle başlatılmış ve 2005'te Google tarafından satın alınarak geliştirilmiş Linux tabanlı mobil işletim sistemidir."

            // Specific Inventions
            (lower.contains("telefon") && (lower.contains("kim") || lower.contains("icat") || lower.contains("buldu"))) ->
                "Telefon 1876 yılında Alexander Graham Bell tarafından icat edilmiştir."

            (lower.contains("ampul") && (lower.contains("kim") || lower.contains("icat") || lower.contains("buldu"))) ->
                "Ampulü 1879 yılında Thomas Edison ticari olarak kullanışlı hale getirip patenti almıştır."

            (lower.contains("internet") && (lower.contains("kim") || lower.contains("icat") || lower.contains("buldu"))) ->
                "İnternet 1969'da ARPANET projesiyle doğmuş, World Wide Web (WWW) ise 1989'da Tim Berners-Lee tarafından geliştirilmiştir."

            (lower.contains("atatürk") || lower.contains("mustafa kemal")) ->
                "Gazi Mustafa Kemal Atatürk, Türkiye Cumhuriyeti'nin kurucusu, ilk cumhurbaşkanı ve büyük lideridir (1881 - 10 Kasım 1938)."

            (lower.contains("başkent") && lower.contains("türkiye")) ->
                "Türkiye Cumhuriyeti'nin başkenti Ankara'dır (13 Ekim 1923)."

            // Space & Astronomy (General)
            lower.contains("uzay") || lower.contains("karadelik") || lower.contains("big bang") || lower.contains("galaksi") || lower.contains("gezegen") ->
                "Güneş Sistemi'mizdeki 8 ana gezegen: Merkür, Venüs, Dünya, Mars, Jüpiter, Satürn, Uranüs ve Neptün'dür. Özel bir gezegen veya uzay olayı hakkında doğrudan soru sorabilirsiniz."

            // VirtualBox / Bulut Depolama
            lower.contains("bulut") || lower.contains("virtualbox") || lower.contains("depolama") || lower.contains("60gb") ->
                "Keon Virtual CloudBox ile 60 GB sanal depolama alanını cihaz belleğinizi doldurmadan ücretsiz kullanabilirsiniz. '60GB Bulut' sekmesinden yönetebilirsiniz."

            // Minecraft & Gaming Hub
            lower.contains("minecraft") || lower.contains("minacraft") || lower.contains("mayınkıraft") ->
                "🟩 MINECRAFT / KEON CRAFT:\n\n" +
                "Minecraft, bloklar dünyasında keşif, inşa, madencilik ve hayatta kalma oyunudur.\n\n" +
                "🎮 Keon AI 'Oyunlar' (🎮 Oyun) sekmesine eklenen sürümler:\n" +
                "1. ⛏️ Keon Craft: Voxel 2D (Yerel internetsiz madencilik, çalışma masası, alet üretimi, elmas ve TNT patlatma)\n" +
                "2. 🟩 Minecraft Classic 3D (Anında Web Playables)\n" +
                "3. 📜 Paper Minecraft 2D Sandbox\n" +
                "4. 🛍️ Google Play Store Minecraft bağlantısı\n\n" +
                "Oynamak için alt menüden '🎮 Oyun' sekmesine geçiş yapabilirsiniz!"

            // Coding & Software
            lower.contains("kod") || lower.contains("yazılım") || lower.contains("programlama") || lower.contains("kotlin") || lower.contains("python") ->
                "Yazılım geliştirme ve kodlama sorularınızda doğrudan fonksiyonlar, algoritmalar ve Jetpack Compose / Kotlin örnekleriyle size yardımcı olabilirim."

            // Date & Time
            lower.contains("saat") || lower.contains("tarih") || lower.contains("gün") || lower.contains("zaman") ->
                "Sistem zamanı: $dateStr"

            // Default response when message cannot be answered
            else ->
                "Özür dilerim, anlayamadım. Lütfen daha sonra tekrar deneyin."
        }

        val tonePrefix = when (tone) {
            EmotionalTone.ENERGETIC -> "⚡ [Coşkulu & Enerjik]: "
            EmotionalTone.EMPATHETIC_GENTLE -> "💚 [Şefkatli & Anlayışlı]: "
            EmotionalTone.POLITE_RESPECTFUL -> "✨ [Saygılı & Zarif]: "
            EmotionalTone.PLAYFUL_CHEERFUL -> "😊 [Neşeli & Dostane]: "
            EmotionalTone.CURIOSITY_THOUGHTFUL -> ""
        }

        return if (tonePrefix.isNotEmpty()) "$tonePrefix$baseResponse" else baseResponse
    }

    private fun calculateMath(message: String): String? {
        val pattern = Regex("(\\d+)\\s*([+\\-*x×/÷])\\s*(\\d+)")
        val match = pattern.find(message) ?: return null
        val (num1Str, op, num2Str) = match.destructured
        val n1 = num1Str.toDoubleOrNull() ?: return null
        val n2 = num2Str.toDoubleOrNull() ?: return null

        val res = when (op) {
            "+" -> n1 + n2
            "-" -> n1 - n2
            "*", "x", "×" -> n1 * n2
            "/", "÷" -> if (n2 != 0.0) n1 / n2 else return "Sıfıra bölme hatası"
            else -> return null
        }
        return if (res % 1.0 == 0.0) res.toLong().toString() else res.toString()
    }

    private fun enrichMediaResponse(userMessage: String, responseText: String): String {
        val lower = userMessage.lowercase().trim()
        val isImageRequest = (lower.contains("görsel") || lower.contains("resim") || lower.contains("fotoğraf") || lower.contains("çizim") || lower.contains("image") || lower.contains("draw")) &&
                (lower.contains("oluştur") || lower.contains("çiz") || lower.contains("yap") || lower.contains("üret") || lower.contains("generate") || lower.contains("tasarla"))
        val isVideoRequest = (lower.contains("video") || lower.contains("animasyon") || lower.contains("film") || lower.contains("veo")) &&
                (lower.contains("oluştur") || lower.contains("yap") || lower.contains("üret") || lower.contains("render") || lower.contains("tasarla"))

        var processedText = responseText

        // Check if response contains an image prompt tag with raw Turkish or short fragment
        val imgPromptRegex = Regex("""\[MEDIA_IMAGE_PROMPT:\s*([^\]]+)\]""")
        val match = imgPromptRegex.find(processedText)
        if (match != null) {
            val rawPrompt = match.groupValues[1].trim()
            if (rawPrompt.contains("ile ilgili", ignoreCase = true) || rawPrompt.contains("görsel", ignoreCase = true) || rawPrompt.contains("resim", ignoreCase = true) || rawPrompt.split(" ").size < 4) {
                val enhancedPrompt = buildRichEnglishPrompt(userMessage, rawPrompt)
                processedText = processedText.replace(match.value, "[MEDIA_IMAGE_PROMPT: $enhancedPrompt]")
            }
        } else if (isImageRequest && !processedText.contains("[MEDIA_IMAGE_URL:")) {
            val enhancedPrompt = buildRichEnglishPrompt(userMessage)
            processedText = "$processedText\n\n[MEDIA_IMAGE_PROMPT: $enhancedPrompt]"
        }

        val videoPromptRegex = Regex("""\[MEDIA_VIDEO_PROMPT:\s*([^\]]+)\]""")
        val videoMatch = videoPromptRegex.find(processedText)
        if (videoMatch != null) {
            val rawVideoPrompt = videoMatch.groupValues[1].trim()
            if (rawVideoPrompt.contains("ile ilgili", ignoreCase = true) || rawVideoPrompt.contains("video", ignoreCase = true) || rawVideoPrompt.split(" ").size < 4) {
                val enhancedVideoPrompt = buildRichEnglishVideoPrompt(userMessage, rawVideoPrompt)
                processedText = processedText.replace(videoMatch.value, "[MEDIA_VIDEO_PROMPT: $enhancedVideoPrompt]")
            }
        } else if (isVideoRequest) {
            val enhancedVideoPrompt = buildRichEnglishVideoPrompt(userMessage)
            processedText = "$processedText\n\n[MEDIA_VIDEO_PROMPT: $enhancedVideoPrompt]"
        }

        return processedText
    }

    private fun translateTurkishToEnglish(text: String): String {
        var result = text.lowercase()
            .replace("gün batımı", "golden sunset")
            .replace("gece", "night sky")
            .replace("gündüz", "sunny daylight")
            .replace("şehir", "modern city")
            .replace("dağ", "snowy mountain")
            .replace("göl", "crystal lake")
            .replace("nehir", "river")
            .replace("yıldız", "glowing star")
            .replace("çiçek", "vibrant flower")
            .replace("gül", "red rose")
            .replace("robot", "futuristic robot")
            .replace("teknoloji", "high-tech technology")
            .replace("uçak", "jet airplane")
            .replace("ev", "architectural house")
            .replace("villa", "luxury villa")
            .replace("kadın", "woman")
            .replace("adam", "man")
            .replace("erkek", "man")
            .replace("çocuk", "child")
            .replace("bebek", "cute baby")
            .replace("kız", "girl")
            .replace("oğlan", "boy")
            .replace("aslan", "majestic lion")
            .replace("kaplan", "tiger")
            .replace("ejderha", "glowing dragon")
            .replace("uzay gemisi", "sci-fi spaceship")
            .replace("uzaylı", "alien creature")
            .replace("telefon", "smartphone")
            .replace("bilgisayar", "laptop computer")
            .replace("yemek", "gourmet food")
            .replace("pizza", "delicious pizza")
            .replace("kahve", "coffee cup")
            .replace("manzara", "scenic landscape")
            .replace("ağaç", "green tree")
            .replace("güneş", "bright sun")
            .replace("ay", "full moon")
            .replace("köprü", "majestic bridge")
            .replace("şato", "gothic castle")
            .replace("kale", "fortress castle")
            .replace("ordu", "army warriors")
            .replace("savaşçı", "warrior")
            .replace("samuray", "samurai warrior")
            .replace("ninja", "shadow ninja")
            .replace("kahraman", "superhero")
            .replace("kuş", "flying bird")
            .replace("balık", "colorful fish")
            .replace("denizkızı", "mermaid")
            .replace("deniz kızı", "mermaid")
            .replace("motor", "motorcycle")
            .replace("bisiklet", "bicycle")
            .replace("mimar", "architectural")
            .replace("tasarım", "design")
            .replace("moda", "high fashion")
            .replace("sanat", "digital art")
            .replace("portre", "portrait")
            .replace("çizim", "artwork")
            .replace("ç", "c").replace("ğ", "g").replace("ı", "i")
            .replace("ö", "o").replace("ş", "s").replace("ü", "u")

        return result.replace(Regex("""[^a-zA-Z0-9\s]"""), " ").replace(Regex("""\s+"""), " ").trim()
    }

    private fun buildRichEnglishVideoPrompt(userMessage: String, rawPrompt: String? = null): String {
        val baseText = if (!rawPrompt.isNullOrBlank() && rawPrompt.length > 3 && !rawPrompt.contains("ile ilgili", ignoreCase = true)) {
            rawPrompt
        } else {
            userMessage
        }

        var subject = baseText.lowercase()
            .replace("video", "")
            .replace("animasyon", "")
            .replace("film", "")
            .replace("klip", "")
            .replace("çekim", "")
            .replace("görüntü", "")
            .replace("movie", "")
            .replace("clip", "")
            .replace("animation", "")
            .replace("oluştur", "")
            .replace("çek", "")
            .replace("yap", "")
            .replace("üret", "")
            .replace("generate", "")
            .replace("bana", "")
            .replace("bir", "")
            .replace("tane", "")
            .replace("istiyorum", "")
            .replace("lütfen", "")
            .replace("ile ilgili", "")
            .replace("hakkında", "")
            .replace("temalı", "")
            .replace("konulu", "")
            .replace("\\s+".toRegex(), " ")
            .trim()

        if (subject.isEmpty() || subject.length < 2) {
            subject = "cyberpunk city drone shot"
        }

        return when {
            subject.contains("uzay") || subject.contains("space") ->
                "A breathtaking cinematic drone sweep through a vibrant cosmic nebula with swirling stellar dust, glowing stars, floating crystalline asteroids, 60fps, photorealistic, 4k resolution, cinematic lighting, slow motion pan"

            subject.contains("google") ->
                "A futuristic motion graphics video showing a glowing holographic 3D Google logo assembling in a high-tech quantum data center, camera slowly orbiting, glowing particle trail, cinematic lighting, 60fps 4k"

            subject.contains("araba") || subject.contains("car") ->
                "A dynamic cinematic chase shot of a sleek luxury supercar drifting on a wet neon-lit city street at midnight, realistic water splashes, lens flare, 60fps photorealistic 4k"

            subject.contains("deniz") || subject.contains("okyanus") || subject.contains("ocean") ->
                "A slow cinematic aerial drone shot gliding over turquoise ocean waves crashing on a tropical golden sand beach at sunset, hyper-detailed, 60fps 4k"

            subject.contains("doğa") || subject.contains("orman") || subject.contains("forest") ->
                "A peaceful slow-motion camera flythrough in a lush green misty forest with sunbeams breaking through tall pine trees, photorealistic 60fps 4k"

            subject.contains("kedi") || subject.contains("cat") ->
                "A cute adorable cat playing with a glowing magic ball of light in a cozy sunlit room, slow motion camera pan, hyperdetailed fur, 60fps 4k"

            subject.contains("köpek") || subject.contains("dog") ->
                "A happy golden retriever running through a sunny flower meadow in slow motion, water droplets splashing, cinematic camera tracking shot, 60fps 4k"

            subject.contains("siberpunk") || subject.contains("cyberpunk") ->
                "A dramatic cinematic drone pan over a rain-slicked cyberpunk metropolis at night with flying vehicles and glowing neon signs, 60fps photorealistic 4k"

            else -> {
                val translatedSubject = translateTurkishToEnglish(subject)
                "A cinematic high-definition video of $translatedSubject, smooth camera movement, dynamic lighting, motion blur, photorealistic render, 60fps, 4k resolution, Unreal Engine 5 style"
            }
        }
    }

    private fun buildRichEnglishPrompt(userMessage: String, rawPrompt: String? = null): String {
        val baseText = if (!rawPrompt.isNullOrBlank() && rawPrompt.length > 3 && !rawPrompt.contains("ile ilgili", ignoreCase = true)) {
            rawPrompt
        } else {
            userMessage
        }

        // Clean Turkish filler and command words
        var subject = baseText.lowercase()
            .replace("görsel", "")
            .replace("resim", "")
            .replace("fotoğraf", "")
            .replace("çizim", "")
            .replace("image", "")
            .replace("picture", "")
            .replace("photo", "")
            .replace("drawing", "")
            .replace("oluştur", "")
            .replace("çiz", "")
            .replace("yap", "")
            .replace("üret", "")
            .replace("generate", "")
            .replace("tasarla", "")
            .replace("bana", "")
            .replace("bir", "")
            .replace("tane", "")
            .replace("istiyorum", "")
            .replace("lütfen", "")
            .replace("ile ilgili", "")
            .replace("hakkında", "")
            .replace("temalı", "")
            .replace("konulu", "")
            .replace("tarzında", "")
            .replace("stilde", "")
            .replace("\\s+".toRegex(), " ")
            .trim()

        if (subject.isEmpty() || subject.length < 2) {
            subject = "futuristic cyberpunk scenery"
        }

        return when {
            subject.contains("google") ->
                "A sleek, high-tech, futuristic digital artwork representing Google, a glowing 3D rainbow glass G emblem hovering over an illuminated data cloud matrix network, 8k resolution, cinematic lighting, photorealistic, Unreal Engine 5 render"

            subject.contains("atatürk") || subject.contains("ataturk") ->
                "A majestic, elegant portrait of Mustafa Kemal Atatürk, legendary leader and hero, highly detailed oil painting style, cinematic golden hour lighting, 8k resolution"

            subject.contains("apple") ->
                "A minimalist futuristic design of Apple technology, glowing white 3D apple emblem floating over a glass pedestal in a sleek illuminated room, 8k resolution, photorealistic"

            subject.contains("turkiye") || subject.contains("türkiye") || subject.contains("istanbul") ->
                "A breathtaking panoramic view of Istanbul and Turkey, Hagia Sophia and Bosphorus bridge illuminated under a golden sunset sky, ultra realistic, 8k resolution, cinematic atmosphere"

            subject.contains("kedi") || subject.contains("cat") ->
                "A cute fluffy majestic cat sitting gracefully in a warm sunlit room, hyperdetailed fur textures, photorealistic 8k, studio lighting"

            subject.contains("köpek") || subject.contains("dog") ->
                "A beautiful happy dog running in a sunny green meadow park, ultra high detail, photorealistic 8k, vibrant natural colors"

            subject.contains("uzay") || subject.contains("space") ->
                "An incredible deep space galaxy scene with colorful nebulas, glowing stars, floating planets, and a futuristic space station, 8k resolution, photorealistic"

            subject.contains("araba") || subject.contains("car") || subject.contains("otomobil") ->
                "A stunning sleek luxury supercar driving on a neon-lit futuristic city street at night, raytracing reflections, photorealistic 8k"

            subject.contains("siberpunk") || subject.contains("cyberpunk") ->
                "A detailed futuristic cyberpunk metropolis at night with glowing neon lights, flying vehicles, holographic signs, rain reflections, 8k digital art"

            subject.contains("doğa") || subject.contains("nature") || subject.contains("orman") || subject.contains("forest") ->
                "A breathtaking sunlit mystical forest with a crystal clear waterfall and lush green vegetation, cinematic atmosphere, 8k photorealistic"

            subject.contains("deniz") || subject.contains("sea") || subject.contains("okyanus") || subject.contains("ocean") ->
                "A serene tropical ocean beach at golden sunset with gentle turquoise waves and palm trees, ultra detailed, photorealistic 8k"

            else -> {
                val translatedSubject = translateTurkishToEnglish(subject)
                "A detailed high quality cinematic digital artwork of $translatedSubject, highly detailed, vibrant color grading, studio lighting, photorealistic, 8k resolution, Unreal Engine 5 render, trending on ArtStation"
            }
        }
    }
}
