package com.edham.logistics.ui.home.accountant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.ui.home.accountant.adapter.DriverSettlementAdapter
import dagger.hilt.android.AndroidEntryPoint
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

@AndroidEntryPoint
class DriverSettlementFragment : Fragment() {

    private val viewModel: AccountantViewModel by viewModels()
    private lateinit var adapter: DriverSettlementAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_settlement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener { viewModel.loadSettlements() }

        setupRecyclerView(view)
        observeViewModel()

        viewModel.loadSettlements()
    }

    private fun setupRecyclerView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rvSettlements)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = DriverSettlementAdapter(emptyList(), 
            onExpenseApprove = { expense ->
                viewModel.approveExpense(expense.id)
            },
            onExpenseReject = { expense ->
                // show reject dialog
                Toast.makeText(context, "رفض المصروف: ${expense.description}", Toast.LENGTH_SHORT).show()
            }
        )
        rv.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.settlements.observe(viewLifecycleOwner) { settlements ->
            adapter.updateData(settlements)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
