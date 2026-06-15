# 🚛 نظام إدهام (Edham) - التوثيق التقني والتشغيلي الشامل

> **نظام لوجستي متكامل للنقل المبرد | Refrigerated Transport Management System**

---

## 🎨 أولاً: الهوية البصرية (Branding & UI Theme)

### لوحة الألوان الرسمية

```css
/* Primary Colors - ألوان أساسية */
--color-primary: #DC2626;        /* الأحمر - للتفاعلات والأزرار الأساسية */
--color-primary-dark: #B91C1C;   /* أحمر داكن - hover states */
--color-primary-light: #FCA5A5;  /* أحمر فاتح - accents */

/* Background Colors - ألوان الخلفيات */
--color-bg-dark: #1A1A1A;        /* أسود داكن - الخلفيات الإدارية */
--color-bg-gray: #2D2D2D;        /* رمادي داكن - البطاقات والأقسام */
--color-bg-light: #404040;       /* رمادي - العناصر الثانوية */

/* Text Colors - ألوان النصوص */
--color-text-primary: #FFFFFF;     /* أبيض - النصوص الرئيسية */
--color-text-secondary: #E5E5E5; /* أبيض مائل - النصوص الثانوية */
--color-text-muted: #A3A3A3;     /* رمادي فاتح - التلميحات والتواريخ */

/* Status Colors - ألوان الحالة */
--color-success: #22C55E;        /* أخضر - نجاح العملية */
--color-warning: #F59E0B;        /* أصفر - تحذير */
--color-error: #DC2626;          /* أحمر - خطأ (يتوافق مع الهوية) */
--color-info: #3B82F6;           /* أزرق - معلومات */
```

### التصميم المرئي

| العنصر | المواصفة |
|--------|----------|
| **Typography** | خط "Inter" أو "Cairo" للدعم العربي - مقاسات: 12px, 14px, 16px, 20px, 24px, 32px |
| **Spacing** | نظام 4px: 4, 8, 12, 16, 24, 32, 48, 64px |
| **Border Radius** | 4px (أزرار), 8px (بطاقات), 16px (أقسام), 50% (أيقونات دائرية) |
| **Shadows** | `0 4px 6px -1px rgba(0, 0, 0, 0.3)` للبطاقات، `0 10px 15px -3px rgba(220, 38, 38, 0.2)` للعناصر النشطة |
| **Animations** | Framer Motion - transitions 300ms ease-in-out |

---

## ⚙️ ثانياً: التفصيل الوظيفي العميق لكل وحدة (System Modules Deep Dive)

### 1️⃣ نظام التتبع الجغرافي والخرائط (Real-time Tracking System)

#### 🎯 الوظيفة البرمجية
يقوم النظام بمراقبة تحركات الشاحنات بشكل لحظي (Real-time) وعرضها على خريطة تفاعلية داخل لوحة التحكم.

#### 🔧 التفاصيل التقنية العميقة

**المكونات:**
```
┌─────────────────┐      WebSocket       ┌─────────────────┐
│  Flutter App    │ ═══════════════════► │  Node.js Server │
│  (GPS Service)  │                      │   (Socket.io)   │
└─────────────────┘                      └────────┬────────┘
                                                   │
                          ┌────────────────────────┘
                          │ Broadcast
                          ▼
                   ┌─────────────────┐
                   │  React Web App  │
                   │  (Google Maps)  │
                   └─────────────────┘
```

**كيف يعمل:**
1. **تطبيق السائق (Flutter)**:
   - يستخدم `geolocator` plugin للحصول على إحداثيات GPS
   - يرسل الإحداثيات كل 5-10 ثوانٍ عبر Socket.io
   - يعمل في الخلفية (Background Mode) حتى عند إغلاق الشاشة

2. **الخادم الوسيط (Node.js + Socket.io)**:
   - يستقبل الإحداثيات عبر `driverLocation` event
   - يخزنها في MongoDB (collection: `locations`)
   - يبثها فوراً (broadcast) لكل المشتركين في `shipmentId` نفسه

3. **لوحة التحكم (React + Google Maps API)**:
   - تستقبل التحديثات عبر Socket.io client
   - تتحرك علامة السيارة (Marker) بسلاسة باستخدام `framer-motion`
   - ترسم مسار الرحلة (Polyline) باللون الأحمر #DC2626

