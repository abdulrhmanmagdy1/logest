// ============================================
// 🚀 Edham Logistics - Local Database (Room)
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ============================================
 * App Database - قاعدة البيانات المحلية
 * ============================================
 * تخزين البيانات محلياً للسائقين في حال انقطع الإنترنت
 */

@Database(
    entities = [
        CachedShipment::class,
        CachedUser::class,
        CachedAddress::class,
        CachedInvoice::class,
        CachedRoute::class,
        CachedTemperatureLog::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shipmentDao(): ShipmentDao
    abstract fun userDao(): UserDao
    abstract fun addressDao(): AddressDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun routeDao(): RouteDao
    abstract fun temperatureLogDao(): TemperatureLogDao
}

// ============================================
// DAOs - Data Access Objects
// ============================================

@Dao
interface ShipmentDao {
    @Query("SELECT * FROM cached_shipments WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserShipments(userId: String): Flow<List<CachedShipment>>
    
    @Query("SELECT * FROM cached_shipments WHERE trackingNumber = :trackingNumber")
    suspend fun getShipmentByTrackingNumber(trackingNumber: String): CachedShipment?
    
    @Query("SELECT * FROM cached_shipments WHERE status = 'IN_TRANSIT' AND userId = :userId")
    suspend fun getActiveShipments(userId: String): List<CachedShipment>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipment(shipment: CachedShipment)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipments(shipments: List<CachedShipment>)
    
    @Query("UPDATE cached_shipments SET status = :status, updatedAt = :timestamp WHERE id = :shipmentId")
    suspend fun updateShipmentStatus(shipmentId: String, status: String, timestamp: Long)
    
    @Query("DELETE FROM cached_shipments WHERE userId = :userId AND updatedAt < :timestamp")
    suspend fun deleteOldShipments(userId: String, timestamp: Long)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM cached_users WHERE id = :userId")
    suspend fun getUser(userId: String): CachedUser?
    
    @Query("SELECT * FROM cached_users WHERE email = :email")
    suspend fun getUserByEmail(email: String): CachedUser?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: CachedUser)
    
    @Query("UPDATE cached_users SET isActive = :isActive WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, isActive: Boolean)
    
    @Query("DELETE FROM cached_users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
}

@Dao
interface AddressDao {
    @Query("SELECT * FROM cached_addresses WHERE userId = :userId ORDER BY isDefault DESC, createdAt DESC")
    fun getUserAddresses(userId: String): Flow<List<CachedAddress>>
    
    @Query("SELECT * FROM cached_addresses WHERE id = :addressId")
    suspend fun getAddress(addressId: String): CachedAddress?
    
    @Query("SELECT * FROM cached_addresses WHERE isDefault = 1 AND userId = :userId")
    suspend fun getDefaultAddress(userId: String): CachedAddress?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: CachedAddress)
    
    @Query("UPDATE cached_addresses SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefaultAddresses(userId: String)
    
    @Query("UPDATE cached_addresses SET isDefault = 1 WHERE id = :addressId")
    suspend fun setDefaultAddress(addressId: String)
    
    @Query("DELETE FROM cached_addresses WHERE id = :addressId")
    suspend fun deleteAddress(addressId: String)
}

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM cached_invoices WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserInvoices(userId: String): Flow<List<CachedInvoice>>
    
    @Query("SELECT * FROM cached_invoices WHERE status = 'PENDING' AND userId = :userId")
    suspend fun getPendingInvoices(userId: String): List<CachedInvoice>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: CachedInvoice)
    
    @Query("UPDATE cached_invoices SET status = :status, paidAt = :timestamp WHERE id = :invoiceId")
    suspend fun updateInvoiceStatus(invoiceId: String, status: String, timestamp: Long)
    
    @Query("DELETE FROM cached_invoices WHERE userId = :userId AND createdAt < :timestamp")
    suspend fun deleteOldInvoices(userId: String, timestamp: Long)
}

@Dao
interface RouteDao {
    @Query("SELECT * FROM cached_routes WHERE driverId = :driverId AND isActive = 1")
    suspend fun getActiveRoute(driverId: String): CachedRoute?
    
    @Query("SELECT * FROM cached_routes WHERE driverId = :driverId ORDER BY createdAt DESC")
    fun getDriverRoutes(driverId: String): Flow<List<CachedRoute>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: CachedRoute)
    
    @Query("UPDATE cached_routes SET isActive = 0 WHERE driverId = :driverId")
    suspend fun deactivateOldRoutes(driverId: String)
    
    @Query("DELETE FROM cached_routes WHERE driverId = :driverId AND createdAt < :timestamp")
    suspend fun deleteOldRoutes(driverId: String, timestamp: Long)
}

