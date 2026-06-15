// ============================================
// 🧭 App Router - GoRouter Configuration with Premium Transitions
// ============================================

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../features/auth/presentation/screens/login_screen.dart';
import '../../features/auth/presentation/screens/splash_screen.dart';
import '../../features/auth/presentation/screens/training_screen.dart';
import '../../features/auth/presentation/screens/reset_password_screen.dart';
import '../../features/auth/presentation/screens/premium_training_screen.dart';
import '../../features/auth/presentation/screens/zaki_splash_screen.dart';
import '../../features/client/presentation/screens/client_dashboard_screen.dart';
import '../../features/client/presentation/screens/create_shipment_screen.dart';
import '../../features/client/presentation/screens/tracking_screen.dart';
import '../../features/client/presentation/screens/shipments_list_screen.dart';
import '../../features/client/presentation/screens/onboarding_screen.dart';
import '../../features/client/presentation/screens/booking_flow_screen.dart';
import '../../features/client/presentation/screens/live_tracking_screen.dart';
import '../../features/client/presentation/screens/shipment_cart_screen.dart';
import '../../features/client/presentation/screens/settings_screen.dart';
import '../../features/client/presentation/screens/privacy_screen.dart';
import '../../features/client/presentation/screens/one_click_ordering.dart';
import '../../features/auth/presentation/screens/registration_screen.dart';
import '../../features/supervisor/presentation/screens/supervisor_login_screen.dart';
import '../../features/supervisor/presentation/screens/premium_supervisor_dashboard.dart';
import '../../features/supervisor/presentation/screens/smart_shipment_management.dart';
import '../../features/driver/presentation/screens/driver_dashboard_screen.dart';
import '../../features/driver/presentation/screens/driver_trips_screen.dart';
import '../../features/driver/presentation/screens/specialized_driver_navigation.dart';
import '../../features/accountant/presentation/screens/accountant_dashboard_screen.dart';
import '../../features/supervisor/presentation/screens/supervisor_dashboard_screen.dart';
import '../../features/workshop/presentation/screens/workshop_dashboard_screen.dart';
import '../../features/workshop/presentation/screens/workshop_maintenance_portal.dart';
import '../../features/admin/presentation/screens/ceo_dashboard_screen.dart';
import '../../features/chat/presentation/screens/chat_list_screen.dart';
import '../../features/support/presentation/screens/support_tickets_screen.dart';
import '../../features/support/presentation/screens/support_account_management.dart';
import '../../features/reviews/presentation/screens/reviews_screen.dart';
import '../../features/documents/presentation/screens/documents_screen.dart';
import '../../features/monitoring/presentation/screens/temperature_monitoring_system.dart';
import '../../features/tracking/presentation/screens/temperature_monitor_screen.dart';
import '../../features/billing/presentation/screens/subscription_screen.dart';
import '../../features/accounting/presentation/screens/accounting_billing_system.dart';
import '../../features/notifications/presentation/screens/notifications_alerts_center.dart';
import '../../features/analytics/presentation/screens/reports_analytics_dashboard.dart';
import '../../features/developer/presentation/screens/api_keys_screen.dart';

class AppRouter {
  static final _rootNavigatorKey = GlobalKey<NavigatorState>();
  static final _shellNavigatorKey = GlobalKey<NavigatorState>();

  static final router = GoRouter(
    navigatorKey: _rootNavigatorKey,
    initialLocation: '/premium-training',
    debugLogDiagnostics: true,
    
    routes: [
      // Onboarding Route
      GoRoute(
        path: '/onboarding',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const OnboardingScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return SlideTransition(
              position: Tween<Offset>(
                begin: const Offset(0, 1.0),
                end: Offset.zero,
              ).animate(CurvedAnimation(
                parent: animation,
                curve: Curves.easeOutCubic,
              )),
              child: FadeTransition(
                opacity: animation,
                child: child,
              ),
            );
          },
        ),
      ),

      // Splash Route
      GoRoute(
        path: '/splash',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const SplashScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),

