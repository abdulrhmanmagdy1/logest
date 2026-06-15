// ============================================
// 📱 Edham Android App Module
// ============================================

plugins {
    id("com.android.application")
    // Note: org.jetbrains.kotlin.android is now built-in with AGP 9.2.1
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

android {
    namespace = "com.edham.logistics"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.edham.logistics"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Build Config Fields
        buildConfigField("String", "API_BASE_URL", "\"http://20.199.136.55:8080/api/v1/\"")
        buildConfigField("int", "API_TIMEOUT", "60")
        buildConfigField("String", "APP_NAME", "\"Edham Logistics\"")
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"AIzaSyBEgCemrt_6FP0EDD9iRmyKlzSG0EHjYco\"")

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            // تفعيل حماية الكود وتقليل الحجم
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // ملاحظة: ستحتاج لإعداد مفتاح التوقيع (Signing Key) يدوياً
            // حالياً سنتركها تستخدم مفتاح التصحيح لحين تجهيز مفتاحك الخاص
            signingConfig = signingConfigs.getByName("debug")
            
            // هنا نضع رابط السيرفر الحقيقي عند توفره
            buildConfigField("String", "API_BASE_URL", "\"https://api.edham-logistics.com/api/v1/\"")
        }
        debug {
            isDebuggable = true
            versionNameSuffix = "-debug"
            buildConfigField("String", "API_BASE_URL", "\"http://20.199.136.55:8080/api/v1/\"")
            
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

// Top-level kotlin block for AGP 9.0+ configuration
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    val coroutinesVersion = rootProject.extra["coroutinesVersion"] as String
    val lifecycleVersion = rootProject.extra["lifecycleVersion"] as String
    val navigationVersion = rootProject.extra["navigationVersion"] as String
    val roomVersion = rootProject.extra["roomVersion"] as String
    val hiltVersion = rootProject.extra["hiltVersion"] as String

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion")

    // AndroidX Core
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("com.google.android.material:material:1.11.0")

    // Architecture Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    // Room Database
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-fragment:1.1.0")
    
    // Hilt Testing
    testImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kspTest("com.google.dagger:hilt-compiler:$hiltVersion")

    // Networking - Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Image Loading - Coil
    implementation("io.coil-kt:coil:2.5.0")
    
    // Image Loading - Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    // PhotoView - Pinch to Zoom
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    
    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Google Maps & Location
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Permissions
    implementation("com.guolindev.permissionx:permissionx:1.7.1")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // DataStore (Preferences)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Security - Biometric
    implementation("androidx.biometric:biometric:1.1.0")

    // Security - EncryptedSharedPreferences
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")

    // Signature Pad
    implementation("com.github.gcacace:signature-pad:1.3.1")

    // CameraX
    val cameraxVersion = "1.3.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.runtime:runtime-livedata")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Lottie Animations
    implementation("com.airbnb.android:lottie:6.3.0")

    // Paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kspAndroidTest("com.google.dagger:hilt-compiler:$hiltVersion")
}

// Kotlin DSL for Android Gradle Plugin
configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, "seconds")
        cacheDynamicVersionsFor(0, "seconds")
    }
}
