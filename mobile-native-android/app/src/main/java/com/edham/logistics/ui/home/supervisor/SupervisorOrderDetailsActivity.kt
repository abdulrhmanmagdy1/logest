package com.edham.logistics.ui.home.supervisor

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import com.edham.logistics.feature.driver.data.models.Trip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupervisorOrderDetailsActivity : BaseActivity() {

    private val viewModel: SupervisorViewModel by viewModels()
    private var tripId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_order_details)

        tripId = intent.getStringExtra("TRIP_ID")
        
        findViewById<View>(R.id.toolbar).setOnClickListener { finish() }

        setupUI()
        observeViewModel()
        
        // In a real app, we'd fetch the specific trip details by ID
        // For now, if we have the trip object passed, we bind it.
    }

    private fun setupUI() {
        findViewById<View>(R.id.btnAssignDriver).setOnClickListener {
            showDriverSelection()
        }
    }

    private fun showDriverSelection() {
        // Here we would open a BottomSheet to select a driver
        Toast.makeText(this, "جاري فتح قائمة السائقين...", Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}
