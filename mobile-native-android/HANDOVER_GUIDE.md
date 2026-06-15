# دليل تسليم مشروع Edham Logistics (Handover Guide)

أهلاً بك في المشروع! هذا الدليل مصمم لمساعدتك على البدء في تطوير التطبيق وفهم هيكليته بسرعة.

## 🏗 هيكلية المشروع (Project Architecture)
يتبع المشروع نمط **MVVM** مع استخدام **Clean Architecture** في بعض الأجزاء:

- **`com.edham.logistics.ui`**: يحتوي على الأنشطة (Activities) والقطع (Fragments) ونماذج العرض (ViewModels).
- **`com.edham.logistics.data`**: يحتوي على المستودعات (Repositories) ومصادر البيانات (Room Database, Retrofit).
- **`com.edham.logistics.domain`**: يحتوي على منطق العمل (Business Logic) والنماذج (Domain Models).
- **`com.edham.logistics.di`**: إعدادات حقن التبعية (Dependency Injection) باستخدام Hilt.
- **`com.edham.logistics.auth`**: نظام تسجيل الدخول والصلاحيات.

## 🛠 المتطلبات التقنية
- **اللغة:** Kotlin (مع بعض أجزاء Java القديمة في `SupervisorDashboardFragment`).
- **قاعدة البيانات:** Room.
- **الشبكة:** Retrofit.
- **التصميم:** XML Layouts (مع بعض مكونات Compose الجديدة).

## ⚠️ تنبيهات هامة (Important Warnings)
- **`MainActivity.kt` حالياً هو نسخة مؤقتة (Stub):** تم استخدامه لضمان استقرار البناء (Build Stability). النسخة الأصلية موجودة في `MainActivity.kt.bak`. يجب على المبرمج القادم إعادة تفعيل نظام الـ Navigation الأصلي وربطه بـ Hilt.
- **تعدد اللغات:** المشروع يدعم العربية والإنجليزية بشكل أساسي في ملفات الـ XML والـ Menus.
- **الـ API:** السيرفر الحالي مضبوط على IP مؤقت، تأكد من تحديث `API_BASE_URL` في `build.gradle.kts` للبيئة الحقيقية.

## 📂 ملاحظات هامة حول الملفات
- **نظام الورشة:** تم تفعيل تنبيهات تغيير الزيت (Oil Change Alerts) في `WorkshopViewModel`.
- **نظام السائق:** تم ربط إنهاء المهمة في `DeliveryProofFragment` بفتح الاستبيان تلقائياً.
- **بوابة الدفع:** تم تجهيز `PaymentGatewayService` للتعامل مع العمليات المالية.
- يوجد العديد من ملفات النسخ الاحتياطي (`.bak`) داخل المجلدات، يفضل تجاهلها أو نقلها لمجلد `archive`.

## 🚀 كيفية البدء
1. قم بعمل `Gradle Sync`.
2. تأكد من إعداد ملف الـ `keystore` إذا كنت ستقوم ببناء نسخة (Release).
3. ابدأ من `MainActivity.kt` لفهم تدفق التطبيق الرئيسي.

---
تم تجهيز هذا الدليل لمساعدتك، بالتوفيق!
