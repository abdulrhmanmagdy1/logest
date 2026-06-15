import 'package:flutter/material.dart';
import '../../../core/theme/app_theme.dart';

class GlassContainer extends StatelessWidget {
  final Widget child;
  final EdgeInsetsGeometry? padding;
  final EdgeInsetsGeometry? margin;
  final double? width;
  final double? height;
  final BorderRadius borderRadius;
  final Color? background;
  final Border? border;
  final List<BoxShadow>? boxShadow;
  final double? blur;
  final double? opacity;

  const GlassContainer({
    super.key,
    required this.child,
    this.padding,
    this.margin,
    this.width,
    this.height,
    this.borderRadius = const BorderRadius.all(Radius.circular(12)),
    this.background,
    this.border,
    this.boxShadow,
    this.blur = 10.0,
    this.opacity = 0.1,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: margin,
      width: width,
      height: height,
      decoration: BoxDecoration(
        borderRadius: borderRadius,
        // Glassmorphism effect
        gradient: LinearGradient(
          colors: [
            (background ?? AppTheme.textPrimary).withOpacity(opacity ?? 0.1),
            (background ?? AppTheme.textPrimary).withOpacity((opacity ?? 0.1) * 0.5),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        border: border ?? Border.all(
          color: (background ?? AppTheme.textPrimary).withOpacity(0.2),
          width: 1,
        ),
        boxShadow: boxShadow ?? [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: blur ?? 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: ClipRRect(
        borderRadius: borderRadius,
        child: Padding(
          padding: padding ?? const EdgeInsets.all(16),
          child: child,
        ),
      ),
    );
  }
}
