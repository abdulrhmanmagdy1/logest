// ============================================
// 🎨 Premium Card Design System
// Modern Card Components with Glassmorphism & Animations
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../theme/app_theme.dart';
import '../glass_container.dart';

class PremiumCard extends StatelessWidget {
  final Widget child;
  final EdgeInsetsGeometry? padding;
  final EdgeInsetsGeometry? margin;
  final double? width;
  final double? height;
  final double borderRadius;
  final Color? backgroundColor;
  final Color? borderColor;
  final double? borderWidth;
  final List<BoxShadow>? shadows;
  final VoidCallback? onTap;
  final bool isGlass;
  final bool isAnimated;
  final Duration animationDelay;
  final bool isHoverable;
  final Gradient? gradient;

  const PremiumCard({
    super.key,
    required this.child,
    this.padding,
    this.margin,
    this.width,
    this.height,
    this.borderRadius = 16,
    this.backgroundColor,
    this.borderColor,
    this.borderWidth,
    this.shadows,
    this.onTap,
    this.isGlass = true,
    this.isAnimated = true,
    this.animationDelay = Duration.zero,
    this.isHoverable = false,
    this.gradient,
  });

  @override
  Widget build(BuildContext context) {
    Widget card;

    if (isGlass) {
      card = GlassContainer(
        width: width,
        height: height,
        radius: borderRadius,
        backgroundColor: backgroundColor ?? Colors.white.withOpacity(0.05),
        borderColor: borderColor ?? Colors.white.withOpacity(0.1),
        borderWidth: borderWidth ?? 1,
        child: Padding(
          padding: padding ?? const EdgeInsets.all(16),
          child: child,
        ),
      );
    } else {
      card = Container(
        width: width,
        height: height,
        margin: margin,
        padding: padding ?? const EdgeInsets.all(16),
        decoration: BoxDecoration(
          gradient: gradient ?? _getDefaultGradient(),
          borderRadius: BorderRadius.circular(borderRadius),
          border: borderColor != null
              ? Border.all(color: borderColor!, width: borderWidth ?? 1)
              : null,
          boxShadow: shadows ?? _getDefaultShadows(),
        ),
        child: child,
      );
    }

    if (isHoverable && onTap != null) {
      card = MouseRegion(
        cursor: SystemMouseCursors.click,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          transform: Matrix4.identity(),
          child: card,
        ),
      );
    }

    if (onTap != null) {
      card = GestureDetector(
        onTap: onTap,
        child: card,
      );
    }

    if (isAnimated) {
      card = card
          .animate()
          .fadeIn(delay: animationDelay)
          .slideY(begin: 0.1, end: 0, delay: animationDelay)
          .scale(begin: const Offset(0.95, 0.95), end: const Offset(1, 1), delay: animationDelay);
    }

    return card;
  }

  Gradient _getDefaultGradient() {
    return LinearGradient(
      colors: [
        AppTheme.surface.withOpacity(0.8),
        AppTheme.surface.withOpacity(0.6),
      ],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    );
  }

  List<BoxShadow> _getDefaultShadows() {
    return [
      BoxShadow(
        color: Colors.black.withOpacity(0.1),
        blurRadius: 10,
        offset: const Offset(0, 4),
      ),
      BoxShadow(
        color: Colors.black.withOpacity(0.05),
        blurRadius: 20,
        offset: const Offset(0, 8),
      ),
    ];
  }
}

class PremiumStatCard extends StatefulWidget {
  final String title;
  final String value;
  final String? change;
  final IconData icon;
  final Color color;
  final Duration animationDelay;
  final VoidCallback? onTap;
  final bool showProgress;
  final double? progressValue;
  final bool isGlass;

  const PremiumStatCard({
    super.key,
    required this.title,
    required this.value,
    this.change,
    required this.icon,
    required this.color,
    this.animationDelay = Duration.zero,
    this.onTap,
    this.showProgress = false,
    this.progressValue,
    this.isGlass = true,
  });

  @override
  State<PremiumStatCard> createState() => _PremiumStatCardState();
}

