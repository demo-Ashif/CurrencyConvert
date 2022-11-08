package com.demo.currencyconvert.feature_currency_convert.domain.model

data class ConvertResponse(
    val info: Info,
    val query: Query,
    val result: Double,
)

