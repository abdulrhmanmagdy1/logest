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
import com.edham.logistics.ui.home.accountant.adapter.WorkshopRequestAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopApprovalsFragment : Fragment() {

    private val viewModel: AccountantViewModel by viewModels()
    private lateinit var adapter: WorkshopRequestAdapter
    private lateinit var swipeRefresh: androidx.swiperefreshlayout.widget.SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_settlement, container, false) // Re-use list layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Update toolbar title if needed (since we re-use layout)
        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.title = "بوابة اعتمادات الورشة"

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener { viewModel.loadWorkshopRequests() }

        setupRecyclerView(view)
        observeViewModel()

        viewModel.loadWorkshopRequests()
    }

    private fun setupRecyclerView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rvSettlements) // Same ID as settlement rv
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = WorkshopRequestAdapter(emptyList(),
            onApprove = { req ->
                viewModel.approveWorkshopRequest(req.id)
                Toast.makeText(context, "تم اعتماد طلب: ${req.title}", Toast.LENGTH_SHORT).show()
            },
            onReject = { req ->
                Toast.makeText(context, "رفض طلب: ${req.title}", Toast.LENGTH_SHORT).show()
            }
        )
        rv.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.workshopRequests.observe(viewLifecycleOwner) { requests ->
            adapter.updateData(requests)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
