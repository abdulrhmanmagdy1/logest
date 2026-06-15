// ============================================
// 🔧 Workshop Dashboard - لوحة الورشة
// ============================================

import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';

class WorkshopDashboardScreen extends StatelessWidget {
  const WorkshopDashboardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'نظام الورشة',
          style: TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            onPressed: () {},
            icon: const Icon(Icons.notifications, color: Colors.white),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Workshop Header
            _buildHeader(),
            
            const SizedBox(height: 24),
            
            // Fleet Health
            _buildFleetHealth(),
            
            const SizedBox(height: 24),
            
            // Maintenance Overview
            _buildMaintenanceOverview(),
            
            const SizedBox(height: 24),
            
            // Pending Requests
            _buildPendingRequests(),
            
            const SizedBox(height: 24),
            
            // Vehicles in Maintenance
            _buildVehiclesInMaintenance(),
            
            const SizedBox(height: 24),
            
            // Parts Inventory Alert
            _buildPartsInventory(),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {},
        backgroundColor: Colors.orange,
        icon: const Icon(Icons.add),
        label: const Text('طلب صيانة'),
      ),
    );
  }

  Widget _buildHeader() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [Colors.orange, Colors.orange.shade800],
        ),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.2),
              borderRadius: BorderRadius.circular(12),
            ),
            child: const Icon(
              Icons.build,
              color: Colors.white,
              size: 32,
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'ورشة الصيانة الرئيسية',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  '5 فنيين متاحين',
                  style: TextStyle(
                    color: Colors.white.withOpacity(0.8),
                    fontSize: 14,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFleetHealth() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'صحة الأسطول',
          style: TextStyle(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            Expanded(
              child: _HealthCard(
                title: 'حالة ممتازة',
                value: '24',
                icon: Icons.check_circle,
                color: AppTheme.successColor,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _HealthCard(
                title: 'يحتاج اهتمام',
                value: '8',
                icon: Icons.warning,
                color: AppTheme.warningColor,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _HealthCard(
                title: 'صيانة عاجلة',
                value: '3',
                icon: Icons.error,
                color: AppTheme.errorColor,
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildMaintenanceOverview() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'نظرة عامة على الصيانة',
            style: TextStyle(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 20),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              _MaintenanceStat(
                icon: Icons.oil_barrel,
                label: 'تغيير زيت',
                value: '5',
                color: Colors.blue,
              ),
              _MaintenanceStat(
                icon: Icons.tire_repair,
                label: 'كشف إطارات',
                value: '3',
                color: Colors.orange,
              ),
              _MaintenanceStat(
                icon: Icons.ac_unit,
                label: 'تبريد',
                value: '2',
                color: Colors.cyan,
              ),
              _MaintenanceStat(
                icon: Icons.car_crash,
                label: 'أعطال',
                value: '1',
                color: Colors.red,
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildPendingRequests() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'طلبات الصيانة المعلقة',
              style: TextStyle(
                color: Colors.white,
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: AppTheme.warningColor.withOpacity(0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text(
                '4 جديد',
                style: TextStyle(
                  color: AppTheme.warningColor,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        _MaintenanceRequestCard(
          vehiclePlate: 'R-234',
          vehicleType: 'شاحنة تبريد',
          issue: 'ارتفاع درجة حرارة المحرك',
          priority: 'critical',
          reportedBy: 'السائق خالد',
          timeAgo: 'منذ 30 دقيقة',
        ),
        _MaintenanceRequestCard(
          vehiclePlate: 'J-567',
          vehicleType: 'شاحنة صغيرة',
          issue: 'تغيير زيت + فلتر',
          priority: 'normal',
          reportedBy: 'السائق أحمد',
          timeAgo: 'منذ ساعتين',
        ),
      ],
    );
  }

  Widget _buildVehiclesInMaintenance() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'مركبات في الصيانة',
              style: TextStyle(
                color: Colors.white,
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            TextButton(
              onPressed: () {},
              child: const Text('عرض الكل'),
            ),
          ],
        ),
        const SizedBox(height: 12),
        _InMaintenanceCard(
          vehiclePlate: 'D-123',
          maintenanceType: 'صيانة دورية 20,000 كم',
          progress: 0.75,
          estimatedCompletion: '3 ساعات',
          technician: 'فهد الفني',
        ),
        _InMaintenanceCard(
          vehiclePlate: 'R-789',
          maintenanceType: 'إصلاح مكيف التبريد',
          progress: 0.3,
          estimatedCompletion: 'يومين',
          technician: 'ناصر الفني',
        ),
      ],
  );
  }

  Widget _buildPartsInventory() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'مخزون قطع الغيار',
          style: TextStyle(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 12),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: AppTheme.cardBackground,
            borderRadius: BorderRadius.circular(16),
          ),
          child: Column(
            children: [
              _PartInventoryItem(
                name: 'فلاتر زيت',
                quantity: 12,
                minRequired: 15,
                unit: 'قطعة',
              ),
              const Divider(color: AppTheme.dividerColor, height: 24),
              _PartInventoryItem(
                name: 'إطارات',
                quantity: 8,
                minRequired: 20,
                unit: 'إطار',
              ),
              const Divider(color: AppTheme.dividerColor, height: 24),
              _PartInventoryItem(
                name: 'سائل تبريد',
                quantity: 25,
                minRequired: 10,
                unit: 'لتر',
              ),
            ],
          ),
        ),
      ],
    );
  }
}

