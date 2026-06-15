package com.edham.logistics.ui.home.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R

data class NotificationItem(
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isUnread: Boolean = true
)

enum class NotificationType {
    SHIPMENT, PAYMENT, ALERT
}

class NotificationAdapter(private val notifications: List<NotificationItem>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvNotifTitle)
        val message: TextView = view.findViewById(R.id.tvNotifMessage)
        val time: TextView = view.findViewById(R.id.tvNotifTime)
        val icon: ImageView = view.findViewById(R.id.ivNotifIcon)
        val iconCard: View = view.findViewById(R.id.notifIconCard)
        val unreadDot: View = view.findViewById(R.id.unreadDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification_premium, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notifications[position]
        holder.title.text = item.title
        holder.message.text = item.message
        holder.time.text = item.time
        holder.unreadDot.visibility = if (item.isUnread) View.VISIBLE else View.GONE

        when (item.type) {
            NotificationType.SHIPMENT -> {
                holder.icon.setImageResource(R.drawable.ic_truck)
                holder.iconCard.setBackgroundResource(R.drawable.sky_glow_bg)
            }
            NotificationType.PAYMENT -> {
                holder.icon.setImageResource(R.drawable.ic_wallet)
                holder.iconCard.setBackgroundResource(R.drawable.emerald_glow_bg)
            }
            NotificationType.ALERT -> {
                holder.icon.setImageResource(R.drawable.ic_alert)
                holder.iconCard.setBackgroundResource(R.drawable.gold_glow_bg)
            }
        }
    }

    override fun getItemCount() = notifications.size
}
