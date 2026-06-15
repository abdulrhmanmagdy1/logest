// ============================================
// 🚛 Specialized Driver Navigation System
// Heavy Vehicle Navigation with Route Optimization
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';

class SpecializedDriverNavigation extends StatefulWidget {
  const SpecializedDriverNavigation({super.key});

  @override
  State<SpecializedDriverNavigation> createState() => _SpecializedDriverNavigationState();
}

class _SpecializedDriverNavigationState extends State<SpecializedDriverNavigation>
    with TickerProviderStateMixin {
  late AnimationController _mapController;
  late AnimationController _routeController;
  late AnimationController _infoController;
  
  // Navigation state
  GoogleMapController? _mapController;
  LatLng _currentPosition = const LatLng(24.7136, 46.6753); // Riyadh
  LatLng _destination = const LatLng(21.4225, 39.8262); // Jeddah
  double _currentSpeed = 85.0;
  double _remainingDistance = 425.0;
  String _estimatedArrival = '4 ساعة 30 دقيقة';
  int _currentStep = 0;
  
  // Route information
  List<NavigationStep> _routeSteps = [];
  List<HeavyVehicleRestriction> _restrictions = [];
  List<TruckStop> _truckStops = [];
  bool _isNavigating = false;
  bool _avoidTraffic = true;
  bool _avoidTolls = false;
  bool _preferHighways = true;

  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _loadRouteData();
  }

  void _initializeAnimations() {
    _mapController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _routeController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _infoController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    
    _mapController.forward();
    _routeController.forward();
    _infoController.forward();
  }

  void _loadRouteData() {
    _routeSteps = [
      NavigationStep(
        instruction: 'انع يميناً إلى طريق الملك فهد',
        distance: 2.5,
        duration: '5 دقائق',
        icon: Icons.turn_right,
        type: StepType.turn,
      ),
      NavigationStep(
        instruction: 'استمر في طريق الملك فهد لمسافة 120 كم',
        distance: 120.0,
        duration: '1 ساعة 30 دقيقة',
        icon: Icons.straight,
        type: StepType.straight,
      ),
      NavigationStep(
        instruction: 'انع يساراً إلى طريق الملك عبدالله',
        distance: 85.0,
        duration: '1 ساعة',
        icon: Icons.turn_left,
        type: StepType.turn,
      ),
      NavigationStep(
        instruction: 'استمر في طريق الملك عبدالله لمسافة 200 كم',
        distance: 200.0,
        duration: '2 ساعة',
        icon: Icons.straight,
        type: StepType.straight,
      ),
      NavigationStep(
        instruction: 'انع يميناً إلى شارع الأمير محمد',
        distance: 17.5,
        duration: '20 دقيقة',
        icon: Icons.turn_right,
        type: StepType.turn,
      ),
    ];

    _restrictions = [
      HeavyVehicleRestriction(
        type: 'height_limit',
        description: 'ارتفاع محدود 4.5 متر',
        location: 'جسر الملك فهد',
        distance: 15.0,
        severity: RestrictionSeverity.warning,
      ),
      HeavyVehicleRestriction(
        type: 'weight_limit',
        description: 'وزن محدود 40 طن',
        location: 'طريق سعد',
        distance: 85.0,
        severity: RestrictionSeverity.danger,
      ),
      HeavyVehicleRestriction(
        type: 'time_restriction',
        description: 'ممنوع المرور 7-9 صباحاً',
        location: 'منطقة وسط الرياض',
        distance: 25.0,
        severity: RestrictionSeverity.info,
      ),
    ];

    _truckStops = [
      TruckStop(
        name: 'محطة وقود أرامكو',
        location: const LatLng(24.4, 46.3),
        distance: 45.0,
        services: ['fuel', 'restaurant', 'restroom', 'mechanic'],
        rating: 4.5,
      ),
      TruckStop(
        name: 'محطة استراحة الطرق السريعة',
        location: const LatLng(23.8, 45.9),
        distance: 180.0,
        services: ['restaurant', 'restroom', 'shower'],
        rating: 4.2,
      ),
      TruckStop(
        name: 'موقف الشاحنات الكبير',
        location: const LatLng(22.7, 44.8),
        distance: 320.0,
        services: ['parking', 'fuel', 'restaurant'],
        rating: 4.0,
      ),
    ];
  }

  @override
  void dispose() {
    _mapController.dispose();
    _routeController.dispose();
    _infoController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: _buildPremiumAppBar(),
      body: Stack(
        children: [
          // Map View
          _buildMapView(),
          
          // Top Navigation Info
          Positioned(
            top: 80,
            left: 20,
            right: 20,
            child: _buildNavigationInfo(),
          ),
          
          // Bottom Navigation Controls
          Positioned(
            bottom: 20,
            left: 20,
            right: 20,
            child: _buildNavigationControls(),
          ),
          
          // Side Panel (when expanded)
          if (_isNavigating) _buildSidePanel(),
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
              Icons.truck,
              color: AppTheme.primary,
              size: 20,
            ),
          ),
          const SizedBox(width: 12),
          Text(
            'ملاحة الشاحنات',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
      actions: [
        IconButton(
          onPressed: _showRouteOptions,
          icon: const Icon(Icons.settings, color: AppTheme.primary),
        ),
        IconButton(
          onPressed: _emergencyCall,
          icon: const Icon(Icons.emergency, color: AppTheme.error),
        ),
        const SizedBox(width: 20),
      ],
    );
  }

  Widget _buildMapView() {
    return GoogleMap(
      initialCameraPosition: CameraPosition(
        target: _currentPosition,
        zoom: 10,
      ),
      onMapCreated: (GoogleMapController controller) {
        _mapController = controller;
      },
      myLocationEnabled: true,
      myLocationButtonEnabled: false,
      zoomControlsEnabled: false,
      mapToolbarEnabled: false,
      polylines: {
        Polyline(
          polylineId: const PolylineId('route'),
          color: AppTheme.primary,
          width: 5,
          points: [
            _currentPosition,
            const LatLng(24.5, 46.5),
            const LatLng(24.0, 46.0),
            const LatLng(23.5, 45.5),
            const LatLng(23.0, 45.0),
            const LatLng(22.5, 44.5),
            const LatLng(22.0, 44.0),
            const LatLng(21.8, 43.5),
            const LatLng(21.6, 42.8),
            const LatLng(21.5, 42.0),
            const LatLng(21.4, 41.0),
            const LatLng(21.4, 40.0),
            const LatLng(21.42, 39.8262),
          ],
        ),
      },
      markers: {
        // Current position
        Marker(
          markerId: const MarkerId('current'),
          position: _currentPosition,
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueBlue),
          infoWindow: InfoWindow(
            title: 'موقعك الحالي',
            snippet: 'الرياض',
          ),
        ),
        // Destination
        Marker(
          markerId: const MarkerId('destination'),
          position: _destination,
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed),
          infoWindow: InfoWindow(
            title: 'الوجهة',
            snippet: 'جدة',
          ),
        ),
        // Truck stops
        ..._truckStops.map((stop) => Marker(
          markerId: MarkerId(stop.name),
          position: stop.location,
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen),
          infoWindow: InfoWindow(
            title: stop.name,
            snippet: 'محطة وقود واستراحة',
          ),
        )),
        // Restrictions
        ..._restrictions.map((restriction) => Marker(
          markerId: MarkerId(restriction.description),
          position: _currentPosition, // Would be actual restriction location
          icon: BitmapDescriptor.defaultMarkerWithHue(
            restriction.severity == RestrictionSeverity.danger 
              ? BitmapDescriptor.hueRed 
              : restriction.severity == RestrictionSeverity.warning
                ? BitmapDescriptor.hueOrange
                : BitmapDescriptor.hueYellow,
          ),
          infoWindow: InfoWindow(
            title: 'تقييد على الشاحنات',
            snippet: restriction.description,
          ),
        )),
      },
    ).animate(controller: _mapController)
      .fadeIn();
  }

  Widget _buildNavigationInfo() {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.95),
      borderColor: AppTheme.primary.withOpacity(0.3),
      boxShadow: AppShadows.glowing(AppTheme.primary),
      child: Column(
        children: [
          // Current step
          Row(
            children: [
              Container(
                width: 50,
                height: 50,
                decoration: BoxDecoration(
                  color: AppTheme.primary.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(25),
                ),
                child: Icon(
                  _routeSteps.isNotEmpty ? _routeSteps[_currentStep].icon : Icons.directions,
                  color: AppTheme.primary,
                  size: 28,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      _routeSteps.isNotEmpty ? _routeSteps[_currentStep].instruction : 'جاري تحميل المسار...',
                      style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        Text(
                          '${_routeSteps.isNotEmpty ? _routeSteps[_currentStep].distance.toStringAsFixed(1) : 0} كم',
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: AppTheme.textSecondary,
                          ),
                        ),
                        const SizedBox(width: 12),
                        Text(
                          _routeSteps.isNotEmpty ? _routeSteps[_currentStep].duration : '0 دقيقة',
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: AppTheme.textSecondary,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Progress bar
          Container(
            height: 8,
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.3),
              borderRadius: BorderRadius.circular(4),
            ),
            child: FractionallySizedBox(
              alignment: Alignment.centerLeft,
              widthFactor: (_currentStep + 1) / _routeSteps.length,
              child: Container(
                decoration: BoxDecoration(
                  color: AppTheme.primary,
                  borderRadius: BorderRadius.circular(4),
                ),
              ),
            ),
          ),
          
          const SizedBox(height: 16),
          
          // Trip stats
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              _buildTripStat('السرعة', '${_currentSpeed.toStringAsFixed(0)} كم/س', Icons.speed),
              _buildTripStat('المسافة المتبقية', '${_remainingDistance.toStringAsFixed(0)} كم', Icons.route),
              _buildTripStat('الوصول', _estimatedArrival, Icons.access_time),
            ],
          ),
        ],
      ),
    ).animate(controller: _infoController)
      .fadeIn()
      .slideY(begin: -0.2, end: 0);
  }

  Widget _buildTripStat(String label, String value, IconData icon) {
    return Column(
      children: [
        Icon(icon, color: AppTheme.primary, size: 20),
        const SizedBox(height: 4),
        Text(
          value,
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: AppTheme.textPrimary,
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

  Widget _buildNavigationControls() {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.95),
      borderColor: AppTheme.primary.withOpacity(0.3),
      child: Column(
        children: [
          // Main navigation controls
          Row(
            children: [
              Expanded(
                child: GlowingButton(
                  text: _isNavigating ? 'إيقاف الملاحة' : 'بدء الملاحة',
                  onPressed: _toggleNavigation,
                  color: _isNavigating ? AppTheme.error : AppTheme.primary,
                  icon: _isNavigating ? Icons.stop : Icons.navigation,
                  height: 56,
                ),
              ),
              const SizedBox(width: 12),
              GlassContainer(
                width: 56,
                height: 56,
                radius: 28,
                backgroundColor: Colors.white.withOpacity(0.05),
                borderColor: AppTheme.textHint.withOpacity(0.2),
                child: IconButton(
                  onPressed: _centerMap,
                  icon: const Icon(
                    Icons.my_location,
                    color: AppTheme.primary,
                  ),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          // Secondary controls
          Row(
            children: [
              GlassContainer(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                radius: 12,
                backgroundColor: Colors.white.withOpacity(0.05),
                borderColor: AppTheme.textHint.withOpacity(0.2),
                child: Row(
                  children: [
                    Icon(
                      Icons.traffic,
                      color: _avoidTraffic ? AppTheme.success : AppTheme.textHint,
                      size: 16,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      'تجنب الزحام',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: _avoidTraffic ? AppTheme.success : AppTheme.textHint,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 8),
              GlassContainer(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                radius: 12,
                backgroundColor: Colors.white.withOpacity(0.05),
                borderColor: AppTheme.textHint.withOpacity(0.2),
                child: Row(
                  children: [
                    Icon(
                      Icons.toll,
                      color: _avoidTolls ? AppTheme.success : AppTheme.textHint,
                      size: 16,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      'تجنب الرسوم',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: _avoidTolls ? AppTheme.success : AppTheme.textHint,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 8),
              GlassContainer(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                radius: 12,
                backgroundColor: Colors.white.withOpacity(0.05),
                borderColor: AppTheme.textHint.withOpacity(0.2),
                child: Row(
                  children: [
                    Icon(
                      Icons.highway,
                      color: _preferHighways ? AppTheme.success : AppTheme.textHint,
                      size: 16,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      'طرق سريعة',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: _preferHighways ? AppTheme.success : AppTheme.textHint,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _routeController)
      .fadeIn()
      .slideY(begin: 0.2, end: 0);
  }

  Widget _buildSidePanel() {
    return Positioned(
      top: 200,
      right: 20,
      child: GlassContainer(
        width: 300,
        height: 400,
        radius: 20,
        backgroundColor: Colors.white.withOpacity(0.95),
        borderColor: AppTheme.primary.withOpacity(0.3),
        child: Column(
          children: [
            // Header
            Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    'تفاصيل الرحلة',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      color: AppTheme.textPrimary,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  IconButton(
                    onPressed: () {
                      setState(() {
                        _isNavigating = false;
                      });
                    },
                    icon: const Icon(Icons.close, color: AppTheme.textHint),
                  ),
                ],
              ),
            ),
            
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Route steps
                    Text(
                      'خطوات المسار',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 12),
                    ..._routeSteps.asMap().entries.map((entry) {
                      final index = entry.key;
                      final step = entry.value;
                      return _buildRouteStep(step, index);
                    }),
                    
                    const SizedBox(height: 20),
                    
                    // Restrictions
                    Text(
                      'تقييدات الشاحنات',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 12),
                    ..._restrictions.map((restriction) => _buildRestriction(restriction)),
                    
                    const SizedBox(height: 20),
                    
                    // Truck stops
                    Text(
                      'محطات الشاحنات',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 12),
                    ..._truckStops.map((stop) => _buildTruckStop(stop)),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildRouteStep(NavigationStep step, int index) {
    final isCurrent = index == _currentStep;
    final isCompleted = index < _currentStep;
    
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: isCurrent 
          ? AppTheme.primary.withOpacity(0.1)
          : isCompleted
            ? AppTheme.success.withOpacity(0.1)
            : Colors.white.withOpacity(0.05),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: isCurrent 
            ? AppTheme.primary.withOpacity(0.3)
            : isCompleted
              ? AppTheme.success.withOpacity(0.3)
              : AppTheme.textHint.withOpacity(0.2),
        ),
      ),
      child: Row(
        children: [
          Container(
            width: 32,
            height: 32,
            decoration: BoxDecoration(
              color: isCurrent 
                ? AppTheme.primary
                : isCompleted
                  ? AppTheme.success
                  : AppTheme.textHint,
              borderRadius: BorderRadius.circular(16),
            ),
            child: Icon(
              isCompleted ? Icons.check : step.icon,
              color: Colors.white,
              size: 16,
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  step.instruction,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: isCurrent ? FontWeight.w600 : FontWeight.normal,
                  ),
                ),
                const SizedBox(height: 4),
                Row(
                  children: [
                    Text(
                      '${step.distance.toStringAsFixed(1)} كم',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textSecondary,
                      ),
                    ),
                    const SizedBox(width: 12),
                    Text(
                      step.duration,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textSecondary,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRestriction(HeavyVehicleRestriction restriction) {
    Color color;
    switch (restriction.severity) {
      case RestrictionSeverity.danger:
        color = AppTheme.error;
        break;
      case RestrictionSeverity.warning:
        color = AppTheme.accent;
        break;
      case RestrictionSeverity.info:
        color = AppTheme.primary;
        break;
    }
    
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Row(
        children: [
          Icon(
            Icons.warning,
            color: color,
            size: 16,
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  restriction.description,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  '${restriction.location} • ${restriction.distance.toStringAsFixed(1)} كم',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: AppTheme.textSecondary,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTruckStop(TruckStop stop) {
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.05),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: AppTheme.textHint.withOpacity(0.2)),
      ),
      child: Row(
        children: [
          Icon(
            Icons.local_gas_station,
            color: AppTheme.success,
            size: 16,
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  stop.name,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Row(
                  children: [
                    Text(
                      '${stop.distance.toStringAsFixed(0)} كم',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textSecondary,
                      ),
                    ),
                    const SizedBox(width: 8),
                    Row(
                      children: [
                        Icon(Icons.star, color: AppTheme.accent, size: 12),
                        Text(
                          stop.rating.toString(),
                          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: AppTheme.accent,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  // Methods
  void _toggleNavigation() {
    setState(() {
      _isNavigating = !_isNavigating;
    });
    
    if (_isNavigating) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          backgroundColor: AppTheme.success,
          content: Text('تم بدء الملاحة'),
        ),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          backgroundColor: AppTheme.error,
          content: Text('تم إيقاف الملاحة'),
        ),
      );
    }
  }

  void _centerMap() {
    if (_mapController != null) {
      _mapController!.animateCamera(
        CameraUpdate.newCameraPosition(
          CameraPosition(
            target: _currentPosition,
            zoom: 12,
          ),
        ),
      );
    }
  }

  void _showRouteOptions() {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (context) => GlassContainer(
        margin: const EdgeInsets.all(20),
        padding: const EdgeInsets.all(20),
        radius: 20,
        backgroundColor: AppTheme.background.withOpacity(0.95),
        borderColor: AppTheme.primary.withOpacity(0.3),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'خيارات المسار',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 20),
            SwitchListTile(
              title: const Text('تجنب الزحام'),
              value: _avoidTraffic,
              onChanged: (value) {
                setState(() {
                  _avoidTraffic = value;
                });
              },
              activeColor: AppTheme.primary,
            ),
            SwitchListTile(
              title: const Text('تجنب الرسوم'),
              value: _avoidTolls,
              onChanged: (value) {
                setState(() {
                  _avoidTolls = value;
                });
              },
              activeColor: AppTheme.primary,
            ),
            SwitchListTile(
              title: const Text('تفضيل الطرق السريعة'),
              value: _preferHighways,
              onChanged: (value) {
                setState(() {
                  _preferHighways = value;
                });
              },
              activeColor: AppTheme.primary,
            ),
            const SizedBox(height: 20),
            GlowingButton(
              text: 'إعادة حساب المسار',
              onPressed: () {
                Navigator.pop(context);
                _recalculateRoute();
              },
              color: AppTheme.primary,
            ),
          ],
        ),
      ),
    );
  }

  void _recalculateRoute() {
    // Recalculate route based on preferences
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        backgroundColor: AppTheme.primary,
        content: Text('جاري إعادة حساب المسار...'),
      ),
    );
  }

  void _emergencyCall() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: AppTheme.background,
        title: const Text(
          'اتصال طوارئ',
          style: TextStyle(color: AppTheme.textPrimary),
        ),
        content: const Text(
          'هل تريد الاتصال بالرقم الطوارئ؟',
          style: TextStyle(color: AppTheme.textSecondary),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء'),
          ),
          GlowingButton(
            text: 'اتصال',
            onPressed: () {
              Navigator.pop(context);
              // Make emergency call
            },
            color: AppTheme.error,
          ),
        ],
      ),
    );
  }
}

// Data models
class NavigationStep {
  String instruction;
  double distance;
  String duration;
  IconData icon;
  StepType type;

  NavigationStep({
    required this.instruction,
    required this.distance,
    required this.duration,
    required this.icon,
    required this.type,
  });
}

enum StepType { turn, straight, roundabout, exit }

class HeavyVehicleRestriction {
  String type;
  String description;
  String location;
  double distance;
  RestrictionSeverity severity;

  HeavyVehicleRestriction({
    required this.type,
    required this.description,
    required this.location,
    required this.distance,
    required this.severity,
  });
}

enum RestrictionSeverity { info, warning, danger }

class TruckStop {
  String name;
  LatLng location;
  double distance;
  List<String> services;
  double rating;

  TruckStop({
    required this.name,
    required this.location,
    required this.distance,
    required this.services,
    required this.rating,
  });
}
