/// API Configuration
/// Centralized API configuration and endpoints
class ApiConfig {
  ApiConfig._(); // Private constructor

  // Base URLs
  static const String baseUrl = 'https://api.edham.com/v1';
  static const String baseUrlDev = 'https://dev-api.edham.com/v1';
  static const String baseUrlStaging = 'https://staging-api.edham.com/v1';

  // Environment
  static const String environment = 'development'; // development, staging, production

  // Get current base URL based on environment
  static String get currentBaseUrl {
    switch (environment) {
      case 'production':
        return baseUrl;
      case 'staging':
        return baseUrlStaging;
      case 'development':
      default:
        return baseUrlDev;
    }
  }

  // API Timeout
  static const Duration connectTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
  static const Duration sendTimeout = Duration(seconds: 30);

  // API Endpoints
  static const String login = '/auth/login';
  static const String register = '/auth/register';
  static const String logout = '/auth/logout';
  static const String refreshToken = '/auth/refresh';
  static const String forgotPassword = '/auth/forgot-password';
  static const String resetPassword = '/auth/reset-password';
  static const String verifyEmail = '/auth/verify-email';
  static const String resendVerification = '/auth/resend-verification';

  // User Endpoints
  static const String getProfile = '/users/profile';
  static const String updateProfile = '/users/profile';
  static const String changePassword = '/users/change-password';
  static const String updateAvatar = '/users/avatar';
  static const String deleteAccount = '/users/account';

  // Trip Endpoints
  static const String getTrips = '/trips';
  static const String getTrip = '/trips/{id}';
  static const String createTrip = '/trips';
  static const String updateTrip = '/trips/{id}';
  static const String deleteTrip = '/trips/{id}';
  static const String acceptTrip = '/trips/{id}/accept';
  static const String startTrip = '/trips/{id}/start';
  static const String completeTrip = '/trips/{id}/complete';
  static const String cancelTrip = '/trips/{id}/cancel';
  static const String getTripHistory = '/trips/history';
  static const String updateTripLocation = '/trips/{id}/location';
  static const String rateTrip = '/trips/{id}/rate';

  // Driver Endpoints
  static const String getDriverStats = '/drivers/stats';
  static const String getDriverEarnings = '/drivers/earnings';
  static const String updateDriverStatus = '/drivers/status';
  static const String getDriverDocuments = '/drivers/documents';
  static const String uploadDocument = '/drivers/documents';
  static const String getDriverRating = '/drivers/rating';
  static const String getDriverReviews = '/drivers/reviews';

  // Vehicle Endpoints
  static const String getVehicles = '/vehicles';
  static const String getVehicle = '/vehicles/{id}';
  static const String createVehicle = '/vehicles';
  static const String updateVehicle = '/vehicles/{id}';
  static const String deleteVehicle = '/vehicles/{id}';
  static const String updateVehicleLocation = '/vehicles/{id}/location';
  static const String updateVehicleStatus = '/vehicles/{id}/status';

  // Maintenance Endpoints
  static const String getMaintenanceRecords = '/maintenance';
  static const String createMaintenanceRecord = '/maintenance';
  static const String getMaintenanceRecord = '/maintenance/{id}';
  static const String updateMaintenanceRecord = '/maintenance/{id}';
  static const String deleteMaintenanceRecord = '/maintenance/{id}';
  static const String getOilChanges = '/maintenance/oil-changes';
  static const String recordOilChange = '/maintenance/oil-change';
  static const String getParts = '/maintenance/parts';
  static const String updatePartStock = '/maintenance/parts/{id}/stock';

  // Notification Endpoints
  static const String getNotifications = '/notifications';
  static const String markNotificationRead = '/notifications/{id}/read';
  static const String markAllNotificationsRead = '/notifications/read-all';
  static const String deleteNotification = '/notifications/{id}';
  static const String updateFcmToken = '/notifications/fcm-token';

  // Settings Endpoints
  static const String getSettings = '/settings';
  static const String updateSettings = '/settings';
  static const String getPrivacySettings = '/settings/privacy';
  static const String updatePrivacySettings = '/settings/privacy';

  // Support Endpoints
  static const String getSupportTickets = '/support/tickets';
  static const String createSupportTicket = '/support/tickets';
  static const String getSupportTicket = '/support/tickets/{id}';
  static const String addSupportMessage = '/support/tickets/{id}/messages';
  static const String closeSupportTicket = '/support/tickets/{id}/close';

  // SOS Endpoints
  static const String sendSOS = '/sos';
  static const String getEmergencyContacts = '/sos/contacts';
  static const String addEmergencyContact = '/sos/contacts';
  static const String updateEmergencyContact = '/sos/contacts/{id}';
  static const String deleteEmergencyContact = '/sos/contacts/{id}';

  // File Upload Endpoints
  static const String uploadFile = '/upload';
  static const String uploadImage = '/upload/image';
  static const String uploadDocument = '/upload/document';
  static const String deleteFile = '/upload/{id}';

  // Analytics Endpoints
  static const String getDashboardStats = '/analytics/dashboard';
  static const String getPerformanceReport = '/analytics/performance';
  static const String getEarningsReport = '/analytics/earnings';
  static const String exportReport = '/analytics/export';

  // Help replace path parameters
  static String withId(String endpoint, String id) {
    return endpoint.replaceAll('{id}', id);
  }
}
