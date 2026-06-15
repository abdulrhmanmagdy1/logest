// ============================================
// 🚀 Zaki Splash Screen - Premium Fast Authentication
// Enterprise Splash with Biometric Authentication
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:local_auth/local_auth.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';

class ZakiSplashScreen extends StatefulWidget {
  const ZakiSplashScreen({super.key});

  @override
  State<ZakiSplashScreen> createState() => _ZakiSplashScreenState();
}

class _ZakiSplashScreenState extends State<ZakiSplashScreen>
    with TickerProviderStateMixin {
  late AnimationController _logoController;
  late AnimationController _pulseController;
  late AnimationController _fadeController;
  late AnimationController _biometricController;
  
  final LocalAuthentication _localAuth = LocalAuthentication();
  bool _isBiometricAvailable = false;
  bool _isBiometricAuthenticated = false;
  bool _isAttemptingBiometric = false;
  bool _showManualLogin = false;

  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _checkBiometricAvailability();
    _startAnimationSequence();
  }

  void _initializeAnimations() {
    _logoController = AnimationController(
      duration: const Duration(milliseconds: 1500),
      vsync: this,
    );
    
    _pulseController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _fadeController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _biometricController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
  }

  void _startAnimationSequence() async {
    // Start logo animation
    _logoController.forward();
    
    // Start pulse effect after logo appears
    await Future.delayed(const Duration(milliseconds: 800));
    _pulseController.repeat(reverse: true);
    
    // Check biometric after initial animations
    await Future.delayed(const Duration(milliseconds: 2000));
    _attemptBiometricAuthentication();
  }

  Future<void> _checkBiometricAvailability() async {
    try {
      bool isAvailable = await _localAuth.canCheckBiometrics;
      bool isDeviceSupported = await _localAuth.isDeviceSupported();
      
      setState(() {
        _isBiometricAvailable = isAvailable && isDeviceSupported;
      });
    } catch (e) {
      setState(() {
        _isBiometricAvailable = false;
      });
    }
  }

  Future<void> _attemptBiometricAuthentication() async {
    if (!_isBiometricAvailable) {
      _showManualLoginOption();
      return;
    }

    setState(() {
      _isAttemptingBiometric = true;
    });

    try {
      bool authenticated = await _localAuth.authenticate(
        localizedReason: 'استخدام بصمة الإصبع للدخول السريع إلى نظام إدهام',
        options: const AuthenticationOptions(
          biometricOnly: true,
          useErrorDialogs: false,
          stickyAuth: true,
        ),
      );

      if (authenticated) {
        setState(() {
          _isBiometricAuthenticated = true;
        });
        
        _biometricController.forward();
        
        // Navigate to main app after successful authentication
        await Future.delayed(const Duration(milliseconds: 1000));
        _navigateToMainApp();
      } else {
        _showManualLoginOption();
      }
    } catch (e) {
      _showManualLoginOption();
    } finally {
      setState(() {
        _isAttemptingBiometric = false;
      });
    }
  }

  void _showManualLoginOption() {
    setState(() {
      _showManualLogin = true;
    });
    _fadeController.forward();
  }

  void _navigateToMainApp() {
    Navigator.pushReplacementNamed(context, '/client/dashboard');
  }

  void _navigateToLogin() {
    Navigator.pushReplacementNamed(context, '/login');
  }

  @override
  void dispose() {
    _logoController.dispose();
    _pulseController.dispose();
    _fadeController.dispose();
    _biometricController.dispose();
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
                  AppTheme.surface.withOpacity(0.3),
                  AppTheme.primary.withOpacity(0.1),
                ],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
            ),
          ),
          
          // Security pattern overlay
          Positioned.fill(
            child: CustomPaint(
              painter: ZakiSecurityPatternPainter(),
            ),
          ),
          
          // Main content
          SafeArea(
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // Premium Logo with Zaki branding
                  _buildZakiLogo(),
                  
                  const SizedBox(height: 40),
                  
                  // Brand text
                  _buildBrandText(),
                  
                  const SizedBox(height: 60),
                  
                  // Biometric authentication section
                  _buildBiometricSection(),
                  
                  const SizedBox(height: 40),
                  
                  // Manual login option
                  if (_showManualLogin) _buildManualLoginOption(),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildZakiLogo() {
    return Hero(
      tag: 'zaki-logo',
      child: GlassContainer(
        width: 140,
        height: 140,
        radius: 35,
        backgroundColor: AppTheme.primary.withOpacity(0.1),
        borderColor: AppTheme.primary.withOpacity(0.3),
        boxShadow: AppShadows.glowing(AppTheme.primary),
        child: Stack(
          children: [
            // Animated rings
            _buildAnimatedRing(60, 60, 50, AppTheme.primary.withOpacity(0.2)),
            _buildAnimatedRing(60, 60, 70, AppTheme.primary.withOpacity(0.1)),
            
            // Main logo icon
            Center(
              child: Icon(
                Icons.security,
                size: 60,
                color: AppTheme.primary,
              ).animate(controller: _logoController)
                .fadeIn(duration: const Duration(milliseconds: 800))
                .scale(begin: const Offset(0.5, 0.5), end: const Offset(1, 1))
                .then()
                .shimmer(duration: const Duration(milliseconds: 2000)),
            ),
            
            // Zaki badge
            Positioned(
              top: 10,
              right: 10,
              child: GlassContainer(
                width: 30,
                height: 30,
                radius: 15,
                backgroundColor: AppTheme.success.withOpacity(0.2),
                borderColor: AppTheme.success.withOpacity(0.4),
                child: const Icon(
                  Icons.verified,
                  size: 16,
                  color: AppTheme.success,
                ),
              ).animate(controller: _biometricController)
                .scale(begin: const Offset(0, 0), end: const Offset(1, 1)),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAnimatedRing(double centerX, double centerY, double radius, Color color) {
    return AnimatedBuilder(
      animation: _pulseController,
      builder: (context, child) {
        return Container(
          width: radius * 2,
          height: radius * 2,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            border: Border.all(
              color: color.withOpacity(_pulseController.value),
              width: 2,
            ),
          ),
        );
      },
    );
  }

  Widget _buildBrandText() {
    return Column(
      children: [
        Text(
          'ZAKI',
          style: Theme.of(context).textTheme.displayLarge?.copyWith(
            color: AppTheme.primary,
            fontWeight: FontWeight.bold,
            letterSpacing: 8,
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 600)),
        
        const SizedBox(height: 8),
        
        Text(
          'Secure Authentication',
          style: Theme.of(context).textTheme.titleMedium?.copyWith(
            color: AppTheme.textSecondary,
            letterSpacing: 2,
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 800)),
        
        const SizedBox(height: 4),
        
        Text(
          'Edham Logistics System',
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: AppTheme.textHint,
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 1000)),
      ],
    );
  }

  Widget _buildBiometricSection() {
    return Column(
      children: [
        if (_isBiometricAvailable && !_isBiometricAuthenticated && !_showManualLogin)
          _buildBiometricPrompt(),
        
        if (_isAttemptingBiometric)
          _buildBiometricLoading(),
        
        if (_isBiometricAuthenticated)
          _buildBiometricSuccess(),
      ],
    );
  }

  Widget _buildBiometricPrompt() {
    return GlassContainer(
      padding: const EdgeInsets.all(24),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.primary.withOpacity(0.2),
      child: Column(
        children: [
          Icon(
            Icons.fingerprint,
            size: 60,
            color: AppTheme.primary,
          ).animate(controller: _pulseController)
            .scale(begin: const Offset(1, 1), end: const Offset(1.1, 1.1))
            .then()
            .scale(begin: const Offset(1.1, 1.1), end: const Offset(1, 1)),
          
          const SizedBox(height: 16),
          
          Text(
            'المصادقة البيومترية',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
          
          const SizedBox(height: 8),
          
          Text(
            'ضع بصمة إصبعك للدخول السريع',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
        ],
      ),
    ).animate().fadeIn(delay: const Duration(milliseconds: 1200));
  }

  Widget _buildBiometricLoading() {
    return GlassContainer(
      padding: const EdgeInsets.all(24),
      radius: 20,
      backgroundColor: AppTheme.primary.withOpacity(0.1),
      borderColor: AppTheme.primary.withOpacity(0.3),
      child: Column(
        children: [
          SizedBox(
            width: 60,
            height: 60,
            child: CircularProgressIndicator(
              strokeWidth: 3,
              valueColor: AlwaysStoppedAnimation<Color>(AppTheme.primary),
            ),
          ),
          
          const SizedBox(height: 16),
          
          Text(
            'جاري التحقق من بصمة الإصبع...',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildBiometricSuccess() {
    return GlassContainer(
      padding: const EdgeInsets.all(24),
      radius: 20,
      backgroundColor: AppTheme.success.withOpacity(0.1),
      borderColor: AppTheme.success.withOpacity(0.3),
      child: Column(
        children: [
          Icon(
            Icons.check_circle,
            size: 60,
            color: AppTheme.success,
          ).animate(controller: _biometricController)
            .scale(begin: const Offset(0, 0), end: const Offset(1, 1)),
          
          const SizedBox(height: 16),
          
          Text(
            'تم المصادقة بنجاح!',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.success,
              fontWeight: FontWeight.bold,
            ),
          ),
          
          const SizedBox(height: 8),
          
          Text(
            'جاري الدخول إلى النظام...',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildManualLoginOption() {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.textHint.withOpacity(0.2),
      child: Column(
        children: [
          Text(
            'تسجيل الدخول اليدوي',
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
            ),
          ),
          
          const SizedBox(height: 12),
          
          Text(
            'استخدم اسم المستخدم وكلمة المرور',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
          
          const SizedBox(height: 20),
          
          GlowingButton(
            text: 'تسجيل الدخول',
            onPressed: _navigateToLogin,
            color: AppTheme.primary,
            icon: Icons.login,
          ),
        ],
      ),
    ).animate(controller: _fadeController)
      .fadeIn()
      .slideY(begin: 0.3, end: 0);
  }
}

// Custom painter for Zaki security pattern
class ZakiSecurityPatternPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = AppTheme.primary.withOpacity(0.02)
      ..style = PaintingStyle.fill;

    // Draw security grid pattern
    for (int i = 0; i < 25; i++) {
      for (int j = 0; j < 25; j++) {
        if ((i + j) % 3 == 0) {
          final x = i * size.width / 25;
          final y = j * size.height / 25;
          canvas.drawCircle(Offset(x, y), 1.5, paint);
        }
      }
    }

    // Draw security lines
    final linePaint = Paint()
      ..color = AppTheme.primary.withOpacity(0.03)
      ..strokeWidth = 1;

    for (int i = 0; i < 10; i++) {
      final startX = (i * size.width / 10);
      final startY = 0;
      final endX = size.width - (i * size.width / 10);
      final endY = size.height;
      
      canvas.drawLine(
        Offset(startX, startY),
        Offset(endX, endY),
        linePaint,
      );
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
