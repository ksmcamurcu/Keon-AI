package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.KeonChatbotSystem
import kotlinx.coroutines.launch

@Composable
fun KeonChatbotComponent(modifier: Modifier = Modifier) {
    var message by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("Merhaba! Sana nasıl yardımcı olabilirim?") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF6200EE), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Keon AI", fontSize = 16.sp, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(response, fontSize = 14.sp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Keon'a bir şey sor...") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    response = KeonChatbotSystem.sendMessage(message)
                    isLoading = false
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White
            )
            else Text("Gönder")
        }
    }
}
