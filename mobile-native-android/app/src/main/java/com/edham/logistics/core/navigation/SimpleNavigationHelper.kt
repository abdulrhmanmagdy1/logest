package com.edham.logistics.core.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import timber.log.Timber

/**
 * Simple Navigation Helper - Lightweight navigation utilities
 * Provides basic navigation without complex state tracking
 * Use for simple fragment-to-fragment navigation within the same flow
 */
object SimpleNavigationHelper {
    
    // Basic navigation options
    private val defaultNavOptions = NavOptions.Builder()
        .setEnterAnim(android.R.anim.fade_in)
        .setExitAnim(android.R.anim.fade_out)
        .setPopEnterAnim(android.R.anim.fade_in)
        .setPopExitAnim(android.R.anim.fade_out)
        .build()
    
    /**
     * Navigate to a destination using standard NavController
     * Use for simple navigation within the same flow
     */
    fun Fragment.navigateTo(destinationId: Int, args: android.os.Bundle? = null) {
        try {
            val navController = findNavController()
            navController.navigate(destinationId, args, defaultNavOptions)
            Timber.d("Simple navigation to destination: $destinationId")
        } catch (e: Exception) {
            Timber.e(e, "Simple navigation failed for destination: $destinationId")
        }
    }
    
    /**
     * Navigate back using NavController
     */
    fun Fragment.navigateBack(): Boolean {
        return try {
            val navController = findNavController()
            navController.navigateUp()
        } catch (e: Exception) {
            Timber.e(e, "Simple navigation back failed")
            false
        }
    }
    
    /**
     * Check if back navigation is possible
     */
    fun Fragment.canGoBack(): Boolean {
        return try {
            val navController = findNavController()
            navController.previousBackStackEntry != null
        } catch (e: Exception) {
            Timber.e(e, "Failed to check back navigation")
            false
        }
    }
    
    /**
     * Get NavController for the fragment
     */
    private fun Fragment.findNavController(): NavController {
        return NavHostFragment.findNavController(this)
    }
    
    /**
     * Navigate with custom animations
     */
    fun Fragment.navigateToWithAnimation(
        destinationId: Int,
        args: android.os.Bundle? = null,
        enterAnim: Int = android.R.anim.fade_in,
        exitAnim: Int = android.R.anim.fade_out
    ) {
        try {
            val navOptions = NavOptions.Builder()
                .setEnterAnim(enterAnim)
                .setExitAnim(exitAnim)
                .setPopEnterAnim(enterAnim)
                .setPopExitAnim(exitAnim)
                .build()
                
            val navController = findNavController()
            navController.navigate(destinationId, args, navOptions)
            Timber.d("Simple navigation with animation to destination: $destinationId")
        } catch (e: Exception) {
            Timber.e(e, "Simple navigation with animation failed for destination: $destinationId")
        }
    }
}
