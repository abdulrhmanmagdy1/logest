package com.edham.logistics.core.utils

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showSnackbar(message: String) {
    view?.let {
        Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
    }
}

fun Fragment.showLongSnackbar(message: String) {
    view?.let {
        Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
    }
}

fun String.formatCurrency(): String {
    return try {
        val amount = this.toDoubleOrNull() ?: return this
        String.format("%.2f ريال", amount)
    } catch (e: Exception) {
        this
    }
}

fun String.formatWeight(): String {
    return try {
        val weight = this.toDoubleOrNull() ?: return this
        String.format("%.1f كجم", weight)
    } catch (e: Exception) {
        this
    }
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhone(): Boolean {
    return this.matches(Regex("^[+]?[0-9]{10,15}$"))
}

fun String.isStrongPassword(): Boolean {
    return this.length >= 8 &&
            this.any { it.isUpperCase() } &&
            this.any { it.isLowerCase() } &&
            this.any { it.isDigit() }
}
