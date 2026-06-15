import 'package:intl/intl.dart';

/// Formatters
/// Utility functions for formatting various data types
class Formatters {
  Formatters._(); // Private constructor

  // Currency Formatter
  static String formatCurrency(double amount, {String symbol = 'ر.س', int decimalDigits = 2}) {
    final formatter = NumberFormat.currency(
      symbol: symbol,
      decimalDigits: decimalDigits,
      locale: 'ar_SA',
    );
    return formatter.format(amount);
  }

  // Number Formatter
  static String formatNumber(num number, {int decimalDigits = 0}) {
    final formatter = NumberFormat.decimalPattern('ar_SA');
    formatter.minimumFractionDigits = decimalDigits;
    formatter.maximumFractionDigits = decimalDigits;
    return formatter.format(number);
  }

  // Percentage Formatter
  static String formatPercentage(double value, {int decimalDigits = 1}) {
    final formatter = NumberFormat.percentPattern('ar_SA');
    formatter.minimumFractionDigits = decimalDigits;
    formatter.maximumFractionDigits = decimalDigits;
    return formatter.format(value / 100);
  }

  // Date Formatters
  static String formatDate(DateTime date, {String pattern = 'dd/MM/yyyy'}) {
    final formatter = DateFormat(pattern, 'ar');
    return formatter.format(date);
  }

  static String formatDateFull(DateTime date) {
    final formatter = DateFormat('EEEE، d MMMM yyyy', 'ar');
    return formatter.format(date);
  }

  static String formatDateShort(DateTime date) {
    final formatter = DateFormat('dd MMM', 'ar');
    return formatter.format(date);
  }

  static String formatTime(DateTime time, {bool use24HourFormat = false}) {
    final pattern = use24HourFormat ? 'HH:mm' : 'hh:mm a';
    final formatter = DateFormat(pattern, 'ar');
    return formatter.format(time);
  }

  static String formatDateTime(DateTime dateTime) {
    final formatter = DateFormat('dd/MM/yyyy hh:mm a', 'ar');
    return formatter.format(dateTime);
  }

  // Distance Formatter
  static String formatDistance(double meters, {bool useImperial = false}) {
    if (useImperial) {
      final miles = meters / 1609.344;
      if (miles < 0.1) {
        final feet = meters * 3.28084;
        return '${feet.toStringAsFixed(0)} قدم';
      }
      return '${miles.toStringAsFixed(1)} ميل';
    } else {
      if (meters < 1000) {
        return '${meters.toStringAsFixed(0)} م';
      }
      final km = meters / 1000;
      if (km < 10) {
        return '${km.toStringAsFixed(1)} كم';
      }
      return '${km.toStringAsFixed(0)} كم';
    }
  }

  // Duration Formatter
  static String formatDuration(Duration duration, {bool short = false}) {
    final hours = duration.inHours;
    final minutes = duration.inMinutes % 60;
    final seconds = duration.inSeconds % 60;

    if (short) {
      if (hours > 0) {
        return '${hours}س ${minutes}د';
      }
      if (minutes > 0) {
        return '${minutes}د';
      }
      return '${seconds}ث';
    }

    final parts = <String>[];
    if (hours > 0) {
      parts.add('$hours ${hours == 1 ? 'ساعة' : 'ساعات'}');
    }
    if (minutes > 0) {
      parts.add('$minutes ${minutes == 1 ? 'دقيقة' : 'دقائق'}');
    }
    if (seconds > 0 && hours == 0) {
      parts.add('$seconds ${seconds == 1 ? 'ثانية' : 'ثواني'}');
    }

    return parts.join(' و ');
  }

  // Phone Number Formatter
  static String formatPhoneNumber(String phone) {
    // Remove all non-digit characters
    final digits = phone.replaceAll(RegExp(r'\D'), '');
    
    // Saudi phone format: 05XX XXX XXX
    if (digits.length == 10 && digits.startsWith('05')) {
      return '${digits.substring(0, 4)} ${digits.substring(4, 7)} ${digits.substring(7)}';
    }
    
    // International format: +966 5X XXX XXXX
    if (digits.length == 12 && digits.startsWith('9665')) {
      return '+${digits.substring(0, 3)} ${digits.substring(3, 4)} ${digits.substring(4, 7)} ${digits.substring(7)}';
    }
    
    return phone;
  }