#### 📁 الملفات المرتبطة:
- `@d:\logest\driverSimulator.js` - محاكي حركة للاختبار
- `@d:\logest\routes\locations.js` - API endpoints للمواقع
- `@d:\logest\sockets\` - منطق WebSocket

#### 🧪 نظام المحاكاة (Testing)
```javascript
// driverSimulator.js - يحاكي حركة سائق حقيقي
const route = [
  { lat: 30.05, lng: 31.24 },  // نقطة البداية
  { lat: 30.06, lng: 31.25 },  // نقطة وسطى
  { lat: 30.07, lng: 31.26 }   // نقطة النهاية
];

// Interpolation بين النقاط لحركة ناعمة
const lat = current.lat + (next.lat - current.lat) * progress;
```

---

### 2️⃣ بوابة الدفع والإدارة المالية (Financial Suite)

#### 🎯 الوظيفة البرمجية
يدير الدورة المالية بالكامل: إنشاء فواتير، معالجة مدفوعات، تتبع مستحقات، وتوليد تقارير مالية.

#### 🔧 التفاصيل التقنية العميقة

**التكامل مع Stripe:**
```javascript
// payments.js - إنشاء Payment Intent
const paymentIntent = await stripe.paymentIntents.create({
  amount: amount * 100,  // Stripe يستخدم أصغر وحدة (قرش)
  currency: 'egp',
  automatic_payment_methods: { enabled: true },
  metadata: {
    shipmentId: shipment._id,
    customerId: customer._id
  }
});
```

**الميزات المالية:**

| الميزة | الوصف التقني | الملف |
|--------|--------------|-------|
| **إنشاء فواتير** | توليد PDF تلقائي مع QR Code للتحقق | `@d:\logest\routes\invoices.js` |
| **سندات القبض** | تسجيل الدفعات اليدوية والإلكترونية | `@d:\logest\routes\payments.js` |
| **المديونيات** | حساب الرصيد المتبقي تلقائياً | `Invoice.balanceDue` |
| **التقارير** | تصدير Excel/CSV للمحاسبين | `@d:\logest\routes\analytics.js` |

**نموذج البيانات (MongoDB):**
```javascript
// Invoice Schema
{
  invoiceNumber: "INV-2024-001",
  shipment: ObjectId,
  customer: ObjectId,
  items: [{
    description: String,
    quantity: Number,
    unitPrice: Number,
    total: Number
  }],
  subtotal: Number,
  tax: Number,
  total: Number,
  amountPaid: Number,
  balanceDue: Number,  // محسوب تلقائياً
  status: "pending|paid|partial|overdue",
  paymentMethod: "cash|card|bank_transfer",
  createdAt: Date,
  paidAt: Date
}
```

---

### 3️⃣ نظام الصيانة والأسطول (Fleet Maintenance)

#### 🎯 الوظيفة البرمجية
يضمن جاهزية الشاحنات عبر إدارة دورية لصيانة الزيت، الكفرات، والقطع المستبدلة.

#### 🔧 التفاصيل التقنية العميقة

**الوحدات الفرعية:**

```
┌─────────────────────────────────────────────────────────┐
│                 Fleet Maintenance System                 │
├─────────────────┬─────────────────┬─────────────────────┤
│  Oil Schedule   │  Tire Tracking  │  Spare Parts        │
│                 │                 │                     │
│  - Next change  │  - Wear level   │  - Inventory        │
│  - KM tracking  │  - Position     │  - Usage history    │
│  - Alerts       │  - Replacement  │  - Reorder alerts   │
└─────────────────┴─────────────────┴─────────────────────┘
```

**1. جدولة تغيير الزيت (@d:\logest\routes\oilSchedule.js):**
```javascript
// حساب الموعد القادم
const nextOilChange = {
  truck: truckId,
  lastChangeDate: new Date(),
  lastChangeKm: currentKm,
  nextChangeKm: currentKm + 5000,  // كل 5000 كم
  nextChangeDate: addDays(new Date(), 90),  // أو 3 أشهر
  status: 'active'
};
```

**2. إدارة الكفرات (@d:\logest\routes\trucks.js):**
```javascript
// تتبع كل كفر على حدة
const tires = [
  { position: 'front_left', brand: 'Michelin', installDate: Date, treadDepth: 8.5 },
  { position: 'front_right', brand: 'Michelin', installDate: Date, treadDepth: 8.2 },
  { position: 'rear_outer_left', brand: 'Bridgestone', installDate: Date, treadDepth: 7.8 }
];
```

**3. نظام التنبيهات:**
```javascript
// daily cron job
const checkMaintenance = async () => {
  const alerts = [];
  
  // التحقق من الزيت
  const oilDue = await OilSchedule.find({
    nextChangeDate: { $lte: addDays(new Date(), 7) }  // خلال أسبوع
  });
  
  // التحقق من الكفرات
  const tiresDue = await Truck.find({
    'tires.treadDepth': { $lt: 3.0 }  // أقل من 3مم
  });
  
  // إرسال إشعارات للمشرفين
  alerts.forEach(alert => {
    io.emit('maintenanceAlert', alert);
  });
};
```

---

### 4️⃣ إدارة المستخدمين والتدقيق (User & Audit Management)

#### 🎯 الوظيفة البرمجية
يتحكم في صلاحيات المستخدمين ويسجل كل حركة في النظام لضمان الشفافية.

#### 🔧 التفاصيل التقنية العميقة

**نظام الأدوار (RBAC):**
```javascript
// الأدوار المتاحة
const ROLES = {
  CLIENT: 'client',           // طلب شحنات + تتبع
  SUPERVISOR: 'supervisor',   // إدارة كاملة للشحنات
  ACCOUNTANT: 'accountant',   // الفواتير والمدفوعات
  DRIVER: 'driver',           // تحديث حالة الشحنة + GPS
  EMPLOYEE: 'employee',       // تتبع الأسطول
  MAINTENANCE: 'maintenance'  // إدارة الصيانة
};

