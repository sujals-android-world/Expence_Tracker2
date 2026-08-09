package com.example.expencetracker2.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expencetracker2.domain.transaction.repository.TransactionRepo
import com.example.expencetracker2.domain.util.ResultState
import com.example.expencetracker2.domain.transaction.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepo
) : ViewModel() {

    private val _allTransaction  = MutableStateFlow(InsertTransactionState())
    val allTransaction = _allTransaction.asStateFlow()



    init {
        getAllTransaction()
    }

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

    fun insertTransaction(amount: Double, subCategoryId: Long?, masterCategoryId  : Long, note: String?, paymentMode: String, isSynced: Boolean, isSpeedExpense: Boolean) {
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                subCategoryId = subCategoryId,
                masterCategoryId = masterCategoryId,
                timestamp = System.currentTimeMillis(),
                note = note,
                paymentMode = paymentMode,
                isSynced = isSynced,
                isSpeedExpense = isSpeedExpense
            )
            repository.insertTransaction(transaction)
        }
    }

}

