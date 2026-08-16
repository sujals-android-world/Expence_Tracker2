package com.example.expencetracker2.presentation.transaction.screens

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expencetracker2.data.tracsaction.local.entity.AccountEntity
import com.example.expencetracker2.presentation.transaction.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddAccountScreen(
    onBackClick: () -> Unit,
    transactionViewModel: TransactionViewModel,
    onSaveAccount: (
        name: String,
        icon: String,
        balance: Double,
        accountType: String,
        isPrimary: Boolean,
        linkedBankId: Long?,
        creditLimit: Double?,
        statementDate: Int?,
        dueDate: Int?
    ) -> Unit
) {

    val bankAccounts = transactionViewModel.accountState.collectAsStateWithLifecycle().value.success.filter { it.accountType == "BANK" }


    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var balanceText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("BANK") }
    var isPrimary by remember { mutableStateOf(false) }

    // 💳 Credit Card Specific Fields
    var creditLimitText by remember { mutableStateOf("") }
    var statementDateText by remember { mutableStateOf("") }
    var dueDateText by remember { mutableStateOf("") }

    var selectedBank by remember { mutableStateOf<AccountEntity?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val accountTypes = listOf("CASH", "BANK", "CREDIT CARD", "DEBIT CARD", "WALLET", "UPI")

    val isBankRequired = selectedType in listOf("UPI", "DEBIT CARD")
    val isBalanceLocked = selectedType in listOf("UPI", "DEBIT CARD")
    val isCreditCard = selectedType == "CREDIT CARD"

    // Reset values on account type change
    LaunchedEffect(selectedType) {
        name = ""
        balanceText = ""
        creditLimitText = ""
        statementDateText = ""
        dueDateText = ""
        isPrimary = false
        selectedBank = null
    }

    // Validation Check
    val parsedCreditLimit = creditLimitText.toDoubleOrNull()
    val parsedStatementDate = statementDateText.toIntOrNull()
    val parsedDueDate = dueDateText.toIntOrNull()

    val isCreditCardValid = !isCreditCard || (
            parsedCreditLimit != null && parsedCreditLimit > 0 &&
                    parsedStatementDate != null && parsedStatementDate in 1..31 &&
                    parsedDueDate != null && parsedDueDate in 1..31
            )

    val isValid = name.isNotBlank() && (!isBankRequired || selectedBank != null) && isCreditCardValid

    val handleSave = {
        val parsedBalance = balanceText.toDoubleOrNull() ?: 0.0
        if (isValid) {
            val icon = when (selectedType) {
                "CASH" -> "ic_cash"
                "BANK" -> "ic_bank"
                "CREDIT CARD" -> "ic_credit_card"
                "DEBIT CARD" -> "ic_debit_card"
                "WALLET" -> "ic_wallet"
                "UPI" -> "ic_upi"
                else -> "ic_wallet"
            }

            val linkedId = if (isBankRequired) selectedBank?.id else null
            val limit = if (isCreditCard) parsedCreditLimit else null
            val statement = if (isCreditCard) parsedStatementDate else null
            val due = if (isCreditCard) parsedDueDate else null

            onSaveAccount(
                name,
                icon,
                parsedBalance,
                selectedType,
                isPrimary,
                linkedId,
                limit,
                statement,
                due
            )
            Toast.makeText(context, "$selectedType Account Created!", Toast.LENGTH_SHORT).show()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Account", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { handleSave() },
                        enabled = isValid
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Account",
                            tint = if (isValid) Color(0xFF16A34A) else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8F9FA)
                )
            )
        },
        containerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {

            // 1. Account Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Account Name") },
                placeholder = { Text("e.g. HDFC Bank, Cash, SBI Card") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Initial Balance / Outstanding Amount Input
            OutlinedTextField(
                value = balanceText,
                onValueChange = { if (!isBalanceLocked) balanceText = it },
                readOnly = isBalanceLocked,
                enabled = !isBalanceLocked,
                label = {
                    Text(
                        when {
                            isBalanceLocked -> "Bank Balance (Auto-Filled)"
                            isCreditCard -> "Used Balance (Optional)"
                            else -> "Initial Balance"
                        }
                    )
                },
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                supportingText = if (isBalanceLocked && selectedBank != null) {
                    {
                        Text(
                            text = "Linked bank ka balance automatic fill ho gaya hai.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Account Type Selector Chips
            Text(
                text = "Account Type",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color.White else Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                accountTypes.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type) }
                    )
                }
            }

            // 💳 4. Credit Card Specific Inputs
            if (isCreditCard) {
                Spacer(modifier = Modifier.height(11.dp))

                Text(
                    text = "Credit Card Details",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Total Credit Limit Field
                OutlinedTextField(
                    value = creditLimitText,
                    onValueChange = { creditLimitText = it },
                    label = { Text("Total Credit Limit (₹)") },
                    placeholder = { Text("e.g. 100000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Statement Date & Payment Due Date Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = statementDateText,
                        onValueChange = { statementDateText = it },
                        label = { Text("Bill Date (1-31)") },
                        placeholder = { Text("e.g. 15") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = dueDateText,
                        onValueChange = { dueDateText = it },
                        label = { Text("Due Date (1-31)") },
                        placeholder = { Text("e.g. 5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 🏦 5. Link Bank Account (For UPI & Debit Card)
            if (isBankRequired) {
                Spacer(modifier = Modifier.height(16.dp))

                if (bankAccounts.isEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Bank Account Required",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "For $selectedType, a Bank Account is required.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(
                                onClick = { selectedType = "BANK" },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Add Bank ->", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Link to Bank Account",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedBank?.name ?: "Select Bank",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            bankAccounts.forEach { bank ->
                                DropdownMenuItem(
                                    text = { Text("${bank.name} (Balance: ₹${bank.balance})") },
                                    onClick = {
                                        selectedBank = bank
                                        if (isBalanceLocked) {
                                            balanceText = bank.balance.toString()
                                        }
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (selectedBank == null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Please select bank account first",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Set as Primary Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Set as Primary Account",
                    fontSize = 14.sp,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )
                Switch(
                    checked = isPrimary,
                    onCheckedChange = { isPrimary = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}