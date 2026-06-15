package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.feature.driver.data.models.DriverProfile

class DriverStatusAdapter(private var drivers: List<DriverProfile>) :
    RecyclerView.Adapter<DriverStatusAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.driver_name)
        val status: TextView = view.findViewById(R.id.driver_status_text)
        val plate: TextView = view.findViewById(R.id.driver_plate)
        val avatar: TextView = view.findViewById(R.id.driver_avatar_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver_status, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val driver = drivers[position]
        holder.name.text = "${driver.firstName} ${driver.lastName}"
        holder.status.text = driver.status
        holder.plate.text = driver.plateNumber ?: "---"
        holder.avatar.text = driver.firstName.take(1) + driver.lastName.take(1)
        
        // Dynamic status coloring
        when(driver.status.uppercase()) {
            "ACTIVE", "ONLINE" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.customer_success))
            "BUSY", "IN_TRANSIT" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.customer_warning))
            else -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.customer_text_muted))
        }
    }

    override fun getItemCount() = drivers.size

    fun updateData(newDrivers: List<DriverProfile>) {
        this.drivers = newDrivers
        notifyDataSetChanged()
    }
}
