import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';

class SideMenu extends StatelessWidget {
  final String userRole;
  final String userName;
  final Function(String) onMenuSelected;

  const SideMenu({
    Key? key,
    required this.userRole,
    required this.userName,
    required this.onMenuSelected,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Drawer(
      backgroundColor: const Color(0xFF1A1F2E),
      child: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [Color(0xFF1A1F2E), Color(0xFF0A0E1A)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
          ),
        ),
        child: ListView(
          padding: EdgeInsets.zero,
          children: [
            // User Profile Header
            _buildUserHeader(),
            
            const Divider(
              color: Colors.white24,
              height: 1,
            ),
            
            // Main Menu Items
            _buildMainMenu(),
            
            const Divider(
              color: Colors.white24,
              height: 1,
            ),
            
            // Role-Specific Menu Items
            _buildRoleSpecificMenu(),
            
            const Divider(
              color: Colors.white24,
              height: 1,
            ),
            
            // Settings and Support
            _buildSettingsMenu(),
            
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }

  Widget _buildUserHeader() {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFFF97316).withOpacity(0.1),
            const Color(0xFF1A1F2E),
          ],
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // User Avatar
          Container(
            width: 60,
            height: 60,
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFFF97316), Color(0xFFEA580C)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(30),
              boxShadow: [
                BoxShadow(
                  color: const Color(0xFFF97316).withOpacity(0.3),
                  blurRadius: 15,
                  offset: const Offset(0, 5),
                ),
              ],
            ),
            child: const Icon(
              Icons.person,
              color: Colors.white,
              size: 30,
            ),
          )
              .animate()
              .scale(duration: 400.ms, curve: Curves.elasticOut),

          const SizedBox(height: 16),

          // User Name
          Text(
            userName,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          )
              .animate()
              .fadeIn(delay: 200.ms, duration: 300.ms),

          const SizedBox(height: 4),

