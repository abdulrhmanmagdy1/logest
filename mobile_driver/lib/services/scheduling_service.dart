import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class SchedulingService extends ChangeNotifier {
  List<Map<String, dynamic>> _scheduledTrips = [];
  List<Map<String, dynamic>> _availableSlots = [];
  bool _isLoading = false;
  String? _error;

  List<Map<String, dynamic>> get scheduledTrips => _scheduledTrips;
  List<Map<String, dynamic>> get availableSlots => _availableSlots;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Get scheduled trips
  Future<void> fetchScheduledTrips({
    String? startDate,
    String? endDate,
    String? driverId,
  }) async {
    try {
      _isLoading = true;
      _error = null;
      notifyListeners();

      String url = '$_apiBaseUrl/scheduling/trips';
      if (startDate != null) url += '?startDate=$startDate';
      if (endDate != null) url += '&endDate=$endDate';
      if (driverId != null) url += '&driverId=$driverId';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _scheduledTrips = List<Map<String, dynamic>>.from(data['trips'] ?? []);
      } else {
        _error = 'Failed to fetch scheduled trips';
      }
    } catch (e) {
      _error = 'Error: $e';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Create scheduled trip
  Future<bool> createScheduledTrip(Map<String, dynamic> tripData) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/scheduling/trips'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(tripData),
      );

      if (response.statusCode == 201) {
        await fetchScheduledTrips();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error creating scheduled trip: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Update scheduled trip
  Future<bool> updateScheduledTrip(
    String tripId,
    Map<String, dynamic> tripData,
  ) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.put(
        Uri.parse('$_apiBaseUrl/scheduling/trips/$tripId'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(tripData),
      );

      if (response.statusCode == 200) {
        await fetchScheduledTrips();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating scheduled trip: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Cancel scheduled trip
  Future<bool> cancelScheduledTrip(String tripId, String reason) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/scheduling/trips/$tripId/cancel'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'reason': reason}),
      );

      if (response.statusCode == 200) {
        _scheduledTrips.removeWhere((t) => t['_id'] == tripId);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error cancelling scheduled trip: $e');
      return false;
    }
  }

  // Get available time slots
  Future<void> getAvailableSlots({
    required String date,
    String? vehicleId,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      String url = '$_apiBaseUrl/scheduling/available-slots?date=$date';
      if (vehicleId != null) url += '&vehicleId=$vehicleId';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _availableSlots = List<Map<String, dynamic>>.from(data['slots'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching available slots: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Check if time slot is available
  Future<bool> isSlotAvailable({
    required String date,
    required String startTime,
    required String endTime,
    String? excludeTripId,
  }) async {
    try {
      String url = '$_apiBaseUrl/scheduling/check-availability?'
          'date=$date&startTime=$startTime&endTime=$endTime';
      if (excludeTripId != null) url += '&excludeTripId=$excludeTripId';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['available'] ?? false;
      }
      return false;
    } catch (e) {
      debugPrint('Error checking slot availability: $e');
      return false;
    }
  }

  // Optimize schedule for driver
  Future<List<Map<String, dynamic>>> optimizeSchedule({
    required String driverId,
    required String date,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/scheduling/optimize'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'driverId': driverId,
          'date': date,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['optimizedSchedule'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error optimizing schedule: $e');
      return [];
    }
  }

  // Get fleet availability
  Future<Map<String, dynamic>> getFleetAvailability({
    required String date,
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/scheduling/fleet-availability?date=$date'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching fleet availability: $e');
      return {};
    }
  }

  // Assign driver to scheduled trip
  Future<bool> assignDriver({
    required String tripId,
    required String driverId,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/scheduling/trips/$tripId/assign'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'driverId': driverId}),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error assigning driver: $e');
      return false;
    }
  }

  // Get driver schedule
  Future<List<Map<String, dynamic>>> getDriverSchedule({
    required String driverId,
    String? startDate,
    String? endDate,
  }) async {
    try {
      String url = '$_apiBaseUrl/scheduling/driver/$driverId/schedule';
      if (startDate != null) url += '?startDate=$startDate';
      if (endDate != null) url += '&endDate=$endDate';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['schedule'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching driver schedule: $e');
      return [];
    }
  }

  // Get recurring schedule
  Future<List<Map<String, dynamic>>> getRecurringSchedule({
    String? customerId,
  }) async {
    try {
      String url = '$_apiBaseUrl/scheduling/recurring';
      if (customerId != null) url += '?customerId=$customerId';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['recurring'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching recurring schedule: $e');
      return [];
    }
  }

  // Create recurring schedule
  Future<bool> createRecurringSchedule(Map<String, dynamic> scheduleData) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/scheduling/recurring'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(scheduleData),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error creating recurring schedule: $e');
      return false;
    }
  }

  // Get schedule conflicts
  Future<List<Map<String, dynamic>>> getScheduleConflicts({
    required String date,
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/scheduling/conflicts?date=$date'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['conflicts'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching schedule conflicts: $e');
      return [];
    }
  }

  // Resolve schedule conflict
  Future<bool> resolveConflict({
    required String conflictId,
    required String resolution,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/scheduling/conflicts/$conflictId/resolve'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'resolution': resolution}),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error resolving conflict: $e');
      return false;
    }
  }

  // Get schedule statistics
  Future<Map<String, dynamic>> getScheduleStats({
    String? startDate,
    String? endDate,
  }) async {
    try {
      String url = '$_apiBaseUrl/scheduling/stats';
      if (startDate != null) url += '?startDate=$startDate';
      if (endDate != null) url += '&endDate=$endDate';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching schedule stats: $e');
      return {};
    }
  }
}
