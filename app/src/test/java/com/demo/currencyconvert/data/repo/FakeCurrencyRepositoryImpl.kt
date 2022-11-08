package com.demo.currencyconvert.data.repo

import com.demo.currencyconvert.core.utils.Resource
import com.demo.currencyconvert.feature_currency_convert.domain.model.LatestCurrency
import com.demo.currencyconvert.feature_currency_convert.domain.model.UserCurrency
import com.demo.currencyconvert.feature_currency_convert.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow

class FakeCurrencyRepositoryImpl () : CurrencyRepository {

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override fun getAndInsertLatestCurrencyRates(): Flow<Resource<LatestCurrency>> {
        TODO("Not yet implemented")
    }

    override fun getAllUserCurrencies(): Flow<Resource<List<UserCurrency>>> {
        TODO("Not yet implemented")
    }

    override suspend fun addUserCurrency(userCurrency: UserCurrency) {
        TODO("Not yet implemented")
    }

    override suspend fun checkUserCurrencyByName(key: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getUserCurrencyByName(key: String?): UserCurrency {
        TODO("Not yet implemented")
    }

    override suspend fun updateSumUserCurrency(key: String, balance: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMinusUserCurrency(key: String, balance: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun getLatestCurrencyRateByName(key: String?): LatestCurrency {
        TODO("Not yet implemented")
    }


}