package com.demo.currencyconvert.feature_currency_convert.domain.repository

import com.demo.currencyconvert.core.utils.Resource
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.CurrencyEntity
import com.demo.currencyconvert.feature_currency_convert.domain.model.LatestCurrency
import com.demo.currencyconvert.feature_currency_convert.domain.model.UserCurrency
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getAndInsertLatestCurrencyRates(): Flow<Resource<LatestCurrency>>

    fun getAllUserCurrencies(): Flow<Resource<List<UserCurrency>>>

    suspend fun addUserCurrency(userCurrency: UserCurrency)

    suspend fun checkUserCurrencyByName(key: String): Int

    suspend fun getUserCurrencyByName(key: String?): UserCurrency

    suspend fun updateSumUserCurrency(key: String, balance: Double)

    suspend fun updateMinusUserCurrency(key: String, balance: Double)

    suspend fun getLatestCurrencyRateByName(key: String?): LatestCurrency
}