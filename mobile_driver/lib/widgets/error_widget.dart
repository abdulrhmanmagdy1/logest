import 'package:flutter/material.dart';
import '../constants/index.dart';
import 'custom_button.dart';

/// Error Widget
/// Display error message with retry option
class ErrorWidget extends StatelessWidget {
  final String message;
  final VoidCallback? onRetry;
  final String? retryText;
  final IconData icon;

  const ErrorWidget({
    super.key,
    required this.message,
    this.onRetry,
    this.retryText,
    this.icon = Icons.error_outline,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              icon,
              size: 64,
              color: AppColors.error,
            ),
            const SizedBox(height: 16),
            Text(
              AppStrings.error,
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: AppColors.textPrimary,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              message,
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 14,
                color: AppColors.textSecondary,
              ),
            ),
            if (onRetry != null) ...[
              const SizedBox(height: 24),
              CustomButton(
                text: retryText ?? AppStrings.tryAgain,
                onPressed: onRetry!,
                type: ButtonType.primary,
                icon: Icons.refresh,
              ),
            ],
          ],
        ),
      ),
    );
  }
}

/// Empty State Widget
/// Display when no data is available
class EmptyStateWidget extends StatelessWidget {
  final String message;
  final String? subMessage;
  final IconData icon;
  final VoidCallback? onAction;
  final String? actionText;

  const EmptyStateWidget({
    super.key,
    required this.message,
    this.subMessage,
    this.icon = Icons.inbox_outlined,
    this.onAction,
    this.actionText,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              icon,
              size: 80,
              color: AppColors.textMuted,
            ),
            const SizedBox(height: 24),
            Text(
              message,
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.w600,
                color: AppColors.textPrimary,
              ),
            ),
            if (subMessage != null) ...[
              const SizedBox(height: 8),
              Text(
                subMessage!,
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 14,
                  color: AppColors.textSecondary,
                ),
              ),
            ],
            if (onAction != null && actionText != null) ...[
              const SizedBox(height: 24),
              CustomButton(
                text: actionText!,
                onPressed: onAction!,
                type: ButtonType.outlined,
              ),
            ],
          ],
        ),
      ),
    );
  }
}
