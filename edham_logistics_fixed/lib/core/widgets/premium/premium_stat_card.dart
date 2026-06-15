// ============================================
// 📊 Premium Stat Card - Animated Glassmorphism Stats
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../theme/app_theme.dart';
import '../glass_container.dart';

class PremiumStatCard extends StatefulWidget {
  final String title;
  final String value;
  final String? change;
  final IconData icon;
  final Color color;
  final bool isGlowing;
  final Duration? animationDelay;

  const PremiumStatCard({
    super.key,
    required this.title,
    required this.value,
    this.change,
    required this.icon,
    required this.color,
    this.isGlowing = true,
    this.animationDelay,
  });

  @override
  State<PremiumStatCard> createState() => _PremiumStatCardState();
}

class _PremiumStatCardState extends State<PremiumStatCard>
    with SingleTickerProviderStateMixin {
  late AnimationController _pulseController;
  late Animation<double> _pulseAnimation;

  @override
  void initState() {
    super.initState();
    if (widget.isGlowing) {
      _pulseController = AnimationController(
        duration: const Duration(seconds: 2),
        vsync: this,
      );
      _pulseAnimation = Tween<double>(
        begin: 1.0,
        end: 1.1,
      ).animate(CurvedAnimation(
        parent: _pulseController,
        curve: Curves.easeInOut,
      ));
      _pulseController.repeat(reverse: true);
    }
  }

  @override
  void dispose() {
    if (widget.isGlowing) {
      _pulseController.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GlassCard(
      onTap: () {},
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Icon with glow effect
          if (widget.isGlowing)
            AnimatedBuilder(
              animation: _pulseAnimation,
              builder: (context, child) {
                return Transform.scale(
                  scale: _pulseAnimation.value,
                  child: Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: widget.color.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(16),
                      border: Border.all(
                        color: widget.color.withOpacity(0.3),
                        width: 1,
                      ),
                      boxShadow: [
                        BoxShadow(
                          color: widget.color.withOpacity(0.4 * _pulseAnimation.value),
                          blurRadius: 20 * _pulseAnimation.value,
                          spreadRadius: 0,
                        ),
                      ],
                    ),
                    child: Icon(
                      widget.icon,
                      color: widget.color,
                      size: 24,
                    ),
                  ),
                );
              },
            )
          else
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: widget.color.withOpacity(0.1),
                borderRadius: BorderRadius.circular(16),
                border: Border.all(
                  color: widget.color.withOpacity(0.3),
                  width: 1,
                ),
              ),
              child: Icon(
                widget.icon,
                color: widget.color,
                size: 24,
              ),
            ),

          const SizedBox(height: 16),

          // Value
          Text(
            widget.value,
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontSize: 28,
              fontWeight: FontWeight.bold,
            ),
          ).animate()
              .fadeIn(delay: widget.animationDelay ?? const Duration(milliseconds: 500))
              .slideY(begin: 0.3, end: 0),

          const SizedBox(height: 4),

          // Title
          Text(
            widget.title,
            style: const TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 14,
              fontWeight: FontWeight.w500,
            ),
          ).animate()
              .fadeIn(delay: (widget.animationDelay ?? const Duration(milliseconds: 500)) + const Duration(milliseconds: 200))
              .slideY(begin: 0.3, end: 0),

          // Change indicator
          if (widget.change != null) ...[
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: widget.change!.startsWith('+') 
                    ? AppTheme.success.withOpacity(0.1)
                    : AppTheme.error.withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(
                  color: widget.change!.startsWith('+')
                      ? AppTheme.success.withOpacity(0.3)
                      : AppTheme.error.withOpacity(0.3),
                  width: 1,
                ),
              ),
              child: Text(
                widget.change!,
                style: TextStyle(
                  color: widget.change!.startsWith('+')
                      ? AppTheme.success
                      : AppTheme.error,
                  fontSize: 12,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ).animate()
                .fadeIn(delay: (widget.animationDelay ?? const Duration(milliseconds: 500)) + const Duration(milliseconds: 400))
                .slideY(begin: 0.3, end: 0),
          ],
        ],
      ),
    ).animate()
        .fadeIn(delay: widget.animationDelay ?? Duration.zero)
        .slideY(begin: 0.2, end: 0);
  }
}

// Compact version for grid layouts
class CompactPremiumStatCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  final Color color;

  const CompactPremiumStatCard({
    super.key,
    required this.title,
    required this.value,
    required this.icon,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return GlassContainer(
      radius: 16,
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: color.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(
                  icon,
                  color: color,
                  size: 20,
                ),
              ),
              const Spacer(),
              if (value.isNotEmpty)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                  decoration: BoxDecoration(
                    color: color.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    value,
                    style: TextStyle(
                      color: color,
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            title,
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }
}
