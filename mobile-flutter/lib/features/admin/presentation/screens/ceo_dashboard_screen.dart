// ============================================
// 👔 CEO Dashboard Screen - لوحة المدير العام
// Premium Cinematic UI with Glassmorphism
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:fl_chart/fl_chart.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/premium_stat_card.dart';
import '../../../../core/widgets/premium/premium_chart.dart';
import '../../../../core/widgets/premium/glowing_button.dart';

class CEODashboardScreen extends StatefulWidget {
  const CEODashboardScreen({super.key});

  @override
  State<CEODashboardScreen> createState() => _CEODashboardScreenState();
}

class _CEODashboardScreenState extends State<CEODashboardScreen> {
  int _selectedTimeRange = 0;
  final List<String> _timeRanges = ['اليوم', 'الأسبوع', 'الشهر', 'السنة'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'لوحة المدير التنفيذي',
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
            icon: const Icon(Icons.notifications_outlined, color: Colors.white),
          ),
          IconButton(
            onPressed: () {},
            icon: const Icon(Icons.settings_outlined, color: Colors.white),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            // Time Range Selector
            _buildTimeRangeSelector(),
            
            const SizedBox(height: 16),
            
            // Key Metrics Cards
            _buildKeyMetrics(),
            
            const SizedBox(height: 16),
            
            // Revenue Chart
            _buildRevenueChart(),
            
            const SizedBox(height: 16),
            
            // Performance Grid
            _buildPerformanceGrid(),
            
            const SizedBox(height: 16),
            
            // Live Operations
            _buildLiveOperations(),
            
            const SizedBox(height: 16),
            
            // Top Drivers
            _buildTopDrivers(),
            
            const SizedBox(height: 16),
            
            // Alerts & Notifications
            _buildAlertsSection(),
          ],
        ),
      ),
    );
  }

  Widget _buildTimeRangeSelector() {
    return GlassContainer(
      radius: 20,
      padding: const EdgeInsets.all(4),
      child: Row(
        children: _timeRanges.asMap().entries.map((entry) {
          final index = entry.key;
          final label = entry.value;
          final isSelected = _selectedTimeRange == index;
          
          return Expanded(
            child: GestureDetector(
              onTap: () {
                setState(() {
                  _selectedTimeRange = index;
                });
              },
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 300),
                margin: const EdgeInsets.symmetric(horizontal: 2),
                padding: const EdgeInsets.symmetric(vertical: 8),
                decoration: BoxDecoration(
                  gradient: isSelected ? AppTheme.primaryGradient : null,
                  color: isSelected ? null : Colors.transparent,
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(
                    color: isSelected 
                        ? Colors.transparent 
                        : AppTheme.primary.withOpacity(0.2),
                    width: 1,
                  ),
                ),
                child: Text(
                  label,
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: isSelected ? Colors.white : AppTheme.textSecondary,
                    fontSize: 13,
                    fontWeight: isSelected ? FontWeight.w600 : FontWeight.w500,
                  ),
                ),
              ),
            ),
          );
        }).toList(),
      ),
    ).animate().fadeIn().slideY(begin: 0.2, end: 0);
  }

  Widget _buildKeyMetrics() {
    return GridView.count(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisCount: 2,
      crossAxisSpacing: 12,
      mainAxisSpacing: 12,
      childAspectRatio: 1.4,
      children: [
        PremiumStatCard(
          title: 'الإيرادات',
          value: '45,250 ر.س',
          change: '+12.5%',
          icon: Icons.attach_money,
          color: AppTheme.success,
          animationDelay: const Duration(milliseconds: 200),
        ),
        PremiumStatCard(
          title: 'الشحنات',
          value: '156',
          change: '+8.3%',
          icon: Icons.local_shipping,
          color: AppTheme.primary,
          animationDelay: const Duration(milliseconds: 400),
        ),
        PremiumStatCard(
          title: 'السائقين النشطين',
          value: '42/48',
          change: '87.5%',
          icon: Icons.people,
          color: AppTheme.info,
          animationDelay: const Duration(milliseconds: 600),
        ),
        PremiumStatCard(
          title: 'معدل التوصيل',
          value: '98.2%',
          change: '+2.1%',
          icon: Icons.verified,
          color: AppTheme.warning,
          animationDelay: const Duration(milliseconds: 800),
        ),
      ],
    ).animate().fadeIn().slideY(begin: 0.3, end: 0);
  }

  Widget _buildMetricCard(
    String label,
    String value,
    String change,
    IconData icon,
    Color color,
    bool isPositive,
  ) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [
            AppTheme.cardBackground,
            AppTheme.cardBackground.withOpacity(0.8),
          ],
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: color.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Icon(icon, color: color, size: 20),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: isPositive
                      ? Colors.green.withOpacity(0.1)
                      : Colors.red.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(
                      isPositive ? Icons.trending_up : Icons.trending_down,
                      color: isPositive ? Colors.green : Colors.red,
                      size: 14,
                    ),
                    const SizedBox(width: 2),
                    Text(
                      change,
                      style: TextStyle(
                        color: isPositive ? Colors.green : Colors.red,
                        fontSize: 11,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const Spacer(),
          Text(
            value,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 22,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            label,
            style: TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRevenueChart() {
    return Container(
      height: 200,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'الإيرادات والمصروفات',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Row(
                children: [
                  _buildLegendItem('الإيرادات', Colors.green),
                  const SizedBox(width: 16),
                  _buildLegendItem('المصروفات', Colors.red),
                ],
              ),
            ],
          ),
          const SizedBox(height: 20),
          
          // Chart Placeholder
          Expanded(
            child: CustomPaint(
              size: Size(double.infinity, 150),
              painter: RevenueChartPainter(),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLegendItem(String label, Color color) {
    return Row(
      children: [
        Container(
          width: 8,
          height: 8,
          decoration: BoxDecoration(
            color: color,
            borderRadius: BorderRadius.circular(2),
          ),
        ),
        const SizedBox(width: 4),
        Text(
          label,
          style: TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 11,
          ),
        ),
      ],
    );
  }

  Widget _buildPerformanceGrid() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'أداء الفروع',
            style: TextStyle(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          
          _buildPerformanceRow('الرياض', 95, '12,450 ر.س', Colors.green),
          const SizedBox(height: 12),
          _buildPerformanceRow('جدة', 87, '9,800 ر.س', Colors.blue),
          const SizedBox(height: 12),
          _buildPerformanceRow('الدمام', 78, '7,200 ر.س', Colors.orange),
          const SizedBox(height: 12),
          _buildPerformanceRow('مكة', 72, '5,600 ر.س', Colors.red),
        ],
      ),
    );
  }

  Widget _buildPerformanceRow(String branch, int score, String revenue, Color color) {
    return Row(
      children: [
        Container(
          width: 40,
          height: 40,
          decoration: BoxDecoration(
            color: color.withOpacity(0.1),
            borderRadius: BorderRadius.circular(10),
          ),
          child: Center(
            child: Text(
              '$score%',
              style: TextStyle(
                color: color,
                fontSize: 12,
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
                branch,
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 4),
              Container(
                height: 4,
                decoration: BoxDecoration(
                  color: AppTheme.backgroundColor,
                  borderRadius: BorderRadius.circular(2),
                ),
                child: FractionallySizedBox(
                  alignment: Alignment.centerRight,
                  widthFactor: score / 100,
                  child: Container(
                    decoration: BoxDecoration(
                      color: color,
                      borderRadius: BorderRadius.circular(2),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
        
        const SizedBox(width: 12),
        
        Text(
          revenue,
          style: const TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
            fontSize: 12,
          ),
        ),
      ],
    );
  }

  Widget _buildLiveOperations() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'العمليات المباشرة',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: Colors.green.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      width: 6,
                      height: 6,
                      decoration: const BoxDecoration(
                        color: Colors.green,
                        shape: BoxShape.circle,
                      ),
                    ),
                    const SizedBox(width: 4),
                    Text(
                      'مباشر',
                      style: TextStyle(
                        color: Colors.green,
                        fontSize: 11,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          
          Row(
            children: [
              _buildOperationStat('الشحنات النشطة', '28', AppTheme.primaryColor),
              const SizedBox(width: 12),
              _buildOperationStat('في الطريق', '18', Colors.blue),
              const SizedBox(width: 12),
              _buildOperationStat('تم التسليم', '156', Colors.green),
              const SizedBox(width: 12),
              _buildOperationStat('متأخرة', '3', Colors.red),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildOperationStat(String label, String value, Color color) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: color.withOpacity(0.1),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          children: [
            Text(
              value,
              style: TextStyle(
                color: color,
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              label,
              style: TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 10,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTopDrivers() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'أفضل السائقين',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              TextButton(
                onPressed: () {},
                child: Text(
                  'عرض الكل',
                  style: TextStyle(
                    color: AppTheme.primaryColor,
                    fontSize: 12,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          
          _buildDriverRow('خالد العبدالله', 5.0, 48, '12,450 ر.س', 1),
          const SizedBox(height: 12),
          _buildDriverRow('محمد السالم', 4.9, 42, '11,200 ر.س', 2),
          const SizedBox(height: 12),
          _buildDriverRow('فهد الرشيد', 4.8, 38, '9,800 ر.س', 3),
        ],
      ),
    );
  }

  Widget _buildDriverRow(String name, double rating, int trips, String earnings, int rank) {
    return Row(
      children: [
        // Rank Badge
        Container(
          width: 28,
          height: 28,
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: rank == 1
                  ? [Colors.amber, Colors.amber.shade700]
                  : rank == 2
                      ? [Colors.grey.shade400, Colors.grey.shade600]
                      : [Colors.orange.shade300, Colors.orange.shade500],
            ),
            shape: BoxShape.circle,
          ),
          child: Center(
            child: Text(
              '$rank',
              style: const TextStyle(
                color: Colors.white,
                fontSize: 12,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
        
        const SizedBox(width: 12),
        
        // Avatar
        Container(
          width: 40,
          height: 40,
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: [AppTheme.primaryColor, AppTheme.primaryDark],
            ),
            borderRadius: BorderRadius.circular(10),
          ),
          child: Center(
            child: Text(
              name.split(' ').map((e) => e[0]).take(2).join(''),
              style: const TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
        
        const SizedBox(width: 12),
        
        // Info
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                name,
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Row(
                children: [
                  Icon(
                    Icons.star,
                    color: AppTheme.warningColor,
                    size: 14,
                  ),
                  const SizedBox(width: 2),
                  Text(
                    '$rating',
                    style: TextStyle(
                      color: AppTheme.textSecondary,
                      fontSize: 12,
                    ),
                  ),
                  const SizedBox(width: 8),
                  Text(
                    '$trips رحلة',
                    style: TextStyle(
                      color: AppTheme.textSecondary,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
        
        // Earnings
        Text(
          earnings,
          style: TextStyle(
            color: Colors.green,
            fontWeight: FontWeight.bold,
          ),
        ),
      ],
    );
  }

  Widget _buildRevenueChart() {
    final revenueData = [25000, 32000, 28000, 45000, 38000, 52000, 45250];
    final timeLabels = ['سبت', 'أحد', 'إثنين', 'ثلاثاء', 'أربعاء', 'خميس', 'جمعة'];
    
    return PremiumRevenueChart(
      revenueData: revenueData,
      timeLabels: timeLabels,
      title: 'إجمالي الإيرادات',
    ).animate().fadeIn(delay: const Duration(milliseconds: 600)).slideY(begin: 0.3, end: 0);
  }

  Widget _buildAlertsSection() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'تنبيهات تحتاج اهتمامك',
            style: TextStyle(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          
          _buildAlertCard(
            '3 شحنات متأخرة',
            'شحنات بحاجة إلى متابعة عاجلة',
            Colors.red,
            Icons.warning,
          ),
          const SizedBox(height: 12),
          _buildAlertCard(
            '2 سائقين غير متاحين',
            'نسبة توفر السائقين منخفضة اليوم',
            Colors.orange,
            Icons.person_off,
          ),
          const SizedBox(height: 12),
          _buildAlertCard(
            'فاتورة معلقة',
            'فاتورة #2024-156 بحاجة إلى مراجعة',
            Colors.blue,
            Icons.receipt,
          ),
        ],
      ),
    );
  }

  Widget _buildAlertCard(String title, String description, Color color, IconData icon) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: color.withOpacity(0.3),
          width: 1,
        ),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: color.withOpacity(0.2),
              shape: BoxShape.circle,
            ),
            child: Icon(icon, color: color, size: 20),
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
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 2),
                Text(
                  description,
                  style: TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
          Icon(
            Icons.arrow_forward_ios,
            color: color,
            size: 16,
          ),
        ],
      ),
    );
  }
}

// Revenue Chart Painter
class RevenueChartPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    // Revenue Line
    final revenuePaint = Paint()
      ..color = Colors.green
      ..strokeWidth = 3
      ..style = PaintingStyle.stroke;

    final revenuePath = Path();
    revenuePath.moveTo(0, size.height * 0.6);
    
    for (int i = 0; i < 7; i++) {
      revenuePath.lineTo(
        size.width * (i + 1) / 7,
        size.height * (0.5 + (i % 4) * 0.1),
      );
    }
    
    canvas.drawPath(revenuePath, revenuePaint);

    // Expense Line
    final expensePaint = Paint()
      ..color = Colors.red
      ..strokeWidth = 3
      ..style = PaintingStyle.stroke;

    final expensePath = Path();
    expensePath.moveTo(0, size.height * 0.8);
    
    for (int i = 0; i < 7; i++) {
      expensePath.lineTo(
        size.width * (i + 1) / 7,
        size.height * (0.7 + (i % 3) * 0.05),
      );
    }
    
    canvas.drawPath(expensePath, expensePaint);

    // Grid lines
    final gridPaint = Paint()
      ..color = Colors.white.withOpacity(0.1)
      ..strokeWidth = 1;

    for (int i = 0; i < 5; i++) {
      canvas.drawLine(
        Offset(0, size.height * i / 5),
        Offset(size.width, size.height * i / 5),
        gridPaint,
      );
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
