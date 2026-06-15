import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../widgets/sidebar_dashboard.dart';

class SidebarDashboardScreen extends StatefulWidget {
  const SidebarDashboardScreen({super.key});

  @override
  State<SidebarDashboardScreen> createState() => _SidebarDashboardScreenState();
}

class _SidebarDashboardScreenState extends State<SidebarDashboardScreen> {
  @override
  void initState() {
    super.initState();
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.light,
        systemNavigationBarColor: Color(0xFF0A1428),
        systemNavigationBarIconBrightness: Brightness.light,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return const SidebarDashboard();
  }
}
