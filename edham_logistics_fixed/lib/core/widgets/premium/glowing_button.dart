// ============================================
// ✨ Glowing Button - Premium Glassmorphism Button
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../theme/app_theme.dart';
import '../glass_container.dart';

class GlowingButton extends StatefulWidget {
  final String text;
  final VoidCallback? onPressed;
  final IconData? icon;
  final Color? color;
  final Color? textColor;
  final double? width;
  final double? height;
  final bool isLoading;
  final bool isGlowing;
  final EdgeInsets? padding;
  final double borderRadius;
  final Duration? animationDelay;

  const GlowingButton({
    super.key,
    required this.text,
    this.onPressed,
    this.icon,
    this.color,
    this.textColor,
    this.width,
    this.height,
    this.isLoading = false,
    this.isGlowing = true,
    this.padding,
    this.borderRadius = 16,
    this.animationDelay,
  });

  @override
  State<GlowingButton> createState() => _GlowingButtonState();
}

class _GlowingButtonState extends State<GlowingButton>
    with SingleTickerProviderStateMixin {
  late AnimationController _glowController;
  late Animation<double> _glowAnimation;
  late AnimationController _pulseController;
  late Animation<double> _pulseAnimation;
  bool _isHovered = false;

  @override
  void initState() {
    super.initState();
    
    if (widget.isGlowing) {
      _glowController = AnimationController(
        duration: const Duration(seconds: 2),
        vsync: this,
      );
      _glowAnimation = Tween<double>(
        begin: 1.0,
        end: 1.5,
      ).animate(CurvedAnimation(
        parent: _glowController,
        curve: Curves.easeInOut,
      ));
      _glowController.repeat(reverse: true);
    }

    _pulseController = AnimationController(
      duration: const Duration(milliseconds: 1500),
      vsync: this,
    );
    _pulseAnimation = Tween<double>(
      begin: 0.95,
      end: 1.05,
    ).animate(CurvedAnimation(
      parent: _pulseController,
      curve: Curves.easeInOut,
    ));
  }

  @override
  void dispose() {
    if (widget.isGlowing) {
      _glowController.dispose();
    }
    _pulseController.dispose();
    super.dispose();
  }

  void _onHoverChange(bool isHovered) {
    setState(() {
      _isHovered = isHovered;
    });
    
    if (isHovered && !widget.isLoading) {
      _pulseController.repeat(reverse: true);
    } else {
      _pulseController.stop();
      _pulseController.reset();
    }
  }

  @override
  Widget build(BuildContext context) {
    final buttonColor = widget.color ?? AppTheme.primary;
    final isDisabled = widget.onPressed == null || widget.isLoading;

    return MouseRegion(
      onEnter: (_) => _onHoverChange(true),
      onExit: (_) => _onHoverChange(false),
      child: GestureDetector(
        onTap: isDisabled ? null : widget.onPressed,
        child: AnimatedBuilder(
          animation: Listenable.merge([_pulseController, _glowController]),
          builder: (context, child) {
            return Transform.scale(
              scale: _isHovered && !widget.isLoading ? _pulseAnimation.value : 1.0,
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 300),
                width: widget.width,
                height: widget.height,
                padding: widget.padding ?? const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
                decoration: BoxDecoration(
                  gradient: isDisabled 
                      ? LinearGradient(colors: [AppTheme.surfaceLight, AppTheme.surfaceLight])
                      : LinearGradient(
                          colors: [buttonColor, buttonColor.withOpacity(0.8)],
                          begin: Alignment.topLeft,
                          end: Alignment.bottomRight,
                        ),
                  borderRadius: BorderRadius.circular(widget.borderRadius),
                  border: Border.all(
                    color: isDisabled 
                        ? Colors.transparent 
                        : buttonColor.withOpacity(0.3),
                    width: 1,
                  ),
                  boxShadow: [
                    if (widget.isGlowing && !isDisabled)
                      BoxShadow(
                        color: buttonColor.withOpacity(0.4 * (_glowAnimation?.value ?? 1.0)),
                        blurRadius: 25 * (_glowAnimation?.value ?? 1.0),
                        spreadRadius: 2,
                        offset: const Offset(0, 8),
                      ),
                    if (_isHovered && !isDisabled)
                      BoxShadow(
                        color: buttonColor.withOpacity(0.6),
                        blurRadius: 20,
                        spreadRadius: 0,
                        offset: const Offset(0, 10),
                      ),
                    if (!isDisabled)
                      BoxShadow(
                        color: Colors.black.withOpacity(0.3),
                        blurRadius: 15,
                        spreadRadius: 0,
                        offset: const Offset(0, 8),
                      ),
                  ],
                ),
                child: Center(
                  child: widget.isLoading
                      ? SizedBox(
                          width: 20,
                          height: 20,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            valueColor: AlwaysStoppedAnimation<Color>(
                              widget.textColor ?? Colors.white,
                            ),
                          ),
                        )
                      : Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            if (widget.icon != null) ...[
                              Icon(
                                widget.icon,
                                color: widget.textColor ?? Colors.white,
                                size: 18,
                              ),
                              const SizedBox(width: 8),
                            ],
                            Text(
                              widget.text,
                              style: TextStyle(
                                color: widget.textColor ?? Colors.white,
                                fontSize: 16,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ],
                        ),
                ),
              ),
            );
          },
        ),
      ).animate()
          .fadeIn(delay: widget.animationDelay ?? Duration.zero)
          .slideY(begin: 0.2, end: 0),
    );
  }
}

// Glass Button with blur effect
class GlassButton extends StatefulWidget {
  final String text;
  final VoidCallback? onPressed;
  final IconData? icon;
  final Color? color;
  final double? width;
  final double? height;
  final bool isLoading;
  final EdgeInsets? padding;
  final double borderRadius;

