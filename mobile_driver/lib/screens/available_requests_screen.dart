import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/trip_service.dart';

class AvailableRequestsScreen extends StatefulWidget {
  const AvailableRequestsScreen({super.key});

  @override
  State<AvailableRequestsScreen> createState() => _AvailableRequestsScreenState();
}

class _AvailableRequestsScreenState extends State<AvailableRequestsScreen> {
  @override
  void initState() {
    super.initState();
    _loadAvailableTrips();
  }

  Future<void> _loadAvailableTrips() async {
    final tripService = Provider.of<TripService>(context, listen: false);
    await tripService.fetchAvailableTrips();
  }

  Future<void> _acceptTrip(String tripId) async {
    final tripService = Provider.of<TripService>(context, listen: false);

    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('قبول الرحلة'),
        content: const Text('هل تريد قبول هذه الرحلة؟'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('قبول'),
          ),
        ],
      ),
    );

    if (confirm != true) return;

    final success = await tripService.acceptTrip(tripId);

    if (success) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('تم قبول الرحلة بنجاح'),
          backgroundColor: Colors.green,
        ),
      );
      Navigator.pushReplacementNamed(context, '/active-trip');
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('فشل قبول الرحلة'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final tripService = Provider.of<TripService>(context);
    final trips = tripService.availableTrips;

    return Scaffold(
      appBar: AppBar(
        title: const Text('الطلبات المتاحة'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadAvailableTrips,
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _loadAvailableTrips,
        child: tripService.isLoading
            ? const Center(
                child: CircularProgressIndicator(
                  valueColor: AlwaysStoppedAnimation<Color>(Color(0xFFD4AF37)),
                ),
              )
            : trips.isEmpty
                ? Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.local_shipping_outlined,
                          size: 80,
                          color: Colors.white.withOpacity(0.3),
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'لا توجد طلبات متاحة حالياً',
                          style: TextStyle(
                            fontSize: 18,
                            color: Colors.white.withOpacity(0.7),
                          ),
                        ),
                      ],
                    ),
                  )
                : ListView.builder(
                    padding: const EdgeInsets.all(16),
                    itemCount: trips.length,
                    itemBuilder: (context, index) {
                      final trip = trips[index];
                      return _buildTripCard(trip);
                    },
                  ),
      ),
    );
  }

  Widget _buildTripCard(dynamic trip) {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      decoration: BoxDecoration(
        color: const Color(0xFF1B2E4D),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFFD4AF37).withOpacity(0.3),
          width: 1,
        ),
      ),
      child: Column(
        children: [
          // Header
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: const Color(0xFFD4AF37).withOpacity(0.1),
              borderRadius: const BorderRadius.vertical(
                top: Radius.circular(16),
              ),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'طلب #${trip['_id']?.toString().substring(0, 8) ?? '---'}',
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'تم الإنشاء: ${_formatDate(trip['createdAt'])}',
                      style: TextStyle(
                        fontSize: 12,
                        color: Colors.white.withOpacity(0.7),
                      ),
                    ),
                  ],
                ),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 6,
                  ),
                  decoration: BoxDecoration(
                    color: const Color(0xFFD4AF37),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    '${trip['price'] ?? '--'} ر.س',
                    style: const TextStyle(
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF0A1428),
                    ),
                  ),
                ),
              ],
            ),
          ),

          // Body
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                _buildLocationInfo(
                  icon: Icons.location_on,
                  color: Colors.green,
                  label: 'الاستلام',
                  address: trip['pickupAddress'] ?? 'غير محدد',
                ),
                const SizedBox(height: 12),
                _buildLocationInfo(
                  icon: Icons.location_on,
                  color: Colors.red,
                  label: 'التسليم',
                  address: trip['deliveryAddress'] ?? 'غير محدد',
                ),
                const Divider(height: 24, color: Colors.white24),
                Row(
                  children: [
                    Expanded(
                      child: _buildInfoItem(
                        icon: Icons.scale,
                        label: 'الوزن',
                        value: '${trip['weight'] ?? '--'} كجم',
                      ),
                    ),
                    Expanded(
                      child: _buildInfoItem(
                        icon: Icons.straighten,
                        label: 'المسافة',
                        value: '${trip['distance'] ?? '--'} كم',
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),

          // Footer
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: const Color(0xFF0F3460).withOpacity(0.5),
              borderRadius: const BorderRadius.vertical(
                bottom: Radius.circular(16),
              ),
            ),
            child: Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: () {
                      // View details
                    },
                    icon: const Icon(Icons.visibility),
                    label: const Text('التفاصيل'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () => _acceptTrip(trip['_id']),
                    icon: const Icon(Icons.check),
                    label: const Text('قبول'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF27AE60),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLocationInfo({
    required IconData icon,
    required Color color,
    required String label,
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
                label,
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

  Widget _buildInfoItem({
    required IconData icon,
    required String label,
    required String value,
  }) {
    return Row(
      children: [
        Icon(icon, color: const Color(0xFF0099D8), size: 20),
        const SizedBox(width: 8),
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              label,
              style: TextStyle(
                fontSize: 12,
                color: Colors.white.withOpacity(0.7),
              ),
            ),
            Text(
              value,
              style: const TextStyle(
                fontSize: 14,
                color: Colors.white,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
      ],
    );
  }

  String _formatDate(String? dateString) {
    if (dateString == null) return '--';
    try {
      final date = DateTime.parse(dateString);
      return '${date.day}/${date.month}/${date.year}';
    } catch (e) {
      return '--';
    }
  }
}
