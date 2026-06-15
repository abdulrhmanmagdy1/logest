import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../widgets/dashboard_card.dart';
import '../../widgets/statistic_card.dart';
import '../../widgets/quick_action_button.dart';

class CustomerDashboardScreen extends StatelessWidget {
  const CustomerDashboardScreen({Key? key}) : super(key: key);

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
              
              // Shipments Overview
              _buildShipmentsOverview(),
              
              const SizedBox(height: 24),
              
              // Active Shipments
              _buildActiveShipments(),
              
              const SizedBox(height: 24),
              
              // Quick Actions
              _buildQuickActions(),
              
              const SizedBox(height: 24),
              
              // Recent Activity
              _buildRecentActivity(),
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
            const Color(0xFFF97316).withOpacity(0.1),
            const Color(0xFF1A1F2E),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFFF97316).withOpacity(0.2),
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                Icons.person,
                color: const Color(0xFFF97316),
                size: 24,
              ),
              const SizedBox(width: 8),
              Text(
                'لوحة تحكم العميل',
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
            'مرحباً بك، محمد أحمد',
            style: const TextStyle(
              color: Colors.white,
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            'العضوية: #CUS-2024-0234',
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

  Widget _buildShipmentsOverview() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'نظرة عامة على الشحنات',
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
                title: 'الشحنات النشطة',
                value: '3',
                icon: Icons.local_shipping,
                color: const Color(0xFFF97316),
                change: '0',
                changeType: 'positive',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: StatisticCard(
                title: 'الشحنات المكتملة',
                value: '47',
                icon: Icons.check_circle,
                color: const Color(0xFF10B981),
                change: '+5',
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
                title: 'إجمالي التكلفة',
                value: '12,450',
                icon: Icons.attach_money,
                color: const Color(0xFF3B82F6),
                change: '+2,340',
                changeType: 'positive',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: StatisticCard(
                title: 'نقاط الولاء',
                value: '2,850',
                icon: Icons.star,
                color: const Color(0xFF8B5CF6),
                change: '+120',
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

  Widget _buildActiveShipments() {
    return DashboardCard(
      title: 'الشحنات النشطة',
      icon: Icons.local_shipping,
      iconColor: const Color(0xFFF97316),
      child: Column(
        children: [
          _buildShipmentItem('SH-2024-123', 'جدة → الرياض', 'في الطريق', const Color(0xFF3B82F6), '85%'),
          const SizedBox(height: 12),
          _buildShipmentItem('SH-2024-124', 'الرياض → الدمام', 'تم التسليم', const Color(0xFF10B981), '100%'),
          const SizedBox(height: 12),
          _buildShipmentItem('SH-2024-125', 'الخبر → جدة', 'قيد التجهيز', const Color(0xFFF59E0B), '25%'),
        ],
      ),
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms, delay: 200.ms)
        .fadeIn(duration: 400.ms, delay: 200.ms);
  }

  Widget _buildShipmentItem(String id, String route, String status, Color statusColor, String progress) {
    return Column(
      children: [
        Row(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    id,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Text(
                    route,
                    style: TextStyle(
                      color: Colors.white.withOpacity(0.7),
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: statusColor.withOpacity(0.2),
                borderRadius: BorderRadius.circular(6),
              ),
              child: Text(
                status,
                style: TextStyle(
                  color: statusColor,
                  fontSize: 12,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Container(
          height: 4,
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.1),
            borderRadius: BorderRadius.circular(2),
          ),
          child: FractionallySizedBox(
            alignment: Alignment.centerLeft,
            widthFactor: double.parse(progress) / 100,
            child: Container(
              decoration: BoxDecoration(
                color: statusColor,
                borderRadius: BorderRadius.circular(2),
              ),
            ),
          ),
        ),
        const SizedBox(height: 4),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'التقدم: $progress',
              style: TextStyle(
                color: Colors.white.withOpacity(0.6),
                fontSize: 11,
              ),
            ),
            Text(
              status == 'تم التسليم' ? 'تم التسليم' : 'الوصول المتوقع: 2-3 ساعات',
              style: TextStyle(
                color: statusColor.withOpacity(0.8),
                fontSize: 11,
              ),
            ),
          ],
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
              icon: Icons.add_box,
              label: 'شحنة جديدة',
              color: const Color(0xFFF97316),
              onTap: () {
                // TODO: Create new shipment
              },
            ),
            QuickActionButton(
              icon: Icons.search,
              label: 'تتبع الشحنة',
              color: const Color(0xFF3B82F6),
              onTap: () {
                // TODO: Track shipment
              },
            ),
            QuickActionButton(
              icon: Icons.receipt,
              label: 'الفواتير',
              color: const Color(0xFF10B981),
              onTap: () {
                // TODO: View invoices
              },
            ),
            QuickActionButton(
              icon: Icons.support_agent,
              label: 'الدعم الفني',
              color: const Color(0xFF8B5CF6),
              onTap: () {
                // TODO: Contact support
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

  Widget _buildRecentActivity() {
    return DashboardCard(
      title: 'النشاط الأخير',
      icon: Icons.history,
      iconColor: const Color(0xFFF97316),
      child: Column(
        children: [
          _buildActivityItem('تم تسليم شحنة SH-2024-122', 'قبل يومين', const Color(0xFF10B981)),
          const SizedBox(height: 12),
          _buildActivityItem('تم إرسال فاتورة #INV-456', 'قبل 3 أيام', const Color(0xFF3B82F6)),
          const SizedBox(height: 12),
          _buildActivityItem('تم إضافة 120 نقطة ولاء', 'قبل أسبوع', const Color(0xFF8B5CF6)),
          const SizedBox(height: 12),
          _buildActivityItem('تم إنشاء شحنة SH-2024-123', 'قبل 10 أيام', const Color(0xFFF97316)),
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
