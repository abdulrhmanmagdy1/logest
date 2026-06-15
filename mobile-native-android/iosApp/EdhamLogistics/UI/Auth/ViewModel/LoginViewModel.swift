import Foundation
import Combine

class LoginViewModel: ObservableObject {
    @Published var username = ""
    @Published var password = ""
    @Published var isLoading = false
    @Published var errorMessage: String?

    private let apiService = APIService.shared
    private let sessionManager = SessionManager.shared

    func login() {
        guard !username.isEmpty && !password.isEmpty else {
            errorMessage = "الرجاء إدخال اسم المستخدم وكلمة المرور"
            return
        }

        isLoading = true
        errorMessage = nil

        Task {
            do {
                let credentials = LoginRequest(username: username, password: password)
                let response = try await apiService.login(credentials: credentials)

                DispatchQueue.main.async {
                    self.sessionManager.saveSession(token: response.token, role: response.role)
                    self.isLoading = false
                }
            } catch {
                DispatchQueue.main.async {
                    self.errorMessage = "اسم المستخدم أو كلمة المرور غير صحيحة"
                    self.isLoading = false
                }
            }
        }
    }
}
