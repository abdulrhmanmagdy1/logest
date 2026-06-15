package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.SmartAlert

class SmartAlertAdapter(
    private var items: List<SmartAlert>,
    private val onItemClick: (SmartAlert) -> Unit
) : RecyclerView.Adapter<SmartAlertAdapter.ViewHolder>() {

    fun updateData(newItems: List<SmartAlert>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_smart_alert, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvEmoji = view.findViewById<TextView>(R.id.tvEmoji)
        private val iconContainer = view.findViewById<View>(R.id.iconContainer)
        private val tvTitle = view.findViewById<TextView>(R.id.alertTitle)
        private val tvMessage = view.findViewById<TextView>(R.id.alertMessage)
        private val tvTime = view.findViewById<TextView>(R.id.alertTime)

        fun bind(item: SmartAlert) {
            tvTitle.text = item.title
            tvMessage.text = item.message
            tvTime.text = item.time

            // Style based on type
            when (item.type.uppercase()) {
                "DELAY" -> {
                    tvEmoji.text = "⚠️"
                    iconContainer.setBackgroundResource(R.drawable.bg_stat_icon_orange)
                }
                "TEMPERATURE" -> {
                    tvEmoji.text = "❄️"
                    iconContainer.setBackgroundResource(R.drawable.bg_stat_icon_success)
                }
                "EMERGENCY" -> {
                    tvEmoji.text = "🚨"
                    iconContainer.setBackgroundResource(R.drawable.bg_stat_icon_success) // Re-use or create bg_stat_icon_rust
                    // iconContainer.setBackgroundResource(R.drawable.bg_stat_icon_rust)
                }
                "MAINTENANCE" -> {
                    tvEmoji.text = "🔧"
                    iconContainer.setBackgroundResource(R.drawable.bg_stat_icon_teal)
                }
                else -> {
                    tvEmoji.text = "🔔"
                    iconContainer.setBackgroundResource(R.drawable.bg_stat_icon_teal)
                }
            }
            
            // Rust/Emergency special case
            if (item.priority.uppercase() == "HIGH" || item.type.uppercase() == "EMERGENCY") {
                iconContainer.setBackgroundResource(R.drawable.bg_stat_icon_orange) // Fallback to orange if rust bg not ready
            }

            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}
