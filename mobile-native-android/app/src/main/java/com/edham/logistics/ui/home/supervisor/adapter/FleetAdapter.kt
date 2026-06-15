package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.VehicleItem

class FleetAdapter(private var vehicles: List<VehicleItem>) :
    RecyclerView.Adapter<FleetAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.tvVehicleId)
        val plate: TextView = view.findViewById(R.id.tvPlateNumber)
        val driver: TextView = view.findViewById(R.id.tvDriverName)
        val temp: TextView = view.findViewById(R.id.tvTemperature)
        val mileage: TextView = view.findViewById(R.id.tvMileage)
        val status: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = vehicles[position]
        holder.id.text = vehicle.id
        holder.plate.text = vehicle.plateNumber
        holder.driver.text = "السائق: ${vehicle.driverName ?: "---"}"
        holder.temp.text = "${vehicle.temperature}°م"
        holder.mileage.text = "${vehicle.mileage} كم"
        holder.status.text = vehicle.status
        
        when(vehicle.status.uppercase()) {
            "ACTIVE", "READY" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.status_success))
            "MAINTENANCE" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.status_warning))
            "OFFLINE", "GROUNDED" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.status_error))
        }
        
        // Temperature warning
        if (vehicle.temperature > -5.0) {
            holder.temp.setTextColor(holder.itemView.context.getColor(R.color.status_error))
        } else {
            holder.temp.setTextColor(holder.itemView.context.getColor(R.color.status_info))
        }
    }

    override fun getItemCount() = vehicles.size

    fun updateData(newVehicles: List<VehicleItem>) {
        this.vehicles = newVehicles
        notifyDataSetChanged()
    }
}
