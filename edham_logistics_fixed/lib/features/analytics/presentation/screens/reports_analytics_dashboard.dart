// ============================================
// 📊 Reports & Analytics Dashboard
// Comprehensive Analytics with Data Export
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/widgets/premium/premium_stat_card.dart';
import '../../../../core/design_system/shadows.dart';

class ReportsAnalyticsDashboard extends StatefulWidget {
  const ReportsAnalyticsDashboard({super.key});

  @override
  State<ReportsAnalyticsDashboard> createState() => _ReportsAnalyticsDashboardState();
}

class _ReportsAnalyticsDashboardState extends State<ReportsAnalyticsDashboard>
    with TickerProviderStateMixin {
  late AnimationController _dashboardController;
  late AnimationController _chartsController;
  late AnimationController _reportsController;
  
  // Data
  List<PerformanceMetric> _metrics = [];
  List<AnalyticsReport> _reports = [];
  List<VehiclePerformance> _vehiclePerformance = [];
  String _selectedPeriod = 'month';
  String _selectedReportType = 'all';
  
  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _loadAnalyticsData();
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
    
    _reportsController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    
    _dashboardController.forward();
    _chartsController.forward();
    _reportsController.forward();
  }

  void _loadAnalyticsData() {
    _metrics = [
      PerformanceMetric(
        title: 'إنتاجية الشاحنات',
        value: 94.2,
        change: 5.3,
        changeType: ChangeType.increase,
        icon: Icons.trending_up,
        color: AppTheme.success,
        description: 'متوسط الرحلات المكتملة يومياً',
      ),
      PerformanceMetric(
        title: 'استهلاك الوقود',
        value: 87.5,
        change: -2.1,
        changeType: ChangeType.decrease,
        icon: Icons.local_gas_station,
        color: AppTheme.accent,
        description: 'كفاءة استهلاك الوقود لكل 100 كم',
      ),
      PerformanceMetric(
        title: 'وقت التسليم',
        value: 96.8,
        change: 3.7,
        changeType: ChangeType.increase,
        icon: Icons.timer,
        color: AppTheme.primary,
        description: 'نسبة التسليم في الوقت المحدد',
      ),
      PerformanceMetric(
        title: 'رضا العملاء',
        value: 4.6,
        change: 0.2,
        changeType: ChangeType.increase,
        icon: Icons.star,
        color: AppTheme.success,
        description: 'متوسط تقييم العملاء من 5',
      ),
      PerformanceMetric(
        title: 'تكاليف التشغيل',
        value: 78.3,
        change: -4.5,
        changeType: ChangeType.decrease,
        icon: Icons.money_off,
        color: AppTheme.error,
        description: 'تكاليف التشغيل كنسبة من الإيرادات',
      ),
      PerformanceMetric(
        title: 'استخدام المركبات',
        value: 91.2,
        change: 2.8,
        changeType: ChangeType.increase,
        icon: Icons.directions_car,
        color: AppTheme.primary,
        description: 'نسبة المركبات النشطة',
      ),
    ];

    _reports = [
      AnalyticsReport(
        id: 'RPT-001',
        name: 'تقرير الأداء الشهري',
        type: ReportType.performance,
        generatedDate: DateTime.now().subtract(const Duration(days: 5)),
        period: 'يناير 2024',
        fileSize: '2.4 MB',
        format: 'PDF',
        description: 'تقرير شامل عن أداء الأسطول خلال الشهر',
      ),
      AnalyticsReport(
        id: 'RPT-002',
        name: 'تحليل تكاليف التشغيل',
        type: ReportType.financial,
        generatedDate: DateTime.now().subtract(const Duration(days: 10)),
        period: 'الربع الرابع 2023',
        fileSize: '1.8 MB',
        format: 'Excel',
        description: 'تحليل مفصل لتكاليف التشغيل والصيانة',
      ),
      AnalyticsReport(
        id: 'RPT-003',
        name: 'تقرير كفاءة الوقود',
        type: ReportType.operational,
        generatedDate: DateTime.now().subtract(const Duration(days: 15)),
        period: 'ديسمبر 2023',
        fileSize: '1.2 MB',
        format: 'PDF',
        description: 'تحليل استهلاك الوقود لكل مركبة',
      ),
      AnalyticsReport(
        id: 'RPT-004',
        name: 'تقرير رضا العملاء',
        type: ReportType.customer,
        generatedDate: DateTime.now().subtract(const Duration(days: 20)),
        period: 'السنة 2023',
        fileSize: '3.1 MB',
        format: 'PDF',
        description: 'تحليل شامل لتقييمات العملاء',
      ),
    ];

    _vehiclePerformance = [
      VehiclePerformance(
        vehicleId: 'VH-123',
        plateNumber: '1234-أ-ب',
        model: 'شاحنة فولفو FH16',
        driverName: 'محمد أحمد',
        totalTrips: 156,
        completedTrips: 148,
        fuelEfficiency: 85.2,
        onTimeDelivery: 96.5,
        averageRating: 4.7,
        totalRevenue: 45680.0,
        operatingCost: 12450.0,
      ),
      VehiclePerformance(
        vehicleId: 'VH-456',
        plateNumber: '5678-ج-د',
        model: 'شاحنة مرسيدس Actros',
        driverName: 'عبدالله محمد',
        totalTrips: 142,
        completedTrips: 135,
        fuelEfficiency: 88.7,
        onTimeDelivery: 94.2,
        averageRating: 4.5,
        totalRevenue: 38920.0,
        operatingCost: 11230.0,
      ),
      VehiclePerformance(
        vehicleId: 'VH-789',
        plateNumber: '9012-ه-و',
        model: 'شاحنة سكانيا R450',
        driverName: 'سالم خالد',
        totalTrips: 178,
        completedTrips: 172,
        fuelEfficiency: 82.3,
        onTimeDelivery: 98.1,
        averageRating: 4.8,
        totalRevenue: 52340.0,
        operatingCost: 15670.0,
      ),
      VehiclePerformance(
        vehicleId: 'VH-101',
        plateNumber: '3456-ي-ز',
        model: 'شاحنة مان TGX',
        driverName: 'خالد سعد',
        totalTrips: 134,
        completedTrips: 128,
        fuelEfficiency: 90.1,
        onTimeDelivery: 92.8,
        averageRating: 4.4,
        totalRevenue: 34150.0,
        operatingCost: 10890.0,
      ),
    ];
  }

  @override
  void dispose() {
    _dashboardController.dispose();
    _chartsController.dispose();
    _reportsController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: _buildPremiumAppBar(),
      body: CustomScrollView(
        slivers: [
          // Key Performance Metrics
          SliverToBoxAdapter(
            child: _buildKeyMetrics(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Analytics Charts
          SliverToBoxAdapter(
            child: _buildAnalyticsCharts(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Vehicle Performance
          SliverToBoxAdapter(
            child: _buildVehiclePerformance(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Generated Reports
          SliverToBoxAdapter(
            child: _buildGeneratedReports(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 80)),
        ],
      ),
      floatingActionButton: GlowingButton(
        text: 'تقرير جديد',
        onPressed: _generateNewReport,
        color: AppTheme.primary,
        icon: Icons.add_chart,
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
              Icons.analytics,
              color: AppTheme.primary,
              size: 20,
            ),
          ),
          const SizedBox(width: 12),
          Text(
            'التقارير والتحليلات',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
      actions: [
        IconButton(
          onPressed: _exportAllData,
          icon: const Icon(Icons.download, color: AppTheme.primary),
        ),
        IconButton(
          onPressed: _showCalendar,
          icon: const Icon(Icons.calendar_today, color: AppTheme.primary),
        ),
        const SizedBox(width: 20),
      ],
    );
  }

  Widget _buildKeyMetrics() {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'مؤشرات الأداء الرئيسية',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Row(
                children: [
                  _buildPeriodChip('day', 'يوم'),
                  _buildPeriodChip('week', 'أسبوع'),
                  _buildPeriodChip('month', 'شهر'),
                  _buildPeriodChip('quarter', 'ربع'),
                  _buildPeriodChip('year', 'سنة'),
                ],
              ),
            ],
          ),
          
          const SizedBox(height: 20),
          
          // Metrics Grid
          GridView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 2,
              crossAxisSpacing: 12,
              mainAxisSpacing: 12,
              childAspectRatio: 1.4,
            ),
            itemCount: _metrics.length,
            itemBuilder: (context, index) {
              final metric = _metrics[index];
              return _buildMetricCard(metric, index);
            },
          ),
        ],
      ),
    );
  }

  Widget _buildPeriodChip(String period, String label) {
    final isSelected = _selectedPeriod == period;
    
    return Padding(
      padding: const EdgeInsets.only(left: 8),
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedPeriod = period;
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

  Widget _buildMetricCard(PerformanceMetric metric, int index) {
    return GlassContainer(
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: metric.color.withOpacity(0.1),
      borderColor: metric.color.withOpacity(0.3),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                width: 40,
                height: 40,
                decoration: BoxDecoration(
                  color: metric.color.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Icon(
                  metric.icon,
                  color: metric.color,
                  size: 20,
                ),
              ),
              const Spacer(),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: metric.changeType == ChangeType.increase 
                    ? AppTheme.success.withOpacity(0.2)
                    : AppTheme.error.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(
                      metric.changeType == ChangeType.increase 
                        ? Icons.trending_up
                        : Icons.trending_down,
                      color: metric.changeType == ChangeType.increase 
                        ? AppTheme.success
                        : AppTheme.error,
                      size: 16,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      '${metric.change.abs().toStringAsFixed(1)}%',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: metric.changeType == ChangeType.increase 
                          ? AppTheme.success
                          : AppTheme.error,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          Text(
            metric.title,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
            ),
          ),
          
          const SizedBox(height: 8),
          
          Text(
            metric.value is int 
              ? '${metric.value.toInt()}'
              : metric.value.toStringAsFixed(1),
            style: Theme.of(context).textTheme.displayLarge?.copyWith(
              color: metric.color,
              fontWeight: FontWeight.bold,
            ),
          ),
          
          const SizedBox(height: 8),
          
          Text(
            metric.description,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
        ],
      ),
    ).animate(controller: _dashboardController)
      .fadeIn(delay: Duration(milliseconds: index * 100))
      .scale(begin: const Offset(0.9, 0.9), end: const Offset(1, 1));
  }

  Widget _buildAnalyticsCharts() {
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
              'الرسوم البيانية التحليلية',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            
            const SizedBox(height: 20),
            
            // Revenue Chart
            _buildChartSection(
              'إيرادات شهرية',
              'إجمالي الإيرادات خلال الأشهر الـ 12 الماضية',
              Icons.attach_money,
              AppTheme.success,
            ),
            
            const SizedBox(height: 20),
            
            // Fleet Utilization Chart
            _buildChartSection(
              'استخدام الأسطول',
              'نسبة استخدام المركبات حسب النوع',
              Icons.directions_car,
              AppTheme.primary,
            ),
            
            const SizedBox(height: 20),
            
            // Fuel Efficiency Chart
            _buildChartSection(
              'كفاءة الوقود',
              'متوسط استهلاك الوقود لكل 100 كم',
              Icons.local_gas_station,
              AppTheme.accent,
            ),
          ],
        ),
      ).animate(controller: _chartsController)
        .fadeIn(delay: const Duration(milliseconds: 200)),
    );
  }

  Widget _buildChartSection(String title, String description, IconData icon, Color color) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: color.withOpacity(0.2),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Icon(icon, color: color, size: 20),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      color: AppTheme.textPrimary,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Text(
                    description,
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
        
        const SizedBox(height: 16),
        
        // Chart Placeholder
        Container(
          height: 200,
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
                  Icons.insert_chart,
                  color: AppTheme.textHint,
                  size: 48,
                ),
                const SizedBox(height: 8),
                Text(
                  'رسم بياني تفاعلي',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textHint,
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildVehiclePerformance() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'أداء المركبات',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
          
          const SizedBox(height: 16),
          
          // Vehicle Performance Cards
          ..._vehiclePerformance.map((vehicle) => _buildVehicleCard(vehicle)),
        ],
      ),
    );
  }

  Widget _buildVehicleCard(VehiclePerformance vehicle) {
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.primary.withOpacity(0.2),
      child: Column(
        children: [
          // Header
          Row(
            children: [
              Text(
                vehicle.plateNumber,
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const Spacer(),
              Text(
                vehicle.model,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 8),
          
          // Driver
          Row(
            children: [
              Icon(Icons.person, color: AppTheme.textSecondary, size: 16),
              const SizedBox(width: 8),
              Text(
                vehicle.driverName,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Performance Metrics
          Row(
            children: [
              Expanded(
                child: _buildVehicleMetric(
                  'الرحلات المكتملة',
                  '${vehicle.completedTrips}/${vehicle.totalTrips}',
                  Icons.check_circle,
                  AppTheme.success,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildVehicleMetric(
                  'كفاءة الوقود',
                  '${vehicle.fuelEfficiency.toStringAsFixed(1)}%',
                  Icons.local_gas_station,
                  AppTheme.accent,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          Row(
            children: [
              Expanded(
                child: _buildVehicleMetric(
                  'التسليم في الوقت',
                  '${vehicle.onTimeDelivery.toStringAsFixed(1)}%',
                  Icons.timer,
                  AppTheme.primary,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildVehicleMetric(
                  'متوسط التقييم',
                  '${vehicle.averageRating.toStringAsFixed(1)}/5',
                  Icons.star,
                  AppTheme.success,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Financial Summary
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: AppTheme.surface.withOpacity(0.3),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'الإيرادات',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: AppTheme.textSecondary,
                        ),
                      ),
                      Text(
                        '${vehicle.totalRevenue.toStringAsFixed(0)} ريال',
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: AppTheme.success,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'التكاليف',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: AppTheme.textSecondary,
                        ),
                      ),
                      Text(
                        '${vehicle.operatingCost.toStringAsFixed(0)} ريال',
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: AppTheme.error,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'صافي الربح',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: AppTheme.textSecondary,
                        ),
                      ),
                      Text(
                        '${(vehicle.totalRevenue - vehicle.operatingCost).toStringAsFixed(0)} ريال',
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: AppTheme.primary,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          
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
                  text: 'تقرير',
                  onPressed: () => _generateVehicleReport(vehicle),
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

  Widget _buildVehicleMetric(String label, String value, IconData icon, Color color) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Icon(icon, color: color, size: 16),
        const SizedBox(height: 4),
        Text(
          value,
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: color,
            fontWeight: FontWeight.bold,
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

  Widget _buildGeneratedReports() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'التقارير المولدة',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Row(
                children: [
                  _buildReportTypeChip('all', 'الكل'),
                  _buildReportTypeChip('performance', 'الأداء'),
                  _buildReportTypeChip('financial', 'المالي'),
                  _buildReportTypeChip('operational', 'التشغيلي'),
                  _buildReportTypeChip('customer', 'العملاء'),
                ],
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Reports List
          ..._getFilteredReports().map((report) => _buildReportCard(report)),
        ],
      ),
    );
  }

  Widget _buildReportTypeChip(String type, String label) {
    final isSelected = _selectedReportType == type;
    
    return Padding(
      padding: const EdgeInsets.only(left: 8),
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedReportType = type;
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

  Widget _buildReportCard(AnalyticsReport report) {
    Color typeColor = _getReportTypeColor(report.type);
    
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: typeColor.withOpacity(0.2),
      child: Row(
        children: [
          Container(
            width: 40,
            height: 40,
            decoration: BoxDecoration(
              color: typeColor.withOpacity(0.2),
              borderRadius: BorderRadius.circular(20),
            ),
            child: Icon(
              _getReportTypeIcon(report.type),
              color: typeColor,
              size: 20,
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  report.name,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  report.description,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: AppTheme.textSecondary,
                  ),
                ),
                const SizedBox(height: 4),
                Row(
                  children: [
                    Text(
                      report.period,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textHint,
                      ),
                    ),
                    const SizedBox(width: 12),
                    Text(
                      '${report.fileSize} • ${report.format}',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textHint,
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
                _formatDate(report.generatedDate),
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textHint,
                ),
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  GlowingButton(
                    text: 'عرض',
                    onPressed: () => _viewReport(report),
                    color: AppTheme.primary,
                    height: 32,
                    width: 60,
                  ),
                  const SizedBox(width: 4),
                  GlowingButton(
                    text: 'تحميل',
                    onPressed: () => _downloadReport(report),
                    color: AppTheme.success,
                    height: 32,
                    width: 60,
                  ),
                ],
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _reportsController)
      .fadeIn(delay: const Duration(milliseconds: 200))
      .slideX(begin: -0.1, end: 0);
  }

  // Helper methods
  List<AnalyticsReport> _getFilteredReports() {
    if (_selectedReportType == 'all') return _reports;
    return _reports.where((report) => report.type.name == _selectedReportType).toList();
  }

  Color _getReportTypeColor(ReportType type) {
    switch (type) {
      case ReportType.performance:
        return AppTheme.success;
      case ReportType.financial:
        return AppTheme.accent;
      case ReportType.operational:
        return AppTheme.primary;
      case ReportType.customer:
        return AppTheme.error;
      default:
        return AppTheme.textHint;
    }
  }

  IconData _getReportTypeIcon(ReportType type) {
    switch (type) {
      case ReportType.performance:
        return Icons.trending_up;
      case ReportType.financial:
        return Icons.attach_money;
      case ReportType.operational:
        return Icons.settings;
      case ReportType.customer:
        return Icons.people;
      default:
        return Icons.description;
    }
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
  void _generateNewReport() {
    // Navigate to report generation screen
  }

  void _exportAllData() {
    // Export all analytics data
  }

  void _showCalendar() {
    // Show analytics calendar
  }

  void _viewVehicleDetails(VehiclePerformance vehicle) {
    // Navigate to vehicle details
  }

  void _generateVehicleReport(VehiclePerformance vehicle) {
    // Generate vehicle-specific report
  }

  void _viewReport(AnalyticsReport report) {
    // View report details
  }

  void _downloadReport(AnalyticsReport report) {
    // Download report file
  }
}

// Data models
enum ChangeType { increase, decrease }
enum ReportType { performance, financial, operational, customer }

class PerformanceMetric {
  String title;
  double value;
  double change;
  ChangeType changeType;
  IconData icon;
  Color color;
  String description;

  PerformanceMetric({
    required this.title,
    required this.value,
    required this.change,
    required this.changeType,
    required this.icon,
    required this.color,
    required this.description,
  });
}

class AnalyticsReport {
  String id;
  String name;
  ReportType type;
  DateTime generatedDate;
  String period;
  String fileSize;
  String format;
  String description;

  AnalyticsReport({
    required this.id,
    required this.name,
    required this.type,
    required this.generatedDate,
    required this.period,
    required this.fileSize,
    required this.format,
    required this.description,
  });
}

class VehiclePerformance {
  String vehicleId;
  String plateNumber;
  String model;
  String driverName;
  int totalTrips;
  int completedTrips;
  double fuelEfficiency;
  double onTimeDelivery;
  double averageRating;
  double totalRevenue;
  double operatingCost;

  VehiclePerformance({
    required this.vehicleId,
    required this.plateNumber,
    required this.model,
    required this.driverName,
    required this.totalTrips,
    required this.completedTrips,
    required this.fuelEfficiency,
    required this.onTimeDelivery,
    required this.averageRating,
    required this.totalRevenue,
    required this.operatingCost,
  });
}
