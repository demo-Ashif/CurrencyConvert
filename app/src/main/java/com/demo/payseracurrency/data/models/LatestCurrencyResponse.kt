package com.demo.payseracurrency.data.models

data class LatestCurrencyResponse(
    val base: String,
    val date: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int
)