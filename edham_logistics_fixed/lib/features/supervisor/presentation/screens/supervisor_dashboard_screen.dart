// ============================================
// 👔 Supervisor Dashboard - Premium Management System
// Enterprise Supervisor Portal with Full Management Features
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/premium_stat_card.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';

class SupervisorDashboardScreen extends StatelessWidget {
  const SupervisorDashboardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: Text(
          'لوحة المشرف',
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          GlassContainer(
            width: 50,
            height: 50,
            radius: 25,
            backgroundColor: Colors.white.withOpacity(0.05),
            borderColor: AppTheme.primary.withOpacity(0.2),
            child: Stack(
              children: [
                Center(
                  child: Icon(
                    Icons.notifications,
                    color: AppTheme.textPrimary,
                    size: 24,
                  ),
                ),
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
          ),
        ],
      ),
      body: CustomScrollView(
        slivers: [
          // Premium Header with Stats
          SliverToBoxAdapter(
            child: _buildPremiumHeader(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 20)),
          
          // Operations Overview
          SliverToBoxAdapter(
            child: _buildOperationsOverview(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Management Cards
          SliverToBoxAdapter(
            child: _buildManagementCards(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Fleet Status
          SliverToBoxAdapter(
            child: _buildFleetStatus(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Active Drivers
          SliverToBoxAdapter(
            child: _buildActiveDrivers(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Recent Activities
          SliverToBoxAdapter(
            child: _buildRecentActivities(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 80)),
        ],
      ),
            
            floatingActionButton: GlowingButton(
        text: 'تعيين رحلة جديدة',
        onPressed: () {},
        color: AppTheme.primary,
        icon: Icons.add_circle,
      ),
    );
  }

  Widget _buildPremiumHeader() {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        children: [
          // Welcome Section
          GlassContainer(
            padding: const EdgeInsets.all(24),
            radius: 20,
            backgroundColor: AppTheme.primary.withOpacity(0.1),
            borderColor: AppTheme.primary.withOpacity(0.3),
            boxShadow: AppShadows.glowing(AppTheme.primary),
            child: Row(
              children: [
                GlassContainer(
                  width: 60,
                  height: 60,
                  radius: 30,
                  backgroundColor: AppTheme.primary.withOpacity(0.2),
                  borderColor: AppTheme.primary.withOpacity(0.4),
                  child: const Icon(
                    Icons.admin_panel_settings,
                    size: 30,
                    color: AppTheme.primary,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'مرحباً، المشرف أحمد',
                        style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          color: AppTheme.textPrimary,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        'إدارة النظام - يوم عمل جديد',
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: AppTheme.textSecondary,
                        ),
                      ),
                    ],
                  ),
                ),
                GlassContainer(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  radius: 20,
                  backgroundColor: AppTheme.success.withOpacity(0.1),
                  borderColor: AppTheme.success.withOpacity(0.3),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(
                        Icons.online_prediction,
                        color: AppTheme.success,
                        size: 16,
                      ),
                      const SizedBox(width: 4),
                      Text(
                        'متصل',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: AppTheme.success,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ).animate().fadeIn().slideY(begin: 0.2, end: 0),
          
          const SizedBox(height: 20),
          
          // Quick Stats
          Row(
            children: [
              Expanded(
                child: PremiumStatCard(
                  title: 'الرحلات النشطة',
                  value: '24',
                  change: '+3 اليوم',
                  icon: Icons.local_shipping,
                  color: AppTheme.primary,
                  animationDelay: const Duration(milliseconds: 200),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'السائقين',
                  value: '18',
                  change: '+2 جديد',
                  icon: Icons.people,
                  color: AppTheme.success,
                  animationDelay: const Duration(milliseconds: 400),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'الطلبات',
                  value: '7',
                  change: 'معلقة',
                  icon: Icons.pending_actions,
                  color: AppTheme.accent,
                  animationDelay: const Duration(milliseconds: 600),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildManagementCards() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'إدارة النظام',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ).animate().fadeIn(delay: const Duration(milliseconds: 800)),
          
          const SizedBox(height: 16),
          
          GridView.count(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            crossAxisCount: 2,
            crossAxisSpacing: 16,
            mainAxisSpacing: 16,
            childAspectRatio: 1.2,
            children: [
              _buildManagementCard(context, 'إدارة السائقين', Icons.people, AppTheme.primary, () => _navigateToDrivers()),
              _buildManagementCard(context, 'الشحنات', Icons.local_shipping, AppTheme.success, () => _navigateToShipments()),
              _buildManagementCard(context, 'العملاء', Icons.people, AppTheme.accent, () => _navigateToClients()),
              _buildManagementCard(context, 'التقارير', Icons.analytics, AppTheme.primary, () => _navigateToReports()),
              _buildManagementCard(context, 'المالية', Icons.account_balance, AppTheme.success, () => _navigateToFinance()),
              _buildManagementCard(context, 'الإعدادات', Icons.settings, AppTheme.accent, () => _navigateToSettings()),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildManagementCard(BuildContext context, String title, IconData icon, Color color, VoidCallback onTap) {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      radius: 16,
      backgroundColor: color.withOpacity(0.1),
      borderColor: color.withOpacity(0.3),
      child: InkWell(
        onTap: onTap,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, color: color, size: 40),
            const SizedBox(height: 12),
            Text(
              title,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    ).animate().fadeIn(delay: const Duration(milliseconds: 1000));
  }

  Widget _buildRecentActivities() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: GlassContainer(
        padding: const EdgeInsets.all(20),
        radius: 20,
        backgroundColor: Colors.white.withOpacity(0.05),
        borderColor: AppTheme.textHint.withOpacity(0.2),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'الأنشطة الحديثة',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            
            // Activity Items
            _buildActivityItem(context, 'شحنة جديدة', 'شحنة #1234 تم إنشاؤها', 'منذ 5 دقائق', Icons.add_circle, AppTheme.success),
            _buildActivityItem(context, 'تحديث السائق', 'محمد أحمد بدأ رحلة جديدة', 'منذ 15 دقيقة', Icons.directions_car, AppTheme.primary),
            _buildActivityItem(context, 'تحديث النظام', 'تم تحديث النظام إلى v2.1', 'منذ ساعة', Icons.system_update, AppTheme.accent),
          ],
        ),
      ),
    ).animate().fadeIn(delay: const Duration(milliseconds: 1200));
  }

  Widget _buildActivityItem(
    BuildContext context,
    String title,
    String description,
    String time,
    IconData icon,
    Color color,
  ) {
    return Row(
      children: [
        GlassContainer(
          width: 40,
          height: 40,
          radius: 20,
          backgroundColor: color.withOpacity(0.1),
          borderColor: color.withOpacity(0.3),
          child: Icon(icon, color: color, size: 20),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.w600,
                ),
              ),
              Text(
                description,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
            ],
          ),
        ),
        Text(
          time,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: AppTheme.textHint,
          ),
        ),
      ],
    );
  }

  void _navigateToDrivers() {
    // Navigate to drivers management
  }

  void _navigateToShipments() {
    // Navigate to shipments management
  }

  void _navigateToClients() {
    // Navigate to clients management
  }

  void _navigateToReports() {
    // Navigate to reports
  }

  void _navigateToFinance() {
    // Navigate to finance
  }

  void _navigateToSettings() {
    // Navigate to settings
  }

  Widget _buildOperationsOverview() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [AppTheme.primaryColor, AppTheme.primaryDark],
        ),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'نظرة عامة على العمليات',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: const Text(
                  'اليوم',
                  style: TextStyle(color: Colors.white, fontSize: 12),
                ),
              ),
            ],
          ),
          const SizedBox(height: 20),
          Row(
            children: [
              Expanded(
                child: _buildOverviewItem(
                  icon: Icons.local_shipping,
                  value: '24',
                  label: 'رحلة نشطة',
                ),
              ),
              Expanded(
                child: _buildOverviewItem(
                  icon: Icons.people,
                  value: '18',
                  label: 'سائق متاح',
                ),
              ),
              Expanded(
                child: _buildOverviewItem(
                  icon: Icons.pending_actions,
                  value: '7',
                  label: 'طلبات معلقة',
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildOverviewItem({
    required IconData icon,
    required String value,
    required String label,
  }) {
    return Column(
      children: [
        Icon(icon, color: Colors.white70, size: 28),
        const SizedBox(height: 8),
        Text(
          value,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 24,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          label,
          style: TextStyle(
            color: Colors.white.withOpacity(0.8),
            fontSize: 12,
          ),
        ),
      ],
    );
  }

  Widget _buildFleetStatus() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'حالة الأسطول',
          style: TextStyle(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: AppTheme.cardBackground,
            borderRadius: BorderRadius.circular(16),
          ),
          child: Column(
            children: [
              _FleetStatusBar(
                label: 'مركبات نشطة',
                value: 28,
                total: 35,
                color: AppTheme.successColor,
              ),
              const SizedBox(height: 12),
              _FleetStatusBar(
                label: 'في الصيانة',
                value: 4,
                total: 35,
                color: AppTheme.warningColor,
              ),
              const SizedBox(height: 12),
              _FleetStatusBar(
                label: 'غير متاحة',
                value: 3,
                total: 35,
                color: AppTheme.errorColor,
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildActiveDrivers() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'السائقين النشطين',
              style: TextStyle(
                color: Colors.white,
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            TextButton(
              onPressed: () {},
              child: const Text('عرض الكل'),
            ),
          ],
        ),
        const SizedBox(height: 12),
        SizedBox(
          height: 100,
          child: ListView.builder(
            scrollDirection: Axis.horizontal,
            itemCount: 5,
            itemBuilder: (context, index) {
              final drivers = [
                {'name': 'خالد', 'status': 'في رحلة', 'trips': 8},
                {'name': 'محمد', 'status': 'متاح', 'trips': 12},
                {'name': 'أحمد', 'status': 'في رحلة', 'trips': 6},
                {'name': 'سعد', 'status': 'متاح', 'trips': 15},
                {'name': 'عبدالله', 'status': 'استراحة', 'trips': 4},
              ];
              final driver = drivers[index];
              return _DriverCard(
                name: driver['name'] as String,
                status: driver['status'] as String,
                trips: driver['trips'] as int,
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildPendingShipments() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'شحنات بحاجة لتعيين',
              style: TextStyle(
                color: Colors.white,
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: AppTheme.warningColor.withOpacity(0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text(
                '7 جديد',
                style: TextStyle(
                  color: AppTheme.warningColor,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        _PendingShipmentCard(
          trackingNumber: 'EDH-2024-0158',
          route: 'الرياض → جدة',
          type: 'مجمد (-18°)',
          weight: '3,500 كجم',
          waitingTime: '45 دقيقة',
          priority: 'high',
        ),
        _PendingShipmentCard(
          trackingNumber: 'EDH-2024-0159',
          route: 'الدمام → الخبر',
          type: 'مبرد (+4°)',
          weight: '1,200 كجم',
          waitingTime: '20 دقيقة',
          priority: 'normal',
        ),
      ],
    );
  }

  Widget _buildAlertsSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'تنبيهات تحتاج اهتمام',
          style: TextStyle(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 12),
        _AlertCard(
          icon: Icons.thermostat,
          title: 'درجة حرارة مرتفعة',
          description: 'الشاحنة R-234 تسجل -12° بدلاً من -18°',
          time: 'منذ 5 دقائق',
          color: AppTheme.errorColor,
        ),
        _AlertCard(
          icon: Icons.schedule,
          title: 'تأخير في الرحلة',
          description: 'رحلة EDH-2024-0145 متأخرة 30 دقيقة',
          time: 'منذ 12 دقيقة',
          color: AppTheme.warningColor,
        ),
      ],
    );
  }
}

class _FleetStatusBar extends StatelessWidget {
  final String label;
  final int value;
  final int total;
  final Color color;

  const _FleetStatusBar({
    required this.label,
    required this.value,
    required this.total,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    final percentage = value / total;
    
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              label,
              style: TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 12,
              ),
            ),
            Text(
              '$value/$total',
              style: TextStyle(
                color: color,
                fontSize: 12,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        LinearProgressIndicator(
          value: percentage,
          backgroundColor: color.withOpacity(0.1),
          valueColor: AlwaysStoppedAnimation<Color>(color),
          minHeight: 8,
          borderRadius: BorderRadius.circular(4),
        ),
      ],
    );
  }
}

class _DriverCard extends StatelessWidget {
  final String name;
  final String status;
  final int trips;

  const _DriverCard({
    required this.name,
    required this.status,
    required this.trips,
  });

  @override
  Widget build(BuildContext context) {
    Color statusColor;
    switch (status) {
      case 'في رحلة':
        statusColor = AppTheme.primaryColor;
        break;
      case 'متاح':
        statusColor = AppTheme.successColor;
        break;
      default:
        statusColor = AppTheme.warningColor;
    }

    return Container(
      width: 120,
      margin: const EdgeInsets.only(right: 12),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: statusColor.withOpacity(0.3)),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Container(
            width: 44,
            height: 44,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [statusColor, statusColor.withOpacity(0.7)],
              ),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Center(
              child: Text(
                name[0],
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
          const SizedBox(height: 8),
          Text(
            name,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 14,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 4),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
            decoration: BoxDecoration(
              color: statusColor.withOpacity(0.1),
              borderRadius: BorderRadius.circular(6),
            ),
            child: Text(
              status,
              style: TextStyle(
                color: statusColor,
                fontSize: 10,
              ),
            ),
          ),
          const SizedBox(height: 4),
          Text(
            '$trips رحلة',
            style: TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 10,
            ),
          ),
        ],
      ),
    );
  }
}

class _PendingShipmentCard extends StatelessWidget {
  final String trackingNumber;
  final String route;
  final String type;
  final String weight;
  final String waitingTime;
  final String priority;

  const _PendingShipmentCard({
    required this.trackingNumber,
    required this.route,
    required this.type,
    required this.weight,
    required this.waitingTime,
    required this.priority,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: priority == 'high' 
            ? AppTheme.errorColor.withOpacity(0.3) 
            : Colors.transparent,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                trackingNumber,
                style: TextStyle(
                  color: AppTheme.primaryColor,
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: priority == 'high'
                    ? AppTheme.errorColor.withOpacity(0.1)
                    : AppTheme.warningColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(6),
                ),
                child: Text(
                  priority == 'high' ? 'عاجل' : 'عادي',
                  style: TextStyle(
                    color: priority == 'high'
                      ? AppTheme.errorColor
                      : AppTheme.warningColor,
                    fontSize: 11,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              Icon(Icons.route, color: AppTheme.textSecondary, size: 16),
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  route,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 14,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Row(
            children: [
              Icon(Icons.thermostat, color: AppTheme.textSecondary, size: 16),
              const SizedBox(width: 8),
              Text(
                type,
                style: TextStyle(
                  color: AppTheme.textSecondary,
                  fontSize: 13,
                ),
              ),
              const SizedBox(width: 16),
              Icon(Icons.scale, color: AppTheme.textSecondary, size: 16),
              const SizedBox(width: 8),
              Text(
                weight,
                style: TextStyle(
                  color: AppTheme.textSecondary,
                  fontSize: 13,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Icon(Icons.schedule, color: AppTheme.warningColor, size: 16),
                  const SizedBox(width: 4),
                  Text(
                    'في الانتظار: $waitingTime',
                    style: TextStyle(
                      color: AppTheme.warningColor,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
              ElevatedButton(
                onPressed: () {},
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppTheme.primaryColor,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                child: const Text('تعيين سائق'),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _AlertCard extends StatelessWidget {
  final IconData icon;
  final String title;
  final String description;
  final String time;
  final Color color;

  const _AlertCard({
    required this.icon,
    required this.title,
    required this.description,
    required this.time,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              color: color.withOpacity(0.2),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Icon(icon, color: color, size: 24),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: TextStyle(
                    color: color,
                    fontSize: 14,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  description,
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 13,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  time,
                  style: TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 11,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
