import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

enum UserRole {
  admin,
  supervisor,
  accountant,
  driver,
  client,
}

enum Permission {
  // Trip Management
  viewTrips,
  createTrips,
  editTrips,
  deleteTrips,
  assignTrips,
  
  // Vehicle Management
  viewVehicles,
  createVehicles,
  editVehicles,
  deleteVehicles,
  
  // Driver Management
  viewDrivers,
  createDrivers,
  editDrivers,
  deleteDrivers,
  assignDrivers,
  
  // Client Management
  viewClients,
  createClients,
  editClients,
  deleteClients,
  
  // Financial Management
  viewInvoices,
  createInvoices,
  editInvoices,
  deleteInvoices,
  viewPayments,
  processPayments,
  viewReports,
  
  // Maintenance
  viewMaintenance,
  createMaintenance,
  editMaintenance,
  deleteMaintenance,
  
  // Fleet Management
  viewFleet,
  manageFleet,
  optimizeFleet,
  
  // Analytics
  viewAnalytics,
  exportReports,
  
  // Settings
  viewSettings,
  editSettings,
  manageUsers,
}

class RoleBasedAccessService extends ChangeNotifier {
  UserRole? _currentUserRole;
  Map<String, dynamic>? _currentUserData;
  Set<Permission> _userPermissions = {};

  UserRole? get currentUserRole => _currentUserRole;
  Set<Permission> get userPermissions => _userPermissions;

  // Role permissions mapping
  static const Map<UserRole, Set<Permission>> _rolePermissions = {
    UserRole.admin: {
      // Full access to everything
      Permission.viewTrips,
      Permission.createTrips,
      Permission.editTrips,
      Permission.deleteTrips,
      Permission.assignTrips,
      Permission.viewVehicles,
      Permission.createVehicles,
      Permission.editVehicles,
      Permission.deleteVehicles,
      Permission.viewDrivers,
      Permission.createDrivers,
      Permission.editDrivers,
      Permission.deleteDrivers,
      Permission.assignDrivers,
      Permission.viewClients,
      Permission.createClients,
      Permission.editClients,
      Permission.deleteClients,
      Permission.viewInvoices,
      Permission.createInvoices,
      Permission.editInvoices,
      Permission.deleteInvoices,
      Permission.viewPayments,
      Permission.processPayments,
      Permission.viewReports,
      Permission.viewMaintenance,
      Permission.createMaintenance,
      Permission.editMaintenance,
      Permission.deleteMaintenance,
      Permission.viewFleet,
      Permission.manageFleet,
      Permission.optimizeFleet,
      Permission.viewAnalytics,
      Permission.exportReports,
      Permission.viewSettings,
      Permission.editSettings,
      Permission.manageUsers,
    },
    UserRole.supervisor: {
      // Operations management
      Permission.viewTrips,
      Permission.createTrips,
      Permission.editTrips,
      Permission.assignTrips,
      Permission.viewVehicles,
      Permission.editVehicles,
      Permission.viewDrivers,
      Permission.assignDrivers,
      Permission.viewClients,
      Permission.viewInvoices,
      Permission.viewPayments,
      Permission.viewReports,
      Permission.viewMaintenance,
      Permission.createMaintenance,
      Permission.editMaintenance,
      Permission.viewFleet,
      Permission.manageFleet,
      Permission.viewAnalytics,
      Permission.viewSettings,
    },
    UserRole.accountant: {
      // Financial management
      Permission.viewTrips,
      Permission.viewInvoices,
      Permission.createInvoices,
      Permission.editInvoices,
      Permission.viewPayments,
      Permission.processPayments,
      Permission.viewReports,
      Permission.viewAnalytics,
      Permission.exportReports,
      Permission.viewSettings,
    },
    UserRole.driver: {
      // Driver specific permissions
      Permission.viewTrips,
      Permission.editTrips, // Only own trips
      Permission.viewVehicles, // Only assigned vehicle
      Permission.viewMaintenance,
      Permission.createMaintenance, // Report issues
      Permission.viewAnalytics, // Own stats
    },
    UserRole.client: {
      // Client specific permissions
      Permission.viewTrips, // Only own trips
      Permission.createTrips,
      Permission.editTrips, // Only own trips
      Permission.viewInvoices, // Only own invoices
      Permission.viewPayments, // Only own payments
      Permission.viewAnalytics, // Own stats
    },
  };

  // Initialize user role
  Future<void> initializeUserRole() async {
    final prefs = await SharedPreferences.getInstance();
    final roleString = prefs.getString('userRole');
    final userDataString = prefs.getString('userData');

    if (roleString != null) {
      _currentUserRole = UserRole.values.firstWhere(
        (role) => role.toString() == roleString,
        orElse: () => UserRole.driver,
      );
      _userPermissions = _rolePermissions[_currentUserRole] ?? {};
    }

    if (userDataString != null) {
      // Parse user data if needed
    }

    notifyListeners();
  }

  // Set user role
  void setUserRole(UserRole role) {
    _currentUserRole = role;
    _userPermissions = _rolePermissions[role] ?? {};
    notifyListeners();
  }

  // Check if user has specific permission
  bool hasPermission(Permission permission) {
    return _userPermissions.contains(permission);
  }

  // Check if user has any of the specified permissions
  bool hasAnyPermission(List<Permission> permissions) {
    return permissions.any((p) => _userPermissions.contains(p));
  }

