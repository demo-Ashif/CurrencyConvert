package com.demo.currencyconvert.feature_currency_convert.domain.model

data class Query(
    val amount: Int,
    val from: String,
    val to: String
)