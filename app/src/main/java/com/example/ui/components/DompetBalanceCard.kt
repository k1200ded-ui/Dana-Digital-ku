package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WalletAccount
import com.example.ui.theme.DompetBlueDark
import com.example.ui.theme.DompetBluePrimary
import com.example.ui.theme.DompetCyanAccent
import com.example.ui.theme.DompetGoldReward
import com.example.utils.DompetUtils

@Composable
fun DompetBalanceCard(
    account: WalletAccount?,
    isBalanceHidden: Boolean,
    onToggleBalance: () -> Unit,
    onPointsClick: () -> Unit,
    onTopUpClick: () -> Unit,
    onTransferClick: () -> Unit,
    onRequestClick: () -> Unit,
    onCashoutClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .testTag("dompet_balance_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            DompetBluePrimary,
                            DompetBlueDark
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                // Top row: Saldo info & DompetKu Points
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Saldo DompetKu",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(
                            onClick = onToggleBalance,
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("toggle_balance_btn")
                        ) {
                            Icon(
                                imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Sembunyikan Saldo",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Poin DompetKu badge (Clickable to open Rewards Hub)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .clickable(onClick = onPointsClick)
                            .testTag("dompet_points_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🪙",
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${account?.points ?: 0} Poin",
                                color = DompetGoldReward,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Saldo Amount
                Text(
                    text = if (isBalanceHidden) "Rp ••••••••" else DompetUtils.formatRupiah(account?.balance ?: 0L),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                Spacer(modifier = Modifier.height(16.dp))

                // 4 Main Actions: Top Up, Kirim, Minta, Tarik Tunai
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButton(
                        icon = Icons.Default.AddCard,
                        label = "Isi Saldo",
                        tag = "action_topup",
                        onClick = onTopUpClick
                    )
                    ActionButton(
                        icon = Icons.Default.Send,
                        label = "Kirim",
                        tag = "action_transfer",
                        onClick = onTransferClick
                    )
                    ActionButton(
                        icon = Icons.Default.CallReceived,
                        label = "Minta",
                        tag = "action_request",
                        onClick = onRequestClick
                    )
                    ActionButton(
                        icon = Icons.Default.LocalAtm,
                        label = "Tarik Tunai",
                        tag = "action_cashout",
                        onClick = onCashoutClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(tag)
            .padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
