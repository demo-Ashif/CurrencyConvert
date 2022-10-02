package com.demo.payseracurrency.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CurrencyEntity::class], version = 1, exportSchema = false)
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
                        "accounts_database")
                        .fallbackToDestructiveMigration()
                        .build()
                }
                else -> temp
            }
        }
    }
}