import 'package:flutter/material.dart';
import 'smart_logistics_screen.dart';
import 'shipment_tracking_screen.dart';
import 'cold_chain_screen.dart';

class OnboardingController extends StatefulWidget {
  const OnboardingController({super.key});

  @override
  State<OnboardingController> createState() => _OnboardingControllerState();
}

class _OnboardingControllerState extends State<OnboardingController> {
  final PageController _pageController = PageController();
  int _currentPage = 0;

  final List<Widget> _pages = [
    const SmartLogisticsScreen(),
    const ShipmentTrackingScreen(),
    const ColdChainScreen(),
  ];

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  void _onPageChanged(int page) {
    setState(() {
      _currentPage = page;
    });
  }

  void _nextPage() {
    if (_currentPage < _pages.length - 1) {
      _pageController.nextPage(
        duration: const Duration(milliseconds: 500),
        curve: Curves.easeInOut,
      );
    }
  }

  void _skipToEnd() {
    _pageController.animateToPage(
      _pages.length - 1,
      duration: const Duration(milliseconds: 800),
      curve: Curves.easeInOut,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: PageView(
        controller: _pageController,
        onPageChanged: _onPageChanged,
        physics: const NeverScrollableScrollPhysics(), // Disable swipe navigation
        children: _pages,
      ),
    );
  }
}
