package com.demo.currencyconvert.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.demo.currencyconvert.utils.Constants

@Entity(tableName = Constants.DB_TABLE_LATEST_RATES)
data class LatestRateEntity(
    @PrimaryKey
    var currencyName: String,
    var conversionRate: Double
)

