package com.example.expencetracker2.presentation.transaction

import com.example.expencetracker2.data.premiumDashboard.entity.AccountEntity
import com.example.expencetracker2.domain.transaction.model.Transaction

data class InsertTransactionState(
    val success : List<Transaction> = emptyList(),
    val error : String? = null,
    val loading : Boolean = false
)

data class InsertAccountState(
    val success : List<AccountEntity> = emptyList(),
    val error : String? = null,
    val loading : Boolean = false
)

