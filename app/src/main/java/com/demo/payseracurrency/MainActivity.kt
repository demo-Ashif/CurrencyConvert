package com.demo.payseracurrency

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.demo.payseracurrency.databinding.ActivityMainBinding
import com.demo.payseracurrency.viewmodel.PayseraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PayseraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            viewModel.convertCurrency(
                binding.spinnerFromCurrency.selectedItem.toString(),
                binding.spinnerToCurrency.selectedItem.toString(),
                binding.etFromCurrency.text.toString()
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when (event) {
                    is PayseraViewModel.CurrencyEvent.Success -> {
                        binding.progressBar.isVisible = false
                        binding.tvResultText.setTextColor(Color.GREEN)
                        binding.tvResultText.text = event.resultText
                    }
                    is PayseraViewModel.CurrencyEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.tvResultText.setTextColor(Color.RED)
                        binding.tvResultText.text = event.errorText
                    }
                    is PayseraViewModel.CurrencyEvent.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }
    }
}