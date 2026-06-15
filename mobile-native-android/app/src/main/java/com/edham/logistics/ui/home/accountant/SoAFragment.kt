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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_soa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        observeViewModel(view)

        // Mock search for now
        viewModel.loadSoA("CLIENT_001")
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

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
