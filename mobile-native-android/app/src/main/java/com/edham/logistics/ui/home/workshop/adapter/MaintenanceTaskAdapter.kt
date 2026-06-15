package com.edham.logistics.ui.home.workshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R

data class MaintenanceTask(
    val plate: String,
    val type: String,
    val status: String,
    val time: String
)

class MaintenanceTaskAdapter(private val tasks: List<MaintenanceTask>) :
    RecyclerView.Adapter<MaintenanceTaskAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val plate: TextView = view.findViewById(R.id.tvVehiclePlate)
        val type: TextView = view.findViewById(R.id.tvServiceType)
        val status: TextView = view.findViewById(R.id.tvStatusBadge)
        val time: TextView = view.findViewById(R.id.tvCompletionTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_maintenance_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.plate.text = task.plate
        holder.type.text = task.type
        holder.status.text = task.status
        holder.time.text = task.time
    }

    override fun getItemCount() = tasks.size
}
