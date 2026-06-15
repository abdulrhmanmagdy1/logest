import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../widgets/dashboard_card.dart';
import '../../widgets/statistic_card.dart';
import '../../widgets/quick_action_button.dart';

class WarehouseDashboardScreen extends StatelessWidget {
  const WarehouseDashboardScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0A0E1A),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Welcome Section
              _buildWelcomeSection(),
              
              const SizedBox(height: 24),
              
              // Warehouse Overview
              _buildWarehouseOverview(),
              
              const SizedBox(height: 24),
              
              // Inventory Status
              _buildInventoryStatus(),
              
              const SizedBox(height: 24),
              
              // Quick Actions
              _buildQuickActions(),
              
              const SizedBox(height: 24),
              
              // Recent Activities
              _buildRecentActivities(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildWelcomeSection() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF8B5CF6).withOpacity(0.1),
            const Color(0xFF1A1F2E),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFF8B5CF6).withOpacity(0.2),
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                Icons.warehouse,
                color: const Color(0xFF8B5CF6),
                size: 24,
              ),
              const SizedBox(width: 8),
              Text(
                'لوحة تحكم المستودع',
                style: TextStyle(
                  color: Colors.white.withOpacity(0.9),
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            'مرحباً بك، فهد العتيبي',
            style: const TextStyle(
              color: Colors.white,
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            'المستودع الرئيسي - الرياض',
            style: TextStyle(
              color: Colors.white.withOpacity(0.7),
              fontSize: 14,
            ),
          ),
        ],
      ),
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms)
        .fadeIn(duration: 400.ms);
  }

  Widget _buildWarehouseOverview() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'نظرة عامة على المستودع',
          style: TextStyle(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            Expanded(
              child: StatisticCard(
                title: 'إجمالي المخزون',
                value: '1,247',
                icon: Icons.inventory,
                color: const Color(0xFF8B5CF6),
                change: '+45',
                changeType: 'positive',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: StatisticCard(
                title: 'المناطق',
                value: '12',
                icon: Icons.grid_view,
                color: const Color(0xFF10B981),
                change: '0',
                changeType: 'positive',
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: StatisticCard(
                title: 'الشحنات المستلمة',
                value: '28',
                icon: Icons.receipt_long,
                color: const Color(0xFF3B82F6),
                change: '+8',
                changeType: 'positive',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: StatisticCard(
                title: 'الشحنات الصادرة',
                value: '32',
                icon: Icons.local_shipping,
                color: const Color(0xFFF59E0B),
                change: '+5',
                changeType: 'positive',
              ),
            ),
          ],
        ),
      ],
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms, delay: 100.ms)
        .fadeIn(duration: 400.ms, delay: 100.ms);
  }

  Widget _buildInventoryStatus() {
    return DashboardCard(
      title: 'حالة المخزون',
      icon: Icons.inventory_2,
      iconColor: const Color(0xFF8B5CF6),
      child: Column(
        children: [
          _buildInventoryItem('منطقة A', 85, const Color(0xFF10B981)),
          const SizedBox(height: 12),
          _buildInventoryItem('منطقة B', 92, const Color(0xFF10B981)),
          const SizedBox(height: 12),
          _buildInventoryItem('منطقة C', 67, const Color(0xFFF59E0B)),
          const SizedBox(height: 12),
          _buildInventoryItem('منطقة D', 45, const Color(0xFFEF4444)),
        ],
      ),
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms, delay: 200.ms)
        .fadeIn(duration: 400.ms, delay: 200.ms);
  }

  Widget _buildInventoryItem(String zone, int percentage, Color color) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              zone,
              style: const TextStyle(
                color: Colors.white,
                fontSize: 14,
                fontWeight: FontWeight.w500,
              ),
            ),
            Text(
              '$percentage%',
              style: TextStyle(
                color: color,
                fontSize: 14,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Container(
          height: 6,
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.1),
            borderRadius: BorderRadius.circular(3),
          ),
          child: FractionallySizedBox(
            alignment: Alignment.centerLeft,
            widthFactor: percentage / 100,
            child: Container(
              decoration: BoxDecoration(
                color: color,
                borderRadius: BorderRadius.circular(3),
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildQuickActions() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'إجراءات سريعة',
          style: TextStyle(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 16),
        GridView.count(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisCount: 2,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          childAspectRatio: 1.2,
          children: [
            QuickActionButton(
              icon: Icons.qr_code_scanner,
              label: 'مسح ضوئي',
              color: const Color(0xFF8B5CF6),
              onTap: () {
                // TODO: Open scanner
              },
            ),
            QuickActionButton(
              icon: Icons.add_box,
              label: 'استلام شحنة',
              color: const Color(0xFF10B981),
              onTap: () {
                // TODO: Receive shipment
              },
            ),
            QuickActionButton(
              icon: Icons.send,
              label: 'شحن',
              color: const Color(0xFF3B82F6),
              onTap: () {
                // TODO: Dispatch items
              },
            ),
            QuickActionButton(
              icon: Icons.inventory,
              label: 'جرد المخزون',
              color: const Color(0xFFF59E0B),
              onTap: () {
                // TODO: Start inventory count
              },
            ),
          ],
        ),
      ],
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms, delay: 300.ms)
        .fadeIn(duration: 400.ms, delay: 300.ms);
  }

  Widget _buildRecentActivities() {
    return DashboardCard(
      title: 'الأنشطة الأخيرة',
      icon: Icons.history,
      iconColor: const Color(0xFF8B5CF6),
      child: Column(
        children: [
          _buildActivityItem('استلام شحنة #SH-2024-045', 'قبل 30 دقيقة', const Color(0xFF10B981)),
          const SizedBox(height: 12),
          _buildActivityItem('شحن طلب #ORD-789', 'قبل ساعة', const Color(0xFF3B82F6)),
          const SizedBox(height: 12),
          _buildActivityItem('جرد منطقة C', 'قبل ساعتين', const Color(0xFFF59E0B)),
          const SizedBox(height: 12),
          _buildActivityItem('إضافة أصناف جديدة', 'قبل 3 ساعات', const Color(0xFF8B5CF6)),
        ],
      ),
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms, delay: 400.ms)
        .fadeIn(duration: 400.ms, delay: 400.ms);
  }

  Widget _buildActivityItem(String title, String time, Color color) {
    return Row(
      children: [
        Container(
          width: 40,
          height: 40,
          decoration: BoxDecoration(
            color: color.withOpacity(0.1),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Icon(
            Icons.circle,
            color: color,
            size: 12,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 14,
                  fontWeight: FontWeight.w500,
                ),
              ),
              Text(
                time,
                style: TextStyle(
                  color: Colors.white.withOpacity(0.6),
                  fontSize: 12,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}
