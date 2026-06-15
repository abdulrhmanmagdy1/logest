package com.edham.logistics.ui.home.driver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.feature.driver.data.models.Trip

class TacticalMissionAdapter(private var items: List<Trip>) :
    RecyclerView.Adapter<TacticalMissionAdapter.ViewHolder>() {

    fun updateData(newList: List<Trip>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assigned_trip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip = items[position]
        holder.id.text = "#${trip.tripId}"
        holder.route.text = "${trip.origin} ← ${trip.destination}"
        holder.status.text = trip.status
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.tvTripId)
        val route: TextView = view.findViewById(R.id.tvRoute)
        val status: TextView = view.findViewById(R.id.tvStatus)
    }
}
