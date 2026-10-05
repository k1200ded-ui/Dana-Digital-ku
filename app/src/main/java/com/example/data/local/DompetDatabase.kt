package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BeneficiaryContact
import com.example.data.model.LoyaltyReward
import com.example.data.model.PointsHistoryRecord
import com.example.data.model.SavingPocket
import com.example.data.model.TransactionRecord
import com.example.data.model.VoucherCoupon
import com.example.data.model.WalletAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WalletAccount::class,
        TransactionRecord::class,
        BeneficiaryContact::class,
        VoucherCoupon::class,
        SavingPocket::class,
        LoyaltyReward::class,
        PointsHistoryRecord::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DompetDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao

    companion object {
        @Volatile
        private var INSTANCE: DompetDatabase? = null

        fun getInstance(context: Context): DompetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DompetDatabase::class.java,
                    "dompetku_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.walletDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: WalletDao) {
                // Initial Wallet Account
                val account = WalletAccount(
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
                dao.insertOrUpdateAccount(account)

                val currentTime = System.currentTimeMillis()
                val oneHour = 3600000L
                val oneDay = 86400000L

                // Initial Indonesian Transactions
                val initialTransactions = listOf(
                    TransactionRecord(
                        title = "QRIS - Kopi Kenangan Senopati",
                        category = "QRIS",
                        amount = 38000L,
                        type = "OUT",
                        status = "BERHASIL",
                        timestamp = currentTime - (2 * oneHour),
                        recipientOrSource = "NMID: ID102004819280",
                        referenceId = "TRX-QRIS-20261005-0812",
                        adminFee = 0L,
                        discount = 5000L,
                        notes = "2 Kopi Kenangan Mantan",
                        pointsEarned = 45
                    ),
                    TransactionRecord(
                        title = "Top Up via BCA OneKlik",
                        category = "TOPUP",
                        amount = 500000L,
                        type = "IN",
                        status = "BERHASIL",
                        timestamp = currentTime - (18 * oneHour),
                        recipientOrSource = "BCA •••• 4892",
                        referenceId = "TRX-TOPUP-20261004-1420",
                        adminFee = 0L,
                        discount = 0L,
                        notes = "Isi Saldo DompetKu Berhasil",
                        pointsEarned = 15
                    ),
                    TransactionRecord(
                        title = "Transfer ke Siti Rahma (BCA)",
                        category = "TRANSFER",
                        amount = 150000L,
                        type = "OUT",
                        status = "BERHASIL",
                        timestamp = currentTime - (1 * oneDay),
                        recipientOrSource = "BCA 8801928371",
                        referenceId = "TRX-TRF-20261004-0931",
                        adminFee = 0L,
                        discount = 0L,
                        notes = "Uang makan siang kantor",
                        pointsEarned = 30
                    ),
                    TransactionRecord(
                        title = "Token Listrik PLN 100rb",
                        category = "BILL_PLN",
                        amount = 102500L,
                        type = "OUT",
                        status = "BERHASIL",
                        timestamp = currentTime - (2 * oneDay),
                        recipientOrSource = "ID Pel: 521098471629",
                        referenceId = "TRX-PLN-20261003-1815",
                        adminFee = 2500L,
                        discount = 0L,
                        notes = "Nomor Token: 2948-1829-4710-9481-2094",
                        pointsEarned = 120
                    ),
                    TransactionRecord(
                        title = "QRIS - Indomaret Tebet Barat",
                        category = "QRIS",
                        amount = 64500L,
                        type = "OUT",
                        status = "BERHASIL",
                        timestamp = currentTime - (3 * oneDay),
                        recipientOrSource = "NMID: ID102984729103",
                        referenceId = "TRX-QRIS-20261002-1240",
                        adminFee = 0L,
                        discount = 0L,
                        notes = "Belanja kebutuhan sehari-hari",
                        pointsEarned = 75
                    ),
                    TransactionRecord(
                        title = "Pulsa Telkomsel 50.000",
                        category = "BILL_PULSA",
                        amount = 51000L,
                        type = "OUT",
                        status = "BERHASIL",
                        timestamp = currentTime - (4 * oneDay),
                        recipientOrSource = "0812-8899-7721",
                        referenceId = "TRX-PLS-20261001-1605",
                        adminFee = 1000L,
                        discount = 0L,
                        notes = "Pengisian pulsa reguler",
                        pointsEarned = 60
                    )
                )
                for (trx in initialTransactions) {
                    dao.insertTransaction(trx)
                }

                // Initial Indonesian Beneficiaries
                val initialContacts = listOf(
                    BeneficiaryContact(
                        name = "Siti Rahma",
                        accountNumberOrPhone = "8801928371",
                        bankOrProvider = "BCA",
                        avatarColorHex = "#0A68EB",
                        isFavorite = true
                    ),
                    BeneficiaryContact(
                        name = "Ahmad Rizky",
                        accountNumberOrPhone = "1370019283741",
                        bankOrProvider = "Mandiri",
                        avatarColorHex = "#F59E0B",
                        isFavorite = true
                    ),
                    BeneficiaryContact(
                        name = "Ibu (Keluarga)",
                        accountNumberOrPhone = "002901827364501",
                        bankOrProvider = "BRI",
                        avatarColorHex = "#10B981",
                        isFavorite = true
                    ),
                    BeneficiaryContact(
                        name = "Farhan Saputra",
                        accountNumberOrPhone = "0857-1928-3344",
                        bankOrProvider = "DompetKu",
                        avatarColorHex = "#8B5CF6",
                        isFavorite = true
                    ),
                    BeneficiaryContact(
                        name = "Dian Permata",
                        accountNumberOrPhone = "0819-3388-7711",
                        bankOrProvider = "DompetKu",
                        avatarColorHex = "#EC4899",
                        isFavorite = false
                    ),
                    BeneficiaryContact(
                        name = "Rudi Hermawan",
                        accountNumberOrPhone = "7182938471",
                        bankOrProvider = "BSI",
                        avatarColorHex = "#059669",
                        isFavorite = false
                    )
                )
                dao.insertAllBeneficiaries(initialContacts)

                // Initial Indonesian Vouchers & Promos
                val initialVouchers = listOf(
                    VoucherCoupon(
                        title = "Cashback QRIS 30% Hingga Rp 15.000",
                        discountText = "30% OFF",
                        discountPercentage = 30,
                        fixedDiscount = 15000L,
                        category = "QRIS",
                        code = "QRISHEMAT30",
                        expiryText = "Berlaku hingga 31 Okt 2026",
                        minTransaction = 20000L,
                        isClaimed = true
                    ),
                    VoucherCoupon(
                        title = "Diskon Listrik PLN Rp 10.000",
                        discountText = "Rp 10.000",
                        discountPercentage = 0,
                        fixedDiscount = 10000L,
                        category = "TAGIHAN",
                        code = "PLNHEMAT10",
                        expiryText = "Berlaku hingga 25 Okt 2026",
                        minTransaction = 100000L,
                        isClaimed = true
                    ),
                    VoucherCoupon(
                        title = "Bebas Biaya Transfer Seluruh Bank",
                        discountText = "GRATIS ADMIN",
                        discountPercentage = 100,
                        fixedDiscount = 2500L,
                        category = "TRANSFER",
                        code = "FREEBIFAST",
                        expiryText = "Tersisa 8x transaksi gratis bulan ini",
                        minTransaction = 10000L,
                        isClaimed = true
                    ),
                    VoucherCoupon(
                        title = "Cashback Jajan Kuliner Rp 12.000",
                        discountText = "Rp 12.000",
                        discountPercentage = 0,
                        fixedDiscount = 12000L,
                        category = "QRIS",
                        code = "JAJANKENYANG",
                        expiryText = "Berlaku di merchant FnB pilihan",
                        minTransaction = 35000L,
                        isClaimed = true
                    )
                )
                dao.insertAllVouchers(initialVouchers)

                // Initial Saving Pockets
                val initialPockets = listOf(
                    SavingPocket(
                        title = "Liburan ke Bali",
                        currentAmount = 1800000L,
                        targetAmount = 4000000L,
                        categoryEmoji = "✈️"
                    ),
                    SavingPocket(
                        title = "Dana Darurat",
                        currentAmount = 3500000L,
                        targetAmount = 5000000L,
                        categoryEmoji = "🛡️"
                    ),
                    SavingPocket(
                        title = "Gadget Impian",
                        currentAmount = 900000L,
                        targetAmount = 3000000L,
                        categoryEmoji = "📱"
                    )
                )
                dao.insertAllPockets(initialPockets)

                // Initial Loyalty Rewards Catalog
                val initialRewards = listOf(
                    LoyaltyReward(
                        title = "Cashback Saldo Rp 10.000",
                        category = "CASHBACK",
                        pointsCost = 200,
                        rewardType = "SALDO_CASHBACK",
                        rewardValue = 10000L,
                        description = "Saldo langsung ditambahkan ke akun DompetKu Anda.",
                        iconEmoji = "💵",
                        expiryText = "Instan Masuk Saldo"
                    ),
                    LoyaltyReward(
                        title = "Cashback Saldo Rp 25.000",
                        category = "CASHBACK",
                        pointsCost = 450,
                        rewardType = "SALDO_CASHBACK",
                        rewardValue = 25000L,
                        description = "Tukar 450 poin untuk mendapatkan cashback saldo Rp 25.000.",
                        iconEmoji = "💰",
                        expiryText = "Instan Masuk Saldo"
                    ),
                    LoyaltyReward(
                        title = "Cashback Saldo Rp 50.000",
                        category = "CASHBACK",
                        pointsCost = 850,
                        rewardType = "SALDO_CASHBACK",
                        rewardValue = 50000L,
                        description = "Spesial member! Saldo Rp 50.000 langsung cair ke dompet.",
                        iconEmoji = "💎",
                        expiryText = "Instan Masuk Saldo"
                    ),
                    LoyaltyReward(
                        title = "Voucher Diskon QRIS Rp 15.000",
                        category = "VOUCHER",
                        pointsCost = 250,
                        rewardType = "NEW_VOUCHER",
                        rewardValue = 15000L,
                        description = "Potongan langsung Rp 15.000 di semua merchant QRIS di Indonesia.",
                        iconEmoji = "🏷️",
                        expiryText = "Berlaku 30 hari"
                    ),
                    LoyaltyReward(
                        title = "Diskon Token Listrik PLN Rp 20.000",
                        category = "BILL",
                        pointsCost = 350,
                        rewardType = "NEW_VOUCHER",
                        rewardValue = 20000L,
                        description = "Potongan Rp 20.000 untuk pembelian token listrik atau tagihan PLN.",
                        iconEmoji = "⚡",
                        expiryText = "Berlaku 30 hari"
                    ),
                    LoyaltyReward(
                        title = "Pulsa Gratis Rp 10.000",
                        category = "BILL",
                        pointsCost = 200,
                        rewardType = "NEW_VOUCHER",
                        rewardValue = 10000L,
                        description = "Voucher potongan Rp 10.000 untuk semua operator seluler.",
                        iconEmoji = "📱",
                        expiryText = "Berlaku 30 hari"
                    ),
                    LoyaltyReward(
                        title = "Voucher Kopi Kenangan Rp 25.000",
                        category = "FNB",
                        pointsCost = 400,
                        rewardType = "NEW_VOUCHER",
                        rewardValue = 25000L,
                        description = "Diskon Rp 25.000 khusus pembelian kopi & pastry favoritmu.",
                        iconEmoji = "☕",
                        expiryText = "Berlaku di seluruh outlet"
                    ),
                    LoyaltyReward(
                        title = "Voucher Belanja Indomaret Rp 20.000",
                        category = "FNB",
                        pointsCost = 300,
                        rewardType = "NEW_VOUCHER",
                        rewardValue = 20000L,
                        description = "Potongan langsung belanja minimarket via scan QRIS.",
                        iconEmoji = "🏪",
                        expiryText = "Berlaku 30 hari"
                    )
                )
                dao.insertAllRewards(initialRewards)

                // Initial Points History
                val initialPointsHistory = listOf(
                    PointsHistoryRecord(
                        title = "Poin Transaksi QRIS - Kopi Kenangan",
                        pointsChange = 45,
                        timestamp = currentTime - (2 * oneHour),
                        type = "EARNED",
                        relatedRef = "TRX-QRIS-20261005-0812"
                    ),
                    PointsHistoryRecord(
                        title = "Bonus Poin Top Up BCA OneKlik",
                        pointsChange = 15,
                        timestamp = currentTime - (18 * oneHour),
                        type = "EARNED",
                        relatedRef = "TRX-TOPUP-20261004-1420"
                    ),
                    PointsHistoryRecord(
                        title = "Poin Transaksi Token Listrik PLN",
                        pointsChange = 120,
                        timestamp = currentTime - (2 * oneDay),
                        type = "EARNED",
                        relatedRef = "TRX-PLN-20261003-1815"
                    ),
                    PointsHistoryRecord(
                        title = "Bonus Selamat Datang Member Silver",
                        pointsChange = 500,
                        timestamp = currentTime - (7 * oneDay),
                        type = "EARNED",
                        relatedRef = "BONUS-TIER-SILVER"
                    )
                )
                dao.insertAllPointsHistory(initialPointsHistory)
            }
        }
    }
}
