import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class AdminPanelService extends ChangeNotifier {
  Map<String, dynamic> _dashboardData = {};
  List<Map<String, dynamic>> _widgets = [];
  List<Map<String, dynamic>> _alerts = [];
  Map<String, dynamic> _systemHealth = {};
  bool _isLoading = false;
  String? _error;

  Map<String, dynamic> get dashboardData => _dashboardData;
  List<Map<String, dynamic>> get widgets => _widgets;
  List<Map<String, dynamic>> get alerts => _alerts;
  Map<String, dynamic> get systemHealth => _systemHealth;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Get live dashboard data
  Future<void> fetchLiveDashboardData() async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/admin/dashboard/live'),
      );

      if (response.statusCode == 200) {
        _dashboardData = jsonDecode(response.body);
      }
    } catch (e) {
      debugPrint('Error fetching live dashboard: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get widgets configuration
  Future<void> fetchWidgets() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/admin/widgets'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _widgets = List<Map<String, dynamic>>.from(data['widgets'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching widgets: $e');
    }
  }

  // Save widgets layout
  Future<bool> saveWidgetsLayout(List<Map<String, dynamic>> layout) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/admin/widgets/layout'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'layout': layout}),
      );

      if (response.statusCode == 200) {
        _widgets = layout;
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error saving widgets layout: $e');
      return false;
    }
  }

  // Add widget
  Future<bool> addWidget(Map<String, dynamic> widget) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/admin/widgets'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(widget),
      );

      if (response.statusCode == 201) {
        _widgets.add(widget);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error adding widget: $e');
      return false;
    }
  }

  // Remove widget
  Future<bool> removeWidget(String widgetId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_apiBaseUrl/admin/widgets/$widgetId'),
      );

      if (response.statusCode == 200) {
        _widgets.removeWhere((w) => w['id'] == widgetId);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error removing widget: $e');
      return false;
    }
  }

  // Update widget
  Future<bool> updateWidget(String widgetId, Map<String, dynamic> widgetData) async {
    try {
      final response = await http.put(
        Uri.parse('$_apiBaseUrl/admin/widgets/$widgetId'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(widgetData),
      );

      if (response.statusCode == 200) {
        final index = _widgets.indexWhere((w) => w['id'] == widgetId);
        if (index != -1) {
          _widgets[index] = widgetData;
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating widget: $e');
      return false;
    }
  }

  // Get alerts
  Future<void> fetchAlerts({
    String? severity,
    int limit = 50,
  }) async {
    try {
      String url = '$_apiBaseUrl/admin/alerts?limit=$limit';
      if (severity != null) url += '&severity=$severity';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _alerts = List<Map<String, dynamic>>.from(data['alerts'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching alerts: $e');
    }
  }

  // Create alert
  Future<bool> createAlert(Map<String, dynamic> alertData) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/admin/alerts'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(alertData),
      );

      if (response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error creating alert: $e');
      return false;
    }
  }

  // Dismiss alert
  Future<bool> dismissAlert(String alertId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/admin/alerts/$alertId/dismiss'),
      );

      if (response.statusCode == 200) {
        _alerts.removeWhere((a) => a['id'] == alertId);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error dismissing alert: $e');
      return false;
    }
  }

  // Get system health
  Future<void> fetchSystemHealth() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/admin/system/health'),
      );

      if (response.statusCode == 200) {
        _systemHealth = jsonDecode(response.body);
      }
    } catch (e) {
      debugPrint('Error fetching system health: $e');
    }
  }

  // Get real-time metrics
  Stream<Map<String, dynamic>> getRealTimeMetrics() {
    return Stream.periodic(
      const Duration(seconds: 5),
      (_) => {
        'activeTrips': _dashboardData['activeTrips'] ?? 0,
        'activeDrivers': _dashboardData['activeDrivers'] ?? 0,
        'pendingRequests': _dashboardData['pendingRequests'] ?? 0,
        'todayRevenue': _dashboardData['todayRevenue'] ?? 0.0,
        'serverLoad': _systemHealth['serverLoad'] ?? 0.0,
        'databaseLoad': _systemHealth['databaseLoad'] ?? 0.0,
        'activeUsers': _dashboardData['activeUsers'] ?? 0,
      },
    );
  }

  // Get widget data
  Future<Map<String, dynamic>> getWidgetData(String widgetType) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/admin/widgets/$widgetType/data'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching widget data: $e');
      return {};
    }
  }

  // Export dashboard
  Future<String?> exportDashboard({
    required String format, // pdf, excel, image
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/admin/dashboard/export?format=$format'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['downloadUrl'];
      }
      return null;
    } catch (e) {
      debugPrint('Error exporting dashboard: $e');
      return null;
    }
  }

  // Get user activity
  Future<List<Map<String, dynamic>>> getUserActivity({
    int limit = 100,
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/admin/activity?limit=$limit'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['activities'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching user activity: $e');
      return [];
    }
  }

  // Get system logs
  Future<List<Map<String, dynamic>>> getSystemLogs({
    String? level,
    int limit = 100,
  }) async {
    try {
      String url = '$_apiBaseUrl/admin/logs?limit=$limit';
      if (level != null) url += '&level=$level';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['logs'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching system logs: $e');
      return [];
    }
  }

  // Get performance metrics
  Future<Map<String, dynamic>> getPerformanceMetrics({
    String period = 'hour',
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/admin/performance?period=$period'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching performance metrics: $e');
      return {};
    }
  }

  // Get API usage stats
  Future<Map<String, dynamic>> getAPIUsageStats({
    String period = 'day',
  }) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/admin/api-usage?period=$period'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching API usage stats: $e');
      return {};
    }
  }

  // Get storage usage
  Future<Map<String, dynamic>> getStorageUsage() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/admin/storage'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching storage usage: $e');
      return {};
    }
  }

  // Clear cache
  Future<bool> clearCache({String? cacheType}) async {
    try {
      String url = '$_apiBaseUrl/admin/cache/clear';
      if (cacheType != null) url += '?type=$cacheType';

      final response = await http.post(Uri.parse(url));

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error clearing cache: $e');
      return false;
    }
  }

  // Restart service
  Future<bool> restartService(String serviceName) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/admin/services/$serviceName/restart'),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error restarting service: $e');
      return false;
    }
  }

  // Get service status
  Future<Map<String, dynamic>> getServiceStatus() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/admin/services/status'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching service status: $e');
      return {};
    }
  }

  // Get backup status
  Future<Map<String, dynamic>> getBackupStatus() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/admin/backup/status'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching backup status: $e');
      return {};
    }
  }

  // Create backup
  Future<bool> createBackup() async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/admin/backup/create'),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error creating backup: $e');
      return false;
    }
  }

  // Restore backup
  Future<bool> restoreBackup(String backupId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/admin/backup/$backupId/restore'),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error restoring backup: $e');
      return false;
    }
  }
}

