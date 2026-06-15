// ============================================
// 🎨 World-Class Onboarding Flow - Premium Experience
// ============================================

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/design_system/shadows.dart';

class OnboardingScreen extends StatefulWidget {
  const OnboardingScreen({super.key});

  @override
  State<OnboardingScreen> createState() => _OnboardingScreenState();
}

class _OnboardingScreenState extends State<OnboardingScreen> {
  final PageController _pageController = PageController();
  int _currentPage = 0;
  String _selectedLanguage = 'English';

  final List<OnboardingPage> _pages = [
    OnboardingPage(
      title: 'Global Logistics\nMade Simple',
      subtitle: 'Track shipments worldwide with real-time precision',
      image: 'assets/images/onboarding/global_logistics.png',
      description: 'Experience the future of logistics with our cutting-edge global tracking system. Monitor your shipments across continents with live updates and predictive analytics.',
    ),
    OnboardingPage(
      title: 'Fleet Management\nAt Your Fingertips',
      subtitle: 'Complete control over your entire fleet',
      image: 'assets/images/onboarding/fleet_management.png',
      description: 'Manage your entire fleet from a single dashboard. Optimize routes, monitor driver performance, and reduce operational costs by up to 30%.',
    ),
    OnboardingPage(
      title: 'Smart Logistics\nPowered by AI',
      subtitle: 'Intelligent automation for maximum efficiency',
      image: 'assets/images/onboarding/smart_logistics.png',
      description: 'Leverage artificial intelligence to predict delays, optimize routes, and automate decision-making. Stay ahead with smart logistics solutions.',
    ),
    OnboardingPage(
      title: 'Real-Time\nMonitoring',
      subtitle: '24/7 visibility into your operations',
      image: 'assets/images/onboarding/realtime_monitoring.png',
      description: 'Monitor every aspect of your logistics operation in real-time. From temperature control to delivery confirmations, never miss a critical update.',
    ),
    OnboardingPage(
      title: 'Cold Chain\nIntegrity',
      subtitle: 'Perfect temperature control for sensitive cargo',
      image: 'assets/images/onboarding/cold_chain.png',
      description: 'Maintain perfect temperature control for pharmaceuticals, food, and other sensitive cargo. Real-time alerts and compliance reporting included.',
    ),
    OnboardingPage(
      title: 'Enterprise\nLogistics Platform',
      subtitle: 'Scale your business with confidence',
      image: 'assets/images/onboarding/enterprise_platform.png',
      description: 'Built for enterprise scale. Handle thousands of shipments, manage complex operations, and grow your business with our robust logistics platform.',
    ),
  ];

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  void _nextPage() {
    if (_currentPage < _pages.length - 1) {
      _pageController.nextPage(
        duration: const Duration(milliseconds: 500),
        curve: Curves.easeInOutCubic,
      );
    } else {
      _navigateToLogin();
    }
  }

  void _previousPage() {
    if (_currentPage > 0) {
      _pageController.previousPage(
        duration: const Duration(milliseconds: 500),
        curve: Curves.easeInOutCubic,
      );
    }
  }

  void _navigateToLogin() {
    context.go('/login');
  }

