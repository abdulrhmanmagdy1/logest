package com.edham.logistics.ui.home.accountant

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountantExportActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accountant_export)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<View>(R.id.btnExportRevenue).setOnClickListener {
            Toast.makeText(this, "جاري إنشاء ملف الإيرادات (Excel)... 📊", Toast.LENGTH_LONG).show()
        }

        findViewById<View>(R.id.btnExportExpenses).setOnClickListener {
            Toast.makeText(this, "جاري تصدير سجل المصروفات (PDF)... 💸", Toast.LENGTH_LONG).show()
        }

        findViewById<View>(R.id.btnExportTaxes).setOnClickListener {
            Toast.makeText(this, "جاري تحضير ملف الضريبة والزكاة... ⚖️", Toast.LENGTH_LONG).show()
        }
    }
}
