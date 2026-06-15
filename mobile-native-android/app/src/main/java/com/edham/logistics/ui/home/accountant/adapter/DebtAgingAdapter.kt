package com.edham.logistics.ui.home.accountant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.ClientDebt

class DebtAgingAdapter(
    private var items: List<ClientDebt>,
    private val onWhatsapp: (ClientDebt) -> Unit,
    private val onCollect: (ClientDebt) -> Unit
) : RecyclerView.Adapter<DebtAgingAdapter.ViewHolder>() {

    fun updateData(newItems: List<ClientDebt>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_debt_aging_premium, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.clientName
        holder.amount.text = "${item.amount.toInt()} ج"
        holder.delay.text = "متأخر ${item.delayDays} يوماً"
        holder.status.text = item.status
        
        holder.btnWhatsapp.setOnClickListener { onWhatsapp(item) }
        holder.btnCollect.setOnClickListener { onCollect(item) }
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvClientName)
        val amount: TextView = view.findViewById(R.id.tvDebtAmount)
        val delay: TextView = view.findViewById(R.id.tvDelayDays)
        val status: TextView = view.findViewById(R.id.tvDebtStatus)
        val btnWhatsapp: View = view.findViewById(R.id.btnWhatsapp)
        val btnCollect: View = view.findViewById(R.id.btnCollect)
    }
}
