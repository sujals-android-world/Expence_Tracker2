package com.example.expencetracker2.data.tracsaction.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.expencetracker2.data.tracsaction.local.dao.BudgetDao

import com.example.expencetracker2.data.tracsaction.local.dao.TransactionDao
import com.example.expencetracker2.data.tracsaction.local.entity.AccountEntity
import com.example.expencetracker2.data.tracsaction.local.entity.BudgetEntity
import com.example.expencetracker2.data.tracsaction.local.entity.MasterCategoryEntity
import com.example.expencetracker2.data.tracsaction.local.entity.SubCategoryEntity
import com.example.expencetracker2.data.tracsaction.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, MasterCategoryEntity::class, SubCategoryEntity::class, BudgetEntity::class, AccountEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null
        fun getDatabase(context: Context) : ExpenseDatabase {
            return INSTANCE ?: synchronized(this ) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expense_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}