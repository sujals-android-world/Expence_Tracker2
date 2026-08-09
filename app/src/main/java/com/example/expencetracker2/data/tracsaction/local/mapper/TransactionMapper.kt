package com.example.expencetracker2.data.tracsaction.local.mapper

import com.example.expencetracker2.data.tracsaction.local.entity.TransactionEntity
import com.example.expencetracker2.domain.transaction.model.Transaction


fun TransactionEntity.toDomain() : Transaction {
    return Transaction(
        id = id,
        amount = amount,
        subCategoryId = subCategoryId,
        masterCategoryId = masterCategoryId,
        timestamp = timestamp,
        note = note,
        paymentMode = paymentMode,
        isSynced = isSynced,
        isSpeedExpense = isSpeedExpense
    )
}

fun Transaction.toEntity() : TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        subCategoryId = subCategoryId,
        masterCategoryId = masterCategoryId,
        timestamp = timestamp,
        note = note,
        paymentMode = paymentMode,
        isSynced = isSynced,
        isSpeedExpense = isSpeedExpense
    )
}
