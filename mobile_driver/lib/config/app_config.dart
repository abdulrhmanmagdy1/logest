/// App Configuration
/// Application-wide configuration settings
class AppConfig {
  AppConfig._(); // Private constructor

  // App Info
  static const String appName = 'إدهام';
  static const String appNameEn = 'EDHAM';
  static const String appTagline = 'نقل لوجستي احترافي';
  static const String appVersion = '1.0.0';
  static const String appBuildNumber = '1';

  // Company Info
  static const String companyName = 'شركة إدهام للنقل المبرد';
  static const String companyEmail = 'support@edham.com';
  static const String companyPhone = '920012345';
  static const String companyWebsite = 'https://edham.com';

  // Support
  static const String supportEmail = 'support@edham.com';
  static const String supportPhone = '920012345';
  static const String privacyPolicyUrl = 'https://edham.com/privacy';
  static const String termsOfServiceUrl = 'https://edham.com/terms';
  static const String helpCenterUrl = 'https://edham.com/help';

  // Features Flags
  static const bool enableNotifications = true;
  static const bool enableOfflineMode = true;
  static const bool enableDarkMode = true;
  static const bool enableBiometricAuth = false;
  static const bool enableLocationTracking = true;
  static const bool enableCrashReporting = true;
  static const bool enableAnalytics = true;
  static const bool enableRateApp = true;
  static const bool enableShareApp = true;

  // Location Settings
  static const double defaultZoomLevel = 15.0;
  static const double minZoomLevel = 5.0;
  static const double maxZoomLevel = 20.0;
  static const int locationUpdateIntervalSeconds = 10;
  static const int locationUpdateDistanceMeters = 50;
  static const int geofencingRadiusMeters = 500;

  // Pagination
  static const int defaultPageSize = 20;
  static const int maxPageSize = 100;

  // Timeouts
  static const int splashScreenDurationSeconds = 3;
  static const int sessionTimeoutMinutes = 30;
  static const int autoLogoutMinutes = 60;

  // Cache Settings
  static const int cacheMaxAgeDays = 7;
  static const int maxCacheSizeMB = 100;

  // File Upload
  static const int maxImageSizeMB = 5;
  static const int maxFileSizeMB = 10;
  static const List<String> allowedImageTypes = ['jpg', 'jpeg', 'png'];
  static const List<String> allowedFileTypes = ['pdf', 'jpg', 'jpeg', 'png'];

  // Map Settings
  static const String mapStyle = 'standard';
  static const bool showTraffic = true;
  static const bool showCompass = true;
  static const bool showMyLocationButton = true;

  // Notification Settings
  static const String notificationChannelId = 'edham_driver_channel';
  static const String notificationChannelName = 'EDHAM Driver';
  static const String notificationChannelDescription = 'Notifications for EDHAM Driver App';

  // Social Links
  static const String twitterUrl = 'https://twitter.com/edham';
  static const String instagramUrl = 'https://instagram.com/edham';
  static const String linkedinUrl = 'https://linkedin.com/company/edham';

  // App Store Links
  static const String appStoreUrl = 'https://apps.apple.com/app/edham/id123456789';
  static const String playStoreUrl = 'https://play.google.com/store/apps/details?id=com.edham.driver';

  // Is Debug Mode
  static bool get isDebugMode {
    bool inDebugMode = false;
    assert(inDebugMode = true);
    return inDebugMode;
  }

  // Is Production
  static bool get isProduction => !isDebugMode;

  // Get API Base URL based on environment
  static String get apiBaseUrl {
    if (isDebugMode) {
      return 'https://dev-api.edham.com/v1';
    }
    return 'https://api.edham.com/v1';
  }
}