  // Check if user has all specified permissions
  bool hasAllPermissions(List<Permission> permissions) {
    return permissions.every((p) => _userPermissions.contains(p));
  }

  // Get accessible menu items based on role
  List<MenuItem> getAccessibleMenuItems() {
    switch (_currentUserRole) {
      case UserRole.admin:
        return _getAdminMenuItems();
      case UserRole.supervisor:
        return _getSupervisorMenuItems();
      case UserRole.accountant:
        return _getAccountantMenuItems();
      case UserRole.driver:
        return _getDriverMenuItems();
      case UserRole.client:
        return _getClientMenuItems();
      default:
        return [];
    }
  }

  List<MenuItem> _getAdminMenuItems() {
    return [
      MenuItem(icon: Icons.dashboard, label: 'لوحة التحكم', route: '/dashboard'),
      MenuItem(icon: Icons.local_shipping, label: 'الرحلات', route: '/trips'),
      MenuItem(icon: Icons.directions_car, label: 'المركبات', route: '/vehicles'),
      MenuItem(icon: Icons.people, label: 'السائقين', route: '/drivers'),
      MenuItem(icon: Icons.business, label: 'العملاء', route: '/clients'),
      MenuItem(icon: Icons.receipt, label: 'الفواتير', route: '/invoices'),
      MenuItem(icon: Icons.payments, label: 'المدفوعات', route: '/payments'),
      MenuItem(icon: Icons.build, label: 'الصيانة', route: '/maintenance'),
      MenuItem(icon: Icons.analytics, label: 'التقارير', route: '/analytics'),
      MenuItem(icon: Icons.settings, label: 'الإعدادات', route: '/settings'),
      MenuItem(icon: Icons.admin_panel_settings, label: 'إدارة المستخدمين', route: '/users'),
    ];
  }

  List<MenuItem> _getSupervisorMenuItems() {
    return [
      MenuItem(icon: Icons.dashboard, label: 'لوحة التحكم', route: '/dashboard'),
      MenuItem(icon: Icons.local_shipping, label: 'الرحلات', route: '/trips'),
      MenuItem(icon: Icons.directions_car, label: 'المركبات', route: '/vehicles'),
      MenuItem(icon: Icons.people, label: 'السائقين', route: '/drivers'),
      MenuItem(icon: Icons.business, label: 'العملاء', route: '/clients'),
      MenuItem(icon: Icons.receipt, label: 'الفواتير', route: '/invoices'),
      MenuItem(icon: Icons.build, label: 'الصيانة', route: '/maintenance'),
      MenuItem(icon: Icons.analytics, label: 'التقارير', route: '/analytics'),
      MenuItem(icon: Icons.settings, label: 'الإعدادات', route: '/settings'),
    ];
  }

  List<MenuItem> _getAccountantMenuItems() {
    return [
      MenuItem(icon: Icons.dashboard, label: 'لوحة التحكم', route: '/dashboard'),
      MenuItem(icon: Icons.local_shipping, label: 'الرحلات', route: '/trips'),
      MenuItem(icon: Icons.receipt, label: 'الفواتير', route: '/invoices'),
      MenuItem(icon: Icons.payments, label: 'المدفوعات', route: '/payments'),
      MenuItem(icon: Icons.analytics, label: 'التقارير', route: '/analytics'),
      MenuItem(icon: Icons.settings, label: 'الإعدادات', route: '/settings'),
    ];
  }

  List<MenuItem> _getDriverMenuItems() {
    return [
      MenuItem(icon: Icons.home, label: 'الرئيسية', route: '/dashboard'),
      MenuItem(icon: Icons.local_shipping, label: 'الرحلات', route: '/trips'),
      MenuItem(icon: Icons.history, label: 'السجل', route: '/history'),
      MenuItem(icon: Icons.account_balance_wallet, label: 'الأرباح', route: '/earnings'),
      MenuItem(icon: Icons.person, label: 'حسابي', route: '/profile'),
    ];
  }

  List<MenuItem> _getClientMenuItems() {
    return [
      MenuItem(icon: Icons.home, label: 'الرئيسية', route: '/dashboard'),
      MenuItem(icon: Icons.add, label: 'طلب جديد', route: '/new-trip'),
      MenuItem(icon: Icons.local_shipping, label: 'طلباتي', route: '/my-trips'),
      MenuItem(icon: Icons.map, label: 'التتبع', route: '/tracking'),
      MenuItem(icon: Icons.receipt, label: 'الفواتير', route: '/invoices'),
      MenuItem(icon: Icons.person, label: 'حسابي', route: '/profile'),
    ];
  }

  // Check if user can access specific route
  bool canAccessRoute(String route) {
    final menuItems = getAccessibleMenuItems();
    return menuItems.any((item) => item.route == route);
  }

  // Get role display name
  String getRoleDisplayName(UserRole role) {
    switch (role) {
      case UserRole.admin:
        return 'مدير النظام';
      case UserRole.supervisor:
        return 'مشرف العمليات';
      case UserRole.accountant:
        return 'محاسب';
      case UserRole.driver:
        return 'سائق';
      case UserRole.client:
        return 'عميل';
    }
  }

  // Clear user session
  Future<void> clearSession() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('userRole');
    await prefs.remove('userData');
    _currentUserRole = null;
    _currentUserData = null;
    _userPermissions = {};
    notifyListeners();
  }
}

class MenuItem {
  final IconData icon;
  final String label;
  final String route;

  MenuItem({
    required this.icon,
    required this.label,
    required this.route,
  });
}
