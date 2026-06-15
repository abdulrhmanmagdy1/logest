import 'package:flutter/material.dart';
import '../constants/index.dart';

/// Loading Widget
/// Reusable loading indicator with customizable size and color
class LoadingWidget extends StatelessWidget {
  final double size;
  final Color? color;
  final double strokeWidth;
  final String? message;

  const LoadingWidget({
    super.key,
    this.size = 40,
    this.color,
    this.strokeWidth = 3,
    this.message,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          SizedBox(
            width: size,
            height: size,
            child: CircularProgressIndicator(
              strokeWidth: strokeWidth,
              valueColor: AlwaysStoppedAnimation<Color>(
                color ?? AppColors.primary,
              ),
            ),
          ),
          if (message != null) ...[
            const SizedBox(height: 16),
            Text(
              message!,
              style: TextStyle(
                color: AppColors.textSecondary,
                fontSize: 14,
              ),
            ),
          ],
        ],
      ),
    );
  }
}

/// Full Screen Loading
/// Covers entire screen with loading indicator
class FullScreenLoading extends StatelessWidget {
  final String? message;

  const FullScreenLoading({
    super.key,
    this.message,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      color: AppColors.background.withOpacity(0.8),
      child: LoadingWidget(
        size: 60,
        message: message ?? AppStrings.loading,
      ),
    );
  }
}
