# إدهام للنقل المبرد - تطبيق موبايل

تطبيق موبايل Flutter لشركة إدهام للنقل المبرد.

## المتطلبات

- Flutter SDK (>=3.0.0)
- Android Studio / VS Code مع Flutter extension
- جهاز Android أو محاكي Android

## التثبيت

1. استنساخ المشروع:
```bash
cd mobile
```

2. قم بتثبيت المكتبات:
```bash
flutter pub get
```

## تشغيل التطبيق

### على محاكي Android:
```bash
flutter emulators --launch <emulator-id>
flutter run
```

### على جهاز Android:
```bash
flutter devices
flutter run -d <device-id>
```

## بناء APK للإصدار

### 1. إنشاء مفتاح التوقيع (Keystore)

```bash
keytool -genkey -v -keystore ~/upload-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```

### 2. تحديث ملف key.properties

قم بتحديث ملف `android/key.properties` ببيانات ملف التوقيع:

```properties
storePassword=<your-store-password>
keyPassword=<your-key-password>
keyAlias=upload
storeFile=/path/to/your/upload-keystore.jks
```

### 3. بناء APK للإصدار

```bash
flutter build apk --release
```

سيتم إنشاء الملف في:
`build/app/outputs/flutter-apk/app-release.apk`

### 4. بناء App Bundle (لـ Google Play)

```bash
flutter build appbundle --release
```

سيتم إنشاء الملف في:
`build/app/outputs/bundle/release/app-release.aab`

## التكوين

### عنوان الـ API

قم بتحديث عنوان الـ API في ملف `lib/services/auth_service.dart`:

```dart
final String baseUrl = 'http://your-server-ip:5000/api';
```

## الميزات

- تسجيل الدخول للمستخدمين
- لوحة تحكم خاصة بكل دور (عميل، مشرف، محاسب، سائق، موظف)
- إدارة الشحنات
- تتبع الشحنات
- إدارة الفواتير
- تتبع الموقع الجغرافي

## الأدوار

- **عميل**: طلب حمولة، تتبع شحنات
- **مشرف**: إدارة الشحنات، إدارة الشاحنات
- **محاسب**: إدارة الفواتير، سجل المدفوعات
- **سائق**: إدارة الرحلات، تحديث الموقع
- **موظف**: إدارة المركبات، تتبع الرحلات

## الألوان

- الأسود: #1A1A1A
- الرمادي الداكن: #2D2D2D
- الرمادي: #404040
- الأحمر: #DC2626
- الأبيض: #FFFFFF

## الدعم

للمساعدة والدعم، تواصل مع فريق التطوير.
