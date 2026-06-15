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
import com.edham.logistics.ui.home.accountant.adapter.SoAAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoAFragment : Fragment() {

    private val viewModel: AccountantViewModel by viewModels()
    private lateinit var adapter: SoAAdapter
    private lateinit var swipeRefresh: androidx.swiperefreshlayout.widget.SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_soa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        val etSearch = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSearchClient)
        
        swipeRefresh.setOnRefreshListener { 
            val query = etSearch?.text?.toString()
            if (!query.isNullOrEmpty()) {
                viewModel.loadSoA(query)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }

        setupRecyclerView(view)
        setupListeners(view)
        setupSearch(view)
        observeViewModel(view)
    }

    private fun setupSearch(view: View) {
        val etSearch = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSearchClient)
        etSearch?.setOnEditorActionListener { v, _, _ ->
            val query = v.text.toString()
            if (query.isNotEmpty()) {
                viewModel.loadSoA(query)
            }
            true
        }
    }

    private fun setupListeners(view: View) {
        view.findViewById<View>(R.id.btnExportPdf).setOnClickListener {
            viewModel.soa.value?.let { soa ->
                com.edham.logistics.core.utils.PdfGenerator.generateSoA(requireContext(), soa)
            } ?: Toast.makeText(context, "يرجى البحث عن عميل أولاً", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnDateRange).setOnClickListener {
            Toast.makeText(context, "تحديد الفترة المحاسبية...", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnPrintSoA).setOnClickListener {
            Toast.makeText(context, "إرسال للطابعة اللاسلكية...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rvSoA)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = SoAAdapter(emptyList())
        rv.adapter = adapter
    }

    private fun observeViewModel(view: View) {
        viewModel.soa.observe(viewLifecycleOwner) { soa ->
            view.findViewById<TextView>(R.id.tvTotalInvoiced).text = soa.totalInvoiced.toInt().toString()
            view.findViewById<TextView>(R.id.tvTotalPaid).text = soa.totalPaid.toInt().toString()
            view.findViewById<TextView>(R.id.tvTotalRemaining).text = soa.remaining.toInt().toString()
            adapter.updateData(soa.entries)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
