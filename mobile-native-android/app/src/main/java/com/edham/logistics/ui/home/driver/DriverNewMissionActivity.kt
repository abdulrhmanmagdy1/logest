package com.edham.logistics.ui.home.driver

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverNewMissionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_new_mission)

        findViewById<View>(R.id.btnAccept).setOnClickListener {
            Toast.makeText(this, "تم قبول المهمة! بالتوفيق في رحلتك 🚛", Toast.LENGTH_LONG).show()
            finish()
        }

        findViewById<View>(R.id.btnReject).setOnClickListener {
            // Show reject reason dialog
            finish()
        }
    }
}
