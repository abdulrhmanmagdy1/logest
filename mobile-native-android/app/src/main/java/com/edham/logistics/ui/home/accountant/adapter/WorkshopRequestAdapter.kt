package com.edham.logistics.ui.home.accountant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.WorkshopFinancialRequest

class WorkshopRequestAdapter(
    private var items: List<WorkshopFinancialRequest>,
    private val onApprove: (WorkshopFinancialRequest) -> Unit,
    private val onReject: (WorkshopFinancialRequest) -> Unit
) : RecyclerView.Adapter<WorkshopRequestAdapter.ViewHolder>() {

    fun updateData(newItems: List<WorkshopFinancialRequest>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workshop_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.truck.text = "شاحنة ${item.truckId} — ${item.truckType}"
        holder.amount.text = "${item.amount.toInt()} ج"
        holder.priority.text = item.priority
        holder.requestedBy.text = item.requestedBy
        
        holder.btnApprove.setOnClickListener { onApprove(item) }
        holder.btnReject.setOnClickListener { onReject(item) }
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvReqTitle)
        val truck: TextView = view.findViewById(R.id.tvTruckId)
        val amount: TextView = view.findViewById(R.id.tvReqAmount)
        val priority: TextView = view.findViewById(R.id.tvPriority)
        val requestedBy: TextView = view.findViewById(R.id.tvRequestedBy)
        val btnApprove: View = view.findViewById(R.id.btnApprove)
        val btnReject: View = view.findViewById(R.id.btnReject)
    }
}
