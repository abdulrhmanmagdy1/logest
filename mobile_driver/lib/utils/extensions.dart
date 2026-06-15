import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

/// String Extensions
extension StringExtensions on String {
  /// Check if string is a valid email
  bool get isValidEmail {
    final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
    return emailRegex.hasMatch(this);
  }

  /// Check if string is a valid phone number (Saudi)
  bool get isValidPhone {
    final phoneRegex = RegExp(r'^(05|5)\d{8}$');
    return phoneRegex.hasMatch(this);
  }

  /// Capitalize first letter
  String get capitalize {
    if (isEmpty) return this;
    return '${this[0].toUpperCase()}${substring(1)}';
  }

  /// Truncate string with ellipsis
  String truncate(int maxLength) {
    if (length <= maxLength) return this;
    return '${substring(0, maxLength)}...';
  }

  /// Remove all whitespace
  String get removeWhitespace => replaceAll(RegExp(r'\s+'), '');

  /// Convert to title case
  String get toTitleCase {
    if (isEmpty) return this;
    return split(' ').map((word) => word.capitalize).join(' ');
  }
}

/// DateTime Extensions
extension DateTimeExtensions on DateTime {
  /// Format to readable date (Arabic)
  String get formattedDate {
    return DateFormat('dd MMMM yyyy', 'ar').format(this);
  }

  /// Format to readable time (Arabic)
  String get formattedTime {
    return DateFormat('hh:mm a', 'ar').format(this);
  }

  /// Format to readable date and time
  String get formattedDateTime {
    return DateFormat('dd/MM/yyyy hh:mm a', 'ar').format(this);
  }

  /// Check if date is today
  bool get isToday {
    final now = DateTime.now();
    return year == now.year && month == now.month && day == now.day;
  }

  /// Check if date is yesterday
  bool get isYesterday {
    final yesterday = DateTime.now().subtract(const Duration(days: 1));
    return year == yesterday.year && month == yesterday.month && day == yesterday.day;
  }

  /// Get relative time (e.g., "منذ 2 ساعة")
  String get relativeTime {
    final now = DateTime.now();
    final difference = now.difference(this);

    if (difference.inDays > 365) {
      return 'منذ ${difference.inDays ~/ 365} سنة';
    } else if (difference.inDays > 30) {
      return 'منذ ${difference.inDays ~/ 30} شهر';
    } else if (difference.inDays > 0) {
      return 'منذ ${difference.inDays} يوم';
    } else if (difference.inHours > 0) {
      return 'منذ ${difference.inHours} ساعة';
    } else if (difference.inMinutes > 0) {
      return 'منذ ${difference.inMinutes} دقيقة';
    } else {
      return 'الآن';
    }
  }
}

/// Double Extensions
extension DoubleExtensions on double {
  /// Format as currency (SAR)
  String get toCurrency {
    return '${toStringAsFixed(2)} ر.س';
  }

  /// Format as distance (km)
  String get toDistance {
    if (this < 1) {
      return '${(this * 1000).toStringAsFixed(0)} م';
    }
    return '${toStringAsFixed(1)} كم';
  }

  /// Format as percentage
  String get toPercentage {
    return '${(this * 100).toStringAsFixed(1)}%';
  }
}

/// BuildContext Extensions
extension BuildContextExtensions on BuildContext {
  /// Get screen width
  double get screenWidth => MediaQuery.of(this).size.width;

  /// Get screen height
  double get screenHeight => MediaQuery.of(this).size.height;

  /// Check if screen is small
  bool get isSmallScreen => screenWidth < 600;

  /// Check if screen is tablet
  bool get isTablet => screenWidth >= 600 && screenWidth < 1024;

  /// Check if screen is desktop
  bool get isDesktop => screenWidth >= 1024;

  /// Show snackbar
  void showSnackBar(String message, {bool isError = false}) {
    ScaffoldMessenger.of(this).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red : Colors.green,
        behavior: SnackBarBehavior.floating,
      ),
    );
  }

  /// Show loading dialog
  void showLoadingDialog() {
    showDialog(
      context: this,
      barrierDismissible: false,
      builder: (context) => const Center(
        child: CircularProgressIndicator(),
      ),
    );
  }

  /// Hide loading dialog
  void hideLoadingDialog() {
    Navigator.of(this).pop();
  }
}

/// List Extensions
extension ListExtensions<T> on List<T> {
  /// Get first element or null
  T? get firstOrNull => isEmpty ? null : first;

  /// Get last element or null
  T? get lastOrNull => isEmpty ? null : last;

  /// Split list into chunks
  List<List<T>> chunk(int size) {
    List<List<T>> chunks = [];
    for (var i = 0; i < length; i += size) {
      chunks.add(sublist(i, i + size > length ? length : i + size));
    }
    return chunks;
  }
}
