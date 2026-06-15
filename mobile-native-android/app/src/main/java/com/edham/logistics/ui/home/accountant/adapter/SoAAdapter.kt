package com.edham.logistics.ui.home.accountant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.SoAEntry

class SoAAdapter(private var items: List<SoAEntry>) :
    RecyclerView.Adapter<SoAAdapter.ViewHolder>() {

    fun updateData(newItems: List<SoAEntry>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_soa_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.id.text = item.id
        holder.date.text = item.date
        holder.amount.text = item.amount.toInt().toString()
        holder.paid.text = item.paid.toInt().toString()
        holder.status.text = item.status
        
        // Color based on status
        when(item.status.uppercase()) {
            "PAID" -> {
                holder.status.setTextColor(holder.itemView.context.getColor(R.color.ed_success))
                holder.status.setBackgroundResource(R.drawable.status_paid_bg)
            }
            "PARTIAL" -> {
                holder.status.setTextColor(holder.itemView.context.getColor(R.color.ed_orange))
                holder.status.setBackgroundResource(R.drawable.status_partial_bg)
            }
            else -> {
                holder.status.setTextColor(holder.itemView.context.getColor(R.color.ed_rust))
                holder.status.setBackgroundResource(R.drawable.ed_rust_15)
            }
        }
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.tvEntryId)
        val date: TextView = view.findViewById(R.id.tvEntryDate)
        val amount: TextView = view.findViewById(R.id.tvAmount)
        val paid: TextView = view.findViewById(R.id.tvPaid)
        val status: TextView = view.findViewById(R.id.tvStatus)
    }
}
