package com.example.expencetracker2.domain.transaction.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transaction")
data class Transaction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val amount: Double,
    val popularCategoryId: Long? = null,
    val regularCategoryId: Long? = null,
    val masterCategoryId: Long,

    val timestamp: Long,
    val note: String? = null,
    val paymentMode : String = "UPI",

    val isSynced : Boolean = false,
    val isSpeedExpense : Boolean = true,
    val isExpense : Boolean = true,
    val isIncome : Boolean = false,
    val  isTransfer : Boolean = false
)
