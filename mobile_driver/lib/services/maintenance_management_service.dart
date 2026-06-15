import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class MaintenanceRecord {
  final String id;
  final String vehicleId;
  final String vehicleNumber;
  final String maintenanceType; // routine, repair, inspection, emergency
  final String category; // oil_change, tire_change, engine, brake, electrical, etc.
  final String description;
  final DateTime date;
  final double cost;
  final String? mechanicName;
  final String? workshopName;
  final List<String>? partsUsed;
  final String? notes;
  final int? odometerReading;
  final String? imageUrl;
  final String status; // scheduled, in_progress, completed, cancelled

  MaintenanceRecord({
    required this.id,
    required this.vehicleId,
    required this.vehicleNumber,
    required this.maintenanceType,
    required this.category,
    required this.description,
    required this.date,
    required this.cost,
    this.mechanicName,
    this.workshopName,
    this.partsUsed,
    this.notes,
    this.odometerReading,
    this.imageUrl,
    this.status = 'completed',
  });

  factory MaintenanceRecord.fromJson(Map<String, dynamic> json) {
    return MaintenanceRecord(
      id: json['id'] ?? '',
      vehicleId: json['vehicleId'] ?? '',
      vehicleNumber: json['vehicleNumber'] ?? '',
      maintenanceType: json['maintenanceType'] ?? 'routine',
      category: json['category'] ?? '',
      description: json['description'] ?? '',
      date: DateTime.parse(json['date'] ?? DateTime.now().toIso8601String()),
      cost: (json['cost'] ?? 0.0).toDouble(),
      mechanicName: json['mechanicName'],
      workshopName: json['workshopName'],
      partsUsed: json['partsUsed'] != null ? List<String>.from(json['partsUsed']) : null,
      notes: json['notes'],
      odometerReading: json['odometerReading'],
      imageUrl: json['imageUrl'],
      status: json['status'] ?? 'completed',
    );
  }
}

class MaintenanceManagementService extends ChangeNotifier {
  List<MaintenanceRecord> _maintenanceRecords = [];
  List<MaintenanceRecord> _scheduledMaintenance = [];
  List<MaintenanceRecord> _maintenanceHistory = [];
  Map<String, dynamic>? _vehicleMaintenanceSummary;
  bool _isLoading = false;
  String? _error;

