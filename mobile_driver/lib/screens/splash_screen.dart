import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:math' as math;
import '../constants/index.dart';
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

    _logoController = AnimationController(
      duration: const Duration(milliseconds: 2200),
      vsync: this,
    );

    _particleController = AnimationController(
      duration: const Duration(seconds: 8),
      vsync: this,
    )..repeat();

    _textController = AnimationController(
      duration: const Duration(milliseconds: 2800),
      vsync: this,
    );

    _glowController = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    )..repeat(reverse: true);

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

    _titleSlide = Tween<Offset>(begin: const Offset(0, 0.5), end: Offset.zero).animate(
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

    _subtitleSlide = Tween<Offset>(begin: const Offset(0, 0.4), end: Offset.zero).animate(
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

    _loadingOpacity = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _textController,
        curve: const Interval(0.7, 1.0, curve: Curves.easeIn),
      ),
    );

    _logoController.forward();
    _textController.forward();

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
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              AppColors.background,
              AppColors.surface,
              AppColors.primary,
              AppColors.secondary,
              AppColors.background,
            ],
            stops: const [0.0, 0.25, 0.5, 0.75, 1.0],
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

            Center(
              child: SingleChildScrollView(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    // Logo
                    AnimatedBuilder(
                      animation: Listenable.merge([_logoController, _glowController]),
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
                                  BoxShadow(
                                    color: AppColors.secondary
                                        .withOpacity(0.4 * _logoGlow.value),
                                    blurRadius: 30 + (20 * _glowController.value),
                                    spreadRadius: 10 + (10 * _glowController.value),
                                  ),
                                  BoxShadow(
                                    color: AppColors.primary
                                        .withOpacity(0.3 * _logoGlow.value),
                                    blurRadius: 50 + (30 * _glowController.value),
                                    spreadRadius: 15 + (15 * _glowController.value),
                                  ),
                                  BoxShadow(
                                    color: Colors.black.withOpacity(0.1),
                                    blurRadius: 20,
                                    inset: true,
                                  ),
                                ],
                              ),
                              child: Stack(
                                alignment: Alignment.center,
                                children: [
                                  Transform.rotate(
                                    angle: _particleController.value * 2 * math.pi,
                                    child: Container(
                                      decoration: BoxDecoration(
                                        shape: BoxShape.circle,
                                        border: Border.all(
                                          color: AppColors.secondary
                                              .withOpacity(0.15),
                                          width: 1,
                                        ),
                                      ),
                                    ),
                                  ),
                                  CustomPaint(
                                    painter: PremiumSnowflakePainter(
                                      rotation: _particleController.value * math.pi,
                                    ),
                                    size: const Size(100, 100),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        );
                      },
                    ),

                    const SizedBox(height: 50),

                    SlideTransition(
                      position: _titleSlide,
                      child: FadeTransition(
                        opacity: _titleOpacity,
                        child: Column(
                              child: const Text(
                                'EDHAM DRIVER',
                                style: TextStyle(
                                  fontSize: 16,
                                  fontWeight: FontWeight.w900,
                                  color: AppColors.textInverse,
                                  letterSpacing: 4,
                                  shadows: [
                                    Shadow(
                                      color: Colors.black12,
                                      blurRadius: 5,
                                      offset: Offset(2, 2),
                                    ),
                                  ],
                                ),
                              ),
                                      child: Container(
                                        width: 42,
                                        height: 42,
                                        decoration: BoxDecoration(
                                          shape: BoxShape.circle,
                                          border: Border.all(
                                            color: AppColors.secondary,
                                            width: 2,
                                          ),
                                        ),
                                      ),
                                    ),
                                    Transform.scale(
                                      scale: 0.8 + (0.2 * math.sin(_particleController.value * 2 * math.pi)),
                                      child: Container(
                                        width: 28,
                                        height: 28,
                                        decoration: BoxDecoration(
                                          shape: BoxShape.circle,
                                          color: AppColors.secondary,
                                          boxShadow: [
                                            BoxShadow(
                                              color: AppColors.secondary
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

            Positioned(
              top: 0,
              left: 0,
              right: 0,
              child: Container(
                height: 4,
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    colors: [
                      AppColors.secondary,
                      AppColors.primary,
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

class PremiumSnowflakePainter extends CustomPainter {
  final double rotation;

  PremiumSnowflakePainter({required this.rotation});

  @override
  void paint(Canvas canvas, Size size) {
    canvas.translate(size.width / 2, size.height / 2);
    canvas.rotate(rotation);

    final paint = Paint()
      ..color = AppColors.primary
      ..strokeWidth = 2.2
      ..strokeCap = StrokeCap.round
      ..strokeJoin = StrokeJoin.round
      ..style = PaintingStyle.stroke;

    const armLength = 28.0;
    const branchLength = 12.0;

    for (int i = 0; i < 6; i++) {
      final angle = (i * 60 * math.pi) / 180;
      final endX = armLength * math.cos(angle);
      final endY = armLength * math.sin(angle);

      canvas.drawLine(const Offset(0, 0), Offset(endX, endY), paint);

      for (int j = 1; j <= 2; j++) {
        final branchStartX = (armLength * 0.35 * j) * math.cos(angle);
        final branchStartY = (armLength * 0.35 * j) * math.sin(angle);

        final branchAngle1 = angle + (30 * math.pi) / 180;
        final branchAngle2 = angle - (30 * math.pi) / 180;

        final actualBranchLength = branchLength * (1 - j * 0.15);

        canvas.drawLine(
          Offset(branchStartX, branchStartY),
          Offset(
            branchStartX + actualBranchLength * math.cos(branchAngle1),
            branchStartY + actualBranchLength * math.sin(branchAngle1),
          ),
          paint,
        );

        canvas.drawLine(
          Offset(branchStartX, branchStartY),
          Offset(
            branchStartX + actualBranchLength * math.cos(branchAngle2),
            branchStartY + actualBranchLength * math.sin(branchAngle2),
          ),
          paint,
        );
      }
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}

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

      if (i % 3 == 0) {
        final nextX = ((i + 1) * 120.0 + progress * size.width) % size.width;
        final nextY = ((i + 1) * 80.0 + progress * size.height * 0.5) % size.height;

        paint.style = PaintingStyle.stroke;
        canvas.drawLine(
          Offset(x, y),
          Offset(nextX, nextY),
          paint,
        );
      }
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}

class AnimatedCirclesPainter extends CustomPainter {
  final double progress;

  AnimatedCirclesPainter({required this.progress});

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.5;

    for (int i = 0; i < 4; i++) {
      final radius = 60.0 + (i * 40) + (math.sin(progress * math.pi * 2 + i) * 30);
      final opacity = (0.15 * (1 - (progress + i * 0.25) % 1)).clamp(0.0, 0.15);

      paint.color = AppColors.secondary.withOpacity(opacity);

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
