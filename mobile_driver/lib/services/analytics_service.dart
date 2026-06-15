import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class AnalyticsService extends ChangeNotifier {
  Map<String, dynamic> _dashboardData = {};
  List<Map<String, dynamic>> _tripAnalytics = [];
  List<Map<String, dynamic>> _driverPerformance = [];
  List<Map<String, dynamic>> _revenueData = [];
  bool _isLoading = false;
  String? _error;

  Map<String, dynamic> get dashboardData => _dashboardData;
  List<Map<String, dynamic>> get tripAnalytics => _tripAnalytics;
  List<Map<String, dynamic>> get driverPerformance => _driverPerformance;
  List<Map<String, dynamic>> get revenueData => _revenueData;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Get dashboard overview
  Future<void> fetchDashboardData({
    String? startDate,
    String? endDate,
  }) async {
    try {
      _isLoading = true;
      _error = null;
      notifyListeners();

      String url = '$_apiBaseUrl/analytics/dashboard';
      if (startDate != null && endDate != null) {
        url += '?startDate=$startDate&endDate=$endDate';
      }

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        _dashboardData = jsonDecode(response.body);
      } else {
        _error = 'Failed to fetch dashboard data';
      }
    } catch (e) {
      _error = 'Error: $e';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get trip analytics
  Future<void> fetchTripAnalytics({
    String period = 'week', // day, week, month, year
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/analytics/trips?period=$period'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _tripAnalytics = List<Map<String, dynamic>>.from(data['trips'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching trip analytics: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get driver performance
  Future<void> fetchDriverPerformance({
    String? driverId,
    String period = 'month',
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      String url = '$_apiBaseUrl/analytics/drivers?period=$period';
      if (driverId != null) {
        url += '&driverId=$driverId';
      }

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _driverPerformance = List<Map<String, dynamic>>.from(data['drivers'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching driver performance: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get revenue analytics
  Future<void> fetchRevenueAnalytics({
    String period = 'month',
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/analytics/revenue?period=$period'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _revenueData = List<Map<String, dynamic>>.from(data['revenue'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching revenue analytics: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get daily trip counts
  Future<List<Map<String, int>>> getDailyTripCounts({
    int days = 30,
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/analytics/daily-trips?days=$days'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, int>>.from(data['counts'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching daily trip counts: $e');
      return [];
    }
  }

  // Get average delivery time
  Future<Map<String, dynamic>> getAverageDeliveryTime({
    String period = 'week',
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/analytics/avg-delivery-time?period=$period'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching average delivery time: $e');
      return {};
    }
  }

  // Get top performing drivers
  Future<List<Map<String, dynamic>>> getTopDrivers({
    int limit = 10,
    String period = 'month',
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/analytics/top-drivers?limit=$limit&period=$period'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['drivers'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching top drivers: $e');
      return [];
    }
  }

  // Get fleet utilization
  Future<Map<String, dynamic>> getFleetUtilization({
    String period = 'week',
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/analytics/fleet-utilization?period=$period'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching fleet utilization: $e');
      return {};
    }
  }

  // Get revenue by region
  Future<List<Map<String, dynamic>>> getRevenueByRegion({
    String period = 'month',
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/analytics/revenue-by-region?period=$period'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['regions'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching revenue by region: $e');
      return [];
    }
  }

  // Generate custom report
  Future<Map<String, dynamic>> generateCustomReport({
    required String reportType,
    required Map<String, dynamic> filters,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/analytics/custom-report'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'reportType': reportType,
          'filters': filters,
        }),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error generating custom report: $e');
      return {};
    }
  }

  // Export report
  Future<String?> exportReport({
    required String reportId,
    required String format, // pdf, excel, csv
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/analytics/export/$reportId?format=$format'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['downloadUrl'];
      }
      return null;
    } catch (e) {
      debugPrint('Error exporting report: $e');
      return null;
    }
  }

  // Get real-time stats
  Stream<Map<String, dynamic>> getRealTimeStats() {
    // This would typically use WebSocket or Server-Sent Events
    // For now, returning a mock stream
    return Stream.periodic(
      const Duration(seconds: 5),
      (_) => {
        'activeTrips': 15,
        'activeDrivers': 12,
        'pendingRequests': 5,
        'todayRevenue': 3500.0,
      },
    );
  }

  // Calculate key metrics locally
  Map<String, dynamic> calculateKeyMetrics(List<Map<String, dynamic>> trips) {
    if (trips.isEmpty) {
      return {
        'totalTrips': 0,
        'totalRevenue': 0.0,
        'avgDeliveryTime': 0.0,
        'onTimeDeliveryRate': 0.0,
        'customerSatisfaction': 0.0,
      };
    }

    final totalTrips = trips.length;
    final totalRevenue = trips.fold<double>(
      0.0,
      (sum, trip) => sum + (trip['price'] ?? 0.0),
    );

    final deliveryTimes = trips
        .map((t) => t['deliveryTime'] ?? 0)
        .where((t) => t > 0)
        .toList();
    final avgDeliveryTime = deliveryTimes.isEmpty
        ? 0.0
        : deliveryTimes.reduce((a, b) => a + b) / deliveryTimes.length;

    final onTimeDeliveries = trips.where((t) => t['onTime'] == true).length;
    final onTimeDeliveryRate = totalTrips > 0
        ? (onTimeDeliveries / totalTrips) * 100
        : 0.0;

    final ratings = trips
        .map((t) => t['rating'] ?? 0.0)
        .where((r) => r > 0)
        .toList();
    final customerSatisfaction = ratings.isEmpty
        ? 0.0
        : ratings.reduce((a, b) => a + b) / ratings.length;

    return {
      'totalTrips': totalTrips,
      'totalRevenue': totalRevenue,
      'avgDeliveryTime': avgDeliveryTime,
      'onTimeDeliveryRate': onTimeDeliveryRate,
      'customerSatisfaction': customerSatisfaction,
    };
  }

  // Get trends data for charts
  Map<String, List<Map<String, dynamic>>> getChartData() {
    return {
      'dailyTrips': [
        {'date': '2026-04-25', 'count': 12},
        {'date': '2026-04-26', 'count': 15},
        {'date': '2026-04-27', 'count': 18},
        {'date': '2026-04-28', 'count': 14},
        {'date': '2026-04-29', 'count': 20},
        {'date': '2026-04-30', 'count': 22},
        {'date': '2026-05-01', 'count': 25},
      ],
      'revenue': [
        {'date': '2026-04-25', 'amount': 1500},
        {'date': '2026-04-26', 'amount': 1800},
        {'date': '2026-04-27', 'amount': 2100},
        {'date': '2026-04-28', 'amount': 1650},
        {'date': '2026-04-29', 'amount': 2400},
        {'date': '2026-04-30', 'amount': 2700},
        {'date': '2026-05-01', 'amount': 3500},
      ],
      'driverPerformance': [
        {'driver': 'أحمد', 'trips': 45, 'rating': 4.8},
        {'driver': 'محمد', 'trips': 42, 'rating': 4.7},
        {'driver': 'عبدالله', 'trips': 38, 'rating': 4.9},
        {'driver': 'سعود', 'trips': 35, 'rating': 4.6},
        {'driver': 'خالد', 'trips': 32, 'rating': 4.5},
      ],
    };
  }
}
