/// App Strings
/// Centralized string management for easy localization
class AppStrings {
  AppStrings._(); // Private constructor

  // App Info
  static const String appName = 'إدهام';
  static const String appSubtitle = 'تطبيق السائق';
  static const String appVersion = '1.0.0';

  // Auth
  static const String login = 'تسجيل الدخول';
  static const String logout = 'تسجيل الخروج';
  static const String email = 'البريد الإلكتروني';
  static const String password = 'كلمة المرور';
  static const String forgotPassword = 'نسيت كلمة المرور؟';
  static const String rememberMe = 'تذكرني';
  static const String dontHaveAccount = 'ليس لديك حساب؟';
  static const String alreadyHaveAccount = 'لديك حساب بالفعل؟';
  static const String register = 'إنشاء حساب';

  // Navigation
  static const String home = 'الرئيسية';
  static const String trips = 'الرحلات';
  static const String earnings = 'الأرباح';
  static const String profile = 'الملف الشخصي';
  static const String settings = 'الإعدادات';
  static const String notifications = 'الإشعارات';

  // Trip Status
  static const String tripPending = 'في الانتظار';
  static const String tripAccepted = 'تم القبول';
  static const String tripPickup = 'في الاستلام';
  static const String tripInTransit = 'في الطريق';
  static const String tripArrived = 'تم الوصول';
  static const String tripDelivered = 'تم التسليم';
  static const String tripCompleted = 'مكتمل';
  static const String tripCancelled = 'ملغي';

  // Actions
  static const String accept = 'قبول';
  static const String reject = 'رفض';
  static const String cancel = 'إلغاء';
  static const String confirm = 'تأكيد';
  static const String save = 'حفظ';
  static const String edit = 'تعديل';
  static const String delete = 'حذف';
  static const String update = 'تحديث';
  static const String submit = 'إرسال';
  static const String close = 'إغلاق';
  static const String back = 'رجوع';
  static const String next = 'التالي';
  static const String skip = 'تخطي';
  static const String done = 'تم';
  static const String loading = 'جاري التحميل...';
  static const String pleaseWait = 'يرجى الانتظار...';

  // Messages
  static const String success = 'نجاح';
  static const String error = 'خطأ';
  static const String warning = 'تحذير';
  static const String info = 'معلومة';
  static const String noInternet = 'لا يوجد اتصال بالإنترنت';
  static const String tryAgain = 'حاول مرة أخرى';
  static const String somethingWentWrong = 'حدث خطأ ما';

  // Validation
  static const String requiredField = 'هذا الحقل مطلوب';
  static const String invalidEmail = 'البريد الإلكتروني غير صالح';
  static const String invalidPhone = 'رقم الهاتف غير صالح';
  static const String passwordTooShort = 'كلمة المرور قصيرة جداً';
  static const String passwordsDoNotMatch = 'كلمات المرور غير متطابقة';

  // SOS & Emergency
  static const String sos = 'SOS';
  static const String emergency = 'طوارئ';
  static const String callEmergency = 'اتصال طوارئ';
  static const String sendSOS = 'إرسال إشارة استغاثة';

  // Maintenance
  static const String maintenance = 'صيانة';
  static const String oilChange = 'تغيير الزيت';
  static const String parts = 'قطع الغيار';
  static const String breakdown = 'أعطال';
}
