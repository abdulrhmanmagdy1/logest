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
        setupSearch(view)
        observeViewModel()

        viewModel.loadSettlements()
    }

    private fun setupSearch(view: View) {
        view.findViewById<android.widget.EditText>(R.id.etSearchDriver)?.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s.toString().lowercase()
                val fullList = viewModel.settlements.value ?: emptyList()
                val filtered = fullList.filter { it.driverName.lowercase().contains(query) }
                adapter.updateData(filtered)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupRecyclerView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rvSettlements)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = DriverSettlementAdapter(emptyList(), 
            onExpenseApprove = { expense ->
                viewModel.approveExpense(expense.id)
            },
            onExpenseReject = { expense ->
                showRejectionDialog(expense)
            },
            onImageClick = { url ->
                val intent = android.content.Intent(requireContext(), com.edham.logistics.ui.screens.ImageZoomActivity::class.java)
                intent.putExtra("IMAGE_URL", url)
                startActivity(intent)
            }
        )
        rv.adapter = adapter
    }

    private fun showRejectionDialog(expense: com.edham.logistics.core.network.api.DriverExpense) {
        val etReason = android.widget.EditText(requireContext())
        etReason.hint = "ادخل سبب الرفض"
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("رفض المصروف")
            .setMessage("يرجى توضيح سبب رفض مصروف: ${expense.description}")
            .setView(etReason)
            .setPositiveButton("تأكيد الرفض") { _, _ ->
                val reason = etReason.text.toString()
                if (reason.isNotEmpty()) {
                    viewModel.rejectExpense(expense.id, reason)
                } else {
                    Toast.makeText(context, "يجب إدخال السبب", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.settlements.observe(viewLifecycleOwner) { settlements ->
            adapter.updateData(settlements)
            view?.findViewById<View>(R.id.emptyState)?.visibility = 
                if (settlements.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
