package com.demo.currencyconvert.feature_currency_convert.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.demo.currencyconvert.core.common.Constants
import com.demo.currencyconvert.feature_currency_convert.domain.model.LatestCurrency
import com.demo.currencyconvert.feature_currency_convert.domain.model.UserCurrency

@Entity(tableName = Constants.DB_TABLE_LATEST_RATES)
data class LatestRateEntity(
    @PrimaryKey
    val currencyName: String,
    val conversionRate: Double
){
    fun toLatestCurrency(): LatestCurrency {
        return LatestCurrency(
            currencyName = currencyName,
            conversionRate = conversionRate
        )
    }
}

