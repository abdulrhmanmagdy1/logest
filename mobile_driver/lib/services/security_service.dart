import 'dart:async';
import 'dart:convert';
import 'dart:math' as math;
import 'package:flutter/foundation.dart';
import 'package:crypto/crypto.dart';
import 'package:encrypt/encrypt.dart.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;

class SecurityService extends ChangeNotifier {
  String? _sessionToken;
  String? _refreshToken;
  DateTime? _tokenExpiry;
  Timer? _tokenRefreshTimer;
  bool _isLocked = false;
  int _failedAttempts = 0;
  DateTime? _lockoutUntil;

  String? get sessionToken => _sessionToken;
  bool get isLocked => _isLocked;
  int get failedAttempts => _failedAttempts;
  bool get isLockedOut => _lockoutUntil != null && DateTime.now().isBefore(_lockoutUntil!);

  final String _apiBaseUrl = 'http://your-api-url.com/api';
  final int _maxFailedAttempts = 5;
  final Duration _lockoutDuration = const Duration(minutes: 15);

  // Initialize security
  Future<void> initialize() async {
    await _loadSession();
    _startTokenRefreshTimer();
  }

  // Load session from secure storage
  Future<void> _loadSession() async {
    final prefs = await SharedPreferences.getInstance();
    _sessionToken = prefs.getString('sessionToken');
    _refreshToken = prefs.getString('refreshToken');
    final expiryStr = prefs.getString('tokenExpiry');
    if (expiryStr != null) {
      _tokenExpiry = DateTime.parse(expiryStr);
    }
    _failedAttempts = prefs.getInt('failedAttempts') ?? 0;
    final lockoutStr = prefs.getString('lockoutUntil');
    if (lockoutStr != null) {
      _lockoutUntil = DateTime.parse(lockoutStr);
    }
  }

  // Save session to secure storage
  Future<void> _saveSession() async {
    final prefs = await SharedPreferences.getInstance();
    if (_sessionToken != null) {
      await prefs.setString('sessionToken', _sessionToken!);
    }
    if (_refreshToken != null) {
      await prefs.setString('refreshToken', _refreshToken!);
    }
    if (_tokenExpiry != null) {
      await prefs.setString('tokenExpiry', _tokenExpiry!.toIso8601String());
    }
    await prefs.setInt('failedAttempts', _failedAttempts);
    if (_lockoutUntil != null) {
      await prefs.setString('lockoutUntil', _lockoutUntil!.toIso8601String());
    }
  }

  // Generate secure random string
  String generateSecureToken({int length = 32}) {
    final random = math.Random.secure();
    final values = List<int>.generate(length, (i) => random.nextInt(256));
    return base64Url.encode(values);
  }

  // Hash password using SHA-256
  String hashPassword(String password) {
    final bytes = utf8.encode(password);
    final hash = sha256.convert(bytes);
    return hash.toString();
  }

  // Encrypt data
  String encryptData(String data, String encryptionKey) {
    final key = Key.fromUtf8(encryptionKey.padRight(32, '0').substring(0, 32));
    final iv = IV.fromLength(16);
    final encrypter = Encrypter(AES(key));
    final encrypted = encrypter.encrypt(data, iv: iv);
    return encrypted.base64;
  }

  // Decrypt data
  String decryptData(String encryptedData, String encryptionKey) {
    try {
      final key = Key.fromUtf8(encryptionKey.padRight(32, '0').substring(0, 32));
      final iv = IV.fromLength(16);
      final encrypter = Encrypter(AES(key));
      final decrypted = encrypter.decrypt64(encryptedData, iv: iv);
      return decrypted;
    } catch (e) {
      debugPrint('Decryption error: $e');
      return '';
    }
  }

  // Validate password strength
  Map<String, dynamic> validatePasswordStrength(String password) {
    int score = 0;
    List<String> feedback = [];

    if (password.length >= 8) {
      score += 1;
    } else {
      feedback.add('كلمة المرور يجب أن تكون 8 أحرف على الأقل');
    }

    if (password.contains(RegExp(r'[A-Z]'))) {
      score += 1;
    } else {
      feedback.add('أضف حرف كبير');
    }

    if (password.contains(RegExp(r'[a-z]'))) {
      score += 1;
    } else {
      feedback.add('أضف حرف صغير');
    }

    if (password.contains(RegExp(r'[0-9]'))) {
      score += 1;
    } else {
      feedback.add('أضف رقم');
    }

    if (password.contains(RegExp(r'[!@#$%^&*(),.?":{}|<>]'))) {
      score += 1;
    } else {
      feedback.add('أضف رمز خاص');
    }

    String strength;
    if (score <= 2) {
      strength = 'ضعيفة';
    } else if (score <= 3) {
      strength = 'متوسطة';
    } else if (score <= 4) {
      strength = 'قوية';
    } else {
      strength = 'قوية جداً';
    }

    return {
      'score': score,
      'strength': strength,
      'feedback': feedback,
    };
  }

  // Generate OTP
  String generateOTP({int length = 6}) {
    final random = math.Random.secure();
    final otp = List<int>.generate(length, (i) => random.nextInt(10));
    return otp.join();
  }

