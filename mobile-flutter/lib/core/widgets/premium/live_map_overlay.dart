// ============================================
// 🗺️ Live Map Overlay - Premium Glowing Map Effects
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../../theme/app_theme.dart';
import '../glass_container.dart';

class LiveMapOverlay extends StatefulWidget {
  final LatLng pickupLocation;
  final LatLng deliveryLocation;
  final LatLng? currentLocation;
  final List<LatLng> routePoints;
  final String? driverName;
  final String? estimatedTime;
  final bool isLive;

  const LiveMapOverlay({
    super.key,
    required this.pickupLocation,
    required this.deliveryLocation,
    this.currentLocation,
    required this.routePoints,
    this.driverName,
    this.estimatedTime,
    this.isLive = true,
  });

  @override
  State<LiveMapOverlay> createState() => _LiveMapOverlayState();
}

class _LiveMapOverlayState extends State<LiveMapOverlay>
    with TickerProviderStateMixin {
  late AnimationController _pulseController;
  late AnimationController _routeController;
  late Animation<double> _pulseAnimation;
  late Animation<double> _routeAnimation;

  @override
  void initState() {
    super.initState();
    
    _pulseController = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    );
    _pulseAnimation = Tween<double>(
      begin: 1.0,
      end: 1.3,
    ).animate(CurvedAnimation(
      parent: _pulseController,
      curve: Curves.easeInOut,
    ));
    _pulseController.repeat(reverse: true);

    _routeController = AnimationController(
      duration: const Duration(seconds: 3),
      vsync: this,
    );
    _routeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _routeController,
      curve: Curves.easeInOut,
    ));
    _routeController.repeat();
  }

  @override
  void dispose() {
    _pulseController.dispose();
    _routeController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        // Glowing Route Line
        if (widget.routePoints.isNotEmpty)
          AnimatedBuilder(
            animation: _routeAnimation,
            builder: (context, child) {
              return _GlowingPolyline(
                points: widget.routePoints,
                color: AppTheme.primary,
                width: 4,
                glowIntensity: _routeAnimation.value,
              );
            },
          ),

        // Pickup Marker with Glow
        _GlowingMarker(
          position: widget.pickupLocation,
          icon: Icons.location_on,
          color: AppTheme.success,
          glowColor: AppTheme.success,
          size: 40,
          label: 'نقطة الاستلام',
        ),

        // Delivery Marker with Pulse
        _GlowingMarker(
          position: widget.deliveryLocation,
          icon: Icons.flag,
          color: AppTheme.error,
          glowColor: AppTheme.error,
          size: 40,
          label: 'نقطة التسليم',
          isPulsing: true,
        ),

        // Moving Truck Marker
        if (widget.currentLocation != null)
          _AnimatedTruckMarker(
            position: widget.currentLocation!,
            isLive: widget.isLive,
          ),

        // Floating ETA Card
        if (widget.estimatedTime != null)
          Positioned(
            top: 20,
            left: 20,
            right: 20,
            child: FloatingGlassContainer(
              radius: 16,
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      gradient: AppTheme.primaryGradient,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: const Icon(
                      Icons.access_time,
                      color: Colors.white,
                      size: 20,
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          'الوقت المتوقع',
                          style: TextStyle(
                            color: AppTheme.textSecondary,
                            fontSize: 12,
                          ),
                        ),
                        Text(
                          widget.estimatedTime!,
                          style: const TextStyle(
                            color: AppTheme.textPrimary,
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ).animate().fadeIn().slideY(begin: 0.2, end: 0),
          ),

        // Driver Info Card
        if (widget.driverName != null)
          Positioned(
            bottom: 20,
            left: 20,
            right: 20,
            child: GlassContainer(
              radius: 16,
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  Container(
                    width: 50,
                    height: 50,
                    decoration: BoxDecoration(
                      gradient: AppTheme.primaryGradient,
                      borderRadius: BorderRadius.circular(25),
                      boxShadow: [
                        BoxShadow(
                          color: AppTheme.primary.withOpacity(0.4),
                          blurRadius: 15,
                          spreadRadius: 0,
                        ),
                      ],
                    ),
                    child: const Icon(
                      Icons.person,
                      color: Colors.white,
                      size: 24,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'السائق',
                          style: const TextStyle(
                            color: AppTheme.textSecondary,
                            fontSize: 12,
                          ),
                        ),
                        Text(
                          widget.driverName!,
                          style: const TextStyle(
                            color: AppTheme.textPrimary,
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        if (widget.isLive)
                          Row(
                            children: [
                              Container(
                                width: 8,
                                height: 8,
                                decoration: BoxDecoration(
                                  color: AppTheme.success,
                                  borderRadius: BorderRadius.circular(4),
                                ),
                              ),
                              const SizedBox(width: 6),
                              const Text(
                                'متصل الآن',
                                style: TextStyle(
                                  color: AppTheme.success,
                                  fontSize: 12,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ],
                          ),
                      ],
                    ),
                  ),
                ],
              ),
            ).animate().fadeIn().slideY(begin: 0.2, end: 0),
          ),
      ],
    );
  }
}

