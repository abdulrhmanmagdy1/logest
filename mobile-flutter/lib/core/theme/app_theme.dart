// ============================================
// 🎨 Edham App Theme - Modern Dark Orange Theme
// Matching the design from reference images
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_fonts/google_fonts.dart';

class AppTheme {
  // Brand Colors - Orange Theme from images
  static const Color primary = Color(0xFFF97316);        // Orange-500
  static const Color primaryDark = Color(0xFFEA580C);    // Orange-600
  static const Color primaryLight = Color(0xFFFB923C);   // Orange-400
  static const Color accent = Color(0xFFF97316);         // Orange
  static const Color success = Color(0xFF22C55E);
  static const Color error = Color(0xFFEF4444);
  static const Color warning = Color(0xFFF59E0B);
  static const Color info = Color(0xFF3B82F6);

  // Background Colors - Dark theme from images
  static const Color background = Color(0xFF0A0A0A);     // Almost black
  static const Color surface = Color(0xFF1C1C1E);       // Dark gray cards
  static const Color surfaceLight = Color(0xFF2C2C2E);   // Lighter dark for inputs
  static const Color cardBackground = Color(0xFF1C1C1E); // Card background

  // Text Colors
  static const Color textPrimary = Color(0xFFFFFFFF);
  static const Color textSecondary = Color(0xFFAAB4C8);
  static const Color textHint = Color(0xFF64748B);
  static const Color textDisabled = Color(0xFF475569);

  // Status Colors
  static const Color statusPending = Color(0xFFF59E0B);
  static const Color statusConfirmed = Color(0xFF3B82F6);
  static const Color statusTransit = Color(0xFF3B82F6);
  static const Color statusDelivered = Color(0xFF22C55E);
  static const Color statusCancelled = Color(0xFFEF4444);

  // Legacy color aliases for compatibility
  static const Color primaryColor = primary;
  static const Color backgroundColor = background;
  static const Color successColor = success;
  static const Color warningColor = warning;
  static const Color errorColor = error;
  static const Color dividerColor = surfaceLight;

