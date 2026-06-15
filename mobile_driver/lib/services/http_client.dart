import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../config/index.dart';
import 'logger_service.dart';

/// HTTP Client
/// Wrapper around http package with interceptors and error handling
class HttpClient {
  static final HttpClient _instance = HttpClient._internal();
  factory HttpClient() => _instance;
  HttpClient._internal();

  final http.Client _client = http.Client();
  String? _authToken;

  // Set auth token
  void setAuthToken(String token) {
    _authToken = token;
  }

  // Clear auth token
  void clearAuthToken() {
    _authToken = null;
  }

  // Headers
  Map<String, String> get _defaultHeaders => {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    if (_authToken != null) 'Authorization': 'Bearer $_authToken',
  };

  // GET Request
  Future<ApiResponse> get(String endpoint, {
    Map<String, dynamic>? queryParameters,
    Map<String, String>? headers,
  }) async {
    final uri = _buildUri(endpoint, queryParameters);
    final requestHeaders = {..._defaultHeaders, ...?headers};

    logger.logNetwork('GET $uri');
    
    try {
      final response = await _client.get(
        uri,
        headers: requestHeaders,
      ).timeout(ApiConfig.receiveTimeout);

      return _handleResponse(response);
    } on SocketException catch (e) {
      return ApiResponse.error('No internet connection', error: e);
    } on TimeoutException catch (e) {
      return ApiResponse.error('Request timeout', error: e);
    } catch (e, stackTrace) {
      logger.logError('GET request failed', error: e, stackTrace: stackTrace);
      return ApiResponse.error('Request failed', error: e);
    }
  }

  // POST Request
  Future<ApiResponse> post(String endpoint, {
    Map<String, dynamic>? body,
    Map<String, dynamic>? queryParameters,
    Map<String, String>? headers,
  }) async {
    final uri = _buildUri(endpoint, queryParameters);
    final requestHeaders = {..._defaultHeaders, ...?headers};

    logger.logNetwork('POST $uri');
    
    try {
      final response = await _client.post(
        uri,
        headers: requestHeaders,
        body: body != null ? jsonEncode(body) : null,
      ).timeout(ApiConfig.receiveTimeout);

      return _handleResponse(response);
    } on SocketException catch (e) {
      return ApiResponse.error('No internet connection', error: e);
    } on TimeoutException catch (e) {
      return ApiResponse.error('Request timeout', error: e);
    } catch (e, stackTrace) {
      logger.logError('POST request failed', error: e, stackTrace: stackTrace);
      return ApiResponse.error('Request failed', error: e);
    }
  }

  // PUT Request
  Future<ApiResponse> put(String endpoint, {
    Map<String, dynamic>? body,
    Map<String, dynamic>? queryParameters,
    Map<String, String>? headers,
  }) async {
    final uri = _buildUri(endpoint, queryParameters);
    final requestHeaders = {..._defaultHeaders, ...?headers};

    logger.logNetwork('PUT $uri');
    
    try {
      final response = await _client.put(
        uri,
        headers: requestHeaders,
        body: body != null ? jsonEncode(body) : null,
      ).timeout(ApiConfig.receiveTimeout);

      return _handleResponse(response);
    } on SocketException catch (e) {
      return ApiResponse.error('No internet connection', error: e);
    } on TimeoutException catch (e) {
      return ApiResponse.error('Request timeout', error: e);
    } catch (e, stackTrace) {
      logger.logError('PUT request failed', error: e, stackTrace: stackTrace);
      return ApiResponse.error('Request failed', error: e);
    }
  }

