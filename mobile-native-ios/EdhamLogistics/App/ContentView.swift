//
//  ContentView.swift
//  EdhamLogistics
//
//  Created by Edham Logistics Team on 2026.
//  Copyright © 2026 Edham Logistics. All rights reserved.
//

import SwiftUI
import Stripe

struct ContentView: View {
    @StateObject private var authViewModel = AuthViewModel()
    @StateObject private var apiService = APIService.shared
    
    var body: some View {
        Group {
            if authViewModel.isAuthenticated {
                MainTabView()
                    .environmentObject(authViewModel)
                    .environmentObject(apiService)
            } else {
                LoginView()
                    .environmentObject(authViewModel)
            }
        }
        .onAppear {
            // Initialize Stripe
            StripeAPI.defaultPublishableKey = "pk_test_your_publishable_key_here"
        }
    }
}

struct MainTabView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            // Dynamic tabs based on user role
            ForEach(authViewModel.roleConfig.navigation, id: \.id) { navItem in
                NavigationView {
                    getViewForTab(navItem.id)
                        .navigationTitle(navItem.label)
                        .navigationBarTitleDisplayMode(.large)
                }
                .tabItem {
                    Label(navItem.label, systemImage: getIconForTab(navItem.icon))
                }
                .tag(getTabIndex(navItem.id))
            }
        }
        .accentColor(Color(hex: authViewModel.roleConfig.primaryColor))
    }
    
    @ViewBuilder
    private func getViewForTab(_ tabId: String) -> some View {
        switch authViewModel.userRole {
        case .CUSTOMER:
            CustomerView(tabId: tabId)
        case .DRIVER:
            DriverView(tabId: tabId)
        case .SUPERVISOR:
            SupervisorView(tabId: tabId)
        case .ACCOUNTANT:
            AccountantView(tabId: tabId)
        case .WORKSHOP:
            WorkshopView(tabId: tabId)
        case .ADMIN:
            AdminView(tabId: tabId)
        }
    }
    
    private func getIconForTab(_ iconName: String) -> String {
        switch iconName {
        case "home": return "house.fill"
        case "package": return "box.truck.fill"
        case "map-pin": return "location.fill"
        case "clock": return "clock.fill"
        case "user": return "person.fill"
        case "navigation": return "location.north.fill"
        case "dollar-sign": return "dollarsign.circle.fill"
        case "truck": return "truck.fill"
        case "users": return "person.3.fill"
        case "bar-chart": return "chart.bar.fill"
        case "file-text": return "doc.text.fill"
        case "credit-card": return "creditcard.fill"
        case "wrench": return "wrench.fill"
        case "alert-circle": return "exclamationmark.triangle.fill"
        case "shield": return "shield.fill"
        case "settings": return "gearshape.fill"
        default: return "circle.fill"
        }
    }
    
    private func getTabIndex(_ tabId: String) -> Int {
        guard let index = authViewModel.roleConfig.navigation.firstIndex(where: { $0.id == tabId }) else {
            return 0
        }
        return index
    }
}

// MARK: - Customer Views
struct CustomerView: View {
    let tabId: String
    @StateObject private var apiService = APIService.shared
    
    var body: some View {
        switch tabId {
        case "dashboard":
            CustomerDashboardView()
        case "booking":
            BookingFlowView()
        case "tracking":
            OrderTrackingView()
        case "history":
            OrderHistoryView()
        case "profile":
            ProfileView()
        default:
            Text("View not implemented")
        }
    }
}

struct CustomerDashboardView: View {
    @State private var orders: [Order] = []
    @State private var isLoading = false
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Welcome Section
                VStack(alignment: .leading, spacing: 8) {
                    Text("مرحباً بك")
                        .font(.title2)
                        .fontWeight(.bold)
                    Text("ماذا تريد أن تفعل اليوم؟")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(12)
                
                // Quick Actions
                VStack(spacing: 16) {
                    NavigationLink(destination: BookingFlowView()) {
                        QuickActionCard(
                            title: "طلب حمولة جديدة",
                            subtitle: "احجز شحنتك الآن",
                            icon: "box.truck.fill",
                            color: .blue
                        )
                    }
                    
                    NavigationLink(destination: OrderTrackingView()) {
                        QuickActionCard(
                            title: "تتبع شحنتك",
                            subtitle: "تابع حالة الشحن",
                            icon: "location.fill",
                            color: .green
                        )
                    }
                }
                
                // Recent Orders
                VStack(alignment: .leading, spacing: 12) {
                    Text("الطلبات الأخيرة")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    if isLoading {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                    } else if orders.isEmpty {
                        Text("لا توجد طلبات حالية")
                            .foregroundColor(.secondary)
                            .frame(maxWidth: .infinity, alignment: .center)
                            .padding()
                    } else {
                        ForEach(orders.prefix(3), id: \.id) { order in
                            OrderCard(order: order)
                        }
                    }
                }
            }
            .padding()
        }
        .navigationTitle("الرئيسية")
        .onAppear {
            loadOrders()
        }
    }
    
    private func loadOrders() {
        isLoading = true
        APIService.shared.getCustomerOrders { result in
            DispatchQueue.main.async {
                isLoading = false
                switch result {
                case .success(let orders):
                    self.orders = orders
                case .failure(let error):
                    print("Error loading orders: \(error)")
                }
            }
        }
    }
}

