package com.edham.logistics.ui.home.driver

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverExpenseSubmissionActivity : BaseActivity() {

    private val viewModel: DriverExpensesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_expense_submission)

        findViewById<View>(R.id.toolbar).setOnClickListener { finish() }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        findViewById<View>(R.id.btnSubmitExpense).setOnClickListener {
            val amount = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etAmount).text.toString().toDoubleOrNull()
            val desc = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etDescription).text.toString()
            
            if (amount != null && desc.isNotEmpty()) {
                val session = com.edham.logistics.app.AuthSession.get(this)
                session.userId?.let { uid ->
                    viewModel.submitExpense(uid, null, amount, "FUEL", desc, null)
                }
            } else {
                Toast.makeText(this, "يرجى ملء كافة الحقول", Toast.LENGTH_SHORT).show()
            }
        }
        
        findViewById<View>(R.id.btnCaptureReceipt).setOnClickListener {
            val intent = android.content.Intent(this, com.edham.logistics.ui.screens.EliteCameraActivity::class.java)
            startActivityForResult(intent, 1002)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            val path = data?.getStringExtra("IMAGE_PATH")
            if (path != null) {
                Toast.makeText(this, "تم إرفاق الإيصال بنجاح ✓", Toast.LENGTH_SHORT).show()
                // Update UI to show preview if needed
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isSubmitted.observe(this) { sent ->
            if (sent) {
                Toast.makeText(this, "تم إرسال المصروف للمحاسب بنجاح ✅", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
