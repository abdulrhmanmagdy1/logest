package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.ui.home.customer.adapter.NotificationAdapter
import com.edham.logistics.ui.home.customer.adapter.NotificationItem
import com.edham.logistics.ui.home.customer.adapter.NotificationType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerNotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_notifications) // Reusing the layout

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvNotifications)
        rv.layoutManager = LinearLayoutManager(this)
        
        val items = listOf(
            NotificationItem("شحنتك في الطريق ❄️", "الشحنة #IDH-0891 غادرت الرياض وفي طريقها إلى جدة", "منذ 30 دقيقة", NotificationType.SHIPMENT),
            NotificationItem("تم الدفع بنجاح ✅", "تم خصم 750 ريال من محفظتك للشحنة #IDH-0891", "منذ ساعة", NotificationType.PAYMENT),
            NotificationItem("تنبيه درجة الحرارة ⚠️", "تم ضبط درجة حرارة الشحنة #IDH-0867 بنجاح", "منذ 3 ساعات", NotificationType.ALERT)
        )
        
        rv.adapter = NotificationAdapter(items)
    }
}
