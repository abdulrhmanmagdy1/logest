import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:location/location.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class LocationService extends ChangeNotifier {
  Location? _location;
  StreamSubscription<LocationData>? _locationSubscription;
  LocationData? _currentLocation;
  bool _isTracking = false;
  bool _serviceEnabled = false;
  PermissionStatus? _permissionStatus;
  String? _error;

  LocationData? get currentLocation => _currentLocation;
  bool get isTracking => _isTracking;
  bool get serviceEnabled => _serviceEnabled;
  PermissionStatus? get permissionStatus => _permissionStatus;
  String? get error => _error;

  // Initialize location service
  Future<bool> initialize() async {
    try {
      _location = Location();
      
      // Check if service is enabled
      _serviceEnabled = await _location!.serviceEnabled();
      if (!_serviceEnabled) {
        _serviceEnabled = await _location!.requestService();
        if (!_serviceEnabled) {
          _error = 'Location service is disabled';
          notifyListeners();
          return false;
        }
      }

      // Check permission
      _permissionStatus = await _location!.hasPermission();
      if (_permissionStatus == PermissionStatus.denied) {
        _permissionStatus = await _location!.requestPermission();
        if (_permissionStatus != PermissionStatus.granted) {
          _error = 'Location permission denied';
          notifyListeners();
          return false;
        }
      }

      // Configure location settings
      await _location!.changeSettings(
        accuracy: LocationAccuracy.high,
        interval: 10000, // 10 seconds
        distanceFilter: 10, // 10 meters
      );

      // Get initial location
      _currentLocation = await _location!.getLocation();
      notifyListeners();

      return true;
    } catch (e) {
      _error = 'Error initializing location: $e';
      notifyListeners();
      return false;
    }
  }

  // Start tracking location
  Future<void> startTracking({Function(LocationData)? onLocationUpdate}) async {
    if (_location == null) {
      final initialized = await initialize();
      if (!initialized) return;
    }

    try {
      _isTracking = true;
      notifyListeners();

      _locationSubscription = _location!.onLocationChanged.listen((LocationData locationData) {
        _currentLocation = locationData;
        
        if (onLocationUpdate != null) {
          onLocationUpdate(locationData);
        }
        
        notifyListeners();
      });
    } catch (e) {
      _error = 'Error starting tracking: $e';
      _isTracking = false;
      notifyListeners();
    }
  }

  // Stop tracking
  Future<void> stopTracking() async {
    try {
      await _locationSubscription?.cancel();
      _locationSubscription = null;
      _isTracking = false;
      notifyListeners();
    } catch (e) {
      debugPrint('Error stopping tracking: $e');
    }
  }

  // Get current location once
  Future<LocationData?> getCurrentLocation() async {
    try {
      if (_location == null) {
        final initialized = await initialize();
        if (!initialized) return null;
      }

      return await _location!.getLocation();
    } catch (e) {
      _error = 'Error getting location: $e';
      notifyListeners();
      return null;
    }
  }

  // Get LatLng for Google Maps
  LatLng? get currentLatLng {
    if (_currentLocation?.latitude != null && _currentLocation?.longitude != null) {
      return LatLng(
        _currentLocation!.latitude!,
        _currentLocation!.longitude!,
      );
    }
    return null;
  }

  // Calculate distance between two points
  double calculateDistance(double startLatitude, double startLongitude,
      double endLatitude, double endLongitude) {
    return Geolocator.distanceBetween(
      startLatitude,
      startLongitude,
      endLatitude,
      endLongitude,
    );
  }

  // Calculate distance from current location
  double? distanceFromCurrent(double targetLat, double targetLng) {
    if (_currentLocation?.latitude != null && _currentLocation?.longitude != null) {
      return calculateDistance(
        _currentLocation!.latitude!,
        _currentLocation!.longitude!,
        targetLat,
        targetLng,
      );
    }
    return null;
  }

  // Get address from coordinates (reverse geocoding)
  // This would typically use Google Maps Geocoding API
  Future<String?> getAddressFromCoordinates(double lat, double lng) async {
    // TODO: Implement reverse geocoding using Google Maps API
    return 'Address placeholder';
  }

  // Check if within destination radius
  bool isWithinRadius(double targetLat, double targetLng, double radiusInMeters) {
    final distance = distanceFromCurrent(targetLat, targetLng);
    if (distance != null) {
      return distance <= radiusInMeters;
    }
    return false;
  }

  // Background location tracking
  Future<void> enableBackgroundMode() async {
    try {
      if (_location == null) return;
      
      final result = await _location!.enableBackgroundMode(enable: true);
      if (result) {
        await _location!.changeSettings(
          accuracy: LocationAccuracy.high,
          interval: 30000, // 30 seconds in background
          distanceFilter: 50, // 50 meters
        );
      }
    } catch (e) {
      debugPrint('Error enabling background mode: $e');
    }
  }

  Future<void> disableBackgroundMode() async {
    try {
      if (_location == null) return;
      await _location!.enableBackgroundMode(enable: false);
    } catch (e) {
      debugPrint('Error disabling background mode: $e');
    }
  }

  @override
  void dispose() {
    stopTracking();
    super.dispose();
  }
}
