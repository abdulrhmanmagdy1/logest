package com.edham.logistics.ui.home.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.Invoice

class InvoiceAdapter(
    private var invoices: List<Invoice>,
    private val onInvoiceClick: (Invoice) -> Unit
) : RecyclerView.Adapter<InvoiceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.tv_invoice_id)
        val name: TextView = view.findViewById(R.id.tv_client_name)
        val amount: TextView = view.findViewById(R.id.tv_invoice_amount)
        val date: TextView = view.findViewById(R.id.tv_invoice_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invoice_new, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val invoice = invoices[position]
        holder.id.text = "#${invoice.id}"
        holder.name.text = invoice.clientName
        holder.amount.text = "${invoice.amount} ريال"
        holder.date.text = invoice.date
        
        holder.itemView.setOnClickListener { onInvoiceClick(invoice) }
    }

    override fun getItemCount() = invoices.size

    fun updateData(newInvoices: List<Invoice>) {
        this.invoices = newInvoices
        notifyDataSetChanged()
    }
}
