package com.edham.logistics.ui.home.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.Load

class RecentShipmentAdapter(
    private var shipments: List<Load>,
    private val onShipmentClick: (Load) -> Unit
) : RecyclerView.Adapter<RecentShipmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.tvShipmentId)
        val route: TextView = view.findViewById(R.id.tvRoute)
        val date: TextView = view.findViewById(R.id.tvDate)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val status: TextView = view.findViewById(R.id.tvStatusBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_shipment_production, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val load = shipments[position]
        holder.id.text = load.id
        holder.route.text = "${load.from} ← ${load.to}"
        holder.date.text = load.date
        holder.price.text = if (load.price.contains("ريال")) load.price else "${load.price} ريال"
        
        holder.status.text = when(load.status.lowercase()) {
            "pending" -> "قيد الانتظار"
            "active", "in transit" -> "في الطريق"
            "delivered", "completed" -> "تم التسليم"
            else -> load.status
        }
        
        holder.itemView.setOnClickListener { onShipmentClick(load) }
    }

    override fun getItemCount() = shipments.size

    fun updateData(newShipments: List<Load>) {
        this.shipments = newShipments
        notifyDataSetChanged()
    }
}
