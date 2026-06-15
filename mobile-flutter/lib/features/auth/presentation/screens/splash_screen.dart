// ============================================
// 🎨 Premium Splash Screen - Ultra Modern Logistics Platform
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
      duration: const Duration(milliseconds: 3000),
    );

    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.0, 0.6, curve: Curves.easeInOutCubic),
      ),
    );

    _scaleAnimation = Tween<double>(begin: 0.6, end: 1.0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.0, 0.6, curve: Curves.elasticOut),
      ),
    );

    _controller.forward();
    
    // Navigate after premium animation
    Future.delayed(const Duration(seconds: 4), () {
      if (mounted) {
        context.go('/onboarding');
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
                
                // Ultra-Premium Logo with cinematic animations
                Hero(
                  tag: 'logo',
                  child: GlassContainer(
                    width: 200,
                    height: 200,
                    radius: 50,
                    backgroundColor: AppTheme.primary.withOpacity(0.15),
                    borderColor: AppTheme.primary.withOpacity(0.6),
                    boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 1.2),
                    child: Stack(
                      children: [
                        // Outer rotating ring
                        Positioned.fill(
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: AppTheme.primary.withOpacity(0.4),
                                width: 4,
                              ),
                              borderRadius: BorderRadius.circular(46),
                            ),
                          ).animate()
                            .scaleXY(begin: 0.7, end: 1.3, duration: const Duration(milliseconds: 2500))
                            .then()
                            .scaleXY(begin: 1.3, end: 0.7, duration: const Duration(milliseconds: 2500))
                            .repeat(),
                        ),
                        
                        // Middle pulsing ring
                        Positioned.fill(
                          margin: const EdgeInsets.all(16),
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: AppTheme.primary.withOpacity(0.6),
                                width: 3,
                              ),
                              borderRadius: BorderRadius.circular(34),
                            ),
                          ).animate()
                            .scaleXY(begin: 1.3, end: 0.7, duration: const Duration(milliseconds: 2500))
                            .then()
                            .scaleXY(begin: 0.7, end: 1.3, duration: const Duration(milliseconds: 2500))
                            .repeat(),
                        ),
                        
                        // Inner rotating ring
                        Positioned.fill(
                          margin: const EdgeInsets.all(24),
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: AppTheme.primary.withOpacity(0.8),
                                width: 2,
                              ),
                              borderRadius: BorderRadius.circular(26),
                            ),
                          ).animate()
                            .rotate(begin: 0, end: 0.2, duration: const Duration(milliseconds: 1500))
                            .then()
                            .rotate(begin: 0.2, end: -0.2, duration: const Duration(milliseconds: 1500))
                            .repeat(),
                        ),
                        
                        // Premium logo icon with glow
                        Center(
                          child: Container(
                            width: 90,
                            height: 90,
                            decoration: BoxDecoration(
                              gradient: AppTheme.primaryGradient,
                              borderRadius: BorderRadius.circular(22),
                              boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 1.5),
                            ),
                            child: Icon(
                              Icons.local_shipping_rounded,
                              size: 50,
                              color: Colors.white,
                            ),
                          ).animate()
                            .rotate(begin: 0, end: 0.05, duration: const Duration(milliseconds: 2000))
                            .then()
                            .rotate(begin: 0.05, end: -0.05, duration: const Duration(milliseconds: 2000))
                            .repeat(),
                        ),
                      ],
                    ),
                  ).animate()
                    .fadeIn(duration: const Duration(milliseconds: 1500))
                    .scale(begin: const Offset(0.2, 0.2), end: const Offset(1, 1), curve: Curves.elasticOut)
                    .then()
                    .shimmer(duration: const Duration(milliseconds: 3000)),
                ),
                
                const SizedBox(height: 50),
                
                // Ultra-Premium App Name with cinematic typography
                Text(
                  'EDHAM',
                  style: Theme.of(context).textTheme.displayLarge?.copyWith(
                    fontSize: 72,
                    fontWeight: FontWeight.w900,
                    letterSpacing: 8,
                    color: AppTheme.textPrimary,
                    height: 1.1,
                    shadows: [
                      Shadow(
                        color: AppTheme.primary.withOpacity(0.3),
                        blurRadius: 20,
                        offset: const Offset(0, 4),
                      ),
                    ],
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 800), duration: const Duration(milliseconds: 1500))
                  .slideY(begin: 0.6, end: 0, curve: Curves.easeOutCubic)
                  .then()
                  .shimmer(delay: const Duration(milliseconds: 3000), duration: const Duration(milliseconds: 2500)),
                
                Text(
                  'LOGISTICS',
                  style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                    fontSize: 24,
                    letterSpacing: 16,
                    color: AppTheme.textSecondary,
                    fontWeight: FontWeight.w800,
                    height: 1.2,
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 1000), duration: const Duration(milliseconds: 1200))
                  .slideY(begin: 0.4, end: 0, curve: Curves.easeOutCubic),
                
                const SizedBox(height: 24),
                
                // Premium cinematic tagline
                Text(
                  'Premium Global Logistics Platform',
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    fontSize: 18,
                    color: AppTheme.textHint,
                    letterSpacing: 3,
                    fontWeight: FontWeight.w600,
                    height: 1.3,
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 1200), duration: const Duration(milliseconds: 1500))
                  .slideY(begin: 0.3, end: 0, curve: Curves.easeOutCubic),
                
                const SizedBox(height: 100),
                
                // Ultra-Premium Loading Indicator with advanced animation
                Container(
                  width: 80,
                  height: 80,
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    gradient: AppTheme.primaryGradient,
                    borderRadius: BorderRadius.circular(40),
                    boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 1.0),
                  ),
                  child: Stack(
                    children: [
                      CircularProgressIndicator(
                        strokeWidth: 6,
                        valueColor: const AlwaysStoppedAnimation<Color>(Colors.white),
                        backgroundColor: Colors.white.withOpacity(0.2),
                      ),
                      // Inner rotating indicator
                      Positioned.fill(
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          valueColor: AlwaysStoppedAnimation<Color>(Colors.white.withOpacity(0.6)),
                          backgroundColor: Colors.transparent,
                        ).animate()
                          .rotate(duration: const Duration(milliseconds: 2000))
                          .repeat(),
                      ),
                    ],
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 1500))
                  .scaleXY(begin: 0, end: 1, curve: Curves.elasticOut)
                  .then()
                  .rotate(duration: const Duration(milliseconds: 4000)),
                
                const SizedBox(height: 48),
                
                // Premium loading text with pulse effect
                Text(
                  'Initializing Premium Experience...',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    fontSize: 16,
                    color: AppTheme.textSecondary,
                    letterSpacing: 1.5,
                    fontWeight: FontWeight.w600,
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 1800))
                  .then()
                  .fade(duration: const Duration(milliseconds: 1200))
                  .then()
                  .fadeIn()
                  .then()
                  .scaleXY(begin: 1, end: 1.05, duration: const Duration(milliseconds: 800))
                  .then()
                  .scaleXY(begin: 1.05, end: 1, duration: const Duration(milliseconds: 800))
                  .repeat(),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildAnimatedParticles() {
    return Stack(
      children: List.generate(30, (index) {
        final random = index * 137.5; // Prime number for better distribution
        final size = 2.0 + (index % 3) * 1.5; // Variable sizes
        final opacity = 0.2 + (index % 5) * 0.1; // Variable opacity
        
        return Positioned(
          left: (random * 45.0) % MediaQuery.of(context).size.width,
          top: (random * 25.0) % MediaQuery.of(context).size.height,
          child: Container(
            width: size,
            height: size,
            decoration: BoxDecoration(
              color: AppTheme.primary.withOpacity(opacity),
              borderRadius: BorderRadius.circular(size / 2),
              boxShadow: [
                BoxShadow(
                  color: AppTheme.primary.withOpacity(opacity * 0.5),
                  blurRadius: size,
                  spreadRadius: size / 2,
                ),
              ],
            ),
          ).animate()
            .fadeIn(delay: Duration(milliseconds: index * 80), duration: const Duration(milliseconds: 800))
            .moveY(
              begin: 0,
              end: -MediaQuery.of(context).size.height - 100,
              duration: Duration(seconds: 8 + (index % 4) * 2), // Variable speeds
              curve: Curves.easeIn,
            )
            .then()
            .moveY(
              begin: MediaQuery.of(context).size.height + 100,
              end: 0,
              duration: Duration.zero,
            )
            .repeat(),
        );
      }),
    );
  }
}
