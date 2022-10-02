package com.demo.payseracurrency.data.repo

import androidx.lifecycle.LiveData
import com.demo.payseracurrency.data.room.CurrencyEntity
import kotlinx.coroutines.flow.Flow

interface RoomRepository {

    suspend fun insert(currencyEntity: CurrencyEntity)

    fun checkKey(key: String): Flow<Int>

    fun update(currencyEntity: CurrencyEntity)

    fun getAllAccounts(): Flow<List<CurrencyEntity>>

    fun getAccountsById(key: String?): LiveData<CurrencyEntity>

    suspend fun updateSum(key: String, balance: Long)

    suspend fun updateMinus(key: String, balance: Double)
}