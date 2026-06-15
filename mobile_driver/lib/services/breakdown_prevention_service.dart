import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class BreakdownPrediction {
  final String vehicleId;
  final String vehicleNumber;
  final String component; // engine, transmission, brake, tire, etc.
  final String issueType;
  final double probability; // 0-1
  final DateTime predictedDate;
  final String severity; // low, medium, high, critical
  final String? recommendedAction;
  final double? estimatedCost;
  final String? notes;

  BreakdownPrediction({
    required this.vehicleId,
    required this.vehicleNumber,
    required this.component,
    required this.issueType,
    required this.probability,
    required this.predictedDate,
    required this.severity,
    this.recommendedAction,
    this.estimatedCost,
    this.notes,
  });

  factory BreakdownPrediction.fromJson(Map<String, dynamic> json) {
    return BreakdownPrediction(
      vehicleId: json['vehicleId'] ?? '',
      vehicleNumber: json['vehicleNumber'] ?? '',
      component: json['component'] ?? '',
      issueType: json['issueType'] ?? '',
      probability: (json['probability'] ?? 0.0).toDouble(),
      predictedDate: DateTime.parse(json['predictedDate'] ?? DateTime.now().toIso8601String()),
      severity: json['severity'] ?? 'low',
      recommendedAction: json['recommendedAction'],
      estimatedCost: json['estimatedCost']?.toDouble(),
      notes: json['notes'],
    );
  }
}

class BreakdownPreventionService extends ChangeNotifier {
  List<BreakdownPrediction> _predictions = [];
  List<BreakdownPrediction> _highRiskPredictions = [];
  Map<String, dynamic>? _fleetHealthScore;
  List<Map<String, dynamic>> _breakdownHistory = [];
  bool _isLoading = false;
  String? _error;

  List<BreakdownPrediction> get predictions => _predictions;
  List<BreakdownPrediction> get highRiskPredictions => _highRiskPredictions;
  Map<String, dynamic>? get fleetHealthScore => _fleetHealthScore;
  List<Map<String, dynamic>> get breakdownHistory => _breakdownHistory;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Fetch breakdown predictions
  Future<void> fetchPredictions({
    String? vehicleId,
    String? severity,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      String url = '$_apiBaseUrl/breakdown-prevention/predictions';
      if (vehicleId != null) url += '?vehicleId=$vehicleId';
      if (severity != null) url += '&severity=$severity';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _predictions = (data['predictions'] as List)
            .map((p) => BreakdownPrediction.fromJson(p))
            .toList()
          ..sort((a, b) => b.probability.compareTo(a.probability));
        
        _highRiskPredictions = _predictions
            .where((p) => p.severity == 'high' || p.severity == 'critical')
            .toList();
      }
    } catch (e) {
      debugPrint('Error fetching predictions: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Generate prediction for vehicle
  Future<BreakdownPrediction?> generatePrediction(String vehicleId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/predict'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'vehicleId': vehicleId}),
      );

      if (response.statusCode == 201) {
        final data = jsonDecode(response.body);
        return BreakdownPrediction.fromJson(data);
      }
      return null;
    } catch (e) {
      debugPrint('Error generating prediction: $e');
      return null;
    }
  }

