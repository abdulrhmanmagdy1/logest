import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/navigation/app_router.dart';

class TrainingScreen extends StatefulWidget {
  const TrainingScreen({super.key});

  @override
  State<TrainingScreen> createState() => _TrainingScreenState();
}

class _TrainingScreenState extends State<TrainingScreen>
    with TickerProviderStateMixin {
  late AnimationController _logoController;
  late AnimationController _contentController;
  late AnimationController _buttonController;

  @override
  void initState() {
    super.initState();
    
    _logoController = AnimationController(
      duration: const Duration(milliseconds: 1500),
      vsync: this,
    );
    
    _contentController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _buttonController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );

    // Start animations
    _startAnimations();
  }

  void _startAnimations() async {
    await Future.delayed(const Duration(milliseconds: 300));
    _logoController.forward();
    
    await Future.delayed(const Duration(milliseconds: 500));
    _contentController.forward();
    
    await Future.delayed(const Duration(milliseconds: 300));
    _buttonController.forward();
  }

  @override
  void dispose() {
    _logoController.dispose();
    _contentController.dispose();
    _buttonController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Column(
            children: [
              const SizedBox(height: 40),
              
              // Logo Section
              _buildLogoSection(),
              
              const SizedBox(height: 40),
              
              // Training Content
              _buildTrainingContent(),
              
              const SizedBox(height: 40),
              
              // Action Buttons
              _buildActionButtons(),
              
              const SizedBox(height: 40),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildLogoSection() {
    return AnimatedBuilder(
      animation: _logoController,
      builder: (context, child) {
        return Transform.scale(
          scale: 0.8 + (0.2 * _logoController.value),
          child: Container(
            width: 200,
            height: 200,
            decoration: BoxDecoration(
              gradient: RadialGradient(
                colors: [
                  AppTheme.primary.withOpacity(0.1),
                  AppTheme.primary.withOpacity(0.05),
                  Colors.transparent,
                ],
              ),
              borderRadius: BorderRadius.circular(100),
            ),
            child: Center(
              child: Container(
                width: 120,
                height: 120,
                decoration: BoxDecoration(
                  color: AppTheme.primary,
                  borderRadius: BorderRadius.circular(60),
                  boxShadow: [
                    BoxShadow(
                      color: AppTheme.primary.withOpacity(0.3),
                      blurRadius: 20,
                      spreadRadius: 5,
                    ),
                  ],
                ),
                child: Center(
                  child: Icon(
                    Icons.local_shipping,
                    size: 60,
                    color: Colors.white,
                  ),
                ),
              ),
            ),
          ),
        );
      },
    ).animate(controller: _logoController)
     .scale(duration: 800.ms, curve: Curves.elasticOut)
     .fadeIn(duration: 500.ms);
  }

  Widget _buildTrainingContent() {
    return Column(
      children: [
        // Title
        Text(
          'نظام إدهام اللوجستي',
          style: GoogleFonts.inter(
            fontSize: 32,
            fontWeight: FontWeight.bold,
            color: AppTheme.textPrimary,
          ),
          textAlign: TextAlign.center,
        ).animate(controller: _contentController)
         .fadeIn(duration: 600.ms, delay: 200.ms)
         .slideY(begin: 30, end: 0, duration: 600.ms, delay: 200.ms),
        
        const SizedBox(height: 16),
        
        // Subtitle
        Text(
          'منصة شحن احترافية متكاملة',
          style: GoogleFonts.inter(
            fontSize: 18,
            color: AppTheme.textSecondary,
          ),
          textAlign: TextAlign.center,
        ).animate(controller: _contentController)
         .fadeIn(duration: 600.ms, delay: 400.ms)
         .slideY(begin: 20, end: 0, duration: 600.ms, delay: 400.ms),
        
        const SizedBox(height: 40),
        
        // Features Grid
        _buildFeaturesGrid(),
      ],
    );
  }

  Widget _buildFeaturesGrid() {
    final features = [
      {
        'icon': Icons.speed,
        'title': 'تتبع مباشر',
        'description': 'تابع شحنتك لحظة بلحظة',
      },
      {
        'icon': Icons.local_shipping,
        'title': 'أسطول حديث',
        'description': 'مركبات مجهزة بأحدث التقنيات',
      },
      {
        'icon': Icons.thermostat,
        'title': 'تحكم حراري',
        'description': 'حفظ البضائع بدرجة حرارة مثالية',
      },
      {
        'icon': Icons.security,
        'title': 'أمان عالي',
        'description': 'تأمين شحناتك بالكامل',
      },
    ];

    return GridView.builder(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 2,
        crossAxisSpacing: 16,
        mainAxisSpacing: 16,
        childAspectRatio: 1.2,
      ),
      itemCount: features.length,
      itemBuilder: (context, index) {
        final feature = features[index];
        return _buildFeatureCard(
          feature['icon'] as IconData,
          feature['title'] as String,
          feature['description'] as String,
          index,
        );
      },
    );
  }

  Widget _buildFeatureCard(IconData icon, String title, String description, int index) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.surface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: AppTheme.surfaceLight,
          width: 1,
        ),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Container(
            width: 50,
            height: 50,
            decoration: BoxDecoration(
              color: AppTheme.primary.withOpacity(0.1),
              borderRadius: BorderRadius.circular(25),
            ),
            child: Icon(
              icon,
              color: AppTheme.primary,
              size: 24,
            ),
          ),
          const SizedBox(height: 12),
          Text(
            title,
            style: GoogleFonts.inter(
              fontSize: 16,
              fontWeight: FontWeight.w600,
              color: AppTheme.textPrimary,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 8),
          Text(
            description,
            style: GoogleFonts.inter(
              fontSize: 12,
              color: AppTheme.textSecondary,
            ),
            textAlign: TextAlign.center,
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    ).animate(controller: _contentController)
     .fadeIn(duration: 500.ms, delay: (600 + (index * 100)).ms)
     .scale(duration: 500.ms, delay: (600 + (index * 100)).ms);
  }

  Widget _buildActionButtons() {
    return Column(
      children: [
        // Start Training Button
        SizedBox(
          width: double.infinity,
          height: 56,
          child: ElevatedButton(
            onPressed: () {
              AppRouter.goLogin(context);
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: AppTheme.primary,
              foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
              elevation: 0,
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  'بدء التدريب',
                  style: GoogleFonts.inter(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(width: 8),
                const Icon(Icons.arrow_forward, size: 20),
              ],
            ),
          ),
        ).animate(controller: _buttonController)
         .fadeIn(duration: 600.ms, delay: 200.ms)
         .slideY(begin: 20, end: 0, duration: 600.ms, delay: 200.ms),
        
        const SizedBox(height: 16),
        
        // Skip Button
        SizedBox(
          width: double.infinity,
          height: 56,
          child: OutlinedButton(
            onPressed: () {
              AppRouter.goLogin(context);
            },
            style: OutlinedButton.styleFrom(
              foregroundColor: AppTheme.textSecondary,
              side: BorderSide(color: AppTheme.surfaceLight),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
            ),
            child: Text(
              'تخطي',
              style: GoogleFonts.inter(
                fontSize: 16,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        ).animate(controller: _buttonController)
         .fadeIn(duration: 600.ms, delay: 400.ms)
         .slideY(begin: 20, end: 0, duration: 600.ms, delay: 400.ms),
      ],
    );
  }
}
