import Foundation
import Alamofire

class APIService {
    static let shared = APIService()
    private let baseURL = "http://localhost:8080/api/v1"

    private init() {}

    private var headers: HTTPHeaders {
        var h: HTTPHeaders = []
        if let token = SessionManager.shared.getToken() {
            h.add(name: "Authorization", value: "Bearer \(token)")
        }
        return h
    }

    // Auth
    func login(credentials: LoginRequest) async throws -> LoginResponse {
        let url = "\(baseURL)/auth/login"
        return try await AF.request(url, method: .post, parameters: credentials, encoder: JSONParameterEncoder.default)
            .serializingDecodable(LoginResponse.self)
            .value
    }

    // Driver
    func fetchDriverDashboard() async throws -> DriverDashboardData {
        let url = "\(baseURL)/driver/dashboard"
        return try await AF.request(url, headers: headers).serializingDecodable(DriverDashboardData.self).value
    }

    func startMission(shipmentId: Int) async throws -> Bool {
        let url = "\(baseURL)/shipments/\(shipmentId)/start"
        return try await AF.request(url, method: .post, headers: headers).serializingData().result.isSuccess
    }

    func triggerSOS() async throws -> Bool {
        let url = "\(baseURL)/driver/sos"
        return try await AF.request(url, method: .post, headers: headers).serializingData().result.isSuccess
    }

    // Customer
    func fetchCustomerDashboard() async throws -> CustomerDashboardData {
        let url = "\(baseURL)/customer/dashboard"
        return try await AF.request(url, headers: headers).serializingDecodable(CustomerDashboardData.self).value
    }

    func requestShipment(details: ShipmentRequest) async throws -> Bool {
        let url = "\(baseURL)/customer/shipments/request"
        return try await AF.request(url, method: .post, parameters: details, encoder: JSONParameterEncoder.default, headers: headers).serializingData().result.isSuccess
    }

    // Supervisor
    func fetchSupervisorDashboard() async throws -> SupervisorDashboardData {
        let url = "\(baseURL)/supervisor/dashboard"
        return try await AF.request(url, headers: headers).serializingDecodable(SupervisorDashboardData.self).value
    }
}

// Models
struct LoginRequest: Encodable { let username, password: String }
struct LoginResponse: Decodable { let token, role, name: String }

struct DriverDashboardData: Codable {
    let driverName, driverRank: String
    let isOnline: Bool
    let currentTemperature: Double
    let performanceScore: Int
    let todayEarnings, dailyGoal: Double
    let goalPercentage: Int
    let activeMission: Mission?
}

struct Mission: Codable {
    let id: Int
    let pickupName, pickupAddress, deliveryName, deliveryAddress, status: String
}

struct CustomerDashboardData: Codable {
    let customerName: String
    let totalShipments, activeShipmentsCount: Int
    let recentShipments: [CustomerShipment]
}

struct CustomerShipment: Codable, Identifiable {
    let id: Int
    let trackingNumber, status, destination, updatedAt: String
}

struct ShipmentRequest: Encodable {
    let pickupLocation, deliveryLocation, cargoType: String
    let weight: Double
}

struct SupervisorDashboardData: Codable {
    let activeFleetCount, pendingShipments, totalAlerts: Int
    let fleetStatus: [FleetMember]
}

struct FleetMember: Codable, Identifiable {
    let id: Int
    let driverName, truckId, lastKnownLocation, status: String
    let currentTemp: Double
}
