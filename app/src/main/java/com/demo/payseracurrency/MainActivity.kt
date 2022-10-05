package com.demo.payseracurrency

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.payseracurrency.data.room.CurrencyEntity
import com.demo.payseracurrency.databinding.ActivityMainBinding
import com.demo.payseracurrency.ui.adapter.CurrencyAdapter
import com.demo.payseracurrency.viewmodel.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrencyViewModel by viewModels()
    private var currencyAdapter = CurrencyAdapter()

    var userFromCurrencyList = ArrayList<String>()
    var fromCurrencyAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            viewModel.startValidationCheck(
                binding.spinnerFromCurrency.selectedItem.toString(),
                binding.spinnerToCurrency.selectedItem.toString(),
                binding.etFromCurrency.text.toString()
            )
//            viewModel.convertCurrency(
//                binding.spinnerFromCurrency.selectedItem.toString(),
//                binding.spinnerToCurrency.selectedItem.toString(),
//                binding.etFromCurrency.text.toString().toDouble()
//            )
        }

        val rvCurrency = binding.rvCurrency


        rvCurrency.adapter = currencyAdapter
        rvCurrency.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        rvCurrency.setHasFixedSize(true)


        initAllObservers()

        viewModel.setInitialCurrency()

        //viewModel.getLatestRates()
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
                        binding.tvResultText.setTextColor(Color.GREEN)
                        binding.tvResultText.text = "All validation success before converting!"
                        viewModel.convertCurrency(event.from, event.to, event.fromAmount)
                    }

                    is CurrencyViewModel.CurrencyConversionEvent.Loading -> {
                        binding.progressBar.isVisible = true
                    }

                    is CurrencyViewModel.CurrencyConversionEvent.ConversionSuccess -> {
                        binding.progressBar.isVisible = false
                        binding.tvConvertedAmountText.setTextColor(Color.GREEN)
                        binding.tvConvertedAmountText.text = event.successMsg

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
            fromCurrencyAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line, userFromCurrencyList
            )

            binding.spinnerFromCurrency.adapter = fromCurrencyAdapter
        }
    }

}