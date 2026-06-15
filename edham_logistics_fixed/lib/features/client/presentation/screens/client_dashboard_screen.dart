// ============================================
// 🏠 Client Dashboard Screen - Premium E-Commerce Store
// Enterprise Client Portal with Shopping Experience
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/models/user_model.dart';
import '../../../../core/models/shipment_model.dart';
import '../../../../core/navigation/app_router.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/premium_stat_card.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/widgets/premium/floating_navbar.dart';
import '../../../../core/design_system/shadows.dart';
import '../../../../core/design_system/gradients.dart';
import '../../../../core/design_system/spacing.dart';
import '../../../auth/presentation/bloc/auth_bloc.dart';

class ClientDashboardScreen extends StatelessWidget {
  const ClientDashboardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<AuthBloc, AuthState>(
      builder: (context, state) {
        if (state is! Authenticated) {
          return const Scaffold(
            body: Center(child: CircularProgressIndicator()),
          );
        }

        final user = state.user;

        return Scaffold(
          backgroundColor: AppTheme.background,
          body: SafeArea(
            child: CustomScrollView(
              slivers: [
                // Premium App Bar with Wallet
                SliverToBoxAdapter(
                  child: _buildPremiumAppBar(context, user),
                ),

                // Wallet Balance Card
                SliverToBoxAdapter(
                  child: _buildWalletCard(),
                ),

                // E-Commerce Store Section
                SliverToBoxAdapter(
                  child: _buildStoreSection(),
                ),

                // Quick Actions
                SliverToBoxAdapter(
                  child: _buildQuickActions(context),
                ),

                // Recent Shipments Header
                SliverToBoxAdapter(
                  child: Padding(
                    padding: const EdgeInsets.fromLTRB(20, 24, 20, 12),
                    child: Row(
                      children: [
                        Text(
                          'آخر الشحنات',
                          style: Theme.of(context).textTheme.titleLarge,
                        ),
                        const Spacer(),
                        TextButton(
                          onPressed: () => AppRouter.goShipments(context),
                          child: const Text('عرض الكل'),
                        ),
                      ],
                    ),
                  ),
                ),

                // Shipments List
                SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) => _buildShipmentCard(index),
                    childCount: 5,
                  ),
                ),

                const SliverPadding(padding: EdgeInsets.only(bottom: 80)),
              ],
            ),
          ),

