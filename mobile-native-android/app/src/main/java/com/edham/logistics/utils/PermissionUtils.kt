package com.edham.logistics.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionUtils(private val context: Context) {
    
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}
