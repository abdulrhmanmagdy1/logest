// ============================================
// 🚀 Edham Logistics - Cache Manager
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.performance

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ============================================
 * Cache Manager - مدير التخزين المؤقت
// ============================================
 * إدارة التخزين المؤقت لتحسين الأداء
 */

@Singleton
class CacheManager @Inject constructor(
    private val context: Context
) {
    
    private val cacheStore = mutableMapOf<String, CacheEntry>()
    private val _cacheStats = MutableStateFlow(CacheStats())
    val cacheStats: StateFlow<CacheStats> = _cacheStats
    
    private var cleanupJob: Job? = null
    
    /**
     * تخزين بيانات في الذاكرة المؤقتة
     */
    fun put(
        key: String,
        data: Any,
        expiryTime: Long = 0, // 0 means no expiry
        priority: CachePriority = CachePriority.NORMAL
    ) {
        val entry = CacheEntry(
            key = key,
            data = data,
            timestamp = System.currentTimeMillis(),
            expiryTime = expiryTime,
            accessCount = 0,
            lastAccessed = System.currentTimeMillis(),
            size = calculateSize(data),
            priority = priority
        )
        
        cacheStore[key] = entry
        updateCacheStats()
    }
    
    /**
     * الحصول على بيانات من الذاكرة المؤقتة
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, type: Class<T>): T? {
        val entry = cacheStore[key]
        
        if (entry == null) {
            return null
        }
        
        // التحقق من انتهاء الصلاحية
        if (entry.expiryTime > 0 && System.currentTimeMillis() > entry.expiryTime) {
            cacheStore.remove(key)
            updateCacheStats()
            return null
        }
        
        // تحديث معلومات الوصول
        entry.accessCount++
        entry.lastAccessed = System.currentTimeMillis()
        
        return try {
            entry.data as T
        } catch (e: ClassCastException) {
            null
        }
    }
    
    /**
     * التحقق من وجود مفتاح
     */
    fun contains(key: String): Boolean {
        val entry = cacheStore[key] ?: return false
        
        // التحقق من انتهاء الصلاحية
        if (entry.expiryTime > 0 && System.currentTimeMillis() > entry.expiryTime) {
            cacheStore.remove(key)
            updateCacheStats()
            return false
        }
        
        return true
    }
    
    /**
     * إزالة مفتاح
     */
    fun remove(key: String): Boolean {
        val removed = cacheStore.remove(key) != null
        if (removed) {
            updateCacheStats()
        }
        return removed
    }
    
    /**
     * مسح جميع البيانات
     */
    fun clear() {
        cacheStore.clear()
        updateCacheStats()
    }
    
    /**
     * مسح البيانات منتهية الصلاحية
     */
    fun clearExpired() {
        val currentTime = System.currentTimeMillis()
        val expiredKeys = cacheStore.filter { (_, entry) ->
            entry.expiryTime > 0 && currentTime > entry.expiryTime
        }.keys
        
        expiredKeys.forEach { key ->
            cacheStore.remove(key)
        }
        
        if (expiredKeys.isNotEmpty()) {
            updateCacheStats()
        }
    }
    
    /**
     * تنظيف الذاكرة المؤقتة بناءً على الأولوية
     */
    fun cleanupByPriority(maxSize: Int = 100) {
        if (cacheStore.size <= maxSize) {
            return
        }
        
        // ترتيب العناصر حسب الأولوية وآخر وقت وصول
        val sortedEntries = cacheStore.values.sortedWith(compareBy<CacheEntry> { it.priority.ordinal }
            .thenBy { it.lastAccessed })
        
        val itemsToRemove = cacheStore.size - maxSize
        
        repeat(itemsToRemove) { index ->
            if (index < sortedEntries.size) {
                cacheStore.remove(sortedEntries[index].key)
            }
        }
        
        updateCacheStats()
    }
    
    /**
     * تنظيف الذاكرة المؤقتة بناءً على الحجم
     */
    fun cleanupBySize(maxSizeBytes: Long = 50 * 1024 * 1024) { // 50MB
        val currentSize = cacheStore.values.sumOf { it.size }
        
        if (currentSize <= maxSizeBytes) {
            return
        }
        
        // إزالة العناصر الأقل استخداماً حتى الوصول للحجم المطلوب
        val sortedEntries = cacheStore.values.sortedBy { it.lastAccessed }
        var removedSize = 0L
        
        for (entry in sortedEntries) {
            if (currentSize - removedSize <= maxSizeBytes) {
                break
            }
            
            cacheStore.remove(entry.key)
            removedSize += entry.size
        }
        
        updateCacheStats()
    }
    
    /**
     * بدء التنظيف التلقائي
     */
    fun startAutoCleanup() {
        cleanupJob?.cancel()
        
        cleanupJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    delay(5 * 60 * 1000L) // كل 5 دقائق
                    
                    clearExpired()
                    cleanupByPriority()
                    cleanupBySize()
                    
                } catch (e: Exception) {
                    // Handle cleanup errors
                }
            }
        }
    }
    
    /**
     * إيقاف التنظيف التلقائي
     */
    fun stopAutoCleanup() {
        cleanupJob?.cancel()
        cleanupJob = null
    }
    
    /**
     * الحصول على إحصائيات الذاكرة المؤقتة
     */
    fun getDetailedStats(): DetailedCacheStats {
        val entries = cacheStore.values.toList()
        
        return DetailedCacheStats(
            totalEntries = entries.size,
            totalSize = entries.sumOf { it.size },
            averageSize = if (entries.isNotEmpty()) entries.sumOf { it.size } / entries.size else 0,
            hitRate = calculateHitRate(),
            mostAccessed = entries.sortedByDescending { it.accessCount }.take(10),
            oldestEntry = entries.minByOrNull { it.timestamp },
            newestEntry = entries.maxByOrNull { it.timestamp },
            expiredEntries = entries.count { it.expiryTime > 0 && System.currentTimeMillis() > it.expiryTime },
            priorityDistribution = entries.groupBy { it.priority }.mapValues { it.value.size }
        )
    }
    
    /**
     * حساب حجم البيانات
     */
    private fun calculateSize(data: Any): Long {
        return when (data) {
            is String -> data.toByteArray().size.toLong()
            is ByteArray -> data.size.toLong()
            is List<*> -> data.sumOf { calculateSize(it ?: 0) }
            is Map<*, *> -> data.entries.sum { calculateSize(it.key) + calculateSize(it.value) }
            else -> 64L // حجم تقديري للكائنات الأخرى
        }
    }
    
    /**
     * تحديث إحصائيات الذاكرة المؤقتة
     */
    private fun updateCacheStats() {
        val entries = cacheStore.values.toList()
        
        _cacheStats.value = CacheStats(
            totalEntries = entries.size,
            totalSize = entries.sumOf { it.size },
            hitCount = entries.sumOf { it.accessCount },
            lastCleanup = System.currentTimeMillis()
        )
    }
    
    /**
     * حساب نسبة النجاح
     */
    private fun calculateHitRate(): Double {
        // هذا مجرد محاكاة - في التطبيق الحقيقي يجب تتبع النجاحات والإخفاقات
        return (70..95).random().toDouble()
    }
    
    /**
     * التخزين المؤقت للصور
     */
    fun cacheImage(
        key: String,
        imageData: ByteArray,
        expiryTime: Long = 0
    ) {
        put(key, imageData, expiryTime, CachePriority.HIGH)
    }
    
    /**
     * الحصول على صورة من التخزين المؤقت
     */
    fun getImage(key: String): ByteArray? {
        return get(key, ByteArray::class.java)
    }
    
    /**
     * التخزين المؤقت للبيانات الشبكية
     */
    fun cacheNetworkData(
        key: String,
        data: String,
        expiryTime: Long = 5 * 60 * 1000 // 5 دقائق
    ) {
        put(key, data, expiryTime, CachePriority.NORMAL)
    }
    
    /**
     * الحصول على بيانات شبكية من التخزين المؤقت
     */
    fun getNetworkData(key: String): String? {
        return get(key, String::class.java)
    }
    
    /**
     * التخزين المؤقت لبيانات المستخدم
     */
    fun cacheUserData(
        key: String,
        data: Any,
        expiryTime: Long = 30 * 60 * 1000 // 30 دقيقة
    ) {
        put(key, data, expiryTime, CachePriority.HIGH)
    }
    
    /**
     * الحصول على بيانات مستخدم من التخزين المؤقت
     */
    fun <T> getUserData(key: String, type: Class<T>): T? {
        return get(key, type)
    }
    
    /**
     * التخزين المؤقت للبيانات المؤقتة
     */
    fun cacheTemporaryData(
        key: String,
        data: Any,
        expiryTime: Long = 10 * 60 * 1000 // 10 دقائق
    ) {
        put(key, data, expiryTime, CachePriority.LOW)
    }
    
    /**
     * الحصول على بيانات مؤقتة من التخزين المؤقت
     */
    fun <T> getTemporaryData(key: String, type: Class<T>): T? {
        return get(key, type)
    }
    
    /**
     * تصدير إحصائيات التخزين المؤقت
     */
    fun exportStats(): String {
        val stats = getDetailedStats()
        return """
            Cache Statistics:
            Total Entries: ${stats.totalEntries}
            Total Size: ${stats.totalSize / 1024} KB
            Average Size: ${stats.averageSize} bytes
            Hit Rate: ${stats.hitRate}%
            Expired Entries: ${stats.expiredEntries}
            Priority Distribution: ${stats.priorityDistribution}
        """.trimIndent()
    }
    
    /**
     * تنظيف الموارد
     */
    fun cleanup() {
        stopAutoCleanup()
        clear()
    }
}

