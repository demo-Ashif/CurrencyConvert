package com.demo.currencyconvert.data.repo

import com.demo.currencyconvert.data.models.ConvertResponse
import com.demo.currencyconvert.data.models.LatestCurrencyResponse
import com.demo.currencyconvert.data.remote.CurrencyApi
import com.demo.currencyconvert.utils.Resource
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi
) : CurrencyRepository {

    override suspend fun getRates(): Resource<LatestCurrencyResponse> {
        return try {
            val response = api.getRates("EUR")
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