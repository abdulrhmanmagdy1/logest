# 📱 Data Models - مسار العميل في Edham

> **جميع الـ Data Classes والـ Models اللي تحتاجها تطبيق العميل**

---

## 📂 هيكل المشروع

```
app/src/main/java/com/edham/customer/
├─ models/
│  ├─ Shipment.kt
│  ├─ Payment.kt
│  ├─ Location.kt
│  ├─ Driver.kt
│  ├─ Cargo.kt
│  ├─ Notification.kt
│  ├─ Invoice.kt
│  ├─ Rating.kt
│  ├─ Wallet.kt
│  ├─ Address.kt
│  ├─ ApiResponse.kt
│  └─ requests/
│     ├─ CreateShipmentRequest.kt
│     ├─ PaymentRequest.kt
│     └─ RatingRequest.kt
```

---

## 1️⃣ Address.kt - نموذج العناوين

```kotlin
package com.edham.customer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * نموذج العنوان - يُستخدم للاستقبال والتسليم
 */
data class Address(
    @SerializedName("_id")
    val id: String = "",
    
    @SerializedName("street")
    val street: String = "",
    
    @SerializedName("city")
    val city: String = "",
    
    @SerializedName("region")
    val region: String = "",
    
    @SerializedName("postalCode")
    val postalCode: String = "",
    
    @SerializedName("country")
    val country: String = "SA",
    
    @SerializedName("coordinates")
    val coordinates: Coordinates = Coordinates(),
    
    @SerializedName("buildingNumber")
    val buildingNumber: String = "",
    
    @SerializedName("floorNumber")
    val floorNumber: String = "",
    
    @SerializedName("unitNumber")
    val unitNumber: String = "",
    
    @SerializedName("additionalDirections")
    val additionalDirections: String = "",
    
    @SerializedName("isDefault")
    val isDefault: Boolean = false,
    
    @SerializedName("label")
    val label: String = "" // "Home", "Work", "Other"
) : Serializable {
    
    fun isValid(): Boolean {
        return street.isNotEmpty() && 
               city.isNotEmpty() && 
               region.isNotEmpty() &&
               coordinates.latitude != 0.0 &&
               coordinates.longitude != 0.0
    }
    
    fun getFullAddress(): String {
        return "$street, $city, $region, $country"
    }
}

data class Coordinates(
    @SerializedName("lat")
    val latitude: Double = 0.0,
    
    @SerializedName("lng")
    val longitude: Double = 0.0
) : Serializable
```

---

## 2️⃣ Cargo.kt - نموذج البضاعة

```kotlin
package com.edham.customer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * نموذج البضاعة/المحتويات
 */
data class Cargo(
    @SerializedName("type")
    val type: CargoType = CargoType.GENERAL,
    
    @SerializedName("description")
    val description: String = "",
    
    @SerializedName("weight")
    val weight: Weight = Weight(),
    
    @SerializedName("dimensions")
    val dimensions: Dimensions = Dimensions(),
    
    @SerializedName("quantity")
    val quantity: Int = 1,
    
    @SerializedName("value")
    val value: Double = 0.0,
    
    @SerializedName("specialRequirements")
    val specialRequirements: List<String> = emptyList(),
    
    @SerializedName("images")
    val images: List<String> = emptyList(),
    
    @SerializedName("hazardous")
    val hazardous: Boolean = false,
    
    @SerializedName("fragile")
    val fragile: Boolean = false,
    
    @SerializedName("requiresSignature")
    val requiresSignature: Boolean = false,
    
    @SerializedName("photoDocumentation")
    val photoDocumentation: Boolean = false
) : Serializable {
    
    fun isValid(): Boolean {
        return description.isNotEmpty() && 
               weight.value > 0 &&
               quantity > 0
    }
    
    fun getTotalWeight(): Double {
        return weight.value * quantity
    }
}

enum class CargoType(val displayName: String) {
    @SerializedName("general")
    GENERAL("عام"),
    
    @SerializedName("frozen")
    FROZEN("مجمد"),
    
    @SerializedName("pharmaceutical")
    PHARMACEUTICAL("دواء"),
    
    @SerializedName("food")
    FOOD("غذائي"),
    
    @SerializedName("medical")
    MEDICAL("معدات طبية"),
    
    @SerializedName("electronics")
    ELECTRONICS("إلكترونيات"),
    
    @SerializedName("valuable")
    VALUABLE("نقود/مجوهرات"),
    
    @SerializedName("hazardous")
    HAZARDOUS("مواد خطرة"),
    
    @SerializedName("documents")
    DOCUMENTS("وثائق"),
    
    @SerializedName("plants")
    PLANTS("نباتات"),
    
    @SerializedName("animals")
    ANIMALS("حيوانات"),
    
    @SerializedName("other")
    OTHER("أخرى")
}

data class Weight(
    @SerializedName("value")
    val value: Double = 0.0,
    
    @SerializedName("unit")
    val unit: String = "kg"
) : Serializable

data class Dimensions(
    @SerializedName("length")
    val length: Double = 0.0,
    
    @SerializedName("width")
    val width: Double = 0.0,
    
    @SerializedName("height")
    val height: Double = 0.0,
    
    @SerializedName("unit")
    val unit: String = "cm"
) : Serializable {
    
    fun getVolume(): Double {
        return length * width * height
    }
}
```