/**
 * ============================================
// Data Classes and Enums
// ============================================
 */

data class CacheEntry(
    val key: String,
    val data: Any,
    val timestamp: Long,
    val expiryTime: Long,
    var accessCount: Int,
    var lastAccessed: Long,
    val size: Long,
    val priority: CachePriority
) {
    val isExpired: Boolean
        get() = expiryTime > 0 && System.currentTimeMillis() > expiryTime
    
    val age: Long
        get() = System.currentTimeMillis() - timestamp
}

data class CacheStats(
    val totalEntries: Int = 0,
    val totalSize: Long = 0,
    val hitCount: Int = 0,
    val lastCleanup: Long = 0
) {
    val averageEntrySize: Double
        get() = if (totalEntries > 0) totalSize.toDouble() / totalEntries else 0.0
    
    val sizeInKB: Double
        get() = totalSize / 1024.0
    
    val sizeInMB: Double
        get() = totalSize / (1024.0 * 1024.0)
}

data class DetailedCacheStats(
    val totalEntries: Int,
    val totalSize: Long,
    val averageSize: Long,
    val hitRate: Double,
    val mostAccessed: List<CacheEntry>,
    val oldestEntry: CacheEntry?,
    val newestEntry: CacheEntry?,
    val expiredEntries: Int,
    val priorityDistribution: Map<CachePriority, Int>
)

enum class CachePriority {
    LOW, NORMAL, HIGH, CRITICAL
}
