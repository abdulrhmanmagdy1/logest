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

## 📂 ملاحظات هامة حول الملفات
- تم توفير ملفات توثيق شاملة في جذر المشروع (مثل `BUILD_GUIDE.md` و `DEPLOYMENT_GUIDE.md`).
- يوجد العديد من ملفات النسخ الاحتياطي (`.bak`) داخل المجلدات، يفضل تجاهلها أو نقلها لمجلد `archive`.
- الشاشات الحالية قيد التطوير تتركز في قسم "المحاسب" (Accountant) و "المشرف" (Supervisor).

## 🚀 كيفية البدء
1. قم بعمل `Gradle Sync`.
2. تأكد من إعداد ملف الـ `keystore` إذا كنت ستقوم ببناء نسخة (Release).
3. ابدأ من `MainActivity.kt` لفهم تدفق التطبيق الرئيسي.

---
تم تجهيز هذا الدليل لمساعدتك، بالتوفيق!
