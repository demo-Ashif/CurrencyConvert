package com.demo.payseracurrency.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.demo.payseracurrency.utils.Constants

@Entity(tableName = Constants.DB_TABLE_LATEST_RATES)
data class LatestRateEntity(
    @PrimaryKey
    var currencyName: String,
    var conversionRate: Double
)

