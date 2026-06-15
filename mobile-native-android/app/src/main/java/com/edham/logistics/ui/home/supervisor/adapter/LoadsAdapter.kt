package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.feature.driver.data.models.Trip

class LoadsAdapter(private var trips: List<Trip>) :
    RecyclerView.Adapter<LoadsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val loadId: TextView = view.findViewById(R.id.tvLoadId)
        val clientName: TextView = view.findViewById(R.id.tvClientName)
        val route: TextView = view.findViewById(R.id.tvRoute)
        val weight: TextView = view.findViewById(R.id.tvWeight)
        val content: TextView = view.findViewById(R.id.tvContent)
        val statusBadge: TextView = view.findViewById(R.id.tvStatusBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_load_card_premium, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip = trips[position]
        holder.loadId.text = "#${trip.tripId}"
        holder.clientName.text = trip.routeSummary // Use routeSummary for client placeholder if needed
        holder.route.text = "${trip.origin} ← ${trip.destination}"
        holder.weight.text = "${trip.distance} كم" // Placeholder weight with distance
        holder.content.text = "حمولة مبردة" // Placeholder
        holder.statusBadge.text = trip.status
        
        when(trip.status.uppercase()) {
            "COMPLETED", "DELIVERED" -> {
                holder.statusBadge.setTextColor(holder.itemView.context.getColor(R.color.html_success))
                holder.statusBadge.setBackgroundResource(R.drawable.ed_badge_success)
            }
            "IN_TRANSIT", "STARTED" -> {
                holder.statusBadge.setTextColor(holder.itemView.context.getColor(R.color.html_info))
                holder.statusBadge.setBackgroundResource(R.drawable.badge_info)
            }
            else -> {
                holder.statusBadge.setTextColor(holder.itemView.context.getColor(R.color.html_warning))
                holder.statusBadge.setBackgroundResource(R.drawable.warning_badge_background)
            }
        }
    }

    override fun getItemCount() = trips.size

    fun updateData(newTrips: List<Trip>) {
        this.trips = newTrips
        notifyDataSetChanged()
    }
}
