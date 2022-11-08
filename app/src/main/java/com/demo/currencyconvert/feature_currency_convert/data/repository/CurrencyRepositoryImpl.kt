package com.demo.currencyconvert.feature_currency_convert.data.repository

import com.demo.currencyconvert.feature_currency_convert.data.remote.CurrencyApi
import com.demo.currencyconvert.core.utils.Resource
import com.demo.currencyconvert.feature_currency_convert.data.local.CurrencyDAO
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.CurrencyEntity
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.LatestRateEntity
import com.demo.currencyconvert.feature_currency_convert.domain.model.LatestCurrency
import com.demo.currencyconvert.feature_currency_convert.domain.model.UserCurrency
import com.demo.currencyconvert.feature_currency_convert.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi,
    private val dao: CurrencyDAO
) : CurrencyRepository {

    override fun getAndInsertLatestCurrencyRates(): Flow<Resource<LatestCurrency>> = flow {
        emit(Resource.Loading())
        try {
            val remoteLatestRates = api.getRates("EUR")
            dao.deleteAllLatestRates()
            for (item in remoteLatestRates.rates) {
                val latestRateEntity = LatestRateEntity(item.key, item.value)
                dao.insertRate(latestRateEntity)
            }

        } catch (e: HttpException) {
            emit(Resource.Error(null, message = "Oops, something went wrong!"))
        } catch (e: IOException) {

        }
    }

    override fun getAllUserCurrencies(): Flow<Resource<List<UserCurrency>>> = flow {
        emit(Resource.Loading())
        val allUserCurrencies = dao.getAllCurrencies().map { it.toUserCurrency() }
        emit(Resource.Success(data = allUserCurrencies))
    }

    override suspend fun addUserCurrency(userCurrency: UserCurrency) {
        dao.insert(userCurrency.toCurrencyEntity())
    }

    override suspend fun checkUserCurrencyByName(key: String): Int {
        return dao.containsCurrency(key)
    }

    override suspend fun getUserCurrencyByName(key: String?): UserCurrency {
        return dao.getCurrencyByKey(key).toUserCurrency()
    }

    override suspend fun updateSumUserCurrency(key: String, balance: Double) {
        dao.updateSum(key, balance)
    }

    override suspend fun updateMinusUserCurrency(key: String, balance: Double) {
        dao.updateMinus(key, balance)
    }


    override suspend fun getLatestCurrencyRateByName(key: String?): LatestCurrency {
        return dao.getLatestRateByKey(key).toLatestCurrency()
    }

}