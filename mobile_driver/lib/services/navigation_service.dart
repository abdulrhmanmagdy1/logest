import 'package:flutter/material.dart';
import '../constants/index.dart';

/// Navigation Service
/// Centralized navigation management without BuildContext
class NavigationService {
  static final NavigationService _instance = NavigationService._internal();
  factory NavigationService() => _instance;
  NavigationService._internal();

  final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

  NavigatorState? get _navigator => navigatorKey.currentState;

  /// Navigate to a named route
  Future<T?>? navigateTo<T>(String routeName, {Object? arguments}) {
    return _navigator?.pushNamed<T>(routeName, arguments: arguments);
  }

  /// Navigate to a named route and remove all previous routes
  Future<T?>? navigateToAndRemoveAll<T>(String routeName, {Object? arguments}) {
    return _navigator?.pushNamedAndRemoveUntil<T, T>(
      routeName,
      (route) => false,
      arguments: arguments,
    );
  }

  /// Navigate to a named route and remove until condition
  Future<T?>? navigateToAndRemoveUntil<T>(
    String routeName,
    bool Function(Route<dynamic>) predicate, {
    Object? arguments,
  }) {
    return _navigator?.pushNamedAndRemoveUntil<T, T>(
      routeName,
      predicate,
      arguments: arguments,
    );
  }

  /// Replace current route
  Future<T?>? replaceWith<T>(String routeName, {Object? arguments}) {
    return _navigator?.pushReplacementNamed<T, T>(routeName, arguments: arguments);
  }

  /// Go back
  void goBack<T>([T? result]) {
    _navigator?.pop<T>(result);
  }

  /// Check if can go back
  bool get canGoBack => _navigator?.canPop() ?? false;

  /// Show dialog
  Future<T?>? showDialog<T>({
    required WidgetBuilder builder,
    bool barrierDismissible = true,
  }) {
    return _navigator?.push<T>(
      MaterialPageRoute<T>(
        builder: builder,
        fullscreenDialog: true,
      ),
    );
  }

  /// Show snackbar
  void showSnackBar(String message, {
    bool isError = false,
    Duration duration = const Duration(seconds: 3),
  }) {
    if (_navigator?.overlay?.context != null) {
      ScaffoldMessenger.of(_navigator!.overlay!.context).showSnackBar(
        SnackBar(
          content: Text(message),
          backgroundColor: isError ? AppColors.error : AppColors.success,
          duration: duration,
          behavior: SnackBarBehavior.floating,
        ),
      );
    }
  }

  /// Show loading dialog
  void showLoading({String? message}) {
    showDialog(
      builder: (context) => WillPopScope(
        onWillPop: () async => false,
        child: Center(
          child: Container(
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: AppColors.surface,
              borderRadius: BorderRadius.circular(16),
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                CircularProgressIndicator(
                  valueColor: AlwaysStoppedAnimation<Color>(AppColors.primary),
                ),
                if (message != null) ...[
                  const SizedBox(height: 16),
                  Text(
                    message,
                    style: TextStyle(color: AppColors.textPrimary),
                  ),
                ],
              ],
            ),
          ),
        ),
      ),
    );
  }

  /// Hide loading dialog
  void hideLoading() {
    goBack();
  }

  /// Show bottom sheet
  Future<T?>? showBottomSheet<T>({
    required WidgetBuilder builder,
    bool isScrollControlled = false,
  }) {
    if (_navigator?.overlay?.context != null) {
      return showModalBottomSheet<T>(
        context: _navigator!.overlay!.context,
        builder: builder,
        isScrollControlled: isScrollControlled,
        backgroundColor: AppColors.surface,
        shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
      );
    }
    return null;
  }

  /// Get current route name
  String? get currentRoute {
    String? current;
    _navigator?.popUntil((route) {
      current = route.settings.name;
      return true;
    });
    return current;
  }
}

// Global navigation service
NavigationService get navigation => NavigationService();
