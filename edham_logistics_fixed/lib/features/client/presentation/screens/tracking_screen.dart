// ============================================
// 🗺️ Tracking Screen - Live Shipment Tracking
// ============================================

import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/navigation/app_router.dart';

class TrackingScreen extends StatefulWidget {
  const TrackingScreen({super.key});

  @override
  State<TrackingScreen> createState() => _TrackingScreenState();
}

class _TrackingScreenState extends State<TrackingScreen> {
  final TextEditingController _trackingController = TextEditingController();
  bool _showResults = false;
  GoogleMapController? _mapController;

  static const LatLng _riyadh = LatLng(24.7136, 46.6753);
  static const LatLng _jeddah = LatLng(21.4858, 39.1925);

  @override
  void dispose() {
    _trackingController.dispose();
    _mapController?.dispose();
    super.dispose();
  }

  void _search() {
    if (_trackingController.text.isNotEmpty) {
      setState(() {
        _showResults = true;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: SafeArea(
        child: Column(
          children: [
            // Search Header
            _buildSearchHeader(),

            // Results
            Expanded(
              child: _showResults ? _buildTrackingResults() : _buildEmptyState(),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSearchHeader() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.surface,
        borderRadius: const BorderRadius.vertical(
          bottom: Radius.circular(24),
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 20,
          ),
        ],
      ),
      child: Column(
        children: [
          // Back & Title
          Row(
            children: [
              IconButton(
                onPressed: () => AppRouter.goBack(context),
                icon: const Icon(Icons.arrow_back),
              ),
              const Expanded(
                child: Text(
                  'تتبع الشحنة',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              const SizedBox(width: 48),
            ],
          ),
          const SizedBox(height: 16),

          // Search Field
          TextField(
            controller: _trackingController,
            style: const TextStyle(color: AppTheme.textPrimary),
            decoration: InputDecoration(
              hintText: 'أدخل رقم التتبع',
              prefixIcon: const Icon(Icons.search, color: AppTheme.textHint),
              suffixIcon: IconButton(
                onPressed: () => _trackingController.clear(),
                icon: const Icon(Icons.clear, color: AppTheme.textHint),
              ),
            ),
            onSubmitted: (_) => _search(),
          ),
          const SizedBox(height: 16),

          // Search Button
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: _search,
              icon: const Icon(Icons.track_changes),
              label: const Text('تتبع'),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.track_changes_outlined,
            size: 80,
            color: AppTheme.textHint.withOpacity(0.5),
          ),
          const SizedBox(height: 24),
          const Text(
            'أدخل رقم التتبع لمتابعة شحنتك',
            style: TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 16,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTrackingResults() {
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        // Map Card
        Card(
          child: Container(
            height: 200,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(16),
            ),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(16),
              child: GoogleMap(
                initialCameraPosition: const CameraPosition(
                  target: LatLng(23.5, 43.5),
                  zoom: 6,
                ),
                onMapCreated: (controller) => _mapController = controller,
                markers: {
                  Marker(
                    markerId: const MarkerId('pickup'),
                    position: _riyadh,
                    infoWindow: const InfoWindow(title: 'نقطة الاستلام'),
                    icon: BitmapDescriptor.defaultMarkerWithHue(
                      BitmapDescriptor.hueGreen,
                    ),
                  ),
                  Marker(
                    markerId: const MarkerId('delivery'),
                    position: _jeddah,
                    infoWindow: const InfoWindow(title: 'نقطة التسليم'),
                    icon: BitmapDescriptor.defaultMarkerWithHue(
                      BitmapDescriptor.hueRed,
                    ),
                  ),
                },
                polylines: {
                  Polyline(
                    polylineId: const PolylineId('route'),
                    points: [_riyadh, _jeddah],
                    color: AppTheme.primary,
                    width: 4,
                  ),
                },
              ),
            ),
          ),
        ),

        const SizedBox(height: 16),

        // Shipment Info
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Text(
                      '#EDH-1234',
                      style: Theme.of(context).textTheme.titleLarge,
                    ),
                    const Spacer(),
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 6,
                      ),
                      decoration: BoxDecoration(
                        color: AppTheme.statusTransit.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: const Text(
                        'في الطريق',
                        style: TextStyle(
                          color: AppTheme.statusTransit,
                          fontSize: 12,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                _buildInfoRow('المرسل', 'شركة النجاح للتجارة'),
                _buildInfoRow('الوزن', '2,500 كجم'),
                _buildInfoRow('النوع', 'مبرد'),
                _buildInfoRow('المسافة المتبقية', '350 كم'),
                _buildInfoRow('الوصول المتوقع', 'اليوم، 6:00 م'),
              ],
            ),
          ),
        ),

        const SizedBox(height: 16),

        // Timeline
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'حالة الشحنة',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const SizedBox(height: 16),
                _buildTimelineItem(
                  isFirst: true,
                  isActive: true,
                  icon: Icons.check_circle,
                  title: 'تم تأكيد الطلب',
                  subtitle: '2024-01-15 09:30 ص',
                ),
                _buildTimelineItem(
                  isActive: true,
                  icon: Icons.warehouse,
                  title: 'تم استلام الشحنة',
                  subtitle: '2024-01-15 14:00 م',
                ),
                _buildTimelineItem(
                  isActive: true,
                  icon: Icons.local_shipping,
                  title: 'في الطريق',
                  subtitle: '2024-01-15 16:30 م',
                ),
                _buildTimelineItem(
                  isActive: false,
                  icon: Icons.location_on,
                  title: 'وصول للتسليم',
                  subtitle: 'قريباً',
                ),
                _buildTimelineItem(
                  isLast: true,
                  isActive: false,
                  icon: Icons.check,
                  title: 'تم التسليم',
                  subtitle: 'قريباً',
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Row(
        children: [
          Text(
            '$label: ',
            style: const TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 14,
            ),
          ),
          Text(
            value,
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontSize: 14,
              fontWeight: FontWeight.w500,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTimelineItem({
    bool isFirst = false,
    bool isLast = false,
    required bool isActive,
    required IconData icon,
    required String title,
    required String subtitle,
  }) {
    return Container(
      padding: const EdgeInsets.all(12),
      child: Row(
        children: [
          // Indicator
          Container(
            width: 40,
            height: 40,
            decoration: BoxDecoration(
              color: isActive ? AppTheme.primary : AppTheme.surfaceLight,
              borderRadius: BorderRadius.circular(20),
            ),
            child: Icon(
              icon,
              size: 20,
              color: isActive ? Colors.white : AppTheme.textHint,
            ),
          ),
          const SizedBox(width: 16),
          // Content
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: TextStyle(
                    color: isActive ? AppTheme.textPrimary : AppTheme.textHint,
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  subtitle,
                  style: TextStyle(
                    color: isActive ? AppTheme.textSecondary : AppTheme.textHint,
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
