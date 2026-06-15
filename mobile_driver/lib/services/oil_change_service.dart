import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class OilChangeRecord {
  final String id;
  final String vehicleId;
  final String vehicleNumber;
  final DateTime changeDate;
  final int odometerReading;
  final String oilType; // synthetic, semi_synthetic, conventional
  final String oilBrand;
  final double oilQuantity; // in liters
  final double cost;
  final String? filterBrand;
  final double? filterCost;
  final String? workshopName;
  final String? notes;
  final int? nextChangeOdometer;
  final DateTime? nextChangeDate;
  final String? imageUrl;

  OilChangeRecord({
    required this.id,
    required this.vehicleId,
    required this.vehicleNumber,
    required this.changeDate,
    required this.odometerReading,
    required this.oilType,
    required this.oilBrand,
    required this.oilQuantity,
    required this.cost,
    this.filterBrand,
    this.filterCost,
    this.workshopName,
    this.notes,
    this.nextChangeOdometer,
    this.nextChangeDate,
    this.imageUrl,
  });

  factory OilChangeRecord.fromJson(Map<String, dynamic> json) {
    return OilChangeRecord(
      id: json['id'] ?? '',
      vehicleId: json['vehicleId'] ?? '',
      vehicleNumber: json['vehicleNumber'] ?? '',
      changeDate: DateTime.parse(json['changeDate'] ?? DateTime.now().toIso8601String()),
      odometerReading: json['odometerReading'] ?? 0,
      oilType: json['oilType'] ?? 'conventional',
      oilBrand: json['oilBrand'] ?? '',
      oilQuantity: (json['oilQuantity'] ?? 0.0).toDouble(),
      cost: (json['cost'] ?? 0.0).toDouble(),
      filterBrand: json['filterBrand'],
      filterCost: json['filterCost']?.toDouble(),
      workshopName: json['workshopName'],
      notes: json['notes'],
      nextChangeOdometer: json['nextChangeOdometer'],
      nextChangeDate: json['nextChangeDate'] != null ? DateTime.parse(json['nextChangeDate']) : null,
      imageUrl: json['imageUrl'],
    );
  }
}

class OilChangeService extends ChangeNotifier {
  List<OilChangeRecord> _oilChangeHistory = [];
  Map<String, dynamic>? _nextOilChange;
  List<Map<String, dynamic>> _oilChangeAlerts = [];
  bool _isLoading = false;
  String? _error;

  List<OilChangeRecord> get oilChangeHistory => _oilChangeHistory;
  Map<String, dynamic>? get nextOilChange => _nextOilChange;
  List<Map<String, dynamic>> get oilChangeAlerts => _oilChangeAlerts;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Fetch oil change history
  Future<void> fetchOilChangeHistory(String vehicleId) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/oil-change/history?vehicleId=$vehicleId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _oilChangeHistory = (data['records'] as List)
            .map((r) => OilChangeRecord.fromJson(r))
            .toList()
          ..sort((a, b) => b.changeDate.compareTo(a.changeDate));
        
