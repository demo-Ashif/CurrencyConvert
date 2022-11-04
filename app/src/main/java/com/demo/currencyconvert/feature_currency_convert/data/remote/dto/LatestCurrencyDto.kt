package com.demo.currencyconvert.feature_currency_convert.data.remote.dto

data class LatestCurrencyDto(
    val base: String,
    val date: String,
    val rates: Map<String,Double>,
    val success: Boolean,
    val timestamp: Int
)