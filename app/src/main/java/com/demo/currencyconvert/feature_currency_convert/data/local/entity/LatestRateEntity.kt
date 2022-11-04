package com.demo.currencyconvert.feature_currency_convert.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.demo.currencyconvert.core.common.Constants

@Entity(tableName = Constants.DB_TABLE_LATEST_RATES)
data class LatestRateEntity(
    @PrimaryKey
    var currencyName: String,
    var conversionRate: Double
)