        await _calculateNextOilChange(vehicleId);
      }
    } catch (e) {
      debugPrint('Error fetching oil change history: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Calculate next oil change
  Future<void> _calculateNextOilChange(String vehicleId) async {
    if (_oilChangeHistory.isEmpty) {
      _nextOilChange = null;
      return;
    }

    final lastChange = _oilChangeHistory.first;
    final lastOdometer = lastChange.odometerReading;
    final interval = _getOilChangeInterval(lastChange.oilType);
    final nextOdometer = lastOdometer + interval;
    
    // Estimate next change date based on average daily mileage
    final response = await http.get(
      Uri.parse('$_apiBaseUrl/vehicles/$vehicleId/avg-daily-mileage'),
    );

    double avgDailyMileage = 50; // default
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      avgDailyMileage = (data['avgDailyMileage'] ?? 50).toDouble();
    }

    final daysUntilNext = interval / avgDailyMileage;
    final nextDate = DateTime.now().add(Duration(days: daysUntilNext.toInt()));

    _nextOilChange = {
      'nextOdometer': nextOdometer,
      'nextDate': nextDate.toIso8601String(),
      'daysUntil': daysUntilNext.toInt(),
      'currentOdometer': lastOdometer,
      'lastChangeDate': lastChange.changeDate.toIso8601String(),
    };

    // Check for alerts
    await _checkOilChangeAlerts(vehicleId, nextOdometer, nextDate);
  }

  // Get oil change interval based on oil type
  int _getOilChangeInterval(String oilType) {
    switch (oilType) {
      case 'synthetic':
        return 15000; // km
      case 'semi_synthetic':
        return 10000; // km
      case 'conventional':
      default:
        return 5000; // km
    }
  }

  // Check for oil change alerts
  Future<void> _checkOilChangeAlerts(
    String vehicleId,
    int nextOdometer,
    DateTime nextDate,
  ) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/vehicles/$vehicleId/current-odometer'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final currentOdometer = data['odometer'] ?? 0;
        final remaining = nextOdometer - currentOdometer;
        final daysRemaining = nextDate.difference(DateTime.now()).inDays;

        _oilChangeAlerts = [];

        if (remaining <= 500 || daysRemaining <= 7) {
          _oilChangeAlerts.add({
            'type': 'urgent',
            'message': 'تغيير الزيت مطلوب قريباً',
            'remainingKm': remaining,
            'remainingDays': daysRemaining,
          });
        } else if (remaining <= 1000 || daysRemaining <= 14) {
          _oilChangeAlerts.add({
            'type': 'warning',
            'message': 'تغيير الزيت قريباً',
            'remainingKm': remaining,
            'remainingDays': daysRemaining,
          });
        } else if (remaining <= 2000 || daysRemaining <= 30) {
          _oilChangeAlerts.add({
            'type': 'info',
            'message': 'تذكير بتغيير الزيت',
            'remainingKm': remaining,
            'remainingDays': daysRemaining,
          });
        }
      }
    } catch (e) {
      debugPrint('Error checking oil change alerts: $e');
    }
  }

  // Record oil change
  Future<bool> recordOilChange({
    required String vehicleId,
    required String vehicleNumber,
    required int odometerReading,
    required String oilType,
    required String oilBrand,
    required double oilQuantity,
    required double cost,
    String? filterBrand,
    double? filterCost,
    String? workshopName,
    String? notes,
    String? imageUrl,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/oil-change/record'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'vehicleId': vehicleId,
          'vehicleNumber': vehicleNumber,
          'changeDate': DateTime.now().toIso8601String(),
          'odometerReading': odometerReading,
          'oilType': oilType,
          'oilBrand': oilBrand,
          'oilQuantity': oilQuantity,
          'cost': cost,
          'filterBrand': filterBrand,
          'filterCost': filterCost,
          'workshopName': workshopName,
          'notes': notes,
          'imageUrl': imageUrl,
        }),
      );

      if (response.statusCode == 201) {
        await fetchOilChangeHistory(vehicleId);
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error recording oil change: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Schedule oil change reminder
  Future<bool> scheduleOilChangeReminder({
    required String vehicleId,
    required DateTime reminderDate,
    int? odometerThreshold,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/oil-change/schedule-reminder'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'vehicleId': vehicleId,
          'reminderDate': reminderDate.toIso8601String(),
          'odometerThreshold': odometerThreshold,
        }),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error scheduling oil change reminder: $e');
      return false;
    }
  }

  // Get oil change statistics
  Future<Map<String, dynamic>> getOilChangeStatistics(String vehicleId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/oil-change/statistics?vehicleId=$vehicleId'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching oil change statistics: $e');
      return {};
    }
  }

  // Get oil change cost analysis
  Future<Map<String, dynamic>> getOilChangeCostAnalysis({
    String? vehicleId,
    String period = 'year',
  }) async {
    try {
      String url = '$_apiBaseUrl/oil-change/cost-analysis?period=$period';
      if (vehicleId != null) url += '&vehicleId=$vehicleId';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching oil change cost analysis: $e');
      return {};
    }
  }

  // Calculate oil efficiency
  double calculateOilEfficiency(OilChangeRecord record) {
    // Calculate cost per km
    if (record.odometerReading == 0) return 0.0;
    return record.cost / record.odometerReading;
  }

  // Get recommended oil type
  String getRecommendedOilType(String vehicleType, int avgDailyMileage) {
    if (avgDailyMileage > 200) {
      return 'synthetic'; // High mileage - synthetic oil
    } else if (avgDailyMileage > 100) {
      return 'semi_synthetic'; // Medium mileage - semi-synthetic
    } else {
      return 'conventional'; // Low mileage - conventional oil
    }
  }
}

// Oil Types
class OilType {
  static const String synthetic = 'synthetic';
  static const String semiSynthetic = 'semi_synthetic';
  static const String conventional = 'conventional';
  static const String diesel = 'diesel';
  static const String hybrid = 'hybrid';
}
