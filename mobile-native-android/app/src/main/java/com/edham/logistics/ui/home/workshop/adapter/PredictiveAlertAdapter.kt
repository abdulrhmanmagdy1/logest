package com.edham.logistics.ui.home.workshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R

data class PredictiveAlert(
    val title: String,
    val sub: String,
    val km: String,
    val type: String // WARNING, CRITICAL
)

class PredictiveAlertAdapter(private var items: List<PredictiveAlert>) :
    RecyclerView.Adapter<PredictiveAlertAdapter.ViewHolder>() {

    fun updateData(newItems: List<PredictiveAlert>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_predictive_alert_premium, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.sub.text = item.sub
        holder.km.text = item.km
        
        val color = if (item.type == "CRITICAL") R.color.ed_rust else R.color.ed_copper_new
        holder.dot.backgroundTintList = android.content.res.ColorStateList.valueOf(holder.itemView.context.getColor(color))
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvAlertTitle)
        val sub: TextView = view.findViewById(R.id.tvAlertSub)
        val km: TextView = view.findViewById(R.id.tvAlertKm)
        val dot: View = view.findViewById(R.id.alertDot)
    }
}
