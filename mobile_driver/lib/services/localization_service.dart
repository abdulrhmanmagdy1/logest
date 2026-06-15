import 'package:flutter/material.dart';
import 'package:flutter_localization/flutter_localization.dart';
import 'package:shared_preferences/shared_preferences.dart';

class LocalizationService extends ChangeNotifier {
  static const LocalizationService _instance = LocalizationService._internal();

  factory LocalizationService() => _instance;

  LocalizationService._internal();

  Locale _currentLocale = const Locale('ar');
  Map<String, String> _translations = {};

  Locale get currentLocale => _currentLocale;

  // Supported locales
  static const List<Locale> supportedLocales = [
    Locale('ar'),
    Locale('en'),
  ];

  // Initialize localization
  Future<void> initialize() async {
    final prefs = await SharedPreferences.getInstance();
    final savedLocale = prefs.getString('locale');

    if (savedLocale != null) {
      _currentLocale = Locale(savedLocale);
    }

    await _loadTranslations();
    notifyListeners();
  }

  // Load translations
  Future<void> _loadTranslations() async {
    _translations = _getTranslations(_currentLocale.languageCode);
  }

  // Change locale
  Future<void> changeLocale(String languageCode) async {
    _currentLocale = Locale(languageCode);
    await _loadTranslations();

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('locale', languageCode);

    notifyListeners();
  }

  // Get translation
  String translate(String key) {
    return _translations[key] ?? key;
  }

  // Get translations map
  Map<String, String> _getTranslations(String languageCode) {
    switch (languageCode) {
      case 'ar':
        return _arabicTranslations;
      case 'en':
        return _englishTranslations;
      default:
        return _arabicTranslations;
    }
  }

