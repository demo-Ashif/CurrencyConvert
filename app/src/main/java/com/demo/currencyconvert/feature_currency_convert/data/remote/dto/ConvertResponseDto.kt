package com.demo.currencyconvert.feature_currency_convert.data.remote.dto

import com.demo.currencyconvert.feature_currency_convert.domain.model.ConvertResponse

data class ConvertResponseDto(
    val date: String,
    val info: InfoDto,
    val query: QueryDto,
    val result: Double,
    val success: Boolean
) {
    fun toConvertResponse(): ConvertResponse {
        return ConvertResponse(
            info = info.toInfo(),
            query = query.toQuery(),
            result = result
        )
    }
}

