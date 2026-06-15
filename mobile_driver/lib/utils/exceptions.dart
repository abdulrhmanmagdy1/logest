/// App Exceptions
/// Custom exceptions for better error handling

/// Base App Exception
class AppException implements Exception {
  final String message;
  final String? code;
  final Object? originalError;

  AppException(this.message, {this.code, this.originalError});

  @override
  String toString() => 'AppException: $message (Code: $code)';
}

/// Network Exception
class NetworkException extends AppException {
  NetworkException(String message, {String? code, Object? originalError})
      : super(message, code: code ?? 'NETWORK_ERROR', originalError: originalError);
}

/// No Internet Exception
class NoInternetException extends NetworkException {
  NoInternetException({Object? originalError})
      : super(
          'لا يوجد اتصال بالإنترنت',
          code: 'NO_INTERNET',
          originalError: originalError,
        );
}

/// Timeout Exception
class TimeoutException extends NetworkException {
  TimeoutException({Object? originalError})
      : super(
          'انتهت مهلة الطلب',
          code: 'TIMEOUT',
          originalError: originalError,
        );
}

/// Server Exception
class ServerException extends AppException {
  final int statusCode;

  ServerException(
    String message, {
    required this.statusCode,
    String? code,
    Object? originalError,
  }) : super(message, code: code ?? 'SERVER_ERROR', originalError: originalError);
}

/// Authentication Exception
class AuthenticationException extends AppException {
  AuthenticationException(String message, {String? code, Object? originalError})
      : super(message, code: code ?? 'AUTH_ERROR', originalError: originalError);
}

/// Invalid Credentials Exception
class InvalidCredentialsException extends AuthenticationException {
  InvalidCredentialsException({Object? originalError})
      : super(
          'البريد الإلكتروني أو كلمة المرور غير صحيحة',
          code: 'INVALID_CREDENTIALS',
          originalError: originalError,
        );
}

/// Token Expired Exception
class TokenExpiredException extends AuthenticationException {
  TokenExpiredException({Object? originalError})
      : super(
          'انتهت صلاحية الجلسة، يرجى تسجيل الدخول مرة أخرى',
          code: 'TOKEN_EXPIRED',
          originalError: originalError,
        );
}

/// Account Locked Exception
class AccountLockedException extends AuthenticationException {
  AccountLockedException({Object? originalError})
      : super(
          'الحساب مقفل، يرجى الاتصال بالدعم',
          code: 'ACCOUNT_LOCKED',
          originalError: originalError,
        );
}

/// Validation Exception
class ValidationException extends AppException {
  final Map<String, String>? fieldErrors;

  ValidationException(
    String message, {
    this.fieldErrors,
    String? code,
    Object? originalError,
  }) : super(message, code: code ?? 'VALIDATION_ERROR', originalError: originalError);

  String? getFieldError(String field) => fieldErrors?[field];
}

/// Not Found Exception
class NotFoundException extends AppException {
  NotFoundException(String resource, {Object? originalError})
      : super(
          '$resource غير موجود',
          code: 'NOT_FOUND',
          originalError: originalError,
        );
}

/// Permission Denied Exception
class PermissionDeniedException extends AppException {
  PermissionDeniedException({Object? originalError})
      : super(
          'ليس لديك صلاحية للوصول إلى هذا المورد',
          code: 'PERMISSION_DENIED',
          originalError: originalError,
        );
}

/// Rate Limit Exception
class RateLimitException extends AppException {
  final int? retryAfterSeconds;

  RateLimitException({this.retryAfterSeconds, Object? originalError})
      : super(
          'تم تجاوز الحد الأقصى للطلبات، يرجى المحاولة لاحقاً',
          code: 'RATE_LIMIT',
          originalError: originalError,
        );
}

/// Database Exception
class DatabaseException extends AppException {
  DatabaseException(String message, {String? code, Object? originalError})
      : super(message, code: code ?? 'DATABASE_ERROR', originalError: originalError);
}

/// Cache Exception
class CacheException extends AppException {
  CacheException(String message, {Object? originalError})
      : super(message, code: 'CACHE_ERROR', originalError: originalError);
}

/// Location Exception
class LocationException extends AppException {
  LocationException(String message, {String? code, Object? originalError})
      : super(message, code: code ?? 'LOCATION_ERROR', originalError: originalError);
}

/// Location Service Disabled Exception
class LocationServiceDisabledException extends LocationException {
  LocationServiceDisabledException({Object? originalError})
      : super(
          'خدمة الموقع معطلة، يرجى تفعيلها',
          code: 'LOCATION_DISABLED',
          originalError: originalError,
        );
}

/// Location Permission Denied Exception
class LocationPermissionDeniedException extends LocationException {
  LocationPermissionDeniedException({Object? originalError})
      : super(
          'تم رفض إذن الوصول إلى الموقع',
          code: 'LOCATION_PERMISSION_DENIED',
          originalError: originalError,
        );
}

/// File Exception
class FileException extends AppException {
  FileException(String message, {String? code, Object? originalError})
      : super(message, code: code ?? 'FILE_ERROR', originalError: originalError);
}

/// File Not Found Exception
class FileNotFoundException extends FileException {
  FileNotFoundException(String path, {Object? originalError})
      : super(
          'الملف غير موجود: $path',
          code: 'FILE_NOT_FOUND',
          originalError: originalError,
        );
}

/// File Too Large Exception
class FileTooLargeException extends FileException {
  final int maxSize;
  final int actualSize;

  FileTooLargeException({
    required this.maxSize,
    required this.actualSize,
    Object? originalError,
  }) : super(
          'حجم الملف كبير جداً (${actualSize}بايت)، الحد الأقصى: ${maxSize}بايت',
          code: 'FILE_TOO_LARGE',
          originalError: originalError,
        );
}

/// Unknown Exception
class UnknownException extends AppException {
  UnknownException({Object? originalError})
      : super(
          'حدث خطأ غير متوقع',
          code: 'UNKNOWN_ERROR',
          originalError: originalError,
        );
}

/// Exception Handler
class ExceptionHandler {
  /// Handle any exception and convert to AppException
  static AppException handle(dynamic error, {StackTrace? stackTrace}) {
    // Already an AppException
    if (error is AppException) {
      return error;
    }

    // Handle specific exception types
    if (error is FormatException) {
      return ValidationException(
        'بيانات غير صالحة',
        originalError: error,
      );
    }

    if (error is TypeError) {
      return ValidationException(
        'نوع بيانات غير متوقع',
        originalError: error,
      );
    }

    if (error is ArgumentError) {
      return ValidationException(
        error.message?.toString() ?? 'معامل غير صالح',
        originalError: error,
      );
    }

    if (error is StateError) {
      return AppException(
        error.message,
        code: 'STATE_ERROR',
        originalError: error,
      );
    }

    // Default to unknown
    return UnknownException(originalError: error);
  }

  /// Get user-friendly error message
  static String getErrorMessage(dynamic error) {
    if (error is AppException) {
      return error.message;
    }

    final appException = handle(error);
    return appException.message;
  }

  /// Check if error is network related
  static bool isNetworkError(dynamic error) {
    return error is NetworkException ||
           error is NoInternetException ||
           error is TimeoutException;
  }

  /// Check if error is auth related
  static bool isAuthError(dynamic error) {
    return error is AuthenticationException ||
           error is InvalidCredentialsException ||
           error is TokenExpiredException ||
           error is AccountLockedException;
  }

  /// Check if error is validation related
  static bool isValidationError(dynamic error) {
    return error is ValidationException;
  }
}
