package com.demo.payseracurrency.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currencies: CurrencyEntity)

    @Update
    fun update(currencies: CurrencyEntity)

    @Delete
    fun delete(currencies: CurrencyEntity)

    @Query("SELECT * FROM currency_table")
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    @Query("delete from currency_table")
    fun deleteCurrencies(){}

    @Query("SELECT count(*) FROM currency_table WHERE currencyName = :uid ")
    fun containsPrimaryKey(uid: String): Flow<Int>

    @Query("UPDATE currency_table SET currencyBalance = currencyBalance + :addValue WHERE currencyName =:key")
    suspend fun updateSum(key : String, addValue: Long)

    @Query("UPDATE currency_table SET currencyBalance = currencyBalance - :addValue WHERE currencyName =:key")
    suspend fun updateMinus(key : String, addValue: Double)

    @Query("SELECT * FROM currency_table WHERE currencyName = :key")
    fun getCurrencyById(key: String?): LiveData<CurrencyEntity>
}