---

## 3️⃣ Location.kt - نموذج الموقع

```kotlin
package com.edham.customer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

/**
 * نموذج الموقع الجغرافي والموقع التتبع الحية
 */
data class Location(
    @SerializedName("latitude")
    val latitude: Double = 0.0,
    
    @SerializedName("longitude")
    val longitude: Double = 0.0,
    
    @SerializedName("accuracy")
    val accuracy: Float = 0f,
    
    @SerializedName("speed")
    val speed: Float = 0f,
    
    @SerializedName("heading")
    val heading: Float = 0f,
    
    @SerializedName("altitude")
    val altitude: Double = 0.0,
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @SerializedName("provider")
    val provider: String = "GPS"
) : Serializable {
    
    fun isValid(): Boolean {
        return latitude != 0.0 && longitude != 0.0 && accuracy > 0
    }
    
    fun getCoordinatePair(): String {
        return "$latitude, $longitude"
    }
}

/**
 * سجل الموقع - لتخزين جميع تحديثات الموقع
 */
data class LocationHistory(
    @SerializedName("shipmentId")
    val shipmentId: String = "",
    
    @SerializedName("locations")
    val locations: List<Location> = emptyList(),
    
    @SerializedName("startTime")
    val startTime: Long = 0L,
    
    @SerializedName("endTime")
    val endTime: Long = 0L,
    
    @SerializedName("totalDistance")
    val totalDistance: Double = 0.0,
    
    @SerializedName("averageSpeed")
    val averageSpeed: Float = 0f
) : Serializable {
    
    fun getTotalTime(): Long {
        return endTime - startTime
    }
}
```

---

## 4️⃣ Driver.kt - نموذج السائق

```kotlin
package com.edham.customer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * نموذج السائق - معلومات السائق المسند إليه الرحلة
 */
data class Driver(
    @SerializedName("_id")
    val id: String = "",
    
    @SerializedName("firstName")
    val firstName: String = "",
    
    @SerializedName("lastName")
    val lastName: String = "",
    
    @SerializedName("email")
    val email: String = "",
    
    @SerializedName("phone")
    val phone: String = "",
    
    @SerializedName("profileImage")
    val profileImage: String = "",
    
    @SerializedName("licenseNumber")
    val licenseNumber: String = "",
    
    @SerializedName("licenseExpiry")
    val licenseExpiry: Long = 0L,
    
    @SerializedName("rating")
    val rating: Double = 0.0,
    
    @SerializedName("totalRatings")
    val totalRatings: Int = 0,
    
    @SerializedName("status")
    val status: DriverStatus = DriverStatus.OFFLINE,
    
    @SerializedName("totalTrips")
    val totalTrips: Int = 0,
    
    @SerializedName("yearsExperience")
    val yearsExperience: Int = 0,
    
    @SerializedName("currentLocation")
    val currentLocation: Location = Location(),
    
    @SerializedName("isAvailable")
    val isAvailable: Boolean = false,
    
    @SerializedName("verified")
    val verified: Boolean = false
) : Serializable {
    
    fun getFullName(): String {
        return "$firstName $lastName"
    }
    
    fun getRatingDisplay(): String {
        return String.format("%.1f", rating)
    }
    
    fun isVerifiedAndActive(): Boolean {
        return verified && isAvailable && status == DriverStatus.ONLINE
    }
}

enum class DriverStatus {
    @SerializedName("online")
    ONLINE,
    
    @SerializedName("offline")
    OFFLINE,
    
    @SerializedName("busy")
    BUSY,
    
    @SerializedName("on_break")
    ON_BREAK
}
```

