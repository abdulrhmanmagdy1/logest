import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../widgets/dashboard_card.dart';
import '../../widgets/statistic_card.dart';
import '../../widgets/quick_action_button.dart';

class DriverDashboardScreen extends StatelessWidget {
  const DriverDashboardScreen({Key? key}) : super(key: key);

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
              
              // Today's Stats
              _buildTodayStats(),
              
              const SizedBox(height: 24),
              
              // Current Trip
              _buildCurrentTripCard(),
              
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
                Icons.wb_sunny_outlined,
                color: const Color(0xFFF97316),
                size: 24,
              ),
              const SizedBox(width: 8),
              Text(
                'صباح الخير!',
                style: TextStyle(
                  color: Colors.white.withOpacity(0.9),
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            'أحمد محمد',
            style: const TextStyle(
              color: Colors.white,
              fontSize: 24,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            'جاهز لبدء يوم جديد',
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

  Widget _buildTodayStats() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'إحصائيات اليوم',
          style: const TextStyle(
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
                title: 'الرحلات',
                value: '3',
                icon: Icons.local_shipping,
                color: const Color(0xFF10B981),
                change: '+1',
                changeType: 'positive',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: StatisticCard(
                title: 'الكيلومترات',
                value: '127',
                icon: Icons.route,
                color: const Color(0xFF3B82F6),
                change: '+45',
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
                title: 'الوقود',
                value: '15L',
                icon: Icons.local_gas_station,
                color: const Color(0xFFF59E0B),
                change: '-2L',
                changeType: 'negative',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: StatisticCard(
                title: 'الوقت',
                value: '4.5h',
                icon: Icons.access_time,
                color: const Color(0xFF8B5CF6),
                change: '+1.2h',
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

  Widget _buildCurrentTripCard() {
    return DashboardCard(
      title: 'الرحلة الحالية',
      icon: Icons.play_circle_outline,
      iconColor: const Color(0xFF10B981),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'شحنة #SH-2024-001',
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: const Color(0xFF10B981).withOpacity(0.2),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Text(
                  'جارية',
                  style: TextStyle(
                    color: Color(0xFF10B981),
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Icon(
                Icons.location_on_outlined,
                color: Colors.white.withOpacity(0.7),
                size: 20,
              ),
              const SizedBox(width: 8),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'من: الرياض - الملك فهد',
                      style: TextStyle(
                        color: Colors.white.withOpacity(0.9),
                        fontSize: 14,
                      ),
                    ),
                    Text(
                      'إلى: جدة - حي الروضة',
                      style: TextStyle(
                        color: Colors.white.withOpacity(0.9),
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          Container(
            height: 4,
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.1),
              borderRadius: BorderRadius.circular(2),
            ),
            child: FractionallySizedBox(
              alignment: Alignment.centerLeft,
              widthFactor: 0.65,
              child: Container(
                decoration: BoxDecoration(
                  gradient: const LinearGradient(
                    colors: [Color(0xFF10B981), Color(0xFF059669)],
                  ),
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
            ),
          ),
          const SizedBox(height: 8),
          Text(
            '65% مكتمل',
            style: TextStyle(
              color: Colors.white.withOpacity(0.7),
              fontSize: 12,
            ),
          ),
        ],
      ),
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms, delay: 200.ms)
        .fadeIn(duration: 400.ms, delay: 200.ms);
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
              icon: Icons.navigation,
              label: 'التنقل',
              color: const Color(0xFF3B82F6),
              onTap: () {
                // TODO: Navigate to navigation
              },
            ),
            QuickActionButton(
              icon: Icons.camera_alt,
              label: 'مسح ضوئي',
              color: const Color(0xFF10B981),
              onTap: () {
                // TODO: Open scanner
              },
            ),
            QuickActionButton(
              icon: Icons.phone,
              label: 'اتصال طوارئ',
              color: const Color(0xFFEF4444),
              onTap: () {
                // TODO: Emergency call
              },
            ),
            QuickActionButton(
              icon: Icons.history,
              label: 'السجل',
              color: const Color(0xFF8B5CF6),
              onTap: () {
                // TODO: Navigate to history
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
      iconColor: const Color(0xFF8B5CF6),
      child: Column(
        children: [
          _buildActivityItem(
            icon: Icons.check_circle,
            title: 'تسليم شحنة #SH-2024-002',
            time: 'قبل 30 دقيقة',
            color: const Color(0xFF10B981),
          ),
          const SizedBox(height: 12),
          _buildActivityItem(
            icon: Icons.local_gas_station,
            title: 'تعبئة وقود - 25 لتر',
            time: 'قبل ساعتين',
            color: const Color(0xFFF59E0B),
          ),
          const SizedBox(height: 12),
          _buildActivityItem(
            icon: Icons.play_arrow,
            title: 'بدء رحلة جديدة',
            time: 'قبل 3 ساعات',
            color: const Color(0xFF3B82F6),
          ),
        ],
      ),
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms, delay: 400.ms)
        .fadeIn(duration: 400.ms, delay: 400.ms);
  }

  Widget _buildActivityItem({
    required IconData icon,
    required String title,
    required String time,
    required Color color,
  }) {
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
            icon,
            color: color,
            size: 20,
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
