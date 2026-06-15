// ============================================
// 🔐 Biometric Authentication Service
// Enterprise Biometric Security System
// ============================================

import 'dart:async';
import 'package:flutter/services.dart';
import 'package:local_auth/local_auth.dart';
import 'package:local_auth_android/local_auth_android.dart';
import 'package:local_auth_darwin/local_auth_darwin.dart';

class BiometricAuthService {
  static final BiometricAuthService _instance = BiometricAuthService._internal();
  factory BiometricAuthService() => _instance;
  BiometricAuthService._internal();

  final LocalAuthentication _localAuth = LocalAuthentication();
  
  // Authentication state
  bool _isAvailable = false;
  bool _isDeviceSupported = false;
  List<BiometricType> _availableBiometrics = [];
  
  // Getters
  bool get isAvailable => _isAvailable;
  bool get isDeviceSupported => _isDeviceSupported;
  List<BiometricType> get availableBiometrics => _availableBiometrics;

  /// Initialize biometric service and check availability
  Future<void> initialize() async {
    try {
      // Check if device supports biometric authentication
      _isDeviceSupported = await _localAuth.isDeviceSupported();
      
      if (_isDeviceSupported) {
        // Check which biometric types are available
        _availableBiometrics = await _localAuth.getAvailableBiometrics();
        _isAvailable = _availableBiometrics.isNotEmpty;
      } else {
        _isAvailable = false;
        _availableBiometrics = [];
      }
    } catch (e) {
      _isAvailable = false;
      _isDeviceSupported = false;
      _availableBiometrics = [];
    }
  }

  /// Check if biometric authentication is available
  bool isBiometricAvailable() {
    return _isAvailable && _availableBiometrics.isNotEmpty;
  }

  /// Check if fingerprint is available
  bool isFingerprintAvailable() {
    return _availableBiometrics.contains(BiometricType.fingerprint);
  }

  /// Check if face ID is available
  bool isFaceIdAvailable() {
    return _availableBiometrics.contains(BiometricType.face);
  }

  /// Get available biometric types as strings
  List<String> getAvailableBiometricTypes() {
    return _availableBiometrics.map((type) {
      switch (type) {
        case BiometricType.fingerprint:
          return 'بصمة الإصبع';
        case BiometricType.face:
          return 'التعرف على الوجه';
        case BiometricType.iris:
          return 'التعرف على قزحية العين';
        case BiometricType.strong:
          return 'مصادقة قوية';
        case BiometricType.weak:
          return 'مصادقة ضعيفة';
        default:
          return 'غير معروف';
      }
    }).toList();
  }

