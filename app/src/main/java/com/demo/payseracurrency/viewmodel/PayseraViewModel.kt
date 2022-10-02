package com.demo.payseracurrency.viewmodel

import android.util.Log
import android.util.Log.d
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.payseracurrency.data.models.Rates
import com.demo.payseracurrency.data.repo.PayseraRepository
import com.demo.payseracurrency.utils.DispatcherProvider
import com.demo.payseracurrency.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.d
import java.lang.Math.round
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PayseraViewModel @Inject constructor(
    private val repository: PayseraRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    sealed class CurrencyEvent {
        class Success(val resultText: String) : CurrencyEvent()
        class Failure(val errorText: String) : CurrencyEvent()
        object Loading : CurrencyEvent()
        object Empty : CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    fun convertCurrency(from: String, to: String, amount: String) {

        val fromAmount = amount.toDoubleOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Amount is not valid")
            return
        }

        viewModelScope.launch(dispatcherProvider.io) {
            _conversion.value = CurrencyEvent.Loading
            when (val rateResponse = repository.getRates(from)) {
                is Resource.Error -> _conversion.value =
                    CurrencyEvent.Failure(rateResponse.message!!)
                is Resource.Success -> {
                    val rates = rateResponse.data!!.rates
                    val rate = getRateForCurrency(to, rates)
                    if (rate == null) {
                        _conversion.value = CurrencyEvent.Failure("Unexpected error")
                    } else {
                        val convertedCurrency = round(fromAmount * rate * 100) / 100
                        _conversion.value = CurrencyEvent.Success(
                            "$fromAmount $from = $convertedCurrency $to"
                        )
                    }
                }
            }
        }
    }

    private fun getRateForCurrency(currency: String, rates: Rates) = when (currency) {
//        "CAD" -> rates.CAD
//        "HKD" -> rates.HKD
//        "ISK" -> rates.ISK
//        "EUR" -> rates.EUR
//        "PHP" -> rates.PHP
//        "DKK" -> rates.DKK
//        "HUF" -> rates.HUF
//        "CZK" -> rates.CZK
//        "AUD" -> rates.AUD
//        "RON" -> rates.RON
//        "SEK" -> rates.SEK
//        "IDR" -> rates.IDR
//        "INR" -> rates.INR
//        "BRL" -> rates.BRL
//        "RUB" -> rates.RUB
//        "HRK" -> rates.HRK
//        "JPY" -> rates.JPY
//        "THB" -> rates.THB
//        "CHF" -> rates.CHF
//        "SGD" -> rates.SGD
//        "PLN" -> rates.PLN
//        "BGN" -> rates.BGN
//        "CNY" -> rates.CNY
//        "NOK" -> rates.NOK
//        "NZD" -> rates.NZD
//        "ZAR" -> rates.ZAR
//        "USD" -> rates.USD
//        "MXN" -> rates.MXN
//        "ILS" -> rates.ILS
//        "GBP" -> rates.GBP
//        "KRW" -> rates.KRW
//        "MYR" -> rates.MYR
        "EUR" -> rates.eUR
        "BGN" -> rates.bGN
        "USD" -> rates.uSD
        "AUD" -> rates.aUD
        else -> null
    }
}