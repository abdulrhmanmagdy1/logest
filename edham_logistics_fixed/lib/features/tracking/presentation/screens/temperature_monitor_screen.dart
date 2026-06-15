// ============================================
// 🌡️ Temperature Monitor Screen - مراقبة درجات الحرارة
// ============================================

import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';

class TemperatureMonitorScreen extends StatefulWidget {
  const TemperatureMonitorScreen({super.key});

  @override
  State<TemperatureMonitorScreen> createState() => _TemperatureMonitorScreenState();
}

class _TemperatureMonitorScreenState extends State<TemperatureMonitorScreen> {
  int _selectedTimeRange = 0;
  final List<String> _timeRanges = ['ساعة', '6 ساعات', '24 ساعة', '7 أيام'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'مراقبة درجة الحرارة',
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
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            // Current Status Card
            _buildCurrentStatusCard(),
            
            const SizedBox(height: 16),
            
            // Sensors Status
            _buildSensorsStatus(),
            
            const SizedBox(height: 16),
            
            // Time Range Selector
            _buildTimeRangeSelector(),
            
            const SizedBox(height: 16),
            
            // Temperature Chart Placeholder
            _buildTemperatureChart(),
            
            const SizedBox(height: 16),
            
            // Temperature Stats
            _buildTemperatureStats(),
            
            const SizedBox(height: 16),
            
            // Alerts Section
            _buildAlertsSection(),
            
            const SizedBox(height: 16),
            
            // Zone Temperatures
            _buildZoneTemperatures(),
          ],
        ),
      ),
    );
  }

  Widget _buildCurrentStatusCard() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [Colors.blue.shade700, Colors.blue.shade900],
        ),
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: Colors.blue.withOpacity(0.3),
            blurRadius: 20,
            offset: const Offset(0, 8),
          ),
        ],
      ),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'درجة الحرارة الحالية',
                    style: TextStyle(
                      color: Colors.white70,
                      fontSize: 14,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      const Text(
                        '-18.5',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 48,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Column(
                        children: const [
                          Text(
                            '°C',
                            style: TextStyle(
                              color: Colors.white70,
                              fontSize: 20,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: Colors.green.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: const [
                        Icon(
                          Icons.check_circle,
                          color: Colors.green,
                          size: 16,
                        ),
                        SizedBox(width: 4),
                        Text(
                          'في المعدل الطبيعي',
                          style: TextStyle(
                            color: Colors.green,
                            fontSize: 12,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              
              // Circular Progress
              Container(
                width: 100,
                height: 100,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  border: Border.all(
                    color: Colors.white.withOpacity(0.3),
                    width: 8,
                  ),
                ),
                child: Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: const [
                      Icon(
                        Icons.ac_unit,
                        color: Colors.white,
                        size: 32,
                      ),
                      SizedBox(height: 4),
                      Text(
                        'مبرد',
                        style: TextStyle(
                          color: Colors.white70,
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 20),
          
          // Target Range
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              _buildTargetRangeItem('الحد الأدنى', '-25°C', Colors.blue),
              Container(
                height: 30,
                width: 1,
                margin: const EdgeInsets.symmetric(horizontal: 20),
                color: Colors.white.withOpacity(0.3),
              ),
              _buildTargetRangeItem('الحد الأقصى', '-15°C', Colors.orange),
              Container(
                height: 30,
                width: 1,
                margin: const EdgeInsets.symmetric(horizontal: 20),
                color: Colors.white.withOpacity(0.3),
              ),
              _buildTargetRangeItem('الهدف', '-20°C', Colors.green),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildTargetRangeItem(String label, String value, Color color) {
    return Column(
      children: [
        Text(
          label,
          style: TextStyle(
            color: Colors.white.withOpacity(0.7),
            fontSize: 12,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          value,
          style: TextStyle(
            color: color,
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
      ],
    );
  }

  Widget _buildSensorsStatus() {
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
            'حالة المستشعرات',
            style: TextStyle(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              _buildSensorStatus('المقدمة', 'online', '-19.2°C'),
              const SizedBox(width: 12),
              _buildSensorStatus('المنتصف', 'online', '-18.8°C'),
              const SizedBox(width: 12),
              _buildSensorStatus('المؤخرة', 'online', '-18.5°C'),
              const SizedBox(width: 12),
              _buildSensorStatus('الباب', 'warning', 'مفتوح'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildSensorStatus(String name, String status, String value) {
    final isOnline = status == 'online';
    final isWarning = status == 'warning';
    
    Color statusColor = isOnline 
        ? Colors.green 
        : isWarning 
            ? Colors.orange 
            : Colors.red;
    
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: AppTheme.backgroundColor,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: statusColor.withOpacity(0.3),
            width: 1,
          ),
        ),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  width: 8,
                  height: 8,
                  decoration: BoxDecoration(
                    color: statusColor,
                    shape: BoxShape.circle,
                  ),
                ),
                const SizedBox(width: 4),
                Text(
                  name,
                  style: TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 11,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              value,
              style: TextStyle(
                color: isWarning ? Colors.orange : Colors.white,
                fontSize: 14,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTimeRangeSelector() {
    return Container(
      height: 40,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: _timeRanges.length,
        itemBuilder: (context, index) {
          final isSelected = _selectedTimeRange == index;
          return Padding(
            padding: const EdgeInsets.only(right: 8),
            child: ChoiceChip(
              label: Text(_timeRanges[index]),
              selected: isSelected,
              onSelected: (selected) {
                setState(() {
                  _selectedTimeRange = index;
                });
              },
              selectedColor: Colors.blue,
              backgroundColor: AppTheme.cardBackground,
              labelStyle: TextStyle(
                color: isSelected ? Colors.white : AppTheme.textSecondary,
                fontSize: 13,
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildTemperatureChart() {
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
          const Text(
            'سجل درجات الحرارة',
            style: TextStyle(
              color: Colors.white,
              fontSize: 14,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 20),
          
          // Chart Placeholder
          Expanded(
            child: CustomPaint(
              size: Size(double.infinity, 150),
              painter: TemperatureChartPainter(),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTemperatureStats() {
    return Row(
      children: [
        _buildStatCard('الحد الأدنى', '-21.3°C', Icons.arrow_downward, Colors.blue),
        const SizedBox(width: 12),
        _buildStatCard('المتوسط', '-19.1°C', Icons.trending_flat, Colors.green),
        const SizedBox(width: 12),
        _buildStatCard('الحد الأقصى', '-17.2°C', Icons.arrow_upward, Colors.orange),
      ],
    );
  }

  Widget _buildStatCard(String label, String value, IconData icon, Color color) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: AppTheme.cardBackground,
          borderRadius: BorderRadius.circular(16),
        ),
        child: Column(
          children: [
            Icon(icon, color: color, size: 20),
            const SizedBox(height: 8),
            Text(
              value,
              style: const TextStyle(
                color: Colors.white,
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              label,
              style: TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 11,
              ),
            ),
          ],
        ),
      ),
    );
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
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'التنبيهات والإشعارات',
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
          const SizedBox(height: 12),
          
          _buildAlertItem(
            'تنبيه باب مفتوح',
            'تم فتح باب الشاحنة لمدة 3 دقائق',
            'منذ 5 دقائق',
            Colors.orange,
            Icons.meeting_room,
          ),
          const SizedBox(height: 8),
          _buildAlertItem(
            'درجة حرارة مرتفعة',
            'وصلت درجة الحرارة إلى -15.5°C',
            'منذ ساعة',
            Colors.red,
            Icons.thermostat,
          ),
        ],
      ),
    );
  }

  Widget _buildAlertItem(
    String title,
    String description,
    String time,
    Color color,
    IconData icon,
  ) {
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
                    fontSize: 14,
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
          Text(
            time,
            style: TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 11,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildZoneTemperatures() {
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
            'درجات الحرارة حسب المنطقة',
            style: TextStyle(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          
          // Truck Zones Visualization
          Container(
            height: 120,
            child: Row(
              children: [
                _buildZoneItem('المقدمة', '-19.2°C', 0.4),
                const SizedBox(width: 8),
                _buildZoneItem('المنتصف', '-18.8°C', 0.5),
                const SizedBox(width: 8),
                _buildZoneItem('المؤخرة', '-18.5°C', 0.4),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildZoneItem(String name, String temp, double height) {
    final tempValue = double.parse(temp.replaceAll('°C', ''));
    final isNormal = tempValue >= -25 && tempValue <= -15;
    
    return Expanded(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          Container(
            height: 80 * height,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.bottomCenter,
                end: Alignment.topCenter,
                colors: [
                  isNormal ? Colors.blue : Colors.orange,
                  isNormal ? Colors.blue.shade300 : Colors.orange.shade300,
                ],
              ),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Center(
              child: RotatedBox(
                quarterTurns: 3,
                child: Text(
                  temp,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ),
          const SizedBox(height: 8),
          Text(
            name,
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

// Chart Painter
class TemperatureChartPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = Colors.blue.withOpacity(0.5)
      ..strokeWidth = 2
      ..style = PaintingStyle.stroke;

    final fillPaint = Paint()
      ..shader = LinearGradient(
        begin: Alignment.topCenter,
        end: Alignment.bottomCenter,
        colors: [
          Colors.blue.withOpacity(0.3),
          Colors.transparent,
        ],
      ).createShader(Rect.fromLTRB(0, 0, size.width, size.height));

    final path = Path();
    path.moveTo(0, size.height * 0.4);
    
    for (int i = 0; i < 10; i++) {
      path.lineTo(
        size.width * (i + 1) / 10,
        size.height * (0.3 + (i % 3) * 0.1),
      );
    }

    final fillPath = Path.from(path);
    fillPath.lineTo(size.width, size.height);
    fillPath.lineTo(0, size.height);
    fillPath.close();

    canvas.drawPath(fillPath, fillPaint);
    canvas.drawPath(path, paint);

    // Draw target line
    final targetPaint = Paint()
      ..color = Colors.green.withOpacity(0.5)
      ..strokeWidth = 1
      ..style = PaintingStyle.stroke;

    canvas.drawLine(
      Offset(0, size.height * 0.3),
      Offset(size.width, size.height * 0.3),
      targetPaint,
    );
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
