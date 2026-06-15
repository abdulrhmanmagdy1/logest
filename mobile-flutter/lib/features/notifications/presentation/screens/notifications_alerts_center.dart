// ============================================
// 🔔 Notifications & Alerts Center
// Centralized Notification Hub with Smart Filtering
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/widgets/premium/premium_stat_card.dart';
import '../../../../core/design_system/shadows.dart';

class NotificationsAlertsCenter extends StatefulWidget {
  const NotificationsAlertsCenter({super.key});

  @override
  State<NotificationsAlertsCenter> createState() => _NotificationsAlertsCenterState();
}

class _NotificationsAlertsCenterState extends State<NotificationsAlertsCenter>
    with TickerProviderStateMixin {
  late AnimationController _headerController;
  late AnimationController _listController;
  late AnimationController _filtersController;
  
  // Data
  List<NotificationItem> _notifications = [];
  List<NotificationItem> _alerts = [];
  List<NotificationCategory> _categories = [];
  String _selectedCategory = 'all';
  String _selectedType = 'all';
  bool _showUnreadOnly = false;
  
  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _loadNotifications();
  }

  void _initializeAnimations() {
    _headerController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _listController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _filtersController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    
    _headerController.forward();
    _listController.forward();
    _filtersController.forward();
  }

  void _loadNotifications() {
    _notifications = [
      NotificationItem(
        id: 'NOTIF-001',
        title: 'شحنة جديدة',
        message: 'طلب #EDH-1001 تم إنشاؤه بنجاح',
        type: NotificationType.shipment,
        priority: NotificationPriority.normal,
        timestamp: DateTime.now().subtract(const Duration(minutes: 5)),
        isRead: false,
        icon: Icons.local_shipping,
        actionUrl: '/shipment/EDH-1001',
      ),
      NotificationItem(
        id: 'NOTIF-002',
        title: 'تحديث درجة الحرارة',
        message: 'شحنة #EDH-1002: درجة الحرارة تجاوزت الحد المسموح',
        type: NotificationType.temperature,
        priority: NotificationPriority.high,
        timestamp: DateTime.now().subtract(const Duration(minutes: 15)),
        isRead: false,
        icon: Icons.thermostat,
        actionUrl: '/monitoring/EDH-1002',
      ),
      NotificationItem(
        id: 'NOTIF-003',
        title: 'تأكيد الدفع',
        message: 'تم استلام الدفع لفاتورة #INV-001',
        type: NotificationType.payment,
        priority: NotificationPriority.normal,
        timestamp: DateTime.now().subtract(const Duration(hours: 1)),
        isRead: true,
        icon: Icons.payment,
        actionUrl: '/billing/INV-001',
      ),
      NotificationItem(
        id: 'NOTIF-004',
        title: 'صيانة مجدولة',
        message: 'شاحنة #VH-123 تحتاج إلى صيانة دورية',
        type: NotificationType.maintenance,
        priority: NotificationPriority.medium,
        timestamp: DateTime.now().subtract(const Duration(hours: 2)),
        isRead: false,
        icon: Icons.build,
        actionUrl: '/workshop/VH-123',
      ),
      NotificationItem(
        id: 'NOTIF-005',
        title: 'تقييم الرحلة',
        message: 'يرجى تقييم الرحلة #TRIP-001',
        type: NotificationType.rating,
        priority: NotificationPriority.low,
        timestamp: DateTime.now().subtract(const Duration(hours: 3)),
        isRead: true,
        icon: Icons.star_rate,
        actionUrl: '/rating/TRIP-001',
      ),
      NotificationItem(
        id: 'NOTIF-006',
        title: 'إنذار زحام',
        message: 'زحام مروري على طريق الملك فهد',
        type: NotificationType.traffic,
        priority: NotificationPriority.high,
        timestamp: DateTime.now().subtract(const Duration(minutes: 30)),
        isRead: false,
        icon: Icons.traffic,
        actionUrl: '/navigation/traffic',
      ),
      NotificationItem(
        id: 'NOTIF-007',
        title: 'تحديث النظام',
        message: 'تم تحديث النظام إلى الإصدار 2.1.0',
        type: NotificationType.system,
        priority: NotificationPriority.low,
        timestamp: DateTime.now().subtract(const Duration(days: 1)),
        isRead: true,
        icon: Icons.system_update,
        actionUrl: '/system/update',
      ),
    ];

    _categories = [
      NotificationCategory(
        type: 'all',
        name: 'الكل',
        icon: Icons.apps,
        count: _notifications.length,
        color: AppTheme.primary,
      ),
      NotificationCategory(
        type: 'shipment',
        name: 'الشحنات',
        icon: Icons.local_shipping,
        count: _notifications.where((n) => n.type == NotificationType.shipment).length,
        color: AppTheme.success,
      ),
      NotificationCategory(
        type: 'temperature',
        name: 'درجة الحرارة',
        icon: Icons.thermostat,
        count: _notifications.where((n) => n.type == NotificationType.temperature).length,
        color: AppTheme.error,
      ),
      NotificationCategory(
        type: 'payment',
        name: 'المدفوعات',
        icon: Icons.payment,
        count: _notifications.where((n) => n.type == NotificationType.payment).length,
        color: AppTheme.accent,
      ),
      NotificationCategory(
        type: 'maintenance',
        name: 'الصيانة',
        icon: Icons.build,
        count: _notifications.where((n) => n.type == NotificationType.maintenance).length,
        color: AppTheme.primary,
      ),
      NotificationCategory(
        type: 'traffic',
        name: 'المرور',
        icon: Icons.traffic,
        count: _notifications.where((n) => n.type == NotificationType.traffic).length,
        color: AppTheme.error,
      ),
    ];

    // Separate alerts from notifications
    _alerts = _notifications.where((n) => n.priority == NotificationPriority.high).toList();
  }

  @override
  void dispose() {
    _headerController.dispose();
    _listController.dispose();
    _filtersController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: _buildPremiumAppBar(),
      body: Column(
        children: [
          // Header with Stats
          _buildHeaderSection(),
          
          // Categories and Filters
          _buildCategoriesSection(),
          
          // Notifications List
          Expanded(
            child: _buildNotificationsList(),
          ),
        ],
      ),
    );
  }

  PreferredSizeWidget _buildPremiumAppBar() {
    return AppBar(
      backgroundColor: Colors.transparent,
      elevation: 0,
      title: Row(
        children: [
          GlassContainer(
            width: 40,
            height: 40,
            radius: 20,
            backgroundColor: AppTheme.primary.withOpacity(0.1),
            borderColor: AppTheme.primary.withOpacity(0.3),
            child: const Icon(
              Icons.notifications,
              color: AppTheme.primary,
              size: 20,
            ),
          ),
          const SizedBox(width: 12),
          Text(
            'مركز الإشعارات',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
      actions: [
        Stack(
          children: [
            IconButton(
              onPressed: _markAllAsRead,
              icon: const Icon(Icons.done_all, color: AppTheme.primary),
            ),
            if (_getUnreadCount() > 0)
              Positioned(
                top: 8,
                right: 8,
                child: Container(
                  width: 12,
                  height: 12,
                  decoration: BoxDecoration(
                    color: AppTheme.error,
                    borderRadius: BorderRadius.circular(6),
                  ),
                ),
              ),
          ],
        ),
        IconButton(
          onPressed: _showSettings,
          icon: const Icon(Icons.settings, color: AppTheme.primary),
        ),
        const SizedBox(width: 20),
      ],
    );
  }

  Widget _buildHeaderSection() {
    return GlassContainer(
      margin: const EdgeInsets.all(20),
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: AppTheme.primary.withOpacity(0.1),
      borderColor: AppTheme.primary.withOpacity(0.3),
      boxShadow: AppShadows.glowing(AppTheme.primary),
      child: Column(
        children: [
          Row(
            children: [
              Icon(
                Icons.notifications_active,
                color: AppTheme.primary,
                size: 32,
              ),
              const SizedBox(width: 16),
              Text(
                'مركز الإشعارات والتنبيهات',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 20),
          
          // Stats Cards
          Row(
            children: [
              Expanded(
                child: PremiumStatCard(
                  title: 'الإشعارات غير المقروءة',
                  value: '${_getUnreadCount()}',
                  change: '${_getHighPriorityCount()} عالية',
                  icon: Icons.mark_email_unread,
                  color: AppTheme.error,
                  animationDelay: const Duration(milliseconds: 200),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'التنبيهات النشطة',
                  value: '${_alerts.length}',
                  change: 'تتطلب انتباه',
                  icon: Icons.warning,
                  color: AppTheme.accent,
                  animationDelay: const Duration(milliseconds: 400),
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _headerController)
      .fadeIn()
      .slideY(begin: 0.2, end: 0);
  }

  Widget _buildCategoriesSection() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Filter Options
          Row(
            children: [
              Text(
                'التصفية',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const Spacer(),
              Row(
                children: [
                  _buildFilterChip('all', 'الكل'),
                  _buildPriorityChip('high', 'عالية'),
                  _buildPriorityChip('normal', 'عادية'),
                  _buildUnreadOnlyChip(),
                ],
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Categories
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            child: Row(
              children: _categories.map((category) => _buildCategoryChip(category)).toList(),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterChip(String filter, String label) {
    final isSelected = _selectedType == filter;
    
    return Padding(
      padding: const EdgeInsets.only(left: 8),
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedType = filter;
          });
        },
        child: GlassContainer(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
          radius: 16,
          backgroundColor: isSelected 
            ? AppTheme.primary.withOpacity(0.2)
            : Colors.white.withOpacity(0.05),
          borderColor: isSelected 
            ? AppTheme.primary.withOpacity(0.4)
            : AppTheme.textHint.withOpacity(0.2),
          child: Text(
            label,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: isSelected ? AppTheme.primary : AppTheme.textSecondary,
              fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildPriorityChip(String priority, String label) {
    final isSelected = _selectedType == priority;
    
    return Padding(
      padding: const EdgeInsets.only(left: 8),
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedType = priority;
          });
        },
        child: GlassContainer(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
          radius: 16,
          backgroundColor: isSelected 
            ? AppTheme.accent.withOpacity(0.2)
            : Colors.white.withOpacity(0.05),
          borderColor: isSelected 
            ? AppTheme.accent.withOpacity(0.4)
            : AppTheme.textHint.withOpacity(0.2),
          child: Text(
            label,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: isSelected ? AppTheme.accent : AppTheme.textSecondary,
              fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildUnreadOnlyChip() {
    return Padding(
      padding: const EdgeInsets.only(left: 8),
      child: InkWell(
        onTap: () {
          setState(() {
            _showUnreadOnly = !_showUnreadOnly;
          });
        },
        child: GlassContainer(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
          radius: 16,
          backgroundColor: _showUnreadOnly 
            ? AppTheme.primary.withOpacity(0.2)
            : Colors.white.withOpacity(0.05),
          borderColor: _showUnreadOnly 
            ? AppTheme.primary.withOpacity(0.4)
            : AppTheme.textHint.withOpacity(0.2),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                Icons.mark_email_unread,
                color: _showUnreadOnly ? AppTheme.primary : AppTheme.textHint,
                size: 16,
              ),
              const SizedBox(width: 4),
              Text(
                'غير مقروء',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: _showUnreadOnly ? AppTheme.primary : AppTheme.textSecondary,
                  fontWeight: _showUnreadOnly ? FontWeight.w600 : FontWeight.normal,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildCategoryChip(NotificationCategory category) {
    final isSelected = _selectedCategory == category.type;
    
    return Padding(
      padding: const EdgeInsets.only(right: 8),
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedCategory = category.type;
          });
        },
        child: GlassContainer(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          radius: 20,
          backgroundColor: isSelected 
            ? category.color.withOpacity(0.2)
            : Colors.white.withOpacity(0.05),
          borderColor: isSelected 
            ? category.color.withOpacity(0.4)
            : AppTheme.textHint.withOpacity(0.2),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                category.icon,
                color: isSelected ? category.color : AppTheme.textHint,
                size: 20,
              ),
              const SizedBox(width: 8),
              Text(
                category.name,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: isSelected ? category.color : AppTheme.textSecondary,
                  fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
                ),
              ),
              if (category.count > 0) ...[
                const SizedBox(width: 8),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                  decoration: BoxDecoration(
                    color: category.color,
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Text(
                    '${category.count}',
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildNotificationsList() {
    final filteredNotifications = _getFilteredNotifications();
    
    if (filteredNotifications.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.notifications_none,
              color: AppTheme.textHint,
              size: 64,
            ),
            const SizedBox(height: 16),
            Text(
              'لا توجد إشعارات',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                color: AppTheme.textHint,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'جميع الإشعارات مقروءة أو لا توجد إشعارات مطابقة للتصفية',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppTheme.textSecondary,
              ),
            ),
          ],
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.fromLTRB(20, 0, 20, 20),
      itemCount: filteredNotifications.length,
      itemBuilder: (context, index) {
        final notification = filteredNotifications[index];
        return _buildNotificationCard(notification, index);
      },
    );
  }

  Widget _buildNotificationCard(NotificationItem notification, int index) {
    Color priorityColor = _getPriorityColor(notification.priority);
    
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: notification.isRead 
        ? Colors.white.withOpacity(0.05)
        : priorityColor.withOpacity(0.1),
      borderColor: notification.isRead 
        ? AppTheme.textHint.withOpacity(0.2)
        : priorityColor.withOpacity(0.3),
      child: Column(
        children: [
          // Header
          Row(
            children: [
              Container(
                width: 40,
                height: 40,
                decoration: BoxDecoration(
                  color: priorityColor.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Icon(
                  notification.icon,
                  color: priorityColor,
                  size: 20,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Text(
                          notification.title,
                          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                            color: AppTheme.textPrimary,
                            fontWeight: notification.isRead 
                              ? FontWeight.normal 
                              : FontWeight.bold,
                          ),
                        ),
                        if (!notification.isRead)
                          Container(
                            margin: const EdgeInsets.only(left: 8),
                            width: 8,
                            height: 8,
                            decoration: BoxDecoration(
                              color: priorityColor,
                              borderRadius: BorderRadius.circular(4),
                            ),
                          ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      notification.message,
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.textSecondary,
                      ),
                    ),
                  ],
                ),
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    _formatTime(notification.timestamp),
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: AppTheme.textHint,
                    ),
                  ),
                  const SizedBox(height: 4),
                  _buildPriorityBadge(notification.priority),
                ],
              ),
            ],
          ),
          
          // Actions
          const SizedBox(height: 12),
          Row(
            children: [
              Expanded(
                child: GlowingButton(
                  text: 'عرض',
                  onPressed: () => _viewNotification(notification),
                  color: AppTheme.primary,
                  height: 36,
                ),
              ),
              const SizedBox(width: 8),
              if (!notification.isRead)
                Expanded(
                  child: GlowingButton(
                    text: 'علامة كمقروء',
                    onPressed: () => _markAsRead(notification),
                    color: AppTheme.success,
                    height: 36,
                  ),
                ),
              const SizedBox(width: 8),
              GlassContainer(
                width: 36,
                height: 36,
                radius: 18,
                backgroundColor: Colors.white.withOpacity(0.05),
                borderColor: AppTheme.textHint.withOpacity(0.2),
                child: IconButton(
                  onPressed: () => _showNotificationOptions(notification),
                  icon: const Icon(
                    Icons.more_vert,
                    color: AppTheme.textHint,
                    size: 16,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _listController)
      .fadeIn(delay: Duration(milliseconds: index * 50))
      .slideX(begin: -0.1, end: 0);
  }

  Widget _buildPriorityBadge(NotificationPriority priority) {
    Color color;
    String text;
    
    switch (priority) {
      case NotificationPriority.high:
        color = AppTheme.error;
        text = 'عالية';
        break;
      case NotificationPriority.medium:
        color = AppTheme.accent;
        text = 'متوسطة';
        break;
      case NotificationPriority.low:
        color = AppTheme.success;
        text = 'منخفضة';
        break;
      case NotificationPriority.normal:
        color = AppTheme.primary;
        text = 'عادية';
        break;
    }
    
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(
        color: color.withOpacity(0.2),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Text(
        text,
        style: Theme.of(context).textTheme.bodySmall?.copyWith(
          color: color,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  // Helper methods
  List<NotificationItem> _getFilteredNotifications() {
    List<NotificationItem> filtered = _notifications;
    
    // Filter by category
    if (_selectedCategory != 'all') {
      filtered = filtered.where((n) => n.type.name == _selectedCategory).toList();
    }
    
    // Filter by priority
    if (_selectedType == 'high') {
      filtered = filtered.where((n) => n.priority == NotificationPriority.high).toList();
    } else if (_selectedType == 'normal') {
      filtered = filtered.where((n) => n.priority != NotificationPriority.high).toList();
    }
    
    // Filter by read status
    if (_showUnreadOnly) {
      filtered = filtered.where((n) => !n.isRead).toList();
    }
    
    // Sort by timestamp (newest first)
    filtered.sort((a, b) => b.timestamp.compareTo(a.timestamp));
    
    return filtered;
  }

  int _getUnreadCount() {
    return _notifications.where((n) => !n.isRead).length;
  }

  int _getHighPriorityCount() {
    return _notifications.where((n) => n.priority == NotificationPriority.high && !n.isRead).length;
  }

  Color _getPriorityColor(NotificationPriority priority) {
    switch (priority) {
      case NotificationPriority.high:
        return AppTheme.error;
      case NotificationPriority.medium:
        return AppTheme.accent;
      case NotificationPriority.low:
        return AppTheme.success;
      case NotificationPriority.normal:
        return AppTheme.primary;
      default:
        return AppTheme.textHint;
    }
  }

  String _formatTime(DateTime timestamp) {
    final now = DateTime.now();
    final difference = now.difference(timestamp);
    
    if (difference.inMinutes == 0) {
      return 'الآن';
    } else if (difference.inMinutes < 60) {
      return 'منذ ${difference.inMinutes} دقيقة';
    } else if (difference.inHours < 24) {
      return 'منذ ${difference.inHours} ساعة';
    } else if (difference.inDays < 7) {
      return 'منذ ${difference.inDays} يوم';
    } else {
      return '${timestamp.day}/${timestamp.month}/${timestamp.year}';
    }
  }

  // Action methods
  void _markAllAsRead() {
    setState(() {
      for (var notification in _notifications) {
        notification.isRead = true;
      }
    });
    
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        backgroundColor: AppTheme.success,
        content: Text('تم تعليم جميع الإشعارات كمقروءة'),
      ),
    );
  }

  void _markAsRead(NotificationItem notification) {
    setState(() {
      notification.isRead = true;
    });
    
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        backgroundColor: AppTheme.success,
        content: Text('تم تعليم الإشعار كمقروء'),
      ),
    );
  }

  void _viewNotification(NotificationItem notification) {
    // Navigate to notification details
    if (notification.actionUrl.isNotEmpty) {
      // Navigate to specific screen
      Navigator.pushNamed(context, notification.actionUrl);
    }
  }

  void _showNotificationOptions(NotificationItem notification) {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (context) => GlassContainer(
        margin: const EdgeInsets.all(20),
        padding: const EdgeInsets.all(20),
        radius: 20,
        backgroundColor: AppTheme.background.withOpacity(0.95),
        borderColor: AppTheme.primary.withOpacity(0.3),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'خيارات الإشعار',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 20),
            ListTile(
              leading: const Icon(Icons.open_in_new, color: AppTheme.primary),
              title: const Text('فتح'),
              onTap: () {
                Navigator.pop(context);
                _viewNotification(notification);
              },
            ),
            ListTile(
              leading: const Icon(Icons.share, color: AppTheme.accent),
              title: const Text('مشاركة'),
              onTap: () {
                Navigator.pop(context);
                // Share notification
              },
            ),
            ListTile(
              leading: const Icon(Icons.delete, color: AppTheme.error),
              title: const Text('حذف'),
              onTap: () {
                Navigator.pop(context);
                setState(() {
                  _notifications.remove(notification);
                });
              },
            ),
            const SizedBox(height: 20),
            GlowingButton(
              text: 'إغلاق',
              onPressed: () => Navigator.pop(context),
              color: AppTheme.primary,
            ),
          ],
        ),
      ),
    );
  }

  void _showSettings() {
    // Show notification settings
  }
}

// Data models
enum NotificationType { shipment, temperature, payment, maintenance, rating, traffic, system }
enum NotificationPriority { high, medium, low, normal }

class NotificationItem {
  String id;
  String title;
  String message;
  NotificationType type;
  NotificationPriority priority;
  DateTime timestamp;
  bool isRead;
  IconData icon;
  String actionUrl;

  NotificationItem({
    required this.id,
    required this.title,
    required this.message,
    required this.type,
    required this.priority,
    required this.timestamp,
    required this.isRead,
    required this.icon,
    required this.actionUrl,
  });
}

class NotificationCategory {
  String type;
  String name;
  IconData icon;
  int count;
  Color color;

  NotificationCategory({
    required this.type,
    required this.name,
    required this.icon,
    required this.count,
    required this.color,
  });
}
