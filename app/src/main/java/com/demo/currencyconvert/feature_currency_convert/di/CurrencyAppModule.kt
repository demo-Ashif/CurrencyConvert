package com.demo.currencyconvert.feature_currency_convert.di

import android.content.Context
import android.content.SharedPreferences
import com.demo.currencyconvert.feature_currency_convert.data.remote.CurrencyApi
import com.demo.currencyconvert.feature_currency_convert.domain.repository.CurrencyRepository
import com.demo.currencyconvert.feature_currency_convert.data.repository.CurrencyRepositoryImpl
import com.demo.currencyconvert.feature_currency_convert.data.local.CurrencyDAO
import com.demo.currencyconvert.feature_currency_convert.data.local.CurrencyDatabase
import com.demo.currencyconvert.core.common.DispatcherProvider
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
object CurrencyAppModule {

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

//    @Provides
//    @Singleton
//    fun provideGetWordInfoUseCase(repository: WordInfoRepository): GetWordInfo {
//        return GetWordInfo(repository)
//    }

    @Provides
    @Singleton
    fun providesCurrencyApi(okHttpClient: OkHttpClient): CurrencyApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(CurrencyApi.BASE_URL)
            .client(okHttpClient)
            .build()
            .create(CurrencyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyRepository(api: CurrencyApi, currencyDAO: CurrencyDAO): CurrencyRepository =
        CurrencyRepositoryImpl(api, currencyDAO)

    @Provides
    @Singleton
    fun provideCurrencyDatabase(@ApplicationContext appContext: Context): CurrencyDatabase =
        CurrencyDatabase.getInstance(appContext)

    @Provides
    @Singleton
    fun provideCurrencyDAO(currencyDatabase: CurrencyDatabase): CurrencyDAO =
        currencyDatabase.currencyDao()


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