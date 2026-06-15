import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:math' as math;
import 'login_screen.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen>
    with TickerProviderStateMixin {
  late AnimationController _logoController;
  late AnimationController _particleController;
  late AnimationController _textController;
  late AnimationController _glowController;

  late Animation<double> _logoScale;
  late Animation<double> _logoRotate;
  late Animation<double> _logoGlow;
  late Animation<Offset> _titleSlide;
  late Animation<double> _titleOpacity;
  late Animation<Offset> _subtitleSlide;
  late Animation<double> _subtitleOpacity;
  late Animation<double> _badgeScale;
  late Animation<double> _loadingOpacity;

  @override
  void initState() {
    super.initState();
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.light,
      ),
    );

    // Logo animation controller
    _logoController = AnimationController(
      duration: const Duration(milliseconds: 2200),
      vsync: this,
    );

    // Particle animation controller
    _particleController = AnimationController(
      duration: const Duration(seconds: 8),
      vsync: this,
    )..repeat();

    // Text animation controller
    _textController = AnimationController(
      duration: const Duration(milliseconds: 2800),
      vsync: this,
    );

    // Glow animation controller
    _glowController = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    )..repeat(reverse: true);

    // Logo animations
    _logoScale = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _logoController,
        curve: const Interval(0.0, 0.6, curve: Curves.elasticOut),
      ),
    );

    _logoRotate = Tween<double>(begin: -math.pi, end: 0.0).animate(
      CurvedAnimation(
        parent: _logoController,
        curve: const Interval(0.0, 0.6, curve: Curves.easeOut),
      ),
    );

    _logoGlow = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _logoController,
        curve: const Interval(0.2, 1.0, curve: Curves.easeInOut),
      ),
    );

    // Title animations
    _titleSlide = Tween<Offset>(begin: const Offset(0, 0.5), end: Offset.zero)
        .animate(
      CurvedAnimation(
        parent: _textController,
        curve: const Interval(0.2, 0.6, curve: Curves.easeOut),
      ),
    );

    _titleOpacity = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _textController,
        curve: const Interval(0.2, 0.6, curve: Curves.easeIn),
      ),
    );

    // Subtitle animations
    _subtitleSlide = Tween<Offset>(begin: const Offset(0, 0.4), end: Offset.zero)
        .animate(
      CurvedAnimation(
        parent: _textController,
        curve: const Interval(0.4, 0.8, curve: Curves.easeOut),
      ),
    );

    _subtitleOpacity = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _textController,
        curve: const Interval(0.4, 0.8, curve: Curves.easeIn),
      ),
    );

    // Badge scale animation
    _badgeScale = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _textController,
        curve: const Interval(0.6, 1.0, curve: Curves.elasticOut),
      ),
    );

    // Loading indicator animation
    _loadingOpacity = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _textController,
        curve: const Interval(0.7, 1.0, curve: Curves.easeIn),
      ),
    );

    _logoController.forward();
    _textController.forward();

    // Navigate after animation completes
    Future.delayed(const Duration(seconds: 4), () {
      if (mounted) {
        Navigator.of(context).pushReplacement(
          PageRouteBuilder(
            transitionDuration: const Duration(milliseconds: 800),
            pageBuilder: (context, animation, secondaryAnimation) =>
                const LoginScreen(),
            transitionsBuilder: (context, animation, secondaryAnimation, child) {
              return FadeTransition(
                opacity: animation,
                child: child,
              );
            },
          ),
        );
      }
    });
  }

  @override
  void dispose() {
    _logoController.dispose();
    _particleController.dispose();
    _textController.dispose();
    _glowController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              Color(0xFF0A1428),
              Color(0xFF1B2E4D),
              Color(0xFF0F3460),
              Color(0xFF1A5F7A),
              Color(0xFF007BA7),
            ],
            stops: [0.0, 0.25, 0.5, 0.75, 1.0],
          ),
        ),
        child: Stack(
          children: [
            // Animated particle background
            Positioned.fill(
              child: AnimatedBuilder(
                animation: _particleController,
                builder: (context, child) {
                  return CustomPaint(
                    painter: LuxuryParticlePainter(
                      progress: _particleController.value,
                    ),
                  );
                },
              ),
            ),

            // Animated circles background
            Positioned.fill(
              child: AnimatedBuilder(
                animation: _particleController,
                builder: (context, child) {
                  return CustomPaint(
                    painter: AnimatedCirclesPainter(
                      progress: _particleController.value,
                    ),
                  );
                },
              ),
            ),

            // Main content
            Center(
              child: SingleChildScrollView(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    // Logo with multiple animation layers
                    AnimatedBuilder(
                      animation: Listenable.merge([
                        _logoController,
                        _glowController,
                      ]),
                      builder: (context, child) {
                        return Transform.rotate(
                          angle: _logoRotate.value,
                          child: Transform.scale(
                            scale: _logoScale.value,
                            child: Container(
                              width: 160,
                              height: 160,
                              decoration: BoxDecoration(
                                shape: BoxShape.circle,
                                gradient: RadialGradient(
                                  colors: [
                                    Colors.white.withOpacity(0.98),
                                    Colors.white.withOpacity(0.95),
                                    Colors.blue.shade50,
                                  ],
                                ),
                                boxShadow: [
                                  // Primary glow
                                  BoxShadow(
                                    color: const Color(0xFFD4AF37)
                                        .withOpacity(0.4 * _logoGlow.value),
                                    blurRadius: 30 + (20 * _glowController.value),
                                    spreadRadius:
                                        10 + (10 * _glowController.value),
                                  ),
                                  // Secondary blue glow
                                  BoxShadow(
                                    color: const Color(0xFF0099D8).withOpacity(
                                        0.3 * _logoGlow.value),
                                    blurRadius: 50 + (30 * _glowController.value),
                                    spreadRadius:
                                        15 + (15 * _glowController.value),
                                  ),
                                  // Inner shadow
                                  BoxShadow(
                                    color: Colors.black.withOpacity(0.1),
                                    blurRadius: 20,
                                    inset: true,
                                  ),
                                  // Outer rim
                                  BoxShadow(
                                    color: const Color(0xFFD4AF37)
                                        .withOpacity(0.2 * _logoGlow.value),
                                    blurRadius: 15,
                                    spreadRadius: 2,
                                  ),
                                ],
                              ),
                              child: Stack(
                                alignment: Alignment.center,
                                children: [
                                  // Rotating background ring
                                  Transform.rotate(
                                    angle: _particleController.value *
                                        2 *
                                        math.pi,
                                    child: Container(
                                      decoration: BoxDecoration(
                                        shape: BoxShape.circle,
                                        border: Border.all(
                                          color: const Color(0xFFD4AF37)
                                              .withOpacity(0.15),
                                          width: 1,
                                        ),
                                      ),
                                    ),
                                  ),
                                  // Edham logo with perfect styling
                                  Container(
                                    padding: const EdgeInsets.all(8),
                                    decoration: BoxDecoration(
                                      shape: BoxShape.circle,
                                      gradient: RadialGradient(
                                        colors: [
                                          Colors.white.withOpacity(0.15),
                                          Colors.transparent,
                                        ],
                                      ),
                                      boxShadow: [
                                        BoxShadow(
                                          color: const Color(0xFF007BA7).withOpacity(0.3),
                                          blurRadius: 30,
                                          spreadRadius: 10,
                                        ),
                                        BoxShadow(
                                          color: Colors.white.withOpacity(0.2),
                                          blurRadius: 15,
                                          spreadRadius: 5,
                                        ),
                                      ],
                                    ),
                                    child: Image.asset(
                                      'assets/images/edham-logo.png',
                                      width: 150,
                                      height: 150,
                                      fit: BoxFit.contain,
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        );
                      },
                    ),

                    const SizedBox(height: 50),

                    // Title with advanced animation
                    SlideTransition(
                      position: _titleSlide,
                      child: FadeTransition(
                        opacity: _titleOpacity,
                        child: Column(
                          children: [
                            // Arabic title with gradient
                            ShaderMask(
                              shaderCallback: (bounds) {
                                return LinearGradient(
                                  colors: [
                                    Colors.white,
                                    Colors.white.withOpacity(0.9),
                                    const Color(0xFFD4AF37),
                                  ],
                                  begin: Alignment.topLeft,
                                  end: Alignment.bottomRight,
                                  stops: const [0.0, 0.5, 1.0],
                                ).createShader(bounds);
                              },
                              child: const Text(
                                'إدهام',
                                style: TextStyle(
                                  fontSize: 68,
                                  fontWeight: FontWeight.w900,
                                  color: Colors.white,
                                  letterSpacing: 3,
                                  height: 1.0,
                                ),
                              ),
                            ),

                            const SizedBox(height: 18),

                            // Premium badge with scale animation
                            ScaleTransition(
                              scale: Tween<double>(begin: 0.0, end: 1.0)
                                  .animate(
                                CurvedAnimation(
                                  parent: _textController,
                                  curve: const Interval(
                                    0.6,
                                    1.0,
                                    curve: Curves.elasticOut,
                                  ),
                                ),
                              ),
                              child: Container(
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 28,
                                  vertical: 10,
                                ),
                                decoration: BoxDecoration(
                                  gradient: const LinearGradient(
                                    colors: [
                                      Color(0xFFD4AF37),
                                      Color(0xFFF4D03F),
                                      Color(0xFFE5B000),
                                    ],
                                    begin: Alignment.topLeft,
                                    end: Alignment.bottomRight,
                                  ),
                                  borderRadius: BorderRadius.circular(30),
                                  boxShadow: [
                                    BoxShadow(
                                      color: const Color(0xFFD4AF37)
                                          .withOpacity(0.5),
                                      blurRadius: 20,
                                      spreadRadius: 5,
                                    ),
                                  ],
                                ),
                                child: const Text(
                                  'EDHAM',
                                  style: TextStyle(
                                    fontSize: 20,
                                    fontWeight: FontWeight.w900,
                                    color: Color(0xFF0A1428),
                                    letterSpacing: 5,
                                    shadows: [
                                      Shadow(
                                        color: Colors.black12,
                                        blurRadius: 5,
                                        offset: Offset(2, 2),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),

                    const SizedBox(height: 28),

                    // Subtitle with slide animation
                    SlideTransition(
                      position: _subtitleSlide,
                      child: FadeTransition(
                        opacity: _subtitleOpacity,
                        child: Column(
                          children: [
                            const Text(
                              'for Refrigerated Transportation',
                              style: TextStyle(
                                fontSize: 17,
                                color: Colors.white70,
                                letterSpacing: 2,
                                fontStyle: FontStyle.italic,
                                fontWeight: FontWeight.w300,
                              ),
                            ),
                            const SizedBox(height: 10),
                            Container(
                              padding: const EdgeInsets.symmetric(
                                horizontal: 20,
                                vertical: 6,
                              ),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: const Color(0xFFD4AF37)
                                      .withOpacity(0.6),
                                  width: 1.5,
                                ),
                                borderRadius: BorderRadius.circular(20),
                                color:
                                    const Color(0xFFD4AF37).withOpacity(0.05),
                              ),
                              child: const Text(
                                'للنقل المبرد',
                                style: TextStyle(
                                  fontSize: 16,
                                  color: Color(0xFFD4AF37),
                                  letterSpacing: 1.5,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),

                    const SizedBox(height: 80),

                    // Premium loading indicator
                    FadeTransition(
                      opacity: _loadingOpacity,
                      child: Column(
                        children: [
                          SizedBox(
                            width: 60,
                            height: 60,
                            child: AnimatedBuilder(
                              animation: _particleController,
                              builder: (context, child) {
                                return Stack(
                                  alignment: Alignment.center,
                                  children: [
                                    // Outer rotating ring
                                    Transform.rotate(
                                      angle: _particleController.value *
                                          2 *
                                          math.pi,
                                      child: Container(
                                        width: 60,
                                        height: 60,
                                        decoration: BoxDecoration(
                                          shape: BoxShape.circle,
                                          border: Border.all(
                                            color: const Color(0xFFD4AF37)
                                                .withOpacity(0.3),
                                            width: 2.5,
                                          ),
                                        ),
                                      ),
                                    ),
                                    // Middle rotating ring (reverse)
                                    Transform.rotate(
                                      angle: -_particleController.value *
                                          1.5 *
                                          math.pi,
                                      child: Container(
                                        width: 42,
                                        height: 42,
                                        decoration: BoxDecoration(
                                          shape: BoxShape.circle,
                                          border: Border.all(
                                            color: const Color(0xFFD4AF37),
                                            width: 2,
                                          ),
                                        ),
                                      ),
                                    ),
                                    // Inner pulsing ring
                                    Transform.scale(
                                      scale: 0.8 +
                                          (0.2 *
                                              math.sin(
                                                  _particleController.value *
                                                      2 *
                                                      math.pi)),
                                      child: Container(
                                        width: 28,
                                        height: 28,
                                        decoration: BoxDecoration(
                                          shape: BoxShape.circle,
                                          color: const Color(0xFFD4AF37),
                                          boxShadow: [
                                            BoxShadow(
                                              color: const Color(0xFFD4AF37)
                                                  .withOpacity(0.8),
                                              blurRadius: 15,
                                              spreadRadius: 3,
                                            ),
                                          ],
                                        ),
                                      ),
                                    ),
                                  ],
                                );
                              },
                            ),
                          ),
                          const SizedBox(height: 24),
                          Text(
                            'جاري التحميل...',
                            style: TextStyle(
                              fontSize: 15,
                              color: Colors.white.withOpacity(0.85),
                              letterSpacing: 1.2,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),

            // Top accent bar
            Positioned(
              top: 0,
              left: 0,
              right: 0,
              child: Container(
                height: 4,
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [
                      const Color(0xFFD4AF37),
                      Colors.blue.shade300,
                      const Color(0xFF0099D8),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}


// Luxury particle painter
class LuxuryParticlePainter extends CustomPainter {
  final double progress;

  LuxuryParticlePainter({required this.progress});

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..strokeWidth = 1.5
      ..strokeCap = StrokeCap.round
      ..style = PaintingStyle.stroke;

    for (int i = 0; i < 30; i++) {
      final x = (i * 120.0 + progress * size.width) % size.width;
      final y = (i * 80.0 + progress * size.height * 0.5) % size.height;

      final opacity = (0.3 * (1 - (progress % 0.3) / 0.3)).clamp(0.0, 0.3);
      paint.color = Colors.white.withOpacity(opacity);

      final radius = 1.0 + (i % 3) * 0.8;
      canvas.drawCircle(Offset(x, y), radius, paint);

      // Connecting lines between particles
      if (i % 3 == 0) {
        final nextX =
            ((i + 1) * 120.0 + progress * size.width) % size.width;
        final nextY =
            ((i + 1) * 80.0 + progress * size.height * 0.5) % size.height;

        paint.style = PaintingStyle.stroke;
        canvas.drawLine(
          Offset(x, y),
          Offset(nextX, nextY),
          paint,
        );
        paint.style = PaintingStyle.stroke;
      }
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}

// Animated circles painter
class AnimatedCirclesPainter extends CustomPainter {
  final double progress;

  AnimatedCirclesPainter({required this.progress});

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.5;

    for (int i = 0; i < 4; i++) {
      final radius = 60.0 +
          (i * 40) +
          (math.sin(progress * math.pi * 2 + i) * 30);
      final opacity =
          (0.15 * (1 - (progress + i * 0.25) % 1)).clamp(0.0, 0.15);

      paint.color = const Color(0xFFD4AF37).withOpacity(opacity);

      canvas.drawCircle(
        Offset(size.width / 2, size.height / 2),
        radius,
        paint,
      );
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}
