# إدهام - نظام لوجستي متكامل (End-to-End Logistics System)

## نظرة عامة

إدهام هو نظام لوجستي متكامل لإدارة النقل والشحن، مصمم لإدارة كل جوانب العمليات اللوجستية من طلب الحمولة إلى التسليم. النظام يدعم 4 أدوار رئيسية: العميل، المشرف، السائق، والمحاسب.

## البنية التقنية

### تطبيق السائق (Driver App)
- **المنصة**: Flutter
- **اللغة**: Dart
- **إدارة الحالة**: Provider
- **التخزين المحلي**: SQLite, SharedPreferences
- **الخرائط**: Google Maps Flutter
- **الموقع**: Geolocator, Location

### لوحة التحكم (Admin Dashboard)
- **المنصة**: React
- **اللغة**: JavaScript
- **إدارة الحالة**: React Context / Redux
- **المكونات**: Material-UI, Ant Design

## الخدمات الأساسية (Core Services)

### 1. AuthService
إدارة المصادقة وتسجيل الدخول
- تسجيل الدخول والخروج
- إدارة الـ Tokens
- تحديث الملف الشخصي
- OTP Login
- إعادة تعيين كلمة المرور

### 2. TripService
إدارة الرحلات والطلبات
- جلب الرحلات المتاحة
- قبول/رفض الرحلات
- تحديث حالة الرحلة
- تتبع الموقع
- سجل الرحلات

### 3. LocationService
خدمات الموقع وتتبع GPS
- تتبع الموقع الحالي
- حساب المسافات
- تتبع في الخلفية
- إدارة الأذونات

### 4. UploadService
رفع الصور والمستندات
- التقاط الصور من الكاميرا
- اختيار من المعرض
- رفع متعدد
- تتبع التقدم

## الخدمات المتقدمة (Advanced Services)

### 1. AdvancedTrackingService
نظام تتبع متقدم مع:
- **Live Tracking**: تحديث كل ثواني
- **Route Optimization**: أفضل طريق باستخدام Google Maps API
- **Route Deviation Detection**: تنبيهات عند الخروج عن المسار
- **ETA Calculation**: وقت الوصول المتوقع
- **Traffic Information**: معلومات المرور
- **Geocoding**: تحويل العناوين لإحداثيات والعكس
- **Nearby Places**: أماكن قريبة (محطات وقود، استراحات)

### 2. NotificationService
نظام إشعارات ذكي مع:
- **Push Notifications**: Firebase Cloud Messaging
- **Local Notifications**: إشعارات محلية
- **SMS Integration**: رسائل نصية للأمور المهمة
- **Email Integration**: إرسال إيميلات
- **Scheduled Notifications**: جدولة الإشعارات
- **Notification Templates**: قوالب جاهزة للإشعارات

### 3. RoleBasedAccessService
نظام صلاحيات متقدم:
- **Admin**: صلاحيات كاملة
- **Supervisor**: إدارة العمليات
- **Accountant**: إدارة المالية
- **Driver**: صلاحيات السائق
- **Client**: صلاحيات العميل
- **Permission System**: نظام صلاحيات دقيق
- **Menu Access**: تحديد القوائم المتاحة لكل دور

### 4. AnalyticsService
نظام تقارير وتحليلات قوي:
- **Dashboard Overview**: نظرة عامة
- **Trip Analytics**: تحليلات الرحلات
- **Driver Performance**: أداء السائقين
- **Revenue Analytics**: تحليلات الإيرادات
- **Daily Trip Counts**: عدد الرحلات اليومية
- **Average Delivery Time**: متوسط وقت التوصيل
- **Top Drivers**: أفضل السائقين
- **Fleet Utilization**: استغلال الأسطول
- **Revenue by Region**: الإيرادات حسب المنطقة
- **Custom Reports**: تقارير مخصصة
- **Export**: تصدير التقارير (PDF, Excel, CSV)
- **Real-time Stats**: إحصائيات حية

### 5. CRMService
نظام إدارة العملاء:
- **Customer Management**: إدارة العملاء
- **Customer Tiers**: تصنيف العملاء (Regular, Silver, Gold, Platinum, VIP)
- **Order History**: سجل الطلبات
- **Customer Notes**: ملاحظات للعملاء
- **Tier Benefits**: مزايا كل تصنيف
- **Churn Prediction**: التنبؤ بفقدان العملاء
- **Customer Lifetime Value**: القيمة العمرية للعميل
- **Customer Insights**: رؤى عن العملاء

