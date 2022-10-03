package com.demo.payseracurrency.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.payseracurrency.R
import com.demo.payseracurrency.data.room.CurrencyEntity

class CurrencyAdapter : ListAdapter<CurrencyEntity, CurrencyAdapter.CurrencyViewHolder>(CurrencyComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.currencyName,current.currencyBalance)

    }

    class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCurrencyName: TextView = itemView.findViewById(R.id.tvCurrencyName)
        private val tvCurrencyAmount: TextView = itemView.findViewById(R.id.tvCurrencyBalance)

        fun bind(name: String?,balance: Double) {
            tvCurrencyName.text = name
            tvCurrencyAmount.text = "$balance"
        }

        companion object {
            fun create(parent: ViewGroup): CurrencyViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_currencies, parent, false)
                return CurrencyViewHolder(view)
            }
        }
    }

    class CurrencyComparator : DiffUtil.ItemCallback<CurrencyEntity>() {
        override fun areItemsTheSame(oldItem: CurrencyEntity, newItem: CurrencyEntity): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: CurrencyEntity, newItem: CurrencyEntity): Boolean {
            return oldItem.currencyName == newItem.currencyName
        }
    }
}