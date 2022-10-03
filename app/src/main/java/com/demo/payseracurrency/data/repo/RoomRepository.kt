package com.demo.payseracurrency.data.repo

import com.demo.payseracurrency.data.room.CurrencyEntity
import kotlinx.coroutines.flow.Flow

interface RoomRepository {

    suspend fun insert(currencyEntity: CurrencyEntity)

    fun checkCurrencyByKey(key: String): Flow<Int>

    suspend fun update(currencyEntity: CurrencyEntity)

    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    fun getCurrencyByKey(key: String?): Flow<CurrencyEntity>

    suspend fun updateSum(key: String, balance: Double)

    suspend fun updateMinus(key: String, balance: Double)
}