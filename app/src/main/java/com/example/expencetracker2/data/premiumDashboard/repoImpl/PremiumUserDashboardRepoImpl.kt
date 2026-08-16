package com.example.expencetracker2.data.premiumDashboard.repoImpl

import com.example.expencetracker2.data.premiumDashboard.dao.PremiumDashboardDao
import com.example.expencetracker2.data.premiumDashboard.entity.AccountEntity
import com.example.expencetracker2.domain.premiumuserDashboard.repository.PremiumUserDashboardRepo
import com.example.expencetracker2.domain.util.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class PremiumUserDashboardRepoImpl @Inject constructor(
    private val premiumDashboardDao: PremiumDashboardDao
) : PremiumUserDashboardRepo {


    override suspend fun insertAccount(accountEntity: AccountEntity) {
        premiumDashboardDao.insertAccount(accountEntity)
    }


    override fun getAllAccounts(): Flow<ResultState<List<AccountEntity>>> = flow {
        emit(ResultState.Loading)

        try {
            premiumDashboardDao.getAllAccounts().collect {
                emit(ResultState.Success(it))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.localizedMessage ?: "An unknown error occurred"))
        }
    }

    override suspend fun updateAmount(balance: Double, id: Long) {
        premiumDashboardDao.updateAmount(balance,id)
    }

    override suspend fun removePrimaryStatusForType(accountType: String) {
        premiumDashboardDao.removePrimaryStatusForType(accountType = accountType)
    }


}