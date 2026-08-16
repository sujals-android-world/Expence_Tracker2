package com.example.expencetracker2.data.premiumDashboard.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,              // "HDFC Bank", "Axis Credit Card", "Cash", "Paytm Wallet"
    val balance: Double,           // Current Balance या Card Limit
    val icon: String?,
    val isPrimary: Boolean = false,

    // 🔹 Sub-Types के लिए:
    val accountType: String,       // "CASH", "BANK", "CREDIT_CARD", "WALLET"
    val linkedBankId: Long? = null, // अगर Debit Card या Bank UPI है, तो जिस Bank से लिंक है उसकी ID
    val creditLimit: Double? = null,        // Total Limit (उदा. ₹1,00,000)
    val statementDate: Int? = null,         // हर महीने की बिल बनने की तारीख (1 to 31)
    val dueDate: Int? = null,                // बिल भरने की आखिरी तारीख (1 to 31)
    val usageCount: Int = 0
)