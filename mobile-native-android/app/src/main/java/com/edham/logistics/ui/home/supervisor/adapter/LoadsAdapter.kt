package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.feature.driver.data.models.Trip

class LoadsAdapter(
    private var loads: List<Trip>,
    private val onItemClick: (Trip) -> Unit
) : RecyclerView.Adapter<LoadsAdapter.ViewHolder>() {

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
        val load = loads[position]
        holder.itemView.setOnClickListener { onItemClick(load) }

        holder.loadId.text = load.tripId
        holder.clientName.text = "عميل إدهام" // Trip model missing clientName
        holder.route.text = "${load.origin} ← ${load.destination}"
        holder.weight.text = "25 طن" // Trip model missing weight
        holder.content.text = "حمولة مبردة" // Placeholder or extract if added to Load
        holder.statusBadge.text = load.status
        
        when(load.status.uppercase()) {
            "COMPLETED", "DELIVERED" -> {
                holder.statusBadge.setTextColor(holder.itemView.context.getColor(R.color.status_success))
                holder.statusBadge.setBackgroundResource(R.drawable.bg_role_badge)
            }
            "IN_TRANSIT", "STARTED" -> {
                holder.statusBadge.setTextColor(holder.itemView.context.getColor(R.color.status_info))
                holder.statusBadge.setBackgroundResource(R.drawable.bg_tag_blue)
            }
            else -> {
                holder.statusBadge.setTextColor(holder.itemView.context.getColor(R.color.status_warning))
                holder.statusBadge.setBackgroundResource(R.drawable.bg_tag_orange)
            }
        }
    }

    override fun getItemCount() = loads.size

    fun updateData(newLoads: List<Trip>) {
        this.loads = newLoads
        notifyDataSetChanged()
    }
}
