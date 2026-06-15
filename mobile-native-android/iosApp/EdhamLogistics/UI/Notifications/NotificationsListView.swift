import SwiftUI

struct NotificationsListView: View {
    @Environment(\.dismiss) var dismiss
    @State private var notifications: [AppNotification] = [
        AppNotification(title: "تنبيه حرارة", body: "انخفاض درجة الحرارة في الشاحنة #402", time: "منذ دقيقتين", type: .temperatureAlert, isRead: false),
        AppNotification(title: "مهمة جديدة", body: "تم تكليفك برحلة جديدة إلى مستودع السلي", time: "منذ ساعة", type: .newMission, isRead: true),
        AppNotification(title: "تحديث شحنة", body: "العميل استلم الشحنة رقم #9921", time: "منذ 3 ساعات", type: .shipmentUpdate, isRead: true)
    ]

    var body: some View {
        ZStack {
            AppColors.cockpitBlack.ignoresSafeArea()

            VStack(spacing: 0) {
                headerSection

                ScrollView {
                    VStack(spacing: 12) {
                        ForEach(notifications) { notification in
                            notificationRow(notification)
                        }
                    }
                    .padding()
                }
            }
        }
    }

    var headerSection: some View {
        HStack {
            Button(action: { dismiss() }) {
                Image(systemName: "chevron.right")
                    .foregroundColor(.white)
                    .padding()
                    .background(AppColors.cockpitBlack2)
                    .clipShape(Circle())
            }
            Spacer()
            Text("مركز التنبيهات")
                .font(.headline)
                .foregroundColor(.white)
            Spacer()
            Button(action: { notifications.removeAll() }) {
                Text("مسح الكل")
                    .font(.caption)
                    .foregroundColor(.red)
            }
        }
        .padding()
    }

    func notificationRow(_ notification: AppNotification) -> some View {
        HStack(spacing: 15) {
            ZStack {
                Circle()
                    .fill(iconColor(for: notification.type).opacity(0.15))
                    .frame(width: 45, height: 45)
                Image(systemName: iconName(for: notification.type))
                    .foregroundColor(iconColor(for: notification.type))
            }

            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text(notification.title)
                        .font(.subheadline)
                        .bold()
                        .foregroundColor(.white)
                    Spacer()
                    if !notification.isRead {
                        Circle().fill(Color.blue).frame(width: 8, height: 8)
                    }
                }

                Text(notification.body)
                    .font(.caption)
                    .foregroundColor(.gray)
                    .lineLimit(2)

                Text(notification.time)
                    .font(.system(size: 9))
                    .foregroundColor(AppColors.cockpitBlue.opacity(0.7))
            }
        }
        .padding()
        .background(AppColors.cockpitBlack2)
        .cornerRadius(16)
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(notification.isRead ? AppColors.border : AppColors.cockpitBlue.opacity(0.3), lineWidth: 1))
    }

    func iconName(for type: NotificationType) -> String {
        switch type {
        case .temperatureAlert: return "thermometer.snowflake"
        case .newMission: return "box.truck.fill"
        case .shipmentUpdate: return "bell.badge.fill"
        case .sosResponse: return "exclamationmark.shield.fill"
        }
    }

    func iconColor(for type: NotificationType) -> Color {
        switch type {
        case .temperatureAlert: return .red
        case .newMission: return AppColors.cockpitGreen
        case .shipmentUpdate: return AppColors.cockpitBlue
        case .sosResponse: return .orange
        }
    }
}

struct AppNotification: Identifiable {
    let id = UUID()
    let title: String
    let body: String
    let time: String
    let type: NotificationType
    let isRead: Bool
}
