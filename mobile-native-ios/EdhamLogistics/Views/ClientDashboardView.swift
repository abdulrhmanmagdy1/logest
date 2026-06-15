// ============================================
// 🏠 Client Dashboard View - SwiftUI
// ============================================

import SwiftUI

struct ClientDashboardView: View {
    @StateObject private var viewModel = ClientDashboardViewModel()
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            HomeView(viewModel: viewModel)
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("الرئيسية")
                }
                .tag(0)
            
            ShipmentsListView()
                .tabItem {
                    Image(systemName: "shippingbox.fill")
                    Text("الشحنات")
                }
                .tag(1)
            
            TrackingView()
                .tabItem {
                    Image(systemName: "location.fill")
                    Text("التتبع")
                }
                .tag(2)
            
            ProfileView()
                .tabItem {
                    Image(systemName: "person.fill")
                    Text("حسابي")
                }
                .tag(3)
        }
        .accentColor(Color(hex: "2563EB"))
        .onAppear {
            Task {
                await viewModel.loadDashboard()
            }
        }
    }
}

// MARK: - Home View
struct HomeView: View {
    @ObservedObject var viewModel: ClientDashboardViewModel
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(hex: "0A1128").ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 20) {
                        // Header
                        headerView
                        
                        // Stats Cards
                        statsView
                        
                        // Quick Actions
                        quickActionsView
                        
                        // Recent Shipments
                        recentShipmentsView
                    }
                    .padding(.horizontal, 16)
                }
            }
            .navigationBarHidden(true)
        }
    }
    
    private var headerView: some View {
        HStack {
            // Profile
            HStack(spacing: 12) {
                ZStack {
                    Circle()
                        .fill(
                            LinearGradient(
                                colors: [Color(hex: "2563EB"), Color(hex: "1D4ED8")],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        )
                        .frame(width: 50, height: 50)
                    
                    Text("م أ")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.white)
                }
                
                VStack(alignment: .leading, spacing: 4) {
                    Text("مرحباً، محمد")
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(.white)
                    
                    Text("عميل")
                        .font(.system(size: 12))
                        .foregroundColor(Color(hex: "AAB4C8"))
                }
            }
            
            Spacer()
            
            // Notifications
            Button(action: {}) {
                ZStack {
                    Image(systemName: "bell.fill")
                        .font(.system(size: 20))
                        .foregroundColor(.white)
                    
                    Circle()
                        .fill(Color.red)
                        .frame(width: 8, height: 8)
                        .offset(x: 6, y: -6)
                }
            }
        }
        .padding(.top, 16)
    }
    
    private var statsView: some View {
        HStack(spacing: 12) {
            StatCard(
                icon: "shippingbox",
                title: "الشحنات النشطة",
                value: "\(viewModel.activeCount)",
                color: Color(hex: "2563EB")
            )
            
            StatCard(
                icon: "checkmark.circle",
                title: "تم التسليم",
                value: "\(viewModel.completedCount)",
                color: Color(hex: "22C55E")
            )
        }
    }
    
    private var quickActionsView: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("إجراءات سريعة")
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(.white)
            
            HStack(spacing: 12) {
                QuickActionButton(
                    icon: "plus.square",
                    title: "شحنة جديدة",
                    color: Color(hex: "2563EB")
                )
                
                QuickActionButton(
                    icon: "location.viewfinder",
                    title: "تتبع الشحنة",
                    color: Color(hex: "F5C542")
                )
                
                QuickActionButton(
                    icon: "doc.text",
                    title: "الفواتير",
                    color: Color(hex: "22C55E")
                )
            }
        }
    }
    
    private var recentShipmentsView: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Text("آخر الشحنات")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.white)
                
                Spacer()
                
                Button("عرض الكل") {}
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(Color(hex: "2563EB"))
            }
            
            ForEach(viewModel.shipments) { shipment in
                ShipmentCard(shipment: shipment)
            }
        }
    }
}

// MARK: - Stat Card
struct StatCard: View {
    let icon: String
    let title: String
    let value: String
    let color: Color
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 20))
                .foregroundColor(color)
                .frame(width: 40, height: 40)
                .background(color.opacity(0.1))
                .cornerRadius(8)
            
            Text(value)
                .font(.system(size: 24, weight: .bold))
                .foregroundColor(.white)
            
            Text(title)
                .font(.system(size: 12))
                .foregroundColor(Color(hex: "AAB4C8"))
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(Color(hex: "111C3A"))
        .cornerRadius(16)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(color.opacity(0.2), lineWidth: 1)
        )
    }
}

// MARK: - Quick Action Button
struct QuickActionButton: View {
    let icon: String
    let title: String
    let color: Color
    
    var body: some View {
        Button(action: {}) {
            VStack(spacing: 8) {
                Image(systemName: icon)
                    .font(.system(size: 24))
                    .foregroundColor(color)
                
                Text(title)
                    .font(.system(size: 12))
                    .foregroundColor(Color(hex: "AAB4C8"))
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
            .background(Color(hex: "111C3A"))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(color.opacity( 0.2), lineWidth: 1)
            )
        }
    }
}

// MARK: - Shipment Card
struct ShipmentCard: View {
    let shipment: Shipment
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                StatusBadge(status: shipment.status)
                
                Spacer()
                
                Text("#\(shipment.trackingNumber)")
                    .font(.system(size: 12))
                    .foregroundColor(Color(hex: "64748B"))
            }
            
            HStack(spacing: 8) {
                Image(systemName: "mappin.circle")
                    .foregroundColor(Color(hex: "AAB4C8"))
                
                Text("\(shipment.pickup.address.city ?? "") ← \(shipment.delivery.address.city ?? "")")
                    .font(.system(size: 14))
                    .foregroundColor(.white)
            }
            
            HStack(spacing: 8) {
                Image(systemName: "calendar")
                    .foregroundColor(Color(hex: "AAB4C8"))
                
                Text(formatDate(shipment.createdAt))
                    .font(.system(size: 12))
                    .foregroundColor(Color(hex: "AAB4C8"))
            }
        }
        .padding(16)
        .background(Color(hex: "111C3A"))
        .cornerRadius(16)
    }
    
    private func formatDate(_ date: Date?) -> String {
        guard let date = date else { return "-" }
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: date)
    }
}

// MARK: - Status Badge
struct StatusBadge: View {
    let status: ShipmentStatus
    
    var body: some View {
        Text(status.displayName)
            .font(.system(size: 12, weight: .semibold))
            .foregroundColor(Color(hex: status.color))
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(Color(hex: status.color).opacity(0.1))
            .cornerRadius(20)
    }
}

// MARK: - Placeholder Views
struct ShipmentsListView: View {
    var body: some View {
        Text("قائمة الشحنات")
            .foregroundColor(.white)
    }
}

struct TrackingView: View {
    var body: some View {
        Text("تتبع الشحنة")
            .foregroundColor(.white)
    }
}

struct ProfileView: View {
    var body: some View {
        Text("الملف الشخصي")
            .foregroundColor(.white)
    }
}

// MARK: - Preview
struct ClientDashboardView_Previews: PreviewProvider {
    static var previews: some View {
        ClientDashboardView()
            .preferredColorScheme(.dark)
    }
}
