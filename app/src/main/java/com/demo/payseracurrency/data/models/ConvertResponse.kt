package com.demo.payseracurrency.data.models

data class ConvertResponse(
    val date: String,
    val info: Info,
    val query: Query,
    val result: Double,
    val success: Boolean
)