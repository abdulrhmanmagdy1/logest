// ============================================
// 💰 Accountant Dashboard - لوحة المحاسب
// ============================================

import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';

class AccountantDashboardScreen extends StatelessWidget {
  const AccountantDashboardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'النظام المحاسبي',
          style: TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            onPressed: () {},
            icon: const Icon(Icons.notifications, color: Colors.white),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Welcome Header
            _buildHeader(),
            
            const SizedBox(height: 24),
            
            // Stats Grid
            _buildStatsGrid(),
            
            const SizedBox(height: 24),
            
            // Quick Actions
            _buildQuickActions(),
            
            const SizedBox(height: 24),
            
            // Recent Transactions
            _buildRecentTransactions(),
            
            const SizedBox(height: 24),
            
            // Driver Settlements
            _buildDriverSettlements(),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [AppTheme.primaryColor, AppTheme.primaryDark],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.2),
              borderRadius: BorderRadius.circular(12),
            ),
            child: const Icon(
              Icons.account_balance_wallet,
              color: Colors.white,
              size: 32,
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'مرحباً، أحمد المحاسب',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  'اليوم: 15 يناير 2025',
                  style: TextStyle(
                    color: Colors.white.withOpacity(0.8),
                    fontSize: 14,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStatsGrid() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'إحصائيات اليوم',
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
              child: _StatCard(
                title: 'الإيرادات',
                value: '45,250 ريال',
                icon: Icons.trending_up,
                color: AppTheme.successColor,
                change: '+12%',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _StatCard(
                title: 'الفواتير',
                value: '28 فاتورة',
                icon: Icons.receipt,
                color: AppTheme.primaryColor,
                change: '8 جديد',
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: _StatCard(
                title: 'المديونيات',
                value: '12,800 ريال',
                icon: Icons.warning,
                color: AppTheme.warningColor,
                change: '5 عميل',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _StatCard(
                title: 'تسوية السائقين',
                value: '8 سائقين',
                icon: Icons.people,
                color: Colors.purple,
                change: '3 معلق',
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildQuickActions() {
    final actions = [
      _ActionItem(
        icon: Icons.receipt_long,
        title: 'إنشاء فاتورة',
        color: AppTheme.primaryColor,
      ),
      _ActionItem(
        icon: Icons.payments,
        title: 'تسجيل دفعة',
        color: AppTheme.successColor,
      ),
      _ActionItem(
        icon: Icons.account_balance,
        title: 'الحوالات',
        color: Colors.orange,
      ),
      _ActionItem(
        icon: Icons.assessment,
        title: 'التقارير',
        color: Colors.purple,
      ),
    ];

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
        GridView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 4,
            childAspectRatio: 0.8,
            crossAxisSpacing: 12,
          ),
          itemCount: actions.length,
          itemBuilder: (context, index) {
            final action = actions[index];
            return _buildActionButton(action);
          },
        ),
      ],
    );
  }

  Widget _buildActionButton(_ActionItem action) {
    return Column(
      children: [
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: action.color.withOpacity(0.1),
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: action.color.withOpacity(0.3)),
          ),
          child: Icon(
            action.icon,
            color: action.color,
            size: 28,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          action.title,
          style: TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 12,
          ),
          textAlign: TextAlign.center,
          maxLines: 1,
        ),
      ],
    );
  }

  Widget _buildRecentTransactions() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'آخر المعاملات',
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
        _TransactionCard(
          title: 'فاتورة شحنة #EDH-2024-0156',
          client: 'أسواق التميمي',
          amount: '2,450 ريال',
          type: 'income',
          time: '14:30',
        ),
        _TransactionCard(
          title: 'دفعة عميل',
          client: 'شركة الغذاء',
          amount: '5,800 ريال',
          type: 'income',
          time: '13:15',
        ),
        _TransactionCard(
          title: 'تسوية سائق',
          client: 'محمد السائق',
          amount: '1,200 ريال',
          type: 'expense',
          time: '11:45',
        ),
      ],
    );
  }

  Widget _buildDriverSettlements() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'تسوية السائقين',
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
        _DriverSettlementCard(
          name: 'خالد العبدالله',
          trips: 12,
          amount: '3,600 ريال',
          status: 'pending',
        ),
        _DriverSettlementCard(
          name: 'سعد المطيري',
          trips: 8,
          amount: '2,400 ريال',
          status: 'pending',
        ),
      ],
    );
  }
}

// Supporting Classes
class _StatCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  final Color color;
  final String change;

  const _StatCard({
    required this.title,
    required this.value,
    required this.icon,
    required this.color,
    required this.change,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Icon(icon, color: color, size: 24),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: color.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(
                  change,
                  style: TextStyle(
                    color: color,
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            value,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            title,
            style: TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }
}

class _ActionItem {
  final IconData icon;
  final String title;
  final Color color;

  _ActionItem({required this.icon, required this.title, required this.color});
}

class _TransactionCard extends StatelessWidget {
  final String title;
  final String client;
  final String amount;
  final String type;
  final String time;

  const _TransactionCard({
    required this.title,
    required this.client,
    required this.amount,
    required this.type,
    required this.time,
  });

  @override
  Widget build(BuildContext context) {
    final isIncome = type == 'income';
    
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              color: isIncome 
                ? AppTheme.successColor.withOpacity(0.1) 
                : AppTheme.errorColor.withOpacity(0.1),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Icon(
              isIncome ? Icons.arrow_downward : Icons.arrow_upward,
              color: isIncome ? AppTheme.successColor : AppTheme.errorColor,
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
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  client,
                  style: TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                amount,
                style: TextStyle(
                  color: isIncome ? AppTheme.successColor : AppTheme.errorColor,
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
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
        ],
      ),
    );
  }
}

class _DriverSettlementCard extends StatelessWidget {
  final String name;
  final int trips;
  final String amount;
  final String status;

  const _DriverSettlementCard({
    required this.name,
    required this.trips,
    required this.amount,
    required this.status,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          Container(
            width: 48,
            height: 48,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [Colors.purple, Colors.purple.shade800],
              ),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Center(
              child: Text(
                name.split(' ').map((e) => e[0]).join(''),
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
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
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  '$trips رحلة',
                  style: TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                amount,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 4),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: AppTheme.warningColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(6),
                ),
                child: Text(
                  'معلقة',
                  style: TextStyle(
                    color: AppTheme.warningColor,
                    fontSize: 11,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
