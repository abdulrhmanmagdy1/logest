// ============================================
// 📦 Shipment Model - Domain Entity with Serialization
// ============================================

import 'package:equatable/equatable.dart';
import 'package:json_annotation/json_annotation.dart';

part 'shipment_model.g.dart';

enum ShipmentStatus {
  pending,
  confirmed,
  assigned,
  inTransit,
  pickedUp,
  onTheWay,
  atDelivery,
  delivered,
  completed,
  cancelled
}

enum CargoType {
  general,
  frozen,
  chilled,
  dryIce,
  pharmaceutical,
  flowers,
  food
}

@JsonSerializable()
class Shipment extends Equatable {
  final String id;
  final String trackingNumber;
  final Cargo cargo;
  final Pickup pickup;
  final Delivery delivery;
  final ShipmentStatus status;
  final String createdBy;
  final String? assignedDriver;
  final String? assignedTruck;
  final Pricing? pricing;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const Shipment({
    required this.id,
    required this.trackingNumber,
    required this.cargo,
    required this.pickup,
    required this.delivery,
    required this.status,
    required this.createdBy,
    this.assignedDriver,
    this.assignedTruck,
    this.pricing,
    this.createdAt,
    this.updatedAt,
  });

  factory Shipment.fromJson(Map<String, dynamic> json) => _$ShipmentFromJson(json);
  Map<String, dynamic> toJson() => _$ShipmentToJson(this);

  String get statusLabel {
    switch (status) {
      case ShipmentStatus.pending:
        return 'قيد الانتظار';
      case ShipmentStatus.confirmed:
        return 'مؤكد';
      case ShipmentStatus.assigned:
        return 'معين';
      case ShipmentStatus.inTransit:
        return 'في الطريق';
      case ShipmentStatus.pickedUp:
        return 'تم الاستلام';
      case ShipmentStatus.onTheWay:
        return 'في الطريق للتسليم';
      case ShipmentStatus.atDelivery:
        return 'عند نقطة التسليم';
      case ShipmentStatus.delivered:
        return 'تم التسليم';
      case ShipmentStatus.completed:
        return 'مكتمل';
      case ShipmentStatus.cancelled:
        return 'ملغي';
    }
  }

  bool get isActive => [
    ShipmentStatus.pending,
    ShipmentStatus.confirmed,
    ShipmentStatus.assigned,
    ShipmentStatus.inTransit,
    ShipmentStatus.pickedUp,
    ShipmentStatus.onTheWay,
    ShipmentStatus.atDelivery,
  ].contains(status);

  bool get isCompleted => 
    status == ShipmentStatus.delivered || 
    status == ShipmentStatus.completed;

  @override
  List<Object?> get props => [
    id, trackingNumber, cargo, pickup, delivery, status,
    createdBy, assignedDriver, assignedTruck, pricing, createdAt, updatedAt
  ];
}

@JsonSerializable()
class Cargo extends Equatable {
  final CargoType type;
  final String? description;
  final Weight? weight;
  final Temperature? temperature;
  final String? specialInstructions;

  const Cargo({
    required this.type,
    this.description,
    this.weight,
    this.temperature,
    this.specialInstructions,
  });

  factory Cargo.fromJson(Map<String, dynamic> json) => _$CargoFromJson(json);
  Map<String, dynamic> toJson() => _$CargoToJson(this);

  String get typeLabel {
    switch (type) {
      case CargoType.general:
        return 'عام';
      case CargoType.frozen:
        return 'مجمد';
      case CargoType.chilled:
        return 'مبرد';
      case CargoType.dryIce:
        return 'جليد جاف';
      case CargoType.pharmaceutical:
        return 'أدوية';
      case CargoType.flowers:
        return 'زهور';
      case CargoType.food:
        return 'مواد غذائية';
    }
  }

  @override
  List<Object?> get props => [type, description, weight, temperature, specialInstructions];
}

@JsonSerializable()
class Weight extends Equatable {
  final double value;
  final String unit;

  const Weight({
    required this.value,
    required this.unit,
  });

  factory Weight.fromJson(Map<String, dynamic> json) => _$WeightFromJson(json);
  Map<String, dynamic> toJson() => _$WeightToJson(this);

