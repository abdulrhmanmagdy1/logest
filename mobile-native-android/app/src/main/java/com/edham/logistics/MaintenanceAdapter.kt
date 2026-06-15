package com.edham.logistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MaintenanceAdapter(private var records: List<MaintenanceRecord>) :
    RecyclerView.Adapter<MaintenanceAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvVehicle: TextView = itemView.findViewById(R.id.tv_vehicle_name)
        val tvType: TextView = itemView.findViewById(R.id.tv_maintenance_type)
        val tvDate: TextView = itemView.findViewById(R.id.tv_maintenance_date)
        val tvCost: TextView = itemView.findViewById(R.id.tv_maintenance_cost)
        val tvNext: TextView = itemView.findViewById(R.id.tv_next_service)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_maintenance_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_maintenance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.tvVehicle.text = record.vehicleName
        holder.tvType.text = record.type
        holder.tvDate.text = record.date
        holder.tvCost.text = record.cost
        holder.tvNext.text = "Next: ${record.nextService}"
        holder.tvStatus.text = record.status

        holder.tvStatus.background = when (record.status) {
            "Completed" -> holder.itemView.context.getDrawable(R.drawable.bg_status_completed)
            "Pending" -> holder.itemView.context.getDrawable(R.drawable.bg_status_pending)
            "Scheduled" -> holder.itemView.context.getDrawable(R.drawable.bg_status_confirmed)
            else -> holder.itemView.context.getDrawable(R.drawable.bg_status_pending)
        }
    }

    override fun getItemCount(): Int = records.size

    fun updateData(newRecords: List<MaintenanceRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }
}
