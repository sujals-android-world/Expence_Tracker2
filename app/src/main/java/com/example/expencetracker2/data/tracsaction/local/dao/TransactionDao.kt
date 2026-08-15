package com.example.expencetracker2.data.tracsaction.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expencetracker2.data.tracsaction.local.entity.AccountEntity
import com.example.expencetracker2.data.tracsaction.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInTransaction(transactionEntity: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(accountEntity: AccountEntity)

    @Query("SELECT * FROM `transaction` ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM `accounts`")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("update accounts set balance  = :balance where id = :id")
    fun updateAmount(balance : Double, id : Long)


}