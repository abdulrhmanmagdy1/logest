import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';

class SidebarDashboard extends StatefulWidget {
  const SidebarDashboard({super.key});

  @override
  State<SidebarDashboard> createState() => _SidebarDashboardState();
}

class _SidebarDashboardState extends State<SidebarDashboard>
    with TickerProviderStateMixin {
  late AnimationController _sidebarController;
  late AnimationController _contentController;
  late Animation<double> _sidebarAnimation;
  late Animation<double> _contentAnimation;
  int _selectedMenuIndex = 0;

  @override
  void initState() {
    super.initState();
    _sidebarController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    _contentController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );

    _sidebarAnimation = CurvedAnimation(
      parent: _sidebarController,
      curve: Curves.easeInOut,
    );
    _contentAnimation = CurvedAnimation(
      parent: _contentController,
      curve: Curves.easeOut,
    );

    _sidebarController.forward();
    Future.delayed(const Duration(milliseconds: 300), () {
      if (mounted) _contentController.forward();
    });
  }

  @override
  void dispose() {
    _sidebarController.dispose();
    _contentController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0A1428),
      body: Row(
        children: [
          // Sidebar
          AnimatedBuilder(
            animation: _sidebarAnimation,
            builder: (context, child) {
              return Container(
                width: 280,
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    colors: [
                      const Color(0xFF1B2E4D),
                      const Color(0xFF0F3460),
                      const Color(0xFF0A1428),
                    ],
                  ),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.3),
                      blurRadius: 20,
                      spreadRadius: 5,
                    ),
                  ],
                ),
                child: Column(
                  children: [
                    // Logo Section
                    Container(
                      height: 100,
                      padding: const EdgeInsets.all(20),
                      decoration: BoxDecoration(
                        gradient: LinearGradient(
                          colors: [
                            const Color(0xFF007BA7).withOpacity(0.3),
                            Colors.transparent,
                          ],
                          begin: Alignment.topCenter,
                          end: Alignment.bottomCenter,
                        ),
                      ),
                      child: Row(
                        children: [
                          Container(
                            width: 50,
                            height: 50,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              gradient: const LinearGradient(
                                colors: [Color(0xFF007BA7), Color(0xFF0099D8)],
                              ),
                              boxShadow: [
                                BoxShadow(
                                  color: const Color(0xFF007BA7).withOpacity(0.4),
                                  blurRadius: 10,
                                  spreadRadius: 2,
                                ),
                              ],
                            ),
                            child: const Icon(
                              Icons.local_shipping,
                              color: Colors.white,
                              size: 28,
                            ),
                          ),
                          const SizedBox(width: 12),
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              const Text(
                                'إدهام',
                                style: TextStyle(
                                  color: Colors.white,
                                  fontSize: 20,
                                  fontWeight: FontWeight.w900,
                                ),
                              ),
                              Text(
                                'للنقل المبرد',
                                style: TextStyle(
                                  color: const Color(0xFFD4AF37),
                                  fontSize: 12,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),

                    const SizedBox(height: 20),

                    // Menu Items
                    Expanded(
                      child: ListView(
                        padding: EdgeInsets.zero,
                        children: [
                          _buildMenuItem(
                            icon: Icons.dashboard,
                            title: 'لوحة التحكم',
                            index: 0,
                          ),
                          _buildMenuItem(
                            icon: Icons.local_shipping,
                            title: 'الشحنات',
                            index: 1,
                          ),
                          _buildMenuItem(
                            icon: Icons.people,
                            title: 'العملاء',
                            index: 2,
                          ),
                          _buildMenuItem(
                            icon: Icons.directions_car,
                            title: 'السائقين',
                            index: 3,
                          ),
                          _buildMenuItem(
                            icon: Icons.assessment,
                            title: 'التقارير',
                            index: 4,
                          ),
                          _buildMenuItem(
                            icon: Icons.notifications,
                            title: 'الإشعارات',
                            index: 5,
                          ),
                          _buildMenuItem(
                            icon: Icons.settings,
                            title: 'الإعدادات',
                            index: 6,
                          ),
                        ],
                      ),
                    ),

                    // User Profile Section
                    Container(
                      padding: const EdgeInsets.all(20),
                      decoration: BoxDecoration(
                        gradient: LinearGradient(
                          colors: [
                            Colors.transparent,
                            const Color(0xFF007BA7).withOpacity(0.2),
                          ],
                          begin: Alignment.topCenter,
                          end: Alignment.bottomCenter,
                        ),
                      ),
                      child: Row(
                        children: [
                          Container(
                            width: 40,
                            height: 40,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              gradient: const LinearGradient(
                                colors: [Color(0xFFD4AF37), Color(0xFFF4C430)],
                              ),
                              boxShadow: [
                                BoxShadow(
                                  color: const Color(0xFFD4AF37).withOpacity(0.4),
                                  blurRadius: 8,
                                  spreadRadius: 2,
                                ),
                              ],
                            ),
                            child: const Icon(
                              Icons.person,
                              color: Colors.white,
                              size: 20,
                            ),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                const Text(
                                  'مدير النظام',
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 14,
                                    fontWeight: FontWeight.w600,
                                  ),
                                ),
                                Text(
                                  'admin@edham.com',
                                  style: TextStyle(
                                    color: Colors.white.withOpacity(0.7),
                                    fontSize: 12,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          Icon(
                            Icons.logout,
                            color: Colors.white.withOpacity(0.7),
                            size: 20,
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              );
            },
          ),

          // Main Content
          Expanded(
            child: FadeTransition(
              opacity: _contentAnimation,
              child: _buildMainContent(),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMenuItem({
    required IconData icon,
    required String title,
    required int index,
  }) {
    final isSelected = _selectedMenuIndex == index;
    
    return GestureDetector(
      onTap: () {
        setState(() {
          _selectedMenuIndex = index;
        });
      },
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 300),
        margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
        decoration: BoxDecoration(
          gradient: isSelected
              ? LinearGradient(
                  colors: [
                    const Color(0xFF007BA7),
                    const Color(0xFF0099D8),
                  ],
                )
              : null,
          color: isSelected ? null : Colors.transparent,
          borderRadius: BorderRadius.circular(12),
          boxShadow: isSelected
              ? [
                  BoxShadow(
                    color: const Color(0xFF007BA7).withOpacity(0.4),
                    blurRadius: 10,
                    spreadRadius: 2,
                  ),
                ]
              : null,
        ),
        child: Row(
          children: [
            Icon(
              icon,
              color: isSelected ? Colors.white : Colors.white70,
              size: 20,
            ),
            const SizedBox(width: 12),
            Text(
              title,
              style: TextStyle(
                color: isSelected ? Colors.white : Colors.white70,
                fontSize: 14,
                fontWeight: isSelected ? FontWeight.w600 : FontWeight.w500,
              ),
            ),
            const Spacer(),
            if (isSelected)
              Container(
                width: 6,
                height: 6,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: const Color(0xFFD4AF37),
                  boxShadow: [
                    BoxShadow(
                      color: const Color(0xFFD4AF37).withOpacity(0.6),
                      blurRadius: 4,
                      spreadRadius: 1,
                    ),
                  ],
                ),
              ),
          ],
        ),
      ).animate().fadeIn(delay: Duration(milliseconds: 100 * index)).slideX(
        begin: -0.2,
        end: 0,
        delay: Duration(milliseconds: 100 * index),
      ),
    );
  }

  Widget _buildMainContent() {
    return Container(
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
          colors: [
            const Color(0xFF0A1428),
            const Color(0xFF1B2E4D),
            const Color(0xFF0F3460),
          ],
        ),
      ),
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'لوحة التحكم',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 28,
                        fontWeight: FontWeight.w900,
                      ),
                    ),
                    Text(
                      'مرحباً بك في نظام إدهام للنقل المبرد',
                      style: TextStyle(
                        color: Colors.white.withOpacity(0.7),
                        fontSize: 16,
                      ),
                    ),
                  ],
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  decoration: BoxDecoration(
                    gradient: const LinearGradient(
                      colors: [Color(0xFF007BA7), Color(0xFF0099D8)],
                    ),
                    borderRadius: BorderRadius.circular(20),
                    boxShadow: [
                      BoxShadow(
                        color: const Color(0xFF007BA7).withOpacity(0.4),
                        blurRadius: 10,
                        spreadRadius: 2,
                      ),
                    ],
                  ),
                  child: Row(
                    children: [
                      Icon(
                        Icons.notifications,
                        color: Colors.white,
                        size: 18,
                      ),
                      const SizedBox(width: 8),
                      const Text(
                        'الإشعارات',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 14,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),

            const SizedBox(height: 32),

            // Stats Cards
            Row(
              children: [
                Expanded(
                  child: _buildStatCard(
                    title: 'إجمالي الشحنات',
                    value: '1,234',
                    icon: Icons.local_shipping,
                    color: const Color(0xFF007BA7),
                    change: '+12%',
                    isPositive: true,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _buildStatCard(
                    title: 'السائقين النشطين',
                    value: '89',
                    icon: Icons.people,
                    color: const Color(0xFFD4AF37),
                    change: '+5%',
                    isPositive: true,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _buildStatCard(
                    title: 'الإيرادات',
                    value: '45.2K',
                    icon: Icons.attach_money,
                    color: const Color(0xFF27AE60),
                    change: '+18%',
                    isPositive: true,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _buildStatCard(
                    title: 'معدل التوصيل',
                    value: '98.5%',
                    icon: Icons.assessment,
                    color: const Color(0xFFE74C3C),
                    change: '-2%',
                    isPositive: false,
                  ),
                ),
              ],
            ),

            const SizedBox(height: 32),

            // Charts Section
            Row(
              children: [
                Expanded(
                  flex: 2,
                  child: _buildChartCard(
                    title: 'إحصائيات الشحنات',
                    child: _buildLineChart(),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _buildChartCard(
                    title: 'توزيع الشحنات',
                    child: _buildPieChart(),
                  ),
                ),
              ],
            ),

            const SizedBox(height: 32),

            // Recent Activities
            _buildRecentActivities(),
          ],
        ),
      ),
    );
  }

  Widget _buildStatCard({
    required String title,
    required String value,
    required IconData icon,
    required Color color,
    required String change,
    required bool isPositive,
  }) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF1B2E4D),
            const Color(0xFF0F3460),
          ],
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: color.withOpacity(0.3),
          width: 1,
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            blurRadius: 10,
            spreadRadius: 2,
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: color.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Icon(
                  icon,
                  color: color,
                  size: 20,
                ),
              ),
              const Spacer(),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: isPositive
                      ? const Color(0xFF27AE60).withOpacity(0.2)
                      : const Color(0xFFE74C3C).withOpacity(0.2),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  change,
                  style: TextStyle(
                    color: isPositive ? const Color(0xFF27AE60) : const Color(0xFFE74C3C),
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          Text(
            value,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 24,
              fontWeight: FontWeight.w900,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            title,
            style: TextStyle(
              color: Colors.white.withOpacity(0.7),
              fontSize: 14,
            ),
          ),
        ],
      ),
    ).animate().fadeIn().slideY(begin: 0.2, end: 0);
  }

  Widget _buildChartCard({
    required String title,
    required Widget child,
  }) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF1B2E4D),
            const Color(0xFF0F3460),
          ],
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFF007BA7).withOpacity(0.3),
          width: 1,
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            blurRadius: 10,
            spreadRadius: 2,
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 18,
              fontWeight: FontWeight.w700,
            ),
          ),
          const SizedBox(height: 20),
          Expanded(child: child),
        ],
      ),
    );
  }

  Widget _buildLineChart() {
    return Container(
      height: 200,
      child: CustomPaint(
        painter: LineChartPainter(),
      ),
    );
  }

  Widget _buildPieChart() {
    return Container(
      height: 200,
      child: CustomPaint(
        painter: PieChartPainter(),
      ),
    );
  }

  Widget _buildRecentActivities() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF1B2E4D),
            const Color(0xFF0F3460),
          ],
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFF007BA7).withOpacity(0.3),
          width: 1,
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            blurRadius: 10,
            spreadRadius: 2,
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'الأنشطة الأخيرة',
            style: TextStyle(
              color: Colors.white,
              fontSize: 18,
              fontWeight: FontWeight.w700,
            ),
          ),
          const SizedBox(height: 16),
          _buildActivityItem(
            icon: Icons.local_shipping,
            title: 'شحنة جديدة',
            description: 'شحنة #1234 تمت إضافتها',
            time: 'منذ 5 دقائق',
            color: const Color(0xFF007BA7),
          ),
          _buildActivityItem(
            icon: Icons.check_circle,
            title: 'تم التسليم',
            description: 'شحنة #1233 تم تسليمها بنجاح',
            time: 'منذ 15 دقيقة',
            color: const Color(0xFF27AE60),
          ),
          _buildActivityItem(
            icon: Icons.warning,
            title: 'تنبيه',
            description: 'درجة حرارة الشحنة #1232 مرتفعة',
            time: 'منذ 30 دقيقة',
            color: const Color(0xFFE74C3C),
          ),
        ],
      ),
    );
  }

  Widget _buildActivityItem({
    required IconData icon,
    required String title,
    required String description,
    required String time,
    required Color color,
  }) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
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
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  description,
                  style: TextStyle(
                    color: Colors.white.withOpacity(0.7),
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
          Text(
            time,
            style: TextStyle(
              color: Colors.white.withOpacity(0.5),
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }
}

class LineChartPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = const Color(0xFF007BA7)
      ..strokeWidth = 3
      ..style = PaintingStyle.stroke;

    final path = Path();
    final points = [
      Offset(0, size.height * 0.8),
      Offset(size.width * 0.2, size.height * 0.6),
      Offset(size.width * 0.4, size.height * 0.7),
      Offset(size.width * 0.6, size.height * 0.4),
      Offset(size.width * 0.8, size.height * 0.3),
      Offset(size.width, size.height * 0.2),
    ];

    path.moveTo(points[0].dx, points[0].dy);
    for (int i = 1; i < points.length; i++) {
      path.lineTo(points[i].dx, points[i].dy);
    }

    canvas.drawPath(path, paint);

    // Draw points
    final pointPaint = Paint()
      ..color = const Color(0xFF007BA7)
      ..style = PaintingStyle.fill;

    for (final point in points) {
      canvas.drawCircle(point, 4, pointPaint);
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}

class PieChartPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 3;

    final sections = [
      {'color': const Color(0xFF007BA7), 'value': 0.35},
      {'color': const Color(0xFFD4AF37), 'value': 0.25},
      {'color': const Color(0xFF27AE60), 'value': 0.20},
      {'color': const Color(0xFFE74C3C), 'value': 0.20},
    ];

    double startAngle = -pi / 2;

    for (final section in sections) {
      final paint = Paint()
        ..color = section['color'] as Color
        ..style = PaintingStyle.fill;

      final sweepAngle = (section['value'] as double) * 2 * pi;
      canvas.drawArc(
        Rect.fromCircle(center: center, radius: radius),
        startAngle,
        sweepAngle,
        true,
        paint,
      );

      startAngle += sweepAngle;
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
