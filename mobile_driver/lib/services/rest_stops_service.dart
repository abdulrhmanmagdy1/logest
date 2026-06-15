import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:geolocator/geolocator.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class RestStop {
  final String id;
  final String name;
  final String type; // gas_station, rest_area, restaurant, parking
  final double latitude;
  final double longitude;
  final double distance;
  final String? address;
  final List<String>? amenities; // wifi, food, restrooms, showers, atm
  final String? rating;
  final String? phoneNumber;
  final bool isOpen24h;
  final String? imageUrl;

  RestStop({
    required this.id,
    required this.name,
    required this.type,
    required this.latitude,
    required this.longitude,
    required this.distance,
    this.address,
    this.amenities,
    this.rating,
    this.phoneNumber,
    this.isOpen24h = false,
    this.imageUrl,
  });

  factory RestStop.fromJson(Map<String, dynamic> json) {
    return RestStop(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      type: json['type'] ?? 'rest_area',
      latitude: json['latitude'] ?? 0.0,
      longitude: json['longitude'] ?? 0.0,
      distance: json['distance'] ?? 0.0,
      address: json['address'],
      amenities: json['amenities'] != null
          ? List<String>.from(json['amenities'])
          : null,
      rating: json['rating'],
      phoneNumber: json['phoneNumber'],
      isOpen24h: json['isOpen24h'] ?? false,
      imageUrl: json['imageUrl'],
    );
  }
}

class RestStopsService extends ChangeNotifier {
  List<RestStop> _nearbyStops = [];
  List<RestStop> _favoriteStops = [];
  List<RestStop> _recentStops = [];
  bool _isLoading = false;
  String? _error;

  List<RestStop> get nearbyStops => _nearbyStops;
  List<RestStop> get favoriteStops => _favoriteStops;
  List<RestStop> get recentStops => _recentStops;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Find nearby rest stops
  Future<void> findNearbyStops({
    required double radius, // in km
    String? stopType,
    List<String>? requiredAmenities,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
      );

      String url = '$_apiBaseUrl/rest-stops/nearby?'
          'lat=${position.latitude}&lng=${position.longitude}&radius=$radius';
      if (stopType != null) url += '&type=$stopType';
      if (requiredAmenities != null) {
        url += '&amenities=${requiredAmenities.join(',')}';
      }

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _nearbyStops = (data['stops'] as List)
            .map((s) => RestStop.fromJson(s))
            .toList();
      }
    } catch (e) {
      debugPrint('Error finding nearby stops: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get rest stops along route
  Future<List<RestStop>> getStopsAlongRoute({
    required List<Map<String, double>> routePoints,
    String? stopType,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/rest-stops/along-route'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'route': routePoints,
          'type': stopType,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return (data['stops'] as List)
            .map((s) => RestStop.fromJson(s))
            .toList();
      }
      return [];
    } catch (e) {
      debugPrint('Error getting stops along route: $e');
      return [];
    }
  }

  // Add to favorites
  Future<bool> addToFavorites(String stopId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/rest-stops/$stopId/favorite'),
      );

      if (response.statusCode == 200) {
        final stop = _nearbyStops.firstWhere((s) => s.id == stopId);
        _favoriteStops.add(stop);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error adding to favorites: $e');
      return false;
    }
  }

  // Remove from favorites
  Future<bool> removeFromFavorites(String stopId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_apiBaseUrl/rest-stops/$stopId/favorite'),
      );

      if (response.statusCode == 200) {
        _favoriteStops.removeWhere((s) => s.id == stopId);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error removing from favorites: $e');
      return false;
    }
  }

  // Get favorite stops
  Future<void> fetchFavoriteStops() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/rest-stops/favorites'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _favoriteStops = (data['stops'] as List)
            .map((s) => RestStop.fromJson(s))
            .toList();
      }
    } catch (e) {
      debugPrint('Error fetching favorite stops: $e');
    }
  }

  // Record visit
  Future<bool> recordVisit(String stopId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/rest-stops/$stopId/visit'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'timestamp': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error recording visit: $e');
      return false;
    }
  }

  // Get recent stops
  Future<void> fetchRecentStops() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/rest-stops/recent'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _recentStops = (data['stops'] as List)
            .map((s) => RestStop.fromJson(s))
            .toList();
      }
    } catch (e) {
      debugPrint('Error fetching recent stops: $e');
    }
  }

  // Get stop details
  Future<RestStop?> getStopDetails(String stopId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/rest-stops/$stopId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return RestStop.fromJson(data);
      }
      return null;
    } catch (e) {
      debugPrint('Error fetching stop details: $e');
      return null;
    }
  }

  // Submit review
  Future<bool> submitReview({
    required String stopId,
    required double rating,
    required String comment,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/rest-stops/$stopId/reviews'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'rating': rating,
          'comment': comment,
          'timestamp': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error submitting review: $e');
      return false;
    }
  }

  // Report issue
  Future<bool> reportIssue({
    required String stopId,
    required String issueType,
    required String description,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/rest-stops/$stopId/report'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'issueType': issueType,
          'description': description,
          'timestamp': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error reporting issue: $e');
      return false;
    }
  }

  // Get directions to stop
  Future<String?> getDirectionsToStop(String stopId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/rest-stops/$stopId/directions'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['directionsUrl'];
      }
      return null;
    } catch (e) {
      debugPrint('Error getting directions: $e');
      return null;
    }
  }
}

// Stop Types
class StopType {
  static const String gasStation = 'gas_station';
  static const String restArea = 'rest_area';
  static const String restaurant = 'restaurant';
  static const String parking = 'parking';
  static const String hotel = 'hotel';
}

// Amenities
class Amenity {
  static const String wifi = 'wifi';
  static const String food = 'food';
  static const String restrooms = 'restrooms';
  static const String showers = 'showers';
  static const String atm = 'atm';
  static const String charging = 'charging';
  static const String laundry = 'laundry';
  static const String mechanic = 'mechanic';
}
