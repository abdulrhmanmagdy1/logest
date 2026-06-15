// ============================================
// 📏 App Spacing - Design System Spacing Constants
// ============================================

import 'package:flutter/material.dart';

class AppSpacing {
  // Base spacing unit (4px)
  static const double unit = 4.0;
  
  // Micro spacing
  static const double xxSmall = unit * 0.5; // 2px
  static const double xSmall = unit * 1;     // 4px
  static const double small = unit * 2;       // 8px
  
  // Regular spacing
  static const double medium = unit * 4;      // 16px
  static const double large = unit * 6;       // 24px
  static const double xLarge = unit * 8;      // 32px
  
  // Macro spacing
  static const double xxLarge = unit * 10;    // 40px
  static const double xxxLarge = unit * 12;   // 48px
  
  // Component specific spacing
  static const double componentGap = medium;    // 16px
  static const double sectionGap = large;       // 24px
  static const double cardPadding = medium;      // 16px
  static const double screenPadding = large;     // 24px
  static const double buttonPadding = small;     // 8px
  static const double inputPadding = small;      // 8px
  static const double iconPadding = xSmall;     // 4px
  
  // Layout spacing
  static const double gridGap = medium;         // 16px
  static const double listGap = medium;          // 16px
  static const double cardGap = medium;          // 16px
  static const double sectionSpacing = large;     // 24px
  static const double pageSpacing = xLarge;      // 32px
  
  // Edge Insets
  static const EdgeInsets paddingAllSmall = EdgeInsets.all(small);
  static const EdgeInsets paddingAllMedium = EdgeInsets.all(medium);
  static const EdgeInsets paddingAllLarge = EdgeInsets.all(large);
  static const EdgeInsets paddingAllXLarge = EdgeInsets.all(xLarge);
  
  static const EdgeInsets paddingSymmetricMedium = EdgeInsets.symmetric(horizontal: medium, vertical: medium);
  static const EdgeInsets paddingSymmetricLarge = EdgeInsets.symmetric(horizontal: large, vertical: large);
  static const EdgeInsets paddingSymmetricXLarge = EdgeInsets.symmetric(horizontal: xLarge, vertical: large);
  
  static const EdgeInsets paddingOnlyMedium = EdgeInsets.only(
    left: medium,
    right: medium,
    top: medium,
    bottom: medium,
  );
  
  // Margin Insets
  static const EdgeInsets marginAllSmall = EdgeInsets.all(small);
  static const EdgeInsets marginAllMedium = EdgeInsets.all(medium);
  static const EdgeInsets marginAllLarge = EdgeInsets.all(large);
  static const EdgeInsets marginAllXLarge = EdgeInsets.all(xLarge);
  
  static const EdgeInsets marginSymmetricMedium = EdgeInsets.symmetric(horizontal: medium, vertical: medium);
  static const EdgeInsets marginSymmetricLarge = EdgeInsets.symmetric(horizontal: large, vertical: large);
  static const EdgeInsets marginSymmetricXLarge = EdgeInsets.symmetric(horizontal: xLarge, vertical: large);
  
  // Sliver padding
  static const EdgeInsets sliverPaddingMedium = EdgeInsets.symmetric(horizontal: medium, vertical: medium);
  static const EdgeInsets sliverPaddingLarge = EdgeInsets.symmetric(horizontal: large, vertical: large);
  
  // Safe area offsets
  static const double safeAreaTop = 20.0;
  static const double safeAreaBottom = 20.0;
  static const EdgeInsets safeAreaPadding = EdgeInsets.only(
    top: safeAreaTop,
    bottom: safeAreaBottom,
  );
  
  // Floating element spacing
  static const double floatingButtonBottom = large + small; // 32px
  static const double floatingCardBottom = large;          // 24px
  static const double floatingNavBottom = medium;           // 16px
  
  // Responsive spacing helpers
  static double getResponsiveSpacing(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    if (screenWidth < 600) return medium;      // Mobile
    if (screenWidth < 1024) return large;      // Tablet
    return xLarge;                                // Desktop
  }
  
  static EdgeInsets getResponsivePadding(BuildContext context) {
    final spacing = getResponsiveSpacing(context);
    return EdgeInsets.all(spacing);
  }
  
  // Aspect ratio helpers
  static const double cardAspectRatio = 1.2;
  static const double listTileAspectRatio = 3.5;
  static const double chartAspectRatio = 1.5;
  static const double gridAspectRatio = 1.0;
  
  // Breakpoint spacing
  static const double mobileBreakpoint = 600;
  static const double tabletBreakpoint = 1024;
  static const double desktopBreakpoint = 1440;
}
