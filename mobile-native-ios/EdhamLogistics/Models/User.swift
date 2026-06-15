// ============================================
// 👤 User Model - Swift
// ============================================

import Foundation

enum UserRole: String, Codable {
    case admin = "admin"
    case supervisor = "supervisor"
    case accountant = "accountant"
    case driver = "driver"
    case client = "client"
    case employee = "employee"
    
    var displayName: String {
        switch self {
        case .admin: return "مدير"
        case .supervisor: return "مشرف"
        case .accountant: return "محاسب"
        case .driver: return "سائق"
        case .client: return "عميل"
        case .employee: return "موظف"
        }
    }
}

enum UserStatus: String, Codable {
    case active = "active"
    case inactive = "inactive"
    case suspended = "suspended"
}

struct User: Codable, Identifiable {
    let id: String
    let firstName: String
    let lastName: String
    let email: String
    let phone: String?
    let role: UserRole
    let avatar: String?
    let status: UserStatus
    let company: Company?
    let lastLogin: Date?
    let createdAt: Date?
    
    var fullName: String { "\(firstName) \(lastName)" }
    var initials: String { "\(firstName.prefix(1))\(lastName.prefix(1))" }
    
    enum CodingKeys: String, CodingKey {
        case id = "_id"
        case firstName, lastName, email, phone, role, avatar, status, company, lastLogin, createdAt
    }
}

struct Company: Codable {
    let name: String
    let crNumber: String?
    let taxNumber: String?
    let address: Address?
}

struct Address: Codable {
    let street: String?
    let city: String?
    let region: String?
    let zipCode: String?
    let country: String?
    let coordinates: Coordinates?
    
    var formattedAddress: String {
        var parts: [String] = []
        if let street = street { parts.append(street) }
        if let city = city { parts.append(city) }
        if let region = region { parts.append(region) }
        return parts.joined(separator: ", ")
    }
}

struct Coordinates: Codable {
    let latitude: Double
    let longitude: Double
}

// MARK: - Auth Models
struct LoginRequest: Codable {
    let email: String
    let password: String
}

struct LoginResponse: Codable {
    let success: Bool
    let token: String
    let user: User
}

struct RegisterRequest: Codable {
    let firstName: String
    let lastName: String
    let email: String
    let password: String
    let phone: String?
    let role: UserRole
    let companyName: String?
}
