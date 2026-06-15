# Production-specific ProGuard rules for Edham Logistics
# This file contains additional rules for production release builds

# ============================================
# 🔒 Enhanced Security Rules for Production
# ============================================

# Remove all debug logging in production
-assumenosideffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
}

-assumenosideffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Remove all test code in production
-assumenosideffects class ** {
    public void test*(...);
    public *** test*();
}

# Remove debug methods
-assumenosideffects class ** {
    public void debug*(...);
    public *** debug*();
}

# ============================================
# 🚀 Performance Optimizations
# ============================================

# Aggressive optimization settings
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 7
-allowaccessmodification
-dontpreverify

# Remove unused code aggressively
-dontshrink
-keep,allowshrinking,allowobfuscation class com.edham.logistics.BuildConfig { *; }

# ============================================
# 🛡️ Enhanced Obfuscation
# ============================================

# Use unique class member names
-useuniqueclassmembernames
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

# Obfuscate package names
-repackageclasses ''
-flattenpackagehierarchy

# ============================================
# 🔐 Security-Specific Rules
# ============================================

# Keep security-related classes but obfuscate their internals
-keep class com.edham.logistics.security.** { *; }
-keep class com.edham.logistics.encryption.** { *; }
-keep class com.edham.logistics.authentication.** { *; }

# Keep certificate pinning
-keep class com.edham.logistics.security.CertificatePinner { *; }
-keep class com.edham.logistics.security.NetworkSecurityConfig { *; }

# Keep biometric authentication
-keep class com.edham.logistics.security.BiometricAuthManager { *; }

# Keep payment security
-keep class com.edham.logistics.payment.security.** { *; }

# ============================================
# 📱 Firebase Production Rules
# ============================================

# Keep Firebase Crashlytics
-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.google.firebase.crashlytics.internal.** { *; }
-keep class com.crashlytics.** { *; }
-keep class io.fabric.** { *; }

# Keep Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }

# Keep Firebase Performance
-keep class com.google.firebase.perf.** { *; }

# Keep Firebase Messaging
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.iid.** { *; }

# ============================================
# 🌐 Network Security Rules
# ============================================

# Keep HTTPS and certificate validation
-keep class javax.net.ssl.** { *; }
-keep class java.security.cert.** { *; }
-keep class android.security.** { *; }

# Keep network security configuration
-keep class androidx.security.network.** { *; }

# ============================================
# 💾 Database Security Rules
# ============================================

# Keep Room database with encryption
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keep class androidx.room.migration.** { *; }

# Keep SQLCipher if used for encryption
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# ============================================
# 🔑 Key Management Rules
# ============================================

# Keep Android Keystore
-keep class android.security.keystore.** { *; }
-keep class java.security.KeyStore { *; }
-keep class javax.crypto.** { *; }

# Keep key generation and management
-keep class com.edham.logistics.security.KeyManager { *; }
-keep class com.edham.logistics.security.KeyStoreManager { *; }

# ============================================
# 📍 Location and Maps Security
# ============================================

# Keep location services
-keep class com.google.android.gms.location.** { *; }
-keep class com.google.android.gms.maps.** { *; }

# Keep geofencing
-keep class com.google.android.gms.location.Geofence { *; }
-keep class com.google.android.gms.location.GeofencingClient { *; }

# ============================================
# 📊 Analytics and Monitoring Rules
# ============================================

# Keep performance monitoring
-keep class com.edham.logistics.monitoring.** { *; }
-keep class com.edham.logistics.analytics.** { *; }

# Keep crash reporting
-keep class com.edham.logistics.crashreporting.** { *; }

# ============================================
# 🔧 Production Build Specific Rules
# ============================================

# Keep production configuration
-keep class com.edham.logistics.ProductionBuildConfiguration { *; }

# Keep release signing
-keep class com.edham.logistics.security.ReleaseSigningConfig { *; }

# Keep environment detection
-keep class com.edham.logistics.util.EnvironmentUtils { *; }

# ============================================
# 🎯 Feature Flag Rules
# ============================================

# Keep feature flag management
-keep class com.edham.logistics.features.** { *; }
-keep class com.edham.logistics.flags.** { *; }

# ============================================
# 🔄 Offline Sync Rules
# ============================================

# Keep offline synchronization
-keep class com.edham.logistics.sync.** { *; }
-keep class com.edham.logistics.offline.** { *; }

# Keep conflict resolution
-keep class com.edham.logistics.conflict.** { *; }

# ============================================
# 🚨 Error Handling Rules
# ============================================

# Keep error reporting
-keep class com.edham.logistics.error.** { *; }
-keep class com.edham.logistics.exception.** { *; }

# Keep crash handling
-keep class com.edham.logistics.crash.** { *; }

# ============================================
# 📱 Device-Specific Rules
# ============================================

# Keep device fingerprinting for security
-keep class com.edham.logistics.security.DeviceFingerprint { *; }
-keep class com.edham.logistics.security.DeviceInfo { *; }

# Keep root detection
-keep class com.edham.logistics.security.RootDetection { *; }

# ============================================
# 🌍 Localization Rules
# ============================================

# Keep localization resources
-keep class com.edham.logistics.localization.** { *; }

# ============================================
# 🎨 UI Security Rules
# ============================================

# Keep screenshot prevention
-keep class com.edham.logistics.security.ScreenshotProtection { *; }

# Keep screen recording detection
-keep class com.edham.logistics.security.ScreenRecordingDetection { *; }

# ============================================
# ⚡ Performance Monitoring Rules
# ============================================

# Keep performance metrics
-keep class com.edham.logistics.performance.** { *; }
-keep class com.edham.logistics.metrics.** { *; }

# Keep memory monitoring
-keep class com.edham.logistics.memory.** { *; }

# ============================================
# 🔍 Debugging Prevention Rules
# ============================================

# Remove debugging capabilities
-assumenosideffects class android.os.Debug {
    public static *** startMethodTracing(...);
    public static *** stopMethodTracing(...);
    public static *** threadCpuTimeNanos(...);
}

# Remove reflection-based debugging
-assumenosideffects class java.lang.reflect.** { *; }

# ============================================
# 📦 Third-Party Library Rules
# ============================================

# Keep OkHttp with security features
-keep class okhttp3.** { *; }
-keep class okhttp3.internal.** { *; }
-dontwarn okhttp3.**

# Keep Retrofit with security
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# Keep Gson with security
-keep class com.google.gson.** { *; }
-keep class com.google.gson.internal.** { *; }
-dontwarn com.google.gson.internal.**

# Keep Glide with security
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

# ============================================
# 🎯 Final Production Optimizations
# ============================================

# Remove all unused warnings
-dontwarn java.lang.**
-dontwarn javax.lang.**
-dontwarn javax.annotation.**
-dontwarn kotlin.**
-dontwarn kotlinx.**
-dontwarn androidx.**
-dontwarn com.google.**
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn com.google.gson.**
-dontwarn dagger.**
-dontwarn hilt.**
-dontwarn com.edham.logistics.**

# Keep essential metadata
-keep class kotlin.Metadata { *; }
-keep class kotlin.Unit { *; }
-keep class kotlin.jvm.internal.** { *; }

# Final optimization pass
-optimizationpasses 8
-allowaccessmodification
-dontpreverify
-useuniqueclassmembernames
