package com.example.data.repository

import com.example.data.local.WalletDao
import com.example.data.model.BeneficiaryContact
import com.example.data.model.LoyaltyReward
import com.example.data.model.PointsHistoryRecord
import com.example.data.model.SavingPocket
import com.example.data.model.TierLevel
import com.example.data.model.TransactionRecord
import com.example.data.model.VoucherCoupon
import com.example.data.model.WalletAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

class WalletRepository(private val dao: WalletDao) {

    val walletAccount: Flow<WalletAccount?> = dao.getWalletAccount()
    val allTransactions: Flow<List<TransactionRecord>> = dao.getAllTransactions()
    val beneficiaries: Flow<List<BeneficiaryContact>> = dao.getAllBeneficiaries()
    val vouchers: Flow<List<VoucherCoupon>> = dao.getAllVouchers()
    val savingPockets: Flow<List<SavingPocket>> = dao.getAllSavingPockets()
    val loyaltyRewards: Flow<List<LoyaltyReward>> = dao.getAllRewards()
    val pointsHistory: Flow<List<PointsHistoryRecord>> = dao.getAllPointsHistory()

    suspend fun ensureAccountExists() {
        val default = WalletAccount(
            id = 1,
            userName = "Budi Pratama",
            phoneNumber = "0812-8899-7721",
            balance = 1850000L,
            points = 1250,
            isPremium = true,
            securityPin = "123456",
            monthlyExpenses = 780000L,
            monthlyIncome = 3000000L
        )
        dao.insertOrUpdateAccount(default)
    }

    private fun generateRefId(prefix: String): String {
        val sdf = SimpleDateFormat("yyyyMMdd-HHmm", Locale.getDefault())
        val datePart = sdf.format(Date())
        val rand = Random().nextInt(9000) + 1000
        return "TRX-$prefix-$datePart-$rand"
    }

    private suspend fun calculatePoints(amount: Long, category: String): Int {
        val currentAccount = dao.getWalletAccount().firstOrNull()
        val tier = TierLevel.getTier(currentAccount?.points ?: 0)
        val basePoints = when (category) {
            "QRIS" -> (amount / 1000L).coerceAtLeast(10L)
            "BILL" -> (amount / 1000L).coerceAtLeast(15L)
            "TRANSFER" -> (amount / 5000L).coerceAtLeast(5L)
            else -> 10L
        }
        val calculated = (basePoints * tier.multiplier).toInt().coerceAtMost(500)
        return calculated
    }

    suspend fun topUp(amount: Long, method: String): TransactionRecord {
        val refId = generateRefId("TOPUP")
        val bonusPoints = 15
        val record = TransactionRecord(
            title = "Isi Saldo via $method",
            category = "TOPUP",
            amount = amount,
            type = "IN",
            status = "BERHASIL",
            timestamp = System.currentTimeMillis(),
            recipientOrSource = method,
            referenceId = refId,
            adminFee = 0L,
            discount = 0L,
            notes = "Pengisian saldo berhasil masuk ke DompetKu",
            pointsEarned = bonusPoints
        )
        dao.addBalance(amount)
        dao.addPoints(bonusPoints)
        dao.insertPointsHistory(
            PointsHistoryRecord(
                title = "Bonus Top Up $method",
                pointsChange = bonusPoints,
                timestamp = System.currentTimeMillis(),
                type = "EARNED",
                relatedRef = refId
            )
        )
        val id = dao.insertTransaction(record)
        return record.copy(id = id)
    }

