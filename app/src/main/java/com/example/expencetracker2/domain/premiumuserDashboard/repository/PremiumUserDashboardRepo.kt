package com.example.expencetracker2.domain.premiumuserDashboard.repository

import com.example.expencetracker2.data.premiumDashboard.entity.AccountEntity
import com.example.expencetracker2.domain.util.ResultState
import kotlinx.coroutines.flow.Flow


interface PremiumUserDashboardRepo {


    suspend fun insertAccount(accountEntity: AccountEntity)


    fun getAllAccounts(): Flow<ResultState<List<AccountEntity>>>

    suspend fun updateAmount(balance : Double, id : Long)

    suspend fun removePrimaryStatusForType(accountType : String)





}