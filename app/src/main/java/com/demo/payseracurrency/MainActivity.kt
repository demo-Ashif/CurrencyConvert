package com.demo.payseracurrency

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
    var accountList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
//            viewModel.startValidationCheck(
//                binding.spinnerFromCurrency.selectedItem.toString(),
//                binding.spinnerToCurrency.selectedItem.toString(),
//                binding.etFromCurrency.text.toString()
//            )
            viewModel.convertCurrency(
                binding.spinnerFromCurrency.selectedItem.toString(),
                binding.spinnerToCurrency.selectedItem.toString(),
                binding.etFromCurrency.text.toString().toDouble()
            )
        }

        val rvCurrency = binding.rvCurrency


        rvCurrency.adapter = currencyAdapter
        rvCurrency.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        rvCurrency.setHasFixedSize(true)


        initAllObservers()

        viewModel.setInitialCurrency()
    }

    private fun initAllObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.conversionEvent.collect { event ->
                when (event) {
                    is CurrencyViewModel.CurrencyConversionEvent.ConversionSuccess -> {
                        binding.progressBar.isVisible = false
                        binding.tvConvertedAmountText.setTextColor(Color.GREEN)
                        binding.tvConvertedAmountText.text =
                            String.format("%.2f", event.convertResponse.result)

                        viewModel.postConversionCalculation(
                            event.convertResponse,
                        )
                    }
                    is CurrencyViewModel.CurrencyConversionEvent.PostConversionCalculationDone -> {

                        binding.tvResultText.setTextColor(Color.GREEN)
                        val resultText =
                            if (event.commission <= 0) "${event.convertResponse.query.from} ${event.convertResponse.query.amount} = ${event.convertResponse.query.to} ${event.convertResponse.result}"
                            else "${event.convertResponse.query.from} ${event.convertResponse.query.amount} = ${event.convertResponse.query.to} ${event.convertResponse.result} Commission: ${event.commission}"
                        binding.tvResultText.text = resultText

//                        showSuccessDialog(
//                            this@MainActivity,
//                            """${it.query.amount}${" "}${it.query.from}""",
//                            """${it.result}${" "}${it.query.to}""",
//                            String.format("%.2f", viewModel.commision.get())
//                        )


                    }
                    is CurrencyViewModel.CurrencyConversionEvent.ConversionFailure -> {
                        binding.progressBar.isVisible = false
                        binding.tvResultText.setTextColor(Color.RED)
                        binding.tvResultText.text = event.errorText
                    }
                    is CurrencyViewModel.CurrencyConversionEvent.CheckValidationSuccess -> {
                        viewModel.convertCurrency(event.from, event.to, event.fromAmount)
                    }
                    is CurrencyViewModel.CurrencyConversionEvent.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.roomEvent.collect { event ->
                when (event) {
                    is CurrencyViewModel.RoomDataUpdateEvent.InsertNeeded -> {
//                        Log.d("insert done called", "${event.toString()}")
                        viewModel.getAllCurrencies()
                    }
                    is CurrencyViewModel.RoomDataUpdateEvent.CheckComplete -> {
//                        Log.d("check complete called", "")
                        viewModel.getAllCurrencies()
                    }

                    else -> Unit
                }
            }
        }

        viewModel.allCurrencies.observe(this) { items ->
            Log.d("all items", items.toString())
            currencyAdapter.setCurrencies(items)
            accountList.clear()
        }

        viewModel.convertResponse.observe(this) {
            lifecycleScope.launch {
                viewModel.checkAvailableCurrencyByKey(it.query.to).cancellable()
                    .collect { checkStatus ->
                        if (checkStatus == 1) {
                            viewModel.updateMinus(
                                it.query.from,
                                it.query.amount.toDouble()
                            )
                            viewModel.updateSum(
                                it.query.to,
                                it.result
                            )

                            this.cancel()
                        } else {
                            viewModel.updateMinus(
                                it.query.from,
                                it.result
                            )
                            val currencyEntity =
                                CurrencyEntity(
                                    currencyName = it.query.to,
                                    currencyBalance = it.result
                                )

                            viewModel.insert(currencyEntity)

                            this.cancel()
                        }
                    }
            }

            viewModel.getAllCurrencies()
        }
    }

}