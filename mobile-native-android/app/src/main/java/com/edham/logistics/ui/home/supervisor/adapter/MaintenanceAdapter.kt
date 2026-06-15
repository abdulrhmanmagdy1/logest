package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.MaintenanceRecord

class MaintenanceAdapter(private var records: List<MaintenanceRecord>) :
    RecyclerView.Adapter<MaintenanceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val truckId: TextView = view.findViewById(R.id.tvTruckId)
        val serviceType: TextView = view.findViewById(R.id.tvServiceType)
        val date: TextView = view.findViewById(R.id.tvDate)
        val cost: TextView = view.findViewById(R.id.tvCost)
        val status: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_maintenance_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.truckId.text = record.vehicleId
        holder.serviceType.text = record.serviceType
        holder.date.text = record.date
        holder.cost.text = String.format("%.2f ج.م", record.cost)
        holder.status.text = record.status
        
        when(record.status.uppercase()) {
            "COMPLETED" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.html_success))
            "IN_PROGRESS" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.html_warning))
            "URGENT" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.html_danger))
        }
    }

    override fun getItemCount() = records.size

    fun updateData(newRecords: List<MaintenanceRecord>) {
        this.records = newRecords
        notifyDataSetChanged()
    }
}
