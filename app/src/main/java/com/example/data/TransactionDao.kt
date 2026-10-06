package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'INCOME' ORDER BY date DESC")
    fun getIncome(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'EXPENSE' ORDER BY date DESC")
    fun getExpense(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date >= :startOfDay AND date < :endOfDay ORDER BY date DESC")
    fun getTodayTransactions(startOfDay: Long, endOfDay: Long): Flow<List<TransactionEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'INCOME' AND date >= :startOfDay AND date < :endOfDay")
    fun getTodayIncome(startOfDay: Long, endOfDay: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'EXPENSE' AND date >= :startOfDay AND date < :endOfDay")
    fun getTodayExpense(startOfDay: Long, endOfDay: Long): Flow<Double>
}