  void _changeLanguage(String language) {
    setState(() {
      _selectedLanguage = language;
    });
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
                  AppTheme.surface.withOpacity(0.3),
                  AppTheme.primary.withOpacity(0.1),
                ],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
            ),
          ),
          
          // Language switcher (top-right)
          Positioned(
            top: 60,
            right: 20,
            child: GlassContainer(
              width: 140,
              height: 40,
              radius: 20,
              backgroundColor: AppTheme.surface.withOpacity(0.2),
              borderColor: AppTheme.primary.withOpacity(0.3),
              child: DropdownButtonHideUnderline(
                child: DropdownButton<String>(
                  value: _selectedLanguage,
                  icon: Icon(
                    Icons.keyboard_arrow_down,
                    color: AppTheme.textSecondary,
                    size: 20,
                  ),
                  dropdownColor: AppTheme.surface,
                  borderRadius: BorderRadius.circular(12),
                  items: ['English', 'العربية', 'Français', 'Español'].map((String language) {
                    return DropdownMenuItem<String>(
                      value: language,
                      child: Text(
                        language,
                        style: TextStyle(
                          color: AppTheme.textPrimary,
                          fontSize: 14,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    );
                  }).toList(),
                  onChanged: (String? newValue) {
                    if (newValue != null) {
                      _changeLanguage(newValue);
                    }
                  },
                ),
              ),
            ).animate()
              .fadeIn(delay: const Duration(milliseconds: 500))
              .slideY(begin: -0.5, end: 0),
          ),
          
          // Main content
          SafeArea(
            child: Column(
              children: [
                const SizedBox(height: 120),
                
                // Page indicator
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: List.generate(
                    _pages.length,
                    (index) => AnimatedContainer(
                      duration: const Duration(milliseconds: 300),
                      margin: const EdgeInsets.symmetric(horizontal: 4),
                      height: 4,
                      width: _currentPage == index ? 32 : 8,
                      decoration: BoxDecoration(
                        color: _currentPage == index
                            ? AppTheme.primary
                            : AppTheme.textHint.withOpacity(0.3),
                        borderRadius: BorderRadius.circular(2),
                      ),
                    ),
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 600)),
                
                // PageView
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
                      return _buildPage(_pages[index]);
                    },
                  ),
                ),
                
                // Navigation buttons
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
                  child: Row(
                    children: [
                      // Previous button
                      if (_currentPage > 0)
                        Expanded(
                          child: GlassContainer(
                            height: 56,
                            radius: 28,
                            backgroundColor: AppTheme.surface.withOpacity(0.2),
                            borderColor: AppTheme.primary.withOpacity(0.3),
                            child: Center(
                              child: Text(
                                'Previous',
                                style: TextStyle(
                                  color: AppTheme.textPrimary,
                                  fontSize: 16,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                          ).animate()
                            .fadeIn()
                            .scaleXY(begin: 0.8, end: 1.0),
                        ),
                      
                      if (_currentPage > 0) const SizedBox(width: 16),
                      
                      // Next/Get Started button
                      Expanded(
                        child: GlassContainer(
                          height: 56,
                          radius: 28,
                          backgroundColor: AppTheme.primary.withOpacity(0.2),
                          borderColor: AppTheme.primary.withOpacity(0.6),
                          boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 0.3),
                          child: Center(
                            child: Text(
                              _currentPage == _pages.length - 1 ? 'Get Started' : 'Next',
                              style: TextStyle(
                                color: AppTheme.primary,
                                fontSize: 16,
                                fontWeight: FontWeight.w700,
                              ),
                            ),
                          ),
                        ).animate()
                          .fadeIn()
                          .scaleXY(begin: 0.8, end: 1.0)
                          .then()
                          .shimmer(),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildPage(OnboardingPage page) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // Hero image placeholder (would be actual image in production)
          Container(
            width: 280,
            height: 280,
            decoration: BoxDecoration(
              gradient: AppTheme.primaryGradient,
              borderRadius: BorderRadius.circular(40),
              boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 0.4),
            ),
            child: Stack(
              children: [
                // Background pattern
                Positioned.fill(
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(40),
                      color: Colors.white.withOpacity(0.1),
                    ),
                  ),
                ),
                
                // Icon placeholder
                Center(
                  child: Icon(
                    _getIconForPage(_currentPage),
                    size: 120,
                    color: Colors.white.withOpacity(0.9),
                  ),
                ),
                
                // Decorative elements
                Positioned(
                  top: 20,
                  right: 20,
                  child: Container(
                    width: 40,
                    height: 40,
                    decoration: BoxDecoration(
                      color: Colors.white.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(20),
                    ),
                  ),
                ),
                Positioned(
                  bottom: 30,
                  left: 25,
                  child: Container(
                    width: 30,
                    height: 30,
                    decoration: BoxDecoration(
                      color: Colors.white.withOpacity(0.15),
                      borderRadius: BorderRadius.circular(15),
                    ),
                  ),
                ),
              ],
            ),
          ).animate()
            .fadeIn(delay: const Duration(milliseconds: 800))
            .scaleXY(begin: 0.6, end: 1.0, curve: Curves.elasticOut)
            .then()
            .rotate(begin: 0, end: 0.02, duration: const Duration(milliseconds: 2000))
            .then()
            .rotate(begin: 0.02, end: -0.02, duration: const Duration(milliseconds: 2000))
            .repeat(),
          
          const SizedBox(height: 48),
          
          // Title
          Text(
            page.title,
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.headlineLarge?.copyWith(
              fontSize: 32,
              fontWeight: FontWeight.w900,
              color: AppTheme.textPrimary,
              height: 1.2,
              letterSpacing: -0.5,
            ),
          ).animate()
            .fadeIn(delay: const Duration(milliseconds: 1000))
            .slideY(begin: 0.3, end: 0),
          
          const SizedBox(height: 16),
          
          // Subtitle
          Text(
            page.subtitle,
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppTheme.primary,
              height: 1.3,
            ),
          ).animate()
            .fadeIn(delay: const Duration(milliseconds: 1200))
            .slideY(begin: 0.3, end: 0),
          
          const SizedBox(height: 24),
          
          // Description
          Text(
            page.description,
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              fontSize: 16,
              color: AppTheme.textSecondary,
              height: 1.5,
              letterSpacing: 0.2,
            ),
          ).animate()
            .fadeIn(delay: const Duration(milliseconds: 1400))
            .slideY(begin: 0.3, end: 0),
        ],
      ),
    );
  }

  IconData _getIconForPage(int pageIndex) {
    switch (pageIndex) {
      case 0:
        return Icons.public_rounded;
      case 1:
        return Icons.local_shipping_rounded;
      case 2:
        return Icons.psychology_rounded;
      case 3:
        return Icons.monitor_heart_rounded;
      case 4:
        return Icons.ac_unit_rounded;
      case 5:
        return Icons.business_rounded;
      default:
        return Icons logistics_rounded;
    }
  }
}

class OnboardingPage {
  final String title;
  final String subtitle;
  final String image;
  final String description;

  OnboardingPage({
    required this.title,
    required this.subtitle,
    required this.image,
    required this.description,
  });
}