// Middleware للتحقق من الصلاحيات
const requireRole = (roles) => {
  return (req, res, next) => {
    if (!roles.includes(req.user.role)) {
      return res.status(403).json({ 
        message: 'غير مصرح: ليس لديك الصلاحية الكافية' 
      });
    }
    next();
  };
};
```

**سجل التدقيق (@d:\logest\routes\auditLogs.js):**
```javascript
// تسجيل كل عملية
const auditLog = {
  user: userId,
  userRole: 'supervisor',
  action: 'shipment_status_updated',
  entityType: 'shipment',
  entityId: shipmentId,
  oldValue: { status: 'pending' },
  newValue: { status: 'in_transit' },
  ip: req.ip,
  userAgent: req.headers['user-agent'],
  timestamp: new Date()
};

// استخدام تلقائي عبر middleware
app.use((req, res, next) => {
  res.on('finish', () => {
    if (req.user) {
      createAuditLog({
        user: req.user._id,
        action: req.method + '_' + req.path,
        // ...
      });
    }
  });
  next();
});
```

**لوحات التحكم حسب الدور:**

| الدور | اللوحة | الميزات الرئيسية |
|-------|--------|------------------|
| العميل | `ClientDashboard` | طلب شحنة، تتبع شحناتي، الفواتير |
| المشرف | `SupervisorDashboard` | إدارة الشحنات، الشاحنات، المستخدمين |
| المحاسب | `AccountantDashboard` | الفواتير، المدفوعات، التقارير المالية |
| السائق | `DriverDashboard` | الرحلات الحالية، تحديث الموقع، الاستبيان |
| الموظف | `EmployeeDashboard` | تتبع الأسطول، تقارير الرحلات |
| الصيانة | `MaintenanceDashboard` | جدولة الصيانة، قطع الغيار |

---

### 5️⃣ توثيق الشحنات واستبيانات الأداء

#### 🎯 الوظيفة البرمجية
يضمن جودة الخدمة عبر توثيق الشحنات بالصور وجمع تقييمات الأداء.

#### 🔧 التفاصيل التقنية العميقة

**1. رفع المرفقات (@d:\logest\routes\shipments.js + Multer):**
```javascript
// Multer configuration
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, 'uploads/shipments/');
  },
  filename: (req, file, cb) => {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
    cb(null, `shipment-${req.params.id}-${uniqueSuffix}.jpg`);
  }
});

