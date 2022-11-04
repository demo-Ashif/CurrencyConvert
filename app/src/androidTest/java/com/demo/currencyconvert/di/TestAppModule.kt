package com.demo.currencyconvert.di

import android.content.Context
import androidx.room.Room
import com.demo.currencyconvert.feature_currency_convert.data.local.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("test_currency_db")
    fun provideInMemoryDb(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(
            context, CurrencyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
}