package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.View
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import com.github.chrisbanes.photoview.PhotoView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

/**
 * High-fidelity image inspection tool for receipts and cargo photos.
 */
@AndroidEntryPoint
class ImageZoomActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_zoom)

        val imageUrl = intent.getStringExtra("IMAGE_URL")
        val photoView = findViewById<PhotoView>(R.id.photoView)
        
        findViewById<View>(R.id.btnClose).setOnClickListener { finish() }

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.bg_stat_icon_teal)
                .error(R.drawable.ic_warning)
                .into(photoView)
        }
    }
}
