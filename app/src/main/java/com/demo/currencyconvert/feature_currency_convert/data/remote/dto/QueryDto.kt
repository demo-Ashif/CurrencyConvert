package com.demo.currencyconvert.feature_currency_convert.data.remote.dto

import com.demo.currencyconvert.feature_currency_convert.domain.model.Query

data class QueryDto(
    val amount: Int,
    val from: String,
    val to: String
) {
    fun toQuery(): Query {
        return Query(
            amount = amount,
            from = from,
            to = to
        )
    }
}