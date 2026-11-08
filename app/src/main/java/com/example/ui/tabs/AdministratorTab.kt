package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KeonUserAccount
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.KeonPackage
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Send
import com.example.ui.components.RobotCaptchaCard
import com.example.ui.components.pulsatingNeonGlow

@Composable
fun AdministratorTab(
    isAdmin: Boolean,
    adminPhoneNumber: String?,
    currentUserAccount: KeonUserAccount?,
    registeredUsersList: List<KeonUserAccount> = emptyList(),
    isVipApprovedByAdmin: Boolean,
    activePackage: KeonPackage?,
    allocatedGb: Int,
    onToggleVipApprovalByAdmin: (Boolean) -> Unit,
    onSelectPackage: (KeonPackage) -> Unit,
    onGrantVipToUser: (userId: String, pkg: KeonPackage) -> Unit = { _, _ -> },
    onRevokeVipFromUser: (userId: String) -> Unit = {},
    onResolveSupportTicket: (userId: String, note: String) -> Unit = { _, _ -> },
    onAddUserByAdmin: (name: String, contact: String, isEmail: Boolean, pkg: KeonPackage?) -> Unit = { _, _, _, _ -> },
    onDeleteUserByAdmin: (userId: String) -> Unit = {},
    onApproveVipRequest: (userId: String) -> Unit = {},
    onRejectVipRequest: (userId: String) -> Unit = {},
    onKickUserSession: (userId: String) -> Unit = {},
    onTerminateAllOtherSessions: () -> Unit = {},
    onSendNotificationToUserSession: (userId: String, message: String) -> Unit = { _, _ -> },
    onSetAllocatedGb: (Int) -> Unit,
    onRevokeAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableStateOf("all") } // "all", "active", "vip", "support"

    var userToAssignVip by remember { mutableStateOf<KeonUserAccount?>(null) }
    var userToSendMsg by remember { mutableStateOf<KeonUserAccount?>(null) }
    var messageToSendText by remember { mutableStateOf("") }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showTubitakDefenseSection by remember { mutableStateOf(false) }

    // If TÜBİTAK mode is toggled, render the dedicated TÜBİTAK Autonomous Defense Center
    if (showTubitakDefenseSection) {
        TubitakDefenseSection(
            isAdmin = isAdmin,
            adminPhoneNumber = adminPhoneNumber,
            onBack = { showTubitakDefenseSection = false },
            modifier = modifier
        )
        return
    }

    // New user form state
    var newUserName by remember { mutableStateOf("") }
    var newUserContact by remember { mutableStateOf("") }
    var newUserIsEmail by remember { mutableStateOf(true) }
    var newUserVipPackage by remember { mutableStateOf<KeonPackage?>(KeonPackage.AURA) }
    var isAdminNotRobotChecked by remember { mutableStateOf(false) }

    // Active session accounts
    val activeSessions = remember(registeredUsersList) {
        registeredUsersList.filter { it.isLoggedIn }
    }

    // Filter users list
    val filteredUsers = remember(registeredUsersList, searchQuery, selectedFilterTab) {
        registeredUsersList.filter { user ->
            val matchesSearch = user.name.contains(searchQuery, ignoreCase = true) ||
                    user.contact.contains(searchQuery, ignoreCase = true) ||
                    user.deviceModel.contains(searchQuery, ignoreCase = true) ||
                    user.ipAddress.contains(searchQuery, ignoreCase = true)
            val matchesTab = when (selectedFilterTab) {
                "active" -> user.isLoggedIn
                "vip" -> user.isVipApproved || user.vipPackage != null && user.vipPackage != KeonPackage.FREE
                "support" -> user.supportTicket != null || user.accountStatus.contains("Destek")
                else -> true
            }
            matchesSearch && matchesTab
        }
    }

    val supportRequests = remember(registeredUsersList) {
        registeredUsersList.filter { it.supportTicket != null || it.accountStatus.contains("Destek") }
    }

    val pendingVipRequests = remember(registeredUsersList) {
        registeredUsersList.filter { it.vipRequestStatus == "PENDING" || (it.requestedVipPackage != null && !it.isVipApproved && it.vipRequestStatus != "REJECTED") }
    }

    val totalVipCount = remember(registeredUsersList) {
        registeredUsersList.count { it.isVipApproved || (it.vipPackage != null && it.vipPackage != KeonPackage.FREE) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF07040F))
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF0055))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF2B0014),
                                Color(0xFF140026),
                                Color(0xFF001A26)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color(0xFFFF0055),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "👑 EDMINASTÖR PANELİ",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Yönetici • Kayıt Yardımı • Hesap & VIP Yönetimi",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFF88AA)
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { onRevokeAdmin() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF3366)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF3366)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Çıkış",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Çıkış", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Quick Stats Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF150A21), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${registeredUsersList.size}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            Text("Kayıtlı", fontSize = 10.sp, color = TextSecondary)
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.Gray.copy(alpha = 0.3f)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${activeSessions.size}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                            Text("Açık Oturum", fontSize = 10.sp, color = TextSecondary)
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.Gray.copy(alpha = 0.3f)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$totalVipCount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeonMagenta)
                            Text("VIP Üye", fontSize = 10.sp, color = TextSecondary)
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.Gray.copy(alpha = 0.3f)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${pendingVipRequests.size}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF0055))
                            Text("VIP Talebi", fontSize = 10.sp, color = TextSecondary)
                        }
                    }

                    // TÜBİTAK Özel Savunma & Otonom Sistemler Bölmesi Giriş Butonu
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTubitakDefenseSection = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF04182E)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NeonCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🇹🇷", fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "TÜBİTAK SAVUNMA & ROBOTİK MERKEZİ",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "✈️ İHA/Uçak • 🐕 Robot Köpek • 🤖 İnsansı Robot • 🛸 Dron (.KDF)",
                                        fontSize = 10.sp,
                                        color = NeonCyan
                                    )
                                }
                            }

                            Button(
                                onClick = { showTubitakDefenseSection = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("AÇ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        // 1.2 REAL-TIME ACTIVE SESSIONS & OPEN ACCOUNTS (CİHAZLAR & AÇIK OTURUMLAR)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonGreen)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🌐 CANLI AÇIK HESAPLAR & OTURUMLAR",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(NeonGreen.copy(alpha = 0.2f))
                            .border(1.dp, NeonGreen, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🟢 ${activeSessions.size} CİHAZ AÇIK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGreen
                        )
                    }
                }

                Text(
                    text = "Aşağıda Keon AI sisteminde farklı telefonlarda veya cihazlarda oturumu gerçekten açık olan hesaplar ve canlı cihaz bilgileri listelenmektedir. Administrator olarak oturumları anında sonlandırabilir veya uyarı gönderebilirsiniz:",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                if (activeSessions.size > 1) {
                    OutlinedButton(
                        onClick = { onTerminateAllOtherSessions() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF3366)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF3366)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🚨 Tüm Harici Cihaz Oturumlarını Kapat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (activeSessions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F1A15), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚠️ Şu anda sistemde açık olan aktif cihaz oturumu bulunmuyor.",
                            fontSize = 11.sp,
                            color = Color.Yellow
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        activeSessions.forEach { user ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0C131A), RoundedCornerShape(12.dp))
                                    .border(1.2.dp, NeonGreen, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.PhoneAndroid,
                                                contentDescription = null,
                                                tint = NeonGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = user.name,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "📱 ${user.deviceModel} (${user.deviceType})",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = NeonCyan
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF0A2B1D))
                                                .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "🟢 CANLI AKTİF",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NeonGreen
                                            )
                                        }
                                    }

                                    // Session details row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF060D12), RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("📍 Konum: ${user.location}", fontSize = 10.sp, color = TextSecondary)
                                            Text("🌐 IP: ${user.ipAddress}", fontSize = 10.sp, color = TextSecondary)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("🔑 Session: ${user.sessionToken}", fontSize = 10.sp, color = NeonCyan)
                                            Text("🕒 ${user.lastActiveTime}", fontSize = 10.sp, color = NeonGreen)
                                        }
                                    }

                                    // Action buttons for session
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = { onKickUserSession(user.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0055)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(imageVector = Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Oturumu Kapat", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                userToSendMsg = user
                                                messageToSendText = ""
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Mesaj Gönder", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1.5 VIP NOTIFICATIONS FOR ADMIN (BİLDİRİMLER: VIP Olmak İsteyen Kullanıcılar)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (pendingVipRequests.isNotEmpty()) Color(0xFFFF0055) else NeonGreen)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = if (pendingVipRequests.isNotEmpty()) Color(0xFFFF0055) else NeonGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔔 VIP Olmak İsteyenler (Bildirim)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (pendingVipRequests.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFF0055), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${pendingVipRequests.size} VIP TALEBİ",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                Text(
                    text = "Aşağıda VIP paket kullanmak isteyen kullanıcıların bildirimleri yer almaktadır. Reddet veya Onayla butonları ile karar verebilirsiniz:",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                if (pendingVipRequests.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F1A15), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✅ Bekleyen VIP paket talebi veya bildirimi bulunmuyor.",
                            fontSize = 11.sp,
                            color = NeonGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        pendingVipRequests.forEach { user ->
                            val reqPkg = user.requestedVipPackage ?: KeonPackage.AURA
                            val context = androidx.compose.ui.platform.LocalContext.current

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF191224)),
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, reqPkg.primaryColor)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "👤 ${user.name}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(${user.contact})",
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                        }

                                        Text(
                                            text = user.registeredAt,
                                            fontSize = 9.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(reqPkg.primaryColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                            .border(1.dp, reqPkg.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "👑 İstenen VIP Paket: ${reqPkg.title} (${reqPkg.badge})",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = reqPkg.primaryColor
                                            )
                                            Text(
                                                text = reqPkg.price,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Text(
                                        text = "📲 Bu kullanıcı VIP yetkisi talep etti. Kararınızı veriniz:",
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )

                                    // 2 BUTTONS: REDDET ve ONAYLA (Underneath Notification)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // REDDET BUTTON
                                        Button(
                                            onClick = {
                                                onRejectVipRequest(user.id)
                                                android.widget.Toast.makeText(
                                                    context,
                                                    "❌ ${user.name} kullanıcısının VIP talebi REDDEDİLDİ. Kullanıcının telefonuna bildirim gönderildi.",
                                                    android.widget.Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Reddet",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "REDDET",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }

                                        // ONAYLA BUTTON
                                        Button(
                                            onClick = {
                                                onApproveVipRequest(user.id)
                                                android.widget.Toast.makeText(
                                                    context,
                                                    "🎉 ${user.name} kullanıcısının VIP talebi ONAYLANDI!",
                                                    android.widget.Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Onayla",
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "ONAYLA",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. REGISTRATION SUPPORT & HELP SECTION (Kaydolmaya Çalışanlara Yardım Etme)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Yellow)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🆘 Kaydolmaya Çalışanlar & Destek",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { showAddUserDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Manuel Kayıt Et", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "Kayıt olurken doğrulama kodu veya şifre sorunu yaşayan kullanıcılara yardım edin, hesaplarını anında aktifleştirin:",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                if (supportRequests.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF100B1A), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✅ Bekleyen kaydolma destek talebi bulunmuyor.",
                            fontSize = 11.sp,
                            color = NeonGreen
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        supportRequests.forEach { user ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1A1208), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.Yellow.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "👤 ${user.name}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = user.registeredAt,
                                            fontSize = 10.sp,
                                            color = Color.Yellow
                                        )
                                    }

                                    Text(
                                        text = "İletişim: ${user.contact}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )

                                    if (!user.supportTicket.isNullOrEmpty()) {
                                        Text(
                                            text = user.supportTicket,
                                            fontSize = 11.sp,
                                            color = Color(0xFFFFCC00),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = { onResolveSupportTicket(user.id, "Admin tarafından hesap doğrulanıp aktifleştirildi.") },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("🚀 Kaydını Onayla", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                onResolveSupportTicket(user.id, "VIP Paketi tanımlandı ve kaydı tamamlandı.")
                                                onGrantVipToUser(user.id, KeonPackage.AURA)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("👑 Onayla + VIP Ver", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. REGISTERED USERS DIRECTORY (Kaydolan Kişilerin Hesabını Görebilme & VIP Vermek)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "👥 Kaydolan Kullanıcılar & VIP Yönetimi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "Sisteme kaydolan tüm kişilerin hesaplarını inceleyin, VIP paket ve lisans durumlarını yönetin:",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Kullanıcı adı veya iletişim bilgisi ara...", fontSize = 11.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedContainerColor = Color(0xFF0C081A),
                        unfocusedContainerColor = Color(0xFF0C081A)
                    )
                )

                // Filter Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "all" to "Tümü (${registeredUsersList.size})",
                        "active" to "🟢 Açık Oturum (${activeSessions.size})",
                        "vip" to "VIP Üyeler ($totalVipCount)",
                        "support" to "Destek (${supportRequests.size})"
                    ).forEach { (key, label) ->
                        val isSelected = selectedFilterTab == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonCyan else Color(0xFF160F2A))
                                .clickable { selectedFilterTab = key }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }

                // Users List Cards
                if (filteredUsers.isEmpty()) {
                    Text(
                        text = "Aramaya uygun kullanıcı bulunamadı.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        filteredUsers.forEach { user ->
                            val isVip = user.isVipApproved || (user.vipPackage != null && user.vipPackage != KeonPackage.FREE)
                            val userPkg = user.vipPackage ?: if (user.isVipApproved) KeonPackage.AURA else KeonPackage.FREE

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F0B1E), RoundedCornerShape(12.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (isVip) NeonMagenta else NeonCyan.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isVip) NeonMagenta else NeonCyan),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = user.name.take(1).uppercase(),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column {
                                                Text(
                                                    text = user.name,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = user.contact,
                                                    fontSize = 11.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                        }

                                        // Status Badge
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isVip) Color(0xFF3B0024) else Color(0xFF002233))
                                                .border(1.dp, if (isVip) NeonMagenta else NeonCyan, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (isVip) "👑 VIP: ${userPkg.title}" else "Standart Üye",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isVip) NeonMagenta else NeonCyan
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📱 ${user.deviceModel} (${user.registeredAt})",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )

                                        Text(
                                            text = "Durum: ${user.accountStatus}",
                                            fontSize = 10.sp,
                                            color = if (isVip) NeonGreen else TextSecondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    // Action Buttons Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = { userToAssignVip = user },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("VIP Ver / Değiştir", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }

                                        if (isVip) {
                                            OutlinedButton(
                                                onClick = { onRevokeVipFromUser(user.id) },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF3366)),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF3366)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("VIP İptal", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        IconButton(
                                            onClick = { onDeleteUserByAdmin(user.id) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Sil",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. GLOBAL VIP LICENSE CONTROL SWITCH
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🌐 Genel VIP İzin Anahtarı",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isVipApprovedByAdmin) "✅ Genel VIP İzinleri Açık" else "🔒 Genel VIP İzinleri Beklemede",
                            fontSize = 11.sp,
                            color = if (isVipApprovedByAdmin) NeonGreen else Color.Yellow,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Switch(
                        checked = isVipApprovedByAdmin,
                        onCheckedChange = { onToggleVipApprovalByAdmin(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = NeonGreen
                        )
                    )
                }
            }
        }

        // 5. STORAGE & SYSTEM CONTROL
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "☁️ Bulut Depolama Limiti Yönetimi (${allocatedGb} GB)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Slider(
                    value = allocatedGb.toFloat(),
                    onValueChange = { onSetAllocatedGb(it.toInt()) },
                    valueRange = 10f..100f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFFD700),
                        activeTrackColor = Color(0xFFFFD700)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("10 GB", fontSize = 10.sp, color = TextSecondary)
                    Text("60 GB (Varsayılan)", fontSize = 10.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                    Text("100 GB", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
    }

    // DIALOG: Assign VIP Package to User
    userToAssignVip?.let { user ->
        AlertDialog(
            onDismissRequest = { userToAssignVip = null },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = NeonMagenta)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("VIP Paket Ataması", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("👤 ${user.name} (${user.contact}) için tanımlamak istediğiniz VIP paketini seçiniz:", fontSize = 12.sp, color = TextSecondary)

                    listOf(
                        KeonPackage.FREE,
                        KeonPackage.ULTIMATUM,
                        KeonPackage.AURA,
                        KeonPackage.ZUMRUDU_ANKA
                    ).forEach { pkg ->
                        Button(
                            onClick = {
                                if (pkg == KeonPackage.FREE) {
                                    onRevokeVipFromUser(user.id)
                                } else {
                                    onGrantVipToUser(user.id, pkg)
                                }
                                userToAssignVip = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = pkg.primaryColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(pkg.title, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(pkg.price, color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { userToAssignVip = null }) {
                    Text("Vazgeç", color = Color.Gray)
                }
            }
        )
    }

    // DIALOG: Add / Register User Manually by Admin
    if (showAddUserDialog) {
        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kullanıcı Kaydı & VIP Tanımla", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Kaydolmakta zorluk çeken kişilere Administrator eliyle anında hesap oluşturun:", fontSize = 11.sp, color = TextSecondary)

                    OutlinedTextField(
                        value = newUserName,
                        onValueChange = { newUserName = it },
                        label = { Text("Kullanıcı Adı Soyadı", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = newUserContact,
                        onValueChange = { newUserContact = it },
                        label = { Text("E-Posta veya Telefon", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (newUserIsEmail) KeyboardType.Email else KeyboardType.Phone
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("E-posta ile mi?", fontSize = 11.sp, color = TextPrimary)
                        Switch(
                            checked = newUserIsEmail,
                            onCheckedChange = { newUserIsEmail = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonCyan)
                        )
                    }

                    Text("Tanımlanacak VIP Paket:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonMagenta)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(KeonPackage.ULTIMATUM, KeonPackage.AURA, KeonPackage.ZUMRUDU_ANKA).forEach { pkg ->
                            val isSelected = newUserVipPackage == pkg
                            Button(
                                onClick = { newUserVipPackage = pkg },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) pkg.primaryColor else Color(0xFF1B1028)
                                ),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = pkg.title.split(" ").firstOrNull() ?: pkg.title,
                                    fontSize = 9.sp,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    RobotCaptchaCard(
                        isVerified = isAdminNotRobotChecked,
                        onVerifyChanged = { isAdminNotRobotChecked = it },
                        title = "Hesap Açma Robot Doğrulaması"
                    )
                }
            },
            confirmButton = {
                val context = androidx.compose.ui.platform.LocalContext.current
                Button(
                    onClick = {
                        if (!isAdminNotRobotChecked) {
                            android.widget.Toast.makeText(
                                context,
                                "❌ Lütfen hesap oluşturmak için 'Ben robot değilim' kutusunu işaretleyin.",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        if (newUserName.isNotBlank() && newUserContact.isNotBlank()) {
                            onAddUserByAdmin(newUserName, newUserContact, newUserIsEmail, newUserVipPackage)
                            newUserName = ""
                            newUserContact = ""
                            isAdminNotRobotChecked = false
                            showAddUserDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Text("Hesabı Oluştur & Aktifleştir", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUserDialog = false }) {
                    Text("İptal", color = Color.Gray)
                }
            }
        )
    }

    // DIALOG: Send Admin Message to User Session
    if (userToSendMsg != null) {
        val u = userToSendMsg!!
        AlertDialog(
            onDismissRequest = { userToSendMsg = null },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Anlık Cihaz Bildirimi Gönder", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "📱 Cihaz: ${u.deviceModel} (${u.contact}) hesabı açık olan kullanıcıya ekranda görünecek özel yönetici mesajı gönderin:",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = messageToSendText,
                        onValueChange = { messageToSendText = it },
                        placeholder = { Text("Örn: Lütfen hesabınızı onaylayın veya güvenlik uyarısı...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (messageToSendText.isNotBlank()) {
                            onSendNotificationToUserSession(u.id, messageToSendText)
                            messageToSendText = ""
                            userToSendMsg = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Gönder", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToSendMsg = null }) {
                    Text("İptal", color = Color.Gray)
                }
            }
        )
    }
}
