//
//  APIClient.swift
//  Edham Logistics
//

import Foundation

actor APIClient {
    static let shared = APIClient()
    
    private let baseURL = "https://api.edham-logistics.com/api/v1"
    private let session: URLSession
    private let decoder: JSONDecoder
    private let encoder: JSONEncoder
    
    private init() {
        let config = URLSessionConfiguration.default
        config.timeoutIntervalForRequest = 30
        config.timeoutIntervalForResource = 300
        config.httpAdditionalHeaders = [
            "Content-Type": "application/json",
            "Accept": "application/json"
        ]
        
        self.session = URLSession(configuration: config)
        
        self.decoder = JSONDecoder()
        self.decoder.keyDecodingStrategy = .convertFromSnakeCase
        self.decoder.dateDecodingStrategy = .iso8601
        
        self.encoder = JSONEncoder()
        self.encoder.keyEncodingStrategy = .convertToSnakeCase
        self.encoder.dateEncodingStrategy = .iso8601
    }
    
    // MARK: - GET Request
    func get<T: Decodable>(
        endpoint: String,
        headers: [String: String] = [:]
    ) async throws -> T {
        return try await request(
            method: "GET",
            endpoint: endpoint,
            headers: headers
        )
    }
    
    // MARK: - POST Request
    func post<T: Decodable>(
        endpoint: String,
        body: [String: Any]? = nil,
        headers: [String: String] = [:]
    ) async throws -> T {
        return try await request(
            method: "POST",
            endpoint: endpoint,
            body: body,
            headers: headers
        )
    }
    
    // MARK: - PUT Request
    func put<T: Decodable>(
        endpoint: String,
        body: [String: Any]? = nil,
        headers: [String: String] = [:]
    ) async throws -> T {
        return try await request(
            method: "PUT",
            endpoint: endpoint,
            body: body,
            headers: headers
        )
    }
    
    // MARK: - DELETE Request
    func delete<T: Decodable>(
        endpoint: String,
        headers: [String: String] = [:]
    ) async throws -> T {
        return try await request(
            method: "DELETE",
            endpoint: endpoint,
            headers: headers
        )
    }
    
    // MARK: - Upload File
    func upload<T: Decodable>(
        endpoint: String,
        fileData: Data,
        fileName: String,
        mimeType: String,
        headers: [String: String] = [:]
    ) async throws -> T {
        guard let url = URL(string: baseURL + endpoint) else {
            throw APIError.invalidURL
        }
        
        let boundary = UUID().uuidString
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        // Headers
        var allHeaders = headers
        allHeaders["Content-Type"] = "multipart/form-data; boundary=\(boundary)"
        allHeaders.forEach { request.setValue($1, forHTTPHeaderField: $0) }
        
        // Body
        var body = Data()
        
        // File data
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"file\"; filename=\"\(fileName)\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: \(mimeType)\r\n\r\n".data(using: .utf8)!)
        body.append(fileData)
        body.append("\r\n".data(using: .utf8)!)
        body.append("--\(boundary)--\r\n".data(using: .utf8)!)
        
        request.httpBody = body
        
        let (data, response) = try await session.data(for: request)
        return try handleResponse(data: data, response: response)
    }
    
    // MARK: - Base Request
    private func request<T: Decodable>(
        method: String,
        endpoint: String,
        body: [String: Any]? = nil,
        headers: [String: String] = [:]
    ) async throws -> T {
        guard let url = URL(string: baseURL + endpoint) else {
            throw APIError.invalidURL
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = method
        
        // Headers
        headers.forEach { request.setValue($1, forHTTPHeaderField: $0) }
        
        // Add auth token if available
        if let token = KeychainSwift().get("auth_token") {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        // Body
        if let body = body {
            request.httpBody = try JSONSerialization.data(withJSONObject: body)
        }
        
        let (data, response) = try await session.data(for: request)
        return try handleResponse(data: data, response: response)
    }
    
    // MARK: - Response Handler
    private func handleResponse<T: Decodable>(data: Data, response: URLResponse) throws -> T {
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        // Debug
        if let jsonString = String(data: data, encoding: .utf8) {
            print("📡 Response: \(jsonString)")
        }
        
        switch httpResponse.statusCode {
        case 200...299:
            do {
                return try decoder.decode(T.self, from: data)
            } catch {
                print("❌ Decoding error: \(error)")
                throw APIError.decodingError
            }
            
        case 401:
            throw APIError.unauthorized
            
        case 403:
            throw APIError.forbidden
            
        case 404:
            throw APIError.notFound
            
        case 400...499:
            if let apiError = try? decoder.decode(APIErrorResponse.self, from: data) {
                throw APIError.serverError(apiError.message)
            }
            throw APIError.badRequest
            
        case 500...599:
            throw APIError.serverError("Internal server error")
            
        default:
            throw APIError.unknown
        }
    }
}

// MARK: - API Errors

enum APIError: Error, LocalizedError {
    case invalidURL
    case invalidResponse
    case decodingError
    case unauthorized
    case forbidden
    case notFound
    case badRequest
    case serverError(String)
    case unknown
    case noInternet
    
    var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "Invalid URL"
        case .invalidResponse:
            return "Invalid response"
        case .decodingError:
            return "Failed to parse data"
        case .unauthorized:
            return "Session expired. Please login again"
        case .forbidden:
            return "Access denied"
        case .notFound:
            return "Resource not found"
        case .badRequest:
            return "Invalid request"
        case .serverError(let message):
            return message
        case .unknown:
            return "Unknown error"
        case .noInternet:
            return "No internet connection"
        }
    }
}

struct APIErrorResponse: Codable {
    let success: Bool
    let message: String
    let errors: [String]?
}

struct APIResponse<T: Codable>: Codable {
    let success: Bool
    let message: String?
    let data: T
    let meta: MetaData?
}

struct MetaData: Codable {
    let currentPage: Int
    let totalPages: Int
    let totalItems: Int
    let itemsPerPage: Int
}