// أنواع المرفقات
const ATTACHMENT_TYPES = [
  'pickup_proof',      // صورة عند الاستلام
  'delivery_proof',      // صورة عند التسليم
  'damaged_goods',     // تقرير عن تلف
  'invoice_scan',      // فاتورة ممسوحة
  'id_verification',   // تحقق من الهوية
  'temperature_log'    // سجل درجة الحرارة (للنقل المبرد)
];
```

**2. نظام الاستبيان (@d:\logest\routes\surveys.js):**
```javascript
// نموذج استبيان السائق
const driverSurvey = {
  trip: tripId,
  driver: driverId,
  ratings: {
    vehicleCondition: 1-5,    // حالة المركبة
    routeEfficiency: 1-5,     // كفاءة المسار
    customerService: 1-5,     // التعامل مع العميل
    timeManagement: 1-5       // إدارة الوقت
  },
  issues: [{
    type: 'mechanical|traffic|customer|other',
    description: String,
    severity: 'low|medium|high'
  }],
  comments: String,
  submittedAt: Date
};

// حساب متوسط التقييم
const calculateDriverScore = (surveys) => {
  const avg = surveys.reduce((acc, s) => {
    return acc + (s.ratings.vehicleCondition + s.ratings.routeEfficiency + 
                  s.ratings.customerService + s.ratings.timeManagement) / 4;
  }, 0) / surveys.length;
  return avg.toFixed(1);  // مثال: 4.3/5
};
```

---

## 🛠️ ثالثاً: البنية التحتية البرمجية (The Backend Stack)

### معمارية النظام

```
┌─────────────────────────────────────────────────────────────────────┐
│                          CLIENT LAYER                                │
├──────────────────┬──────────────────┬───────────────────────────────┤
│   Flutter App    │   React Web App  │      Third Party APIs       │
│   (iOS/Android)  │   (Admin Panel)  │      (Stripe/Google Maps)   │
└────────┬─────────┴────────┬─────────┴───────────────┬───────────────┘
         │                  │                       │
         └──────────────────┼───────────────────────┘
                            │ HTTPS / WebSocket
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          API GATEWAY                                 │
│  Express.js + CORS + Rate Limiting + Helmet Security Headers         │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│  Auth Router │   │  REST API    │   │  WebSocket   │
│  /api/auth   │   │  /api/...    │   │  Socket.io   │
└──────┬───────┘   └──────┬───────┘   └──────┬───────┘
       │                  │                  │
       └──────────────────┼──────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                                   │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐        │
│  │  Shipments │ │   Trucks   │ │  Payments  │ │    Auth    │        │
│  │  Service   │ │  Service   │ │  Service   │ │  Service   │        │
│  └────────────┘ └────────────┘ └────────────┘ └────────────┘        │
└───────────────────────────┬───────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      DATA LAYER                                      │
│  MongoDB Atlas + Mongoose ODM + Soft Delete + Encryption           │
└─────────────────────────────────────────────────────────────────────┘
```

### تفاصيل المكونات

#### 1. الخادم (Node.js + Express)

**الملف الرئيسي:** `@d:\logest\server.js`

```javascript
// هيكل التطبيق
const app = express();

// Middleware Pipeline
app.use(cors({ origin: '*', credentials: true }));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Routes Registration
app.use('/api/auth', authRoutes);
app.use('/api/shipments', shipmentRoutes);
app.use('/api/trucks', truckRoutes);
app.use('/api/invoices', invoiceRoutes);
app.use('/api/maintenance', maintenanceRoutes);
app.use('/api/payments', paymentRoutes);
app.use('/api/audit-logs', auditLogRoutes);
app.use('/api/surveys', surveyRoutes);

