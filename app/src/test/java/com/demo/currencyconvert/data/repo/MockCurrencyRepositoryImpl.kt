package com.demo.currencyconvert.data.repo

import com.demo.currencyconvert.data.models.ConvertResponse
import com.demo.currencyconvert.data.models.LatestCurrencyResponse
import com.demo.currencyconvert.data.remote.CurrencyApi
import com.demo.currencyconvert.utils.Resource
import javax.inject.Inject

class MockCurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi
) : CurrencyRepository {

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun getRates(): Resource<LatestCurrencyResponse> {

        val result = LatestCurrencyResponse(
            base = "EUR",
            date = "",
            rates = mapOf("USD" to 0.98, "AED" to 1.25),
            success = true,
            timestamp = 12345
        )

        return if (shouldReturnNetworkError) {
            Resource.Error("Error")
        } else {
            Resource.Success(result)
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