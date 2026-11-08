package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RobotCaptchaCard(
    isVerified: Boolean,
    onVerifyChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Ben robot değilim"
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isVerifying by remember { mutableStateOf(false) }
    var showPuzzleDialog by remember { mutableStateOf(false) }

    // Loading rotation animation for the captcha check
    val infiniteTransition = rememberInfiniteTransition(label = "captcha_trans")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .pulsatingNeonGlow(
                glowColor = if (isVerified) NeonGreen else NeonCyan.copy(alpha = 0.4f),
                minAlpha = if (isVerified) 0.3f else 0.15f,
                maxAlpha = if (isVerified) 0.8f else 0.5f,
                glowRadius = if (isVerified) 12.dp else 8.dp,
                shape = RoundedCornerShape(12.dp),
                borderWidth = 1.5.dp
            )
            .clickable(enabled = !isVerifying) {
                if (isVerified) {
                    onVerifyChanged(false)
                } else {
                    // Start simulated reCAPTCHA security verification scan
                    coroutineScope.launch {
                        isVerifying = true
                        delay(900)
                        isVerifying = false
                        onVerifyChanged(true)
                        android.widget.Toast
                            .makeText(context, "✅ Robot doğrulaması (reCAPTCHA) başarılı!", android.widget.Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161A2B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Checkbox Box with Animated States
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                color = when {
                                    isVerified -> NeonGreen
                                    isVerifying -> DarkSurface
                                    else -> Color(0xFF0D111E)
                                }
                            )
                            .border(
                                width = 2.dp,
                                color = when {
                                    isVerified -> NeonGreen
                                    isVerifying -> NeonCyan
                                    else -> Color.Gray
                                },
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isVerifying -> {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Taranıyor",
                                    tint = NeonCyan,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .rotate(rotation)
                                )
                            }
                            isVerified -> {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Robot Değilim Onaylı",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Column {
                        Text(
                            text = if (isVerifying) "Doğrulanıyor..." else title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isVerified) "✅ İnsan kullanıcı doğrulandı" else "Kutuya tıklayarak robot olmadığınızı doğrulayın",
                            fontSize = 10.sp,
                            color = if (isVerified) NeonGreen else TextSecondary
                        )
                    }
                }

                // reCAPTCHA Logo & Brand
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "reCAPTCHA",
                            tint = if (isVerified) NeonGreen else NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "reCAPTCHA",
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "Keon Quantum Guard v3",
                        fontSize = 8.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Görsel Test İle Doğrula 🔍",
                        fontSize = 8.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable { showPuzzleDialog = true }
                    )
                }
            }
        }
    }

    // VISUAL PUZZLE CAPTCHA DIALOG (Görsel Robot Testi)
    if (showPuzzleDialog) {
        CaptchaPuzzleDialog(
            onDismiss = { showPuzzleDialog = false },
            onSuccess = {
                onVerifyChanged(true)
                showPuzzleDialog = false
                android.widget.Toast
                    .makeText(context, "🎉 Görsel test başarıyla tamamlandı!", android.widget.Toast.LENGTH_SHORT)
                    .show()
            }
        )
    }
}

@Composable
fun CaptchaPuzzleDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    // 9-grid captcha tiles (3 correct tiles containing AI Memory icons)
    val correctIndices = remember { setOf(1, 4, 7) }
    val selectedIndices = remember { mutableStateListOf<Int>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF121626),
        shape = RoundedCornerShape(16.dp),
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keon AI Robot Doğrulama Testi",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "İçinde Yapay Zeka Çipi (🧠) bulunan 3 kareyi seçip 'Doğrula' butonuna basın:",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 3x3 Grid of Captcha tiles
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (row in 0 until 3) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                val isSelected = selectedIndices.contains(index)
                                val isChipTile = correctIndices.contains(index)

                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) NeonCyan.copy(alpha = 0.3f) else DarkSurface
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) NeonCyan else Color.Gray.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            if (isSelected) {
                                                selectedIndices.remove(index)
                                            } else {
                                                selectedIndices.add(index)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        if (isChipTile) {
                                            Icon(
                                                imageVector = Icons.Default.Memory,
                                                contentDescription = "Keon Chip",
                                                tint = NeonCyan,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Text("Keon Çip", fontSize = 8.sp, color = NeonCyan)
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = "Icon",
                                                tint = Color.Gray.copy(alpha = 0.6f),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Text("Resim $index", fontSize = 8.sp, color = Color.Gray)
                                        }
                                    }

                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(NeonCyan),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        fontSize = 11.sp,
                        color = Color(0xFFFF3366),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedIndices.toSet() == correctIndices) {
                        onSuccess()
                    } else {
                        errorMessage = "❌ Yanlış kareler seçildi! Lütfen çip içeren 3 kareyi doğru seçin."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("DOĞRULA", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat", color = Color.Gray)
            }
        }
    )
}
