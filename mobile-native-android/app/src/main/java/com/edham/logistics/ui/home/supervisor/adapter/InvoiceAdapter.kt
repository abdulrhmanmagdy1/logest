package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.Invoice

class InvoiceAdapter(private var invoices: List<Invoice>) :
    RecyclerView.Adapter<InvoiceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.tv_invoice_id)
        val client: TextView = view.findViewById(R.id.tv_client_name)
        val amount: TextView = view.findViewById(R.id.tv_invoice_amount)
        val date: TextView = view.findViewById(R.id.tv_invoice_date)
        val statusDot: View = view.findViewById(R.id.invoice_status_dot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invoice_new, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val invoice = invoices[position]
        holder.id.text = "#${invoice.id}"
        holder.client.text = invoice.clientName
        holder.amount.text = String.format("%,.2f ج.م", invoice.amount)
        holder.date.text = invoice.date
        
        when(invoice.status.uppercase()) {
            "PAID" -> holder.statusDot.setBackgroundResource(R.drawable.circle_green)
            "PENDING" -> holder.statusDot.setBackgroundResource(R.drawable.circle_orange_bg)
            else -> holder.statusDot.setBackgroundResource(R.drawable.circle_red_bg)
        }
    }

    override fun getItemCount() = invoices.size

    fun updateData(newInvoices: List<Invoice>) {
        this.invoices = newInvoices
        notifyDataSetChanged()
    }
}
