package com.example.expencetracker2.domain.transaction.usecase

import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.MASTER_CATEGORIES
import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.POPULAR_CATEGORIES
import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.REGULAR_CATEGORIES
import com.example.expencetracker2.domain.transaction.model.Transaction
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.math.abs

class GetFilteredTransactionsUseCase @Inject constructor() {

    operator fun invoke(
        transactions: List<Transaction>,
        query: String,
        category: String,
        method: String,
        dateMillis: Long?
    ): List<Transaction> {
        val cleanQuery = query.trim().replace(Regex("[+\\-₹$\\s]"), "")
        val rawQuery = query.trim()

        // Material3 DatePicker gives millis in UTC Midnight
        val selectedYearMonth = dateMillis?.let { millis ->
            val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
            Pair(date.year, date.monthValue)
        }

        val systemZone = ZoneId.systemDefault()

        return transactions.filter { item ->
            val resolvedCategoryName = resolveCategoryName(item)

            // 1. DATE FILTER (Exact Year & Month Check via LocalDate)
            val matchesDate = if (selectedYearMonth != null) {
                val itemDate = Instant.ofEpochMilli(item.timestamp).atZone(systemZone).toLocalDate()
                itemDate.year == selectedYearMonth.first && itemDate.monthValue == selectedYearMonth.second
            } else true

            // 2. CATEGORY FILTER
            val matchesCategory = if (category.contains("Category", ignoreCase = true) ||
                category.contains("All", ignoreCase = true)
            ) {
                true
            } else {
                resolvedCategoryName.equals(category, ignoreCase = true)
            }

            // 3. PAYMENT METHOD FILTER
            val matchesMethod = if (method.contains("Method", ignoreCase = true) ||
                method.contains("All", ignoreCase = true)
            ) {
                true
            } else {
                item.paymentMode.equals(method, ignoreCase = true)
            }

            // 4. SEARCH QUERY FILTER (Exact Amount Match + Note/Mode/Cat)
            val matchesSearch = if (rawQuery.isBlank()) true else {
                val matchesNote = item.note?.contains(rawQuery, ignoreCase = true) == true
                val matchesMode = item.paymentMode.contains(rawQuery, ignoreCase = true)
                val matchesCat = resolvedCategoryName.contains(rawQuery, ignoreCase = true)

                val matchesAmount = if (cleanQuery.isNotEmpty()) {
                    val queryDouble = cleanQuery.toDoubleOrNull()
                    val queryLong = cleanQuery.toLongOrNull()
                    val absAmount = abs(item.amount)
                    val longAmount = absAmount.toLong()

                    when {
                        queryLong != null -> longAmount == queryLong || absAmount == queryLong.toDouble()
                        queryDouble != null -> absAmount == queryDouble
                        else -> false
                    }
                } else false

                matchesNote || matchesMode || matchesCat || matchesAmount
            }

            matchesDate && matchesCategory && matchesMethod && matchesSearch
        }
    }

    private fun resolveCategoryName(transaction: Transaction): String {
        val masterCat = MASTER_CATEGORIES.firstOrNull { it.id == transaction.masterCategoryId }

        return when {
            transaction.popularCategoryId != null -> {
                POPULAR_CATEGORIES.firstOrNull { it.id == transaction.popularCategoryId }?.name
                    ?: masterCat?.name ?: "Expense"
            }
            transaction.regularCategoryId != null -> {
                REGULAR_CATEGORIES.firstOrNull { it.id == transaction.regularCategoryId }?.name
                    ?: masterCat?.name ?: "Expense"
            }
            else -> {
                masterCat?.name ?: "Expense"
            }
        }
    }
}