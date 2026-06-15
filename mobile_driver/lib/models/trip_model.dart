/// Trip Model
/// Represents a delivery trip/shipment
class TripModel {
  final String id;
  final String trackingNumber;
  final String status;
  final String? driverId;
  final String? driverName;
  final String origin;
  final String destination;
  final double? distance;
  final double? estimatedTime;
  final DateTime? pickupTime;
  final DateTime? deliveryTime;
  final String? notes;
  final List<String>? photos;
  final double? rating;
  final String? feedback;
  final Map<String, dynamic>? metadata;

  TripModel({
    required this.id,
    required this.trackingNumber,
    required this.status,
    this.driverId,
    this.driverName,
    required this.origin,
    required this.destination,
    this.distance,
    this.estimatedTime,
    this.pickupTime,
    this.deliveryTime,
    this.notes,
    this.photos,
    this.rating,
    this.feedback,
    this.metadata,
  });

  factory TripModel.fromJson(Map<String, dynamic> json) {
    return TripModel(
      id: json['id'] ?? '',
      trackingNumber: json['trackingNumber'] ?? '',
      status: json['status'] ?? 'pending',
      driverId: json['driverId'],
      driverName: json['driverName'],
      origin: json['origin'] ?? '',
      destination: json['destination'] ?? '',
      distance: json['distance']?.toDouble(),
      estimatedTime: json['estimatedTime']?.toDouble(),
      pickupTime: json['pickupTime'] != null ? DateTime.parse(json['pickupTime']) : null,
      deliveryTime: json['deliveryTime'] != null ? DateTime.parse(json['deliveryTime']) : null,
      notes: json['notes'],
      photos: json['photos'] != null ? List<String>.from(json['photos']) : null,
      rating: json['rating']?.toDouble(),
      feedback: json['feedback'],
      metadata: json['metadata'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'trackingNumber': trackingNumber,
      'status': status,
      'driverId': driverId,
      'driverName': driverName,
      'origin': origin,
      'destination': destination,
      'distance': distance,
      'estimatedTime': estimatedTime,
      'pickupTime': pickupTime?.toIso8601String(),
      'deliveryTime': deliveryTime?.toIso8601String(),
      'notes': notes,
      'photos': photos,
      'rating': rating,
      'feedback': feedback,
      'metadata': metadata,
    };
  }

  /// Check if trip is active
  bool get isActive => status == 'in_transit' || status == 'pickup';

  /// Check if trip is completed
  bool get isCompleted => status == 'completed' || status == 'delivered';

  /// Check if trip is cancelled
  bool get isCancelled => status == 'cancelled';
}

/// Trip Status Constants
class TripStatuses {
  static const String pending = 'pending';
  static const String accepted = 'accepted';
  static const String driverAssigned = 'driver_assigned';
  static const String pickup = 'pickup';
  static const String inTransit = 'in_transit';
  static const String arrived = 'arrived';
  static const String delivered = 'delivered';
  static const String completed = 'completed';
  static const String cancelled = 'cancelled';
}