  /// Authenticate with biometrics
  Future<BiometricResult> authenticate({
    String localizedReason = 'استخدام المصادقة البيومترية للدخول الآمن',
    bool useErrorDialogs = true,
    bool stickyAuth = true,
    bool biometricOnly = true,
  }) async {
    try {
      if (!isBiometricAvailable()) {
        return BiometricResult(
          success: false,
          error: 'المصادقة البيومترية غير متاحة على هذا الجهاز',
          type: BiometricResultType.unavailable,
        );
      }

      bool authenticated = await _localAuth.authenticate(
        localizedReason: localizedReason,
        options: AuthenticationOptions(
          biometricOnly: biometricOnly,
          useErrorDialogs: useErrorDialogs,
          stickyAuth: stickyAuth,
          // Android specific options
          androidAuthStrings: AndroidAuthStrings(
            signInTitle: 'تسجيل الدخول البيومتري',
            biometricHint: 'استخدم بصمة الإصبع',
            biometricNotRecognized: 'بصمة الإصبع غير معروفة',
            biometricRequiredTitle: 'المصادقة البيومترية مطلوبة',
            biometricSuccess: 'تم التحقق بنجاح',
            deviceCredentialsRequiredTitle: 'بيانات اعتماد الجهاز مطلوبة',
            deviceCredentialsSetupDescription: 'إعداد بيانات اعتماد الجهاز',
            goToButtonLabel: 'الانتقال إلى الإعدادات',
            goToSettingsDescription: 'إعداد المصادقة البيومترية في إعدادات الجهاز',
            signInTitle: 'تسجيل الدخول',
            cancelButtonLabel: 'إلغاء',
          ),
          // iOS specific options
          authStrings: AuthStrings(
            lockOut: 'تم تجاوز عدد المحاولات المتاحة',
            goToSettings: 'الانتقال إلى الإعدادات',
            biometricHint: 'استخدم المصادقة البيومترية',
            biometricNotRecognized: 'المصادقة البيومترية غير معروفة',
            biometricRequiredTitle: 'المصادقة البيومترية مطلوبة',
            biometricSuccess: 'تم التحقق بنجاح',
            deviceCredentialsRequiredTitle: 'بيانات اعتماد الجهاز مطلوبة',
            deviceCredentialsSetupDescription: 'إعداد بيانات اعتماد الجهاز',
            localizedFallbackTitle: 'استخدام بديل',
            localizedReason: localizedReason,
            other: 'آخر',
            passcodeNotSet: 'لم يتم تعيين رمز مرور',
            pinNotSet: 'لم يتم تعيين PIN',
            signInTitle: 'تسجيل الدخول',
          ),
        ),
      );

      if (authenticated) {
        return BiometricResult(
          success: true,
          type: BiometricResultType.success,
        );
      } else {
        return BiometricResult(
          success: false,
          error: 'فشلت المصادقة البيومترية',
          type: BiometricResultType.failed,
        );
      }
    } on PlatformException catch (e) {
      String errorMessage = 'حدث خطأ في المصادقة البيومترية';
      BiometricResultType resultType = BiometricResultType.error;

      switch (e.code) {
        case 'NotAvailable':
          errorMessage = 'المصادقة البيومترية غير متاحة';
          resultType = BiometricResultType.unavailable;
          break;
        case 'NotEnrolled':
          errorMessage = 'لم يتم تسجيل أي بيانات بيومترية';
          resultType = BiometricResultType.notEnrolled;
          break;
        case 'LockedOut':
        case 'PermanentlyLockedOut':
          errorMessage = 'تم تجاوز عدد المحاولات المتاحة';
          resultType = BiometricResultType.lockedOut;
          break;
        case 'OtherOperatingSystem':
          errorMessage = 'نظام التشغيل غير مدعوم';
          resultType = BiometricResultType.unsupported;
          break;
        case 'PasscodeNotSet':
          errorMessage = 'يجب تعيين رمز مرور للجهاز';
          resultType = BiometricResultType.passcodeNotSet;
          break;
        default:
          errorMessage = 'خطأ غير متوقع: ${e.message}';
          resultType = BiometricResultType.error;
      }

      return BiometricResult(
        success: false,
        error: errorMessage,
        type: resultType,
      );
    } catch (e) {
      return BiometricResult(
        success: false,
        error: 'خطأ غير متوقع: ${e.toString()}',
        type: BiometricResultType.error,
      );
    }
  }

  /// Check if biometric authentication is enrolled
  Future<bool> isBiometricEnrolled() async {
    try {
      if (!isBiometricAvailable()) return false;
      
      // Try a quick authentication check to see if biometrics are enrolled
      bool result = await _localAuth.authenticate(
        localizedReason: 'التحقق من تسجيل البيانات البيومترية',
        options: const AuthenticationOptions(
          biometricOnly: true,
          useErrorDialogs: false,
          stickyAuth: false,
        ),
      );
      
      return result;
    } catch (e) {
      return false;
    }
  }

  /// Stop authentication
  Future<void> stopAuthentication() async {
    try {
      await _localAuth.stopAuthentication();
    } catch (e) {
      // Ignore errors when stopping authentication
    }
  }