// Socket.io للإشعارات الفورية
const io = new Server(server, { cors: { origin: '*' } });
require('./sockets')(io);
```

**الـ Routes المتاحة:**

| Endpoint | الوصف | الملف |
|----------|-------|-------|
| `/api/auth` | تسجيل الدخول والتسجيل | `@d:\logest\routes\auth.js` |
| `/api/shipments` | إدارة الشحنات | `@d:\logest\routes\shipments.js` |
| `/api/trucks` | إدارة الشاحنات | `@d:\logest\routes\trucks.js` |
| `/api/invoices` | الفواتير | `@d:\logest\routes\invoices.js` |
| `/api/maintenance` | الصيانة | `@d:\logest\routes\maintenance.js` |
| `/api/payments` | المدفوعات | `@d:\logest\routes\payments.js` |
| `/api/audit-logs` | سجل العمليات | `@d:\logest\routes\auditLogs.js` |
| `/api/surveys` | الاستبيانات | `@d:\logest\routes\surveys.js` |
| `/api/analytics` | التحليلات | `@d:\logest\routes\analytics.js` |
| `/api/locations` | المواقع | `@d:\logest\routes\locations.js` |
| `/api/trips` | الرحلات | `@d:\logest\routes\trips.js` |
| `/api/spare-parts` | قطع الغيار | `@d:\logest\routes\spareParts.js` |
| `/api/oil-schedule` | جدولة الزيت | `@d:\logest\routes\oilSchedule.js` |

#### 2. قاعدة البيانات (MongoDB Atlas)

**المخططات الرئيسية (Models):**

```
models/
├── User.js           # المستخدمين (بكلمات مرور مشفرة)
├── Shipment.js       # الشحنات مع تتبع الحالة
├── Truck.js          # الشاحنات + معلومات الكفرات
├── Invoice.js        # الفواتير المالية
├── Trip.js           # الرحلات + الموقع الحي
├── Maintenance.js    # سجل الصيانة
├── AuditLog.js       # سجل التدقيق
├── Survey.js         # استبيانات الأداء
├── Payment.js        # المدفوعات
├── OilSchedule.js    # جدولة تغيير الزيت
└── SparePart.js      # قطع الغيار
```

**ميزات الأمان:**
- تشفير كلمات المرور بـ bcrypt (10 rounds)
- JWT tokens للمصادقة
- Soft delete (حذف منطقي)
- Indexing على الحقول المستخدمة في البحث

#### 3. نظام الأمان المتكامل

```javascript
// @d:\logest\middleware\auth.js
const authenticate = async (req, res, next) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    if (!token) throw new Error('No token');
    
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    const user = await User.findById(decoded.id).select('-password');
    if (!user) throw new Error('User not found');
    
    req.user = user;
    next();
  } catch (error) {
    res.status(401).json({ message: 'Unauthorized' });
  }
};

// @d:\logest\middleware\validate.js
const validate = (schema) => (req, res, next) => {
  const { error } = schema.validate(req.body);
  if (error) {
    return res.status(400).json({ 
      message: 'Validation error', 
      details: error.details 
    });
  }
  next();
};
```

#### 4. تطبيق الويب (React 18)

**التقنيات المستخدمة:**

| التقنية | الإصدار | الاستخدام |
|---------|---------|-----------|
| React | 18.2.0 | واجهة المستخدم |
| React Router | 6.20.0 | التنقل بين الصفحات |
| TailwindCSS | 3.3.6 | التصميم (الألوان المخصصة) |
| Framer Motion | 12.38.0 | الحركات والانتقالات |
| Axios | 1.6.2 | طلبات API |
| Socket.io Client | 4.8.3 | الإشعارات الفورية |
| Recharts | 2.15.4 | الرسوم البيانية |
| Lucide React | 1.8.0 | الأيقونات |

**هيكل المشروع:**
```
client/
├── src/
│   ├── pages/           # الصفحات الكاملة
│   │   ├── ClientDashboard.js
│   │   ├── SupervisorDashboard.js
│   │   ├── AccountantDashboard.js
│   │   ├── DriverDashboard.js
│   │   ├── FleetGallery.js
│   │   └── InvoiceVerification.js
│   ├── components/      # مكونات قابلة لإعادة الاستخدام
│   ├── context/         # حالة التطبيق (Auth, Theme)
│   ├── services/        # API calls
│   └── utils/           # دوال مساعدة
```

#### 5. تطبيق الموبايل (Flutter)

**المكتبات المستخدمة:**

```yaml
# pubspec.yaml
dependencies:
  flutter:
    sdk: flutter
  http: ^1.1.0                    # طلبات API
  shared_preferences: ^2.2.2      # التخزين المحلي
  provider: ^6.1.1                # إدارة الحالة
  geolocator: ^10.1.0             # GPS
  image_picker: ^1.0.4            # الكاميرا
  permission_handler: ^11.0.1     # الصلاحيات
  flutter_svg: ^2.0.7             # SVG
  cached_network_image: ^3.3.0    # تحميل الصور
