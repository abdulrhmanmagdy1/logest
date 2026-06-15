import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:lottie/lottie.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/widgets/premium/glass_container.dart';
import '../widgets/premium_onboarding_content.dart';

class PremiumOnboardingScreen extends StatefulWidget {
  const PremiumOnboardingScreen({super.key});

  @override
  State<PremiumOnboardingScreen> createState() => _PremiumOnboardingScreenState();
}

class _PremiumOnboardingScreenState extends State<PremiumOnboardingScreen> {
  final PageController _pageController = PageController();
  int _currentPage = 0;

  final List<OnboardingData> _pages = [
    OnboardingData(
      title: 'Smart Logistics Solutions',
      description: 'Reliable refrigerated, frozen, dry, and general cargo transportation across Saudi Arabia and Gulf countries with real-time operational management.',
      imagePath: 'assets/images/onboarding/smart_logistics.png',
      backgroundColor: AppTheme.primaryGradient,
    ),
    OnboardingData(
      title: 'Real-Time Shipment Tracking',
      description: 'Track your shipments live with GPS monitoring, estimated delivery times, and instant operational updates anytime, anywhere.',
      imagePath: 'assets/images/onboarding/live_tracking.png',
      backgroundColor: const LinearGradient(
        colors: [Color(0xFF1A1A2E), Color(0xFF16213E)],
        begin: Alignment.topLeft,
        end: Alignment.bottomRight,
      ),
    ),
    OnboardingData(
      title: 'Cold Chain Transportation',
      description: 'Advanced temperature-controlled logistics solutions for food, medical, and sensitive products with full monitoring and safety compliance.',
      imagePath: 'assets/images/onboarding/cold_chain.png',
      backgroundColor: const LinearGradient(
        colors: [Color(0xFF0F4C75), Color(0xFF3282B8)],
        begin: Alignment.topLeft,
        end: Alignment.bottomRight,
      ),
    ),
    OnboardingData(
      title: 'Fast & Reliable Delivery',
      description: 'Efficient same-day and scheduled delivery services designed for businesses, warehouses, retail stores, and e-commerce operations.',
      imagePath: 'assets/images/onboarding/fast_delivery.png',
      backgroundColor: const LinearGradient(
        colors: [Color(0xFF2D3748), Color(0xFF4A5568)],
        begin: Alignment.topLeft,
        end: Alignment.bottomRight,
      ),
    ),
    OnboardingData(
      title: 'Enterprise Logistics Platform',
      description: 'Manage shipments, fleet operations, drivers, notifications, analytics, and logistics workflows in one intelligent platform.',
      imagePath: 'assets/images/onboarding/enterprise_platform.png',
      backgroundColor: const LinearGradient(
        colors: [Color(0xFF1A202C), Color(0xFF2D3748)],
        begin: Alignment.topLeft,
        end: Alignment.bottomRight,
      ),
    ),
  ];

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: Stack(
        children: [
          // Background gradient
          Container(
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                colors: [Color(0xFF0A0A0A), Color(0xFF1A1A1A)],
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
              ),
            ),
          ),
          
          // Main content
          PageView.builder(
            controller: _pageController,
            onPageChanged: (index) {
              setState(() {
                _currentPage = index;
              });
            },
            itemCount: _pages.length,
            itemBuilder: (context, index) {
              return PremiumOnboardingContent(
                data: _pages[index],
                onNext: () {
                  if (index < _pages.length - 1) {
                    _pageController.nextPage(
                      duration: const Duration(milliseconds: 500),
                      curve: Curves.easeInOut,
                    );
                  } else {
                    _navigateToLogin();
                  }
                },
                onSkip: _navigateToLogin,
              );
            },
          ),
          
          // Language button
          Positioned(
            top: 50,
            right: 20,
            child: GlassContainer(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              borderRadius: BorderRadius.circular(20),
              child: Text(
                'EN',
                style: GoogleFonts.inter(
                  color: AppTheme.textPrimary,
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ),
          
          // Page indicators
          Positioned(
            bottom: 100,
            left: 0,
            right: 0,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List.generate(
                _pages.length,
                (index) => AnimatedContainer(
                  duration: const Duration(milliseconds: 300),
                  margin: const EdgeInsets.symmetric(horizontal: 4),
                  height: 8,
                  width: _currentPage == index ? 24 : 8,
                  decoration: BoxDecoration(
                    color: _currentPage == index
                        ? AppTheme.primary
                        : AppTheme.textHint.withOpacity(0.3),
                    borderRadius: BorderRadius.circular(4),
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _navigateToLogin() {
    Navigator.of(context).pushReplacementNamed('/login');
  }
}

class OnboardingData {
  final String title;
  final String description;
  final String imagePath;
  final LinearGradient backgroundColor;

  OnboardingData({
    required this.title,
    required this.description,
    required this.imagePath,
    required this.backgroundColor,
  });
}
