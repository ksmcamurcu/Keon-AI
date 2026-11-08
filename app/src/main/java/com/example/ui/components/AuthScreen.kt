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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    onSkip: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isCaptchaVerified by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0A1C))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Keon AI - Giriş Yap",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = NeonCyan,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        RobotCaptchaCard(
            isVerified = isCaptchaVerified,
            onVerifyChanged = { isCaptchaVerified = it },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    onAuthError("E-posta veya şifre boş olamaz")
                    return@Button
                }
                if (!isCaptchaVerified) {
                    onAuthError("Lütfen robot olmadığınızı doğrulayın")
                    return@Button
                }
                isLoading = true
                com.google.firebase.auth.FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            onAuthSuccess()
                        } else {
                            onAuthError(task.exception?.message ?: "Giriş başarısız")
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && isCaptchaVerified,
            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Giriş Yap", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    onAuthError("E-posta veya şifre boş olamaz")
                    return@Button
                }
                if (!isCaptchaVerified) {
                    onAuthError("Lütfen robot olmadığınızı doğrulayın")
                    return@Button
                }
                isLoading = true
                com.google.firebase.auth.FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            onAuthSuccess()
                        } else {
                            onAuthError(task.exception?.message ?: "Kayıt başarısız")
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && isCaptchaVerified,
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
        ) {
            Text("Kayıt Ol", color = Color.White)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { onSkip() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Atla (Misafir)", color = Color.Gray)
        }
    }
}
