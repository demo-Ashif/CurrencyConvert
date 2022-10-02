package com.demo.payseracurrency.data.repo

import com.demo.payseracurrency.data.models.ConvertResponse
import com.demo.payseracurrency.data.models.LatestCurrencyResponse
import com.demo.payseracurrency.data.remote.PayseraApi
import com.demo.payseracurrency.utils.Resource
import javax.inject.Inject

class PayseraRepositoryImpl @Inject constructor(
    private val api: PayseraApi
    ) : PayseraRepository {

    override suspend fun getRates(base: String): Resource<LatestCurrencyResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getConverted(base: String): Resource<ConvertResponse> {
        TODO("Not yet implemented")
    }

}