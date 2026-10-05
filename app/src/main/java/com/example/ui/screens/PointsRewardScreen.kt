package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LoyaltyReward
import com.example.data.model.PointsHistoryRecord
import com.example.data.model.TierLevel
import com.example.data.model.WalletAccount
import com.example.ui.theme.DompetBlueDark
import com.example.ui.theme.DompetBluePrimary
import com.example.ui.theme.DompetCyanAccent
import com.example.ui.theme.DompetGoldReward
import com.example.ui.theme.DompetGreenSuccess
import com.example.utils.DompetUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsRewardScreen(
    account: WalletAccount?,
    userTier: TierLevel,
    rewards: List<LoyaltyReward>,
    pointsHistory: List<PointsHistoryRecord>,
    onBack: () -> Unit,
    onRedeemReward: (LoyaltyReward) -> Unit
) {
    BackHandler { onBack() }

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var rewardToRedeem by remember { mutableStateOf<LoyaltyReward?>(null) }

    val currentPoints = account?.points ?: 0
    val nextTier = TierLevel.getNextTier(currentPoints)

    val tierProgress = if (nextTier != null) {
        val currentTierMin = userTier.minPoints
        val nextTierMin = nextTier.minPoints
        val pointsIntoTier = (currentPoints - currentTierMin).coerceAtLeast(0)
        val tierSpan = (nextTierMin - currentTierMin).coerceAtLeast(1)
        (pointsIntoTier.toFloat() / tierSpan.toFloat()).coerceIn(0f, 1f)
    } else 1.0f

    val pointsToNextTier = if (nextTier != null) {
        (nextTier.minPoints - currentPoints).coerceAtLeast(0)
    } else 0

    val filteredRewards = rewards.filter { reward ->
        when (selectedCategoryFilter) {
            "CASHBACK" -> reward.category == "CASHBACK"
            "VOUCHER" -> reward.category == "VOUCHER"
            "BILL" -> reward.category == "BILL"
            "FNB" -> reward.category == "FNB"
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Poin & Reward DompetKu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("points_screen_back_btn")) {
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
                .testTag("points_reward_screen_column")
        ) {
            // Tier & Points Gamified Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("loyalty_tier_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0F2B5C),
                                    DompetBlueDark
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(DompetGoldReward.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = DompetGoldReward,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "${userTier.title} Member",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${userTier.multiplier}x Multiplier Poin",
                                        fontSize = 11.sp,
                                        color = DompetGoldReward,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Big Points Pill
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🪙", fontSize = 16.sp)
                                    Text(
                                        text = "$currentPoints Poin",
                                        color = DompetGoldReward,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Next Tier Progress Bar
                        if (nextTier != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Menuju ${nextTier.title} Tier",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "$pointsToNextTier Poin Lagi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DompetCyanAccent
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { tierProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = DompetGoldReward,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                        } else {
                            Text(
                                text = "🏆 Kamu telah mencapai Tier Tertinggi Platinum!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DompetGoldReward
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Perks Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "✨ Keuntungan: ${userTier.perksDescription}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Tab Navigation
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Tukar Poin", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_reward_catalog")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Riwayat Poin", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_points_history")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Cara Dapat Poin", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_points_guide")
                )
            }

            when (selectedTab) {
                0 -> {
                    // TAB 0: REWARD CATALOG
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Category Filters
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = selectedCategoryFilter == "ALL",
                                        onClick = { selectedCategoryFilter = "ALL" },
                                        label = { Text("Semua") },
                                        modifier = Modifier.testTag("filter_reward_all")
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = selectedCategoryFilter == "CASHBACK",
                                        onClick = { selectedCategoryFilter = "CASHBACK" },
                                        label = { Text("Cashback Saldo") },
                                        modifier = Modifier.testTag("filter_reward_cashback")
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = selectedCategoryFilter == "VOUCHER",
                                        onClick = { selectedCategoryFilter = "VOUCHER" },
                                        label = { Text("Voucher QRIS") },
                                        modifier = Modifier.testTag("filter_reward_voucher")
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = selectedCategoryFilter == "BILL",
                                        onClick = { selectedCategoryFilter = "BILL" },
                                        label = { Text("Tagihan & Pulsa") },
                                        modifier = Modifier.testTag("filter_reward_bill")
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = selectedCategoryFilter == "FNB",
                                        onClick = { selectedCategoryFilter = "FNB" },
                                        label = { Text("Kuliner & Belanja") },
                                        modifier = Modifier.testTag("filter_reward_fnb")
                                    )
                                }
                            }
                        }

                        // Rewards List
                        items(filteredRewards) { reward ->
                            val canAfford = currentPoints >= reward.pointsCost
                            val pointsNeeded = reward.pointsCost - currentPoints

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reward_item_${reward.id}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (reward.category == "CASHBACK") DompetGreenSuccess.copy(alpha = 0.15f)
                                                    else DompetBluePrimary.copy(alpha = 0.12f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(reward.iconEmoji, fontSize = 24.sp)
                                        }

                                        Column {
                                            Text(
                                                text = reward.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = reward.description,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = DompetGoldReward.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "🪙 ${reward.pointsCost} Poin",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = DompetGoldReward,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = { rewardToRedeem = reward },
                                        enabled = canAfford,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DompetBluePrimary,
                                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        modifier = Modifier.testTag("redeem_btn_${reward.id}")
                                    ) {
                                        Text(
                                            text = if (canAfford) "Tukar" else "Kurang $pointsNeeded",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: POINTS HISTORY LEDGER
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (pointsHistory.isEmpty()) {
                            item {
                                Text(
                                    text = "Belum ada riwayat perolehan atau penukaran poin.",
                                    modifier = Modifier.padding(vertical = 30.dp),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(pointsHistory) { record ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = record.title,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = DompetUtils.formatTimestamp(record.timestamp),
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (record.relatedRef.isNotBlank()) {
                                                Text(
                                                    text = "Ref: ${record.relatedRef}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        }

                                        Text(
                                            text = (if (record.pointsChange > 0) "+ " else "") + "${record.pointsChange} Poin",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = if (record.pointsChange > 0) DompetGreenSuccess else Color(0xFFE53935)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: CARA DAPAT POIN GUIDE
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            GuideCard(
                                iconEmoji = "📱",
                                title = "Bayar QRIS di Mana Saja",
                                description = "Dapatkan 1 Poin setiap transaksi kelipatan Rp 1.000 di seluruh merchant QRIS seluruh Indonesia tanpa batas!"
                            )
                        }
                        item {
                            GuideCard(
                                iconEmoji = "⚡",
                                title = "Bayar Tagihan PLN & Pulsa",
                                description = "Kumpulkan poin lebih banyak setiap isi ulang token listrik, pulsa, BPJS, dan tagihan bulanan keluarga."
                            )
                        }
                        item {
                            GuideCard(
                                iconEmoji = "💳",
                                title = "Kirim Uang & Top Up",
                                description = "Transfer antar bank via BI-FAST dan isi saldo DompetKu juga memberikan poin loyalitas otomatis."
                            )
                        }
                        item {
                            GuideCard(
                                iconEmoji = "👑",
                                title = "Naikkan Tier Kamu!",
                                description = "Mulai dari Bronze, Silver (1.2x), Gold (1.5x) hingga Platinum (2x lipat poin)! Semakin tinggi tier, semakin cepat poin bertambah."
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Redemption
    rewardToRedeem?.let { reward ->
        AlertDialog(
            onDismissRequest = { rewardToRedeem = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(reward.iconEmoji, fontSize = 22.sp)
                    Text("Konfirmasi Penukaran")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Apakah kamu yakin ingin menukarkan ${reward.pointsCost} Poin untuk ${reward.title}?",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DompetBluePrimary.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Sisa Poin Kamu: ${currentPoints - reward.pointsCost} Poin", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("Tipe Hadiah: ${if (reward.rewardType == "SALDO_CASHBACK") "Saldo Tunai DompetKu Langsung" else "Kupon Diskon DompetKu"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val selected = reward
                        rewardToRedeem = null
                        onRedeemReward(selected)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DompetBluePrimary),
                    modifier = Modifier.testTag("confirm_redeem_dialog_btn")
                ) {
                    Text("Tukarkan Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { rewardToRedeem = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun GuideCard(
    iconEmoji: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(DompetBluePrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(iconEmoji, fontSize = 24.sp)
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