    suspend fun transfer(
        recipientName: String,
        accountNumber: String,
        bankOrProvider: String,
        amount: Long,
        notes: String,
        adminFee: Long = 0L
    ): Result<TransactionRecord> {
        val totalDeduction = amount + adminFee
        val refId = generateRefId("TRF")
        val pointsEarned = calculatePoints(amount, "TRANSFER")
        val record = TransactionRecord(
            title = "Transfer ke $recipientName ($bankOrProvider)",
            category = "TRANSFER",
            amount = totalDeduction,
            type = "OUT",
            status = "BERHASIL",
            timestamp = System.currentTimeMillis(),
            recipientOrSource = "$bankOrProvider $accountNumber",
            referenceId = refId,
            adminFee = adminFee,
            discount = 0L,
            notes = if (notes.isBlank()) "Transfer dana DompetKu" else notes,
            pointsEarned = pointsEarned
        )
        dao.deductBalance(totalDeduction)
        dao.addPoints(pointsEarned)
        dao.insertPointsHistory(
            PointsHistoryRecord(
                title = "Poin Transfer ke $recipientName",
                pointsChange = pointsEarned,
                timestamp = System.currentTimeMillis(),
                type = "EARNED",
                relatedRef = refId
            )
        )
        val id = dao.insertTransaction(record)

        // Save contact to beneficiaries if not exists
        dao.insertBeneficiary(
            BeneficiaryContact(
                name = recipientName,
                accountNumberOrPhone = accountNumber,
                bankOrProvider = bankOrProvider,
                avatarColorHex = when (bankOrProvider) {
                    "BCA" -> "#0A68EB"
                    "Mandiri" -> "#F59E0B"
                    "BRI" -> "#10B981"
                    "BNI" -> "#F97316"
                    "BSI" -> "#059669"
                    else -> "#6366F1"
                },
                isFavorite = false
            )
        )

        return Result.success(record.copy(id = id))
    }

    suspend fun payQris(
        merchantName: String,
        nmid: String,
        amount: Long,
        discount: Long = 0L,
        notes: String = ""
    ): Result<TransactionRecord> {
        val totalDeduction = (amount - discount).coerceAtLeast(0L)
        val refId = generateRefId("QRIS")
        val pointsEarned = calculatePoints(totalDeduction, "QRIS")
        val record = TransactionRecord(
            title = "QRIS - $merchantName",
            category = "QRIS",
            amount = totalDeduction,
            type = "OUT",
            status = "BERHASIL",
            timestamp = System.currentTimeMillis(),
            recipientOrSource = "NMID: $nmid",
            referenceId = refId,
            adminFee = 0L,
            discount = discount,
            notes = if (notes.isBlank()) "Pembayaran QRIS Standar Bank Indonesia" else notes,
            pointsEarned = pointsEarned
        )
        dao.deductBalance(totalDeduction)
        dao.addPoints(pointsEarned)
        dao.insertPointsHistory(
            PointsHistoryRecord(
                title = "Poin QRIS di $merchantName",
                pointsChange = pointsEarned,
                timestamp = System.currentTimeMillis(),
                type = "EARNED",
                relatedRef = refId
            )
        )
        val id = dao.insertTransaction(record)
        return Result.success(record.copy(id = id))
    }

    suspend fun payBill(
        category: String,
        title: String,
        targetNumber: String,
        amount: Long,
        adminFee: Long = 0L,
        discount: Long = 0L,
        notes: String = ""
    ): Result<TransactionRecord> {
        val totalDeduction = (amount + adminFee - discount).coerceAtLeast(0L)
        val prefix = when (category) {
            "BILL_PLN" -> "PLN"
            "BILL_PULSA" -> "PLS"
            "BILL_BPJS" -> "BPJS"
            "BILL_PDAM" -> "PDAM"
            else -> "BILL"
        }
        val refId = generateRefId(prefix)
        val pointsEarned = calculatePoints(totalDeduction, "BILL")
        val record = TransactionRecord(
            title = title,
            category = category,
            amount = totalDeduction,
            type = "OUT",
            status = "BERHASIL",
            timestamp = System.currentTimeMillis(),
            recipientOrSource = targetNumber,
            referenceId = refId,
            adminFee = adminFee,
            discount = discount,
            notes = notes,
            pointsEarned = pointsEarned
        )
        dao.deductBalance(totalDeduction)
        dao.addPoints(pointsEarned)
        dao.insertPointsHistory(
            PointsHistoryRecord(
                title = "Poin Pembayaran $title",
                pointsChange = pointsEarned,
                timestamp = System.currentTimeMillis(),
                type = "EARNED",
                relatedRef = refId
            )
        )
        val id = dao.insertTransaction(record)
        return Result.success(record.copy(id = id))
    }

