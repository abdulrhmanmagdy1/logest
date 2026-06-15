package com.edham.logistics.ui.home.workshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.ui.home.workshop.adapter.ProcurementOrder
import com.edham.logistics.ui.home.workshop.adapter.ProcurementOrderAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopProcurementFragment : Fragment() {

    private val viewModel: WorkshopViewModel by viewModels()
    private lateinit var adapter: ProcurementOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workshop_procurement_premium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val rv = view.findViewById<RecyclerView>(R.id.rvProcurementOrders)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProcurementOrderAdapter(emptyList())
        rv.adapter = adapter

        loadMockOrders()
    }

    private fun loadMockOrders() {
        val list = listOf(
            ProcurementOrder("PR-882", "كمبروسور ثرمو كينج", "المبلغ: 3,400 ج.م · الكمية: 1", "WAIT"),
            ProcurementOrder("PR-880", "طقم فلاتر زيت", "المبلغ: 1,100 ج.م · الكمية: 6 طقم", "SHIP")
        )
        adapter.updateData(list)
    }
}