  // Get fleet health score
  Future<void> fetchFleetHealthScore() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/fleet-health'),
      );

      if (response.statusCode == 200) {
        _fleetHealthScore = jsonDecode(response.body);
      }
    } catch (e) {
      debugPrint('Error fetching fleet health score: $e');
    }
  }

  // Get breakdown history
  Future<void> fetchBreakdownHistory({
    String? vehicleId,
    DateTime? startDate,
    DateTime? endDate,
  }) async {
    try {
      String url = '$_apiBaseUrl/breakdown-prevention/history';
      if (vehicleId != null) url += '?vehicleId=$vehicleId';
      if (startDate != null) url += '&startDate=${startDate.toIso8601String()}';
      if (endDate != null) url += '&endDate=${endDate.toIso8601String()}';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _breakdownHistory = List<Map<String, dynamic>>.from(data['history'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching breakdown history: $e');
    }
  }

  // Record breakdown
  Future<bool> recordBreakdown({
    required String vehicleId,
    required String component,
    required String issueType,
    required String description,
    required DateTime occurredAt,
    required double repairCost,
    int? downtimeHours,
    String? rootCause,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/record'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'vehicleId': vehicleId,
          'component': component,
          'issueType': issueType,
          'description': description,
          'occurredAt': occurredAt.toIso8601String(),
          'repairCost': repairCost,
          'downtimeHours': downtimeHours,
          'rootCause': rootCause,
        }),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error recording breakdown: $e');
      return false;
    }
  }

  // Get preventive maintenance recommendations
  Future<List<Map<String, dynamic>>> getPreventiveRecommendations(String vehicleId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/recommendations?vehicleId=$vehicleId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['recommendations'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching preventive recommendations: $e');
      return [];
    }
  }

  // Get component health analysis
  Future<Map<String, dynamic>> getComponentHealthAnalysis(String vehicleId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/component-health?vehicleId=$vehicleId'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching component health analysis: $e');
      return {};
    }
  }

  // Calculate MTBF (Mean Time Between Failures)
  Future<double> calculateMTBF(String vehicleId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/mtbf?vehicleId=$vehicleId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return (data['mtbf'] ?? 0.0).toDouble();
      }
      return 0.0;
    } catch (e) {
      debugPrint('Error calculating MTBF: $e');
      return 0.0;
    }
  }

  // Get breakdown trends
  Future<Map<String, dynamic>> getBreakdownTrends({
    String period = 'year',
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/trends?period=$period'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching breakdown trends: $e');
      return {};
    }
  }

  // Get cost savings from prevention
  Future<Map<String, dynamic>> getCostSavingsFromPrevention({
    String period = 'year',
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/cost-savings?period=$period'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching cost savings: $e');
      return {};
    }
  }

  // Schedule preventive maintenance based on prediction
  Future<bool> schedulePreventiveMaintenance({
    required String vehicleId,
    required String component,
    required DateTime scheduledDate,
    String? notes,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/schedule-preventive'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'vehicleId': vehicleId,
          'component': component,
          'scheduledDate': scheduledDate.toIso8601String(),
          'notes': notes,
        }),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error scheduling preventive maintenance: $e');
      return false;
    }
  }

  // Get risk factors
  Future<List<Map<String, dynamic>>> getRiskFactors(String vehicleId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/risk-factors?vehicleId=$vehicleId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['factors'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching risk factors: $e');
      return [];
    }
  }

  // Update prediction accuracy
  Future<bool> updatePredictionAccuracy({
    required String predictionId,
    required bool wasAccurate,
    String? actualOutcome,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/predictions/$predictionId/accuracy'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'wasAccurate': wasAccurate,
          'actualOutcome': actualOutcome,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating prediction accuracy: $e');
      return false;
    }
  }

  // Get prevention strategies
  Future<List<Map<String, dynamic>>> getPreventionStrategies() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/breakdown-prevention/strategies'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['strategies'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching prevention strategies: $e');
      return [];
    }
  }
}

// Component Types
class ComponentType {
  static const String engine = 'engine';
  static const String transmission = 'transmission';
  static const String brake = 'brake';
  static const String tire = 'tire';
  static const String electrical = 'electrical';
  static const String cooling = 'cooling';
  static const String suspension = 'suspension';
  static const String exhaust = 'exhaust';
  static const String fuel = 'fuel';
  static const String battery = 'battery';
}

// Issue Types
class IssueType {
  static const String wear = 'wear';
  static const String failure = 'failure';
  static const String leak = 'leak';
  static const String overheating = 'overheating';
  static const String vibration = 'vibration';
  static const String noise = 'noise';
  static const String performance = 'performance';
}

// Severity Levels
class Severity {
  static const String low = 'low';
  static const String medium = 'medium';
  static const String high = 'high';
  static const String critical = 'critical';
}
