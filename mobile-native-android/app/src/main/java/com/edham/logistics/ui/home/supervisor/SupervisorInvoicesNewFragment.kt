package com.edham.logistics.ui.home.supervisor

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
class SupervisorInvoicesNewFragment : Fragment() {

    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var invoiceAdapter: InvoiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supervisor_invoices_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        observeViewModel(view)
        
        viewModel.loadInvoices()
        viewModel.loadDashboardData()
    }

    private fun setupRecyclerView(view: View) {
        val rvInvoices = view.findViewById<RecyclerView>(R.id.rvInvoices)
        rvInvoices.layoutManager = LinearLayoutManager(requireContext())
        invoiceAdapter = InvoiceAdapter(emptyList())
        rvInvoices.adapter = invoiceAdapter
    }

    private fun observeViewModel(view: View) {
        viewModel.invoices.observe(viewLifecycleOwner) { invoices ->
            invoiceAdapter.updateData(invoices)
            val totalPending = invoices.filter { it.status != "PAID" }.sumOf { it.amount }
            view.findViewById<TextView>(R.id.tv_total_pending).text = String.format("إجمالي معلق: %.0f ج", totalPending)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
