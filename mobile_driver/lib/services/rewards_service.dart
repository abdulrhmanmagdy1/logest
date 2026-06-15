import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class RewardsService extends ChangeNotifier {
  List<Map<String, dynamic>> _availableRewards = [];
  List<Map<String, dynamic>> _myRewards = [];
  Map<String, dynamic>? _pointsBalance;
  List<Map<String, dynamic>> _bonusHistory = [];
  List<Map<String, dynamic>> _achievements = [];
  bool _isLoading = false;
  String? _error;

  List<Map<String, dynamic>> get availableRewards => _availableRewards;
  List<Map<String, dynamic>> get myRewards => _myRewards;
  Map<String, dynamic>? get pointsBalance => _pointsBalance;
  List<Map<String, dynamic>> get bonusHistory => _bonusHistory;
  List<Map<String, dynamic>> get achievements => _achievements;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Get points balance
  Future<void> fetchPointsBalance(String driverId) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/points'),
      );

      if (response.statusCode == 200) {
        _pointsBalance = jsonDecode(response.body);
      }
    } catch (e) {
      debugPrint('Error fetching points balance: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get available rewards
  Future<void> fetchAvailableRewards() async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/rewards/available'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _availableRewards = List<Map<String, dynamic>>.from(data['rewards'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching available rewards: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get my rewards
  Future<void> fetchMyRewards(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/rewards'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _myRewards = List<Map<String, dynamic>>.from(data['rewards'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching my rewards: $e');
    }
  }

  // Redeem reward
  Future<bool> redeemReward({
    required String rewardId,
    required String driverId,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/rewards/$rewardId/redeem'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'driverId': driverId}),
      );

      if (response.statusCode == 200) {
        await fetchPointsBalance(driverId);
        await fetchMyRewards(driverId);
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error redeeming reward: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get bonus history
  Future<void> fetchBonusHistory(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/bonus-history'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _bonusHistory = List<Map<String, dynamic>>.from(data['bonuses'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching bonus history: $e');
    }
  }

  // Get achievements
  Future<void> fetchAchievements(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/achievements'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _achievements = List<Map<String, dynamic>>.from(data['achievements'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching achievements: $e');
    }
  }

  // Add points for activity
  Future<bool> addPoints({
    required String driverId,
    required int points,
    required String reason,
    String? activityType,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/driver/$driverId/points/add'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'points': points,
          'reason': reason,
          'activityType': activityType,
          'timestamp': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error adding points: $e');
      return false;
    }
  }

  // Get leaderboard
  Future<List<Map<String, dynamic>>> getLeaderboard({
    String period = 'month',
    int limit = 10,
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/rewards/leaderboard?period=$period&limit=$limit'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['leaderboard'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching leaderboard: $e');
      return [];
    }
  }

  // Check for new achievements
  Future<List<Map<String, dynamic>>> checkNewAchievements(String driverId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/driver/$driverId/achievements/check'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['newAchievements'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error checking new achievements: $e');
      return [];
    }
  }

  // Get streak info
  Future<Map<String, dynamic>> getStreakInfo(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/streak'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching streak info: $e');
      return {};
    }
  }

  // Calculate bonus for trip
  int calculateTripBonus({
    required double baseFare,
    required double distance,
    required int durationMinutes,
    bool isNight = false,
    bool isWeekend = false,
    bool isHoliday = false,
    double? customerTip,
  }) {
    double bonus = 0.0;

    // Distance bonus
    if (distance > 50) {
      bonus += baseFare * 0.05; // 5% bonus for long trips
    }

    // Time bonus
    if (durationMinutes < 30) {
      bonus += baseFare * 0.03; // 3% bonus for quick delivery
    }

    // Night bonus
    if (isNight) {
      bonus += baseFare * 0.10; // 10% night bonus
    }

    // Weekend bonus
    if (isWeekend) {
      bonus += baseFare * 0.05; // 5% weekend bonus
    }

    // Holiday bonus
    if (isHoliday) {
      bonus += baseFare * 0.15; // 15% holiday bonus
    }

    // Tip bonus (points equivalent)
    if (customerTip != null) {
      bonus += customerTip * 0.5; // 50% of tip as bonus points
    }

    return bonus.toInt();
  }
}

// Reward Types
class RewardType {
  static const String cash = 'cash';
  static const String discount = 'discount';
  static const String fuel = 'fuel';
  static const String maintenance = 'maintenance';
  static const String gift = 'gift';
  static const String experience = 'experience';
}

// Achievement Types
class AchievementType {
  static const String trips = 'trips';
  static const String rating = 'rating';
  static const String streak = 'streak';
  static const String distance = 'distance';
  static const String punctuality = 'punctuality';
  static const String safety = 'safety';
  static const String referrals = 'referrals';
}

// Activity Types for Points
class ActivityType {
  static const String tripCompleted = 'trip_completed';
  static const String fiveStarRating = 'five_star_rating';
  static const String onTimeDelivery = 'on_time_delivery';
  static const String weekendWork = 'weekend_work';
  static const String nightWork = 'night_work';
  static const String referral = 'referral';
  static const String trainingCompleted = 'training_completed';
  static const String safetyCheck = 'safety_check';
}
