package com.edham.logistics.ui.home.driver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R

data class LogEntry(
    val route: String,
    val meta: String,
    val earning: String,
    val date: String
)

class LogBookAdapter(private val logs: List<LogEntry>) :
    RecyclerView.Adapter<LogBookAdapter.ViewHolder>() {

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
        holder.route.text = log.route
        holder.meta.text = log.meta
        holder.earning.text = log.earning
        holder.date.text = log.date
    }

    override fun getItemCount() = logs.size
}
