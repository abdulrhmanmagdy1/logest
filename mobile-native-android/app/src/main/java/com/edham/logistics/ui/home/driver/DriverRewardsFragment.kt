package com.edham.logistics.ui.home.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.edham.logistics.R
import dagger.hilt.android.AndroidEntryPoint

import android.widget.TextView
import androidx.fragment.app.viewModels
import com.edham.logistics.feature.driver.presentation.viewmodels.DriverDashboardViewModel

@AndroidEntryPoint
class DriverRewardsFragment : Fragment() {

    private val viewModel: DriverDashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_rewards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val session = com.edham.logistics.app.AuthSession.get(requireContext())
        session.userId?.let { viewModel.loadData(it) }

        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            val rank = when {
                stats.rating >= 4.8 -> "النخبة الذهبية"
                stats.rating >= 4.5 -> "الكابتن المتميز"
                else -> "السائق المعتمد"
            }
            view.findViewById<TextView>(R.id.tvRankTitle).text = rank
            view.findViewById<TextView>(R.id.tvRewardIcon).text = if (stats.rating >= 4.8) "🏅" else "🎖️"
            view.findViewById<TextView>(R.id.tvRankDesc).text = if (stats.rating >= 4.8) "أنت ضمن أفضل 5% من سائقي الأسطول" else "استمر في الأداء المتميز لرفع رتبتك"
            
            // Progress Binding
            view.findViewById<TextView>(R.id.tvRewardGoalTitle).text = "بونس الالتزام (250 ريال)"
            view.findViewById<TextView>(R.id.tvRewardGoalProgress).text = "8/10 رحلات" // Mock for now, but localized
            view.findViewById<android.widget.ProgressBar>(R.id.pbRewardProgress).progress = 80
        }
    }
}
