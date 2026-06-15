/// User Model
/// Represents a driver or admin user in the system
class UserModel {
  final String id;
  final String name;
  final String email;
  final String phone;
  final String? avatarUrl;
  final String role; // driver, admin, supervisor
  final String status; // active, inactive, suspended
  final DateTime createdAt;
  final DateTime? lastLogin;
  final Map<String, dynamic>? metadata;

  UserModel({
    required this.id,
    required this.name,
    required this.email,
    required this.phone,
    this.avatarUrl,
    required this.role,
    this.status = 'active',
    required this.createdAt,
    this.lastLogin,
    this.metadata,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      email: json['email'] ?? '',
      phone: json['phone'] ?? '',
      avatarUrl: json['avatarUrl'],
      role: json['role'] ?? 'driver',
      status: json['status'] ?? 'active',
      createdAt: DateTime.parse(json['createdAt'] ?? DateTime.now().toIso8601String()),
      lastLogin: json['lastLogin'] != null ? DateTime.parse(json['lastLogin']) : null,
      metadata: json['metadata'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'email': email,
      'phone': phone,
      'avatarUrl': avatarUrl,
      'role': role,
      'status': status,
      'createdAt': createdAt.toIso8601String(),
      'lastLogin': lastLogin?.toIso8601String(),
      'metadata': metadata,
    };
  }
}
