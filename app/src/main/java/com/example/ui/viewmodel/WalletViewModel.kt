package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DompetDatabase
import com.example.data.model.BeneficiaryContact
import com.example.data.model.LoyaltyReward
import com.example.data.model.PointsHistoryRecord
import com.example.data.model.SavingPocket
import com.example.data.model.TierLevel
import com.example.data.model.TransactionRecord
import com.example.data.model.VoucherCoupon
import com.example.data.model.WalletAccount
import com.example.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME,
    HISTORY,
    POCKET,
    PROFILE
}

enum class ActiveScreen {
    MAIN_TABS,
    QRIS_SCANNER,
    TRANSFER,
    TOP_UP,
    BILLS,
    POINTS_REWARDS
}

class WalletViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WalletRepository

    init {
        val db = DompetDatabase.getInstance(application)
        repository = WalletRepository(db.walletDao())
        viewModelScope.launch {
            repository.ensureAccountExists()
        }
    }

    val walletAccount: StateFlow<WalletAccount?> = repository.walletAccount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WalletAccount(id = 1, balance = 1850000L, points = 1250)
        )

    val userTier: StateFlow<TierLevel> = walletAccount
        .map { account -> TierLevel.getTier(account?.points ?: 0) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TierLevel.SILVER
        )

    val allTransactions: StateFlow<List<TransactionRecord>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val beneficiaries: StateFlow<List<BeneficiaryContact>> = repository.beneficiaries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val vouchers: StateFlow<List<VoucherCoupon>> = repository.vouchers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savingPockets: StateFlow<List<SavingPocket>> = repository.savingPockets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val loyaltyRewards: StateFlow<List<LoyaltyReward>> = repository.loyaltyRewards
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pointsHistory: StateFlow<List<PointsHistoryRecord>> = repository.pointsHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI States
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _activeScreen = MutableStateFlow(ActiveScreen.MAIN_TABS)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    private val _isBalanceHidden = MutableStateFlow(false)
    val isBalanceHidden: StateFlow<Boolean> = _isBalanceHidden.asStateFlow()

    private val _selectedReceipt = MutableStateFlow<TransactionRecord?>(null)
    val selectedReceipt: StateFlow<TransactionRecord?> = _selectedReceipt.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _redeemSuccessMessage = MutableStateFlow<String?>(null)
    val redeemSuccessMessage: StateFlow<String?> = _redeemSuccessMessage.asStateFlow()

    // Navigation methods
    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
        _activeScreen.value = ActiveScreen.MAIN_TABS
    }

    fun navigateTo(screen: ActiveScreen) {
        _activeScreen.value = screen
    }

    fun navigateBack() {
        _activeScreen.value = ActiveScreen.MAIN_TABS
    }

    fun toggleBalanceVisibility() {
        _isBalanceHidden.value = !_isBalanceHidden.value
    }

    fun showReceipt(transaction: TransactionRecord) {
        _selectedReceipt.value = transaction
    }

    fun dismissReceipt() {
        _selectedReceipt.value = null
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun dismissRedeemSuccess() {
        _redeemSuccessMessage.value = null
    }

    fun redeemReward(reward: LoyaltyReward) {
        viewModelScope.launch {
            val result = repository.redeemReward(reward)
            result.onSuccess { message ->
                _redeemSuccessMessage.value = message
                _toastMessage.value = "Berhasil menukarkan ${reward.title}!"
            }.onFailure { error ->
                _toastMessage.value = error.message ?: "Gagal menukarkan reward"
            }
        }
    }

    fun processTopUp(amount: Long, method: String) {
        viewModelScope.launch {
            try {
                val record = repository.topUp(amount, method)
                _toastMessage.value = "Isi saldo Rp $amount berhasil! +${record.pointsEarned} Poin"
                _selectedReceipt.value = record
                _activeScreen.value = ActiveScreen.MAIN_TABS
            } catch (e: Exception) {
                _toastMessage.value = "Gagal memproses top up: ${e.localizedMessage}"
            }
        }
    }

    fun processTransfer(
        recipientName: String,
        accountNumber: String,
        bank: String,
        amount: Long,
        notes: String
    ) {
        viewModelScope.launch {
            val currentBalance = walletAccount.value?.balance ?: 0L
            if (currentBalance < amount) {
                _toastMessage.value = "Saldo tidak mencukupi untuk transfer!"
                return@launch
            }

            try {
                val result = repository.transfer(
                    recipientName = recipientName,
                    accountNumber = accountNumber,
                    bankOrProvider = bank,
                    amount = amount,
                    notes = notes,
                    adminFee = 0L // Bebas biaya admin BI-FAST
                )
                result.onSuccess { record ->
                    _toastMessage.value = "Transfer berhasil! Kamu dapat +${record.pointsEarned} Poin DompetKu"
                    _selectedReceipt.value = record
                    _activeScreen.value = ActiveScreen.MAIN_TABS
                }
            } catch (e: Exception) {
                _toastMessage.value = "Transfer gagal: ${e.localizedMessage}"
            }
        }
    }

    fun processQrisPayment(
        merchantName: String,
        nmid: String,
        amount: Long,
        voucherDiscount: Long,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val netAmount = (amount - voucherDiscount).coerceAtLeast(0L)
            val currentBalance = walletAccount.value?.balance ?: 0L
            if (currentBalance < netAmount) {
                _toastMessage.value = "Saldo DompetKu tidak mencukupi!"
                return@launch
            }

            try {
                val result = repository.payQris(
                    merchantName = merchantName,
                    nmid = nmid,
                    amount = amount,
                    discount = voucherDiscount,
                    notes = notes
                )
                result.onSuccess { record ->
                    _toastMessage.value = "Pembayaran QRIS berhasil! +${record.pointsEarned} Poin Loyalitas"
                    _selectedReceipt.value = record
                    _activeScreen.value = ActiveScreen.MAIN_TABS
                }
            } catch (e: Exception) {
                _toastMessage.value = "Pembayaran QRIS gagal: ${e.localizedMessage}"
            }
        }
    }

    fun processBillPayment(
        category: String,
        title: String,
        targetNumber: String,
        amount: Long,
        adminFee: Long,
        voucherDiscount: Long,
        notes: String
    ) {
        viewModelScope.launch {
            val netAmount = (amount + adminFee - voucherDiscount).coerceAtLeast(0L)
            val currentBalance = walletAccount.value?.balance ?: 0L
            if (currentBalance < netAmount) {
                _toastMessage.value = "Saldo DompetKu tidak mencukupi untuk tagihan ini!"
                return@launch
            }

            try {
                val result = repository.payBill(
                    category = category,
                    title = title,
                    targetNumber = targetNumber,
                    amount = amount,
                    adminFee = adminFee,
                    discount = voucherDiscount,
                    notes = notes
                )
                result.onSuccess { record ->
                    _toastMessage.value = "Tagihan berhasil dibayar! +${record.pointsEarned} Poin didapatkan"
                    _selectedReceipt.value = record
                    _activeScreen.value = ActiveScreen.MAIN_TABS
                }
            } catch (e: Exception) {
                _toastMessage.value = "Gagal membayar tagihan: ${e.localizedMessage}"
            }
        }
    }

    fun addSaving(pocketId: Long, amount: Long) {
        viewModelScope.launch {
            val currentBalance = walletAccount.value?.balance ?: 0L
            if (currentBalance < amount) {
                _toastMessage.value = "Saldo tidak mencukupi untuk dialokasikan!"
                return@launch
            }
            repository.addAmountToSaving(pocketId, amount)
            _toastMessage.value = "Berhasil menambah tabungan! +5 Poin"
        }
    }
}
