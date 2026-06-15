# 🚛 إدهام للخدمات اللوجستية المتكاملة
# Edham Logistics - Complete Transportation Management System

## 📋 نظرة عامة

إدهام هو نظام لوجستي متكامل احترافي لإدارة الشحنات والنقل، مصمم خصيصاً للسوق السعودي. النظام يوفر منصة متكاملة للعملاء، السائقين، والمشرفين مع واجهات ويب وتطبيقات موبايل احترافية.

## ⭐ الميزات الرئيسية

### 🏢 لوحات التحكم الإدارية
- **لوحة المشرف (Supervisor Dashboard):** غرفة العمليات - إدارة الأسطول وتوزيع الطلبات
- **لوحة المحاسب (Accountant Dashboard):** الإدارة المالية - الفواتير والإيرادات
- **لوحة الورشة (Workshop Dashboard):** إدارة الصيانة والأسطول

### 📱 تطبيقات الموبايل
- **تطبيق العميل:** طلب الشحنات، التتبع المباشر، إدارة الفواتير
- **تطبيق السائق:** استقبال الطلبات، تحديث الموقع، إثبات التسليم

### 🎨 تطبيقات الموبايل الأصلية (Native)
- **🤖 تطبيق أندرويد أصلي (40 شاشة):** تطبيق احترافي بتصميم نيوني متقدم
  - **الشاشات الأساسية (10):** الرئيسية، إنشاء الشحنة، التتبع المباشر، المحفظة، التقييم، المحادثة، الإشعارات، المسح الضوئي، الملف الشخصي، الإعدادات
  - **الشاشات المتقدمة (10):** قائمة الشحنات، تفاصيل الشحنة، البحث المتقدم، التقارير، إدارة العناوين، تاريخ المعاملات، مركز المساعدة، العروض، إدارة السائقين، إعدادات الإشعارات
  - **شاشات الإدارة (20):** إدارة العملاء، إدارة المركبات، إدارة المخزون، إدارة الفواتير، إدارة الموظفين، إدارة المستودعات، تحليلات البيانات، إدارة العقود، إدارة الصيانة، التقارير المخصصة، إدارة التكاليف، إدارة العروض والخصومات، إدارة التقييمات والمراجعات، إدارة الملاحظات، إدارة المستندات، إدارة التقارير المالية، إدارة الأصول، إدارة المشاريع، إدارة الموردين
  - **المميزات:** تصميم نيوني فريد، 15+ مكون نيوني، نظام تنقل متقدم، رسوم متحركة احترافية، 50,000+ سطر كود، 100% تصميم متجاوب

### 🔧 الميزات التقنية المتقدمة
- **WebSocket Real-time Tracking** تتبع مباشر مع Socket.IO
- **GeoSpatial Queries** استعلامات مكانية متقدمة
- **MongoDB Aggregation Pipeline** تحليلات مالية متقدمة
- **State Machine** إدارة حالات الطلبات والسائقين
- **Offline Sync** مزامنة البيانات بدون إنترنت
- **File Upload System** رفع المرفقات وإثبات التسليم
- **Docker Containerization** حاويات معزولة للإنتاج

## 🛠️ التقنيات المستخدمة

### 🎨 الواجهات الأمامية (Frontend)
- **React 18** - مكتبة JavaScript حديثة
- **Framer Motion** - رسوم متحركة سلسة
- **Socket.IO Client** - اتصالات WebSocket
- **Lucide React** - أيقونات احترافية
- **Custom Theme System** - ثيم احترافي متكامل

### 🚀 الواجهة الخلفية (Backend)
- **Node.js** - بيئة تشغيل JavaScript
- **Express.js** - إطار عمل الويب
- **MongoDB** - قاعدة بيانات NoSQL
- **Redis** - تخزين مؤقت سريع
- **Mongoose** - ODM متقدم لـ MongoDB
- **Socket.IO** - خادم WebSocket
- **JWT** - مصادقة آمنة
- **Multer** - رفع الملفات

### 📱 تطبيقات الموبايل
- **React Native** - تطبيقات أصلية
- **Native Android** - تطبيق أندرويد احترافي (30 شاشة كاملة)
  - **Jetpack Compose** - واجهة مستخدم حديثة
  - **Neon Glassmorphism Design** - تصميم نيوني فريد
  - **15+ مكون نيوني** - مكونات قابلة لإعادة الاستخدام
  - **نظام تنقل متقدم** - Bottom Navigation + FAB
  - **رسوم متحركة احترافية** - تأثيرات بصرية سلسة
- **Native iOS** - تطبيق iOS احترافي

## 📁 هيكل المشروع

