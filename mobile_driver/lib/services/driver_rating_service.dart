import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class DriverRatingService extends ChangeNotifier {
  Map<String, dynamic>? _driverRating;
  List<Map<String, dynamic>> _customerReviews = [];
  List<Map<String, dynamic>> _feedbackHistory = [];
  Map<String, dynamic>? _performanceMetrics;
  bool _isLoading = false;
  String? _error;

  Map<String, dynamic>? get driverRating => _driverRating;
  List<Map<String, dynamic>> get customerReviews => _customerReviews;
  List<Map<String, dynamic>> get feedbackHistory => _feedbackHistory;
  Map<String, dynamic>? get performanceMetrics => _performanceMetrics;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Get driver rating
  Future<void> fetchDriverRating(String driverId) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/rating'),
      );

      if (response.statusCode == 200) {
        _driverRating = jsonDecode(response.body);
      }
    } catch (e) {
      debugPrint('Error fetching driver rating: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get customer reviews
  Future<void> fetchCustomerReviews(String driverId) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/reviews'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _customerReviews = List<Map<String, dynamic>>.from(data['reviews'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching customer reviews: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Submit customer review
  Future<bool> submitReview({
    required String tripId,
    required String driverId,
    required double rating,
    required String comment,
    List<String>? categories, // punctuality, professionalism, vehicle_condition, etc.
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/driver/$driverId/reviews'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'tripId': tripId,
          'rating': rating,
          'comment': comment,
          'categories': categories,
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

  // Get feedback history
  Future<void> fetchFeedbackHistory(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/feedback-history'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _feedbackHistory = List<Map<String, dynamic>>.from(data['feedback'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching feedback history: $e');
    }
  }

  // Submit driver feedback
  Future<bool> submitFeedback({
    required String driverId,
    required String feedbackType, // complaint, suggestion, appreciation
    required String message,
    String? tripId,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/driver/$driverId/feedback'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'type': feedbackType,
          'message': message,
          'tripId': tripId,
          'timestamp': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error submitting feedback: $e');
      return false;
    }
  }

  // Get performance metrics
  Future<void> fetchPerformanceMetrics(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/performance'),
      );

      if (response.statusCode == 200) {
        _performanceMetrics = jsonDecode(response.body);
      }
    } catch (e) {
      debugPrint('Error fetching performance metrics: $e');
    }
  }

  // Get rating trends
  Future<List<Map<String, dynamic>>> getRatingTrends(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/rating-trends'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['trends'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching rating trends: $e');
      return [];
    }
  }

  // Respond to review
  Future<bool> respondToReview({
    required String reviewId,
    required String response,
  }) async {
    try {
      final httpResponse = await http.post(
        Uri.parse('$_apiBaseUrl/reviews/$reviewId/respond'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'response': response,
          'timestamp': DateTime.now().toIso8601String(),
        }),
      );

      if (httpResponse.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error responding to review: $e');
      return false;
    }
  }

  // Report inappropriate review
  Future<bool> reportReview(String reviewId, String reason) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/reviews/$reviewId/report'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'reason': reason}),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error reporting review: $e');
      return false;
    }
  }

  // Get rating breakdown by category
  Future<Map<String, double>> getRatingBreakdown(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/rating-breakdown'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return Map<String, double>.from(data['breakdown'] ?? {});
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching rating breakdown: $e');
      return {};
    }
  }

  // Calculate driver badge
  String calculateDriverBadge(double rating, int totalTrips) {
    if (rating >= 4.8 && totalTrips >= 100) {
      return 'Elite Driver';
    } else if (rating >= 4.5 && totalTrips >= 50) {
      return 'Gold Driver';
    } else if (rating >= 4.0 && totalTrips >= 25) {
      return 'Silver Driver';
    } else if (rating >= 3.5 && totalTrips >= 10) {
      return 'Bronze Driver';
    } else {
      return 'New Driver';
    }
  }

  // Get improvement suggestions
  List<String> getImprovementSuggestions(Map<String, dynamic> metrics) {
    final suggestions = <String>[];

    final punctuality = metrics['punctuality'] ?? 0.0;
    final professionalism = metrics['professionalism'] ?? 0.0;
    final vehicleCondition = metrics['vehicleCondition'] ?? 0.0;
    final communication = metrics['communication'] ?? 0.0;

    if (punctuality < 4.0) {
      suggestions.add('حاول الوصول في الوقت المحدد لتحسين تقييمك');
    }
    if (professionalism < 4.0) {
      suggestions.add('حافظ على السلوك المهني مع العملاء');
    }
    if (vehicleCondition < 4.0) {
      suggestions.add('حافظ على نظافة المركبة وحالتها الجيدة');
    }
    if (communication < 4.0) {
      suggestions.add('تواصل بشكل أفضل مع العملاء أثناء الرحلة');
    }

    if (suggestions.isEmpty) {
      suggestions.add('أداؤك ممتاز! استمر في الحفاظ على هذا المستوى');
    }

    return suggestions;
  }
}

// Rating Categories
class RatingCategory {
  static const String punctuality = 'punctuality';
  static const String professionalism = 'professionalism';
  static const String vehicleCondition = 'vehicle_condition';
  static const String communication = 'communication';
  static const String safety = 'safety';
  static const String navigation = 'navigation';
}

// Feedback Types
class FeedbackType {
  static const String complaint = 'complaint';
  static const String suggestion = 'suggestion';
  static const String appreciation = 'appreciation';
  static const String bug = 'bug';
  static const String other = 'other';
}
