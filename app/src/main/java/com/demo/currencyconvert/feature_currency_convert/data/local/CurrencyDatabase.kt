package com.demo.currencyconvert.feature_currency_convert.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.demo.currencyconvert.core.common.Constants
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.CurrencyEntity
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.LatestRateEntity

@Database(
    entities = [CurrencyEntity::class, LatestRateEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CurrencyDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDAO

    companion object {
        @Volatile
        private var instance: CurrencyDatabase? = null

        fun getInstance(context: Context): CurrencyDatabase {
            return when (val temp = instance) {
                null -> synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext, CurrencyDatabase::class.java,
                        Constants.DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                else -> temp
            }
        }
    }
}