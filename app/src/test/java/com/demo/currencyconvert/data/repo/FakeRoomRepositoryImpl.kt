package com.demo.currencyconvert.data.repo

import com.demo.currencyconvert.feature_currency_convert.data.local.entity.CurrencyEntity
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.LatestRateEntity

class FakeRoomRepositoryImpl() : RoomRepository {
    override suspend fun insert(currencyEntity: CurrencyEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun checkCurrencyByKey(key: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun update(currencyEntity: CurrencyEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCurrencies(): List<CurrencyEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrencyByKey(key: String?): CurrencyEntity {
        TODO("Not yet implemented")
    }

    override suspend fun updateSum(key: String, balance: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMinus(key: String, balance: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun insertRate(rateEntity: LatestRateEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getLatestRateByKey(key: String?): LatestRateEntity {
        TODO("Not yet implemented")
    }


}