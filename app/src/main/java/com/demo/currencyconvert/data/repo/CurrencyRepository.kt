package com.demo.currencyconvert.data.repo

import com.demo.currencyconvert.data.models.ConvertResponse
import com.demo.currencyconvert.data.models.LatestCurrencyResponse
import com.demo.currencyconvert.utils.Resource

interface CurrencyRepository {
    suspend fun getRates(): Resource<LatestCurrencyResponse>
    suspend fun getConverted(from: String, to: String, amount: Double): Resource<ConvertResponse>
}