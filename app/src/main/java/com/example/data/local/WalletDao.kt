package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BeneficiaryContact
import com.example.data.model.LoyaltyReward
import com.example.data.model.PointsHistoryRecord
import com.example.data.model.SavingPocket
import com.example.data.model.TransactionRecord
import com.example.data.model.VoucherCoupon
import com.example.data.model.WalletAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    // Wallet Account
    @Query("SELECT * FROM wallet_account WHERE id = 1 LIMIT 1")
    fun getWalletAccount(): Flow<WalletAccount?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAccount(account: WalletAccount)

    @Query("UPDATE wallet_account SET balance = balance + :amount, monthlyIncome = monthlyIncome + :amount WHERE id = 1")
    suspend fun addBalance(amount: Long)

    @Query("UPDATE wallet_account SET balance = balance - :amount, monthlyExpenses = monthlyExpenses + :amount WHERE id = 1")
    suspend fun deductBalance(amount: Long)

    @Query("UPDATE wallet_account SET points = points + :points WHERE id = 1")
    suspend fun addPoints(points: Int)

    @Query("UPDATE wallet_account SET points = points - :points WHERE id = 1")
    suspend fun deductPoints(points: Int)

    @Query("UPDATE wallet_account SET securityPin = :newPin WHERE id = 1")
    suspend fun updateSecurityPin(newPin: String)

    // Transactions
    @Query("SELECT * FROM transaction_records ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transaction_records WHERE type = :type ORDER BY timestamp DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transaction_records ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 5): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transaction_records WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionRecord): Long

    // Beneficiaries
    @Query("SELECT * FROM beneficiary_contacts ORDER BY isFavorite DESC, id ASC")
    fun getAllBeneficiaries(): Flow<List<BeneficiaryContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeneficiary(contact: BeneficiaryContact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBeneficiaries(contacts: List<BeneficiaryContact>)

    // Vouchers
    @Query("SELECT * FROM voucher_coupons ORDER BY id DESC")
    fun getAllVouchers(): Flow<List<VoucherCoupon>>

    @Query("SELECT * FROM voucher_coupons WHERE category = :category OR category = 'SEMUA' ORDER BY id DESC")
    fun getVouchersByCategory(category: String): Flow<List<VoucherCoupon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoucher(voucher: VoucherCoupon): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVouchers(vouchers: List<VoucherCoupon>)

    // Saving Pockets
    @Query("SELECT * FROM saving_pockets ORDER BY id ASC")
    fun getAllSavingPockets(): Flow<List<SavingPocket>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingPocket(pocket: SavingPocket): Long

    @Query("UPDATE saving_pockets SET currentAmount = currentAmount + :amount WHERE id = :pocketId")
    suspend fun addAmountToPocket(pocketId: Long, amount: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPockets(pockets: List<SavingPocket>)

    // Loyalty Rewards
    @Query("SELECT * FROM loyalty_rewards ORDER BY pointsCost ASC")
    fun getAllRewards(): Flow<List<LoyaltyReward>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRewards(rewards: List<LoyaltyReward>)

    @Query("UPDATE loyalty_rewards SET stockCount = stockCount - 1 WHERE id = :rewardId AND stockCount > 0")
    suspend fun decrementRewardStock(rewardId: Long)

    // Points History
    @Query("SELECT * FROM points_history ORDER BY timestamp DESC")
    fun getAllPointsHistory(): Flow<List<PointsHistoryRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointsHistory(record: PointsHistoryRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPointsHistory(records: List<PointsHistoryRecord>)
}
