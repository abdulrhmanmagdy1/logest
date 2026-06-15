package com.edham.logistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InvoiceAdapter(private var invoices: List<Invoice>) :
    RecyclerView.Adapter<InvoiceAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId: TextView = itemView.findViewById(R.id.tv_invoice_id)
        val tvClient: TextView = itemView.findViewById(R.id.tv_client_name)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvLoadId: TextView = itemView.findViewById(R.id.tv_load_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_invoice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val invoice = invoices[position]
        holder.tvId.text = invoice.id
        holder.tvClient.text = invoice.clientName
        holder.tvAmount.text = invoice.amount
        holder.tvStatus.text = invoice.status
        holder.tvDate.text = invoice.date
        holder.tvLoadId.text = "Load: ${invoice.loadId}"

        holder.tvStatus.background = when (invoice.status) {
            "Paid" -> holder.itemView.context.getDrawable(R.drawable.bg_status_completed)
            "Pending" -> holder.itemView.context.getDrawable(R.drawable.bg_status_pending)
            "Overdue" -> holder.itemView.context.getDrawable(R.drawable.bg_status_cancelled)
            else -> holder.itemView.context.getDrawable(R.drawable.bg_status_pending)
        }
    }

    override fun getItemCount(): Int = invoices.size

    fun updateData(newInvoices: List<Invoice>) {
        invoices = newInvoices
        notifyDataSetChanged()
    }
}
