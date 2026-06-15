// ============================================
// 🌟 App Shadows - Design System Shadow Constants
// ============================================

import 'package:flutter/material.dart';

class AppShadows {
  // Base shadow values
  static const double lightBlur = 10.0;
  static const double mediumBlur = 20.0;
  static const double heavyBlur = 30.0;
  static const double extremeBlur = 50.0;
  
  static const double lightSpread = 0.0;
  static const double mediumSpread = 2.0;
  static const double heavySpread = 5.0;
  static const double extremeSpread = 10.0;
  
  // Shadow offsets
  static const Offset lightOffset = Offset(0, 4);
  static const Offset mediumOffset = Offset(0, 8);
  static const Offset heavyOffset = Offset(0, 15);
  static const Offset extremeOffset = Offset(0, 25);
  
  // Shadow colors
  static const Color lightColor = Color(0x1A000000); // Colors.black.withOpacity(0.1)
  static const Color mediumColor = Color(0x33000000); // Colors.black.withOpacity(0.2)
  static const Color heavyColor = Color(0x66000000); // Colors.black.withOpacity(0.4)
  static const Color extremeColor = Color(0x99000000); // Colors.black.withOpacity(0.6)
  
  // Predefined shadow lists
  static const List<BoxShadow> light = [
    BoxShadow(
      color: lightColor,
      blurRadius: lightBlur,
      spreadRadius: lightSpread,
      offset: lightOffset,
    ),
  ];
  
  static const List<BoxShadow> medium = [
    BoxShadow(
      color: mediumColor,
      blurRadius: mediumBlur,
      spreadRadius: mediumSpread,
      offset: mediumOffset,
    ),
  ];
  
  static const List<BoxShadow> heavy = [
    BoxShadow(
      color: heavyColor,
      blurRadius: heavyBlur,
      spreadRadius: heavySpread,
      offset: heavyOffset,
    ),
  ];
  
  static const List<BoxShadow> extreme = [
    BoxShadow(
      color: extremeColor,
      blurRadius: extremeBlur,
      spreadRadius: extremeSpread,
      offset: extremeOffset,
    ),
  ];
  
  // Component specific shadows
  static const List<BoxShadow> button = [
    BoxShadow(
      color: mediumColor,
      blurRadius: mediumBlur,
      spreadRadius: lightSpread,
      offset: mediumOffset,
    ),
  ];
  
  static const List<BoxShadow> card = [
    BoxShadow(
      color: heavyColor,
      blurRadius: heavyBlur,
      spreadRadius: mediumSpread,
      offset: heavyOffset,
    ),
  ];
  
  static const List<BoxShadow> floating = [
    BoxShadow(
      color: heavyColor,
      blurRadius: heavyBlur,
      spreadRadius: mediumSpread,
      offset: heavyOffset,
    ),
  ];
  
  static const List<BoxShadow> dialog = [
    BoxShadow(
      color: extremeColor,
      blurRadius: extremeBlur,
      spreadRadius: heavySpread,
      offset: extremeOffset,
    ),
  ];
  
  // Glassmorphism shadows
  static const List<BoxShadow> glassmorphism = [
    BoxShadow(
      color: heavyColor,
      blurRadius: heavyBlur,
      spreadRadius: lightSpread,
      offset: heavyOffset,
    ),
  ];
  
  static const List<BoxShadow> glassmorphismLight = [
    BoxShadow(
      color: mediumColor,
      blurRadius: mediumBlur,
      spreadRadius: lightSpread,
      offset: mediumOffset,
    ),
  ];
  
  // Glowing shadows
  static List<BoxShadow> glowing(Color color, {double intensity = 1.0}) {
    return [
      BoxShadow(
        color: color.withOpacity(0.4 * intensity),
        blurRadius: 25.0 * intensity,
        spreadRadius: 2.0 * intensity,
        offset: const Offset(0, 10),
      ),
      BoxShadow(
        color: color.withOpacity(0.2 * intensity),
        blurRadius: 40.0 * intensity,
        spreadRadius: -5.0 * intensity,
        offset: const Offset(0, 15),
      ),
    ];
  }
  
