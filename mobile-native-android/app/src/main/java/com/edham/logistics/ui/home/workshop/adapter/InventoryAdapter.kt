package com.edham.logistics.ui.home.workshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.PartItem

class InventoryAdapter(private var items: List<PartItem>) :
    RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    fun updateData(newItems: List<PartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory_part, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.code.text = "كود: ${item.id.take(8)}"
        holder.stock.text = "${item.quantity} ${item.unit}"
        
        if (item.quantity < 10) {
            holder.stock.setTextColor(holder.itemView.context.getColor(R.color.ed_rust))
            holder.status.text = "مخزون منخفض ⚠️"
            holder.status.setTextColor(holder.itemView.context.getColor(R.color.ed_rust))
        } else {
            holder.stock.setTextColor(holder.itemView.context.getColor(R.color.ed_success))
            holder.status.text = "مخزون آمن ✅"
            holder.status.setTextColor(holder.itemView.context.getColor(R.color.ed_success))
        }
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvPartName)
        val code: TextView = view.findViewById(R.id.tvPartCode)
        val stock: TextView = view.findViewById(R.id.tvStockLevel)
        val status: TextView = view.findViewById(R.id.tvStockStatus)
    }
}
