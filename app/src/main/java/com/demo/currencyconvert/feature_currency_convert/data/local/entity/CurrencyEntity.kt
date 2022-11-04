package com.demo.currencyconvert.feature_currency_convert.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.demo.currencyconvert.core.common.Constants

@Entity(tableName = Constants.DB_TABLE_USER_CURRENCY)
data class CurrencyEntity(
    @PrimaryKey
    var currencyName: String,
    var currencyBalance: Double
)

