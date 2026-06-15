// ============================================
// 📋 Shipments List Screen
// ============================================

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/navigation/app_router.dart';

class ShipmentsListScreen extends StatelessWidget {
  const ShipmentsListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 4,
      child: Scaffold(
        backgroundColor: AppTheme.background,
        appBar: AppBar(
          title: const Text('الشحنات'),
          centerTitle: true,
          leading: IconButton(
            icon: const Icon(Icons.arrow_back),
            onPressed: () => AppRouter.goBack(context),
          ),
          bottom: const TabBar(
            isScrollable: true,
            tabs: [
              Tab(text: 'الكل'),
              Tab(text: 'نشط'),
              Tab(text: 'مكتمل'),
              Tab(text: 'ملغي'),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            _buildShipmentsList(),
            _buildShipmentsList(filter: 'active'),
            _buildShipmentsList(filter: 'completed'),
            _buildShipmentsList(filter: 'cancelled'),
          ],
        ),
        floatingActionButton: FloatingActionButton.extended(
          onPressed: () => AppRouter.goCreateShipment(context),
          backgroundColor: AppTheme.primary,
          icon: const Icon(Icons.add),
          label: const Text('شحنة جديدة'),
        ),
      ),
    );
  }

  Widget _buildShipmentsList({String? filter}) {
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: 10,
      itemBuilder: (context, index) => _buildShipmentCard(index, filter),
    );
  }

  Widget _buildShipmentCard(int index, String? filter) {
    final statuses = ['pending', 'confirmed', 'in_transit', 'delivered', 'completed'];
    final status = statuses[index % statuses.length];
    final statusColor = AppTheme.getStatusColor(status);
    final statusLabel = AppTheme.getStatusLabel(status);

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  decoration: BoxDecoration(
                    color: statusColor.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    statusLabel,
                    style: TextStyle(
                      color: statusColor,
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                const Spacer(),
                Text(
                  '#EDH-${1000 + index}',
                  style: const TextStyle(
                    color: AppTheme.textHint,
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            
            // Route
            Row(
              children: [
                Container(
                  width: 12,
                  height: 12,
                  decoration: BoxDecoration(
                    color: AppTheme.primary,
                    borderRadius: BorderRadius.circular(6),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: Container(
                    height: 2,
                    decoration: BoxDecoration(
                      gradient: const LinearGradient(
                        colors: [AppTheme.primary, AppTheme.success],
                      ),
                      borderRadius: BorderRadius.circular(1),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                Container(
                  width: 12,
                  height: 12,
                  decoration: BoxDecoration(
                    color: AppTheme.success,
                    borderRadius: BorderRadius.circular(6),
                  ),
                ),
              ],
            ),
            
            const SizedBox(height: 12),
            Row(
              children: [
                const Expanded(
                  child: Text(
                    'الرياض',
                    style: TextStyle(
                      color: AppTheme.textSecondary,
                      fontSize: 12,
                    ),
                  ),
                ),
                Text(
                  '${index * 150 + 250} كم',
                  style: const TextStyle(
                    color: AppTheme.textHint,
                    fontSize: 12,
                  ),
                ),
                const Expanded(
                  child: Text(
                    'جدة',
                    textAlign: TextAlign.end,
                    style: TextStyle(
                      color: AppTheme.textSecondary,
                      fontSize: 12,
                    ),
                  ),
                ),
              ],
            ),
            
            const SizedBox(height: 16),
            const Divider(),
            const SizedBox(height: 12),
            
            Row(
              children: [
                _buildInfoChip(
                  Icons.scale_outlined,
                  '${(index + 1) * 500} كجم',
                ),
                const SizedBox(width: 12),
                _buildInfoChip(
                  Icons.calendar_today_outlined,
                  '2024-01-${15 + index}',
                ),
                const Spacer(),
                TextButton(
                  onPressed: () {},
                  child: const Text('التفاصيل'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoChip(IconData icon, String text) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 16, color: AppTheme.textHint),
        const SizedBox(width: 4),
        Text(
          text,
          style: const TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 12,
          ),
        ),
      ],
    );
  }
}
