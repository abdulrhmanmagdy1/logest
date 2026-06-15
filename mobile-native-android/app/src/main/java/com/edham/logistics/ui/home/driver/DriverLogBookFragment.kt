package com.edham.logistics.ui.home.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.ui.home.driver.adapter.LogBookAdapter
import com.edham.logistics.ui.home.driver.adapter.LogEntry
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverLogBookFragment : Fragment() {

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
        
        val mockLogs = listOf(
            LogEntry("الرياض ← الدمام", "142 كم • دواجن مجمدة • 2س 10د", "+380 ريال", "اليوم، 08:30"),
            LogEntry("جدة ← مكة المكرمة", "86 كم • ألبان • 1س 25د", "+210 ريال", "أمس، 14:00"),
            LogEntry("المدينة ← ينبع", "196 كم • أدوية • 2س 45د", "+490 ريال", "أمس، 09:15"),
            LogEntry("الرياض ← القصيم", "312 كم • خضروات • 3س 30د", "+650 ريال", "29/5، 07:00")
        )
        
        rv.adapter = LogBookAdapter(mockLogs)
    }
}
