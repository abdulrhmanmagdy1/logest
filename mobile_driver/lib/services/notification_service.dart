import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class NotificationService extends ChangeNotifier {
  final FlutterLocalNotificationsPlugin _localNotificationsPlugin =
      FlutterLocalNotificationsPlugin();
  final FirebaseMessaging _firebaseMessaging = FirebaseMessaging.instance;
  String? _fcmToken;
  StreamSubscription<RemoteMessage>? _messageSubscription;

  String? get fcmToken => _fcmToken;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Initialize notifications
  Future<void> initialize() async {
    // Initialize local notifications
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

    await _localNotificationsPlugin.initialize(
      initializationSettings,
      onDidReceiveNotificationResponse: _onNotificationTapped,
    );

    // Initialize Firebase Messaging
    await _initializeFirebaseMessaging();

    // Request permissions
    await _requestPermissions();
  }

  Future<void> _initializeFirebaseMessaging() async {
    // Get FCM token
    _fcmToken = await _firebaseMessaging.getToken();
    notifyListeners();

    // Listen to token refresh
    _firebaseMessaging.onTokenRefresh.listen((token) {
      _fcmToken = token;
      notifyListeners();
      _sendTokenToServer(token);
    });

    // Handle foreground messages
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      _handleForegroundMessage(message);
    });

    // Handle background messages
    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      _handleMessageOpened(message);
    });
  }

  Future<void> _requestPermissions() async {
    // Local notifications permission
    await _localNotificationsPlugin
        .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin>()
        ?.requestNotificationsPermission();

    // Firebase messaging permission
    final settings = await _firebaseMessaging.requestPermission(
      alert: true,
      announcement: false,
      badge: true,
      carPlay: false,
      criticalAlert: false,
      provisional: false,
      sound: true,
    );

    if (kDebugMode) {
      print('Notification permission: ${settings.authorizationStatus}');
    }
  }

  // Send FCM token to server
  Future<void> _sendTokenToServer(String token) async {
    try {
      await http.post(
        Uri.parse('$_apiBaseUrl/notifications/register'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'fcmToken': token}),
      );
    } catch (e) {
      debugPrint('Error sending token to server: $e');
    }
  }

  // Handle foreground message
  void _handleForegroundMessage(RemoteMessage message) {
    if (message.notification != null) {
      _showLocalNotification(
        title: message.notification!.title ?? '',
        body: message.notification!.body ?? '',
        payload: message.data.toString(),
      );
    }
  }

  // Handle message opened
  void _handleMessageOpened(RemoteMessage message) {
    // Navigate to appropriate screen based on message data
    if (message.data.containsKey('type')) {
      _handleNotificationAction(message.data);
    }
  }

  // Handle notification tap
  void _onNotificationTapped(NotificationResponse response) {
    if (response.payload != null) {
      final data = jsonDecode(response.payload!);
      _handleNotificationAction(data);
    }
  }

  // Handle notification action
  void _handleNotificationAction(Map<String, dynamic> data) {
    final type = data['type'];
    switch (type) {
      case 'new_trip':
        // Navigate to available trips
        break;
      case 'trip_update':
        // Navigate to active trip
        break;
      case 'message':
        // Navigate to messages
        break;
      default:
        break;
    }
  }

  // Show local notification
  Future<void> _showLocalNotification({
    required String title,
    required String body,
    String? payload,
  }) async {
    const AndroidNotificationDetails androidPlatformChannelSpecifics =
        AndroidNotificationDetails(
      'edham_driver_channel',
      'إدهام - إشعارات السائق',
      channelDescription: 'إشعارات مهمة للسائق',
      importance: Importance.high,
      priority: Priority.high,
      showWhen: true,
      icon: '@mipmap/ic_launcher',
      styleInformation: BigTextStyleInformation(body),
    );

    const DarwinNotificationDetails iOSPlatformChannelSpecifics =
        DarwinNotificationDetails(
      presentAlert: true,
      presentBadge: true,
      presentSound: true,
    );

    const NotificationDetails platformChannelSpecifics = NotificationDetails(
      android: androidPlatformChannelSpecifics,
      iOS: iOSPlatformChannelSpecifics,
    );

    await _localNotificationsPlugin.show(
      DateTime.now().millisecondsSinceEpoch ~/ 1000,
      title,
      body,
      platformChannelSpecifics,
      payload: payload,
    );
  }

  // Send notification to specific user
  Future<bool> sendNotificationToUser({
    required String userId,
    required String title,
    required String body,
    Map<String, dynamic>? data,
  }) async {
    try {
      await http.post(
        Uri.parse('$_apiBaseUrl/notifications/send'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'userId': userId,
          'title': title,
          'body': body,
          'data': data,
        }),
      );
      return true;
    } catch (e) {
      debugPrint('Error sending notification: $e');
      return false;
    }
  }

  // Send notification to all drivers
  Future<bool> sendBroadcastNotification({
    required String title,
    required String body,
    Map<String, dynamic>? data,
  }) async {
    try {
      await http.post(
        Uri.parse('$_apiBaseUrl/notifications/broadcast'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'target': 'drivers',
          'title': title,
          'body': body,
          'data': data,
        }),
      );
      return true;
    } catch (e) {
      debugPrint('Error sending broadcast notification: $e');
      return false;
    }
  }

  // Schedule notification
  Future<void> scheduleNotification({
    required DateTime scheduledTime,
    required String title,
    required String body,
    String? payload,
  }) async {
    const AndroidNotificationDetails androidPlatformChannelSpecifics =
        AndroidNotificationDetails(
      'edham_driver_channel',
      'إدهام - إشعارات السائق',
      channelDescription: 'إشعارات مهمة للسائق',
      importance: Importance.high,
      priority: Priority.high,
    );

    const NotificationDetails platformChannelSpecifics = NotificationDetails(
      android: androidPlatformChannelSpecifics,
    );

    await _localNotificationsPlugin.zonedSchedule(
      scheduledTime.millisecondsSinceEpoch ~/ 1000,
      title,
      body,
      tz.TZDateTime.from(scheduledTime, tz.local),
      platformChannelSpecifics,
      payload: payload,
      uiLocalNotificationDateInterpretation:
          UILocalNotificationDateInterpretation.absoluteTime,
    );
  }

  // Cancel notification
  Future<void> cancelNotification(int id) async {
    await _localNotificationsPlugin.cancel(id);
  }

  // Cancel all notifications
  Future<void> cancelAllNotifications() async {
    await _localNotificationsPlugin.cancelAll();
  }

  // Get notification channels
  Future<List<NotificationChannel>> getNotificationChannels() async {
    final androidPlugin = _localNotificationsPlugin
        .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin>();
    return await androidPlugin?.getNotificationChannels() ?? [];
  }

  @override
  void dispose() {
    _messageSubscription?.cancel();
    super.dispose();
  }
}

