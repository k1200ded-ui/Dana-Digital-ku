package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SavingPocket
import com.example.data.model.VoucherCoupon
import com.example.data.model.WalletAccount
import com.example.ui.theme.DompetBluePrimary
import com.example.ui.theme.DompetGoldReward
import com.example.ui.theme.DompetGreenSuccess
import com.example.utils.DompetUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketAndVoucherScreen(
    account: WalletAccount?,
    pockets: List<SavingPocket>,
    vouchers: List<VoucherCoupon>,
    onAddSaving: (pocketId: Long, amount: Long) -> Unit,
    onUseVoucher: (VoucherCoupon) -> Unit,
    onOpenRewards: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedPocketForSaving by remember { mutableStateOf<SavingPocket?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dompet & Keuntungan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
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
                .testTag("pocket_voucher_column")
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Kantong Impian", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Savings, contentDescription = null) },
                    modifier = Modifier.testTag("tab_pockets")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Kupon & Voucher", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Sell, contentDescription = null) },
                    modifier = Modifier.testTag("tab_vouchers")
                )
            }

            if (selectedTab == 0) {
                // KANTONG IMPIAN TAB
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = DompetBluePrimary.copy(alpha = 0.08f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("🎯", fontSize = 28.sp)
                                Column {
                                    Text(
                                        text = "Tabungan Khusus Masa Depan",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Pisahkan uang jajan dan uang target impianmu tanpa biaya admin sepeserpun.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    items(pockets) { pocket ->
                        val progress = if (pocket.targetAmount > 0) {
                            (pocket.currentAmount.toFloat() / pocket.targetAmount.toFloat()).coerceIn(0f, 1f)
                        } else 0f

                        val percentage = (progress * 100).toInt()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pocket_card_${pocket.id}"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(pocket.categoryEmoji, fontSize = 22.sp)
                                        Text(
                                            text = pocket.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = DompetGreenSuccess.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "$percentage% Tercapai",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DompetGreenSuccess,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = DompetBluePrimary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = DompetUtils.formatRupiah(pocket.currentAmount),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = DompetBluePrimary
                                    )
                                    Text(
                                        text = "Target: ${DompetUtils.formatRupiah(pocket.targetAmount)}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedButton(
                                    onClick = { selectedPocketForSaving = pocket },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Setor Tabungan (+ Rp 50.000)", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                // VOUCHERS TAB
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DompetGoldReward.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("🎁", fontSize = 24.sp)
                                Text(
                                    text = "Gunakan kupon saat transaksi QRIS, pulsa, atau tagihan untuk potongan harga langsung!",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onOpenRewards)
                                .testTag("banner_points_to_rewards"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DompetBluePrimary.copy(alpha = 0.08f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DompetBluePrimary.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("🪙", fontSize = 22.sp)
                                    Column {
                                        Text(
                                            text = "Punya ${account?.points ?: 0} Poin DompetKu?",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Tukarkan jadi voucher belanja, pulsa & cashback saldo!",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = "Tukar >",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DompetBluePrimary
                                )
                            }
                        }
                    }

                    items(vouchers) { voucher ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("voucher_card_${voucher.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DompetBluePrimary
                                    ) {
                                        Text(
                                            text = voucher.discountText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        text = voucher.category,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = voucher.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = voucher.expiryText,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = "KODE: ${voucher.code}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Button(
                                        onClick = { onUseVoucher(voucher) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DompetBluePrimary),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("Gunakan", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Setor Tabungan
    selectedPocketForSaving?.let { pocket ->
        AlertDialog(
            onDismissRequest = { selectedPocketForSaving = null },
            title = { Text("Setor Tabungan ke ${pocket.title}") },
            text = {
                Text(
                    "Alokasikan Rp 50.000 dari saldo DompetKu ke kantong impianmu?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddSaving(pocket.id, 50000L)
                        selectedPocketForSaving = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DompetBluePrimary)
                ) {
                    Text("Ya, Setor Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPocketForSaving = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
