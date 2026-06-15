package com.edham.logistics.ui.home.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import androidx.fragment.app.viewModels
import com.edham.logistics.ui.home.driver.adapter.LogBookAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverLogBookFragment : Fragment() {

    private val viewModel: DriverLogViewModel by viewModels()
    private lateinit var adapter: LogBookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_log_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val rv = view.findViewById<RecyclerView>(R.id.rvTripLog)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = LogBookAdapter(emptyList())
        rv.adapter = adapter

        val session = com.edham.logistics.app.AuthSession.get(requireContext())
        session.userId?.let { viewModel.loadHistory(it) }

        viewModel.logs.observe(viewLifecycleOwner) { logs ->
            adapter.updateData(logs)
        }
    }
}