  // File Size Formatter
  static String formatFileSize(int bytes, {int decimals = 1}) {
    const suffixes = ['بايت', 'كيلوبايت', 'ميغابايت', 'جيغابايت', 'تيرابايت'];
    
    if (bytes == 0) return '0 بايت';
    
    final i = (bytes.bitLength ~/ 10);
    final size = bytes / (1 << (i * 10));
    
    return '${size.toStringAsFixed(decimals)} ${suffixes[i]}';
  }

  // Speed Formatter
  static String formatSpeed(double kmPerHour) {
    return '${kmPerHour.toStringAsFixed(1)} كم/س';
  }

  // Weight Formatter
  static String formatWeight(double kg, {int decimalDigits = 1}) {
    if (kg < 1) {
      return '${(kg * 1000).toStringAsFixed(0)} غ';
    }
    if (kg >= 1000) {
      return '${(kg / 1000).toStringAsFixed(decimalDigits)} طن';
    }
    return '${kg.toStringAsFixed(decimalDigits)} كجم';
  }

  // Temperature Formatter
  static String formatTemperature(double celsius, {bool useFahrenheit = false}) {
    if (useFahrenheit) {
      final fahrenheit = (celsius * 9 / 5) + 32;
      return '${fahrenheit.toStringAsFixed(1)}°ف';
    }
    return '${celsius.toStringAsFixed(1)}°م';
  }

  // Coordinates Formatter
  static String formatCoordinates(double latitude, double longitude) {
    final latDir = latitude >= 0 ? 'N' : 'S';
    final longDir = longitude >= 0 ? 'E' : 'W';
    return '${latitude.abs().toStringAsFixed(6)}° $latDir, ${longitude.abs().toStringAsFixed(6)}° $longDir';
  }

  // Plate Number Formatter (Saudi)
  static String formatPlateNumber(String plate) {
    // Handle formats like "1234 ABC" or "12345 A"
    final parts = plate.trim().split(RegExp(r'\s+'));
    if (parts.length == 2) {
      return '${parts[0]} ${parts[1]}';
    }
    return plate;
  }

  // Name Formatter
  static String formatName(String name, {bool capitalize = true}) {
    if (name.isEmpty) return name;
    
    if (capitalize) {
      return name.split(' ').map((word) {
        if (word.isEmpty) return word;
        return word[0].toUpperCase() + word.substring(1).toLowerCase();
      }).join(' ');
    }
    
    return name;
  }

  // Email Masking
  static String maskEmail(String email) {
    if (!email.contains('@')) return email;
    
    final parts = email.split('@');
    final localPart = parts[0];
    final domain = parts[1];
    
    String maskedLocal;
    if (localPart.length <= 2) {
      maskedLocal = localPart;
    } else {
      maskedLocal = '${localPart[0]}***${localPart[localPart.length - 1]}';
    }
    
    return '$maskedLocal@$domain';
  }

  // Phone Masking
  static String maskPhone(String phone) {
    final digits = phone.replaceAll(RegExp(r'\D'), '');
    
    if (digits.length < 4) return phone;
    
    final visibleStart = digits.substring(0, 2);
    final visibleEnd = digits.substring(digits.length - 2);
    final masked = '*' * (digits.length - 4);
    
    return '$visibleStart$masked$visibleEnd';
  }

  // Truncate Text
  static String truncateText(String text, int maxLength, {String suffix = '...'}) {
    if (text.length <= maxLength) return text;
    return '${text.substring(0, maxLength - suffix.length)}$suffix';
  }

  // Format Large Numbers (K, M, B)
  static String formatCompactNumber(num number) {
    final formatter = NumberFormat.compact(locale: 'ar');
    return formatter.format(number);
  }
}
