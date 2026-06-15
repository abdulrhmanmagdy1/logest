package com.edham.logistics.feature.driver.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.databinding.ItemStatusTimelineBinding

data class StatusEvent(
    val title: String,
    val time: String,
    val isCompleted: Boolean,
    val isLast: Boolean = false
)

class StatusTimelineAdapter(
    private var events: List<StatusEvent>
) : RecyclerView.Adapter<StatusTimelineAdapter.ViewHolder>() {

    fun updateEvents(newEvents: List<StatusEvent>) {
        this.events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStatusTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class ViewHolder(private val binding: ItemStatusTimelineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: StatusEvent) {
            binding.tvStatusTitle.text = event.title
            binding.tvStatusTime.text = event.time
            
            // UI logic for timeline lines
            binding.lineAbove.visibility = if (adapterPosition == 0) View.INVISIBLE else View.VISIBLE
            binding.lineBelow.visibility = if (event.isLast) View.INVISIBLE else View.VISIBLE
            
            val color = if (event.isCompleted) 0xFF2ECC71.toInt() else 0xFFE0E0E0.toInt()
            binding.indicator.setBackgroundColor(color)
            binding.lineAbove.setBackgroundColor(color)
        }
    }
}
