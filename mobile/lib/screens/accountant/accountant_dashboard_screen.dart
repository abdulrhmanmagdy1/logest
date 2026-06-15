import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../widgets/dashboard_card.dart';
import '../../widgets/statistic_card.dart';
import '../../widgets/quick_action_button.dart';

class AccountantDashboardScreen extends StatelessWidget {
  const AccountantDashboardScreen({Key? key}) : super(key: key);

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
              
              // Financial Overview
              _buildFinancialOverview(),
              
              const SizedBox(height: 24),
              
              // Revenue Chart
              _buildRevenueChart(),
              
              const SizedBox(height: 24),
              
              // Quick Actions
              _buildQuickActions(),
              
              const SizedBox(height: 24),
              
              // Recent Transactions
              _buildRecentTransactions(),
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
            const Color(0xFF10B981).withOpacity(0.1),
            const Color(0xFF1A1F2E),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFF10B981).withOpacity(0.2),
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                Icons.account_balance,
                color: const Color(0xFF10B981),
                size: 24,
              ),
              const SizedBox(width: 8),
              Text(
                'لوحة تحكم المحاسب',
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
            'مرحباً بك، نورة السعيد',
            style: const TextStyle(
              color: Colors.white,
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            'قسم المحاسبة - المالية',
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

  Widget _buildFinancialOverview() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'نظرة عامة مالية',
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
                title: 'إجمالي الإيرادات',
                value: '458,320',
                icon: Icons.trending_up,
                color: const Color(0xFF10B981),
                change: '+12.5%',
                changeType: 'positive',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: StatisticCard(
                title: 'المصروفات',
                value: '127,850',
                icon: Icons.trending_down,
                color: const Color(0xFFEF4444),
                change: '+8.3%',
                changeType: 'negative',
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: StatisticCard(
                title: 'صافي الربح',
                value: '330,470',
                icon: Icons.attach_money,
                color: const Color(0xFF3B82F6),
                change: '+15.2%',
                changeType: 'positive',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: StatisticCard(
                title: 'الفواتير المعلقة',
                value: '23',
                icon: Icons.receipt_long,
                color: const Color(0xFFF59E0B),
                change: '-5',
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

  Widget _buildRevenueChart() {
    return DashboardCard(
      title: 'مخطط الإيرادات الشهري',
      icon: ShowChart,
      iconColor: const Color(0xFF10B981),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              _buildMonthBar('يناير', 65, const Color(0xFF10B981)),
              _buildMonthBar('فبراير', 78, const Color(0xFF10B981)),
              _buildMonthBar('مارس', 82, const Color(0xFF10B981)),
              _buildMonthBar('أبريل', 71, const Color(0xFF3B82F6)),
              _buildMonthBar('مايو', 88, const Color(0xFF3B82F6)),
              _buildMonthBar('يونيو', 95, const Color(0xFF8B5CF6)),
            ],
          ),
          const SizedBox(height: 16),
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: const Color(0xFF1A1F2E),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'متوسط الشهر: 79,833 ريال',
                  style: TextStyle(
                    color: Colors.white.withOpacity(0.8),
                    fontSize: 12,
                  ),
                ),
                Text(
                  'نمو: +15.2%',
                  style: const TextStyle(
                    color: Color(0xFF10B981),
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms, delay: 200.ms)
        .fadeIn(duration: 400.ms, delay: 200.ms);
  }

  Widget _buildMonthBar(String month, int height, Color color) {
    return Column(
      children: [
        Container(
          width: 30,
          height: height.toDouble(),
          decoration: BoxDecoration(
            color: color,
            borderRadius: BorderRadius.circular(4),
          ),
        ),
        const SizedBox(height: 8),
        Text(
          month,
          style: TextStyle(
            color: Colors.white.withOpacity(0.6),
            fontSize: 10,
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
              icon: Icons.receipt,
              label: 'إنشاء فاتورة',
              color: const Color(0xFF10B981),
              onTap: () {
                // TODO: Create invoice
              },
            ),
            QuickActionButton(
              icon: Icons.payment,
              label: 'تسجيل دفعة',
              color: const Color(0xFF3B82F6),
              onTap: () {
                // TODO: Record payment
              },
            ),
            QuickActionButton(
              icon: Icons.assessment,
              label: 'تقارير مالية',
              color: const Color(0xFF8B5CF6),
              onTap: () {
                // TODO: Generate reports
              },
            ),
            QuickActionButton(
              icon: Icons.calculate,
              label: 'حسابات العملاء',
              color: const Color(0xFFF59E0B),
              onTap: () {
                // TODO: View accounts
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

  Widget _buildRecentTransactions() {
    return DashboardCard(
      title: 'المعاملات الأخيرة',
      icon: Icons.receipt_long,
      iconColor: const Color(0xFF10B981),
      child: Column(
        children: [
          _buildTransactionItem('فاتورة #INV-2024-089', '15,750 ريال', 'قبل ساعة', const Color(0xFF10B981)),
          const SizedBox(height: 12),
          _buildTransactionItem('دفعة #PAY-045', '8,200 ريال', 'قبل 3 ساعات', const Color(0xFF3B82F6)),
          const SizedBox(height: 12),
          _buildTransactionItem('فاتورة #INV-2024-088', '22,100 ريال', 'قبل 5 ساعات', const Color(0xFF10B981)),
          const SizedBox(height: 12),
          _buildTransactionItem('مصروفات #EXP-034', '3,450 ريال', 'قبل يوم', const Color(0xFFEF4444)),
        ],
      ),
    )
        .animate()
        .slideY(begin: 0.1, end: 0, duration: 400.ms, delay: 400.ms)
        .fadeIn(duration: 400.ms, delay: 400.ms);
  }

  Widget _buildTransactionItem(String reference, String amount, String time, Color color) {
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
            color == const Color(0xFFEF4444) ? Icons.arrow_downward : Icons.arrow_upward,
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
                reference,
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
        Text(
          amount,
          style: TextStyle(
            color: color,
            fontSize: 14,
            fontWeight: FontWeight.bold,
          ),
        ),
      ],
    );
  }
}
