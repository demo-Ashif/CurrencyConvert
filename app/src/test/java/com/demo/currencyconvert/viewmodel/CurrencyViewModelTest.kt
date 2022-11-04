package com.demo.currencyconvert.viewmodel

import androidx.databinding.ObservableDouble
import com.demo.currencyconvert.data.repo.FakeCurrencyRepositoryImpl
import com.demo.currencyconvert.data.repo.FakeRoomRepositoryImpl
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.CurrencyEntity
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.LatestRateEntity
import com.demo.currencyconvert.core.common.Constants
import com.demo.currencyconvert.feature_currency_convert.presentation.CurrencyViewModel
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.math.round

class CurrencyViewModelTest {
    private lateinit var currencyViewModel: CurrencyViewModel


    @Before
    fun setUp() {
        currencyViewModel =
            CurrencyViewModel(FakeCurrencyRepositoryImpl(), FakeRoomRepositoryImpl())
    }



    val currencyEntity = CurrencyEntity(
        currencyName = "Euro",
        currencyBalance = 1000.00
    )

    private val fromLatestRate = LatestRateEntity(
        currencyName = "EUR",
        conversionRate = 1.0
    )
    private val toLatestRate = LatestRateEntity(
        currencyName = "USD",
        conversionRate = 0.98
    )

    private val currentCommission = ObservableDouble(0.0)

    private val commissionMsgEmpty = ""
    private val commissionMsgTenthCharge = "No commission on your every 10th exchange!"
    private val commissionMsgEuro = "No commission up to 200 EUR!"
    private val commissionMsgApplied = "Commission: ${fromLatestRate.currencyName} ${
        String.format(
            "%.2f",
            currentCommission.get()
        )
    }"

    private val numberOfExchange = 3
    private val convertingAmount = 250.0

    @Test
    fun `input valid amounts, returns success with proper commission rate and msg`() {

        val probableConvertedAmount = getTwoDecimalConvertedAmount(
            fromLatestRate.conversionRate,
            toLatestRate.conversionRate,
            convertingAmount
        )

      assertThat(probableConvertedAmount).isEqualTo(245.00)

        val commissionMsg = applyCommissionRules(
            convertingAmount, toLatestRate.conversionRate,
            probableConvertedAmount
        )

        assertThat(commissionMsg).isEqualTo(commissionMsgEmpty)

        val conversionMsg = "+$probableConvertedAmount"
    }


    private fun getTwoDecimalConvertedAmount(
        fromLatestRate: Double,
        toLatestRate: Double,
        fromAmount: Double
    ): Double {

        val convertedAmount = (toLatestRate / fromLatestRate) * fromAmount
        return round((convertedAmount * 100.0)) / 100.0
    }


    private fun applyCommissionRules(
        fromAmount: Double,
        toLatestConversionRate: Double,
        convertedAmount: Double
    ): String {

        currentCommission.set(0.0)

        if (numberOfExchange <= Constants.NUMBER_OF_FREE_EXCHANGE) {
            return ""
        }

        // checking the amount is under 200 euro
        val amountInEuro = (convertedAmount / toLatestConversionRate)

        if (amountInEuro < 200) {

            return commissionMsgEuro // no commission on every 10th exchange
        }

        if (numberOfExchange % Constants.FREE_EXCHANGE_DIVIDER == 0) {
            return commissionMsgTenthCharge // no commission on every 10th exchange
        }

        val commissionAmount = fromAmount * (0.7 / 100.0)

        currentCommission.set(commissionAmount)

        return commissionMsgApplied


    }
}

