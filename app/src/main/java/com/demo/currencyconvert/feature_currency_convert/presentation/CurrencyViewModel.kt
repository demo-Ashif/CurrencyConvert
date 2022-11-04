package com.demo.currencyconvert.feature_currency_convert.presentation

import android.content.SharedPreferences
import android.util.Log
import androidx.databinding.ObservableDouble
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.currencyconvert.feature_currency_convert.domain.repository.CurrencyRepository
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.CurrencyEntity
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.LatestRateEntity
import com.demo.currencyconvert.core.common.Constants
import com.demo.currencyconvert.core.common.DispatcherProvider
import com.demo.currencyconvert.core.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    @Inject lateinit var sharedPref: SharedPreferences
    @Inject lateinit var dispatcher: DispatcherProvider

    private val TAG = CurrencyViewModel::class.simpleName

    sealed class CurrencyConversionEvent {
        object Loading : CurrencyConversionEvent()
        object Empty : CurrencyConversionEvent()
        class ConversionFailure(val errorText: String) : CurrencyConversionEvent()

        class AddNewCurrencyFailure(val errorText: String) : CurrencyConversionEvent()
        class AddNewCurrencySuccess(val successMsg: String) : CurrencyConversionEvent()

        class ConversionSuccess(
            val successMsg: String,
            val from: String,
            val fromAmount: Double,
            val to: String,
            val convertedAmount: Double,
            val commissionMsg: String
        ) : CurrencyConversionEvent()

    }

    sealed class RoomDataUpdateEvent {
        object InsertNeeded : RoomDataUpdateEvent()
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

    fun getLatestRates() {
        viewModelScope.launch(dispatcher.io) {

            when (val latestRatesResponse = repository.getRates()) {
                is Resource.Loading -> {}
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
        checkAvailableBalanceAndCommissionAndConvert(from, fromAmount, to)
    }

    private fun checkAvailableBalanceAndCommissionAndConvert(
        from: String,
        fromAmount: Double,
        to: String
    ) {

        _conversionEvent.value = CurrencyConversionEvent.Loading

        viewModelScope.launch(dispatcher.io) {

            //check there is available fromCurrency amount including probable commission
            try {
                val currencyEntity = getCurrencyByKey(from)
                val fromLatestRate = getCurrencyRateByKey(from)
                val toLatestRate = getCurrencyRateByKey(to)

                val totalNumber = sharedPref.getInt(Constants.CONVERSION_COUNTER_KEY, 0)

                val probableConvertedAmount = getTwoDecimalConvertedAmount(
                    fromLatestRate.conversionRate,
                    toLatestRate.conversionRate,
                    fromAmount
                )

                val commissionMsg = applyCommissionRules(
                    totalNumber, from, fromAmount, toLatestRate.conversionRate,
                    probableConvertedAmount
                )

                val conversionMsg = "+$probableConvertedAmount"

                if (currencyEntity.currencyBalance < (fromAmount + currentCommission.get())) {
                   _conversionEvent.value =
                       CurrencyConversionEvent.ConversionFailure("Insufficient balance (incl. commission)!")

                } else {
                    // saving number of total exchange occurred by user
                    saveExchangeCount()

                    _conversionEvent.value =
                        CurrencyConversionEvent.ConversionSuccess(
                            conversionMsg,
                            from,
                            fromAmount,
                            to,
                            probableConvertedAmount,
                            commissionMsg
                        )
                }
            } catch (e: Exception) {
                _conversionEvent.value =
                    CurrencyConversionEvent.ConversionFailure("Something went wrong. Conversion failed!")
            }
        }

    }

    val currentCommission = ObservableDouble(0.0)


    private fun applyCommissionRules(
        totalConversionNumber: Int,
        from: String,
        fromAmount: Double,
        toLatestConversionRate: Double,
        convertedAmount: Double
    ): String {

        //Log.d(TAG, "Total no of conversion: $totalConversionNumber")

        currentCommission.set(0.0)

        if (totalConversionNumber <= Constants.NUMBER_OF_FREE_EXCHANGE) {
            return ""
        }

        // checking the amount is under 200 euro
        val amountInEuro = (convertedAmount / toLatestConversionRate)

        Log.d(TAG, "Amount in euro: $amountInEuro")

        if (amountInEuro < 200) {
            return "No commission up to 200 EUR!" // no commission on every 10th exchange
        }

        if (totalConversionNumber % Constants.FREE_EXCHANGE_DIVIDER == 0) {
            return "No commission on your every 10th exchange!" // no commission on every 10th exchange
        }

        val commissionAmount = fromAmount * (0.7 / 100.0)

        currentCommission.set(commissionAmount)

        return "Commission: $from ${
            String.format(
                "%.2f",
                commissionAmount
            )
        }"


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


    fun getAllCurrencies() {
        viewModelScope.launch(dispatcher.io) {
            try {
                val allCurrenciesFromDb = repository.getAllCurrencies()
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

    //adding new currency

    fun addNewCurrency(currencyName: String, currencyAmountStr: String) {
        val currencyAmount = currencyAmountStr.toDoubleOrNull()

        if (currencyAmount == null) {
            _conversionEvent.value =
                CurrencyConversionEvent.AddNewCurrencyFailure("Please give valid amount!")
            return
        }

        viewModelScope.launch(dispatcher.io) {
            try {
                val checkStatus = checkAvailableCurrencyByKey(currencyName)
                if (checkStatus != 1) {
                    insert(
                        CurrencyEntity(
                            currencyName,
                            currencyAmount
                        )
                    )
                } else {
                    updateSum(currencyName, currencyAmount)
                }

                getAllCurrencies()

                _conversionEvent.value =
                    CurrencyConversionEvent.AddNewCurrencySuccess("New $currencyName currency added!")

            } catch (e: Exception) {
                _conversionEvent.value =
                    CurrencyConversionEvent.AddNewCurrencyFailure("Add new currency failed!")
            }
        }
    }

    private suspend fun insert(currencyEntity: CurrencyEntity) {
        repository.insert(currencyEntity)
    }

    private suspend fun updateSum(key: String, balance: Double) {
        repository.updateSum(key, balance)
    }

    private suspend fun updateMinus(key: String, balance: Double) {
        repository.updateMinus(key, balance)
    }

    private suspend fun getCurrencyByKey(key: String?): CurrencyEntity {
        return repository.getCurrencyByKey(key)
    }

    private suspend fun checkAvailableCurrencyByKey(key: String): Int {
        return repository.checkCurrencyByKey(key)
    }

    private fun insertRate(rateEntity: LatestRateEntity) {
        viewModelScope.launch {
            repository.insertRate(rateEntity)
        }
    }

    private suspend fun getCurrencyRateByKey(key: String?): LatestRateEntity {
        return repository.getLatestRateByKey(key)
    }

}