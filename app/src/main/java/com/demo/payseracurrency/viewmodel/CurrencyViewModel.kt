package com.demo.payseracurrency.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.payseracurrency.data.repo.CurrencyRepository
import com.demo.payseracurrency.data.repo.RoomRepository
import com.demo.payseracurrency.utils.DispatcherProvider
import com.demo.payseracurrency.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun convertCurrency(from: String, to: String, amount: String) {

        val fromAmount = amount.toDoubleOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Amount is not valid")
            return
        }

        viewModelScope.launch(dispatcher.io) {
            _conversion.value = CurrencyEvent.Loading
            when (val convertResponse = repository.getConverted(from, to, fromAmount)) {
                is Resource.Error -> _conversion.value =
                    CurrencyEvent.Failure(convertResponse.message!!)
                is Resource.Success -> {
                    val currencyConvertData = convertResponse.data
                    //val rate = getRateForCurrency(to, rates)
                    if (currencyConvertData == null) {
                        _conversion.value = CurrencyEvent.Failure("Unexpected error")
                    } else {
                        val convertedCurrency =
                            (currencyConvertData.result * 100.0).roundToInt() / 100.0
                        _conversion.value = CurrencyEvent.Success(
                            "$fromAmount $from = $convertedCurrency $to"
                        )
                    }
                }
            }
        }
    }

    fun customFunction() {
        sharedPref.edit().putString("firstStoredString", "this is the content").apply()
        val firstStoredString = sharedPref.getString("firstStoredString", "")
    }

}