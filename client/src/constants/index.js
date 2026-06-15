/**
 * ============================================
 * 📋 Constants Index - نظام إدهام
 * Centralized constants export
 * ============================================
 */

// App Constants
export const APP_NAME = 'إدهام';
export const APP_NAME_EN = 'EDHAM';
export const APP_VERSION = '1.0.0';
export const COMPANY_NAME = 'شركة إدهام للنقل المبرد';

// API Constants
export const API_BASE_URL = process.env.REACT_APP_API_URL || 'https://api.edham.com/v1';
export const API_TIMEOUT = 30000;

// Pagination
export const DEFAULT_PAGE_SIZE = 20;
export const MAX_PAGE_SIZE = 100;

// File Upload
export const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
export const MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
export const ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
export const ALLOWED_FILE_TYPES = ['application/pdf', 'image/jpeg', 'image/png'];

// Status Constants
export const SHIPMENT_STATUS = {
  PENDING: 'pending',
  PROCESSING: 'processing',
  SHIPPED: 'shipped',
  IN_TRANSIT: 'in_transit',
  DELIVERED: 'delivered',
  CANCELLED: 'cancelled',
};

export const SHIPMENT_STATUS_LABELS = {
  pending: 'معلق',
  processing: 'قيد المعالجة',
  shipped: 'تم الشحن',
  in_transit: 'في الطريق',
  delivered: 'تم التسليم',
  cancelled: 'ملغي',
};

// User Roles
export const USER_ROLES = {
  CLIENT: 'client',
  DRIVER: 'driver',
  EMPLOYEE: 'employee',
  SUPERVISOR: 'supervisor',
  ADMIN: 'admin',
};

// Local Storage Keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'edham_auth_token',
  USER_DATA: 'edham_user_data',
  THEME: 'edham_theme',
  LANGUAGE: 'edham_language',
  NOTIFICATIONS: 'edham_notifications',
  RECENT_SHIPMENTS: 'edham_recent_shipments',
};

// Routes
export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  FORGOT_PASSWORD: '/forgot-password',
  DASHBOARD: '/dashboard',
  SHIPMENTS: '/shipments',
  TRACKING: '/tracking',
  INVOICES: '/invoices',
  PROFILE: '/profile',
  SETTINGS: '/settings',
  SUPPORT: '/support',
};

// Error Messages
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'خطأ في الاتصال بالشبكة',
  SERVER_ERROR: 'خطأ في الخادم',
  UNAUTHORIZED: 'غير مصرح',
  FORBIDDEN: 'غير مسموح',
  NOT_FOUND: 'غير موجود',
  VALIDATION_ERROR: 'خطأ في التحقق من البيانات',
  UNKNOWN_ERROR: 'خطأ غير معروف',
};

// Success Messages
export const SUCCESS_MESSAGES = {
  LOGIN_SUCCESS: 'تم تسجيل الدخول بنجاح',
  LOGOUT_SUCCESS: 'تم تسجيل الخروج بنجاح',
  SHIPMENT_CREATED: 'تم إنشاء الشحنة بنجاح',
  SHIPMENT_UPDATED: 'تم تحديث الشحنة بنجاح',
  SHIPMENT_CANCELLED: 'تم إلغاء الشحنة بنجاح',
  PROFILE_UPDATED: 'تم تحديث الملف الشخصي بنجاح',
  PASSWORD_CHANGED: 'تم تغيير كلمة المرور بنجاح',
};

// Support
export const SUPPORT_CONTACT = {
  PHONE: '920012345',
  EMAIL: 'support@edham.com',
  WHATSAPP: '966500123456',
};
