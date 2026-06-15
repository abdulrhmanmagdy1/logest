import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class Part {
  final String id;
  final String partNumber;
  final String name;
  final String category; // tire, brake, engine, electrical, filter, etc.
  final String? brand;
  final String? model;
  final String? compatibleVehicles;
  final double? unitPrice;
  final int? currentStock;
  final int? minStockLevel;
  final int? maxStockLevel;
  final String? supplier;
  final String? location; // warehouse shelf location
  final String? imageUrl;
  final String? description;
  final bool isAvailable;

  Part({
    required this.id,
    required this.partNumber,
    required this.name,
    required this.category,
    this.brand,
    this.model,
    this.compatibleVehicles,
    this.unitPrice,
    this.currentStock,
    this.minStockLevel,
    this.maxStockLevel,
    this.supplier,
    this.location,
    this.imageUrl,
    this.description,
    this.isAvailable = true,
  });

  factory Part.fromJson(Map<String, dynamic> json) {
    return Part(
      id: json['id'] ?? '',
      partNumber: json['partNumber'] ?? '',
      name: json['name'] ?? '',
      category: json['category'] ?? '',
      brand: json['brand'],
      model: json['model'],
      compatibleVehicles: json['compatibleVehicles'],
      unitPrice: json['unitPrice']?.toDouble(),
      currentStock: json['currentStock'],
      minStockLevel: json['minStockLevel'],
      maxStockLevel: json['maxStockLevel'],
      supplier: json['supplier'],
      location: json['location'],
      imageUrl: json['imageUrl'],
      description: json['description'],
      isAvailable: json['isAvailable'] ?? true,
    );
  }
}

class PartsManagementService extends ChangeNotifier {
  List<Part> _parts = [];
  List<Part> _lowStockParts = [];
  List<Part> _outOfStockParts = [];
  List<Map<String, dynamic>> _partsUsageHistory = [];
  bool _isLoading = false;
  String? _error;

  List<Part> get parts => _parts;
  List<Part> get lowStockParts => _lowStockParts;
  List<Part> get outOfStockParts => _outOfStockParts;
  List<Map<String, dynamic>> get partsUsageHistory => _partsUsageHistory;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Fetch all parts
  Future<void> fetchParts({
    String? category,
    String? search,
    bool onlyLowStock = false,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      String url = '$_apiBaseUrl/parts';
      if (category != null) url += '?category=$category';
      if (search != null) url += (category != null ? '&' : '?') + 'search=$search';
      if (onlyLowStock) url += '&lowStock=true';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _parts = (data['parts'] as List)
            .map((p) => Part.fromJson(p))
            .toList();
        
        _updateStockAlerts();
      }
    } catch (e) {
      debugPrint('Error fetching parts: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Update stock alerts
  void _updateStockAlerts() {
    _lowStockParts = _parts
        .where((p) => 
            p.currentStock != null && 
            p.minStockLevel != null && 
            p.currentStock! <= p.minStockLevel! &&
            p.currentStock! > 0)
        .toList();
    
    _outOfStockParts = _parts
        .where((p) => p.currentStock == 0)
        .toList();
  }

  // Add new part
  Future<bool> addPart({
    required String partNumber,
    required String name,
    required String category,
    String? brand,
    String? model,
    String? compatibleVehicles,
    double? unitPrice,
    int? initialStock,
    int? minStockLevel,
    int? maxStockLevel,
    String? supplier,
    String? location,
    String? imageUrl,
    String? description,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/parts'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'partNumber': partNumber,
          'name': name,
          'category': category,
          'brand': brand,
          'model': model,
          'compatibleVehicles': compatibleVehicles,
          'unitPrice': unitPrice,
          'currentStock': initialStock,
          'minStockLevel': minStockLevel,
          'maxStockLevel': maxStockLevel,
          'supplier': supplier,
          'location': location,
          'imageUrl': imageUrl,
          'description': description,
        }),
      );

      if (response.statusCode == 201) {
        await fetchParts();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error adding part: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Update part stock
  Future<bool> updatePartStock({
    required String partId,
    required int quantity,
    required String operation, // add, subtract, set
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/parts/$partId/stock'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'quantity': quantity,
          'operation': operation,
        }),
      );

      if (response.statusCode == 200) {
        await fetchParts();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating part stock: $e');
      return false;
    }
  }

