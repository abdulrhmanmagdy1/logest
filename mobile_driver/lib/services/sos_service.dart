import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:geolocator/geolocator.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class SOSService extends ChangeNotifier {
  bool _isEmergencyActive = false;
  String? _currentEmergencyId;
  DateTime? _emergencyStartTime;
  List<Map<String, dynamic>> _emergencyContacts = [];
  List<Map<String, dynamic>> _emergencyHistory = [];
  Timer? _emergencyTimer;

  bool get isEmergencyActive => _isEmergencyActive;
  String? get currentEmergencyId => _currentEmergencyId;
  DateTime? get emergencyStartTime => _emergencyStartTime;
  List<Map<String, dynamic>> get emergencyContacts => _emergencyContacts;
  List<Map<String, dynamic>> get emergencyHistory => _emergencyHistory;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Initialize SOS service
  Future<void> initialize() async {
    await _loadEmergencyContacts();
    await _loadEmergencyHistory();
  }

  // Trigger SOS emergency
  Future<bool> triggerSOS({
    required String driverId,
    required String emergencyType,
    String? description,
    List<String>? additionalContacts,
  }) async {
    try {
      // Get current location
      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
      );

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/sos/trigger'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'driverId': driverId,
          'emergencyType': emergencyType,
          'description': description,
          'latitude': position.latitude,
          'longitude': position.longitude,
          'timestamp': DateTime.now().toIso8601String(),
          'additionalContacts': additionalContacts,
        }),
      );

      if (response.statusCode == 201) {
        final data = jsonDecode(response.body);
        _currentEmergencyId = data['emergencyId'];
        _emergencyStartTime = DateTime.now();
        _isEmergencyActive = true;
        
        // Start emergency timer
        _startEmergencyTimer();
        
        // Notify emergency contacts
        await _notifyEmergencyContacts(position);
        
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error triggering SOS: $e');
      return false;
    }
  }

  // Cancel SOS
  Future<bool> cancelSOS(String reason) async {
    try {
      if (_currentEmergencyId == null) return false;

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/sos/$_currentEmergencyId/cancel'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'reason': reason,
          'cancelledAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 200) {
        _isEmergencyActive = false;
        _currentEmergencyId = null;
        _emergencyStartTime = null;
        _emergencyTimer?.cancel();
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error cancelling SOS: $e');
      return false;
    }
  }

  // Update emergency status
  Future<bool> updateEmergencyStatus({
    required String status,
    String? notes,
  }) async {
    try {
      if (_currentEmergencyId == null) return false;

      final response = await http.patch(
        Uri.parse('$_apiBaseUrl/sos/$_currentEmergencyId/status'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'status': status,
          'notes': notes,
          'updatedAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 200) {
        if (status == 'resolved') {
          _isEmergencyActive = false;
          _currentEmergencyId = null;
          _emergencyStartTime = null;
          _emergencyTimer?.cancel();
        }
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating emergency status: $e');
      return false;
    }
  }

  // Load emergency contacts
  Future<void> _loadEmergencyContacts() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/sos/contacts'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _emergencyContacts = List<Map<String, dynamic>>.from(data['contacts'] ?? []);
      }
    } catch (e) {
      debugPrint('Error loading emergency contacts: $e');
    }
  }

  // Add emergency contact
  Future<bool> addEmergencyContact(Map<String, dynamic> contact) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/sos/contacts'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(contact),
      );

      if (response.statusCode == 201) {
        _emergencyContacts.add(contact);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error adding emergency contact: $e');
      return false;
    }
  }

  // Remove emergency contact
  Future<bool> removeEmergencyContact(String contactId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_apiBaseUrl/sos/contacts/$contactId'),
      );

      if (response.statusCode == 200) {
        _emergencyContacts.removeWhere((c) => c['id'] == contactId);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error removing emergency contact: $e');
      return false;
    }
  }

  // Load emergency history
  Future<void> _loadEmergencyHistory() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/sos/history'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _emergencyHistory = List<Map<String, dynamic>>.from(data['emergencies'] ?? []);
      }
    } catch (e) {
      debugPrint('Error loading emergency history: $e');
    }
  }

  // Notify emergency contacts
  Future<void> _notifyEmergencyContacts(Position position) async {
    for (var contact in _emergencyContacts) {
      try {
        await http.post(
          Uri.parse('$_apiBaseUrl/sos/notify'),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({
            'contactId': contact['id'],
            'contactNumber': contact['phone'],
            'message': 'تنبيه طوارئ: السائق في حالة طوارئ. الموقع: ${position.latitude}, ${position.longitude}',
            'emergencyId': _currentEmergencyId,
          }),
        );
      } catch (e) {
        debugPrint('Error notifying contact: $e');
      }
    }
  }

  // Start emergency timer
  void _startEmergencyTimer() {
    _emergencyTimer?.cancel();
    _emergencyTimer = Timer.periodic(
      const Duration(minutes: 5),
      (_) async {
        // Update location every 5 minutes
        final position = await Geolocator.getCurrentPosition(
          desiredAccuracy: LocationAccuracy.high,
        );

        await http.post(
          Uri.parse('$_apiBaseUrl/sos/$_currentEmergencyId/location'),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({
            'latitude': position.latitude,
            'longitude': position.longitude,
            'timestamp': DateTime.now().toIso8601String(),
          }),
        );
      },
    );
  }

  // Get nearby emergency services
  Future<List<Map<String, dynamic>>> getNearbyEmergencyServices({
    required double latitude,
    required double longitude,
    String? serviceType, // hospital, police, fire_station, gas_station
  }) async {
    try {
      String url = '$_apiBaseUrl/sos/nearby-services?lat=$latitude&lng=$longitude';
      if (serviceType != null) {
        url += '&type=$serviceType';
      }

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['services'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching nearby emergency services: $e');
      return [];
    }
  }

  // Share location with emergency services
  Future<bool> shareLocationWithEmergency({
    required String emergencyId,
    required List<String> recipientNumbers,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/sos/$emergencyId/share-location'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'recipients': recipientNumbers,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error sharing location: $e');
      return false;
    }
  }

  // Get emergency duration
  Duration? getEmergencyDuration() {
    if (_emergencyStartTime == null) return null;
    return DateTime.now().difference(_emergencyStartTime!);
  }

  @override
  void dispose() {
    _emergencyTimer?.cancel();
    super.dispose();
  }
}

// Emergency Types
class EmergencyType {
  static const String accident = 'accident';
  static const String medical = 'medical';
  static const String mechanical = 'mechanical';
  static const String security = 'security';
  static const String other = 'other';
}

// Emergency Status
class EmergencyStatus {
  static const String active = 'active';
  static const String responding = 'responding';
  static const String resolved = 'resolved';
  static const String cancelled = 'cancelled';
}
