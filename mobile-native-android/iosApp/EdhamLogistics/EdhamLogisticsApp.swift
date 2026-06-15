import SwiftUI

@main
struct EdhamLogisticsApp: App {
    @StateObject private var sessionManager = SessionManager.shared

    var body: some Scene {
        WindowGroup {
            if sessionManager.isLoggedIn {
                switch sessionManager.getRole() {
                case "CUSTOMER":
                    CustomerDashboardView()
                case "SUPERVISOR":
                    SupervisorDashboardView()
                case "ACCOUNTANT":
                    AccountantDashboardView()
                default:
                    DriverDashboardView()
                }
            } else {
                LoginView()
            }
        }
        .preferredColorScheme(.dark)
    }
}
