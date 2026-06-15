package com.edham.logistics.ui.screens

import android.os.Bundle
import android.widget.Toast
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import com.google.android.material.button.MaterialButton

class CustomerCompanyDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_company_details)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnSave).setOnClickListener {
            Toast.makeText(this, "تم حفظ بيانات الشركة بنجاح", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
