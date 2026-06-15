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
import com.edham.logistics.ui.home.supervisor.adapter.PartsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupervisorPartsNewFragment : Fragment() {

    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var partsAdapter: PartsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supervisor_parts_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        observeViewModel(view)
        
        viewModel.loadPartsData()
        viewModel.loadDashboardData()
    }

    private fun setupRecyclerView(view: View) {
        val rvInventory = view.findViewById<RecyclerView>(R.id.rvPartsInventory)
        rvInventory.layoutManager = LinearLayoutManager(requireContext())
        partsAdapter = PartsAdapter(emptyList())
        rvInventory.adapter = partsAdapter
    }

    private fun observeViewModel(view: View) {
        viewModel.parts.observe(viewLifecycleOwner) { parts ->
            partsAdapter.updateData(parts)
            
            // Dynamic summary update
            view.findViewById<TextView>(R.id.tv_parts_summary).text = 
                "${parts.size} صنف مسجل في المستودع"
            
            view.findViewById<TextView>(R.id.tv_total_parts_count).text = parts.size.toString()
            view.findViewById<TextView>(R.id.tv_low_stock_count).text = 
                parts.count { it.status.uppercase() == "LOW_STOCK" }.toString()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}
