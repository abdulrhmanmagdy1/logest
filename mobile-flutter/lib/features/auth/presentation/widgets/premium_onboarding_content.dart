import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:lottie/lottie.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/widgets/premium/glass_container.dart';
import '../../../core/widgets/premium/smooth_animation_system.dart';

class PremiumOnboardingContent extends StatelessWidget {
  final OnboardingData data;
  final VoidCallback onNext;
  final VoidCallback onSkip;

  const PremiumOnboardingContent({
    super.key,
    required this.data,
    required this.onNext,
    required this.onSkip,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        gradient: data.backgroundColor,
      ),
      child: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            children: [
              const Spacer(flex: 2),
              
              // Main image/illustration
              _buildMainImage(),
              
              const SizedBox(height: 40),
              
              // Title
              _buildTitle(),
              
              const SizedBox(height: 20),
              
              // Description
              _buildDescription(),
              
              const Spacer(flex: 3),
              
              // Next button
              _buildNextButton(),
              
              const SizedBox(height: 20),
              
              // Skip button
              _buildSkipButton(),
              
              const SizedBox(height: 40),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildMainImage() {
    return Container(
      height: 280,
      width: double.infinity,
      child: Stack(
        children: [
          // Background glow effect
          Positioned.fill(
            child: Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(20),
                gradient: RadialGradient(
                  colors: [
                    AppTheme.primary.withOpacity(0.1),
                    Colors.transparent,
                  ],
                  center: Alignment.center,
                  radius: 1.2,
                ),
              ),
            ),
          ),
          
          // Main image with glassmorphism frame
          Center(
            child: GlassContainer(
              padding: const EdgeInsets.all(20),
              borderRadius: BorderRadius.circular(20),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(16),
                child: Image.asset(
                  data.imagePath,
                  height: 240,
                  width: double.infinity,
                  fit: BoxFit.contain,
                  errorBuilder: (context, error, stackTrace) {
                    return _buildFallbackIllustration();
                  },
                ),
              ),
            ),
          ),
        ],
      ),
    ).animate()
      .fadeIn(duration: const Duration(milliseconds: 800))
      .slideY(begin: 0.3, end: 0, duration: const Duration(milliseconds: 800))
      .shimmer(duration: const Duration(milliseconds: 1500));
  }

  Widget _buildFallbackIllustration() {
    return Container(
      height: 240,
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            AppTheme.primary.withOpacity(0.1),
            AppTheme.accent.withOpacity(0.05),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Center(
        child: Icon(
          _getIconForPage(),
          size: 80,
          color: AppTheme.primary.withOpacity(0.6),
        ),
      ),
    );
  }

  IconData _getIconForPage() {
    final title = data.title.toLowerCase();
    if (title.contains('smart')) return Icons.local_shipping;
    if (title.contains('tracking')) return Icons.gps_fixed;
    if (title.contains('cold')) return Icons.ac_unit;
    if (title.contains('delivery')) return Icons.flash_on;
    if (title.contains('enterprise')) return Icons.dashboard;
    return Icons.business;
  }

  Widget _buildTitle() {
    return Text(
      data.title,
      textAlign: TextAlign.center,
      style: GoogleFonts.inter(
        fontSize: 32,
        fontWeight: FontWeight.w800,
        color: AppTheme.textPrimary,
        height: 1.2,
        letterSpacing: -0.5,
      ),
    ).animate()
      .fadeIn(delay: const Duration(milliseconds: 600), duration: const Duration(milliseconds: 800))
      .slideY(begin: 0.2, end: 0, delay: const Duration(milliseconds: 600));
  }

  Widget _buildDescription() {
    return Text(
      data.description,
      textAlign: TextAlign.center,
      style: GoogleFonts.inter(
        fontSize: 16,
        fontWeight: FontWeight.w400,
        color: AppTheme.textSecondary,
        height: 1.6,
        letterSpacing: 0.2,
      ),
    ).animate()
      .fadeIn(delay: const Duration(milliseconds: 800), duration: const Duration(milliseconds: 800))
      .slideY(begin: 0.2, end: 0, delay: const Duration(milliseconds: 800));
  }

  Widget _buildNextButton() {
    return MicroInteractionButton(
      onPressed: onNext,
      child: GlassContainer(
        padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 16),
        borderRadius: BorderRadius.circular(30),
        background: AppTheme.primary.withOpacity(0.2),
        border: Border.all(
          color: AppTheme.primary.withOpacity(0.3),
          width: 1,
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'Next',
              style: GoogleFonts.inter(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: AppTheme.textPrimary,
              ),
            ),
            const SizedBox(width: 8),
            Icon(
              Icons.arrow_forward,
              size: 20,
              color: AppTheme.textPrimary,
            ),
          ],
        ),
      ),
    ).animate()
      .fadeIn(delay: const Duration(milliseconds: 1000), duration: const Duration(milliseconds: 800))
      .slideY(begin: 0.2, end: 0, delay: const Duration(milliseconds: 1000));
  }

  Widget _buildSkipButton() {
    return MicroInteractionButton(
      onPressed: onSkip,
      child: Text(
        'Skip',
        style: GoogleFonts.inter(
          fontSize: 14,
          fontWeight: FontWeight.w500,
          color: AppTheme.textHint,
          letterSpacing: 0.5,
        ),
      ),
    ).animate()
      .fadeIn(delay: const Duration(milliseconds: 1200), duration: const Duration(milliseconds: 600));
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
