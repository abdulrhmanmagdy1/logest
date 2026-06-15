package com.edham.logistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(private var notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_notif_title)
        val tvMessage: TextView = itemView.findViewById(R.id.tv_notif_message)
        val tvType: TextView = itemView.findViewById(R.id.tv_notif_type)
        val tvTime: TextView = itemView.findViewById(R.id.tv_notif_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notif = notifications[position]
        holder.tvTitle.text = notif.title
        holder.tvMessage.text = notif.message
        holder.tvType.text = notif.type.uppercase()
        holder.tvTime.text = notif.time

        holder.tvType.background = when (notif.type) {
            "warning" -> holder.itemView.context.getDrawable(R.drawable.bg_status_cancelled)
            "error" -> holder.itemView.context.getDrawable(R.drawable.bg_status_cancelled)
            "success" -> holder.itemView.context.getDrawable(R.drawable.bg_status_completed)
            else -> holder.itemView.context.getDrawable(R.drawable.bg_status_transit)
        }
    }

    override fun getItemCount(): Int = notifications.size

    fun updateData(newNotifications: List<Notification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }
}
