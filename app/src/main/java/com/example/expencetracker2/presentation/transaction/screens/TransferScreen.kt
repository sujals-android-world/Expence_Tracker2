package com.example.expencetracker2.presentation.transaction.screens

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Add
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
import com.example.expencetracker2.data.tracsaction.local.entity.AccountEntity
import com.example.expencetracker2.domain.transaction.model.Transaction
import com.example.expencetracker2.presentation.transaction.TransactionViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    viewModel: TransactionViewModel,
    onBackClick: () -> Unit,
    onNavigateToAddAccount: () -> Unit = {},
    onSaveTransfer: (
        transaction: Transaction,
        accountsToUpdate: List<Pair<Long, Double>>
    ) -> Unit
) {
    val account by viewModel.accountState.collectAsStateWithLifecycle()
    val accountsList = account.success

    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    // 🟢 EXCLUDE CREDIT CARDS: Transfer sirf Bank, Cash aur Wallets ke beech hi hoga
    val validTransferAccounts = remember(accountsList) {
        accountsList.filter { it.accountType != "CREDIT CARD" }
    }

    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    var fromAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var toAccount by remember { mutableStateOf<AccountEntity?>(null) }

    var fromDropdownExpanded by remember { mutableStateOf(false) }
    var toDropdownExpanded by remember { mutableStateOf(false) }

    // Dynamic initial selection (Non-Credit Card accounts strictly)
    LaunchedEffect(validTransferAccounts) {
        if (validTransferAccounts.size >= 2) {
            if (fromAccount == null || fromAccount?.accountType == "CREDIT CARD") {
                fromAccount = validTransferAccounts[0]
            }
            if (toAccount == null || toAccount?.accountType == "CREDIT CARD") {
                toAccount = validTransferAccounts.find { it.id != fromAccount?.id } ?: validTransferAccounts[1]
            }
        } else if (validTransferAccounts.isNotEmpty()) {
            if (fromAccount == null || fromAccount?.accountType == "CREDIT CARD") {
                fromAccount = validTransferAccounts.firstOrNull()
            }
        }
    }

    val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
    val hasSufficientBalance = fromAccount != null && fromAccount!!.balance >= parsedAmount
    val isValid = parsedAmount > 0 && fromAccount != null && toAccount != null && fromAccount!!.id != toAccount!!.id && hasSufficientBalance

    val handleSave = {
        if (isValid && fromAccount != null && toAccount != null) {
            val fromAcc = fromAccount!!
            val toAcc = toAccount!!

            val transferTransaction = Transaction(
                amount = parsedAmount,
                masterCategoryId = 0L, // 0 for Transfer
                timestamp = System.currentTimeMillis(),
                note = noteText.ifBlank { "Transfer: ${fromAcc.name} ➔ ${toAcc.name}" },
                paymentMode = "${fromAcc.accountType} -> ${toAcc.accountType}",

                isExpense = false,
                isIncome = false,
                isTransfer = true,
                isSpeedExpense = false
            )

            val updatesMap = mutableMapOf<Long, Double>()

            // 1️⃣ DEDUCT FROM "FROM ACCOUNT" + SIBLINGS & PARENT
            val fromMainBankId = fromAcc.linkedBankId ?: fromAcc.id

            updatesMap[fromAcc.id] = (updatesMap[fromAcc.id] ?: fromAcc.balance) - parsedAmount

            if (fromAcc.linkedBankId != null) {
                val parentBank = accountsList.find { it.id == fromMainBankId }
                if (parentBank != null) {
                    updatesMap[parentBank.id] = (updatesMap[parentBank.id] ?: parentBank.balance) - parsedAmount
                }
            }

            accountsList.filter { it.linkedBankId == fromMainBankId && it.id != fromAcc.id }.forEach { sibling ->
                updatesMap[sibling.id] = (updatesMap[sibling.id] ?: sibling.balance) - parsedAmount
            }

            // 2️⃣ ADD TO "TO ACCOUNT" + SIBLINGS & PARENT
            val toMainBankId = toAcc.linkedBankId ?: toAcc.id

            updatesMap[toAcc.id] = (updatesMap[toAcc.id] ?: toAcc.balance) + parsedAmount

            if (toAcc.linkedBankId != null) {
                val parentBank = accountsList.find { it.id == toMainBankId }
                if (parentBank != null) {
                    updatesMap[parentBank.id] = (updatesMap[parentBank.id] ?: parentBank.balance) + parsedAmount
                }
            }

            accountsList.filter { it.linkedBankId == toMainBankId && it.id != toAcc.id }.forEach { sibling ->
                updatesMap[sibling.id] = (updatesMap[sibling.id] ?: sibling.balance) + parsedAmount
            }

            val accountsToUpdateList = updatesMap.map { Pair(it.key, it.value) }

            onSaveTransfer(transferTransaction, accountsToUpdateList)

            Toast.makeText(context, "₹$parsedAmount Transferred Successfully!", Toast.LENGTH_SHORT).show()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Money", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                            contentDescription = "Save Transfer",
                            tint = if (isValid) Color(0xFF2563EB) else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF0F172A) else Color.White
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
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // 💰 1. Amount Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "₹",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        placeholder = { Text("0.00", fontSize = 28.sp, color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        ),
                        modifier = Modifier.width(180.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🟢 Minimum 2 Non-Credit Card Accounts Check
            if (validTransferAccounts.size < 2) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "You need at least 2 Savings/Bank/Cash accounts to transfer!",
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = onNavigateToAddAccount,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                // 📤 2. FROM ACCOUNT DROPDOWN (ONLY NON-CREDIT CARDS)
                Text("From (Source Account)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (isDark) Color.White else Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = fromDropdownExpanded,
                    onExpandedChange = { fromDropdownExpanded = !fromDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = fromAccount?.let { "${it.name} (${it.accountType}) - ₹${it.balance}" } ?: "Select Account",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = fromDropdownExpanded,
                        onDismissRequest = { fromDropdownExpanded = false }
                    ) {
                        validTransferAccounts.forEach { accountItem ->
                            DropdownMenuItem(
                                text = { Text("${accountItem.name} (${accountItem.accountType}) - ₹${accountItem.balance}") },
                                onClick = {
                                    fromAccount = accountItem
                                    fromDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                if (fromAccount != null && !hasSufficientBalance && parsedAmount > 0) {
                    Text(
                        text = "Insufficient balance in ${fromAccount?.name}!",
                        color = Color.Red,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 📥 3. TO ACCOUNT DROPDOWN (ONLY NON-CREDIT CARDS)
                Text("To (Destination Account)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (isDark) Color.White else Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = toDropdownExpanded,
                    onExpandedChange = { toDropdownExpanded = !toDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = toAccount?.let { "${it.name} (${it.accountType}) - ₹${it.balance}" } ?: "Select Account",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = toDropdownExpanded,
                        onDismissRequest = { toDropdownExpanded = false }
                    ) {
                        validTransferAccounts.forEach { accountItem ->
                            DropdownMenuItem(
                                text = { Text("${accountItem.name} (${accountItem.accountType}) - ₹${accountItem.balance}") },
                                onClick = {
                                    toAccount = accountItem
                                    toDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                if (fromAccount?.id == toAccount?.id && fromAccount != null) {
                    Text(
                        text = "From and To accounts cannot be the same!",
                        color = Color.Red,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📝 4. Optional Note Field
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Note / Reason (Optional)", fontSize = 12.sp) },
                placeholder = { Text("e.g. Self Transfer to Bank/Cash", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}