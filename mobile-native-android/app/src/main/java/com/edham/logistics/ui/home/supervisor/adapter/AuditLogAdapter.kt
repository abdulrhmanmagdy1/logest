package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.AuditEntry

class AuditLogAdapter(private var entries: List<AuditEntry>) :
    RecyclerView.Adapter<AuditLogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val action: TextView = view.findViewById(R.id.tvAction)
        val user: TextView = view.findViewById(R.id.tvUser)
        val time: TextView = view.findViewById(R.id.tvTime)
        val details: TextView = view.findViewById(R.id.tvDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audit_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.action.text = entry.action
        holder.user.text = entry.user
        holder.time.text = entry.timestamp
        holder.details.text = entry.details
        holder.details.visibility = if (entry.details.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun getItemCount() = entries.size

    fun updateData(newEntries: List<AuditEntry>) {
        this.entries = newEntries
        notifyDataSetChanged()
    }
}
