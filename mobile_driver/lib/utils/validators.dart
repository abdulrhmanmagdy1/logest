/// Validators
/// Collection of validation functions for form inputs
class Validators {
  Validators._(); // Private constructor

  /// Validate email
  static String? validateEmail(String? value) {
    if (value == null || value.isEmpty) {
      return 'البريد الإلكتروني مطلوب';
    }
    if (!value.isValidEmail) {
      return 'البريد الإلكتروني غير صالح';
    }
    return null;
  }

  /// Validate phone number (Saudi)
  static String? validatePhone(String? value) {
    if (value == null || value.isEmpty) {
      return 'رقم الهاتف مطلوب';
    }
    if (!value.isValidPhone) {
      return 'رقم الهاتف غير صالح (مثال: 0501234567)';
    }
    return null;
  }

  /// Validate password
  static String? validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      return 'كلمة المرور مطلوبة';
    }
    if (value.length < 8) {
      return 'كلمة المرور يجب أن تكون 8 أحرف على الأقل';
    }
    if (!value.contains(RegExp(r'[A-Z]'))) {
      return 'كلمة المرور يجب أن تحتوي على حرف كبير';
    }
    if (!value.contains(RegExp(r'[a-z]'))) {
      return 'كلمة المرور يجب أن تحتوي على حرف صغير';
    }
    if (!value.contains(RegExp(r'[0-9]'))) {
      return 'كلمة المرور يجب أن تحتوي على رقم';
    }
    return null;
  }

  /// Validate required field
  static String? validateRequired(String? value, {String? fieldName}) {
    if (value == null || value.trim().isEmpty) {
      return '${fieldName ?? 'هذا الحقل'} مطلوب';
    }
    return null;
  }

  /// Validate minimum length
  static String? validateMinLength(String? value, int minLength, {String? fieldName}) {
    if (value == null || value.isEmpty) {
      return '${fieldName ?? 'هذا الحقل'} مطلوب';
    }
    if (value.length < minLength) {
      return '${fieldName ?? 'هذا الحقل'} يجب أن يكون $minLength أحرف على الأقل';
    }
    return null;
  }

  /// Validate maximum length
  static String? validateMaxLength(String? value, int maxLength, {String? fieldName}) {
    if (value != null && value.length > maxLength) {
      return '${fieldName ?? 'هذا الحقل'} يجب أن لا يتجاوز $maxLength حرف';
    }
    return null;
  }

  /// Validate number
  static String? validateNumber(String? value, {String? fieldName}) {
    if (value == null || value.isEmpty) {
      return '${fieldName ?? 'هذا الحقل'} مطلوب';
    }
    if (double.tryParse(value) == null) {
      return '${fieldName ?? 'هذا الحقل'} يجب أن يكون رقماً';
    }
    return null;
  }

  /// Validate positive number
  static String? validatePositiveNumber(String? value, {String? fieldName}) {
    if (value == null || value.isEmpty) {
      return '${fieldName ?? 'هذا الحقل'} مطلوب';
    }
    final number = double.tryParse(value);
    if (number == null) {
      return '${fieldName ?? 'هذا الحقل'} يجب أن يكون رقماً';
    }
    if (number <= 0) {
      return '${fieldName ?? 'هذا الحقل'} يجب أن يكون أكبر من صفر';
    }
    return null;
  }

  /// Validate date range
  static String? validateDateRange(DateTime? startDate, DateTime? endDate) {
    if (startDate == null || endDate == null) {
      return 'التاريخ مطلوب';
    }
    if (endDate.isBefore(startDate)) {
      return 'تاريخ النهاية يجب أن يكون بعد تاريخ البداية';
    }
    return null;
  }

  /// Validate URL
  static String? validateUrl(String? value) {
    if (value == null || value.isEmpty) {
      return 'الرابط مطلوب';
    }
    final urlRegex = RegExp(
      r'^(http|https)://[a-zA-Z0-9\-\.]+\.[a-zA-Z]{2,}(/\S*)?$',
    );
    if (!urlRegex.hasMatch(value)) {
      return 'الرابط غير صالح';
    }
    return null;
  }

  /// Validate Saudi ID number
  static String? validateSaudiId(String? value) {
    if (value == null || value.isEmpty) {
      return 'رقم الهوية مطلوب';
    }
    if (value.length != 10) {
      return 'رقم الهوية يجب أن يكون 10 أرقام';
    }
    if (!RegExp(r'^[0-9]+$').hasMatch(value)) {
      return 'رقم الهوية يجب أن يحتوي على أرقام فقط';
    }
    return null;
  }

  /// Validate plate number
  static String? validatePlateNumber(String? value) {
    if (value == null || value.isEmpty) {
      return 'رقم اللوحة مطلوب';
    }
    // Saudi plate format: 1234 ABC or 12345 A
    final plateRegex = RegExp(r'^[0-9]{1,5}\s*[أ-يa-zA-Z]{1,3}$');
    if (!plateRegex.hasMatch(value)) {
      return 'صيغة رقم اللوحة غير صالحة';
    }
    return null;
  }
}
