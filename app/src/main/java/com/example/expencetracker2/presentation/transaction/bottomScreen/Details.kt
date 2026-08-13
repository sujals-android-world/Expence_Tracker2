package com.example.expencetracker2.presentation.transaction.bottomScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.MASTER_CATEGORIES
import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.POPULAR_CATEGORIES
import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.REGULAR_CATEGORIES
import com.example.expencetracker2.domain.transaction.model.Transaction
import com.example.expencetracker2.presentation.transaction.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

private val LightCardGradientStart = Color(0xFFFFFFFF)
private val LightCardGradientEnd = Color(0xFFF1E4EC)
private val BorderGradientStart = Color(0xFFE2B0C2)
private val BorderGradientEnd = Color(0xFF9E6B7B)

private val PremiumDarkText = Color(0xFF1E1B18)

private val HighContrastIncomeGreen = Color(0xFF047857)
private val HighContrastIncomeBg = Color(0xFFECFDF5)
private val PremiumExpenseRed = Color(0xFFBE123C)

private val PremiumSurfaceColor = Color(0xFFF3F4F6)
private val PremiumBorderColor = Color(0xFFD1D5DB)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    searchQuery: String,
    selectedCategoryText: String,
    selectedMethodText: String,
    datePickerState: DatePickerState,
    showDatePicker: Boolean,
    transactionViewModel: TransactionViewModel,
    onSearchQueryChange: (String) -> Unit,
    onClearSearchClick: () -> Unit,
    onDateChipClick: () -> Unit,
    onDatePickerDismiss: () -> Unit,
    onDatePickerConfirm: (Long?) -> Unit,
    onDatePickerClearFilter: () -> Unit,
    onCategorySelect: (String) -> Unit,
    onMethodSelect: (String) -> Unit
) {
    // 1. Date Chip Label (Filter Clear hone par "Date" dikhega)
    val selectedDateDisplay = remember(datePickerState.selectedDateMillis) {
        val millis = datePickerState.selectedDateMillis
        if (millis != null) {
            val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            sdf.format(Date(millis))
        } else {
            "Date"
        }
    }

    val state by transactionViewModel.allTransaction.collectAsStateWithLifecycle()
    var isCategoryDropdownOpen by remember { mutableStateOf(false) }
    var isMethodDropdownOpen by remember { mutableStateOf(false) }

    // 2. Updated Filtered List (Exact Amount Match + UTC/Local Date Alignment)
    val filteredList = remember(
        state.success,
        datePickerState.selectedDateMillis,
        selectedCategoryText,
        selectedMethodText,
        searchQuery
    ) {
        val selMillis = datePickerState.selectedDateMillis
        val utcZone = TimeZone.getTimeZone("UTC")
        val defaultZone = TimeZone.getDefault()

        val cleanQuery = searchQuery.trim().replace(Regex("[+\\-₹$\\s]"), "")
        val rawQuery = searchQuery.trim()

        state.success.filter { item ->
            val resolvedCatName = resolveCategoryName(item)

            // Date Filter Fix (DatePicker UTC vs Local Timestamp)
            val matchesDate = if (selMillis != null) {
                val selectedCal = Calendar.getInstance(utcZone).apply { timeInMillis = selMillis }
                val itemCal = Calendar.getInstance(defaultZone).apply { timeInMillis = item.timestamp }

                itemCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                        itemCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH)
            } else true

            // Category Filter
            val matchesCategory = if (selectedCategoryText.contains("Category", ignoreCase = true) ||
                selectedCategoryText.contains("All", ignoreCase = true)
            ) {
                true
            } else {
                resolvedCatName.equals(selectedCategoryText, ignoreCase = true)
            }

            // Payment Method Filter
            val matchesMethod = if (selectedMethodText.contains("Method", ignoreCase = true) ||
                selectedMethodText.contains("All", ignoreCase = true)
            ) {
                true
            } else {
                item.paymentMode.equals(selectedMethodText, ignoreCase = true)
            }

            // Search Query Filter (Exact Amount Match)
            val matchesSearch = if (rawQuery.isBlank()) true else {
                val matchesNote = item.note?.contains(rawQuery, ignoreCase = true) == true
                val matchesMode = item.paymentMode.contains(rawQuery, ignoreCase = true)
                val matchesCat = resolvedCatName.contains(rawQuery, ignoreCase = true)

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

    // Dynamic Financial Calculations
    val totalIncome = remember(filteredList) {
        filteredList.filter { !it.isExpense }.sumOf { it.amount }
    }
    val totalExpense = remember(filteredList) {
        filteredList.filter { it.isExpense }.sumOf { it.amount }
    }

    val dailyAvgExpense = remember(filteredList, totalExpense) {
        val expenseTransactions = filteredList.filter { it.isExpense }
        if (expenseTransactions.isEmpty()) 0.0
        else {
            val uniqueDaysCount = expenseTransactions.map {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp))
            }.toSet().size
            if (uniqueDaysCount > 0) totalExpense / uniqueDaysCount else totalExpense
        }
    }

    val paymentMethods = remember { listOf("Method", "UPI", "Cash", "Bank", "Card") }
    val categoriesList = remember {
        listOf("Category", "Expense", "Income", "Food & Dining", "Travel & Commute", "Shopping & Lifestyle", "Bills & Utilities", "Health & Wellness", "Others")
    }

    val premiumCardGradient = remember { Brush.linearGradient(colors = listOf(LightCardGradientStart, LightCardGradientEnd)) }
    val borderGradient = remember { Brush.horizontalGradient(colors = listOf(BorderGradientStart, BorderGradientEnd)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumSurfaceColor)
    ) {
        // 1. SUMMARY CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp, top = 16.dp, bottom = 6.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, borderGradient),
            colors = CardDefaults.cardColors(containerColor = LightCardGradientStart),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(premiumCardGradient)
                    .padding(vertical = 18.dp, horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Income",
                            fontSize = 12.sp,
                            color = PremiumDarkText.copy(alpha = 0.65f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+ ₹${String.format(LocalLocale.current.platformLocale, "%.2f", totalIncome)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HighContrastIncomeGreen
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(42.dp)
                            .background(BorderGradientEnd.copy(alpha = 0.3f))
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total Expense",
                            fontSize = 12.sp,
                            color = PremiumDarkText.copy(alpha = 0.65f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "- ₹${String.format(LocalLocale.current.platformLocale, "%.2f", totalExpense)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PremiumExpenseRed
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Daily Avg: ₹${String.format(LocalLocale.current.platformLocale, "%.2f", dailyAvgExpense)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PremiumDarkText.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }

        // 2. SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search transactions...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearchClick) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            },
            textStyle = TextStyle(fontSize = 14.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .heightIn(min = 52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = PremiumBorderColor,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            singleLine = true
        )

        // 3. SINGLE ROW FILTERS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // DATE CHIP
            Row(
                modifier = Modifier
                    .weight(1.3f)
                    .clickable { onDateChipClick() }
                    .padding(vertical = 8.dp, horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = selectedDateDisplay,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // CATEGORY DROPDOWN
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    onClick = { isCategoryDropdownOpen = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, PremiumBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (selectedCategoryText.contains("All", ignoreCase = true)) "Category" else selectedCategoryText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = isCategoryDropdownOpen,
                    onDismissRequest = { isCategoryDropdownOpen = false }
                ) {
                    categoriesList.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, fontSize = 12.sp) },
                            onClick = {
                                onCategorySelect(cat)
                                isCategoryDropdownOpen = false
                            }
                        )
                    }
                }
            }

            // METHOD DROPDOWN
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    onClick = { isMethodDropdownOpen = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, PremiumBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (selectedMethodText.contains("All", ignoreCase = true)) "Method" else selectedMethodText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = isMethodDropdownOpen,
                    onDismissRequest = { isMethodDropdownOpen = false }
                ) {
                    paymentMethods.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method, fontSize = 12.sp) },
                            onClick = {
                                onMethodSelect(method)
                                isMethodDropdownOpen = false
                            }
                        )
                    }
                }
            }
        }

        // Calendar Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = onDatePickerDismiss,
                confirmButton = {
                    TextButton(onClick = { onDatePickerConfirm(datePickerState.selectedDateMillis) }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDatePickerClearFilter) {
                        Text("Clear")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // 4. TRANSACTION LIST
        when {
            state.loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            filteredList.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No transactions found",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredList,
                        key = { it.id }
                    ) { transaction ->
                        DistinctTransactionCard(transaction = transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun DistinctTransactionCard(transaction: Transaction) {
    val context = LocalContext.current

    val categoryName = remember(transaction) {
        resolveCategoryName(transaction)
    }

    // 🔹 FIX: अब HAMESHA Master Category का ही Icon लोड होगा
    val iconName = remember(transaction.masterCategoryId) {
        val masterCat = MASTER_CATEGORIES.firstOrNull { it.id == transaction.masterCategoryId }
        masterCat?.iconName ?: "ic_master_others"
    }

    val drawableId = remember(iconName) {
        if (iconName.isNotBlank()) {
            context.resources.getIdentifier(iconName, "drawable", context.packageName)
        } else 0
    }

    val timeText = remember(transaction.timestamp) {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(transaction.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!transaction.isExpense) HighContrastIncomeBg else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (!transaction.isExpense) HighContrastIncomeGreen.copy(alpha = 0.3f) else PremiumBorderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = if (!transaction.isExpense) HighContrastIncomeGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (drawableId != 0) {
                        Icon(
                            painter = painterResource(id = drawableId),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.Unspecified
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (!transaction.isExpense) HighContrastIncomeGreen else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = categoryName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!transaction.note.isNullOrBlank()) {
                    Text(
                        text = transaction.note,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = timeText,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${if (!transaction.isExpense) "+" else "-"} ₹${String.format(LocalLocale.current.platformLocale, "%.2f", transaction.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (!transaction.isExpense) HighContrastIncomeGreen else MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = if (!transaction.isExpense) HighContrastIncomeGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = transaction.paymentMode,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!transaction.isExpense) HighContrastIncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}