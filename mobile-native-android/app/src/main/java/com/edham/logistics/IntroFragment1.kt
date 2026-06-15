package com.edham.logistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class IntroFragment1 : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro_1, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Load the first image
        val imageView = view.findViewById<ImageView>(R.id.intro_image)
        Glide.with(this)
            .load("C:\\Users\\facebook\\Downloads\\download.jpg")
            .into(imageView)
            
        // Set texts
        view.findViewById<TextView>(R.id.intro_title).text = "Smart Logistics Solutions"
        view.findViewById<TextView>(R.id.intro_description).text = 
            "Reliable refrigerated, frozen, dry, and general cargo transportation across Saudi Arabia and Gulf countries with real-time operational management."
    }
}
