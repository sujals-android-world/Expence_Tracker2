package com.example.expencetracker2.data.tracsaction.repoImpl

import com.example.expencetracker2.data.tracsaction.local.dao.TransactionDao
import com.example.expencetracker2.data.tracsaction.local.mapper.toDomain
import com.example.expencetracker2.data.tracsaction.local.mapper.toEntity
import com.example.expencetracker2.domain.transaction.repository.TransactionRepo
import com.example.expencetracker2.domain.util.ResultState
import com.example.expencetracker2.domain.transaction.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class TransactionRepoImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepo {
    override suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertInTransaction(transaction.toEntity())
    }

    override fun getAllTransaction(): Flow<ResultState<List<Transaction>>> = flow {
        emit(ResultState.Loading)

        try {
            transactionDao.getAllTransactions().collect {
                emit(ResultState.Success(it.map { entity -> entity.toDomain() }))
            }
        } catch (e : Exception) {
            emit(ResultState.Error(e.localizedMessage!!))
        }
    }


}