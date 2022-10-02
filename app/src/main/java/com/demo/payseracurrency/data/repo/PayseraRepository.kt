package com.demo.payseracurrency.data.repo

import com.demo.payseracurrency.data.models.ConvertResponse
import com.demo.payseracurrency.data.models.LatestCurrencyResponse
import com.demo.payseracurrency.utils.Resource

interface PayseraRepository {
    suspend fun getRates(base: String): Resource<LatestCurrencyResponse>
    suspend fun getConverted(base: String): Resource<ConvertResponse>
}