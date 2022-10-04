package com.demo.payseracurrency.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: CurrencyEntity)

    @Update
    suspend fun update(currency: CurrencyEntity)

    @Delete
    suspend fun delete(currencies: CurrencyEntity)

    @Query("SELECT * FROM currency_table")
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT count(*) FROM currency_table WHERE currencyName = :key ")
    fun containsPrimaryKey(key: String): Flow<Int>

    @Query("UPDATE currency_table SET currencyBalance = currencyBalance + :addValue WHERE currencyName =:key")
    suspend fun updateSum(key : String, addValue: Long)

    @Query("UPDATE currency_table SET currencyBalance = currencyBalance - :addValue WHERE currencyName =:key")
    suspend fun updateMinus(key : String, addValue: Long)

    @Query("SELECT * FROM currency_table WHERE currencyName = :key")
    fun getCurrencyByKey(key: String?): Flow<CurrencyEntity>
}