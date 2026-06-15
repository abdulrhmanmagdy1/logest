import 'package:flutter/material.dart';
import '../constants/index.dart';

/// Custom Card
/// Professional card with multiple variants
class CustomCard extends StatelessWidget {
  final Widget child;
  final VoidCallback? onTap;
  final EdgeInsets padding;
  final double? width;
  final double? height;
  final Color? backgroundColor;
  final Color? borderColor;
  final double borderRadius;
  final double elevation;
  final Widget? header;
  final Widget? footer;
  final bool hasShadow;
  final Gradient? gradient;

  const CustomCard({
    super.key,
    required this.child,
    this.onTap,
    this.padding = const EdgeInsets.all(16),
    this.width,
    this.height,
    this.backgroundColor,
    this.borderColor,
    this.borderRadius = 16,
    this.elevation = 4,
    this.header,
    this.footer,
    this.hasShadow = true,
    this.gradient,
  });

  @override
  Widget build(BuildContext context) {
    Widget cardContent = Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (header != null) ...[
          header!,
          const SizedBox(height: 12),
        ],
        child,
        if (footer != null) ...[
          const SizedBox(height: 12),
          footer!,
        ],
      ],
    );

    Widget card = Container(
      width: width,
      height: height,
      padding: padding,
      decoration: BoxDecoration(
        gradient: gradient,
        color: gradient == null ? (backgroundColor ?? AppColors.card) : null,
        borderRadius: BorderRadius.circular(borderRadius),
        border: borderColor != null
            ? Border.all(color: borderColor!, width: 1)
            : null,
        boxShadow: hasShadow
            ? [
                BoxShadow(
                  color: Colors.black.withOpacity(0.1),
                  blurRadius: elevation * 2,
                  spreadRadius: elevation * 0.5,
                  offset: Offset(0, elevation),
                ),
              ]
            : null,
      ),
      child: cardContent,
    );

    if (onTap != null) {
      return GestureDetector(
        onTap: onTap,
        child: card,
      );
    }

    return card;
  }
}

/// Info Card
/// Card with icon and info
class InfoCard extends StatelessWidget {
  final IconData icon;
  final String title;
  final String value;
  final String? subtitle;
  final Color? iconColor;
  final Color? valueColor;
  final VoidCallback? onTap;

  const InfoCard({
    super.key,
    required this.icon,
    required this.title,
    required this.value,
    this.subtitle,
    this.iconColor,
    this.valueColor,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return CustomCard(
      onTap: onTap,
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: (iconColor ?? AppColors.primary).withOpacity(0.1),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(
              icon,
              color: iconColor ?? AppColors.primary,
              size: 24,
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: TextStyle(
                    fontSize: 12,
                    color: AppColors.textSecondary,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  value,
                  style: TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                    color: valueColor ?? AppColors.textPrimary,
                  ),
                ),
                if (subtitle != null) ...[
                  const SizedBox(height: 2),
                  Text(
                    subtitle!,
                    style: TextStyle(
                      fontSize: 11,
                      color: AppColors.textMuted,
                    ),
                  ),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }
}

/// Stat Card
/// Card for statistics display
class StatCard extends StatelessWidget {
  final String title;
  final String value;
  final double? percentageChange;
  final IconData icon;
  final Color color;
  final VoidCallback? onTap;

  const StatCard({
    super.key,
    required this.title,
    required this.value,
    this.percentageChange,
    required this.icon,
    required this.color,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return CustomCard(
      onTap: onTap,
      gradient: LinearGradient(
        colors: [
          color.withOpacity(0.1),
          color.withOpacity(0.05),
        ],
        begin: Alignment.topLeft,
        end: Alignment.bottomRight,
      ),
      borderColor: color.withOpacity(0.2),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Icon(icon, color: color, size: 28),
              if (percentageChange != null)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: percentageChange! >= 0
                        ? AppColors.success.withOpacity(0.1)
                        : AppColors.error.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(
                        percentageChange! >= 0
                            ? Icons.arrow_upward
                            : Icons.arrow_downward,
                        size: 12,
                        color: percentageChange! >= 0
                            ? AppColors.success
                            : AppColors.error,
                      ),
                      const SizedBox(width: 4),
                      Text(
                        '${percentageChange!.abs().toStringAsFixed(1)}%',
                        style: TextStyle(
                          fontSize: 11,
                          fontWeight: FontWeight.w600,
                          color: percentageChange! >= 0
                              ? AppColors.success
                              : AppColors.error,
                        ),
                      ),
                    ],
                  ),
                ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            value,
            style: TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
              color: AppColors.textPrimary,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            title,
            style: TextStyle(
              fontSize: 12,
              color: AppColors.textSecondary,
            ),
          ),
        ],
      ),
    );
  }
}
