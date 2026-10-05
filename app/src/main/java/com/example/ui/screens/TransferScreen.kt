package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeneficiaryContact
import com.example.data.model.WalletAccount
import com.example.ui.theme.DompetBluePrimary
import com.example.ui.theme.DompetGreenSuccess
import com.example.utils.DompetUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    account: WalletAccount?,
    savedContacts: List<BeneficiaryContact>,
    prefilledContact: BeneficiaryContact? = null,
    onBack: () -> Unit,
    onConfirmTransfer: (name: String, number: String, bank: String, amount: Long, notes: String) -> Unit
) {
    BackHandler { onBack() }

    var selectedTab by remember { mutableIntStateOf(if (prefilledContact?.bankOrProvider == "DompetKu") 1 else 0) }

    val banks = listOf(
        "BCA", "Mandiri", "BRI", "BNI", "BSI", "CIMB Niaga", "Permata", "Bank Jago", "SeaBank"
    )

    var selectedBank by remember { mutableStateOf(prefilledContact?.bankOrProvider ?: "BCA") }
    var accountNumber by remember { mutableStateOf(prefilledContact?.accountNumberOrPhone ?: "") }
    var recipientName by remember { mutableStateOf(prefilledContact?.name ?: "") }
    var amountText by remember { mutableStateOf("") }
    var transferNotes by remember { mutableStateOf("") }
    var isCheckingAccount by remember { mutableStateOf(false) }

    // Simulated account lookup when 8+ digits entered
    LaunchedEffect(accountNumber, selectedBank) {
        if (accountNumber.length >= 8 && recipientName.isEmpty()) {
            isCheckingAccount = true
            kotlinx.coroutines.delay(400)
            recipientName = when {
                accountNumber.endsWith("1") -> "Siti Rahma"
                accountNumber.endsWith("2") -> "Ahmad Rizky"
                accountNumber.endsWith("3") -> "Ibu Kartini"
                accountNumber.endsWith("4") -> "Farhan Saputra"
                else -> "Penerima $selectedBank Terdaftar"
            }
            isCheckingAccount = false
        }
    }

    val quickAmounts = listOf(50000L, 100000L, 250000L, 500000L, 1000000L)
    val parsedAmount = amountText.toLongOrNull() ?: 0L
    val currentBalance = account?.balance ?: 0L
    val isBalanceSufficient = parsedAmount in 10000..currentBalance

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kirim / Transfer Dana",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("transfer_back_btn")) {
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
                .testTag("transfer_screen_column")
        ) {
            // Transfer Destination Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Rekening Bank", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                    modifier = Modifier.testTag("tab_transfer_bank")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Ke DompetKu", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null) },
                    modifier = Modifier.testTag("tab_transfer_phone")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BI-FAST Guarantee Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                color = DompetGreenSuccess.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = DompetGreenSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Transfer Antar Bank via BI-FAST • GRATIS Biaya Admin 100%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DompetGreenSuccess
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // BANK TRANSFER
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Pilih Bank Tujuan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(banks) { bank ->
                            val isSelected = selectedBank == bank
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedBank = bank },
                                label = { Text(bank, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DompetBluePrimary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("chip_bank_$bank")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Nomor Rekening",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = {
                            accountNumber = it.filter { char -> char.isDigit() }
                            recipientName = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_account_number"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("Contoh: 8801928371") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (isCheckingAccount) {
                        Text(
                            text = "Memeriksa nomor rekening...",
                            fontSize = 11.sp,
                            color = DompetBluePrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else if (recipientName.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DompetGreenSuccess.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DompetGreenSuccess, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Nama Penerima: $recipientName",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DompetGreenSuccess
                                )
                            }
                        }
                    }
                }
            } else {
                // DOMPETKU USER TRANSFER
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Nomor HP Pengguna DompetKu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = {
                            accountNumber = it
                            if (it.length >= 10 && recipientName.isEmpty()) {
                                recipientName = "Farhan Saputra"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_phone_transfer"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        placeholder = { Text("08xx-xxxx-xxxx") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (recipientName.isNotEmpty()) {
                        Text(
                            text = "✓ Akun DompetKu: $recipientName",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DompetGreenSuccess,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Favorit Penerima Terakhir
            if (savedContacts.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Penerima Favorit",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(savedContacts.take(4)) { contact ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.clickable {
                                    recipientName = contact.name
                                    accountNumber = contact.accountNumberOrPhone
                                    selectedBank = contact.bankOrProvider
                                    if (contact.bankOrProvider == "DompetKu") {
                                        selectedTab = 1
                                    } else {
                                        selectedTab = 0
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(DompetBluePrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(contact.name.take(1), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(contact.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nominal Input
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Nominal Transfer",
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
                        .testTag("transfer_amount_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Saldo tersedia: ${DompetUtils.formatRupiah(currentBalance)}",
                    fontSize = 11.sp,
                    color = if (parsedAmount > currentBalance) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Nominal Buttons
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quickAmounts) { amt ->
                        SuggestionChip(
                            onClick = { amountText = amt.toString() },
                            label = { Text(DompetUtils.formatRupiah(amt), fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Catatan (Opsional)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = transferNotes,
                    onValueChange = { transferNotes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transfer_notes_input"),
                    placeholder = { Text("Contoh: Pembayaran makan siang, patungan, dll.") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val finalName = if (recipientName.isNotBlank()) recipientName else "Penerima $accountNumber"
                        val finalBank = if (selectedTab == 0) selectedBank else "DompetKu"
                        onConfirmTransfer(finalName, accountNumber, finalBank, parsedAmount, transferNotes)
                    },
                    enabled = isBalanceSufficient && accountNumber.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_transfer_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DompetBluePrimary)
                ) {
                    Text(
                        text = "Transfer Sekarang",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