class _PremiumStatCardState extends State<PremiumStatCard>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _progressAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );

    if (widget.showProgress && widget.progressValue != null) {
      _progressAnimation = Tween<double>(begin: 0.0, end: widget.progressValue!)
          .animate(CurvedAnimation(parent: _controller, curve: Curves.easeOut));
    }

    Future.delayed(widget.animationDelay, () {
      if (mounted) {
        _controller.forward();
      }
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return PremiumCard(
      onTap: widget.onTap,
      isGlass: widget.isGlass,
      animationDelay: widget.animationDelay,
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Icon and Title
          Row(
            children: [
              Container(
                width: 40,
                height: 40,
                decoration: BoxDecoration(
                  color: widget.color.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(
                  widget.icon,
                  color: widget.color,
                  size: 20,
                ),
              ),
              const Spacer(),
              if (widget.change != null)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: widget.change!.startsWith('+')
                        ? AppTheme.success.withOpacity(0.1)
                        : AppTheme.error.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(
                        widget.change!.startsWith('+')
                            ? Icons.trending_up
                            : Icons.trending_down,
                        color: widget.change!.startsWith('+')
                            ? AppTheme.success
                            : AppTheme.error,
                        size: 12,
                      ),
                      const SizedBox(width: 4),
                      Text(
                        widget.change!,
                        style: TextStyle(
                          color: widget.change!.startsWith('+')
                              ? AppTheme.success
                              : AppTheme.error,
                          fontSize: 12,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ),
            ],
          ),

          const SizedBox(height: 16),

          // Value
          Text(
            widget.value,
            style: Theme.of(context).textTheme.headlineMedium?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ).animate(controller: _controller)
            .fadeIn(delay: const Duration(milliseconds: 200))
            .slideY(begin: 0.2, end: 0),

          const SizedBox(height: 4),

          // Title
          Text(
            widget.title,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textSecondary,
              fontWeight: FontWeight.w500,
            ),
          ).animate(controller: _controller)
            .fadeIn(delay: const Duration(milliseconds: 300))
            .slideY(begin: 0.2, end: 0),

          // Progress Bar (if enabled)
          if (widget.showProgress && widget.progressValue != null) ...[
            const SizedBox(height: 12),
            Container(
              height: 4,
              decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.1),
                borderRadius: BorderRadius.circular(2),
              ),
              child: AnimatedBuilder(
                animation: _progressAnimation,
                builder: (context, child) {
                  return FractionallySizedBox(
                    alignment: Alignment.centerLeft,
                    widthFactor: _progressAnimation.value,
                    child: Container(
                      decoration: BoxDecoration(
                        color: widget.color,
                        borderRadius: BorderRadius.circular(2),
                      ),
                    ),
                  );
                },
              ),
            ),
          ],
        ],
      ),
    );
  }
}

class PremiumInteractiveCard extends StatefulWidget {
  final Widget child;
  final EdgeInsetsGeometry? padding;
  final double? width;
  final double? height;
  final double borderRadius;
  final Color? backgroundColor;
  final Color? borderColor;
  final VoidCallback? onTap;
  final VoidCallback? onLongPress;
  final bool isPressable;
  final bool isHoverable;
  final Duration animationDelay;

  const PremiumInteractiveCard({
    super.key,
    required this.child,
    this.padding,
    this.width,
    this.height,
    this.borderRadius = 16,
    this.backgroundColor,
    this.borderColor,
    this.onTap,
    this.onLongPress,
    this.isPressable = true,
    this.isHoverable = true,
    this.animationDelay = Duration.zero,
  });

  @override
  State<PremiumInteractiveCard> createState() => _PremiumInteractiveCardState();
}