  String get display => '$value $unit';

  @override
  List<Object> get props => [value, unit];
}

@JsonSerializable()
class Temperature extends Equatable {
  final double min;
  final double max;
  final bool critical;

  const Temperature({
    required this.min,
    required this.max,
    this.critical = false,
  });

  factory Temperature.fromJson(Map<String, dynamic> json) => _$TemperatureFromJson(json);
  Map<String, dynamic> toJson() => _$TemperatureToJson(this);

  String get display => '$min° - $max°';

  @override
  List<Object> get props => [min, max, critical];
}

@JsonSerializable()
class Pickup extends Equatable {
  final Address address;
  final DateTime? scheduledDate;
  final DateTime? actualDate;
  final TimeWindow? timeWindow;

  const Pickup({
    required this.address,
    this.scheduledDate,
    this.actualDate,
    this.timeWindow,
  });

  factory Pickup.fromJson(Map<String, dynamic> json) => _$PickupFromJson(json);
  Map<String, dynamic> toJson() => _$PickupToJson(this);

  @override
  List<Object?> get props => [address, scheduledDate, actualDate, timeWindow];
}

@JsonSerializable()
class Delivery extends Equatable {
  final Address address;
  final DateTime? scheduledDate;
  final DateTime? actualDate;
  final TimeWindow? timeWindow;
  final String? contactName;
  final String? contactPhone;
  final String? instructions;

  const Delivery({
    required this.address,
    this.scheduledDate,
    this.actualDate,
    this.timeWindow,
    this.contactName,
    this.contactPhone,
    this.instructions,
  });

  factory Delivery.fromJson(Map<String, dynamic> json) => _$DeliveryFromJson(json);
  Map<String, dynamic> toJson() => _$DeliveryToJson(this);

  @override
  List<Object?> get props => [
    address, scheduledDate, actualDate, timeWindow,
    contactName, contactPhone, instructions
  ];
}

@JsonSerializable()
class Address extends Equatable {
  final String? street;
  final String? city;
  final String? region;
  final String? zipCode;
  final String? country;
  final Coordinates? coordinates;

  const Address({
    this.street,
    this.city,
    this.region,
    this.zipCode,
    this.country,
    this.coordinates,
  });

  factory Address.fromJson(Map<String, dynamic> json) => _$AddressFromJson(json);
  Map<String, dynamic> toJson() => _$AddressToJson(this);

  String get formattedAddress {
    final parts = <String>[];
    if (street != null) parts.add(street!);
    if (city != null) parts.add(city!);
    if (region != null) parts.add(region!);
    return parts.join(', ');
  }

  @override
  List<Object?> get props => [street, city, region, zipCode, country, coordinates];
}

@JsonSerializable()
class Coordinates extends Equatable {
  final double latitude;
  final double longitude;

  const Coordinates({
    required this.latitude,
    required this.longitude,
  });

  factory Coordinates.fromJson(Map<String, dynamic> json) => _$CoordinatesFromJson(json);
  Map<String, dynamic> toJson() => _$CoordinatesToJson(this);

  @override
  List<Object> get props => [latitude, longitude];
}

@JsonSerializable()
class TimeWindow extends Equatable {
  final String start;
  final String end;

  const TimeWindow({
    required this.start,
    required this.end,
  });

  factory TimeWindow.fromJson(Map<String, dynamic> json) => _$TimeWindowFromJson(json);
  Map<String, dynamic> toJson() => _$TimeWindowToJson(this);

  @override
  List<Object> get props => [start, end];
}

@JsonSerializable()
class Pricing extends Equatable {
  final double basePrice;
  final double? weightPrice;
  final double? distancePrice;
  final double? fuelSurcharge;
  final double? tax;
  final double total;

  const Pricing({
    required this.basePrice,
    this.weightPrice,
    this.distancePrice,
    this.fuelSurcharge,
    this.tax,
    required this.total,
  });

  factory Pricing.fromJson(Map<String, dynamic> json) => _$PricingFromJson(json);
  Map<String, dynamic> toJson() => _$PricingToJson(this);

  @override
  List<Object?> get props => [
    basePrice, weightPrice, distancePrice, fuelSurcharge, tax, total
  ];
}
