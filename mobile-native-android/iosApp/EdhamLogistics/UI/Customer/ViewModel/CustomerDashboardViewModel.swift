import Foundation
import Combine

class CustomerDashboardViewModel: ObservableObject {
    @Published var customerData: CustomerDashboardData?
    @Published var isLoading = false
    @Published var errorMessage: String?

    private let apiService = APIService.shared

    init() {
        fetchDashboard()
    }

    func fetchDashboard() {
        isLoading = true
        Task {
            do {
                let data = try await apiService.fetchCustomerDashboard()
                DispatchQueue.main.async {
                    self.customerData = data
                    self.isLoading = false
                }
            } catch {
                DispatchQueue.main.async {
                    self.errorMessage = "حدث خطأ أثناء تحميل بيانات العميل"
                    self.isLoading = false
                }
            }
        }
    }
}
