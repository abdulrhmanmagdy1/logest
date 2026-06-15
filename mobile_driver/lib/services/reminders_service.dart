import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class Reminder {
  final String id;
  final String title;
  final String description;
  final DateTime reminderTime;
  final String type; // maintenance, document, appointment, custom
  final bool isRecurring;
  final String? recurringPattern; // daily, weekly, monthly
  final bool isCompleted;
  final Map<String, dynamic>? metadata;

  Reminder({
    required this.id,
    required this.title,
    required this.description,
    required this.reminderTime,
    required this.type,
    this.isRecurring = false,
    this.recurringPattern,
    this.isCompleted = false,
    this.metadata,
  });

  factory Reminder.fromJson(Map<String, dynamic> json) {
    return Reminder(
      id: json['id'] ?? '',
      title: json['title'] ?? '',
      description: json['description'] ?? '',
      reminderTime: DateTime.parse(json['reminderTime'] ?? DateTime.now().toIso8601String()),
      type: json['type'] ?? 'custom',
      isRecurring: json['isRecurring'] ?? false,
      recurringPattern: json['recurringPattern'],
      isCompleted: json['isCompleted'] ?? false,
      metadata: json['metadata'],
    );
  }
}

class RemindersService extends ChangeNotifier {
  List<Reminder> _reminders = [];
  List<Reminder> _upcomingReminders = [];
  List<Reminder> _completedReminders = [];
  Timer? _reminderCheckTimer;
  final FlutterLocalNotificationsPlugin _notificationsPlugin =
      FlutterLocalNotificationsPlugin();

  List<Reminder> get reminders => _reminders;
  List<Reminder> get upcomingReminders => _upcomingReminders;
  List<Reminder> get completedReminders => _completedReminders;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Initialize service
  Future<void> initialize() async {
    await _initializeNotifications();
    await fetchReminders();
    _startReminderCheck();
  }

  // Initialize notifications
  Future<void> _initializeNotifications() async {
    const AndroidInitializationSettings initializationSettingsAndroid =
        AndroidInitializationSettings('@mipmap/ic_launcher');

    const DarwinInitializationSettings initializationSettingsDarwin =
        DarwinInitializationSettings(
      requestAlertPermission: true,
      requestBadgePermission: true,
      requestSoundPermission: true,
    );

    const InitializationSettings initializationSettings =
        InitializationSettings(
      android: initializationSettingsAndroid,
      iOS: initializationSettingsDarwin,
    );

    await _notificationsPlugin.initialize(initializationSettings);
  }

  // Fetch reminders
  Future<void> fetchReminders() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/reminders'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _reminders = (data['reminders'] as List)
            .map((r) => Reminder.fromJson(r))
            .toList();
        
