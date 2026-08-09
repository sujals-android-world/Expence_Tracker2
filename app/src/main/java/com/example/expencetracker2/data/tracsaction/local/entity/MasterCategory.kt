package com.example.expencetracker2.data.tracsaction.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "master_category")
data class MasterCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val name  : String,
    val iconName  : String,
    val colorHex : String,
    val displayOrder : Int
)