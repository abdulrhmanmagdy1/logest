import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class IntegrationService extends ChangeNotifier {
  Map<String, dynamic> _paymentConfig = {};
  Map<String, dynamic> _smsConfig = {};
  Map<String, dynamic> _erpConfig = {};
  bool _isLoading = false;
  String? _error;

  Map<String, dynamic> get paymentConfig => _paymentConfig;
  Map<String, dynamic> get smsConfig => _smsConfig;
  Map<String, dynamic> get erpConfig => _erpConfig;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Payment Gateway Integration
  Future<bool> processPayment({
    required String gateway, // stripe, paypal, paymob
    required double amount,
    required String currency,
    required String description,
    Map<String, dynamic>? metadata,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/integrations/payment/process'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'gateway': gateway,
          'amount': amount,
          'currency': currency,
          'description': description,
          'metadata': metadata,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['success'] ?? false;
      }
      return false;
    } catch (e) {
      debugPrint('Payment processing error: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Refund payment
  Future<bool> refundPayment({
    required String paymentId,
    required double amount,
    required String reason,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/integrations/payment/refund'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'paymentId': paymentId,
          'amount': amount,
          'reason': reason,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Refund error: $e');
      return false;
    }
  }

  // Get payment methods
  Future<List<Map<String, dynamic>>> getPaymentMethods() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/integrations/payment/methods'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['methods'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching payment methods: $e');
      return [];
    }
  }

  // SMS Gateway Integration
  Future<bool> sendSMS({
    required String phoneNumber,
    required String message,
    String? templateId,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/integrations/sms/send'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'phoneNumber': phoneNumber,
          'message': message,
          'templateId': templateId,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['success'] ?? false;
      }
      return false;
    } catch (e) {
      debugPrint('SMS sending error: $e');
      return false;
    }
  }

  // Send bulk SMS
  Future<bool> sendBulkSMS({
    required List<String> phoneNumbers,
    required String message,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/integrations/sms/bulk'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'phoneNumbers': phoneNumbers,
          'message': message,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Bulk SMS error: $e');
      return false;
    }
  }

  // Get SMS delivery status
  Future<Map<String, dynamic>> getSMSDeliveryStatus(String messageId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/integrations/sms/status/$messageId'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching SMS status: $e');
      return {};
    }
  }

  // Email Integration
  Future<bool> sendEmail({
    required String to,
    required String subject,
    required String body,
    String? templateId,
    Map<String, dynamic>? templateData,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/integrations/email/send'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'to': to,
          'subject': subject,
          'body': body,
          'templateId': templateId,
          'templateData': templateData,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Email sending error: $e');
      return false;
    }
  }

  // ERP System Integration
  Future<bool> syncWithERP({
    required String erpSystem, // sap, oracle, microsoft_dynamics
    required String syncType, // customers, invoices, inventory
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/integrations/erp/sync'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'erpSystem': erpSystem,
          'syncType': syncType,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('ERP sync error: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get ERP sync status
  Future<Map<String, dynamic>> getERPSyncStatus() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/integrations/erp/sync-status'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching ERP sync status: $e');
      return {};
    }
  }

  // Configure integration
  Future<bool> configureIntegration({
    required String integrationType,
    required Map<String, dynamic> config,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/integrations/configure'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'type': integrationType,
          'config': config,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Integration configuration error: $e');
      return false;
    }
  }

  // Test integration
  Future<Map<String, dynamic>> testIntegration(String integrationType) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/integrations/test'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'type': integrationType}),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {'success': false, 'message': 'Test failed'};
    } catch (e) {
      debugPrint('Integration test error: $e');
      return {'success': false, 'message': 'Test failed'};
    }
  }

  // Get integration logs
  Future<List<Map<String, dynamic>>> getIntegrationLogs({
    String? integrationType,
    int limit = 50,
  }) async {
    try {
      String url = '$_apiBaseUrl/integrations/logs?limit=$limit';
      if (integrationType != null) {
        url += '&type=$integrationType';
      }

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['logs'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching integration logs: $e');
      return [];
    }
  }

  // Webhook handler
  Future<bool> handleWebhook({
    required String integrationType,
    required Map<String, dynamic> payload,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/integrations/webhook'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'type': integrationType,
          'payload': payload,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Webhook handling error: $e');
      return false;
    }
  }

  // Get available integrations
  Future<List<Map<String, dynamic>>> getAvailableIntegrations() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/integrations/available'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['integrations'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching available integrations: $e');
      return [];
    }
  }

  // Enable/Disable integration
  Future<bool> toggleIntegration({
    required String integrationType,
    required bool enabled,
  }) async {
    try {
      final response = await http.patch(
        Uri.parse('$_apiBaseUrl/integrations/$integrationType/toggle'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'enabled': enabled}),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Toggle integration error: $e');
      return false;
    }
  }
}

// Integration Types
class IntegrationType {
  static const String payment = 'payment';
  static const String sms = 'sms';
  static const String email = 'email';
  static const String erp = 'erp';
  static const String accounting = 'accounting';
  static const String crm = 'crm';
  static const String maps = 'maps';
  static const String analytics = 'analytics';
}

// Payment Gateways
class PaymentGateway {
  static const String stripe = 'stripe';
  static const String paypal = 'paypal';
  static const String paymob = 'paymob';
  static const String hyperpay = 'hyperpay';
  static const String tap = 'tap';
}

// SMS Providers
class SMSProvider {
  static const String twilio = 'twilio';
  static const String nexmo = 'nexmo';
  static const String messagebird = 'messagebird';
  static const String local = 'local';
}

// ERP Systems
class ERPSystem {
  static const String sap = 'sap';
  static const String oracle = 'oracle';
  static const String microsoftDynamics = 'microsoft_dynamics';
  static const String netsuite = 'netsuite';
}
