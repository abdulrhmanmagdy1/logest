package com.edham.logistics.feature.driver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

class NavigationIntegration(private val context: Context) {
    
    fun startNavigation(destination: LatLng, destinationName: String) {
        try {
            val uri = Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}&mode=d")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            context.startActivity(intent)
            Timber.d("Navigation started to $destinationName")
        } catch (e: Exception) {
            Timber.e(e, "Failed to start navigation")
        }
    }
}
