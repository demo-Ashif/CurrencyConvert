package com.demo.payseracurrency.data.repo

import com.demo.payseracurrency.data.room.CurrencyEntity
import com.demo.payseracurrency.data.room.LatestRateEntity
import kotlinx.coroutines.flow.Flow

interface RoomRepository {

    // user currency
    suspend fun insert(currencyEntity: CurrencyEntity)

    suspend fun checkCurrencyByKey(key: String): Int

    suspend fun update(currencyEntity: CurrencyEntity)

    suspend fun getAllCurrencies(): List<CurrencyEntity>

    suspend fun getCurrencyByKey(key: String?): CurrencyEntity

    suspend fun updateSum(key: String, balance: Double)

    suspend fun updateMinus(key: String, balance: Double)

    //latest rate
    suspend fun insertRate(rateEntity: LatestRateEntity)

    suspend fun getLatestRateByKey(key: String?):LatestRateEntity
}