  /// Get device biometric capabilities
  BiometricCapabilities getCapabilities() {
    return BiometricCapabilities(
      isAvailable: _isAvailable,
      isDeviceSupported: _isDeviceSupported,
      availableBiometrics: _availableBiometrics,
      hasFingerprint: isFingerprintAvailable(),
      hasFaceId: isFaceIdAvailable(),
      biometricCount: _availableBiometrics.length,
    );
  }
}

/// Biometric authentication result
class BiometricResult {
  final bool success;
  final String? error;
  final BiometricResultType type;

  BiometricResult({
    required this.success,
    this.error,
    required this.type,
  });

  @override
  String toString() {
    return 'BiometricResult(success: $success, error: $error, type: $type)';
  }
}

/// Biometric result types
enum BiometricResultType {
  success,
  failed,
  unavailable,
  notEnrolled,
  lockedOut,
  passcodeNotSet,
  unsupported,
  error,
}

/// Biometric capabilities information
class BiometricCapabilities {
  final bool isAvailable;
  final bool isDeviceSupported;
  final List<BiometricType> availableBiometrics;
  final bool hasFingerprint;
  final bool hasFaceId;
  final int biometricCount;

  BiometricCapabilities({
    required this.isAvailable,
    required this.isDeviceSupported,
    required this.availableBiometrics,
    required this.hasFingerprint,
    required this.hasFaceId,
    required this.biometricCount,
  });

  @override
  String toString() {
    return 'BiometricCapabilities('
        'isAvailable: $isAvailable, '
        'hasFingerprint: $hasFingerprint, '
        'hasFaceId: $hasFaceId, '
        'biometricCount: $biometricCount'
        ')';
  }
}

/// Biometric authentication helper methods
class BiometricHelper {
  /// Get user-friendly error message
  static String getErrorMessage(BiometricResult result) {
    switch (result.type) {
      case BiometricResultType.unavailable:
        return 'المصادقة البيومترية غير متاحة على هذا الجهاز';
      case BiometricResultType.notEnrolled:
        return 'لم يتم تسجيل أي بيانات بيومترية. يرجى إضافة بصمة إصبع أو وجه في إعدادات الجهاز';
      case BiometricResultType.lockedOut:
        return 'تم تجاوز عدد المحاولات المتاحة. يرجى استخدام رمز المرور أو الانتظار';
      case BiometricResultType.passcodeNotSet:
        return 'يجب تعيين رمز مرور للجهاز أولاً';
      case BiometricResultType.unsupported:
        return 'نظام التشغيل الحالي لا يدعم المصادقة البيومترية';
      case BiometricResultType.failed:
        return 'فشلت المصادقة البيومترية. يرجى المحاولة مرة أخرى';
      case BiometricResultType.error:
        return result.error ?? 'حدث خطأ غير متوقع';
      case BiometricResultType.success:
        return 'تمت المصادقة بنجاح';
    }
  }

  /// Get icon for biometric type
  static IconData getBiometricIcon(BiometricType type) {
    switch (type) {
      case BiometricType.fingerprint:
        return Icons.fingerprint;
      case BiometricType.face:
        return Icons.face;
      case BiometricType.iris:
        return Icons.visibility;
      case BiometricType.strong:
        return Icons.security;
      case BiometricType.weak:
        return Icons.lock_outline;
      default:
        return Icons.help_outline;
    }
  }

  /// Get localized biometric type name
  static String getBiometricTypeName(BiometricType type) {
    switch (type) {
      case BiometricType.fingerprint:
        return 'بصمة الإصبع';
      case BiometricType.face:
        return 'التعرف على الوجه';
      case BiometricType.iris:
        return 'التعرف على قزحية العين';
      case BiometricType.strong:
        return 'مصادقة قوية';
      case BiometricType.weak:
        return 'مصادقة ضعيفة';
      default:
        return 'غير معروف';
    }
  }
}
