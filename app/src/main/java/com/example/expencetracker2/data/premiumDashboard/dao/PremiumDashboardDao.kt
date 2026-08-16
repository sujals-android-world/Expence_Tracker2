package com.example.expencetracker2.data.premiumDashboard.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expencetracker2.data.premiumDashboard.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PremiumDashboardDao {
    @Query("SELECT * FROM `accounts`")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAccount(accountEntity: AccountEntity)

    @Query("update accounts set balance  = :balance where id = :id")
    suspend fun updateAmount(balance : Double, id : Long)

    @Query("UPDATE accounts SET isPrimary = 0 WHERE accountType = :accountType AND isPrimary = 1")
    suspend fun removePrimaryStatusForType(accountType: String)
}