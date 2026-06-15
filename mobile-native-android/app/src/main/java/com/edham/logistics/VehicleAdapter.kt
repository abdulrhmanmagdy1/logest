package com.edham.logistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VehicleAdapter(
    private var vehicles: List<Vehicle>,
    private val onVehicleSelected: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {
    
    private var selectedPosition = -1
    
    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivVehicle: ImageView = itemView.findViewById(R.id.iv_vehicle)
        val tvName: TextView = itemView.findViewById(R.id.tv_vehicle_name)
        val tvCapacity: TextView = itemView.findViewById(R.id.tv_capacity)
        val tvDimensions: TextView = itemView.findViewById(R.id.tv_dimensions)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        val radioVehicle: RadioButton = itemView.findViewById(R.id.radio_vehicle)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicles[position]
        
        holder.tvName.text = vehicle.name
        holder.tvCapacity.text = "Capacity: ${vehicle.capacity}"
        holder.tvDimensions.text = vehicle.dimensions
        holder.tvPrice.text = "SAR ${vehicle.basePrice}"
        holder.radioVehicle.isChecked = position == selectedPosition
        
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onVehicleSelected(vehicle)
        }
        
        holder.radioVehicle.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onVehicleSelected(vehicle)
        }
    }
    
    override fun getItemCount(): Int = vehicles.size
    
    fun getSelectedVehicle(): Vehicle? {
        return if (selectedPosition >= 0 && selectedPosition < vehicles.size) {
            vehicles[selectedPosition]
        } else null
    }
}
