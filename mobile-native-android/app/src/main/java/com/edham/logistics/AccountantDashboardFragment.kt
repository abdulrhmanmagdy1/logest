package com.edham.logistics

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AccountantDashboardFragment : Fragment() {
    private lateinit var tvMonthlyRevenue: TextView
    private lateinit var tvPendingInvoices: TextView
    private lateinit var tvOverduePayments: TextView
    private lateinit var tvAverageInvoice: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_accountant_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        tvMonthlyRevenue = view.findViewById(R.id.tv_monthly_revenue)
        tvPendingInvoices = view.findViewById(R.id.tv_pending_invoices)
        tvOverduePayments = view.findViewById(R.id.tv_overdue_payments)
        tvAverageInvoice = view.findViewById(R.id.tv_average_invoice)

        // Set Arabic titles
        view.findViewById<TextView>(R.id.tv_dashboard_title).text = "لوحة تحكم المحاسب"
        view.findViewById<TextView>(R.id.tv_monthly_revenue_label).text = "إيرادات شهرية"
        view.findViewById<TextView>(R.id.tv_pending_invoices_label).text = "فواتير معلقة"
        view.findViewById<TextView>(R.id.tv_overdue_payments_label).text = "مدفوعات متأخرة"
        view.findViewById<TextView>(R.id.tv_average_invoice_label).text = "متوسط الفاتورة"

        // Animate counter values
        animateCounter(tvMonthlyRevenue, 245000, " ريال")
        animateCounter(tvPendingInvoices, 18)
        animateCounter(tvOverduePayments, 7)
        animateCounter(tvAverageInvoice, 5420, " ريال")
    }

    private fun animateCounter(textView: TextView, targetValue: Int, suffix: String = "") {
        val handler = Handler(Looper.getMainLooper())
        var currentValue = 0
        val increment = Math.max(1, targetValue / 30)
        val delay = 30L

        val runnable = object : Runnable {
            override fun run() {
                if (currentValue < targetValue) {
                    currentValue = Math.min(currentValue + increment, targetValue)
                    textView.text = "$currentValue$suffix"
                    handler.postDelayed(this, delay)
                } else {
                    textView.text = "$targetValue$suffix"
                }
            }
        }
        handler.post(runnable)
    }
}
