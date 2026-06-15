// ============================================
// 📦 Smart Shipment Management System
// Enterprise Shipment Management with Priority & Filtering
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';

class SmartShipmentManagement extends StatefulWidget {
  const SmartShipmentManagement({super.key});

  @override
  State<SmartShipmentManagement> createState() => _SmartShipmentManagementState();
}

class _SmartShipmentManagementState extends State<SmartShipmentManagement>
    with TickerProviderStateMixin {
  late AnimationController _listController;
  late AnimationController _filterController;
  
  // Search and filter state
  final TextEditingController _searchController = TextEditingController();
  String _searchQuery = '';
  ShipmentPriority _selectedPriority = ShipmentPriority.all;
  ShipmentStatus _selectedStatus = ShipmentStatus.all;
  String _selectedTemperature = 'all';
  String _selectedRegion = 'all';
  
  // Shipment data
  List<SmartShipment> _allShipments = [];
  List<SmartShipment> _filteredShipments = [];
  
  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _loadShipments();
    _applyFilters();
  }

  void _initializeAnimations() {
    _listController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _filterController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    
    _listController.forward();
  }

  void _loadShipments() {
    _allShipments = [
      SmartShipment(
        id: 'EDH-1001',
        customerName: 'شركة النقل السريع',
        priority: ShipmentPriority.high,
        status: ShipmentStatus.inTransit,
        from: 'الرياض',
        to: 'جدة',
        temperature: TemperatureType.frozen,
        weight: 2500.0,
        estimatedDelivery: DateTime.now().add(const Duration(hours: 3)),
        driver: 'محمد أحمد',
        vehicle: 'شاحنة #123',
        progress: 0.65,
        lastUpdate: DateTime.now().subtract(const Duration(minutes: 15)),
      ),
      SmartShipment(
        id: 'EDH-1002',
        customerName: 'مستودع الأدوية الطبية',
        priority: ShipmentPriority.urgent,
        status: ShipmentStatus.loading,
        from: 'جدة',
        to: 'الدمام',
        temperature: TemperatureType.cold,
        weight: 1800.0,
        estimatedDelivery: DateTime.now().add(const Duration(hours: 5)),
        driver: 'عبدالله محمد',
        vehicle: 'شاحنة #456',
        progress: 0.15,
        lastUpdate: DateTime.now().subtract(const Duration(minutes: 30)),
      ),
      SmartShipment(
        id: 'EDH-1003',
        customerName: 'شركة الأغذية الفاخرة',
        priority: ShipmentPriority.medium,
        status: ShipmentStatus.delivered,
        from: 'مكة',
        to: 'المدينة',
        temperature: TemperatureType.chilled,
        weight: 3200.0,
        estimatedDelivery: DateTime.now().subtract(const Duration(hours: 2)),
        driver: 'سالم خالد',
        vehicle: 'شاحنة #789',
        progress: 1.0,
        lastUpdate: DateTime.now().subtract(const Duration(hours: 2)),
      ),
      SmartShipment(
        id: 'EDH-1004',
        customerName: 'مصنع المواد الكيميائية',
        priority: ShipmentPriority.low,
        status: ShipmentStatus.pending,
        from: 'القصيم',
        to: 'حائل',
        temperature: TemperatureType.ambient,
        weight: 4500.0,
        estimatedDelivery: DateTime.now().add(const Duration(hours: 8)),
        driver: 'خالد سعد',
        vehicle: 'شاحنة #101',
        progress: 0.0,
        lastUpdate: DateTime.now().subtract(const Duration(hours: 1)),
      ),
      SmartShipment(
        id: 'EDH-1005',
        customerName: 'شركة التبريد المركزي',
        priority: ShipmentPriority.high,
        status: ShipmentStatus.inTransit,
        from: 'تبوك',
        to: 'الطائف',
        temperature: TemperatureType.deepFrozen,
        weight: 2100.0,
        estimatedDelivery: DateTime.now().add(const Duration(hours: 4)),
        driver: 'سعد محمد',
        vehicle: 'شاحنة #112',
        progress: 0.45,
        lastUpdate: DateTime.now().subtract(const Duration(minutes: 45)),
      ),
    ];
  }

  void _applyFilters() {
    setState(() {
      _filteredShipments = _allShipments.where((shipment) {
        // Search filter
        final matchesSearch = _searchQuery.isEmpty ||
            shipment.id.toLowerCase().contains(_searchQuery.toLowerCase()) ||
            shipment.customerName.toLowerCase().contains(_searchQuery.toLowerCase());
        
        // Priority filter
        final matchesPriority = _selectedPriority == ShipmentPriority.all ||
            shipment.priority == _selectedPriority;
        
        // Status filter
        final matchesStatus = _selectedStatus == ShipmentStatus.all ||
            shipment.status == _selectedStatus;
        
        // Temperature filter
        final matchesTemperature = _selectedTemperature == 'all' ||
            shipment.temperature.name == _selectedTemperature;
        
        // Region filter
        final matchesRegion = _selectedRegion == 'all' ||
            shipment.from.contains(_selectedRegion) ||
            shipment.to.contains(_selectedRegion);
        
        return matchesSearch && matchesPriority && matchesStatus && 
               matchesTemperature && matchesRegion;
      }).toList();
    });
  }

  @override
  void dispose() {
    _listController.dispose();
    _filterController.dispose();
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: _buildPremiumAppBar(),
      body: Column(
        children: [
          // Search and Filter Section
          _buildSearchAndFilterSection(),
          
          // Results Summary
          _buildResultsSummary(),
          
          // Shipment List
          Expanded(
            child: _buildShipmentList(),
          ),
        ],
      ),
      floatingActionButton: GlowingButton(
        text: 'شحنة جديدة',
        onPressed: _createNewShipment,
        color: AppTheme.primary,
        icon: Icons.add_circle,
      ),
    );
  }

  PreferredSizeWidget _buildPremiumAppBar() {
    return AppBar(
      backgroundColor: Colors.transparent,
      elevation: 0,
      title: Text(
        'إدارة الشحنات الذكية',
        style: Theme.of(context).textTheme.titleLarge?.copyWith(
          color: AppTheme.textPrimary,
          fontWeight: FontWeight.bold,
        ),
      ),
      actions: [
        IconButton(
          onPressed: _exportData,
          icon: const Icon(Icons.download, color: AppTheme.primary),
        ),
        IconButton(
          onPressed: _refreshData,
          icon: const Icon(Icons.refresh, color: AppTheme.primary),
        ),
        const SizedBox(width: 20),
      ],
    );
  }

  Widget _buildSearchAndFilterSection() {
    return GlassContainer(
      margin: const EdgeInsets.all(20),
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.primary.withOpacity(0.2),
      child: Column(
        children: [
          // Search bar
          GlassContainer(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            radius: 12,
            backgroundColor: Colors.white.withOpacity(0.05),
            borderColor: AppTheme.textHint.withOpacity(0.2),
            child: Row(
              children: [
                const Icon(Icons.search, color: AppTheme.textHint),
                const SizedBox(width: 12),
                Expanded(
                  child: TextField(
                    controller: _searchController,
                    style: const TextStyle(color: AppTheme.textPrimary),
                    decoration: const InputDecoration(
                      hintText: 'البحث بالرقم التسلسلي أو اسم العميل',
                      hintStyle: TextStyle(color: AppTheme.textHint),
                      border: InputBorder.none,
                    ),
                    onChanged: (value) {
                      setState(() {
                        _searchQuery = value;
                      });
                      _applyFilters();
                    },
                  ),
                ),
                if (_searchQuery.isNotEmpty)
                  IconButton(
                    onPressed: () {
                      _searchController.clear();
                      setState(() {
                        _searchQuery = '';
                      });
                      _applyFilters();
                    },
                    icon: const Icon(Icons.clear, color: AppTheme.textHint),
                  ),
              ],
            ),
          ),
          
          const SizedBox(height: 16),
          
          // Filter chips
          Row(
            children: [
              Expanded(
                child: _buildFilterChip(
                  'الأولوية',
                  _getPriorityText(_selectedPriority),
                  () => _showPriorityFilter(),
                  _getPriorityColor(_selectedPriority),
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: _buildFilterChip(
                  'الحالة',
                  _getStatusText(_selectedStatus),
                  () => _showStatusFilter(),
                  _getStatusColor(_selectedStatus),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 8),
          
          Row(
            children: [
              Expanded(
                child: _buildFilterChip(
                  'درجة الحرارة',
                  _getTemperatureText(_selectedTemperature),
                  () => _showTemperatureFilter(),
                  AppTheme.accent,
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: _buildFilterChip(
                  'المنطقة',
                  _getRegionText(_selectedRegion),
                  () => _showRegionFilter(),
                  AppTheme.success,
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _filterController)
      .fadeIn()
      .slideY(begin: -0.2, end: 0);
  }

  Widget _buildFilterChip(String label, String value, VoidCallback onTap, Color color) {
    return InkWell(
      onTap: onTap,
      child: GlassContainer(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        radius: 12,
        backgroundColor: color.withOpacity(0.1),
        borderColor: color.withOpacity(0.3),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              label,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: AppTheme.textSecondary,
              ),
            ),
            Row(
              children: [
                Text(
                  value,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: color,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(width: 4),
                Icon(
                  Icons.keyboard_arrow_down,
                  color: color,
                  size: 16,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildResultsSummary() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Row(
        children: [
          Text(
            '${_filteredShipments.length} شحنة',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
            ),
          ),
          const Spacer(),
          if (_searchQuery.isNotEmpty || _selectedPriority != ShipmentPriority.all || 
              _selectedStatus != ShipmentStatus.all || _selectedTemperature != 'all' || 
              _selectedRegion != 'all')
            TextButton(
              onPressed: _clearAllFilters,
              child: Text(
                'مسح التصفية',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.primary,
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildShipmentList() {
    if (_filteredShipments.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.inbox_outlined,
              color: AppTheme.textHint,
              size: 64,
            ),
            const SizedBox(height: 16),
            Text(
              'لا توجد شحنات مطابقة',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                color: AppTheme.textHint,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'جرب تعديل معايير البحث أو التصفية',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppTheme.textSecondary,
              ),
            ),
          ],
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.fromLTRB(20, 0, 20, 100),
      itemCount: _filteredShipments.length,
      itemBuilder: (context, index) {
        final shipment = _filteredShipments[index];
        return _buildShipmentCard(shipment, index);
      },
    );
  }

  Widget _buildShipmentCard(SmartShipment shipment, int index) {
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: _getPriorityColor(shipment.priority).withOpacity(0.2),
      child: Column(
        children: [
          // Header
          Row(
            children: [
              // Priority badge
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: _getPriorityColor(shipment.priority).withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  _getPriorityText(shipment.priority),
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: _getPriorityColor(shipment.priority),
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              
              const SizedBox(width: 8),
              
              // Status badge
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
              
              // Shipment ID
              Text(
                shipment.id,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textHint,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Customer and route
          Row(
            children: [
              Icon(
                Icons.business,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  shipment.customerName,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 8),
          
          Row(
            children: [
              Icon(
                Icons.location_on_outlined,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Text(
                '${shipment.from} ← ${shipment.to}',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textPrimary,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Details row
          Row(
            children: [
              // Temperature
              Row(
                children: [
                  Icon(
                    Icons.ac_unit,
                    color: AppTheme.accent,
                    size: 16,
                  ),
                  const SizedBox(width: 4),
                  Text(
                    _getTemperatureText(shipment.temperature.name),
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
                  ),
                ],
              ),
              
              const SizedBox(width: 16),
              
              // Weight
              Row(
                children: [
                  Icon(
                    Icons.monitor_weight,
                    color: AppTheme.textSecondary,
                    size: 16,
                  ),
                  const SizedBox(width: 4),
                  Text(
                    '${shipment.weight.toStringAsFixed(0)} كجم',
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
                  ),
                ],
              ),
              
              const Spacer(),
              
              // Driver
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
                ],
              ),
            ],
          ),
          
          if (shipment.status == ShipmentStatus.inTransit) ...[
            const SizedBox(height: 16),
            
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
                  'الوصول المتوقع: ${_formatTime(shipment.estimatedDelivery)}',
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
          
          const SizedBox(height: 16),
          
          // Action buttons
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
                  text: 'تتبع',
                  onPressed: () => _trackShipment(shipment),
                  color: AppTheme.accent,
                  height: 36,
                ),
              ),
              const SizedBox(width: 8),
              GlassContainer(
                width: 36,
                height: 36,
                radius: 18,
                backgroundColor: Colors.white.withOpacity(0.05),
                borderColor: AppTheme.textHint.withOpacity(0.2),
                child: IconButton(
                  onPressed: () => _showShipmentMenu(shipment),
                  icon: const Icon(
                    Icons.more_vert,
                    color: AppTheme.textHint,
                    size: 16,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _listController)
      .fadeIn(delay: Duration(milliseconds: index * 50))
      .slideX(begin: -0.1, end: 0);
  }

  // Filter dialogs
  void _showPriorityFilter() {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (context) => GlassContainer(
        margin: const EdgeInsets.all(20),
        padding: const EdgeInsets.all(20),
        radius: 20,
        backgroundColor: AppTheme.background.withOpacity(0.9),
        borderColor: AppTheme.primary.withOpacity(0.3),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'اختر الأولوية',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 20),
            ...ShipmentPriority.values.map((priority) {
              return RadioListTile<ShipmentPriority>(
                title: Text(
                  _getPriorityText(priority),
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                  ),
                ),
                value: priority,
                groupValue: _selectedPriority,
                onChanged: (value) {
                  setState(() {
                    _selectedPriority = value!;
                  });
                  _applyFilters();
                  Navigator.pop(context);
                },
                activeColor: AppTheme.primary,
              );
            }),
          ],
        ),
      ),
    );
  }

  void _showStatusFilter() {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (context) => GlassContainer(
        margin: const EdgeInsets.all(20),
        padding: const EdgeInsets.all(20),
        radius: 20,
        backgroundColor: AppTheme.background.withOpacity(0.9),
        borderColor: AppTheme.primary.withOpacity(0.3),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'اختر الحالة',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 20),
            ...ShipmentStatus.values.map((status) {
              return RadioListTile<ShipmentStatus>(
                title: Text(
                  _getStatusText(status),
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                  ),
                ),
                value: status,
                groupValue: _selectedStatus,
                onChanged: (value) {
                  setState(() {
                    _selectedStatus = value!;
                  });
                  _applyFilters();
                  Navigator.pop(context);
                },
                activeColor: AppTheme.primary,
              );
            }),
          ],
        ),
      ),
    );
  }

  void _showTemperatureFilter() {
    final temperatures = ['all', 'frozen', 'deepFrozen', 'chilled', 'cold', 'ambient'];
    
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (context) => GlassContainer(
        margin: const EdgeInsets.all(20),
        padding: const EdgeInsets.all(20),
        radius: 20,
        backgroundColor: AppTheme.background.withOpacity(0.9),
        borderColor: AppTheme.primary.withOpacity(0.3),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'اختر درجة الحرارة',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 20),
            ...temperatures.map((temp) {
              return RadioListTile<String>(
                title: Text(
                  _getTemperatureText(temp),
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                  ),
                ),
                value: temp,
                groupValue: _selectedTemperature,
                onChanged: (value) {
                  setState(() {
                    _selectedTemperature = value!;
                  });
                  _applyFilters();
                  Navigator.pop(context);
                },
                activeColor: AppTheme.accent,
              );
            }),
          ],
        ),
      ),
    );
  }

  void _showRegionFilter() {
    final regions = ['all', 'الرياض', 'جدة', 'مكة', 'المدينة', 'الدمام', 'القصيم', 'حائل', 'تبوك', 'الطائف'];
    
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (context) => GlassContainer(
        margin: const EdgeInsets.all(20),
        padding: const EdgeInsets.all(20),
        radius: 20,
        backgroundColor: AppTheme.background.withOpacity(0.9),
        borderColor: AppTheme.primary.withOpacity(0.3),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'اختر المنطقة',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 20),
            ...regions.map((region) {
              return RadioListTile<String>(
                title: Text(
                  _getRegionText(region),
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                  ),
                ),
                value: region,
                groupValue: _selectedRegion,
                onChanged: (value) {
                  setState(() {
                    _selectedRegion = value!;
                  });
                  _applyFilters();
                  Navigator.pop(context);
                },
                activeColor: AppTheme.success,
              );
            }),
          ],
        ),
      ),
    );
  }

  // Helper methods
  String _getPriorityText(ShipmentPriority priority) {
    switch (priority) {
      case ShipmentPriority.urgent:
        return 'عاجل';
      case ShipmentPriority.high:
        return 'عالي';
      case ShipmentPriority.medium:
        return 'متوسط';
      case ShipmentPriority.low:
        return 'منخفض';
      case ShipmentPriority.all:
        return 'الكل';
    }
  }

  Color _getPriorityColor(ShipmentPriority priority) {
    switch (priority) {
      case ShipmentPriority.urgent:
        return AppTheme.error;
      case ShipmentPriority.high:
        return AppTheme.primary;
      case ShipmentPriority.medium:
        return AppTheme.accent;
      case ShipmentPriority.low:
        return AppTheme.textHint;
      case ShipmentPriority.all:
        return AppTheme.textHint;
    }
  }

  String _getStatusText(ShipmentStatus status) {
    switch (status) {
      case ShipmentStatus.pending:
        return 'معلق';
      case ShipmentStatus.loading:
        return 'تحميل';
      case ShipmentStatus.inTransit:
        return 'قيد التوصيل';
      case ShipmentStatus.delivered:
        return 'تم التسليم';
      case ShipmentStatus.cancelled:
        return 'ملغي';
      case ShipmentStatus.all:
        return 'الكل';
    }
  }

  Color _getStatusColor(ShipmentStatus status) {
    switch (status) {
      case ShipmentStatus.pending:
        return AppTheme.textHint;
      case ShipmentStatus.loading:
        return AppTheme.accent;
      case ShipmentStatus.inTransit:
        return AppTheme.primary;
      case ShipmentStatus.delivered:
        return AppTheme.success;
      case ShipmentStatus.cancelled:
        return AppTheme.error;
      case ShipmentStatus.all:
        return AppTheme.textHint;
    }
  }

  String _getTemperatureText(String temperature) {
    switch (temperature) {
      case 'all':
        return 'الكل';
      case 'frozen':
        return 'مجمد (-18°)';
      case 'deepFrozen':
        return 'مجمد عميق (-25°)';
      case 'chilled':
        return 'مبرد (4°)';
      case 'cold':
        return 'بارد (8°)';
      case 'ambient':
        return 'درجة حرارة الغرفة';
      default:
        return temperature;
    }
  }

  String _getRegionText(String region) {
    switch (region) {
      case 'all':
        return 'كل المناطق';
      default:
        return region;
    }
  }

  String _formatTime(DateTime dateTime) {
    final now = DateTime.now();
    final difference = dateTime.difference(now);
    
    if (difference.isNegative) {
      final pastDifference = now.difference(dateTime);
      if (pastDifference.inHours > 0) {
        return 'منذ ${pastDifference.inHours} ساعة';
      } else if (pastDifference.inMinutes > 0) {
        return 'منذ ${pastDifference.inMinutes} دقيقة';
      } else {
        return 'الآن';
      }
    } else {
      if (difference.inHours > 0) {
        return 'خلال ${difference.inHours} ساعة';
      } else if (difference.inMinutes > 0) {
        return 'خلال ${difference.inMinutes} دقيقة';
      } else {
        return 'قريباً';
      }
    }
  }

  // Action methods
  void _clearAllFilters() {
    setState(() {
      _searchQuery = '';
      _searchController.clear();
      _selectedPriority = ShipmentPriority.all;
      _selectedStatus = ShipmentStatus.all;
      _selectedTemperature = 'all';
      _selectedRegion = 'all';
    });
    _applyFilters();
  }

  void _createNewShipment() {
    // Navigate to create shipment screen
  }

  void _viewShipmentDetails(SmartShipment shipment) {
    // Navigate to shipment details
  }

  void _trackShipment(SmartShipment shipment) {
    // Navigate to tracking screen
  }

  void _showShipmentMenu(SmartShipment shipment) {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (context) => GlassContainer(
        margin: const EdgeInsets.all(20),
        padding: const EdgeInsets.all(20),
        radius: 20,
        backgroundColor: AppTheme.background.withOpacity(0.9),
        borderColor: AppTheme.primary.withOpacity(0.3),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'خيارات الشحنة',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 20),
            ListTile(
              leading: const Icon(Icons.edit, color: AppTheme.primary),
              title: const Text('تعديل الشحنة'),
              onTap: () {
                Navigator.pop(context);
                // Edit shipment
              },
            ),
            ListTile(
              leading: const Icon(Icons.share, color: AppTheme.accent),
              title: const Text('مشاركة التفاصيل'),
              onTap: () {
                Navigator.pop(context);
                // Share shipment
              },
            ),
            ListTile(
              leading: const Icon(Icons.print, color: AppTheme.success),
              title: const Text('طباعة التقرير'),
              onTap: () {
                Navigator.pop(context);
                // Print report
              },
            ),
            ListTile(
              leading: const Icon(Icons.cancel, color: AppTheme.error),
              title: const Text('إلغاء الشحنة'),
              onTap: () {
                Navigator.pop(context);
                // Cancel shipment
              },
            ),
          ],
        ),
      ),
    );
  }

  void _exportData() {
    // Export shipment data
  }

  void _refreshData() {
    // Refresh shipment data
    _loadShipments();
    _applyFilters();
  }
}

// Enums and data models
enum ShipmentPriority { all, urgent, high, medium, low }
enum ShipmentStatus { all, pending, loading, inTransit, delivered, cancelled }
enum TemperatureType { frozen, deepFrozen, chilled, cold, ambient }

class SmartShipment {
  String id;
  String customerName;
  ShipmentPriority priority;
  ShipmentStatus status;
  String from;
  String to;
  TemperatureType temperature;
  double weight;
  DateTime estimatedDelivery;
  String driver;
  String vehicle;
  double progress;
  DateTime lastUpdate;

  SmartShipment({
    required this.id,
    required this.customerName,
    required this.priority,
    required this.status,
    required this.from,
    required this.to,
    required this.temperature,
    required this.weight,
    required this.estimatedDelivery,
    required this.driver,
    required this.vehicle,
    required this.progress,
    required this.lastUpdate,
  });
}
