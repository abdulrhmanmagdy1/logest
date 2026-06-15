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
import com.edham.logistics.ui.home.supervisor.adapter.InvoiceAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountantDashboardNewFragment : Fragment() {

    private val viewModel: AccountantViewModel by viewModels()
    private lateinit var invoiceAdapter: InvoiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_accountant_dashboard_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        observeViewModel(view)
        
        viewModel.loadFinancialData()
    }

    private fun setupRecyclerView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rvPendingInvoices)
        rv.layoutManager = LinearLayoutManager(requireContext())
        invoiceAdapter = InvoiceAdapter(emptyList())
        rv.adapter = invoiceAdapter
    }

    private fun observeViewModel(view: View) {
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            view.findViewById<TextView>(R.id.tvNetProfit).text = String.format("%,.2f", stats.net_profit)
            view.findViewById<TextView>(R.id.tvTotalRevenue).text = String.format("%,.0f ر.س", stats.total_revenue)
            view.findViewById<TextView>(R.id.tvTotalExpenses).text = String.format("%,.0f ر.س", stats.total_expenses)
            view.findViewById<TextView>(R.id.tvOutstanding).text = String.format("%,.0f", stats.outstanding_invoices)
            view.findViewById<TextView>(R.id.tvOverdue).text = String.format("%,.0f", stats.urgent_overdue)
        }

        viewModel.pendingInvoices.observe(viewLifecycleOwner) { invoices ->
            invoiceAdapter.updateData(invoices)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
