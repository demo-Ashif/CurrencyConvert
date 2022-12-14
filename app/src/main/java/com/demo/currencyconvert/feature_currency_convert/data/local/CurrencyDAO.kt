package com.demo.currencyconvert.feature_currency_convert.data.local

import androidx.room.*
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.CurrencyEntity
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.LatestRateEntity

@Dao
interface CurrencyDAO {

    // user currency
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: CurrencyEntity)

    @Query("SELECT * FROM currency_table")
    suspend fun getAllCurrencies(): List<CurrencyEntity>

    @Query("UPDATE currency_table SET currencyBalance = currencyBalance + :addValue WHERE currencyName =:key")
    suspend fun updateSum(key : String, addValue: Double)

    @Query("UPDATE currency_table SET currencyBalance = currencyBalance - :addValue WHERE currencyName =:key")
    suspend fun updateMinus(key : String, addValue: Double)

    @Query("SELECT count(*) FROM currency_table WHERE currencyName = :key ")
    suspend fun containsCurrency(key: String): Int

    @Query("SELECT * FROM currency_table WHERE currencyName = :key")
    suspend fun getCurrencyByKey(key: String?): CurrencyEntity

    // latest currency rates
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rateEntity: LatestRateEntity)

    @Query("DELETE FROM latest_rates_table")
    fun deleteAllLatestRates()

    @Query("SELECT * FROM latest_rates_table WHERE currencyName = :key")
    suspend fun getLatestRateByKey(key: String?): LatestRateEntity

}