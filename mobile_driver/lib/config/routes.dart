import 'package:flutter/material.dart';
import '../screens/index.dart';

/// App Routes
/// Centralized route management
class AppRoutes {
  AppRoutes._(); // Private constructor

  // Route Names
  static const String splash = '/';
  static const String login = '/login';
  static const String register = '/register';
  static const String forgotPassword = '/forgot-password';
  static const String dashboard = '/dashboard';
  static const String trips = '/trips';
  static const String tripDetail = '/trips/:id';
  static const String activeTrip = '/trips/active';
  static const String tripHistory = '/trips/history';
  static const String availableRequests = '/requests';
  static const String earnings = '/earnings';
  static const String profile = '/profile';
  static const String editProfile = '/profile/edit';
  static const String documents = '/documents';
  static const String uploadDocument = '/documents/upload';
  static const String settings = '/settings';
  static const String notifications = '/notifications';
  static const String maintenance = '/maintenance';
  static const String oilChanges = '/maintenance/oil-changes';
  static const String parts = '/maintenance/parts';
  static const String support = '/support';
  static const String chat = '/support/chat';
  static const String sos = '/sos';
  static const String map = '/map';
  static const String training = '/training';
  static const String announcements = '/announcements';

  // Route Map
  static Map<String, WidgetBuilder> get routes => {
        splash: (context) => const SplashScreen(),
        login: (context) => const LoginScreen(),
        dashboard: (context) => const DriverDashboardScreen(),
        activeTrip: (context) => const ActiveTripScreen(),
        tripHistory: (context) => const TripHistoryScreen(),
        availableRequests: (context) => const AvailableRequestsScreen(),
        earnings: (context) => const EarningsScreen(),
        profile: (context) => const ProfileScreen(),
        documents: (context) => const DocumentUploadScreen(),
      };

  // Initial Route
  static const String initialRoute = splash;

  // Unknown Route
  static Route<dynamic> onUnknownRoute(RouteSettings settings) {
    return MaterialPageRoute(
      builder: (context) => const UnknownRouteScreen(),
    );
  }

  // Generate Route
  static Route<dynamic> onGenerateRoute(RouteSettings settings) {
    final uri = Uri.parse(settings.name ?? '');
    final path = uri.path;

    // Handle dynamic routes
    if (path.startsWith('/trips/')) {
      final id = path.split('/').last;
      return MaterialPageRoute(
        builder: (context) => ActiveTripScreen(tripId: id),
      );
    }

    // Default to routes map
    final builder = routes[path];
    if (builder != null) {
      return MaterialPageRoute(builder: builder);
    }

    return onUnknownRoute(settings);
  }

  // Navigation Helper Methods
  static void goToDashboard(BuildContext context) {
    Navigator.of(context).pushReplacementNamed(dashboard);
  }

  static void goToLogin(BuildContext context) {
    Navigator.of(context).pushReplacementNamed(login);
  }

  static void goToActiveTrip(BuildContext context, {String? tripId}) {
    if (tripId != null) {
      Navigator.of(context).pushNamed('/trips/$tripId');
    } else {
      Navigator.of(context).pushNamed(activeTrip);
    }
  }

  static void goBack(BuildContext context) {
    Navigator.of(context).pop();
  }

  static void goToTripHistory(BuildContext context) {
    Navigator.of(context).pushNamed(tripHistory);
  }

  static void goToAvailableRequests(BuildContext context) {
    Navigator.of(context).pushNamed(availableRequests);
  }

  static void goToEarnings(BuildContext context) {
    Navigator.of(context).pushNamed(earnings);
  }

  static void goToProfile(BuildContext context) {
    Navigator.of(context).pushNamed(profile);
  }

  static void goToDocuments(BuildContext context) {
    Navigator.of(context).pushNamed(documents);
  }

  static void goToSOS(BuildContext context) {
    Navigator.of(context).pushNamed(sos);
  }
}

/// Unknown Route Screen
class UnknownRouteScreen extends StatelessWidget {
  const UnknownRouteScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.error_outline,
              size: 64,
              color: Colors.red,
            ),
            const SizedBox(height: 16),
            const Text(
              'الصفحة غير موجودة',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            const Text(
              'الصفحة التي تبحث عنها غير موجودة',
              style: TextStyle(
                fontSize: 16,
                color: Colors.grey,
              ),
            ),
            const SizedBox(height: 24),
            ElevatedButton(
              onPressed: () {
                Navigator.of(context).pushReplacementNamed(AppRoutes.dashboard);
              },
              child: const Text('العودة للرئيسية'),
            ),
          ],
        ),
      ),
    );
  }
}
