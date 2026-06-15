package com.edham.logistics.ui.home.driver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R

import com.edham.logistics.feature.driver.data.models.Trip

class LogBookAdapter(private var logs: List<Trip>) :
    RecyclerView.Adapter<LogBookAdapter.ViewHolder>() {

    fun updateData(newLogs: List<Trip>) {
        logs = newLogs
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val route: TextView = view.findViewById(R.id.tvTripRoute)
        val meta: TextView = view.findViewById(R.id.tvTripMeta)
        val earning: TextView = view.findViewById(R.id.tvTripEarning)
        val date: TextView = view.findViewById(R.id.tvTripDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        holder.route.text = "${log.origin} ← ${log.destination}"
        holder.meta.text = "${log.distance} كم • ${log.routeSummary}"
        holder.earning.text = "+${log.earnings} ريال"
        holder.date.text = log.startTime
    }

    override fun getItemCount() = logs.size
}
