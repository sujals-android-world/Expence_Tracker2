package com.example.expencetracker2.data.tracsaction.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "sub_category",)
data class SubCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val masterCategoryId : Long,
    val name : String,
    val iconName : String,
    val colorHex: String,

    val isDefault :  Boolean = false,
    val isPopular : Boolean = false,
    val isCustom  : Boolean = false,

    val usageCount : Int = 0,
    val createdAt : Long = System.currentTimeMillis(),

    val affiliateUrl : String? = null
)
