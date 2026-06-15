import 'package:flutter/material.dart';
import '../services/api_service.dart';

class NotificationsScreen extends StatefulWidget {
  const NotificationsScreen({super.key});

  @override
  State<NotificationsScreen> createState() => _NotificationsScreenState();
}

class _NotificationsScreenState extends State<NotificationsScreen> {
  List<Map<String, dynamic>> _notifications = [];
  bool _isLoading = true;
  String _filter = 'all'; // all, unread, read

  @override
  void initState() {
    super.initState();
    _loadNotifications();
  }

  Future<void> _loadNotifications() async {
    setState(() => _isLoading = true);
    try {
      // Simulated data - replace with actual API call
      await Future.delayed(const Duration(milliseconds: 500));
      setState(() {
        _notifications = [
          {
            'id': '1',
            'title': 'شحنة جديدة',
            'message': 'تم إنشاء شحنة جديدة برقم SHP-2024-001',
            'type': 'shipment',
            'read': false,
            'createdAt': DateTime.now().subtract(const Duration(minutes: 5)),
          },
          {
            'id': '2',
            'title': 'تذكير صيانة',
            'message': 'صيانة مجدولة للشاحنة A-123 غداً',
            'type': 'maintenance',
            'read': false,
            'createdAt': DateTime.now().subtract(const Duration(hours: 1)),
          },
          {
            'id': '3',
            'title': 'تم تسليم الشحنة',
            'message': 'تم تسليم الشحنة SHP-2024-002 بنجاح',
            'type': 'delivery',
            'read': true,
            'createdAt': DateTime.now().subtract(const Duration(hours: 3)),
          },
          {
            'id': '4',
            'title': 'فاتيرة جديدة',
            'message': 'تم إصدار فاتورة جديدة بقيمة 2,500 ريال',
            'type': 'invoice',
            'read': true,
            'createdAt': DateTime.now().subtract(const Duration(days: 1)),
          },
        ];
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('خطأ في تحميل الإشعارات: $e')),
      );
    }
  }

  IconData _getIconForType(String type) {
    switch (type) {
      case 'shipment':
        return Icons.local_shipping;
      case 'maintenance':
        return Icons.build;
      case 'delivery':
        return Icons.check_circle;
      case 'invoice':
        return Icons.receipt;
      default:
        return Icons.notifications;
    }
  }

  Color _getColorForType(String type) {
    switch (type) {
      case 'shipment':
        return const Color(0xFF0099D8);
      case 'maintenance':
        return const Color(0xFFF59E0B);
      case 'delivery':
        return const Color(0xFF10B981);
      case 'invoice':
        return const Color(0xFF8B5CF6);
      default:
        return const Color(0xFF0099D8);
    }
  }

  String _formatTime(DateTime dateTime) {
    final now = DateTime.now();
    final diff = now.difference(dateTime);

    if (diff.inMinutes < 60) {
      return 'منذ ${diff.inMinutes} دقيقة';
    } else if (diff.inHours < 24) {
      return 'منذ ${diff.inHours} ساعة';
    } else {
      return 'منذ ${diff.inDays} يوم';
    }
  }

  List<Map<String, dynamic>> get _filteredNotifications {
    switch (_filter) {
      case 'unread':
        return _notifications.where((n) => !n['read']).toList();
      case 'read':
        return _notifications.where((n) => n['read']).toList();
      default:
        return _notifications;
    }
  }

  void _markAsRead(String id) {
    setState(() {
      final index = _notifications.indexWhere((n) => n['id'] == id);
      if (index != -1) {
        _notifications[index]['read'] = true;
      }
    });
  }

  void _markAllAsRead() {
    setState(() {
      for (var notification in _notifications) {
        notification['read'] = true;
      }
    });
  }

  void _deleteNotification(String id) {
    setState(() {
      _notifications.removeWhere((n) => n['id'] == id);
    });
  }

