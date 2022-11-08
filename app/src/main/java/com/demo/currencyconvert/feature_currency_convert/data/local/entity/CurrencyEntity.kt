package com.demo.currencyconvert.feature_currency_convert.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.demo.currencyconvert.core.common.Constants
import com.demo.currencyconvert.feature_currency_convert.domain.model.UserCurrency

@Entity(tableName = Constants.DB_TABLE_USER_CURRENCY)
data class CurrencyEntity(
    @PrimaryKey
    val currencyName: String,
    val currencyBalance: Double
) {
    fun toUserCurrency(): UserCurrency {
        return UserCurrency(
            currencyName = currencyName,
            currencyBalance = currencyBalance
        )
    }
}

