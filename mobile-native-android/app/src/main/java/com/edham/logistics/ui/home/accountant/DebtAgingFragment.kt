package com.edham.logistics.ui.home.accountant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.ui.home.accountant.adapter.DebtAgingAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DebtAgingFragment : Fragment() {

    private val viewModel: AccountantViewModel by viewModels()
    private lateinit var adapter: DebtAgingAdapter
    private lateinit var swipeRefresh: androidx.swiperefreshlayout.widget.SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_debt_aging, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener { viewModel.loadDebtReport() }

        setupRecyclerView(view)
        observeViewModel(view)

        viewModel.loadDebtReport()
    }

    private fun setupRecyclerView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rvDebts)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = DebtAgingAdapter(emptyList(),
            onWhatsapp = { debt ->
                Toast.makeText(context, "فتح واتساب لتذكير ${debt.clientName}", Toast.LENGTH_SHORT).show()
            },
            onCollect = { debt ->
                showCollectionDialog(debt)
            }
        )
        rv.adapter = adapter
    }

    private fun showCollectionDialog(debt: com.edham.logistics.core.network.api.ClientDebt) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_manual_override, null)
        val etAmount = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_override_price)
        val etNotes = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_override_notes)
        
        // Reuse manual override layout but update hints via code or create new layout
        etAmount.setText(debt.amount.toString())
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("تحصيل مديونية: ${debt.clientName}")
            .setView(dialogView)
            .setPositiveButton("تأكيد التحصيل") { _, _ ->
                val amount = etAmount.text.toString().toDoubleOrNull()
                val notes = etNotes.text.toString()
                if (amount != null) {
                    viewModel.collectPayment(debt.clientId, amount, "MANUAL", notes)
                }
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun observeViewModel(view: View) {
        viewModel.debtAging.observe(viewLifecycleOwner) { report ->
            view.findViewById<TextView>(R.id.tvAging30).text = report.t30_days.toInt().toString()
            view.findViewById<TextView>(R.id.tvAging60).text = report.t60_days.toInt().toString()
            view.findViewById<TextView>(R.id.tvAging90).text = report.t90_plus_days.toInt().toString()
            adapter.updateData(report.clients)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
