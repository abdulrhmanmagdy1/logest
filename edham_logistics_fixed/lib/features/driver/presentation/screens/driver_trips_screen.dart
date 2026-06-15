// ============================================
// 🚛 Driver Trips Screen - رحلات السائق
// ============================================

import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';
import '../../data/models/trip_model.dart';

class DriverTripsScreen extends StatefulWidget {
  const DriverTripsScreen({super.key});

  @override
  State<DriverTripsScreen> createState() => _DriverTripsScreenState();
}

class _DriverTripsScreenState extends State<DriverTripsScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  
  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'رحلاتي',
          style: TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: AppTheme.primaryColor,
          labelColor: AppTheme.primaryColor,
          unselectedLabelColor: AppTheme.textSecondary,
          tabs: const [
            Tab(text: 'النشطة'),
            Tab(text: 'مكتملة'),
            Tab(text: 'الكل'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildTripsList('active'),
          _buildTripsList('completed'),
          _buildTripsList('all'),
        ],
      ),
    );
  }

  Widget _buildTripsList(String filter) {
    final trips = _getDemoTrips(filter);
    
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: trips.length,
      itemBuilder: (context, index) {
        final trip = trips[index];
        return _TripCard(trip: trip);
      },
    );
  }

  List<TripModel> _getDemoTrips(String filter) {
    final allTrips = [
      TripModel(
        id: '1',
        trackingNumber: 'EDH-2024-001',
        status: TripStatus.inProgress,
        pickup: 'مستودع الرياض',
        delivery: 'سوبرماركت جدة',
        cargoType: 'مجمد (-18°)',
        weight: '2,500 كجم',
        scheduledTime: '08:00 ص',
        estimatedArrival: '14:30 م',
        customerName: 'أسواق التميمي',
        customerPhone: '0501234567',
        temperature: -18,
        distance: '950 كم',
      ),
      TripModel(
        id: '2',
        trackingNumber: 'EDH-2024-002',
        status: TripStatus.pending,
        pickup: 'مصنع الدمام',
        delivery: 'مستودع الخبر',
        cargoType: 'مبرد (+4°)',
        weight: '1,800 كجم',
        scheduledTime: '10:00 ص',
        estimatedArrival: '12:00 م',
        customerName: 'شركة الغذاء',
        customerPhone: '0507654321',
        temperature: 4,
        distance: '45 كم',
      ),
      TripModel(
        id: '3',
        trackingNumber: 'EDH-2024-003',
        status: TripStatus.completed,
        pickup: 'مزرعة القصيم',
        delivery: 'سوق الرياض',
        cargoType: 'خضروات مبردة',
        weight: '3,200 كجم',
        scheduledTime: '06:00 ص',
        estimatedArrival: '11:00 ص',
        customerName: 'سوق الخضار المركزي',
        customerPhone: '0509876543',
        temperature: 2,
        distance: '380 كم',
      ),
    ];

    switch (filter) {
      case 'active':
        return allTrips.where((t) => 
          t.status == TripStatus.inProgress || 
          t.status == TripStatus.pending
        ).toList();
      case 'completed':
        return allTrips.where((t) => 
          t.status == TripStatus.completed
        ).toList();
      default:
        return allTrips;
    }
  }
}

class _TripCard extends StatelessWidget {
  final TripModel trip;

  const _TripCard({required this.trip});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppTheme.dividerColor),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // Tracking Number
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                  color: AppTheme.primaryColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(
                  trip.trackingNumber,
                  style: TextStyle(
                    color: AppTheme.primaryColor,
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              
              // Status Badge
              _StatusBadge(status: trip.status),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Temperature Alert
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: _getTempColor(trip.temperature).withOpacity(0.1),
              borderRadius: BorderRadius.circular(12),
              border: Border.all(
                color: _getTempColor(trip.temperature).withOpacity(0.3),
              ),
            ),
            child: Row(
              children: [
                Icon(
                  Icons.thermostat,
                  color: _getTempColor(trip.temperature),
                  size: 20,
                ),
                const SizedBox(width: 8),
                Text(
                  'درجة الحرارة: ${trip.temperature}°C',
                  style: TextStyle(
                    color: _getTempColor(trip.temperature),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const Spacer(),
                Text(
                  trip.cargoType,
                  style: TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
          
          const SizedBox(height: 16),
          
          // Route
          Row(
            children: [
              Expanded(
                child: _LocationPoint(
                  icon: Icons.location_on,
                  title: trip.pickup,
                  subtitle: trip.scheduledTime,
                  isPickup: true,
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 12),
                child: Column(
                  children: [
                    Icon(
                      Icons.arrow_forward,
                      color: AppTheme.textSecondary,
                      size: 20,
                    ),
                    Text(
                      trip.distance,
                      style: TextStyle(
                        color: AppTheme.textSecondary,
                        fontSize: 10,
                      ),
                    ),
                  ],
                ),
              ),
              Expanded(
                child: _LocationPoint(
                  icon: Icons.flag,
                  title: trip.delivery,
                  subtitle: trip.estimatedArrival,
                  isPickup: false,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Customer Info
          Row(
            children: [
              Icon(
                Icons.person,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  trip.customerName,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 14,
                  ),
                ),
              ),
              IconButton(
                onPressed: () {},
                icon: Icon(
                  Icons.phone,
                  color: AppTheme.successColor,
                  size: 20,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          // Action Buttons
          if (trip.status == TripStatus.pending)
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: () {},
                icon: const Icon(Icons.play_arrow),
                label: const Text('بدء الرحلة'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppTheme.successColor,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 12),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
              ),
            ),
          
          if (trip.status == TripStatus.inProgress)
            Row(
              children: [
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () {},
                    icon: const Icon(Icons.check_circle),
                    label: const Text('تم الاستلام'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppTheme.primaryColor,
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () {},
                    icon: const Icon(Icons.local_shipping),
                    label: const Text('في الطريق'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppTheme.warningColor,
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                  ),
                ),
              ],
            ),
        ],
      ),
    );
  }

  Color _getTempColor(double temp) {
    if (temp <= -18) return Colors.blue;
    if (temp <= 4) return Colors.cyan;
    return Colors.orange;
  }
}

class _StatusBadge extends StatelessWidget {
  final TripStatus status;

  const _StatusBadge({required this.status});

  @override
  Widget build(BuildContext context) {
    Color color;
    String text;
    
    switch (status) {
      case TripStatus.pending:
        color = AppTheme.warningColor;
        text = 'معلقة';
        break;
      case TripStatus.inProgress:
        color = AppTheme.primaryColor;
        text = 'نشطة';
        break;
      case TripStatus.completed:
        color = AppTheme.successColor;
        text = 'مكتملة';
        break;
      case TripStatus.cancelled:
        color = AppTheme.errorColor;
        text = 'ملغية';
        break;
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        text,
        style: TextStyle(
          color: color,
          fontSize: 12,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }
}

class _LocationPoint extends StatelessWidget {
  final IconData icon;
  final String title;
  final String subtitle;
  final bool isPickup;

  const _LocationPoint({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.isPickup,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Icon(
              icon,
              color: isPickup ? AppTheme.primaryColor : AppTheme.successColor,
              size: 16,
            ),
            const SizedBox(width: 4),
            Text(
              isPickup ? 'الاستلام' : 'التسليم',
              style: TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 10,
              ),
            ),
          ],
        ),
        const SizedBox(height: 4),
        Text(
          title,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 13,
            fontWeight: FontWeight.w600,
          ),
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
        Text(
          subtitle,
          style: TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 11,
          ),
        ),
      ],
    );
  }
}
