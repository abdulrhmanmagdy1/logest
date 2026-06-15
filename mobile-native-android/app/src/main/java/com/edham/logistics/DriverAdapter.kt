package com.edham.logistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DriverAdapter(private var drivers: List<Driver>) :
    RecyclerView.Adapter<DriverAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_driver_name)
        val tvPhone: TextView = itemView.findViewById(R.id.tv_driver_phone)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_driver_status)
        val tvRating: TextView = itemView.findViewById(R.id.tv_driver_rating)
        val tvTrips: TextView = itemView.findViewById(R.id.tv_driver_trips)
        val tvLocation: TextView = itemView.findViewById(R.id.tv_driver_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_driver, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val driver = drivers[position]
        holder.tvName.text = driver.name
        holder.tvPhone.text = driver.phone
        holder.tvStatus.text = driver.status
        holder.tvRating.text = "★ ${driver.rating}"
        holder.tvTrips.text = "${driver.trips} trips"
        holder.tvLocation.text = driver.currentLocation ?: "Unknown"

        holder.tvStatus.background = when (driver.status) {
            "Active" -> holder.itemView.context.getDrawable(R.drawable.bg_status_completed)
            "On Trip" -> holder.itemView.context.getDrawable(R.drawable.bg_status_transit)
            "Offline" -> holder.itemView.context.getDrawable(R.drawable.bg_status_cancelled)
            else -> holder.itemView.context.getDrawable(R.drawable.bg_status_pending)
        }
    }

    override fun getItemCount(): Int = drivers.size

    fun updateData(newDrivers: List<Driver>) {
        drivers = newDrivers
        notifyDataSetChanged()
    }
}
