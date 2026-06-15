package com.edham.logistics.ui.home.supervisor

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
import com.edham.logistics.ui.home.supervisor.adapter.SurveyAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupervisorSurveyNewFragment : Fragment() {

    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var surveyAdapter: SurveyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supervisor_survey_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        observeViewModel(view)
        
        viewModel.loadSurveyData()
    }

    private fun setupRecyclerView(view: View) {
        val rvSurveys = view.findViewById<RecyclerView>(R.id.rvDriverFeedbacks)
        rvSurveys.layoutManager = LinearLayoutManager(requireContext())
        surveyAdapter = SurveyAdapter(emptyList())
        rvSurveys.adapter = surveyAdapter
    }

    private fun observeViewModel(view: View) {
        viewModel.surveys.observe(viewLifecycleOwner) { surveys ->
            surveyAdapter.updateData(surveys)
            view.findViewById<TextView>(R.id.tv_survey_summary).text = "${surveys.size} استبيان جديد"
            view.findViewById<TextView>(R.id.tv_responses_count).text = surveys.size.toString()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
