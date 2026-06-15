// ============================================
// 🫧 Glass Container - Premium Glassmorphism Widget
// ============================================

import 'dart:ui';
import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

class GlassContainer extends StatelessWidget {
  final Widget child;
  final EdgeInsets? padding;
  final EdgeInsets? margin;
  final double radius;
  final double? width;
  final double? height;
  final Color? backgroundColor;
  final Color? borderColor;
  final double? borderWidth;
  final List<BoxShadow>? boxShadow;
  final bool blurEnabled;
  final double blurSigma;

  const GlassContainer({
    super.key,
    required this.child,
    this.padding,
    this.margin,
    this.radius = 24,
    this.width,
    this.height,
    this.backgroundColor,
    this.borderColor,
    this.borderWidth,
    this.boxShadow,
    this.blurEnabled = true,
    this.blurSigma = 20.0,
  });

  @override
  Widget build(BuildContext context) {
    Widget container = Container(
      width: width,
      height: height,
      margin: margin,
      padding: padding ?? const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: backgroundColor ?? Colors.white.withOpacity(0.05),
        borderRadius: BorderRadius.circular(radius),
        border: Border.all(
          color: borderColor ?? Colors.white.withOpacity(0.08),
          width: borderWidth ?? 1,
        ),
        boxShadow: boxShadow ?? AppTheme.glowShadow,
      ),
      child: child,
    );

    if (blurEnabled) {
      return ClipRRect(
        borderRadius: BorderRadius.circular(radius),
        child: BackdropFilter(
          filter: ImageFilter.blur(
            sigmaX: blurSigma,
            sigmaY: blurSigma,
          ),
          child: container,
        ),
      );
    }

    return container;
  }
}

// Glass Card with hover effect
class GlassCard extends StatefulWidget {
  final Widget child;
  final EdgeInsets? padding;
  final EdgeInsets? margin;
  final double radius;
  final double? width;
  final double? height;
  final VoidCallback? onTap;
  final bool enableHover;

  const GlassCard({
    super.key,
    required this.child,
    this.padding,
    this.margin,
    this.radius = 24,
    this.width,
    this.height,
    this.onTap,
    this.enableHover = true,
  });

  @override
  State<GlassCard> createState() => _GlassCardState();
}

class _GlassCardState extends State<GlassCard>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;
  late Animation<double> _glowAnimation;
  bool _isHovered = false;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(milliseconds: 200),
      vsync: this,
    );
    _scaleAnimation = Tween<double>(
      begin: 1.0,
      end: 1.02,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: Curves.easeOutCubic,
    ));
    _glowAnimation = Tween<double>(
      begin: 1.0,
      end: 1.5,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: Curves.easeOutCubic,
    ));
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _onHoverChange(bool isHovered) {
    if (!widget.enableHover) return;
    
    setState(() {
      _isHovered = isHovered;
    });
    
    if (isHovered) {
      _controller.forward();
    } else {
      _controller.reverse();
    }
  }

  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      onEnter: (_) => _onHoverChange(true),
      onExit: (_) => _onHoverChange(false),
      child: GestureDetector(
        onTap: widget.onTap,
        child: AnimatedBuilder(
          animation: _controller,
          builder: (context, child) {
            return Transform.scale(
              scale: _scaleAnimation.value,
              child: GlassContainer(
                radius: widget.radius,
                padding: widget.padding,
                margin: widget.margin,
                width: widget.width,
                height: widget.height,
                boxShadow: _isHovered
                    ? [
                        BoxShadow(
                          color: AppTheme.primary.withOpacity(0.4 * _glowAnimation.value),
                          blurRadius: 30 * _glowAnimation.value,
                          spreadRadius: 2,
                          offset: const Offset(0, 10),
                        ),
                      ]
                    : AppTheme.glowShadow,
                child: widget.child,
              ),
            );
          },
        ),
      ),
    );
  }
}

// Floating Glass Container
class FloatingGlassContainer extends StatefulWidget {
  final Widget child;
  final EdgeInsets? padding;
  final EdgeInsets? margin;
  final double radius;
  final double? width;
  final double? height;
  final Duration? animationDuration;

  const FloatingGlassContainer({
    super.key,
    required this.child,
    this.padding,
    this.margin,
    this.radius = 24,
    this.width,
    this.height,
    this.animationDuration = const Duration(seconds: 3),
  });

  @override
  State<FloatingGlassContainer> createState() => _FloatingGlassContainerState();
}

class _FloatingGlassContainerState extends State<FloatingGlassContainer>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _floatAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.animationDuration,
      vsync: this,
    );
    _floatAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: Curves.easeInOut,
    ));
    _controller.repeat(reverse: true);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _floatAnimation,
      builder: (context, child) {
        return Transform.translate(
          offset: Offset(0, -10 * _floatAnimation.value),
          child: GlassContainer(
            radius: widget.radius,
            padding: widget.padding,
            margin: widget.margin,
            width: widget.width,
            height: widget.height,
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.4),
                blurRadius: 30,
                spreadRadius: 1,
                offset: Offset(0, 10 + 5 * _floatAnimation.value),
              ),
            ],
            child: widget.child,
          ),
        );
      },
    );
  }
}
