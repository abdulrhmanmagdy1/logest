package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.edham.logistics.R
import dagger.hilt.android.AndroidEntryPoint

import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.ui.home.customer.adapter.ChatAdapter
import com.edham.logistics.ui.home.customer.adapter.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CustomerSupportActivity : AppCompatActivity() {

    private lateinit var rvChat: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: View
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_support)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        
        rvChat = findViewById(R.id.rvChat)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        setupChat()
    }

    private fun setupChat() {
        val session = com.edham.logistics.app.AuthSession.get(this)
        val firstName = session.displayName?.split(" ")?.firstOrNull() ?: "عميلنا العزيز"
        
        adapter = ChatAdapter(messages)
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = adapter

        // Welcome messages
        addMessage(ChatMessage("مرحباً بك يا $firstName! 👋 أنا مساعدك الذكي في إدهام. كيف يمكنني مساعدتك اليوم؟", false))
        addMessage(ChatMessage("يمكنك أيضاً التواصل معنا مباشرة على 0554568771 أو عبر البريد care@edham.co", false))
        
        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                addMessage(ChatMessage(text, true))
                etMessage.text.clear()
                simulateBotResponse()
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)
    }

    private fun simulateBotResponse() {
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val responses = listOf(
                "شكراً على تواصلك! سأقوم بمراجعة طلبك والرد عليك فوراً 🙏",
                "بالتأكيد! يمكنني مساعدتك في ذلك. هل يمكنك إعطائي رقم الشحنة؟",
                "تم استلام استفسارك وسيتم التواصل معك خلال دقائق ⚡"
            )
            addMessage(ChatMessage(responses.random(), false))
        }, 1000)
    }
}