struct QuickActionCard: View {
    let title: String
    let subtitle: String
    let icon: String
    let color: Color
    
    var body: some View {
        HStack(spacing: 16) {
            Image(systemName: icon)
                .font(.title2)
                .foregroundColor(color)
                .frame(width: 50, height: 50)
                .background(color.opacity(0.1))
                .cornerRadius(12)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.headline)
                    .fontWeight(.semibold)
                Text(subtitle)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Image(systemName: "chevron.left")
                .foregroundColor(.secondary)
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
    }
}

struct OrderCard: View {
    let order: Order
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text("طلب #\(order.orderNumber)")
                    .font(.headline)
                    .fontWeight(.semibold)
                Spacer()
                StatusBadge(status: order.status)
            }
            
            Text("\(order.route.pickup.address) → \(order.route.dropoff.address)")
                .font(.subheadline)
                .foregroundColor(.secondary)
            
            HStack {
                Text("\(order.invoice.totalAmount) ريال")
                    .font(.subheadline)
                    .fontWeight(.medium)
                Spacer()
                Text(formatDate(order.createdAt))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
    }
    
    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.locale = Locale(identifier: "ar_SA")
        return formatter.string(from: date)
    }
}

struct StatusBadge: View {
    let status: String
    
    var body: some View {
        Text(getStatusText(status))
            .font(.caption)
            .fontWeight(.medium)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(getStatusColor(status).opacity(0.1))
            .foregroundColor(getStatusColor(status))
            .cornerRadius(8)
    }
    
    private func getStatusText(_ status: String) -> String {
        switch status {
        case "PENDING": return "في الانتظار"
        case "CONFIRMED": return "مؤكد"
        case "ASSIGNED": return "مُعين"
        case "IN_TRANSIT": return "في الطريق"
        case "DELIVERED": return "تم التوصيل"
        default: return status
        }
    }
    
    private func getStatusColor(_ status: String) -> Color {
        switch status {
        case "PENDING": return .orange
        case "CONFIRMED": return .blue
        case "ASSIGNED": return .purple
        case "IN_TRANSIT": return .green
        case "DELIVERED": return .green
        default: return .gray
        }
    }
}

// MARK: - Driver Views
struct DriverView: View {
    let tabId: String
    
    var body: some View {
        switch tabId {
        case "dashboard":
            DriverDashboardView()
        case "tasks":
            DriverTasksView()
        case "location":
            DriverLocationView()
        case "earnings":
            DriverEarningsView()
        case "profile":
            ProfileView()
        default:
            Text("View not implemented")
        }
    }
}

struct DriverDashboardView: View {
    @State private var activeTask: DriverTask?
    @State private var isLoading = false
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Driver Status
                DriverStatusCard()
                
                // Active Task
                if let task = activeTask {
                    ActiveTaskCard(task: task)
                } else {
                    NoActiveTaskCard()
                }
                
                // Quick Actions
                VStack(spacing: 12) {
                    Button(action: updateLocation) {
                        HStack {
                            Image(systemName: "location.fill")
                            Text("تحديث الموقع")
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.green)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                    }
                    
                    Button(action: refreshTasks) {
                        HStack {
                            Image(systemName: "arrow.clockwise")
                            Text("تحديث المهام")
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                    }
                }
            }
            .padding()
        }
        .navigationTitle("لوحة التحكم")
        .onAppear {
            loadActiveTask()
        }
    }
    
    private func loadActiveTask() {
        isLoading = true
        APIService.shared.getActiveTask { result in
            DispatchQueue.main.async {
                isLoading = false
                switch result {
                case .success(let task):
                    self.activeTask = task
                case .failure(let error):
                    print("Error loading active task: \(error)")
                }
            }
        }
    }
    
    private func updateLocation() {
        // Get current location and update
        LocationManager.shared.getCurrentLocation { location in
            guard let location = location else { return }
            
            APIService.shared.updateDriverLocation(
                coordinates: [location.coordinate.longitude, location.coordinate.latitude],
                accuracy: location.horizontalAccuracy,
                speed: location.speed,
                heading: location.course,
                timestamp: location.timestamp
            ) { result in
            switch result {
            case .success:
                print("Location updated successfully")
            case .failure(let error):
                print("Error updating location: \(error)")
            }
        }
    }
    
    private func refreshTasks() {
        loadActiveTask()
    }
}

