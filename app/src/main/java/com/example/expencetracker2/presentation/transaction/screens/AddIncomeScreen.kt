package com.example.expencetracker2.presentation.transaction.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expencetracker2.data.tracsaction.local.entity.AccountEntity
import com.example.expencetracker2.domain.transaction.model.Transaction
import com.example.expencetracker2.presentation.transaction.TransactionViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// 🟢 9 Income Master Categories Data Class
data class IncomeCategoryItem(
    val id: Long,
    val name: String,
    val icon: ImageVector,
    val colorHex: Color
)

val INCOME_CATEGORIES = listOf(
    IncomeCategoryItem(9, "Salary & Wages", Icons.Default.Payments, Color(0xFF16A34A)),
    IncomeCategoryItem(10, "Business & Freelance", Icons.Default.BusinessCenter, Color(0xFF0D9488)),
    IncomeCategoryItem(11, "Investments & Interest", Icons.AutoMirrored.Filled.TrendingUp, Color(0xFF2563EB)),
    IncomeCategoryItem(12, "Pocket Money", Icons.Default.CardGiftcard, Color(0xFF9333EA)),
    IncomeCategoryItem(13, "Rental & Property", Icons.Default.HomeWork, Color(0xFFDB2777)),
    IncomeCategoryItem(14, "Cashback & Refunds", Icons.Default.Redeem, Color(0xFFD97706)),
    IncomeCategoryItem(15, "Gifts & Scholarships", Icons.Default.AccountBalance, Color(0xFF0891B2)),
    IncomeCategoryItem(16, "Selling Old Items", Icons.Default.Sell, Color(0xFF78350F)),
    IncomeCategoryItem(17, "Other Income", Icons.Default.MoreHoriz, Color(0xFF4B5563))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeScreen(
    viewModel: TransactionViewModel,
    onBackClick: () -> Unit,
    onNavigateToAddAccount: () -> Unit = {}, // 🟢 Account Add Screen par bhejne ke liye callback
    onSaveIncome: (
        transaction: Transaction,
        selectedAccountId: Long,
        selectedAccountNewBalance: Double,
        linkedBankId: Long?,
        linkedBankNewBalance: Double?,
        childAccountsToUpdate: List<Pair<Long, Double>>
    ) -> Unit
) {
    val account by viewModel.accountState.collectAsStateWithLifecycle()
    val accountsList = account.success

    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    // 🟢 EXCLUDE CREDIT CARDS: Income sirf Cash/Bank/Wallet accounts me hi aa sakti hai
    val validIncomeAccounts = remember(accountsList) {
        accountsList.filter { it.accountType != "CREDIT CARD" }
    }

    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(9L) }
    var selectedAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // 🟢 Dynamic list update ke sath sync rakho (Strictly non-credit card account hi select hoga)
    LaunchedEffect(validIncomeAccounts) {
        if (validIncomeAccounts.isNotEmpty() && (selectedAccount == null || selectedAccount?.accountType == "CREDIT CARD")) {
            selectedAccount = validIncomeAccounts.firstOrNull()
        }
    }

    val isValid = (amountText.toDoubleOrNull() ?: 0.0) > 0 && selectedCategoryId != null && selectedAccount != null

    val handleSave = {
        val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
        val currentAccount = selectedAccount

        if (isValid && currentAccount != null) {

            val incomeTransaction = Transaction(
                amount = parsedAmount,
                masterCategoryId = selectedCategoryId!!,
                timestamp = System.currentTimeMillis(),
                note = noteText,
                paymentMode = currentAccount.accountType,

                isExpense = false,
                isIncome = true,
                isTransfer = false,
                isSpeedExpense = false
            )

            val selectedAccountNewBalance = currentAccount.balance + parsedAmount

            val mainBankId = currentAccount.linkedBankId ?: currentAccount.id

            var linkedBankId: Long? = null
            var linkedBankNewBalance: Double? = null

            if (currentAccount.linkedBankId != null) {
                val parentBank = accountsList.find { it.id == mainBankId }
                if (parentBank != null) {
                    linkedBankId = parentBank.id
                    linkedBankNewBalance = parentBank.balance + parsedAmount
                }
            }

            val childAccountsToUpdate = mutableListOf<Pair<Long, Double>>()

            accountsList.filter { it.linkedBankId == mainBankId && it.id != currentAccount.id }.forEach { childAcc ->
                childAccountsToUpdate.add(Pair(childAcc.id, childAcc.balance + parsedAmount))
            }

            onSaveIncome(
                incomeTransaction,
                currentAccount.id,
                selectedAccountNewBalance,
                linkedBankId,
                linkedBankNewBalance,
                childAccountsToUpdate
            )

            Toast.makeText(context, "₹$parsedAmount Income Saved!", Toast.LENGTH_SHORT).show()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Income", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                            contentDescription = "Save Income",
                            tint = if (isValid) Color(0xFF16A34A) else Color.Gray
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
                        color = Color(0xFF16A34A)
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

            Spacer(modifier = Modifier.height(14.dp))

            // 🏦 2. Deposit To Account Dropdown / No Account View
            Text(
                text = "Deposit To (Target Account)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color.White else Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // 🟢 AGAR VALID NON-CREDIT CARD ACCOUNT LIST EMPTY HAI TO YE WIDGET DIKHAO
            if (validIncomeAccounts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEF2F2)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "No Savings Bank/Cash Account Found! Please add one first.",
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
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedAccount?.let { "${it.name} (${it.accountType}) - ₹${it.balance}" } ?: "Select Account",
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
                        // 🟢 SIRF VALID NON-CREDIT CARD ACCOUNTS HI SHOW HONGE
                        validIncomeAccounts.forEach { accountItem ->
                            DropdownMenuItem(
                                text = { Text("${accountItem.name} (${accountItem.accountType}) - ₹${accountItem.balance}") },
                                onClick = {
                                    selectedAccount = accountItem
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 🏷️ 3. Select Category Grid
            Text(
                text = "Income Category",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color.White else Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(INCOME_CATEGORIES) { category ->
                    val isSelected = selectedCategoryId == category.id
                    val itemBg = if (isSelected) category.colorHex.copy(alpha = 0.15f) else if (isDark) Color(0xFF1E293B) else Color.White
                    val borderColor = if (isSelected) category.colorHex else Color.Transparent

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable { selectedCategoryId = category.id },
                        color = itemBg
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = category.name,
                                tint = category.colorHex,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = category.name,
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 📝 4. Optional Note Field
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Note / Description (Optional)", fontSize = 12.sp) },
                placeholder = { Text("e.g. Monthly Salary Credited", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}