---

## 5️⃣ Shipment.kt - نموذج الشحنة (الأساسي)

```kotlin
package com.edham.customer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * نموذج الشحنة - الكائن الرئيسي للنظام
 */
data class Shipment(
    @SerializedName("_id")
    val id: String = "",
    
    @SerializedName("trackingNumber")
    val trackingNumber: String = "",
    
    @SerializedName("customerId")
    val customerId: String = "",
    
    @SerializedName("status")
    val status: ShipmentStatus = ShipmentStatus.PENDING,
    
    @SerializedName("pickup")
    val pickup: ShipmentLocation = ShipmentLocation(),
    
    @SerializedName("delivery")
    val delivery: ShipmentLocation = ShipmentLocation(),
    
    @SerializedName("cargo")
    val cargo: Cargo = Cargo(),
    
    @SerializedName("driver")
    val driver: Driver? = null,
    
    @SerializedName("truck")
    val truck: Truck? = null,
    
    @SerializedName("service")
    val service: ServiceType = ServiceType.STANDARD,
    
    @SerializedName("pricing")
    val pricing: Pricing = Pricing(),
    
    @SerializedName("payment")
    val payment: Payment? = null,
    
    @SerializedName("statusHistory")
    val statusHistory: List<StatusHistoryItem> = emptyList(),
    
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),
    
    @SerializedName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis(),
    
    @SerializedName("completedAt")
    val completedAt: Long? = null,
    
    @SerializedName("rating")
    val rating: Rating? = null,
    
    @SerializedName("proofOfDelivery")
    val proofOfDelivery: ProofOfDelivery? = null
) : Serializable {
    
    fun isActive(): Boolean {
        return status !in listOf(
            ShipmentStatus.CANCELLED,
            ShipmentStatus.COMPLETED,
            ShipmentStatus.FAILED
        )
    }
    
    fun isDelivered(): Boolean {
        return status == ShipmentStatus.DELIVERED || 
               status == ShipmentStatus.COMPLETED
    }
    
    fun canBeCancelled(): Boolean {
        return status in listOf(
            ShipmentStatus.PENDING,
            ShipmentStatus.CONFIRMED,
            ShipmentStatus.ASSIGNED
        )
    }
    
    fun getStatusDisplay(): String {
        return when (status) {
            ShipmentStatus.PENDING -> "معلقة"
            ShipmentStatus.CONFIRMED -> "مؤكدة"
            ShipmentStatus.ASSIGNED -> "معينة"
            ShipmentStatus.AT_PICKUP -> "في موقع الاستقبال"
            ShipmentStatus.PICKED_UP -> "تم الاستقبال"
            ShipmentStatus.ON_THE_WAY -> "في الطريق"
            ShipmentStatus.ARRIVING_SOON -> "سيصل قريباً"
            ShipmentStatus.AT_DELIVERY -> "في موقع التسليم"
            ShipmentStatus.DELIVERED -> "تم التسليم"
            ShipmentStatus.COMPLETED -> "مكتملة"
            ShipmentStatus.CANCELLED -> "ملغاة"
            ShipmentStatus.FAILED -> "فشلت"
        }
    }
}

enum class ShipmentStatus {
    @SerializedName("pending")
    PENDING,
    
    @SerializedName("confirmed")
    CONFIRMED,
    
    @SerializedName("assigned")
    ASSIGNED,
    
    @SerializedName("at_pickup")
    AT_PICKUP,
    
    @SerializedName("picked_up")
    PICKED_UP,
    
    @SerializedName("on_the_way")
    ON_THE_WAY,
    
    @SerializedName("arriving_soon")
    ARRIVING_SOON,
    
    @SerializedName("at_delivery")
    AT_DELIVERY,
    
    @SerializedName("delivered")
    DELIVERED,
    
    @SerializedName("completed")
    COMPLETED,
    
    @SerializedName("cancelled")
    CANCELLED,
    
    @SerializedName("failed")
    FAILED
}

data class ShipmentLocation(
    @SerializedName("address")
    val address: Address = Address(),
    
    @SerializedName("contact")
    val contact: ContactInfo = ContactInfo(),
    
    @SerializedName("scheduledTime")
    val scheduledTime: Long? = null,
    
    @SerializedName("actualTime")
    val actualTime: Long? = null,
    
    @SerializedName("instructions")
    val instructions: String = ""
) : Serializable

data class ContactInfo(
    @SerializedName("name")
    val name: String = "",
    
    @SerializedName("phone")
    val phone: String = "",
    
    @SerializedName("email")
    val email: String = ""
) : Serializable

data class Truck(
    @SerializedName("_id")
    val id: String = "",
    
    @SerializedName("plateNumber")
    val plateNumber: String = "",
    
    @SerializedName("type")
    val type: String = "", // "box", "van", "truck"
    
    @SerializedName("color")
    val color: String = "",
    
    @SerializedName("capacity")
    val capacity: Double = 0.0,
    
    @SerializedName("model")
    val model: String = "",
    
    @SerializedName("year")
    val year: Int = 0,
    
    @SerializedName("status")
    val status: String = "available"
) : Serializable

data class Pricing(
    @SerializedName("basePrice")
    val basePrice: Double = 100.0,
    
    @SerializedName("distancePrice")
    val distancePrice: Double = 0.0,
    
    @SerializedName("weightPrice")
    val weightPrice: Double = 0.0,
    
    @SerializedName("servicePrice")
    val servicePrice: Double = 0.0,
    
    @SerializedName("additionalFees")
    val additionalFees: Double = 0.0,
    
    @SerializedName("discounts")
    val discounts: Double = 0.0,
    
    @SerializedName("subtotal")
    val subtotal: Double = 0.0,
    
    @SerializedName("tax")
    val tax: Double = 0.0,
    
    @SerializedName("total")
    val total: Double = 0.0,
    
    @SerializedName("currency")
    val currency: String = "SAR"
) : Serializable {
    
    fun getTotalWithoutTax(): Double {
        return subtotal - discounts
    }
}

data class StatusHistoryItem(
    @SerializedName("status")
    val status: ShipmentStatus,
    
    @SerializedName("timestamp")
    val timestamp: Long,
    
    @SerializedName("notes")
    val notes: String = "",
    
    @SerializedName("changedBy")
    val changedBy: String = ""
) : Serializable

enum class ServiceType {
    @SerializedName("express")
    EXPRESS,
    
    @SerializedName("standard")
    STANDARD,
    
    @SerializedName("scheduled")
    SCHEDULED,
    
    @SerializedName("cold_chain")
    COLD_CHAIN
}

data class ProofOfDelivery(
    @SerializedName("images")
    val images: List<String> = emptyList(),
    
    @SerializedName("signature")
    val signature: String = "",
    
    @SerializedName("receiverName")
    val receiverName: String = "",
    
    @SerializedName("notes")
    val notes: String = "",
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
```