### 6. SchedulingService
نظام الجدولة:
- **Scheduled Trips**: رحلات مجدولة
- **Available Slots**: الفتحات المتاحة
- **Slot Availability**: التحقق من توفر الوقت
- **Schedule Optimization**: تحسين الجدولة
- **Fleet Availability**: توفر الأسطول
- **Driver Assignment**: تعيين السائقين
- **Driver Schedule**: جدول السائق
- **Recurring Schedule**: جدول متكرر
- **Conflict Detection**: كشف التعارضات
- **Schedule Statistics**: إحصائيات الجدولة

### 7. LocalizationService
نظام تعدد اللغات:
- **Arabic**: اللغة العربية
- **English**: اللغة الإنجليزية
- **Dynamic Translation**: ترجمة ديناميكية
- **RTL Support**: دعم الاتجاه من اليمين لليسار
- **Comprehensive Translations**: ترجمات شاملة لجميع العناصر

### 8. OfflineService
نظام العمل بدون إنترنت:
- **Local Database**: قاعدة بيانات محلية (SQLite)
- **Offline Trip Storage**: تخزين الرحلات محلياً
- **Location Updates**: تحديثات الموقع محلياً
- **Document Storage**: تخزين المستندات محلياً
- **Sync Queue**: قائمة انتظار المزامنة
- **Auto Sync**: مزامنة تلقائية عند عودة الإنترنت
- **Conflict Resolution**: حل التعارضات
- **Data Cleanup**: تنظيف البيانات القديمة

### 9. SecurityService
نظام أمان متقدم:
- **Password Hashing**: تشفير كلمات المرور
- **Data Encryption**: تشفير البيانات
- **OTP System**: نظام المصادقة برقم واحد
- **Password Strength**: قوة كلمة المرور
- **Failed Attempts**: محاولات فاشلة
- **Account Lockout**: قفل الحساب
- **Token Refresh**: تجديد الـ Tokens
- **Audit Logging**: سجل التدقيق
- **Session Management**: إدارة الجلسات

### 10. ContractsService
نظام إدارة العقود:
- **Contract Management**: إدارة العقود
- **Contract Pricing**: تسعير العقود
- **Price Calculation**: حساب الأسعار
- **Contract Invoices**: فواتير العقود
- **Contract Renewal**: تجديد العقود
- **Contract Termination**: إنهاء العقود
- **Usage Statistics**: إحصائيات الاستخدام
- **Expiring Contracts**: العقود المنتهية
- **Contract Templates**: قوالب العقود
- **Contract PDF**: توليد PDF للعقود
- **Milestones**: معالم العقود

### 11. FleetOptimizationService
تحسين الأسطول:
- **Fleet Assignment**: تعيين الأسطول
- **Optimal Routes**: المسارات المثلى
- **Fuel Efficiency**: كفاءة الوقود
- **Maintenance Prediction**: التنبؤ بالصيانة
- **Fuel Consumption**: استهلاك الوقود
- **Driver Schedules**: جداول السائقين
- **Performance Metrics**: مقاييس الأداء
- **Replacement Suggestions**: اقتراحات الاستبدال
- **Cost Savings**: توفير التكاليف
- **Real-time Status**: حالة حية
- **Route Efficiency**: كفاءة المسار

### 12. IntegrationService
التكاملات الخارجية:
- **Payment Gateways**: بوابات الدفع (Stripe, PayPal, PayMob, HyperPay, Tap)
- **SMS Providers**: مزودي الرسائل (Twilio, Nexmo, MessageBird)
- **Email Service**: خدمة الإيميل
- **ERP Systems**: أنظمة ERP (SAP, Oracle, Microsoft Dynamics, NetSuite)
- **Webhooks**: Webhooks
- **Integration Logs**: سجلات التكامل
- **Configuration**: إعدادات التكامل

