package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoucherCoupon
import com.example.data.model.WalletAccount
import com.example.ui.theme.DompetBlueDark
import com.example.ui.theme.DompetBluePrimary
import com.example.ui.theme.DompetCyanAccent
import com.example.ui.theme.DompetGoldReward
import com.example.ui.theme.DompetGreenSuccess
import com.example.utils.DompetUtils

data class PresetMerchant(
    val name: String,
    val nmid: String,
    val amount: Long,
    val location: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrisScannerScreen(
    account: WalletAccount?,
    vouchers: List<VoucherCoupon>,
    onBack: () -> Unit,
    onPayQris: (merchantName: String, nmid: String, amount: Long, discount: Long) -> Unit
) {
    BackHandler { onBack() }

    var selectedMode by remember { mutableIntStateOf(0) } // 0: Pindai QR, 1: QRIS Saya
    var selectedMerchantName by remember { mutableStateOf("Kopi Kenangan Senopati") }
    var selectedNmid by remember { mutableStateOf("ID102008492019") }
    var amountText by remember { mutableStateOf("38000") }
    var selectedVoucher by remember { mutableStateOf<VoucherCoupon?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }

    val presetMerchants = listOf(
        PresetMerchant("Kopi Kenangan Senopati", "ID102008492019", 38000L, "Jakarta Selatan"),
        PresetMerchant("Indomaret Tebet Barat", "ID102009381720", 45000L, "Jakarta Selatan"),
        PresetMerchant("Chatime Grand Indonesia", "ID102001928374", 28000L, "Jakarta Pusat"),
        PresetMerchant("Warung Padang Sederhana", "ID102003847291", 42000L, "Jakarta Timur"),
        PresetMerchant("SPBU Pertamina 31", "ID102005829103", 100000L, "Jakarta Barat")
    )

    // Scanning laser animation
    val infiniteTransition = rememberInfiniteTransition(label = "qris_laser")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "QRIS Indonesia",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("qris_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .testTag("qris_screen_column"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode Selector Tabs (Pindai QRIS vs Tampilkan QRIS Saya)
            TabRow(
                selectedTabIndex = selectedMode,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedMode == 0,
                    onClick = { selectedMode = 0 },
                    text = { Text("Pindai QRIS", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
                    modifier = Modifier.testTag("tab_mode_scan")
                )
                Tab(
                    selected = selectedMode == 1,
                    onClick = { selectedMode = 1 },
                    text = { Text("QRIS Saya", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                    modifier = Modifier.testTag("tab_mode_my_qr")
                )
            }

            if (selectedMode == 0) {
                // SCANNER MODE
                Spacer(modifier = Modifier.height(16.dp))

                // Camera Scanner Viewport Simulation
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF10192A))
                        .border(3.dp, DompetBluePrimary, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Scanning laser canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val yPos = canvasHeight * laserY

                        // Corner targeting guides
                        val cornerLen = 32.dp.toPx()
                        val strokeW = 4.dp.toPx()
                        val pad = 16.dp.toPx()

                        // Top-left
                        drawLine(Color(0xFF00C2FF), Offset(pad, pad), Offset(pad + cornerLen, pad), strokeW)
                        drawLine(Color(0xFF00C2FF), Offset(pad, pad), Offset(pad, pad + cornerLen), strokeW)

                        // Top-right
                        drawLine(Color(0xFF00C2FF), Offset(canvasWidth - pad, pad), Offset(canvasWidth - pad - cornerLen, pad), strokeW)
                        drawLine(Color(0xFF00C2FF), Offset(canvasWidth - pad, pad), Offset(canvasWidth - pad, pad + cornerLen), strokeW)

                        // Bottom-left
                        drawLine(Color(0xFF00C2FF), Offset(pad, canvasHeight - pad), Offset(pad + cornerLen, canvasHeight - pad), strokeW)
                        drawLine(Color(0xFF00C2FF), Offset(pad, canvasHeight - pad), Offset(pad, canvasHeight - pad - cornerLen), strokeW)

                        // Bottom-right
                        drawLine(Color(0xFF00C2FF), Offset(canvasWidth - pad, canvasHeight - pad), Offset(canvasWidth - pad - cornerLen, canvasHeight - pad), strokeW)
                        drawLine(Color(0xFF00C2FF), Offset(canvasWidth - pad, canvasHeight - pad), Offset(canvasWidth - pad, canvasHeight - pad - cornerLen), strokeW)

                        // Red/Cyan laser line
                        drawLine(
                            Color(0xFFE53935),
                            Offset(pad + 10, yPos),
                            Offset(canvasWidth - pad - 10, yPos),
                            strokeWidth = 3.dp.toPx()
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Arahkan kamera ke kode QRIS",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Scanner bottom controls (Flash & Gallery)
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(
                            onClick = { isFlashOn = !isFlashOn },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isFlashOn) DompetGoldReward else Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Flash",
                                tint = if (isFlashOn) Color.Black else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { /* gallery mock */ },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Pilih dari Galeri",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Merchant Preset Selection
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Pilih Toko / Merchant QRIS Terdekat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        presetMerchants.forEach { merchant ->
                            val isSelected = selectedMerchantName == merchant.name
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        selectedMerchantName = merchant.name
                                        selectedNmid = merchant.nmid
                                        amountText = merchant.amount.toString()
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) DompetBluePrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, DompetBluePrimary) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Store,
                                            contentDescription = null,
                                            tint = if (isSelected) DompetBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Column {
                                            Text(
                                                text = merchant.name,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${merchant.location} • NMID: ${merchant.nmid.take(8)}...",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Text(
                                        text = DompetUtils.formatRupiah(merchant.amount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) DompetBluePrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input Nominal & Voucher
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Nominal Pembayaran (Rp)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("qris_amount_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Voucher Selector
                        val qrisVouchers = vouchers.filter { it.category == "QRIS" || it.category == "SEMUA" }
                        if (qrisVouchers.isNotEmpty()) {
                            Text(
                                text = "Gunakan Voucher Cashback",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            qrisVouchers.forEach { voucher ->
                                val isVoucherSelected = selectedVoucher?.id == voucher.id
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clickable {
                                            selectedVoucher = if (isVoucherSelected) null else voucher
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isVoucherSelected) DompetGreenSuccess.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    border = if (isVoucherSelected) androidx.compose.foundation.BorderStroke(1.dp, DompetGreenSuccess) else null
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🎟️ ${voucher.title}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isVoucherSelected) "✓ Terpasang" else "Pakai",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isVoucherSelected) DompetGreenSuccess else DompetBluePrimary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Total Calculation
                        val rawAmount = amountText.toLongOrNull() ?: 0L
                        val discount = if (selectedVoucher != null) {
                            if (selectedVoucher!!.discountPercentage > 0) {
                                (rawAmount * selectedVoucher!!.discountPercentage / 100).coerceAtMost(selectedVoucher!!.fixedDiscount)
                            } else {
                                selectedVoucher!!.fixedDiscount
                            }
                        } else 0L

                        val finalPay = (rawAmount - discount).coerceAtLeast(0L)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Bayar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                DompetUtils.formatRupiah(finalPay),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = DompetBluePrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (rawAmount > 0) {
                                    onPayQris(selectedMerchantName, selectedNmid, rawAmount, discount)
                                }
                            },
                            enabled = rawAmount > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("qris_pay_submit_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DompetBluePrimary)
                        ) {
                            Text(
                                text = "Bayar Sekarang dengan QRIS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            } else {
                // "TAMPILKAN QRIS SAYA" MODE
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .testTag("my_qris_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // QRIS ASPI Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE53935)
                            ) {
                                Text(
                                    text = "QRIS",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "STANDAR NASIONAL PEMBAYARAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = account?.userName ?: "Budi Pratama",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "NMID: ID102008899120 • DompetKu Personal",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Realistic QR Code Representation Canvas
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(14.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val s = size.width
                                val step = s / 7f

                                // Outer QR corner squares
                                drawRect(Color.Black, Offset(0f, 0f), Size(step * 2.2f, step * 2.2f))
                                drawRect(Color.White, Offset(step * 0.4f, step * 0.4f), Size(step * 1.4f, step * 1.4f))
                                drawRect(Color.Black, Offset(step * 0.7f, step * 0.7f), Size(step * 0.8f, step * 0.8f))

                                drawRect(Color.Black, Offset(s - step * 2.2f, 0f), Size(step * 2.2f, step * 2.2f))
                                drawRect(Color.White, Offset(s - step * 1.8f, step * 0.4f), Size(step * 1.4f, step * 1.4f))
                                drawRect(Color.Black, Offset(s - step * 1.5f, step * 0.7f), Size(step * 0.8f, step * 0.8f))

                                drawRect(Color.Black, Offset(0f, s - step * 2.2f), Size(step * 2.2f, step * 2.2f))
                                drawRect(Color.White, Offset(step * 0.4f, s - step * 1.8f), Size(step * 1.4f, step * 1.4f))
                                drawRect(Color.Black, Offset(step * 0.7f, s - step * 1.5f), Size(step * 0.8f, step * 0.8f))

                                // Decorative data patterns
                                drawRect(Color.Black, Offset(step * 3f, step * 1f), Size(step * 0.8f, step * 0.8f))
                                drawRect(Color.Black, Offset(step * 4f, step * 2.5f), Size(step * 0.8f, step * 0.8f))
                                drawRect(Color.Black, Offset(step * 2.5f, step * 4f), Size(step * 0.8f, step * 0.8f))
                                drawRect(Color.Black, Offset(step * 3.5f, step * 5.2f), Size(step * 0.8f, step * 0.8f))
                                drawRect(Color.Black, Offset(step * 5f, step * 4.2f), Size(step * 0.8f, step * 0.8f))

                                // Center badge
                                drawRoundRect(
                                    Color(0xFF0A68EB),
                                    topLeft = Offset(s * 0.35f, s * 0.35f),
                                    size = Size(s * 0.3f, s * 0.3f),
                                    cornerRadius = CornerRadius(10f, 10f)
                                )
                            }

                            // Center text on QR
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("DK", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Terima pembayaran dari seluruh dompet digital dan m-banking di Indonesia (GoPay, OVO, ShopeePay, BCA, Mandiri, BRI, dll.)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { /* share QR */ },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Bagikan QRIS Saya")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
