package com.edham.logistics.ui.screens

import android.os.Bundle
import android.widget.Toast
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import com.google.android.material.button.MaterialButton

class AddCreditCardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_credit_card)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnSaveCard).setOnClickListener {
            Toast.makeText(this, "تمت إضافة البطاقة بنجاح", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
