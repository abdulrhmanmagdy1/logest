package com.edham.logistics.ui.home.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
)

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_BOT = 2

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: TextView = view.findViewById(R.id.tvMessageContent)
        val time: TextView = view.findViewById(R.id.tvMessageTime)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = if (viewType == VIEW_TYPE_USER) 
            R.layout.item_chat_message // We will adjust this layout to handle both side or use two layouts
        else 
            R.layout.item_chat_message
            
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.content.text = message.content
        holder.time.text = message.timestamp
        
        val container = holder.itemView.findViewById<View>(R.id.layoutMessageContainer)
        if (message.isUser) {
            container.setBackgroundResource(R.drawable.chat_message_sent_bg)
            holder.itemView.rotationY = 0f // Standard alignment
        } else {
            container.setBackgroundResource(R.drawable.chat_message_received_bg)
        }
    }

    override fun getItemCount() = messages.size
}
