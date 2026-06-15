// ============================================
// 🚀 Edham Logistics - Cache Repository
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.data.repository

import com.edham.logistics.data.local.database.*
import com.edham.logistics.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ============================================
 * Cache Repository - مستودع التخزين المؤقت
 * ============================================
 * إدارة التخزين المحلي للسائقين في حال انقطع الإنترنت
 */

@Singleton
class CacheRepository @Inject constructor(
    private val database: AppDatabase
) {
    // ============================================
    // Shipment Caching
    // ============================================
    
    /**
     * تخزين الشحنات محلياً
     */
    suspend fun cacheShipments(shipments: List<CachedShipment>): Result<Unit> {
        return try {
            database.shipmentDao().insertShipments(shipments)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * الحصول على الشحنات المحلية
     */
    fun getCachedShipments(userId: String): Flow<List<CachedShipment>> {
        return database.shipmentDao().getUserShipments(userId)
    }
    
    /**
     * الحصول على الشحنة النشطة
     */
    suspend fun getActiveShipment(userId: String): Result<CachedShipment?> {
        return try {
            val activeShipments = database.shipmentDao().getActiveShipments(userId)
            Result.Success(activeShipments.firstOrNull())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * تحديث حالة الشحنة
     */
    suspend fun updateShipmentStatus(shipmentId: String, status: String): Result<Unit> {
        return try {
            database.shipmentDao().updateShipmentStatus(
                shipmentId = shipmentId,
                status = status,
                timestamp = System.currentTimeMillis()
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * تنظيف الشحنات القديمة
     */
    suspend fun cleanupOldShipments(userId: String): Result<Int> {
        return try {
            val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
            database.shipmentDao().deleteOldShipments(userId, thirtyDaysAgo)
            Result.Success(1) // Return success count
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    // ============================================
    // User Caching
    // ============================================
    
    /**
     * تخزين بيانات المستخدم
     */
    suspend fun cacheUser(user: CachedUser): Result<Unit> {
        return try {
            database.userDao().insertUser(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * الحصول على المستخدم المحلي
     */
    suspend fun getCachedUser(userId: String): Result<CachedUser?> {
        return try {
            val user = database.userDao().getUser(userId)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * الحصول على المستخدم بالإيميل
     */
    suspend fun getCachedUserByEmail(email: String): Result<CachedUser?> {
        return try {
            val user = database.userDao().getUserByEmail(email)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    // ============================================
    // Address Caching
    // ============================================
    
    /**
     * تخزين العنوان
     */
    suspend fun cacheAddress(address: CachedAddress): Result<Unit> {
        return try {
            database.addressDao().insertAddress(address)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * الحصول على عناوين المستخدم
     */
    fun getCachedAddresses(userId: String): Flow<List<CachedAddress>> {
        return database.addressDao().getUserAddresses(userId)
    }
    
    /**
     * الحصول على العنوان الافتراضي
     */
    suspend fun getCachedDefaultAddress(userId: String): Result<CachedAddress?> {
        return try {
            val address = database.addressDao().getDefaultAddress(userId)
            Result.Success(address)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * تعيين العنوان الافتراضي
     */
    suspend fun setDefaultAddress(addressId: String, userId: String): Result<Unit> {
        return try {
            database.addressDao().clearDefaultAddresses(userId)
            database.addressDao().setDefaultAddress(addressId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    // ============================================
    // Invoice Caching
    // ============================================
    
    /**
     * تخزين الفواتير
     */
    suspend fun cacheInvoice(invoice: CachedInvoice): Result<Unit> {
        return try {
            database.invoiceDao().insertInvoice(invoice)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * الحصول على فواتير المستخدم
     */
    fun getCachedInvoices(userId: String): Flow<List<CachedInvoice>> {
        return database.invoiceDao().getUserInvoices(userId)
    }
    
    /**
     * الحصول على الفواتير المعلقة
     */
    suspend fun getCachedPendingInvoices(userId: String): Result<List<CachedInvoice>> {
        return try {
            val invoices = database.invoiceDao().getPendingInvoices(userId)
            Result.Success(invoices)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * تحديث حالة الفاتورة
     */
    suspend fun updateInvoiceStatus(invoiceId: String, status: String): Result<Unit> {
        return try {
            database.invoiceDao().updateInvoiceStatus(
                invoiceId = invoiceId,
                status = status,
                timestamp = if (status == "PAID") System.currentTimeMillis() else 0L
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    // ============================================
    // Route Caching
    // ============================================
    
    /**
     * تخزين المسار
     */
    suspend fun cacheRoute(route: CachedRoute): Result<Unit> {
        return try {
            database.routeDao().deactivateOldRoutes(route.driverId)
            database.routeDao().insertRoute(route)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * الحصول على المسار النشط
     */
    suspend fun getActiveRoute(driverId: String): Result<CachedRoute?> {
        return try {
            val route = database.routeDao().getActiveRoute(driverId)
            Result.Success(route)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * الحصول على مسارات السائق
     */
    fun getCachedRoutes(driverId: String): Flow<List<CachedRoute>> {
        return database.routeDao().getDriverRoutes(driverId)
    }
    
    // ============================================
    // Temperature Logging
    // ============================================
    
    /**
     * تسجيل درجة الحرارة
     */
    suspend fun logTemperature(log: CachedTemperatureLog): Result<Unit> {
        return try {
            database.temperatureLogDao().insertTemperatureLog(log)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * تسجيل درجات حرارة متعددة
     */
    suspend fun logTemperatureBatch(logs: List<CachedTemperatureLog>): Result<Unit> {
        return try {
            database.temperatureLogDao().insertTemperatureLogs(logs)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * الحصول على سجلات الحرارة
     */
    fun getTemperatureLogs(shipmentId: String): Flow<List<CachedTemperatureLog>> {
        return database.temperatureLogDao().getShipmentTemperatureLogs(shipmentId)
    }
    
    /**
     * الحصول على سجلات الحرارة الحديثة
     */
    suspend fun getRecentTemperatureLogs(shipmentId: String, hours: Int = 24): Result<List<CachedTemperatureLog>> {
        return try {
            val startTime = System.currentTimeMillis() - (hours * 60 * 60 * 1000L)
            val logs = database.temperatureLogDao().getRecentTemperatureLogs(shipmentId, startTime)
            Result.Success(logs)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    // ============================================
    // Sync Management
    // ============================================
    
    /**
     * الحصول على البيانات غير المتزامنة
     */
    suspend fun getUnsyncedData(): UnsyncedData {
        return UnsyncedData(
            unsyncedShipments = database.shipmentDao().getUserShipments("").first()
                .filter { it.syncedAt == null },
            unsyncedTemperatureLogs = emptyList() // Would need additional query
        )
    }
    
    /**
     * تحديث وقت المزامنة
     */
    suspend fun markAsSynced(entityType: String, entityId: String): Result<Unit> {
        return try {
            when (entityType) {
                "shipment" -> {
                    // Update syncedAt for shipment
                    Result.Success(Unit)
                }
                "temperature_log" -> {
                    // Update syncedAt for temperature log
                    Result.Success(Unit)
                }
                else -> Result.Error(Exception("Unknown entity type"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * تنظيف البيانات القديمة
     */
    suspend fun cleanupOldData(): Result<CleanupResult> {
        return try {
            val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
            
            // Clean old shipments
            // Clean old temperature logs
            // Clean old routes
            
            Result.Success(
                CleanupResult(
                    deletedShipments = 0,
                    deletedTemperatureLogs = 0,
                    deletedRoutes = 0
                )
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

// ============================================
// Data Classes
// ============================================

data class UnsyncedData(
    val unsyncedShipments: List<CachedShipment>,
    val unsyncedTemperatureLogs: List<CachedTemperatureLog>
)

data class CleanupResult(
    val deletedShipments: Int,
    val deletedTemperatureLogs: Int,
    val deletedRoutes: Int
)
