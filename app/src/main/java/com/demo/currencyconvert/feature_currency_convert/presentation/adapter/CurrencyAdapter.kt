package com.demo.currencyconvert.feature_currency_convert.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.currencyconvert.R
import com.demo.currencyconvert.feature_currency_convert.data.local.entity.CurrencyEntity
import com.demo.currencyconvert.feature_currency_convert.domain.model.UserCurrency
import com.google.android.material.textview.MaterialTextView

class CurrencyAdapter : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>(){
    private val currencies : MutableList<UserCurrency> = mutableListOf()

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var currencyName = itemView.findViewById<View>(R.id.tvCurrencyBalance) as MaterialTextView
        var currencyBalance = itemView.findViewById<View>(R.id.tvCurrencyName) as TextView

        fun bind(item : UserCurrency){
            currencyName.text = String.format("%.2f", item.currencyBalance)
            currencyBalance.text = item.currencyName
        }
    }

    fun setCurrencies(accounts: List<UserCurrency>){
        currencies.clear()
        currencies.addAll(accounts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currencies, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {

        holder.bind(currencies[position])
    }

    override fun getItemCount(): Int {
        return currencies.size
    }

}