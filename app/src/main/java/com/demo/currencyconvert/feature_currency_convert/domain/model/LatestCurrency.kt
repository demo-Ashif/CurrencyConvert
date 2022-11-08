package com.demo.currencyconvert.feature_currency_convert.domain.model

data class LatestCurrency(
    val currencyName: String,
    val conversionRate: Double,
)