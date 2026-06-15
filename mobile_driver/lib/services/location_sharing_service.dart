import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:geolocator/geolocator.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class LocationSharingService extends ChangeNotifier {
  Map<String, dynamic>? _activeSharing;
  List<Map<String, dynamic>> _sharingHistory = [];
  Timer? _locationUpdateTimer;
  bool _isSharing = false;

  Map<String, dynamic>? get activeSharing => _activeSharing;
  List<Map<String, dynamic>> get sharingHistory => _sharingHistory;
  bool get isSharing => _isSharing;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Start sharing location with customer
  Future<bool> startSharing({
    required String tripId,
    required String customerId,
    required String customerPhone,
    required String customerEmail,
    Duration? duration,
  }) async {
    try {
      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
      );

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/location-sharing/start'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'tripId': tripId,
          'customerId': customerId,
          'customerPhone': customerPhone,
          'customerEmail': customerEmail,
          'initialLatitude': position.latitude,
          'initialLongitude': position.longitude,
          'startedAt': DateTime.now().toIso8601String(),
          'expiresAt': duration != null
              ? DateTime.now().add(duration).toIso8601String()
              : null,
        }),
      );

      if (response.statusCode == 201) {
        final data = jsonDecode(response.body);
        _activeSharing = data;
        _isSharing = true;
        
        // Start periodic location updates
        _startLocationUpdates(tripId);
        
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error starting location sharing: $e');
      return false;
    }
  }

  // Stop sharing location
  Future<bool> stopSharing(String sharingId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/location-sharing/$sharingId/stop'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'stoppedAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 200) {
        _isSharing = false;
        _activeSharing = null;
        _locationUpdateTimer?.cancel();
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error stopping location sharing: $e');
      return false;
    }
  }

  // Start periodic location updates
  void _startLocationUpdates(String tripId) {
    _locationUpdateTimer?.cancel();
    _locationUpdateTimer = Timer.periodic(
      const Duration(seconds: 30),
      (_) async {
        if (!_isSharing) return;

        try {
          final position = await Geolocator.getCurrentPosition(
            desiredAccuracy: LocationAccuracy.high,
          );

          await http.post(
            Uri.parse('$_apiBaseUrl/location-sharing/update'),
            headers: {'Content-Type': 'application/json'},
            body: jsonEncode({
              'tripId': tripId,
              'latitude': position.latitude,
              'longitude': position.longitude,
              'speed': position.speed,
              'heading': position.heading,
              'timestamp': DateTime.now().toIso8601String(),
            }),
          );
        } catch (e) {
          debugPrint('Error updating location: $e');
        }
      },
    );
  }

  // Get sharing link
  Future<String?> getSharingLink(String sharingId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/location-sharing/$sharingId/link'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['link'];
      }
      return null;
    } catch (e) {
      debugPrint('Error getting sharing link: $e');
      return null;
    }
  }

  // Generate ETA sharing
  Future<Map<String, dynamic>?> generateETASharing({
    required String tripId,
    required String destination,
    required double distance,
    required double estimatedTime,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/location-sharing/eta'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'tripId': tripId,
          'destination': destination,
          'distance': distance,
          'estimatedTime': estimatedTime,
        }),
      );

      if (response.statusCode == 201) {
        return jsonDecode(response.body);
      }
      return null;
    } catch (e) {
      debugPrint('Error generating ETA sharing: $e');
      return null;
    }
  }

  // Share via SMS
  Future<bool> shareViaSMS({
    required String phoneNumber,
    required String message,
    String? link,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/location-sharing/sms'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'phoneNumber': phoneNumber,
          'message': message,
          'link': link,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error sharing via SMS: $e');
      return false;
    }
  }

  // Share via WhatsApp
  Future<bool> shareViaWhatsApp({
    required String phoneNumber,
    required String message,
    String? link,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/location-sharing/whatsapp'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'phoneNumber': phoneNumber,
          'message': message,
          'link': link,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error sharing via WhatsApp: $e');
      return false;
    }
  }

  // Get sharing history
  Future<void> fetchSharingHistory(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/location-sharing/history?driverId=$driverId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _sharingHistory = List<Map<String, dynamic>>.from(data['history'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching sharing history: $e');
    }
  }

  // Check if sharing is still active
  Future<bool> checkSharingStatus(String sharingId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/location-sharing/$sharingId/status'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['isActive'] ?? false;
      }
      return false;
    } catch (e) {
      debugPrint('Error checking sharing status: $e');
      return false;
    }
  }

  // Get remaining time
  Duration? getRemainingTime() {
    if (_activeSharing == null) return null;
    
    final expiresAt = _activeSharing!['expiresAt'];
    if (expiresAt == null) return null;
    
    final expiryDate = DateTime.parse(expiresAt);
    final remaining = expiryDate.difference(DateTime.now());
    
    if (remaining.isNegative) return Duration.zero;
    return remaining;
  }

  @override
  void dispose() {
    _locationUpdateTimer?.cancel();
    super.dispose();
  }
}
