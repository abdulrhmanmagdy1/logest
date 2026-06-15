package com.edham.logistics.ui.home.supervisor

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import com.edham.logistics.feature.driver.data.models.DriverProfile
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupervisorDriverProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_driver_profile)

        findViewById<View>(R.id.toolbar).setOnClickListener { finish() }

        // Bind data from Intent (usually a full object or ID to fetch)
        // intent.getParcelableExtra<DriverProfile>("DRIVER_DATA")?.let { bindDriver(it) }
        
        setupActions()
    }

    private fun bindDriver(driver: DriverProfile) {
        findViewById<TextView>(R.id.tvDriverName).text = "${driver.firstName} ${driver.lastName}"
        findViewById<TextView>(R.id.tvDriverEmail).text = driver.email
        findViewById<TextView>(R.id.tvPlateNumber).text = driver.plateNumber ?: "---"
        findViewById<TextView>(R.id.tvAvatarLarge).text = (driver.firstName.take(1) + driver.lastName.take(1)).uppercase()
        findViewById<TextView>(R.id.tvStatusBadge).text = driver.status
    }

    private fun setupActions() {
        findViewById<View>(R.id.btnCallDriver).setOnClickListener {
            Toast.makeText(this, "جاري الاتصال بالسائق...", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<View>(R.id.btnMessageDriver).setOnClickListener {
            Toast.makeText(this, "فتح الدردشة مع السائق...", Toast.LENGTH_SHORT).show()
        }
    }
}
