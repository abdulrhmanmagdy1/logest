package com.edham.logistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val btnLogout = view.findViewById<TextView>(R.id.btnLogout)
        
        btnLogout.setOnClickListener {
            // Handle logout
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            // Navigate to login screen
        }
    }
}
