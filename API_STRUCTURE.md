# نظام إدهام - هيكل API المنظم
# Edham Logistics - Organized API Structure

## 📁 هيكل المشروع الجديد | New Project Structure

```
d:/logest/
├── config/
│   └── constants.js          # الثوابت والأدوار
├── controllers/              # المتحكمات (15 ملف)
│   ├── index.js             # تصدير جميع المتحكمات
│   ├── authController.js
│   ├── userController.js
│   ├── shipmentController.js
│   ├── truckController.js
│   ├── tripController.js
│   ├── invoiceController.js
│   ├── paymentController.js
│   ├── maintenanceController.js
│   ├── oilScheduleController.js
│   ├── sparePartController.js
│   ├── locationController.js
│   ├── auditLogController.js
│   ├── analyticsController.js
│   ├── employeeVehicleController.js
│   └── surveyController.js
├── routes/                  # المسارات (15 ملف)
│   ├── auth.js
│   ├── users.js
│   ├── shipments.js
│   ├── trucks.js
│   ├── trips.js
│   ├── invoices.js
│   ├── payments.js
│   ├── maintenance.js
│   ├── oilSchedule.js
│   ├── spareParts.js
│   ├── locations.js
│   ├── auditLogs.js
│   ├── analytics.js
│   ├── employeeVehicles.js
│   └── surveys.js
├── utils/
│   ├── logger.js            # نظام التسجيل
│   └── helpers.js           # الدوال المساعدة
├── middleware/
│   └── auth.js              # التحقق من الصلاحيات
└── server.js                # الملف الرئيسي
```

## 🎯 المميزات الجديدة | New Features

### 1. Controller Pattern
- فصل المنطق عن المسارات
- إعادة استخدام الكود
- سهولة الاختبار

### 2. Professional Logger
```javascript
logger.info('Message');
logger.success('Message');
logger.warning('Message');
logger.error('Message', error);
```

### 3. Constants Management
```javascript
const { ROLES, HTTP_STATUS, MESSAGES } = require('../config/constants');
```

### 4. Pagination Support
```javascript
GET /api/shipments?page=1&limit=20
```

### 5. Soft Delete
- الحذف المنطقي بدل الفعلي
- إمكانية الاستعادة
- حفظ السجلات التاريخية

## 🚀 كيفية الاستخدام | Usage

### استيراد المتحكم | Import Controller
```javascript
const { ShipmentController } = require('../controllers');
// أو
const ShipmentController = require('../controllers/shipmentController');
```

### استخدام الـ Logger | Using Logger
```javascript
const logger = require('../utils/logger');

logger.success('Operation completed', { id: 123 });
logger.error('Error occurred', error);
```

### استخدام الثوابت | Using Constants
```javascript
const { ROLES, HTTP_STATUS, MESSAGES } = require('../config/constants');

if (user.role === ROLES.ADMIN) {
  return res.status(HTTP_STATUS.FORBIDDEN).json({
    message: MESSAGES.UNAUTHORIZED
  });
}
```

## 📊 قائمة الـ Controllers | Controllers List

| Controller | Methods | Description |
|------------|---------|-------------|
| `AuthController` | 6 | المصادقة والتسجيل |
| `UserController` | 5 | إدارة المستخدمين |
| `ShipmentController` | 7 | إدارة الشحنات |
| `TruckController` | 7 | إدارة الشاحنات |
| `TripController` | 11 | إدارة الرحلات |
| `InvoiceController` | 6 | إدارة الفواتير |
| `PaymentController` | 5 | المدفوعات |
| `MaintenanceController` | 8 | الصيانة |
| `OilScheduleController` | 8 | جدول الزيت |
| `SparePartController` | 7 | قطع الغيار |
| `LocationController` | 4 | المواقع |
| `AuditLogController` | 5 | السجلات |
| `AnalyticsController` | 5 | التحليلات |
| `EmployeeVehicleController` | 9 | مركبات الموظفين |
| `SurveyController` | 6 | الاستطلاعات |

## 🔒 الأدوار | Roles

```javascript
ROLES = {
  ADMIN: 'admin',
  SUPERVISOR: 'supervisor',
  ACCOUNTANT: 'accountant',
  DRIVER: 'driver',
  CLIENT: 'client',
  EMPLOYEE: 'employee'
}
```

## 📈 أكواد HTTP | HTTP Status Codes

```javascript
HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  CONFLICT: 409,
  INTERNAL_ERROR: 500
}
```

## 💬 الرسائل | Messages

```javascript
MESSAGES = {
  CREATED: 'تم الإنشاء بنجاح',
  UPDATED: 'تم التحديث بنجاح',
  DELETED: 'تم الحذف بنجاح',
  NOT_FOUND: 'العنصر غير موجود',
  UNAUTHORIZED: 'غير مصرح',
  ERROR: 'حدث خطأ'
}
```

## ✨ التحسينات المستقبلية | Future Improvements

1. ✅ اختبارات وحدة (Unit Tests)
2. ✅ توثيق API (Swagger/OpenAPI)
3. ✅ التخزين المؤقت (Redis Caching)
4. ✅ معالجة الأخطاء المتقدمة
5. ✅ مراقبة الأداء (Monitoring)

---
**نظام إدهام للنقل المبرد | Edham Logistics - Refrigerated Transport**
