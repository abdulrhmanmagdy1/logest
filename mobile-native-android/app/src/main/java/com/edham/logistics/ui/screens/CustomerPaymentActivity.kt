package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.edham.logistics.R
import com.edham.logistics.ui.home.CustomerHomeViewModel
import dagger.hilt.android.AndroidEntryPoint

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.ui.home.customer.adapter.InvoiceAdapter
import com.edham.logistics.core.network.api.Invoice

@AndroidEntryPoint
class CustomerPaymentActivity : AppCompatActivity() {

    private val viewModel: CustomerHomeViewModel by viewModels()
    private lateinit var historyAdapter: InvoiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_payment)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        setupAmountSelection()
        setupHistory()
        
        findViewById<View>(R.id.btnConfirmCharge).setOnClickListener {
            startActivity(android.content.Intent(this, AddCreditCardActivity::class.java))
        }
    }

    private fun setupHistory() {
        val rv = findViewById<RecyclerView>(R.id.rvRechargeHistory)
        rv.layoutManager = LinearLayoutManager(this)
        historyAdapter = InvoiceAdapter(emptyList()) { }
        rv.adapter = historyAdapter

        // Simulate history
        val mockHistory = listOf(
            Invoice("RCG-982", "شحن محفظة", 1000.0, "25 مايو 2026", "SUCCESS"),
            Invoice("RCG-971", "شحن محفظة", 500.0, "10 مايو 2026", "SUCCESS")
        )
        historyAdapter.updateData(mockHistory)
    }

    private fun setupAmountSelection() {
        val amt1000 = findViewById<View>(R.id.amt_1000)
        val amt2000 = findViewById<View>(R.id.amt_2000)
        
        amt1000.setOnClickListener {
            // Selection logic
        }
        
        amt2000.setOnClickListener {
            // Selection logic
        }
    }
}
