// ============================================
// ⭐ Splash Screen - Premium Cinematic App Entry
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/design_system/shadows.dart';
import '../bloc/auth_bloc.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _fadeAnimation;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 2000),
    );

    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.0, 0.5, curve: Curves.easeIn),
      ),
    );

    _scaleAnimation = Tween<double>(begin: 0.8, end: 1.0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.0, 0.5, curve: Curves.easeOut),
      ),
    );

    _controller.forward();
    
    // Navigate after animation
    Future.delayed(const Duration(seconds: 3), () {
      if (mounted) {
        context.go('/login');
      }
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: Stack(
        children: [
          // Premium animated background
          Container(
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  AppTheme.background,
                  AppTheme.surface.withOpacity(0.2),
                  AppTheme.primary.withOpacity(0.05),
                ],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
            ),
          ),
          
          // Animated particles background
          Positioned.fill(
            child: _buildAnimatedParticles(),
          ),
          
          // Main content
          Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(height: 60),
                
                // Premium Logo with advanced animations
                Hero(
                  tag: 'logo',
                  child: GlassContainer(
                    width: 160,
                    height: 160,
                    radius: 40,
                    backgroundColor: AppTheme.primary.withOpacity(0.1),
                    borderColor: AppTheme.primary.withOpacity(0.4),
                    boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 0.8),
                    child: Stack(
                      children: [
                        // Animated rings
                        Positioned.fill(
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: AppTheme.primary.withOpacity(0.3),
                                width: 3,
                              ),
                              borderRadius: BorderRadius.circular(36),
                            ),
                          ).animate()
                            .scaleXY(begin: 0.8, end: 1.2, duration: const Duration(milliseconds: 2000))
                            .then()
                            .scaleXY(begin: 1.2, end: 0.8, duration: const Duration(milliseconds: 2000))
                            .loop(),
                        ),
                        
                        // Inner ring
                        Positioned(
                          top: 12,
                          left: 12,
                          right: 12,
                          bottom: 12,
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: AppTheme.primary.withOpacity(0.5),
                                width: 2,
                              ),
                              borderRadius: BorderRadius.circular(28),
                            ),
                          ).animate()
                            .scaleXY(begin: 1.2, end: 0.8, duration: const Duration(milliseconds: 2000))
                            .then()
                            .scaleXY(begin: 0.8, end: 1.2, duration: const Duration(milliseconds: 2000))
                            .loop(),
                        ),
                        
                        // Logo icon
                        Center(
                          child: Icon(
                            Icons.local_shipping_rounded,
                            size: 80,
                            color: AppTheme.primary,
                          ).animate()
                            .rotate(begin: 0, end: 0.1, duration: const Duration(milliseconds: 1000))
                            .then()
                            .rotate(begin: 0.1, end: -0.1, duration: const Duration(milliseconds: 1000))
                            .repeat(),
                        ),
                      ],
                    ),
                  ).animate()
                    .fadeIn(duration: const Duration(milliseconds: 1200))
                    .scale(begin: const Offset(0.3, 0.3), end: const Offset(1, 1))
                    .then()
                    .shimmer(duration: const Duration(milliseconds: 2000)),
                ),
                
                const SizedBox(height: 50),
                
                // App Name with premium animation
                Text(
                  'Edham',
                  style: Theme.of(context).textTheme.displayLarge?.copyWith(
                    fontSize: 64,
                    fontWeight: FontWeight.w900,
                    letterSpacing: 4,
                    color: AppTheme.textPrimary,
                    height: 1.2,
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 600), duration: const Duration(milliseconds: 1000))
                  .slideY(begin: 0.5, end: 0)
                  .then()
                  .shimmer(delay: const Duration(milliseconds: 2500), duration: const Duration(milliseconds: 2000)),
                
                Text(
                  'LOGISTICS',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    fontSize: 20,
                    letterSpacing: 12,
                    color: AppTheme.textSecondary,
                    fontWeight: FontWeight.w700,
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 800), duration: const Duration(milliseconds: 1000))
                  .slideY(begin: 0.5, end: 0),
                
                const SizedBox(height: 20),
                
                // Premium tagline
                Text(
                  'Enterprise Logistics Platform',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    fontSize: 16,
                    color: AppTheme.textHint,
                    letterSpacing: 2,
                    fontWeight: FontWeight.w500,
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 1000), duration: const Duration(milliseconds: 1000))
                  .slideY(begin: 0.3, end: 0),
                
                const SizedBox(height: 80),
                
                // Premium Loading Indicator
                Container(
                  width: 60,
                  height: 60,
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    gradient: AppTheme.primaryGradient,
                    borderRadius: BorderRadius.circular(30),
                    boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 0.6),
                  ),
                  child: CircularProgressIndicator(
                    strokeWidth: 4,
                    valueColor: const AlwaysStoppedAnimation<Color>(Colors.white),
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 1200))
                  .scaleXY(begin: 0, end: 1)
                  .then()
                  .rotate(duration: const Duration(milliseconds: 3000)),
                
                const SizedBox(height: 40),
                
                // Loading text
                Text(
                  'Loading...',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    fontSize: 14,
                    color: AppTheme.textSecondary,
                    letterSpacing: 1,
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 1400))
                  .then()
                  .fade(duration: const Duration(milliseconds: 1000))
                  .then()
                  .fadeIn(),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildAnimatedParticles() {
    return Stack(
      children: List.generate(20, (index) {
        return Positioned(
          left: (index * 50.0) % 400,
          top: (index * 30.0) % 600,
          child: Container(
            width: 4,
            height: 4,
            decoration: BoxDecoration(
              color: AppTheme.primary.withOpacity(0.3),
              borderRadius: BorderRadius.circular(2),
            ),
          ).animate()
            .fadeIn(delay: Duration(milliseconds: index * 100))
            .moveY(
              begin: 0,
              end: -600,
              duration: const Duration(seconds: 10),
              curve: Curves.linear,
            )
            .then()
            .moveY(
              begin: 600,
              end: 0,
              duration: Duration.zero,
            )
            .loop(),
        );
      }),
    );
  }
}
