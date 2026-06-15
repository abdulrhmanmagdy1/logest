# 🏗️ دليل بناء تطبيق إدهام اللوجستي

## 📋 المتطلبات الأساسية

### **تطوير الأندرويد**
- **Android Studio** - الإصدار الأخير (Arctic Fox أو أحدث)
- **JDK 11** - Java Development Kit
- **Android SDK** - API 24 (Android 7.0) كحد أدنى
- **Kotlin** - الإصدار 1.9.0 أو أحدث

### **تطوير الويب**
- **Node.js** - الإصدار 18 أو أحدث
- **npm** - الإصدار 9 أو أحدث
- **Git** - للتحكم في الإصدارات

## 🚀 خطوات البناء

### **1. إعداد بيئة العمل**

#### **تثبيت Android Studio**
```bash
# تحميل Android Studio من الموقع الرسمي
# https://developer.android.com/studio

# تثبيت SDK و Build Tools
# Android SDK Platform 34
# Android SDK Build-Tools 34.0.0
# Android NDK (Side by side) 25.1.8937393
```

#### **إعداد Node.js**
```bash
# تحميل Node.js من الموقع الرسمي
# https://nodejs.org/

# التحقق من التثبيت
node --version
npm --version
```

### **2. استنساخ المشروع**

```bash
# استنساخ المستودع
git clone https://github.com/edham/logistics-app.git
cd logistics-app

# تحديث المستودع
git pull origin main
```

### **3. بناء تطبيق الأندرويد**

#### **الإعدادات الأولية**
```bash
cd mobile-native-android

# فتح المشروع في Android Studio
# File -> Open -> اختر مجلد mobile-native-android

# انتظر حتى اكتمال Gradle Sync
```

#### **إعدادات Gradle**
```kotlin
// في ملف build.gradle (Project level)
buildscript {
    ext {
        compose_version = '1.5.4'
        kotlin_version = '1.9.10'
        hilt_version = '2.48'
    }
}

// في ملف build.gradle (App level)
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.edham.logistics"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary true
        }
    }
    
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
        debug {
            debuggable true
            applicationIdSuffix ".debug"
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = '11'
    }
    
    buildFeatures {
        compose true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion compose_version
    }
    
    packagingOptions {
        resources {
            excludes += '/META-INF/{AL2.0,LGPL2.1}'
        }
    }
}
```

#### **الاعتماديات الرئيسية**
```kotlin
dependencies {
    // Compose BOM
    implementation platform('androidx.compose:compose-bom:2023.10.01')
    
    // Core Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.activity:activity-compose:1.8.0'
    
    // Compose
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.compose.material:material-icons-extended'
    
    // Navigation
    implementation 'androidx.navigation:navigation-compose:2.7.4'
    
    // Hilt
    implementation "com.google.dagger:hilt-android:$hilt_version"
    implementation 'androidx.hilt:hilt-navigation-compose:1.1.0'
    kapt "com.google.dagger:hilt-compiler:$hilt_version"
    
    // Room
    implementation 'androidx.room:room-runtime:2.5.0'
    implementation 'androidx.room:room-ktx:2.5.0'
    kapt 'androidx.room:room-compiler:2.5.0'
    
    // Network
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    
    // Image Loading
    implementation 'io.coil-kt:coil-compose:2.4.0'
    
    // Camera and AR
    implementation 'androidx.camera:camera-camera2:1.3.0'
    implementation 'androidx.camera:camera-lifecycle:1.3.0'
    implementation 'androidx.camera:camera-view:1.3.0'
    
    // Location
    implementation 'com.google.android.gms:play-services-location:21.0.1'
    implementation 'com.google.android.gms:play-services-maps:18.2.0'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation platform('androidx.compose:compose-bom:2023.10.01')
    androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
    debugImplementation 'androidx.compose.ui:ui-tooling'
    debugImplementation 'androidx.compose.ui:ui-test-manifest'
}
```

#### **بناء التطبيق**
```bash
# بناء نسخة Debug
./gradlew assembleDebug

# بناء نسخة Release
./gradlew assembleRelease

# تشغيل الاختبارات
./gradlew test

# تشغيل التطبيق على المحاكي
./gradlew installDebug

# تنظيف المشروع
./gradlew clean
```

### **4. بناء تطبيق الويب**

#### **الإعدادات الأولية**
```bash
cd client

# تثبيت الاعتماديات
npm install

# تثبيت الاعتماديات الإضافية
npm install @reduxjs/toolkit react-redux
npm install @mui/material @emotion/react @emotion/styled
npm install @mui/icons-material
npm install axios
npm install react-router-dom
```