struct DriverStatusCard: View {
    var body: some View {
        HStack(spacing: 16) {
            Image(systemName: "person.fill")
                .font(.title)
                .foregroundColor(.green)
                .frame(width: 50, height: 50)
                .background(Color.green.opacity(0.1))
                .cornerRadius(12)
            
            VStack(alignment: .leading, spacing: 4) {
                Text("متصل الآن")
                    .font(.headline)
                    .fontWeight(.semibold)
                Text("جاهز لاستقبال المهام")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Toggle("", isOn: .constant(true))
                .disabled(true)
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
    }
}

struct ActiveTaskCard: View {
    let task: DriverTask
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("مهمة نشطة")
                    .font(.headline)
                    .fontWeight(.semibold)
                Spacer()
                StatusBadge(status: task.status)
            }
            
            Text("طلب #\(task.order.orderNumber)")
                .font(.subheadline)
                .foregroundColor(.secondary)
            
            Text("\(task.order.route.pickup.address) → \(task.order.route.dropoff.address)")
                .font(.subheadline)
            
            HStack {
                Button(action: updateTaskStatus) {
                    Text("تحديث الحالة")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                
                Button(action: navigateToLocation) {
                    Text("التنقل")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.green)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
    }
    
    private func updateTaskStatus() {
        // Update task status logic
    }
    
    private func navigateToLocation() {
        // Open navigation app logic
    }
}

struct NoActiveTaskCard: View {
    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: "box.truck")
                .font(.system(size: 50))
                .foregroundColor(.secondary)
            
            Text("لا توجد مهام نشطة")
                .font(.headline)
                .fontWeight(.semibold)
            
            Text("في انتظار تعيين مهمة جديدة")
                .font(.subheadline)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
        }
        .padding()
        .frame(maxWidth: .infinity)
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
    }
}

// MARK: - Other Role Views (Simplified)
struct SupervisorView: View {
    let tabId: String
    
    var body: some View {
        Text("Supervisor View - \(tabId)")
            .navigationTitle(getTitle(tabId))
    }
    
    private func getTitle(_ tabId: String) -> String {
        switch tabId {
        case "dashboard": return "غرفة العمليات"
        case "fleet": return "الأسطول"
        case "dispatch": return "توزيع الطلبات"
        case "drivers": return "السائقين"
        case "analytics": return "التحليلات"
        default: return tabId
        }
    }
}

struct AccountantView: View {
    let tabId: String
    
    var body: some View {
        Text("Accountant View - \(tabId)")
            .navigationTitle(getTitle(tabId))
    }
    
    private func getTitle(_ tabId: String) -> String {
        switch tabId {
        case "dashboard": return "لوحة المالية"
        case "invoices": return "الفواتير"
        case "payments": return "المدفوعات"
        case "reports": return "التقارير"
        case "tax": return "الضرائب"
        default: return tabId
        }
    }
}

struct WorkshopView: View {
    let tabId: String
    
    var body: some View {
        Text("Workshop View - \(tabId)")
            .navigationTitle(getTitle(tabId))
    }
    
    private func getTitle(_ tabId: String) -> String {
        switch tabId {
        case "dashboard": return "لوحة الصيانة"
        case "fleet": return "حالة الأسطول"
        case "maintenance": return "الصيانة"
        case "parts": return "القطع"
        case "alerts": return "التنبيهات"
        default: return tabId
        }
    }
}

struct AdminView: View {
    let tabId: String
    
    var body: some View {
        Text("Admin View - \(tabId)")
            .navigationTitle(getTitle(tabId))
    }
    
    private func getTitle(_ tabId: String) -> String {
        switch tabId {
        case "dashboard": return "لوحة التحكم"
        case "supervisor": return "المشرفين"
        case "accountant": return "المحاسبة"
        case "workshop": return "الورشة"
        case "settings": return "الإعدادات"
        default: return tabId
        }
    }
}

// MARK: - Common Views
struct ProfileView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Profile Header
                VStack(spacing: 16) {
                    Image(systemName: "person.circle.fill")
                        .font(.system(size: 80))
                        .foregroundColor(.blue)
                    
                    Text(authViewModel.user?.name ?? "")
                        .font(.title2)
                        .fontWeight(.bold)
                    
                    Text(authViewModel.user?.email ?? "")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding()
                
                // Profile Actions
                VStack(spacing: 12) {
                    Button(action: {}) {
                        HStack {
                            Image(systemName: "person.fill")
                            Text("تعديل الملف الشخصي")
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(12)
                    }
                    
                    Button(action: {}) {
                        HStack {
                            Image(systemName: "bell.fill")
                            Text "الإشعارات")
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(12)
                    }
                    
                    Button(action: {}) {
                        HStack {
                            Image(systemName: "questionmark.circle.fill")
                            Text("المساعدة والدعم")
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(12)
                    }
                    
                    Button(action: authViewModel.logout) {
                        HStack {
                            Image(systemName: "arrow.left.square.fill")
                            Text("تسجيل الخروج")
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.red)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                    }
                }
            }
            .padding()
        }
        .navigationTitle("الملف الشخصي")
    }
}

// MARK: - Extensions
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (1, 1, 1, 0)
        }

        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue:  Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

// MARK: - Preview
struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
