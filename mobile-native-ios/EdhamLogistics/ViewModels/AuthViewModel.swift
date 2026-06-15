// ============================================
// 🔐 Auth ViewModel - Swift
// ============================================

import Foundation
import Combine

@MainActor
class AuthViewModel: ObservableObject {
    @Published var user: User?
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var isAuthenticated = false
    
    private let apiService = APIService.shared
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        checkAuthStatus()
    }
    
    func checkAuthStatus() {
        // TODO: Check Keychain for stored token
        isAuthenticated = false
    }
    
    func login(email: String, password: String) async {
        isLoading = true
        errorMessage = nil
        
        do {
            let response = try await apiService.login(email: email, password: password)
            self.user = response.user
            self.isAuthenticated = true
            // TODO: Save token to Keychain
        } catch APIError.unauthorized {
            errorMessage = "البريد الإلكتروني أو كلمة المرور غير صحيحة"
        } catch {
            errorMessage = "حدث خطأ. يرجى المحاولة مرة أخرى"
        }
        
        isLoading = false
    }
    
    func demoLogin(role: UserRole) async {
        isLoading = true
        
        // Simulate API delay
        try? await Task.sleep(nanoseconds: 1_000_000_000)
        
        switch role {
        case .client:
            user = User(
                id: "1",
                firstName: "عبدالله",
                lastName: "المحمد",
                email: "client@edham.com",
                phone: "0501234567",
                role: .client,
                avatar: nil,
                status: .active,
                company: nil,
                lastLogin: Date(),
                createdAt: Date()
            )
        case .driver:
            user = User(
                id: "2",
                firstName: "خالد",
                lastName: "السائق",
                email: "driver@edham.com",
                phone: "0507654321",
                role: .driver,
                avatar: nil,
                status: .active,
                company: nil,
                lastLogin: Date(),
                createdAt: Date()
            )
        default:
            break
        }
        
        isAuthenticated = true
        isLoading = false
    }
    
    func logout() {
        user = nil
        isAuthenticated = false
        apiService.clearAuthToken()
        // TODO: Clear Keychain
    }
}