  const GlassButton({
    super.key,
    required this.text,
    this.onPressed,
    this.icon,
    this.color,
    this.width,
    this.height,
    this.isLoading = false,
    this.padding,
    this.borderRadius = 24,
  });

  @override
  State<GlassButton> createState() => _GlassButtonState();
}

class _GlassButtonState extends State<GlassButton>
    with SingleTickerProviderStateMixin {
  late AnimationController _shimmerController;
  late Animation<double> _shimmerAnimation;

  @override
  void initState() {
    super.initState();
    _shimmerController = AnimationController(
      duration: const Duration(milliseconds: 2000),
      vsync: this,
    );
    _shimmerAnimation = Tween<double>(
      begin: -1.0,
      end: 1.0,
    ).animate(_shimmerController);
    _shimmerController.repeat();
  }

  @override
  void dispose() {
    _shimmerController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final buttonColor = widget.color ?? AppTheme.primary;
    final isDisabled = widget.onPressed == null || widget.isLoading;

    return GestureDetector(
      onTap: isDisabled ? null : widget.onPressed,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 300),
        width: widget.width,
        height: widget.height,
        padding: widget.padding ?? const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
        decoration: BoxDecoration(
          color: isDisabled 
              ? Colors.white.withOpacity(0.02)
              : Colors.white.withOpacity(0.05),
          borderRadius: BorderRadius.circular(widget.borderRadius),
          border: Border.all(
            color: isDisabled 
                ? Colors.white.withOpacity(0.05)
                : buttonColor.withOpacity(0.3),
            width: 1,
          ),
          boxShadow: [
            if (!isDisabled)
              BoxShadow(
                color: buttonColor.withOpacity(0.2),
                blurRadius: 20,
                spreadRadius: 0,
                offset: const Offset(0, 10),
              ),
            BoxShadow(
              color: Colors.black.withOpacity(0.3),
              blurRadius: 25,
              spreadRadius: 0,
              offset: const Offset(0, 15),
            ),
          ],
        ),
        child: Stack(
          children: [
            // Shimmer effect
            if (!isDisabled)
              AnimatedBuilder(
                animation: _shimmerAnimation,
                builder: (context, child) {
                  return Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(widget.borderRadius),
                      gradient: LinearGradient(
                        colors: [
                          Colors.transparent,
                          buttonColor.withOpacity(0.1),
                          Colors.transparent,
                        ],
                        stops: [
                          (_shimmerAnimation.value - 0.3).clamp(0.0, 1.0),
                          _shimmerAnimation.value.clamp(0.0, 1.0),
                          (_shimmerAnimation.value + 0.3).clamp(0.0, 1.0),
                        ],
                      ),
                    ),
                  );
                },
              ),

            // Button content
            Center(
              child: widget.isLoading
                  ? SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        valueColor: AlwaysStoppedAnimation<Color>(
                          widget.color ?? AppTheme.primary,
                        ),
                      ),
                    )
                  : Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        if (widget.icon != null) ...[
                          Icon(
                            widget.icon,
                            color: widget.color ?? AppTheme.primary,
                            size: 18,
                          ),
                          const SizedBox(width: 8),
                        ],
                        Text(
                          widget.text,
                          style: TextStyle(
                            color: widget.color ?? AppTheme.primary,
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
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
}

// Floating Action Button with glow
class FloatingGlowButton extends StatefulWidget {
  final IconData icon;
  final VoidCallback? onPressed;
  final Color? color;
  final double size;
  final bool isGlowing;

  const FloatingGlowButton({
    super.key,
    required this.icon,
    this.onPressed,
    this.color,
    this.size = 56,
    this.isGlowing = true,
  });

  @override
  State<FloatingGlowButton> createState() => _FloatingGlowButtonState();
}

class _FloatingGlowButtonState extends State<FloatingGlowButton>
    with SingleTickerProviderStateMixin {
  late AnimationController _glowController;
  late Animation<double> _glowAnimation;

  @override
  void initState() {
    super.initState();
    
    if (widget.isGlowing) {
      _glowController = AnimationController(
        duration: const Duration(seconds: 2),
        vsync: this,
      );
      _glowAnimation = Tween<double>(
        begin: 1.0,
        end: 1.3,
      ).animate(CurvedAnimation(
        parent: _glowController,
        curve: Curves.easeInOut,
      ));
      _glowController.repeat(reverse: true);
    }
  }

  @override
  void dispose() {
    if (widget.isGlowing) {
      _glowController.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final buttonColor = widget.color ?? AppTheme.primary;

    return AnimatedBuilder(
      animation: _glowController,
      builder: (context, child) {
        return Container(
          width: widget.size,
          height: widget.size,
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: [buttonColor, buttonColor.withOpacity(0.8)],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
            borderRadius: BorderRadius.circular(widget.size / 2),
            boxShadow: [
              if (widget.isGlowing)
                BoxShadow(
                  color: buttonColor.withOpacity(0.4 * (_glowAnimation?.value ?? 1.0)),
                  blurRadius: 25 * (_glowAnimation?.value ?? 1.0),
                  spreadRadius: 2,
                  offset: const Offset(0, 8),
                ),
              BoxShadow(
                color: Colors.black.withOpacity(0.3),
                blurRadius: 20,
                spreadRadius: 0,
                offset: const Offset(0, 10),
              ),
            ],
          ),
          child: Material(
            color: Colors.transparent,
            child: InkWell(
              borderRadius: BorderRadius.circular(widget.size / 2),
              onTap: widget.onPressed,
              child: Center(
                child: Icon(
                  widget.icon,
                  color: Colors.white,
                  size: widget.size * 0.4,
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}
