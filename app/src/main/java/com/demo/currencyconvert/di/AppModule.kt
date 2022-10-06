package com.demo.currencyconvert.di

import android.content.Context
import android.content.SharedPreferences
import com.demo.currencyconvert.data.remote.CurrencyApi
import com.demo.currencyconvert.data.repo.CurrencyRepository
import com.demo.currencyconvert.data.repo.CurrencyRepositoryImpl
import com.demo.currencyconvert.data.repo.RoomRepository
import com.demo.currencyconvert.data.repo.RoomRepositoryImpl
import com.demo.currencyconvert.data.room.CurrencyDAO
import com.demo.currencyconvert.data.room.CurrencyDatabase
import com.demo.currencyconvert.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //private const val BASE_URL = "https://f83408ef-b9ed-416b-abef-ee1340c81499.mock.pstmn.io/"
    private const val BASE_URL = "https://api.apilayer.com/exchangerates_data/"
    private const val API_KEY = "h45AvFWdBdixiMYYlP240HbENoDeMaAZ"

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun providesInterceptor() = Interceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .addHeader("apikey", API_KEY)
            .build()

        return@Interceptor chain.proceed(request)
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        interceptor: Interceptor
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addNetworkInterceptor(httpLoggingInterceptor)
            .addInterceptor(interceptor)
            .build()

    @Provides
    @Singleton
    fun providesCurrencyApi(okHttpClient: OkHttpClient): CurrencyApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
            .create(CurrencyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyRepository(api: CurrencyApi): CurrencyRepository =
        CurrencyRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideCurrencyDatabase(@ApplicationContext appContext: Context): CurrencyDatabase =
        CurrencyDatabase.getInstance(appContext)

    @Provides
    @Singleton
    fun provideCurrencyDAO(currencyDatabase: CurrencyDatabase): CurrencyDAO =
        currencyDatabase.currencyDao()

    @Provides
    @Singleton
    fun provideRoomRepository(currencyDAO: CurrencyDAO): RoomRepository =
        RoomRepositoryImpl(currencyDAO)

    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("currency_pref", Context.MODE_PRIVATE)
    }


    @Provides
    @Singleton
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}