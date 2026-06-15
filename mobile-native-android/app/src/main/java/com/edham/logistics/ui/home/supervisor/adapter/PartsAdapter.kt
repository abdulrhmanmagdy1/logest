package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.PartItem

class PartsAdapter(private var parts: List<PartItem>) :
    RecyclerView.Adapter<PartsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val code: TextView = view.findViewById(R.id.tvPartCode)
        val name: TextView = view.findViewById(R.id.tvPartName)
        val quantity: TextView = view.findViewById(R.id.tvQuantity)
        val status: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_part_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val part = parts[position]
        holder.code.text = part.code
        holder.name.text = part.name
        holder.quantity.text = "${part.quantity} قطعة"
        holder.status.text = part.status
        
        when(part.status.uppercase()) {
            "AVAILABLE", "IN_STOCK" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.html_success))
            "LOW_STOCK" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.html_warning))
            "OUT_OF_STOCK" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.html_danger))
        }
    }

    override fun getItemCount() = parts.size

    fun updateData(newParts: List<PartItem>) {
        this.parts = newParts
        notifyDataSetChanged()
    }
}
