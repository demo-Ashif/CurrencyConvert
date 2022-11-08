package com.demo.currencyconvert.feature_currency_convert.data.remote.dto

import com.demo.currencyconvert.feature_currency_convert.data.local.entity.LatestRateEntity
import com.demo.currencyconvert.feature_currency_convert.domain.model.LatestCurrency

data class LatestCurrencyDto(
    val base: String,
    val date: String,
    val rates: Map<String, Double>,
    val success: Boolean,
    val timestamp: Int
)