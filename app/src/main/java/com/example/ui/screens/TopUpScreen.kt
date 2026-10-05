package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WalletAccount
import com.example.ui.theme.DompetBluePrimary
import com.example.ui.theme.DompetGreenSuccess
import com.example.utils.DompetUtils

data class TopUpMethod(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val feeText: String = "Bebas Biaya"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopUpScreen(
    account: WalletAccount?,
    onBack: () -> Unit,
    onConfirmTopUp: (amount: Long, method: String) -> Unit
) {
    BackHandler { onBack() }

    val methods = listOf(
        TopUpMethod("BCA", "BCA OneKlik / Virtual Account", "Verifikasi instan otomatis 24 jam", Icons.Default.AccountBalance),
        TopUpMethod("MANDIRI", "Mandiri Livin' VA", "Transfer via aplikasi Livin' by Mandiri", Icons.Default.AccountBalance),
        TopUpMethod("BRI", "BRI Virtual Account (BRIVA)", "Transfer via BRImo atau ATM BRI", Icons.Default.AccountBalance),
        TopUpMethod("BNI", "BNI Virtual Account", "Transfer via BNI Mobile Banking", Icons.Default.AccountBalance),
        TopUpMethod("ALFAMART", "Alfamart & Indomaret", "Tunjukkan barcode di kasir terdekat", Icons.Default.Storefront),
        TopUpMethod("DEBIT", "Kartu Debit Instan", "Visa, Mastercard, GPN terdaftar", Icons.Default.CreditCard)
    )

    var selectedMethod by remember { mutableStateOf(methods[0]) }
    var amountText by remember { mutableStateOf("100000") }

    val quickAmounts = listOf(20000L, 50000L, 100000L, 200000L, 500000L, 1000000L)
    val parsedAmount = amountText.toLongOrNull() ?: 0L
    val currentBalance = account?.balance ?: 0L
    val newEstimatedBalance = currentBalance + parsedAmount

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Isi Saldo DompetKu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("topup_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .testTag("topup_screen_column")
        ) {
            // Saldo saat ini banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                color = DompetBluePrimary.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Saldo DompetKu Saat Ini",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = DompetUtils.formatRupiah(currentBalance),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = DompetBluePrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DompetGreenSuccess.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Bebas Biaya Admin",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DompetGreenSuccess,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Pilih Nominal
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Pilih / Masukkan Nominal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("topup_amount_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Grid 3x2 Quick Amounts
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (row in 0..1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (col in 0..2) {
                                val amt = quickAmounts[row * 3 + col]
                                val isSelected = parsedAmount == amt
                                OutlinedButton(
                                    onClick = { amountText = amt.toString() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) DompetBluePrimary.copy(alpha = 0.1f) else Color.Transparent
                                    ),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, DompetBluePrimary) else null
                                ) {
                                    Text(
                                        text = DompetUtils.formatRupiah(amt).replace("Rp ", ""),
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) DompetBluePrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pilih Metode
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Pilih Metode Pembayaran",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                methods.forEach { method ->
                    val isSelected = selectedMethod.id == method.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedMethod = method }
                            .testTag("method_${method.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) DompetBluePrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, DompetBluePrimary) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = method.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) DompetBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = method.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = method.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Dipilih",
                                    tint = DompetBluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ringkasan Pembayaran
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ringkasan Isi Saldo", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Jumlah Isi Saldo", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(DompetUtils.formatRupiah(parsedAmount), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Biaya Transaksi", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("GRATIS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DompetGreenSuccess)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estimasi Saldo Baru", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(DompetUtils.formatRupiah(newEstimatedBalance), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = DompetBluePrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    if (parsedAmount >= 10000L) {
                        onConfirmTopUp(parsedAmount, selectedMethod.name)
                    }
                },
                enabled = parsedAmount >= 10000L,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 20.dp)
                    .testTag("submit_topup_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DompetBluePrimary)
            ) {
                Text(
                    text = "Konfirmasi & Isi Saldo Sekarang",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
