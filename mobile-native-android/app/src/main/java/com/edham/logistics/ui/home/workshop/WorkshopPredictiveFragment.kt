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
import com.edham.logistics.ui.home.workshop.adapter.PredictiveAlert
import com.edham.logistics.ui.home.workshop.adapter.PredictiveAlertAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopPredictiveFragment : Fragment() {

    private val viewModel: WorkshopViewModel by viewModels()
    private lateinit var adapter: PredictiveAlertAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workshop_predictive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val rv = view.findViewById<RecyclerView>(R.id.rvPredictiveAlerts)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = PredictiveAlertAdapter(emptyList())
        rv.adapter = adapter

        loadMockAlerts()
    }

    private fun loadMockAlerts() {
        val list = listOf(
            PredictiveAlert("شاحنة كريم عبدالله — تغيير سير", "اقتربت من عتبة 100,000 كم", "99,180 كم", "WARNING"),
            PredictiveAlert("شاحنة T-088 — فحص فرامل", "تجاوزت الموعد بـ 15 يوماً", "متأخر 15 يوم", "CRITICAL")
        )
        adapter.updateData(list)
    }
}
