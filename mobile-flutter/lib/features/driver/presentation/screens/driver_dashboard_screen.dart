// ============================================
// 🚛 Driver Dashboard Screen - Premium Modern Design
// Enhanced with Premium Card System & Smooth Animations
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/models/user_model.dart';
import '../../../../core/widgets/premium/premium_card_design.dart';
import '../../../../core/widgets/premium/smooth_animation_system.dart';
import '../../../../core/widgets/premium/interactive_charts.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../auth/presentation/bloc/auth_bloc.dart';

class DriverDashboardScreen extends StatelessWidget {
  const DriverDashboardScreen({super.key});

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
                // Header
                SliverToBoxAdapter(
                  child: _buildHeader(user),
                ),

                // Driver Stats
                SliverToBoxAdapter(
                  child: _buildDriverStats(),
                ),

                // Driver Charts
                SliverToBoxAdapter(
                  child: _buildDriverCharts(),
                ),

                // Today's Trips Header
                SliverToBoxAdapter(
                  child: Padding(
                    padding: const EdgeInsets.fromLTRB(20, 24, 20, 12),
                    child: Row(
                      children: [
                        Text(
                          'رحلات اليوم',
                          style: Theme.of(context).textTheme.titleLarge,
                        ),
                        const Spacer(),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 12,
                            vertical: 6,
                          ),
                          decoration: BoxDecoration(
                            color: AppTheme.success.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(20),
                          ),
                          child: const Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Icon(
                                Icons.circle,
                                size: 8,
                                color: AppTheme.success,
                              ),
                              SizedBox(width: 6),
                              Text(
                                'متاح',
                                style: TextStyle(
                                  color: AppTheme.success,
                                  fontSize: 12,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                ),

                // Trips List
                SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) => _buildTripCard(index),
                    childCount: 3,
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

  Widget _buildHeader(User user) {
    return PremiumCard(
      margin: const EdgeInsets.all(20),
      padding: const EdgeInsets.all(20),
      gradient: AppTheme.primaryGradient,
      borderRadius: 20,
      shadows: AppTheme.glowShadow,
      isGlass: false,
      animationDelay: Duration.zero,
      child: Column(
        children: [
          Row(
            children: [
              // Profile with animation
              GlassContainer(
                width: 60,
                height: 60,
                radius: 16,
                backgroundColor: Colors.white.withOpacity(0.2),
                borderColor: Colors.white.withOpacity(0.3),
                child: Center(
                  child: Text(
                    user.initials,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ).animate()
                .scale(begin: const Offset(0, 0), end: const Offset(1, 1))
                .then()
                .shimmer(duration: const Duration(milliseconds: 1500)),
              
              const SizedBox(width: 16),

              // Info
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      user.fullName,
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ).animate()
                      .fadeIn(delay: const Duration(milliseconds: 200))
                      .slideX(begin: -0.2, end: 0),
                    const SizedBox(height: 4),
                    Text(
                      'سائق نقل ثقيل',
                      style: TextStyle(
                        color: Colors.white.withOpacity(0.8),
                        fontSize: 14,
                      ),
                    ).animate()
                      .fadeIn(delay: const Duration(milliseconds: 300))
                      .slideX(begin: -0.2, end: 0),
                  ],
                ),
              ),

              // Settings with micro-interaction
              MicroInteractionButton(
                onPressed: () {},
                child: GlassContainer(
                  width: 40,
                  height: 40,
                  radius: 12,
                  backgroundColor: Colors.white.withOpacity(0.1),
                  borderColor: Colors.white.withOpacity(0.2),
                  child: const Icon(
                    Icons.settings,
                    color: Colors.white,
                    size: 20,
                  ),
                ),
              ),
            ],
          ),

          const SizedBox(height: 20),

          // Vehicle Info
          GlassContainer(
            padding: const EdgeInsets.all(12),
            radius: 12,
            backgroundColor: Colors.white.withOpacity(0.1),
            borderColor: Colors.white.withOpacity(0.2),
            child: Row(
              children: [
                const Icon(
                  Icons.local_shipping,
                  color: Colors.white,
                  size: 24,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'مرسيدس أكتروس',
                        style: TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      Text(
                        'رقم اللوحة: أ ب هـ 1234',
                        style: TextStyle(
                          color: Colors.white.withOpacity(0.8),
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 6,
                  ),
                  decoration: BoxDecoration(
                    color: AppTheme.success,
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const PulsingDot(
                        color: Colors.white,
                        size: 6,
                        pulseDuration: Duration(milliseconds: 2000),
                      ),
                      const SizedBox(width: 6),
                      const Text(
                        'جاهز',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 12,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ).animate()
            .fadeIn(delay: const Duration(milliseconds: 400))
            .slideY(begin: 0.2, end: 0),
        ],
      ),
    );
  }

  Widget _buildDriverStats() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Section title with improved typography
          Padding(
            padding: const EdgeInsets.only(bottom: 20),
            child: Text(
              'إحصائيات اليوم',
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w700,
                letterSpacing: -0.5,
              ),
            ).animate()
              .fadeIn(delay: const Duration(milliseconds: 400))
              .slideY(begin: -0.2, end: 0),
          ),
          
          // Stats row with improved spacing
          Row(
            children: [
              Expanded(
                child: PremiumStatCard(
                  title: 'مكتملة',
                  value: '24',
                  change: '+3 اليوم',
                  icon: Icons.check_circle,
                  color: AppTheme.success,
                  animationDelay: const Duration(milliseconds: 500),
                  showProgress: true,
                  progressValue: 0.85,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: PremiumStatCard(
                  title: 'الكيلومترات',
                  value: '3,420',
                  change: '+150 اليوم',
                  icon: Icons.route,
                  color: AppTheme.accent,
                  animationDelay: const Duration(milliseconds: 600),
                  showProgress: true,
                  progressValue: 0.72,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: PremiumStatCard(
                  title: 'التقييم',
                  value: '4.9',
                  change: '+0.2',
                  icon: Icons.star,
                  color: AppTheme.warning,
                  animationDelay: const Duration(milliseconds: 700),
                  showProgress: true,
                  progressValue: 0.98,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildDriverCharts() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Section title
          Padding(
            padding: const EdgeInsets.only(bottom: 16),
            child: Text(
              'أداء هذا الأسبوع',
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w700,
                letterSpacing: -0.5,
              ),
            ).animate()
              .fadeIn(delay: const Duration(milliseconds: 800))
              .slideY(begin: -0.2, end: 0),
          ),
          
          // Performance Chart
          PremiumLineChart(
            data: ChartAnalytics.generateRandomData(
              count: 7,
              minValue: 15,
              maxValue: 30,
              prefix: 'يوم ',
            ),
            title: 'الرحلات اليومية',
            subtitle: 'متوسط 22 رحلة يومياً',
            primaryColor: AppTheme.primary,
            showGrid: true,
            showTooltip: true,
            animationDuration: const Duration(milliseconds: 800),
            isAnimated: true,
          ),
          
          const SizedBox(height: 16),
          
          // Earnings Chart
          PremiumBarChart(
            data: ChartAnalytics.generateRandomData(
              count: 7,
              minValue: 800,
              maxValue: 1500,
              prefix: 'يوم ',
            ),
            title: 'الأرباح اليومية',
            subtitle: 'متوسط 1,200 ريال يومياً',
            primaryColor: AppTheme.success,
            showGrid: true,
            showTooltip: true,
            animationDuration: const Duration(milliseconds: 800),
            isAnimated: true,
          ),
          
          const SizedBox(height: 16),
          
          // Fuel Efficiency Gauge
          PremiumGaugeChart(
            value: 85,
            maxValue: 100,
            title: 'كفاءة استهلاك الوقود',
            subtitle: 'استهلاك لتر/100كم',
            unit: '%',
            primaryColor: AppTheme.accent,
            rangeColors: [AppTheme.error, AppTheme.warning, AppTheme.success],
            animationDuration: const Duration(milliseconds: 1000),
            isAnimated: true,
          ),
        ],
      ),
    );
  }

  Widget _buildStatCard({
    required IconData icon,
    required String title,
    required String value,
    required Color color,
  }) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppTheme.surface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: color.withOpacity(0.2)),
      ),
      child: Column(
        children: [
          Icon(icon, color: color, size: 24),
          const SizedBox(height: 8),
          Text(
            value,
            style: const TextStyle(
              fontSize: 20,
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

  Widget _buildPremiumTripCard(int index) {
    final statuses = [
      ('جاري التوصيل', AppTheme.statusTransit, Icons.local_shipping),
      ('في الانتظار', AppTheme.statusPending, Icons.pending),
      ('تم التسليم', AppTheme.statusDelivered, Icons.check_circle),
    ];
    final (status, color, icon) = statuses[index % 3];

    return PremiumListCard(
      title: 'الرياض - مستودع الشمال',
      subtitle: 'جدة - شركة التقنية',
      icon: icon,
      color: color,
      onTap: () {},
      actions: [
        GlassContainer(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
          radius: 16,
          backgroundColor: color.withOpacity(0.12),
          borderColor: color.withOpacity(0.35),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(icon, color: color, size: 14),
              const SizedBox(width: 6),
              Text(
                status,
                style: TextStyle(
                  color: color,
                  fontSize: 12,
                  fontWeight: FontWeight.w700,
                  letterSpacing: 0.25,
                ),
              ),
            ],
          ),
        ),
        const SizedBox(width: 12),
        Text(
          '#EDH-${1000 + index}',
          style: const TextStyle(
            color: AppTheme.textHint,
            fontSize: 12,
            fontWeight: FontWeight.w600,
            letterSpacing: 0.5,
          ),
        ),
      ],
      trailing: Column(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              MicroInteractionButton(
                onPressed: () {},
                child: GlassContainer(
                  width: 40,
                  height: 40,
                  radius: 16,
                  backgroundColor: AppTheme.primary.withOpacity(0.12),
                  borderColor: AppTheme.primary.withOpacity(0.35),
                  child: const Icon(
                    Icons.navigation,
                    color: AppTheme.primary,
                    size: 20,
                  ),
                ),
              ),
              const SizedBox(width: 12),
              MicroInteractionButton(
                onPressed: () {},
                child: GlassContainer(
                  width: 40,
                  height: 40,
                  radius: 16,
                  backgroundColor: Colors.white.withOpacity(0.12),
                  borderColor: Colors.white.withOpacity(0.25),
                  child: const Icon(
                    Icons.phone_outlined,
                    color: AppTheme.textSecondary,
                    size: 20,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate()
      .fadeIn(delay: Duration(milliseconds: 800 + (index * 100)))
      .slideX(begin: 0.1, end: 0, delay: Duration(milliseconds: 800 + (index * 100)));
  }

  Widget _buildBottomNav(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: AppTheme.surface,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            blurRadius: 20,
            offset: const Offset(0, -5),
          ),
        ],
      ),
      child: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
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
                label: 'الرحلات',
                onTap: () {},
              ),
              _buildNavItem(
                icon: Icons.history,
                label: 'السجل',
                onTap: () {},
              ),
              _buildNavItem(
                icon: Icons.person_outline,
                label: 'حسابي',
                onTap: () {},
              ),
            ],
          ),
        ),
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
          Icon(
            icon,
            color: isActive ? AppTheme.primary : AppTheme.textHint,
            size: 24,
          ),
          const SizedBox(height: 4),
          Text(
            label,
            style: TextStyle(
              color: isActive ? AppTheme.primary : AppTheme.textHint,
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }
}
