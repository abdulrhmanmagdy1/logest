import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:provider/provider.dart';
import '../services/trip_service.dart';
import '../services/location_service.dart';
import '../services/upload_service.dart';
import '../services/auth_service.dart';

class ActiveTripScreen extends StatefulWidget {
  const ActiveTripScreen({super.key});

  @override
  State<ActiveTripScreen> createState() => _ActiveTripScreenState();
}

class _ActiveTripScreenState extends State<ActiveTripScreen> {
  GoogleMapController? _mapController;
  StreamSubscription<dynamic>? _tripUpdateSubscription;
  bool _isUploading = false;
  String _currentStatus = TripStatus.accepted;

  final Map<String, Marker> _markers = {};
  List<LatLng> _polylinePoints = [];

  @override
  void initState() {
    super.initState();
    _initializeScreen();
  }

  Future<void> _initializeScreen() async {
    final tripService = Provider.of<TripService>(context, listen: false);
    final locationService = Provider.of<LocationService>(context, listen: false);

    await tripService.fetchActiveTrip();

    if (tripService.activeTrip != null) {
      setState(() {
        _currentStatus = tripService.activeTrip!['status'] ?? TripStatus.accepted;
      });
      _updateMarkers();
      _startLocationTracking();
    }
  }

  void _updateMarkers() {
    final tripService = Provider.of<TripService>(context, listen: false);
    final trip = tripService.activeTrip;

    if (trip == null) return;

    setState(() {
      // Pickup marker
      if (trip['pickupLocation'] != null) {
        _markers['pickup'] = Marker(
          markerId: const MarkerId('pickup'),
          position: LatLng(
            trip['pickupLocation']['lat'],
            trip['pickupLocation']['lng'],
          ),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen),
          infoWindow: const InfoWindow(title: 'نقطة الاستلام'),
        );
      }

      // Delivery marker
      if (trip['deliveryLocation'] != null) {
        _markers['delivery'] = Marker(
          markerId: const MarkerId('delivery'),
          position: LatLng(
            trip['deliveryLocation']['lat'],
            trip['deliveryLocation']['lng'],
          ),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed),
          infoWindow: const InfoWindow(title: 'نقطة التسليم'),
        );
      }
    });
  }

  void _startLocationTracking() {
    final locationService = Provider.of<LocationService>(context, listen: false);
    final tripService = Provider.of<TripService>(context, listen: false);

    locationService.startTracking(onLocationUpdate: (locationData) async {
      if (tripService.activeTrip != null) {
        await tripService.updateLocation(
          tripService.activeTrip!['_id'],
          locationData.latitude!,
          locationData.longitude!,
          speed: locationData.speed,
        );
      }
    });
  }

  Future<void> _updateStatus(String newStatus) async {
    final tripService = Provider.of<TripService>(context, listen: false);
    final trip = tripService.activeTrip;

    if (trip == null) return;

    setState(() => _isUploading = true);

    final success = await tripService.updateTripStatus(
      trip['_id'],
      newStatus,
    );

    setState(() => _isUploading = false);

    if (success) {
      setState(() => _currentStatus = newStatus);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('تم تحديث الحالة: ${tripStatusLabels[newStatus]}'),
          backgroundColor: Colors.green,
        ),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('فشل تحديث الحالة'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  Future<void> _uploadDeliveryProof() async {
    final uploadService = Provider.of<UploadService>(context, listen: false);
    final tripService = Provider.of<TripService>(context, listen: false);
    final authService = Provider.of<AuthService>(context, listen: false);
    final trip = tripService.activeTrip;

    if (trip == null) return;

    final images = await uploadService.pickMultipleImages();
    if (images.isEmpty) return;

    setState(() => _isUploading = true);

    final urls = await uploadService.uploadDeliveryProof(
      images,
      trip['_id'],
      authService.token!,
    );

    setState(() => _isUploading = false);

    if (urls.isNotEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('تم رفع الصور بنجاح'),
          backgroundColor: Colors.green,
        ),
      );
    }
  }

  Future<void> _completeTrip() async {
    final tripService = Provider.of<TripService>(context, listen: false);
    final trip = tripService.activeTrip;

    if (trip == null) return;

    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('إكمال الرحلة'),
        content: const Text('هل أنت متأكد من إكمال الرحلة؟'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('تأكيد'),
          ),
        ],
      ),
    );

    if (confirm != true) return;

    setState(() => _isUploading = true);

    final success = await tripService.completeTrip(
      trip['_id'],
      finalDistance: 25.5, // Calculate actual distance
      deliveryTime: DateTime.now().toIso8601String(),
    );

    setState(() => _isUploading = false);

    if (success) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('تم إكمال الرحلة بنجاح'),
          backgroundColor: Colors.green,
        ),
      );
      Navigator.pushReplacementNamed(context, '/dashboard');
    }
  }

  @override
  void dispose() {
    _mapController?.dispose();
    _tripUpdateSubscription?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final tripService = Provider.of<TripService>(context);
    final trip = tripService.activeTrip;

    if (trip == null) {
      return Scaffold(
        appBar: AppBar(
          title: const Text('الرحلة الحالية'),
        ),
        body: const Center(
          child: Text(
            'لا يوجد رحلة نشطة',
            style: TextStyle(color: Colors.white, fontSize: 18),
          ),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('الرحلة الحالية'),
        actions: [
          IconButton(
            icon: const Icon(Icons.call),
            onPressed: () {
              // Call customer
            },
          ),
          IconButton(
            icon: const Icon(Icons.message),
            onPressed: () {
              // Message customer
            },
          ),
        ],
      ),
      body: Stack(
        children: [
          // Map
          GoogleMap(
            onMapCreated: (controller) => _mapController = controller,
            initialCameraPosition: CameraPosition(
              target: LatLng(
                trip['pickupLocation']?['lat'] ?? 24.7136,
                trip['pickupLocation']?['lng'] ?? 46.6753,
              ),
              zoom: 13,
            ),
            markers: _markers.values.toSet(),
            myLocationEnabled: true,
            myLocationButtonEnabled: true,
            mapType: MapType.normal,
            zoomControlsEnabled: false,
          ),

          // Trip Details Panel
          Positioned(
            bottom: 0,
            left: 0,
            right: 0,
            child: Container(
              decoration: BoxDecoration(
                color: const Color(0xFF0A1428),
                borderRadius: const BorderRadius.vertical(
                  top: Radius.circular(24),
                ),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.3),
                    blurRadius: 20,
                    offset: const Offset(0, -5),
                  ),
                ],
              ),
              child: SafeArea(
                child: Padding(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      // Handle
                      Center(
                        child: Container(
                          width: 40,
                          height: 4,
                          decoration: BoxDecoration(
                            color: Colors.white.withOpacity(0.3),
                            borderRadius: BorderRadius.circular(2),
                          ),
                        ),
                      ),
                      const SizedBox(height: 20),

                      // Status Badge
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 8,
                        ),
                        decoration: BoxDecoration(
                          color: tripStatusColors[_currentStatus]?.withOpacity(0.2) ?? Colors.grey,
                          borderRadius: BorderRadius.circular(20),
                          border: Border.all(
                            color: tripStatusColors[_currentStatus] ?? Colors.grey,
                            width: 1,
                          ),
                        ),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Icon(
                              Icons.circle,
                              size: 8,
                              color: tripStatusColors[_currentStatus] ?? Colors.grey,
                            ),
                            const SizedBox(width: 8),
                            Text(
                              tripStatusLabels[_currentStatus] ?? _currentStatus,
                              style: TextStyle(
                                color: tripStatusColors[_currentStatus] ?? Colors.grey,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(height: 16),

                      // Customer Info
                      Row(
                        children: [
                          Container(
                            width: 50,
                            height: 50,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              color: const Color(0xFF0099D8).withOpacity(0.2),
                            ),
                            child: const Icon(
                              Icons.person,
                              color: Color(0xFF0099D8),
                            ),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  trip['customerName'] ?? 'العميل',
                                  style: const TextStyle(
                                    fontSize: 18,
                                    fontWeight: FontWeight.bold,
                                    color: Colors.white,
                                  ),
                                ),
                                Text(
                                  trip['customerPhone'] ?? '',
                                  style: TextStyle(
                                    fontSize: 14,
                                    color: Colors.white.withOpacity(0.7),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 16),

                      // Locations
                      _buildLocationRow(
                        icon: Icons.location_on,
                        color: Colors.green,
                        title: 'الاستلام',
                        address: trip['pickupAddress'] ?? 'عنوان الاستلام',
                      ),
                      const SizedBox(height: 12),
                      _buildLocationRow(
                        icon: Icons.location_on,
                        color: Colors.red,
                        title: 'التسليم',
                        address: trip['deliveryAddress'] ?? 'عنوان التسليم',
                      ),
                      const SizedBox(height: 20),

                      // Action Buttons
                      _buildActionButtons(),
                    ],
                  ),
                ),
              ),
            ),
          ),

          // Loading Overlay
          if (_isUploading)
            Container(
              color: Colors.black.withOpacity(0.5),
              child: const Center(
                child: CircularProgressIndicator(
                  valueColor: AlwaysStoppedAnimation<Color>(Color(0xFFD4AF37)),
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildLocationRow({
    required IconData icon,
    required Color color,
    required String title,
    required String address,
  }) {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: color.withOpacity(0.2),
            borderRadius: BorderRadius.circular(8),
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
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.white.withOpacity(0.7),
                ),
              ),
              Text(
                address,
                style: const TextStyle(
                  fontSize: 14,
                  color: Colors.white,
                ),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildActionButtons() {
    switch (_currentStatus) {
      case TripStatus.accepted:
        return ElevatedButton.icon(
          onPressed: () => _updateStatus(TripStatus.pickup),
          icon: const Icon(Icons.navigation),
          label: const Text('البدء نحو الاستلام'),
          style: ElevatedButton.styleFrom(
            minimumSize: const Size(double.infinity, 56),
            backgroundColor: const Color(0xFF0099D8),
          ),
        );

      case TripStatus.pickup:
        return ElevatedButton.icon(
          onPressed: () => _updateStatus(TripStatus.inTransit),
          icon: const Icon(Icons.local_shipping),
          label: const Text('تم الاستلام - في الطريق'),
          style: ElevatedButton.styleFrom(
            minimumSize: const Size(double.infinity, 56),
            backgroundColor: const Color(0xFFD4AF37),
          ),
        );

      case TripStatus.inTransit:
        return ElevatedButton.icon(
          onPressed: () => _updateStatus(TripStatus.arrived),
          icon: const Icon(Icons.location_on),
          label: const Text('تم الوصول'),
          style: ElevatedButton.styleFrom(
            minimumSize: const Size(double.infinity, 56),
            backgroundColor: Colors.orange,
          ),
        );

      case TripStatus.arrived:
        return Column(
          children: [
            ElevatedButton.icon(
              onPressed: _uploadDeliveryProof,
              icon: const Icon(Icons.camera_alt),
              label: const Text('رفع صور إثبات التسليم'),
              style: ElevatedButton.styleFrom(
                minimumSize: const Size(double.infinity, 56),
                backgroundColor: const Color(0xFF0099D8),
              ),
            ),
            const SizedBox(height: 12),
            ElevatedButton.icon(
              onPressed: () => _updateStatus(TripStatus.delivered),
              icon: const Icon(Icons.check_circle),
              label: const Text('تم التسليم'),
              style: ElevatedButton.styleFrom(
                minimumSize: const Size(double.infinity, 56),
                backgroundColor: Colors.green,
              ),
            ),
          ],
        );

      case TripStatus.delivered:
        return ElevatedButton.icon(
          onPressed: _completeTrip,
          icon: const Icon(Icons.done_all),
          label: const Text('إكمال الرحلة'),
          style: ElevatedButton.styleFrom(
            minimumSize: const Size(double.infinity, 56),
            backgroundColor: Colors.green,
          ),
        );

      default:
        return const SizedBox.shrink();
    }
  }
}
