package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeneficiaryContact
import com.example.data.model.TransactionRecord
import com.example.data.model.WalletAccount
import com.example.ui.components.DompetBalanceCard
import com.example.ui.components.DompetHeader
import com.example.ui.components.DompetPromoCarousel
import com.example.ui.components.DompetServicesGrid
import com.example.ui.theme.DompetBluePrimary
import com.example.ui.theme.DompetGreenSuccess
import com.example.ui.theme.DompetRedWarning
import com.example.utils.DompetUtils

@Composable
fun HomeScreen(
    account: WalletAccount?,
    recentTransactions: List<TransactionRecord>,
    favoriteContacts: List<BeneficiaryContact>,
    isBalanceHidden: Boolean,
    onToggleBalance: () -> Unit,
    onPointsClick: () -> Unit,
    onTopUpClick: () -> Unit,
    onTransferClick: () -> Unit,
    onRequestClick: () -> Unit,
    onCashoutClick: () -> Unit,
    onSelectService: (String) -> Unit,
    onQuickSendContact: (BeneficiaryContact) -> Unit,
    onTransactionClick: (TransactionRecord) -> Unit,
    onViewAllHistory: () -> Unit,
    onPromoClick: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Header
        item {
            DompetHeader(
                account = account,
                onNotificationClick = onNotificationClick,
                onHelpClick = onHelpClick
            )
        }

        // 2. Balance Card
        item {
            DompetBalanceCard(
                account = account,
                isBalanceHidden = isBalanceHidden,
                onToggleBalance = onToggleBalance,
                onPointsClick = onPointsClick,
                onTopUpClick = onTopUpClick,
                onTransferClick = onTransferClick,
                onRequestClick = onRequestClick,
                onCashoutClick = onCashoutClick
            )
        }

        // 3. Kirim Cepat (Quick Transfer Favorites)
        if (favoriteContacts.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kirim Cepat",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Semua Kontak",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DompetBluePrimary,
                            modifier = Modifier.clickable { onTransferClick() }
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(favoriteContacts.take(5)) { contact ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { onQuickSendContact(contact) }
                                    .testTag("quick_contact_${contact.id}")
                                    .width(72.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(DompetBluePrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = contact.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = DompetBluePrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = contact.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = contact.bankOrProvider,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Layanan Populer DompetKu
        item {
            DompetServicesGrid(onSelectService = onSelectService)
        }

        // 5. Promo Carousel (With AI generated visuals)
        item {
            DompetPromoCarousel(onPromoClick = onPromoClick)
        }

        // 6. Transaksi Terakhir (Recent Transactions)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aktivitas Terkini",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Lihat Riwayat",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DompetBluePrimary,
                        modifier = Modifier.clickable { onViewAllHistory() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        val displayList = recentTransactions.take(4)
                        if (displayList.isEmpty()) {
                            Text(
                                text = "Belum ada riwayat transaksi",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            displayList.forEachIndexed { index, item ->
                                RecentTransactionRow(
                                    transaction = item,
                                    onClick = { onTransactionClick(item) }
                                )
                                if (index < displayList.size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Security Guarantee Footer
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Keamanan Terjamin",
                        tint = DompetBluePrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "DompetKu Protection 100%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Uangmu aman terproteksi. Berlisensi resmi Bank Indonesia & diawasi Otoritas Jasa Keuangan (OJK).",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentTransactionRow(
    transaction: TransactionRecord,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.type == "IN") DompetGreenSuccess.copy(alpha = 0.12f)
                        else DompetRedWarning.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.type == "IN") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = transaction.type,
                    tint = if (transaction.type == "IN") DompetGreenSuccess else DompetRedWarning,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = transaction.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = DompetUtils.formatTimestamp(transaction.timestamp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = (if (transaction.type == "IN") "+ " else "- ") + DompetUtils.formatRupiah(transaction.amount),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (transaction.type == "IN") DompetGreenSuccess else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = transaction.status,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = DompetGreenSuccess
            )
        }
    }
}
