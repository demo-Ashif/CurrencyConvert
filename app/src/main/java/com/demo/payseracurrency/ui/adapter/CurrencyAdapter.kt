package com.demo.payseracurrency.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.payseracurrency.R
import com.demo.payseracurrency.data.room.CurrencyEntity
import com.google.android.material.textview.MaterialTextView

class CurrencyAdapter : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>(){
    private val currencies : MutableList<CurrencyEntity> = mutableListOf()

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var currencyName = itemView.findViewById<View>(R.id.tvCurrencyBalance) as MaterialTextView
        var currencyBalance = itemView.findViewById<View>(R.id.tvCurrencyName) as TextView

        fun bind(item : CurrencyEntity){
            currencyName.text = item.currencyBalance.toString()
            currencyBalance.text = item.currencyName
        }
    }

    fun setCurrencies(accounts: List<CurrencyEntity>){
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