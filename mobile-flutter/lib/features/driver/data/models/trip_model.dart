// ============================================
// 🚛 Trip Model - نموذج الرحلة
// ============================================

enum TripStatus { pending, inProgress, completed, cancelled }

class TripModel {
  final String id;
  final String trackingNumber;
  final TripStatus status;
  final String pickup;
  final String delivery;
  final String cargoType;
  final String weight;
  final String scheduledTime;
  final String estimatedArrival;
  final String customerName;
  final String customerPhone;
  final double temperature;
  final String distance;

  TripModel({
    required this.id,
    required this.trackingNumber,
    required this.status,
    required this.pickup,
    required this.delivery,
    required this.cargoType,
    required this.weight,
    required this.scheduledTime,
    required this.estimatedArrival,
    required this.customerName,
    required this.customerPhone,
    required this.temperature,
    required this.distance,
  });
}