---

## 6️⃣ Payment.kt - نموذج الدفع

```kotlin
package com.edham.customer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * نموذج الدفع والمعاملة المالية
 */
data class Payment(
    @SerializedName("_id")
    val id: String = "",
    
    @SerializedName("shipmentId")
    val shipmentId: String = "",
    
    @SerializedName("customerId")
    val customerId: String = "",
    
    @SerializedName("amount")
    val amount: Double = 0.0,
    
    @SerializedName("currency")
    val currency: String = "SAR",
    
    @SerializedName("paymentMethod")
    val paymentMethod: PaymentMethod = PaymentMethod.WALLET,
    
    @SerializedName("status")
    val status: PaymentStatus = PaymentStatus.PENDING,
    
    @SerializedName("transactionId")
    val transactionId: String = "",
    
    @SerializedName("referenceNumber")
    val referenceNumber: String = "",
    
    @SerializedName("paymentDetails")
    val paymentDetails: PaymentDetails? = null,
    
    @SerializedName("receipt")
    val receipt: PaymentReceipt? = null,
    
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),
    
    @SerializedName("completedAt")
    val completedAt: Long? = null,
    
    @SerializedName("failureReason")
    val failureReason: String = ""
) : Serializable {
    
    fun isCompleted(): Boolean {
        return status == PaymentStatus.COMPLETED
    }
    
    fun isFailed(): Boolean {
        return status == PaymentStatus.FAILED
    }
    
    fun canBeRetried(): Boolean {
        return status == PaymentStatus.FAILED
    }
}

enum class PaymentMethod {
    @SerializedName("wallet")
    WALLET,
    
    @SerializedName("credit_card")
    CREDIT_CARD,
    
    @SerializedName("debit_card")
    DEBIT_CARD,
    
    @SerializedName("bank_transfer")
    BANK_TRANSFER,
    
    @SerializedName("cash_on_delivery")
    CASH_ON_DELIVERY,
    
    @SerializedName("invoice")
    INVOICE
}

enum class PaymentStatus {
    @SerializedName("pending")
    PENDING,
    
    @SerializedName("processing")
    PROCESSING,
    
    @SerializedName("completed")
    COMPLETED,
    
    @SerializedName("failed")
    FAILED,
    
    @SerializedName("refunded")
    REFUNDED
}

data class PaymentDetails(
    @SerializedName("last4Digits")
    val last4Digits: String = "",
    
    @SerializedName("cardBrand")
    val cardBrand: String = "", // Visa, Mastercard, etc.
    
    @SerializedName("bankName")
    val bankName: String = "",
    
    @SerializedName("accountNumber")
    val accountNumber: String = ""
) : Serializable

data class PaymentReceipt(
    @SerializedName("receiptNumber")
    val receiptNumber: String = "",
    
    @SerializedName("issuedDate")
    val issuedDate: Long = System.currentTimeMillis(),
    
    @SerializedName("pdfUrl")
    val pdfUrl: String = "",
    
    @SerializedName("emailSent")
    val emailSent: Boolean = false
) : Serializable
```