@Dao
interface TemperatureLogDao {
    @Query("SELECT * FROM cached_temperature_logs WHERE shipmentId = :shipmentId ORDER BY timestamp DESC")
    fun getShipmentTemperatureLogs(shipmentId: String): Flow<List<CachedTemperatureLog>>
    
    @Query("SELECT * FROM cached_temperature_logs WHERE shipmentId = :shipmentId AND timestamp >= :startTime")
    suspend fun getRecentTemperatureLogs(shipmentId: String, startTime: Long): List<CachedTemperatureLog>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemperatureLog(log: CachedTemperatureLog)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemperatureLogs(logs: List<CachedTemperatureLog>)
    
    @Query("DELETE FROM cached_temperature_logs WHERE shipmentId = :shipmentId AND timestamp < :timestamp")
    suspend fun deleteOldTemperatureLogs(shipmentId: String, timestamp: Long)
}

// ============================================
// Entities - الكيانات المحلية
// ============================================

@Entity(tableName = "cached_shipments")
data class CachedShipment(
    @PrimaryKey val id: String,
    val trackingNumber: String,
    val userId: String,
    val senderName: String,
    val senderPhone: String,
    val receiverName: String,
    val receiverPhone: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val packageType: String,
    val weight: Double,
    val requiredTemperature: Int,
    val specialInstructions: String,
    val status: String,
    val driverId: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val syncedAt: Long?
)

@Entity(tableName = "cached_users")
data class CachedUser(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val phone: String,
    val role: String,
    val currentRole: String,
    val isActive: Boolean,
    val createdAt: Long,
    val lastLoginAt: Long,
    val syncedAt: Long?
)

@Entity(tableName = "cached_addresses")
data class CachedAddress(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean,
    val createdAt: Long,
    val syncedAt: Long?
)

@Entity(tableName = "cached_invoices")
data class CachedInvoice(
    @PrimaryKey val id: String,
    val userId: String,
    val shipmentId: String,
    val amount: Double,
    val currency: String,
    val status: String,
    val dueDate: Long,
    val paidAt: Long?,
    val createdAt: Long,
    val syncedAt: Long?
)

@Entity(tableName = "cached_routes")
data class CachedRoute(
    @PrimaryKey val id: String,
    val driverId: String,
    val routeName: String,
    val waypoints: String, // JSON string
    val estimatedDuration: Int,
    val distance: Double,
    val isActive: Boolean,
    val createdAt: Long,
    val syncedAt: Long?
)

@Entity(tableName = "cached_temperature_logs")
data class CachedTemperatureLog(
    @PrimaryKey val id: String,
    val shipmentId: String,
    val temperature: Double,
    val humidity: Double,
    val location: String, // JSON string with lat/lng
    val timestamp: Long,
    val syncedAt: Long?
)

// ============================================
// Type Converters
// ============================================

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value.split(",")
    }
    
    @TypeConverter
    fun fromLocation(location: Pair<Double, Double>): String {
        return "${location.first},${location.second}"
    }
    
    @TypeConverter
    fun toLocation(location: String): Pair<Double, Double> {
        val parts = location.split(",")
        return Pair(parts[0].toDouble(), parts[1].toDouble())
    }
}