class _GlowingMarker extends StatefulWidget {
  final LatLng position;
  final IconData icon;
  final Color color;
  final Color glowColor;
  final double size;
  final String label;
  final bool isPulsing;

  const _GlowingMarker({
    required this.position,
    required this.icon,
    required this.color,
    required this.glowColor,
    required this.size,
    required this.label,
    this.isPulsing = false,
  });

  @override
  State<_GlowingMarker> createState() => _GlowingMarkerState();
}

class _GlowingMarkerState extends State<_GlowingMarker>
    with SingleTickerProviderStateMixin {
  late AnimationController _pulseController;
  late Animation<double> _pulseAnimation;

  @override
  void initState() {
    super.initState();
    
    if (widget.isPulsing) {
      _pulseController = AnimationController(
        duration: const Duration(seconds: 1.5),
        vsync: this,
      );
      _pulseAnimation = Tween<double>(
        begin: 1.0,
        end: 1.5,
      ).animate(CurvedAnimation(
        parent: _pulseController,
        curve: Curves.easeInOut,
      ));
      _pulseController.repeat(reverse: true);
    }
  }

  @override
  void dispose() {
    if (widget.isPulsing) {
      _pulseController.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Marker(
      markerId: MarkerId(widget.label),
      position: widget.position,
      icon: BitmapDescriptor.defaultMarkerWithHue(
        widget.color == AppTheme.success ? BitmapDescriptor.hueGreen : 
        widget.color == AppTheme.error ? BitmapDescriptor.hueRed : 
        BitmapDescriptor.hueOrange,
      ),
      infoWindow: InfoWindow(
        title: widget.label,
        snippet: '',
      ),
    );
  }
}

class _AnimatedTruckMarker extends StatefulWidget {
  final LatLng position;
  final bool isLive;

  const _AnimatedTruckMarker({
    required this.position,
    required this.isLive,
  });

  @override
  State<_AnimatedTruckMarker> createState() => _AnimatedTruckMarkerState();
}

class _AnimatedTruckMarkerState extends State<_AnimatedTruckMarker>
    with SingleTickerProviderStateMixin {
  late AnimationController _rotateController;
  late Animation<double> _rotateAnimation;

  @override
  void initState() {
    super.initState();
    
    _rotateController = AnimationController(
      duration: const Duration(seconds: 4),
      vsync: this,
    );
    _rotateAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _rotateController,
      curve: Curves.linear,
    ));
    
    if (widget.isLive) {
      _rotateController.repeat();
    }
  }

  @override
  void dispose() {
    _rotateController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Marker(
      markerId: const MarkerId('truck'),
      position: widget.position,
      icon: BitmapDescriptor.defaultMarkerWithHue(
        BitmapDescriptor.hueOrange,
      ),
      infoWindow: InfoWindow(
        title: widget.isLive ? 'شحنة نشطة' : 'شحنة متوقفة',
        snippet: '',
      ),
    );
  }
}

class _GlowingPolyline extends StatelessWidget {
  final List<LatLng> points;
  final Color color;
  final double width;
  final double glowIntensity;

  const _GlowingPolyline({
    required this.points,
    required this.color,
    required this.width,
    required this.glowIntensity,
  });

  @override
  Widget build(BuildContext context) {
    return Polyline(
      polylineId: const PolylineId('glowing_route'),
      color: color.withOpacity(0.8 * glowIntensity),
      width: width * glowIntensity,
      points: points,
      patterns: [
        PatternItem.dash(20, 10),
      ],
    );
  }
}
