// ============================================
// 🌐 API Service - Swift
// ============================================

import Foundation
import Combine

enum APIError: Error {
    case invalidURL
    case invalidResponse
    case decodingError
    case serverError(String)
    case unauthorized
    case networkError(Error)
}

class APIService {
    static let shared = APIService()
    
    private let baseURL = "http://localhost:5000/api/v1"
    private var authToken: String?
    
    private init() {}
    
    func setAuthToken(_ token: String) {
        self.authToken = token
    }
    
    func clearAuthToken() {
        self.authToken = nil
    }
    
    // MARK: - Generic Request
    func request<T: Decodable>(
        endpoint: String,
        method: String = "GET",
        body: Encodable? = nil
    ) async throws -> T {
        guard let url = URL(string: "\(baseURL)\(endpoint)") else {
            throw APIError.invalidURL
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = method
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        if let token = authToken {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        if let body = body {
            request.httpBody = try JSONEncoder().encode(body)
        }
        
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        switch httpResponse.statusCode {
        case 200...299:
            do {
                return try JSONDecoder().decode(T.self, from: data)
            } catch {
                throw APIError.decodingError
            }
        case 401:
            throw APIError.unauthorized
        default:
            let message = String(data: data, encoding: .utf8) ?? "Unknown error"
            throw APIError.serverError(message)
        }
    }
    
    // MARK: - Auth Endpoints
    func login(email: String, password: String) async throws -> LoginResponse {
        let body = LoginRequest(email: email, password: password)
        let response: LoginResponse = try await request(
            endpoint: "/auth/login",
            method: "POST",
            body: body
        )
        setAuthToken(response.token)
        return response
    }
    
    func register(request: RegisterRequest) async throws -> LoginResponse {
        let response: LoginResponse = try await request(
            endpoint: "/auth/register",
            method: "POST",
            body: request
        )
        setAuthToken(response.token)
        return response
    }
    
    func getCurrentUser() async throws -> User {
        return try await request(endpoint: "/auth/me")
    }
    
    // MARK: - Shipment Endpoints
    func getShipments(status: String? = nil) async throws -> [Shipment] {
        var endpoint = "/shipments"
        if let status = status {
            endpoint += "?status=\(status)"
        }
        return try await request(endpoint: endpoint)
    }
    
    func getShipment(id: String) async throws -> Shipment {
        return try await request(endpoint: "/shipments/\(id)")
    }
    
    func trackShipment(trackingNumber: String) async throws -> Shipment {
        return try await request(endpoint: "/shipments/tracking/\(trackingNumber)")
    }
    
    func createShipment(shipment: CreateShipmentRequest) async throws -> Shipment {
        return try await request(
            endpoint: "/shipments",
            method: "POST",
            body: shipment
        )
    }
    
    func cancelShipment(id: String) async throws -> Shipment {
        return try await request(
            endpoint: "/shipments/\(id)/cancel",
            method: "PUT"
        )
    }
}

// MARK: - Request Models
struct CreateShipmentRequest: Codable {
    let cargo: Cargo
    let pickup: Pickup
    let delivery: Delivery
}
