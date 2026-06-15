import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';

/// Cache Service
/// Centralized caching with expiration support
class CacheService {
  static final CacheService _instance = CacheService._internal();
  factory CacheService() => _instance;
  CacheService._internal();

  SharedPreferences? _prefs;

  Future<void> init() async {
    _prefs ??= await SharedPreferences.getInstance();
  }

  /// Get cached data
  Future<T?> get<T>(String key) async {
    await init();
    
    final data = _prefs!.getString(key);
    if (data == null) return null;

    try {
      final cached = jsonDecode(data);
      
      // Check expiration
      if (cached['expiry'] != null) {
        final expiry = DateTime.parse(cached['expiry']);
        if (DateTime.now().isAfter(expiry)) {
          await remove(key);
          return null;
        }
      }

      return cached['data'] as T;
    } catch (e) {
      return null;
    }
  }

  /// Set cached data with optional expiration
  Future<void> set<T>(String key, T data, {Duration? expiration}) async {
    await init();
    
    final cacheData = {
      'data': data,
      'timestamp': DateTime.now().toIso8601String(),
      if (expiration != null)
        'expiry': DateTime.now().add(expiration).toIso8601String(),
    };

    await _prefs!.setString(key, jsonEncode(cacheData));
  }

  /// Remove cached data
  Future<void> remove(String key) async {
    await init();
    await _prefs!.remove(key);
  }

  /// Check if key exists and not expired
  Future<bool> has(String key) async {
    final data = await get(key);
    return data != null;
  }

  /// Clear all cache
  Future<void> clear() async {
    await init();
    await _prefs!.clear();
  }

  /// Clear expired entries
  Future<void> clearExpired() async {
    await init();
    
    final keys = _prefs!.getKeys();
    for (final key in keys) {
      final data = _prefs!.getString(key);
      if (data != null) {
        try {
          final cached = jsonDecode(data);
          if (cached['expiry'] != null) {
            final expiry = DateTime.parse(cached['expiry']);
            if (DateTime.now().isAfter(expiry)) {
              await remove(key);
            }
          }
        } catch (e) {
          // Invalid data, remove it
          await remove(key);
        }
      }
    }
  }

  /// Get cache info
  Future<Map<String, dynamic>> getInfo() async {
    await init();
    
    final keys = _prefs!.getKeys();
    int totalSize = 0;
    int expiredCount = 0;
    
    for (final key in keys) {
      final data = _prefs!.getString(key);
      if (data != null) {
        totalSize += data.length;
        try {
          final cached = jsonDecode(data);
          if (cached['expiry'] != null) {
            final expiry = DateTime.parse(cached['expiry']);
            if (DateTime.now().isAfter(expiry)) {
              expiredCount++;
            }
          }
        } catch (e) {
          expiredCount++;
        }
      }
    }

    return {
      'totalEntries': keys.length,
      'totalSizeKB': (totalSize / 1024).toStringAsFixed(2),
      'expiredEntries': expiredCount,
    };
  }

  // Common cache keys
  static const String keyUser = 'cached_user';
  static const String keyTrips = 'cached_trips';
  static const String keyNotifications = 'cached_notifications';
  static const String keySettings = 'cached_settings';
  static const String keyAuthToken = 'auth_token';
  static const String keyLastSync = 'last_sync';
}
