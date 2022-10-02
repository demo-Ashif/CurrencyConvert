package com.demo.payseracurrency.data.models

data class ConvertResponse(
    val date: String,
    val info: Info,
    val query: Query,
    val result: Double,
    val success: Boolean
)

data class Info(
    val rate: Double,
    val timestamp: Int
)
data class Query(
    val amount: Int,
    val from: String,
    val to: String
)

