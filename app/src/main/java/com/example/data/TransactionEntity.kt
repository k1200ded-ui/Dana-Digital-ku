package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val type: TransactionType,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
)

enum class TransactionType {
    INCOME,
    EXPENSE
}