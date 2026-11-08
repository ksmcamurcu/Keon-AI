package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "user", "ai", or "system"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val emotionTone: String = "" // "ENERGETIC", "EMPATHETIC_GENTLE", "POLITE_RESPECTFUL", "PLAYFUL_CHEERFUL", "CURIOSITY_THOUGHTFUL"
)