  // Arabic translations
  static const Map<String, String> _arabicTranslations = {
    // Common
    'app_name': 'إدهام',
    'loading': 'جاري التحميل',
    'error': 'خطأ',
    'success': 'نجح',
    'cancel': 'إلغاء',
    'confirm': 'تأكيد',
    'save': 'حفظ',
    'delete': 'حذف',
    'edit': 'تعديل',
    'view': 'عرض',
    'search': 'بحث',
    'filter': 'تصفية',
    'sort': 'ترتيب',
    'refresh': 'تحديث',
    'close': 'إغلاق',
    'back': 'رجوع',
    'next': 'التالي',
    'previous': 'السابق',
    'submit': 'إرسال',
    'yes': 'نعم',
    'no': 'لا',
    'ok': 'موافق',
    'done': 'تم',
    'retry': 'إعادة المحاولة',

    // Auth
    'login': 'تسجيل الدخول',
    'logout': 'تسجيل الخروج',
    'register': 'تسجيل جديد',
    'email': 'البريد الإلكتروني',
    'password': 'كلمة المرور',
    'forgot_password': 'نسيت كلمة المرور',
    'reset_password': 'إعادة تعيين كلمة المرور',
    'login_success': 'تم تسجيل الدخول بنجاح',
    'login_failed': 'فشل تسجيل الدخول',
    'invalid_credentials': 'بيانات الدخول غير صحيحة',

    // Navigation
    'home': 'الرئيسية',
    'dashboard': 'لوحة التحكم',
    'trips': 'الرحلات',
    'vehicles': 'المركبات',
    'drivers': 'السائقين',
    'clients': 'العملاء',
    'invoices': 'الفواتير',
    'payments': 'المدفوعات',
    'maintenance': 'الصيانة',
    'analytics': 'التقارير',
    'settings': 'الإعدادات',
    'profile': 'الملف الشخصي',
    'notifications': 'الإشعارات',
    'earnings': 'الأرباح',
    'history': 'السجل',

    // Trip
    'new_trip': 'رحلة جديدة',
    'trip_details': 'تفاصيل الرحلة',
    'trip_status': 'حالة الرحلة',
    'pickup_location': 'نقطة الاستلام',
    'delivery_location': 'نقطة التسليم',
    'pickup_address': 'عنوان الاستلام',
    'delivery_address': 'عنوان التسليم',
    'customer_name': 'اسم العميل',
    'customer_phone': 'رقم العميل',
    'trip_price': 'سعر الرحلة',
    'trip_distance': 'مسافة الرحلة',
    'trip_weight': 'وزن الحمولة',
    'trip_date': 'تاريخ الرحلة',
    'trip_time': 'وقت الرحلة',
    'accept_trip': 'قبول الرحلة',
    'reject_trip': 'رفض الرحلة',
    'start_trip': 'بدء الرحلة',
    'complete_trip': 'إكمال الرحلة',
    'cancel_trip': 'إلغاء الرحلة',

    // Trip Status
    'status_pending': 'في الانتظار',
    'status_accepted': 'تم القبول',
    'status_assigned': 'تم التعيين',
    'status_pickup': 'في الاستلام',
    'status_in_transit': 'في الطريق',
    'status_arrived': 'تم الوصول',
    'status_delivered': 'تم التسليم',
    'status_completed': 'مكتمل',
    'status_cancelled': 'ملغي',

    // Driver
    'driver_name': 'اسم السائق',
    'driver_phone': 'رقم السائق',
    'driver_rating': 'تقييم السائق',
    'driver_earnings': 'أرباح السائق',
    'driver_trips': 'رحلات السائق',
    'driver_status': 'حالة السائق',
    'status_available': 'متاح',
    'status_busy': 'مشغول',
    'status_offline': 'غير متصل',

    // Vehicle
    'vehicle_number': 'رقم المركبة',
    'vehicle_type': 'نوع المركبة',
    'vehicle_capacity': 'سعة المركبة',
    'vehicle_status': 'حالة المركبة',
    'vehicle_maintenance': 'صيانة المركبة',
    'fuel_level': 'مستوى الوقود',
    'mileage': 'المسافة المقطوعة',

    // Invoice
    'invoice_number': 'رقم الفاتورة',
    'invoice_date': 'تاريخ الفاتورة',
    'invoice_amount': 'مبلغ الفاتورة',
    'invoice_status': 'حالة الفاتورة',
    'status_paid': 'مدفوعة',
    'status_unpaid': 'غير مدفوعة',
    'status_overdue': 'متأخرة',
    'pay_invoice': 'دفع الفاتورة',
    'download_invoice': 'تحميل الفاتورة',

    // Payment
    'payment_method': 'طريقة الدفع',
    'payment_date': 'تاريخ الدفع',
    'payment_amount': 'مبلغ الدفع',
    'payment_status': 'حالة الدفع',
    'payment_success': 'تم الدفع بنجاح',
    'payment_failed': 'فشل الدفع',
    'cash': 'نقدي',
    'card': 'بطاقة',
    'bank_transfer': 'تحويل بنكي',

    // Maintenance
    'maintenance_type': 'نوع الصيانة',
    'maintenance_date': 'تاريخ الصيانة',
    'maintenance_cost': 'تكلفة الصيانة',
    'maintenance_notes': 'ملاحظات الصيانة',
    'oil_change': 'تغيير الزيت',
    'tire_replacement': 'تغيير الإطارات',
    'engine_check': 'فحص المحرك',
    'brake_service': 'خدمة الفرامل',

    // Analytics
    'total_trips': 'إجمالي الرحلات',
    'total_revenue': 'إجمالي الإيرادات',
    'avg_delivery_time': 'متوسط وقت التوصيل',
    'on_time_rate': 'معدل التسليم في الوقت',
    'customer_satisfaction': 'رضا العملاء',
    'driver_performance': 'أداء السائقين',
    'fleet_utilization': 'استغلال الأسطول',
    'revenue_by_region': 'الإيرادات حسب المنطقة',

    // Settings
    'language': 'اللغة',
    'notifications': 'الإشعارات',
    'privacy': 'الخصوصية',
    'terms': 'الشروط والأحكام',
    'about': 'عن التطبيق',
    'version': 'الإصدار',
    'dark_mode': 'الوضع الداكن',
    'sound': 'الصوت',
    'vibration': 'الاهتزاز',

    // Messages
    'no_data': 'لا توجد بيانات',
    'no_trips': 'لا توجد رحلات',
    'no_notifications': 'لا توجد إشعارات',
    'connection_error': 'خطأ في الاتصال',
    'server_error': 'خطأ في الخادم',
    'try_again': 'حاول مرة أخرى',
    'something_went_wrong': 'حدث خطأ ما',
  };

