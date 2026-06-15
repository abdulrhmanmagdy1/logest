import SwiftUI

struct DriverDashboardView: View {
    @StateObject private var viewModel = DriverDashboardViewModel()

    var body: some View {
        NavigationView {
            ZStack {
                AppColors.cockpitBlack.ignoresSafeArea()

                if viewModel.isLoading && viewModel.driverData == nil {
                    ProgressView()
                        .tint(AppColors.cockpitGreen)
                } else {
                    ScrollView {
                        VStack(spacing: 20) {
                            // Status Header
                            headerSection

                            // Tactical Telemetry & SOS
                            HStack(spacing: 12) {
                                TacticalGaugeView(value: viewModel.driverData?.currentTemperature ?? 0, title: "درجة المبرد")

                                VStack(spacing: 12) {
                                    sosButton
                                    performanceMiniCard
                                }
                            }

                            // Quick Actions
                            HStack(spacing: 15) {
                                NavigationLink(destination: ExpenseReportView()) {
                                    quickActionItem(title: "تسجيل وقود", icon: "fuelpump.fill", color: .orange)
                                }
                                NavigationLink(destination: ExpenseReportView()) {
                                    quickActionItem(title: "بلاغ صيانة", icon: "wrench.and.screwdriver.fill", color: .gray)
                                }
                            }

                            // Earnings Radar
                            earningsCard

                            // Active Mission
                            activeMissionSection

                            Spacer(minLength: 50)
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
        .preferredColorScheme(.dark)
    }

    var headerSection: some View {
        HStack {
            NavigationLink(destination: NotificationsListView()) {
                ZStack {
                    Image(systemName: "bell.fill")
                        .foregroundColor(.white)
                        .padding(10)
                        .background(AppColors.cockpitBlack2)
                        .clipShape(Circle())
                        .overlay(Circle().stroke(AppColors.border, lineWidth: 1))

                    Circle()
                        .fill(Color.red)
                        .frame(width: 8, height: 8)
                        .offset(x: 10, y: -10)
                }
            }

            Spacer()

            VStack(alignment: .trailing) {
                Text(viewModel.driverData?.driverName ?? "جاري التحميل...")
                    .font(.system(size: 16, weight: .bold))
                Text(viewModel.driverData?.driverRank ?? "---")
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(AppColors.cockpitGreen)
            }
            .padding(.trailing, 8)

            Image(systemName: "person.circle.fill")
                .resizable()
                .frame(width: 35, height: 35)
                .foregroundColor(AppColors.cockpitGreen)
        }
        .padding(.horizontal)
        .padding(.top, 10)
    }

    var sosButton: some View {
        Button(action: { /* Trigger SOS */ }) {
            VStack {
                Text("🚨")
                    .font(.title2)
                Text("SOS")
                    .font(.caption)
                    .bold()
                    .foregroundColor(AppColors.edRust)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(AppColors.edRust.opacity(0.15))
            .cornerRadius(20)
            .overlay(RoundedRectangle(cornerRadius: 20).stroke(AppColors.edRust, lineWidth: 2))
        }
    }

    var performanceMiniCard: some View {
        NavigationLink(destination: DriverRewardsView()) {
            VStack {
                Text("\(viewModel.driverData?.performanceScore ?? 0)")
                    .font(.title2)
                    .bold()
                    .foregroundColor(AppColors.cockpitGreen)
                Text("مؤشر الأداء")
                    .font(.system(size: 9))
                    .foregroundColor(.gray)
                Text("عرض التفاصيل")
                    .font(.system(size: 8))
                    .foregroundColor(AppColors.cockpitBlue)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(AppColors.cockpitBlack2)
            .cornerRadius(20)
            .overlay(RoundedRectangle(cornerRadius: 20).stroke(AppColors.border, lineWidth: 1))
        }
    }

    func quickActionItem(title: String, icon: String, color: Color) -> some View {
        HStack {
            Image(systemName: icon).foregroundColor(color)
            Text(title).font(.subheadline).bold().foregroundColor(.white)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(AppColors.cockpitBlack2)
        .cornerRadius(12)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.border, lineWidth: 1))
    }

    var earningsCard: some View {
        VStack(spacing: 12) {
            Text("أرباح اليوم")
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(.gray)
                .kerning(2)

            Text("\(Int(viewModel.driverData?.todayEarnings ?? 0))")
                .font(.system(size: 42, weight: .black, design: .monospaced))
                .foregroundColor(AppColors.cockpitBlue)

            Text("ريال سعودي")
                .font(.caption)
                .foregroundColor(.gray)

            VStack(spacing: 8) {
                HStack {
                    Text("الهدف اليومي")
                        .font(.system(size: 10))
                        .foregroundColor(.gray)
                    Spacer()
                    Text("\(viewModel.driverData?.goalPercentage ?? 0)%")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundColor(AppColors.cockpitGreen)
                }

                ProgressView(value: Double(viewModel.driverData?.goalPercentage ?? 0) / 100.0)
                    .tint(AppColors.cockpitGreen)
                    .background(Color.white.opacity(0.1))
                    .scaleEffect(x: 1, y: 1.5, anchor: .center)
                    .cornerRadius(4)
            }
            .padding(.top, 10)
        }
        .padding(24)
        .background(AppColors.cockpitBlack2)
        .cornerRadius(24)
        .overlay(RoundedRectangle(cornerRadius: 24).stroke(AppColors.blueGlow, lineWidth: 1))
    }

    var activeMissionSection: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("📍 المهمة الحالية")
                .font(.system(size: 16, weight: .bold))

            if let mission = viewModel.driverData?.activeMission {
                NavigationLink(destination: ShipmentDetailView(shipmentId: mission.id)) {
                    VStack(alignment: .leading, spacing: 0) {
                        locationRow(name: mission.pickupName, address: mission.pickupAddress, color: .green)

                        Rectangle()
                            .fill(Color.green.opacity(0.2))
                            .frame(width: 2, height: 25)
                            .padding(.leading, 5)

                        locationRow(name: mission.deliveryName, address: mission.deliveryAddress, color: .red)
                    }
                    .padding()
                    .background(AppColors.cockpitCard)
                    .cornerRadius(20)
                    .overlay(RoundedRectangle(cornerRadius: 20).stroke(AppColors.greenGlow, lineWidth: 1))
                }

                Button(action: { viewModel.startMission() }) {
                    HStack {
                        Image(systemName: "engine.combustion.fill")
                        Text("إشعال المحرك — بدء الرحلة")
                            .bold()
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 60)
                    .background(AppColors.cockpitGreen)
                    .foregroundColor(AppColors.cockpitBlack)
                    .cornerRadius(16)
                    .shadow(color: AppColors.cockpitGreen.opacity(0.3), radius: 10)
                }
            } else {
                Text("لا توجد مهام نشطة حالياً")
                    .foregroundColor(.gray)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding()
            }
        }
    }

    func locationRow(name: String, address: String, color: Color) -> some View {
        HStack(alignment: .top) {
            Circle()
                .fill(color)
                .frame(width: 12, height: 12)
                .padding(.top, 4)

            VStack(alignment: .leading, spacing: 2) {
                Text(name)
                    .font(.system(size: 14, weight: .bold))
                Text(address)
                    .font(.system(size: 11))
                    .foregroundColor(.gray)
            }
            .padding(.leading, 10)
        }
    }
}
