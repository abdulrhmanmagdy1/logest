// ============================================
// 🔄 App Radius - Design System Border Radius Constants
// ============================================

import 'package:flutter/material.dart';

class AppRadius {
  // Base radius unit
  static const double base = 8.0;
  
  // Micro radius
  static const double xxxSmall = base * 0.5;  // 4px
  static const double xxSmall = base * 0.75; // 6px
  static const double xSmall = base * 1;     // 8px
  static const double small = base * 1.5;     // 12px
  
  // Regular radius
  static const double medium = base * 2;      // 16px
  static const double large = base * 3;       // 24px
  static const double xLarge = base * 4;      // 32px
  
  // Macro radius
  static const double xxLarge = base * 5;     // 40px
  static const double xxxLarge = base * 6;    // 48px
  
  // Component specific radius
  static const double buttonRadius = medium;           // 16px
  static const double cardRadius = large;               // 24px
  static const double inputRadius = medium;              // 16px
  static const double chipRadius = small;               // 12px
  static const double dialogRadius = large;              // 24px
  static const double bottomNavRadius = xLarge;         // 32px
  static const double floatingButtonRadius = xLarge;     // 32px
  static const double containerRadius = large;           // 24px
  static const double glassContainerRadius = large;       // 24px
  static const double avatarRadius = xLarge;            // 32px
  static const double badgeRadius = small;               // 12px
  
  // Border radius objects
  static const BorderRadius allXSmall = BorderRadius.all(Radius.circular(xSmall));
  static const BorderRadius allSmall = BorderRadius.all(Radius.circular(small));
  static const BorderRadius allMedium = BorderRadius.all(Radius.circular(medium));
  static const BorderRadius allLarge = BorderRadius.all(Radius.circular(large));
  static const BorderRadius allXLarge = BorderRadius.all(Radius.circular(xLarge));
  static const BorderRadius allXXLarge = BorderRadius.all(Radius.circular(xxLarge));
  
  // Component specific border radius
  static const BorderRadius button = BorderRadius.all(Radius.circular(buttonRadius));
  static const BorderRadius card = BorderRadius.all(Radius.circular(cardRadius));
  static const BorderRadius input = BorderRadius.all(Radius.circular(inputRadius));
  static const BorderRadius chip = BorderRadius.all(Radius.circular(chipRadius));
  static const BorderRadius dialog = BorderRadius.all(Radius.circular(dialogRadius));
  static const BorderRadius bottomNav = BorderRadius.all(Radius.circular(bottomNavRadius));
  static const BorderRadius floatingButton = BorderRadius.all(Radius.circular(floatingButtonRadius));
  static const BorderRadius container = BorderRadius.all(Radius.circular(containerRadius));
  static const BorderRadius glassContainer = BorderRadius.all(Radius.circular(glassContainerRadius));
  static const BorderRadius avatar = BorderRadius.all(Radius.circular(avatarRadius));
  static const BorderRadius badge = BorderRadius.all(Radius.circular(badgeRadius));
  
  // Asymmetric border radius
  static const BorderRadius topOnly = BorderRadius.only(
    topLeft: Radius.circular(medium),
    topRight: Radius.circular(medium),
  );
  
  static const BorderRadius bottomOnly = BorderRadius.only(
    bottomLeft: Radius.circular(medium),
    bottomRight: Radius.circular(medium),
  );
  
  static const BorderRadius leftOnly = BorderRadius.only(
    topLeft: Radius.circular(medium),
    bottomLeft: Radius.circular(medium),
  );
  
  static const BorderRadius rightOnly = BorderRadius.only(
    topRight: Radius.circular(medium),
    bottomRight: Radius.circular(medium),
  );
  
  // Custom border radius helpers
  static BorderRadius custom(double radius) => BorderRadius.all(Radius.circular(radius));
  static BorderRadius customAsymmetric({
    double topLeft = 0,
    double topRight = 0,
    double bottomLeft = 0,
    double bottomRight = 0,
  }) {
    return BorderRadius.only(
      topLeft: Radius.circular(topLeft),
      topRight: Radius.circular(topRight),
      bottomLeft: Radius.circular(bottomLeft),
      bottomRight: Radius.circular(bottomRight),
    );
  }
  
  // Responsive radius helpers
  static double getResponsiveRadius(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    if (screenWidth < 600) return medium;      // Mobile
    if (screenWidth < 1024) return large;      // Tablet
    return xLarge;                                // Desktop
  }
  
  static BorderRadius getResponsiveBorderRadius(BuildContext context) {
    final radius = getResponsiveRadius(context);
    return BorderRadius.all(Radius.circular(radius));
  }
  
  // Specialized radius for premium components
  static const BorderRadius glassmorphismCard = BorderRadius.all(Radius.circular(large));
  static const BorderRadius glowingButton = BorderRadius.all(Radius.circular(medium));
  static const BorderRadius floatingCard = BorderRadius.all(Radius.circular(xLarge));
  static const BorderRadius premiumInput = BorderRadius.all(Radius.circular(small));
  static const BorderRadius premiumChip = BorderRadius.all(Radius.circular(xSmall));
}
