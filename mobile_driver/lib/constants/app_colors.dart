import 'package:flutter/material.dart';

/// App Colors
/// Centralized color management for the entire app
class AppColors {
  AppColors._(); // Private constructor to prevent instantiation

  // Primary Colors
  static const Color primary = Color(0xFF1E3A8A);
  static const Color primaryDark = Color(0xFF1E40AF);
  static const Color primaryLight = Color(0xFF3B82F6);

  // Secondary Colors
  static const Color secondary = Color(0xFF10B981);
  static const Color secondaryDark = Color(0xFF059669);
  static const Color secondaryLight = Color(0xFF34D399);

  // Accent Colors
  static const Color accent = Color(0xFFF59E0B);
  static const Color accentDark = Color(0xFFD97706);
  static const Color accentLight = Color(0xFFFBBF24);

  // Semantic Colors
  static const Color success = Color(0xFF10B981);
  static const Color warning = Color(0xFFF59E0B);
  static const Color error = Color(0xFFEF4444);
  static const Color info = Color(0xFF3B82F6);

  // Background Colors
  static const Color background = Color(0xFF0F172A);
  static const Color surface = Color(0xFF1E293B);
  static const Color surfaceLight = Color(0xFF334155);
  static const Color card = Color(0xFF1E293B);

  // Text Colors
  static const Color textPrimary = Colors.white;
  static const Color textSecondary = Color(0xFF94A3B8);
  static const Color textMuted = Color(0xFF64748B);
  static const Color textInverse = Color(0xFF0F172A);

  // Border Colors
  static const Color border = Color(0xFF334155);
  static const Color borderLight = Color(0xFF475569);
  static const Color borderDark = Color(0xFF1E293B);

  // Overlay Colors
  static Color overlay = Colors.black.withOpacity(0.5);
  static Color overlayLight = Colors.black.withOpacity(0.3);
  static Color overlayDark = Colors.black.withOpacity(0.7);

  // Gradient Colors
  static const List<Color> primaryGradient = [
    Color(0xFF1E3A8A),
    Color(0xFF10B981),
  ];

  static const List<Color> backgroundGradient = [
    Color(0xFF0F172A),
    Color(0xFF1E293B),
    Color(0xFF1E3A8A),
  ];
}