  // PATCH Request
  Future<ApiResponse> patch(String endpoint, {
    Map<String, dynamic>? body,
    Map<String, dynamic>? queryParameters,
    Map<String, String>? headers,
  }) async {
    final uri = _buildUri(endpoint, queryParameters);
    final requestHeaders = {..._defaultHeaders, ...?headers};

    logger.logNetwork('PATCH $uri');
    
    try {
      final response = await _client.patch(
        uri,
        headers: requestHeaders,
        body: body != null ? jsonEncode(body) : null,
      ).timeout(ApiConfig.receiveTimeout);

      return _handleResponse(response);
    } on SocketException catch (e) {
      return ApiResponse.error('No internet connection', error: e);
    } on TimeoutException catch (e) {
      return ApiResponse.error('Request timeout', error: e);
    } catch (e, stackTrace) {
      logger.logError('PATCH request failed', error: e, stackTrace: stackTrace);
      return ApiResponse.error('Request failed', error: e);
    }
  }

  // DELETE Request
  Future<ApiResponse> delete(String endpoint, {
    Map<String, dynamic>? queryParameters,
    Map<String, String>? headers,
  }) async {
    final uri = _buildUri(endpoint, queryParameters);
    final requestHeaders = {..._defaultHeaders, ...?headers};

    logger.logNetwork('DELETE $uri');
    
    try {
      final response = await _client.delete(
        uri,
        headers: requestHeaders,
      ).timeout(ApiConfig.receiveTimeout);

      return _handleResponse(response);
    } on SocketException catch (e) {
      return ApiResponse.error('No internet connection', error: e);
    } on TimeoutException catch (e) {
      return ApiResponse.error('Request timeout', error: e);
    } catch (e, stackTrace) {
      logger.logError('DELETE request failed', error: e, stackTrace: stackTrace);
      return ApiResponse.error('Request failed', error: e);
    }
  }

  // Build URI
  Uri _buildUri(String endpoint, Map<String, dynamic>? queryParameters) {
    final baseUrl = ApiConfig.currentBaseUrl;
    final uri = Uri.parse('$baseUrl$endpoint');
    
    if (queryParameters != null && queryParameters.isNotEmpty) {
      return uri.replace(
        queryParameters: queryParameters.map(
          (key, value) => MapEntry(key, value.toString()),
        ),
      );
    }
    
    return uri;
  }

  // Handle Response
  ApiResponse _handleResponse(http.Response response) {
    logger.logNetwork(
      'Response: ${response.statusCode} - ${response.reasonPhrase}',
      data: response.body,
    );

    if (response.statusCode >= 200 && response.statusCode < 300) {
      try {
        final data = jsonDecode(response.body);
        return ApiResponse.success(data, statusCode: response.statusCode);
      } catch (e) {
        return ApiResponse.success(response.body, statusCode: response.statusCode);
      }
    }

    // Handle errors
    String errorMessage = 'Request failed';
    
    try {
      final errorData = jsonDecode(response.body);
      errorMessage = errorData['message'] ?? errorData['error'] ?? 'Request failed';
    } catch (e) {
      errorMessage = response.reasonPhrase ?? 'Request failed';
    }

    return ApiResponse.error(
      errorMessage,
      statusCode: response.statusCode,
      data: response.body,
    );
  }

  // Dispose
  void dispose() {
    _client.close();
  }
}

/// API Response
class ApiResponse {
  final bool success;
  final dynamic data;
  final String? message;
  final int statusCode;
  final Object? error;

  ApiResponse({
    required this.success,
    this.data,
    this.message,
    required this.statusCode,
    this.error,
  });

  factory ApiResponse.success(dynamic data, {int statusCode = 200}) {
    return ApiResponse(
      success: true,
      data: data,
      statusCode: statusCode,
    );
  }

  factory ApiResponse.error(String message, {
    int statusCode = 500,
    Object? error,
    dynamic data,
  }) {
    return ApiResponse(
      success: false,
      message: message,
      statusCode: statusCode,
      error: error,
      data: data,
    );
  }

  bool get isSuccess => success;
  bool get isError => !success;
  bool get isUnauthorized => statusCode == 401;
  bool get isNotFound => statusCode == 404;
  bool get isServerError => statusCode >= 500;
}
