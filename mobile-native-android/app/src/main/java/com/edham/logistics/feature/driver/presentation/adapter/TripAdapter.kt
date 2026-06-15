package com.edham.logistics.feature.driver.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.databinding.ItemTripCardBinding
import com.edham.logistics.feature.driver.data.models.Trip

class TripAdapter(
    private var trips: List<Trip>,
    private val onTripClick: (Trip) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    fun updateTrips(newTrips: List<Trip>) {
        this.trips = newTrips
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(trips[position])
    }

    override fun getItemCount(): Int = trips.size

    inner class TripViewHolder(private val binding: ItemTripCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: Trip) {
            binding.tvTripId.text = trip.tripId
            binding.tvTripStatus.text = trip.status
            binding.tvTripEarnings.text = "${trip.earnings} ريال"
            binding.tvOrigin.text = trip.origin
            binding.tvDestination.text = trip.destination
            binding.tvDistance.text = "${trip.distance} كم"
            
            binding.root.setOnClickListener { onTripClick(trip) }
        }
    }
}
