import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';

class SupervisorSidebarDashboard extends StatefulWidget {
  const SupervisorSidebarDashboard({super.key});

  @override
  State<SupervisorSidebarDashboard> createState() => _SupervisorSidebarDashboardState();
}

class _SupervisorSidebarDashboardState extends State<SupervisorSidebarDashboard>
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
      backgroundColor: const Color(0xFF1A1A2E),
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
                      const Color(0xFF16213E),
                      const Color(0xFF0F3460),
                      const Color(0xFF1A1A2E),
                    ],
                  ),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.4),
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
                            const Color(0xFFE94560).withOpacity(0.3),
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
                                colors: [Color(0xFFE94560), Color(0xFFFF6B6B)],
                              ),
                              boxShadow: [
                                BoxShadow(
                                  color: const Color(0xFFE94560).withOpacity(0.4),
                                  blurRadius: 10,
                                  spreadRadius: 2,
                                ),
                              ],
                            ),
                            child: const Icon(
                              Icons.admin_panel_settings,
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
                                'المشرف',
                                style: TextStyle(
                                  color: Colors.white,
                                  fontSize: 20,
                                  fontWeight: FontWeight.w900,
                                ),
                              ),
                              Text(
                                'لوحة التحكم',
                                style: TextStyle(
                                  color: const Color(0xFFE94560),
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
                            title: 'لوحة المشرف',
                            index: 0,
                          ),
                          _buildMenuItem(
                            icon: Icons.people,
                            title: 'إدارة السائقين',
                            index: 1,
                          ),
                          _buildMenuItem(
                            icon: Icons.local_shipping,
                            title: 'مراقبة الشحنات',
                            index: 2,
                          ),
                          _buildMenuItem(
                            icon: Icons.assignment,
                            title: 'توزيع المهام',
                            index: 3,
                          ),
                          _buildMenuItem(
                            icon: Icons.analytics,
                            title: 'تحليل الأداء',
                            index: 4,
                          ),
                          _buildMenuItem(
                            icon: Icons.warning,
                            title: 'التنبيهات',
                            index: 5,
                          ),
                          _buildMenuItem(
                            icon: Icons.report,
                            title: 'التقارير',
                            index: 6,
                          ),
                          _buildMenuItem(
                            icon: Icons.settings,
                            title: 'الإعدادات',
                            index: 7,
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
                            const Color(0xFFE94560).withOpacity(0.2),
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
                                colors: [Color(0xFFE94560), Color(0xFFFF6B6B)],
                              ),
                              boxShadow: [
                                BoxShadow(
                                  color: const Color(0xFFE94560).withOpacity(0.4),
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
                                  'المشرف العام',
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 14,
                                    fontWeight: FontWeight.w600,
                                  ),
                                ),
                                Text(
                                  'supervisor@edham.com',
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
                    const Color(0xFFE94560),
                    const Color(0xFFFF6B6B),
                  ],
                )
              : null,
          color: isSelected ? null : Colors.transparent,
          borderRadius: BorderRadius.circular(12),
          boxShadow: isSelected
              ? [
                  BoxShadow(
                    color: const Color(0xFFE94560).withOpacity(0.4),
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
                  color: const Color(0xFFFFD700),
                  boxShadow: [
                    BoxShadow(
                      color: const Color(0xFFFFD700).withOpacity(0.6),
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
            const Color(0xFF1A1A2E),
            const Color(0xFF16213E),
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
                      'لوحة المشرف',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 28,
                        fontWeight: FontWeight.w900,
                      ),
                    ),
                    Text(
                      'مرحباً بك في لوحة تحكم المشرف',
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
                      colors: [Color(0xFFE94560), Color(0xFFFF6B6B)],
                    ),
                    borderRadius: BorderRadius.circular(20),
                    boxShadow: [
                      BoxShadow(
                        color: const Color(0xFFE94560).withOpacity(0.4),
                        blurRadius: 10,
                        spreadRadius: 2,
                      ),
                    ],
                  ),
                  child: Row(
                    children: [
                      Icon(
                        Icons.notifications_active,
                        color: Colors.white,
                        size: 18,
                      ),
                      const SizedBox(width: 8),
                      const Text(
                        'تنبيهات',
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
                    title: 'السائقين النشطين',
                    value: '45',
                    icon: Icons.people,
                    color: const Color(0xFFE94560),
                    change: '+8%',
                    isPositive: true,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _buildStatCard(
                    title: 'المهام المنجزة',
                    value: '127',
                    icon: Icons.check_circle,
                    color: const Color(0xFF27AE60),
                    change: '+15%',
                    isPositive: true,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _buildStatCard(
                    title: 'المهام المعلقة',
                    value: '23',
                    icon: Icons.pending,
                    color: const Color(0xFFF39C12),
                    change: '-5%',
                    isPositive: false,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _buildStatCard(
                    title: 'كفاءة الفريق',
                    value: '92%',
                    icon: Icons.trending_up,
                    color: const Color(0xFF3498DB),
                    change: '+3%',
                    isPositive: true,
                  ),
                ),
              ],
            ),

            const SizedBox(height: 32),

            // Map Section
            _buildMapSection(),

            const SizedBox(height: 32),

            // Stats and Charts Section
            Row(
              children: [
                Expanded(
                  child: _buildChartCard(
                    title: 'أداء السائقين',
                    child: _buildLineChart(),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _buildChartCard(
                    title: 'توزيع المهام',
                    child: _buildPieChart(),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _buildInfoCard(
                    title: 'معلومات النظام',
                    child: _buildSystemInfo(),
                  ),
                ),
              ],
            ),

            const SizedBox(height: 32),

            // Driver Status Section
            _buildDriverStatusSection(),

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
            const Color(0xFF16213E),
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
            color: Colors.black.withOpacity(0.3),
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
            const Color(0xFF16213E),
            const Color(0xFF0F3460),
          ],
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFFE94560).withOpacity(0.3),
          width: 1,
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.3),
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

  Widget _buildInfoCard({
    required String title,
    required Widget child,
  }) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF16213E),
            const Color(0xFF0F3460),
          ],
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFFE94560).withOpacity(0.3),
          width: 1,
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.3),
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
        painter: SupervisorLineChartPainter(),
      ),
    );
  }

  Widget _buildPieChart() {
    return Container(
      height: 200,
      child: CustomPaint(
        painter: SupervisorPieChartPainter(),
      ),
    );
  }

  Widget _buildSystemInfo() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildInfoItem(
          icon: Icons.speed,
          label: 'سرعة النظام',
          value: '98%',
          color: const Color(0xFF27AE60),
        ),
        const SizedBox(height: 12),
        _buildInfoItem(
          icon: Icons.storage,
          label: 'مساحة التخزين',
          value: '45GB',
          color: const Color(0xFF3498DB),
        ),
        const SizedBox(height: 12),
        _buildInfoItem(
          icon: Icons.memory,
          label: 'استهلاك الذاكرة',
          value: '67%',
          color: const Color(0xFFF39C12),
        ),
        const SizedBox(height: 12),
        _buildInfoItem(
          icon: Icons.network_check,
          label: 'حالة الشبكة',
          value: 'ممتازة',
          color: const Color(0xFFE94560),
        ),
        const SizedBox(height: 12),
        _buildInfoItem(
          icon: Icons.security,
          label: 'حماية النظام',
          value: 'نشطة',
          color: const Color(0xFF9B59B6),
        ),
      ],
    );
  }

  Widget _buildInfoItem({
    required IconData icon,
    required String label,
    required String value,
    required Color color,
  }) {
    return Container(
      padding: const EdgeInsets.all(10),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(
          color: color.withOpacity(0.3),
          width: 1,
        ),
      ),
      child: Row(
        children: [
          Icon(
            icon,
            color: color,
            size: 18,
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Text(
              label,
              style: TextStyle(
                color: Colors.white.withOpacity(0.8),
                fontSize: 12,
              ),
            ),
          ),
          Text(
            value,
            style: TextStyle(
              color: color,
              fontSize: 12,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMapSection() {
    return Container(
      height: 400,
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF16213E),
            const Color(0xFF0F3460),
          ],
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFFE94560).withOpacity(0.3),
          width: 1,
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.3),
            blurRadius: 10,
            spreadRadius: 2,
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.all(20),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  'خريطة السائقين',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 18,
                    fontWeight: FontWeight.w700,
                  ),
                ),
                Row(
                  children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: const Color(0xFF27AE60).withOpacity(0.2),
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(
                          color: const Color(0xFF27AE60).withOpacity(0.5),
                          width: 1,
                        ),
                      ),
                      child: const Text(
                        '45 نشط',
                        style: TextStyle(
                          color: Color(0xFF27AE60),
                          fontSize: 12,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: const Color(0xFFF39C12).withOpacity(0.2),
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(
                          color: const Color(0xFFF39C12).withOpacity(0.5),
                          width: 1,
                        ),
                      ),
                      child: const Text(
                        '12 في مهمة',
                        style: TextStyle(
                          color: Color(0xFFF39C12),
                          fontSize: 12,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
          Expanded(
            child: Container(
              margin: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: const Color(0xFF0A0F1F),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(
                  color: const Color(0xFFE94560).withOpacity(0.2),
                  width: 1,
                ),
              ),
              child: CustomPaint(
                painter: MapPainter(),
                child: Stack(
                  children: [
                    // Map markers for drivers
                    Positioned(
                      top: 80,
                      left: 120,
                      child: _buildDriverMarker('أحمد', 'نشط', const Color(0xFF27AE60)),
                    ),
                    Positioned(
                      top: 150,
                      left: 200,
                      child: _buildDriverMarker('محمد', 'في مهمة', const Color(0xFFF39C12)),
                    ),
                    Positioned(
                      top: 200,
                      left: 80,
                      child: _buildDriverMarker('عبدالله', 'متاح', const Color(0xFF3498DB)),
                    ),
                    Positioned(
                      top: 120,
                      left: 280,
                      child: _buildDriverMarker('سالم', 'نشط', const Color(0xFF27AE60)),
                    ),
                    Positioned(
                      top: 250,
                      left: 150,
                      child: _buildDriverMarker('خالد', 'في مهمة', const Color(0xFFF39C12)),
                    ),
                    // Map controls
                    Positioned(
                      top: 10,
                      right: 10,
                      child: Column(
                        children: [
                          _buildMapControl(Icons.add, () {}),
                          const SizedBox(height: 8),
                          _buildMapControl(Icons.remove, () {}),
                          const SizedBox(height: 8),
                          _buildMapControl(Icons.my_location, () {}),
                        ],
                      ),
                    ),
                    // Map legend
                    Positioned(
                      bottom: 10,
                      left: 10,
                      child: Container(
                        padding: const EdgeInsets.all(8),
                        decoration: BoxDecoration(
                          color: Colors.black.withOpacity(0.7),
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            _buildLegendItem('نشط', const Color(0xFF27AE60)),
                            _buildLegendItem('في مهمة', const Color(0xFFF39C12)),
                            _buildLegendItem('متاح', const Color(0xFF3498DB)),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDriverMarker(String name, String status, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.3),
            blurRadius: 4,
            spreadRadius: 1,
          ),
        ],
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 8,
            height: 8,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: color,
            ),
          ),
          const SizedBox(width: 4),
          Text(
            name,
            style: const TextStyle(
              color: Colors.black,
              fontSize: 10,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMapControl(IconData icon, VoidCallback onTap) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: 32,
        height: 32,
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(6),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.2),
              blurRadius: 2,
              spreadRadius: 1,
            ),
          ],
        ),
        child: Icon(
          icon,
          color: const Color(0xFF0A0F1F),
          size: 18,
        ),
      ),
    );
  }

  Widget _buildLegendItem(String label, Color color) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 4),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 8,
            height: 8,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: color,
            ),
          ),
          const SizedBox(width: 6),
          Text(
            label,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 10,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDriverStatusSection() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF16213E),
            const Color(0xFF0F3460),
          ],
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFFE94560).withOpacity(0.3),
          width: 1,
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.3),
            blurRadius: 10,
            spreadRadius: 2,
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'حالة السائقين',
            style: TextStyle(
              color: Colors.white,
              fontSize: 18,
              fontWeight: FontWeight.w700,
            ),
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(
                child: _buildDriverStatusItem(
                  name: 'أحمد محمد',
                  status: 'نشط',
                  statusColor: const Color(0xFF27AE60),
                  tasks: '3 مهام',
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildDriverStatusItem(
                  name: 'محمد علي',
                  status: 'في مهمة',
                  statusColor: const Color(0xFFF39C12),
                  tasks: '1 مهمة',
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildDriverStatusItem(
                  name: 'عبدالله سالم',
                  status: 'متاح',
                  statusColor: const Color(0xFF3498DB),
                  tasks: '0 مهام',
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildDriverStatusItem({
    required String name,
    required String status,
    required Color statusColor,
    required String tasks,
  }) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: statusColor.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: statusColor.withOpacity(0.3),
          width: 1,
        ),
      ),
      child: Column(
        children: [
          Container(
            width: 40,
            height: 40,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              gradient: LinearGradient(
                colors: [statusColor, statusColor.withOpacity(0.7)],
              ),
            ),
            child: const Icon(
              Icons.person,
              color: Colors.white,
              size: 20,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            name,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 12,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 4),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
            decoration: BoxDecoration(
              color: statusColor,
              borderRadius: BorderRadius.circular(8),
            ),
            child: Text(
              status,
              style: const TextStyle(
                color: Colors.white,
                fontSize: 10,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          const SizedBox(height: 4),
          Text(
            tasks,
            style: TextStyle(
              color: Colors.white.withOpacity(0.7),
              fontSize: 10,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRecentActivities() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF16213E),
            const Color(0xFF0F3460),
          ],
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFFE94560).withOpacity(0.3),
          width: 1,
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.3),
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
            icon: Icons.assignment_turned_in,
            title: 'مهمة مكتملة',
            description: 'أحمد محمد أكمل مهمة التوصيل #1234',
            time: 'منذ 5 دقائق',
            color: const Color(0xFF27AE60),
          ),
          _buildActivityItem(
            icon: Icons.add_task,
            title: 'مهمة جديدة',
            description: 'تم تعيين مهمة جديدة لمحمد علي',
            time: 'منذ 15 دقيقة',
            color: const Color(0xFFE94560),
          ),
          _buildActivityItem(
            icon: Icons.warning,
            title: 'تنبيه مهم',
            description: 'تأخر في مهمة #1235',
            time: 'منذ 30 دقيقة',
            color: const Color(0xFFF39C12),
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

class SupervisorLineChartPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = const Color(0xFFE94560)
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
      ..color = const Color(0xFFE94560)
      ..style = PaintingStyle.fill;

    for (final point in points) {
      canvas.drawCircle(point, 4, pointPaint);
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}

class SupervisorPieChartPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 3;

    final sections = [
      {'color': const Color(0xFFE94560), 'value': 0.35},
      {'color': const Color(0xFFFFD700), 'value': 0.25},
      {'color': const Color(0xFF27AE60), 'value': 0.20},
      {'color': const Color(0xFF3498DB), 'value': 0.20},
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

class MapPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    // Draw map background
    final bgPaint = Paint()
      ..color = const Color(0xFF1A2B4C)
      ..style = PaintingStyle.fill;
    
    canvas.drawRect(Rect.fromLTWH(0, 0, size.width, size.height), bgPaint);

    // Draw grid lines
    final gridPaint = Paint()
      ..color = const Color(0xFF2A3B5C)
      ..strokeWidth = 0.5
      ..style = PaintingStyle.stroke;

    // Vertical lines
    for (double x = 0; x < size.width; x += 40) {
      canvas.drawLine(
        Offset(x, 0),
        Offset(x, size.height),
        gridPaint,
      );
    }

    // Horizontal lines
    for (double y = 0; y < size.height; y += 40) {
      canvas.drawLine(
        Offset(0, y),
        Offset(size.width, y),
        gridPaint,
      );
    }

    // Draw roads
    final roadPaint = Paint()
      ..color = const Color(0xFF3A4B6C)
      ..strokeWidth = 2
      ..style = PaintingStyle.stroke;

    // Main road
    final path = Path();
    path.moveTo(0, size.height * 0.5);
    path.quadraticBezierTo(
      size.width * 0.5, size.height * 0.3,
      size.width, size.height * 0.6,
    );
    canvas.drawPath(path, roadPaint);

    // Secondary road
    final path2 = Path();
    path2.moveTo(size.width * 0.2, 0);
    path2.quadraticBezierTo(
      size.width * 0.3, size.height * 0.5,
      size.width * 0.8, size.height,
    );
    canvas.drawPath(path2, roadPaint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
