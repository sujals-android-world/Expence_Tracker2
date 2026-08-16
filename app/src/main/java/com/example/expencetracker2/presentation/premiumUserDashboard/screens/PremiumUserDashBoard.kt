package com.example.expencetracker2.presentation.premiumUserDashboard.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expencetracker2.data.premiumDashboard.entity.AccountEntity
import com.example.expencetracker2.domain.transaction.model.Transaction
import com.example.expencetracker2.presentation.premiumUserDashboard.PremiumUserDashboardViewmodel
import com.example.expencetracker2.presentation.transaction.TransactionViewModel

// 🔹 Helper Data Class & Function for 2-Row Column Pair
data class AccountColumnPair(
    val topAccount: AccountEntity?,
    val bottomAccount: AccountEntity?
)

fun groupAccountsFor2Rows(accounts: List<AccountEntity>): List<AccountColumnPair> {
    if (accounts.isEmpty()) return emptyList()
    val columns = mutableListOf<AccountColumnPair>()
    val totalBlocks = (accounts.size + 3) / 4

    for (b in 0 until totalBlocks) {
        val item0 = accounts.getOrNull(b * 4 + 0)
        val item1 = accounts.getOrNull(b * 4 + 1)
        val item2 = accounts.getOrNull(b * 4 + 2)
        val item3 = accounts.getOrNull(b * 4 + 3)

        if (item0 != null || item2 != null) {
            columns.add(AccountColumnPair(topAccount = item0, bottomAccount = item2))
        }
        if (item1 != null || item3 != null) {
            columns.add(AccountColumnPair(topAccount = item1, bottomAccount = item3))
        }
    }
    return columns
}

@Composable
fun PremiumUserDashBoard(
    premiumUserDashboardViewmodel: PremiumUserDashboardViewmodel,
    transactionViewModel: TransactionViewModel,
    onAccountClick: (AccountEntity) -> Unit,
    onAddIncomeClick: () -> Unit,      // 🟢 Add Income click
    onTransferClick: () -> Unit,       // 🔁 Transfer click
    onAddAccountClick: () -> Unit      // ➕ Add Account Screen Navigation Callback
) {
    val isDark = isSystemInDarkTheme()
    val accountsState by premiumUserDashboardViewmodel.accountState.collectAsStateWithLifecycle()
    val accountList = accountsState.success
    val list = transactionViewModel.allTransaction.collectAsStateWithLifecycle()
    val recentActivity = remember(list.value.success) {
        list.value.success
            .filter { it.isIncome || it.isTransfer }
            .sortedByDescending { it.timestamp }
            .take(5)
    }

    // 💰 Calculate Total Balance (Linked Bank accounts ko count nahi karega taaki double balance na aaye)
    val totalBalance = remember(accountList) {
        accountList
            .filter { it.linkedBankId == null } // Duplicate balance se bachne ke liye
            .sumOf { account ->
                if (account.accountType == "CREDIT CARD") {
                    -account.balance // 💳 Credit Card spent/due balance is a liability (minus)
                } else {
                    account.balance  // 💵 Cash and Bank accounts are assets (plus)
                }
            }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 🔹 1. Header with Total Balance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFACC15), // Soft Gold
                            Color(0x40FACC15)  // Translucent Gold
                        )
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2563EB), // Royal Medium Blue
                                    Color(0xFF4F46E5), // Rich Indigo
                                    Color(0xFF7C3AED)  // Vibrant Metallic Violet
                                )
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL BALANCE",
                                fontSize = 11.sp,
                                letterSpacing = 1.2.sp,
                                color = Color(0xFFFEF08A), // Light Soft Gold
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                color = Color(0x33FFFFFF), // Glassmorphic Badge
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "PREMIUM",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "₹${String.format("%.2f", totalBalance)}",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 🔹 2. Action Buttons (Add Income & Transfer)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 🟢 Add Income Button
                Button(
                    onClick = onAddIncomeClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Income",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Add Income", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // 🔁 Transfer Button
                Button(
                    onClick = onTransferClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Transfer",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Transfer", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // 🔹 3. My Accounts Header & Add Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Accounts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1E293B)
                )

                Surface(
                    onClick = onAddAccountClick, // 🟢 Ab direct Screen Navigation Lambda call hoga
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color(0xFF334155) else Color(0xFFEFF6FF)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Account",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
            }
        }

        // 🔹 4. Accounts 2-Row Grid
        item {
            Spacer(modifier = Modifier.height(10.dp))
            val groupedColumns = remember(accountList) {
                groupAccountsFor2Rows(accountList)
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(groupedColumns) { columnPair ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        columnPair.topAccount?.let { account ->
                            AccountCardItem(account = account, onClick = { onAccountClick(account) })
                        }
                        columnPair.bottomAccount?.let { account ->
                            AccountCardItem(account = account, onClick = { onAccountClick(account) })
                        }
                    }
                }
            }
        }

        // 🔹 5. Recent Activity Section Header
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Recent Activity",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF1E293B),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 🔹 6. Recent Activity List
        if (recentActivity.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent activity recorded yet.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(recentActivity) { activity ->
                RecentActivityTransactionItem(transaction = activity, isDark = isDark)
            }
        }
    }
}
// 🟢 Single Income Item Design
@Composable
fun RecentActivityTransactionItem(
    transaction: Transaction,
    isDark: Boolean
) {
    val isTransfer = transaction.isTransfer

    // 🎯 Transaction की masterCategoryId से Matching Category निकालना
    val matchedCategory = remember(transaction.masterCategoryId, isTransfer) {
        if (!isTransfer) {
            INCOME_CATEGORIES.find { it.id == transaction.masterCategoryId }
        } else null
    }

    val categoryName = if (isTransfer) "Account Transfer" else (matchedCategory?.name ?: "Income")

    val iconBgColor = if (isTransfer) Color(0xFFDBEAFE) else Color(0xFFDCFCE7)
    val iconTint = if (isTransfer) Color(0xFF2563EB) else (matchedCategory?.colorHex ?: Color(0xFF16A34A))

    // 🎨 dynamic icon set: Transfer है तो SwapHoriz, वरना Selected Category का Icon (Fallback: TrendingUp)
    val iconVector = if (isTransfer) {
        Icons.Default.SwapHoriz
    } else {
        matchedCategory?.icon ?: Icons.AutoMirrored.Filled.TrendingUp
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = if (isTransfer) "Transfer" else categoryName,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = categoryName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = transaction.paymentMode,
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    transaction.note?.let { noteText ->
                        if (noteText.isNotBlank()) {
                            Text(
                                text = noteText,
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isTransfer) "₹${transaction.amount}" else "+₹${transaction.amount}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isTransfer) (if (isDark) Color.White else Color(0xFF0F172A)) else Color(0xFF16A34A)
            )
        }
    }
}
@Composable
fun AccountCardItem(
    account: AccountEntity,
    onClick: () -> Unit
) {
    val bgBrush = if (account.isPrimary) {
        Brush.horizontalGradient(listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFFE0E7FF), Color(0xFFC7D2FE)))
    }

    val textColor = if (account.isPrimary) Color.White else Color(0xFF0F172A)
    val subTextColor = if (account.isPrimary) Color(0xFF93C5FD) else Color(0xFF475569)

    Box(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgBrush)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = account.accountType,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = subTextColor
                )
                if (account.isPrimary) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Primary",
                        tint = Color(0xFFFACC15),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = account.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "₹${account.balance}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1
            )
        }
    }
}