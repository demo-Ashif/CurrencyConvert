package com.demo.payseracurrency.data.remote

import com.demo.payseracurrency.data.models.ConvertResponse
import com.demo.payseracurrency.data.models.LatestCurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PayseraApi {

    @GET("/latest")
    suspend fun getRates(
        @Query("base") base: String
    ): Response<LatestCurrencyResponse>

    @GET("/convert")
    suspend fun getConverted(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("from") amount: Double
    ): Response<ConvertResponse>
}