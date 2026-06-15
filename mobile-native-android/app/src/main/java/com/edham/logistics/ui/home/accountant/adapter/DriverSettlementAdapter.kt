package com.edham.logistics.ui.home.accountant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.DriverSettlement
import com.edham.logistics.core.network.api.DriverExpense

class DriverSettlementAdapter(
    private var items: List<DriverSettlement>,
    private val onExpenseApprove: (DriverExpense) -> Unit,
    private val onExpenseReject: (DriverExpense) -> Unit
) : RecyclerView.Adapter<DriverSettlementAdapter.ViewHolder>() {

    fun updateData(newItems: List<DriverSettlement>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver_settlement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvDriverName)
        val lastUpdate: TextView = view.findViewById(R.id.tvLastUpdate)
        val advance: TextView = view.findViewById(R.id.tvAdvance)
        val approved: TextView = view.findViewById(R.id.tvApproved)
        val remaining: TextView = view.findViewById(R.id.tvRemaining)
        val rvExpenses: RecyclerView = view.findViewById(R.id.rvExpenses)

        fun bind(item: DriverSettlement) {
            name.text = item.driverName
            lastUpdate.text = item.lastUpdate
            advance.text = "${item.advanceAmount.toInt()} ج"
            approved.text = "${item.approvedExpenses.toInt()} ج"
            remaining.text = "${(item.advanceAmount - item.approvedExpenses).toInt()} ج"

            rvExpenses.layoutManager = LinearLayoutManager(itemView.context)
            val adapter = ExpenseAdapter(item.expenses, onExpenseApprove, onExpenseReject)
            rvExpenses.adapter = adapter
        }
    }
}