---

## 7️⃣ Rating.kt - نموذج التقييم

```kotlin
package com.edham.customer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * نموذج التقييم والملاحظات
 */
data class Rating(
    @SerializedName("_id")
    val id: String = "",
    
    @SerializedName("shipmentId")
    val shipmentId: String = "",
    
    @SerializedName("customerId")
    val customerId: String = "",
    
    @SerializedName("driverId")
    val driverId: String? = null,
    
    @SerializedName("score")
    val score: Int = 5, // 1-5 stars
    
    @SerializedName("comment")
    val comment: String = "",
    
    @SerializedName("issues")
    val issues: List<String> = emptyList(),
    
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis()
) : Serializable {
    
    fun isValid(): Boolean {
        return score in 1..5
    }
    
    fun hasIssues(): Boolean {
        return issues.isNotEmpty()
    }
}

enum class RatingIssue(val displayName: String) {
    @SerializedName("delayed")
    DELAYED("تأخير في التسليم"),
    
    @SerializedName("poor_handling")
    POOR_HANDLING("سوء معاملة"),
    
    @SerializedName("damaged_cargo")
    DAMAGED_CARGO("ضرر في البضاعة"),
    
    @SerializedName("unprofessional")
    UNPROFESSIONAL("عدم احترافية"),
    
    @SerializedName("missing_items")
    MISSING_ITEMS("أشياء ناقصة"),
    
    @SerializedName("communication_issue")
    COMMUNICATION_ISSUE("مشاكل في التواصل"),
    
    @SerializedName("wrong_delivery")
    WRONG_DELIVERY("تسليم خاطئ"),
    
    @SerializedName("other")
    OTHER("أخرى")
}
```

---

## 8️⃣ Notification.kt - نموذج الإشعار

