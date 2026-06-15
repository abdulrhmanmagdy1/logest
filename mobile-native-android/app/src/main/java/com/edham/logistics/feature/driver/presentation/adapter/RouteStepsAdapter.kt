package com.edham.logistics.feature.driver.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.databinding.ItemRouteStepBinding
import com.edham.logistics.feature.driver.domain.model.RouteStep
import javax.inject.Inject

class RouteStepsAdapter @Inject constructor() : ListAdapter<RouteStep, RouteStepsAdapter.RouteStepViewHolder>(RouteStepDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteStepViewHolder {
        val binding = ItemRouteStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RouteStepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteStepViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RouteStepViewHolder(
        private val binding: ItemRouteStepBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(step: RouteStep) {
            binding.apply {
                textStepNumber.text = "${adapterPosition + 1}"
                textInstruction.text = step.instruction
                textDistance.text = "${String.format("%.1f", step.distance)} كم"
                textDuration.text = "${step.duration} دقيقة"
                
                // Set step number background color
                val stepColor = when (adapterPosition) {
                    0 -> android.graphics.Color.parseColor("#4CAF50") // Green for first step
                    currentList.size - 1 -> android.graphics.Color.parseColor("#FF5722") // Orange for last step
                    else -> android.graphics.Color.parseColor("#2196F3") // Blue for middle steps
                }
                
                textStepNumber.setBackgroundColor(stepColor)
                
                // Highlight current step (in real app, this would be based on actual location)
                if (adapterPosition == 0) {
                    root.setBackgroundColor(android.graphics.Color.parseColor("#E3F2FD"))
                } else {
                    root.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            }
        }
    }

    private class RouteStepDiffCallback : DiffUtil.ItemCallback<RouteStep>() {
        override fun areItemsTheSame(oldItem: RouteStep, newItem: RouteStep): Boolean {
            return oldItem.startLocation.latitude == newItem.startLocation.latitude &&
                   oldItem.startLocation.longitude == newItem.startLocation.longitude
        }

        override fun areContentsTheSame(oldItem: RouteStep, newItem: RouteStep): Boolean {
            return oldItem == newItem
        }
    }
}
