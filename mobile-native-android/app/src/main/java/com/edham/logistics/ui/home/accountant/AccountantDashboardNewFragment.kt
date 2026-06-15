package com.edham.logistics.ui.home.accountant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.edham.logistics.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AccountantDashboardNewFragment : Fragment() {

    private val viewModel: AccountantViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_accountant_dashboard_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Elite Security Check
        checkBiometricAuth()

        observeViewModel(view)
        setupListeners(view)
        viewModel.loadDashboardData()
        startHealthMonitoring()

        val session = com.edham.logistics.app.AuthSession.get(requireContext())
        view.findViewById<TextView>(R.id.tvAvatarCircle)?.text = 
            session.displayName?.take(2)?.uppercase() ?: "أح"
    }

    private fun startHealthMonitoring() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                viewModel.refreshHealth()
                handler.postDelayed(this, 5000)
            }
        })
    }

    private fun checkBiometricAuth() {
        val executor = androidx.core.content.ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = androidx.biometric.BiometricPrompt(this, executor,
            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode != androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON && 
                        errorCode != androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED) {
                        Toast.makeText(context, "خطأ أمني: $errString", Toast.LENGTH_LONG).show()
                        activity?.onBackPressed()
                    }
                }
                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                    Toast.makeText(context, "هوية موثقة ✅ مرحباً بك في المنطقة الآمنة", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("تأكيد الهوية المالية")
            .setSubtitle("يجب التحقق من البصمة للوصول لبيانات الخزنة")
            .setNegativeButtonText("إلغاء")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun setupListeners(view: View) {
        view.findViewById<View>(R.id.btnNotifCenter).setOnClickListener {
            // Open Notifications Hub
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.edham.logistics.ui.home.accountant.AccountantNotificationsFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<View>(R.id.btnOpenReports).setOnClickListener {
            startActivity(android.content.Intent(requireContext(), AccountantExportActivity::class.java))
        }

        view.findViewById<View>(R.id.btnIssueReceipt).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ReceiptVoucherFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun observeViewModel(view: View) {
        viewModel.dashboardStats.observe(viewLifecycleOwner) { stats ->
            view.findViewById<TextView>(R.id.tvLiquidity).text = formatCurrency(stats.liquidity)
            view.findViewById<TextView>(R.id.tvOutstanding).text = formatCurrency(stats.outstanding_debts)
            view.findViewById<TextView>(R.id.tvExpenses).text = formatCurrency(stats.monthly_expenses)
            view.findViewById<TextView>(R.id.tvNetProfit).text = formatCurrency(stats.net_profit)
            
            setupRevenueChart(view.findViewById(R.id.revenueChart), stats.revenue_history)
            setupExpenseDonut(view.findViewById(R.id.expenseDonutChart), stats.expense_distribution)
        }

        viewModel.systemLatency.observe(viewLifecycleOwner) { ms ->
            val dot = view.findViewById<View>(R.id.heartbeatDot)
            val label = view.findViewById<TextView>(R.id.tvLatency)
            label.text = "${ms}ms"
            dot.backgroundTintList = android.content.res.ColorStateList.valueOf(
                if (ms < 300) requireContext().getColor(R.color.ed_success) 
                else requireContext().getColor(R.color.ed_rust)
            )
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatCurrency(amount: Double): String {
        return String.format(Locale.getDefault(), "%,.0f", amount)
    }

    private fun setupRevenueChart(chart: BarChart, data: List<com.edham.logistics.core.network.api.ChartData>) {
        val entries = data.mapIndexed { index, item -> BarEntry(index.toFloat(), item.value.toFloat()) }
        val dataSet = BarDataSet(entries, "الإيرادات")
        dataSet.color = requireContext().getColor(R.color.acc_emerald)
        dataSet.setDrawValues(false)
        
        chart.apply {
            this.data = BarData(dataSet)
            xAxis.isEnabled = true
            xAxis.textColor = requireContext().getColor(R.color.acc_text_muted)
            xAxis.setDrawGridLines(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            animateY(1000)
            invalidate()
        }
    }

    private fun setupExpenseDonut(chart: PieChart, data: List<com.edham.logistics.core.network.api.ChartData>) {
        val entries = data.map { PieEntry(it.value.toFloat(), it.label) }
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            requireContext().getColor(R.color.acc_sky),
            requireContext().getColor(R.color.acc_emerald),
            requireContext().getColor(R.color.acc_copper),
            requireContext().getColor(R.color.acc_text_muted)
        )
        dataSet.setDrawValues(false)
        dataSet.sliceSpace = 2f
        
        chart.apply {
            this.data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true
            legend.textColor = requireContext().getColor(R.color.acc_text_muted)
            legend.textSize = 10f
            legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
            legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.CENTER
            legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT
            legend.setDrawInside(false)
            
            setHoleColor(requireContext().getColor(R.color.acc_card_bg))
            holeRadius = 70f
            transparentCircleRadius = 0f
            setDrawEntryLabels(false)
            animateY(1000)
            invalidate()
        }
    }
}