      // Training Route
      GoRoute(
        path: '/training',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const TrainingScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return SlideTransition(
              position: Tween<Offset>(
                begin: const Offset(1.0, 0),
                end: Offset.zero,
              ).animate(CurvedAnimation(
                parent: animation,
                curve: Curves.easeOutCubic,
              )),
              child: FadeTransition(
                opacity: animation,
                child: child,
              ),
            );
          },
        ),
      ),

      // Premium Training Route
      GoRoute(
        path: '/premium-training',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const PremiumTrainingScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 0.3),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),

      // Zaki Splash Route
      GoRoute(
        path: '/zaki-splash',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const ZakiSplashScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),

      // Auth Routes
      GoRoute(
        path: '/login',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const LoginScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.9,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/reset-password',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const ResetPasswordScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return SlideTransition(
              position: Tween<Offset>(
                begin: const Offset(-1.0, 0),
                end: Offset.zero,
              ).animate(CurvedAnimation(
                parent: animation,
                curve: Curves.easeOutCubic,
              )),
              child: FadeTransition(
                opacity: animation,
                child: child,
              ),
            );
          },
        ),
      ),

      // Supervisor Login Route
      GoRoute(
        path: '/supervisor/login',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const SupervisorLoginScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),

      // Client Routes
      GoRoute(
        path: '/client/dashboard',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const ClientDashboardScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0.1, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/client/shipments',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const ShipmentsListScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/client/create-shipment',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const CreateShipmentScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 1.0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/client/tracking',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const TrackingScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(-1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/client/booking',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const BookingFlowScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/client/live-tracking',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const LiveTrackingScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 1.0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),

      // New Client Routes
      GoRoute(
        path: '/shipment/cart',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const ShipmentCartScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/settings',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const SettingsScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 1.0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/privacy',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const PrivacyScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(-1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/billing',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const SubscriptionScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/one-click-ordering',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const OneClickOrdering(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 1.0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/profile',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const SettingsScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/shipments',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const ShipmentsListScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 1.0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/tracking',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const TrackingScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(-1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),

      // Driver Routes
      GoRoute(
        path: '/driver/dashboard',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const DriverDashboardScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 1.0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/driver/trips',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const DriverTripsScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/driver/navigation',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const SpecializedDriverNavigation(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),

      // Accountant Routes
      GoRoute(
        path: '/accountant/dashboard',
        builder: (context, state) => const AccountantDashboardScreen(),
      ),

      // Supervisor Routes
      GoRoute(
        path: '/supervisor/dashboard',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const SupervisorDashboardScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/supervisor/premium-dashboard',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const PremiumSupervisorDashboard(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/supervisor/shipment-management',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const SmartShipmentManagement(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),

      // Workshop Routes
      GoRoute(
        path: '/workshop/dashboard',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const WorkshopDashboardScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 1.0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/workshop/maintenance',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const WorkshopMaintenancePortal(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),

      // Admin/CEO Routes
      GoRoute(
        path: '/admin/ceo-dashboard',
        builder: (context, state) => const CEODashboardScreen(),
      ),

      // Professional Features Routes
      GoRoute(
        path: '/chat',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const ChatListScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/support/tickets',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const SupportTicketsScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 1.0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/support/account',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const SupportAccountManagement(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/reviews',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const ReviewsScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(-1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/documents',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const DocumentsScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 1.0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/tracking/temperature',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const TemperatureMonitorScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/monitoring/temperature',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const TemperatureMonitoringSystem(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/notifications',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const NotificationsAlertsCenter(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(0, 1.0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/analytics',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const ReportsAnalyticsDashboard(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
      GoRoute(
        path: '/accounting',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const AccountingBillingSystem(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(-1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),

      // Billing & Subscription
      GoRoute(
        path: '/billing/subscription',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const SubscriptionScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: ScaleTransition(
                scale: Tween<double>(
                  begin: 0.8,
                  end: 1.0,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),

      // Developer
      GoRoute(
        path: '/developer/api-keys',
        pageBuilder: (context, state) => CustomTransitionPage(
          child: const ApiKeysScreen(),
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: animation,
              child: SlideTransition(
                position: Tween<Offset>(
                  begin: const Offset(1.0, 0),
                  end: Offset.zero,
                ).animate(CurvedAnimation(
                  parent: animation,
                  curve: Curves.easeOutCubic,
                )),
                child: child,
              ),
            );
          },
        ),
      ),
    ],

    // Redirect logic based on auth state
    redirect: (context, state) {
      // TODO: Implement auth state check
      return null;
    },

    // Error handling
    errorBuilder: (context, state) => Scaffold(
      body: Center(
        child: Text(
          'Page not found: ${state.uri.path}',
          style: const TextStyle(color: Colors.white),
        ),
      ),
    ),
  );

  // Helper methods for navigation
  static void goToOnboarding(BuildContext context) => context.go('/onboarding');
  static void goToTraining(BuildContext context) => context.go('/training');
  static void goToPremiumTraining(BuildContext context) => context.go('/premium-training');
  static void goLogin(BuildContext context) => context.go('/login');
  static void goResetPassword(BuildContext context) => context.go('/reset-password');
  static void goClientDashboard(BuildContext context) => context.go('/client/dashboard');
  static void goDriverDashboard(BuildContext context) => context.go('/driver/dashboard');
  static void goCreateShipment(BuildContext context) => context.push('/client/create-shipment');
  static void goTracking(BuildContext context) => context.push('/client/tracking');
  static void goShipments(BuildContext context) => context.push('/client/shipments');
  static void goBooking(BuildContext context) => context.push('/client/booking');
  static void goLiveTracking(BuildContext context) => context.push('/client/live-tracking');
  static void goBack(BuildContext context) => context.pop();

  // Driver Navigation
  static void goDriverTrips(BuildContext context) => context.push('/driver/trips');

  // Role Navigation
  static void goAccountantDashboard(BuildContext context) => context.go('/accountant/dashboard');
  static void goSupervisorDashboard(BuildContext context) => context.go('/supervisor/dashboard');
  static void goWorkshopDashboard(BuildContext context) => context.go('/workshop/dashboard');
  static void goCEODashboard(BuildContext context) => context.go('/admin/ceo-dashboard');

  // Professional Features
  static void goChat(BuildContext context) => context.push('/chat');
  static void goSupportTickets(BuildContext context) => context.push('/support/tickets');
  static void goReviews(BuildContext context) => context.push('/reviews');
  static void goDocuments(BuildContext context) => context.push('/documents');
  static void goTemperatureMonitor(BuildContext context) => context.push('/tracking/temperature');

  // Billing
  static void goSubscription(BuildContext context) => context.push('/billing/subscription');

  // Developer
  static void goApiKeys(BuildContext context) => context.push('/developer/api-keys');
}
