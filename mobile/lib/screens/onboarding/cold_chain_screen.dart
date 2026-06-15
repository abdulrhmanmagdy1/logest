import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class ColdChainScreen extends StatefulWidget {
  const ColdChainScreen({super.key});

  @override
  State<ColdChainScreen> createState() => _ColdChainScreenState();
}

class _ColdChainScreenState extends State<ColdChainScreen>
    with TickerProviderStateMixin {
  late AnimationController _imageController;
  late AnimationController _textController;
  late AnimationController _buttonController;
  late Animation<double> _imageOpacity;
  late Animation<Offset> _imageSlide;
  late Animation<double> _textOpacity;
  late Animation<Offset> _textSlide;
  late Animation<double> _buttonOpacity;
  late Animation<Offset> _buttonSlide;

  @override
  void initState() {
    super.initState();
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.light,
      ),
    );

    _imageController = AnimationController(
      duration: const Duration(milliseconds: 1200),
      vsync: this,
    );

    _textController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );

    _buttonController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );

    _imageOpacity = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _imageController,
        curve: Curves.easeInOut,
      ),
    );

    _imageSlide = Tween<Offset>(begin: const Offset(0, 0.3), end: Offset.zero).animate(
      CurvedAnimation(
        parent: _imageController,
        curve: Curves.easeOut,
      ),
    );

    _textOpacity = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _textController,
        curve: Curves.easeIn,
      ),
    );

    _textSlide = Tween<Offset>(begin: const Offset(0, 0.5), end: Offset.zero).animate(
      CurvedAnimation(
        parent: _textController,
        curve: Curves.easeOut,
      ),
    );

    _buttonOpacity = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _buttonController,
        curve: Curves.easeIn,
      ),
    );

    _buttonSlide = Tween<Offset>(begin: const Offset(0, 1.0), end: Offset.zero).animate(
      CurvedAnimation(
        parent: _buttonController,
        curve: Curves.easeOut,
      ),
    );

    _imageController.forward();
    Future.delayed(const Duration(milliseconds: 400), () {
      if (mounted) _textController.forward();
    });
    Future.delayed(const Duration(milliseconds: 800), () {
      if (mounted) _buttonController.forward();
    });
  }

  @override
  void dispose() {
    _imageController.dispose();
    _textController.dispose();
    _buttonController.dispose();
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
              Color(0xFF0A1428),
              Color(0xFF1B2E4D),
              Color(0xFF0F3460),
            ],
            stops: [0.0, 0.5, 1.0],
          ),
        ),
        child: Stack(
          children: [
            // Cinematic background overlay
            Positioned.fill(
              child: Container(
                decoration: BoxDecoration(
                  gradient: RadialGradient(
                    center: const Alignment(0.0, -0.3),
                    radius: 1.5,
                    colors: [
                      Colors.black.withOpacity(0.3),
                      Colors.transparent,
                    ],
                  ),
                ),
              ),
            ),

            SafeArea(
              child: Column(
                children: [
                  Expanded(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        // Hero image with cinematic effect
                        SlideTransition(
                          position: _imageSlide,
                          child: FadeTransition(
                            opacity: _imageOpacity,
                            child: Container(
                              height: 300,
                              margin: const EdgeInsets.symmetric(horizontal: 20),
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(20),
                                boxShadow: [
                                  BoxShadow(
                                    color: Colors.black.withOpacity(0.5),
                                    blurRadius: 30,
                                    spreadRadius: 10,
                                    offset: const Offset(0, 15),
                                  ),
                                  BoxShadow(
                                    color: const Color(0xFF007BA7).withOpacity(0.3),
                                    blurRadius: 20,
                                    spreadRadius: 5,
                                  ),
                                ],
                              ),
                              child: ClipRRect(
                                borderRadius: BorderRadius.circular(20),
                                child: Stack(
                                  children: [
                                    // Main image
                                    Positioned.fill(
                                      child: Image.asset(
                                        'assets/images/cold-chain.jpg',
                                        fit: BoxFit.cover,
                                      ),
                                    ),
                                    // Cinematic overlay
                                    Positioned.fill(
                                      child: Container(
                                        decoration: BoxDecoration(
                                          gradient: LinearGradient(
                                            begin: Alignment.topCenter,
                                            end: Alignment.bottomCenter,
                                            colors: [
                                              Colors.transparent,
                                              Colors.black.withOpacity(0.7),
                                            ],
                                            stops: const [0.6, 1.0],
                                          ),
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                          ),
                        ),

                        const SizedBox(height: 40),

                        // Title and description
                        SlideTransition(
                          position: _textSlide,
                          child: FadeTransition(
                            opacity: _textOpacity,
                            child: Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 30),
                              child: Column(
                                children: [
                                  // Title
                                  ShaderMask(
                                    shaderCallback: (bounds) {
                                      return const LinearGradient(
                                        colors: [
                                          Colors.white,
                                          Color(0xFFD4AF37),
                                        ],
                                        begin: Alignment.topLeft,
                                        end: Alignment.bottomRight,
                                      ).createShader(bounds);
                                    },
                                    child: const Text(
                                      'Cold Chain Transportation',
                                      style: TextStyle(
                                        fontSize: 28,
                                        fontWeight: FontWeight.w900,
                                        color: Colors.white,
                                        height: 1.2,
                                      ),
                                      textAlign: TextAlign.center,
                                    ),
                                  ),

                                  const SizedBox(height: 16),

                                  // Description
                                  const Text(
                                    'Advanced temperature-controlled logistics solutions for food, medical, and sensitive products with full monitoring and safety compliance.',
                                    style: TextStyle(
                                      fontSize: 16,
                                      color: Colors.white70,
                                      height: 1.5,
                                      fontWeight: FontWeight.w400,
                                    ),
                                    textAlign: TextAlign.center,
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),

                        const SizedBox(height: 50),

                        // Navigation dots
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            _buildDot(false),
                            _buildDot(false),
                            _buildDot(true),
                          ],
                        ),

                        const SizedBox(height: 40),

                        // Get Started button
                        SlideTransition(
                          position: _buttonSlide,
                          child: FadeTransition(
                            opacity: _buttonOpacity,
                            child: Container(
                              width: 220,
                              height: 55,
                              decoration: BoxDecoration(
                                gradient: const LinearGradient(
                                  colors: [
                                    Color(0xFFD4AF37),
                                    Color(0xFFF4C430),
                                  ],
                                  begin: Alignment.topLeft,
                                  end: Alignment.bottomRight,
                                ),
                                borderRadius: BorderRadius.circular(27.5),
                                boxShadow: [
                                  BoxShadow(
                                    color: const Color(0xFFD4AF37).withOpacity(0.4),
                                    blurRadius: 20,
                                    spreadRadius: 5,
                                  ),
                                ],
                              ),
                              child: MaterialButton(
                                onPressed: () {
                                  Navigator.pushReplacementNamed(context, '/login');
                                },
                                child: const Text(
                                  'ابدأ الآن',
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 18,
                                    fontWeight: FontWeight.w700,
                                    letterSpacing: 1.0,
                                  ),
                                ),
                              ),
                            ),
                          ),
                        ),
                      ],
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

  Widget _buildDot(bool isActive) {
    return AnimatedContainer(
      duration: const Duration(milliseconds: 300),
      margin: const EdgeInsets.symmetric(horizontal: 6),
      height: isActive ? 12 : 8,
      width: isActive ? 12 : 8,
      decoration: BoxDecoration(
        color: isActive ? const Color(0xFFD4AF37) : Colors.white30,
        shape: BoxShape.circle,
        boxShadow: isActive
            ? [
                BoxShadow(
                  color: const Color(0xFFD4AF37).withOpacity(0.5),
                  blurRadius: 8,
                  spreadRadius: 2,
                ),
              ]
            : null,
      ),
    );
  }
}
