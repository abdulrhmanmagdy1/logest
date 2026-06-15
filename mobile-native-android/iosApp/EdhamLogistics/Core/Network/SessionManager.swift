import Foundation

class SessionManager: ObservableObject {
    static let shared = SessionManager()

    private let tokenKey = "edham_auth_token"
    private let userRoleKey = "edham_user_role"

    @Published var isLoggedIn: Bool = false

    private init() {
        self.isLoggedIn = getToken() != nil
    }

    func saveSession(token: String, role: String) {
        UserDefaults.standard.set(token, forKey: tokenKey)
        UserDefaults.standard.set(role, forKey: userRoleKey)
        DispatchQueue.main.async {
            self.isLoggedIn = true
        }
    }

    func getToken() -> String? {
        return UserDefaults.standard.string(forKey: tokenKey)
    }

    func getRole() -> String? {
        return UserDefaults.standard.string(forKey: userRoleKey)
    }

    func clearSession() {
        UserDefaults.standard.removeObject(forKey: tokenKey)
        UserDefaults.standard.removeObject(forKey: userRoleKey)
        DispatchQueue.main.async {
            self.isLoggedIn = false
        }
    }
}
