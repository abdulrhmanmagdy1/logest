import 'dart:async';
import 'package:flutter/foundation.dart';
import '../utils/exceptions.dart';
import 'logger_service.dart';

/// Request Interceptor
class RequestInterceptor {
  final List<Future<void> Function(Map<String, dynamic>)> _interceptors = [];

  void add(Future<void> Function(Map<String, dynamic>) interceptor) {
    _interceptors.add(interceptor);
  }

  Future<void> intercept(Map<String, dynamic> request) async {
    for (final interceptor in _interceptors) {
      await interceptor(request);
    }
  }
}

/// Response Interceptor
class ResponseInterceptor {
  final List<Future<void> Function(dynamic)> _interceptors = [];

  void add(Future<void> Function(dynamic) interceptor) {
    _interceptors.add(interceptor);
  }

  Future<void> intercept(dynamic response) async {
    for (final interceptor in _interceptors) {
      await interceptor(response);
    }
  }
}

/// Error Interceptor
class ErrorInterceptor {
  final List<Future<void> Function(dynamic, StackTrace?)> _interceptors = [];

  void add(Future<void> Function(dynamic, StackTrace?) interceptor) {
    _interceptors.add(interceptor);
  }

  Future<void> intercept(dynamic error, StackTrace? stackTrace) async {
    for (final interceptor in _interceptors) {
      await interceptor(error, stackTrace);
    }
  }
}

/// HTTP Interceptor Chain
class InterceptorChain {
  static final InterceptorChain _instance = InterceptorChain._internal();
  factory InterceptorChain() => _instance;
  InterceptorChain._internal();

  final RequestInterceptor request = RequestInterceptor();
  final ResponseInterceptor response = ResponseInterceptor();
  final ErrorInterceptor error = ErrorInterceptor();

  void setupDefaultInterceptors() {
    // Add auth header interceptor
    request.add(_authInterceptor);
    
    // Add logging interceptor
    request.add(_loggingRequestInterceptor);
    response.add(_loggingResponseInterceptor);
    error.add(_loggingErrorInterceptor);
    
    // Add analytics interceptor
    request.add(_analyticsRequestInterceptor);
    error.add(_analyticsErrorInterceptor);
  }

  // Auth Interceptor
  static Future<void> _authInterceptor(Map<String, dynamic> request) async {
    // Add auth token if available
    final token = request['auth_token'];
    if (token != null) {
      final headers = request['headers'] as Map<String, String>? ?? {};
      headers['Authorization'] = 'Bearer $token';
      request['headers'] = headers;
    }
  }

  // Logging Interceptors
  static Future<void> _loggingRequestInterceptor(Map<String, dynamic> request) async {
    if (kDebugMode) {
      logger.v('INTERCEPTOR', 'Request: ${request['method']} ${request['url']}');
    }
  }

  static Future<void> _loggingResponseInterceptor(dynamic response) async {
    if (kDebugMode) {
      logger.v('INTERCEPTOR', 'Response: ${response.toString().substring(0, response.toString().length > 100 ? 100 : response.toString().length)}...');
    }
  }

  static Future<void> _loggingErrorInterceptor(dynamic error, StackTrace? stackTrace) async {
    logger.e('INTERCEPTOR', 'Error: $error', stackTrace: stackTrace);
  }

  // Analytics Interceptors
  static Future<void> _analyticsRequestInterceptor(Map<String, dynamic> request) async {
    // Track API calls
    // TODO: Implement analytics tracking
  }

  static Future<void> _analyticsErrorInterceptor(dynamic error, StackTrace? stackTrace) async {
    // Track errors
    final exception = ExceptionHandler.handle(error);
    if (exception is! NetworkException && exception is! TimeoutException) {
      // Send to error tracking service
      // TODO: Implement error tracking
    }
  }
}

// Global interceptor instance
InterceptorChain get interceptors => InterceptorChain();
