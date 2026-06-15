// ============================================
// 📦 Shipment Model - Swift
// ============================================

import Foundation

enum ShipmentStatus: String, Codable {
    case pending = "pending"
    case confirmed = "confirmed"
    case assigned = "assigned"
    case inTransit = "in_transit"
    case pickedUp = "picked_up"
    case onTheWay = "on_the_way"
    case atDelivery = "at_delivery"
    case delivered = "delivered"
    case completed = "completed"
    case cancelled = "cancelled"
    
    var displayName: String {
        switch self {
        case .pending: return "قيد الانتظار"
        case .confirmed: return "مؤكد"
        case .assigned: return "معين"
        case .inTransit: return "في الطريق"
        case .pickedUp: return "تم الاستلام"
        case .onTheWay: return "في الطريق للتسليم"
        case .atDelivery: return "عند نقطة التسليم"
        case .delivered: return "تم التسليم"
        case .completed: return "مكتمل"
        case .cancelled: return "ملغي"
        }
    }
    
    var color: String {
        switch self {
        case .pending: return "F59E0B"
        case .confirmed, .assigned: return "3B82F6"
        case .inTransit, .pickedUp, .onTheWay, .atDelivery: return "3B82F6"
        case .delivered, .completed: return "22C55E"
        case .cancelled: return "EF4444"
        }
    }
}

enum CargoType: String, Codable {
    case general = "general"
    case frozen = "frozen"
    case chilled = "chilled"
    case dryIce = "dry_ice"
    case pharmaceutical = "pharmaceutical"
    case flowers = "flowers"
    case food = "food"
    
    var displayName: String {
        switch self {
        case .general: return "عام"
        case .frozen: return "مجمد"
        case .chilled: return "مبرد"
        case .dryIce: return "جليد جاف"
        case .pharmaceutical: return "أدوية"
        case .flowers: return "زهور"
        case .food: return "مواد غذائية"
        }
    }
}

struct Shipment: Codable, Identifiable {
    let id: String
    let trackingNumber: String
    let cargo: Cargo
    let pickup: Pickup
    let delivery: Delivery
    let status: ShipmentStatus
    let createdBy: String
    let assignedDriver: String?
    let assignedTruck: String?
    let pricing: Pricing?
    let createdAt: Date?
    let updatedAt: Date?
    
    var isActive: Bool {
        [.pending, .confirmed, .assigned, .inTransit, .pickedUp, .onTheWay, .atDelivery].contains(status)
    }
    
    var isCompleted: Bool {
        status == .delivered || status == .completed
    }
    
    enum CodingKeys: String, CodingKey {
        case id = "_id"
        case trackingNumber, cargo, pickup, delivery, status, createdBy
        case assignedDriver, assignedTruck, pricing, createdAt, updatedAt
    }
}

struct Cargo: Codable {
    let type: CargoType
    let description: String?
    let weight: Weight?
    let temperature: Temperature?
    let specialInstructions: String?
}

struct Weight: Codable {
    let value: Double
    let unit: String
    
    var display: String { "\(value) \(unit)" }
}

struct Temperature: Codable {
    let min: Double
    let max: Double
    let critical: Bool
    
    var display: String { "\(min)° - \(max)°" }
}

struct Pickup: Codable {
    let address: Address
    let scheduledDate: Date?
    let actualDate: Date?
    let timeWindow: TimeWindow?
}

struct Delivery: Codable {
    let address: Address
    let scheduledDate: Date?
    let actualDate: Date?
    let timeWindow: TimeWindow?
    let contactName: String?
    let contactPhone: String?
    let instructions: String?
}

struct TimeWindow: Codable {
    let start: String
    let end: String
}

struct Pricing: Codable {
    let basePrice: Double
    let weightPrice: Double?
    let distancePrice: Double?
    let fuelSurcharge: Double?
    let tax: Double?
    let total: Double
}
