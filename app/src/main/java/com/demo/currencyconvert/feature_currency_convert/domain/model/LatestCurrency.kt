package com.demo.currencyconvert.feature_currency_convert.domain.model

data class LatestCurrency(
    val base: String,
    val rates: Map<String,Double>,
)