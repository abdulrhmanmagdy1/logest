package com.edham.logistics.ui.home.workshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.VehicleItem

class GroundedTruckAdapter(
    private var items: List<VehicleItem>,
    private val onRelease: (VehicleItem) -> Unit
) : RecyclerView.Adapter<GroundedTruckAdapter.ViewHolder>() {

    fun updateData(newItems: List<VehicleItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grounded_truck_premium, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = "${item.plateNumber} — ${item.type}"
        holder.fault.text = "⚠️ إيقاف فني: عطل في المبرد" // In real app, item.lastFault
        holder.timer.text = "⏱ مُوقف منذ 4 ساعات" 
        
        holder.btnRelease.setOnClickListener { onRelease(item) }
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvTruckName)
        val fault: TextView = view.findViewById(R.id.tvFault)
        val timer: TextView = view.findViewById(R.id.tvTimer)
        val btnRelease: View = view.findViewById(R.id.btnFix)
    }
}
