import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../auth/login_screen.dart';

class OnboardingScreen extends StatefulWidget {
  const OnboardingScreen({Key? key}) : super(key: key);

  @override
  State<OnboardingScreen> createState() => _OnboardingScreenState();
}

class _OnboardingScreenState extends State<OnboardingScreen> {
  final PageController _pageController = PageController();
  int _currentPage = 0;

  final List<OnboardingPage> _pages = [
    OnboardingPage(
      title: 'مرحباً بك في إدهام',
      subtitle: 'نظام الشحن الذكي الذي يربط عالمك',
      description: 'منصة متكاملة لإدارة الشحنات والتتبع المباشر مع واجهة سهلة الاستخدام للعملاء والسائقين',
      imagePath: 'assets/images/onboarding1.jpg',
      icon: Icons.local_shipping,
      color: const Color(0xFFF97316),
    ),
    OnboardingPage(
      title: 'تتبع مباشر وموثوق',
      subtitle: 'راقب شحناتك في الوقت الفعلي',
      description: 'تتبع دقيق مع تحديثات فورية، إشعارات ذكية، ووصول متوقع دقيق لجميع شحناتك',
      imagePath: 'assets/images/onboarding2.jpg',
      icon: Icons.location_on,
      color: const Color(0xFF10B981),
    ),
    OnboardingPage(
      title: 'إدارة متقدمة للأسطول',
      subtitle: 'تحكم كامل في عملياتك اللوجستية',
      description: 'لوحة تحكم شاملة للإشراف، تحليلات متقدمة، وأدوات ذكية لاتخاذ القرارات',
      imagePath: 'assets/images/onboarding3.jpg',
      icon: Icons.dashboard,
      color: const Color(0xFF3B82F6),
    ),
    OnboardingPage(
      title: 'أمان وموثوقية',
      subtitle: 'حماية شحناتك بمعايير عالمية',
      description: 'تشفير متقدم، مراقبة درجة الحرارة، وضمان الجودة للمنتجات الحساسة',
      imagePath: 'assets/images/onboarding4.jpg',
      icon: Icons.security,
      color: const Color(0xFF8B5CF6),
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
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [
              Color(0xFF0A0E1A),
              Color(0xFF1A1F2E),
            ],
          ),
        ),
        child: SafeArea(
          child: Column(
            children: [
              // Skip Button
              Align(
                alignment: Alignment.topLeft,
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: TextButton(
                    onPressed: () => _navigateToLogin(),
                    child: Text(
                      'تخطي',
                      style: TextStyle(
                        color: Colors.white.withOpacity(0.7),
                        fontSize: 16,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ),
                ),
              ),

              // Page View
              Expanded(
                flex: 3,
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

              // Page Indicators
              Expanded(
                flex: 1,
                child: Column(
                  children: [
                    const SizedBox(height: 32),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: List.generate(
                        _pages.length,
                        (index) => _buildIndicator(index == _currentPage),
                      ),
                    ),
                    const SizedBox(height: 32),
                    _buildNavigationButtons(),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildPage(OnboardingPage page) {
    return Padding(
      padding: const EdgeInsets.all(32.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // Icon Container
          Container(
            width: 120,
            height: 120,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [page.color, page.color.withOpacity(0.7)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(30),
              boxShadow: [
                BoxShadow(
                  color: page.color.withOpacity(0.3),
                  blurRadius: 30,
                  offset: const Offset(0, 15),
                ),
              ],
            ),
            child: Icon(
              page.icon,
              color: Colors.white,
              size: 60,
            ),
          )
              .animate()
              .scale(duration: 600.ms, curve: Curves.elasticOut)
              .rotate(duration: 800.ms, curve: Curves.easeInOut),

          const SizedBox(height: 48),

          // Title
          Text(
            page.title,
            style: Theme.of(context).textTheme.headlineMedium?.copyWith(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 28,
            ),
            textAlign: TextAlign.center,
          )
              .animate()
              .fadeIn(duration: 600.ms, delay: 200.ms)
              .slideY(begin: 0.3, end: 0, duration: 600.ms, delay: 200.ms),

          const SizedBox(height: 16),

          // Subtitle
          Text(
            page.subtitle,
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              color: page.color,
              fontWeight: FontWeight.w600,
              fontSize: 20,
            ),
            textAlign: TextAlign.center,
          )
              .animate()
              .fadeIn(duration: 600.ms, delay: 400.ms)
              .slideY(begin: 0.3, end: 0, duration: 600.ms, delay: 400.ms),

          const SizedBox(height: 24),

          // Description
          Text(
            page.description,
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
              color: Colors.white.withOpacity(0.8),
              fontSize: 16,
              height: 1.5,
            ),
            textAlign: TextAlign.center,
          )
              .animate()
              .fadeIn(duration: 600.ms, delay: 600.ms)
              .slideY(begin: 0.3, end: 0, duration: 600.ms, delay: 600.ms),
        ],
      ),
    );
  }

  Widget _buildIndicator(bool isActive) {
    return AnimatedContainer(
      duration: const Duration(milliseconds: 300),
      margin: const EdgeInsets.symmetric(horizontal: 4),
      width: isActive ? 32 : 8,
      height: 8,
      decoration: BoxDecoration(
        color: isActive ? const Color(0xFFF97316) : Colors.white.withOpacity(0.3),
        borderRadius: BorderRadius.circular(4),
      ),
    );
  }

  Widget _buildNavigationButtons() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 32.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          // Previous Button
          if (_currentPage > 0)
            TextButton(
              onPressed: () {
                HapticFeedback.lightImpact();
                _pageController.previousPage(
                  duration: const Duration(milliseconds: 300),
                  curve: Curves.easeInOut,
                );
              },
              child: Text(
                'السابق',
                style: TextStyle(
                  color: Colors.white.withOpacity(0.7),
                  fontSize: 16,
                  fontWeight: FontWeight.w500,
                ),
              ),
            )
          else
            const SizedBox(width: 80),

          // Next/Get Started Button
            ElevatedButton(
              onPressed: () {
                HapticFeedback.mediumImpact();
                if (_currentPage < _pages.length - 1) {
                  _pageController.nextPage(
                    duration: const Duration(milliseconds: 300),
                    curve: Curves.easeInOut,
                  );
                } else {
                  _navigateToLogin();
                }
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFF97316),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(50),
                ),
                elevation: 10,
                shadowColor: const Color(0xFFF97316).withOpacity(0.4),
              ),
              child: Text(
                _currentPage < _pages.length - 1 ? 'التالي' : 'ابدأ الآن',
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: Colors.white,
                ),
              ),
            ),
        ],
      ),
    );
  }

  void _navigateToLogin() {
    Navigator.pushReplacement(
      context,
      PageRouteBuilder(
        pageBuilder: (context, animation, secondaryAnimation) {
          return LoginScreen();
        },
        transitionsBuilder: (context, animation, secondaryAnimation, child) {
          return SlideTransition(
            position: animation.drive(
              Tween(begin: const Offset(1.0, 0.0), end: Offset.zero)
                  .chain(CurveTween(curve: Curves.easeInOut)),
            ),
            child: child,
          );
        },
        transitionDuration: const Duration(milliseconds: 500),
      ),
    );
  }
}

class OnboardingPage {
  final String title;
  final String subtitle;
  final String description;
  final String imagePath;
  final IconData icon;
  final Color color;

  OnboardingPage({
    required this.title,
    required this.subtitle,
    required this.description,
    required this.imagePath,
    required this.icon,
    required this.color,
  });
}
