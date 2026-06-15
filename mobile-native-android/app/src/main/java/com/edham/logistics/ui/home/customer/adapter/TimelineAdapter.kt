package com.edham.logistics.ui.home.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R

data class TimelineStep(
    val title: String,
    val time: String,
    val isCompleted: Boolean,
    val isActive: Boolean
)

class TimelineAdapter(private var steps: List<TimelineStep>) :
    RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    fun updateData(newSteps: List<TimelineStep>) {
        this.steps = newSteps
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvEventTitle)
        val time: TextView = view.findViewById(R.id.tvEventTime)
        val icon: ImageView = view.findViewById(R.id.ivStatusIcon)
        val dotContainer: View = view.findViewById(R.id.dotContainer)
        val line: View = view.findViewById(R.id.lineConnector)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timeline_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val step = steps[position]
        holder.title.text = step.title
        holder.time.text = step.time
        
        // Hide line for last item
        holder.line.visibility = if (position == steps.size - 1) View.GONE else View.VISIBLE

        if (step.isCompleted) {
            holder.dotContainer.setBackgroundResource(R.drawable.circle_green)
            holder.icon.setImageResource(R.drawable.ic_check)
            holder.title.setTextColor(holder.itemView.context.getColor(R.color.html_white))
        } else if (step.isActive) {
            holder.dotContainer.setBackgroundResource(R.drawable.circle_glow)
            holder.icon.setImageResource(R.drawable.ic_truck)
            holder.title.setTextColor(holder.itemView.context.getColor(R.color.prem_sky))
        } else {
            holder.dotContainer.setBackgroundResource(R.drawable.circle_grey)
            holder.icon.setImageResource(R.drawable.ic_clock)
            holder.title.setTextColor(holder.itemView.context.getColor(R.color.customer_text_muted))
        }
    }

    override fun getItemCount() = steps.size
}
