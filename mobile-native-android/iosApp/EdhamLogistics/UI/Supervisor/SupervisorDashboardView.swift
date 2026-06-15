import SwiftUI

struct SupervisorDashboardView: View {
    @StateObject private var viewModel = SupervisorViewModel()

    var body: some View {
        NavigationView {
            ZStack {
                AppColors.cockpitBlack.ignoresSafeArea()

                ScrollView {
                    VStack(spacing: 20) {
                        headerSection

                        // Summary Cards
                        HStack(spacing: 12) {
                            summaryCard(title: "الأسطول النشط", value: "\(viewModel.data?.activeFleetCount ?? 0)", color: AppColors.cockpitGreen)
                            summaryCard(title: "شحنات معلقة", value: "\(viewModel.data?.pendingShipments ?? 0)", color: AppColors.cockpitBlue)
                            summaryCard(title: "تنبيهات", value: "\(viewModel.data?.totalAlerts ?? 0)", color: AppColors.edRust)
                        }

                        fleetListSection

                        Spacer()
                    }
                    .padding()
                }
            }
            .navigationTitle("لوحة إشراف إدهام")
            .navigationBarTitleDisplayMode(.inline)
        }
    }

    var headerSection: some View {
        HStack {
            Text("حالة العمليات المباشرة")
                .font(.headline)
                .foregroundColor(.white)
            Spacer()
            Circle().fill(Color.green).frame(width: 8, height: 8)
            Text("متصل").font(.caption).foregroundColor(.green)
        }
    }

    func summaryCard(title: String, value: String, color: Color) -> some View {
        VStack {
            Text(value).font(.title2).bold().foregroundColor(color)
            Text(title).font(.system(size: 10)).foregroundColor(.gray)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(AppColors.cockpitBlack2)
        .cornerRadius(12)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.border, lineWidth: 1))
    }

    var fleetListSection: some View {
        VStack(alignment: .leading, spacing: 15) {
            Text("مراقبة الأسطول").font(.headline).foregroundColor(.white)

            if let members = viewModel.data?.fleetStatus {
                ForEach(members) { member in
                    fleetRow(member)
                }
            }
        }
    }

    func fleetRow(_ member: FleetMember) -> some View {
        HStack {
            VStack(alignment: .leading) {
                Text(member.driverName).font(.subheadline).bold().foregroundColor(.white)
                Text("شاحنة: \(member.truckId)").font(.caption2).foregroundColor(.gray)
            }
            Spacer()
            VStack(alignment: .trailing) {
                Text("\(String(format: "%.1f", member.currentTemp))°C")
                    .foregroundColor(member.currentTemp > -15 ? .red : AppColors.cockpitGreen)
                    .bold()
                Text(member.status).font(.system(size: 10)).foregroundColor(AppColors.cockpitBlue)
            }
        }
        .padding()
        .background(AppColors.cockpitCard)
        .cornerRadius(12)
    }
}

class SupervisorViewModel: ObservableObject {
    @Published var data: SupervisorDashboardData?
    private let apiService = APIService.shared

    init() { fetch() }
    func fetch() {
        Task {
            if let result = try? await apiService.fetchSupervisorDashboard() {
                DispatchQueue.main.async { self.data = result }
            }
        }
    }
}
