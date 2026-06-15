package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.feature.driver.data.models.DriverProfile

class DriverStatusAdapter(
    private var drivers: List<DriverProfile>,
    private val onItemClick: ((DriverProfile) -> Unit)? = null
) : RecyclerView.Adapter<DriverStatusAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val subInfo: TextView = view.findViewById(R.id.tvSubInfo)
        val statusBadge: TextView = view.findViewById(R.id.tvStatusBadge)
        val avatar: TextView = view.findViewById(R.id.tvAvatar)
        val medal: TextView? = view.findViewById(R.id.tvMedal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver_status, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val driver = drivers[position]
        holder.name.text = "${driver.firstName} ${driver.lastName}"
        holder.avatar.text = (driver.firstName.take(1) + driver.lastName.take(1)).uppercase()
        
        // Medals for top 3
        holder.medal?.visibility = if (position < 3) View.VISIBLE else View.GONE
        holder.medal?.text = when(position) {
            0 -> "🥇"
            1 -> "🥈"
            2 -> "🥉"
            else -> ""
        }

        holder.subInfo.text = "الالتزام: %98 | ${driver.plateNumber ?: "---"}"
        holder.statusBadge.text = driver.status
        
        // Dynamic status coloring (using professional palette)
        when(driver.status.uppercase()) {
            "ACTIVE", "ONLINE" -> {
                holder.statusBadge.setTextColor(holder.itemView.context.getColor(R.color.status_success))
                holder.statusBadge.setBackgroundResource(R.drawable.bg_role_badge) // Reuse for now
            }
            "BUSY", "IN_TRANSIT" -> {
                holder.statusBadge.setTextColor(holder.itemView.context.getColor(R.color.status_warning))
                holder.statusBadge.setBackgroundResource(R.drawable.bg_tag_orange)
            }
            else -> {
                holder.statusBadge.setTextColor(holder.itemView.context.getColor(R.color.text_tertiary))
                holder.statusBadge.setBackgroundResource(R.drawable.bg_circle_gray)
            }
        }

        holder.itemView.setOnClickListener { onItemClick?.invoke(driver) }
    }

    override fun getItemCount() = drivers.size

    fun updateData(newDrivers: List<DriverProfile>) {
        this.drivers = newDrivers
        notifyDataSetChanged()
    }
}
