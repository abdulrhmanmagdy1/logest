import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class DailyReportService extends ChangeNotifier {
  Map<String, dynamic>? _todayReport;
  List<Map<String, dynamic>> _weeklyReports = [];
  List<Map<String, dynamic>> _monthlyReports = [];
  bool _isLoading = false;
  String? _error;

  Map<String, dynamic>? get todayReport => _todayReport;
  List<Map<String, dynamic>> get weeklyReports => _weeklyReports;
  List<Map<String, dynamic>> get monthlyReports => _monthlyReports;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Get today's report
  Future<void> fetchTodayReport(String driverId) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/daily-report/today'),
      );

      if (response.statusCode == 200) {
        _todayReport = jsonDecode(response.body);
      }
    } catch (e) {
      debugPrint('Error fetching today report: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get weekly reports
  Future<void> fetchWeeklyReports(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/daily-report/weekly'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _weeklyReports = List<Map<String, dynamic>>.from(data['reports'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching weekly reports: $e');
    }
  }

  // Get monthly reports
  Future<void> fetchMonthlyReports(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/daily-report/monthly'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _monthlyReports = List<Map<String, dynamic>>.from(data['reports'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching monthly reports: $e');
    }
  }

  // Generate daily report
  Future<Map<String, dynamic>?> generateDailyReport({
    required String driverId,
    required DateTime date,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/driver/$driverId/daily-report/generate'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'date': date.toIso8601String(),
        }),
      );

      if (response.statusCode == 201) {
        return jsonDecode(response.body);
      }
      return null;
    } catch (e) {
      debugPrint('Error generating daily report: $e');
      return null;
    }
  }

  // Export report
  Future<String?> exportReport({
    required String reportId,
    required String format, // pdf, excel
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/daily-reports/$reportId/export?format=$format'),
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

  // Get report summary
  Map<String, dynamic> getReportSummary(Map<String, dynamic> report) {
    return {
      'totalTrips': report['totalTrips'] ?? 0,
      'totalDistance': report['totalDistance'] ?? 0.0,
      'totalEarnings': report['totalEarnings'] ?? 0.0,
      'totalHours': report['totalHours'] ?? 0.0,
      'fuelCost': report['fuelCost'] ?? 0.0,
      'netEarnings': (report['totalEarnings'] ?? 0.0) - (report['fuelCost'] ?? 0.0),
      'averageRating': report['averageRating'] ?? 0.0,
      'onTimeDeliveries': report['onTimeDeliveries'] ?? 0,
      'completedTrips': report['completedTrips'] ?? 0,
    };
  }

  // Get performance comparison
  Future<Map<String, dynamic>> getPerformanceComparison({
    required String driverId,
    required String period, // week, month
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/performance-comparison?period=$period'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching performance comparison: $e');
      return {};
    }
  }

  // Get trip breakdown
  Future<List<Map<String, dynamic>>> getTripBreakdown(String reportId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/daily-reports/$reportId/trip-breakdown'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['trips'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching trip breakdown: $e');
      return [];
    }
  }

  // Add note to report
  Future<bool> addReportNote({
    required String reportId,
    required String note,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/daily-reports/$reportId/notes'),
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
      debugPrint('Error adding report note: $e');
      return false;
    }
  }

  // Get fuel efficiency
  double calculateFuelEfficiency({
    required double distance,
    required double fuelConsumed,
  }) {
    if (fuelConsumed == 0) return 0.0;
    return distance / fuelConsumed; // km per liter
  }

  // Get earnings per hour
  double calculateEarningsPerHour({
    required double earnings,
    required double hours,
  }) {
    if (hours == 0) return 0.0;
    return earnings / hours;
  }

  // Get earnings per km
  double calculateEarningsPerKm({
    required double earnings,
    required double distance,
  }) {
    if (distance == 0) return 0.0;
    return earnings / distance;
  }
}
