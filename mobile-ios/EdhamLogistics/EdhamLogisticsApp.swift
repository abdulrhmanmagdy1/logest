//
//  EdhamLogisticsApp.swift
//  Edham Logistics - تطبيق إدهام اللوجستي
//
//  Created by Edham Team on 2024.
//

import SwiftUI
import FirebaseCore
import FirebaseMessaging
import UserNotifications

@main
struct EdhamLogisticsApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @StateObject private var authManager = AuthManager.shared
    @StateObject private var locationManager = LocationManager.shared
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(authManager)
                .environmentObject(locationManager)
                .preferredColorScheme(.dark)
        }
    }
}

// MARK: - App Delegate
class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        // Firebase
        FirebaseApp.configure()
        
        // Notifications
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self
        
        requestNotificationPermissions()
        
        // Appearance
        setupAppearance()
        
        return true
    }
    
    func requestNotificationPermissions() {
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { _, _ in }
        )
    }
    
    func setupAppearance() {
        UINavigationBar.appearance().tintColor = UIColor(ColorTheme.primary)
        UITabBar.appearance().tintColor = UIColor(ColorTheme.primary)
        UITableView.appearance().backgroundColor = UIColor(ColorTheme.background)
    }
    
    // MARK: - Push Notifications
    func application(_ application: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("FCM Token: \(fcmToken ?? "")")
        // Send token to backend
        Task {
            await AuthManager.shared.updateFCMToken(fcmToken)
        }
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([[.banner, .sound, .badge]])
    }
}

// MARK: - Color Theme
struct ColorTheme {
    static let primary = Color("PrimaryColor")
    static let primaryDark = Color("PrimaryDark")
    static let background = Color("BackgroundColor")
    static let cardBackground = Color("CardBackground")
    static let success = Color.green
    static let warning = Color.orange
    static let error = Color.red
    static let info = Color.blue
    
    // Semantic colors
    static let textPrimary = Color.white
    static let textSecondary = Color.gray
    static let border = Color.gray.opacity(0.3)
}

// MARK: - Content View
struct ContentView: View {
    @EnvironmentObject var authManager: AuthManager
    
    var body: some View {
        Group {
            if authManager.isAuthenticated {
                MainTabView()
                    .transition(.opacity)
            } else {
                LoginView()
                    .transition(.opacity)
            }
        }
    }
}

// MARK: - Preview
struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
            .environmentObject(AuthManager.shared)
    }
}
