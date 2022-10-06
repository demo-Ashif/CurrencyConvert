package com.demo.currencyconvert.data.remote

import com.demo.currencyconvert.data.models.ConvertResponse
import com.demo.currencyconvert.data.models.LatestCurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    @GET("latest")
    suspend fun getRates(
        @Query("base") base: String
    ): Response<LatestCurrencyResponse>

    @GET("convert")
    suspend fun getConverted(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): Response<ConvertResponse>
}