  List<MaintenanceRecord> get maintenanceRecords => _maintenanceRecords;
  List<MaintenanceRecord> get scheduledMaintenance => _scheduledMaintenance;
  List<MaintenanceRecord> get maintenanceHistory => _maintenanceHistory;
  Map<String, dynamic>? get vehicleMaintenanceSummary => _vehicleMaintenanceSummary;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Fetch all maintenance records
  Future<void> fetchMaintenanceRecords({
    String? vehicleId,
    String? status,
    String? category,
    DateTime? startDate,
    DateTime? endDate,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      String url = '$_apiBaseUrl/maintenance/records';
      if (vehicleId != null) url += '?vehicleId=$vehicleId';
      if (status != null) url += '&status=$status';
      if (category != null) url += '&category=$category';
      if (startDate != null) url += '&startDate=${startDate.toIso8601String()}';
      if (endDate != null) url += '&endDate=${endDate.toIso8601String()}';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _maintenanceRecords = (data['records'] as List)
            .map((r) => MaintenanceRecord.fromJson(r))
            .toList();
        
        _updateMaintenanceLists();
      }
    } catch (e) {
      debugPrint('Error fetching maintenance records: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Update maintenance lists
  void _updateMaintenanceLists() {
    final now = DateTime.now();
    _scheduledMaintenance = _maintenanceRecords
        .where((r) => r.status == 'scheduled' && r.date.isAfter(now))
        .toList()
      ..sort((a, b) => a.date.compareTo(b.date));
    
    _maintenanceHistory = _maintenanceRecords
        .where((r) => r.status == 'completed')
        .toList()
      ..sort((a, b) => b.date.compareTo(a.date));
  }

  // Create maintenance record
  Future<bool> createMaintenanceRecord({
    required String vehicleId,
    required String vehicleNumber,
    required String maintenanceType,
    required String category,
    required String description,
    required DateTime date,
    required double cost,
    String? mechanicName,
    String? workshopName,
    List<String>? partsUsed,
    String? notes,
    int? odometerReading,
    String? imageUrl,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/maintenance/records'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'vehicleId': vehicleId,
          'vehicleNumber': vehicleNumber,
          'maintenanceType': maintenanceType,
          'category': category,
          'description': description,
          'date': date.toIso8601String(),
          'cost': cost,
          'mechanicName': mechanicName,
          'workshopName': workshopName,
          'partsUsed': partsUsed,
          'notes': notes,
          'odometerReading': odometerReading,
          'imageUrl': imageUrl,
          'status': 'scheduled',
        }),
      );

      if (response.statusCode == 201) {
        await fetchMaintenanceRecords();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error creating maintenance record: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Update maintenance record
  Future<bool> updateMaintenanceRecord({
    required String recordId,
    String? status,
    String? description,
    double? cost,
    String? notes,
  }) async {
    try {
      final response = await http.put(
        Uri.parse('$_apiBaseUrl/maintenance/records/$recordId'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'status': status,
          'description': description,
          'cost': cost,
          'notes': notes,
        }),
      );

      if (response.statusCode == 200) {
        await fetchMaintenanceRecords();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating maintenance record: $e');
      return false;
    }
  }

  // Delete maintenance record
  Future<bool> deleteMaintenanceRecord(String recordId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_apiBaseUrl/maintenance/records/$recordId'),
      );

      if (response.statusCode == 200) {
        _maintenanceRecords.removeWhere((r) => r.id == recordId);
        _updateMaintenanceLists();
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error deleting maintenance record: $e');
      return false;
    }
  }

  // Get vehicle maintenance summary
  Future<void> fetchVehicleMaintenanceSummary(String vehicleId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/maintenance/vehicles/$vehicleId/summary'),
      );

      if (response.statusCode == 200) {
        _vehicleMaintenanceSummary = jsonDecode(response.body);
      }
    } catch (e) {
      debugPrint('Error fetching vehicle maintenance summary: $e');
    }
  }

  // Get maintenance cost by category
  Future<Map<String, double>> getMaintenanceCostByCategory({
    String? vehicleId,
    String period = 'month',
  }) async {
    try {
      String url = '$_apiBaseUrl/maintenance/cost-by-category?period=$period';
      if (vehicleId != null) url += '&vehicleId=$vehicleId';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return Map<String, double>.from(data['costs'] ?? {});
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching maintenance cost by category: $e');
      return {};
    }
  }

  // Get upcoming maintenance
  Future<List<MaintenanceRecord>> getUpcomingMaintenance({
    String? vehicleId,
    int days = 30,
  }) async {
    try {
      String url = '$_apiBaseUrl/maintenance/upcoming?days=$days';
      if (vehicleId != null) url += '&vehicleId=$vehicleId';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return (data['maintenance'] as List)
            .map((r) => MaintenanceRecord.fromJson(r))
            .toList();
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching upcoming maintenance: $e');
      return [];
    }
  }

  // Schedule maintenance
  Future<bool> scheduleMaintenance({
    required String vehicleId,
    required String category,
    required DateTime scheduledDate,
    String? notes,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/maintenance/schedule'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'vehicleId': vehicleId,
          'category': category,
          'scheduledDate': scheduledDate.toIso8601String(),
          'notes': notes,
        }),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error scheduling maintenance: $e');
      return false;
    }
  }

  // Complete maintenance
  Future<bool> completeMaintenance({
    required String recordId,
    required double actualCost,
    required List<String> partsUsed,
    String? mechanicNotes,
    String? imageUrl,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/maintenance/records/$recordId/complete'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'actualCost': actualCost,
          'partsUsed': partsUsed,
          'mechanicNotes': mechanicNotes,
          'imageUrl': imageUrl,
          'completedAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 200) {
        await fetchMaintenanceRecords();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error completing maintenance: $e');
      return false;
    }
  }

  // Get maintenance trends
  Future<Map<String, dynamic>> getMaintenanceTrends({
    String? vehicleId,
    String period = 'year',
  }) async {
    try {
      String url = '$_apiBaseUrl/maintenance/trends?period=$period';
      if (vehicleId != null) url += '&vehicleId=$vehicleId';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching maintenance trends: $e');
      return {};
    }
  }

  // Get maintenance statistics
  Map<String, dynamic> getMaintenanceStatistics() {
    if (_maintenanceRecords.isEmpty) {
      return {
        'totalRecords': 0,
        'totalCost': 0.0,
        'averageCost': 0.0,
        'completed': 0,
        'scheduled': 0,
        'inProgress': 0,
      };
    }

    final totalCost = _maintenanceRecords.fold<double>(
      0.0,
      (sum, r) => sum + r.cost,
    );

    final completed = _maintenanceRecords.where((r) => r.status == 'completed').length;
    final scheduled = _maintenanceRecords.where((r) => r.status == 'scheduled').length;
    final inProgress = _maintenanceRecords.where((r) => r.status == 'in_progress').length;

    return {
      'totalRecords': _maintenanceRecords.length,
      'totalCost': totalCost,
      'averageCost': totalCost / _maintenanceRecords.length,
      'completed': completed,
      'scheduled': scheduled,
      'inProgress': inProgress,
    };
  }
}

// Maintenance Types
class MaintenanceType {
  static const String routine = 'routine';
  static const String repair = 'repair';
  static const String inspection = 'inspection';
  static const String emergency = 'emergency';
  static const String preventive = 'preventive';
}

// Maintenance Categories
class MaintenanceCategory {
  static const String oilChange = 'oil_change';
  static const String tireChange = 'tire_change';
  static const String engine = 'engine';
  static const String brake = 'brake';
  static const String electrical = 'electrical';
  static const String suspension = 'suspension';
  static const String transmission = 'transmission';
  static const String cooling = 'cooling';
  static const String exhaust = 'exhaust';
  static const String ac = 'ac';
  static const String body = 'body';
  static const String other = 'other';
}

// Maintenance Status
class MaintenanceStatus {
  static const String scheduled = 'scheduled';
  static const String inProgress = 'in_progress';
  static const String completed = 'completed';
  static const String cancelled = 'cancelled';
  static const String pending = 'pending';
}
