package com.example.expencetracker2.presentation.premiumUserDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expencetracker2.data.premiumDashboard.entity.AccountEntity
import com.example.expencetracker2.domain.premiumuserDashboard.repository.PremiumUserDashboardRepo
import com.example.expencetracker2.domain.util.ResultState
import com.example.expencetracker2.presentation.transaction.InsertAccountState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PremiumUserDashboardViewmodel @Inject constructor(
    private val repository: PremiumUserDashboardRepo,
) : ViewModel() {


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
                        _accountState.value =
                            InsertAccountState(error = it.exception, loading = false)
                    }
                }
            }
        }
    }

    init {
        getAllAccounts()
    }


    fun insertAccount(name: String, icon :String, balance: Double, accountType: String, isPrimary: Boolean = false, linkedBankId: Long? = null,creditLimit : Double? = null, statementDate : Int? = null,dueDate : Int? = null)  {

        viewModelScope.launch(Dispatchers.IO) {
            if (isPrimary) {
                repository.removePrimaryStatusForType(accountType)
            }
            val account = AccountEntity(
                name = name,
                icon = icon,
                balance = balance,
                isPrimary = isPrimary,
                accountType = accountType,
                linkedBankId = linkedBankId,
                creditLimit = creditLimit,
                statementDate = statementDate,
                dueDate = dueDate
            )
            repository.insertAccount(account)
        }

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

