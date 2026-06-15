import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import 'shipments_screen.dart';
import 'maintenance_screen.dart';
import 'reports_screen.dart';
import 'notifications_screen.dart';
import 'profile_screen.dart';

class DashboardScreen extends StatefulWidget {
  const DashboardScreen({super.key});

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> {
  int _currentIndex = 0;

  final List<Widget> _screens = [
    const _DashboardContent(),
    const ShipmentsScreen(),
    const MaintenanceScreen(),
    const ReportsScreen(),
    const NotificationsScreen(),
    const ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_currentIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        backgroundColor: const Color(0xFF003D5C),
        selectedItemColor: const Color(0xFFD4AF37),
        unselectedItemColor: Colors.white70,
        type: BottomNavigationBarType.fixed,
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.dashboard),
            label: 'الرئيسية',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.local_shipping),
            label: 'الشحنات',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.build),
            label: 'الصيانة',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.assessment),
            label: 'التقارير',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.notifications),
            label: 'الإشعارات',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person),
            label: 'الملف',
          ),
        ],
      ),
    );
  }
}

class _DashboardContent extends StatelessWidget {
  const _DashboardContent();

  @override
  Widget build(BuildContext context) {
    final authService = Provider.of<AuthService>(context);
    final userRole = authService.userRole;

    return Scaffold(
      appBar: AppBar(
        title: Row(
          children: [
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: Colors.white,
                shape: BoxShape.circle,
              ),
              child: const Icon(
                Icons.ac_unit,
                size: 24,
                color: Color(0xFF003D5C),
              ),
            ),
            const SizedBox(width: 12),
            const Text('إدهام'),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              final authService = Provider.of<AuthService>(context, listen: false);
              await authService.logout();
              if (context.mounted) {
                Navigator.pushReplacementNamed(context, '/login');
              }
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Welcome Section
            Card(
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'مرحباً، ${authService.user?['name'] ?? 'مستخدم'}',
                      style: Theme.of(context).textTheme.headlineMedium,
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'الدور: ${_getRoleName(userRole)}',
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),

            // Role-specific content
            _buildRoleContent(context, userRole),
          ],
        ),
      ),
    );
  }

  Widget _buildRoleContent(BuildContext context, String? role) {
    switch (role) {
      case 'client':
        return _buildClientDashboard(context);
      case 'supervisor':
        return _buildSupervisorDashboard(context);
      case 'accountant':
        return _buildAccountantDashboard(context);
      case 'driver':
        return _buildDriverDashboard(context);
      case 'employee':
        return _buildEmployeeDashboard(context);
      default:
        return _buildDefaultDashboard(context);
    }
  }

  Widget _buildClientDashboard(BuildContext context) {
    return Column(
      children: [
        _buildActionCard(
          context,
          icon: Icons.local_shipping,
          title: 'طلب حمولة جديدة',
          subtitle: 'إنشاء طلب شحن جديد',
          onTap: () {},
        ),
        const SizedBox(height: 16),
        _buildActionCard(
          context,
          icon: Icons.track_changes,
          title: 'تتبع شحناتي',
          subtitle: 'متابعة حالة الشحنات',
          onTap: () {},
        ),
      ],
    );
  }

  Widget _buildSupervisorDashboard(BuildContext context) {
    return Column(
      children: [
        _buildActionCard(
          context,
          icon: Icons.assignment,
          title: 'إدارة الشحنات',
          subtitle: 'عرض وتعيين الشحنات',
          onTap: () {},
        ),
        const SizedBox(height: 16),
        _buildActionCard(
          context,
          icon: Icons.local_shipping,
          title: 'إدارة الشاحنات',
          subtitle: 'عرض وإدارة الأسطول',
          onTap: () {},
        ),
      ],
    );
  }

  Widget _buildAccountantDashboard(BuildContext context) {
    return Column(
      children: [
        _buildActionCard(
          context,
          icon: Icons.receipt_long,
          title: 'إدارة الفواتير',
          subtitle: 'إنشاء وإدارة الفواتير',
          onTap: () {},
        ),
        const SizedBox(height: 16),
        _buildActionCard(
          context,
          icon: Icons.paid,
          title: 'سجل المدفوعات',
          subtitle: 'عرض سجل المدفوعات',
          onTap: () {},
        ),
      ],
    );
  }

  Widget _buildDriverDashboard(BuildContext context) {
    return Column(
      children: [
        _buildActionCard(
          context,
          icon: Icons.directions_car,
          title: 'رحلاتي',
          subtitle: 'عرض وتحديث الرحلات',
          onTap: () {},
        ),
        const SizedBox(height: 16),
        _buildActionCard(
          context,
          icon: Icons.location_on,
          title: 'تحديث الموقع',
          subtitle: 'إرسال موقعك الحالي',
          onTap: () {},
        ),
      ],
    );
  }

  Widget _buildEmployeeDashboard(BuildContext context) {
    return Column(
      children: [
        _buildActionCard(
          context,
          icon: Icons.car_rental,
          title: 'مركباتي',
          subtitle: 'عرض وإدارة مركباتي',
          onTap: () {},
        ),
        const SizedBox(height: 16),
        _buildActionCard(
          context,
          icon: Icons.route,
          title: 'رحلاتي',
          subtitle: 'عرض وتتبع رحلاتي',
          onTap: () {},
        ),
      ],
    );
  }

  Widget _buildDefaultDashboard(BuildContext context) {
    return const Card(
      child: Padding(
        padding: EdgeInsets.all(20),
        child: Text(
          'لا توجد صلاحيات متاحة',
          style: TextStyle(color: Colors.white70),
        ),
      ),
    );
  }

  Widget _buildActionCard(
    BuildContext context, {
    required IconData icon,
    required String title,
    required String subtitle,
    required VoidCallback onTap,
  }) {
    return Card(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Row(
            children: [
              Container(
                width: 56,
                height: 56,
                decoration: BoxDecoration(
                  color: const Color(0xFF0099D8).withOpacity(0.2),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(
                  icon,
                  size: 28,
                  color: const Color(0xFF0099D8),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: Theme.of(context).textTheme.headlineSmall,
                    ),
                    const SizedBox(height: 4),
                    Text(
                      subtitle,
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                  ],
                ),
              ),
              const Icon(
                Icons.arrow_forward_ios,
                color: Colors.white54,
              ),
            ],
          ),
        ),
      ),
    );
  }

  String _getRoleName(String? role) {
    switch (role) {
      case 'client':
        return 'عميل';
      case 'supervisor':
        return 'مشرف';
      case 'accountant':
        return 'محاسب';
      case 'driver':
        return 'سائق';
      case 'employee':
        return 'موظف';
      default:
        return 'غير محدد';
    }
  }
}
