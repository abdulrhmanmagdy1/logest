import SwiftUI

struct CustomerDashboardView: View {
    @StateObject private var viewModel = CustomerDashboardViewModel()

    var body: some View {
        NavigationView {
            ZStack {
                AppColors.cockpitBlack.ignoresSafeArea()

                if viewModel.isLoading && viewModel.customerData == nil {
                    ProgressView().tint(AppColors.cockpitGreen)
                } else {
                    ScrollView {
                        VStack(spacing: 20) {
                            headerSection

                            statsGrid

                            activeShipmentsHeader

                            if let shipments = viewModel.customerData?.recentShipments {
                                ForEach(shipments) { shipment in
                                    shipmentRow(shipment)
                                }
                            }

                            Spacer()
                        }
                        .padding()
                    }
                    .refreshable {
                        viewModel.fetchDashboard()
                    }
                }
            }
            .navigationBarHidden(true)
        }
    }

    var headerSection: some View {
        HStack {
            VStack(alignment: .leading) {
                Text("مرحباً بك،")
                    .font(.caption)
                    .foregroundColor(.gray)
                Text(viewModel.customerData?.customerName ?? "العميل")
                    .font(.title2)
                    .bold()
                    .foregroundColor(.white)
            }
            Spacer()
            Button(action: { SessionManager.shared.clearSession() }) {
                Image(systemName: "rectangle.portrait.and.arrow.right")
                    .foregroundColor(.red)
            }
        }
        .padding()
        .background(AppColors.cockpitBlack2)
        .cornerRadius(16)
    }

    var statsGrid: some View {
        HStack(spacing: 15) {
            statCard(title: "إجمالي الشحنات", value: "\(viewModel.customerData?.totalShipments ?? 0)", icon: "shippingbox", color: AppColors.cockpitBlue)
            statCard(title: "شحنات نشطة", value: "\(viewModel.customerData?.activeShipmentsCount ?? 0)", icon: "timer", color: AppColors.cockpitGreen)
        }
    }

    func statCard(title: String, value: String, icon: String, color: Color) -> some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                Image(systemName: icon)
                    .foregroundColor(color)
                Spacer()
            }
            Text(value)
                .font(.title)
                .bold()
                .foregroundColor(.white)
            Text(title)
                .font(.caption2)
                .foregroundColor(.gray)
        }
        .padding()
        .frame(maxWidth: .infinity)
        .background(AppColors.cockpitBlack2)
        .cornerRadius(16)
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(AppColors.border, lineWidth: 1))
    }

    var activeShipmentsHeader: some View {
        HStack {
            Text("آخر التحديثات")
                .font(.headline)
                .foregroundColor(.white)
            Spacer()
        }
        .padding(.top, 10)
    }

    func shipmentRow(_ shipment: CustomerShipment) -> some View {
        HStack {
            VStack(alignment: .leading, spacing: 5) {
                Text(shipment.trackingNumber)
                    .font(.system(size: 14, weight: .bold, design: .monospaced))
                    .foregroundColor(AppColors.cockpitBlue)
                Text(shipment.destination)
                    .font(.caption)
                    .foregroundColor(.white)
            }
            Spacer()
            VStack(alignment: .trailing, spacing: 5) {
                Text(shipment.status)
                    .font(.caption2)
                    .bold()
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(AppColors.greenGlow)
                    .foregroundColor(AppColors.cockpitGreen)
                    .cornerRadius(8)
                Text(shipment.updatedAt)
                    .font(.system(size: 9))
                    .foregroundColor(.gray)
            }
        }
        .padding()
        .background(AppColors.cockpitCard)
        .cornerRadius(12)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.border, lineWidth: 1))
    }
}