        _updateReminderLists();
        notifyListeners();
      }
    } catch (e) {
      debugPrint('Error fetching reminders: $e');
    }
  }

  // Update reminder lists
  void _updateReminderLists() {
    final now = DateTime.now();
    _upcomingReminders = _reminders
        .where((r) => !r.isCompleted && r.reminderTime.isAfter(now))
        .toList()
      ..sort((a, b) => a.reminderTime.compareTo(b.reminderTime));
    
    _completedReminders = _reminders
        .where((r) => r.isCompleted)
        .toList()
      ..sort((a, b) => b.reminderTime.compareTo(a.reminderTime));
  }

  // Create reminder
  Future<bool> createReminder(Reminder reminder) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/reminders'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'title': reminder.title,
          'description': reminder.description,
          'reminderTime': reminder.reminderTime.toIso8601String(),
          'type': reminder.type,
          'isRecurring': reminder.isRecurring,
          'recurringPattern': reminder.recurringPattern,
          'metadata': reminder.metadata,
        }),
      );

      if (response.statusCode == 201) {
        final data = jsonDecode(response.body);
        _reminders.add(Reminder.fromJson(data));
        _updateReminderLists();
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error creating reminder: $e');
      return false;
    }
  }

  // Update reminder
  Future<bool> updateReminder(Reminder reminder) async {
    try {
      final response = await http.put(
        Uri.parse('$_apiBaseUrl/reminders/${reminder.id}'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'title': reminder.title,
          'description': reminder.description,
          'reminderTime': reminder.reminderTime.toIso8601String(),
          'type': reminder.type,
          'isRecurring': reminder.isRecurring,
          'recurringPattern': reminder.recurringPattern,
          'isCompleted': reminder.isCompleted,
          'metadata': reminder.metadata,
        }),
      );

      if (response.statusCode == 200) {
        final index = _reminders.indexWhere((r) => r.id == reminder.id);
        if (index != -1) {
          _reminders[index] = reminder;
          _updateReminderLists();
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating reminder: $e');
      return false;
    }
  }

  // Complete reminder
  Future<bool> completeReminder(String reminderId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/reminders/$reminderId/complete'),
      );

      if (response.statusCode == 200) {
        final index = _reminders.indexWhere((r) => r.id == reminderId);
        if (index != -1) {
          _reminders[index] = Reminder(
            id: _reminders[index].id,
            title: _reminders[index].title,
            description: _reminders[index].description,
            reminderTime: _reminders[index].reminderTime,
            type: _reminders[index].type,
            isRecurring: _reminders[index].isRecurring,
            recurringPattern: _reminders[index].recurringPattern,
            isCompleted: true,
            metadata: _reminders[index].metadata,
          );
          
          // Handle recurring reminders
          if (_reminders[index].isRecurring) {
            await _createNextOccurrence(_reminders[index]);
          }
          
          _updateReminderLists();
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error completing reminder: $e');
      return false;
    }
  }

  // Create next occurrence for recurring reminder
  Future<void> _createNextOccurrence(Reminder reminder) async {
    DateTime nextTime;
    switch (reminder.recurringPattern) {
      case 'daily':
        nextTime = reminder.reminderTime.add(const Duration(days: 1));
        break;
      case 'weekly':
        nextTime = reminder.reminderTime.add(const Duration(days: 7));
        break;
      case 'monthly':
        nextTime = DateTime(
          reminder.reminderTime.year,
          reminder.reminderTime.month + 1,
          reminder.reminderTime.day,
          reminder.reminderTime.hour,
          reminder.reminderTime.minute,
        );
        break;
      default:
        return;
    }

    await createReminder(Reminder(
      id: '',
      title: reminder.title,
      description: reminder.description,
      reminderTime: nextTime,
      type: reminder.type,
      isRecurring: reminder.isRecurring,
      recurringPattern: reminder.recurringPattern,
      metadata: reminder.metadata,
    ));
  }

  // Delete reminder
  Future<bool> deleteReminder(String reminderId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_apiBaseUrl/reminders/$reminderId'),
      );

      if (response.statusCode == 200) {
        _reminders.removeWhere((r) => r.id == reminderId);
        _updateReminderLists();
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error deleting reminder: $e');
      return false;
    }
  }

  // Start reminder check timer
  void _startReminderCheck() {
    _reminderCheckTimer?.cancel();
    _reminderCheckTimer = Timer.periodic(
      const Duration(minutes: 1),
      (_) => _checkReminders(),
    );
  }

  // Check for due reminders
  void _checkReminders() {
    final now = DateTime.now();
    for (var reminder in _upcomingReminders) {
      if (reminder.reminderTime.isBefore(now) || reminder.reminderTime.isAtSameMomentAs(now)) {
        _showNotification(reminder);
      }
    }
  }

  // Show notification
  Future<void> _showNotification(Reminder reminder) async {
    const AndroidNotificationDetails androidPlatformChannelSpecifics =
        AndroidNotificationDetails(
      'reminders_channel',
      'التذكيرات',
      channelDescription: 'إشعارات التذكيرات',
      importance: Importance.high,
      priority: Priority.high,
      showWhen: true,
    );

    const NotificationDetails platformChannelSpecifics = NotificationDetails(
      android: androidPlatformChannelSpecifics,
    );

    await _notificationsPlugin.show(
      reminder.id.hashCode,
      reminder.title,
      reminder.description,
      platformChannelSpecifics,
    );
  }

  // Get reminders by type
  List<Reminder> getRemindersByType(String type) {
    return _reminders.where((r) => r.type == type && !r.isCompleted).toList();
  }

  // Get reminders for today
  List<Reminder> getTodayReminders() {
    final now = DateTime.now();
    final startOfDay = DateTime(now.year, now.month, now.day);
    final endOfDay = startOfDay.add(const Duration(days: 1));

    return _reminders
        .where((r) =>
            !r.isCompleted &&
            r.reminderTime.isAfter(startOfDay) &&
            r.reminderTime.isBefore(endOfDay))
        .toList()
      ..sort((a, b) => a.reminderTime.compareTo(b.reminderTime));
  }

  // Get overdue reminders
  List<Reminder> getOverdueReminders() {
    final now = DateTime.now();
    return _reminders
        .where((r) => !r.isCompleted && r.reminderTime.isBefore(now))
        .toList()
      ..sort((a, b) => a.reminderTime.compareTo(b.reminderTime));
  }

  @override
  void dispose() {
    _reminderCheckTimer?.cancel();
    super.dispose();
  }
}

// Reminder Types
class ReminderType {
  static const String maintenance = 'maintenance';
  static const String document = 'document';
  static const String appointment = 'appointment';
  static const String custom = 'custom';
  static const String license = 'license';
  static const String insurance = 'insurance';
  static const String inspection = 'inspection';
}

// Recurring Patterns
class RecurringPattern {
  static const String daily = 'daily';
  static const String weekly = 'weekly';
  static const String monthly = 'monthly';
  static const String yearly = 'yearly';
}
