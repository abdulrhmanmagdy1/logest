// ============================================
// 🌈 App Gradients - Design System Gradient Constants
// ============================================

import 'package:flutter/material.dart';

class AppGradients {
  // Primary brand gradients
  static const LinearGradient primary = LinearGradient(
    colors: [
      Color(0xFFF97316), // Orange-500
      Color(0xFFFFB067), // Orange-300
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  static const LinearGradient primaryReversed = LinearGradient(
    colors: [
      Color(0xFFFFB067), // Orange-300
      Color(0xFFF97316), // Orange-500
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  static const LinearGradient primaryVertical = LinearGradient(
    colors: [
      Color(0xFFF97316), // Orange-500
      Color(0xFFFFB067), // Orange-300
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0.0, 1.0],
  );
  
  static const LinearGradient primaryRadial = RadialGradient(
    colors: [
      Color(0xFFF97316), // Orange-500
      Color(0xFFFFB067), // Orange-300
    ],
    center: Alignment.center,
    radius: 1.0,
    stops: [0.0, 1.0],
  );
  
  // Success gradients
  static const LinearGradient success = LinearGradient(
    colors: [
      Color(0xFF22C55E), // Green-500
      Color(0xFF10B981), // Green-600
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  static const LinearGradient successVertical = LinearGradient(
    colors: [
      Color(0xFF22C55E), // Green-500
      Color(0xFF10B981), // Green-600
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0.0, 1.0],
  );
  
  // Error gradients
  static const LinearGradient error = LinearGradient(
    colors: [
      Color(0xFFEF4444), // Red-500
      Color(0xFFDC2626), // Red-600
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  // Warning gradients
  static const LinearGradient warning = LinearGradient(
    colors: [
      Color(0xFFF59E0B), // Amber-500
      Color(0xFFD97706), // Amber-600
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  // Info gradients
  static const LinearGradient info = LinearGradient(
    colors: [
      Color(0xFF3B82F6), // Blue-500
      Color(0xFF2563EB), // Blue-600
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  // Glassmorphism gradients
  static const LinearGradient glass = LinearGradient(
    colors: [
      Color(0x0DFFFFFF), // White.withOpacity(0.05)
      Color(0x0AFFFFFF), // White.withOpacity(0.04)
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  static const LinearGradient glassReversed = LinearGradient(
    colors: [
      Color(0x0AFFFFFF), // White.withOpacity(0.04)
      Color(0x0DFFFFFF), // White.withOpacity(0.05)
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  // Dark gradients
  static const LinearGradient darkSurface = LinearGradient(
    colors: [
      Color(0xFF1C1C1E), // Surface
      Color(0xFF2C2C2E), // Surface Light
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  static const LinearGradient darkBackground = LinearGradient(
    colors: [
      Color(0xFF0A0A0A), // Background
      Color(0xFF1A1A1C), // Slightly lighter
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0.0, 1.0],
  );
  
  // Status gradients
  static const LinearGradient statusPending = LinearGradient(
    colors: [
      Color(0xFFF59E0B), // Warning-500
      Color(0xFFD97706), // Warning-600
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  static const LinearGradient statusSuccess = LinearGradient(
    colors: [
      Color(0xFF22C55E), // Green-500
      Color(0xFF10B981), // Green-600
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  static const LinearGradient statusError = LinearGradient(
    colors: [
      Color(0xFFEF4444), // Red-500
      Color(0xFFDC2626), // Red-600
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 1.0],
  );
  
  // Animated gradients
  static LinearGradient animatedPrimary(double animationValue) {
    return LinearGradient(
      colors: [
        Color.lerp(
          const Color(0xFFF97316),
          const Color(0xFFFFB067),
          animationValue,
        ),
        Color.lerp(
          const Color(0xFFFFB067),
          const Color(0xFFF97316),
          animationValue,
        ),
      ],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      stops: [0.0, 1.0],
    );
  }
  
  static LinearGradient glowingPrimary(double glowIntensity) {
    return LinearGradient(
      colors: [
        Color(0xFFF97316).withOpacity(glowIntensity),
        Color(0xFFFFB067).withOpacity(glowIntensity * 0.7),
        Colors.transparent,
      ],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      stops: [0.0, 0.8, 1.0],
    );
  }
  
  // Custom gradient builders
  static LinearGradient custom({
    required List<Color> colors,
    Alignment begin = Alignment.topLeft,
    Alignment end = Alignment.bottomRight,
    List<double>? stops,
  }) {
    return LinearGradient(
      colors: colors,
      begin: begin,
      end: end,
      stops: stops ?? List.generate(colors.length, (index) => index / (colors.length - 1)),
    );
  }
  
  static LinearGradient radial({
    required List<Color> colors,
    Alignment center = Alignment.center,
    double radius = 1.0,
    List<double>? stops,
  }) {
    return RadialGradient(
      colors: colors,
      center: center,
      radius: radius,
      stops: stops ?? List.generate(colors.length, (index) => index / (colors.length - 1)),
    );
  }
  
  // Sweep gradients for loading effects
  static LinearGradient sweep({
    required Color color,
    double startAngle = 0.0,
    double endAngle = 3.14159,
    Alignment center = Alignment.center,
  }) {
    return SweepGradient(
      colors: [
        color.withOpacity(0.1),
        color.withOpacity(0.3),
        color.withOpacity(0.5),
        color.withOpacity(0.3),
        color.withOpacity(0.1),
      ],
      startAngle: startAngle,
      endAngle: endAngle,
      center: center,
    );
  }
  
  // Gradient utilities
  static Color lerpGradient(Gradient gradient1, Gradient gradient2, double t) {
    // Simple interpolation between two gradients of same type
    if (gradient1 is LinearGradient && gradient2 is LinearGradient) {
      final g1 = gradient1 as LinearGradient;
      final g2 = gradient2 as LinearGradient;
      
      return LinearGradient(
        colors: [
          Color.lerp(g1.colors.first, g2.colors.first, t),
          Color.lerp(g1.colors.last, g2.colors.last, t),
        ],
        begin: Alignment.lerp(g1.begin, g2.begin, t),
        end: Alignment.lerp(g1.end, g2.end, t),
        stops: g1.stops, // Use stops from first gradient
      );
    }
    
    // Fallback to first gradient
    return gradient1;
  }
  
  // Gradient presets for common use cases
  static const Map<String, LinearGradient> presets = {
    'primary': primary,
    'success': success,
    'error': error,
    'warning': warning,
    'info': info,
    'glass': glass,
    'darkSurface': darkSurface,
    'darkBackground': darkBackground,
    'statusPending': statusPending,
    'statusSuccess': statusSuccess,
    'statusError': statusError,
  };
  
  static LinearGradient? getPreset(String name) {
    return presets[name];
  }
  
  // Responsive gradients
  static LinearGradient getResponsiveGradient(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    if (screenWidth < 600) return primary;      // Mobile
    if (screenWidth < 1024) return primaryReversed; // Tablet
    return primaryRadial;                              // Desktop
  }
}
