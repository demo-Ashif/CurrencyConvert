package com.demo.payseracurrency

import android.graphics.Color
import android.os.Bundle
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
    private val currencyAdapter = CurrencyAdapter()

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

        viewModel.setInitialCurrency()

        initAllObservers()


    }

    private fun initAllObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when (event) {
                    is CurrencyViewModel.CurrencyEvent.Success -> {
                        binding.progressBar.isVisible = false
                        binding.tvResultText.setTextColor(Color.GREEN)
                        binding.tvResultText.text = event.resultText
                    }
                    is CurrencyViewModel.CurrencyEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.tvResultText.setTextColor(Color.RED)
                        binding.tvResultText.text = event.errorText
                    }
                    is CurrencyViewModel.CurrencyEvent.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }

        viewModel.allCurrencies.observe(this) { items ->
            currencyAdapter.submitList(items)
        }
    }
}