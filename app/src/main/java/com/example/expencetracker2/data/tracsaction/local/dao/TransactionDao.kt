package com.example.expencetracker2.data.tracsaction.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expencetracker2.data.tracsaction.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInTransaction(transactionEntity: TransactionEntity)

    @Query("select * from `transaction`")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
}