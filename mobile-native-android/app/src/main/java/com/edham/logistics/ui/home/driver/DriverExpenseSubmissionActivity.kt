package com.edham.logistics.ui.home.driver

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.edham.logistics.R
import com.edham.logistics.feature.driver.presentation.viewmodels.DriverDashboardViewModel
import com.edham.logistics.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverExpenseSubmissionActivity : BaseActivity() {

    private val viewModel: DriverDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_expense_submission)

        findViewById<View>(R.id.toolbar).setOnClickListener { finish() }

        findViewById<View>(R.id.btnSubmitExpense).setOnClickListener {
            // Logic to collect form data and submit
            Toast.makeText(this, "تم إرسال المصروف للمحاسب بنجاح ✅", Toast.LENGTH_LONG).show()
            finish()
        }
        
        findViewById<View>(R.id.btnCaptureReceipt).setOnClickListener {
            Toast.makeText(this, "فتح الكاميرا للالتقاط...", Toast.LENGTH_SHORT).show()
        }
    }
}
