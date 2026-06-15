package com.edham.logistics.ui.home.supervisor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edham.logistics.R

class ImagePreviewAdapter(private var images: List<String>) :
    RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.ivPreview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_preview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = images[position]
        Glide.with(holder.itemView.context)
            .load(url)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.imageView)
    }

    override fun getItemCount() = images.size

    fun updateData(newImages: List<String>) {
        this.images = newImages
        notifyDataSetChanged()
    }
}
