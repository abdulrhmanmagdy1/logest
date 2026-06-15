# 📱 بناء APK - تعليمات بناء تطبيق Android

## 🎯 متطلبات البناء

### 1. المتطلبات الأساسية
- ✅ Android Studio Hedgehog (2023.1.1) أو أحدث
- ✅ JDK 17 أو أحدث
- ✅ Android SDK 34 (Android 14)
- ✅ Kotlin 1.9.0 أو أحدث
- ✅ Gradle 8.2

### 2. ملفات Gradle المطلوبة
```
📁 mobile-native-android/
├── 📄 build.gradle.kts (Project level)
├── 📄 settings.gradle.kts
├── 📄 gradle.properties
└── 📁 app/
    └── 📄 build.gradle.kts (App level)
```

## 🚀 خطوات البناء

### الخطوة 1: فتح المشروع
```bash
# افتح Android Studio
File → Open → اختر مجلد mobile-native-android
```

### الخطوة 2: مزامنة Gradle
```bash
# في Android Studio
Click: File → Sync Project with Gradle Files
# أو اضغط: Ctrl+Shift+O
```

### الخطوة 3: بناء APK
```bash
# طريقة 1: من Android Studio
Build → Build Bundle(s) / APK(s) → Build APK(s)

# طريقة 2: من Terminal
./gradlew assembleDebug      # APK للتطوير
./gradlew assembleRelease    # APK للإنتاج
```

### الخطوة 4: العثور على APK
```
المسار:
📁 app/build/outputs/apk/debug/app-debug.apk
📁 app/build/outputs/apk/release/app-release.apk
```

## ⚙️ إعدادات البناء

### إعداد التوقيع (للـ Release)
```kotlin
// app/build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file("edham-keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "edham"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### توليد Keystore
```bash
keytool -genkey -v \
  -keystore edham-keystore.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias edham
```

## 📦 أوامر Gradle السريعة

| الأمر | الوظيفة |
|-------|---------|
| `./gradlew clean` | تنظيف المشروع |
| `./gradlew build` | بناء كامل |
| `./gradlew assembleDebug` | APK Debug |
| `./gradlew assembleRelease` | APK Release |
| `./gradlew bundleRelease` | AAB للـ Play Store |

## 🔧 حل المشاكل الشائعة

### مشكلة 1: Gradle Sync Failed
```bash
# الحل
File → Invalidate Caches → Invalidate and Restart
```

### مشكلة 2: Out of Memory
```bash
# في gradle.properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
```

### مشكلة 3: Dependency Conflicts
```bash
# تحديث Dependencies
./gradlew dependencies --configuration implementation
```

## 📱 معلومات APK

| الخاصية | القيمة |
|---------|--------|
| اسم التطبيق | EDHAM Logistics |
| اسم الحزمة | com.edham.logistics |
| الحجم | ~15-20 MB |
| minSdk | 24 (Android 7.0) |
| targetSdk | 34 (Android 14) |
| الأقواس | armeabi-v7a, arm64-v8a |

## ✅ قائمة التحقق قبل البناء

- [ ] تحديث رقم الإصدار في `build.gradle.kts`
- [ ] تحديث `versionName` و `versionCode`
- [ ] التأكد من وجود Keystore للـ Release
- [ ] تشغيل الاختبارات: `./gradlew test`
- [ ] فحص الأمان: `./gradlew lint`
- [ ] التأكد من إعدادات ProGuard

## 🚀 رفع APK على Play Store

### توليد AAB (Android App Bundle)
```bash
./gradlew bundleRelease
```

### المسار النهائي
```
📁 app/build/outputs/bundle/release/app-release.aab
```

### رفع AAB
1. افتح [Google Play Console](https://play.google.com/console)
2. اختر التطبيق
3. Production → Create Release
4. ارفع ملف AAB
5. أكمل معلومات الإصدار
6. اضغط Review Release → Start Rollout

## 📞 دعم فني

للمساعدة في البناء:
- 📧 support@edham-logistics.com
- 💬 WhatsApp Business: +966 XX XXX XXXX

---
**🎉 بالتوفيق في إطلاق التطبيق!**
