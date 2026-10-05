package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoucherCoupon
import com.example.data.model.WalletAccount
import com.example.ui.theme.DompetBluePrimary
import com.example.ui.theme.DompetGoldReward
import com.example.ui.theme.DompetGreenSuccess
import com.example.utils.DompetUtils

data class BillPackage(
    val title: String,
    val description: String,
    val price: Long,
    val adminFee: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillPaymentScreen(
    initialCategory: String = "BILL_PULSA",
    account: WalletAccount?,
    vouchers: List<VoucherCoupon>,
    onBack: () -> Unit,
    onPayBill: (category: String, title: String, targetNumber: String, amount: Long, adminFee: Long, discount: Long, notes: String) -> Unit
) {
    BackHandler { onBack() }

    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var targetInput by remember { mutableStateOf(if (initialCategory == "BILL_PULSA") "0812-8899-7721" else "") }
    var selectedPackageIndex by remember { mutableIntStateOf(0) }
    var selectedVoucher by remember { mutableStateOf<VoucherCoupon?>(null) }

    val categories = listOf(
        Triple("BILL_PULSA", "Pulsa & Data", Icons.Default.PhoneAndroid),
        Triple("BILL_PLN", "Listrik PLN", Icons.Default.Bolt),
        Triple("BILL_BPJS", "BPJS Kesehatan", Icons.Default.MedicalServices),
        Triple("BILL_PDAM", "Air PDAM", Icons.Default.WaterDrop)
    )

    val pulsaPackages = listOf(
        BillPackage("Pulsa 25.000", "Masa aktif 30 hari", 26000L),
        BillPackage("Pulsa 50.000", "Masa aktif 45 hari", 51000L),
        BillPackage("Pulsa 100.000", "Masa aktif 60 hari", 100000L),
        BillPackage("Paket Data 15 GB", "30 Hari • Semua Jaringan 4G/5G", 55000L),
        BillPackage("Paket Data 35 GB", "30 Hari • Internet Unlimited Malam", 85000L)
    )

    val plnPackages = listOf(
        BillPackage("Token Listrik Rp 20.000", "Estimasi daya ~14.8 kWh", 22500L, 2500L),
        BillPackage("Token Listrik Rp 50.000", "Estimasi daya ~37.1 kWh", 52500L, 2500L),
        BillPackage("Token Listrik Rp 100.000", "Estimasi daya ~74.2 kWh", 102500L, 2500L),
        BillPackage("Token Listrik Rp 200.000", "Estimasi daya ~148.4 kWh", 202500L, 2500L),
        BillPackage("Token Listrik Rp 500.000", "Estimasi daya ~371.0 kWh", 502500L, 2500L)
    )

    val bpjsPackages = listOf(
        BillPackage("Tagihan BPJS Kelas 3 (1 Orang)", "Iuran JKN-KIS Mandiri per bulan", 35000L, 2500L),
        BillPackage("Tagihan BPJS Kelas 2 (1 Orang)", "Iuran JKN-KIS Mandiri per bulan", 100000L, 2500L),
        BillPackage("Tagihan BPJS Kelas 1 (1 Orang)", "Iuran JKN-KIS Mandiri per bulan", 15000L, 2500L)
    )

    val pdamPackages = listOf(
        BillPackage("Tagihan Air PAM Jaya DKI", "Periode Pemakaian Bulan Ini", 78500L, 2500L),
        BillPackage("Tagihan PDAM Surabaya", "Periode Pemakaian Bulan Ini", 65000L, 2500L),
        BillPackage("Tagihan PDAM Tirtawening", "Periode Pemakaian Bulan Ini", 59000L, 2500L)
    )

    val activePackages = when (selectedCategory) {
        "BILL_PLN" -> plnPackages
        "BILL_BPJS" -> bpjsPackages
        "BILL_PDAM" -> pdamPackages
        else -> pulsaPackages
    }

    val selectedPackage = activePackages.getOrElse(selectedPackageIndex) { activePackages.first() }

    val rawAmount = selectedPackage.price
    val adminFee = selectedPackage.adminFee

    // Discount check
    val discount = if (selectedVoucher != null) {
        if (selectedVoucher!!.discountPercentage > 0) {
            (rawAmount * selectedVoucher!!.discountPercentage / 100).coerceAtMost(selectedVoucher!!.fixedDiscount)
        } else {
            selectedVoucher!!.fixedDiscount
        }
    } else 0L

    val finalTotal = (rawAmount - discount).coerceAtLeast(0L)
    val currentBalance = account?.balance ?: 0L
    val isBalanceEnough = currentBalance >= finalTotal

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bayar Tagihan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("bills_back_btn")) {
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
                .testTag("bills_screen_column")
        ) {
            // Category Chips Row
            ScrollableTabRow(
                selectedTabIndex = categories.indexOfFirst { it.first == selectedCategory }.coerceAtLeast(0),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                categories.forEach { (catId, catName, catIcon) ->
                    val isSelected = selectedCategory == catId
                    Tab(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = catId
                            selectedPackageIndex = 0
                            if (catId == "BILL_PLN") targetInput = "521098471629"
                            else if (catId == "BILL_BPJS") targetInput = "0001234567890"
                            else if (catId == "BILL_PDAM") targetInput = "109827361"
                        },
                        text = { Text(catName, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        icon = { Icon(catIcon, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.testTag("tab_cat_$catId")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Target Field (No HP / ID Pelanggan / No Meter)
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                val inputLabel = when (selectedCategory) {
                    "BILL_PLN" -> "Nomor Meter / ID Pelanggan PLN"
                    "BILL_BPJS" -> "Nomor Kartu BPJS Kesehatan"
                    "BILL_PDAM" -> "Nomor Sambungan / Pelanggan PDAM"
                    else -> "Nomor Telepon Seluler"
                }

                Text(
                    text = inputLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = targetInput,
                    onValueChange = { targetInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bill_target_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Masukkan nomor target...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (selectedCategory == "BILL_PULSA" && targetInput.isNotBlank()) {
                    Text(
                        text = "Operator: ${DompetUtils.detectProvider(targetInput)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DompetBluePrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else if (selectedCategory == "BILL_PLN" && targetInput.length >= 8) {
                    Text(
                        text = "✓ Terverifikasi: Budi Pratama (R1M / 1300 VA)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DompetGreenSuccess,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Package List Selection
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Pilih Produk / Nominal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                activePackages.forEachIndexed { index, pkg ->
                    val isSelected = selectedPackageIndex == index
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedPackageIndex = index }
                            .testTag("package_item_$index"),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pkg.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = pkg.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = DompetUtils.formatRupiah(pkg.price),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = if (isSelected) DompetBluePrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Voucher promo
            val billVouchers = vouchers.filter { it.category == "TAGIHAN" || it.category == "SEMUA" }
            if (billVouchers.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Voucher Tagihan Hemat",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    billVouchers.forEach { voucher ->
                        val isVoucherSelected = selectedVoucher?.id == voucher.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    selectedVoucher = if (isVoucherSelected) null else voucher
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isVoucherSelected) DompetGreenSuccess.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = if (isVoucherSelected) androidx.compose.foundation.BorderStroke(1.dp, DompetGreenSuccess) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎟️ ${voucher.title}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isVoucherSelected) "✓ Terpakai" else "Gunakan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isVoucherSelected) DompetGreenSuccess else DompetBluePrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rincian Pembayaran
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Rincian Pembayaran", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Harga Produk", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(DompetUtils.formatRupiah(rawAmount), fontSize = 12.sp)
                    }
                    if (discount > 0L) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Diskon Kupon", fontSize = 12.sp, color = DompetGreenSuccess)
                            Text("- " + DompetUtils.formatRupiah(discount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DompetGreenSuccess)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Biaya Admin", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(if (adminFee == 0L) "GRATIS" else DompetUtils.formatRupiah(adminFee), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Pembayaran", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(DompetUtils.formatRupiah(finalTotal), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DompetBluePrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (targetInput.isNotBlank() && isBalanceEnough) {
                        onPayBill(
                            selectedCategory,
                            selectedPackage.title,
                            targetInput,
                            selectedPackage.price,
                            adminFee,
                            discount,
                            "Pembayaran tagihan $selectedCategory berhasil"
                        )
                    }
                },
                enabled = targetInput.isNotBlank() && isBalanceEnough,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 20.dp)
                    .testTag("submit_bill_pay_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DompetBluePrimary)
            ) {
                Text(
                    text = if (isBalanceEnough) "Bayar Sekarang" else "Saldo DompetKu Tidak Cukup",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
