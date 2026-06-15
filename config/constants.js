/**
 * ============================================
 * ⚙️ Constants Configuration - نظام إدهام
 * Edham Logistics - Application Constants
 * ============================================
 */

module.exports = {
  // HTTP Status Codes
  HTTP_STATUS: {
    OK: 200,
    CREATED: 201,
    BAD_REQUEST: 400,
    UNAUTHORIZED: 401,
    FORBIDDEN: 403,
    NOT_FOUND: 404,
    CONFLICT: 409,
    UNPROCESSABLE: 422,
    INTERNAL_ERROR: 500
  },

  // User Roles
  ROLES: {
    ADMIN: 'admin',
    SUPERVISOR: 'supervisor',
    ACCOUNTANT: 'accountant',
    DRIVER: 'driver',
    CLIENT: 'client',
    EMPLOYEE: 'employee',
    MAINTENANCE: 'maintenance'
  },

  // Shipment Status
  SHIPMENT_STATUS: {
    PENDING: 'pending',
    ASSIGNED: 'assigned',
    IN_TRANSIT: 'in_transit',
    DELIVERED: 'delivered',
    CANCELLED: 'cancelled',
    FAILED: 'failed'
  },

  // Invoice Status
  INVOICE_STATUS: {
    PENDING: 'pending',
    SENT: 'sent',
    PAID: 'paid',
    PARTIAL: 'partial',
    OVERDUE: 'overdue',
    CANCELLED: 'cancelled'
  },

  // Truck Status
  TRUCK_STATUS: {
    ACTIVE: 'active',
    INACTIVE: 'inactive',
    MAINTENANCE: 'maintenance',
    OUT_OF_SERVICE: 'out_of_service',
    IN_TRANSIT: 'in_transit'
  },

  // Trip Status
  TRIP_STATUS: {
    PENDING: 'pending',
    STARTED: 'started',
    IN_PROGRESS: 'in_progress',
    PAUSED: 'paused',
    COMPLETED: 'completed',
    CANCELLED: 'cancelled'
  },

  // Maintenance Types
  MAINTENANCE_TYPE: {
    OIL_CHANGE: 'oil_change',
    TIRE_REPLACEMENT: 'tire_replacement',
    BRAKE_SERVICE: 'brake_service',
    ENGINE_INSPECTION: 'engine_inspection',
    GENERAL_CHECKUP: 'general_checkup',
    REPAIR: 'repair',
    COOLING_SYSTEM: 'cooling_system'
  },

  // Payment Methods
  PAYMENT_METHOD: {
    CASH: 'cash',
    BANK_TRANSFER: 'bank_transfer',
    CREDIT_CARD: 'credit_card',
    STRIPE: 'stripe',
    CHECK: 'check'
  },

  // Pagination
  PAGINATION: {
    DEFAULT_PAGE: 1,
    DEFAULT_LIMIT: 20,
    MAX_LIMIT: 100
  },

  // Saudi Cities
  SAUDI_CITIES: [
    'الرياض', 'جدة', 'مكة', 'المدينة',
    'الدمام', 'الخبر', 'تبوك', 'الطائف',
    'أبها', 'حائل', 'بريدة', 'خميس مشيط',
    'ينبع', 'جازان', 'نجران', 'سكاكا',
    'عرعر', 'رفحاء', 'الجبيل', 'الهفوف',
    'القطيف', 'الظهران', 'عنيزة', 'رأس تنورة', 'العلا'
  ],

  // API Response Messages (Arabic)
  MESSAGES: {
    SUCCESS: 'العملية تمت بنجاح',
    CREATED: 'تم الإنشاء بنجاح',
    UPDATED: 'تم التحديث بنجاح',
    DELETED: 'تم الحذف بنجاح',
    NOT_FOUND: 'العنصر غير موجود',
    UNAUTHORIZED: 'غير مصرح',
    ERROR: 'حدث خطأ',
    DATA_EXISTS: 'البيانات موجودة بالفعل',
    INVALID_CREDENTIALS: 'بيانات الدخول غير صحيحة',
    TOKEN_EXPIRED: 'انتهت صلاحية الجلسة',
    INVALID_TOKEN: 'الرمز غير صالح',
    ACCESS_DENIED: 'تم رفض الوصول',
    USER_NOT_FOUND: 'المستخدم غير موجود',
    ACCOUNT_DEACTIVATED: 'الحساب معطل',
    ACCOUNT_DELETED: 'الحساب محذوف',
    DRIVERS_ONLY: 'هذا الإجراء متاح للسائقين فقط',
    VALIDATION_ERROR: 'خطأ في التحقق من البيانات',
    TOO_MANY_REQUESTS: 'طلبات كثيرة جداً، يرجى المحاولة لاحقاً',
    ALREADY_EXISTS: 'موجود بالفعل',
    LOGGED_IN: 'تم تسجيل الدخول بنجاح',
    LOGGED_OUT: 'تم تسجيل الخروج بنجاح',
    REQUEST_TOO_LARGE: 'حجم الطلب كبير جداً. الحد الأقصى 10 ميجابايت',
    RESPONSE_TOO_LARGE: 'حجم الرد كبير جداً',
    FORBIDDEN: 'ليس لديك الصلاحية',
    ACCOUNT_LOCKED: 'الحساب مقفل مؤقتاً',
    INTERNAL_ERROR: 'خطأ داخلي بالخادم'
  }
};