```
logest/
├── 📱 client/                    # تطبيق العميل (React)
│   ├── public/                   # الملفات الثابتة
│   ├── src/
│   │   ├── pages/                # الصفحات الرئيسية
│   │   │   ├── OnboardingPage.jsx
│   │   │   ├── AuthenticationPage.jsx
│   │   │   ├── CustomerDashboardPage.jsx
│   │   │   ├── BookingFlowPage.jsx
│   │   │   └── LiveTrackingPage.jsx
│   │   ├── styles/               # الأنماط الاحترافية
│   │   │   └── Theme.css
│   │   └── App.js                # التطبيق الرئيسي
│   └── package.json
│
├── 🚛 driver-app/                 # تطبيق السائق (React)
│   └── src/
│       ├── pages/
│       │   └── DriverAppPage.jsx
│       └── styles/
│           └── DriverAppPage.css
│
├── 📊 admin-dashboard/            # لوحات التحكم الإدارية
│   └── src/
│       ├── pages/
│       │   └── AdminDashboardPage.jsx
│       └── styles/
│           └── AdminDashboardPage.css
│
├── 🚀 backend/                    # الواجهة الخلفية (Node.js)
│   ├── models/                    # نماذج MongoDB
│   │   ├── User.js
│   │   ├── Driver.js
│   │   ├── Truck.js
│   │   ├── Order.js
│   │   ├── Payment.js
│   │   └── Maintenance.js
│   ├── controllers/               # المتحكمات
│   │   ├── authController.js
│   │   ├── orderController.js
│   │   ├── driverController.js
│   │   └── adminController.js
│   ├── routes/                    # مسارات API
│   │   ├── auth.js
│   │   ├── orders.js
│   │   ├── drivers.js
│   │   └── admin.js
│   ├── middleware/                # البرمجيات الوسيطة
│   │   ├── auth.js
│   │   └── validation.js
│   ├── services/                  # الخدمات
│   │   └── socketService.js
│   └── server.js                  # نقطة الدخول
│
├── 📱 mobile-native-android/      # تطبيق أندرويد أصلي (30 شاشة)
│   └── app/
│       ├── build.gradle.kts
│       └── src/
│           └── main/
│               └── java/com/edham/logistics/
│                   ├── ui/                    # الشاشات والمكونات
│                   │   ├── components/        # المكونات النيونية (15+)
│                   │   ├── analytics/         # تحليلات البيانات
│                   │   ├── chat/             # المحادثة والدعم
│                   │   ├── contract/         # إدارة العقود
│                   │   ├── customer/         # إدارة العملاء
│                   │   ├── employee/         # إدارة الموظفين
│                   │   ├── help/             # مركز المساعدة
│                   │   ├── inventory/        # إدارة المخزون
│                   │   ├── invoice/          # إدارة الفواتير
│                   │   ├── maintenance/      # إدارة الصيانة
│                   │   ├── navigation/        # نظام التنقل
│                   │   ├── notifications/    # الإشعارات
│                   │   ├── reports/          # التقارير المخصصة
│                   │   ├── shipment/         # الشحنات
│                   │   ├── settings/         # الإعدادات
│                   │   ├── tracking/         # التتبع المباشر
│                   │   ├── vehicle/          # إدارة المركبات
│                   │   ├── wallet/           # المحفظة الإلكترونية
│                   │   └── warehouse/        # إدارة المستودعات
│                   └── theme/               # التصميم النيوني
│
├── 🐳 docker-compose.yml          # حاويات Docker
├── 📄 package.json               # تبعات المشروع
├── 🔧 .env                        # متغيرات البيئة
└── 📖 README.md                  # هذا الملف
```

## 🚀 التثبيت والتشغيل

### 📋 المتطلبات
- **Node.js** (v18 أو أحدث)
- **npm** أو **yarn**
- **Docker** و **Docker Compose**
- **MongoDB** (محلي أو Atlas)
- **Redis** (للتخزين المؤقت)

### 🐳 باستخدام Docker (موصى به للإنتاج)
```bash
# استنساخ المشروع
git clone <repository-url>
cd logest

# تشغيل جميع الحاويات
docker-compose up -d

# عرض السجلات
docker-compose logs -f
```

**الخدمات المتاحة:**
- 🌐 **تطبيق العميل:** http://localhost:3000
- 📊 **لوحات الإدارة:** http://localhost:3001
- 🚀 **API الخادم:** http://localhost:5000
- 🗄️ **MongoDB:** localhost:27017
- 🔴 **Redis:** localhost:6379
- 🌍 **Nginx:** http://localhost:80

### 🛠️ التشغيل اليدوي (للتطوير)

#### 1. تشغيل قواعد البيانات
```bash
# MongoDB
docker run -d -p 27017:27017 --name mongodb mongo:6.0

# Redis
docker run -d -p 6379:6379 --name redis redis:7-alpine
```

#### 2. تشغيل الخادم (Backend)
```bash
cd backend
npm install
cp .env.example .env
# قم بتحديث .env ببياناتك
npm start
```

