// ============================================
// 🔧 Workshop Maintenance Portal
// Digital Service Records with Smart Maintenance System
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/widgets/premium/premium_stat_card.dart';
import '../../../../core/design_system/shadows.dart';

class WorkshopMaintenancePortal extends StatefulWidget {
  const WorkshopMaintenancePortal({super.key});

  @override
  State<WorkshopMaintenancePortal> createState() => _WorkshopMaintenancePortalState();
}

class _WorkshopMaintenancePortalState extends State<WorkshopMaintenancePortal>
    with TickerProviderStateMixin {
  late AnimationController _dashboardController;
  late AnimationController _vehiclesController;
  late AnimationController _maintenanceController;
  
  // Data
  List<VehicleMaintenance> _vehicles = [];
  List<MaintenanceRecord> _maintenanceRecords = [];
  List<PreventiveAlert> _preventiveAlerts = [];
  String _selectedFilter = 'all';
  String _selectedStatus = 'all';
  
  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _loadData();
  }

  void _initializeAnimations() {
    _dashboardController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _vehiclesController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _maintenanceController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    
    _dashboardController.forward();
    _vehiclesController.forward();
    _maintenanceController.forward();
  }

  void _loadData() {
    _vehicles = [
      VehicleMaintenance(
        id: 'VH-001',
        plateNumber: '1234-أ-ب',
        model: 'شاحنة فولفو FH16',
        type: 'شاحنة ثقيلة',
        status: VehicleStatus.active,
        lastMaintenance: DateTime.now().subtract(const Duration(days: 15)),
        nextMaintenance: DateTime.now().add(const Duration(days: 45)),
        mileage: 125000,
        fuelLevel: 85,
        engineHours: 8750,
        driverName: 'محمد أحمد',
        issues: [],
      ),
      VehicleMaintenance(
        id: 'VH-002',
        plateNumber: '5678-ج-د',
        model: 'شاحنة مرسيدس Actros',
        type: 'شاحنة متوسطة',
        status: VehicleStatus.maintenance,
        lastMaintenance: DateTime.now().subtract(const Duration(days: 2)),
        nextMaintenance: DateTime.now().add(const Duration(days: 30)),
        mileage: 98000,
        fuelLevel: 45,
        engineHours: 6200,
        driverName: 'عبدالله محمد',
        issues: ['تآكل الفرامل', 'ضغط إطار منخفض'],
      ),
      VehicleMaintenance(
        id: 'VH-003',
        plateNumber: '9012-ه-و',
        model: 'شاحنة سكانيا R450',
        type: 'شاحنة خفيفة',
        status: VehicleStatus.active,
        lastMaintenance: DateTime.now().subtract(const Duration(days: 30)),
        nextMaintenance: DateTime.now().add(const Duration(days: 60)),
        mileage: 76000,
        fuelLevel: 67,
        engineHours: 4500,
        driverName: 'سالم خالد',
        issues: [],
      ),
      VehicleMaintenance(
        id: 'VH-004',
        plateNumber: '3456-ي-ز',
        model: 'شاحنة مان TGX',
        type: 'شاحنة ثقيلة',
        status: VehicleStatus.repair,
        lastMaintenance: DateTime.now().subtract(const Duration(days: 7)),
        nextMaintenance: DateTime.now().add(const Duration(days: 15)),
        mileage: 156000,
        fuelLevel: 92,
        engineHours: 9200,
        driverName: 'خالد سعد',
        issues: ['مشكلة في المحرك', 'تسريب زيت'],
      ),
    ];

    _maintenanceRecords = [
      MaintenanceRecord(
        id: 'MR-001',
        vehicleId: 'VH-001',
        vehiclePlate: '1234-أ-ب',
        type: MaintenanceType.routine,
        description: 'تغيير زيت المحرك والفلاتر',
        date: DateTime.now().subtract(const Duration(days: 15)),
        cost: 450.0,
        mechanic: 'أحمد علي',
        parts: ['زيت محرك', 'فلاتر زيت', 'فلاتر هواء'],
        status: MaintenanceStatus.completed,
      ),
      MaintenanceRecord(
        id: 'MR-002',
        vehicleId: 'VH-002',
        vehiclePlate: '5678-ج-د',
        type: MaintenanceType.repair,
        description: 'إصلاح نظام الفرامل',
        date: DateTime.now().subtract(const Duration(days: 2)),
        cost: 1250.0,
        mechanic: 'محمد سعيد',
        parts: ['وسادات فرامل', 'سوائل فرامل'],
        status: MaintenanceStatus.completed,
      ),
      MaintenanceRecord(
        id: 'MR-003',
        vehicleId: 'VH-004',
        vehiclePlate: '3456-ي-ز',
        type: MaintenanceType.emergency,
        description: 'إصلاح تسريب زيت المحرك',
        date: DateTime.now().subtract(const Duration(days: 7)),
        cost: 2800.0,
        mechanic: 'عمر حسن',
        parts: ['جوانات المحرك', 'زيت محرك'],
        status: MaintenanceStatus.inProgress,
      ),
    ];

    _preventiveAlerts = [
      PreventiveAlert(
        id: 'PA-001',
        vehicleId: 'VH-002',
        vehiclePlate: '5678-ج-د',
        type: AlertType.oilChange,
        message: 'حان وقت تغيير زيت المحرك',
        dueDate: DateTime.now().add(const Duration(days: 7)),
        priority: AlertPriority.high,
        estimatedCost: 450.0,
      ),
      PreventiveAlert(
        id: 'PA-002',
        vehicleId: 'VH-001',
        vehiclePlate: '1234-أ-ب',
        type: AlertType.tireRotation,
        message: 'دوران الإطارات مطلوب',
        dueDate: DateTime.now().add(const Duration(days: 15)),
        priority: AlertPriority.medium,
        estimatedCost: 200.0,
      ),
      PreventiveAlert(
        id: 'PA-003',
        vehicleId: 'VH-003',
        vehiclePlate: '9012-ه-و',
        type: AlertType.brakeInspection,
        message: 'فحص الفرامل الدوري',
        dueDate: DateTime.now().add(const Duration(days: 30)),
        priority: AlertPriority.low,
        estimatedCost: 150.0,
      ),
    ];
  }

  @override
  void dispose() {
    _dashboardController.dispose();
    _vehiclesController.dispose();
    _maintenanceController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: _buildPremiumAppBar(),
      body: CustomScrollView(
        slivers: [
          // Dashboard Stats
          SliverToBoxAdapter(
            child: _buildDashboardStats(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Preventive Alerts
          SliverToBoxAdapter(
            child: _buildPreventiveAlerts(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Vehicle Status
          SliverToBoxAdapter(
            child: _buildVehicleStatus(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Maintenance Records
          SliverToBoxAdapter(
            child: _buildMaintenanceRecords(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 80)),
        ],
      ),
      floatingActionButton: GlowingButton(
        text: 'صيانة جديدة',
        onPressed: _createNewMaintenance,
        color: AppTheme.primary,
        icon: Icons.build,
      ),
    );
  }

  PreferredSizeWidget _buildPremiumAppBar() {
    return AppBar(
      backgroundColor: Colors.transparent,
      elevation: 0,
      title: Row(
        children: [
          GlassContainer(
            width: 40,
            height: 40,
            radius: 20,
            backgroundColor: AppTheme.primary.withOpacity(0.1),
            borderColor: AppTheme.primary.withOpacity(0.3),
            child: const Icon(
              Icons.build,
              color: AppTheme.primary,
              size: 20,
            ),
          ),
          const SizedBox(width: 12),
          Text(
            'بوابة الورشة',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
      actions: [
        IconButton(
          onPressed: _generateReport,
          icon: const Icon(Icons.description, color: AppTheme.primary),
        ),
        IconButton(
          onPressed: _showCalendar,
          icon: const Icon(Icons.calendar_today, color: AppTheme.primary),
        ),
        const SizedBox(width: 20),
      ],
    );
  }

  Widget _buildDashboardStats() {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'نظرة عامة على الصيانة',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ).animate(controller: _dashboardController)
            .fadeIn(delay: const Duration(milliseconds: 200)),
          
          const SizedBox(height: 20),
          
          // Stats Cards
          Row(
            children: [
              Expanded(
                child: PremiumStatCard(
                  title: 'المركبات النشطة',
                  value: '${_vehicles.where((v) => v.status == VehicleStatus.active).length}',
                  change: '2 قيد الصيانة',
                  icon: Icons.directions_car,
                  color: AppTheme.primary,
                  animationDelay: const Duration(milliseconds: 300),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'تنبيهات وقائية',
                  value: '${_preventiveAlerts.length}',
                  change: '3 عالية',
                  icon: Icons.notification_important,
                  color: AppTheme.accent,
                  animationDelay: const Duration(milliseconds: 400),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          Row(
            children: [
              Expanded(
                child: PremiumStatCard(
                  title: 'تكاليف الشهر',
                  value: '${_getMonthlyCosts().toStringAsFixed(0)} ريال',
                  change: '+12%',
                  icon: Icons.attach_money,
                  color: AppTheme.success,
                  animationDelay: const Duration(milliseconds: 500),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'معدل الإنجاز',
                  value: '94%',
                  change: '+5%',
                  icon: Icons.trending_up,
                  color: AppTheme.primary,
                  animationDelay: const Duration(milliseconds: 600),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildPreventiveAlerts() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'التنبيهات الوقائية',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              TextButton(
                onPressed: _viewAllAlerts,
                child: Text(
                  'عرض الكل',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.primary,
                  ),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Alert Cards
          ..._preventiveAlerts.take(3).map((alert) => _buildAlertCard(alert)),
        ],
      ),
    );
  }

  Widget _buildAlertCard(PreventiveAlert alert) {
    Color priorityColor;
    switch (alert.priority) {
      case AlertPriority.high:
        priorityColor = AppTheme.error;
        break;
      case AlertPriority.medium:
        priorityColor = AppTheme.accent;
        break;
      case AlertPriority.low:
        priorityColor = AppTheme.success;
        break;
    }
    
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: priorityColor.withOpacity(0.1),
      borderColor: priorityColor.withOpacity(0.3),
      child: Row(
        children: [
          Container(
            width: 40,
            height: 40,
            decoration: BoxDecoration(
              color: priorityColor.withOpacity(0.2),
              borderRadius: BorderRadius.circular(20),
            ),
            child: Icon(
              _getAlertIcon(alert.type),
              color: priorityColor,
              size: 20,
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  alert.message,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Row(
                  children: [
                    Text(
                      alert.vehiclePlate,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textSecondary,
                      ),
                    ),
                    const SizedBox(width: 12),
                    Text(
                      'خلال ${alert.dueDate.difference(DateTime.now()).inDays} يوم',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: priorityColor,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                '${alert.estimatedCost.toStringAsFixed(0)} ريال',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textSecondary,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 4),
              GlowingButton(
                text: 'تنفيذ',
                onPressed: () => _executeMaintenance(alert),
                color: priorityColor,
                height: 32,
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _maintenanceController)
      .fadeIn(delay: const Duration(milliseconds: 200));
  }

  Widget _buildVehicleStatus() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'حالة المركبات',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Row(
                children: [
                  _buildFilterChip('all', 'الكل'),
                  _buildFilterChip('active', 'نشطة'),
                  _buildFilterChip('maintenance', 'صيانة'),
                  _buildFilterChip('repair', 'إصلاح'),
                ],
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Vehicle Cards
          ..._getFilteredVehicles().map((vehicle) => _buildVehicleCard(vehicle)),
        ],
      ),
    );
  }

  Widget _buildFilterChip(String filter, String label) {
    final isSelected = _selectedFilter == filter;
    
    return Padding(
      padding: const EdgeInsets.only(left: 8),
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedFilter = filter;
          });
        },
        child: GlassContainer(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
          radius: 12,
          backgroundColor: isSelected 
            ? AppTheme.primary.withOpacity(0.2)
            : Colors.white.withOpacity(0.05),
          borderColor: isSelected 
            ? AppTheme.primary.withOpacity(0.4)
            : AppTheme.textHint.withOpacity(0.2),
          child: Text(
            label,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: isSelected ? AppTheme.primary : AppTheme.textSecondary,
              fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildVehicleCard(VehicleMaintenance vehicle) {
    Color statusColor = _getVehicleStatusColor(vehicle.status);
    
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: statusColor.withOpacity(0.2),
      child: Column(
        children: [
          // Header
          Row(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: statusColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  _getVehicleStatusText(vehicle.status),
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: statusColor,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              const Spacer(),
              Text(
                vehicle.plateNumber,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Vehicle Info
          Row(
            children: [
              Icon(
                Icons.directions_car,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Text(
                vehicle.model,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textPrimary,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 8),
          
          Row(
            children: [
              Icon(
                Icons.person,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Text(
                vehicle.driverName,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const Spacer(),
              Icon(
                Icons.local_gas_station,
                color: vehicle.fuelLevel > 50 ? AppTheme.success : AppTheme.accent,
                size: 16,
              ),
              const SizedBox(width: 4),
              Text(
                '${vehicle.fuelLevel}%',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: vehicle.fuelLevel > 50 ? AppTheme.success : AppTheme.accent,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Stats Row
          Row(
            children: [
              Expanded(
                child: _buildVehicleStat(
                  'المسافة',
                  '${vehicle.mileage.toStringAsFixed(0)} كم',
                  Icons.speed,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildVehicleStat(
                  'ساعات التشغيل',
                  '${vehicle.engineHours}',
                  Icons.timer,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildVehicleStat(
                  'الصيانة التالية',
                  '${vehicle.nextMaintenance.difference(DateTime.now()).inDays} يوم',
                  Icons.build,
                ),
              ),
            ],
          ),
          
          // Issues
          if (vehicle.issues.isNotEmpty) ...[
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: AppTheme.error.withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: AppTheme.error.withOpacity(0.3)),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(
                        Icons.warning,
                        color: AppTheme.error,
                        size: 16,
                      ),
                      const SizedBox(width: 8),
                      Text(
                        'مشاكل تحتاج إلى عناية',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: AppTheme.error,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  ...vehicle.issues.map((issue) => Padding(
                    padding: const EdgeInsets.only(bottom: 4),
                    child: Row(
                      children: [
                        Container(
                          width: 4,
                          height: 4,
                          decoration: BoxDecoration(
                            color: AppTheme.error,
                            borderRadius: BorderRadius.circular(2),
                          ),
                        ),
                        const SizedBox(width: 8),
                        Text(
                          issue,
                          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: AppTheme.textSecondary,
                          ),
                        ),
                      ],
                    ),
                  )),
                ],
              ),
            ),
          ],
          
          // Actions
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(
                child: GlowingButton(
                  text: 'تفاصيل',
                  onPressed: () => _viewVehicleDetails(vehicle),
                  color: AppTheme.primary,
                  height: 36,
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: GlowingButton(
                  text: 'صيانة',
                  onPressed: () => _scheduleMaintenance(vehicle),
                  color: AppTheme.accent,
                  height: 36,
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _vehiclesController)
      .fadeIn(delay: const Duration(milliseconds: 200))
      .slideX(begin: -0.1, end: 0);
  }

  Widget _buildVehicleStat(String label, String value, IconData icon) {
    return Column(
      children: [
        Icon(icon, color: AppTheme.textSecondary, size: 16),
        const SizedBox(height: 4),
        Text(
          value,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.w600,
          ),
        ),
        Text(
          label,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: AppTheme.textSecondary,
          ),
        ),
      ],
    );
  }

  Widget _buildMaintenanceRecords() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'سجل الصيانة',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              TextButton(
                onPressed: _viewAllRecords,
                child: Text(
                  'عرض الكل',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.primary,
                  ),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Records List
          ..._maintenanceRecords.take(3).map((record) => _buildMaintenanceRecord(record)),
        ],
      ),
    );
  }

  Widget _buildMaintenanceRecord(MaintenanceRecord record) {
    Color typeColor = _getMaintenanceTypeColor(record.type);
    
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: typeColor.withOpacity(0.2),
      child: Column(
        children: [
          // Header
          Row(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: typeColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  _getMaintenanceTypeText(record.type),
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: typeColor,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              const Spacer(),
              Text(
                record.vehiclePlate,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          // Description
          Text(
            record.description,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textPrimary,
            ),
          ),
          
          const SizedBox(height: 12),
          
          // Details
          Row(
            children: [
              Icon(
                Icons.person,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 4),
              Text(
                record.mechanic,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const SizedBox(width: 16),
              Icon(
                Icons.attach_money,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 4),
              Text(
                '${record.cost.toStringAsFixed(0)} ريال',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const Spacer(),
              Text(
                _formatDate(record.date),
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textHint,
                ),
              ),
            ],
          ),
          
          // Parts
          if (record.parts.isNotEmpty) ...[
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              runSpacing: 4,
              children: record.parts.map((part) => Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: AppTheme.textHint.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(
                  part,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: AppTheme.textHint,
                  ),
                ),
              )).toList(),
            ),
          ],
        ],
      ),
    ).animate(controller: _maintenanceController)
      .fadeIn(delay: const Duration(milliseconds: 400));
  }

  // Helper methods
  List<VehicleMaintenance> _getFilteredVehicles() {
    if (_selectedFilter == 'all') return _vehicles;
    return _vehicles.where((vehicle) {
      switch (_selectedFilter) {
        case 'active':
          return vehicle.status == VehicleStatus.active;
        case 'maintenance':
          return vehicle.status == VehicleStatus.maintenance;
        case 'repair':
          return vehicle.status == VehicleStatus.repair;
        default:
          return true;
      }
    }).toList();
  }

  Color _getVehicleStatusColor(VehicleStatus status) {
    switch (status) {
      case VehicleStatus.active:
        return AppTheme.success;
      case VehicleStatus.maintenance:
        return AppTheme.accent;
      case VehicleStatus.repair:
        return AppTheme.error;
      default:
        return AppTheme.textHint;
    }
  }

  String _getVehicleStatusText(VehicleStatus status) {
    switch (status) {
      case VehicleStatus.active:
        return 'نشط';
      case VehicleStatus.maintenance:
        return 'صيانة';
      case VehicleStatus.repair:
        return 'إصلاح';
      default:
        return 'غير معروف';
    }
  }

  Color _getMaintenanceTypeColor(MaintenanceType type) {
    switch (type) {
      case MaintenanceType.routine:
        return AppTheme.success;
      case MaintenanceType.repair:
        return AppTheme.accent;
      case MaintenanceType.emergency:
        return AppTheme.error;
      default:
        return AppTheme.textHint;
    }
  }

  String _getMaintenanceTypeText(MaintenanceType type) {
    switch (type) {
      case MaintenanceType.routine:
        return 'روتيني';
      case MaintenanceType.repair:
        return 'إصلاح';
      case MaintenanceType.emergency:
        return 'طوارئ';
      default:
        return 'غير معروف';
    }
  }

  IconData _getAlertIcon(AlertType type) {
    switch (type) {
      case AlertType.oilChange:
        return Icons.local_gas_station;
      case AlertType.tireRotation:
        return Icons.rotate_right;
      case AlertType.brakeInspection:
        return Icons.build;
      default:
        return Icons.warning;
    }
  }

  double _getMonthlyCosts() {
    final now = DateTime.now();
    return _maintenanceRecords
        .where((record) => record.date.month == now.month && record.date.year == now.year)
        .fold(0.0, (sum, record) => sum + record.cost);
  }

  String _formatDate(DateTime date) {
    final now = DateTime.now();
    final difference = now.difference(date);
    
    if (difference.inDays == 0) {
      return 'اليوم';
    } else if (difference.inDays == 1) {
      return 'أمس';
    } else if (difference.inDays < 7) {
      return 'منذ ${difference.inDays} أيام';
    } else if (difference.inDays < 30) {
      return 'منذ ${(difference.inDays / 7).floor()} أسابيع';
    } else {
      return 'منذ ${(difference.inDays / 30).floor()} شهور';
    }
  }

  // Action methods
  void _createNewMaintenance() {
    // Navigate to create maintenance screen
  }

  void _generateReport() {
    // Generate maintenance report
  }

  void _showCalendar() {
    // Show maintenance calendar
  }

  void _viewAllAlerts() {
    // Navigate to all alerts screen
  }

  void _executeMaintenance(PreventiveAlert alert) {
    // Execute preventive maintenance
  }

  void _viewVehicleDetails(VehicleMaintenance vehicle) {
    // Navigate to vehicle details
  }

  void _scheduleMaintenance(VehicleMaintenance vehicle) {
    // Schedule maintenance for vehicle
  }

  void _viewAllRecords() {
    // Navigate to all maintenance records
  }
}

// Data models
enum VehicleStatus { active, maintenance, repair }
enum MaintenanceType { routine, repair, emergency }
enum MaintenanceStatus { pending, inProgress, completed }
enum AlertType { oilChange, tireRotation, brakeInspection }
enum AlertPriority { high, medium, low }

class VehicleMaintenance {
  String id;
  String plateNumber;
  String model;
  String type;
  VehicleStatus status;
  DateTime lastMaintenance;
  DateTime nextMaintenance;
  double mileage;
  int fuelLevel;
  int engineHours;
  String driverName;
  List<String> issues;

  VehicleMaintenance({
    required this.id,
    required this.plateNumber,
    required this.model,
    required this.type,
    required this.status,
    required this.lastMaintenance,
    required this.nextMaintenance,
    required this.mileage,
    required this.fuelLevel,
    required this.engineHours,
    required this.driverName,
    required this.issues,
  });
}

class MaintenanceRecord {
  String id;
  String vehicleId;
  String vehiclePlate;
  MaintenanceType type;
  String description;
  DateTime date;
  double cost;
  String mechanic;
  List<String> parts;
  MaintenanceStatus status;

  MaintenanceRecord({
    required this.id,
    required this.vehicleId,
    required this.vehiclePlate,
    required this.type,
    required this.description,
    required this.date,
    required this.cost,
    required this.mechanic,
    required this.parts,
    required this.status,
  });
}

class PreventiveAlert {
  String id;
  String vehicleId;
  String vehiclePlate;
  AlertType type;
  String message;
  DateTime dueDate;
  AlertPriority priority;
  double estimatedCost;

  PreventiveAlert({
    required this.id,
    required this.vehicleId,
    required this.vehiclePlate,
    required this.type,
    required this.message,
    required this.dueDate,
    required this.priority,
    required this.estimatedCost,
  });
}
