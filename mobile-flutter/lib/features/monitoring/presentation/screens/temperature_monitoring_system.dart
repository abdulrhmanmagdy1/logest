// ============================================
// 🌡️ Temperature Monitoring System
// Real-time Temperature Tracking with Alerts
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/widgets/premium/premium_stat_card.dart';
import '../../../../core/design_system/shadows.dart';

class TemperatureMonitoringSystem extends StatefulWidget {
  const TemperatureMonitoringSystem({super.key});

  @override
  State<TemperatureMonitoringSystem> createState() => _TemperatureMonitoringSystemState();
}

class _TemperatureMonitoringSystemState extends State<TemperatureMonitoringSystem>
    with TickerProviderStateMixin {
  late AnimationController _dashboardController;
  late AnimationController _chartsController;
  late AnimationController _alertsController;
  
  // Data
  List<MonitoredShipment> _shipments = [];
  List<TemperatureAlert> _alerts = [];
  List<TemperatureReading> _readings = [];
  String _selectedShipment = 'all';
  String _selectedStatus = 'all';
  
  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _loadData();
    _startRealTimeMonitoring();
  }

  void _initializeAnimations() {
    _dashboardController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _chartsController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _alertsController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    
    _dashboardController.forward();
    _chartsController.forward();
    _alertsController.forward();
  }

  void _loadData() {
    _shipments = [
      MonitoredShipment(
        id: 'EDH-1001',
        customerName: 'شركة الأدوية الطبية',
        route: 'الرياض → جدة',
        currentTemperature: 4.2,
        targetTemperature: 4.0,
        minTemperature: 2.0,
        maxTemperature: 8.0,
        status: TemperatureStatus.normal,
        lastUpdate: DateTime.now(),
        driverName: 'محمد أحمد',
        vehicleId: 'VH-123',
        temperatureType: TemperatureType.chilled,
        alertCount: 0,
      ),
      MonitoredShipment(
        id: 'EDH-1002',
        customerName: 'مستودع الأغذية الفاخرة',
        route: 'جدة → الدمام',
        currentTemperature: -18.5,
        targetTemperature: -18.0,
        minTemperature: -25.0,
        maxTemperature: -15.0,
        status: TemperatureStatus.warning,
        lastUpdate: DateTime.now().subtract(const Duration(minutes: 5)),
        driverName: 'عبدالله محمد',
        vehicleId: 'VH-456',
        temperatureType: TemperatureType.frozen,
        alertCount: 2,
      ),
      MonitoredShipment(
        id: 'EDH-1003',
        customerName: 'شركة المواد الكيميائية',
        route: 'مكة → المدينة',
        currentTemperature: 8.7,
        targetTemperature: 8.0,
        minTemperature: 6.0,
        maxTemperature: 12.0,
        status: TemperatureStatus.critical,
        lastUpdate: DateTime.now().subtract(const Duration(minutes: 2)),
        driverName: 'سالم خالد',
        vehicleId: 'VH-789',
        temperatureType: TemperatureType.cold,
        alertCount: 5,
      ),
      MonitoredShipment(
        id: 'EDH-1004',
        customerName: 'شركة التبريد المركزي',
        route: 'القصيم → حائل',
        currentTemperature: -25.2,
        targetTemperature: -25.0,
        minTemperature: -30.0,
        maxTemperature: -20.0,
        status: TemperatureStatus.normal,
        lastUpdate: DateTime.now(),
        driverName: 'خالد سعد',
        vehicleId: 'VH-101',
        temperatureType: TemperatureType.deepFrozen,
        alertCount: 0,
      ),
    ];

    _alerts = [
      TemperatureAlert(
        id: 'ALERT-001',
        shipmentId: 'EDH-1002',
        shipmentName: 'مستودع الأغذية الفاخرة',
        type: AlertType.warning,
        message: 'درجة الحرارة أعلى من الحد الأقصى',
        temperature: -14.5,
        threshold: -15.0,
        timestamp: DateTime.now().subtract(const Duration(minutes: 15)),
        isResolved: false,
      ),
      TemperatureAlert(
        id: 'ALERT-002',
        shipmentId: 'EDH-1003',
        shipmentName: 'شركة المواد الكيميائية',
        type: AlertType.critical,
        message: 'درجة الحرارة تجاوزت الحد المسموح',
        temperature: 13.2,
        threshold: 12.0,
        timestamp: DateTime.now().subtract(const Duration(minutes: 5)),
        isResolved: false,
      ),
      TemperatureAlert(
        id: 'ALERT-003',
        shipmentId: 'EDH-1002',
        shipmentName: 'مستودع الأغذية الفاخرة',
        type: AlertType.warning,
        message: 'تقلبات مفاجئة في درجة الحرارة',
        temperature: -19.8,
        threshold: -18.0,
        timestamp: DateTime.now().subtract(const Duration(minutes: 30)),
        isResolved: true,
      ),
    ];

    // Generate historical readings
    _generateHistoricalReadings();
  }

  void _generateHistoricalReadings() {
    final now = DateTime.now();
    for (int i = 0; i < 24; i++) {
      for (final shipment in _shipments) {
        _readings.add(TemperatureReading(
          shipmentId: shipment.id,
          temperature: _generateRealisticTemperature(shipment.targetTemperature),
          timestamp: now.subtract(Duration(hours: i)),
        ));
      }
    }
  }

  double _generateRealisticTemperature(double target) {
    final random = (target - 2) + (4 * (DateTime.now().millisecond % 100) / 100);
    return random;
  }

  void _startRealTimeMonitoring() {
    Future.delayed(const Duration(seconds: 5), () {
      if (mounted) {
        _updateRealTimeData();
      }
    });
  }

  void _updateRealTimeData() {
    setState(() {
      // Update temperatures with realistic fluctuations
      for (var shipment in _shipments) {
        final fluctuation = (DateTime.now().millisecond % 10 - 5) * 0.1;
        shipment.currentTemperature = (shipment.targetTemperature + fluctuation)
            .clamp(shipment.minTemperature, shipment.maxTemperature);
        
        // Update status based on temperature
        if (shipment.currentTemperature > shipment.maxTemperature ||
            shipment.currentTemperature < shipment.minTemperature) {
          shipment.status = TemperatureStatus.critical;
        } else if (shipment.currentTemperature > shipment.maxTemperature - 2 ||
                   shipment.currentTemperature < shipment.minTemperature + 2) {
          shipment.status = TemperatureStatus.warning;
        } else {
          shipment.status = TemperatureStatus.normal;
        }
        
        shipment.lastUpdate = DateTime.now();
      }
      
      // Add new reading
      for (final shipment in _shipments) {
        _readings.add(TemperatureReading(
          shipmentId: shipment.id,
          temperature: shipment.currentTemperature,
          timestamp: DateTime.now(),
        ));
      }
      
      // Keep only last 100 readings per shipment
      if (_readings.length > 400) {
        _readings.removeRange(0, _readings.length - 400);
      }
    });
    
    // Schedule next update
    Future.delayed(const Duration(seconds: 3), () {
      if (mounted) {
        _updateRealTimeData();
      }
    });
  }

  @override
  void dispose() {
    _dashboardController.dispose();
    _chartsController.dispose();
    _alertsController.dispose();
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
          
          // Temperature Charts
          SliverToBoxAdapter(
            child: _buildTemperatureCharts(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Active Alerts
          SliverToBoxAdapter(
            child: _buildActiveAlerts(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Monitored Shipments
          SliverToBoxAdapter(
            child: _buildMonitoredShipments(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 80)),
        ],
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
              Icons.thermostat,
              color: AppTheme.primary,
              size: 20,
            ),
          ),
          const SizedBox(width: 12),
          Text(
            'مراقبة درجة الحرارة',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
      actions: [
        IconButton(
          onPressed: _exportData,
          icon: const Icon(Icons.download, color: AppTheme.primary),
        ),
        IconButton(
          onPressed: _showSettings,
          icon: const Icon(Icons.settings, color: AppTheme.primary),
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
            'نظرة عامة على درجة الحرارة',
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
                  title: 'الشحنات النشطة',
                  value: '${_shipments.length}',
                  change: 'جميعها مراقبة',
                  icon: Icons.local_shipping,
                  color: AppTheme.primary,
                  animationDelay: const Duration(milliseconds: 300),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'التنبيهات النشطة',
                  value: '${_alerts.where((a) => !a.isResolved).length}',
                  change: '${_alerts.where((a) => a.type == AlertType.critical && !a.isResolved).length} حرجة',
                  icon: Icons.warning,
                  color: AppTheme.error,
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
                  title: 'متوسط درجة الحرارة',
                  value: '${_getAverageTemperature().toStringAsFixed(1)}°م',
                  change: 'طبيعي',
                  icon: Icons.thermostat,
                  color: AppTheme.success,
                  animationDelay: const Duration(milliseconds: 500),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'استقرار النظام',
                  value: '96%',
                  change: '+2%',
                  icon: Icons.speed,
                  color: AppTheme.accent,
                  animationDelay: const Duration(milliseconds: 600),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildTemperatureCharts() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: GlassContainer(
        padding: const EdgeInsets.all(20),
        radius: 20,
        backgroundColor: Colors.white.withOpacity(0.05),
        borderColor: AppTheme.primary.withOpacity(0.2),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'رسوم بيانية لدرجة الحرارة',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            
            const SizedBox(height: 20),
            
            // Temperature Graph Placeholder
            Container(
              height: 250,
              decoration: BoxDecoration(
                color: AppTheme.surface.withOpacity(0.3),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: AppTheme.textHint.withOpacity(0.2)),
              ),
              child: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.show_chart,
                      color: AppTheme.textHint,
                      size: 48,
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'رسم بياني لدرجة الحرارة على مدار 24 ساعة',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.textHint,
                      ),
                    ),
                    Text(
                      'مراقبة مستمرة لاستقرار درجة الحرارة',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textSecondary,
                      ),
                    ),
                  ],
                ),
              ),
            ),
            
            const SizedBox(height: 20),
            
            // Chart Controls
            Row(
              children: [
                Expanded(
                child: _buildChartControl('24 ساعة', '24h'),
              ),
              const SizedBox(width: 8),
                Expanded(
                child: _buildChartControl('7 أيام', '7d'),
              ),
              const SizedBox(width: 8),
                Expanded(
                child: _buildChartControl('30 يوم', '30d'),
                ),
              ],
            ),
          ],
        ),
      ).animate(controller: _chartsController)
        .fadeIn(delay: const Duration(milliseconds: 200)),
    );
  }

  Widget _buildChartControl(String label, String period) {
    return GlassContainer(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      radius: 12,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.textHint.withOpacity(0.2),
      child: Center(
        child: Text(
          label,
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: AppTheme.textSecondary,
          ),
        ),
      ),
    );
  }

  Widget _buildActiveAlerts() {
    final activeAlerts = _alerts.where((alert) => !alert.isResolved).toList();
    
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'التنبيهات النشطة',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              TextButton(
                onPressed: _viewAllAlerts,
                child: Text(
                  'عرض الكل (${activeAlerts.length})',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.primary,
                  ),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Alert Cards
          ...activeAlerts.take(3).map((alert) => _buildAlertCard(alert)),
        ],
      ),
    );
  }

  Widget _buildAlertCard(TemperatureAlert alert) {
    Color alertColor = _getAlertTypeColor(alert.type);
    
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: alertColor.withOpacity(0.1),
      borderColor: alertColor.withOpacity(0.3),
      child: Row(
        children: [
          Container(
            width: 40,
            height: 40,
            decoration: BoxDecoration(
              color: alertColor.withOpacity(0.2),
              borderRadius: BorderRadius.circular(20),
            ),
            child: Icon(
              _getAlertTypeIcon(alert.type),
              color: alertColor,
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
                      alert.shipmentName,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textSecondary,
                      ),
                    ),
                    const SizedBox(width: 12),
                    Text(
                      '${alert.temperature.toStringAsFixed(1)}°م',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: alertColor,
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
                _formatTime(alert.timestamp),
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textHint,
                ),
              ),
              const SizedBox(height: 4),
              GlowingButton(
                text: 'معالجة',
                onPressed: () => _handleAlert(alert),
                color: alertColor,
                height: 32,
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _alertsController)
      .fadeIn(delay: const Duration(milliseconds: 200));
  }

  Widget _buildMonitoredShipments() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'الشحنات المراقبة',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Row(
                children: [
                  _buildFilterChip('all', 'الكل'),
                  _buildFilterChip('normal', 'طبيعي'),
                  _buildFilterChip('warning', 'تحذير'),
                  _buildFilterChip('critical', 'حرج'),
                ],
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Shipment Cards
          ..._getFilteredShipments().map((shipment) => _buildShipmentCard(shipment)),
        ],
      ),
    );
  }

  Widget _buildFilterChip(String filter, String label) {
    final isSelected = _selectedStatus == filter;
    
    return Padding(
      padding: const EdgeInsets.only(left: 8),
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedStatus = filter;
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

  Widget _buildShipmentCard(MonitoredShipment shipment) {
    Color statusColor = _getTemperatureStatusColor(shipment.status);
    
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
                  _getTemperatureStatusText(shipment.status),
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: statusColor,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              const Spacer(),
              Text(
                shipment.id,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Customer and Route
          Row(
            children: [
              Icon(
                Icons.business,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Text(
                shipment.customerName,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textPrimary,
                ),
              ),
              const Spacer(),
              Icon(
                Icons.route,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 4),
              Text(
                shipment.route,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Temperature Display
          Row(
            children: [
              Expanded(
                child: _buildTemperatureDisplay(
                  'الحرارة الحالية',
                  shipment.currentTemperature,
                  shipment.targetTemperature,
                  shipment.minTemperature,
                  shipment.maxTemperature,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: _buildTemperatureInfo(
                  'النوع',
                  _getTemperatureTypeText(shipment.temperatureType),
                  Icons.ac_unit,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Driver and Vehicle Info
          Row(
            children: [
              Icon(
                Icons.person,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Text(
                shipment.driverName,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const Spacer(),
              Icon(
                Icons.directions_car,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 4),
              Text(
                shipment.vehicleId,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const Spacer(),
              Text(
                'التحديث: ${_formatTime(shipment.lastUpdate)}',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textHint,
                ),
              ),
            ],
          ),
          
          // Alert Count
          if (shipment.alertCount > 0) ...[
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: AppTheme.error.withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: AppTheme.error.withOpacity(0.3)),
              ),
              child: Row(
                children: [
                  Icon(
                    Icons.warning,
                    color: AppTheme.error,
                    size: 16,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    '${shipment.alertCount} تنبيهات نشطة',
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: AppTheme.error,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const Spacer(),
                  GlowingButton(
                    text: 'عرض',
                    onPressed: () => _viewShipmentAlerts(shipment),
                    color: AppTheme.error,
                    height: 32,
                  ),
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
                  onPressed: () => _viewShipmentDetails(shipment),
                  color: AppTheme.primary,
                  height: 36,
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: GlowingButton(
                  text: 'رسوم بيانية',
                  onPressed: () => _viewShipmentChart(shipment),
                  color: AppTheme.accent,
                  height: 36,
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate().fadeIn(delay: const Duration(milliseconds: 200)).slideX(begin: -0.1, end: 0);
  }

  Widget _buildTemperatureDisplay(
    String label,
    double current,
    double target,
    double min,
    double max,
  ) {
    Color tempColor = _getTemperatureColor(current, target, min, max);
    
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: AppTheme.textSecondary,
          ),
        ),
        const SizedBox(height: 8),
        Row(
          children: [
            Text(
              '${current.toStringAsFixed(1)}°م',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: tempColor,
                fontWeight: FontWeight.bold,
              ),
            ),
            const Spacer(),
            Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text(
                  'الهدف: ${target.toStringAsFixed(1)}°م',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: AppTheme.textSecondary,
                  ),
                ),
                Text(
                  'نطاق: ${min.toStringAsFixed(1)}° - ${max.toStringAsFixed(1)}°',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: AppTheme.textHint,
                  ),
                ),
              ],
            ),
          ],
        ),
        const SizedBox(height: 8),
        // Temperature Bar
        Container(
          height: 8,
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.1),
            borderRadius: BorderRadius.circular(4),
          ),
          child: FractionallySizedBox(
            alignment: Alignment.centerLeft,
            widthFactor: ((current - min) / (max - min)).clamp(0.0, 1.0),
            child: Container(
              decoration: BoxDecoration(
                color: tempColor,
                borderRadius: BorderRadius.circular(4),
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildTemperatureInfo(String label, String value, IconData icon) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: AppTheme.textSecondary,
          ),
        ),
        const SizedBox(height: 8),
        Row(
          children: [
            Icon(icon, color: AppTheme.primary, size: 20),
            const SizedBox(width: 8),
            Text(
              value,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
      ],
    );
  }

  // Helper methods
  List<MonitoredShipment> _getFilteredShipments() {
    if (_selectedStatus == 'all') return _shipments;
    return _shipments.where((shipment) {
      switch (_selectedStatus) {
        case 'normal':
          return shipment.status == TemperatureStatus.normal;
        case 'warning':
          return shipment.status == TemperatureStatus.warning;
        case 'critical':
          return shipment.status == TemperatureStatus.critical;
        default:
          return true;
      }
    }).toList();
  }

  double _getAverageTemperature() {
    if (_shipments.isEmpty) return 0.0;
    return _shipments.fold(0.0, (sum, shipment) => sum + shipment.currentTemperature) / _shipments.length;
  }

  Color _getTemperatureStatusColor(TemperatureStatus status) {
    switch (status) {
      case TemperatureStatus.normal:
        return AppTheme.success;
      case TemperatureStatus.warning:
        return AppTheme.accent;
      case TemperatureStatus.critical:
        return AppTheme.error;
      default:
        return AppTheme.textHint;
    }
  }

  String _getTemperatureStatusText(TemperatureStatus status) {
    switch (status) {
      case TemperatureStatus.normal:
        return 'طبيعي';
      case TemperatureStatus.warning:
        return 'تحذير';
      case TemperatureStatus.critical:
        return 'حرج';
      default:
        return 'غير معروف';
    }
  }

  Color _getAlertTypeColor(AlertType type) {
    switch (type) {
      case AlertType.info:
        return AppTheme.primary;
      case AlertType.warning:
        return AppTheme.accent;
      case AlertType.critical:
        return AppTheme.error;
      default:
        return AppTheme.textHint;
    }
  }

  IconData _getAlertTypeIcon(AlertType type) {
    switch (type) {
      case AlertType.info:
        return Icons.info;
      case AlertType.warning:
        return Icons.warning;
      case AlertType.critical:
        return Icons.error;
      default:
        return Icons.notifications;
    }
  }

  Color _getTemperatureColor(double current, double target, double min, double max) {
    if (current > max || current < min) {
      return AppTheme.error;
    } else if (current > max - 2 || current < min + 2) {
      return AppTheme.accent;
    } else {
      return AppTheme.success;
    }
  }

  String _getTemperatureTypeText(TemperatureType type) {
    switch (type) {
      case TemperatureType.frozen:
        return 'مجمد (-18°م)';
      case TemperatureType.deepFrozen:
        return 'مجمد عميق (-25°م)';
      case TemperatureType.chilled:
        return 'مبرد (4°م)';
      case TemperatureType.cold:
        return 'بارد (8°م)';
      case TemperatureType.ambient:
        return 'درجة غرفة';
      default:
        return 'غير معروف';
    }
  }

  String _formatTime(DateTime dateTime) {
    final now = DateTime.now();
    final difference = now.difference(dateTime);
    
    if (difference.inMinutes == 0) {
      return 'الآن';
    } else if (difference.inMinutes < 60) {
      return 'منذ ${difference.inMinutes} دقيقة';
    } else if (difference.inHours < 24) {
      return 'منذ ${difference.inHours} ساعة';
    } else {
      return 'منذ ${difference.inDays} يوم';
    }
  }

  // Action methods
  void _exportData() {
    // Export temperature data
  }

  void _showSettings() {
    // Show temperature monitoring settings
  }

  void _viewAllAlerts() {
    // Navigate to all alerts screen
  }

  void _handleAlert(TemperatureAlert alert) {
    setState(() {
      alert.isResolved = true;
    });
    
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        backgroundColor: AppTheme.success,
        content: Text('تم معالجة التنبيه بنجاح'),
      ),
    );
  }

  void _viewShipmentAlerts(MonitoredShipment shipment) {
    // Navigate to shipment alerts
  }

  void _viewShipmentDetails(MonitoredShipment shipment) {
    // Navigate to shipment details
  }

  void _viewShipmentChart(MonitoredShipment shipment) {
    // Navigate to shipment temperature chart
  }
}

// Data models
enum TemperatureStatus { normal, warning, critical }
enum TemperatureType { frozen, deepFrozen, chilled, cold, ambient }
enum AlertType { info, warning, critical }

class MonitoredShipment {
  String id;
  String customerName;
  String route;
  double currentTemperature;
  double targetTemperature;
  double minTemperature;
  double maxTemperature;
  TemperatureStatus status;
  DateTime lastUpdate;
  String driverName;
  String vehicleId;
  TemperatureType temperatureType;
  int alertCount;

  MonitoredShipment({
    required this.id,
    required this.customerName,
    required this.route,
    required this.currentTemperature,
    required this.targetTemperature,
    required this.minTemperature,
    required this.maxTemperature,
    required this.status,
    required this.lastUpdate,
    required this.driverName,
    required this.vehicleId,
    required this.temperatureType,
    required this.alertCount,
  });
}

class TemperatureAlert {
  String id;
  String shipmentId;
  String shipmentName;
  AlertType type;
  String message;
  double temperature;
  double threshold;
  DateTime timestamp;
  bool isResolved;

  TemperatureAlert({
    required this.id,
    required this.shipmentId,
    required this.shipmentName,
    required this.type,
    required this.message,
    required this.temperature,
    required this.threshold,
    required this.timestamp,
    required this.isResolved,
  });
}

class TemperatureReading {
  String shipmentId;
  double temperature;
  DateTime timestamp;

  TemperatureReading({
    required this.shipmentId,
    required this.temperature,
    required this.timestamp,
  });
}