#### **إعدادات package.json**
```json
{
  "name": "edham-logistics-web",
  "version": "1.0.0",
  "private": true,
  "dependencies": {
    "@reduxjs/toolkit": "^1.9.7",
    "@mui/material": "^5.14.15",
    "@mui/icons-material": "^5.14.15",
    "@emotion/react": "^11.11.1",
    "@emotion/styled": "^11.11.0",
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-redux": "^8.1.3",
    "react-router-dom": "^6.17.0",
    "axios": "^1.6.0",
    "web-vitals": "^3.5.0"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject",
    "lint": "eslint src --ext .js,.jsx,.ts,.tsx",
    "lint:fix": "eslint src --ext .js,.jsx,.ts,.tsx --fix",
    "format": "prettier --write src/**/*.{js,jsx,ts,tsx,css,md}"
  },
  "devDependencies": {
    "@types/react": "^18.2.37",
    "@types/react-dom": "^18.2.15",
    "eslint": "^8.54.0",
    "prettier": "^3.1.0",
    "react-scripts": "5.0.1",
    "typescript": "^4.9.5"
  }
}
```

#### **بناء التطبيق**
```bash
# تشغيل بيئة التطوير
npm start

# بناء نسخة الإنتاج
npm run build

# تشغيل الاختبارات
npm test

# فحص الكود
npm run lint

# تنسيق الكود
npm run format
```

### **5. بناء الخادم الخلفي**

#### **الإعدادات الأولية**
```bash
cd backend

# تثبيت الاعتماديات
npm install

# تثبيت الاعتماديات الإضافية
npm install express mongoose cors helmet morgan
npm install jsonwebtoken bcryptjs
npm install socket.io nodemailer
npm install multer sharp
npm install dotenv compression
```

#### **إعدادات package.json**
```json
{
  "name": "edham-logistics-backend",
  "version": "1.0.0",
  "description": "Backend for Edham Logistics App",
  "main": "server.js",
  "scripts": {
    "start": "node server.js",
    "dev": "nodemon server.js",
    "test": "jest",
    "test:watch": "jest --watch",
    "lint": "eslint . --ext .js",
    "lint:fix": "eslint . --ext .js --fix"
  },
  "dependencies": {
    "express": "^4.18.2",
    "mongoose": "^7.6.3",
    "cors": "^2.8.5",
    "helmet": "^7.1.0",
    "morgan": "^1.10.0",
    "jsonwebtoken": "^9.0.2",
    "bcryptjs": "^2.4.3",
    "socket.io": "^4.7.4",
    "nodemailer": "^6.9.7",
    "multer": "^1.4.5-lts.1",
    "sharp": "^0.32.6",
    "dotenv": "^16.3.1",
    "compression": "^1.7.4"
  },
  "devDependencies": {
    "nodemon": "^3.0.1",
    "jest": "^29.7.0",
    "supertest": "^6.3.3",
    "eslint": "^8.54.0"
  }
}
```

#### **بناء الخادم**
```bash
# تشغيل بيئة التطوير
npm run dev

# تشغيل بيئة الإنتاج
npm start

# تشغيل الاختبارات
npm test

# فحص الكود
npm run lint
```

## 🔧 إعدادات البيئة

### **متغيرات البيئة للخادم**
```env
# إنشاء ملف .env في مجلد backend

# إعدادات الخادم
PORT=3000
NODE_ENV=development

# قاعدة البيانات
MONGODB_URI=mongodb://localhost:27017/edham-logistics
MONGODB_TEST_URI=mongodb://localhost:27017/edham-logistics-test

# المصادقة
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRE=7d
JWT_REFRESH_EXPIRE=30d

# البريد الإلكتروني
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USER=your-email@gmail.com
EMAIL_PASS=your-app-password

# الخدمات الخارجية
GOOGLE_MAPS_API_KEY=your-google-maps-api-key
WEATHER_API_KEY=your-weather-api-key
STRIPE_SECRET_KEY=your-stripe-secret-key

# التخزين السحابي
AWS_ACCESS_KEY_ID=your-aws-access-key
AWS_SECRET_ACCESS_KEY=your-aws-secret-key
AWS_REGION=us-east-1
AWS_S3_BUCKET=edham-logistics-uploads

# Redis (للتخزين المؤقت)
REDIS_URL=redis://localhost:6379
```

