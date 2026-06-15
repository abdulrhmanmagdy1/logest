package com.edham.logistics.presentation.util

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateFormatter @Inject constructor() {
    
    private val arabicLocale = Locale("ar", "SA")
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", arabicLocale)
    private val outputTimeFormat = SimpleDateFormat("hh:mm a", arabicLocale)
    private val outputDateTimeFormat = SimpleDateFormat("dd MMMM yyyy hh:mm a", arabicLocale)
    
    fun formatDate(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            date?.let { outputDateFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun formatTime(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            date?.let { outputTimeFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun formatDateTime(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            date?.let { outputDateTimeFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun formatRelativeTime(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            val now = Date()
            val diff = now.time - (date?.time ?: 0)
            
            when {
                diff < 60 * 1000 -> "الآن"
                diff < 60 * 60 * 1000 -> {
                    val minutes = diff / (60 * 1000)
                    "منذ ${minutes} دقيقة"
                }
                diff < 24 * 60 * 60 * 1000 -> {
                    val hours = diff / (60 * 60 * 1000)
                    "منذ ${hours} ساعة"
                }
                diff < 7 * 24 * 60 * 60 * 1000 -> {
                    val days = diff / (24 * 60 * 60 * 1000)
                    "منذ ${days} يوم"
                }
                else -> {
                    formatDate(dateString)
                }
            }
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun isToday(dateString: String): Boolean {
        return try {
            val date = inputFormat.parse(dateString)
            val today = Calendar.getInstance()
            val calendar = Calendar.getInstance()
            calendar.time = date ?: return false
            
            today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
        } catch (e: Exception) {
            false
        }
    }
}
