package com.demo.currencyconvert.feature_currency_convert.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.currencyconvert.core.common.Constants
import com.demo.currencyconvert.core.common.DispatcherProvider
import com.demo.currencyconvert.core.utils.Resource
import com.demo.currencyconvert.feature_currency_convert.domain.model.UserCurrency
import com.demo.currencyconvert.feature_currency_convert.domain.repository.CurrencyRepository
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

    //    @Inject
//    lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var dispatcher: DispatcherProvider

    private val TAG = CurrencyViewModel::class.simpleName

    sealed class CurrencyUiEvent {
        object Loading : CurrencyUiEvent()
        object Empty : CurrencyUiEvent()

        class AddNewCurrencyFailure(val errorText: String) : CurrencyUiEvent()
        class AddNewCurrencySuccess(val successMsg: String) : CurrencyUiEvent()

        class ConversionFailure(val errorText: String) : CurrencyUiEvent()
        class ConversionSuccess(
            val from: String,
            val fromAmount: Double,
            val to: String,
            val convertedAmount: Double
        ) : CurrencyUiEvent()

    }

    private val _conversionEvent =
        MutableStateFlow<CurrencyUiEvent>(CurrencyUiEvent.Empty)
    val conversionEvent: StateFlow<CurrencyUiEvent> = _conversionEvent

    private val _allUserCurrencies = MutableLiveData<List<UserCurrency>>()
    val allUserCurrencies: LiveData<List<UserCurrency>> = _allUserCurrencies

    fun getLatestRates() {
        viewModelScope.launch(dispatcher.io) {

            repository.getAllUserCurrencies().collect { result ->
                when (result) {
                    is Resource.Error -> _conversionEvent.value =
                        CurrencyUiEvent.ConversionFailure(result.message!!)
                    is Resource.Loading -> TODO()
                    is Resource.Success -> TODO()
                }
            }
        }
    }

    fun setInitialCurrency() {
        viewModelScope.launch(dispatcher.io) {
            val checkStatus = repository.checkUserCurrencyByName("EUR")
            if (checkStatus != 1) {
                repository.addUserCurrency(
                    UserCurrency(
                        Constants.DEFAULT_ACCOUNT,
                        Constants.ACCOUNT_BALANCE
                    )
                )
                getAllUserCurrencies()
            }

        }
    }


    fun startValidationCheck(from: String, to: String, amount: String) {
        val fromAmount = amount.toDoubleOrNull()

        if (from.isEmpty()) {
            _conversionEvent.value =
                CurrencyUiEvent.ConversionFailure("Select correct currency!")
            return
        }

        if (from == to) {
            _conversionEvent.value =
                CurrencyUiEvent.ConversionFailure("You are selling same currency!")
            return
        }

        if ((fromAmount == null) || (fromAmount <= 0)) {
            _conversionEvent.value =
                CurrencyUiEvent.ConversionFailure("Amount is not valid!")
            return
        }

        //check user has the available balance in from currency
        checkAvailableBalanceAndConvert(from, fromAmount, to)
    }

    private fun checkAvailableBalanceAndConvert(
        from: String,
        fromAmount: Double,
        to: String
    ) {

        _conversionEvent.value = CurrencyUiEvent.Loading

        viewModelScope.launch(dispatcher.io) {

            //check there is available fromCurrency amount
            try {
                val userCurrency = repository.getUserCurrencyByName(from)
                val fromLatestRate = repository.getLatestCurrencyRateByName(from)
                val toLatestRate = repository.getLatestCurrencyRateByName(to)


                val probableConvertedAmount = getTwoDecimalConvertedAmount(
                    fromLatestRate.conversionRate,
                    toLatestRate.conversionRate,
                    fromAmount
                )

                if (userCurrency.currencyBalance < fromAmount) {
                    _conversionEvent.value =
                        CurrencyUiEvent.ConversionFailure("Insufficient balance (incl. commission)!")

                } else {
                    _conversionEvent.value =
                        CurrencyUiEvent.ConversionSuccess(
                            from, fromAmount, to, probableConvertedAmount
                        )
                    updateUserCurrency(
                        from, fromAmount, to, probableConvertedAmount
                    )
                }
            } catch (e: Exception) {
                _conversionEvent.value =
                    CurrencyUiEvent.ConversionFailure("Something went wrong. Conversion failed!")
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


    private fun getAllUserCurrencies() {
        viewModelScope.launch(dispatcher.io) {
            try {
                repository.getAllUserCurrencies().collect { result ->
                    when (result) {
                        is Resource.Error -> TODO()
                        is Resource.Loading -> TODO()
                        is Resource.Success -> _allUserCurrencies.postValue(result.data!!)
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }
    }

    // update user currency
    private fun updateUserCurrency(
        from: String,
        fromAmount: Double,
        to: String,
        convertedAmount: Double
    ) {
        viewModelScope.launch(dispatcher.io) {
            val checkStatus = repository.checkUserCurrencyByName(to)

            if (checkStatus == 1) {
                repository.updateMinusUserCurrency(from, fromAmount)
                repository.updateSumUserCurrency(to, convertedAmount)

            } else {
                repository.updateMinusUserCurrency(from, fromAmount)
                val userCurrency =
                    UserCurrency(
                        currencyName = to,
                        currencyBalance = convertedAmount
                    )

                repository.addUserCurrency(userCurrency)
            }
            getAllUserCurrencies()

        }


    }

    //adding new currency
    fun addNewCurrency(currencyName: String, currencyAmountStr: String) {
        val currencyAmount = currencyAmountStr.toDoubleOrNull()

        if (currencyAmount == null) {
            _conversionEvent.value =
                CurrencyUiEvent.AddNewCurrencyFailure("Please give valid amount!")
            return
        }

        viewModelScope.launch(dispatcher.io) {
            try {
                val checkStatus = repository.checkUserCurrencyByName(currencyName)
                if (checkStatus != 1) {
                    val userCurrency =
                        UserCurrency(
                            currencyName = currencyName,
                            currencyBalance = currencyAmount
                        )

                    repository.addUserCurrency(userCurrency)
                } else {
                    repository.updateSumUserCurrency(currencyName, currencyAmount)
                }

                getAllUserCurrencies()

                _conversionEvent.value =
                    CurrencyUiEvent.AddNewCurrencySuccess("New $currencyName currency added!")

            } catch (e: Exception) {
                _conversionEvent.value =
                    CurrencyUiEvent.AddNewCurrencyFailure("Add new currency failed!")
            }
        }
    }

}