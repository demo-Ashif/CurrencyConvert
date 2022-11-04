package com.demo.currencyconvert.data.repo

import com.demo.currencyconvert.feature_currency_convert.data.models.ConvertResponse
import com.demo.currencyconvert.feature_currency_convert.data.remote.dto.LatestCurrencyDto
import com.demo.currencyconvert.feature_currency_convert.domain.repository.CurrencyRepository
import com.demo.currencyconvert.core.utils.Resource

class FakeCurrencyRepositoryImpl () : CurrencyRepository {

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun getRates(): Resource<LatestCurrencyDto> {

        val result = LatestCurrencyDto(
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