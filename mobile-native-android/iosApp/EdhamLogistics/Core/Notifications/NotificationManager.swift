import Foundation
import UserNotifications

class NotificationManager: NSObject, ObservableObject {
    static let shared = NotificationManager()

    @Published var permissionGranted = false

    override init() {
        super.init()
        requestPermission()
    }

    func requestPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            DispatchQueue.main.async {
                self.permissionGranted = granted
            }
        }
    }

    func sendLocalNotification(title: String, body: String, type: NotificationType) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = .default

        // Add custom data based on type
        content.userInfo = ["type": type.rawValue]

        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest(identifier: UUID().uuidString, content: content, trigger: trigger)

        UNUserNotificationCenter.current().add(request)
    }
}

enum NotificationType: String {
    case temperatureAlert = "TEMP_ALERT"
    case newMission = "NEW_MISSION"
    case shipmentUpdate = "SHIPMENT_UPDATE"
    case sosResponse = "SOS_RESPONSE"
}
