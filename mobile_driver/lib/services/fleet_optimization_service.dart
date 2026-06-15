import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'dart:math' as math;

class FleetOptimizationService extends ChangeNotifier {
  List<Map<String, dynamic>> _optimizedRoutes = [];
  Map<String, dynamic>? _fleetStats;
  List<Map<String, dynamic>> _fuelRecommendations = [];
  bool _isLoading = false;
  String? _error;

  List<Map<String, dynamic>> get optimizedRoutes => _optimizedRoutes;
  Map<String, dynamic>? get fleetStats => _fleetStats;
  List<Map<String, dynamic>> get fuelRecommendations => _fuelRecommendations;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Optimize fleet assignment
  Future<List<Map<String, dynamic>>> optimizeFleetAssignment({
    required List<Map<String, dynamic>> trips,
    required List<Map<String, dynamic>> availableVehicles,
    required List<Map<String, dynamic>> availableDrivers,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/fleet/optimize-assignment'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'trips': trips,
          'vehicles': availableVehicles,
          'drivers': availableDrivers,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _optimizedRoutes = List<Map<String, dynamic>>.from(data['assignments'] ?? []);
        return _optimizedRoutes;
      }
      return [];
    } catch (e) {
      debugPrint('Error optimizing fleet assignment: $e');
      return [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get fleet statistics
  Future<void> fetchFleetStats({
    String? startDate,
    String? endDate,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      String url = '$_apiBaseUrl/fleet/stats';
      if (startDate != null) url += '?startDate=$startDate';
      if (endDate != null) url += '&endDate=$endDate';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        _fleetStats = jsonDecode(response.body);
      }
    } catch (e) {
      debugPrint('Error fetching fleet stats: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Calculate optimal route for multiple stops
  Future<List<Map<String, dynamic>>> calculateOptimalRoute({
    required List<Map<String, dynamic>> stops,
    String? startLocation,
    String? endLocation,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/fleet/optimal-route'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'stops': stops,
          'startLocation': startLocation,
          'endLocation': endLocation,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['route'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error calculating optimal route: $e');
      return [];
    }
  }

  // Get fuel efficiency recommendations
  Future<void> fetchFuelRecommendations() async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/fleet/fuel-recommendations'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _fuelRecommendations = List<Map<String, dynamic>>.from(data['recommendations'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching fuel recommendations: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Predict maintenance needs
  Future<List<Map<String, dynamic>>> predictMaintenanceNeeds({
    required String vehicleId,
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/fleet/vehicles/$vehicleId/maintenance-prediction'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['predictions'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error predicting maintenance needs: $e');
      return [];
    }
  }

  // Calculate fuel consumption
  double calculateFuelConsumption({
    required double distance,
    required double fuelEfficiency, // km per liter
    double? trafficFactor,
  }) {
    final baseConsumption = distance / fuelEfficiency;
    final trafficAdjustment = trafficFactor ?? 1.0;
    return baseConsumption * trafficAdjustment;
  }

  // Optimize fuel usage
  Map<String, dynamic> optimizeFuelUsage({
    required List<Map<String, dynamic>> trips,
    required Map<String, double> vehicleFuelEfficiency,
  }) {
    double totalFuel = 0;
    Map<String, dynamic> vehicleAssignments = {};

    for (var trip in trips) {
      final distance = trip['distance'] ?? 0.0;
      final bestVehicle = _findBestVehicleForTrip(
        trip,
        vehicleFuelEfficiency,
      );

      if (bestVehicle != null) {
        final fuelUsed = calculateFuelConsumption(
          distance: distance,
          fuelEfficiency: vehicleFuelEfficiency[bestVehicle] ?? 10.0,
        );
        totalFuel += fuelUsed;

        vehicleAssignments[bestVehicle] = (vehicleAssignments[bestVehicle] ?? 0) + fuelUsed;
      }
    }

    return {
      'totalFuelConsumption': totalFuel,
      'vehicleAssignments': vehicleAssignments,
      'estimatedCost': totalFuel * 2.5, // Assuming 2.5 SAR per liter
    };
  }

  String? _findBestVehicleForTrip(
    Map<String, dynamic> trip,
    Map<String, double> vehicleFuelEfficiency,
  ) {
    String? bestVehicle;
    double bestEfficiency = 0;

    vehicleFuelEfficiency.forEach((vehicle, efficiency) {
      if (efficiency > bestEfficiency) {
        bestEfficiency = efficiency;
        bestVehicle = vehicle;
      }
    });

    return bestVehicle;
  }

  // Get vehicle utilization rate
  double calculateUtilizationRate({
    required int totalVehicles,
    required int activeVehicles,
  }) {
    if (totalVehicles == 0) return 0.0;
    return (activeVehicles / totalVehicles) * 100;
  }

  // Optimize driver schedules
  Future<List<Map<String, dynamic>>> optimizeDriverSchedules({
    required List<Map<String, dynamic>> drivers,
    required List<Map<String, dynamic>> trips,
    int maxHoursPerDay = 8,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/fleet/optimize-schedules'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'drivers': drivers,
          'trips': trips,
          'maxHoursPerDay': maxHoursPerDay,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['schedules'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error optimizing driver schedules: $e');
      return [];
    }
  }

  // Get fleet performance metrics
  Future<Map<String, dynamic>> getFleetPerformanceMetrics({
    String period = 'month',
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/fleet/performance?period=$period'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching fleet performance: $e');
      return {};
    }
  }

  // Suggest vehicle replacement
  Future<List<Map<String, dynamic>>> suggestVehicleReplacement() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/fleet/replacement-suggestions'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['suggestions'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error getting replacement suggestions: $e');
      return [];
    }
  }

  // Calculate cost savings from optimization
  Map<String, dynamic> calculateCostSavings({
    required double currentFuelCost,
    required double optimizedFuelCost,
    required double currentMaintenanceCost,
    required double optimizedMaintenanceCost,
  }) {
    final fuelSavings = currentFuelCost - optimizedFuelCost;
    final maintenanceSavings = currentMaintenanceCost - optimizedMaintenanceCost;
    final totalSavings = fuelSavings + maintenanceSavings;
    final savingsPercentage = (totalSavings / (currentFuelCost + currentMaintenanceCost)) * 100;

    return {
      'fuelSavings': fuelSavings,
      'maintenanceSavings': maintenanceSavings,
      'totalSavings': totalSavings,
      'savingsPercentage': savingsPercentage,
    };
  }

  // Get real-time fleet status
  Stream<Map<String, dynamic>> getRealTimeFleetStatus() {
    // This would typically use WebSocket
    return Stream.periodic(
      const Duration(seconds: 10),
      (_) => {
        'activeVehicles': 15,
        'idleVehicles': 5,
        'maintenanceVehicles': 2,
        'totalDistance': 12500.5,
        'averageSpeed': 45.2,
      },
    );
  }

  // Analyze route efficiency
  Map<String, dynamic> analyzeRouteEfficiency({
    required List<Map<String, dynamic>> completedRoutes,
  }) {
    if (completedRoutes.isEmpty) {
      return {
        'averageEfficiency': 0.0,
        'bestRoute': null,
        'worstRoute': null,
        'improvementSuggestions': [],
      };
    }

    double totalEfficiency = 0;
    Map<String, dynamic>? bestRoute;
    Map<String, dynamic>? worstRoute;
    double bestEfficiency = 0;
    double worstEfficiency = double.infinity;

    for (var route in completedRoutes) {
      final plannedDistance = route['plannedDistance'] ?? 0.0;
      final actualDistance = route['actualDistance'] ?? 0.0;
      final plannedTime = route['plannedTime'] ?? 0.0;
      final actualTime = route['actualTime'] ?? 0.0;

      final distanceEfficiency = plannedDistance / actualDistance;
      final timeEfficiency = plannedTime / actualTime;
      final routeEfficiency = (distanceEfficiency + timeEfficiency) / 2;

      totalEfficiency += routeEfficiency;

      if (routeEfficiency > bestEfficiency) {
        bestEfficiency = routeEfficiency;
        bestRoute = route;
      }

      if (routeEfficiency < worstEfficiency) {
        worstEfficiency = routeEfficiency;
        worstRoute = route;
      }
    }

    final averageEfficiency = totalEfficiency / completedRoutes.length;

    return {
      'averageEfficiency': averageEfficiency,
      'bestRoute': bestRoute,
      'worstRoute': worstRoute,
      'improvementSuggestions': _generateImprovementSuggestions(averageEfficiency),
    };
  }

  List<String> _generateImprovementSuggestions(double efficiency) {
    final suggestions = <String>[];

    if (efficiency < 0.7) {
      suggestions.add('إعادة تقييم المسارات الحالية');
      suggestions.add('تدريب السائقين على القيادة الفعالة');
      suggestions.add('مراجعة أوقات الذروة');
    } else if (efficiency < 0.85) {
      suggestions.add('تحسين توزيع الرحلات');
      suggestions.add('استخدام تكنولوجيا التتبع المتقدم');
    } else {
      suggestions.add('الحفاظ على الأداء الحالي');
      suggestions.add('استمرار في مراقبة الكفاءة');
    }

    return suggestions;
  }
}
