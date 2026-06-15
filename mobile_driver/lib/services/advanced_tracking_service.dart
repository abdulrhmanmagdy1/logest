import 'dart:async';
import 'dart:math' as math;
import 'package:flutter/foundation.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:polyline/polyline.dart' as polyline;
import 'package:http/http.dart' as http;
import 'dart:convert';

class AdvancedTrackingService extends ChangeNotifier {
  String? _googleMapsApiKey;
  List<LatLng> _currentRoute = [];
  LatLng? _currentPosition;
  double _totalDistance = 0;
  double _estimatedTime = 0;
  bool _isOffRoute = false;
  double _routeDeviation = 0;
  StreamSubscription<Position>? _positionSubscription;
  Timer? _trackingTimer;

  List<LatLng> get currentRoute => _currentRoute;
  LatLng? get currentPosition => _currentPosition;
  double get totalDistance => _totalDistance;
  double get estimatedTime => _estimatedTime;
  bool get isOffRoute => _isOffRoute;
  double get routeDeviation => _routeDeviation;

  final String _baseUrl = 'https://maps.googleapis.com/maps/api';

  void setApiKey(String apiKey) {
    _googleMapsApiKey = apiKey;
  }

  // Get optimized route using Google Maps Directions API
  Future<List<LatLng>> getOptimizedRoute(
    LatLng origin,
    LatLng destination, {
    List<LatLng>? waypoints,
    String travelMode = 'driving',
  }) async {
    try {
      if (_googleMapsApiKey == null) {
        debugPrint('Google Maps API Key not set');
        return [origin, destination];
      }

      String url = '$_baseUrl/directions/json?'
          'origin=${origin.latitude},${origin.longitude}&'
          'destination=${destination.latitude},${destination.longitude}&'
          'mode=$travelMode&'
          'key=$_googleMapsApiKey';

      if (waypoints != null && waypoints.isNotEmpty) {
        final waypointsStr = waypoints
            .map((p) => '${p.latitude},${p.longitude}')
            .join('|');
        url += '&waypoints=optimize:true|$waypointsStr';
      }

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['status'] == 'OK') {
          final routes = data['routes'] as List;
          if (routes.isNotEmpty) {
            final points = routes[0]['overview_polyline']['points'];
            _currentRoute = polyline.PolylineDecoder().decodePoints(points);
            _estimatedTime = routes[0]['legs'][0]['duration']['value'] / 60.0;
            notifyListeners();
            return _currentRoute;
          }
        }
      }
      return [origin, destination];
    } catch (e) {
      debugPrint('Error getting route: $e');
      return [origin, destination];
    }
  }

  // Calculate ETA based on current speed and distance
  double calculateETA(double distance, double speed) {
    if (speed <= 0) return 0;
    return distance / speed * 60; // Return in minutes
  }

  // Start real-time tracking with route monitoring
  Future<void> startAdvancedTracking({
    required LatLng destination,
    required Function(bool) onRouteDeviation,
    required Function(double) onETAUpdate,
    int updateInterval = 5, // seconds
  }) async {
    _positionSubscription = Geolocator.getPositionStream(
      locationSettings: const LocationSettings(
        accuracy: LocationAccuracy.high,
        distanceFilter: 10,
        timeLimit: Duration(seconds: 5),
      ),
    ).listen((Position position) {
      _currentPosition = LatLng(position.latitude, position.longitude);
      notifyListeners();

      // Check route deviation
      if (_currentRoute.isNotEmpty) {
        _checkRouteDeviation(position);
        if (_isOffRoute) {
          onRouteDeviation(true);
        }
      }

      // Update ETA
      if (destination != null) {
        final distance = Geolocator.distanceBetween(
          position.latitude,
          position.longitude,
          destination.latitude,
          destination.longitude,
        );
        final speed = position.speed;
        final eta = calculateETA(distance, speed);
        onETAUpdate(eta);
      }
    });

    // Periodic route recalculation
    _trackingTimer = Timer.periodic(
      Duration(seconds: updateInterval),
      (_) async {
        if (_currentPosition != null && destination != null) {
          if (_isOffRoute) {
            // Recalculate route if off route
            await getOptimizedRoute(_currentPosition!, destination);
          }
        }
      },
    );
  }

  // Check if driver deviated from route
  void _checkRouteDeviation(Position position) {
    if (_currentRoute.isEmpty) return;

    double minDistance = double.infinity;
    LatLng? closestPoint;

    for (var point in _currentRoute) {
      final distance = Geolocator.distanceBetween(
        position.latitude,
        position.longitude,
        point.latitude,
        point.longitude,
      );
      if (distance < minDistance) {
        minDistance = distance;
        closestPoint = point;
      }
    }

    _routeDeviation = minDistance;
    _isOffRoute = minDistance > 100; // 100 meters threshold

    notifyListeners();
  }

  // Stop tracking
  void stopTracking() {
    _positionSubscription?.cancel();
    _trackingTimer?.cancel();
    _positionSubscription = null;
    _trackingTimer = null;
  }

  // Get nearby places (gas stations, rest areas)
  Future<List<Map<String, dynamic>>> getNearbyPlaces(
    LatLng location,
    String placeType,
    int radius,
  ) async {
    try {
      if (_googleMapsApiKey == null) return [];

      final url = '$_baseUrl/place/nearbysearch/json?'
          'location=${location.latitude},${location.longitude}&'
          'radius=$radius&'
          'type=$placeType&'
          'key=$_googleMapsApiKey';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['status'] == 'OK') {
          return List<Map<String, dynamic>>.from(data['results']);
        }
      }
      return [];
    } catch (e) {
      debugPrint('Error getting nearby places: $e');
      return [];
    }
  }

  // Geocode address to coordinates
  Future<LatLng?> geocodeAddress(String address) async {
    try {
      if (_googleMapsApiKey == null) return null;

      final url = '$_baseUrl/geocode/json?'
          'address=${Uri.encodeComponent(address)}&'
          'key=$_googleMapsApiKey';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['status'] == 'OK' && data['results'].isNotEmpty) {
          final location = data['results'][0]['geometry']['location'];
          return LatLng(location['lat'], location['lng']);
        }
      }
      return null;
    } catch (e) {
      debugPrint('Error geocoding address: $e');
      return null;
    }
  }

  // Reverse geocode coordinates to address
  Future<String?> reverseGeocode(LatLng location) async {
    try {
      if (_googleMapsApiKey == null) return null;

      final url = '$_baseUrl/geocode/json?'
          'latlng=${location.latitude},${location.longitude}&'
          'key=$_googleMapsApiKey';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['status'] == 'OK' && data['results'].isNotEmpty) {
          return data['results'][0]['formatted_address'];
        }
      }
      return null;
    } catch (e) {
      debugPrint('Error reverse geocoding: $e');
      return null;
    }
  }

  // Calculate total route distance
  double calculateRouteDistance(List<LatLng> route) {
    if (route.length < 2) return 0;

    double total = 0;
    for (int i = 0; i < route.length - 1; i++) {
      total += Geolocator.distanceBetween(
        route[i].latitude,
        route[i].longitude,
        route[i + 1].latitude,
        route[i + 1].longitude,
      );
    }
    return total;
  }

  // Get traffic information
  Future<Map<String, dynamic>> getTrafficInfo(LatLng origin, LatLng destination) async {
    try {
      if (_googleMapsApiKey == null) return {};

      final url = '$_baseUrl/distancematrix/json?'
          'origins=${origin.latitude},${origin.longitude}&'
          'destinations=${destination.latitude},${destination.longitude}&'
          'departure_time=now&'
          'traffic_model=best_guess&'
          'key=$_googleMapsApiKey';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['status'] == 'OK') {
          return data['rows'][0]['elements'][0];
        }
      }
      return {};
    } catch (e) {
      debugPrint('Error getting traffic info: $e');
      return {};
    }
  }

  @override
  void dispose() {
    stopTracking();
    super.dispose();
  }
}
