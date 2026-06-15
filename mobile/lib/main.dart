import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:google_fonts/google_fonts.dart';

import 'screens/splash_screen.dart';
import 'screens/login_screen.dart';
import 'screens/driver_dashboard_screen.dart';
import 'screens/active_trip_screen.dart';
import 'screens/available_requests_screen.dart';
import 'screens/trip_history_screen.dart';
import 'screens/profile_screen.dart';
import 'screens/onboarding/onboarding_controller.dart';
import 'screens/onboarding/smart_logistics_screen.dart';
import 'screens/onboarding/shipment_tracking_screen.dart';
import 'screens/onboarding/cold_chain_screen.dart';
import 'screens/sidebar_dashboard_screen.dart';
import 'screens/supervisor_dashboard_screen.dart';
import 'services/auth_service.dart';
import 'services/driver_trip_service.dart';
import 'services/location_service.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  
  // Set preferred orientations
  SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]);
  
  // Set system UI overlay style with premium dark theme
  SystemChrome.setSystemUIOverlayStyle(
    const SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      statusBarIconBrightness: Brightness.light,
      systemNavigationBarColor: Color(0xFF003D5C),
      systemNavigationBarIconBrightness: Brightness.light,
    ),
  );
  
  runApp(const EdhamApp());
}

class EdhamApp extends StatelessWidget {
  const EdhamApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider<AuthService>(
      create: (_) => AuthService(),
      child: MaterialApp(
        title: 'إدهام للنقل المبرد - Premium Fleet Management',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          useMaterial3: true,
          brightness: Brightness.dark,
          primaryColor: const Color(0xFF003D5C),
          scaffoldBackgroundColor: const Color(0xFF003D5C),
          colorScheme: const ColorScheme.dark(
            primary: Color(0xFF0099D8),
            secondary: Color(0xFFD4AF37),
            surface: Color(0xFF005A7F),
            background: Color(0xFF003D5C),
            error: Color(0xFFE74C3C),
            onPrimary: Colors.white,
            onSecondary: Colors.white,
            onSurface: Colors.white,
            onBackground: Colors.white,
            onError: Colors.white,
          ),
          
          // Premium Typography with Google Fonts
          textTheme: GoogleFonts.cairoTextTheme(
            const TextTheme(
              displayLarge: TextStyle(
                color: Colors.white,
                fontSize: 32,
                fontWeight: FontWeight.bold,
              ),
              displayMedium: TextStyle(
                color: Colors.white,
                fontSize: 28,
                fontWeight: FontWeight.bold,
              ),
              headlineLarge: TextStyle(
                color: Colors.white,
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
              headlineMedium: TextStyle(
                color: Colors.white,
                fontSize: 20,
                fontWeight: FontWeight.w600,
              ),
              bodyLarge: TextStyle(
                color: Colors.white,
                fontSize: 16,
              ),
              bodyMedium: TextStyle(
                color: Colors.white70,
                fontSize: 14,
              ),
              bodySmall: TextStyle(
                color: Colors.white54,
                fontSize: 12,
              ),
            ),
          ),
          
          // Premium App Bar Theme
          appBarTheme: const AppBarTheme(
            backgroundColor: Color(0xFF003D5C),
            foregroundColor: Colors.white,
            elevation: 0,
            centerTitle: true,
            titleTextStyle: TextStyle(
              color: Colors.white,
              fontSize: 20,
              fontWeight: FontWeight.w600,
            ),
          ),
          
          // Premium Card Theme with Glassmorphism
          cardTheme: CardThemeData(
            color: const Color(0xFF005A7F).withOpacity(0.8),
            elevation: 8,
            shadowColor: const Color(0xFF0099D8).withOpacity(0.3),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
          ),
          
          // Premium Input Decoration Theme
          inputDecorationTheme: InputDecorationTheme(
            filled: true,
            fillColor: const Color(0xFF003D5C).withOpacity(0.3),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFF0099D8), width: 2),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: BorderSide(color: const Color(0xFF0099D8).withOpacity(0.5), width: 1),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: Color(0xFFD4AF37), width: 2),
            ),
            labelStyle: const TextStyle(color: Color(0xFF0099D8)),
            hintStyle: TextStyle(color: Colors.white.withOpacity(0.4)),
          ),
          
          // Premium Button Theme
          elevatedButtonTheme: ElevatedButtonThemeData(
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF0099D8),
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
              elevation: 4,
              shadowColor: const Color(0xFF0099D8).withOpacity(0.4),
            ),
          ),
          
          // Premium Outlined Button Theme
          outlinedButtonTheme: OutlinedButtonThemeData(
            style: OutlinedButton.styleFrom(
              foregroundColor: const Color(0xFFD4AF37),
              side: const BorderSide(color: Color(0xFFD4AF37), width: 2),
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
            ),
          ),
        ),
        home: const SplashScreen(),
        builder: (context, child) {
          return ScrollConfiguration(
            behavior: const ScrollBehavior().copyWith(
              physics: const BouncingScrollPhysics(
                parent: AlwaysScrollableScrollPhysics(),
              ),
            ),
            child: child!.animate().fadeIn(),
          );
        },
        routes: {
          '/onboarding': (context) => const OnboardingController(),
          '/onboarding/smart-logistics': (context) => const SmartLogisticsScreen(),
          '/onboarding/tracking': (context) => const ShipmentTrackingScreen(),
          '/onboarding/cold-chain': (context) => const ColdChainScreen(),
          '/login': (context) => const LoginScreen(),
          '/dashboard': (context) => const DashboardScreen(),
          '/sidebar-dashboard': (context) => const SidebarDashboardScreen(),
          '/supervisor-dashboard': (context) => const SupervisorDashboardScreen(),
          '/shipments': (context) => const ShipmentsScreen(),
          '/maintenance': (context) => const MaintenanceScreen(),
          '/reports': (context) => const ReportsScreen(),
          '/profile': (context) => const ProfileScreen(),
          '/notifications': (context) => const NotificationsScreen(),
        },
      ),
    );
  }
}
