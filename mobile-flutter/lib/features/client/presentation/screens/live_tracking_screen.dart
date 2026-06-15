// ============================================
// 🗺️ Edham Logistics - Live Tracking Screen
// Premium Cinematic UI with Glassmorphism and Glowing Effects
// ============================================

import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/navigation/app_router.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/live_map_overlay.dart';

class LiveTrackingScreen extends StatefulWidget {
  const LiveTrackingScreen({super.key});

  @override
  State<LiveTrackingScreen> createState() => _LiveTrackingScreenState();
}

class _LiveTrackingScreenState extends State<LiveTrackingScreen>
    with TickerProviderStateMixin {
  late AnimationController _fadeController;
  late AnimationController _slideController;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;
  
  GoogleMapController? _mapController;
  bool _showDriverInfo = true;
  bool _showTrackingDetails = true;

  static const LatLng _riyadh = LatLng(24.7136, 46.6753);
  static const LatLng _jeddah = LatLng(21.4858, 39.1925);

  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _startAnimations();
  }

  void _initializeAnimations() {
    _fadeController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _slideController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );

    _fadeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _fadeController,
      curve: Curves.easeIn,
    ));

    _slideAnimation = Tween<Offset>(
      begin: const Offset(0, 0.3),
      end: Offset.zero,
    ).animate(CurvedAnimation(
      parent: _slideController,
      curve: Curves.easeOutCubic,
    ));
  }

  void _startAnimations() {
    _fadeController.forward();
    Future.delayed(const Duration(milliseconds: 200), () {
      if (mounted) {
        _slideController.forward();
      }
    });
  }

  @override
  void dispose() {
    _fadeController.dispose();
    _slideController.dispose();
    _mapController?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: SafeArea(
        child: FadeTransition(
          opacity: _fadeAnimation,
          child: SlideTransition(
            position: _slideAnimation,
            child: Column(
              children: [
                // Header
                _buildHeader(),

                // Map
                Expanded(
                  flex: 3,
                  child: _buildMap(),
                ),

                // Tracking Details
                if (_showTrackingDetails)
                  Expanded(
                    flex: 2,
                    child: _buildTrackingDetails(),
                  ),

                // Driver Info
                if (_showDriverInfo)
                  _buildDriverInfo(),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.background,
        border: Border(
          bottom: BorderSide(
            color: AppTheme.surface,
            width: 1,
          ),
        ),
      ),
      child: Row(
        children: [
          // Back Button
          IconButton(
            onPressed: () => AppRouter.goBack(context),
            icon: const Icon(Icons.arrow_back),
            color: AppTheme.textPrimary,
          ),

          // Title
          Expanded(
            child: Text(
              'تتبع مباشر',
              textAlign: TextAlign.center,
              style: GoogleFonts.inter(
                fontSize: 18,
                fontWeight: FontWeight.w600,
                color: AppTheme.textPrimary,
              ),
            ),
          ),

          // Menu Button
          IconButton(
            onPressed: () {
              setState(() {
                _showTrackingDetails = !_showTrackingDetails;
                _showDriverInfo = !_showDriverInfo;
              });
            },
            icon: const Icon(Icons.more_vert),
            color: AppTheme.textPrimary,
          ),
        ],
      ),
    );
  }

  Widget _buildMap() {
    final routePoints = [_riyadh, LatLng(23.5, 43.0), _jeddah];
    
    return GlassContainer(
      margin: const EdgeInsets.all(20),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(24),
        child: Stack(
          children: [
            // Google Map with dark style
            GoogleMap(
              onMapCreated: (controller) {
                _mapController = controller;
              },
              initialCameraPosition: const CameraPosition(
                target: _riyadh,
                zoom: 10,
              ),
              mapType: MapType.normal,
              polylines: {
                Polyline(
                  polylineId: const PolylineId('route'),
                  color: AppTheme.primary.withOpacity(0.8),
                  width: 6,
                  points: routePoints,
                  patterns: [
                    PatternItem.dash(20, 10),
                  ],
                ),
              },
              markers: {
                // Pickup Marker
                Marker(
                  markerId: const MarkerId('pickup'),
                  position: _riyadh,
                  icon: BitmapDescriptor.defaultMarkerWithHue(
                    BitmapDescriptor.hueGreen,
                  ),
                  infoWindow: const InfoWindow(title: 'نقطة الاستلام'),
                ),
                // Dropoff Marker
                Marker(
                  markerId: const MarkerId('dropoff'),
                  position: _jeddah,
                  icon: BitmapDescriptor.defaultMarkerWithHue(
                    BitmapDescriptor.hueRed,
                  ),
                  infoWindow: const InfoWindow(title: 'نقطة التسليم'),
                ),
                // Moving Truck Marker
                Marker(
                  markerId: const MarkerId('truck'),
                  position: LatLng(23.5, 43.0), // Midpoint
                  icon: BitmapDescriptor.defaultMarkerWithHue(
                    BitmapDescriptor.hueOrange,
                  ),
                  infoWindow: const InfoWindow(title: 'شحنة في الطريق'),
                ),
              },
            ),

            // Premium Map Overlay
            LiveMapOverlay(
              pickupLocation: _riyadh,
              deliveryLocation: _jeddah,
              currentLocation: LatLng(23.5, 43.0),
              routePoints: routePoints,
              driverName: 'أحمد السائق',
              estimatedTime: '2-3 ساعات',
              isLive: true,
            ),
          ],
        ),
      ),
    ).animate().fadeIn().scaleY(begin: 0.8, end: 1.0);
  }

  Widget _buildTrackingDetails() {
    return Container(
      margin: const EdgeInsets.fromLTRB(20, 0, 20, 20),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.surface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: AppTheme.surfaceLight,
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Title
          Text(
            'تفاصيل التتبع',
            style: GoogleFonts.inter(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppTheme.textPrimary,
            ),
          ),

          const SizedBox(height: 16),

          // Status Badge
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
            decoration: BoxDecoration(
              color: AppTheme.primary.withOpacity(0.1),
              borderRadius: BorderRadius.circular(20),
            ),
            child: Text(
              'في الطريق',
              style: GoogleFonts.inter(
                fontSize: 12,
                fontWeight: FontWeight.w600,
                color: AppTheme.primary,
              ),
            ),
          ),

          const SizedBox(height: 16),

          // Tracking Info
          _buildTrackingInfoRow('رقم التتبع', '#EDH-1234'),
          _buildTrackingInfoRow('الحالة', 'في الطريق'),
          _buildTrackingInfoRow('الوقت المتوقع', '2-3 ساعات'),
          _buildTrackingInfoRow('المسافة المتبقية', '45 كم'),

          const SizedBox(height: 16),

          // Progress Bar
          Container(
            height: 8,
            decoration: BoxDecoration(
              color: AppTheme.surfaceLight,
              borderRadius: BorderRadius.circular(4),
            ),
            child: FractionallySizedBox(
              alignment: Alignment.centerLeft,
              widthFactor: 0.6, // 60% progress
              child: Container(
                decoration: BoxDecoration(
                  color: AppTheme.primary,
                  borderRadius: BorderRadius.circular(4),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTrackingInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            label,
            style: GoogleFonts.inter(
              fontSize: 14,
              color: AppTheme.textSecondary,
            ),
          ),
          Text(
            value,
            style: GoogleFonts.inter(
              fontSize: 14,
              fontWeight: FontWeight.w600,
              color: AppTheme.textPrimary,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDriverInfo() {
    return Container(
      margin: const EdgeInsets.fromLTRB(20, 0, 20, 20),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.surface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: AppTheme.surfaceLight,
          width: 1,
        ),
      ),
      child: Column(
        children: [
          // Driver Header
          Row(
            children: [
              // Driver Avatar
              Container(
                width: 50,
                height: 50,
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [
                      AppTheme.primary,
                      AppTheme.primaryDark,
                    ],
                  ),
                  borderRadius: BorderRadius.circular(25),
                ),
                child: const Center(
                  child: Icon(
                    Icons.person,
                    color: Colors.white,
                    size: 24,
                  ),
                ),
              ),

              const SizedBox(width: 16),

              // Driver Info
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'أحمد محمد',
                      style: GoogleFonts.inter(
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                        color: AppTheme.textPrimary,
                      ),
                    ),
                    Text(
                      'سائق محترف',
                      style: GoogleFonts.inter(
                        fontSize: 14,
                        color: AppTheme.textSecondary,
                      ),
                    ),
                  ],
                ),
              ),

              // Rating
              Row(
                children: List.generate(5, (index) => Icon(
                  Icons.star,
                  color: index < 4 ? AppTheme.warning : AppTheme.surfaceLight,
                  size: 16,
                )),
              ),
            ],
          ),

          const SizedBox(height: 20),

          // Action Buttons
          Row(
            children: [
              Expanded(
                child: _buildActionButton(
                  icon: Icons.phone,
                  label: 'اتصال',
                  color: AppTheme.primary,
                  onTap: () {},
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildActionButton(
                  icon: Icons.message,
                  label: 'رسالة',
                  color: AppTheme.accent,
                  onTap: () {},
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildActionButton({
    required IconData icon,
    required String label,
    required Color color,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 12),
        decoration: BoxDecoration(
          color: color.withOpacity(0.1),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: color.withOpacity(0.3),
            width: 1,
          ),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, color: color, size: 20),
            const SizedBox(width: 8),
            Text(
              label,
              style: GoogleFonts.inter(
                fontSize: 14,
                fontWeight: FontWeight.w600,
                color: color,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// Custom painter for route line
class RouteLinePainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = const Color(0xFFF97316)
      ..strokeWidth = 4.0
      ..style = PaintingStyle.stroke;

    final path = Path();
    path.moveTo(size.width * 0.2, size.height * 0.3);
    path.quadraticBezierTo(
      size.width * 0.5, size.height * 0.2,
      size.width * 0.8, size.height * 0.7,
    );

    canvas.drawPath(path, paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
