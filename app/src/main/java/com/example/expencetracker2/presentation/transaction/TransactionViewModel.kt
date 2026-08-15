package com.example.expencetracker2.presentation.transaction

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expencetracker2.data.tracsaction.local.entity.AccountEntity
import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.MASTER_CATEGORIES
import com.example.expencetracker2.domain.transaction.model.Transaction
import com.example.expencetracker2.domain.transaction.repository.TransactionRepo
import com.example.expencetracker2.domain.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs


@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepo,
) : ViewModel() {



    private val _allTransaction  = MutableStateFlow(InsertTransactionState())
    val allTransaction = _allTransaction.asStateFlow()


    fun getAllTransaction() {
        viewModelScope.launch {
            repository.getAllTransaction().collect {
                when (it) {
                    is ResultState.Loading -> {
                        _allTransaction.value = InsertTransactionState(loading = true)
                    }
                    is ResultState.Success -> {
                        _allTransaction.value = InsertTransactionState(success = it.data, loading = false)
                    }
                    is ResultState.Error -> {
                        _allTransaction.value = InsertTransactionState(error = it.exception, loading = false)
                    }
                }
            }

        }
    }

    private val _accountState = MutableStateFlow(InsertAccountState())
    val accountState = _accountState.asStateFlow()

    fun getAllAccounts() {
        viewModelScope.launch {
            repository.getAllAccounts().collect {
                when(it) {
                    is ResultState.Loading -> {
                        _accountState.value = InsertAccountState(loading = true)
                    }
                    is ResultState.Success -> {
                        _accountState.value = InsertAccountState(success = it.data, loading = false)
                    }
                    is ResultState.Error -> {
                        _accountState.value = InsertAccountState(error = it.exception, loading = false)
                    }
                }
            }
        }
    }

    init {
        getAllTransaction()
        getAllAccounts()
    }

    fun insertTransaction(
        amount: Double,
        masterCategoryId: Long,
        popularCategoryId: Long?,
        regularCategoryId: Long?,
        note: String?,
        paymentMode: String,
        isSynced: Boolean,
        isSpeedExpense: Boolean,
        isExpense: Boolean = true,
        isIncome : Boolean = false,
        isTransfer : Boolean = false
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                popularCategoryId = popularCategoryId,
                regularCategoryId = regularCategoryId,
                masterCategoryId = masterCategoryId,
                timestamp = System.currentTimeMillis(),
                note = note,
                paymentMode = paymentMode,
                isSynced = isSynced,
                isSpeedExpense = isSpeedExpense,
                isIncome = isIncome,
                isExpense = isExpense,
                isTransfer = isTransfer
            )
            repository.insertTransaction(transaction)
        }
    }

    fun insertAccount(name: String, icon :String, balance: Double, accountType: String, isPrimary: Boolean = false, linkedBankId: Long? = null)  {
        viewModelScope.launch {
            val account = AccountEntity(
                name = name,
                icon = icon,
                balance = balance,
                isPrimary = isPrimary,
                accountType = accountType,
                linkedBankId = linkedBankId
            )
            repository.insertAccount(account)
        }

    }


    private val _selectedSlice = MutableStateFlow<PieChartSlice?>(null)
    val selectedSlice: StateFlow<PieChartSlice?> = _selectedSlice.asStateFlow()

    // रैंडम कलर्स की लिस्ट (हर कैटेगरी के लिए)
    private val sliceColors = listOf(
        Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFD166),
        Color(0xFF06D6A0), Color(0xFF118AB2), Color(0xFF073B4C),
        Color(0xFF9C27B0), Color(0xFFFF9800)
    )

    // लिस्ट से पाई चार्ट का डेटा कैलकुलेट करने का फ़ंक्शन
    fun getPieChartSlices(transactions: List<Transaction>): List<PieChartSlice> {
        val expensesOnly = transactions.filter { it.amount != 0.0 }
        val totalExpense = expensesOnly.sumOf { abs(it.amount) }

        if (totalExpense == 0.0) return emptyList()

        // 1. masterCategoryId के हिसाब से ग्रुप करना
        val grouped = expensesOnly.groupBy { it.masterCategoryId }

        var currentStartAngle = 0f
        val slices = mutableListOf<PieChartSlice>()

        grouped.entries.forEachIndexed { index, entry ->
            val masterCatId = entry.key

            // 2. MASTER_CATEGORIES की लिस्ट में से असली नाम निकालना
            val realCategoryName = MASTER_CATEGORIES.firstOrNull { it.id == masterCatId }?.name ?: "Others"

            val categoryTotal = entry.value.sumOf { abs(it.amount) }
            val percentage = ((categoryTotal / totalExpense) * 100).toFloat()
            val sweepAngle = ((categoryTotal / totalExpense) * 360).toFloat()
            val color = sliceColors[index % sliceColors.size]

            slices.add(
                PieChartSlice(
                    categoryName = realCategoryName, // अब यहाँ असली नाम (जैसे Food, Travel आदि) आएगा
                    amount = categoryTotal,
                    percentage = percentage,
                    sweepAngle = sweepAngle,
                    startAngle = currentStartAngle,
                    color = color
                )
            )

            currentStartAngle += sweepAngle
        }

        return slices
    }

    fun selectSlice(slice: PieChartSlice?) {
        _selectedSlice.value = slice
    }


    fun updateAccountBalance(balance: Double,id : Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAmount(balance,id)
        }
    }

    fun updateMultipleAccounts(accountsToUpdate: List<Pair<Long, Double>>) {
        viewModelScope.launch(Dispatchers.IO) {
            accountsToUpdate.forEach { (accountId, newBalance) ->
                // तेरा पुराना update account function यहाँ कॉल होगा
                updateAccountBalance(
                    id = accountId,
                    balance = newBalance
                )
            }
        }
    }


}

data class PieChartSlice(
    val categoryName: String,
    val amount: Double,
    val percentage: Float,
    val sweepAngle: Float,
    val startAngle: Float,
    val color: Color
)