  // 🎨 Premium Gradients for Glassmorphism
  static const LinearGradient primaryGradient = LinearGradient(
    colors: [
      Color(0xFFF97316),
      Color(0xFFFFB067),
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );

  static const LinearGradient secondaryGradient = LinearGradient(
    colors: [
      Color(0xFF1C1C1E),
      Color(0xFF2C2C2E),
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );

  static const LinearGradient successGradient = LinearGradient(
    colors: [
      Color(0xFF22C55E),
      Color(0xFF10B981),
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );

  static const LinearGradient errorGradient = LinearGradient(
    colors: [
      Color(0xFFEF4444),
      Color(0xFFDC2626),
    ],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );

  // 🌟 Premium Shadows for Glassmorphism
  static const List<BoxShadow> glowShadow = [
    BoxShadow(
      color: primary,
      blurRadius: 25,
      spreadRadius: 1,
      offset: Offset(0, 10),
    ),
    BoxShadow(
      color: Color(0xFFF97316),
      blurRadius: 40,
      spreadRadius: -5,
      offset: Offset(0, 15),
    ),
  ];

  static const List<BoxShadow> softShadow = [
    BoxShadow(
      color: Colors.black26,
      blurRadius: 20,
      spreadRadius: 0,
      offset: Offset(0, 8),
    ),
  ];

  static const List<BoxShadow> strongShadow = [
    BoxShadow(
      color: Colors.black54,
      blurRadius: 30,
      spreadRadius: 2,
      offset: Offset(0, 15),
    ),
  ];

  // 🫧 Glassmorphism Decorations
  static BoxDecoration glassDecoration({double radius = 24}) => BoxDecoration(
    color: Colors.white.withOpacity(0.05),
    borderRadius: BorderRadius.all(Radius.circular(radius)),
    border: Border.all(
      color: Colors.white.withOpacity(0.08),
      width: 1,
    ),
    boxShadow: [
      BoxShadow(
        color: Colors.black.withOpacity(0.4),
        blurRadius: 30,
        spreadRadius: 0,
        offset: const Offset(0, 15),
      ),
    ],
  );

  static BoxDecoration glassDecorationLight({double radius = 20}) => BoxDecoration(
    color: Colors.white.withOpacity(0.1),
    borderRadius: BorderRadius.all(Radius.circular(radius)),
    border: Border.all(
      color: Colors.white.withOpacity(0.12),
      width: 1,
    ),
    boxShadow: [
      BoxShadow(
        color: Colors.black.withOpacity(0.3),
        blurRadius: 25,
        spreadRadius: 0,
        offset: const Offset(0, 10),
      ),
    ],
  );

  // 🎯 Design System Constants
  static const double borderRadiusSmall = 12;
  static const double borderRadiusMedium = 16;
  static const double borderRadiusLarge = 24;
  static const double borderRadiusXLarge = 32;

  static const double paddingSmall = 8;
  static const double paddingMedium = 16;
  static const double paddingLarge = 24;
  static const double paddingXLarge = 32;

  static const double spacingSmall = 8;
  static const double spacingMedium = 16;
  static const double spacingLarge = 24;
  static const double spacingXLarge = 32;

  // Shipment Status Colors
  static Color getStatusColor(String status) {
    switch (status.toLowerCase()) {
      case 'pending':
        return statusPending;
      case 'confirmed':
      case 'assigned':
        return statusConfirmed;
      case 'in_transit':
      case 'picked_up':
      case 'on_the_way':
        return statusTransit;
      case 'delivered':
      case 'completed':
        return statusDelivered;
      case 'cancelled':
        return statusCancelled;
      default:
        return textSecondary;
    }
  }

  static String getStatusLabel(String status) {
    switch (status.toLowerCase()) {
      case 'pending':
        return 'قيد الانتظار';
      case 'confirmed':
        return 'مؤكد';
      case 'assigned':
        return 'معين';
      case 'in_transit':
        return 'في الطريق';
      case 'picked_up':
        return 'تم الاستلام';
      case 'on_the_way':
        return 'في الطريق للتسليم';
      case 'delivered':
        return 'تم التسليم';
      case 'completed':
        return 'مكتمل';
      case 'cancelled':
        return 'ملغي';
      default:
        return status;
    }
  }

  // Premium Dark Theme with Glassmorphism
  static ThemeData get darkTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.dark,
      primaryColor: primary,
      scaffoldBackgroundColor: background,
      colorScheme: const ColorScheme.dark(
        primary: primary,
        secondary: accent,
        surface: surface,
        background: background,
        error: error,
        onPrimary: textPrimary,
        onSecondary: background,
        onSurface: textPrimary,
        onBackground: textPrimary,
        onError: textPrimary,
      ),
      
      // Premium Typography with Cairo for Arabic
      textTheme: TextTheme(
        displayLarge: GoogleFonts.cairo(
          fontSize: 32,
          fontWeight: FontWeight.bold,
          color: textPrimary,
        ),
        displayMedium: GoogleFonts.cairo(
          fontSize: 28,
          fontWeight: FontWeight.bold,
          color: textPrimary,
        ),
        headlineLarge: GoogleFonts.cairo(
          fontSize: 24,
          fontWeight: FontWeight.bold,
          color: textPrimary,
        ),
        headlineMedium: GoogleFonts.cairo(
          fontSize: 20,
          fontWeight: FontWeight.w600,
          color: textPrimary,
        ),
        titleLarge: GoogleFonts.cairo(
          fontSize: 18,
          fontWeight: FontWeight.w600,
          color: textPrimary,
        ),
        titleMedium: GoogleFonts.cairo(
          fontSize: 16,
          fontWeight: FontWeight.w600,
          color: textPrimary,
        ),
        bodyLarge: GoogleFonts.cairo(
          fontSize: 16,
          fontWeight: FontWeight.normal,
          color: textPrimary,
        ),
        bodyMedium: GoogleFonts.inter(
          fontSize: 14,
          fontWeight: FontWeight.normal,
          color: textSecondary,
        ),
        bodySmall: GoogleFonts.inter(
          fontSize: 12,
          fontWeight: FontWeight.normal,
          color: textHint,
        ),
        labelLarge: GoogleFonts.inter(
          fontSize: 14,
          fontWeight: FontWeight.w600,
          color: primary,
        ),
      ),

      // AppBar Theme
      appBarTheme: AppBarTheme(
        elevation: 0,
        centerTitle: true,
        backgroundColor: background,
        foregroundColor: textPrimary,
        titleTextStyle: GoogleFonts.inter(
          fontSize: 18,
          fontWeight: FontWeight.w600,
          color: textPrimary,
        ),
        systemOverlayStyle: SystemUiOverlayStyle.light,
      ),

      // Card Theme
      cardTheme: CardThemeData(
        elevation: 4,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        color: surface,
        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      ),

      // Button Themes
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: primary,
          foregroundColor: textPrimary,
          elevation: 0,
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          textStyle: GoogleFonts.inter(
            fontSize: 16,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),

      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: textPrimary,
          side: const BorderSide(color: primary, width: 2),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          textStyle: GoogleFonts.inter(
            fontSize: 16,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),

      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: primary,
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          textStyle: GoogleFonts.inter(
            fontSize: 14,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),

      // Input Decoration Theme
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: surfaceLight,
        contentPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide.none,
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide.none,
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: primary, width: 2),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: error, width: 2),
        ),
        hintStyle: GoogleFonts.inter(
          fontSize: 14,
          color: textHint,
        ),
        labelStyle: GoogleFonts.inter(
          fontSize: 14,
          color: textSecondary,
        ),
      ),

      // Bottom Navigation Bar Theme
      bottomNavigationBarTheme: BottomNavigationBarThemeData(
        backgroundColor: surface,
        selectedItemColor: primary,
        unselectedItemColor: textHint,
        type: BottomNavigationBarType.fixed,
        elevation: 8,
        selectedLabelStyle: GoogleFonts.inter(fontSize: 12),
        unselectedLabelStyle: GoogleFonts.inter(fontSize: 12),
      ),
    );
  }
}
