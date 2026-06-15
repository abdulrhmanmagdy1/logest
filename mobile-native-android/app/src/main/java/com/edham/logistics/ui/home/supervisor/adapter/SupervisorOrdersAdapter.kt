package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.Load
import com.edham.logistics.R

class SupervisorOrdersAdapter(
    private var orders: List<Load>,
    private val onOrderClick: (Load) -> Unit
) : RecyclerView.Adapter<SupervisorOrdersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trackingNumber: TextView = view.findViewById(R.id.tvTrackingNumber)
        val route: TextView = view.findViewById(R.id.tvRoute)
        val status: TextView = view.findViewById(R.id.tvStatus)
        val driverName: TextView = view.findViewById(R.id.tvDriverName)
        val eta: TextView = view.findViewById(R.id.tvEta)
        val cargoType: TextView = view.findViewById(R.id.tvCargoType)
        val price: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_supervisor_shipment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.trackingNumber.text = order.id
        holder.route.text = "${order.from} ← ${order.to}"
        holder.status.text = order.status
        holder.driverName.text = order.driverName ?: "لم يتم التعيين"
        holder.eta.text = if (order.driverName != null) "جاري النقل" else "بانتظار سائق"
        holder.cargoType.text = order.temperature ?: "جاف"
        holder.price.text = order.price

        holder.itemView.setOnClickListener { onOrderClick(order) }
    }

    override fun getItemCount() = orders.size

    fun updateData(newOrders: List<Load>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}
