import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../driver/driver_dashboard_screen.dart';
import '../supervisor/supervisor_dashboard_screen.dart';
import '../warehouse/warehouse_dashboard_screen.dart';
import '../accountant/accountant_dashboard_screen.dart';
import '../customer/customer_dashboard_screen.dart';
import '../../widgets/custom_app_bar.dart';
import '../../widgets/side_menu.dart';

class DashboardScreen extends StatefulWidget {
  const DashboardScreen({Key? key}) : super(key: key);

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> with TickerProviderStateMixin {
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
  late AnimationController _fabController;
  late Animation<double> _fabAnimation;

  // Mock user data - in real app, this would come from authentication
  String userRole = 'Driver'; // Change this to test different roles
  String userName = 'أحمد محمد';
  String userAvatar = '';

  @override
  void initState() {
    super.initState();
    
    _fabController = AnimationController(
      duration: const Duration(milliseconds: 300),
      vsync: this,
    );
    
    _fabAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _fabController,
      curve: Curves.easeInOut,
    ));

    // Start FAB animation after a delay
    Future.delayed(const Duration(milliseconds: 500), () {
      _fabController.forward();
    });
  }

  @override
  void dispose() {
    _fabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _scaffoldKey,
      backgroundColor: const Color(0xFF0A0E1A),
      appBar: CustomAppBar(
        title: _getDashboardTitle(),
        scaffoldKey: _scaffoldKey,
        userName: userName,
        userRole: userRole,
        userAvatar: userAvatar,
      ),
      drawer: SideMenu(
        userRole: userRole,
        userName: userName,
        onMenuSelected: _handleMenuSelection,
      ),
      floatingActionButton: _buildFloatingActionButton(),
      body: _buildRoleBasedDashboard(),
    );
  }

  Widget _buildRoleBasedDashboard() {
    switch (userRole) {
      case 'Driver':
        return const DriverDashboardScreen();
      case 'Supervisor':
        return const SupervisorDashboardScreen();
      case 'Warehouse Manager':
        return const WarehouseDashboardScreen();
      case 'Accountant':
        return const AccountantDashboardScreen();
      case 'Customer':
        return const CustomerDashboardScreen();
      default:
        return const DriverDashboardScreen();
    }
  }

  Widget _buildFloatingActionButton() {
    return AnimatedBuilder(
      animation: _fabAnimation,
      builder: (context, child) {
        return Transform.scale(
          scale: _fabAnimation.value,
          child: FloatingActionButton(
            onPressed: _handleQuickAction,
            backgroundColor: const Color(0xFFF97316),
            elevation: 8,
            child: const Icon(
              Icons.add,
              color: Colors.white,
              size: 28,
            ),
          ),
        );
      },
    );
  }

  String _getDashboardTitle() {
    switch (userRole) {
      case 'Driver':
        return 'لوحة تحكم السائق';
      case 'Supervisor':
        return 'لوحة تحكم المشرف';
      case 'Warehouse Manager':
        return 'لوحة تحكم مدير المستودع';
      case 'Accountant':
        return 'لوحة تحكم المحاسب';
      case 'Customer':
        return 'لوحة تحكم العميل';
      default:
        return 'لوحة التحكم';
    }
  }

  void _handleMenuSelection(String menuItem) {
    Navigator.pop(context); // Close drawer
    
    switch (menuItem) {
      case 'dashboard':
        // Already on dashboard
        break;
      case 'profile':
        _navigateToProfile();
        break;
      case 'settings':
        _navigateToSettings();
        break;
      case 'notifications':
        _navigateToNotifications();
        break;
      case 'help':
        _navigateToHelp();
        break;
      case 'logout':
        _handleLogout();
        break;
      default:
        // Handle role-specific menu items
        _handleRoleSpecificMenu(menuItem);
    }
  }

  void _handleRoleSpecificMenu(String menuItem) {
    // This would be implemented based on the user role
    // For now, just show a snackbar
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('تم اختيار: $menuItem'),
        backgroundColor: const Color(0xFFF97316),
      ),
    );
  }

  void _handleQuickAction() {
    // Quick action based on user role
    switch (userRole) {
      case 'Driver':
        _startNewTrip();
        break;
      case 'Supervisor':
        _assignNewTask();
        break;
      case 'Warehouse Manager':
        _manageInventory();
        break;
      case 'Accountant':
        _createInvoice();
        break;
      case 'Customer':
        _createShipment();
        break;
      default:
        _showQuickActionMenu();
    }
  }

  void _startNewTrip() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('بدء رحلة جديدة'),
        content: const Text('هل تريد بدء رحلة جديدة الآن؟'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              // TODO: Navigate to trip creation
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFFF97316),
            ),
            child: const Text('بدء'),
          ),
        ],
      ),
    );
  }

  void _assignNewTask() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('تعيين مهمة جديدة'),
        content: const Text('هل تريد تعيين مهمة جديدة؟'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              // TODO: Navigate to task assignment
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFFF97316),
            ),
            child: const Text('تعيين'),
          ),
        ],
      ),
    );
  }

  void _manageInventory() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('إدارة المخزون'),
        content: const Text('هل تريد إدارة المخزون؟'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              // TODO: Navigate to inventory management
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFFF97316),
            ),
            child: const Text('إدارة'),
          ),
        ],
      ),
    );
  }

  void _createInvoice() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('إنشاء فاتورة'),
        content: const Text('هل تريد إنشاء فاتورة جديدة؟'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              // TODO: Navigate to invoice creation
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFFF97316),
            ),
            child: const Text('إنشاء'),
          ),
        ],
      ),
    );
  }

  void _createShipment() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('إنشاء شحنة'),
        content: const Text('هل تريد إنشاء شحنة جديدة؟'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              // TODO: Navigate to shipment creation
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFFF97316),
            ),
            child: const Text('إنشاء'),
          ),
        ],
      ),
    );
  }

  void _showQuickActionMenu() {
    showModalBottomSheet(
      context: context,
      backgroundColor: const Color(0xFF1A1F2E),
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) => Container(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text(
              'الإجراءات السريعة',
              style: TextStyle(
                color: Colors.white,
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 20),
            ListTile(
              leading: const Icon(Icons.local_shipping, color: Color(0xFFF97316)),
              title: const Text('إنشاء شحنة', style: TextStyle(color: Colors.white)),
              onTap: () {
                Navigator.pop(context);
                _createShipment();
              },
            ),
            ListTile(
              leading: const Icon(Icons.track_changes, color: Color(0xFFF97316)),
              title: const Text('تتبع الشحنة', style: TextStyle(color: Colors.white)),
              onTap: () {
                Navigator.pop(context);
                // TODO: Navigate to tracking
              },
            ),
            ListTile(
              leading: const Icon(Icons.history, color: Color(0xFFF97316)),
              title: const Text('السجل', style: TextStyle(color: Colors.white)),
              onTap: () {
                Navigator.pop(context);
                // TODO: Navigate to history
              },
            ),
          ],
        ),
      ),
    );
  }

  void _navigateToProfile() {
    // TODO: Navigate to profile screen
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('الملف الشخصي'),
        backgroundColor: Color(0xFFF97316),
      ),
    );
  }

  void _navigateToSettings() {
    // TODO: Navigate to settings screen
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('الإعدادات'),
        backgroundColor: Color(0xFFF97316),
      ),
    );
  }

  void _navigateToNotifications() {
    // TODO: Navigate to notifications screen
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('الإشعارات'),
        backgroundColor: Color(0xFFF97316),
      ),
    );
  }

  void _navigateToHelp() {
    // TODO: Navigate to help screen
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('المساعدة'),
        backgroundColor: Color(0xFFF97316),
      ),
    );
  }

  void _handleLogout() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('تسجيل الخروج'),
        content: const Text('هل أنت متأكد من تسجيل الخروج؟'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              // TODO: Implement logout logic
              Navigator.pushReplacementNamed(context, '/login');
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFFF97316),
            ),
            child: const Text('تسجيل الخروج'),
          ),
        ],
      ),
    );
  }
}
