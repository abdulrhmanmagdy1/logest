package com.edham.logistics

import java.util.Locale
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.edham.logistics.databinding.FragmentWorkshopDashboardBinding

class WorkshopDashboardFragment : Fragment() {
    private var _binding: FragmentWorkshopDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkshopDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set Arabic titles
        binding.tvDashboardTitle.text = "لوحة تحكم الورشة"
        binding.tvVehiclesInServiceLabel.text = "مركبات في الخدمة"
        binding.tvScheduledMaintenanceLabel.text = "صيانة مجدولة"
        binding.tvUrgentRepairsLabel.text = "إصلاحات عاجلة"
        binding.tvCompletedTodayLabel.text = "مكتمل اليوم"

        // Animate counter values
        animateCounter(binding.tvVehiclesInService, 7)
        animateCounter(binding.tvScheduledMaintenance, 12)
        animateCounter(binding.tvUrgentRepairs, 3)
        animateCounter(binding.tvCompletedToday, 5)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun animateCounter(textView: TextView, targetValue: Int, suffix: String = "") {
        val handler = Handler(Looper.getMainLooper())
        var currentValue = 0
        val increment = maxOf(1, targetValue / 30)
        val delay = 30L

        val runnable = object : Runnable {
            override fun run() {
                if (currentValue < targetValue) {
                    currentValue = minOf(currentValue + increment, targetValue)
                    textView.text = String.format(Locale.getDefault(), "%d%s", currentValue, suffix)
                    handler.postDelayed(this, delay)
                } else {
                    textView.text = String.format(Locale.getDefault(), "%d%s", targetValue, suffix)
                }
            }
        }
        handler.post(runnable)
    }
}
