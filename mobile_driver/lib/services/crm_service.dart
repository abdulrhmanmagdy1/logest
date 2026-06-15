import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class CustomerTier {
  static const String regular = 'regular';
  static const String silver = 'silver';
  static const String gold = 'gold';
  static const String platinum = 'platinum';
  static const String vip = 'vip';
}

class CRMService extends ChangeNotifier {
  List<Map<String, dynamic>> _customers = [];
  Map<String, dynamic>? _selectedCustomer;
  bool _isLoading = false;
  String? _error;

  List<Map<String, dynamic>> get customers => _customers;
  Map<String, dynamic>? get selectedCustomer => _selectedCustomer;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Get all customers
  Future<void> fetchCustomers({
    String? tier,
    String? search,
    int page = 1,
    int limit = 20,
  }) async {
    try {
      _isLoading = true;
      _error = null;
      notifyListeners();

      String url = '$_apiBaseUrl/crm/customers?page=$page&limit=$limit';
      if (tier != null) url += '&tier=$tier';
      if (search != null) url += '&search=$search';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _customers = List<Map<String, dynamic>>.from(data['customers'] ?? []);
      } else {
        _error = 'Failed to fetch customers';
      }
    } catch (e) {
      _error = 'Error: $e';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get customer details
  Future<Map<String, dynamic>?> getCustomerDetails(String customerId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/crm/customers/$customerId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _selectedCustomer = data;
        notifyListeners();
        return data;
      }
      return null;
    } catch (e) {
      debugPrint('Error fetching customer details: $e');
      return null;
    }
  }

  // Create new customer
  Future<bool> createCustomer(Map<String, dynamic> customerData) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/crm/customers'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(customerData),
      );

      if (response.statusCode == 201) {
        await fetchCustomers();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error creating customer: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Update customer
  Future<bool> updateCustomer(
    String customerId,
    Map<String, dynamic> customerData,
  ) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.put(
        Uri.parse('$_apiBaseUrl/crm/customers/$customerId'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(customerData),
      );

      if (response.statusCode == 200) {
        await fetchCustomers();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating customer: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Delete customer
  Future<bool> deleteCustomer(String customerId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_apiBaseUrl/crm/customers/$customerId'),
      );

      if (response.statusCode == 200) {
        _customers.removeWhere((c) => c['_id'] == customerId);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error deleting customer: $e');
      return false;
    }
  }

  // Get customer order history
  Future<List<Map<String, dynamic>>> getCustomerOrderHistory(
    String customerId,
  ) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/crm/customers/$customerId/orders'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['orders'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching order history: $e');
      return [];
    }
  }

  // Get customer statistics
  Future<Map<String, dynamic>> getCustomerStats(String customerId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/crm/customers/$customerId/stats'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching customer stats: $e');
      return {};
    }
  }

  // Update customer tier
  Future<bool> updateCustomerTier(
    String customerId,
    String newTier,
  ) async {
    try {
      final response = await http.patch(
        Uri.parse('$_apiBaseUrl/crm/customers/$customerId/tier'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'tier': newTier}),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating customer tier: $e');
      return false;
    }
  }

  // Add customer note
  Future<bool> addCustomerNote(
    String customerId,
    String note,
  ) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/crm/customers/$customerId/notes'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'note': note,
          'timestamp': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error adding customer note: $e');
      return false;
    }
  }

  // Get customer notes
  Future<List<Map<String, dynamic>>> getCustomerNotes(
    String customerId,
  ) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/crm/customers/$customerId/notes'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['notes'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching customer notes: $e');
      return [];
    }
  }

  // Calculate customer tier based on activity
  String calculateCustomerTier(Map<String, dynamic> customerStats) {
    final totalOrders = customerStats['totalOrders'] ?? 0;
    final totalSpent = customerStats['totalSpent'] ?? 0.0;
    final avgOrderValue = totalOrders > 0 ? totalSpent / totalOrders : 0.0;

    if (totalOrders >= 100 && totalSpent >= 50000) {
      return CustomerTier.vip;
    } else if (totalOrders >= 50 && totalSpent >= 25000) {
      return CustomerTier.platinum;
    } else if (totalOrders >= 25 && totalSpent >= 10000) {
      return CustomerTier.gold;
    } else if (totalOrders >= 10 && totalSpent >= 5000) {
      return CustomerTier.silver;
    } else {
      return CustomerTier.regular;
    }
  }

  // Get tier benefits
  Map<String, dynamic> getTierBenefits(String tier) {
    switch (tier) {
      case CustomerTier.vip:
        return {
          'discount': 15,
          'priority': 1,
          'features': [
            'خصم 15% على جميع الطلبات',
            'أولوية في التوزيع',
            'دعم فني متاح 24/7',
            'مدير حساب خاص',
          ],
        };
      case CustomerTier.platinum:
        return {
          'discount': 10,
          'priority': 2,
          'features': [
            'خصم 10% على جميع الطلبات',
            'أولوية عالية في التوزيع',
            'دعم فني مخصص',
          ],
        };
      case CustomerTier.gold:
        return {
          'discount': 7,
          'priority': 3,
          'features': [
            'خصم 7% على جميع الطلبات',
            'أولوية متوسطة في التوزيع',
          ],
        };
      case CustomerTier.silver:
        return {
          'discount': 5,
          'priority': 4,
          'features': [
            'خصم 5% على جميع الطلبات',
          ],
        };
      default:
        return {
          'discount': 0,
          'priority': 5,
          'features': [],
        };
    }
  }

  // Search customers
  Future<List<Map<String, dynamic>>> searchCustomers(String query) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/crm/customers/search?q=$query'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['customers'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error searching customers: $e');
      return [];
    }
  }

  // Get customer insights
  Future<Map<String, dynamic>> getCustomerInsights() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/crm/insights'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching customer insights: $e');
      return {};
    }
  }

  // Get churn prediction
  Future<Map<String, dynamic>> getChurnPrediction(String customerId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/crm/customers/$customerId/churn-prediction'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching churn prediction: $e');
      return {};
    }
  }

  // Get customer lifetime value
  Future<double> getCustomerLifetimeValue(String customerId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/crm/customers/$customerId/clv'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return (data['clv'] ?? 0.0).toDouble();
      }
      return 0.0;
    } catch (e) {
      debugPrint('Error fetching CLV: $e');
      return 0.0;
    }
  }
}
