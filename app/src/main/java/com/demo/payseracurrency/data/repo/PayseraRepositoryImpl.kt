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
        return try {
            val response = api.getRates(base)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun getConverted(
        from: String,
        to: String,
        amount: Double
    ): Resource<ConvertResponse> {

        return try {
            val response = api.getConverted(from, to, amount)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }

}