# 🚛 EDHAM Logistics - Android Application

<div align="center">

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.22-blue.svg)]()
[![Compose](https://img.shields.io/badge/compose-1.5.8-green.svg)]()
[![License](https://img.shields.io/badge/license-MIT-blue.svg)]()

**نظام النقل المبرد المتكامل**

</div>

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

**EDHAM Logistics** هو تطبيق Android احترافي مبني باستخدام **Kotlin** و **Jetpack Compose** يدير عمليات النقل- **🤖 تطبيق أندرويد أصلي (45 شاشة):** تطبيق احترافي بتصميم نيوني متقدم
  - **الشاشات الأساسية (10)**
  - **الشاشات المتقدمة (10)**
  - **شاشات الإدارة (25)**

### 🚛 أدوار النظام
- 🚛 **سائق** - إدارة الشحنات والتتبع
- 👨‍💼 **مشرف** - الإشراف على العمليات
- 💰 **محاسب** - إدارة الفواتير والمدفوعات
- 🔧 **صيانة** - متابعة المركبات والصيانة

---

## ✨ Features

### 🏠 Core Features

- ✅ **Multi-Role Support** - 5 أدوار مختلفة في تطبيق واحد
- ✅ **45 Professional Screens** - 45 شاشة احترافية متكاملة
- ✅ **Real-time Tracking** - تتبع GPS مباشر للشحنات
- ✅ **Interactive Maps** - خرائط تفاعلية مع Google Maps
- ✅ **Professional UI/UX** - تصميم Material Design 3
- ✅ **Secure Authentication** - نظام مصادقة آمن
- ✅ **Multi-Language** - دعم اللغة العربية والإنجليزية
- ✅ **Offline Support** - العمل بدون إنترنت
- ✅ **Push Notifications** - إشعارات فورية

### 📱 Dashboard Features

#### Customer Dashboard
- 📦 طلب شحنة جديدة
- 📍 تتبع الشحنات
- 💳 عرض الفواتير والمدفوعات
- 📜 سجل الشحنات
- ⭐ تقييم السائقين

#### Driver Dashboard
- 🚚 عرض الرحلات المخصصة
- 🗺️ التنقل باستخدام الخرائط
- 📸 إرفاق صور والتوقيع
- 💬 التواصل مع العملاء
- 📊 إحصائيات الأداء

#### Supervisor Dashboard
- 📊 لوحة تحكم شاملة
- 🗺️ خريطة تتبع مباشر
- 👥 إدارة السائقين
- 📋 تعيين الرحلات
- ⚠️ التنبيهات والإشعارات

#### Accountant Dashboard
- 💰 لوحة المالية
- 🧾 إنشاء الفواتير
- 📄 سندات القبض
- 📈 التقارير المالية
- 💳 إدارة المدفوعات

#### Maintenance Dashboard
- 🚙 إدارة المركبات
- 🔧 جدولة الصيانة
- 📦 إدارة قطع الغيار
- ⚠️ التنبيهات الفنية
- 📊 صحة الأسطول

---

## 🏗️ Architecture

### MVVM Architecture Pattern

```
┌─────────────────────────────────────────┐
│         📱 Presentation Layer           │
│   (UI - Jetpack Compose Screens)       │
├─────────────────────────────────────────┤
│         🔄 Domain Layer                 │
│   (ViewModels - Business Logic)          │
├─────────────────────────────────────────┤
│         💾 Data Layer                   │
│   (Repositories + API + Database)         │
├─────────────────────────────────────────┤
│         🌐 Network Layer                 │
│   (Retrofit + OkHttp + Interceptors)    │
└─────────────────────────────────────────┘
```

### Clean Architecture Principles

- **Separation of Concerns** - كل طبقة لها مهمة محددة
- **Dependency Inversion** - الاعتماد على Abstractions
- **Testability** - كل مكون قابل للاختبار
- **Scalability** - سهل التوسع والتطوير

---

## 🛠️ Tech Stack

### Core Technologies

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Kotlin | 1.9.22 |
| **UI Framework** | Jetpack Compose | 1.5.8 |
| **Architecture** | MVVM | - |
| **DI** | Hilt | 2.48 |
| **Networking** | Retrofit | 2.9.0 |
| **Database** | Room | 2.6.1 |
| **Maps** | Google Maps | 18.2.0 |

### Dependencies

```kotlin
// Jetpack Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.02.00"))

// Core
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

// Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.7")

// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Dependency Injection
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-compiler:2.48")

// Maps
implementation("com.google.android.gms:play-services-maps:18.2.0")
implementation("com.google.android.gms:play-services-location:21.0.1")

// Storage
implementation("androidx.datastore:datastore-preferences:1.0.0")
implementation("androidx.room:room-runtime:2.6.1")

// Testing
testImplementation("junit:junit:4.13.2")
androidTestImplementation("androidx.test.ext:junit:1.1.5")
```

---

## 📁 Project Structure

```
📁 mobile-native-android/
│
├── 📁 app/
│   ├── 📁 src/
│   │   ├── 📁 main/
│   │   │   ├── 📁 java/com/edham/logistics/
│   │   │   │   │
│   │   │   │   ├── 📁 data/
│   │   │   │   │   ├── 📁 model/
│   │   │   │   │   │   ├── 📄 UserRole.kt
│   │   │   │   │   │   ├── 📄 Shipment.kt
│   │   │   │   │   │   ├── 📄 Driver.kt
│   │   │   │   │   │   └── 📄 User.kt
│   │   │   │   │   │
│   │   │   │   │   ├── 📁 repository/
│   │   │   │   │   │   ├── 📄 AuthRepository.kt
│   │   │   │   │   │   ├── 📄 RoleRepository.kt
│   │   │   │   │   │   ├── 📄 ShipmentRepository.kt
│   │   │   │   │   │   └── 📄 DriverRepository.kt
│   │   │   │   │   │
│   │   │   │   │   └── 📁 network/
│   │   │   │   │       ├── 📄 ApiService.kt (70+ endpoints)
│   │   │   │   │       └── 📄 RetrofitClient.kt
│   │   │   │   │
│   │   │   │   ├── 📁 di/
│   │   │   │   │   ├── 📄 AppModule.kt
│   │   │   │   │   └── 📄 NetworkModule.kt
│   │   │   │   │
│   │   │   │   ├── 📁 navigation/
│   │   │   │   │   └── 📄 AppNavigation.kt
│   │   │   │   │
│   │   │   │   ├── 📁 ui/
│   │   │   │   │   ├── 📁 splash/
│   │   │   │   │   │   └── 📄 SplashScreen.kt
│   │   │   │   │   │
│   │   │   │   │   ├── 📁 auth/
│   │   │   │   │   │   ├── 📄 AuthViewModel.kt
│   │   │   │   │   │   ├── 📄 LoginScreen.kt
│   │   │   │   │   │   └── 📄 RegisterScreen.kt
│   │   │   │   │   │
│   │   │   │   │   ├── 📁 roleselection/
│   │   │   │   │   │   ├── 📄 RoleSelectionScreen.kt
│   │   │   │   │   │   └── 📄 RoleSelectionViewModel.kt
│   │   │   │   │   │
│   │   │   │   │   ├── 📁 customer/
│   │   │   │   │   │   └── 📄 CustomerDashboardScreen.kt
│   │   │   │   │   │
│   │   │   │   │   ├── 📁 driver/
│   │   │   │   │   │   └── 📄 DriverDashboardScreen.kt
│   │   │   │   │   │
│   │   │   │   │   ├── 📁 accountant/
│   │   │   │   │   │   └── 📄 AccountantDashboardScreen.kt
│   │   │   │   │   │
│   │   │   │   │   ├── 📁 supervisor/
│   │   │   │   │   │   ├── 📄 SupervisorDashboardScreen.kt
│   │   │   │   │   │   └── 📄 SupervisorMapScreen.kt
│   │   │   │   │   │
│   │   │   │   │   ├── 📁 maintenance/
│   │   │   │   │   │   └── 📄 MaintenanceDashboardScreen.kt
│   │   │   │   │   │
│   │   │   │   │   ├── 📁 components/
│   │   │   │   │   │   ├── 📄 CommonComponents.kt
│   │   │   │   │   │   └── 📄 LoadingButton.kt
│   │   │   │   │   │
│   │   │   │   │   └── 📁 theme/
│   │   │   │   │       ├── 📄 Color.kt
│   │   │   │   │       ├── 📄 Theme.kt
│   │   │   │   │       └── 📄 Type.kt
│   │   │   │   │
│   │   │   │   ├── 📄 EdhamLogisticsApp.kt
│   │   │   │   └── 📄 MainActivity.kt
│   │   │   │
│   │   │   ├── 📁 res/
│   │   │   │   ├── 📁 drawable/
│   │   │   │   ├── 📁 values/
│   │   │   │   │   ├── 📄 colors.xml
│   │   │   │   │   ├── 📄 strings.xml
│   │   │   │   │   └── 📄 themes.xml
│   │   │   │   └── 📁 mipmap-xxxhdpi/
│   │   │   │
│   │   │   └── 📄 AndroidManifest.xml
│   │   │
│   │   └── 📁 test/
│   │
│   ├── 📄 build.gradle.kts
│   └── 📄 proguard-rules.pro
│
├── 📄 build.gradle.kts
├── 📄 settings.gradle.kts
├── 📄 gradle.properties
├── 📄 README.md
├── 📄 BUILD_APK.md
└── 📄 LICENSE
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio Hedgehog** (2023.1.1) or later
- **JDK 17** or later
- **Android SDK 34**
- **Kotlin 1.9.22**

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/edham-logistics/mobile-app.git
cd mobile-native-android
```

2. **Open in Android Studio**
- File → Open → Select project folder
- Wait for Gradle sync to complete

3. **Configure API Keys**
- Add your Google Maps API key in `AndroidManifest.xml`
- Configure backend API URL in `ApiService.kt`

4. **Build the project**
```bash
./gradlew build
```

5. **Run on device/emulator**
```bash
./gradlew installDebug
```

---

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Code Coverage
```bash
./gradlew jacocoTestReport
```

---

## 📱 Screenshots

<div align="center">

| Splash Screen | Login Screen | Role Selection |
|--------------|--------------|----------------|
| <img src="screenshots/splash.png" width="200"/> | <img src="screenshots/login.png" width="200"/> | <img src="screenshots/roles.png" width="200"/> |

| Customer Dashboard | Driver Dashboard | Supervisor Map |
|-------------------|------------------|----------------|
| <img src="screenshots/customer.png" width="200"/> | <img src="screenshots/driver.png" width="200"/> | <img src="screenshots/map.png" width="200"/> |

</div>

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add documentation comments for public APIs
- Write unit tests for new features

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - UI Framework
- [Material Design 3](https://m3.material.io/) - Design System
- [Retrofit](https://square.github.io/retrofit/) - HTTP Client
- [Hilt](https://dagger.dev/hilt/) - Dependency Injection

---

## 📞 Support

For support and questions:
- 📧 Email: support@edham-logistics.com
- 🌐 Website: [https://edham-logistics.com](https://edham-logistics.com)

---

<div align="center">

**Made with ❤️ by EDHAM Logistics Team**

</div>
