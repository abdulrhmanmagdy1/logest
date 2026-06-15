import Foundation
import Combine

class DriverDashboardViewModel: ObservableObject {
    @Published var driverData: DriverDashboardData?
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var isOnline = true

    private var cancellables = Set<AnyCancellable>()
    private let apiService = APIService.shared

    init() {
        fetchDashboard()
    }

    func fetchDashboard() {
        isLoading = true
        Task {
            do {
                let data = try await apiService.fetchDriverDashboard()
                DispatchQueue.main.async {
                    self.driverData = data
                    self.isOnline = data.isOnline
                    self.isLoading = false
                }
            } catch {
                DispatchQueue.main.async {
                    self.errorMessage = "حدث خطأ أثناء تحميل البيانات"
                    self.isLoading = false
                }
            }
        }
    }

    func toggleOnlineStatus() {
        // Optimistic UI update
        isOnline.toggle()
        // Here we would call API to update status
    }

    func startMission() {
        guard let missionId = driverData?.activeMission?.id else { return }
        Task {
            do {
                let success = try await apiService.startMission(shipmentId: missionId)
                if success {
                    // Update UI locally or re-fetch
                }
            } catch {
                print("Error starting mission")
            }
        }
    }
}
