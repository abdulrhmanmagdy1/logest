package com.edham.logistics.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapPickerActivity : BaseActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_picker)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<View>(R.id.btnConfirmLocation).setOnClickListener {
            val center = googleMap?.cameraPosition?.target ?: return@setOnClickListener
            val intent = Intent()
            intent.putExtra("LAT", center.latitude)
            intent.putExtra("LNG", center.longitude)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val saudi = LatLng(23.8859, 45.0792)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(saudi, 5.0f))
    }
}