  // English translations
  static const Map<String, String> _englishTranslations = {
    // Common
    'app_name': 'EDHAM',
    'loading': 'Loading',
    'error': 'Error',
    'success': 'Success',
    'cancel': 'Cancel',
    'confirm': 'Confirm',
    'save': 'Save',
    'delete': 'Delete',
    'edit': 'Edit',
    'view': 'View',
    'search': 'Search',
    'filter': 'Filter',
    'sort': 'Sort',
    'refresh': 'Refresh',
    'close': 'Close',
    'back': 'Back',
    'next': 'Next',
    'previous': 'Previous',
    'submit': 'Submit',
    'yes': 'Yes',
    'no': 'No',
    'ok': 'OK',
    'done': 'Done',
    'retry': 'Retry',

    // Auth
    'login': 'Login',
    'logout': 'Logout',
    'register': 'Register',
    'email': 'Email',
    'password': 'Password',
    'forgot_password': 'Forgot Password',
    'reset_password': 'Reset Password',
    'login_success': 'Login successful',
    'login_failed': 'Login failed',
    'invalid_credentials': 'Invalid credentials',

    // Navigation
    'home': 'Home',
    'dashboard': 'Dashboard',
    'trips': 'Trips',
    'vehicles': 'Vehicles',
    'drivers': 'Drivers',
    'clients': 'Clients',
    'invoices': 'Invoices',
    'payments': 'Payments',
    'maintenance': 'Maintenance',
    'analytics': 'Analytics',
    'settings': 'Settings',
    'profile': 'Profile',
    'notifications': 'Notifications',
    'earnings': 'Earnings',
    'history': 'History',

    // Trip
    'new_trip': 'New Trip',
    'trip_details': 'Trip Details',
    'trip_status': 'Trip Status',
    'pickup_location': 'Pickup Location',
    'delivery_location': 'Delivery Location',
    'pickup_address': 'Pickup Address',
    'delivery_address': 'Delivery Address',
    'customer_name': 'Customer Name',
    'customer_phone': 'Customer Phone',
    'trip_price': 'Trip Price',
    'trip_distance': 'Trip Distance',
    'trip_weight': 'Trip Weight',
    'trip_date': 'Trip Date',
    'trip_time': 'Trip Time',
    'accept_trip': 'Accept Trip',
    'reject_trip': 'Reject Trip',
    'start_trip': 'Start Trip',
    'complete_trip': 'Complete Trip',
    'cancel_trip': 'Cancel Trip',

    // Trip Status
    'status_pending': 'Pending',
    'status_accepted': 'Accepted',
    'status_assigned': 'Assigned',
    'status_pickup': 'Pickup',
    'status_in_transit': 'In Transit',
    'status_arrived': 'Arrived',
    'status_delivered': 'Delivered',
    'status_completed': 'Completed',
    'status_cancelled': 'Cancelled',

    // Driver
    'driver_name': 'Driver Name',
    'driver_phone': 'Driver Phone',
    'driver_rating': 'Driver Rating',
    'driver_earnings': 'Driver Earnings',
    'driver_trips': 'Driver Trips',
    'driver_status': 'Driver Status',
    'status_available': 'Available',
    'status_busy': 'Busy',
    'status_offline': 'Offline',

    // Vehicle
    'vehicle_number': 'Vehicle Number',
    'vehicle_type': 'Vehicle Type',
    'vehicle_capacity': 'Vehicle Capacity',
    'vehicle_status': 'Vehicle Status',
    'vehicle_maintenance': 'Vehicle Maintenance',
    'fuel_level': 'Fuel Level',
    'mileage': 'Mileage',

    // Invoice
    'invoice_number': 'Invoice Number',
    'invoice_date': 'Invoice Date',
    'invoice_amount': 'Invoice Amount',
    'invoice_status': 'Invoice Status',
    'status_paid': 'Paid',
    'status_unpaid': 'Unpaid',
    'status_overdue': 'Overdue',
    'pay_invoice': 'Pay Invoice',
    'download_invoice': 'Download Invoice',

    // Payment
    'payment_method': 'Payment Method',
    'payment_date': 'Payment Date',
    'payment_amount': 'Payment Amount',
    'payment_status': 'Payment Status',
    'payment_success': 'Payment Successful',
    'payment_failed': 'Payment Failed',
    'cash': 'Cash',
    'card': 'Card',
    'bank_transfer': 'Bank Transfer',

    // Maintenance
    'maintenance_type': 'Maintenance Type',
    'maintenance_date': 'Maintenance Date',
    'maintenance_cost': 'Maintenance Cost',
    'maintenance_notes': 'Maintenance Notes',
    'oil_change': 'Oil Change',
    'tire_replacement': 'Tire Replacement',
    'engine_check': 'Engine Check',
    'brake_service': 'Brake Service',

    // Analytics
    'total_trips': 'Total Trips',
    'total_revenue': 'Total Revenue',
    'avg_delivery_time': 'Avg Delivery Time',
    'on_time_rate': 'On-Time Rate',
    'customer_satisfaction': 'Customer Satisfaction',
    'driver_performance': 'Driver Performance',
    'fleet_utilization': 'Fleet Utilization',
    'revenue_by_region': 'Revenue by Region',

    // Settings
    'language': 'Language',
    'notifications': 'Notifications',
    'privacy': 'Privacy',
    'terms': 'Terms & Conditions',
    'about': 'About',
    'version': 'Version',
    'dark_mode': 'Dark Mode',
    'sound': 'Sound',
    'vibration': 'Vibration',

    // Messages
    'no_data': 'No Data',
    'no_trips': 'No Trips',
    'no_notifications': 'No Notifications',
    'connection_error': 'Connection Error',
    'server_error': 'Server Error',
    'try_again': 'Try Again',
    'something_went_wrong': 'Something Went Wrong',
  };
}