// Notification Types
class NotificationType {
  static const String newTrip = 'new_trip';
  static const String tripAssigned = 'trip_assigned';
  static const String tripUpdate = 'trip_update';
  static const String tripCompleted = 'trip_completed';
  static const String tripCancelled = 'trip_cancelled';
  static const String message = 'message';
  static const String payment = 'payment';
  static const String maintenance = 'maintenance';
  static const String emergency = 'emergency';
}

// Notification Templates
class NotificationTemplates {
  static Map<String, String> getTripAssignedTemplate(String tripId) {
    return {
      'title': 'تم تعيين رحلة جديدة',
      'body': 'لديك رحلة جديدة #$tripId',
      'type': NotificationType.tripAssigned,
    };
  }

  static Map<String, String> getTripUpdateTemplate(
    String status,
    String tripId,
  ) {
    final statusText = _getStatusText(status);
    return {
      'title': 'تحديث حالة الرحلة',
      'body': 'رحلة #$tripId: $statusText',
      'type': NotificationType.tripUpdate,
    };
  }

  static Map<String, String> getPaymentTemplate(String amount) {
    return {
      'title': 'تم استلام دفعة',
      'body': 'تم استلام مبلغ $amount ر.س',
      'type': NotificationType.payment,
    };
  }

  static Map<String, String> getMaintenanceTemplate(String vehicleNumber) {
    return {
      'title': 'تنبيه صيانة',
      'body': 'المركبة $vehicleNumber تحتاج صيانة',
      'type': NotificationType.maintenance,
    };
  }

  static Map<String, String> getEmergencyTemplate(String message) {
    return {
      'title': 'تنبيه طوارئ',
      'body': message,
      'type': NotificationType.emergency,
    };
  }

  static String _getStatusText(String status) {
    switch (status) {
      case 'pickup':
        return 'في الاستلام';
      case 'in_transit':
        return 'في الطريق';
      case 'arrived':
        return 'تم الوصول';
      case 'delivered':
        return 'تم التسليم';
      default:
        return status;
    }
  }
}
