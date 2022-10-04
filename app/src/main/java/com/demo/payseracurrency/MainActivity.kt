package com.demo.payseracurrency

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.payseracurrency.databinding.ActivityMainBinding
import com.demo.payseracurrency.ui.adapter.CurrencyAdapter
import com.demo.payseracurrency.viewmodel.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint

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
            viewModel.startProcessToConvertCurrency(
                binding.spinnerFromCurrency.selectedItem.toString(),
                binding.spinnerToCurrency.selectedItem.toString(),
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
    }

    private fun initAllObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.conversionEvent.collect { event ->
                when (event) {
                    is CurrencyViewModel.CurrencyConversionEvent.Success -> {
                        binding.progressBar.isVisible = false
                        binding.tvResultText.setTextColor(Color.GREEN)
                        binding.tvResultText.text = event.resultText
                    }
                    is CurrencyViewModel.CurrencyConversionEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.tvResultText.setTextColor(Color.RED)
                        binding.tvResultText.text = event.errorText
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
                        Log.d("insert done called", "${event.toString()}")
                        viewModel.getAllCurrencies()
                    }
                    is CurrencyViewModel.RoomDataUpdateEvent.CheckComplete -> {
                        Log.d("check complete called", "")
                        viewModel.getAllCurrencies()
                    }
                    else -> Unit
                }
            }
        }

        viewModel.allCurrencies.observe(this) { items ->
            Log.d("all items",items.toString())
            currencyAdapter.setCurrencies(items)
            accountList.clear()

//            // add List<AccountsEntity to List<String> for adapter
//            for (item in items) {
//                accountList.add(item.currencyName)
//            }
        }
    }
}