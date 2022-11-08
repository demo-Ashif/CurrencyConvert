package com.demo.currencyconvert.feature_currency_convert.domain.model

import com.demo.currencyconvert.feature_currency_convert.data.local.entity.CurrencyEntity


data class UserCurrency(
    val currencyName: String,
    val currencyBalance: Double
) {
    fun toCurrencyEntity(): CurrencyEntity {
        return CurrencyEntity(
            currencyName = currencyName,
            currencyBalance = currencyBalance
        )
    }
}