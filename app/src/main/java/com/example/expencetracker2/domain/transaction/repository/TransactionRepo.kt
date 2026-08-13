package com.example.expencetracker2.domain.transaction.repository

import com.example.expencetracker2.data.tracsaction.local.entity.AccountEntity
import com.example.expencetracker2.domain.util.ResultState
import com.example.expencetracker2.domain.transaction.model.Transaction
import kotlinx.coroutines.flow.Flow


interface TransactionRepo {

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun insertAccount(accountEntity: AccountEntity)


    fun getAllTransaction() : Flow<ResultState<List<Transaction>>>

    fun getAllAccounts(): Flow<ResultState<List<AccountEntity>>>





}