  // Verify OTP
  Future<bool> verifyOTP({
    required String userId,
    required String otp,
    required String type, // login, password_reset, etc.
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/auth/verify-otp'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'userId': userId,
          'otp': otp,
          'type': type,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['valid'] == true) {
          return true;
        }
      }
      return false;
    } catch (e) {
      debugPrint('OTP verification error: $e');
      return false;
    }
  }

  // Send OTP
  Future<bool> sendOTP({
    required String userId,
    required String type,
    String? method, // sms, email
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/auth/send-otp'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'userId': userId,
          'type': type,
          'method': method ?? 'sms',
        }),
      );

      return response.statusCode == 200;
    } catch (e) {
      debugPrint('Send OTP error: $e');
      return false;
    }
  }

  // Record failed login attempt
  Future<void> recordFailedAttempt() async {
    _failedAttempts++;
    
    if (_failedAttempts >= _maxFailedAttempts) {
      _lockoutUntil = DateTime.now().add(_lockoutDuration);
      _isLocked = true;
    }
    
    await _saveSession();
    notifyListeners();
  }

  // Reset failed attempts
  Future<void> resetFailedAttempts() async {
    _failedAttempts = 0;
    _lockoutUntil = null;
    _isLocked = false;
    await _saveSession();
    notifyListeners();
  }

  // Lock app
  void lockApp() {
    _isLocked = true;
    notifyListeners();
  }

  // Unlock app
  Future<bool> unlockApp(String password) async {
    // Verify password or biometric
    final prefs = await SharedPreferences.getInstance();
    final storedPassword = prefs.getString('userPassword');
    
    if (storedPassword != null && hashPassword(password) == storedPassword) {
      _isLocked = false;
      await resetFailedAttempts();
      return true;
    }
    
    await recordFailedAttempt();
    return false;
  }

  // Start token refresh timer
  void _startTokenRefreshTimer() {
    _tokenRefreshTimer?.cancel();
    
    if (_tokenExpiry != null) {
      final timeUntilExpiry = _tokenExpiry!.difference(DateTime.now());
      final refreshTime = timeUntilExpiry - const Duration(minutes: 5);
      
      if (refreshTime.inSeconds > 0) {
        _tokenRefreshTimer = Timer(refreshTime, _refreshToken);
      }
    }
  }

  // Refresh token
  Future<void> _refreshToken() async {
    if (_refreshToken == null) return;

    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/auth/refresh'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'refreshToken': _refreshToken,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _sessionToken = data['token'];
        _refreshToken = data['refreshToken'];
        _tokenExpiry = DateTime.now().add(Duration(seconds: data['expiresIn']));
        await _saveSession();
        _startTokenRefreshTimer();
      } else {
        // Refresh failed, logout
        await logout();
      }
    } catch (e) {
      debugPrint('Token refresh error: $e');
      await logout();
    }
  }

  // Logout
  Future<void> logout() async {
    _tokenRefreshTimer?.cancel();
    _sessionToken = null;
    _refreshToken = null;
    _tokenExpiry = null;
    _isLocked = false;
    _failedAttempts = 0;
    _lockoutUntil = null;

    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('sessionToken');
    await prefs.remove('refreshToken');
    await prefs.remove('tokenExpiry');
    await prefs.remove('failedAttempts');
    await prefs.remove('lockoutUntil');

    notifyListeners();
  }

  // Audit log
  Future<void> logAuditEvent({
    required String action,
    required String entity,
    String? entityId,
    Map<String, dynamic>? details,
  }) async {
    try {
      await http.post(
        Uri.parse('$_apiBaseUrl/audit/log'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $_sessionToken',
        },
        body: jsonEncode({
          'action': action,
          'entity': entity,
          'entityId': entityId,
          'details': details,
          'timestamp': DateTime.now().toIso8601String(),
        }),
      );
    } catch (e) {
      debugPrint('Audit log error: $e');
    }
  }

  // Check if session is valid
  bool isSessionValid() {
    if (_sessionToken == null) return false;
    if (_tokenExpiry == null) return false;
    return DateTime.now().isBefore(_tokenExpiry!);
  }

  // Get time until lockout ends
  Duration? getTimeUntilLockoutEnd() {
    if (_lockoutUntil == null) return null;
    return _lockoutUntil!.difference(DateTime.now());
  }

  @override
  void dispose() {
    _tokenRefreshTimer?.cancel();
    super.dispose();
  }
}

// Audit action types
class AuditAction {
  static const String login = 'login';
  static const String logout = 'logout';
  static const String create = 'create';
  static const String update = 'update';
  static const String delete = 'delete';
  static const String view = 'view';
  static const String export = 'export';
  static const String print = 'print';
  static const String share = 'share';
}

// Entity types
class AuditEntity {
  static const String trip = 'trip';
  static const String vehicle = 'vehicle';
  static const String driver = 'driver';
  static const String client = 'client';
  static const String invoice = 'invoice';
  static const String payment = 'payment';
  static const String maintenance = 'maintenance';
  static const String user = 'user';
  static const String settings = 'settings';
}
