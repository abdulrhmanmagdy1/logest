import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  static const String baseUrl = 'http://localhost:5000/api';
  static String? _token;

  static void setToken(String? token) {
    _token = token;
  }

  static Map<String, String> get _headers {
    final headers = <String, String>{
      'Content-Type': 'application/json',
    };
    if (_token != null) {
      headers['Authorization'] = 'Bearer $_token';
    }
    return headers;
  }

  // Auth endpoints
  static Future<Map<String, dynamic>> login(String email, String password) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/login'),
      headers: _headers,
      body: jsonEncode({'email': email, 'password': password}),
    );
    return jsonDecode(response.body);
  }

  static Future<Map<String, dynamic>> register(Map<String, dynamic> userData) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/register'),
      headers: _headers,
      body: jsonEncode(userData),
    );
    return jsonDecode(response.body);
  }

  // Shipments endpoints
  static Future<List<dynamic>> getShipments() async {
    final response = await http.get(
      Uri.parse('$baseUrl/shipments'),
      headers: _headers,
    );
    return jsonDecode(response.body);
  }

  static Future<Map<String, dynamic>> createShipment(Map<String, dynamic> shipmentData) async {
    final response = await http.post(
      Uri.parse('$baseUrl/shipments'),
      headers: _headers,
      body: jsonEncode(shipmentData),
    );
    return jsonDecode(response.body);
  }

  static Future<Map<String, dynamic>> updateShipment(String id, Map<String, dynamic> data) async {
    final response = await http.put(
      Uri.parse('$baseUrl/shipments/$id'),
      headers: _headers,
      body: jsonEncode(data),
    );
    return jsonDecode(response.body);
  }

  // Maintenance endpoints
  static Future<List<dynamic>> getMaintenanceRecords() async {
    final response = await http.get(
      Uri.parse('$baseUrl/maintenance'),
      headers: _headers,
    );
    return jsonDecode(response.body);
  }

  static Future<Map<String, dynamic>> createMaintenanceRecord(Map<String, dynamic> data) async {
    final response = await http.post(
      Uri.parse('$baseUrl/maintenance'),
      headers: _headers,
      body: jsonEncode(data),
    );
    return jsonDecode(response.body);
  }

  // Trucks endpoints
  static Future<List<dynamic>> getTrucks() async {
    final response = await http.get(
      Uri.parse('$baseUrl/trucks'),
      headers: _headers,
    );
    return jsonDecode(response.body);
  }

  // Analytics endpoints
  static Future<Map<String, dynamic>> getAnalytics() async {
    final response = await http.get(
      Uri.parse('$baseUrl/analytics'),
      headers: _headers,
    );
    return jsonDecode(response.body);
  }

  // Payment endpoints
  static Future<Map<String, dynamic>> createPaymentIntent(Map<String, dynamic> data) async {
    final response = await http.post(
      Uri.parse('$baseUrl/payments/create-payment-intent'),
      headers: _headers,
      body: jsonEncode(data),
    );
    return jsonDecode(response.body);
  }

  static Future<Map<String, dynamic>> confirmPayment(String paymentIntentId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/payments/confirm-payment'),
      headers: _headers,
      body: jsonEncode({'paymentIntentId': paymentIntentId}),
    );
    return jsonDecode(response.body);
  }

  // Location endpoints
  static Future<Map<String, dynamic>> updateLocation(Map<String, dynamic> locationData) async {
    final response = await http.post(
      Uri.parse('$baseUrl/locations'),
      headers: _headers,
      body: jsonEncode(locationData),
    );
    return jsonDecode(response.body);
  }

  // Maintenance alerts
  static Future<Map<String, dynamic>> getMaintenanceAlerts() async {
    final response = await http.get(
      Uri.parse('$baseUrl/maintenance/alerts'),
      headers: _headers,
    );
    return jsonDecode(response.body);
  }

  // Complete maintenance
  static Future<Map<String, dynamic>> completeMaintenance(String id) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/maintenance/$id/complete'),
      headers: _headers,
    );
    return jsonDecode(response.body);
  }

  // Error handling
  static String handleError(dynamic error) {
    if (error is http.Response) {
      final responseBody = jsonDecode(error.body);
      return responseBody['message'] ?? 'An error occurred';
    }
    return error.toString();
  }
}
