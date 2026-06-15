package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import androidx.activity.viewModels
import com.edham.logistics.ui.home.CustomerHomeViewModel
import com.edham.logistics.ui.home.customer.adapter.NotificationAdapter
import com.edham.logistics.ui.home.customer.adapter.NotificationItem
import com.edham.logistics.ui.home.customer.adapter.NotificationType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerNotificationsActivity : AppCompatActivity() {

    private val viewModel: CustomerHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_notifications)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvNotifications)
        rv.layoutManager = LinearLayoutManager(this)
        
        // For now, we reuse the mock list but indicate it's waiting for live API
        val items = listOf(
            NotificationItem("تحديث الشحنة ❄️", "الشحنة غادرت المركز الرئيسي", "الآن", NotificationType.SHIPMENT),
            NotificationItem("عملية دفع ✅", "تم استلام دفعتك بنجاح", "منذ ساعة", NotificationType.PAYMENT)
        )
        
        rv.adapter = NotificationAdapter(items)
        
        // In the next sprint, we bind this to viewModel.notifications
    }
}
