// Flutter imports
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:google_fonts/google_fonts.dart';

// Screens
import 'screens/index.dart';

// Services
import 'services/index.dart';

// Constants
import 'constants/index.dart';

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
      systemNavigationBarColor: Color(0xFF1A1A2E),
      systemNavigationBarIconBrightness: Brightness.light,
    ),
  );
  
  runApp(const EdhamDriverApp());
}

class EdhamDriverApp extends StatelessWidget {
  const EdhamDriverApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        // Core Services
        ChangeNotifierProvider<AuthService>(create: (_) => AuthService()),
        ChangeNotifierProvider<TripService>(create: (_) => TripService()),
        ChangeNotifierProvider<LocationService>(create: (_) => LocationService()),
        ChangeNotifierProvider<UploadService>(create: (_) => UploadService()),
        
        // Advanced Services
        ChangeNotifierProvider<AdvancedTrackingService>(create: (_) => AdvancedTrackingService()),
        ChangeNotifierProvider<NotificationService>(create: (_) => NotificationService()),
        ChangeNotifierProvider<RoleBasedAccessService>(create: (_) => RoleBasedAccessService()),
        ChangeNotifierProvider<AnalyticsService>(create: (_) => AnalyticsService()),
        ChangeNotifierProvider<CRMService>(create: (_) => CRMService()),
        ChangeNotifierProvider<SchedulingService>(create: (_) => SchedulingService()),
        ChangeNotifierProvider<LocalizationService>(create: (_) => LocalizationService()),
        ChangeNotifierProvider<OfflineService>(create: (_) => OfflineService()),
        ChangeNotifierProvider<SecurityService>(create: (_) => SecurityService()),
        ChangeNotifierProvider<ContractsService>(create: (_) => ContractsService()),
        ChangeNotifierProvider<FleetOptimizationService>(create: (_) => FleetOptimizationService()),
        ChangeNotifierProvider<IntegrationService>(create: (_) => IntegrationService()),
        ChangeNotifierProvider<AdminPanelService>(create: (_) => AdminPanelService()),
        
        // Driver Professional Services
        ChangeNotifierProvider<DriverRatingService>(create: (_) => DriverRatingService()),
        ChangeNotifierProvider<RewardsService>(create: (_) => RewardsService()),
        ChangeNotifierProvider<ChatSupportService>(create: (_) => ChatSupportService()),
        ChangeNotifierProvider<SOSService>(create: (_) => SOSService()),
        ChangeNotifierProvider<LocationSharingService>(create: (_) => LocationSharingService()),
        ChangeNotifierProvider<AnnouncementsService>(create: (_) => AnnouncementsService()),
        ChangeNotifierProvider<RestStopsService>(create: (_) => RestStopsService()),
        ChangeNotifierProvider<DailyReportService>(create: (_) => DailyReportService()),
        ChangeNotifierProvider<RemindersService>(create: (_) => RemindersService()),
        ChangeNotifierProvider<DocumentsManagementService>(create: (_) => DocumentsManagementService()),
        ChangeNotifierProvider<TrainingService>(create: (_) => TrainingService()),
        ChangeNotifierProvider<PostTripSurveyService>(create: (_) => PostTripSurveyService()),
        
        // Maintenance Services
        ChangeNotifierProvider<MaintenanceManagementService>(create: (_) => MaintenanceManagementService()),
        ChangeNotifierProvider<OilChangeService>(create: (_) => OilChangeService()),
        ChangeNotifierProvider<PartsManagementService>(create: (_) => PartsManagementService()),
        ChangeNotifierProvider<BreakdownPreventionService>(create: (_) => BreakdownPreventionService()),
      ],
      child: MaterialApp(
        title: '${AppStrings.appName} - ${AppStrings.appSubtitle} - Premium Driver Portal',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          useMaterial3: true,
          brightness: Brightness.dark,
          primaryColor: AppColors.primary,
          scaffoldBackgroundColor: AppColors.background,
          colorScheme: ColorScheme.fromSeed(
            seedColor: AppColors.primary,
            primary: AppColors.primary,
            secondary: AppColors.secondary,
            tertiary: AppColors.accent,
            error: AppColors.error,
            brightness: Brightness.dark,
          ),
          
          // Premium Typography with Google Fonts
          textTheme: GoogleFonts.cairoTextTheme(
            const TextTheme(
              displayLarge: TextStyle(
                fontSize: 32,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
              displayMedium: TextStyle(
                fontSize: 28,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
              headlineLarge: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
              headlineMedium: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.w600,
                color: Colors.white,
              ),
              bodyLarge: TextStyle(
                fontSize: 16,
                color: Colors.white,
              ),
              bodyMedium: TextStyle(
                fontSize: 14,
                color: Colors.white70,
              ),
              bodySmall: TextStyle(
                fontSize: 12,
                color: Colors.white54,
              ),
            ),
          ),
          
          // Premium App Bar Theme
          appBarTheme: const AppBarTheme(
            backgroundColor: AppColors.primary,
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
          cardTheme: CardTheme(
            color: AppColors.card.withOpacity(0.9),
            elevation: 8,
            shadowColor: AppColors.primary.withOpacity(0.3),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
          ),
          
          // Premium Button Theme
          elevatedButtonTheme: ElevatedButtonThemeData(
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.primary,
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
              elevation: 4,
              shadowColor: AppColors.primary.withOpacity(0.4),
            ),
          ),
          
          // Premium Outlined Button Theme
          outlinedButtonTheme: OutlinedButtonThemeData(
            style: OutlinedButton.styleFrom(
              foregroundColor: AppColors.secondary,
              side: BorderSide(color: AppColors.secondary, width: 2),
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
            ),
          ),
          
          // Premium Input Decoration Theme
          inputDecorationTheme: InputDecorationTheme(
            filled: true,
            fillColor: AppColors.surface.withOpacity(0.3),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: BorderSide(color: AppColors.primary, width: 2),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: BorderSide(color: AppColors.primary.withOpacity(0.5), width: 1),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: BorderSide(color: AppColors.secondary, width: 2),
            ),
            labelStyle: const TextStyle(color: AppColors.primary),
            hintStyle: TextStyle(color: Colors.white.withOpacity(0.4)),
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
          '/login': (context) => const LoginScreen(),
          '/dashboard': (context) => const DriverDashboardScreen(),
          '/active-trip': (context) => const ActiveTripScreen(),
          '/available-requests': (context) => const AvailableRequestsScreen(),
          '/trip-history': (context) => const TripHistoryScreen(),
          '/document-upload': (context) => const DocumentUploadScreen(),
          '/profile': (context) => const ProfileScreen(),
          '/earnings': (context) => const EarningsScreen(),
        },
      ),
    );
  }
}
