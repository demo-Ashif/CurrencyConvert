package com.demo.currencyconvert.feature_currency_convert.data.repository

import com.demo.currencyconvert.feature_currency_convert.data.models.ConvertResponse
import com.demo.currencyconvert.feature_currency_convert.data.remote.dto.LatestCurrencyDto
import com.demo.currencyconvert.feature_currency_convert.data.remote.CurrencyApi
import com.demo.currencyconvert.core.utils.Resource
import com.demo.currencyconvert.feature_currency_convert.data.local.CurrencyDAO
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.CurrencyEntity
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.LatestRateEntity
import com.demo.currencyconvert.feature_currency_convert.domain.repository.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi,
    private val currencyDAO: CurrencyDAO
) : CurrencyRepository {

    override suspend fun getRates(): Resource<LatestCurrencyDto> {
        return try {
            val response = api.getRates("EUR")
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun getConverted(
        from: String,
        to: String,
        amount: Double
    ): Resource<ConvertResponse> {

        return try {
            val response = api.getConverted(from, to, amount)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }

    // user currency
    override suspend fun insert(currencyEntity: CurrencyEntity) {
        withContext(Dispatchers.IO) {
            currencyDAO.insert(currencyEntity)
        }
    }

    override suspend fun checkCurrencyByKey(key: String): Int {
        return currencyDAO.containsPrimaryKey(key)
    }

    override suspend fun update(currencyEntity: CurrencyEntity) {
        currencyDAO.update(currencyEntity)
    }

    override suspend fun getAllCurrencies(): List<CurrencyEntity> {
        return currencyDAO.getAllCurrencies()
    }

    override suspend fun getCurrencyByKey(key: String?): CurrencyEntity {
        return currencyDAO.getCurrencyByKey(key)
    }

    override suspend fun updateSum(key: String, balance: Double) {
        currencyDAO.updateSum(key, balance)
    }


    override suspend fun updateMinus(key: String, balance: Double) {
        currencyDAO.updateMinus(key, balance)
    }

    // latest rates
    override suspend fun insertRate(rateEntity: LatestRateEntity) {
        currencyDAO.insertRate(rateEntity)
    }

    override suspend fun getLatestRateByKey(key: String?): LatestRateEntity {
        return currencyDAO.getLatestRateByKey(key)
    }

}