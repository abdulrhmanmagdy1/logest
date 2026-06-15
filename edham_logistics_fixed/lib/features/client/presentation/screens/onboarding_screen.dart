// ============================================
// 🚀 Edham Logistics - Premium Cinematic Onboarding Screen
// Enterprise Introduction with Glassmorphism Effects
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/navigation/app_router.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';
import '../../../../core/design_system/gradients.dart';

class OnboardingScreen extends StatefulWidget {
  const OnboardingScreen({super.key});

  @override
  State<OnboardingScreen> createState() => _OnboardingScreenState();
}

class _OnboardingScreenState extends State<OnboardingScreen>
    with TickerProviderStateMixin {
  late AnimationController _logoController;
  late AnimationController _slideController;
  late AnimationController _fadeController;
  
  late Animation<double> _logoScale;
  late Animation<double> _logoRotation;
  late Animation<Offset> _slideAnimation;
  late Animation<double> _fadeAnimation;
  
  int _currentPage = 0;
  final PageController _pageController = PageController();

  final List<OnboardingPage> _pages = [
    OnboardingPage(
      title: 'نظام إدهام اللوجستي',
      subtitle: 'حلول الشحن الذكية والفعالة',
      description: 'نقدم لكم أفضل خدمات الشحن والتوصيل مع تتبع مباشر وآمن',
      icon: '🚛️',
      backgroundColor: AppTheme.primary,
    ),
    OnboardingPage(
      title: 'تتبع مباشر',
      subtitle: 'راقب شحناتك في الوقت الفعلي',
      description: 'تتبع شحناتك خطوة بخطوة مع خرائط تفاعلية وإشعارات فورية',
      icon: '📍',
      backgroundColor: AppTheme.accent,
    ),
    OnboardingPage(
      title: 'توصيل سريع',
      subtitle: 'خدمة توصيل موثوقة وسريعة',
      description: 'فريق من السائقين المحترفين يضمن وصول شحنتك بأمان وفي الوقت المحدد',
      icon: '📦',
      backgroundColor: AppTheme.success,
    ),
  ];

  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _startAnimations();
  }

  void _initializeAnimations() {
    _logoController = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    );
    
    _slideController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _fadeController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );

    _logoScale = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _logoController,
      curve: Curves.elasticOut,
    ));

    _logoRotation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _logoController,
      curve: const Interval(0.0, 0.5, curve: Curves.easeInOut),
    ));

    _slideAnimation = Tween<Offset>(
      begin: const Offset(0, 1),
      end: Offset.zero,
    ).animate(CurvedAnimation(
      parent: _slideController,
      curve: Curves.easeOutCubic,
    ));

    _fadeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _fadeController,
      curve: Curves.easeIn,
    ));
  }

  void _startAnimations() {
    _logoController.forward();
    Future.delayed(const Duration(milliseconds: 500), () {
      if (mounted) {
        _slideController.forward();
        _fadeController.forward();
      }
    });
  }

  @override
  void dispose() {
    _logoController.dispose();
    _slideController.dispose();
    _fadeController.dispose();
    _pageController.dispose();
    super.dispose();
  }

  void _nextPage() {
    if (_currentPage < _pages.length - 1) {
      _pageController.nextPage(
        duration: const Duration(milliseconds: 400),
        curve: Curves.easeInOut,
      );
    } else {
      _getStarted();
    }
  }

  void _getStarted() {
    AppRouter.goLogin(context);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: SafeArea(
        child: Column(
          children: [
            // Skip Button
            Align(
              alignment: Alignment.topLeft,
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: TextButton(
                  onPressed: _getStarted,
                  child: Text(
                    'تخطي',
                    style: GoogleFonts.inter(
                      color: AppTheme.textSecondary,
                      fontSize: 16,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
              ),
            ),

            // Premium Logo Animation with Glassmorphism
            GlassContainer(
              width: 140,
              height: 140,
              radius: 35,
              backgroundColor: AppTheme.primary.withOpacity(0.1),
              borderColor: AppTheme.primary.withOpacity(0.3),
              boxShadow: AppShadows.glowing(AppTheme.primary),
              child: Stack(
                children: [
                  // Animated rings
                  Positioned.fill(
                    child: Container(
                      decoration: BoxDecoration(
                        border: Border.all(
                          color: AppTheme.primary.withOpacity(0.3),
                          width: 2,
                        ),
                        borderRadius: BorderRadius.circular(32),
                      ),
                    ),
                  ),
                  Center(
                    child: Text(
                      '🚛️',
                      style: const TextStyle(fontSize: 60),
                    ),
                  ),
                ],
              ),
            ).animate()
              .fadeIn(duration: const Duration(milliseconds: 1000))
              .scale(begin: const Offset(0.5, 0.5), end: const Offset(1, 1))
              .then()
              .shimmer(duration: const Duration(milliseconds: 2000)),

            const SizedBox(height: 40),

            // Premium Page Indicator with Glassmorphism
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List.generate(
                _pages.length,
                (index) => AnimatedContainer(
                  duration: const Duration(milliseconds: 400),
                  margin: const EdgeInsets.symmetric(horizontal: 6),
                  width: _currentPage == index ? 32 : 12,
                  height: 12,
                  decoration: BoxDecoration(
                    gradient: _currentPage == index
                        ? AppTheme.primaryGradient
                        : LinearGradient(
                            colors: [
                              AppTheme.textHint.withOpacity(0.2),
                              AppTheme.textHint.withOpacity(0.1),
                            ],
                          ),
                    borderRadius: BorderRadius.circular(6),
                    boxShadow: _currentPage == index
                        ? AppShadows.glowing(AppTheme.primary, intensity: 0.5)
                        : null,
                  ),
                ).animate()
                    .fadeIn(delay: Duration(milliseconds: index * 100))
                    .scaleXY(begin: 0, end: 1),
              ),
            ),

            const SizedBox(height: 30),

            // Page Content
            Expanded(
              child: PageView.builder(
                controller: _pageController,
                onPageChanged: (index) {
                  setState(() {
                    _currentPage = index;
                  });
                },
                itemCount: _pages.length,
                itemBuilder: (context, index) {
                  final page = _pages[index];
                  return AnimatedBuilder(
                    animation: _slideAnimation,
                    builder: (context, child) {
                      return FadeTransition(
                        opacity: _fadeAnimation,
                        child: SlideTransition(
                          position: _slideAnimation,
                          child: _buildPageContent(page),
                        ),
                      );
                    },
                  );
                },
              ),
            ),

            // Premium Bottom Actions with Glassmorphism
            Padding(
              padding: const EdgeInsets.all(20),
              child: Row(
                children: [
                  // Previous Button with Glassmorphism
                  if (_currentPage > 0)
                    Expanded(
                      child: GlassContainer(
                        height: 56,
                        radius: 16,
                        backgroundColor: Colors.white.withOpacity(0.02),
                        borderColor: AppTheme.primary.withOpacity(0.2),
                        child: TextButton(
                          onPressed: () {
                            _pageController.previousPage(
                              duration: const Duration(milliseconds: 400),
                              curve: Curves.easeInOut,
                            );
                          },
                          child: Text(
                            'السابق',
                            style: GoogleFonts.cairo(
                              color: AppTheme.textPrimary,
                              fontSize: 16,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                      ).animate()
                          .fadeIn(delay: const Duration(milliseconds: 200))
                          .slideX(begin: -0.2, end: 0),
                    ),

                  if (_currentPage > 0) const SizedBox(width: 16),

                  // Next/Get Started Button with Premium Glassmorphism
                  Expanded(
                    child: GlowingButton(
                      text: _currentPage == _pages.length - 1 ? 'ابدأ الآن' : 'التالي',
                      onPressed: () {
                        if (_currentPage == _pages.length - 1) {
                          _getStarted();
                        } else {
                          _pageController.nextPage(
                            duration: const Duration(milliseconds: 400),
                            curve: Curves.easeInOut,
                          );
                        }
                      },
                      color: _pages[_currentPage].backgroundColor,
                      icon: _currentPage == _pages.length - 1 ? Icons.rocket_launch : Icons.arrow_forward,
                      animationDelay: const Duration(milliseconds: 300),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPageContent(OnboardingPage page) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 30),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // Premium Icon with Glassmorphism
          GlassContainer(
            width: 120,
            height: 120,
            radius: 30,
            backgroundColor: page.backgroundColor.withOpacity(0.1),
            borderColor: page.backgroundColor.withOpacity(0.3),
            boxShadow: AppShadows.glowing(page.backgroundColor, intensity: 0.4),
            child: Center(
              child: Text(
                page.icon,
                style: const TextStyle(fontSize: 50),
              ),
            ),
          ),

          const SizedBox(height: 30),

          // Premium Title with Animation
          Text(
            page.title,
            textAlign: TextAlign.center,
            style: GoogleFonts.cairo(
              fontSize: 32,
              fontWeight: FontWeight.bold,
              color: AppTheme.textPrimary,
              height: 1.3,
            ),
          ).animate()
              .fadeIn(delay: const Duration(milliseconds: 200))
              .slideY(begin: 0.2, end: 0),

          const SizedBox(height: 16),

          // Premium Subtitle with Animation
          Text(
            page.subtitle,
            textAlign: TextAlign.center,
            style: GoogleFonts.cairo(
              fontSize: 20,
              fontWeight: FontWeight.w600,
              color: page.backgroundColor,
              height: 1.3,
            ),
          ).animate()
              .fadeIn(delay: const Duration(milliseconds: 400))
              .slideY(begin: 0.2, end: 0),

          const SizedBox(height: 24),

          // Premium Description with Animation
          Text(
            page.description,
            textAlign: TextAlign.center,
            style: GoogleFonts.cairo(
              fontSize: 16,
              color: AppTheme.textSecondary,
              height: 1.5,
            ),
          ).animate()
              .fadeIn(delay: const Duration(milliseconds: 600))
              .slideY(begin: 0.2, end: 0),
        ],
      ),
    );
  }
}

class OnboardingPage {
  final String title;
  final String subtitle;
  final String description;
  final String icon;
  final Color backgroundColor;

  OnboardingPage({
    required this.title,
    required this.subtitle,
    required this.description,
    required this.icon,
    required this.backgroundColor,
  });
}
