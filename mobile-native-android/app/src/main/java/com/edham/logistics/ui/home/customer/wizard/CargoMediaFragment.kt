package com.edham.logistics.ui.home.customer.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.edham.logistics.R

import android.app.Activity
import android.content.Intent
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels

class CargoMediaFragment : Fragment() {
    private val viewModel: CargoWizardViewModel by activityViewModels()
    
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data?.clipData != null) {
                val uris = mutableListOf<String>()
                for (i in 0 until data.clipData!!.itemCount) {
                    uris.add(data.clipData!!.getItemAt(i).uri.toString())
                }
                viewModel.cargoPhotos.value = uris
            } else if (data?.data != null) {
                viewModel.cargoPhotos.value = listOf(data.data.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cargo_media, container, false)
        
        val textCount = view.findViewById<TextView>(R.id.textImagesCount)
        
        view.findViewById<View>(R.id.btnGallery).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            pickImage.launch(intent)
        }
        
        view.findViewById<View>(R.id.btnCamera).setOnClickListener {
            // Simplified: Just open gallery for now to ensure compatibility
            view.findViewById<View>(R.id.btnGallery).performClick()
        }

        viewModel.cargoPhotos.observe(viewLifecycleOwner) { uris ->
            if (uris.isNotEmpty()) {
                textCount.visibility = View.VISIBLE
                textCount.text = "تم اختيار ${uris.size} صور"
            } else {
                textCount.visibility = View.GONE
            }
        }
        
        return view
    }
}
