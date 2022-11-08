package com.demo.currencyconvert.feature_currency_convert.data.remote.dto

import com.demo.currencyconvert.feature_currency_convert.domain.model.Info

data class InfoDto(
    val rate: Double,
    val timestamp: Int
) {
    fun toInfo(): Info {
        return Info(
            rate = rate
        )
    }
}