```

**الشاشات المتاحة:**
- `LoginScreen` - تسجيل الدخول
- `ShipmentsScreen` - إدارة الشحنات
- `MaintenanceScreen` - الصيانة
- `ReportsScreen` - التقارير
- `ProfileScreen` - الملف الشخصي
- `NotificationsScreen` - الإشعارات

---

## 📊 رابعاً: التحليلات والتقارير (Analytics)

### المقاييس الرئيسية (KPIs)

```javascript
// @d:\logest\routes\analytics.js
const KPIs = {
  // العمليات
  totalShipments: await Shipment.countDocuments(),
  activeShipments: await Shipment.countDocuments({ status: 'in_transit' }),
  completedShipments: await Shipment.countDocuments({ status: 'delivered' }),
  
  // المالية
  totalRevenue: await Invoice.aggregate([{ $group: { _id: null, total: { $sum: '$total' } } }]),
  pendingPayments: await Invoice.aggregate([{ $match: { status: 'pending' }, $group: { _id: null, total: { $sum: '$balanceDue' } } }]),
  
  // الأسطول
  activeTrucks: await Truck.countDocuments({ status: 'active' }),
  maintenanceDue: await Truck.countDocuments({ 'maintenanceStatus': 'due' }),
  
  // الأداء
  onTimeDeliveryRate: calculateOTDR(),  // نسبة التسليم في الوقت المحدد
  averageTripDuration: calculateAvgTrip(),
  driverPerformance: calculateDriverScores()
};
```

### أنواع التقارير

| التقرير | الصيغة | التكرار |
|---------|--------|---------|
| ملخص العمليات | PDF, Excel | يومي |
| التقرير المالي | PDF, CSV | شهري |
| أداء السائقين | Excel | أسبوعي |
| حالة الصيانة | PDF | أسبوعي |
| تحليل العملاء | Excel | شهري |

---

## 🔐 خامساً: الأمان والحماية (Security)

### إجراءات الأمان المطبقة

```javascript
// 1. Rate Limiting - الحماية من الهجمات
const rateLimit = require('express-rate-limit');
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000,  // 15 دقيقة
  max: 100,                  // 100 طلب لكل IP
  message: 'Too many requests'
});
app.use(limiter);

// 2. Helmet - Security Headers
const helmet = require('helmet');
app.use(helmet());

// 3. CORS - التحكم في الوصول
app.use(cors({
  origin: process.env.ALLOWED_ORIGINS?.split(',') || '*',
  credentials: true
}));

// 4. Input Sanitization
const mongoSanitize = require('express-mongo-sanitize');
app.use(mongoSanitize());

// 5. Password Hashing
const bcrypt = require('bcryptjs');
const salt = await bcrypt.genSalt(10);
const hashedPassword = await bcrypt.hash(password, salt);

// 6. JWT Security
const jwt = require('jsonwebtoken');
const token = jwt.sign(
  { id: user._id, role: user.role },
  process.env.JWT_SECRET,
  { expiresIn: '24h' }
);
```

### متغيرات البيئة (Environment Variables)

```bash
# @d:\logest\.env
NODE_ENV=production
PORT=5000
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/edham
JWT_SECRET=your-secret-key-here
JWT_EXPIRE=24h
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...
GOOGLE_MAPS_API_KEY=AIza...
```

---

## 🚀 الخلاصة: لماذا يعتبر هذا النظام احترافياً؟

### النقاط القوة التقنية:

1. **التكامل الكامل** - يدمج اللوجستيات، المالية، والصيانة في منصة واحدة
2. **Real-time** - تتبع فوري للشاحنات عبر WebSocket
3. **الأمان** - طبقات متعددة من الحماية (JWT, Rate Limiting, Helmet)
4. **قابلية التوسع** - MongoDB + Node.js يتحملان آلاف المستخدمين
5. **الهوية البصرية** - تصميم موحد بألوان الشركة (أحمر/أسود/أبيض)
6. **الشفافية** - سجل تدقيق كامل لكل عملية
7. **تجربة المستخدم** - Framer Motion + TailwindCSS لواجهة سلسة

### المستقبل (Roadmap):

- [ ] تكامل IoT - مستشعرات حرارة للشاحنات المبردة
- [ ] AI Route Optimization - تحسين المسارات بالذكاء الاصطناعي
- [ ] Predictive Maintenance - التنبؤ بالأعطال قبل حدوثها
- [ ] Mobile Notifications - إشعارات فورية عبر Firebase
- [ ] Multi-language - دعم العربية والإنجليزية بالكامل

---

## 📞 معلومات التواصل

**المطور:** أمنية أحمد  
**البريد:** admin@awss.tech  
**الموقع:** https://awss.tech  
**الشركة:** إدهام للنقل المبرد

---

**© 2024 إدهام للنقل المبرد - جميع الحقوق محفوظة**
