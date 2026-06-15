package com.edham.logistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ShimmerAdapter : RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder>() {
    
    private val SHIMMER_ITEM_COUNT = 5

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_shimmer_item, parent, false)
        return ShimmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) {
        // No binding needed for shimmer items
    }

    override fun getItemCount(): Int {
        return SHIMMER_ITEM_COUNT
    }

    class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
