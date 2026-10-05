package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_account")
data class WalletAccount(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Budi Pratama",
    val phoneNumber: String = "0812-8899-7721",
    val balance: Long = 1850000L,
    val points: Int = 1250,
    val isPremium: Boolean = true,
    val securityPin: String = "123456",
    val monthlyExpenses: Long = 845000L,
    val monthlyIncome: Long = 2500000L
)

@Entity(tableName = "transaction_records")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // QRIS, TRANSFER, TOPUP, BILL_PLN, BILL_PULSA, BILL_BPJS, BILL_PDAM, REWARDS, CASHOUT
    val amount: Long,
    val type: String, // "IN" or "OUT"
    val status: String = "BERHASIL", // "BERHASIL", "DIPROSES", "GAGAL"
    val timestamp: Long = System.currentTimeMillis(),
    val recipientOrSource: String,
    val referenceId: String,
    val adminFee: Long = 0L,
    val discount: Long = 0L,
    val notes: String = "",
    val pointsEarned: Int = 0
)

@Entity(tableName = "beneficiary_contacts")
data class BeneficiaryContact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val accountNumberOrPhone: String,
    val bankOrProvider: String, // "BCA", "BRI", "Mandiri", "BNI", "BSI", "DompetKu"
    val avatarColorHex: String = "#0A68EB",
    val isFavorite: Boolean = true
)

@Entity(tableName = "voucher_coupons")
data class VoucherCoupon(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val discountText: String, // e.g. "Cashback 30%" or "Diskon Rp 10.000"
    val discountPercentage: Int = 0,
    val fixedDiscount: Long = 0L,
    val category: String, // "SEMUA", "QRIS", "TAGIHAN", "TRANSFER"
    val code: String,
    val expiryText: String,
    val minTransaction: Long = 20000L,
    val isClaimed: Boolean = true
)

@Entity(tableName = "saving_pockets")
data class SavingPocket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val currentAmount: Long,
    val targetAmount: Long,
    val categoryEmoji: String = "🎯"
)

@Entity(tableName = "loyalty_rewards")
data class LoyaltyReward(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "CASHBACK", "VOUCHER", "BILL", "FNB"
    val pointsCost: Int,
    val rewardType: String, // "SALDO_CASHBACK" or "NEW_VOUCHER"
    val rewardValue: Long, // Nominal cashback saldo atau potongan diskon voucher
    val description: String,
    val iconEmoji: String = "🎁",
    val expiryText: String = "Berlaku 30 hari",
    val stockCount: Int = 99
)

@Entity(tableName = "points_history")
data class PointsHistoryRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val pointsChange: Int, // +50 or -350
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // "EARNED" or "REDEEMED"
    val relatedRef: String = ""
)

enum class TierLevel(
    val title: String,
    val minPoints: Int,
    val multiplier: Double,
    val badgeColorHex: String,
    val perksDescription: String
) {
    BRONZE("Bronze", 0, 1.0, "#CD7F32", "1x Poin Tiap Transaksi"),
    SILVER("Silver", 500, 1.2, "#9E9E9E", "1.2x Poin & Voucher Bulanan"),
    GOLD("Gold", 1500, 1.5, "#FFB800", "1.5x Poin & Bebas Biaya Transfer"),
    PLATINUM("Platinum", 3500, 2.0, "#00C2FF", "2x Poin Maksimal & CS Prioritas VIP");

    companion object {
        fun getTier(points: Int): TierLevel {
            return when {
                points >= PLATINUM.minPoints -> PLATINUM
                points >= GOLD.minPoints -> GOLD
                points >= SILVER.minPoints -> SILVER
                else -> BRONZE
            }
        }

        fun getNextTier(points: Int): TierLevel? {
            return when {
                points < SILVER.minPoints -> SILVER
                points < GOLD.minPoints -> GOLD
                points < PLATINUM.minPoints -> PLATINUM
                else -> null
            }
        }
    }
}
