package com.demo.payseracurrency.data.repo

import com.demo.payseracurrency.data.models.ConvertResponse
import com.demo.payseracurrency.data.models.LatestCurrencyResponse
import com.demo.payseracurrency.utils.Resource

interface CurrencyRepository {
    suspend fun getRates(): Resource<LatestCurrencyResponse>
    suspend fun getConverted(from: String, to: String, amount: Double): Resource<ConvertResponse>
}