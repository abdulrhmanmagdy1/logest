package com.edham.logistics.ui.home.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.Load

class RecentShipmentAdapter(private var shipments: List<Load>) : 
    RecyclerView.Adapter<RecentShipmentAdapter.ShipmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_supervisor_shipment, parent, false) // Reusing existing item layout for now
        return ShipmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShipmentViewHolder, position: Int) {
        val shipment = shipments[position]
        holder.bind(shipment)
    }

    override fun getItemCount(): Int = shipments.size

    fun updateData(newList: List<Load>) {
        this.shipments = newList
        notifyDataSetChanged()
    }

    class ShipmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(shipment: Load) {
            view.findViewById<TextView>(R.id.shipmentId).text = shipment.id
            view.findViewById<TextView>(R.id.shipmentStatus).text = shipment.status
            // Bind more fields (from, to, etc.) as per design
        }
    }
}
