package com.demo.currencyconvert.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.currencyconvert.data.repo.CurrencyRepository
import com.demo.currencyconvert.data.repo.RoomRepository
import com.demo.currencyconvert.data.room.CurrencyEntity
import com.demo.currencyconvert.data.room.LatestRateEntity
import com.demo.currencyconvert.utils.Constants
import com.demo.currencyconvert.utils.DispatcherProvider
import com.demo.currencyconvert.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val roomRepository: RoomRepository,
    private val dispatcher: DispatcherProvider,
    private val sharedPref: SharedPreferences
) : ViewModel() {

    private val TAG = CurrencyViewModel::class.simpleName

    sealed class CurrencyConversionEvent {
        object Loading : CurrencyConversionEvent()
        object Empty : CurrencyConversionEvent()
        class ConversionFailure(val errorText: String) : CurrencyConversionEvent()

        class CheckValidationSuccess(
            val from: String,
            val to: String,
            val fromAmount: Double
        ) : CurrencyConversionEvent()

        class ConversionSuccess(
            val successMsg: String,
            val from: String,
            val fromAmount: Double,
            val to: String,
            val convertedAmount: Double,
            val commission: Double
        ) : CurrencyConversionEvent()

    }

    sealed class RoomDataUpdateEvent {
        object InsertNeeded : RoomDataUpdateEvent()

        //object RoomDbUpdateDone : RoomDataUpdateEvent()
        object CheckComplete : RoomDataUpdateEvent()
        object Empty : RoomDataUpdateEvent()
    }

    private val _conversionEvent =
        MutableStateFlow<CurrencyConversionEvent>(CurrencyConversionEvent.Empty)
    val conversionEvent: StateFlow<CurrencyConversionEvent> = _conversionEvent

    private val _roomEvent = MutableStateFlow<RoomDataUpdateEvent>(RoomDataUpdateEvent.Empty)
    val roomEvent: StateFlow<RoomDataUpdateEvent> = _roomEvent

    private val _allCurrencies = MutableLiveData<List<CurrencyEntity>>()
    val allCurrencies: LiveData<List<CurrencyEntity>> = _allCurrencies//

    fun getLatestRates() {
        viewModelScope.launch(dispatcher.io) {

            when (val latestRatesResponse = repository.getRates()) {
                is Resource.Error -> _conversionEvent.value =
                    CurrencyConversionEvent.ConversionFailure(latestRatesResponse.message!!)
                is Resource.Success -> {
                    val latestRates = latestRatesResponse.data
                    if (latestRates != null) {
                        for (item in latestRates.rates) {
                            val insertRateEntity = LatestRateEntity(item.key, item.value)
                            insertRate(insertRateEntity)
                        }
                    }
                }
            }
        }
    }


    fun setInitialCurrency() {
        viewModelScope.launch(dispatcher.io) {
            val checkStatus = checkAvailableCurrencyByKey("EUR")
            if (checkStatus != 1) {
                insert(
                    CurrencyEntity(
                        Constants.DEFAULT_ACCOUNT,
                        Constants.ACCOUNT_BALANCE
                    )
                )
                _roomEvent.value = RoomDataUpdateEvent.InsertNeeded
            } else {
                _roomEvent.value = RoomDataUpdateEvent.CheckComplete
            }
        }
    }

    //val commission = ObservableDouble(0.0)


    fun startValidationCheck(from: String, to: String, amount: String) {
        val fromAmount = amount.toDoubleOrNull()

        if (from.isEmpty()) {
            _conversionEvent.value =
                CurrencyConversionEvent.ConversionFailure("Select correct currency!")
            return
        }

        if (from == to) {
            _conversionEvent.value =
                CurrencyConversionEvent.ConversionFailure("You are selling same currency!")
            return
        }

        if ((fromAmount == null) || (fromAmount <= 0)) {
            _conversionEvent.value =
                CurrencyConversionEvent.ConversionFailure("Amount is not valid!")
            return
        }

        //check user has the available balance in from currency
        viewModelScope.launch(dispatcher.io) {
            val currencyEntity = getCurrencyByKey(from)
            checkAvailableBalanceToSell(currencyEntity, from, to, fromAmount)
        }
    }

    private fun checkAvailableBalanceToSell(
        currencyEntity: CurrencyEntity,
        from: String,
        to: String,
        fromAmount: Double
    ) {

        if (currencyEntity.currencyBalance < fromAmount) {
            _conversionEvent.value =
                CurrencyConversionEvent.ConversionFailure("Insufficient balance!")
            return
        } else {
            _conversionEvent.value =
                CurrencyConversionEvent.CheckValidationSuccess(from, to, fromAmount)
            return
        }

    }

    fun convertCurrency(from: String, to: String, fromAmount: Double) {

        _conversionEvent.value = CurrencyConversionEvent.Loading

        viewModelScope.launch {
            try {
                val fromLatestRate = getCurrencyRateByKey(from)
                val toLatestRate = getCurrencyRateByKey(to)

                val convertedAmount = getTwoDecimalConvertedAmount(
                    fromLatestRate.conversionRate,
                    toLatestRate.conversionRate,
                    fromAmount
                )


                saveExchangeCount()

                val totalNumber = sharedPref.getInt(Constants.CONVERSION_COUNTER_KEY, 0)

                val commission =
                    if (totalNumber <= Constants.NUMBER_OF_EXCHANGE) 0.0 else fromAmount * (0.7 / 100.0)

                val conversionMsg = "+$convertedAmount"

                _conversionEvent.value = CurrencyConversionEvent.ConversionSuccess(
                    conversionMsg, from, fromAmount, to, convertedAmount, commission
                )

            } catch (e: Exception) {
                _conversionEvent.value =
                    CurrencyConversionEvent.ConversionFailure("Something went wrong. Conversion failed!")
            }
        }

    }

    private fun getTwoDecimalConvertedAmount(
        fromLatestRate: Double,
        toLatestRate: Double,
        fromAmount: Double
    ): Double {

        val convertedAmount = (toLatestRate / fromLatestRate) * fromAmount
        return round((convertedAmount * 100.0)) / 100.0
    }


    private fun saveExchangeCount() {
        with(sharedPref.edit()) {
            putInt(
                Constants.CONVERSION_COUNTER_KEY,
                sharedPref.getInt(Constants.CONVERSION_COUNTER_KEY, 0) + 1
            ).commit()
        }
    }

    private suspend fun checkAvailableCurrencyByKey(key: String): Int {
        return roomRepository.checkCurrencyByKey(key)
    }

    fun getAllCurrencies() {
        viewModelScope.launch(dispatcher.io) {
            try {
                val allCurrenciesFromDb = roomRepository.getAllCurrencies()
                _allCurrencies.postValue(allCurrenciesFromDb)
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }
    }

    fun roomDbUpdateData(
        from: String,
        fromAmount: Double,
        to: String,
        convertedAmount: Double,
        commission: Double
    ) {
        viewModelScope.launch(dispatcher.io) {
            val checkStatus = checkAvailableCurrencyByKey(to)
            val fromAmountWithCommission = (fromAmount + commission)


            if (checkStatus == 1) {
                updateMinus(from, fromAmountWithCommission)
                updateSum(to, convertedAmount)

            } else {
                updateMinus(from, fromAmountWithCommission)
                val currencyEntity =
                    CurrencyEntity(
                        currencyName = to,
                        currencyBalance = convertedAmount
                    )

                insert(currencyEntity)
            }
            getAllCurrencies()

        }


    }

    private suspend fun insert(currencyEntity: CurrencyEntity) {
        roomRepository.insert(currencyEntity)
    }

    private suspend fun updateSum(key: String, balance: Double) {
        roomRepository.updateSum(key, balance)
    }

    private suspend fun updateMinus(key: String, balance: Double) {
//        Log.d(TAG, "fromAmountWithCommission: $balance")
//        Log.d(TAG, "fromAmountWithCommission toLong: ${balance.toLong()}")
        roomRepository.updateMinus(key, balance)
    }

    private suspend fun getCurrencyByKey(key: String?): CurrencyEntity {
        return roomRepository.getCurrencyByKey(key)
    }

    private fun insertRate(rateEntity: LatestRateEntity) {
        viewModelScope.launch {
            roomRepository.insertRate(rateEntity)
        }
    }

    private suspend fun getCurrencyRateByKey(key: String?): LatestRateEntity {
        return roomRepository.getLatestRateByKey(key)
    }

}