class _HealthCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  final Color color;

  const _HealthCard({
    required this.title,
    required this.value,
    required this.icon,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Column(
        children: [
          Icon(icon, color: color, size: 28),
          const SizedBox(height: 8),
          Text(
            value,
            style: TextStyle(
              color: color,
              fontSize: 24,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            title,
            style: TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 11,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }
}

class _MaintenanceStat extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;
  final Color color;

  const _MaintenanceStat({
    required this.icon,
    required this.label,
    required this.value,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            color: color.withOpacity(0.1),
            borderRadius: BorderRadius.circular(12),
          ),
          child: Icon(icon, color: color, size: 24),
        ),
        const SizedBox(height: 8),
        Text(
          value,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          label,
          style: TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 11,
          ),
        ),
      ],
    );
  }
}

class _MaintenanceRequestCard extends StatelessWidget {
  final String vehiclePlate;
  final String vehicleType;
  final String issue;
  final String priority;
  final String reportedBy;
  final String timeAgo;

  const _MaintenanceRequestCard({
    required this.vehiclePlate,
    required this.vehicleType,
    required this.issue,
    required this.priority,
    required this.reportedBy,
    required this.timeAgo,
  });

  @override
  Widget build(BuildContext context) {
    final isCritical = priority == 'critical';
    
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: isCritical 
          ? AppTheme.errorColor.withOpacity(0.1) 
          : AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: isCritical 
            ? AppTheme.errorColor.withOpacity(0.3) 
            : AppTheme.dividerColor,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: Colors.orange.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(
                      vehiclePlate,
                      style: TextStyle(
                        color: Colors.orange,
                        fontSize: 14,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Text(
                    vehicleType,
                    style: TextStyle(
                      color: AppTheme.textSecondary,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
              if (isCritical)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: AppTheme.errorColor,
                    borderRadius: BorderRadius.circular(6),
                  ),
                  child: const Text(
                    'عاجل',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 10,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              Icon(
                isCritical ? Icons.error : Icons.build,
                color: isCritical ? AppTheme.errorColor : Colors.orange,
                size: 20,
              ),
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  issue,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Icon(Icons.person, color: AppTheme.textSecondary, size: 16),
                  const SizedBox(width: 4),
                  Text(
                    reportedBy,
                    style: TextStyle(
                      color: AppTheme.textSecondary,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
              Row(
                children: [
                  Icon(Icons.schedule, color: AppTheme.textSecondary, size: 16),
                  const SizedBox(width: 4),
                  Text(
                    timeAgo,
                    style: TextStyle(
                      color: AppTheme.textSecondary,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              Expanded(
                child: ElevatedButton(
                  onPressed: () {},
                  style: ElevatedButton.styleFrom(
                    backgroundColor: isCritical ? AppTheme.errorColor : Colors.orange,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(vertical: 10),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                  ),
                  child: const Text('بدء الصيانة'),
                ),
              ),
              const SizedBox(width: 8),
              OutlinedButton(
                onPressed: () {},
                style: OutlinedButton.styleFrom(
                  foregroundColor: AppTheme.textSecondary,
                  side: BorderSide(color: AppTheme.dividerColor),
                  padding: const EdgeInsets.symmetric(vertical: 10),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                child: const Text('تفاصيل'),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _InMaintenanceCard extends StatelessWidget {
  final String vehiclePlate;
  final String maintenanceType;
  final double progress;
  final String estimatedCompletion;
  final String technician;

  const _InMaintenanceCard({
    required this.vehiclePlate,
    required this.maintenanceType,
    required this.progress,
    required this.estimatedCompletion,
    required this.technician,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: Colors.orange.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(
                      vehiclePlate,
                      style: TextStyle(
                        color: Colors.orange,
                        fontSize: 14,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: AppTheme.primaryColor.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(6),
                    ),
                    child: Text(
                      '${(progress * 100).toInt()}%',
                      style: TextStyle(
                        color: AppTheme.primaryColor,
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            maintenanceType,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 14,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 12),
          LinearProgressIndicator(
            value: progress,
            backgroundColor: AppTheme.dividerColor,
            valueColor: AlwaysStoppedAnimation<Color>(Colors.orange),
            minHeight: 6,
            borderRadius: BorderRadius.circular(3),
          ),
          const SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Icon(Icons.person, color: AppTheme.textSecondary, size: 16),
                  const SizedBox(width: 4),
                  Text(
                    technician,
                    style: TextStyle(
                      color: AppTheme.textSecondary,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
              Row(
                children: [
                  Icon(Icons.schedule, color: Colors.orange, size: 16),
                  const SizedBox(width: 4),
                  Text(
                    'الانتهاء: $estimatedCompletion',
                    style: TextStyle(
                      color: Colors.orange,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _PartInventoryItem extends StatelessWidget {
  final String name;
  final int quantity;
  final int minRequired;
  final String unit;

  const _PartInventoryItem({
    required this.name,
    required this.quantity,
    required this.minRequired,
    required this.unit,
  });

  @override
  Widget build(BuildContext context) {
    final isLow = quantity < minRequired;
    
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Row(
          children: [
            Icon(
              isLow ? Icons.warning : Icons.check_circle,
              color: isLow ? AppTheme.warningColor : AppTheme.successColor,
              size: 20,
            ),
            const SizedBox(width: 12),
            Text(
              name,
              style: const TextStyle(
                color: Colors.white,
                fontSize: 14,
              ),
            ),
          ],
        ),
        Row(
          children: [
            Text(
              '$quantity $unit',
              style: TextStyle(
                color: isLow ? AppTheme.warningColor : Colors.white,
                fontSize: 14,
                fontWeight: isLow ? FontWeight.bold : FontWeight.normal,
              ),
            ),
            if (isLow)
              Container(
                margin: const EdgeInsets.only(right: 8),
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: AppTheme.warningColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  'منخفض',
                  style: TextStyle(
                    color: AppTheme.warningColor,
                    fontSize: 10,
                  ),
                ),
              ),
          ],
        ),
      ],
    );
  }
}
