package com.edham.logistics.ui.home.supervisor

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.Load
import com.edham.logistics.ui.BaseActivity
import com.edham.logistics.ui.home.supervisor.adapter.AuditLogAdapter
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SupervisorOrderDetailsActivity : BaseActivity() {

    private val viewModel: SupervisorViewModel by viewModels()
    private var loadId: String? = null
    private lateinit var auditAdapter: AuditLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_order_details)

        loadId = intent.getStringExtra("LOAD_ID")
        
        findViewById<View>(R.id.toolbar).setOnClickListener { finish() }

        setupUI()
        observeViewModel()
        
        loadId?.let { viewModel.loadAuditLog(it) }
        
        // Bind incoming load data if available
        val loadData: Load? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("LOAD_DATA", Load::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Load>("LOAD_DATA")
        }
        
        loadData?.let { bindLoad(it) }
    }

    private fun bindLoad(load: Load) {
        findViewById<TextView>(R.id.tvOrderId).text = load.id
        findViewById<TextView>(R.id.tvOrigin).text = load.from
        findViewById<TextView>(R.id.tvDest).text = load.to
        findViewById<TextView>(R.id.tvCargoType).text = "بضاعة عامة"
        findViewById<TextView>(R.id.tvWeight).text = load.weight
        findViewById<TextView>(R.id.tvTemp).text = load.temperature ?: "غير محدد"
        findViewById<TextView>(R.id.tvStatus).text = load.status

        // Financial P&L Calculator
        val clientPriceStr = load.price.replace(Regex("[^0-9.]"), "")
        val clientPrice = clientPriceStr.toDoubleOrNull() ?: 0.0
        val fuelCost = clientPrice * 0.25 
        val driverComm = clientPrice * 0.15 
        val netProfit = clientPrice - (fuelCost + driverComm)

        findViewById<TextView>(R.id.tvClientPrice).text = load.price
        findViewById<TextView>(R.id.tvFuelCost).text = String.format(Locale.getDefault(), "%.0f ريال", fuelCost)
        findViewById<TextView>(R.id.tvDriverComm).text = String.format(Locale.getDefault(), "%.0f ريال", driverComm)
        findViewById<TextView>(R.id.tvNetProfit).text = String.format(Locale.getDefault(), "%.0f ريال", netProfit)
    }

    private fun setupUI() {
        auditAdapter = AuditLogAdapter(emptyList())
        val rvAudit = findViewById<RecyclerView>(R.id.rvAuditLog)
        rvAudit.layoutManager = LinearLayoutManager(this)
        rvAudit.adapter = auditAdapter

        findViewById<View>(R.id.btnAssignDriver).setOnClickListener {
            showDriverSelection()
        }

        findViewById<View>(R.id.tvOrderId).setOnClickListener {
            showOverrideDialog()
        }

        findViewById<View>(R.id.tvStatus).setOnClickListener {
            showStatusUpdateDialog()
        }
    }

    private fun showOverrideDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_manual_override, null)
        val etPrice = dialogView.findViewById<TextInputEditText>(R.id.et_override_price)
        val etNotes = dialogView.findViewById<TextInputEditText>(R.id.et_override_notes)
        
        AlertDialog.Builder(this)
            .setTitle("تعديل يدوي للتسعيرة")
            .setView(dialogView)
            .setPositiveButton("تحديث") { _, _ ->
                val newPrice = etPrice.text.toString().toDoubleOrNull()
                val notes = etNotes.text.toString()
                if (newPrice != null && loadId != null) {
                    viewModel.updatePrice(loadId!!, newPrice, notes)
                } else {
                    Toast.makeText(this, "يرجى إدخال سعر صحيح", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun showStatusUpdateDialog() {
        val statuses = arrayOf("PENDING", "ASSIGNED", "PICKED_UP", "ON_THE_WAY", "DELIVERED", "CANCELLED")
        
        AlertDialog.Builder(this)
            .setTitle("تحديث حالة الشحنة يدوياً")
            .setItems(statuses) { _, which ->
                val selectedStatus = statuses[which]
                loadId?.let { id ->
                    viewModel.updateTripStatus(id, selectedStatus)
                }
            }
            .show()
    }

    private fun showDriverSelection() {
        val bottomSheet = DriverSelectionBottomSheet { driver ->
            loadId?.let { id ->
                viewModel.assignDriver(id, driver.id)
                Toast.makeText(this, "تم تعيين السائق ${driver.firstName}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        bottomSheet.show(supportFragmentManager, "DriverSelection")
    }

    private fun observeViewModel() {
        viewModel.auditLog.observe(this) { entries ->
            auditAdapter.updateData(entries)
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}