  static List<BoxShadow> softGlow(Color color, {double intensity = 1.0}) {
    return [
      BoxShadow(
        color: color.withOpacity(0.3 * intensity),
        blurRadius: 20.0 * intensity,
        spreadRadius: 0.0,
        offset: const Offset(0, 8),
      ),
    ];
  }
  
  static List<BoxShadow> intenseGlow(Color color, {double intensity = 1.0}) {
    return [
      BoxShadow(
        color: color.withOpacity(0.6 * intensity),
        blurRadius: 30.0 * intensity,
        spreadRadius: 2.0 * intensity,
        offset: const Offset(0, 15),
      ),
      BoxShadow(
        color: color.withOpacity(0.3 * intensity),
        blurRadius: 50.0 * intensity,
        spreadRadius: -10.0 * intensity,
        offset: const Offset(0, 25),
      ),
    ];
  }
  
  // Animated shadows
  static List<BoxShadow> pulse(Color color, double animationValue) {
    return [
      BoxShadow(
        color: color.withOpacity(0.4 * animationValue),
        blurRadius: 25.0 * animationValue,
        spreadRadius: 2.0 * animationValue,
        offset: const Offset(0, 10),
      ),
    ];
  }
  
  static List<BoxShadow> float(double animationValue) {
    return [
      BoxShadow(
        color: heavyColor,
        blurRadius: heavyBlur,
        spreadRadius: mediumSpread,
        offset: Offset(0, 15 + 5 * animationValue),
      ),
    ];
  }
  
  // Inner shadows
  static const List<BoxShadow> inner = [
    BoxShadow(
      color: Color(0x1A000000), // Colors.black.withOpacity(0.1)
      blurRadius: 10.0,
      spreadRadius: -2.0,
      offset: const Offset(0, 2),
    ),
  ];
  
  static const List<BoxShadow> innerHeavy = [
    BoxShadow(
      color: Color(0x33000000), // Colors.black.withOpacity(0.2)
      blurRadius: 20.0,
      spreadRadius: -5.0,
      offset: const Offset(0, 5),
    ),
  ];
  
  // Colored shadows
  static List<BoxShadow> primary = [
    BoxShadow(
      color: Color(0x4DF97316), // AppTheme.primary.withOpacity(0.3)
      blurRadius: mediumBlur,
      spreadRadius: lightSpread,
      offset: mediumOffset,
    ),
  ];
  
  static List<BoxShadow> success = [
    BoxShadow(
      color: Color(0x4D22C55E), // AppTheme.success.withOpacity(0.3)
      blurRadius: mediumBlur,
      spreadRadius: lightSpread,
      offset: mediumOffset,
    ),
  ];
  
  static List<BoxShadow> error = [
    BoxShadow(
      color: Color(0x4DEF4444), // AppTheme.error.withOpacity(0.3)
      blurRadius: mediumBlur,
      spreadRadius: lightSpread,
      offset: mediumOffset,
    ),
  ];
  
  static List<BoxShadow> warning = [
    BoxShadow(
      color: Color(0x4DF59E0B), // AppTheme.warning.withOpacity(0.3)
      blurRadius: mediumBlur,
      spreadRadius: lightSpread,
      offset: mediumOffset,
    ),
  ];
  
  // Custom shadow helpers
  static List<BoxShadow> custom({
    Color color = heavyColor,
    double blurRadius = heavyBlur,
    double spreadRadius = mediumSpread,
    Offset offset = heavyOffset,
  }) {
    return [
      BoxShadow(
        color: color,
        blurRadius: blurRadius,
        spreadRadius: spreadRadius,
        offset: offset,
      ),
    ];
  }
  
  static List<BoxShadow> multiLayer({
    required List<BoxShadow> layers,
  }) {
    return layers;
  }
  
  // Responsive shadows
  static List<BoxShadow> getResponsiveShadow(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    if (screenWidth < 600) return light;      // Mobile
    if (screenWidth < 1024) return medium;    // Tablet
    return heavy;                                // Desktop
  }
  
  // Elevation-based shadows
  static List<BoxShadow> fromElevation(double elevation) {
    switch (elevation.toInt()) {
      case 1:
        return light;
      case 2:
        return medium;
      case 3:
        return heavy;
      case 4:
      return extreme;
      default:
        return custom(blurRadius: elevation * 4);
    }
  }
}
