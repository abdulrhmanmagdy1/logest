// ============================================
// 📊 Premium Supervisor Dashboard - KPIs & Live Tracking
// Enterprise Supervisor Portal with Real-time Analytics
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/premium_stat_card.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';

class PremiumSupervisorDashboard extends StatefulWidget {
  const PremiumSupervisorDashboard({super.key});

  @override
  State<PremiumSupervisorDashboard> createState() => _PremiumSupervisorDashboardState();
}

class _PremiumSupervisorDashboardState extends State<PremiumSupervisorDashboard>
    with TickerProviderStateMixin {
  late AnimationController _kpiController;
  late AnimationController _mapController;
  late AnimationController _chartController;
  
  // KPI Data
  int _activeShipments = 24;
  int _availableVehicles = 18;
  int _completedTrips = 156;
  int _totalRevenue = 45680;
  double _shipmentRate = 87.5;
  double _vehicleUtilization = 92.3;
  
  // Live tracking data
  List<LiveShipment> _liveShipments = [];
  List<VehicleStatus> _vehicleStatuses = [];
  
  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _loadLiveData();
    _startRealTimeUpdates();
  }

  void _initializeAnimations() {
    _kpiController = AnimationController(
      duration: const Duration(milliseconds: 1200),
      vsync: this,
    );
    
    _mapController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _chartController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _kpiController.forward();
    _mapController.forward();
    _chartController.forward();
  }

  void _loadLiveData() {
    // Simulate loading live shipment data
    _liveShipments = [
      LiveShipment(
        id: 'EDH-1001',
        status: 'in_transit',
        from: 'الرياض',
        to: 'جدة',
        progress: 0.65,
        driver: 'محمد أحمد',
        vehicle: 'شاحنة #123',
        temperature: 4.2,
        estimatedArrival: '2 ساعة',
      ),
      LiveShipment(
        id: 'EDH-1002',
        status: 'loading',
        from: 'جدة',
        to: 'الدمام',
        progress: 0.15,
        driver: 'عبدالله محمد',
        vehicle: 'شاحنة #456',
        temperature: -18.5,
        estimatedArrival: '4 ساعة',
      ),
      LiveShipment(
        id: 'EDH-1003',
        status: 'delivered',
        from: 'مكة',
        to: 'المدينة',
        progress: 1.0,
        driver: 'سالم خالد',
        vehicle: 'شاحنة #789',
        temperature: 8.7,
        estimatedArrival: 'تم التسليم',
      ),
    ];

    // Simulate loading vehicle status data
    _vehicleStatuses = [
      VehicleStatus(id: '123', status: 'active', location: 'الرياض', driver: 'محمد أحمد', fuel: 85),
      VehicleStatus(id: '456', status: 'loading', location: 'جدة', driver: 'عبدالله محمد', fuel: 67),
      VehicleStatus(id: '789', status: 'maintenance', location: 'الورشة', driver: 'سالم خالد', fuel: 92),
      VehicleStatus(id: '101', status: 'active', location: 'الدمام', driver: 'خالد سعد', fuel: 45),
      VehicleStatus(id: '112', status: 'idle', location: 'القصيم', driver: 'سعد محمد', fuel: 78),
    ];
  }

  void _startRealTimeUpdates() {
    // Simulate real-time updates
    Future.delayed(const Duration(seconds: 5), () {
      if (mounted) {
        _updateLiveData();
      }
    });
  }

  void _updateLiveData() {
    setState(() {
      // Update KPIs
      _activeShipments = 24 + (DateTime.now().second % 5);
      _completedTrips = 156 + (DateTime.now().second % 3);
      
      // Update shipment progress
      for (var shipment in _liveShipments) {
        if (shipment.status == 'in_transit') {
          shipment.progress = (shipment.progress + 0.05).clamp(0.0, 1.0);
          if (shipment.progress >= 1.0) {
            shipment.status = 'delivered';
            shipment.estimatedArrival = 'تم التسليم';
          }
        }
      }
    });
    
    // Schedule next update
    Future.delayed(const Duration(seconds: 3), () {
      if (mounted) {
        _updateLiveData();
      }
    });
  }

  @override
  void dispose() {
    _kpiController.dispose();
    _mapController.dispose();
    _chartController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: _buildPremiumAppBar(),
      body: CustomScrollView(
        slivers: [
          // KPI Section
          SliverToBoxAdapter(
            child: _buildKPISection(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Live Tracking Map
          SliverToBoxAdapter(
            child: _buildLiveTrackingMap(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Shipment Management
          SliverToBoxAdapter(
            child: _buildShipmentManagement(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Vehicle Status
          SliverToBoxAdapter(
            child: _buildVehicleStatus(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Performance Charts
          SliverToBoxAdapter(
            child: _buildPerformanceCharts(),
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
              Icons.admin_panel_settings,
              color: AppTheme.primary,
              size: 20,
            ),
          ),
          const SizedBox(width: 12),
          Text(
            'لوحة المشرف',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
      actions: [
        GlassContainer(
          width: 50,
          height: 50,
          radius: 25,
          backgroundColor: Colors.white.withOpacity(0.05),
          borderColor: AppTheme.primary.withOpacity(0.2),
          child: Stack(
            children: [
              Center(
                child: Icon(
                  Icons.notifications,
                  color: AppTheme.textPrimary,
                  size: 24,
                ),
              ),
              Positioned(
                top: 8,
                right: 8,
                child: Container(
                  width: 12,
                  height: 12,
                  decoration: BoxDecoration(
                    color: AppTheme.error,
                    borderRadius: BorderRadius.circular(6),
                  ),
                ),
              ),
            ],
          ),
        ),
        const SizedBox(width: 8),
        GlassContainer(
          width: 50,
          height: 50,
          radius: 25,
          backgroundColor: Colors.white.withOpacity(0.05),
          borderColor: AppTheme.primary.withOpacity(0.2),
          child: const Icon(
            Icons.refresh,
            color: AppTheme.primary,
            size: 24,
          ),
        ),
        const SizedBox(width: 20),
      ],
    );
  }

  Widget _buildKPISection() {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'مؤشرات الأداء الرئيسية',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ).animate(controller: _kpiController)
            .fadeIn(delay: const Duration(milliseconds: 200)),
          
          const SizedBox(height: 20),
          
          // Primary KPIs
          Row(
            children: [
              Expanded(
                child: PremiumStatCard(
                  title: 'الشحنات النشطة',
                  value: '$_activeShipments',
                  change: '+3 اليوم',
                  icon: Icons.local_shipping,
                  color: AppTheme.primary,
                  animationDelay: const Duration(milliseconds: 300),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'المركبات المتاحة',
                  value: '$_availableVehicles',
                  change: '+2 جديد',
                  icon: Icons.directions_car,
                  color: AppTheme.success,
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
                  title: 'الرحلات المكتملة',
                  value: '$_completedTrips',
                  change: '+8 اليوم',
                  icon: Icons.check_circle,
                  color: AppTheme.accent,
                  animationDelay: const Duration(milliseconds: 500),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'الإيرادات',
                  value: '$_totalRevenue',
                  change: '+12% هذا الشهر',
                  icon: Icons.attach_money,
                  color: AppTheme.success,
                  animationDelay: const Duration(milliseconds: 600),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 20),
          
          // Performance Metrics
          GlassContainer(
            padding: const EdgeInsets.all(20),
            radius: 16,
            backgroundColor: Colors.white.withOpacity(0.05),
            borderColor: AppTheme.primary.withOpacity(0.2),
            child: Column(
              children: [
                _buildPerformanceMetric(
                  'معدل الشحنات',
                  '${_shipmentRate.toStringAsFixed(1)}%',
                  _shipmentRate / 100,
                  AppTheme.primary,
                ),
                const SizedBox(height: 16),
                _buildPerformanceMetric(
                  'استخدام المركبات',
                  '${_vehicleUtilization.toStringAsFixed(1)}%',
                  _vehicleUtilization / 100,
                  AppTheme.success,
                ),
              ],
            ),
          ).animate(controller: _kpiController)
            .fadeIn(delay: const Duration(milliseconds: 700)),
        ],
      ),
    );
  }

  Widget _buildPerformanceMetric(String title, String value, double progress, Color color) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              title,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppTheme.textSecondary,
              ),
            ),
            Text(
              value,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: color,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Container(
          height: 8,
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.1),
            borderRadius: BorderRadius.circular(4),
          ),
          child: FractionallySizedBox(
            alignment: Alignment.centerLeft,
            widthFactor: progress,
            child: Container(
              decoration: BoxDecoration(
                color: color,
                borderRadius: BorderRadius.circular(4),
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildLiveTrackingMap() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: GlassContainer(
        height: 300,
        radius: 20,
        backgroundColor: Colors.white.withOpacity(0.05),
        borderColor: AppTheme.primary.withOpacity(0.2),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.all(20),
              child: Row(
                children: [
                  Icon(
                    Icons.map,
                    color: AppTheme.primary,
                    size: 24,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    'خريطة التتبع المباشر',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      color: AppTheme.textPrimary,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const Spacer(),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: AppTheme.success.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Container(
                          width: 8,
                          height: 8,
                          decoration: BoxDecoration(
                            color: AppTheme.success,
                            borderRadius: BorderRadius.circular(4),
                          ),
                        ),
                        const SizedBox(width: 4),
                        Text(
                          'مباشر',
                          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: AppTheme.success,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            
            Expanded(
              child: _buildMapContent(),
            ),
          ],
        ),
      ).animate(controller: _mapController)
        .fadeIn(delay: const Duration(milliseconds: 200)),
    );
  }

  Widget _buildMapContent() {
    return Container(
      margin: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.surface.withOpacity(0.3),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppTheme.textHint.withOpacity(0.2)),
      ),
      child: Stack(
        children: [
          // Map placeholder
          Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.map_outlined,
                  color: AppTheme.textHint,
                  size: 48,
                ),
                const SizedBox(height: 8),
                Text(
                  'خريطة المملكة العربية السعودية',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textHint,
                  ),
                ),
                Text(
                  '${_liveShipments.length} شحنة نشطة',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: AppTheme.textSecondary,
                  ),
                ),
              ],
            ),
          ),
          
          // Live shipment markers
          ..._liveShipments.asMap().entries.map((entry) {
            final index = entry.key;
            final shipment = entry.value;
            return _buildShipmentMarker(shipment, index);
          }),
        ],
      ),
    );
  }

  Widget _buildShipmentMarker(LiveShipment shipment, int index) {
    final positions = [
      const Offset(0.2, 0.3), // Riyadh
      const Offset(0.7, 0.6), // Jeddah
      const Offset(0.4, 0.8), // Mecca
    ];
    
    final position = positions[index % positions.length];
    
    return Positioned(
      left: position.dx * 280 + 20,
      top: position.dy * 200 + 20,
      child: GlassContainer(
        width: 40,
        height: 40,
        radius: 20,
        backgroundColor: _getStatusColor(shipment.status).withOpacity(0.2),
        borderColor: _getStatusColor(shipment.status).withOpacity(0.5),
        child: Icon(
          Icons.local_shipping,
          color: _getStatusColor(shipment.status),
          size: 20,
        ),
      ).animate(controller: _mapController)
        .scale(begin: const Offset(0, 0), end: const Offset(1, 1))
        .then()
        .shimmer(duration: const Duration(milliseconds: 2000)),
    );
  }

  Widget _buildShipmentManagement() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'إدارة الشحنات',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              GlowingButton(
                text: 'تصفية متقدمة',
                onPressed: () => _showAdvancedFilter(),
                color: AppTheme.primary,
                height: 36,
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Shipment list
          ..._liveShipments.asMap().entries.map((entry) {
            final index = entry.key;
            final shipment = entry.value;
            return _buildShipmentCard(shipment, index);
          }),
        ],
      ),
    );
  }

  Widget _buildShipmentCard(LiveShipment shipment, int index) {
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: _getStatusColor(shipment.status).withOpacity(0.2),
      child: Column(
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: _getStatusColor(shipment.status).withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  _getStatusText(shipment.status),
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: _getStatusColor(shipment.status),
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              const Spacer(),
              Text(
                shipment.id,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textHint,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          Row(
            children: [
              Icon(
                Icons.location_on_outlined,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 4),
              Text(
                '${shipment.from} ← ${shipment.to}',
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
                Icons.person_outline,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 4),
              Text(
                shipment.driver,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const SizedBox(width: 16),
              Icon(
                Icons.directions_car,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 4),
              Text(
                shipment.vehicle,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          // Progress bar
          Row(
            children: [
              Text(
                'التقدم: ${(shipment.progress * 100).toInt()}%',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const Spacer(),
              Text(
                shipment.estimatedArrival,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.primary,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 8),
          
          Container(
            height: 6,
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.1),
              borderRadius: BorderRadius.circular(3),
            ),
            child: FractionallySizedBox(
              alignment: Alignment.centerLeft,
              widthFactor: shipment.progress,
              child: Container(
                decoration: BoxDecoration(
                  color: _getStatusColor(shipment.status),
                  borderRadius: BorderRadius.circular(3),
                ),
              ),
            ),
          ),
        ],
      ),
    ).animate().fadeIn(delay: Duration(milliseconds: index * 100)).slideX(begin: -0.1, end: 0);
  }

  Widget _buildVehicleStatus() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'حالة المركبات',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
          
          const SizedBox(height: 16),
          
          // Vehicle status grid
          GridView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 2,
              crossAxisSpacing: 12,
              mainAxisSpacing: 12,
              childAspectRatio: 1.4,
            ),
            itemCount: _vehicleStatuses.length,
            itemBuilder: (context, index) {
              final vehicle = _vehicleStatuses[index];
              return _buildVehicleCard(vehicle, index);
            },
          ),
        ],
      ),
    );
  }

  Widget _buildVehicleCard(VehicleStatus vehicle, int index) {
    return GlassContainer(
      padding: const EdgeInsets.all(12),
      radius: 16,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: _getVehicleStatusColor(vehicle.status).withOpacity(0.2),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                width: 8,
                height: 8,
                decoration: BoxDecoration(
                  color: _getVehicleStatusColor(vehicle.status),
                  borderRadius: BorderRadius.circular(4),
                ),
              ),
              const SizedBox(width: 8),
              Text(
                'شاحنة #${vehicle.id}',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 8),
          
          Text(
            vehicle.driver,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
          
          const SizedBox(height: 4),
          
          Text(
            vehicle.location,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: AppTheme.textHint,
            ),
          ),
          
          const Spacer(),
          
          Row(
            children: [
              Icon(
                Icons.local_gas_station,
                color: AppTheme.textSecondary,
                size: 14,
              ),
              const SizedBox(width: 4),
              Text(
                '${vehicle.fuel}%',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const Spacer(),
              Text(
                _getVehicleStatusText(vehicle.status),
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: _getVehicleStatusColor(vehicle.status),
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate().fadeIn(delay: Duration(milliseconds: index * 100)).scale(begin: const Offset(0.9, 0.9), end: const Offset(1, 1));
  }

  Widget _buildPerformanceCharts() {
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
              'التحليلات والأداء',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            
            const SizedBox(height: 20),
            
            // Performance metrics
            Row(
              children: [
                Expanded(
                  child: _buildChartItem(
                    'معدل التوصيل',
                    '94.2%',
                    '+2.3%',
                    AppTheme.success,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _buildChartItem(
                    'رضا العملاء',
                    '96.8%',
                    '+1.2%',
                    AppTheme.primary,
                  ),
                ),
              ],
            ),
            
            const SizedBox(height: 16),
            
            Row(
              children: [
                Expanded(
                  child: _buildChartItem(
                    'كفاءة الوقود',
                    '87.5%',
                    '+3.7%',
                    AppTheme.accent,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _buildChartItem(
                    'وقت الاستجابة',
                    '12 دقيقة',
                    '-5 دقيقة',
                    AppTheme.success,
                  ),
                ),
              ],
            ),
          ],
        ),
      ).animate(controller: _chartController)
        .fadeIn(delay: const Duration(milliseconds: 200)),
    );
  }

  Widget _buildChartItem(String title, String value, String change, Color color) {
    return GlassContainer(
      padding: const EdgeInsets.all(16),
      radius: 12,
      backgroundColor: color.withOpacity(0.1),
      borderColor: color.withOpacity(0.3),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            value,
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: color,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 4),
          Row(
            children: [
              Icon(
                change.startsWith('+') ? Icons.trending_up : Icons.trending_down,
                color: change.startsWith('+') ? AppTheme.success : AppTheme.error,
                size: 16,
              ),
              const SizedBox(width: 4),
              Text(
                change,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: change.startsWith('+') ? AppTheme.success : AppTheme.error,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'in_transit':
        return AppTheme.primary;
      case 'loading':
        return AppTheme.accent;
      case 'delivered':
        return AppTheme.success;
      case 'pending':
        return AppTheme.error;
      default:
        return AppTheme.textHint;
    }
  }

  String _getStatusText(String status) {
    switch (status) {
      case 'in_transit':
        return 'قيد التوصيل';
      case 'loading':
        return 'جاري التحميل';
      case 'delivered':
        return 'تم التسليم';
      case 'pending':
        return 'معلق';
      default:
        return 'غير معروف';
    }
  }

  Color _getVehicleStatusColor(String status) {
    switch (status) {
      case 'active':
        return AppTheme.success;
      case 'loading':
        return AppTheme.primary;
      case 'maintenance':
        return AppTheme.error;
      case 'idle':
        return AppTheme.textHint;
      default:
        return AppTheme.textHint;
    }
  }

  String _getVehicleStatusText(String status) {
    switch (status) {
      case 'active':
        return 'نشط';
      case 'loading':
        return 'تحميل';
      case 'maintenance':
        return 'صيانة';
      case 'idle':
        return 'خامل';
      default:
        return 'غير معروف';
    }
  }

  void _showAdvancedFilter() {
    showDialog(
      context: context,
      builder: (context) => Dialog(
        backgroundColor: Colors.transparent,
        child: GlassContainer(
          padding: const EdgeInsets.all(24),
          radius: 20,
          backgroundColor: AppTheme.background.withOpacity(0.9),
          borderColor: AppTheme.primary.withOpacity(0.3),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                'التصفية المتقدمة',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 20),
              // Filter options would go here
              Text(
                'خيارات التصفية المتقدمة',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const SizedBox(height: 20),
              GlowingButton(
                text: 'إغلاق',
                onPressed: () => Navigator.pop(context),
                color: AppTheme.primary,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

// Data models
class LiveShipment {
  String id;
  String status;
  String from;
  String to;
  double progress;
  String driver;
  String vehicle;
  double temperature;
  String estimatedArrival;

  LiveShipment({
    required this.id,
    required this.status,
    required this.from,
    required this.to,
    required this.progress,
    required this.driver,
    required this.vehicle,
    required this.temperature,
    required this.estimatedArrival,
  });
}

class VehicleStatus {
  String id;
  String status;
  String location;
  String driver;
  int fuel;

  VehicleStatus({
    required this.id,
    required this.status,
    required this.location,
    required this.driver,
    required this.fuel,
  });
}