    suspend fun redeemReward(reward: LoyaltyReward): Result<String> {
        val account = dao.getWalletAccount().firstOrNull() ?: return Result.failure(Exception("Akun tidak ditemukan"))
        if (account.points < reward.pointsCost) {
            return Result.failure(Exception("Poin kamu belum mencukupi untuk reward ini!"))
        }

        // Deduct points
        dao.deductPoints(reward.pointsCost)
        dao.decrementRewardStock(reward.id)

        // Record points history
        val refId = generateRefId("REDEEM")
        dao.insertPointsHistory(
            PointsHistoryRecord(
                title = "Tukar: ${reward.title}",
                pointsChange = -reward.pointsCost,
                timestamp = System.currentTimeMillis(),
                type = "REDEEMED",
                relatedRef = refId
            )
        )

        // Process reward fulfillment
        if (reward.rewardType == "SALDO_CASHBACK") {
            dao.addBalance(reward.rewardValue)
            val cashbackTrx = TransactionRecord(
                title = "Cashback Hadiah Poin: ${reward.title}",
                category = "REWARDS",
                amount = reward.rewardValue,
                type = "IN",
                status = "BERHASIL",
                timestamp = System.currentTimeMillis(),
                recipientOrSource = "DompetKu Loyalty Rewards",
                referenceId = refId,
                adminFee = 0L,
                discount = 0L,
                notes = "Hasil penukaran ${reward.pointsCost} Poin DompetKu",
                pointsEarned = 0
            )
            dao.insertTransaction(cashbackTrx)
            return Result.success("Selamat! Saldo cashback Rp ${reward.rewardValue} langsung masuk ke DompetKu kamu!")
        } else {
            // Generate Voucher
            val voucherCode = "POIN" + (Random().nextInt(90000) + 10000)
            val newVoucher = VoucherCoupon(
                title = reward.title,
                discountText = "Potongan Rp ${reward.rewardValue}",
                discountPercentage = 0,
                fixedDiscount = reward.rewardValue,
                category = if (reward.category == "FNB") "QRIS" else if (reward.category == "BILL") "TAGIHAN" else "SEMUA",
                code = voucherCode,
                expiryText = "Berlaku 30 hari ke depan",
                minTransaction = reward.rewardValue,
                isClaimed = true
            )
            dao.insertVoucher(newVoucher)
            return Result.success("Voucher '${reward.title}' berhasil ditukarkan dan siap digunakan di menu Voucher!")
        }
    }

    suspend fun addAmountToSaving(pocketId: Long, amount: Long) {
        dao.deductBalance(amount)
        dao.addAmountToPocket(pocketId, amount)
        val refId = generateRefId("SAVE")
        val record = TransactionRecord(
            title = "Setor Tabungan Impian",
            category = "SAVINGS",
            amount = amount,
            type = "OUT",
            status = "BERHASIL",
            timestamp = System.currentTimeMillis(),
            recipientOrSource = "Kantong DompetKu",
            referenceId = refId,
            adminFee = 0L,
            discount = 0L,
            notes = "Alokasi tabungan masa depan",
            pointsEarned = 5
        )
        dao.insertTransaction(record)
    }

    suspend fun updatePin(newPin: String) {
        dao.updateSecurityPin(newPin)
    }

    suspend fun getTransactionById(id: Long): TransactionRecord? {
        return dao.getTransactionById(id)
    }
}
