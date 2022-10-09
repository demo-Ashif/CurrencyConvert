package com.demo.currencyconvert

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.demo.currencyconvert.data.room.CurrencyDAO
import com.demo.currencyconvert.data.room.CurrencyDatabase
import com.demo.currencyconvert.data.room.CurrencyEntity
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@SmallTest
class CurrencyDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var currencyDatabase: CurrencyDatabase
    private lateinit var currencyDao: CurrencyDAO

    @Before
    fun setUp() {
        hiltRule.inject()
        currencyDao = currencyDatabase.currencyDao()
    }

    @After
    fun tearDown() {
        currencyDatabase.close()
    }

    //@OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertUser() = runBlocking{
        val currency = CurrencyEntity(
            currencyName = "Euro",
            currencyBalance = 1000.00
        )
        currencyDao.insert(currency)
        val allCurrencies = currencyDao.getAllCurrencies()
        assertThat(allCurrencies).contains(currency)
    }


}