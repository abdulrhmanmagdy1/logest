package com.edham.logistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class IntroFragment2 : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro_2, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Load the second image
        val imageView = view.findViewById<ImageView>(R.id.intro_image)
        Glide.with(this)
            .load("C:\\Users\\facebook\\Downloads\\#شاحنات_نقل_العفش #رويال_لنقل_الاثاث تقدم خدمات….jpg")
            .into(imageView)
            
        // Set texts
        view.findViewById<TextView>(R.id.intro_title).text = "Real-Time Shipment Tracking"
        view.findViewById<TextView>(R.id.intro_description).text = 
            "Track your shipments live with GPS monitoring, estimated delivery times, and instant operational updates anytime, anywhere."
    }
}
