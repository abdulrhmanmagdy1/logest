package com.edham.logistics.ui.home.supervisor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import com.edham.logistics.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * Helper to generate dynamic, color-coded truck markers for the Supervisor map.
 */
object MapMarkerHelper {

    enum class TruckStatus {
        NORMAL,   // Green/Teal
        DELAYED,  // Orange
        CRITICAL  // Red (Heat alert / major delay)
    }

    fun getTruckMarker(context: Context, status: TruckStatus, heading: Float = 0f): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_truck) ?: return BitmapDescriptorFactory.defaultMarker()
        
        val color = when (status) {
            TruckStatus.NORMAL -> ContextCompat.getColor(context, R.color.status_success)
            TruckStatus.DELAYED -> ContextCompat.getColor(context, R.color.status_warning)
            TruckStatus.CRITICAL -> ContextCompat.getColor(context, R.color.status_error)
        }

        // Create a bitmap and draw the tinted and rotated drawable
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Handle rotation in canvas if heading is provided
        if (heading != 0f) {
            canvas.rotate(heading, (canvas.width / 2).toFloat(), (canvas.height / 2).toFloat())
        }

        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        drawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
