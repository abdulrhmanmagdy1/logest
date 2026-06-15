import Foundation
import Alamofire

class APIService {
    static let shared = APIService()
    private let baseURL = "http://localhost:8080/api/v1" // For local testing

    private init() {}

    func fetchDriverDashboard() async throws -> DriverDashboardData {
        let url = "\(baseURL)/driver/dashboard"
        return try await AF.request(url)
            .serializingDecodable(DriverDashboardData.self)
            .value
    }

    func startMission(shipmentId: Int) async throws -> Bool {
        let url = "\(baseURL)/shipments/\(shipmentId)/start"
        let response = try await AF.request(url, method: .post).serializingData().result
        return response.isSuccess
    }
}

struct DriverDashboardData: Codable {
    let driverName: String
    let driverRank: String
    let isOnline: Bool
    let currentTemperature: Double
    let performanceScore: Int
    let todayEarnings: Double
    let dailyGoal: Double
    let goalPercentage: Int
    let activeMission: Mission?
}

struct Mission: Codable {
    let id: Int
    let pickupName: String
    let pickupAddress: String
    let deliveryName: String
    let deliveryAddress: String
    let status: String
}
