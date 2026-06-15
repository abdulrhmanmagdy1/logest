package com.edham.logistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LoadAdapter(private var loads: List<Load>, private val onLoadClick: (Load) -> Unit = {}) :
    RecyclerView.Adapter<LoadAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId: TextView = itemView.findViewById(R.id.tvShipmentId)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvFrom: TextView = itemView.findViewById(R.id.tvFrom)
        val tvTo: TextView = itemView.findViewById(R.id.tvTo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shipment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val load = loads[position]
        holder.tvId.text = load.id
        holder.tvStatus.text = load.status
        holder.tvFrom.text = load.from
        holder.tvTo.text = load.to

        holder.itemView.setOnClickListener { onLoadClick(load) }
    }

    override fun getItemCount(): Int = loads.size

    fun updateData(newLoads: List<Load>) {
        loads = newLoads
        notifyDataSetChanged()
    }
}