```kotlin
package com.edham.customer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * نموذج الإشعار - الإشعارات المختلفة في التطبيق
 */
data class Notification(
    @SerializedName("_id")
    val id: String = "",
    
    @SerializedName("userId")
    val userId: String = "",
    
    @SerializedName("type")
    val type: NotificationType = NotificationType.GENERAL,
    
    @SerializedName("title")
    val title: String = "",
    
    @SerializedName("message")
    val message: String = "",
    
    @SerializedName("shipmentId")
    val shipmentId: String? = null,
    
    @SerializedName("data")
    val data: NotificationData? = null,
    
    @SerializedName("isRead")
    val isRead: Boolean = false,
    
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),
    
    @SerializedName("readAt")
    val readAt: Long? = null
) : Serializable {
    
    fun getIcon(): Int {
        return when (type) {
            NotificationType.SHIPMENT_CREATED -> android.R.drawable.ic_dialog_info
            NotificationType.SHIPMENT_CONFIRMED -> android.R.drawable.ic_dialog_email
            NotificationType.DRIVER_ASSIGNED -> android.R.drawable.ic_dialog_map
            NotificationType.PICKUP_ARRIVED -> android.R.drawable.ic_dialog_map
            NotificationType.DELIVERY_STARTED -> android.R.drawable.ic_dialog_map
            NotificationType.DELIVERY_ARRIVED -> android.R.drawable.ic_dialog_map
            NotificationType.DELIVERED -> android.R.drawable.ic_dialog_email
            NotificationType.PAYMENT_COMPLETED -> android.R.drawable.ic_dialog_info
            NotificationType.PAYMENT_FAILED -> android.R.drawable.ic_dialog_alert
            else -> android.R.drawable.ic_dialog_info
        }
    }
}

enum class NotificationType {
    @SerializedName("shipment_created")
    SHIPMENT_CREATED,
    
    @SerializedName("shipment_confirmed")
    SHIPMENT_CONFIRMED,
    
    @SerializedName("driver_assigned")
    DRIVER_ASSIGNED,
    
    @SerializedName("pickup_arrived")
    PICKUP_ARRIVED,
    
    @SerializedName("delivery_started")
    DELIVERY_STARTED,
    
    @SerializedName("delivery_arrived")
    DELIVERY_ARRIVED,
    
    @SerializedName("delivered")
    DELIVERED,
    
    @SerializedName("payment_completed")
    PAYMENT_COMPLETED,
    
    @SerializedName("payment_failed")
    PAYMENT_FAILED,
    
    @SerializedName("shipment_cancelled")
    SHIPMENT_CANCELLED,
    
    @SerializedName("general")
    GENERAL
}

data class NotificationData(
    @SerializedName("shipmentId")
    val shipmentId: String? = null,
    
    @SerializedName("action")
    val action: String? = null,
    
    @SerializedName("payload")
    val payload: Map<String, String> = emptyMap()
) : Serializable
```

---

## 9️⃣ Invoice.kt - نموذج الفاتورة

```kotlin
package com.edham.customer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * نموذج الفاتورة
 */
data class Invoice(
    @SerializedName("_id")
    val id: String = "",
    
    @SerializedName("invoiceNumber")
    val invoiceNumber: String = "",
    
    @SerializedName("shipmentId")
    val shipmentId: String = "",
    
    @SerializedName("customerId")
    val customerId: String = "",
    
    @SerializedName("type")
    val type: InvoiceType = InvoiceType.SHIPMENT,
    
    @SerializedName("shipmentDetails")
    val shipmentDetails: ShipmentInvoiceDetails = ShipmentInvoiceDetails(),
    
    @SerializedName("pricing")
    val pricing: Pricing = Pricing(),
    
    @SerializedName("payment")
    val payment: PaymentInfo = PaymentInfo(),
    
    @SerializedName("status")
    val status: InvoiceStatus = InvoiceStatus.PENDING,
    
    @SerializedName("issuedDate")
    val issuedDate: Long = System.currentTimeMillis(),
    
    @SerializedName("dueDate")
    val dueDate: Long? = null,
    
    @SerializedName("paidDate")
    val paidDate: Long? = null,
    
    @SerializedName("pdfUrl")
    val pdfUrl: String = "",
    
    @SerializedName("notes")
    val notes: String = ""
) : Serializable

enum class InvoiceType {
    @SerializedName("shipment")
    SHIPMENT,
    
    @SerializedName("monthly")
    MONTHLY,
    
    @SerializedName("settlement")
    SETTLEMENT
}

enum class InvoiceStatus {
    @SerializedName("pending")
    PENDING,
    
    @SerializedName("sent")
    SENT,
    
    @SerializedName("viewed")
    VIEWED,
    
    @SerializedName("paid")
    PAID,
    
    @SerializedName("overdue")
    OVERDUE,
    
    @SerializedName("cancelled")
    CANCELLED
}

data class ShipmentInvoiceDetails(
    @SerializedName("trackingNumber")
    val trackingNumber: String = "",
    
    @SerializedName("from")
    val from: String = "",
    
    @SerializedName("to")
    val to: String = "",
    
    @SerializedName("distance")
    val distance: Double = 0.0,
    
    @SerializedName("cargoType")
    val cargoType: String = "",
    
    @SerializedName("weight")
    val weight: Double = 0.0,
    
    @SerializedName("quantity")
    val quantity: Int = 0
) : Serializable

data class PaymentInfo(
    @SerializedName("method")
    val method: PaymentMethod = PaymentMethod.WALLET,
    
    @SerializedName("accountNumber")
    val accountNumber: String = "",
    
    @SerializedName("bankName")
    val bankName: String = "",
    
    @SerializedName("iban")
    val iban: String = ""
) : Serializable
```