          // User Role
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
            decoration: BoxDecoration(
              color: const Color(0xFFF97316).withOpacity(0.2),
              borderRadius: BorderRadius.circular(12),
              border: Border.all(
                color: const Color(0xFFF97316).withOpacity(0.3),
                width: 1,
              ),
            ),
            child: Text(
              _getRoleDisplayName(userRole),
              style: const TextStyle(
                color: Color(0xFFF97316),
                fontSize: 12,
                fontWeight: FontWeight.w600,
              ),
            ),
          )
              .animate()
              .fadeIn(delay: 400.ms, duration: 300.ms),
        ],
      ),
    );
  }

  Widget _buildMainMenu() {
    final mainMenuItems = [
      {
        'icon': Icons.dashboard_outlined,
        'title': 'لوحة التحكم',
        'key': 'dashboard',
      },
      {
        'icon': Icons.local_shipping_outlined,
        'title': 'الشحنات',
        'key': 'shipments',
      },
      {
        'icon': Icons.map_outlined,
        'title': 'الخريطة',
        'key': 'map',
      },
      {
        'icon': Icons.notifications_outlined,
        'title': 'الإشعارات',
        'key': 'notifications',
        'badge': '3',
      },
    ];

    return Column(
      children: mainMenuItems.map((item) {
        return _buildMenuItem(
          icon: item['icon'] as IconData,
          title: item['title'] as String,
          key: item['key'] as String,
          badge: item['badge'] as String?,
        );
      }).toList(),
    );
  }

  Widget _buildRoleSpecificMenu() {
    List<Map<String, dynamic>> roleItems = [];

    switch (userRole) {
      case 'Driver':
        roleItems = [
          {'icon': Icons.play_arrow_outlined, 'title': 'بدء رحلة', 'key': 'start_trip'},
          {'icon': Icons.history_outlined, 'title': 'سجل الرحلات', 'key': 'trip_history'},
          {'icon': Icons.attach_money_outlined, 'title': 'الأرباح', 'key': 'earnings'},
          {'icon': Icons.local_gas_station_outlined, 'title': 'الوقود', 'key': 'fuel'},
          {'icon': Icons.report_problem_outlined, 'title': 'بلاغ', 'key': 'emergency'},
        ];
        break;
      case 'Supervisor':
        roleItems = [
          {'icon': Icons.people_outlined, 'title': 'السائقين', 'key': 'drivers'},
          {'icon': Icons.assignment_outlined, 'title': 'المهام', 'key': 'tasks'},
          {'icon': Icons.analytics_outlined, 'title': 'التحليلات', 'key': 'analytics'},
          {'icon': Icons.speed_outlined, 'title': 'الأداء', 'key': 'performance'},
        ];
        break;
      case 'Warehouse Manager':
        roleItems = [
          {'icon': Icons.inventory_outlined, 'title': 'المخزون', 'key': 'inventory'},
          {'icon': Icons.category_outlined, 'title': 'الأصناف', 'key': 'categories'},
          {'icon': Icons.receipt_outlined, 'title': 'الاستلام', 'key': 'receiving'},
          {'icon': Icons.send_outlined, 'title': 'الشحن', 'key': 'dispatch'},
        ];
        break;
      case 'Accountant':
        roleItems = [
          {'icon': Icons.account_balance_outlined, 'title': 'الحسابات', 'key': 'accounts'},
          {'icon': Icons.request_quote_outlined, 'title': 'الفواتير', 'key': 'invoices'},
          {'icon': Icons.payment_outlined, 'title': 'المدفوعات', 'key': 'payments'},
          {'icon': Icons.trending_up_outlined, 'title': 'التقارير', 'key': 'reports'},
        ];
        break;
      case 'Customer':
        roleItems = [
          {'icon': Icons.add_box_outlined, 'title': 'شحنة جديدة', 'key': 'new_shipment'},
          {'icon': Icons.history_outlined, 'title': 'السجل', 'key': 'history'},
          {'icon': Icons.star_outlined, 'title': 'التقييمات', 'key': 'ratings'},
          {'icon': Icons.headset_mic_outlined, 'title': 'الدعم', 'key': 'support'},
        ];
        break;
    }

    return Column(
      children: roleItems.map((item) {
        return _buildMenuItem(
          icon: item['icon'] as IconData,
          title: item['title'] as String,
          key: item['key'] as String,
        );
      }).toList(),
    );
  }

  Widget _buildSettingsMenu() {
    final settingsItems = [
      {'icon': Icons.person_outline, 'title': 'الملف الشخصي', 'key': 'profile'},
      {'icon': Icons.settings_outlined, 'title': 'الإعدادات', 'key': 'settings'},
      {'icon': Icons.help_outline, 'title': 'المساعدة', 'key': 'help'},
      {'icon': Icons.logout, 'title': 'تسجيل الخروج', 'key': 'logout'},
    ];

    return Column(
      children: settingsItems.map((item) {
        return _buildMenuItem(
          icon: item['icon'] as IconData,
          title: item['title'] as String,
          key: item['key'] as String,
          isLast: item['key'] == 'logout',
        );
      }).toList(),
    );
  }

  Widget _buildMenuItem({
    required IconData icon,
    required String title,
    required String key,
    String? badge,
    bool isLast = false,
  }) {
    return InkWell(
      onTap: () => onMenuSelected(key),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
        child: Row(
          children: [
            // Icon
            Icon(
              icon,
              color: isLast ? Colors.red : Colors.white.withOpacity(0.8),
              size: 24,
            ),
            
            const SizedBox(width: 16),
            
            // Title
            Expanded(
              child: Text(
                title,
                style: TextStyle(
                  color: isLast ? Colors.red : Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
            
            // Badge (if any)
            if (badge != null) ...[
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: const Color(0xFFF97316),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  badge,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ] else ...[
              Icon(
                Icons.arrow_forward_ios,
                color: Colors.white.withOpacity(0.4),
                size: 16,
              ),
            ],
          ],
        ),
      )
          .animate()
          .fadeIn(delay: 100.ms, duration: 200.ms)
          .slideX(begin: -0.1, end: 0, duration: 200.ms),
    );
  }

  String _getRoleDisplayName(String role) {
    switch (role) {
      case 'Driver':
        return 'سائق';
      case 'Supervisor':
        return 'مشرف';
      case 'Warehouse Manager':
        return 'مدير المستودع';
      case 'Accountant':
        return 'محاسب';
      case 'Customer':
        return 'عميل';
      case 'Admin':
        return 'مدير النظام';
      default:
        return role;
    }
  }
}
