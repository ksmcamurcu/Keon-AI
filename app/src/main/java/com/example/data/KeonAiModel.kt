package com.example.data

enum class KeonAiModel(
    val id: String,
    val displayName: String,
    val modelEndpoint: String,
    val badgeText: String,
    val description: String,
    val icon: String,
    val avgLatencyMs: Int,
    val tokensPerSec: Int,
    val reasoningScore: Int,
    val speedScore: Int
) {
    PRO(
        id = "pro",
        displayName = "Keon AI PRO",
        modelEndpoint = "gemini-3.1-pro-preview",
        badgeText = "HIGH THINKING",
        description = "Derin Mantık & Karmaşık Analiz Engine",
        icon = "🔥",
        avgLatencyMs = 1250,
        tokensPerSec = 48,
        reasoningScore = 98,
        speedScore = 65
    ),
    LITE(
        id = "lite",
        displayName = "Keon AI Lite",
        modelEndpoint = "gemini-3.1-flash-lite-preview",
        badgeText = "LOW LATENCY",
        description = "Işık Hızında Yanıt & Düşük Gecikme Engine",
        icon = "🚀",
        avgLatencyMs = 280,
        tokensPerSec = 195,
        reasoningScore = 72,
        speedScore = 99
    )
}
