package com.example.expencetracker2.domain.transaction.repository

import com.example.expencetracker2.data.premiumDashboard.entity.AccountEntity
import com.example.expencetracker2.domain.util.ResultState
import com.example.expencetracker2.domain.transaction.model.Transaction
import kotlinx.coroutines.flow.Flow


interface TransactionRepo {

    suspend fun insertTransaction(transaction: Transaction)



    fun getAllTransaction() : Flow<ResultState<List<Transaction>>>



}