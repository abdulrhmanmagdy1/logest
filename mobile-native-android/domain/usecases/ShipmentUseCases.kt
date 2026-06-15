// ============================================
// 🚀 Edham Logistics - Shipment Use Cases
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.domain.usecases

import com.edham.logistics.data.model.Shipment
import com.edham.logistics.data.repository.ShipmentRepository
import com.edham.logistics.domain.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ============================================
 * Shipment Use Cases - حالات استخدام الشحنات
 * ============================================
 * تنظيم منطق الأعمال لإدارة الشحنات
 */

@Singleton
class CreateShipmentUseCase @Inject constructor(
    private val shipmentRepository: ShipmentRepository
) {
    /**
     * إنشاء شحنة جديدة
     */
    suspend operator fun invoke(
        senderName: String,
        senderPhone: String,
        receiverName: String,
        receiverPhone: String,
        pickupAddress: String,
        deliveryAddress: String,
        packageType: String,
        weight: Double,
        temperature: Int,
        specialInstructions: String
    ): Result<Shipment> {
        return try {
            val shipment = Shipment(
                id = "SHIP_${System.currentTimeMillis()}",
                trackingNumber = generateTrackingNumber(),
                senderName = senderName,
                senderPhone = senderPhone,
                receiverName = receiverName,
                receiverPhone = receiverPhone,
                pickupAddress = pickupAddress,
                deliveryAddress = deliveryAddress,
                packageType = packageType,
                weight = weight,
                requiredTemperature = temperature,
                specialInstructions = specialInstructions,
                status = "pending",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            shipmentRepository.createShipment(shipment)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * إنشاء رقم تتبع فريد
     */
    private fun generateTrackingNumber(): String {
        val prefix = "EDH"
        val timestamp = System.currentTimeMillis().toString().takeLast(8)
        val random = (1000..9999).random()
        return "$prefix$timestamp$random"
    }
}

@Singleton
class TrackShipmentUseCase @Inject constructor(
    private val shipmentRepository: ShipmentRepository
) {
    /**
     * تتبع الشحنة بالرقم
     */
    suspend operator fun invoke(trackingNumber: String): Result<Shipment> {
        return try {
            val shipment = shipmentRepository.getShipmentByTrackingNumber(trackingNumber)
            if (shipment != null) {
                Result.Success(shipment)
            } else {
                Result.Error(Exception("Shipment not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * الحصول على تتبع الشحنة كـ Flow
     */
    operator fun invokeAsFlow(trackingNumber: String): Flow<Shipment?> {
        return shipmentRepository.getShipmentByTrackingNumberFlow(trackingNumber)
    }
}

@Singleton
class GetShipmentsUseCase @Inject constructor(
    private val shipmentRepository: ShipmentRepository
) {
    /**
     * الحصول على قائمة الشحنات للمستخدم
     */
    operator fun invoke(userId: String): Flow<List<Shipment>> {
        return shipmentRepository.getUserShipments(userId)
    }

    /**
     * الحصول على الشحنات النشطة
     */
    suspend operator fun invokeActive(userId: String): Result<List<Shipment>> {
        return try {
            val shipments = shipmentRepository.getActiveShipments(userId)
            Result.Success(shipments)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * الحصول على الشحنات المكتملة
     */
    suspend operator fun invokeCompleted(userId: String): Result<List<Shipment>> {
        return try {
            val shipments = shipmentRepository.getCompletedShipments(userId)
            Result.Success(shipments)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

@Singleton
class UpdateShipmentStatusUseCase @Inject constructor(
    private val shipmentRepository: ShipmentRepository
) {
    /**
     * تحديث حالة الشحنة
     */
    suspend operator fun invoke(
        shipmentId: String,
        newStatus: String,
        driverId: String? = null
    ): Result<Shipment> {
        return try {
            val updatedShipment = shipmentRepository.updateShipmentStatus(
                shipmentId = shipmentId,
                status = newStatus,
                driverId = driverId,
                timestamp = System.currentTimeMillis()
            )
            Result.Success(updatedShipment)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

@Singleton
class GetShipmentHistoryUseCase @Inject constructor(
    private val shipmentRepository: ShipmentRepository
) {
    /**
     * الحصول على سجل الشحنات
     */
    operator fun invoke(userId: String, limit: Int = 50): Flow<List<Shipment>> {
        return shipmentRepository.getShipmentHistory(userId, limit)
    }
}

@Singleton
class ValidateShipmentDataUseCase @Inject constructor() {
    /**
     * التحقق من صحة بيانات الشحنة
     */
    operator fun invoke(
        senderName: String,
        senderPhone: String,
        receiverName: String,
        receiverPhone: String,
        pickupAddress: String,
        deliveryAddress: String,
        weight: Double,
        temperature: Int
    ): ValidationResult {
        val errors = mutableListOf<String>()

        if (senderName.isBlank()) errors.add("اسم المرسل مطلوب")
        if (senderPhone.isBlank()) errors.add("رقم هاتف المرسل مطلوب")
        if (receiverName.isBlank()) errors.add("اسم المستقبل مطلوب")
        if (receiverPhone.isBlank()) errors.add("رقم هاتف المستقبل مطلوب")
        if (pickupAddress.isBlank()) errors.add("عنوان الاستلام مطلوب")
        if (deliveryAddress.isBlank()) errors.add("عنوان التسليم مطلوب")
        if (weight <= 0) errors.add("الوزن يجب أن يكون أكبر من صفر")
        if (temperature < -20 || temperature > 10) errors.add("درجة الحرارة يجب أن تكون بين -20 و 10 مئوية")

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val errors: List<String>) : ValidationResult()
}
