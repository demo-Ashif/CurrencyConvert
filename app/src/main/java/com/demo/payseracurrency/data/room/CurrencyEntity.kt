package com.demo.payseracurrency.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.demo.payseracurrency.utils.Constants

@Entity(tableName = Constants.DB_TABLE_USER_CURRENCY)
data class CurrencyEntity(
    @PrimaryKey
    var currencyName: String,
    var currencyBalance: Double
)

