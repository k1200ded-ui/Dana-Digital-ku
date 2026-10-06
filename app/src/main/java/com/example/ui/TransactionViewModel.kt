package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TransactionEntity
import com.example.data.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

data class TransactionUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val todayIncome: Double = 0.0,
    val todayExpense: Double = 0.0,
    val todayBalance: Double = 0.0
)

class TransactionViewModel(
    private val db: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState

    init {
        viewModelScope.launch {
            val (startOfDay, endOfDay) = getTodayDateRange()
            combine(
                db.transactionDao().getAll(),
                db.transactionDao().getTotalIncome(),
                db.transactionDao().getTotalExpense(),
                db.transactionDao().getTodayIncome(startOfDay, endOfDay),
                db.transactionDao().getTodayExpense(startOfDay, endOfDay)
            ) { list, income, expense, todayIncome, todayExpense ->
                TransactionUiState(
                    transactions = list,
                    totalIncome = income,
                    totalExpense = expense,
                    balance = income - expense,
                    todayIncome = todayIncome,
                    todayExpense = todayExpense,
                    todayBalance = todayIncome - todayExpense
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun addTransaction(
        title: String,
        category: String,
        type: TransactionType,
        amount: Double,
        note: String
    ) {
        viewModelScope.launch {
            val item = TransactionEntity(
                title = title,
                category = category,
                type = type,
                amount = amount,
                note = note
            )
            db.transactionDao().insert(item)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            db.transactionDao().delete(transaction)
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            db.transactionDao().update(transaction)
        }
    }

    private fun getTodayDateRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis

        return Pair(startOfDay, endOfDay)
    }
}
