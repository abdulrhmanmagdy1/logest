// ============================================
// 🧭 Floating Navigation Bar - Premium Glassmorphism Nav
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:salomon_bottom_bar/salomon_bottom_bar.dart';
import '../../theme/app_theme.dart';
import '../glass_container.dart';

class FloatingNavBar extends StatefulWidget {
  final int currentIndex;
  final Function(int) onTap;
  final List<NavItem> items;

  const FloatingNavBar({
    super.key,
    required this.currentIndex,
    required this.onTap,
    required this.items,
  });

  @override
  State<FloatingNavBar> createState() => _FloatingNavBarState();
}

class _FloatingNavBarState extends State<FloatingNavBar>
    with SingleTickerProviderStateMixin {
  late AnimationController _floatController;
  late Animation<double> _floatAnimation;

  @override
  void initState() {
    super.initState();
    _floatController = AnimationController(
      duration: const Duration(seconds: 3),
      vsync: this,
    );
    _floatAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _floatController,
      curve: Curves.easeInOut,
    ));
    _floatController.repeat(reverse: true);
  }

  @override
  void dispose() {
    _floatController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _floatAnimation,
      builder: (context, child) {
        return Transform.translate(
          offset: Offset(0, -5 * _floatAnimation.value),
          child: Container(
            margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 20),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(30),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.4),
                  blurRadius: 30,
                  spreadRadius: 2,
                  offset: const Offset(0, 15),
                ),
              ],
            ),
            child: GlassContainer(
              radius: 30,
              blurEnabled: true,
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
              child: SalomonBottomBar(
                currentIndex: widget.currentIndex,
                onTap: widget.onTap,
                items: widget.items.map((item) => SalomonBottomBarItem(
                  icon: Icon(item.icon, size: 24),
                  activeIcon: Container(
                    padding: const EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      gradient: AppTheme.primaryGradient,
                      borderRadius: BorderRadius.circular(20),
                      boxShadow: [
                        BoxShadow(
                          color: AppTheme.primary.withOpacity(0.4),
                          blurRadius: 15,
                          spreadRadius: 0,
                        ),
                      ],
                    ),
                    child: Icon(item.icon, size: 20, color: Colors.white),
                  ),
                  title: Text(
                    item.label,
                    style: const TextStyle(
                      fontWeight: FontWeight.w600,
                      fontSize: 12,
                    ),
                  ),
                  selectedColor: AppTheme.primary,
                  unselectedColor: AppTheme.textSecondary,
                )).toList(),
                margin: const EdgeInsets.all(0),
                padding: const EdgeInsets.all(0),
                duration: const Duration(milliseconds: 300),
                curve: Curves.easeOutCubic,
              ),
            ),
          ),
        );
      },
    ).animate().fadeIn().slideY(begin: 0.3, end: 0);
  }
}

class NavItem {
  final IconData icon;
  final String label;

  const NavItem({
    required this.icon,
    required this.label,
  });
}

// Premium Bottom Navigation with Glass Effect
class PremiumBottomNav extends StatelessWidget {
  final int currentIndex;
  final Function(int) onTap;
  final List<NavItem> items;

  const PremiumBottomNav({
    super.key,
    required this.currentIndex,
    required this.onTap,
    required this.items,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(30),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.3),
            blurRadius: 25,
            spreadRadius: 0,
            offset: const Offset(0, 10),
          ),
        ],
      ),
      child: GlassContainer(
        radius: 30,
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: items.asMap().entries.map((entry) {
            final index = entry.key;
            final item = entry.value;
            final isActive = index == currentIndex;

            return GestureDetector(
              onTap: () => onTap(index),
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 300),
                padding: EdgeInsets.symmetric(
                  horizontal: isActive ? 16 : 12,
                  vertical: isActive ? 12 : 8,
                ),
                decoration: BoxDecoration(
                  gradient: isActive ? AppTheme.primaryGradient : null,
                  color: isActive ? null : Colors.transparent,
                  borderRadius: BorderRadius.circular(20),
                  boxShadow: isActive ? [
                    BoxShadow(
                      color: AppTheme.primary.withOpacity(0.4),
                      blurRadius: 15,
                      spreadRadius: 0,
                    ),
                  ] : null,
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(
                      item.icon,
                      color: isActive ? Colors.white : AppTheme.textSecondary,
                      size: 20,
                    ),
                    if (isActive) ...[
                      const SizedBox(width: 8),
                      Text(
                        item.label,
                        style: const TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.w600,
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ],
                ),
              ),
            );
          }).toList(),
        ),
      ),
    );
  }
}

// Minimal Navigation with Glow Effect
class MinimalGlowNav extends StatelessWidget {
  final int currentIndex;
  final Function(int) onTap;
  final List<NavItem> items;

  const MinimalGlowNav({
    super.key,
    required this.currentIndex,
    required this.onTap,
    required this.items,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(20),
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: AppTheme.surface.withOpacity(0.8),
        borderRadius: BorderRadius.circular(30),
        border: Border.all(
          color: AppTheme.primary.withOpacity(0.2),
          width: 1,
        ),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: items.asMap().entries.map((entry) {
          final index = entry.key;
          final item = entry.value;
          final isActive = index == currentIndex;

          return GestureDetector(
            onTap: () => onTap(index),
            child: AnimatedContainer(
              duration: const Duration(milliseconds: 300),
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: isActive ? AppTheme.primary.withOpacity(0.1) : Colors.transparent,
                borderRadius: BorderRadius.circular(20),
                border: isActive ? Border.all(
                  color: AppTheme.primary.withOpacity(0.3),
                  width: 1,
                ) : null,
                boxShadow: isActive ? [
                  BoxShadow(
                    color: AppTheme.primary.withOpacity(0.3),
                    blurRadius: 10,
                    spreadRadius: 0,
                  ),
                ] : null,
              ),
              child: Icon(
                item.icon,
                color: isActive ? AppTheme.primary : AppTheme.textSecondary,
                size: 22,
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}
