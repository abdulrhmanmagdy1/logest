import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../widgets/dashboard_card.dart';
import '../../widgets/statistic_card.dart';
import '../../widgets/quick_action_button.dart';

class SupervisorDashboardScreen extends StatelessWidget {
  const SupervisorDashboardScreen({Key? key}) : super(key: key);

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
              
              // Operations Overview
              _buildOperationsOverview(),
              
              const SizedBox(height: 24),
              
              // Driver Status
              _buildDriverStatus(),
              
              const SizedBox(height: 24),
              
              // Quick Actions
              _buildQuickActions(),
              
              const SizedBox(height: 24),
              
              // Active Tasks
              _buildActiveTasks(),
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
            const Color(0xFF3B82F6).withOpacity(0.1),
            const Color(0xFF1A1F2E),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFF3B82F6).withOpacity(0.2),
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                Icons.supervisor_account,
                color: const Color(0xFF3B82F6),
                size: 24,
              ),
              const SizedBox(width: 8),
              Text(
                'لوحة تحكم المشرف',
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
            'مرحباً بك، خالد الأحمدي',
            style: const TextStyle(
              color: Colors.white,
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            '12 سائق نشط • 8 شحنات جارية',
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

  Widget _buildOperationsOverview() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'نظرة عامة على العمليات',
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
                title: 'السائقين النشطين',
                value: '12',
                icon: Icons.people,
                color: const Color(0xFF10B981),
                change: '+2',
                changeType: 'positive',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: StatisticCard(
                title: 'الشحنات الجارية',
                value: '8',
                icon: Icons.local_shipping,
                color: const Color(0xFF3B82F6),
                change: '+3',
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
                title: 'معدل التسليم',
                value: '94%',
                icon: Icons.trending_up,
                color: const Color(0xFF10B981),
                change: '+2%',
                changeType: 'positive',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: StatisticCard(
                title: 'التأخيرات',
                value: '2',
                icon: Icons.warning,
                color: const Color(0xFFEF4444),
                change: '-1',
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

  Widget _buildDriverStatus() {
    return DashboardCard(
      title: 'حالة السائقين',
      icon: Icons.people_outline,
      iconColor: const Color(0xFF3B82F6),
      child: Column(
        children: [
          _buildDriverStatusItem('أحمد محمد', 'نشط', 'رحلة #SH-001', const Color(0xFF10B981)),
          const SizedBox(height: 12),
          _buildDriverStatusItem('محمد علي', 'متوقف', 'استراحة', const Color(0xFFF59E0B)),
          const SizedBox(height: 12),
          _buildDriverStatusItem('سعيد خالد', 'نشط', 'رحلة #SH-003', const Color(0xFF10B981)),
          const SizedBox(height: 12),
          _buildDriverStatusItem('عمر حسن', 'غير متاحر', 'صيانة', const Color(0xFFEF4444)),
        ],
      ),
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms, delay: 200.ms)
        .fadeIn(duration: 400.ms, delay: 200.ms);
  }

  Widget _buildDriverStatusItem(String name, String status, String currentTask, Color statusColor) {
    return Row(
      children: [
        Container(
          width: 40,
          height: 40,
          decoration: BoxDecoration(
            color: statusColor.withOpacity(0.1),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Icon(
            Icons.person,
            color: statusColor,
            size: 20,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                name,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 14,
                  fontWeight: FontWeight.w500,
                ),
              ),
              Text(
                currentTask,
                style: TextStyle(
                  color: Colors.white.withOpacity(0.6),
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
              icon: Icons.assignment_add,
              label: 'تعيين مهمة',
              color: const Color(0xFF3B82F6),
              onTap: () {
                // TODO: Assign new task
              },
            ),
            QuickActionButton(
              icon: Icons.map,
              label: 'الخريطة المباشرة',
              color: const Color(0xFF10B981),
              onTap: () {
                // TODO: Open live map
              },
            ),
            QuickActionButton(
              icon: Icons.analytics,
              label: 'التحليلات',
              color: const Color(0xFF8B5CF6),
              onTap: () {
                // TODO: Open analytics
              },
            ),
            QuickActionButton(
              icon: Icons.notification_add,
              label: 'إرسال إشعار',
              color: const Color(0xFFF59E0B),
              onTap: () {
                // TODO: Send notification
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

  Widget _buildActiveTasks() {
    return DashboardCard(
      title: 'المهام النشطة',
      icon: Icons.assignment,
      iconColor: const Color(0xFF3B82F6),
      child: Column(
        children: [
          _buildTaskItem('توزيع شحنة #SH-004', 'عالي', 'قبل 10 دقائق'),
          const SizedBox(height: 12),
          _buildTaskItem('متابعة تأخير #SH-002', 'متوسط', 'قبل 30 دقيقة'),
          const SizedBox(height: 12),
          _buildTaskItem('مراجعة تقرير اليوم', 'منخفض', 'قبل ساعة'),
        ],
      ),
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms, delay: 400.ms)
        .fadeIn(duration: 400.ms, delay: 400.ms);
  }

  Widget _buildTaskItem(String title, String priority, String time) {
    Color priorityColor;
    switch (priority) {
      case 'عالي':
        priorityColor = const Color(0xFFEF4444);
        break;
      case 'متوسط':
        priorityColor = const Color(0xFFF59E0B);
        break;
      default:
        priorityColor = const Color(0xFF10B981);
    }

    return Row(
      children: [
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
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
          decoration: BoxDecoration(
            color: priorityColor.withOpacity(0.2),
            borderRadius: BorderRadius.circular(6),
          ),
          child: Text(
            priority,
            style: TextStyle(
              color: priorityColor,
              fontSize: 12,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ],
    );
  }
}
