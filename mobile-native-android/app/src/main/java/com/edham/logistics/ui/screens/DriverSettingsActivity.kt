package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_customer_settings) // Reusing the high-fidelity layout

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.settingsToolbar)
        toolbar.setNavigationOnClickListener { finish() }

        findViewById<View>(R.id.btnSaveSettings).setOnClickListener {
            Toast.makeText(this, "تم حفظ إعدادات السائق بنجاح ✅", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
