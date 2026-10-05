package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.BeneficiaryContact
import com.example.ui.components.DompetBottomNav
import com.example.ui.components.TransactionReceiptDialog
import com.example.ui.screens.*
import com.example.ui.theme.DompetBluePrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.ScreenTab
import com.example.ui.viewmodel.WalletViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: WalletViewModel = viewModel()
                DompetApp(
                    viewModel = viewModel,
                    onShareReceiptText = { shareText ->
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        startActivity(Intent.createChooser(sendIntent, "Bagikan Struk DompetKu"))
                    }
                )
            }
        }
    }
}

@Composable
fun DompetApp(
    viewModel: WalletViewModel,
    onShareReceiptText: (String) -> Unit
) {
    val account by viewModel.walletAccount.collectAsStateWithLifecycle()
    val userTier by viewModel.userTier.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val beneficiaries by viewModel.beneficiaries.collectAsStateWithLifecycle()
    val vouchers by viewModel.vouchers.collectAsStateWithLifecycle()
    val savingPockets by viewModel.savingPockets.collectAsStateWithLifecycle()
    val loyaltyRewards by viewModel.loyaltyRewards.collectAsStateWithLifecycle()
    val pointsHistory by viewModel.pointsHistory.collectAsStateWithLifecycle()

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    val isBalanceHidden by viewModel.isBalanceHidden.collectAsStateWithLifecycle()
    val selectedReceipt by viewModel.selectedReceipt.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val redeemSuccessMessage by viewModel.redeemSuccessMessage.collectAsStateWithLifecycle()

    var prefilledContact by remember { mutableStateOf<BeneficiaryContact?>(null) }
    var selectedBillCategory by remember { mutableStateOf("BILL_PULSA") }
    var showHelpDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (activeScreen == ActiveScreen.MAIN_TABS) {
                DompetBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { tab -> viewModel.setTab(tab) },
                    onQrisClick = { viewModel.navigateTo(ActiveScreen.QRIS_SCANNER) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeScreen) {
                ActiveScreen.MAIN_TABS -> {
                    when (currentTab) {
                        ScreenTab.HOME -> {
                            HomeScreen(
                                account = account,
                                recentTransactions = allTransactions,
                                favoriteContacts = beneficiaries,
                                isBalanceHidden = isBalanceHidden,
                                onToggleBalance = { viewModel.toggleBalanceVisibility() },
                                onPointsClick = { viewModel.navigateTo(ActiveScreen.POINTS_REWARDS) },
                                onTopUpClick = { viewModel.navigateTo(ActiveScreen.TOP_UP) },
                                onTransferClick = {
                                    prefilledContact = null
                                    viewModel.navigateTo(ActiveScreen.TRANSFER)
                                },
                                onRequestClick = { viewModel.navigateTo(ActiveScreen.QRIS_SCANNER) },
                                onCashoutClick = { viewModel.navigateTo(ActiveScreen.TOP_UP) },
                                onSelectService = { category ->
                                    selectedBillCategory = category
                                    viewModel.navigateTo(ActiveScreen.BILLS)
                                },
                                onQuickSendContact = { contact ->
                                    prefilledContact = contact
                                    viewModel.navigateTo(ActiveScreen.TRANSFER)
                                },
                                onTransactionClick = { trx -> viewModel.showReceipt(trx) },
                                onViewAllHistory = { viewModel.setTab(ScreenTab.HISTORY) },
                                onPromoClick = { _ -> viewModel.navigateTo(ActiveScreen.POINTS_REWARDS) },
                                onNotificationClick = {
                                    viewModel.processTopUp(0L, "Sistem")
                                },
                                onHelpClick = { showHelpDialog = true }
                            )
                        }

                        ScreenTab.HISTORY -> {
                            HistoryScreen(
                                account = account,
                                transactions = allTransactions,
                                onTransactionClick = { trx -> viewModel.showReceipt(trx) }
                            )
                        }

                        ScreenTab.POCKET -> {
                            PocketAndVoucherScreen(
                                account = account,
                                pockets = savingPockets,
                                vouchers = vouchers,
                                onAddSaving = { pocketId, amount ->
                                    viewModel.addSaving(pocketId, amount)
                                },
                                onUseVoucher = { voucher ->
                                    if (voucher.category == "QRIS") {
                                        viewModel.navigateTo(ActiveScreen.QRIS_SCANNER)
                                    } else {
                                        viewModel.navigateTo(ActiveScreen.BILLS)
                                    }
                                },
                                onOpenRewards = { viewModel.navigateTo(ActiveScreen.POINTS_REWARDS) }
                            )
                        }

                        ScreenTab.PROFILE -> {
                            ProfileScreen(
                                account = account,
                                onHelpClick = { showHelpDialog = true }
                            )
                        }
                    }
                }

                ActiveScreen.QRIS_SCANNER -> {
                    QrisScannerScreen(
                        account = account,
                        vouchers = vouchers,
                        onBack = { viewModel.navigateBack() },
                        onPayQris = { merchant, nmid, amount, discount ->
                            viewModel.processQrisPayment(merchant, nmid, amount, discount)
                        }
                    )
                }

                ActiveScreen.TRANSFER -> {
                    TransferScreen(
                        account = account,
                        savedContacts = beneficiaries,
                        prefilledContact = prefilledContact,
                        onBack = { viewModel.navigateBack() },
                        onConfirmTransfer = { name, number, bank, amount, notes ->
                            viewModel.processTransfer(name, number, bank, amount, notes)
                        }
                    )
                }

                ActiveScreen.TOP_UP -> {
                    TopUpScreen(
                        account = account,
                        onBack = { viewModel.navigateBack() },
                        onConfirmTopUp = { amount, method ->
                            viewModel.processTopUp(amount, method)
                        }
                    )
                }

                ActiveScreen.BILLS -> {
                    BillPaymentScreen(
                        initialCategory = selectedBillCategory,
                        account = account,
                        vouchers = vouchers,
                        onBack = { viewModel.navigateBack() },
                        onPayBill = { cat, title, target, amount, adminFee, discount, notes ->
                            viewModel.processBillPayment(cat, title, target, amount, adminFee, discount, notes)
                        }
                    )
                }

                ActiveScreen.POINTS_REWARDS -> {
                    PointsRewardScreen(
                        account = account,
                        userTier = userTier,
                        rewards = loyaltyRewards,
                        pointsHistory = pointsHistory,
                        onBack = { viewModel.navigateBack() },
                        onRedeemReward = { reward ->
                            viewModel.redeemReward(reward)
                        }
                    )
                }
            }
        }
    }

    // Receipt Modal Dialog
    selectedReceipt?.let { trx ->
        TransactionReceiptDialog(
            transaction = trx,
            onDismiss = { viewModel.dismissReceipt() },
            onShare = {
                val shareText = """
                    *BUKTI PEMBAYARAN DOMPETKU*
                    Status: BERHASIL
                    ${trx.title}
                    Nominal: Rp ${trx.amount}
                    Poin Didapatkan: +${trx.pointsEarned} Poin
                    Tujuan/Sumber: ${trx.recipientOrSource}
                    Ref: ${trx.referenceId}
                    
                    Aplikasi Dompet Digital Terpercaya Indonesia • Berlisensi Bank Indonesia & OJK
                """.trimIndent()
                onShareReceiptText(shareText)
            },
            onViewRewards = {
                viewModel.navigateTo(ActiveScreen.POINTS_REWARDS)
            }
        )
    }

    // Redemption Celebration Dialog
    redeemSuccessMessage?.let { successMsg ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissRedeemSuccess() },
            title = {
                Text("Penukaran Berhasil! 🎉", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            },
            text = {
                Text(successMsg, fontSize = 14.sp)
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissRedeemSuccess() },
                    colors = ButtonDefaults.buttonColors(containerColor = DompetBluePrimary)
                ) {
                    Text("OK, Mengerti")
                }
            }
        )
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Pusat Bantuan DompetKu 24/7") },
            text = {
                Text(
                    "Butuh bantuan transaksi? Tim customer care DompetKu siap membantu Anda melalui Live Chat atau WhatsApp di 0800-1-DOMPET (Bebas Pulsa).",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(onClick = { showHelpDialog = false }) {
                    Text("Hubungi WhatsApp")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}
