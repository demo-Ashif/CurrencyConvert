package com.demo.currencyconvert

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.demo.currencyconvert.data.room.CurrencyDAO
import com.demo.currencyconvert.data.room.CurrencyDatabase
import com.demo.currencyconvert.data.room.CurrencyEntity
import com.demo.currencyconvert.data.room.LatestRateEntity
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
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

    @Test
    fun insertUser() = runBlocking {
        val currency = CurrencyEntity(
            currencyName = "Euro",
            currencyBalance = 1000.00
        )
        currencyDao.insert(currency)
        val allCurrencies = currencyDao.getAllCurrencies()
        assertThat(allCurrencies).contains(currency)
    }

    @Test
    fun insertAndReadCurrencies() = runBlocking {
        val currency = CurrencyEntity(
            currencyName = "EUR",
            currencyBalance = 1000.0
        )
        currencyDao.insert(currency)
        val allCurrencies = currencyDao.getAllCurrencies()
        assertThat(allCurrencies).contains(currency)
    }

    @Test
    fun insertAndReadLatestRates() = runBlocking {
        val rate = LatestRateEntity(
            currencyName = "USD",
            conversionRate = 0.98
        )
        currencyDao.insertRate(rate)
        val latestRate = currencyDao.getLatestRateByKey(rate.currencyName)
        assertThat(latestRate.conversionRate).isEqualTo(rate.conversionRate)
    }

    @Test
    fun addUserCurrencyValue() = runBlocking {
        val currency = CurrencyEntity(
            currencyName = "EUR",
            currencyBalance = 1000.0
        )
        currencyDao.insert(currency)
        currencyDao.updateSum(currency.currencyName, 15.0)

        val userCurrency = currencyDao.getCurrencyByKey(currency.currencyName)
        assertThat(userCurrency.currencyBalance).isEqualTo(1015.0)
    }

    @Test
    fun minusUserCurrencyValue() = runBlocking {
        val currency = CurrencyEntity(
            currencyName = "EUR",
            currencyBalance = 1000.0
        )
        currencyDao.insert(currency)
        currencyDao.updateMinus(currency.currencyName, 15.0)

        val userCurrency = currencyDao.getCurrencyByKey(currency.currencyName)
        assertThat(userCurrency.currencyBalance).isEqualTo(985.0)
    }


}