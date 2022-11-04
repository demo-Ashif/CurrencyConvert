package com.demo.currencyconvert.feature_currency_convert.data.remote

import com.demo.currencyconvert.feature_currency_convert.data.models.ConvertResponse
import com.demo.currencyconvert.feature_currency_convert.data.remote.dto.LatestCurrencyDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    @GET("latest")
    suspend fun getRates(
        @Query("base") base: String
    ): Response<LatestCurrencyDto>

    @GET("convert")
    suspend fun getConverted(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): Response<ConvertResponse>

    companion object{
        const val BASE_URL = "https://api.apilayer.com/exchangerates_data/"
    }
}