          // Bottom Navigation
          bottomNavigationBar: _buildBottomNav(context),
        );
      },
    );
  }

  Widget _buildPremiumAppBar(BuildContext context, User user) {
    return GlassContainer(
      margin: const EdgeInsets.all(20),
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.primary.withOpacity(0.2),
      boxShadow: AppShadows.glassmorphism,
      child: Row(
        children: [
          // Premium Profile Avatar
          Hero(
            tag: 'profile',
            child: GlassContainer(
              width: 60,
              height: 60,
              radius: 30,
              backgroundColor: AppTheme.primary.withOpacity(0.2),
              borderColor: AppTheme.primary.withOpacity(0.4),
              boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 0.5),
              child: Center(
                child: Text(
                  user.initials,
                  style: const TextStyle(
                    color: AppTheme.textPrimary,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ),
          const SizedBox(width: 16),

          // User Info with Wallet
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'مرحباً، ${user.firstName}',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: AppTheme.textPrimary,
                  ),
                ),
                const SizedBox(height: 4),
                Row(
                  children: [
                    Icon(
                      Icons.account_balance_wallet_outlined,
                      color: AppTheme.primary,
                      size: 16,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      'المحفظة: 2,500 ريال',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.primary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),

          // Notifications with Badge
          GlassContainer(
            width: 50,
            height: 50,
            radius: 25,
            backgroundColor: Colors.white.withOpacity(0.05),
            borderColor: AppTheme.primary.withOpacity(0.2),
            child: Stack(
              children: [
                Center(
                  child: Icon(
                    Icons.notifications_outlined,
                    color: AppTheme.textPrimary,
                    size: 24,
                  ),
                ),
                Positioned(
                  top: 8,
                  right: 8,
                  child: Container(
                    width: 12,
                    height: 12,
                    decoration: BoxDecoration(
                      color: AppTheme.error,
                      borderRadius: BorderRadius.circular(6),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    ).animate().fadeIn().slideY(begin: 0.2, end: 0);
  }

  Widget _buildWalletCard() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: GlassContainer(
        padding: const EdgeInsets.all(24),
        radius: 20,
        backgroundColor: AppTheme.primary.withOpacity(0.1),
        borderColor: AppTheme.primary.withOpacity(0.3),
        boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 0.6),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  Icons.account_balance_wallet,
                  color: AppTheme.primary,
                  size: 28,
                ),
                const SizedBox(width: 12),
                Text(
                  'محفظتك الإلكترونية',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),
            Text(
              '2,500.00 ريال',
              style: Theme.of(context).textTheme.displayMedium?.copyWith(
                color: AppTheme.primary,
                fontWeight: FontWeight.w900,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'رصيد متاح للشحنات',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppTheme.textSecondary,
              ),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: GlowingButton(
                    text: 'شحن الرصيد',
                    onPressed: () {},
                    color: AppTheme.primary,
                    icon: Icons.add_circle_outline,
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: GlassContainer(
                    height: 48,
                    radius: 16,
                    backgroundColor: Colors.white.withOpacity(0.05),
                    borderColor: AppTheme.primary.withOpacity(0.3),
                    child: Center(
                      child: Text(
                        'سجل المعاملات',
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: AppTheme.primary,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ).animate().fadeIn(delay: const Duration(milliseconds: 200)).slideY(begin: 0.3, end: 0),
    );
  }

  Widget _buildStoreSection() {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'المتجر الإلكتروني',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ).animate().fadeIn(delay: const Duration(milliseconds: 400)),
          const SizedBox(height: 16),
          
          // Store Categories
          SizedBox(
            height: 120,
            child: Row(
              children: [
                Expanded(
                  child: _buildStoreCategory(
                    'شحن سريع',
                    Icons.flash_on,
                    AppTheme.primary,
                    'من 50 ريال',
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _buildStoreCategory(
                    'شحن اقتصادي',
                    Icons.savings,
                    AppTheme.success,
                    'من 30 ريال',
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _buildStoreCategory(
                    'شحن دولي',
                    Icons.public,
                    AppTheme.accent,
                    'من 200 ريال',
                  ),
                ),
              ],
            ),
          ),
          
          const SizedBox(height: 20),
          
          // Featured Services
          Text(
            'خدمات مميزة',
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ).animate().fadeIn(delay: const Duration(milliseconds: 600)),
          const SizedBox(height: 12),
          
          SizedBox(
            height: 180,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              itemCount: 3,
              itemBuilder: (context, index) {
                return _buildServiceCard(index);
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStoreCategory(String title, IconData icon, Color color, String price) {
    return GlassContainer(
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: color.withOpacity(0.1),
      borderColor: color.withOpacity(0.3),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(icon, color: color, size: 32),
          const SizedBox(height: 8),
          Text(
            title,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 4),
          Text(
            price,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: color,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    ).animate().fadeIn(delay: Duration(milliseconds: 400 + (title.hashCode % 200)));
  }

  Widget _buildServiceCard(int index) {
    final services = [
      {'title': 'شحن مستعجل', 'price': '150 ريال', 'time': '2-4 ساعات'},
      {'title': 'شحن طبي', 'price': '300 ريال', 'time': 'مبرد مخصص'},
      {'title': 'شحن ضخم', 'price': '500 ريال', 'time': 'حتى 5 طن'},
    ];
    
    final service = services[index];
    
    return Container(
      width: 160,
      margin: const EdgeInsets.only(left: 12),
      child: GlassContainer(
        padding: const EdgeInsets.all(16),
        radius: 16,
        backgroundColor: Colors.white.withOpacity(0.05),
        borderColor: AppTheme.primary.withOpacity(0.2),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              service['title']!,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              service['price']!,
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                color: AppTheme.primary,
                fontWeight: FontWeight.w900,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              service['time']!,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: AppTheme.textSecondary,
              ),
            ),
            const Spacer(),
            GlowingButton(
              text: 'اطلب الآن',
              onPressed: () {},
              color: AppTheme.primary,
              height: 36,
            ),
          ],
        ),
      ),
    ).animate().fadeIn(delay: Duration(milliseconds: 600 + (index * 100)));
  }

  Widget _buildStatsCards() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Row(
        children: [
          Expanded(
            child: PremiumStatCard(
              title: 'الشحنات النشطة',
              value: '12',
              change: '+2 هذا الأسبوع',
              icon: Icons.local_shipping_outlined,
              color: AppTheme.primary,
              animationDelay: const Duration(milliseconds: 200),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: PremiumStatCard(
              title: 'تم التسليم',
              value: '48',
              change: '+8 هذا الأسبوع',
              icon: Icons.check_circle_outline,
              color: AppTheme.success,
              animationDelay: const Duration(milliseconds: 400),
            ),
          ),
        ],
      ),
    ).animate().fadeIn().slideY(begin: 0.2, end: 0);
  }

  Widget _buildCompactStatCard({
    required String title,
    required String value,
    required IconData icon,
    required Color color,
  }) {
    return GlassContainer(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: color.withOpacity(0.1),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Icon(icon, color: color, size: 20),
          ),
          const SizedBox(height: 12),
          Text(
            value,
            style: const TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
              color: AppTheme.textPrimary,
            ),
          ),
          Text(
            title,
            style: const TextStyle(
              fontSize: 12,
              color: AppTheme.textSecondary,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildQuickActions(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'إجراءات سريعة',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ).animate().fadeIn(delay: const Duration(milliseconds: 800)),
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(
                child: GlassContainer(
                  padding: const EdgeInsets.all(16),
                  radius: 16,
                  backgroundColor: AppTheme.primary.withOpacity(0.1),
                  borderColor: AppTheme.primary.withOpacity(0.3),
                  child: InkWell(
                    onTap: () => _navigateToCreateShipment(),
                    child: Column(
                      children: [
                        Icon(Icons.add_circle_outline, color: AppTheme.primary, size: 32),
                        const SizedBox(height: 8),
                        Text(
                          'شحنة جديدة',
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: AppTheme.textPrimary,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                  ),
                ).animate().fadeIn(delay: const Duration(milliseconds: 900)),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: GlassContainer(
                  padding: const EdgeInsets.all(16),
                  radius: 16,
                  backgroundColor: AppTheme.accent.withOpacity(0.1),
                  borderColor: AppTheme.accent.withOpacity(0.3),
                  child: InkWell(
                    onTap: () => _navigateToTracking(),
                    child: Column(
                      children: [
                        Icon(Icons.track_changes, color: AppTheme.accent, size: 32),
                        const SizedBox(height: 8),
                        Text(
                          'تتبع الشحنة',
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: AppTheme.textPrimary,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                  ),
                ).animate().fadeIn(delay: const Duration(milliseconds: 1000)),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: GlassContainer(
                  padding: const EdgeInsets.all(16),
                  radius: 16,
                  backgroundColor: AppTheme.success.withOpacity(0.1),
                  borderColor: AppTheme.success.withOpacity(0.3),
                  child: InkWell(
                    onTap: () => _navigateToBilling(),
                    child: Column(
                      children: [
                        Icon(Icons.receipt_long, color: AppTheme.success, size: 32),
                        const SizedBox(height: 8),
                        Text(
                          'الفواتير',
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: AppTheme.textPrimary,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                  ),
                ).animate().fadeIn(delay: const Duration(milliseconds: 1100)),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildActionCard({
    required IconData icon,
    required String title,
    required Color color,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 12),
        decoration: BoxDecoration(
          color: AppTheme.surface,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: color.withOpacity(0.2)),
        ),
        child: Column(
          children: [
            Icon(icon, color: color, size: 28),
            const SizedBox(height: 8),
            Text(
              title,
              style: TextStyle(
                fontSize: 12,
                color: AppTheme.textSecondary,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildShipmentCard(int index) {
    final shipments = [
      {'status': 'in_transit', 'label': 'قيد التوصيل', 'from': 'الرياض', 'to': 'جدة', 'date': '2024-01-15', 'id': 'EDH-1001'},
      {'status': 'pending', 'label': 'معلق', 'from': 'جدة', 'to': 'الدمام', 'date': '2024-01-16', 'id': 'EDH-1002'},
      {'status': 'delivered', 'label': 'تم التسليم', 'from': 'مكة', 'to': 'المدينة', 'date': '2024-01-14', 'id': 'EDH-1003'},
      {'status': 'confirmed', 'label': 'مؤكد', 'from': 'القصيم', 'to': 'حائل', 'date': '2024-01-17', 'id': 'EDH-1004'},
      {'status': 'picked_up', 'label': 'تم الاستلام', 'from': 'تبوك', 'to': 'الطائف', 'date': '2024-01-13', 'id': 'EDH-1005'},
    ];
    
    final shipment = shipments[index % shipments.length];
    final statusColor = _getStatusColor(shipment['status']!);

    return GlassContainer(
      margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 6),
      padding: const EdgeInsets.all(20),
      radius: 16,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: statusColor.withOpacity(0.2),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                  color: statusColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Text(
                  shipment['label']!,
                  style: TextStyle(
                    color: statusColor,
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              const Spacer(),
              Text(
                '#${shipment['id']}',
                style: const TextStyle(
                  color: AppTheme.textHint,
                  fontSize: 12,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              Icon(
                Icons.location_on_outlined,
                size: 16,
                color: AppTheme.textSecondary,
              ),
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  '${shipment['from']} ← ${shipment['to']}',
                  style: const TextStyle(
                    color: AppTheme.textPrimary,
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Row(
            children: [
              Icon(
                Icons.access_time_outlined,
                size: 16,
                color: AppTheme.textSecondary,
              ),
              const SizedBox(width: 8),
              Text(
                shipment['date']!,
                style: const TextStyle(
                  color: AppTheme.textSecondary,
                  fontSize: 12,
                ),
              ),
              const Spacer(),
              GlowingButton(
                text: 'تتبع',
                onPressed: () => _navigateToTracking(),
                color: statusColor,
                height: 32,
              ),
            ],
          ),
        ],
      ),
    ).animate().fadeIn(delay: Duration(milliseconds: index * 100)).slideX(begin: -0.1, end: 0);
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'in_transit':
        return AppTheme.primary;
      case 'pending':
        return AppTheme.accent;
      case 'delivered':
        return AppTheme.success;
      case 'confirmed':
        return AppTheme.primary;
      case 'picked_up':
        return AppTheme.success;
      default:
        return AppTheme.textHint;
    }
  }

  Widget _buildBottomNav(BuildContext context) {
    return GlassContainer(
      margin: const EdgeInsets.all(20),
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
      radius: 24,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.primary.withOpacity(0.2),
      boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 0.4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          _buildNavItem(
            icon: Icons.home_rounded,
            label: 'الرئيسية',
            isActive: true,
            onTap: () {},
          ),
          _buildNavItem(
            icon: Icons.local_shipping_outlined,
            label: 'الشحنات',
            onTap: () => _navigateToShipments(),
          ),
          _buildNavItem(
            icon: Icons.track_changes_outlined,
            label: 'التتبع',
            onTap: () => _navigateToTracking(),
          ),
          _buildNavItem(
            icon: Icons.person_outline,
            label: 'حسابي',
            onTap: () => _navigateToProfile(),
          ),
        ],
      ),
    );
  }

  Widget _buildNavItem({
    required IconData icon,
    required String label,
    bool isActive = false,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: isActive ? AppTheme.primary.withOpacity(0.2) : Colors.transparent,
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(
              icon,
              color: isActive ? AppTheme.primary : AppTheme.textHint,
              size: 24,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            label,
            style: TextStyle(
              color: isActive ? AppTheme.primary : AppTheme.textHint,
              fontSize: 12,
              fontWeight: isActive ? FontWeight.w600 : FontWeight.normal,
            ),
          ),
        ],
      ),
    );
  }

  // Navigation Methods
  void _navigateToCreateShipment() {
    Navigator.pushNamed(context, '/shipment/cart');
  }

  void _navigateToTracking() {
    Navigator.pushNamed(context, '/tracking');
  }

  void _navigateToBilling() {
    Navigator.pushNamed(context, '/billing');
  }

  void _navigateToShipments() {
    Navigator.pushNamed(context, '/shipments');
  }

  void _navigateToProfile() {
    Navigator.pushNamed(context, '/profile');
  }
}
