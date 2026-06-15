# 📱 Edham Logistics - UI Files Organization by Role

هذا المستند يرتب ملفات الواجهة حسب الدور لتسهيل التعديل

---

## 🎯 الأدوار الرئيسية

### 1. 🏢 المشرف (Supervisor)
**المسار:** `app/src/main/java/com/edham/logistics/ui/home/supervisor/`

| الملف | الوصف |
|------|-------|
| `SupervisorDashboardActivity.kt` | نشاط لوحة تحكم المشرف الرئيسي |
| `SupervisorDashboardFragment.kt` | شاشة لوحة التحكم |
| `SupervisorDriversFragment.kt` | إدارة السائقين |
| `SupervisorOrdersFragment.kt` | إدارة الطلبات/الشحنات |
| `SupervisorVehiclesFragment.kt` | إدارة المركبات |
| `SupervisorAnalyticsFragment.kt` | التحليلات والإحصائيات |
| `SupervisorSettingsFragment.kt` | إعدادات المشرف |

**Layouts المرتبطة:**
- `activity_supervisor_dashboard.xml`
- `fragment_supervisor_dashboard.xml`
- `fragment_supervisor_dashboard_enhanced.xml`
- `fragment_supervisor_drivers.xml`
- `fragment_supervisor_orders.xml`
- `fragment_supervisor_vehicles.xml`
- `fragment_supervisor_analytics.xml`
- `fragment_supervisor_settings.xml`
- `nav_header_supervisor.xml`

---

### 2. 💰 المحاسب (Accountant)
**المسار:** `app/src/main/java/com/edham/logistics/ui/home/accountant/`

| الملف | الوصف |
|------|-------|
| `AccountantHomeActivity.kt` | نشاط لوحة تحكم المحاسب الرئيسي |
| `ReceiptVoucherFragment.kt` | إدارة الإيصالات والسندات |

**Layouts المرتبطة:**
- `fragment_accountant_dashboard.xml`
- `fragment_accountant_dashboard_new.xml`
- `fragment_accountant_billing.xml`
- `fragment_receipt_voucher.xml`
- `fragment_invoices.xml`
- `fragment_payments.xml`

---

### 3. 🚛 السائق (Driver)
**المسار:** `app/src/main/java/com/edham/logistics/ui/home/driver/`

| الملف | الوصف |
|------|-------|
| `DriverHomeActivity.kt` | نشاط لوحة تحكم السائق الرئيسي |
| `DeliveryProofFragment.kt` | إثبات التسليم (صور/توقيع) |
| `DriverSurveyFragment.kt` | استبيان السائق |

**Layouts المرتبطة:**
- `fragment_driver_dashboard.xml`
- `fragment_driver_dashboard_new.xml`
- `fragment_driver_cold_chain.xml`
- `fragment_delivery_proof.xml`
- `fragment_driver_survey.xml`
- `fragment_active_trip.xml`
- `fragment_assigned_trip.xml`

---

### 4. 🛠️ الورشة (Workshop)
**المسار:** `app/src/main/java/com/edham/logistics/ui/home/`

| الملف | الوصف |
|------|-------|
| `WorkshopHomeActivity.kt` | نشاط لوحة تحكم الورشة |

**Layouts المرتبطة:**
- `fragment_workshop_dashboard.xml`
- `fragment_maintenance.xml`
- `fragment_maintenance_calendar.xml`
- `fragment_maintenance_analytics.xml`
- `fragment_repair_timeline.xml`
- `fragment_vehicle_maintenance_history.xml`

---

### 5. 📦 العميل (Customer)
**المسار:** `app/src/main/java/com/edham/logistics/ui/home/`

| الملف | الوصف |
|------|-------|
| `CustomerHomeActivity.kt` | نشاط لوحة تحكم العميل الرئيسي |

**Layouts المرتبطة:**
- `fragment_customer_dashboard.xml`
- `fragment_customer_dashboard_new.xml`
- `fragment_customer_dashboard_production.xml`
- `fragment_customer_dashboard_with_shortcuts.xml`
- `fragment_customer_tracking.xml`
- `fragment_customer_cold_chain.xml`
- `fragment_customer_support.xml`

---

### 6. 👨‍💼 الأدمن (Admin)
**المسار:** `app/src/main/java/com/edham/logistics/ui/admin/` (فارغ حالياً)

**Layouts المرتبطة:**
- `fragment_admin_dashboard.xml`
- `fragment_admin_dashboard_new.xml`
- `fragment_admin_backup.xml`
- `fragment_admin_cold_chain.xml`
- `fragment_user_management.xml`

---

## 🎨 المكونات المشتركة (Components)
**المسار:** `app/src/main/res/layout/component_*.xml`

