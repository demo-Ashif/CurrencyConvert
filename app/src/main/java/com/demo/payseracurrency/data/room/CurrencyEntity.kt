package com.demo.payseracurrency.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_table")
data class CurrencyEntity(
    @PrimaryKey
    var currencyName: String,
    var currencyBalance: Double
)