// Widget Types
class WidgetType {
  static const String stats = 'stats';
  static const String chart = 'chart';
  static const String map = 'map';
  static const String table = 'table';
  static const String list = 'list';
  static const String alert = 'alert';
  static const String progress = 'progress';
  static const String timeline = 'timeline';
  static const String calendar = 'calendar';
  static const String weather = 'weather';
}

// Alert Severity
class AlertSeverity {
  static const String info = 'info';
  static const String warning = 'warning';
  static const String error = 'error';
  static const String critical = 'critical';
}

// Default Widgets Configuration
class DefaultWidgets {
  static List<Map<String, dynamic>> get defaultWidgets => [
    {
      'id': 'active_trips',
      'type': WidgetType.stats,
      'title': 'الرحلات النشطة',
      'position': {'x': 0, 'y': 0, 'w': 1, 'h': 1},
      'config': {'icon': 'local_shipping', 'color': '#0099D8'},
    },
    {
      'id': 'active_drivers',
      'type': WidgetType.stats,
      'title': 'السائقين النشطين',
      'position': {'x': 1, 'y': 0, 'w': 1, 'h': 1},
      'config': {'icon': 'people', 'color': '#27AE60'},
    },
    {
      'id': 'today_revenue',
      'type': WidgetType.stats,
      'title': 'إيرادات اليوم',
      'position': {'x': 2, 'y': 0, 'w': 1, 'h': 1},
      'config': {'icon': 'account_balance_wallet', 'color': '#D4AF37'},
    },
    {
      'id': 'pending_requests',
      'type': WidgetType.stats,
      'title': 'الطلبات المعلقة',
      'position': {'x': 3, 'y': 0, 'w': 1, 'h': 1},
      'config': {'icon': 'pending', 'color': '#E74C3C'},
    },
    {
      'id': 'revenue_chart',
      'type': WidgetType.chart,
      'title': 'الإيرادات',
      'position': {'x': 0, 'y': 1, 'w': 2, 'h': 2},
      'config': {'chartType': 'line', 'period': 'week'},
    },
    {
      'id': 'trips_map',
      'type': WidgetType.map,
      'title': 'الرحلات الحالية',
      'position': {'x': 2, 'y': 1, 'w': 2, 'h': 2},
      'config': {'showRoutes': true, 'showDrivers': true},
    },
    {
      'id': 'recent_alerts',
      'type': WidgetType.list,
      'title': 'التنبيهات الأخيرة',
      'position': {'x': 0, 'y': 3, 'w': 2, 'h': 2},
      'config': {'limit': 5},
    },
    {
      'id': 'top_drivers',
      'type': WidgetType.table,
      'title': 'أفضل السائقين',
      'position': {'x': 2, 'y': 3, 'w': 2, 'h': 2},
      'config': {'limit': 5},
    },
  ];
}
