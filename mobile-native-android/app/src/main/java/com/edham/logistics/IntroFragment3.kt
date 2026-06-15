package com.edham.logistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class IntroFragment3 : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro_3, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Load the third image
        val imageView = view.findViewById<ImageView>(R.id.intro_image)
        Glide.with(this)
            .load("C:\\Users\\facebook\\Downloads\\Cinematic view of a custom Volvo FH truck with….jpg")
            .into(imageView)
            
        // Set texts
        view.findViewById<TextView>(R.id.intro_title).text = "Cold Chain Transportation"
        view.findViewById<TextView>(R.id.intro_description).text = 
            "Advanced temperature-controlled logistics solutions for food, medical, and sensitive products with full monitoring and safety compliance."
    }
}
