package com.example.expencetracker2.presentation.transaction.bottomScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expencetracker2.domain.transaction.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    transactionsList: List<Transaction>,
    searchQuery: String,
    selectedCategoryText: String,
    selectedMethodText: String,
    datePickerState: DatePickerState,
    showDatePicker: Boolean,

    // ⚡ 🔥 LAMBDAS LIST (Jo events niche se upar bhejenge)
    onSearchQueryChange: (String) -> Unit,       // Jab user search bar me type karega
    onClearSearchClick: () -> Unit,              // Jab search bar ka cross (X) button dabaega
    onDateChipClick: () -> Unit,                 // Jab calendar kholne ke liye click karega
    onDatePickerDismiss: () -> Unit,             // App calendar dialog band karne ke liye
    onDatePickerConfirm: (Long?) -> Unit,        // Calendar me ok click karke date select hone par
    onDatePickerClearFilter: () -> Unit,         // Calendar dialog ke andar clear filter click hone par
    onCategorySelect: (String) -> Unit,          // Dropdown me se 8 main categories select hone par
    onMethodSelect: (String) -> Unit,
) {
    val selectedDateDisplay = remember(datePickerState.selectedDateMillis) {
        val millis = datePickerState.selectedDateMillis
        if (millis != null) {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
        } else {
            "All Dates" // बाय-डिफ़ॉल्ट 'All Dates' दिखेगा
        }
    }

    // Dropdown Panels visibility switches
    var isCategoryDropdownOpen by remember { mutableStateOf(false) }
    var isMethodDropdownOpen by remember { mutableStateOf(false) }

    // Master mappings strings
    val masterCategories = remember {
        listOf("Food & Dining", "Travel & Commute", "Shopping & Lifestyle", "Bills & Utilities", "Health & Wellness", "Financials & Debt", "Entertainment", "Others")
    }
    val paymentMethods = remember { listOf("UPI", "Cash", "Card") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 🔍 1. PURE SEARCH BAR BLOCK
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search by note, amount or category...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearchClick) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            singleLine = true
        )

        // 🔄 2. HORIZONTAL SCROLLABLE FILTERS ROW 
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // FILTER A: Date Picker Chip Trigger 
            FilterChip(
                selected = selectedDateDisplay != "All Dates",
                onClick = onDateChipClick,
                label = { Text(selectedDateDisplay, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp)) },
                shape = RoundedCornerShape(10.dp)
            )

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

            // FILTER B: 8 Main Categories Dropdown Standard Chip
            Box {
                FilterChip(
                    selected = selectedCategoryText != "All Categories",
                    onClick = { isCategoryDropdownOpen = true },
                    label = { Text(selectedCategoryText, fontSize = 12.sp) },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    shape = RoundedCornerShape(10.dp)
                )
                DropdownMenu(
                    expanded = isCategoryDropdownOpen,
                    onDismissRequest = { isCategoryDropdownOpen = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Categories") },
                        onClick = { onCategorySelect("All Categories"); isCategoryDropdownOpen = false }
                    )
                    masterCategories.forEach { catName ->
                        DropdownMenuItem(
                            text = { Text(catName) },
                            onClick = { onCategorySelect(catName); isCategoryDropdownOpen = false }
                        )
                    }
                }
            }

            // FILTER C: 3 Payment Modes Dropdown Standard Chip
            Box {
                FilterChip(
                    selected = selectedMethodText != "All Methods",
                    onClick = { isMethodDropdownOpen = true },
                    label = { Text(selectedMethodText, fontSize = 12.sp) },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    shape = RoundedCornerShape(10.dp)
                )
                DropdownMenu(
                    expanded = isMethodDropdownOpen,
                    onDismissRequest = { isMethodDropdownOpen = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Methods") },
                        onClick = { onMethodSelect("All Methods"); isMethodDropdownOpen = false }
                    )
                    paymentMethods.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method) },
                            onClick = { onMethodSelect(method);isMethodDropdownOpen = false }
                        )
                    }
                }
            }
        }

        Text(
            text = "Showing results for filtered expenses",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 📜 3. RESPONDING TRANSACTIONS LIST
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Agar database flow khaki ho toh sample preview data load Karena automatic
            val finalDisplayList = transactionsList.ifEmpty { getSampleTransactionData() }

            // 🔥 Key mapped perfectly using transaction.id (String based Unique key system)
            items(finalDisplayList, key = { transaction -> transaction.id }) { transaction ->
                TransactionRowItem(transaction = transaction)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.LightGray.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
fun TransactionRowItem(transaction: Transaction) {
    // Dynamic mapping for Master category color tint configurations
    val (mainCategoryName, color) = remember(transaction.masterCategoryId) {
        when (transaction.masterCategoryId) {
            1L -> Pair("Food & Dining", Color(0xFFFF5722))
            2L -> Pair("Travel & Commute", Color(0xFF2196F3))
            3L -> Pair("Shopping & Lifestyle", Color(0xFFE91E63))
            4L -> Pair("Bills & Utilities", Color(0xFF9C27B0))
            5L -> Pair("Health & Wellness", Color(0xFF4CAF50))
            6L -> Pair("Financials & Debt", Color(0xFF00BCD4))
            7L -> Pair("Entertainment", Color(0xFFFFC107))
            else -> Pair("Others", Color(0xFF9E9E9E))
        }
    }

    // Dynamic Sub-category mapping logic simulator using your ID numbers system
    val subCategoryName = remember(transaction.subCategoryId) {
        when (transaction.subCategoryId) {
            101L -> "🍕 Fast Food"
            201L -> "🚕 Cab/Auto Rides"
            304L -> "🛍️ Clothes Buying"
            else -> null
        }
    }

    val timeText = remember(transaction.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(transaction.timestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // VERTICAL DATA COLUMN (Main Category, Sub-Category, Note, Time)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = mainCategoryName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // Dynamic Sub-Category check rendering
            if (subCategoryName != null) {
                Text(
                    text = subCategoryName,
                    fontSize = 12.sp,
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            }
            // Dynamic User Note check rendering
            if (!transaction.note.isNullOrBlank()) {
                Text(
                    text = "\"${transaction.note}\"",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            Text(text = timeText, fontSize = 11.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // RIGHT ALIGNED COLUMN (Method badge & Amount display)
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = transaction.paymentMode,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
            Text(
                text = "₹${String.format(LocalLocale.current.platformLocale, "%.2f", transaction.amount)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Preview mockup mapping for layout test rendering
fun getSampleTransactionData(): List<Transaction> {
    val currentTime = System.currentTimeMillis()
    return listOf(
        Transaction(id = "1", amount = 450.0, subCategoryId = 101L, masterCategoryId = 1L, timestamp = currentTime, note = "Treat with college hostel friends"),
        Transaction(id = "2", amount = 120.0, subCategoryId = 201L, masterCategoryId = 2L, timestamp = currentTime - 3600000, note = null),
        Transaction(id = "3", amount = 2499.0, subCategoryId = 304L, masterCategoryId = 3L, timestamp = currentTime - 7200000, note = "Bought brand new wedding shoes"),
        Transaction(id = "4", amount = 1500.0, subCategoryId = null, masterCategoryId = 4L, timestamp = currentTime - 86400000, note = "Home internet wifi bills payment")
    )
}
