import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class Announcement {
  final String id;
  final String title;
  final String content;
  final String type; // info, warning, urgent, promotion
  final DateTime createdAt;
  final DateTime? expiresAt;
  final bool isRead;
  final String? imageUrl;
  final String? link;
  final Map<String, dynamic>? metadata;

  Announcement({
    required this.id,
    required this.title,
    required this.content,
    required this.type,
    required this.createdAt,
    this.expiresAt,
    this.isRead = false,
    this.imageUrl,
    this.link,
    this.metadata,
  });

  factory Announcement.fromJson(Map<String, dynamic> json) {
    return Announcement(
      id: json['id'] ?? '',
      title: json['title'] ?? '',
      content: json['content'] ?? '',
      type: json['type'] ?? 'info',
      createdAt: DateTime.parse(json['createdAt'] ?? DateTime.now().toIso8601String()),
      expiresAt: json['expiresAt'] != null ? DateTime.parse(json['expiresAt']) : null,
      isRead: json['isRead'] ?? false,
      imageUrl: json['imageUrl'],
      link: json['link'],
      metadata: json['metadata'],
    );
  }
}

class AnnouncementsService extends ChangeNotifier {
  List<Announcement> _announcements = [];
  List<Announcement> _unreadAnnouncements = [];
  List<Announcement> _promotions = [];
  bool _isLoading = false;
  String? _error;

  List<Announcement> get announcements => _announcements;
  List<Announcement> get unreadAnnouncements => _unreadAnnouncements;
  List<Announcement> get promotions => _promotions;
  bool get isLoading => _isLoading;
  String? get error => _error;
  int get unreadCount => _unreadAnnouncements.length;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Fetch announcements
  Future<void> fetchAnnouncements({
    String? type,
    bool onlyUnread = false,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      String url = '$_apiBaseUrl/announcements';
      if (type != null) url += '?type=$type';
      if (onlyUnread) url += (type != null ? '&' : '?') + 'unread=true';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _announcements = (data['announcements'] as List)
            .map((a) => Announcement.fromJson(a))
            .toList();
        
        _unreadAnnouncements = _announcements.where((a) => !a.isRead).toList();
        _promotions = _announcements.where((a) => a.type == 'promotion').toList();
        
        notifyListeners();
      }
    } catch (e) {
      debugPrint('Error fetching announcements: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Mark as read
  Future<bool> markAsRead(String announcementId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/announcements/$announcementId/read'),
      );

      if (response.statusCode == 200) {
        final announcement = _announcements.firstWhere((a) => a.id == announcementId);
        // Update local state
        final index = _announcements.indexWhere((a) => a.id == announcementId);
        if (index != -1) {
          _announcements[index] = Announcement(
            id: announcement.id,
            title: announcement.title,
            content: announcement.content,
            type: announcement.type,
            createdAt: announcement.createdAt,
            expiresAt: announcement.expiresAt,
            isRead: true,
            imageUrl: announcement.imageUrl,
            link: announcement.link,
            metadata: announcement.metadata,
          );
          _unreadAnnouncements.removeWhere((a) => a.id == announcementId);
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error marking as read: $e');
      return false;
    }
  }

  // Mark all as read
  Future<bool> markAllAsRead() async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/announcements/mark-all-read'),
      );

      if (response.statusCode == 200) {
        _unreadAnnouncements.clear();
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error marking all as read: $e');
      return false;
    }
  }

  // Dismiss announcement
  Future<bool> dismissAnnouncement(String announcementId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/announcements/$announcementId/dismiss'),
      );

      if (response.statusCode == 200) {
        _announcements.removeWhere((a) => a.id == announcementId);
        _unreadAnnouncements.removeWhere((a) => a.id == announcementId);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error dismissing announcement: $e');
      return false;
    }
  }

  // Get announcement details
  Future<Announcement?> getAnnouncementDetails(String announcementId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/announcements/$announcementId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return Announcement.fromJson(data);
      }
      return null;
    } catch (e) {
      debugPrint('Error fetching announcement details: $e');
      return null;
    }
  }

  // Get active promotions
  Future<void> fetchActivePromotions() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/announcements/promotions/active'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _promotions = (data['promotions'] as List)
            .map((p) => Announcement.fromJson(p))
            .toList();
        notifyListeners();
      }
    } catch (e) {
      debugPrint('Error fetching active promotions: $e');
    }
  }

  // Claim promotion
  Future<bool> claimPromotion(String promotionId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/announcements/promotions/$promotionId/claim'),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error claiming promotion: $e');
      return false;
    }
  }

  // Get announcement statistics
  Future<Map<String, dynamic>> getAnnouncementStats() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/announcements/stats'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching announcement stats: $e');
      return {};
    }
  }
}

// Announcement Types
class AnnouncementType {
  static const String info = 'info';
  static const String warning = 'warning';
  static const String urgent = 'urgent';
  static const String promotion = 'promotion';
  static const String maintenance = 'maintenance';
  static const String update = 'update';
}
