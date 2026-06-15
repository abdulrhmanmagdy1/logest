import 'package:flutter/material.dart';
import '../constants/index.dart';
import 'status_badge.dart';

/// Trip List Item
/// Professional trip item for lists
class TripListItem extends StatelessWidget {
  final String id;
  final String status;
  final String origin;
  final String destination;
  final String? date;
  final double? distance;
  final double? earnings;
  final VoidCallback? onTap;

  const TripListItem({
    super.key,
    required this.id,
    required this.status,
    required this.origin,
    required this.destination,
    this.date,
    this.distance,
    this.earnings,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: AppColors.card,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: AppColors.border),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                StatusBadge(
                  status: status,
                  type: StatusType.trip,
                  isLarge: false,
                ),
                if (date != null)
                  Text(
                    date!,
                    style: TextStyle(
                      fontSize: 12,
                      color: AppColors.textMuted,
                    ),
                  ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Icon(Icons.location_on_outlined,
                    size: 16, color: AppColors.secondary),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    origin,
                    style: TextStyle(
                      fontSize: 14,
                      color: AppColors.textSecondary,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 4),
            Padding(
              padding: const EdgeInsets.only(left: 7),
              child: Container(
                width: 2,
                height: 20,
                color: AppColors.border,
              ),
            ),
            const SizedBox(height: 4),
            Row(
              children: [
                Icon(Icons.location_on,
                    size: 16, color: AppColors.primary),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    destination,
                    style: TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                      color: AppColors.textPrimary,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
            if (distance != null || earnings != null) ...[
              const SizedBox(height: 12),
              const Divider(),
              const SizedBox(height: 8),
              Row(
                children: [
                  if (distance != null) ...[
                    Icon(Icons.route,
                        size: 14, color: AppColors.textMuted),
                    const SizedBox(width: 4),
                    Text(
                      '${distance!.toStringAsFixed(1)} كم',
                      style: TextStyle(
                        fontSize: 12,
                        color: AppColors.textSecondary,
                      ),
                    ),
                  ],
                  if (distance != null && earnings != null) ...[
                    const SizedBox(width: 16),
                    Container(
                      width: 1,
                      height: 12,
                      color: AppColors.border,
                    ),
                    const SizedBox(width: 16),
                  ],
                  if (earnings != null) ...[
                    Icon(Icons.account_balance_wallet_outlined,
                        size: 14, color: AppColors.accent),
                    const SizedBox(width: 4),
                    Text(
                      '${earnings!.toStringAsFixed(2)} ر.س',
                      style: TextStyle(
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                        color: AppColors.accent,
                      ),
                    ),
                  ],
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }
}

/// Notification List Item
/// Professional notification item
class NotificationListItem extends StatelessWidget {
  final String title;
  final String? message;
  final String time;
  final IconData icon;
  final Color iconColor;
  final bool isRead;
  final VoidCallback? onTap;

  const NotificationListItem({
    super.key,
    required this.title,
    this.message,
    required this.time,
    required this.icon,
    required this.iconColor,
    this.isRead = true,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: isRead ? AppColors.card : AppColors.primary.withOpacity(0.1),
          border: Border(
            bottom: BorderSide(color: AppColors.border),
          ),
        ),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(
                color: iconColor.withOpacity(0.1),
                shape: BoxShape.circle,
              ),
              child: Icon(icon, color: iconColor, size: 20),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: TextStyle(
                      fontSize: 14,
                      fontWeight: isRead ? FontWeight.normal : FontWeight.w600,
                      color: AppColors.textPrimary,
                    ),
                  ),
                  if (message != null) ...[
                    const SizedBox(height: 4),
                    Text(
                      message!,
                      style: TextStyle(
                        fontSize: 12,
                        color: AppColors.textSecondary,
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                  const SizedBox(height: 8),
                  Text(
                    time,
                    style: TextStyle(
                      fontSize: 11,
                      color: AppColors.textMuted,
                    ),
                  ),
                ],
              ),
            ),
            if (!isRead)
              Container(
                width: 8,
                height: 8,
                decoration: BoxDecoration(
                  color: AppColors.primary,
                  shape: BoxShape.circle,
                ),
              ),
          ],
        ),
      ),
    );
  }
}

/// Menu Item
/// Professional menu item
class MenuItem extends StatelessWidget {
  final String title;
  final IconData icon;
  final VoidCallback? onTap;
  final Widget? trailing;
  final Color? iconColor;

  const MenuItem({
    super.key,
    required this.title,
    required this.icon,
    this.onTap,
    this.trailing,
    this.iconColor,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        child: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: (iconColor ?? AppColors.primary).withOpacity(0.1),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Icon(
                icon,
                color: iconColor ?? AppColors.primary,
                size: 20,
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Text(
                title,
                style: TextStyle(
                  fontSize: 15,
                  color: AppColors.textPrimary,
                ),
              ),
            ),
            if (trailing != null) trailing!,
            Icon(
              Icons.arrow_forward_ios,
              size: 16,
              color: AppColors.textMuted,
            ),
          ],
        ),
      ),
    );
  }
}