### 13. AdminPanelService
لوحة تحكم احترافية:
- **Live Dashboard**: لوحة تحكم حية
- **Drag & Drop Widgets**: ودجتس قابلة للسحب والإفلات
- **Real-time Monitoring**: مراقبة حية
- **Custom Widgets**: ودجتس مخصصة
- **Alerts System**: نظام التنبيهات
- **System Health**: صحة النظام
- **Performance Metrics**: مقاييس الأداء
- **API Usage**: استخدام الـ API
- **Storage Usage**: استخدام التخزين
- **Backup & Restore**: النسخ الاحتياطي والاستعادة
- **Service Management**: إدارة الخدمات
- **Activity Logs**: سجلات النشاط

## الشاشات (Screens)

### تطبيق السائق
1. **SplashScreen**: شاشة البداية مع أنيميشن فاخر
2. **LoginScreen**: شاشة تسجيل الدخول
3. **DriverDashboardScreen**: لوحة تحكم السائق
   - الرئيسية
   - الرحلات
   - الأرباح
   - الحساب
4. **ActiveTripScreen**: شاشة الرحلة النشطة
   - الخريطة الحية
   - تحديث الحالة
   - رفع الصور
   - إكمال الرحلة
5. **AvailableRequestsScreen**: الطلبات المتاحة
6. **TripHistoryScreen**: سجل الرحلات
7. **DocumentUploadScreen**: رفع المستندات
8. **ProfileScreen**: الملف الشخصي
9. **EarningsScreen**: الأرباح

## التثبيت (Installation)

### المتطلبات
- Flutter SDK >= 3.0.0
- Dart SDK >= 3.0.0
- Android Studio / Xcode
- Google Maps API Key

### خطوات التثبيت
```bash
# استنساخ المشروع
git clone <repository-url>
cd mobile_driver

# تثبيت الحزم
flutter pub get

# تشغيل التطبيق
flutter run
```

### إعداد Google Maps API
1. إنشاء مشروع في Google Cloud Console
2. تفعيل Maps SDK for Android و iOS
3. إنشاء API Key
4. إضافة الـ API Key في `android/app/src/main/AndroidManifest.xml`
5. إضافة الـ API Key في `ios/Runner/Info.plist`

## إعدادات API

قم بتحديث الـ API Base URL في جميع الخدمات:
```dart
final String _apiBaseUrl = 'http://your-api-url.com/api';
```

## هيكل المشروع

```
mobile_driver/
├── lib/
│   ├── main.dart
│   ├── screens/
│   │   ├── splash_screen.dart
│   │   ├── login_screen.dart
│   │   ├── driver_dashboard_screen.dart
│   │   ├── active_trip_screen.dart
│   │   ├── available_requests_screen.dart
│   │   ├── trip_history_screen.dart
│   │   ├── document_upload_screen.dart
│   │   ├── profile_screen.dart
│   │   └── earnings_screen.dart
│   └── services/
│       ├── auth_service.dart
│       ├── trip_service.dart
│       ├── location_service.dart
│       ├── upload_service.dart
│       ├── advanced_tracking_service.dart
│       ├── notification_service.dart
│       ├── role_based_access_service.dart
│       ├── analytics_service.dart
│       ├── crm_service.dart
│       ├── scheduling_service.dart
│       ├── localization_service.dart
│       ├── offline_service.dart
│       ├── security_service.dart
│       ├── contracts_service.dart
│       ├── fleet_optimization_service.dart
│       ├── integration_service.dart
│       └── admin_panel_service.dart
├── android/
├── ios/
├── pubspec.yaml
└── README.md
```

## الميزات الرئيسية

### للسائق
- تتبع GPS حي
- قبول الطلبات
- تحديث حالة الرحلة
- رفع صور إثبات التسليم
- متابعة الأرباح
- العمل بدون إنترنت

### للمشرف
- توزيع الرحلات
- مراقبة الأسطول
- تحليلات الأداء
- إدارة الصيانة
- تحسين المسارات

### للمحاسب
- إدارة الفواتير
- متابعة المدفوعات
- التقارير المالية
- إدارة العقود

### للعميل
- طلب شحنات
- تتبع الشحنات
- عرض الفواتير
- تاريخ الطلبات

## الأمان

- تشفير البيانات
- OTP Login
- Audit Trail
- Session Management
- Rate Limiting

## الأداء

- Offline Mode
- Caching
- Lazy Loading
- Optimistic UI Updates
- Background Sync

## الدعم

للدعم والمساعدة، يرجى التواصل مع فريق الدعم.

## الترخيص

جميع الحقوق محفوظة © 2026 إدهام
