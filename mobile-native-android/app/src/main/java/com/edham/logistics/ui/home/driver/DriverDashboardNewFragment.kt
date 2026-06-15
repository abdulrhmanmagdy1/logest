package com.edham.logistics.ui.home.driver

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.edham.logistics.R
import com.edham.logistics.feature.driver.presentation.viewmodels.DriverDashboardViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DriverDashboardNewFragment : Fragment() {

    private val viewModel: DriverDashboardViewModel by viewModels()
    private lateinit var tvTodayEarnings: TextView
    private lateinit var tempGauge: com.edham.logistics.ui.custom.TemperatureGauge

    @Inject lateinit var voiceAssistant: com.edham.logistics.core.voice.TacticalVoiceAssistant

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_dashboard_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        tvTodayEarnings = view.findViewById(R.id.tvTodayEarnings)
        tempGauge = view.findViewById(R.id.tempGauge)
        
        setupTabs(view)
        setupListeners(view)
        observeViewModel(view)
        
        val session = com.edham.logistics.app.AuthSession.get(requireContext())
        session.userId?.let { viewModel.loadData(it) }

        startTempSimulation()
        
        voiceAssistant.speak("مرحباً كابتن، قمرة القيادة الذكية جاهزة. أتمنى لك رحلة آمنة.")
    }

    private fun startTempSimulation() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                val mockTemp = (-22..-15).random().toFloat()
                tempGauge.setTemperature(mockTemp)
                
                if (mockTemp > -10f) {
                    voiceAssistant.speak("تنبيه: ارتفاع مفاجئ في درجة حرارة المبرد. يرجى التحقق من الأختام.")
                }

                handler.postDelayed(this, 10000)
            }
        })
    }

    private fun observeViewModel(view: View) {
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            tvTodayEarnings.text = String.format(java.util.Locale.getDefault(), "%,.0f", stats.todayEarnings)
            view.findViewById<TextView>(R.id.tvDriverScore).text = stats.rating.toInt().toString()
            
            // Dynamic Goal Binding
            val goal = 2000.0 // In real app, stats.dailyGoal
            val percent = if (goal > 0) (stats.todayEarnings / goal * 100).toInt() else 0
            view.findViewById<TextView>(R.id.tvDailyGoal).text = "الهدف اليومي: %,.0f ريال".format(goal)
            view.findViewById<TextView>(R.id.tvGoalPercent).text = "$percent%"
            view.findViewById<android.widget.ProgressBar>(R.id.pbGoal).progress = percent
        }

        val session = com.edham.logistics.app.AuthSession.get(requireContext())
        view.findViewById<TextView>(R.id.tvDriverName).text = "كابتن ${session.displayName ?: "إدهام"}"
        view.findViewById<TextView>(R.id.tvDriverRank).text = "▲ سائق ذهبي · الأسطول أ"

        viewModel.activeTrip.observe(viewLifecycleOwner) { trip ->
            if (trip != null) {
                view.findViewById<TextView>(R.id.btnStartMission).text = "مهمة نشطة: #${trip.tripId}"
                view.findViewById<TextView>(R.id.tvPickupName).text = trip.origin
                view.findViewById<TextView>(R.id.tvPickupAddress).text = "مستودع التحميل"
                view.findViewById<TextView>(R.id.tvDeliveryName).text = trip.destination
                view.findViewById<TextView>(R.id.tvDeliveryAddress).text = "موقع التسليم"
            } else {
                view.findViewById<TextView>(R.id.btnStartMission).text = "لا توجد مهام نشطة حالياً"
                view.findViewById<TextView>(R.id.tvPickupName).text = "---"
                view.findViewById<TextView>(R.id.tvPickupAddress).text = "---"
                view.findViewById<TextView>(R.id.tvDeliveryName).text = "---"
                view.findViewById<TextView>(R.id.tvDeliveryAddress).text = "---"
            }
        }

        viewModel.isSosSent.observe(viewLifecycleOwner) { sent ->
            if (sent) {
                Toast.makeText(context, "🚨 تم إرسال استغاثة للمشرف مع موقعك الحالي!", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setupTabs(view: View) {
        val tabs = view.findViewById<TabLayout>(R.id.cockpitTabs)
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when(tab.position) {
                    0 -> showHomeSection()
                    1 -> showMissionSection()
                    2 -> showCockpitSection()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupListeners(view: View) {
        view.findViewById<View>(R.id.btnStartMission).setOnClickListener {
            val trip = viewModel.activeTrip.value
            if (trip != null) {
                // If not started, start it
                if (trip.status.lowercase() == "pending" || trip.status.lowercase() == "assigned") {
                    viewModel.startTrip(trip.id)
                    Toast.makeText(context, "إشعال المحرك... تم بدء الرحلة بنجاح! 🚛", Toast.LENGTH_LONG).show()
                }
                // Open tactical mission view
                startActivity(android.content.Intent(requireContext(), DriverMissionActivity::class.java))
            } else {
                Toast.makeText(context, "لا توجد مهمة نشطة حالياً", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<View>(R.id.btnSOS).setOnClickListener {
            showSosConfirmDialog()
        }

        view.findViewById<View>(R.id.btnScorecard).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DriverRewardsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showSosConfirmDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("إرسال استغاثة (SOS)")
            .setMessage("هل أنت متأكد من إرسال نداء استغاثة للمشرف؟ سيتم مشاركة موقعك وصوت من الكابينة.")
            .setPositiveButton("إرسال الآن") { _, _ ->
                val session = com.edham.logistics.app.AuthSession.get(requireContext())
                session.userId?.let { viewModel.sendSOS(it, 0.0, 0.0) }
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun showHomeSection() {
        // Logic to swap UI or fragments
    }

    private fun showMissionSection() {
        // Navigate to mission or update layout
    }

    private fun showCockpitSection() {
        // Navigate to cockpit or update layout
    }
}
