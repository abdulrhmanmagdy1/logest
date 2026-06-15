//
//  DashboardView.swift
//  Edham Logistics
//

import SwiftUI
import Charts

struct DashboardView: View {
    @EnvironmentObject var authManager: AuthManager
    @StateObject private var viewModel = DashboardViewModel()
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Header
                headerView
                
                // Quick Stats
                statsGridView
                
                // Recent Activity
                recentActivityView
                
                // Active Shipments
                activeShipmentsView
                
                // Quick Actions
                quickActionsView
            }
            .padding()
        }
        .background(ColorTheme.background)
        .refreshable {
            await viewModel.loadData()
        }
    }
    
    // MARK: - Header
    private var headerView: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text("مرحباً،")
                    .font(.subheadline)
                    .foregroundColor(ColorTheme.textSecondary)
                
                Text(authManager.currentUser?.firstName ?? "")
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(.white)
            }
            
            Spacer()
            
            // Notification Bell
            Button(action: {
                // Show notifications
            }) {
                ZStack {
                    Image(systemName: "bell.fill")
                        .font(.title3)
                        .foregroundColor(.white)
                    
                    if viewModel.unreadNotifications > 0 {
                        Text("\(viewModel.unreadNotifications)")
                            .font(.caption2)
                            .foregroundColor(.white)
                            .padding(4)
                            .background(Color.red)
                            .clipShape(Circle())
                            .offset(x: 8, y: -8)
                    }
                }
            }
            
            // Profile Image
            Button(action: {
                // Show profile
            }) {
                Circle()
                    .fill(ColorTheme.primary)
                    .frame(width: 44, height: 44)
                    .overlay(
                        Text(String(authManager.currentUser?.firstName.prefix(1) ?? "U"))
                            .font(.headline)
                            .foregroundColor(.white)
                    )
            }
        }
    }
    
    // MARK: - Stats Grid
    private var statsGridView: some View {
        LazyVGrid(columns: [
            GridItem(.flexible()),
            GridItem(.flexible())
        ], spacing: 16) {
            StatCard(
                title: "الشحنات النشطة",
                value: "\(viewModel.activeShipments)",
                icon: "box.truck",
                color: .blue,
                trend: "+5%"
            )
            
            StatCard(
                title: "مكتملة اليوم",
                value: "\(viewModel.completedToday)",
                icon: "checkmark.circle",
                color: .green,
                trend: "+12%"
            )
            
            StatCard(
                title: "قيد الانتظار",
                value: "\(viewModel.pendingShipments)",
                icon: "clock",
                color: .orange,
                trend: "-3%"
            )
            
            StatCard(
                title: "متوسط التقييم",
                value: "\(viewModel.averageRating, specifier: "%.1f")",
                icon: "star.fill",
                color: .yellow,
                trend: "+0.2"
            )
        }
    }
    
    // MARK: - Recent Activity
    private var recentActivityView: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("النشاط الأخير")
                    .font(.headline)
                    .foregroundColor(.white)
                
                Spacer()
                
                Button("عرض الكل") {
                    // Navigate to activity
                }
                .font(.caption)
                .foregroundColor(ColorTheme.primary)
            }
            
            ForEach(viewModel.recentActivities) { activity in
                ActivityRow(activity: activity)
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(ColorTheme.cardBackground)
        )
    }
    
    // MARK: - Active Shipments
    private var activeShipmentsView: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("الشحنات النشطة")
                    .font(.headline)
                    .foregroundColor(.white)
                
                Spacer()
                
                Button("عرض الكل") {
                    // Navigate to shipments
                }
                .font(.caption)
                .foregroundColor(ColorTheme.primary)
            }
            
            if viewModel.shipments.isEmpty {
                EmptyStateView(
                    icon: "box.truck",
                    message: "لا توجد شحنات نشطة"
                )
            } else {
                ForEach(viewModel.shipments.prefix(3)) { shipment in
                    ShipmentRow(shipment: shipment)
                }
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(ColorTheme.cardBackground)
        )
    }
    
    // MARK: - Quick Actions
    private var quickActionsView: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("إجراءات سريعة")
                .font(.headline)
                .foregroundColor(.white)
            
            LazyVGrid(columns: [
                GridItem(.flexible()),
                GridItem(.flexible()),
                GridItem(.flexible())
            ], spacing: 16) {
                QuickActionButton(
                    title: "شحنة جديدة",
                    icon: "plus.circle",
                    color: .blue
                ) {
                    // Create shipment
                }
                
                QuickActionButton(
                    title: "تتبع",
                    icon: "mappin.and.ellipse",
                    color: .green
                ) {
                    // Track shipment
                }
                
                QuickActionButton(
                    title: "الفواتير",
                    icon: "doc.text",
                    color: .orange
                ) {
                    // View invoices
                }
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(ColorTheme.cardBackground)
        )
    }
}

