import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class ContractService extends ChangeNotifier {
  List<Map<String, dynamic>> _contracts = [];
  Map<String, dynamic>? _selectedContract;
  bool _isLoading = false;
  String? _error;

  List<Map<String, dynamic>> get contracts => _contracts;
  Map<String, dynamic>? get selectedContract => _selectedContract;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Get all contracts
  Future<void> fetchContracts({
    String? clientId,
    String? status,
    int page = 1,
    int limit = 20,
  }) async {
    try {
      _isLoading = true;
      _error = null;
      notifyListeners();

      String url = '$_apiBaseUrl/contracts?page=$page&limit=$limit';
      if (clientId != null) url += '&clientId=$clientId';
      if (status != null) url += '&status=$status';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _contracts = List<Map<String, dynamic>>.from(data['contracts'] ?? []);
      } else {
        _error = 'Failed to fetch contracts';
      }
    } catch (e) {
      _error = 'Error: $e';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get contract details
  Future<Map<String, dynamic>?> getContractDetails(String contractId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/contracts/$contractId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _selectedContract = data;
        notifyListeners();
        return data;
      }
      return null;
    } catch (e) {
      debugPrint('Error fetching contract details: $e');
      return null;
    }
  }

  // Create new contract
  Future<bool> createContract(Map<String, dynamic> contractData) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/contracts'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(contractData),
      );

      if (response.statusCode == 201) {
        await fetchContracts();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error creating contract: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Update contract
  Future<bool> updateContract(
    String contractId,
    Map<String, dynamic> contractData,
  ) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.put(
        Uri.parse('$_apiBaseUrl/contracts/$contractId'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(contractData),
      );

      if (response.statusCode == 200) {
        await fetchContracts();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating contract: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Delete contract
  Future<bool> deleteContract(String contractId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_apiBaseUrl/contracts/$contractId'),
      );

      if (response.statusCode == 200) {
        _contracts.removeWhere((c) => c['_id'] == contractId);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error deleting contract: $e');
      return false;
    }
  }

  // Get contract pricing
  Future<Map<String, dynamic>> getContractPricing(String contractId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/contracts/$contractId/pricing'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching contract pricing: $e');
      return {};
    }
  }

  // Calculate trip price based on contract
  Future<double> calculateTripPrice({
    required String contractId,
    required double distance,
    required double weight,
    String? serviceType,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/contracts/$contractId/calculate-price'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'distance': distance,
          'weight': weight,
          'serviceType': serviceType,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return (data['price'] ?? 0.0).toDouble();
      }
      return 0.0;
    } catch (e) {
      debugPrint('Error calculating trip price: $e');
      return 0.0;
    }
  }

  // Get contract invoices
  Future<List<Map<String, dynamic>>> getContractInvoices(String contractId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/contracts/$contractId/invoices'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['invoices'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching contract invoices: $e');
      return [];
    }
  }

  // Renew contract
  Future<bool> renewContract(String contractId, {int? extensionMonths}) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/contracts/$contractId/renew'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'extensionMonths': extensionMonths ?? 12,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error renewing contract: $e');
      return false;
    }
  }

  // Terminate contract
  Future<bool> terminateContract(String contractId, String reason) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/contracts/$contractId/terminate'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'reason': reason}),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error terminating contract: $e');
      return false;
    }
  }

  // Get contract usage statistics
  Future<Map<String, dynamic>> getContractUsage(String contractId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/contracts/$contractId/usage'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching contract usage: $e');
      return {};
    }
  }

  // Get expiring contracts
  Future<List<Map<String, dynamic>>> getExpiringContracts({int days = 30}) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/contracts/expiring?days=$days'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['contracts'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching expiring contracts: $e');
      return [];
    }
  }

  // Generate contract PDF
  Future<String?> generateContractPDF(String contractId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/contracts/$contractId/pdf'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['downloadUrl'];
      }
      return null;
    } catch (e) {
      debugPrint('Error generating contract PDF: $e');
      return null;
    }
  }

  // Get contract templates
  Future<List<Map<String, dynamic>>> getContractTemplates() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/contracts/templates'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['templates'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching contract templates: $e');
      return [];
    }
  }

  // Create contract from template
  Future<bool> createFromTemplate({
    required String templateId,
    required Map<String, dynamic> clientData,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/contracts/templates/$templateId/create'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(clientData),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error creating contract from template: $e');
      return false;
    }
  }

  // Get contract milestones
  Future<List<Map<String, dynamic>>> getContractMilestones(String contractId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/contracts/$contractId/milestones'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['milestones'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching contract milestones: $e');
      return [];
    }
  }

  // Update contract milestone
  Future<bool> updateMilestone({
    required String contractId,
    required String milestoneId,
    required String status,
  }) async {
    try {
      final response = await http.patch(
        Uri.parse('$_apiBaseUrl/contracts/$contractId/milestones/$milestoneId'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'status': status}),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating milestone: $e');
      return false;
    }
  }
}

// Contract Status
class ContractStatus {
  static const String draft = 'draft';
  static const String active = 'active';
  static const String pending = 'pending';
  static const String expired = 'expired';
  static const String terminated = 'terminated';
  static const String renewed = 'renewed';
}

// Contract Type
class ContractType {
  static const String standard = 'standard';
  static const String premium = 'premium';
  static const String vip = 'vip';
  static const String custom = 'custom';
}

// Pricing Model
class PricingModel {
  static const String perTrip = 'per_trip';
  static const String perKm = 'per_km';
  static const String perKg = 'per_kg';
  static const String monthly = 'monthly';
  static const String annual = 'annual';
  static const String custom = 'custom';
}
