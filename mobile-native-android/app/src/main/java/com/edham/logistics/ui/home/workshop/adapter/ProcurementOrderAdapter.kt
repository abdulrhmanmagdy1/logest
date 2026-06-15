package com.edham.logistics.ui.home.workshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R

data class ProcurementOrder(
    val id: String,
    val name: String,
    val amount: String,
    val status: String // WAIT, SHIP, DONE
)

class ProcurementOrderAdapter(private var items: List<ProcurementOrder>) :
    RecyclerView.Adapter<ProcurementOrderAdapter.ViewHolder>() {

    fun updateData(newItems: List<ProcurementOrder>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_procurement_order_premium, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.id.text = "#${item.id}"
        holder.name.text = item.name
        holder.amt.text = item.amount
        
        when(item.status) {
            "WAIT" -> {
                holder.status.text = "⏳ انتظار"
                holder.status.backgroundTintList = android.content.res.ColorStateList.valueOf(holder.itemView.context.getColor(R.color.ed_copper_new).withAlpha(38))
            }
            "SHIP" -> {
                holder.status.text = "🚚 جاري الشحن"
                holder.status.backgroundTintList = android.content.res.ColorStateList.valueOf(holder.itemView.context.getColor(R.color.ed_sky).withAlpha(38))
            }
            else -> {
                holder.status.text = "✅ تم الاستلام"
                holder.status.backgroundTintList = android.content.res.ColorStateList.valueOf(holder.itemView.context.getColor(R.color.ed_emerald_2).withAlpha(38))
            }
        }
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.tvOrderId)
        val name: TextView = view.findViewById(R.id.tvOrderName)
        val amt: TextView = view.findViewById(R.id.tvOrderDetails)
        val status: TextView = view.findViewById(R.id.tvOrderStatus)
    }
    
    private fun Int.withAlpha(alpha: Int): Int {
        return (this and 0x00FFFFFF) or (alpha shl 24)
    }
}
