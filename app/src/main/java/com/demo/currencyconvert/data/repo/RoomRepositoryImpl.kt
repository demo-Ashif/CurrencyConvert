package com.demo.currencyconvert.data.repo

import com.demo.currencyconvert.data.room.CurrencyDAO
import com.demo.currencyconvert.data.room.CurrencyEntity
import com.demo.currencyconvert.data.room.LatestRateEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val currencyDAO: CurrencyDAO
) : RoomRepository {

    // user currency
    override suspend fun insert(currencyEntity: CurrencyEntity) {
        withContext(Dispatchers.IO) {
            currencyDAO.insert(currencyEntity)
        }
    }

    override suspend fun checkCurrencyByKey(key: String): Int {
        return currencyDAO.containsPrimaryKey(key)
    }

    override suspend fun update(currencyEntity: CurrencyEntity) {
        currencyDAO.update(currencyEntity)
    }

    override suspend fun getAllCurrencies(): List<CurrencyEntity> {
        return currencyDAO.getAllCurrencies()
    }

    override suspend fun getCurrencyByKey(key: String?): CurrencyEntity {
        return currencyDAO.getCurrencyByKey(key)
    }

    override suspend fun updateSum(key: String, balance: Double) {
        currencyDAO.updateSum(key, balance)
    }


    override suspend fun updateMinus(key: String, balance: Double) {
        currencyDAO.updateMinus(key, balance)
    }

    // latest rates
    override suspend fun insertRate(rateEntity: LatestRateEntity) {
        currencyDAO.insertRate(rateEntity)
    }

    override suspend fun getLatestRateByKey(key: String?): LatestRateEntity {
        return currencyDAO.getLatestRateByKey(key)
    }


}