# دليل ربط تطبيق Android بالـ Backend
## Backend Connection Guide for Edham Logistics

---

## 📋 ملخص المشاكل والحلول

### المشاكل التي أشار إليها العميل:
1. **كاش كثير** - ملفات build/cache ضخمة في تطبيق Android
2. **وجهات فقط** - التطبيق يستخدم API mock وليس backend حقيقي
3. **مفيش باك اند** - التطبيق لا يتصل بالـ backend الموجود

### الحلول المطبقة:
✅ **تم تنظيف جميع ملفات الكاش** من تطبيق Android
✅ **تم تغيير API URL** من mock إلى backend حقيقي
✅ **التأكد من وجود backend كامل** في المشروع

---

## 🚀 الـ Backend موجود ومكتمل

### موقع الـ Backend:
```
d:\logest\backend\
```

### التقنيات المستخدمة:
- **Node.js** + **Express.js** - إطار عمل الويب
- **MongoDB** - قاعدة البيانات
- **Socket.IO** - للإشعارات والتتبع المباشر
- **JWT** - للمصادقة والأمان
- **Swagger** - لتوثيق API

### الميزات المتوفرة في الـ Backend:
- ✅ نظام إدارة المستخدمين (Users)
- ✅ نظام إدارة الشحنات (Shipments)
- ✅ نظام إدارة السائقين (Drivers)
- ✅ نظام إدارة الشاحنات (Trucks)
- ✅ نظام التتبع المباشر (Tracking)
- ✅ نظام الفواتير (Invoices)
- ✅ نظام الإشعارات (Notifications)
- ✅ نظام المحادثة (Chat)
- ✅ نظام التقارير (Reports)
- ✅ نظام الصيانة (Maintenance)
- ✅ نظام المحاسبة (Accountant)
- ✅ وغيرها الكثير من الميزات

---

## 🔧 كيفية تشغيل الـ Backend

### المتطلبات:
1. **Node.js** (الإصدار 18 أو أحدث)
2. **MongoDB** (محلي أو على السحابة)
3. **npm** (مدير الحزم)

### خطوات التشغيل:

#### 1. الذهاب إلى مجلد الـ Backend:
```bash
cd d:\logest\backend
```

#### 2. تثبيت المكتبات:
```bash
npm install
```

#### 3. إعداد ملف .env:
```bash
# نسخ ملف المثال
copy .env.example .env
```

ثم تعديل ملف `.env` بالقيم المناسبة:
```env
NODE_ENV=development
PORT=5000
MONGODB_URI=mongodb://localhost:27017/logest
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRE=30d
CLIENT_URL=http://localhost:3000
```

#### 4. تشغيل MongoDB:
```bash
# إذا كان MongoDB مثبت محلياً
# سيبدأ تلقائياً عند تشغيل الـ backend
```

#### 5. تشغيل الـ Backend:
```bash
# للتطوير (مع إعادة التشغيل التلقائي)
npm run dev

# أو للإنتاج
npm start
```

#### 6. التحقق من تشغيل الـ Backend:
افتح المتصفح على:
```
http://localhost:5000/api/v1/health
```

يجب أن تحصل على استجابة:
```json
{
  "status": "success",
  "message": "Edham Logistics API is running",
  "timestamp": "2024-05-18T...",
  "version": "1.0.0"
}
```

#### 7. عرض توثيق API:
افتح المتصفح على:
```
http://localhost:5000/api-docs
```

---

## 📱 ربط تطبيق Android بالـ Backend

### التغييرات المطبقة:
تم تغيير `API_BASE_URL` في ملف `mobile-native-android/app/build.gradle.kts`:

**قبل:**
```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://api.edham-mock.local/api/v1/\"")
```

**بعد:**
```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:5000/api/v1/\"")
```

