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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_debt_aging, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                Toast.makeText(context, "تسجيل تحصيل مبلغ ${debt.amount} ج", Toast.LENGTH_SHORT).show()
            }
        )
        rv.adapter = adapter
    }

    private fun observeViewModel(view: View) {
        viewModel.debtAging.observe(viewLifecycleOwner) { report ->
            view.findViewById<TextView>(R.id.tvAging30).text = report.t30_days.toInt().toString()
            view.findViewById<TextView>(R.id.tvAging60).text = report.t60_days.toInt().toString()
            view.findViewById<TextView>(R.id.tvAging90).text = report.t90_plus_days.toInt().toString()
            adapter.updateData(report.clients)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
