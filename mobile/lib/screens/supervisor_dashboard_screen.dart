import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../widgets/supervisor_sidebar_dashboard.dart';

class SupervisorDashboardScreen extends StatefulWidget {
  const SupervisorDashboardScreen({super.key});

  @override
  State<SupervisorDashboardScreen> createState() => _SupervisorDashboardScreenState();
}

class _SupervisorDashboardScreenState extends State<SupervisorDashboardScreen> {
  @override
  void initState() {
    super.initState();
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.light,
        systemNavigationBarColor: Color(0xFF1A1A2E),
        systemNavigationBarIconBrightness: Brightness.light,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return const SupervisorSidebarDashboard();
  }
}
