package com.demo.currencyconvert

import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.currencyconvert.databinding.ActivityMainBinding
import com.demo.currencyconvert.ui.adapter.CurrencyAdapter
import com.demo.currencyconvert.ui.views.CustomDialog.Companion.showSuccessDialog
import com.demo.currencyconvert.utils.Helper
import com.demo.currencyconvert.viewmodel.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrencyViewModel by viewModels()
    private var currencyAdapter = CurrencyAdapter()

    var userFromCurrencyList = ArrayList<String>()

    // spinner values
    lateinit var  fromSpinnerCurrency:String
    lateinit var  toSpinnerCurrency:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinnerFromCurrency.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->
            fromSpinnerCurrency=text
        }


        binding.spinnerToCurrency.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->
            toSpinnerCurrency=text
        }
        binding.spinnerToCurrency.selectItemByIndex(0)

        binding.btnSubmit.setOnClickListener {
            Helper.hideKeyboard(this)
            binding.tvConvertedAmountText.text = ""
            viewModel.startValidationCheck(
                fromSpinnerCurrency,
                toSpinnerCurrency,
                binding.etFromCurrency.text.toString()
            )
        }

        val rvCurrency = binding.rvCurrency


        rvCurrency.adapter = currencyAdapter
        rvCurrency.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        rvCurrency.setHasFixedSize(true)


        initAllObservers()

        viewModel.setInitialCurrency()

        getLatestRateRepeatMethod()


    }

    private fun getLatestRateRepeatMethod() {
        val lifecycle = this
        lifecycle.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    viewModel.getLatestRates()
                    delay(60000)
                }
            }
        }
    }

    private fun initAllObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.conversionEvent.collect { event ->
                when (event) {

                    is CurrencyViewModel.CurrencyConversionEvent.ConversionFailure -> {
                        binding.progressBar.isVisible = false

                        binding.tvResultText.setTextColor(Color.RED)
                        binding.tvResultText.text = event.errorText
                    }
                    is CurrencyViewModel.CurrencyConversionEvent.CheckValidationSuccess -> {
                        binding.tvResultText.setTextColor(Color.TRANSPARENT)
                        binding.tvResultText.text = ""
                        //binding.tvResultText.text = "All validation success before converting!"
                        viewModel.convertCurrency(event.from, event.to, event.fromAmount)
                    }

                    is CurrencyViewModel.CurrencyConversionEvent.Loading -> {
                        binding.progressBar.isVisible = true
                    }

                    is CurrencyViewModel.CurrencyConversionEvent.ConversionSuccess -> {
                        binding.progressBar.isVisible = false
                        binding.tvConvertedAmountText.text = event.successMsg

                        binding.tvResultText.setTextColor(Color.TRANSPARENT)
                        binding.tvResultText.text = ""

                        binding.etFromCurrency.text?.clear()

                        //show Dialog for conversion success message with commission
                        showSuccessDialog(
                            this@MainActivity,
                            event.from,
                            String.format("%.2f",event.fromAmount),
                            event.to,
                            String.format("%.2f",event.convertedAmount),
                            event.commission
                        )

                        viewModel.roomDbUpdateData(
                            event.from,
                            event.fromAmount,
                            event.to,
                            event.convertedAmount,
                            event.commission
                        )

                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.roomEvent.collect { event ->
                when (event) {
                    is CurrencyViewModel.RoomDataUpdateEvent.InsertNeeded -> {
                        viewModel.getAllCurrencies()
                    }
                    is CurrencyViewModel.RoomDataUpdateEvent.CheckComplete -> {
                        viewModel.getAllCurrencies()
                    }

                    else -> Unit
                }
            }
        }

        viewModel.allCurrencies.observe(this) { allCurrencies ->
//            Log.d("all items", allCurrencies.toString())
            currencyAdapter.setCurrencies(allCurrencies)

            userFromCurrencyList.clear()
            for (item in allCurrencies) {
                userFromCurrencyList.add(item.currencyName)
            }

            binding.spinnerFromCurrency.setItems(userFromCurrencyList)
            binding.spinnerFromCurrency.selectItemByIndex(0)

        }
    }

}