class _PremiumInteractiveCardState extends State<PremiumInteractiveCard>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;
  late Animation<double> _opacityAnimation;
  bool _isPressed = false;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(milliseconds: 150),
      vsync: this,
    );

    _scaleAnimation = Tween<double>(begin: 1.0, end: 0.95).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );

    _opacityAnimation = Tween<double>(begin: 1.0, end: 0.8).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _handleTapDown(TapDownDetails details) {
    if (widget.isPressable) {
      setState(() => _isPressed = true);
      _controller.forward();
    }
  }

  void _handleTapUp(TapUpDetails details) {
    if (widget.isPressable) {
      setState(() => _isPressed = false);
      _controller.reverse();
      widget.onTap?.call();
    }
  }

  void _handleTapCancel() {
    if (widget.isPressable) {
      setState(() => _isPressed = false);
      _controller.reverse();
    }
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTapDown: _handleTapDown,
      onTapUp: _handleTapUp,
      onTapCancel: _handleTapCancel,
      onLongPress: widget.onLongPress,
      child: AnimatedBuilder(
        animation: _controller,
        builder: (context, child) {
          return Transform.scale(
            scale: _scaleAnimation.value,
            child: Opacity(
              opacity: _opacityAnimation.value,
              child: PremiumCard(
                width: widget.width,
                height: widget.height,
                borderRadius: widget.borderRadius,
                backgroundColor: widget.backgroundColor,
                borderColor: widget.borderColor,
                padding: widget.padding,
                isAnimated: false,
                isGlass: true,
                child: child,
              ),
            ),
          );
        },
        child: widget.child,
      ),
    ).animate()
      .fadeIn(delay: widget.animationDelay)
      .slideY(begin: 0.1, end: 0, delay: widget.animationDelay);
  }
}

class PremiumListCard extends StatelessWidget {
  final String title;
  final String? subtitle;
  final Widget? leading;
  final Widget? trailing;
  final List<Widget>? actions;
  final Color? color;
  final IconData? icon;
  final VoidCallback? onTap;
  final bool showDivider;
  final EdgeInsetsGeometry? padding;

  const PremiumListCard({
    super.key,
    required this.title,
    this.subtitle,
    this.leading,
    this.trailing,
    this.actions,
    this.color,
    this.icon,
    this.onTap,
    this.showDivider = true,
    this.padding,
  });

  @override
  Widget build(BuildContext context) {
    return PremiumInteractiveCard(
      onTap: onTap,
      padding: padding ?? const EdgeInsets.all(16),
      child: Column(
        children: [
          Row(
            children: [
              if (leading != null) ...[
                leading!,
                const SizedBox(width: 12),
              ] else if (icon != null) ...[
                Container(
                  width: 40,
                  height: 40,
                  decoration: BoxDecoration(
                    color: (color ?? AppTheme.primary).withOpacity(0.1),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Icon(
                    icon,
                    color: color ?? AppTheme.primary,
                    size: 20,
                  ),
                ),
                const SizedBox(width: 12),
              ],
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    if (subtitle != null) ...[
                      const SizedBox(height: 4),
                      Text(
                        subtitle!,
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: AppTheme.textSecondary,
                        ),
                      ),
                    ],
                  ],
                ),
              ),
              if (trailing != null) trailing!,
            ],
          ),
          if (actions != null && actions!.isNotEmpty) ...[
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: actions!,
            ),
          ],
          if (showDivider) ...[
            const SizedBox(height: 12),
            Divider(
              color: AppTheme.surfaceLight.withOpacity(0.3),
              height: 1,
            ),
          ],
        ],
      ),
    );
  }
}

class PremiumChartCard extends StatelessWidget {
  final String title;
  final Widget chart;
  final String? subtitle;
  final List<Widget>? actions;
  final double height;
  final Duration animationDelay;

  const PremiumChartCard({
    super.key,
    required this.title,
    required this.chart,
    this.subtitle,
    this.actions,
    this.height = 200,
    this.animationDelay = Duration.zero,
  });

  @override
  Widget build(BuildContext context) {
    return PremiumCard(
      animationDelay: animationDelay,
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header
          Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    if (subtitle != null) ...[
                      const SizedBox(height: 4),
                      Text(
                        subtitle!,
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: AppTheme.textSecondary,
                        ),
                      ),
                    ],
                  ],
                ),
              ),
              if (actions != null) ...actions!,
            ],
          ),

          const SizedBox(height: 20),

          // Chart
          SizedBox(
            height: height,
            child: chart,
          ),
        ],
      ),
    );
  }
}
