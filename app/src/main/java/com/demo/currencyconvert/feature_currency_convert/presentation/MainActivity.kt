package com.demo.currencyconvert.feature_currency_convert.presentation

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.currencyconvert.R
import com.demo.currencyconvert.databinding.ActivityMainBinding
import com.demo.currencyconvert.feature_currency_convert.presentation.adapter.CurrencyAdapter
import com.demo.currencyconvert.feature_currency_convert.presentation.views.CustomDialog.Companion.showSuccessDialog
import com.demo.currencyconvert.core.utils.Helper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.skydoves.powerspinner.PowerSpinnerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrencyViewModel by viewModels()
    private var currencyAdapter = CurrencyAdapter()

    var userFromCurrencyList = ArrayList<String>()

    lateinit var fromSpinnerCurrency: String
    lateinit var toSpinnerCurrency: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinnerFromCurrency.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->
            fromSpinnerCurrency = text
        }


        binding.spinnerToCurrency.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->
            toSpinnerCurrency = text
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

        binding.btnFabAddNewCurrency.setOnClickListener {
            showAddAccountDialog()
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

                    is CurrencyViewModel.CurrencyUiEvent.ConversionFailure -> {
                        binding.progressBar.isVisible = false

                        binding.tvResultText.setTextColor(Color.RED)
                        binding.tvResultText.text = event.errorText
                    }

                    is CurrencyViewModel.CurrencyUiEvent.Loading -> {
                        binding.progressBar.isVisible = true
                    }

                    is CurrencyViewModel.CurrencyUiEvent.ConversionSuccess -> {
                        binding.progressBar.isVisible = false
                        binding.tvConvertedAmountText.text = "${event.from} ${event.fromAmount} -> ${event.to} ${event.convertedAmount}"

                        binding.tvResultText.setTextColor(Color.TRANSPARENT)
                        binding.tvResultText.text = ""

                        binding.etFromCurrency.text?.clear()

                        //show Dialog for conversion success message with commission
                        showSuccessDialog(
                            this@MainActivity,
                            event.from,
                            String.format("%.2f", event.fromAmount),
                            event.to,
                            String.format("%.2f", event.convertedAmount)
                        )

                    }
                    is CurrencyViewModel.CurrencyUiEvent.AddNewCurrencySuccess -> {
                        binding.tvResultText.setTextColor(Color.TRANSPARENT)
                        binding.tvResultText.text = ""
                        showSnackBar(binding.root, event.successMsg)
                    }
                    is CurrencyViewModel.CurrencyUiEvent.AddNewCurrencyFailure -> {
                        binding.tvResultText.setTextColor(Color.TRANSPARENT)
                        binding.tvResultText.text = ""
                        showSnackBar(binding.root, event.errorText)
                    }
                    else -> Unit
                }
            }
        }

        viewModel.allUserCurrencies.observe(this) { allCurrencies ->
            currencyAdapter.setCurrencies(allCurrencies)

            userFromCurrencyList.clear()
            for (item in allCurrencies) {
                userFromCurrencyList.add(item.currencyName)
            }

            binding.spinnerFromCurrency.setItems(userFromCurrencyList)
            binding.spinnerFromCurrency.selectItemByIndex(0)

        }
    }

    /**
     *  Dialog for Adding new Currency
     */
    private fun showAddAccountDialog() {
        val dialog = Dialog(this@MainActivity)
        with(dialog) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(R.layout.add_currency_dialog)

            var selectedCurrency = "EUR"
            val addCurrencySpinner =
                this.findViewById<PowerSpinnerView>(R.id.spinnerToCurrency)
            val etAddCurrency =
                this.findViewById<TextInputEditText>(R.id.etFromCurrency)
            val btnNext =
                this.findViewById<Button>(R.id.btnAddCurrency)
            val btnClose =
                this.findViewById<Button>(R.id.btnClose)

            addCurrencySpinner.selectItemByIndex(0)
            addCurrencySpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->
                selectedCurrency = text

            }

            btnNext.setOnClickListener {

                viewModel.addNewCurrency(selectedCurrency, etAddCurrency.text.toString())

                dismiss()
            }
            btnClose.setOnClickListener {
                dismiss()
            }
            show()
        }

    }

    private fun showSnackBar(decorView: View, message: String) {
        Snackbar.make(decorView.rootView, message, Snackbar.LENGTH_LONG)
            .show()
    }
}