---

## 🔟 Wallet.kt - نموذج المحفظة

```kotlin
package com.edham.customer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * نموذج المحفظة الرقمية
 */
data class Wallet(
    @SerializedName("_id")
    val id: String = "",
    
    @SerializedName("userId")
    val userId: String = "",
    
    @SerializedName("balance")
    val balance: Double = 0.0,
    
    @SerializedName("currency")
    val currency: String = "SAR",
    
    @SerializedName("transactions")
    val transactions: List<WalletTransaction> = emptyList(),
    
    @SerializedName("lastUpdated")
    val lastUpdated: Long = System.currentTimeMillis()
) : Serializable {
    
    fun hasEnoughBalance(amount: Double): Boolean {
        return balance >= amount
    }
    
    fun getFormattedBalance(): String {
        return String.format("%.2f %s", balance, currency)
    }
}

data class WalletTransaction(
    @SerializedName("_id")
    val id: String = "",
    
    @SerializedName("type")
    val type: TransactionType = TransactionType.DEBIT,
    
    @SerializedName("amount")
    val amount: Double = 0.0,
    
    @SerializedName("description")
    val description: String = "",
    
    @SerializedName("shipmentId")
    val shipmentId: String? = null,
    
    @SerializedName("balanceBefore")
    val balanceBefore: Double = 0.0,
    
    @SerializedName("balanceAfter")
    val balanceAfter: Double = 0.0,
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

enum class TransactionType {
    @SerializedName("credit")
    CREDIT, // حساب إضافي
    
    @SerializedName("debit")
    DEBIT, // سحب
    
    @SerializedName("refund")
    REFUND // استرجاع
}
```

---

## 1️⃣1️⃣ API Response Classes

```kotlin
package com.edham.customer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * استجابة API العامة
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean = false,
    
    @SerializedName("message")
    val message: String = "",
    
    @SerializedName("data")
    val data: T? = null,
    
    @SerializedName("error")
    val error: ErrorResponse? = null,
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

data class ErrorResponse(
    @SerializedName("code")
    val code: String = "",
    
    @SerializedName("message")
    val message: String = "",
    
    @SerializedName("details")
    val details: Map<String, String> = emptyMap()
) : Serializable

/**
 * استجابة البيانات المقسمة (Pagination)
 */
data class PagedResponse<T>(
    @SerializedName("data")
    val data: List<T> = emptyList(),
    
    @SerializedName("page")
    val page: Int = 1,
    
    @SerializedName("pageSize")
    val pageSize: Int = 10,
    
    @SerializedName("total")
    val total: Int = 0,
    
    @SerializedName("totalPages")
    val totalPages: Int = 0,
    
    @SerializedName("hasMore")
    val hasMore: Boolean = false
) : Serializable {
    
    fun getTotalItems(): Int = total
    
    fun getCurrentPageStart(): Int = (page - 1) * pageSize + 1
    
    fun getCurrentPageEnd(): Int = minOf(page * pageSize, total)
}
```

---

## ✅ **ملخص المودلز:**

| الملف | الاستخدام |
|------|----------|
| `Address.kt` | العناوين (استقبال/تسليم) |
| `Cargo.kt` | البضاعة وتفاصيلها |
| `Location.kt` | الموقع الجغرافي والتتبع |
| `Driver.kt` | معلومات السائق |
| `Shipment.kt` | الشحنة الرئيسية |
| `Payment.kt` | المدفوعات |
| `Rating.kt` | التقييمات |
| `Notification.kt` | الإشعارات |
| `Invoice.kt` | الفواتير |
| `Wallet.kt` | المحفظة الرقمية |
| `ApiResponse.kt` | استجابات الـ API |

---

**الآن جاهز للمرحلة التالية: API Services! ✅**

