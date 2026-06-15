package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import com.edham.logistics.ui.home.CustomerHomeViewModel
import com.edham.logistics.ui.home.customer.adapter.RecentShipmentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerHistoryActivity : BaseActivity() {

    private val viewModel: CustomerHomeViewModel by viewModels()
    private lateinit var adapter: RecentShipmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_history)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvHistory)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = RecentShipmentAdapter(emptyList()) { trip ->
            val intent = android.content.Intent(this, TrackShipmentActivity::class.java)
            intent.putExtra("SHIPMENT_ID", trip.id)
            startActivity(intent)
        }
        rv.adapter = adapter

        viewModel.recentShipments.observe(this) { shipments ->
            adapter.updateData(shipments)
        }
        
        viewModel.loadDashboardData()
    }
}
