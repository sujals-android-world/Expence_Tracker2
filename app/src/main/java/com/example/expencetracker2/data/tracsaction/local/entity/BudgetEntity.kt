package com.example.expencetracker2.data.tracsaction.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "budgets",)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val masterCategoryId: Long,
    val limitAmount: Double,
    val monthYear: String // Format: "MM-YYYY"
)
