import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../services/trip_service.dart';

class TripHistoryScreen extends StatefulWidget {
  const TripHistoryScreen({super.key});

  @override
  State<TripHistoryScreen> createState() => _TripHistoryScreenState();
}

class _TripHistoryScreenState extends State<TripHistoryScreen> {
  @override
  void initState() {
    super.initState();
    _loadHistory();
  }

  Future<void> _loadHistory() async {
    final tripService = Provider.of<TripService>(context, listen: false);
    await tripService.fetchTripHistory();
  }

  @override
  Widget build(BuildContext context) {
    final tripService = Provider.of<TripService>(context);
    final trips = tripService.tripHistory;

    return Scaffold(
      appBar: AppBar(
        title: const Text('سجل الرحلات'),
        actions: [
          IconButton(
            icon: const Icon(Icons.filter_list),
            onPressed: () {
              // Show filter dialog
            },
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _loadHistory,
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
                          Icons.history,
                          size: 80,
                          color: Colors.white.withOpacity(0.3),
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'لا يوجد سجل للرحلات',
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
                      return _buildHistoryCard(trip);
                    },
                  ),
      ),
    );
  }

  Widget _buildHistoryCard(dynamic trip) {
    final status = trip['status'] ?? 'completed';
    final statusColor = tripStatusColors[status] ?? Colors.grey;
    final statusLabel = tripStatusLabels[status] ?? status;

    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: const Color(0xFF1B2E4D),
        borderRadius: BorderRadius.circular(12),
      ),
      child: ExpansionTile(
        tilePadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        childrenPadding: const EdgeInsets.all(16),
        expandedAlignment: Alignment.topRight,
        leading: Container(
          width: 50,
          height: 50,
          decoration: BoxDecoration(
            color: statusColor.withOpacity(0.2),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Icon(
            _getStatusIcon(status),
            color: statusColor,
            size: 24,
          ),
        ),
        title: Text(
          'رحلة #${trip['_id']?.toString().substring(0, 8) ?? '---'}',
          style: const TextStyle(
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 4),
            Text(
              DateFormat('yyyy/MM/dd - HH:mm').format(
                DateTime.parse(trip['createdAt'] ?? DateTime.now().toIso8601String()),
              ),
              style: TextStyle(
                fontSize: 12,
                color: Colors.white.withOpacity(0.7),
              ),
            ),
            const SizedBox(height: 4),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: statusColor.withOpacity(0.2),
                borderRadius: BorderRadius.circular(4),
              ),
              child: Text(
                statusLabel,
                style: TextStyle(
                  fontSize: 12,
                  color: statusColor,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ],
        ),
        trailing: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Text(
              '${trip['price'] ?? '--'} ر.س',
              style: const TextStyle(
                fontWeight: FontWeight.bold,
                color: Color(0xFFD4AF37),
              ),
            ),
            const SizedBox(height: 4),
            Text(
              '${trip['distance'] ?? '--'} كم',
              style: TextStyle(
                fontSize: 12,
                color: Colors.white.withOpacity(0.7),
              ),
            ),
          ],
        ),
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildDetailRow(
                icon: Icons.location_on,
                label: 'نقطة الاستلام',
                value: trip['pickupAddress'] ?? 'غير محدد',
              ),
              const SizedBox(height: 8),
              _buildDetailRow(
                icon: Icons.location_on,
                label: 'نقطة التسليم',
                value: trip['deliveryAddress'] ?? 'غير محدد',
              ),
              const SizedBox(height: 8),
              _buildDetailRow(
                icon: Icons.person,
                label: 'العميل',
                value: trip['customerName'] ?? 'غير محدد',
              ),
              const SizedBox(height: 8),
              _buildDetailRow(
                icon: Icons.scale,
                label: 'الحمولة',
                value: '${trip['weight'] ?? '--'} كجم',
              ),
              const SizedBox(height: 8),
              _buildDetailRow(
                icon: Icons.access_time,
                label: 'وقت التسليم',
                value: trip['deliveryTime'] != null
                    ? DateFormat('HH:mm').format(DateTime.parse(trip['deliveryTime']))
                    : '--:--',
              ),
              if (trip['notes'] != null && trip['notes'].toString().isNotEmpty) ...[
                const SizedBox(height: 8),
                _buildDetailRow(
                  icon: Icons.note,
                  label: 'ملاحظات',
                  value: trip['notes'],
                ),
              ],
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: () {
                        // View trip on map
                      },
                      icon: const Icon(Icons.map),
                      label: const Text('عرض على الخريطة'),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: () {
                        // View invoice
                      },
                      icon: const Icon(Icons.receipt),
                      label: const Text('عرض الفاتورة'),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildDetailRow({
    required IconData icon,
    required String label,
    required String value,
  }) {
    return Row(
      children: [
        Icon(icon, size: 16, color: const Color(0xFF0099D8)),
        const SizedBox(width: 8),
        Text(
          '$label: ',
          style: TextStyle(
            fontSize: 12,
            color: Colors.white.withOpacity(0.7),
          ),
        ),
        Expanded(
          child: Text(
            value,
            style: const TextStyle(
              fontSize: 12,
              color: Colors.white,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ],
    );
  }

  IconData _getStatusIcon(String status) {
    switch (status) {
      case 'completed':
        return Icons.check_circle;
      case 'delivered':
        return Icons.local_shipping;
      case 'cancelled':
        return Icons.cancel;
      default:
        return Icons.local_shipping_outlined;
    }
  }
}
