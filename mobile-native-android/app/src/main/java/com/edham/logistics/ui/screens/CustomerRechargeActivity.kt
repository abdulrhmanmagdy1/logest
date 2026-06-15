package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerRechargeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_recharge)

        findViewById<View>(R.id.toolbar).setOnClickListener { finish() }

        findViewById<View>(R.id.btnConfirmPayment).setOnClickListener {
            Toast.makeText(this, "تم شحن الرصيد بنجاح (محاكاة)", Toast.LENGTH_LONG).show()
            finish()
        }
        
        setupAmountSelection()
    }

    private fun setupAmountSelection() {
        val amt1000 = findViewById<View>(R.id.amt_1000)
        val amt2000 = findViewById<View>(R.id.amt_2000)

        amt1000.setOnClickListener {
            amt1000.setBackgroundResource(R.drawable.ed_teal_15)
            amt2000.setBackgroundResource(0)
        }

        amt2000.setOnClickListener {
            amt2000.setBackgroundResource(R.drawable.ed_teal_15)
            amt1000.setBackgroundResource(0)
        }
    }
}