  // Record part usage
  Future<bool> recordPartUsage({
    required String partId,
    required String vehicleId,
    required int quantity,
    required String maintenanceId,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/parts/$partId/usage'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'vehicleId': vehicleId,
          'quantity': quantity,
          'maintenanceId': maintenanceId,
          'usedAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error recording part usage: $e');
      return false;
    }
  }

  // Get parts usage history
  Future<void> fetchPartsUsageHistory({
    String? partId,
    String? vehicleId,
    DateTime? startDate,
    DateTime? endDate,
  }) async {
    try {
      String url = '$_apiBaseUrl/parts/usage-history';
      if (partId != null) url += '?partId=$partId';
      if (vehicleId != null) url += '&vehicleId=$vehicleId';
      if (startDate != null) url += '&startDate=${startDate.toIso8601String()}';
      if (endDate != null) url += '&endDate=${endDate.toIso8601String()}';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _partsUsageHistory = List<Map<String, dynamic>>.from(data['history'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching parts usage history: $e');
    }
  }

  // Get parts by category
  List<Part> getPartsByCategory(String category) {
    return _parts.where((p) => p.category == category).toList();
  }

  // Search parts
  List<Part> searchParts(String query) {
    final lowerQuery = query.toLowerCase();
    return _parts.where((p) =>
        p.name.toLowerCase().contains(lowerQuery) ||
        p.partNumber.toLowerCase().contains(lowerQuery) ||
        (p.brand?.toLowerCase().contains(lowerQuery) ?? false) ||
        (p.model?.toLowerCase().contains(lowerQuery) ?? false)
    ).toList();
  }

  // Get compatible parts for vehicle
  Future<List<Part>> getCompatibleParts(String vehicleId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/parts/compatible?vehicleId=$vehicleId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return (data['parts'] as List)
            .map((p) => Part.fromJson(p))
            .toList();
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching compatible parts: $e');
      return [];
    }
  }

  // Get low stock alerts
  Future<List<Map<String, dynamic>>> getLowStockAlerts() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/parts/low-stock-alerts'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['alerts'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching low stock alerts: $e');
      return [];
    }
  }

  // Create purchase order
  Future<bool> createPurchaseOrder({
    required List<Map<String, dynamic>> items, // [{partId, quantity}]
    required String supplierId,
    String? notes,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/parts/purchase-order'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'items': items,
          'supplierId': supplierId,
          'notes': notes,
          'createdAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error creating purchase order: $e');
      return false;
    }
  }

  // Get parts cost analysis
  Future<Map<String, dynamic>> getPartsCostAnalysis({
    String period = 'month',
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/parts/cost-analysis?period=$period'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching parts cost analysis: $e');
      return {};
    }
  }

  // Calculate total inventory value
  double calculateTotalInventoryValue() {
    double total = 0;
    for (var part in _parts) {
      if (part.unitPrice != null && part.currentStock != null) {
        total += part.unitPrice! * part.currentStock!;
      }
    }
    return total;
  }
}

// Part Categories
class PartCategory {
  static const String tire = 'tire';
  static const String brake = 'brake';
  static const String engine = 'engine';
  static const String electrical = 'electrical';
  static const String filter = 'filter';
  static const String suspension = 'suspension';
  static const String transmission = 'transmission';
  static const String cooling = 'cooling';
  static const String exhaust = 'exhaust';
  static const String body = 'body';
  static const String fluid = 'fluid';
  static const String battery = 'battery';
  static const String other = 'other';
}

// Stock Operation
class StockOperation {
  static const String add = 'add';
  static const String subtract = 'subtract';
  static const String set = 'set';
}