  @override
  Widget build(BuildContext context) {
    final unreadCount = _notifications.where((n) => !n['read']).length;

    return Scaffold(
      appBar: AppBar(
        title: const Text('الإشعارات'),
        centerTitle: true,
        actions: [
          if (unreadCount > 0)
            TextButton.icon(
              onPressed: _markAllAsRead,
              icon: const Icon(Icons.done_all, size: 20),
              label: const Text('قراءة الكل'),
              style: TextButton.styleFrom(
                foregroundColor: const Color(0xFFD4AF37),
              ),
            ),
        ],
      ),
      body: Column(
        children: [
          // Filter Chips
          Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              children: [
                ChoiceChip(
                  label: const Text('الكل'),
                  selected: _filter == 'all',
                  selectedColor: const Color(0xFF0099D8),
                  onSelected: (selected) {
                    if (selected) setState(() => _filter = 'all');
                  },
                ),
                const SizedBox(width: 8),
                ChoiceChip(
                  label: Text('غير مقروء ($unreadCount)'),
                  selected: _filter == 'unread',
                  selectedColor: const Color(0xFF0099D8),
                  onSelected: (selected) {
                    if (selected) setState(() => _filter = 'unread');
                  },
                ),
                const SizedBox(width: 8),
                ChoiceChip(
                  label: const Text('مقروء'),
                  selected: _filter == 'read',
                  selectedColor: const Color(0xFF0099D8),
                  onSelected: (selected) {
                    if (selected) setState(() => _filter = 'read');
                  },
                ),
              ],
            ),
          ),

          // Notifications List
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _filteredNotifications.isEmpty
                    ? _buildEmptyState()
                    : RefreshIndicator(
                        onRefresh: _loadNotifications,
                        child: ListView.builder(
                          padding: const EdgeInsets.symmetric(horizontal: 16),
                          itemCount: _filteredNotifications.length,
                          itemBuilder: (context, index) {
                            final notification = _filteredNotifications[index];
                            return _buildNotificationCard(notification);
                          },
                        ),
                      ),
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.notifications_off,
            size: 64,
            color: const Color(0xFF0099D8).withOpacity(0.5),
          ),
          const SizedBox(height: 16),
          const Text(
            'لا توجد إشعارات',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: Colors.white,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            _filter == 'unread'
                ? 'لا توجد إشعارات جديدة'
                : 'ستظهر الإشعارات هنا',
            style: const TextStyle(color: Colors.white70),
          ),
        ],
      ),
    );
  }

  Widget _buildNotificationCard(Map<String, dynamic> notification) {
    final isUnread = !notification['read'];
    final type = notification['type'] as String;

    return Dismissible(
      key: Key(notification['id']),
      direction: DismissDirection.endToStart,
      background: Container(
        margin: const EdgeInsets.only(bottom: 8),
        decoration: BoxDecoration(
          color: Colors.red,
          borderRadius: BorderRadius.circular(12),
        ),
        alignment: Alignment.centerLeft,
        padding: const EdgeInsets.only(left: 20),
        child: const Icon(Icons.delete, color: Colors.white),
      ),
      onDismissed: (_) => _deleteNotification(notification['id']),
      child: Card(
        margin: const EdgeInsets.only(bottom: 8),
        color: isUnread ? const Color(0xFF005A7F) : const Color(0xFF004050),
        child: ListTile(
          contentPadding: const EdgeInsets.all(12),
          leading: Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              color: _getColorForType(type).withOpacity(0.2),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Icon(
              _getIconForType(type),
              color: _getColorForType(type),
              size: 24,
            ),
          ),
          title: Row(
            children: [
              Expanded(
                child: Text(
                  notification['title'],
                  style: TextStyle(
                    fontWeight: isUnread ? FontWeight.bold : FontWeight.normal,
                    color: Colors.white,
                  ),
                ),
              ),
              if (isUnread)
                Container(
                  width: 8,
                  height: 8,
                  decoration: const BoxDecoration(
                    color: Color(0xFFD4AF37),
                    shape: BoxShape.circle,
                  ),
                ),
            ],
          ),
          subtitle: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 4),
              Text(
                notification['message'],
                style: const TextStyle(color: Colors.white70),
              ),
              const SizedBox(height: 4),
              Text(
                _formatTime(notification['createdAt']),
                style: TextStyle(
                  fontSize: 12,
                  color: const Color(0xFF0099D8),
                ),
              ),
            ],
          ),
          onTap: () => _markAsRead(notification['id']),
        ),
      ),
    );
  }
}
