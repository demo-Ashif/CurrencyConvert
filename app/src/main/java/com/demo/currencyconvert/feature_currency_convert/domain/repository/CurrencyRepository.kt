package com.demo.currencyconvert.feature_currency_convert.domain.repository

import com.demo.currencyconvert.feature_currency_convert.data.models.ConvertResponse
import com.demo.currencyconvert.feature_currency_convert.data.remote.dto.LatestCurrencyDto
import com.demo.currencyconvert.core.utils.Resource
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.CurrencyEntity
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.LatestRateEntity

interface CurrencyRepository {
    suspend fun getRates(): Resource<LatestCurrencyDto>

    suspend fun getConverted(from: String, to: String, amount: Double): Resource<ConvertResponse>

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

    suspend fun getLatestRateByKey(key: String?): LatestRateEntity
}