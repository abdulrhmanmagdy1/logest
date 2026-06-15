package com.edham.logistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ColdChainAdapter(private var records: List<ColdChainRecord>) :
    RecyclerView.Adapter<ColdChainAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLoadId: TextView = itemView.findViewById(R.id.tv_load_id)
        val tvTemp: TextView = itemView.findViewById(R.id.tv_temperature)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cold_chain, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.tvLoadId.text = record.loadId
        holder.tvTemp.text = record.temperature
        holder.tvStatus.text = record.status
        holder.tvTime.text = record.time

        holder.tvStatus.background = when (record.status) {
            "Stable" -> holder.itemView.context.getDrawable(R.drawable.bg_status_completed)
            "Warning" -> holder.itemView.context.getDrawable(R.drawable.bg_status_cancelled)
            else -> holder.itemView.context.getDrawable(R.drawable.bg_status_pending)
        }
    }

    override fun getItemCount(): Int = records.size

    fun updateData(newRecords: List<ColdChainRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }
}
