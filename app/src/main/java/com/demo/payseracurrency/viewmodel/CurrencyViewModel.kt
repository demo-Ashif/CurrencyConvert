package com.demo.payseracurrency.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.payseracurrency.data.repo.CurrencyRepository
import com.demo.payseracurrency.data.repo.RoomRepository
import com.demo.payseracurrency.data.room.CurrencyEntity
import com.demo.payseracurrency.utils.Constants
import com.demo.payseracurrency.utils.DispatcherProvider
import com.demo.payseracurrency.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val roomRepository: RoomRepository,
    private val dispatcher: DispatcherProvider,
    private val sharedPref: SharedPreferences
) : ViewModel() {

    private val TAG = CurrencyViewModel::class.simpleName
    //Log.d(TAG,item.toString())

    sealed class CurrencyConversionEvent {
        class CheckValidationSuccess(val from: String, val to: String, val fromAmount: Double) :
            CurrencyConversionEvent()

        class ConversionFailure(val errorText: String) : CurrencyConversionEvent()
        object Loading : CurrencyConversionEvent()
        object Empty : CurrencyConversionEvent()
        class ConversionSuccess(
            val from: String,
            val to: String,
            val fromAmount: Double,
            val convertedAmount: Double
        ) : CurrencyConversionEvent()

        class PostConversionCalculationDone(val resultText: String) : CurrencyConversionEvent()

    }

    sealed class RoomDataUpdateEvent {
        object InsertNeeded : RoomDataUpdateEvent()
        object RoomDbUpdateDone : RoomDataUpdateEvent()
        object CheckComplete : RoomDataUpdateEvent()
        object Empty : RoomDataUpdateEvent()
    }

    private val _conversionEvent =
        MutableStateFlow<CurrencyConversionEvent>(CurrencyConversionEvent.Empty)
    val conversionEvent: StateFlow<CurrencyConversionEvent> = _conversionEvent

    private val _roomEvent = MutableStateFlow<RoomDataUpdateEvent>(RoomDataUpdateEvent.Empty)
    val roomEvent: StateFlow<RoomDataUpdateEvent> = _roomEvent

    private val _allCurrencies = MutableLiveData<List<CurrencyEntity>>()
    val allCurrencies: LiveData<List<CurrencyEntity>> = _allCurrencies

    fun setInitialCurrency() {

        viewModelScope.launch(dispatcher.io) {
            checkAvailableCurrencyByKey("EUR").cancellable().collect { checkStatus ->
                if (checkStatus != 1) {
                    roomRepository.insert(
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
    }

    fun getAllCurrencies() {
        viewModelScope.launch {
            roomRepository.getAllCurrencies().collect { item ->
                _allCurrencies.postValue(item)
            }
        }
    }

    /**
    1. check user given input is valid primarily
    2. check user have the currency and available balance
    3. convert the amount
    4. insert as new currency if not present or update existing currency balance
    5. update shared pref value for number of exchange
    6. deduct commission if applied and update all balances */

    fun startValidationCheck(from: String, to: String, amount: String) {
        val fromAmount = amount.toDoubleOrNull()

        if (from == to) {
            _conversionEvent.value =
                CurrencyConversionEvent.ConversionFailure("You are selling same currency!")
            return
        }

        if (fromAmount == null || fromAmount <= 0) {
            _conversionEvent.value =
                CurrencyConversionEvent.ConversionFailure("Amount is not valid!")
            return
        }

        //check user has the fromCurrency and available balance
        viewModelScope.launch(dispatcher.io) {
            checkAvailableCurrencyByKey(from).cancellable().collect { checkStatus ->
                if (checkStatus != 1) {
                    _conversionEvent.value =
                        CurrencyConversionEvent.ConversionFailure("Selected currency unavailable to sell!")
                } else {
                    getCurrencyByKey(from).cancellable().collect() { currencyEntity ->
                        checkAvailableBalanceToSell(currencyEntity, from, to, fromAmount)
                    }
                }
            }
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
        viewModelScope.launch(dispatcher.io) {
            _conversionEvent.value = CurrencyConversionEvent.Loading
            when (val convertResponse = repository.getConverted(from, to, fromAmount)) {
                is Resource.Error -> _conversionEvent.value =
                    CurrencyConversionEvent.ConversionFailure(convertResponse.message!!)
                is Resource.Success -> {
                    val currencyConvertData = convertResponse.data
                    if (currencyConvertData == null) {
                        _conversionEvent.value =
                            CurrencyConversionEvent.ConversionFailure("Unexpected error")
                    } else {
                        val convertedCurrencyAmount = getTwoDecimalPoint(currencyConvertData.result)
                        _conversionEvent.value = CurrencyConversionEvent.ConversionSuccess(
                            from, to, fromAmount, convertedCurrencyAmount
                        )
                    }
                }
            }
        }
    }


    fun postConversionCalculation(
        from: String,
        to: String,
        fromAmount: Double,
        convertedCurrencyAmount: Double
    ) {
        // update conversion number count
        saveExchangeCount()


        val totalNumber = sharedPref.getInt(Constants.CONVERSION_COUNTER_KEY, 0)
        var commission: Double = 0.0

        Log.d(TAG,"total conversion: $totalNumber")

        if (totalNumber <= Constants.NUMBER_OF_EXCHANGE) {
            _conversionEvent.value = CurrencyConversionEvent.PostConversionCalculationDone(
                "$fromAmount $from = $convertedCurrencyAmount $to"
            )
        } else {
            // commission applied
            commission = getTwoDecimalPoint((fromAmount * 0.7) / 100.0)
            _conversionEvent.value = CurrencyConversionEvent.PostConversionCalculationDone(
                "$fromAmount $from = $convertedCurrencyAmount $to - Commission: $commission $from"
            )
        }

        //update user currencies in room db
        //updateRoomDb(from, fromAmount, to, convertedCurrencyAmount, commission)
    }

    private fun updateRoomDb(
        from: String,
        fromAmount: Double,
        to: String,
        convertedCurrencyAmount: Double,
        commission: Double
    ) {

        // update fromCurrency
        updateMinus(from, (fromAmount + commission))

        val currencyEntity =
            CurrencyEntity(currencyName = to, currencyBalance = convertedCurrencyAmount)

        // check toCurrency available to insert/update
        viewModelScope.launch(dispatcher.io) {
            checkAvailableCurrencyByKey(to).cancellable().collect { checkStatus ->
                if (checkStatus != 1) {
                    //new value: insert toCurrency
                    insert(currencyEntity)
                } else {
                    //existing value: update toCurrency
                    updateSum(to, convertedCurrencyAmount)
                }

                _roomEvent.value = RoomDataUpdateEvent.RoomDbUpdateDone
            }
        }
    }

    private fun getTwoDecimalPoint(amount: Double): Double {
        return (amount * 100.0).roundToInt() / 100.0
    }


    private fun saveExchangeCount() {
        with(sharedPref.edit()) {
            putInt(
                Constants.CONVERSION_COUNTER_KEY,
                sharedPref.getInt(Constants.CONVERSION_COUNTER_KEY, 0) + 1
            ).commit()
        }
    }

    private fun checkAvailableCurrencyByKey(key: String): Flow<Int> {
        return roomRepository.checkCurrencyByKey(key)
    }


    private fun insert(currencyEntity: CurrencyEntity) {
        viewModelScope.launch {
            roomRepository.insert(currencyEntity)
        }
    }

    private fun updateSum(key: String, balance: Double) {
        viewModelScope.launch {
            roomRepository.updateSum(key, balance.toLong())
        }
    }

    private fun updateMinus(key: String, balance: Double) {
        viewModelScope.launch {
            roomRepository.updateMinus(key, balance.toLong())
        }
    }

    private fun getCurrencyByKey(key: String?): Flow<CurrencyEntity> {
        return roomRepository.getCurrencyByKey(key)
    }

}