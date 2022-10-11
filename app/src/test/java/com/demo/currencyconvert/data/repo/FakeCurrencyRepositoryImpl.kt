package com.demo.currencyconvert.data.repo

import com.demo.currencyconvert.data.models.ConvertResponse
import com.demo.currencyconvert.data.models.LatestCurrencyResponse
import com.demo.currencyconvert.utils.Resource

class FakeCurrencyRepositoryImpl () : CurrencyRepository {

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
        TODO("No need to implement")
    }


}