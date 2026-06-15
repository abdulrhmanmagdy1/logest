import 'package:flutter/material.dart';
import '../constants/index.dart';

/// Status Badge
/// Displays status with appropriate color and icon
class StatusBadge extends StatelessWidget {
  final String status;
  final StatusType type;
  final bool showIcon;
  final bool isLarge;

  const StatusBadge({
    super.key,
    required this.status,
    this.type = StatusType.generic,
    this.showIcon = true,
    this.isLarge = false,
  });

  @override
  Widget build(BuildContext context) {
    final config = _getStatusConfig();
    final padding = isLarge 
        ? const EdgeInsets.symmetric(horizontal: 16, vertical: 8)
        : const EdgeInsets.symmetric(horizontal: 12, vertical: 6);
    final fontSize = isLarge ? 14.0 : 12.0;
    final iconSize = isLarge ? 18.0 : 14.0;

    return Container(
      padding: padding,
      decoration: BoxDecoration(
        color: config.backgroundColor,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(
          color: config.borderColor,
          width: 1,
        ),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (showIcon) ...[
            Icon(
              config.icon,
              size: iconSize,
              color: config.textColor,
            ),
            const SizedBox(width: 6),
          ],
          Text(
            _getStatusLabel(),
            style: TextStyle(
              color: config.textColor,
              fontSize: fontSize,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }

  String _getStatusLabel() {
    switch (type) {
      case StatusType.trip:
        return _getTripStatusLabel();
      case StatusType.maintenance:
        return _getMaintenanceStatusLabel();
      case StatusType.payment:
        return _getPaymentStatusLabel();
      default:
        return status;
    }
  }

  String _getTripStatusLabel() {
    switch (status) {
      case 'pending':
        return AppStrings.tripPending;
      case 'accepted':
        return AppStrings.tripAccepted;
      case 'pickup':
        return AppStrings.tripPickup;
      case 'in_transit':
        return AppStrings.tripInTransit;
      case 'arrived':
        return AppStrings.tripArrived;
      case 'delivered':
        return AppStrings.tripDelivered;
      case 'completed':
        return AppStrings.tripCompleted;
      case 'cancelled':
        return AppStrings.tripCancelled;
      default:
        return status;
    }
  }

  String _getMaintenanceStatusLabel() {
    switch (status) {
      case 'scheduled':
        return 'مجدول';
      case 'in_progress':
        return 'قيد التنفيذ';
      case 'completed':
        return 'مكتمل';
      case 'cancelled':
        return 'ملغي';
      default:
        return status;
    }
  }

  String _getPaymentStatusLabel() {
    switch (status) {
      case 'pending':
        return 'معلق';
      case 'paid':
        return 'مدفوع';
      case 'failed':
        return 'فشل';
      default:
        return status;
    }
  }

  _StatusConfig _getStatusConfig() {
    switch (type) {
      case StatusType.trip:
        return _getTripStatusConfig();
      case StatusType.maintenance:
        return _getMaintenanceStatusConfig();
      case StatusType.payment:
        return _getPaymentStatusConfig();
      default:
        return _getGenericStatusConfig();
    }
  }

  _StatusConfig _getTripStatusConfig() {
    switch (status) {
      case 'pending':
        return _StatusConfig(
          backgroundColor: AppColors.warning.withOpacity(0.1),
          borderColor: AppColors.warning.withOpacity(0.3),
          textColor: AppColors.warning,
          icon: Icons.schedule,
        );
      case 'accepted':
      case 'pickup':
        return _StatusConfig(
          backgroundColor: AppColors.info.withOpacity(0.1),
          borderColor: AppColors.info.withOpacity(0.3),
          textColor: AppColors.info,
          icon: Icons.thumb_up,
        );
      case 'in_transit':
        return _StatusConfig(
          backgroundColor: AppColors.primary.withOpacity(0.1),
          borderColor: AppColors.primary.withOpacity(0.3),
          textColor: AppColors.primary,
          icon: Icons.local_shipping,
        );
      case 'arrived':
      case 'delivered':
      case 'completed':
        return _StatusConfig(
          backgroundColor: AppColors.success.withOpacity(0.1),
          borderColor: AppColors.success.withOpacity(0.3),
          textColor: AppColors.success,
          icon: Icons.check_circle,
        );
      case 'cancelled':
        return _StatusConfig(
          backgroundColor: AppColors.error.withOpacity(0.1),
          borderColor: AppColors.error.withOpacity(0.3),
          textColor: AppColors.error,
          icon: Icons.cancel,
        );
      default:
        return _getGenericStatusConfig();
    }
  }

  _StatusConfig _getMaintenanceStatusConfig() {
    switch (status) {
      case 'scheduled':
        return _StatusConfig(
          backgroundColor: AppColors.warning.withOpacity(0.1),
          borderColor: AppColors.warning.withOpacity(0.3),
          textColor: AppColors.warning,
          icon: Icons.schedule,
        );
      case 'in_progress':
        return _StatusConfig(
          backgroundColor: AppColors.info.withOpacity(0.1),
          borderColor: AppColors.info.withOpacity(0.3),
          textColor: AppColors.info,
          icon: Icons.build,
        );
      case 'completed':
        return _StatusConfig(
          backgroundColor: AppColors.success.withOpacity(0.1),
          borderColor: AppColors.success.withOpacity(0.3),
          textColor: AppColors.success,
          icon: Icons.check_circle,
        );
      case 'cancelled':
        return _StatusConfig(
          backgroundColor: AppColors.error.withOpacity(0.1),
          borderColor: AppColors.error.withOpacity(0.3),
          textColor: AppColors.error,
          icon: Icons.cancel,
        );
      default:
        return _getGenericStatusConfig();
    }
  }

  _StatusConfig _getPaymentStatusConfig() {
    switch (status) {
      case 'pending':
        return _StatusConfig(
          backgroundColor: AppColors.warning.withOpacity(0.1),
          borderColor: AppColors.warning.withOpacity(0.3),
          textColor: AppColors.warning,
          icon: Icons.schedule,
        );
      case 'paid':
        return _StatusConfig(
          backgroundColor: AppColors.success.withOpacity(0.1),
          borderColor: AppColors.success.withOpacity(0.3),
          textColor: AppColors.success,
          icon: Icons.check_circle,
        );
      case 'failed':
        return _StatusConfig(
          backgroundColor: AppColors.error.withOpacity(0.1),
          borderColor: AppColors.error.withOpacity(0.3),
          textColor: AppColors.error,
          icon: Icons.error,
        );
      default:
        return _getGenericStatusConfig();
    }
  }

  _StatusConfig _getGenericStatusConfig() {
    switch (status.toLowerCase()) {
      case 'active':
      case 'success':
      case 'completed':
      case 'done':
        return _StatusConfig(
          backgroundColor: AppColors.success.withOpacity(0.1),
          borderColor: AppColors.success.withOpacity(0.3),
          textColor: AppColors.success,
          icon: Icons.check_circle,
        );
      case 'inactive':
      case 'pending':
      case 'waiting':
        return _StatusConfig(
          backgroundColor: AppColors.warning.withOpacity(0.1),
          borderColor: AppColors.warning.withOpacity(0.3),
          textColor: AppColors.warning,
          icon: Icons.schedule,
        );
      case 'error':
      case 'failed':
      case 'cancelled':
        return _StatusConfig(
          backgroundColor: AppColors.error.withOpacity(0.1),
          borderColor: AppColors.error.withOpacity(0.3),
          textColor: AppColors.error,
          icon: Icons.error,
        );
      default:
        return _StatusConfig(
          backgroundColor: AppColors.surfaceLight,
          borderColor: AppColors.border,
          textColor: AppColors.textSecondary,
          icon: Icons.info,
        );
    }
  }
}

/// Status Types
enum StatusType {
  generic,
  trip,
  maintenance,
  payment,
}

/// Status Configuration
class _StatusConfig {
  final Color backgroundColor;
  final Color borderColor;
  final Color textColor;
  final IconData icon;

  _StatusConfig({
    required this.backgroundColor,
    required this.borderColor,
    required this.textColor,
    required this.icon,
  });
}