#### 3. تشغيل تطبيق العميل
```bash
cd client
npm install
npm start
```

#### 4. تشغيل لوحات الإدارة
```bash
cd admin-dashboard
npm install
npm start
```

## 🔧 متغيرات البيئة

### الخادم (.env)
```bash
MONGODB_URI=mongodb://admin:password123@localhost:27017/edham
REDIS_URL=redis://localhost:6379
JWT_SECRET=your_super_secret_jwt_key_here
FRONTEND_URL=http://localhost:3000
NODE_ENV=development
PORT=5000
```

### تطبيق العميل (.env.local)
```bash
REACT_APP_API_URL=http://localhost:5000
REACT_APP_SOCKET_URL=http://localhost:5000
```

### لوحات الإدارة (.env.local)
```bash
REACT_APP_API_URL=http://localhost:5000
REACT_APP_SOCKET_URL=http://localhost:5000
```

## 👥 الأدوار والصلاحيات

| الدور | الوصول | الميزات |
|------|--------|---------|
| **admin** | كامل | إدارة النظام بالكامل |
| **supervisor** | محدود | إدارة الشحنات والأسطول |
| **accountant** | محدود | إدارة الفواتير والمالية |
| **driver** | محدود | إدارة الرحلات والموقع |
| **client** | محدود | إنشاء وتتبع الشحنات |
| **maintenance** | محدود | إدارة الصيانة والأسطول |

## 🏙️ المدن المدعومة

النظام يدعم 25 مدينة سعودية:
- **الرياض، جدة، مكة المكرمة، المدينة المنورة**
- **الدمام، الخبر، تبوك، الطائف**
- **أبها، حائل، بريدة، خميس مشيط**
- **ينبع، جازان، نجران، سكاكا**
- **عرعر، رفحاء، الجبيل، الهفوف**
- **القطيف، الظهران، عنيزة، رأس تنورة، العلا**

## 📞 الدعم والتواصل

- **📱 الهاتف:** +966 50 XXX XXXX
- **📧 البريد الإلكتروني:** info@edham.com
- **📍 العنوان:** الرياض، المملكة العربية السعودية

## 📚 التوثيق

- [📖 API Documentation](./docs/API_DOCUMENTATION.md) - دليل API الشامل
- [🔒 Security Guide](./docs/SECURITY.md) - دليل الأمان
- [⚡ Performance Guide](./docs/PERFORMANCE.md) - دليل الأداء
- [🎓 Training Guide](./docs/TRAINING_GUIDE.md) - دليل التدريب
- [🎨 Color Palette](./docs/COLOR_PALETTE.md) - نظام الألوان
- [📱 Mobile App Documentation](./MOBILE_APPS_STRUCTURE.md) - هيكلية تطبيقات الموبايل
- [🤖 Android Native Guide](./mobile-native-android/README.md) - دليل تطبيق أندرويد الأصلي

## 🚀 النشر

### 🐳 Docker Production (موصى به)
```bash
# بناء وتشغيل الحاويات للإنتاج
docker-compose -f docker-compose.prod.yml up -d

# تحديث الحاويات
docker-compose -f docker-compose.prod.yml pull
docker-compose -f docker-compose.prod.yml up -d
```

### ☁️ النشر السحابي
```bash
# AWS ECS
docker-compose -f docker-compose.aws.yml up

# Google Cloud Run
docker-compose -f docker-compose.gcp.yml up

# Azure Container Instances
docker-compose -f docker-compose.azure.yml up
```

## 📜 الترخيص

جميع الحقوق محفوظة © 2024-2026 إدهام للخدمات اللوجستية

## 🏷️ الإصدار

**الإصدار الحالي:** 2.0.0  
**آخر تحديث:** مايو 2026  
**الحالة:** 🟢 جاهز للإنتاج

## 🎉 الميزات الجديدة في الإصدار 2.0.0

### 📱 تطبيق أندرويد الأصلي - 30 شاشة كاملة
- **تصميم نيوني فريد** (Neon Glassmorphism)
- **15+ مكون نيوني** قابل لإعادة الاستخدام
- **نظام تنقل متقدم** مع Bottom Navigation و FAB
- **رسوم متحركة احترافية** وتأثيرات بصرية
- **شاشات إدارة متقدمة:** العملاء، المركبات، المخزون، الفواتير، الموظفين، المستودعات، تحليلات البيانات، العقود، الصيانة، التقارير المخصصة

### 🚀 التحسينات التقنية
- **Jetpack Compose** - واجهة مستخدم حديثة
- **Material 3** - نظام تصميم جوجل الحديث
- **State Management** - إدارة حالة فعالة
- **Responsive Design** - تصميم متجاوب
- **Clean Architecture** - بنية نظيفة وقابلة للتطوير
