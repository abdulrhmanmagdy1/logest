# نظام إدهام للخدمات اللوجستية - التوثيق الكامل للمشروع 🚛
**إعداد:** Aws Digital Team

هذا المستند هو المرجع الشامل والنهائي لكافة تفاصيل مشروع "إدهام"، وهو مصمم لمساعدة المبرمجين على فهم، تشغيل، وتطوير النظام بفعالية عالية.

---

## الجزء الأول: خريطة هيكلية المشروع (Project Map) 🗺️

يتبع المشروع نمط **Clean Architecture** مع **MVVM** لضمان قابلية التوسع (Scalability) وسهولة الصيانة.

### 1. المجلدات الرئيسية (Root Directories)
- **`app/`**: المجلد الأساسي لتطبيق الأندرويد (Kotlin).
- **`iosApp/`**: المجلد الأساسي لتطبيق الأيفون (Swift/SwiftUI).
- **`backend-api/`**: الكود المصدري للواجهة الخلفية (Backend) والخدمات.
- **`docs/`**: التقارير الفنية وخطط التطوير.
- **`disabled-files/`**: ملفات احتياطية أو معطلة تحتاج مراجعة.

### 2. تفاصيل تطبيق الأندرويد (`app/src/main/java/com/edham/logistics/`)

#### **A. حزمة الواجهات (`ui/`)**
- **`ui/home/accountant/`**: شاشات المحاسب (الفواتير، سندات القبض، كشوفات الحساب).
- **`ui/home/workshop/`**: شاشات الورشة (تتبع الصيانة، تتبع الزيت، إدارة قطع الغيار).
- **`ui/home/driver/`**: شاشات السائق (تسجيل الرحلات، إثبات التسليم، الاستبيانات).
- **`ui/viewmodel/`**: نماذج العرض التي تربط البيانات بالواجهات.

#### **B. حزمة البيانات (`data/`)**
- **`data/local/`**: إدارة قاعدة البيانات المحلية (Room).
- **`data/remote/`**: إدارة التواصل مع السيرفر (Retrofit APIs).
- **`data/repository/`**: المستودعات التي تجمع بين البيانات المحلية والبعيدة.

#### **C. وحدة الصيانة والورشة (`maintenance/`)**
- **`MaintenanceManager.kt`**: المسؤول عن حساب موعد تغيير الزيت، تتبع حالة الكفرات، وإصدار التنبيهات.
- **`MaintenanceSchedulingEngine.kt`**: محرك ذكاء اصطناعي لجدولة الصيانة بناءً على توفر الفنيين وساعات العمل.

#### **D. الخدمات الأساسية (`core/`)**
- **`core/network/`**: إعدادات الاتصال بالسيرفر ومراقبة جودة الشبكة.
- **`core/voice/`**: نظام المساعد الصوتي للسائق.
- **`core/utils/`**: دوال مساعدة جغرافية وزمنية.

---

### 3. تفاصيل تطبيق الأيفون (`iosApp/EdhamLogistics/`)

#### **A. هيكلية الكود (SwiftUI)**
- **`EdhamLogisticsApp.swift`**: الملف الرئيسي ونظام التوجيه (Routing).
- **`UI/`**: المجلد الذي يحتوي على كافة الشاشات مقسمة حسب الأدوار (Accountant, Driver, etc.).
- **`Core/`**: يحتوي على الـ Network Layer ونظام الـ Location.

#### **B. إدارة الحالة (State Management)**
- يعتمد التطبيق على **ObservableObjects** و **StateObject** لربط الـ ViewModels بالواجهات.
- يتم إدارة الجلسة عبر `SessionManager.shared`.

---

## الجزء الثاني: دليل رفع وتشغيل السيرفر (Server Deployment) 🚀

### 1. بناء السيرفر (Building)
استخدم Maven لبناء ملف الـ Jar:
```bash
cd backend-api
mvn clean package -DskipTests
```
الملف الناتج: `target/edham-logistics-api-1.0.0.jar`.

### 2. رفع الملفات (Uploading)
ارفع ملف الـ Jar إلى السيرفر (Linux/Ubuntu) في المسار `/home/username/app/`.

### 3. إدارة الملفات والمرفقات (Storage)
- المسار الافتراضي لتخزين الصور على السيرفر هو: `/var/www/edham/uploads/`.
- يجب التأكد من وجود صلاحيات الكتابة والقراءة (`chmod 755`).

### 4. التشغيل كخدمة (Service)
يتم التشغيل باستخدام Systemd لضمان عدم توقف السيرفر:
```ini
# /etc/systemd/system/edham-api.service
ExecStart=/usr/bin/java -jar /home/username/app/edham-logistics-api-1.0.0.jar --spring.profiles.active=prod
```

---

## الجزء الثالث: دليل رفع الملفات من خلال الـ API 📁

### 1. مسار الرفع (Endpoint)
`POST /api/v1/files/upload`
- يدعم `multipart/form-data`.
- يقبل بارامتر `type` لتصنيف المرفق (SHIPMENT_PROOF, OIL_CHANGE, etc.).

### 2. كيفية التحميل (Downloading)
`GET /api/v1/files/download/{fileName}`

---

## الجزء الرابع: ملاحظات هامة للمبرمج (Developer Notes) 💡

### 1. حالة الـ MainActivity
- الملف الحالي `MainActivity.kt` هو نسخة مؤقتة (Stub) لاستقرار البناء.
- النسخة الأصلية بكامل نظام الـ Navigation موجودة في `MainActivity.kt.bak`.

### 2. قاعدة البيانات المحلية
- يستخدم التطبيق قاعدة بيانات **EdhamDatabase** (النسخة 5 حالياً).
- تم توحيد كافة المعرفات (IDs) لتكون من نوع `String` لتتوافق مع السيرفر.

### 3. نظام التتبع
- السائق يرسل إحداثياته عبر `LocationTrackingService`.
- البيانات تظهر فوراً في لوحة تحكم المشرف (Supervisor Dashboard).

---
**نصيحة نهائية:** قبل البدء في أي تعديل، قم بعمل `Gradle Sync` وتأكد من أن `API_BASE_URL` في ملف `build.gradle.kts` يشير إلى السيرفر الصحيح.

© 2024 Aws Digital - تم التوثيق بنجاح.
