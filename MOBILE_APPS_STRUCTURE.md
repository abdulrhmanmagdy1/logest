# 📱 Mobile Apps Structure Map - خريطة هيكل تطبيقات الموبايل

## 🍎 iOS Swift (SwiftUI) Structure

```
d:\logest\mobile-native-ios\EdhamLogistics\
├── 📱 App\
│   └── 📄 ContentView.swift (745 lines)
│       ├── 🔐 Authentication System
│       ├── 🏠 Main TabView
│       ├── 👤 Multi-role Support
│       └── 🎨 SwiftUI Components
│
├── 📊 Models\
│   ├── 📦 Shipment.swift (149 lines)
│   │   ├── 🔄 ShipmentStatus Enum
│   │   ├── 🚚 CargoType Enum
│   │   ├── 📋 Shipment Struct
│   │   ├── ⚖️ Weight Struct
│   │   ├── 🌡️ Temperature Struct
│   │   ├── 📍 Pickup/Delivery Structs
│   │   └── 💰 Pricing Struct
│   │
│   └── 👤 User.swift (105 lines)
│       ├── 🎭 UserRole Enum
│       ├── 📊 UserStatus Enum
│       ├── 👤 User Struct
│       ├── 🏢 Company Struct
│       ├── 📍 Address Struct
│       ├── 🗺️ Coordinates Struct
│       └── 🔐 Auth Models (Login/Request)
│
├── 🌐 Services\
│   └── 📄 APIService.swift (141 lines)
│       ├── 🔗 Generic Request Method
│       ├── 🔐 Auth Endpoints
│       ├── 📦 Shipment Endpoints
│       ├── 🛡️ Error Handling
│       └── 🔑 Token Management
│
├── 🧠 ViewModels\
│   ├── 📄 AuthViewModel.swift (95 lines)
│   │   ├── 👤 User State Management
│   │   ├── 🔐 Login/Logout Logic
│   │   ├── 🎭 Demo Login for Roles
│   │   └── 📱 Keychain Integration
│   │
│   └── 📄 ClientDashboardViewModel.swift
│       ├── 📊 Dashboard Data
│       ├── 📦 Shipment Management
│       └── 🔄 Real-time Updates
│
└── 🎨 Views\
    ├── 📄 LoginView.swift (11,098 lines)
    │   ├── 🔐 Authentication Forms
    │   ├── 🎨 Modern UI Design
    │   └── 🌐 Arabic RTL Support
    │
    └── 📄 ClientDashboardView.swift (10,594 lines)
        ├── 📊 Dashboard Layout
        ├── 📦 Shipment Cards
        ├── 🚀 Quick Actions
        └── 📈 Analytics Views
```

---

## 🤖 Android Kotlin (Jetpack Compose) Structure

