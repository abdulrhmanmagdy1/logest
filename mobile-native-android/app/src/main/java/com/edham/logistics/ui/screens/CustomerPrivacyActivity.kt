package com.edham.logistics.ui.screens

import android.os.Bundle
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity

class CustomerPrivacyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_privacy)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }
}
