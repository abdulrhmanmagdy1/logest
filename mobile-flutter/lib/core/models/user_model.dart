// ============================================
// 👤 User Model - Domain Entity
// ============================================

import 'package:equatable/equatable.dart';

enum UserRole { admin, supervisor, accountant, driver, client, employee }

enum UserStatus { active, inactive, suspended }

class User extends Equatable {
  final String id;
  final String firstName;
  final String lastName;
  final String email;
  final String? phone;
  final UserRole role;
  final String? avatar;
  final UserStatus status;
  final Company? company;
  final DateTime? lastLogin;
  final DateTime? createdAt;

  const User({
    required this.id,
    required this.firstName,
    required this.lastName,
    required this.email,
    this.phone,
    required this.role,
    this.avatar,
    required this.status,
    this.company,
    this.lastLogin,
    this.createdAt,
  });

  String get fullName => '$firstName $lastName';
  String get initials => '${firstName[0]}${lastName[0]}';

  String get roleLabel {
    switch (role) {
      case UserRole.admin:
        return 'مدير';
      case UserRole.supervisor:
        return 'مشرف';
      case UserRole.accountant:
        return 'محاسب';
      case UserRole.driver:
        return 'سائق';
      case UserRole.client:
        return 'عميل';
      case UserRole.employee:
        return 'موظف';
    }
  }

  User copyWith({
    String? id,
    String? firstName,
    String? lastName,
    String? email,
    String? phone,
    UserRole? role,
    String? avatar,
    UserStatus? status,
    Company? company,
    DateTime? lastLogin,
    DateTime? createdAt,
  }) {
    return User(
      id: id ?? this.id,
      firstName: firstName ?? this.firstName,
      lastName: lastName ?? this.lastName,
      email: email ?? this.email,
      phone: phone ?? this.phone,
      role: role ?? this.role,
      avatar: avatar ?? this.avatar,
      status: status ?? this.status,
      company: company ?? this.company,
      lastLogin: lastLogin ?? this.lastLogin,
      createdAt: createdAt ?? this.createdAt,
    );
  }

  @override
  List<Object?> get props => [
    id, firstName, lastName, email, phone, role, 
    avatar, status, company, lastLogin, createdAt
  ];
}

class Company extends Equatable {
  final String name;
  final String? crNumber;
  final String? taxNumber;
  final Address? address;

  const Company({
    required this.name,
    this.crNumber,
    this.taxNumber,
    this.address,
  });

  @override
  List<Object?> get props => [name, crNumber, taxNumber, address];
}

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

class Coordinates extends Equatable {
  final double latitude;
  final double longitude;

  const Coordinates({
    required this.latitude,
    required this.longitude,
  });

  @override
  List<Object> get props => [latitude, longitude];
}