// MARK: - Supporting Views

struct StatCard: View {
    let title: String
    let value: String
    let icon: String
    let color: Color
    let trend: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Image(systemName: icon)
                    .font(.title2)
                    .foregroundColor(color)
                
                Spacer()
                
                Text(trend)
                    .font(.caption)
                    .foregroundColor(trend.hasPrefix("+") ? .green : .red)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(
                        (trend.hasPrefix("+") ? Color.green : Color.red)
                            .opacity(0.2)
                    )
                    .cornerRadius(8)
            }
            
            Text(value)
                .font(.title)
                .fontWeight(.bold)
                .foregroundColor(.white)
            
            Text(title)
                .font(.caption)
                .foregroundColor(ColorTheme.textSecondary)
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(ColorTheme.cardBackground)
        )
    }
}

struct ActivityRow: View {
    let activity: Activity
    
    var body: some View {
        HStack(spacing: 12) {
            // Icon
            Circle()
                .fill(activity.type.color.opacity(0.2))
                .frame(width: 40, height: 40)
                .overlay(
                    Image(systemName: activity.type.icon)
                        .foregroundColor(activity.type.color)
                )
            
            VStack(alignment: .leading, spacing: 4) {
                Text(activity.title)
                    .font(.subheadline)
                    .foregroundColor(.white)
                
                Text(activity.timeAgo)
                    .font(.caption)
                    .foregroundColor(ColorTheme.textSecondary)
            }
            
            Spacer()
        }
        .padding(.vertical, 4)
    }
}

struct ShipmentRow: View {
    let shipment: Shipment
    
    var body: some View {
        HStack(spacing: 12) {
            // Status indicator
            Circle()
                .fill(shipment.status.color)
                .frame(width: 12, height: 12)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(shipment.trackingNumber)
                    .font(.subheadline)
                    .fontWeight(.medium)
                    .foregroundColor(.white)
                
                Text("\(shipment.origin) → \(shipment.destination)")
                    .font(.caption)
                    .foregroundColor(ColorTheme.textSecondary)
            }
            
            Spacer()
            
            Text(shipment.status.displayName)
                .font(.caption)
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(shipment.status.color.opacity(0.2))
                .foregroundColor(shipment.status.color)
                .cornerRadius(8)
        }
        .padding(.vertical, 8)
    }
}

struct QuickActionButton: View {
    let title: String
    let icon: String
    let color: Color
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 8) {
                Image(systemName: icon)
                    .font(.title2)
                    .foregroundColor(color)
                
                Text(title)
                    .font(.caption)
                    .foregroundColor(.white)
            }
            .frame(maxWidth: .infinity)
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(ColorTheme.cardBackground)
            )
        }
    }
}

struct EmptyStateView: View {
    let icon: String
    let message: String
    
    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 48))
                .foregroundColor(ColorTheme.textSecondary)
            
            Text(message)
                .font(.subheadline)
                .foregroundColor(ColorTheme.textSecondary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 40)
    }
}

// MARK: - Preview
struct DashboardView_Previews: PreviewProvider {
    static var previews: some View {
        DashboardView()
            .environmentObject(AuthManager.shared)
            .preferredColorScheme(.dark)
    }
}
