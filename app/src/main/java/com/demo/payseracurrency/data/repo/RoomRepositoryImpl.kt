package com.demo.payseracurrency.data.repo

import com.demo.payseracurrency.data.room.CurrencyDAO
import com.demo.payseracurrency.data.room.CurrencyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val currencyDAO: CurrencyDAO
) : RoomRepository {
    override suspend fun insert(currencyEntity: CurrencyEntity) {
        withContext(Dispatchers.IO) {
            currencyDAO.insert(currencyEntity)
        }
    }

    override fun checkCurrencyByKey(key: String): Flow<Int> {
        return currencyDAO.containsPrimaryKey(key)
    }

    override suspend fun update(currencyEntity: CurrencyEntity) {
        currencyDAO.update(currencyEntity)
    }

    override fun getAllCurrencies(): Flow<List<CurrencyEntity>> {
        return currencyDAO.getAllCurrencies()
    }

    override fun getCurrencyByKey(key: String?): Flow<CurrencyEntity> {
        return currencyDAO.getCurrencyByKey(key)
    }

    override suspend fun updateSum(key: String, balance: Long) {
        currencyDAO.updateSum(key, balance)
    }

    override suspend fun updateMinus(key: String, balance: Long) {
        currencyDAO.updateMinus(key, balance)
    }


}