### **إعدادات الأندرويد**
```xml
<!-- في ملف local.properties -->
MAPS_API_KEY=your-google-maps-api-key

<!-- في م AndroidManifest.xml -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${MAPS_API_KEY}" />
```

## 📱 التوقيع الرقمي (Signing)

### **إنشاء مفتاح التوقيع**
```bash
# في مجلد app
keytool -genkey -v -keystore edham-logistics.keystore -alias edham -keyalg RSA -keysize 2048 -validity 10000
```

### **إعدادات التوقيع في build.gradle**
```kotlin
android {
    signingConfigs {
        release {
            storeFile file('../edham-logistics.keystore')
            storePassword 'your-store-password'
            keyAlias 'edham'
            keyPassword 'your-key-password'
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

## 🚀 النشر

### **نشر الأندرويد**

#### **Google Play Store**
1. **إنشاء حساب مطور** على Google Play Console
2. **إنشاء تطبيق جديد**
3. **رفع ملف APK/AAB**
4. **ملء معلومات التطبيق**
5. **المراجعة والنشر**

#### **نشر مباشر**
```bash
# بناء ملف APK موقّع
./gradlew assembleRelease

# ملف APK سيكون في:
# app/build/outputs/apk/release/app-release.apk
```

### **نشر الويب**

#### **Netlify**
```bash
# بناء التطبيق
cd client
npm run build

# رفع مجلد build إلى Netlify
# أو استخدام Netlify CLI
npm install -g netlify-cli
netlify deploy --prod --dir=build
```

#### **Vercel**
```bash
# تثبيت Vercel CLI
npm install -g vercel

# النشر
vercel --prod
```

### **نشر الخادم**

#### **Heroku**
```bash
# تثبيت Heroku CLI
npm install -g heroku

# تسجيل الدخول
heroku login

# إنشاء تطبيق
heroku create edham-logistics-api

# رفع الكود
git push heroku main

# إعدادات البيئة
heroku config:set NODE_ENV=production
heroku config:set MONGODB_URI=your-production-mongodb-uri
```

#### **Docker**
```dockerfile
# Dockerfile للخادم
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY . .

EXPOSE 3000

CMD ["npm", "start"]
```

```bash
# بناء الحاوية
docker build -t edham-logistics-api .

# تشغيل الحاوية
docker run -p 3000:3000 edham-logistics-api
```

## 🧪 الاختبار

### **اختبارات الأندرويد**
```bash
# تشغيل اختبارات الوحدة
./gradlew test

# تشغيل اختبارات الأجهزة
./gradlew connectedAndroidTest

# إنشاء تقرير التغطية
./gradlew jacocoTestReport
```

### **اختبارات الويب**
```bash
# تشغيل اختبارات Jest
npm test

# تشغيل اختبارات مع التغطية
npm test -- --coverage

# تشغيل اختبارات E2E
npm run test:e2e
```

### **اختبارات الخادم**
```bash
# تشغيل اختبارات الوحدة
npm test

# تشغيل اختبارات التكامل
npm run test:integration

# تشغيل اختبارات API
npm run test:api
```

## 📊 المراقبة والتحليل

### **Firebase Analytics**
```kotlin
// في تطبيق الأندرويد
implementation 'com.google.firebase:firebase-analytics-ktx:21.5.0'
implementation 'com.google.firebase:firebase-crashlytics-ktx:18.6.0'
```

### **Sentry**
```javascript
// في تطبيق الويب
import * as Sentry from "@sentry/react";

Sentry.init({
  dsn: "your-sentry-dsn",
  environment: process.env.NODE_ENV,
});
```

## 🔧 استكشاف الأخطاء

### **مشاكل شائعة في الأندرويد**
- **Gradle Sync فشل**: تحقق من إعدادات SDK والإنترنت
- **Build فشل**: نظف المشروع `./gradlew clean`
- **توقيع التطبيق**: تأكد من ملف keystore وكلمة المرور

### **مشاكل شائعة في الويب**
- **npm install فشل**: احذف node_modules و package-lock.json
- **Port مشغول**: غير المنفذ في package.json
- **CORS errors**: تأكد من إعدادات CORS في الخادم

### **مشاكل شائعة في الخادم**
- **اتصال MongoDB**: تأكد من تشغيل MongoDB
- **متغيرات البيئة**: تحقق من ملف .env
- **Port مشغول**: استخدم منفذ مختلف

---

**🎉 تهانينا! الآن لديك تطبيق إدهام اللوجستي الاحترافي جاهز للنشر!**
