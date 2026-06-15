import 'dart:convert';
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class TripService extends ChangeNotifier {
  List<dynamic> _availableTrips = [];
  Map<String, dynamic>? _activeTrip;
  List<dynamic> _tripHistory = [];
  bool _isLoading = false;
  String? _error;
  StreamController<Map<String, dynamic>>? _tripUpdateController;

  List<dynamic> get availableTrips => _availableTrips;
  Map<String, dynamic>? get activeTrip => _activeTrip;
  List<dynamic> get tripHistory => _tripHistory;
  bool get isLoading => _isLoading;
  String? get error => _error;
  Stream<Map<String, dynamic>>? get tripUpdates => _tripUpdateController?.stream;

  final String _baseUrl = 'http://your-api-url.com/api';
  String? _token;

  void setToken(String token) {
    _token = token;
    _tripUpdateController = StreamController<Map<String, dynamic>>.broadcast();
  }

  // Get available trips for driver
  Future<void> fetchAvailableTrips() async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_baseUrl/trips/available'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $_token',
        },
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _availableTrips = data['trips'] ?? [];
      } else {
        _error = 'Failed to fetch trips';
      }
    } catch (e) {
      _error = 'Error: $e';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Accept a trip
  Future<bool> acceptTrip(String tripId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/trips/$tripId/accept'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $_token',
        },
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _activeTrip = data['trip'];
        _availableTrips.removeWhere((t) => t['_id'] == tripId);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Accept trip error: $e');
      return false;
    }
  }

  // Get active trip
  Future<void> fetchActiveTrip() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/trips/active'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $_token',
        },
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _activeTrip = data['trip'];
        notifyListeners();
      }
    } catch (e) {
      debugPrint('Fetch active trip error: $e');
    }
  }

  // Update trip status
  Future<bool> updateTripStatus(String tripId, String status, {Map<String, dynamic>? additionalData}) async {
    try {
      final body = {
        'status': status,
        ...?additionalData,
      };

      final response = await http.patch(
        Uri.parse('$_baseUrl/trips/$tripId/status'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $_token',
        },
        body: jsonEncode(body),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _activeTrip = data['trip'];
        _tripUpdateController?.add(_activeTrip!);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Update status error: $e');
      return false;
    }
  }

  // Complete trip
  Future<bool> completeTrip(String tripId, {
    required double finalDistance,
    required String deliveryTime,
    String? notes,
    List<String>? photoUrls,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/trips/$tripId/complete'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $_token',
        },
        body: jsonEncode({
          'finalDistance': finalDistance,
          'deliveryTime': deliveryTime,
          'notes': notes,
          'photos': photoUrls,
        }),
      );

      if (response.statusCode == 200) {
        _activeTrip = null;
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Complete trip error: $e');
      return false;
    }
  }

  // Get trip history
  Future<void> fetchTripHistory({int page = 1, int limit = 20}) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_baseUrl/trips/history?page=$page&limit=$limit'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $_token',
        },
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (page == 1) {
          _tripHistory = data['trips'] ?? [];
        } else {
          _tripHistory.addAll(data['trips'] ?? []);
        }
      }
    } catch (e) {
      debugPrint('Fetch history error: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Update driver location during trip
  Future<bool> updateLocation(String tripId, double lat, double lng, {double? speed}) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/trips/$tripId/location'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $_token',
        },
        body: jsonEncode({
          'latitude': lat,
          'longitude': lng,
          'speed': speed,
          'timestamp': DateTime.now().toIso8601String(),
        }),
      );

      return response.statusCode == 200;
    } catch (e) {
      debugPrint('Update location error: $e');
      return false;
    }
  }

  // Cancel active trip
  Future<bool> cancelTrip(String tripId, String reason) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/trips/$tripId/cancel'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $_token',
        },
        body: jsonEncode({'reason': reason}),
      );

      if (response.statusCode == 200) {
        _activeTrip = null;
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Cancel trip error: $e');
      return false;
    }
  }

  // Get trip details
  Future<Map<String, dynamic>?> getTripDetails(String tripId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/trips/$tripId'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $_token',
        },
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return null;
    } catch (e) {
      debugPrint('Get trip details error: $e');
      return null;
    }
  }

  // Get driver stats
  Future<Map<String, dynamic>?> getDriverStats() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/trips/driver-stats'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $_token',
        },
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return null;
    } catch (e) {
      debugPrint('Get stats error: $e');
      return null;
    }
  }

  @override
  void dispose() {
    _tripUpdateController?.close();
    super.dispose();
  }
}

// Trip Status Enum
class TripStatus {
  static const String pending = 'pending';
  static const String accepted = 'accepted';
  static const String driverAssigned = 'driver_assigned';
  static const String pickup = 'pickup';
  static const String inTransit = 'in_transit';
  static const String arrived = 'arrived';
  static const String delivered = 'delivered';
  static const String completed = 'completed';
  static const String cancelled = 'cancelled';
}

// Arabic status labels
Map<String, String> tripStatusLabels = {
  'pending': 'في الانتظار',
  'accepted': 'تم القبول',
  'driver_assigned': 'تم تعيين السائق',
  'pickup': 'في الاستلام',
  'in_transit': 'في الطريق',
  'arrived': 'تم الوصول',
  'delivered': 'تم التسليم',
  'completed': 'مكتمل',
  'cancelled': 'ملغي',
};

// Status colors
Map<String, Color> tripStatusColors = {
  'pending': Colors.orange,
  'accepted': Colors.blue,
  'driver_assigned': Colors.purple,
  'pickup': Colors.teal,
  'in_transit': Colors.green,
  'arrived': Colors.indigo,
  'delivered': Colors.green,
  'completed': Colors.green,
  'cancelled': Colors.red,
};
