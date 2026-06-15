package com.edham.logistics.ui.home.accountant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.DriverExpense

class ExpenseAdapter(
    private var items: List<DriverExpense>,
    private val onApprove: (DriverExpense) -> Unit,
    private val onReject: (DriverExpense) -> Unit,
    private val onImageClick: (String) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    fun updateData(newItems: List<DriverExpense>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver_expense, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvExpenseName)
        val amount: TextView = view.findViewById(R.id.tvAmount)
        val attachmentStatus: TextView = view.findViewById(R.id.tvAttachmentStatus)
        val icon: TextView = view.findViewById(R.id.tvIcon)
        val iconContainer: View = view.findViewById(R.id.iconContainer)
        val btnApprove: View = view.findViewById(R.id.btnApprove)
        val btnReject: View = view.findViewById(R.id.btnReject)
        val actionGroup: View = view.findViewById(R.id.actionGroup)

        fun bind(item: DriverExpense) {
            name.text = item.description
            amount.text = "${item.amount.toInt()} ج"
            
            icon.text = when(item.type.uppercase()) {
                "FUEL" -> "⛽"
                "TOLL" -> "🛣️"
                else -> "🧾"
            }

            if (item.imageUrl.isNullOrEmpty()) {
                attachmentStatus.text = "لا يوجد مرفق"
                attachmentStatus.setTextColor(itemView.context.getColor(R.color.ed_rust))
                iconContainer.isClickable = false
            } else {
                attachmentStatus.text = "صورة المرفق متاحة ✓"
                attachmentStatus.setTextColor(itemView.context.getColor(R.color.ed_success))
                iconContainer.setOnClickListener { onImageClick(item.imageUrl) }
            }

            actionGroup.visibility = if (item.status == "PENDING") View.VISIBLE else View.GONE
            
            btnApprove.setOnClickListener { onApprove(item) }
            btnReject.setOnClickListener { onReject(item) }

            // Visual indicator if already reviewed
            itemView.alpha = if (item.status != "PENDING") 0.6f else 1.0f
        }
    }
}
