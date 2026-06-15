// ============================================
// 📱 Edham Logistics - Android Native (Kotlin)
// Root build.gradle.kts
// ============================================

plugins {
    id("com.android.application") version "9.2.1" apply false
    id("com.android.library") version "9.2.1" apply false
    // AGP 9.0+ has built-in Kotlin support. 
    // Manual application of org.jetbrains.kotlin.android is no longer required.
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.3.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.21" apply false
    id("com.google.devtools.ksp") version "2.3.8" apply false
    id("com.google.dagger.hilt.android") version "2.59.2" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.7" apply false
    id("com.google.firebase.firebase-perf") version "2.0.2" apply false
}

extra["kotlin_version"] = "2.3.21"
extra["compileSdk"] = 35
extra["targetSdk"] = 35

// Dependencies versions
extra["retrofitVersion"] = "2.9.0"
extra["okHttpVersion"] = "4.12.0"
extra["coroutinesVersion"] = "1.7.3"
extra["lifecycleVersion"] = "2.6.2"
extra["navigationVersion"] = "2.8.5"
extra["roomVersion"] = "2.8.4"
extra["hiltVersion"] = "2.59.2"
extra["coilVersion"] = "2.5.0"
extra["mapsVersion"] = "18.2.0"
extra["playServicesVersion"] = "21.0.1"

// Edham API Configuration
extra["apiBaseUrl"] = "http://10.0.2.2:8080/api/v1/"  // Emulator localhost
extra["apiTimeout"] = 30  // seconds

println("🚛 Edham Logistics Android Build Initialized")
println("🌐 API Base URL: ${extra["apiBaseUrl"]}")

// Temporary: disable kapt to surface real Kotlin errors when build shows
// "Could not load module <Error module>". Remove after fixing root cause.
if (project.hasProperty("debugKotlinWithoutKapt")) {
    subprojects {
        afterEvaluate {
            tasks.configureEach {
                if (name.contains("kapt", ignoreCase = true)) {
                    enabled = false
                }
            }
        }
    }
}
