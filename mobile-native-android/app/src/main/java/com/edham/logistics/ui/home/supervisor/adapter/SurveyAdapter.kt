package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.feature.driver.data.models.SurveySubmission

class SurveyAdapter(private var surveys: List<SurveySubmission>) :
    RecyclerView.Adapter<SurveyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvDriverName)
        val info: TextView = view.findViewById(R.id.tvSurveyInfo)
        val feedback: TextView = view.findViewById(R.id.tvFeedbackText)
        val avatar: TextView = view.findViewById(R.id.tvAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_survey_feedback, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val survey = surveys[position]
        holder.name.text = "سائق #${survey.driverId}"
        holder.info.text = "شحنة #${survey.shipmentId}"
        
        // Find textual feedback if available
        val textAnswer = survey.answers.find { it.answer.length > 5 }?.answer ?: "لا توجد ملاحظات نصية"
        holder.feedback.text = textAnswer
        
        holder.avatar.text = "S"
    }

    override fun getItemCount() = surveys.size

    fun updateData(newSurveys: List<SurveySubmission>) {
        this.surveys = newSurveys
        notifyDataSetChanged()
    }
}
