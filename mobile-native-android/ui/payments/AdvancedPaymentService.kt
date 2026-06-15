// ============================================
// 🚀 Edham Logistics - Advanced Payment Service
// Premium Dark Theme with Smart Payment Processing
// ============================================

package com.edham.logistics.ui.payments

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.edham.logistics.ui.theme.EdhamOrange
import com.edham.logistics.ui.theme.SuccessGreen
import com.edham.logistics.ui.theme.WarningYellow
import com.edham.logistics.ui.theme.ErrorRed
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * ============================================
 * Advanced Payment Service
 * ============================================
 * خدمة الدفع المتقدمة مع دعم متعدد وسائل الدفع
 */
class AdvancedPaymentService(private val context: Context) {
    
    private val scope = CoroutineScope(Dispatchers.IO)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ar"))
    
    // Secure SharedPreferences for payment data
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_payments",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    data class PaymentMethod(
        val id: String,
        val type: PaymentType,
        val displayName: String,
        val isDefault: Boolean = false,
        val isSaved: Boolean = false,
        val lastUsed: Date? = null,
        val metadata: Map<String, String> = emptyMap()
    )
    
    data class PaymentRequest(
        val amount: Double,
        val currency: String = "SAR",
        val description: String,
        val shipmentId: String? = null,
        val paymentMethodId: String,
        val savePaymentMethod: Boolean = false,
        val metadata: Map<String, Any> = emptyMap()
    )
    
    data class PaymentResult(
        val success: Boolean,
        val transactionId: String?,
        val amount: Double,
        val currency: String,
        val status: PaymentStatus,
        val timestamp: Date,
        val receiptUrl: String? = null,
        val errorMessage: String? = null,
        val metadata: Map<String, Any> = emptyMap()
    )
    
    data class RefundRequest(
        val transactionId: String,
        val amount: Double? = null,
        val reason: String,
        val metadata: Map<String, String> = emptyMap()
    )
    
    data class RefundResult(
        val success: Boolean,
        val refundId: String?,
        val amount: Double,
        val status: RefundStatus,
        val timestamp: Date,
        val estimatedArrival: Date? = null,
        val errorMessage: String? = null
    )
    
    enum class PaymentType {
        CREDIT_CARD,
        DEBIT_CARD,
        BANK_TRANSFER,
        MADA,
        APPLE_PAY,
        GOOGLE_PAY,
        STC_PAY,
        SADAD,
        PAYPAL,
        CASH_ON_DELIVERY
    }
    
    enum class PaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED,
        REFUNDED,
        PARTIALLY_REFUNDED
    }
    
    enum class RefundStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
    
    /**
     * ============================================
     * Payment Processing Methods
     * ============================================
     */
    suspend fun processPayment(paymentRequest: PaymentRequest): PaymentResult = withContext(Dispatchers.IO) {
        try {
            // Validate payment request
            validatePaymentRequest(paymentRequest)
            
            // Get payment method
            val paymentMethod = getPaymentMethod(paymentRequest.paymentMethodId)
                ?: throw IllegalArgumentException("Payment method not found")
            
            // Process payment based on type
            val result = when (paymentMethod.type) {
                PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> {
                    processCardPayment(paymentRequest, paymentMethod)
                }
                PaymentType.BANK_TRANSFER -> {
                    processBankTransfer(paymentRequest, paymentMethod)
                }
                PaymentType.MADA -> {
                    processMadaPayment(paymentRequest, paymentMethod)
                }
                PaymentType.APPLE_PAY -> {
                    processApplePayPayment(paymentRequest, paymentMethod)
                }
                PaymentType.GOOGLE_PAY -> {
                    processGooglePayPayment(paymentRequest, paymentMethod)
                }
                PaymentType.STC_PAY -> {
                    processSTCPayPayment(paymentRequest, paymentMethod)
                }
                PaymentType.SADAD -> {
                    processSadadPayment(paymentRequest, paymentMethod)
                }
                PaymentType.PAYPAL -> {
                    processPayPalPayment(paymentRequest, paymentMethod)
                }
                PaymentType.CASH_ON_DELIVERY -> {
                    processCashOnDeliveryPayment(paymentRequest, paymentMethod)
                }
            }
            
            // Save payment method if requested
            if (paymentRequest.savePaymentMethod && !paymentMethod.isSaved) {
                savePaymentMethod(paymentMethod)
            }
            
            // Record transaction
            recordTransaction(result)
            
            // Send notification
            sendPaymentNotification(result)
            
            result
            
        } catch (e: Exception) {
            PaymentResult(
                success = false,
                transactionId = null,
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.FAILED,
                timestamp = Date(),
                errorMessage = e.message ?: "Payment processing failed"
            )
        }
    }
    
    /**
     * ============================================
     * Card Payment Processing
     * ============================================
     */
    private suspend fun processCardPayment(
        paymentRequest: PaymentRequest,
        paymentMethod: PaymentMethod
    ): PaymentResult {
        // Simulate card payment processing
        delay(2000) // Simulate network delay
        
        // Mock success (in real app, integrate with payment gateway)
        val success = Random.nextBoolean()
        
        return if (success) {
            PaymentResult(
                success = true,
                transactionId = "TXN_${System.currentTimeMillis()}",
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.COMPLETED,
                timestamp = Date(),
                receiptUrl = "https://edham-logistics.com/receipt/TXN_${System.currentTimeMillis()}",
                metadata = mapOf(
                    "payment_method" to "card",
                    "card_last4" to (paymentMethod.metadata["last4"] ?: "****"),
                    "card_brand" to (paymentMethod.metadata["brand"] ?: "Unknown")
                )
            )
        } else {
            PaymentResult(
                success = false,
                transactionId = null,
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.FAILED,
                timestamp = Date(),
                errorMessage = "Transaction declined by bank"
            )
        }
    }
    
    /**
     * ============================================
     * MADA Payment Processing
     * ============================================
     */
    private suspend fun processMadaPayment(
        paymentRequest: PaymentRequest,
        paymentMethod: PaymentMethod
    ): PaymentResult {
        delay(1500)
        
        // MADA specific processing
        val success = Random.nextFloat() > 0.1f // 90% success rate
        
        return if (success) {
            PaymentResult(
                success = true,
                transactionId = "MADA_${System.currentTimeMillis()}",
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.COMPLETED,
                timestamp = Date(),
                receiptUrl = "https://edham-logistics.com/receipt/MADA_${System.currentTimeMillis()}",
                metadata = mapOf(
                    "payment_method" to "mada",
                    "card_last4" to (paymentMethod.metadata["last4"] ?: "****")
                )
            )
        } else {
            PaymentResult(
                success = false,
                transactionId = null,
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.FAILED,
                timestamp = Date(),
                errorMessage = "MADA transaction failed"
            )
        }
    }
    
    /**
     * ============================================
     * STC Pay Payment Processing
     * ============================================
     */
    private suspend fun processSTCPayPayment(
        paymentRequest: PaymentRequest,
        paymentMethod: PaymentMethod
    ): PaymentResult {
        delay(1000)
        
        // STC Pay processing
        val success = Random.nextFloat() > 0.05f // 95% success rate
        
        return if (success) {
            PaymentResult(
                success = true,
                transactionId = "STC_${System.currentTimeMillis()}",
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.COMPLETED,
                timestamp = Date(),
                receiptUrl = "https://edham-logistics.com/receipt/STC_${System.currentTimeMillis()}",
                metadata = mapOf(
                    "payment_method" to "stc_pay",
                    "phone_number" to (paymentMethod.metadata["phone"] ?: "****")
                )
            )
        } else {
            PaymentResult(
                success = false,
                transactionId = null,
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.FAILED,
                timestamp = Date(),
                errorMessage = "STC Pay transaction failed"
            )
        }
    }
    
    /**
     * ============================================
     * SADAD Payment Processing
     * ============================================
     */
    private suspend fun processSadadPayment(
        paymentRequest: PaymentRequest,
        paymentMethod: PaymentMethod
    ): PaymentResult {
        delay(3000) // SADAD takes longer
        
        val success = Random.nextFloat() > 0.08f // 92% success rate
        
        return if (success) {
            PaymentResult(
                success = true,
                transactionId = "SADAD_${System.currentTimeMillis()}",
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.COMPLETED,
                timestamp = Date(),
                receiptUrl = "https://edham-logistics.com/receipt/SADAD_${System.currentTimeMillis()}",
                metadata = mapOf(
                    "payment_method" to "sadad",
                    "sadad_number" to (paymentMethod.metadata["sadad_number"] ?: "****")
                )
            )
        } else {
            PaymentResult(
                success = false,
                transactionId = null,
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.FAILED,
                timestamp = Date(),
                errorMessage = "SADAD payment failed"
            )
        }
    }
    
    /**
     * ============================================
     * Apple Pay Payment Processing
     * ============================================
     */
    private suspend fun processApplePayPayment(
        paymentRequest: PaymentRequest,
        paymentMethod: PaymentMethod
    ): PaymentResult {
        delay(1500)
        
        val success = Random.nextFloat() > 0.03f // 97% success rate
        
        return if (success) {
            PaymentResult(
                success = true,
                transactionId = "APPLE_${System.currentTimeMillis()}",
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.COMPLETED,
                timestamp = Date(),
                receiptUrl = "https://edham-logistics.com/receipt/APPLE_${System.currentTimeMillis()}",
                metadata = mapOf(
                    "payment_method" to "apple_pay",
                    "device_id" to (paymentMethod.metadata["device_id"] ?: "Unknown")
                )
            )
        } else {
            PaymentResult(
                success = false,
                transactionId = null,
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.FAILED,
                timestamp = Date(),
                errorMessage = "Apple Pay transaction failed"
            )
        }
    }
    
    /**
     * ============================================
     * Google Pay Payment Processing
     * ============================================
     */
    private suspend fun processGooglePayPayment(
        paymentRequest: PaymentRequest,
        paymentMethod: PaymentMethod
    ): PaymentResult {
        delay(1500)
        
        val success = Random.nextFloat() > 0.03f // 97% success rate
        
        return if (success) {
            PaymentResult(
                success = true,
                transactionId = "GOOGLE_${System.currentTimeMillis()}",
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.COMPLETED,
                timestamp = Date(),
                receiptUrl = "https://edham-logistics.com/receipt/GOOGLE_${System.currentTimeMillis()}",
                metadata = mapOf(
                    "payment_method" to "google_pay",
                    "device_id" to (paymentMethod.metadata["device_id"] ?: "Unknown")
                )
            )
        } else {
            PaymentResult(
                success = false,
                transactionId = null,
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.FAILED,
                timestamp = Date(),
                errorMessage = "Google Pay transaction failed"
            )
        }
    }
    
    /**
     * ============================================
     * Bank Transfer Payment Processing
     * ============================================
     */
    private suspend fun processBankTransfer(
        paymentRequest: PaymentRequest,
        paymentMethod: PaymentMethod
    ): PaymentResult {
        delay(1000)
        
        // Bank transfer creates a pending transaction
        return PaymentResult(
            success = true,
            transactionId = "BANK_${System.currentTimeMillis()}",
            amount = paymentRequest.amount,
            currency = paymentRequest.currency,
            status = PaymentStatus.PENDING,
            timestamp = Date(),
            receiptUrl = null,
            metadata = mapOf(
                "payment_method" to "bank_transfer",
                "bank_name" to (paymentMethod.metadata["bank_name"] ?: "Unknown"),
                "account_number" to (paymentMethod.metadata["account_number"] ?: "****"),
                "instructions" to "Please transfer the amount to our bank account"
            )
        )
    }
    
    /**
     * ============================================
     * PayPal Payment Processing
     * ============================================
     */
    private suspend fun processPayPalPayment(
        paymentRequest: PaymentRequest,
        paymentMethod: PaymentMethod
    ): PaymentResult {
        delay(2000)
        
        val success = Random.nextFloat() > 0.05f // 95% success rate
        
        return if (success) {
            PaymentResult(
                success = true,
                transactionId = "PAYPAL_${System.currentTimeMillis()}",
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.COMPLETED,
                timestamp = Date(),
                receiptUrl = "https://edham-logistics.com/receipt/PAYPAL_${System.currentTimeMillis()}",
                metadata = mapOf(
                    "payment_method" to "paypal",
                    "paypal_email" to (paymentMethod.metadata["email"] ?: "****")
                )
            )
        } else {
            PaymentResult(
                success = false,
                transactionId = null,
                amount = paymentRequest.amount,
                currency = paymentRequest.currency,
                status = PaymentStatus.FAILED,
                timestamp = Date(),
                errorMessage = "PayPal transaction failed"
            )
        }
    }
    
    /**
     * ============================================
     * Cash on Delivery Payment Processing
     * ============================================
     */
    private suspend fun processCashOnDeliveryPayment(
        paymentRequest: PaymentRequest,
        paymentMethod: PaymentMethod
    ): PaymentResult {
        delay(500)
        
        return PaymentResult(
            success = true,
            transactionId = "COD_${System.currentTimeMillis()}",
            amount = paymentRequest.amount,
            currency = paymentRequest.currency,
            status = PaymentStatus.PENDING,
            timestamp = Date(),
            receiptUrl = null,
            metadata = mapOf(
                "payment_method" to "cash_on_delivery",
                "instructions" to "Payment will be collected upon delivery"
            )
        )
    }
    
    /**
     * ============================================
     * Refund Processing
     * ============================================
     */
    suspend fun processRefund(refundRequest: RefundRequest): RefundResult = withContext(Dispatchers.IO) {
        try {
            // Validate refund request
            validateRefundRequest(refundRequest)
            
            // Check if transaction exists and is refundable
            val transaction = getTransaction(refundRequest.transactionId)
                ?: throw IllegalArgumentException("Transaction not found")
            
            if (!isRefundable(transaction)) {
                throw IllegalArgumentException("Transaction is not refundable")
            }
            
            // Process refund
            delay(2000) // Simulate processing time
            
            val success = Random.nextFloat() > 0.1f // 90% success rate
            
            if (success) {
                RefundResult(
                    success = true,
                    refundId = "REF_${System.currentTimeMillis()}",
                    amount = refundRequest.amount ?: transaction.amount,
                    status = RefundStatus.PROCESSING,
                    timestamp = Date(),
                    estimatedArrival = Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000L) // 5 days
                )
            } else {
                RefundResult(
                    success = false,
                    refundId = null,
                    amount = refundRequest.amount ?: transaction.amount,
                    status = RefundStatus.FAILED,
                    timestamp = Date(),
                    errorMessage = "Refund processing failed"
                )
            }
            
        } catch (e: Exception) {
            RefundResult(
                success = false,
                refundId = null,
                amount = refundRequest.amount ?: 0.0,
                status = RefundStatus.FAILED,
                timestamp = Date(),
                errorMessage = e.message ?: "Refund processing failed"
            )
        }
    }
    
    /**
     * ============================================
     * Payment Methods Management
     * ============================================
     */
    suspend fun getAvailablePaymentMethods(): List<PaymentMethod> = withContext(Dispatchers.IO) {
        listOf(
            PaymentMethod(
                id = "mada_1",
                type = PaymentType.MADA,
                displayName = "مادة",
                isDefault = true,
                isSaved = true,
                lastUsed = Date(),
                metadata = mapOf(
                    "last4" to "1234",
                    "brand" to "MADA"
                )
            ),
            PaymentMethod(
                id = "stc_1",
                type = PaymentType.STC_PAY,
                displayName = "STC Pay",
                isDefault = false,
                isSaved = true,
                lastUsed = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L),
                metadata = mapOf(
                    "phone" to "05********"
                )
            ),
            PaymentMethod(
                id = "sadad_1",
                type = PaymentType.SADAD,
                displayName = "سداد",
                isDefault = false,
                isSaved = true,
                lastUsed = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L),
                metadata = mapOf(
                    "sadad_number" to "************"
                )
            ),
            PaymentMethod(
                id = "apple_1",
                type = PaymentType.APPLE_PAY,
                displayName = "Apple Pay",
                isDefault = false,
                isSaved = false,
                lastUsed = null
            ),
            PaymentMethod(
                id = "google_1",
                type = PaymentType.GOOGLE_PAY,
                displayName = "Google Pay",
                isDefault = false,
                isSaved = false,
                lastUsed = null
            ),
            PaymentMethod(
                id = "cod_1",
                type = PaymentType.CASH_ON_DELIVERY,
                displayName = "الدفع عند الاستلام",
                isDefault = false,
                isSaved = false,
                lastUsed = null
            )
        )
    }
    
    suspend fun savePaymentMethod(paymentMethod: PaymentMethod): Boolean = withContext(Dispatchers.IO) {
        try {
            val savedMethods = getSavedPaymentMethods().toMutableList()
            val updatedMethod = paymentMethod.copy(isSaved = true)
            savedMethods.add(updatedMethod)
            
            // Save to secure preferences
            val methodsJson = JSONObject()
            savedMethods.forEach { method ->
                val methodJson = JSONObject().apply {
                    put("id", method.id)
                    put("type", method.type.name)
                    put("displayName", method.displayName)
                    put("isDefault", method.isDefault)
                    put("metadata", JSONObject(method.metadata))
                }
                methodsJson.put(method.id, methodJson)
            }
            
            securePrefs.edit()
                .putString("saved_payment_methods", methodsJson.toString())
                .apply()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun deletePaymentMethod(paymentMethodId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val savedMethods = getSavedPaymentMethods().toMutableList()
            savedMethods.removeAll { it.id == paymentMethodId }
            
            // Update secure preferences
            val methodsJson = JSONObject()
            savedMethods.forEach { method ->
                val methodJson = JSONObject().apply {
                    put("id", method.id)
                    put("type", method.type.name)
                    put("displayName", method.displayName)
                    put("isDefault", method.isDefault)
                    put("metadata", JSONObject(method.metadata))
                }
                methodsJson.put(method.id, methodJson)
            }
            
            securePrefs.edit()
                .putString("saved_payment_methods", methodsJson.toString())
                .apply()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    private suspend fun getSavedPaymentMethods(): List<PaymentMethod> = withContext(Dispatchers.IO) {
        try {
            val methodsJson = securePrefs.getString("saved_payment_methods", "{}")
            val jsonObject = JSONObject(methodsJson)
            val methods = mutableListOf<PaymentMethod>()
            
            jsonObject.keys().forEach { key ->
                val methodJson =.getJSONObject(key)
                val metadata = mutableMapOf<String, String>()
                methodJson.getJSONObject("metadata").keys().forEach { metaKey ->
                    metadata[metaKey] = methodJson.getJSONObject("metadata").getString(metaKey)
                }
                
                methods.add(
                    PaymentMethod(
                        id = methodJson.getString("id"),
                        type = PaymentType.valueOf(methodJson.getString("type")),
                        displayName = methodJson.getString("displayName"),
                        isDefault = methodJson.getBoolean("isDefault"),
                        isSaved = true,
                        metadata = metadata
                    )
                )
            }
            
            methods
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private suspend fun getPaymentMethod(paymentMethodId: String): PaymentMethod? = withContext(Dispatchers.IO) {
        getAvailablePaymentMethods().find { it.id == paymentMethodId }
    }
    
    /**
     * ============================================
     * Transaction History
     * ============================================
     */
    suspend fun getTransactionHistory(limit: Int = 50): List<PaymentResult> = withContext(Dispatchers.IO) {
        try {
            val historyJson = securePrefs.getString("transaction_history", "[]")
            val jsonArray = org.json.JSONArray(historyJson)
            val transactions = mutableListOf<PaymentResult>()
            
            for (i in 0 until jsonArray.length()) {
                val transactionJson = jsonArray.getJSONObject(i)
                val metadata = mutableMapOf<String, Any>()
                transactionJson.getJSONObject("metadata").keys().forEach { key ->
                    metadata[key] = transactionJson.getJSONObject("metadata").get(key)
                }
                
                transactions.add(
                    PaymentResult(
                        success = transactionJson.getBoolean("success"),
                        transactionId = transactionJson.getString("transactionId"),
                        amount = transactionJson.getDouble("amount"),
                        currency = transactionJson.getString("currency"),
                        status = PaymentStatus.valueOf(transactionJson.getString("status")),
                        timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ar")).parse(transactionJson.getString("timestamp")) ?: Date(),
                        receiptUrl = transactionJson.optString("receiptUrl"),
                        errorMessage = transactionJson.optString("errorMessage"),
                        metadata = metadata
                    )
                )
            }
            
            transactions.sortedByDescending { it.timestamp }.take(limit)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private suspend fun recordTransaction(paymentResult: PaymentResult) = withContext(Dispatchers.IO) {
        try {
            val history = getTransactionHistory().toMutableList()
            history.add(paymentResult)
            
            val jsonArray = org.json.JSONArray()
            history.forEach { transaction ->
                val transactionJson = JSONObject().apply {
                    put("success", transaction.success)
                    put("transactionId", transaction.transactionId)
                    put("amount", transaction.amount)
                    put("currency", transaction.currency)
                    put("status", transaction.status.name)
                    put("timestamp", dateFormat.format(transaction.timestamp))
                    put("receiptUrl", transaction.receiptUrl)
                    put("errorMessage", transaction.errorMessage)
                    put("metadata", JSONObject(transaction.metadata))
                }
                jsonArray.put(transactionJson)
            }
            
            securePrefs.edit()
                .putString("transaction_history", jsonArray.toString())
                .apply()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    private suspend fun getTransaction(transactionId: String): PaymentResult? = withContext(Dispatchers.IO) {
        getTransactionHistory().find { it.transactionId == transactionId }
    }
    
    /**
     * ============================================
     * Validation Methods
     * ============================================
     */
    private fun validatePaymentRequest(paymentRequest: PaymentRequest) {
        require(paymentRequest.amount > 0) { "Amount must be positive" }
        require(paymentRequest.description.isNotBlank()) { "Description is required" }
        require(paymentRequest.paymentMethodId.isNotBlank()) { "Payment method is required" }
    }
    
    private fun validateRefundRequest(refundRequest: RefundRequest) {
        require(refundRequest.transactionId.isNotBlank()) { "Transaction ID is required" }
        require(refundRequest.reason.isNotBlank()) { "Refund reason is required" }
        require(refundRequest.amount == null || refundRequest.amount!! > 0) { "Refund amount must be positive" }
    }
    
    private fun isRefundable(transaction: PaymentResult): Boolean {
        return transaction.success && 
               (transaction.status == PaymentStatus.COMPLETED || 
                transaction.status == PaymentStatus.PARTIALLY_REFUNDED)
    }
    
    /**
     * ============================================
     * Notification Methods
     * ============================================
     */
    private fun sendPaymentNotification(paymentResult: PaymentResult) {
        // Send notification about payment status
        if (paymentResult.success) {
            // Success notification
        } else {
            // Failure notification
        }
    }
    
    /**
     * ============================================
     * Currency Conversion
     * ============================================
     */
    suspend fun convertCurrency(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Double = withContext(Dispatchers.IO) {
        // Mock conversion rates (in real app, integrate with currency API)
        val conversionRates = mapOf(
            "SAR_TO_USD" to 0.267,
            "SAR_TO_EUR" to 0.245,
            "SAR_TO_GBP" to 0.210,
            "USD_TO_SAR" to 3.75,
            "EUR_TO_SAR" to 4.08,
            "GBP_TO_SAR" to 4.76
        )
        
        val key = "${fromCurrency}_TO_${toCurrency}"
        conversionRates[key]?.let { rate ->
            amount * rate
        } ?: amount // Return original amount if conversion not found
    }
    
    /**
     * ============================================
     * Payment Analytics
     * ============================================
     */
    suspend fun getPaymentAnalytics(
        startDate: Date,
        endDate: Date
    ): PaymentAnalytics = withContext(Dispatchers.IO) {
        val transactions = getTransactionHistory().filter { 
            it.timestamp >= startDate && it.timestamp <= endDate 
        }
        
        val successfulTransactions = transactions.filter { it.success }
        val failedTransactions = transactions.filter { !it.success }
        
        val totalRevenue = successfulTransactions.sumOf { it.amount }
        val averageTransactionValue = if (successfulTransactions.isNotEmpty()) {
            totalRevenue / successfulTransactions.size
        } else 0.0
        
        val paymentMethodBreakdown = successfulTransactions
            .groupBy { it.metadata["payment_method"] as? String ?: "unknown" }
            .mapValues { it.value.sumOf { transaction -> transaction.amount } }
        
        PaymentAnalytics(
            totalTransactions = transactions.size,
            successfulTransactions = successfulTransactions.size,
            failedTransactions = failedTransactions.size,
            successRate = if (transactions.isNotEmpty()) {
                (successfulTransactions.size.toDouble() / transactions.size) * 100
            } else 0.0,
            totalRevenue = totalRevenue,
            averageTransactionValue = averageTransactionValue,
            paymentMethodBreakdown = paymentMethodBreakdown,
            period = "${SimpleDateFormat("yyyy-MM-dd", Locale("ar")).format(startDate)} - ${SimpleDateFormat("yyyy-MM-dd", Locale("ar")).format(endDate)}"
        )
    }
    
    data class PaymentAnalytics(
        val totalTransactions: Int,
        val successfulTransactions: Int,
        val failedTransactions: Int,
        val successRate: Double,
        val totalRevenue: Double,
        val averageTransactionValue: Double,
        val paymentMethodBreakdown: Map<String, Double>,
        val period: String
    )
}
