package com.demo.payseracurrency.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.demo.payseracurrency.data.repo.CurrencyRepository
import com.demo.payseracurrency.data.repo.RoomRepository
import com.demo.payseracurrency.data.room.CurrencyEntity
import com.demo.payseracurrency.utils.Constants
import com.demo.payseracurrency.utils.DispatcherProvider
import com.demo.payseracurrency.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    sealed class CurrencyEvent {
        class Success(val resultText: String) : CurrencyEvent()
        class Failure(val errorText: String) : CurrencyEvent()
        object Loading : CurrencyEvent()
        object Empty : CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

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
                }
                getAllCurrencies()
            }
        }
    }

    private fun getAllCurrencies() {
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

    fun startProcessToConvertCurrency(from: String, to: String, amount: String) {
        val fromAmount = amount.toDoubleOrNull()

        if (from == to) {
            _conversion.value = CurrencyEvent.Failure("You are converting same Currency!")
            return
        }

        if (fromAmount == null || fromAmount <= 0) {
            _conversion.value = CurrencyEvent.Failure("Amount is not valid!")
            return
        }

        //check user has the fromCurrency and available balance
        viewModelScope.launch(dispatcher.io) {
            checkAvailableCurrencyByKey(from).cancellable().collect { checkStatus ->
                if (checkStatus != 1) {
                    _conversion.value =
                        CurrencyEvent.Failure("You don't have any balance in selected currency!")
                } else {
                    getCurrencyByKey(from).cancellable().collect() { currencyEntity ->
                        checkAvailableBalanceAndConvert(currencyEntity, from, to, fromAmount)
                    }
                }
            }
        }

    }

    private fun checkAvailableBalanceAndConvert(
        currencyEntity: CurrencyEntity,
        from: String,
        to: String,
        fromAmount: Double
    ) {

        //check available balance first
        if (currencyEntity.currencyBalance < fromAmount) {
            _conversion.value =
                CurrencyEvent.Failure("Insufficient balance!")
            return
        }

        viewModelScope.launch(dispatcher.io) {
            _conversion.value = CurrencyEvent.Loading
            when (val convertResponse = repository.getConverted(from, to, fromAmount)) {
                is Resource.Error -> _conversion.value =
                    CurrencyEvent.Failure(convertResponse.message!!)
                is Resource.Success -> {
                    val currencyConvertData = convertResponse.data
                    if (currencyConvertData == null) {
                        _conversion.value = CurrencyEvent.Failure("Unexpected error")
                    } else {
                        val convertedCurrencyAmount = getTwoDecimalPoint(currencyConvertData.result)

                        //post conversion calculation
                        calculateToUpdateNecessaryData(
                            from,
                            to,
                            fromAmount,
                            convertedCurrencyAmount
                        )
                    }
                }
            }
        }
    }

    private fun calculateToUpdateNecessaryData(
        from: String,
        to: String,
        fromAmount: Double,
        convertedCurrencyAmount: Double
    ) {
        // update conversion number count
        saveExchangeCount()

        val currencyEntity =
            CurrencyEntity(currencyName = to, currencyBalance = convertedCurrencyAmount)

        // check toCurrency available to insert/update
        viewModelScope.launch(dispatcher.io) {
            checkAvailableCurrencyByKey(to).cancellable().collect { checkStatus ->
                if (checkStatus != 1) {
                    //insert
                    insert(currencyEntity)
                } else {
                    //update toCurrency
                    updateSum(to, convertedCurrencyAmount)

                    //minus amount and commission amount if applied
                    updateFromCurrencyAndApplyCommissionRules(
                        from,
                        fromAmount,
                        to,
                        convertedCurrencyAmount
                    )

                    //get all currencies to show in listview
                    getAllCurrencies()
                }
            }
        }

    }

    private fun updateFromCurrencyAndApplyCommissionRules(
        from: String,
        fromAmount: Double,
        to: String,
        convertedCurrencyAmount: Double
    ) {
        val totalNumber = sharedPref.getInt(Constants.NO_OF_CONVERSION_KEY, 0)
        val commission: Double

        if (totalNumber <= Constants.NUMBER_OF_EXCHANGE) {
            updateMinus(from, fromAmount)
            //conversion success message
            _conversion.value = CurrencyEvent.Success(
                "$fromAmount $from = $convertedCurrencyAmount $to"
            )
        } else {
            commission = getTwoDecimalPoint((fromAmount * 0.7) / 100.0)
            //conversion success message
            _conversion.value = CurrencyEvent.Success(
                "$fromAmount $from = $convertedCurrencyAmount $to - Commission: $commission $from"
            )
        }

    }

    private fun getTwoDecimalPoint(amount: Double): Double {
        return (amount * 100.0).roundToInt() / 100.0
    }


    private fun saveExchangeCount() {
        with(sharedPref.edit()) {
            putInt(
                Constants.CONVERSION_COUNTER_KEY,
                sharedPref.getInt(Constants.NO_OF_CONVERSION_KEY, 0) + 1
            ).commit()
        }
    }


    private fun insert(currencyEntity: CurrencyEntity) {
        viewModelScope.launch {
            roomRepository.insert(currencyEntity)
        }
    }


    private fun checkAvailableCurrencyByKey(key: String): Flow<Int> {
        return roomRepository.checkCurrencyByKey(key)
    }


    private fun updateSum(key: String, balance: Double) {
        viewModelScope.launch {
            roomRepository.updateSum(key, balance)
        }
    }

    private fun updateMinus(key: String, balance: Double) {
        viewModelScope.launch {
            roomRepository.updateMinus(key, balance)
        }
    }

    private fun getCurrencyByKey(key: String?): Flow<CurrencyEntity> {
        return roomRepository.getCurrencyByKey(key)
    }

}