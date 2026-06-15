import 'dart:developer' as developer;
import 'package:flutter/foundation.dart';

/// Logger Service
/// Centralized logging for debugging and error tracking
class LoggerService {
  LoggerService._(); // Private constructor

  static LoggerService? _instance;
  static LoggerService get instance {
    _instance ??= LoggerService._();
    return _instance!;
  }

  // Log Levels
  static const int verbose = 0;
  static const int debug = 1;
  static const int info = 2;
  static const int warning = 3;
  static const int error = 4;
  static const int silent = 5;

  // Current log level
  int _currentLevel = kDebugMode ? verbose : warning;
  int get currentLevel => _currentLevel;

  // Enable/Disable logging
  bool _enabled = kDebugMode;
  bool get isEnabled => _enabled;

  void setLevel(int level) {
    _currentLevel = level;
  }

  void enable() {
    _enabled = true;
  }

  void disable() {
    _enabled = false;
  }

  // Log Tags
  static const String tagNetwork = 'NETWORK';
  static const String tagAuth = 'AUTH';
  static const String tagUI = 'UI';
  static const String tagDatabase = 'DATABASE';
  static const String tagLocation = 'LOCATION';
  static const String tagNotification = 'NOTIFICATION';
  static const String tagTrip = 'TRIP';
  static const String tagMaintenance = 'MAINTENANCE';
  static const String tagError = 'ERROR';
  static const String tagPerformance = 'PERFORMANCE';

  /// Verbose logging
  void v(String tag, String message, {Object? data}) {
    _log(verbose, 'V/$tag', message, data: data);
  }

  /// Debug logging
  void d(String tag, String message, {Object? data}) {
    _log(debug, 'D/$tag', message, data: data);
  }

  /// Info logging
  void i(String tag, String message, {Object? data}) {
    _log(info, 'I/$tag', message, data: data);
  }

  /// Warning logging
  void w(String tag, String message, {Object? data, StackTrace? stackTrace}) {
    _log(warning, 'W/$tag', message, data: data, stackTrace: stackTrace);
  }

  /// Error logging
  void e(String tag, String message, {
    Object? error,
    StackTrace? stackTrace,
    Object? data,
  }) {
    _log(error, 'E/$tag', message, error: error, stackTrace: stackTrace, data: data);
    
    // Send to error tracking service in production
    if (!kDebugMode) {
      _sendToErrorTracking(tag, message, error, stackTrace);
    }
  }

  void _log(
    int level,
    String tag,
    String message, {
    Object? error,
    StackTrace? stackTrace,
    Object? data,
  }) {
    if (!_enabled || level < _currentLevel) return;

    final timestamp = DateTime.now().toIso8601String();
    final buffer = StringBuffer();
    
    buffer.writeln('[$timestamp] $tag: $message');
    
    if (data != null) {
      buffer.writeln('Data: $data');
    }
    
    if (error != null) {
      buffer.writeln('Error: $error');
    }
    
    if (stackTrace != null) {
      buffer.writeln('StackTrace: $stackTrace');
    }

    // Use developer.log for better output
    developer.log(
      buffer.toString(),
      name: tag,
      error: error,
      stackTrace: stackTrace,
    );

    // Also print to console in debug mode
    if (kDebugMode) {
      // ignore: avoid_print
      print(buffer.toString());
    }
  }

  void _sendToErrorTracking(
    String tag,
    String message,
    Object? error,
    StackTrace? stackTrace,
  ) {
    // TODO: Implement error tracking service integration
    // Examples: Firebase Crashlytics, Sentry, etc.
  }

  // Convenience methods for specific tags
  void logNetwork(String message, {Object? data}) {
    d(tagNetwork, message, data: data);
  }

  void logAuth(String message, {Object? data}) {
    d(tagAuth, message, data: data);
  }

  void logUI(String message, {Object? data}) {
    v(tagUI, message, data: data);
  }

  void logDatabase(String message, {Object? data}) {
    d(tagDatabase, message, data: data);
  }

  void logLocation(String message, {Object? data}) {
    d(tagLocation, message, data: data);
  }

  void logNotification(String message, {Object? data}) {
    d(tagNotification, message, data: data);
  }

  void logTrip(String message, {Object? data}) {
    i(tagTrip, message, data: data);
  }

  void logMaintenance(String message, {Object? data}) {
    i(tagMaintenance, message, data: data);
  }

  void logError(String message, {Object? error, StackTrace? stackTrace}) {
    e(tagError, message, error: error, stackTrace: stackTrace);
  }

  void logPerformance(String operation, Duration duration) {
    i(tagPerformance, '$operation took ${duration.inMilliseconds}ms');
  }

  // Performance tracking
  Stopwatch? _performanceStopwatch;
  
  void startPerformanceTracking(String operation) {
    _performanceStopwatch = Stopwatch()..start();
    v(tagPerformance, 'Started: $operation');
  }
  
  void endPerformanceTracking(String operation) {
    if (_performanceStopwatch != null) {
      _performanceStopwatch!.stop();
      logPerformance(operation, _performanceStopwatch!.elapsed);
      _performanceStopwatch = null;
    }
  }
}

/// Global logger instance
LoggerService get logger => LoggerService.instance;
