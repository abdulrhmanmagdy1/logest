//
//  AuthManager.swift
//  Edham Logistics
//

import Foundation
import Combine
import KeychainSwift

@MainActor
class AuthManager: ObservableObject {
    static let shared = AuthManager()
    
    @Published var isAuthenticated = false
    @Published var currentUser: User?
    @Published var isLoading = false
    @Published var error: AuthError?
    
    private let keychain = KeychainSwift()
    private let baseURL = "https://api.edham-logistics.com/api/v1"
    
    private init() {
        checkAuthStatus()
    }
    
    // MARK: - Authentication
    
    func login(email: String, password: String) async {
        isLoading = true
        error = nil
        
        do {
            let body: [String: Any] = [
                "email": email,
                "password": password
            ]
            
            let response: AuthResponse = try await APIClient.shared.post(
                endpoint: "/auth/login",
                body: body
            )
            
            // Save token
            keychain.set(response.token, forKey: "auth_token")
            keychain.set(response.refreshToken, forKey: "refresh_token")
            
            // Update state
            currentUser = response.user
            isAuthenticated = true
            
            // Start location tracking if driver
            if response.user.role == .driver {
                LocationManager.shared.startTracking()
            }
            
        } catch {
            self.error = .invalidCredentials
        }
        
        isLoading = false
    }
    
    func register(email: String, password: String, firstName: String, lastName: String, phone: String, role: UserRole) async {
        isLoading = true
        error = nil
        
        do {
            let body: [String: Any] = [
                "email": email,
                "password": password,
                "firstName": firstName,
                "lastName": lastName,
                "phone": phone,
                "role": role.rawValue
            ]
            
            let response: AuthResponse = try await APIClient.shared.post(
                endpoint: "/auth/register",
                body: body
            )
            
            keychain.set(response.token, forKey: "auth_token")
            keychain.set(response.refreshToken, forKey: "refresh_token")
            
            currentUser = response.user
            isAuthenticated = true
            
        } catch {
            self.error = .registrationFailed
        }
        
        isLoading = false
    }
    
    func logout() {
        keychain.delete("auth_token")
        keychain.delete("refresh_token")
        
        currentUser = nil
        isAuthenticated = false
        
        LocationManager.shared.stopTracking()
    }
    
    func checkAuthStatus() {
        if let token = keychain.get("auth_token") {
            Task {
                await validateToken(token)
            }
        }
    }
    
    private func validateToken(_ token: String) async {
        do {
            let user: User = try await APIClient.shared.get(
                endpoint: "/auth/me",
                headers: ["Authorization": "Bearer \(token)"]
            )
            
            currentUser = user
            isAuthenticated = true
            
            if user.role == .driver {
                LocationManager.shared.startTracking()
            }
            
        } catch {
            logout()
        }
    }
    
    func updateFCMToken(_ token: String?) async {
        guard let token = token, let authToken = keychain.get("auth_token") else { return }
        
        do {
            let _: EmptyResponse = try await APIClient.shared.post(
                endpoint: "/auth/fcm-token",
                body: ["token": token],
                headers: ["Authorization": "Bearer \(authToken)"]
            )
        } catch {
            print("Failed to update FCM token: \(error)")
        }
    }
}

// MARK: - Models

struct AuthResponse: Codable {
    let success: Bool
    let token: String
    let refreshToken: String
    let user: User
}

struct User: Codable, Identifiable {
    let id: String
    let email: String
    let firstName: String
    let lastName: String
    let phone: String
    let role: UserRole
    let avatar: String?
    let status: UserStatus
    let company: CompanyInfo?
    
    var fullName: String {
        "\(firstName) \(lastName)"
    }
}

enum UserRole: String, Codable, CaseIterable {
    case client = "client"
    case driver = "driver"
    case accountant = "accountant"
    case supervisor = "supervisor"
    case workshop = "workshop"
    case admin = "admin"
    
    var displayName: String {
        switch self {
        case .client: return "عميل"
        case .driver: return "سائق"
        case .accountant: return "محاسب"
        case .supervisor: return "مشرف"
        case .workshop: return "ورشة"
        case .admin: return "مدير"
        }
    }
    
    var icon: String {
        switch self {
        case .client: return "person.fill"
        case .driver: return "truck.box.fill"
        case .accountant: return "dollarsign.circle.fill"
        case .supervisor: return "eye.fill"
        case .workshop: return "wrench.fill"
        case .admin: return "crown.fill"
        }
    }
}

enum UserStatus: String, Codable {
    case active = "active"
    case inactive = "inactive"
    case suspended = "suspended"
}

struct CompanyInfo: Codable {
    let name: String
    let registrationNumber: String?
}

enum AuthError: Error, LocalizedError {
    case invalidCredentials
    case registrationFailed
    case networkError
    case serverError
    
    var errorDescription: String? {
        switch self {
        case .invalidCredentials:
            return "البريد الإلكتروني أو كلمة المرور غير صحيحة"
        case .registrationFailed:
            return "فشل إنشاء الحساب. يرجى المحاولة مرة أخرى"
        case .networkError:
            return "خطأ في الاتصال بالشبكة"
        case .serverError:
            return "خطأ في الخادم"
        }
    }
}

struct EmptyResponse: Codable {}