### شرح العنوان الجديد:
- **10.0.2.2** - هذا هو عنوان localhost من داخل Android Emulator
- **5000** - هذا هو المنفذ الذي يعمل عليه الـ backend
- **/api/v1/** - هذا هو مسار API الأساسي

### للجهاز الحقيقي (Real Device):
إذا كنت تستخدم جهاز حقيقي بدلاً من المحاكي، استخدم عنوان IP الخاص بجهاز الكمبيوتر:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://YOUR_PC_IP:5000/api/v1/\"")
```

مثال:
```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://192.168.1.100:5000/api/v1/\"")
```

### للإنتاج (Production):
عند النشر للإنتاج، استخدم عنوان الـ backend الحقيقي:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://api.edham.com/api/v1/\"")
```

---

## 🧹 تنظيف الكاش

### تم تنظيف الملفات التالية:
- ✅ جميع ملفات `.log`
- ✅ جميع ملفات `.txt` (ما عدا الملفات المهمة)
- ✅ مجلد `build/`
- ✅ مجلد `.gradle/`
- ✅ مجلد `temp-gradle/`
- ✅ مجلد `app/build/`
- ✅ ملف `gradle-8.4-bin.zip`

### للحفاظ على نظافة المشروع:
استخدم هذا الأمر بشكل دوري:
```bash
cd d:\logest\mobile-native-android
.\gradlew clean
```

---

## 📊 هيكل الـ Backend

### المجلدات الرئيسية:
```
backend/
├── config/          # إعدادات قاعدة البيانات و Swagger
├── controllers/     # المتحكمات (Logic)
├── middleware/      # البرمجيات الوسيطة (Auth, Validation)
├── models/          # نماذج MongoDB
├── routes/          # مسارات API
├── services/        # الخدمات (Email, Socket, etc.)
├── utils/           # أدوات مساعدة
└── server.js        # نقطة الدخول الرئيسية
```

### مسارات API الرئيسية:
- `/api/v1/auth` - المصادقة وتسجيل الدخول
- `/api/v1/users` - إدارة المستخدمين
- `/api/v1/shipments` - إدارة الشحنات
- `/api/v1/drivers` - إدارة السائقين
- `/api/v1/trucks` - إدارة الشاحنات
- `/api/v1/tracking` - التتبع المباشر
- `/api/v1/invoices` - الفواتير
- `/api/v1/notifications` - الإشعارات
- `/api/v1/reports` - التقارير
- `/api/v1/chat` - المحادثة
- `/api/v1/accountant` - المحاسبة
- `/api/v1/workshop` - الصيانة
- `/api/v1/analytics` - التحليلات
- وغيرها الكثير...

---

## 🔐 الأمان

### الـ Backend يستخدم:
- ✅ **JWT Tokens** - للمصادقة
- ✅ **Bcrypt** - لتشفير كلمات المرور
- ✅ **Helmet** - لإضافة Security Headers
- ✅ **CORS** - للتحكم في الوصول
- ✅ **Rate Limiting** - للحماية من الهجمات
- ✅ **Input Validation** - للتحقق من البيانات

---

## 📝 ملاحظات هامة

### 1. تأكد من تشغيل MongoDB قبل تشغيل الـ Backend
```bash
# تحقق من تشغيل MongoDB
mongod
```

### 2. تأكد من المنفذ 5000 غير مستخدم
```bash
# على Windows
netstat -ano | findstr :5000
```

### 3. للإنتاج:
- استخدم MongoDB Atlas (سحابي) بدلاً من MongoDB المحلي
- غيّر `NODE_ENV` إلى `production`
- استخدم `JWT_SECRET` قوي وفريد
- فعّل HTTPS

### 4. للإتصال من تطبيق Android:
- تأكد أن الـ backend يعمل على `http://localhost:5000`
- تأكد من استخدام `10.0.2.2:5000` للمحاكي
- تأكد من إضافة إذن الإنترنت في `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 🆘 استكشاف الأخطاء

### المشكلة: التطبيق لا يتصل بالـ Backend
**الحلول:**
1. تأكد أن الـ backend يعمل: `http://localhost:5000/api/v1/health`
2. تأكد من عنوان API الصحيح في `build.gradle.kts`
3. تأكد من إذن الإنترنت في `AndroidManifest.xml`
4. تأكد أن الجدار الناري لا يمنع الاتصال

### المشكلة: MongoDB لا يعمل
**الحلول:**
1. تأكد من تثبيت MongoDB
2. تأكد من تشغيل خدمة MongoDB
3. تحقق من عنوان MongoDB في `.env`

### المشكلة: خطأ في الاتصال
**الحلول:**
1. تحقق من السجلات في `backend/logs/`
2. تحقق من متغيرات البيئة في `.env`
3. تأكد من تثبيت جميع المكتبات: `npm install`

---

## 📞 الدعم

إذا واجهت أي مشاكل:
1. راجع ملف `backend/README.md`
2. راجع توثيق API على `http://localhost:5000/api-docs`
3. تحقق من السجلات في `backend/logs/`

---

## ✅ الخلاصة

- ✅ **الـ Backend موجود ومكتمل** في مجلد `backend/`
- ✅ **تم تنظيف الكاش** من تطبيق Android
- ✅ **تم ربط التطبيق بالـ Backend** الحقيقي
- ✅ **الـ Backend يعمل على المنفذ 5000**
- ✅ **التطبيق الآن يتصل بالـ Backend** وليس mock

للبدء:
1. شغل MongoDB
2. شغل الـ Backend: `cd backend && npm start`
3. شغل تطبيق Android: `cd mobile-native-android && ./gradlew installDebug`

**النظام الآن جاهز للعمل بشكل كامل!** 🚀