| الملف | الوصف |
|------|-------|
| `component_button_primary.xml` | زر أساسي |
| `component_button_secondary.xml` | زر ثانوي |
| `component_card_default.xml` | كارت افتراضي |
| `component_chip.xml` | شيب/فلتر |
| `component_driver_info_card.xml` | كارت معلومات السائق |
| `component_floating_action_button.xml` | زر عائم |
| `component_icon_button.xml` | زر أيقونة |
| `component_input_field.xml` | حقل إدخال |
| `component_progress_indicator.xml` | مؤشر تقدم |
| `component_shipment_card.xml` | كارت الشحنة |
| `component_shipment_status_timeline.xml` | خط زمني لحالة الشحنة |
| `component_status_badge.xml` | شارة الحالة |

---

## 📋 القوائم (Lists/Items)
**المسار:** `app/src/main/res/layout/item_*.xml`

| الملف | الوصف |
|------|-------|
| `item_driver.xml` | عنصر قائمة السائقين |
| `item_vehicle.xml` | عنصر قائمة المركبات |
| `item_shipment.xml` | عنصر قائمة الشحنات |
| `item_invoice.xml` | عنصر قائمة الفواتير |
| `item_payment.xml` | عنصر قائمة المدفوعات |
| `item_notification.xml` | عنصر قائمة الإشعارات |
| `item_maintenance.xml` | عنصر قائمة الصيانة |
| `item_inventory.xml` | عنصر قائمة المخزون |

---

## 🚀 الشاشات المشتركة
**المسار:** `app/src/main/java/com/edham/logistics/ui/screens/`

| الملف | الوصف |
|------|-------|
| `SplashActivity.kt` | شاشة البداية |
| `LoginActivity.kt` | شاشة تسجيل الدخول |
| `MainActivity.kt` | النشاط الرئيسي |

**Layouts المرتبطة:**
- `activity_splash.xml`
- `activity_login.xml`
- `activity_login_v2.xml`
- `activity_login_new.xml`
- `activity_main.xml`
- `fragment_login.xml`
- `fragment_login_new.xml`
- `fragment_register.xml`
- `fragment_forgot_password.xml`
- `fragment_otp_verification.xml`

---

## 📊 الشاشات التحليلية
**Layouts:**
- `fragment_analytics_dashboard.xml`
- `fragment_analytics_charts.xml`
- `fragment_advanced_analytics.xml`
- `fragment_ai_dashboard.xml`
- `fragment_ai_dashboard_new.xml`
- `fragment_smart_reports.xml`

---

## 🗺️ الشاشات الخرائط والتتبع
**Layouts:**
- `fragment_live_tracking.xml`
- `fragment_tracking.xml`
- `fragment_tracking_new.xml`
- `fragment_shipment_tracking.xml`
- `fragment_shipment_timeline_tracking.xml`
- `fragment_fleet_tracking.xml`
- `fragment_live_fleet_map.xml`
- `activity_track_shipment.xml`

---

## 🔔 الإشعارات
**Layouts:**
- `fragment_notification.xml`
- `fragment_notifications.xml`
- `fragment_notification_center.xml`
- `fragment_notification_settings.xml`
- `fragment_notification_preferences.xml`
- `fragment_smart_alerts.xml`
- `fragment_service_alerts.xml`

---

## ⚙️ الإعدادات
**Layouts:**
- `fragment_settings.xml`
- `fragment_profile.xml`
- `fragment_security.xml`
- `fragment_logout.xml`

---

## 📦 الشحنات
**Layouts:**
- `fragment_shipments.xml`
- `fragment_create_shipment.xml`
- `fragment_create_shipment_new.xml`
- `fragment_shipment_detail.xml`
- `fragment_shipment_timeline.xml`
- `fragment_loads.xml`
- `fragment_dispatch_board.xml`

---

## 💾 النسخ الاحتياطي والأمان
**Layouts:**
- `fragment_admin_backup.xml`
- `fragment_security.xml`
- `fragment_activity_logs.xml`

---

## 🎯 كيف تستخدمين هذا المستند

1. **لتعديل شكل دور معين:** ابحثي عن الدور في الجدول، ثم عدّلي الملفات المذكورة
2. **لتعديل مكون مشترك:** استخدمي قسم "المكونات المشتركة"
3. **لإضافة ميزة جديدة:** حددي الدور المتأثر، أضيفي Fragment + Layout جديد

---

## 📝 ملاحظات مهمة

- كل `Fragment` مرتبط بـ `Activity` أو `Fragment` آخر
- الـ layouts في `res/layout/` مشتركة بين الأدوار
- الملفات التي تنتهي بـ `_new.xml` هي نسخ محدثة/تجريبية
- الملفات التي تنتهي بـ `_enhanced.xml` هي نسخ محسّنة

---

## 🔄 التحديثات

آخر تحديث: 18 مايو 2026
الحالة: جاهز للتعديل
