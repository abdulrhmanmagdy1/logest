package com.edham.logistics.security

import android.content.Context
import android.util.Base64
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Certificate Pinning Security Implementation
 * يوفر حماية متقدمة ضد هجمات Man-in-the-Middle
 */
class CertificatePinning private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: CertificatePinning? = null
        
        fun getInstance(context: Context): CertificatePinning {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CertificatePinning(context.applicationContext).also { INSTANCE = it }
            }
        }
        
        // Production certificates (replace with actual SHA-256 pins from real server before enabling)
        private const val PRODUCTION_CERT_SHA256 = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
        private const val BACKUP_CERT_SHA256 = "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="
        
        // Certificate pinning configuration
        // ⚠️ Disabled until real production server is deployed and real SHA-256 pins are obtained
        private const val PINNING_ENABLED = false
        private const val STRICT_MODE = false
        
        // Certificate validation rules
        private const val MAX_CERTIFICATE_CHAIN_LENGTH = 5
        private const val CERTIFICATE_VALIDITY_DAYS = 395 // ~13 months
    }
    
    /**
     * Certificate Pinning Configuration
     */
    data class PinningConfig(
        val enabled: Boolean,
        val strictMode: Boolean,
        val pinnedCertificates: List<String>,
        val backupCertificates: List<String>,
        val allowedHosts: List<String>,
        val certificateValidationEnabled: Boolean
    )
    
    /**
     * الحصول على إعدادات Certificate Pinning
     */
    fun getPinningConfig(): PinningConfig {
        return PinningConfig(
            enabled = PINNING_ENABLED,
            strictMode = STRICT_MODE,
            pinnedCertificates = listOf(PRODUCTION_CERT_SHA256),
            backupCertificates = listOf(BACKUP_CERT_SHA256),
            allowedHosts = getAllowedHosts(),
            certificateValidationEnabled = true
        )
    }
    
    /**
     * إنشاء OkHttpClient مع Certificate Pinning
     */
    fun createSecureOkHttpClient(): OkHttpClient {
        val config = getPinningConfig()
        
        val builder = OkHttpClient.Builder()
        
        if (config.enabled) {
            // Add certificate pinning
            val pinner = CertificatePinner.Builder()
                .add("api.edham.com", config.pinnedCertificates.first())
                .add("edham.com", config.pinnedCertificates.first())
                
            // Add backup certificates
            config.backupCertificates.forEach { cert ->
                pinner.add("api.edham.com", cert)
                pinner.add("edham.com", cert)
            }
            
            builder.certificatePinner(pinner.build())
            
            // Add custom SSL context for additional validation
            if (config.certificateValidationEnabled) {
                builder.sslSocketFactory(createCustomSSLSocketFactory(), createCustomTrustManager())
            }
        }
        
        return builder.build()
    }
    
    /**
     * التحقق من صحة الشهادة
     */
    fun validateCertificate(certificate: X509Certificate): ValidationResult {
        val errors = mutableListOf<String>()
        
        try {
            // Check certificate validity period
            val now = System.currentTimeMillis()
            if (certificate.notBefore.time > now) {
                errors.add("Certificate is not yet valid")
            }
            if (certificate.notAfter.time < now) {
                errors.add("Certificate has expired")
            }
            
            // Check certificate validity period (should not exceed 395 days)
            val validityPeriod = (certificate.notAfter.time - certificate.notBefore.time) / (24 * 60 * 60 * 1000)
            if (validityPeriod > CERTIFICATE_VALIDITY_DAYS) {
                errors.add("Certificate validity period exceeds $CERTIFICATE_VALIDITY_DAYS days")
            }
            
            // Check certificate chain length
            val certChain = listOf(certificate) // In production, get full chain
            if (certChain.size > MAX_CERTIFICATE_CHAIN_LENGTH) {
                errors.add("Certificate chain too long: ${certChain.size} > $MAX_CERTIFICATE_CHAIN_LENGTH")
            }
            
            // Check certificate purpose
            val keyUsage = certificate.keyUsage
            if (keyUsage != null && !keyUsage[0] && !keyUsage[2]) {
                errors.add("Certificate cannot be used for digital signature or key encipherment")
            }
            
            // Check certificate subject
            val subject = certificate.subjectDN.toString()
            if (!isValidSubject(subject)) {
                errors.add("Invalid certificate subject: $subject")
            }
            
        } catch (e: Exception) {
            errors.add("Certificate validation error: ${e.message}")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            certificateInfo = extractCertificateInfo(certificate)
        )
    }
    
    /**
     * تحديث الشهادات المثبتة
     */
    fun updatePinnedCertificates(newCertificates: List<String>): Result<Boolean> {
        return try {
            // Validate new certificates
            newCertificates.forEach { cert ->
                if (!isValidCertificateFormat(cert)) {
                    return Result.failure(Exception("Invalid certificate format: $cert"))
                }
            }
            
            // Save new certificates (in production, store securely)
            saveCertificatesToSecureStorage(newCertificates)
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * مراقبة انتهاء صلاحية الشهادات
     */
    fun monitorCertificateExpiry(): List<ExpiryWarning> {
        val warnings = mutableListOf<ExpiryWarning>()
        val config = getPinningConfig()
        
        // Check all pinned certificates
        (config.pinnedCertificates + config.backupCertificates).forEach { certHash ->
            try {
                val certificate = loadCertificateFromHash(certHash)
                if (certificate != null) {
                    val daysUntilExpiry = getDaysUntilExpiry(certificate)
                    
                    when {
                        daysUntilExpiry <= 0 -> {
                            warnings.add(
                                ExpiryWarning(
                                    certificateHash = certHash,
                                    daysUntilExpiry = daysUntilExpiry,
                                    severity = WarningSeverity.CRITICAL,
                                    message = "Certificate has expired"
                                )
                            )
                        }
                        daysUntilExpiry <= 7 -> {
                            warnings.add(
                                ExpiryWarning(
                                    certificateHash = certHash,
                                    daysUntilExpiry = daysUntilExpiry,
                                    severity = WarningSeverity.URGENT,
                                    message = "Certificate expires in $daysUntilExpiry days"
                                )
                            )
                        }
                        daysUntilExpiry <= 30 -> {
                            warnings.add(
                                ExpiryWarning(
                                    certificateHash = certHash,
                                    daysUntilExpiry = daysUntilExpiry,
                                    severity = WarningSeverity.WARNING,
                                    message = "Certificate expires in $daysUntilExpiry days"
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                warnings.add(
                    ExpiryWarning(
                        certificateHash = certHash,
                        daysUntilExpiry = -1,
                        severity = WarningSeverity.ERROR,
                        message = "Failed to load certificate: ${e.message}"
                    )
                )
            }
        }
        
        return warnings
    }
    
    // Private helper methods
    
    private fun getAllowedHosts(): List<String> {
        return listOf(
            "api.edham.com",
            "edham.com",
            "staging-api.edham.com",
            "dev-api.edham.com"
        )
    }
    
    private fun createCustomSSLSocketFactory(): javax.net.ssl.SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(createCustomTrustManager()), null)
        return sslContext.socketFactory
    }
    
    private fun createCustomTrustManager(): X509TrustManager {
        val trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagers.init(null as KeyStore?)
        val trustManager = trustManagers.trustManagers[0] as X509TrustManager
        
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // Implement client certificate validation if needed
            }
            
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // Custom server certificate validation
                chain?.forEach { cert ->
                    val validation = validateCertificate(cert)
                    if (!validation.isValid) {
                        throw SecurityException("Certificate validation failed: ${validation.errors.joinToString()}")
                    }
                }
            }
            
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return trustManager.acceptedIssuers
            }
        }
    }
    
    private fun isValidSubject(subject: String): Boolean {
        // Basic subject validation
        return subject.contains("CN=") && 
               (subject.contains("edham.com") || subject.contains("api.edham.com"))
    }
    
    private fun isValidCertificateFormat(certHash: String): Boolean {
        return certHash.startsWith("sha256/") && 
               certHash.length == "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=".length
    }
    
    private fun saveCertificatesToSecureStorage(certificates: List<String>) {
        // In production, use Android Keystore or other secure storage
        // For demo purposes, using SharedPreferences (NOT SECURE FOR PRODUCTION)
        val prefs = context.getSharedPreferences("cert_pinning", Context.MODE_PRIVATE)
        prefs.edit()
            .putStringSet("pinned_certs", certificates.toSet())
            .apply()
    }
    
    private fun loadCertificateFromHash(certHash: String): X509Certificate? {
        // In production, load from secure storage or trust store
        // For demo purposes, return null (would implement actual certificate loading)
        return null
    }
    
    private fun getDaysUntilExpiry(certificate: X509Certificate): Int {
        val now = System.currentTimeMillis()
        val expiryTime = certificate.notAfter.time
        return ((expiryTime - now) / (24 * 60 * 60 * 1000)).toInt()
    }
    
    private fun extractCertificateInfo(certificate: X509Certificate): CertificateInfo {
        return CertificateInfo(
            subject = certificate.subjectDN.toString(),
            issuer = certificate.issuerDN.toString(),
            serialNumber = certificate.serialNumber.toString(),
            validFrom = certificate.notBefore,
            validUntil = certificate.notAfter,
            signatureAlgorithm = certificate.sigAlgName,
            version = certificate.version
        )
    }
    
    // Data classes
    
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String>,
        val certificateInfo: CertificateInfo
    )
    
    data class CertificateInfo(
        val subject: String,
        val issuer: String,
        val serialNumber: String,
        val validFrom: java.util.Date,
        val validUntil: java.util.Date,
        val signatureAlgorithm: String,
        val version: Int
    )
    
    data class ExpiryWarning(
        val certificateHash: String,
        val daysUntilExpiry: Int,
        val severity: WarningSeverity,
        val message: String
    )
    
    enum class WarningSeverity {
        CRITICAL,    // Already expired
        URGENT,      // Expires within 7 days
        WARNING,     // Expires within 30 days
        ERROR        // Failed to load/validate
    }
}
