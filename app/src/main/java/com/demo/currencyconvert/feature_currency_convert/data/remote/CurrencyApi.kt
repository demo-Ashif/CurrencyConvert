package com.demo.currencyconvert.feature_currency_convert.data.remote

import com.demo.currencyconvert.feature_currency_convert.data.remote.dto.ConvertResponseDto
import com.demo.currencyconvert.feature_currency_convert.data.remote.dto.LatestCurrencyDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    @GET("latest")
    suspend fun getRates(
        @Query("base") base: String
    ): LatestCurrencyDto

    @GET("convert")
    suspend fun getConverted(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): ConvertResponseDto

    companion object{
        const val BASE_URL = "https://api.apilayer.com/exchangerates_data/"
    }
}