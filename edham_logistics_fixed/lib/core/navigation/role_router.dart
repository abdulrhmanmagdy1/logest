// ============================================
// 🧭 Role-Based Router - Multi-Role Navigation
// ============================================

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../core/models/user_model.dart';
import '../../features/auth/presentation/screens/splash_screen.dart';
import '../../features/auth/presentation/screens/login_screen.dart';
import '../../features/client/presentation/screens/client_dashboard_screen.dart';
import '../../features/driver/presentation/screens/driver_dashboard_screen.dart';
import '../../features/supervisor/presentation/screens/supervisor_dashboard_screen.dart';
import '../../features/accountant/presentation/screens/accountant_dashboard_screen.dart';
import '../../features/workshop/presentation/screens/workshop_dashboard_screen.dart';

class RoleRouter {
  static final _rootNavigatorKey = GlobalKey<NavigatorState>();
  static final _shellNavigatorKey = GlobalKey<NavigatorState>();
  
  static UserRole? _currentRole;
  
  static void setRole(UserRole role) {
    _currentRole = role;
  }
  
  static UserRole? get currentRole => _currentRole;
  
  // Get initial route based on role
  static String getInitialRoute(UserRole role) {
    switch (role) {
      case UserRole.client:
        return '/client/dashboard';
      case UserRole.driver:
        return '/driver/dashboard';
      case UserRole.supervisor:
        return '/supervisor/dashboard';
      case UserRole.accountant:
        return '/accountant/dashboard';
      case UserRole.workshop:
        return '/workshop/dashboard';
      default:
        return '/client/dashboard';
    }
  }
  
  // Get dashboard screen based on role
  static Widget getDashboardScreen(UserRole role) {
    switch (role) {
      case UserRole.client:
        return const ClientDashboardScreen();
      case UserRole.driver:
        return const DriverDashboardScreen();
      case UserRole.supervisor:
        return const SupervisorDashboardScreen();
      case UserRole.accountant:
        return const AccountantDashboardScreen();
      case UserRole.workshop:
        return const WorkshopDashboardScreen();
      default:
        return const ClientDashboardScreen();
    }
  }
  
  static final router = GoRouter(
    navigatorKey: _rootNavigatorKey,
    initialLocation: '/',
    debugLogDiagnostics: true,
    routes: [
      // Splash
      GoRoute(
        path: '/',
        builder: (context, state) => const SplashScreen(),
      ),
      
      // Auth
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginScreen(),
      ),
      
      // Client Routes
      GoRoute(
        path: '/client/dashboard',
        builder: (context, state) => const ClientDashboardScreen(),
      ),
      GoRoute(
        path: '/client/shipments',
        builder: (context, state) => const ShipmentsListScreen(),
      ),
      GoRoute(
        path: '/client/tracking',
        builder: (context, state) => const TrackingScreen(),
      ),
      GoRoute(
        path: '/client/create-shipment',
        builder: (context, state) => const CreateShipmentScreen(),
      ),
      GoRoute(
        path: '/client/invoices',
        builder: (context, state) => const InvoicesListScreen(),
      ),
      
      // Driver Routes
      GoRoute(
        path: '/driver/dashboard',
        builder: (context, state) => const DriverDashboardScreen(),
      ),
      GoRoute(
        path: '/driver/trips',
        builder: (context, state) => const DriverTripsScreen(),
      ),
      GoRoute(
        path: '/driver/trip/:id',
        builder: (context, state) {
          final tripId = state.pathParameters['id'];
          return TripExecutionScreen(tripId: tripId!);
        },
      ),
      GoRoute(
        path: '/driver/history',
        builder: (context, state) => const DriverHistoryScreen(),
      ),
      GoRoute(
        path: '/driver/vehicle',
        builder: (context, state) => const VehicleStatusScreen(),
      ),
      
      // Supervisor Routes
      GoRoute(
        path: '/supervisor/dashboard',
        builder: (context, state) => const SupervisorDashboardScreen(),
      ),
      GoRoute(
        path: '/supervisor/drivers',
        builder: (context, state) => const DriversManagementScreen(),
      ),
      GoRoute(
        path: '/supervisor/shipments',
        builder: (context, state) => const ShipmentsDispatchScreen(),
      ),
      GoRoute(
        path: '/supervisor/tracking',
        builder: (context, state) => const FleetTrackingScreen(),
      ),
      GoRoute(
        path: '/supervisor/reports',
        builder: (context, state) => const PerformanceReportsScreen(),
      ),
      
      // Accountant Routes
      GoRoute(
        path: '/accountant/dashboard',
        builder: (context, state) => const AccountantDashboardScreen(),
      ),
      GoRoute(
        path: '/accountant/invoices',
        builder: (context, state) => const AllInvoicesScreen(),
      ),
      GoRoute(
        path: '/accountant/payments',
        builder: (context, state) => const PaymentsScreen(),
      ),
      GoRoute(
        path: '/accountant/drivers-settlement',
        builder: (context, state) => const DriversSettlementScreen(),
      ),
      GoRoute(
        path: '/accountant/debts',
        builder: (context, state) => const DebtsScreen(),
      ),
      GoRoute(
        path: '/accountant/reports',
        builder: (context, state) => const FinancialReportsScreen(),
      ),
      
      // Workshop Routes
      GoRoute(
        path: '/workshop/dashboard',
        builder: (context, state) => const WorkshopDashboardScreen(),
      ),
      GoRoute(
        path: '/workshop/vehicles',
        builder: (context, state) => const VehiclesMaintenanceScreen(),
      ),
      GoRoute(
        path: '/workshop/schedule',
        builder: (context, state) => const MaintenanceScheduleScreen(),
      ),
      GoRoute(
        path: '/workshop/requests',
        builder: (context, state) => const MaintenanceRequestsScreen(),
      ),
      GoRoute(
        path: '/workshop/parts',
        builder: (context, state) => const PartsInventoryScreen(),
      ),
    ],
    redirect: (context, state) {
      // Handle role-based redirects
      return null;
    },
  );
}

// Placeholder screens - will be implemented
typealias ShipmentsListScreen = ClientDashboardScreen;
typealias TrackingScreen = ClientDashboardScreen;
typealias CreateShipmentScreen = ClientDashboardScreen;
typealias InvoicesListScreen = ClientDashboardScreen;

typealias DriverTripsScreen = DriverDashboardScreen;
typealias TripExecutionScreen = DriverDashboardScreen;
typealias DriverHistoryScreen = DriverDashboardScreen;
typealias VehicleStatusScreen = DriverDashboardScreen;

typealias DriversManagementScreen = SupervisorDashboardScreen;
typealias ShipmentsDispatchScreen = SupervisorDashboardScreen;
typealias FleetTrackingScreen = SupervisorDashboardScreen;
typealias PerformanceReportsScreen = SupervisorDashboardScreen;

typealias AllInvoicesScreen = AccountantDashboardScreen;
typealias PaymentsScreen = AccountantDashboardScreen;
typealias DriversSettlementScreen = AccountantDashboardScreen;
typealias DebtsScreen = AccountantDashboardScreen;
typealias FinancialReportsScreen = AccountantDashboardScreen;

typealias VehiclesMaintenanceScreen = WorkshopDashboardScreen;
typealias MaintenanceScheduleScreen = WorkshopDashboardScreen;
typealias MaintenanceRequestsScreen = WorkshopDashboardScreen;
typealias PartsInventoryScreen = WorkshopDashboardScreen;
