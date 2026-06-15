import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../onboarding/onboarding_screen.dart';
import '../../auth/login_screen.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({Key? key}) : super(key: key);

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> with TickerProviderStateMixin {
  late AnimationController _logoController;
  late AnimationController _titleController;
  late AnimationController _buttonController;
  late Animation<double> _logoAnimation;
  late Animation<double> _titleAnimation;
  late Animation<double> _buttonAnimation;

  @override
  void initState() {
    super.initState();
    
    // Initialize animation controllers
    _logoController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _titleController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _buttonController = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    );

    // Create animations
    _logoAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _logoController,
      curve: Curves.easeOutBack,
    ));

    _titleAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _titleController,
      curve: Curves.easeOut,
    ));

    _buttonAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _buttonController,
      curve: Curves.elasticOut,
    ));

    // Start animations with delays
    _startAnimations();
  }

  void _startAnimations() async {
    // Logo animation starts immediately
    _logoController.forward();
    
    // Title animation starts after 300ms
    await Future.delayed(const Duration(milliseconds: 300));
    _titleController.forward();
    
    // Button animation starts after 800ms
    await Future.delayed(const Duration(milliseconds: 800));
    _buttonController.repeat(reverse: true);
  }

  @override
  void dispose() {
    _logoController.dispose();
    _titleController.dispose();
    _buttonController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/splash_background.jpg'),
            fit: BoxFit.cover,
          ),
        ),
        child: Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: [
                const Color(0x1A0A0E1A).withOpacity(0.8),
                const Color(0x0A0E1A).withOpacity(0.95),
              ],
            ),
          ),
          child: SafeArea(
            child: Column(
              children: [
                const Spacer(flex: 2),
                
                // Logo Section
                AnimatedBuilder(
                  animation: _logoAnimation,
                  builder: (context, child) {
                    return Transform.translate(
                      offset: Offset(0, (1 - _logoAnimation.value) * 30),
                      child: Opacity(
                        opacity: _logoAnimation.value,
                        child: Container(
                          width: 80,
                          height: 80,
                          decoration: BoxDecoration(
                            gradient: const LinearGradient(
                              colors: [Color(0xFFF97316), Color(0xFFEA580C)],
                              begin: Alignment.topLeft,
                              end: Alignment.bottomRight,
                            ),
                            borderRadius: BorderRadius.circular(20),
                            boxShadow: [
                              BoxShadow(
                                color: const Color(0xFFF97316).withOpacity(0.3),
                                blurRadius: 20,
                                offset: const Offset(0, 10),
                              ),
                            ],
                          ),
                          child: const Icon(
                            Icons.local_shipping,
                            color: Colors.white,
                            size: 40,
                          ),
                        ),
                      ),
                    );
                  },
                ),
                
                const Spacer(flex: 1),
                
                // Title Section
                AnimatedBuilder(
                  animation: _titleAnimation,
                  builder: (context, child) {
                    return Transform.translate(
                      offset: Offset(0, (1 - _titleAnimation.value) * 20),
                      child: Opacity(
                        opacity: _titleAnimation.value,
                        child: Column(
                          children: [
                            Text(
                              'إدهام',
                              style: Theme.of(context).textTheme.displaySmall?.copyWith(
                                color: Colors.white,
                                fontWeight: FontWeight.bold,
                                fontSize: 36,
                              ),
                              textAlign: TextAlign.center,
                            ),
                            const SizedBox(height: 8),
                            Text(
                              'نظام الشحن الذكي',
                              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                                color: const Color(0xFF9CA3AF),
                                fontSize: 16,
                                fontWeight: FontWeight.w500,
                              ),
                              textAlign: TextAlign.center,
                            ),
                          ],
                        ),
                      ),
                    );
                  },
                ),
                
                const Spacer(flex: 3),
                
                // Start Button
                AnimatedBuilder(
                  animation: _buttonAnimation,
                  builder: (context, child) {
                    return Transform.scale(
                      scale: 0.95 + (_buttonAnimation.value * 0.05),
                      child: ElevatedButton(
                        onPressed: () => _navigateToOnboarding(),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFFF97316),
                          foregroundColor: Colors.white,
                          padding: const EdgeInsets.symmetric(horizontal: 48, vertical: 16),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(50),
                          ),
                          elevation: 10,
                          shadowColor: const Color(0xFFF97316).withOpacity(0.4),
                        ),
                        child: Text(
                          'ابدأ الآن',
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.w600,
                            color: Colors.white,
                          ),
                        ),
                      ),
                    );
                  },
                ),
                
                const Spacer(flex: 1),
                
                // Navigation Dots
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    _buildDot(true),
                    const SizedBox(width: 8),
                    _buildDot(false),
                    const SizedBox(width: 8),
                    _buildDot(false),
                    const SizedBox(width: 8),
                    _buildDot(false),
                  ],
                ),
                
                const Spacer(flex: 1),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildDot(bool isActive) {
    return AnimatedContainer(
      duration: const Duration(milliseconds: 300),
      width: isActive ? 24 : 8,
      height: 8,
      decoration: BoxDecoration(
        color: isActive ? const Color(0xFFF97316) : const Color(0xFF9CA3AF).withOpacity(0.3),
        borderRadius: BorderRadius.circular(isActive ? 4 : 4),
      ),
    );
  }

  void _navigateToOnboarding() {
    HapticFeedback.lightImpact();
    Navigator.pushReplacement(
      context,
      PageRouteBuilder(
        pageBuilder: (context, animation, secondaryAnimation) {
          return OnboardingScreen();
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