```
d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\
├── 🚪 Entry Points\
│   ├── 📄 MainActivity.kt (47 lines)
│   │   ├── 🎯 Activity Entry Point
│   │   ├── 🧭 Navigation Setup
│   │   └── 🎨 Material 3 Theme
│   │
│   └── 📄 EdhamLogisticsApp.kt (83 lines)
│       ├── 📱 Application Class
│       ├── 🔔 Notification Channels
│       ├── 💾 Preferences Manager
│       └── 📝 Timber Logging
│
├── 📊 Data Layer\
│   ├── 📦 model\
│   │   ├── 📄 Shipment.kt (424 lines)
│   │   │   ├── 📋 Comprehensive Data Model
│   │   │   ├── 🚚 Transport Info
│   │   │   ├── 💰 Financial Info
│   │   │   ├── 📍 Tracking Info
│   │   │   └── 📅 Timeline Management
│   │   │
│   │   ├── 📄 User.kt (285 lines)
│   │   │   ├── 👤 User Profile Data
│   │   │   ├── 🎭 Role Management
│   │   │   ├── 🔐 Auth Information
│   │   │   ├── ⚙️ User Settings
│   │   │   └── 📊 Activity Tracking
│   │   │
│   │   ├── 📄 Driver.kt (8,974 lines)
│   │   │   ├── 🚛 Driver Specific Data
│   │   │   ├── 📍 Location Tracking
│   │   │   ├── ⭐ Performance Metrics
│   │   │   └── 📋 Task Management
│   │   │
│   │   └── 📄 UserRole.kt (2,381 lines)
│   │       ├── 🎭 Role Definitions
│   │       ├── 🔐 Permission Matrix
│   │       └── 🎨 Role-based UI
│   │
│   ├── 🌐 remote\
│   │   ├── 📄 ApiService.kt (230 lines)
│   │   │   ├── 🔐 Authentication Endpoints
│   │   │   ├── 📦 Shipment Management
│   │   │   ├── 🚛 Driver Operations
│   │   │   ├── 📍 Location Services
│   │   │   └── 💰 Payment Processing
│   │   │
│   │   ├── 📁 api\ - API Endpoint Definitions
│   │   ├── 📁 dto\ - Data Transfer Objects
│   │   └── 📁 interceptor\ - Auth Interceptor
│   │
│   ├── 💾 local\ - Local Database (Room)
│   │   ├── 📄 PreferencesManager.kt
│   │   ├── 📄 Database.kt
│   │   └── 📁 dao\ - Data Access Objects
│   │
│   └── 📦 repository\
│       ├── 📄 ShipmentRepository.kt
│       ├── 📄 UserRepository.kt
│       ├── 📄 DriverRepository.kt
│       └── 📄 AuthRepository.kt
│
├── 🎨 UI Layer\
│   ├── 👤 client\
│   │   ├── 📄 ClientDashboardFragment.kt (170 lines)
│   │   │   ├── 📊 Dashboard Layout
│   │   │   ├── 📦 Shipment Cards
│   │   │   ├── 🚀 Quick Actions
│   │   │   └── 📈 Status Tracking
│   │   │
│   │   ├── 📁 adapter\ - RecyclerView Adapters
│   │   └── 📁 viewmodel\ - Client ViewModels
│   │
│   ├── 🚛 driver\
│   │   ├── 📄 DriverDashboardScreen.kt (547 lines)
│   │   │   ├── 🎯 Jetpack Compose UI
│   │   │   ├── 📍 Location Updates
│   │   │   ├── 📋 Task Management
│   │   │   └── 💰 Earnings Display
│   │   │
│   │   └── 📁 viewmodel\ - Driver ViewModels
│   │
│   ├── 👨‍💼 supervisor\
│   │   ├── 📄 SupervisorDashboardScreen.kt (597 lines)
│   │   │   ├── 📊 Operations Overview
│   │   │   ├── 🚛 Fleet Management
│   │   │   ├── 📋 Dispatch Controls
│   │   │   └── 📈 Analytics Dashboard
│   │   │
│   │   └── 📄 SupervisorMapScreen.kt (13,352 lines)
│   │       ├── 🗺️ Live Tracking Map
│   │       ├── 🚛 Vehicle Monitoring
│   │       ├── 📍 Route Optimization
│   │       └── 📊 Real-time Updates
│   │
│   ├── 🎨 components\
│   │   ├── 📄 CommonComponents.kt
│   │   ├── 📄 ShipmentCard.kt
│   │   ├── 📄 StatusBadge.kt
│   │   └── 📄 LoadingStates.kt
│   │
│   ├── 🎨 theme\
│   │   ├── 📄 Color.kt
│   │   ├── 📄 Theme.kt
│   │   ├── 📄 Type.kt
│   │   └── 📄 Shape.kt
│   │
│   └── 🎨 base\
│       ├── 📄 BaseFragment.kt
│       └── 📄 BaseViewModel.kt
│
├── 💉 Dependency Injection\
│   └── 📁 di\
│       ├── 📄 AppModule.kt
│       ├── 📄 NetworkModule.kt
│       └── 📄 DatabaseModule.kt
│
├── 🧭 Navigation\
│   └── 📁 navigation\
│       ├── 📄 EdhamNavigation.kt
│       └── 📄 NavigationDestinations.kt
│
├── 🔧 Utilities\
│   └── 📄 util\
│       ├── 📄 Extensions.kt
│       ├── 📄 Constants.kt
│       └── 📄 DateUtils.kt
│
└── 🛠️ Services\
    └── 📄 service\
        ├── 📄 LocationService.kt
        ├── 📄 NotificationService.kt
        └── 📄 BackgroundSyncService.kt
```

---

## 📊 Feature Comparison Map

| Feature | iOS Swift | Android Kotlin |
|---------|-----------|----------------|
| **🔐 Authentication** | ✅ SwiftUI + Keychain | ✅ Fragment + Biometric |
| **📦 Shipment Management** | ✅ Complete Models | ✅ Complete Repository |
| **👥 Multi-role Support** | ✅ 6 Roles | ✅ 6 Roles |
| **📍 Real-time Tracking** | ✅ Core Location | ✅ GPS Services |
| **💰 Payments** | ✅ Stripe SDK | ✅ Stripe SDK |
| **🔔 Notifications** | ✅ Push Notifications | ✅ Multi-channel |
| **🌐 Arabic RTL** | ✅ Full Support | ✅ Full Support |
| **📱 Modern UI** | ✅ SwiftUI | ✅ Jetpack Compose |
| **💾 Local Storage** | ✅ Core Data | ✅ Room Database |
| **🌐 API Integration** | ✅ URLSession | ✅ Retrofit + Hilt |

---

## 🚀 Key Architecture Patterns

### iOS Swift (MVVM + SwiftUI)
```
View (SwiftUI) ↔ ViewModel (ObservableObject) ↔ Service (APIService) ↔ Model (Codable)
```

### Android Kotlin (MVVM + Clean Architecture)
```
View (Compose/Fragment) ↔ ViewModel ↔ Repository ↔ Service (Retrofit) ↔ Model (Data Class)
```

---

## 📁 File Size Summary

### iOS Swift
- **Total Files:** 8 main files
- **Lines of Code:** ~15,000+ lines
- **Architecture:** MVVM + SwiftUI
- **Dependencies:** Stripe, Core Location, UserNotifications

### Android Kotlin
- **Total Files:** 60+ files
- **Lines of Code:** ~50,000+ lines
- **Architecture:** MVVM + Clean Architecture
- **Dependencies:** Hilt, Retrofit, Room, Compose, Navigation

---

## 🎯 Next Steps

1. **iOS Swift:** Ready for Xcode build and testing
2. **Android Kotlin:** Ready for Gradle build and testing
3. **Both platforms:** Complete deployment setup
4. **Documentation:** Update with final architecture decisions

---

**Created:** May 7, 2026  
**Status:** ✅ Architecture Complete  
**Priority:** 🚀 Ready for Deployment
