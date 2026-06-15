//
//  LocationManager.swift
//  Edham Logistics
//

import Foundation
import CoreLocation
import Combine

@MainActor
class LocationManager: NSObject, ObservableObject {
    static let shared = LocationManager()
    
    @Published var currentLocation: CLLocation?
    @Published var authorizationStatus: CLAuthorizationStatus = .notDetermined
    @Published var isTracking = false
    @Published var routeCoordinates: [CLLocationCoordinate2D] = []
    
    private let locationManager = CLLocationManager()
    private var trackingTimer: Timer?
    private let baseURL = "https://api.edham-logistics.com/api/v1"
    
    private var lastSentLocation: CLLocation?
    private let minimumDistanceThreshold: CLLocationDistance = 50 // meters
    private let minimumTimeThreshold: TimeInterval = 30 // seconds
    
    private override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.allowsBackgroundLocationUpdates = true
        locationManager.pausesLocationUpdatesAutomatically = false
        locationManager.activityType = .automotiveNavigation
    }
    
    // MARK: - Authorization
    
    func requestAuthorization() {
        locationManager.requestAlwaysAuthorization()
    }
    
    // MARK: - Tracking
    
    func startTracking() {
        guard CLLocationManager.locationServicesEnabled() else { return }
        
        isTracking = true
        locationManager.startUpdatingLocation()
        locationManager.startMonitoringSignificantLocationChanges()
        
        // Background tracking timer
        trackingTimer = Timer.scheduledTimer(withTimeInterval: 60, repeats: true) { _ in
            Task {
                await self.sendLocationUpdate()
            }
        }
        
        print("📍 Location tracking started")
    }
    
    func stopTracking() {
        isTracking = false
        locationManager.stopUpdatingLocation()
        locationManager.stopMonitoringSignificantLocationChanges()
        trackingTimer?.invalidate()
        trackingTimer = nil
        
        print("📍 Location tracking stopped")
    }
    
    // MARK: - Location Updates
    
    private func handleLocationUpdate(_ location: CLLocation) {
        currentLocation = location
        
        // Add to route
        routeCoordinates.append(location.coordinate)
        
        // Check if we should send to server
        if shouldSendLocationUpdate(location) {
            Task {
                await sendLocationUpdate(location)
            }
        }
    }
    
    private func shouldSendLocationUpdate(_ location: CLLocation) -> Bool {
        guard let lastLocation = lastSentLocation else { return true }
        
        let distance = location.distance(from: lastLocation)
        let timeSinceLast = location.timestamp.timeIntervalSince(lastLocation.timestamp)
        
        return distance >= minimumDistanceThreshold || timeSinceLast >= minimumTimeThreshold
    }
    
    private func sendLocationUpdate(_ location: CLLocation? = nil) async {
        guard let location = location ?? currentLocation,
              let token = AuthManager.shared.currentUser?.id else { return }
        
        do {
            let body: [String: Any] = [
                "latitude": location.coordinate.latitude,
                "longitude": location.coordinate.longitude,
                "accuracy": location.horizontalAccuracy,
                "altitude": location.altitude,
                "speed": location.speed,
                "course": location.course,
                "timestamp": ISO8601DateFormatter().string(from: location.timestamp)
            ]
            
            let _: EmptyResponse = try await APIClient.shared.post(
                endpoint: "/drivers/location",
                body: body
            )
            
            lastSentLocation = location
            print("📤 Location sent: \(location.coordinate.latitude), \(location.coordinate.longitude)")
            
        } catch {
            print("❌ Failed to send location: \(error)")
        }
    }
    
    // MARK: - Geofencing
    
    func startMonitoringGeofence(center: CLLocationCoordinate2D, radius: CLLocationDistance, identifier: String) {
        let region = CLCircularRegion(center: center, radius: radius, identifier: identifier)
        region.notifyOnEntry = true
        region.notifyOnExit = true
        locationManager.startMonitoring(for: region)
    }
    
    func stopMonitoringGeofence(identifier: String) {
        for region in locationManager.monitoredRegions {
            if region.identifier == identifier {
                locationManager.stopMonitoring(for: region)
            }
        }
    }
    
    // MARK: - Helpers
    
    func distance(from coordinate: CLLocationCoordinate2D) -> CLLocationDistance? {
        guard let current = currentLocation else { return nil }
        let location = CLLocation(latitude: coordinate.latitude, longitude: coordinate.longitude)
        return current.distance(from: location)
    }
    
    func calculateETA(to destination: CLLocationCoordinate2D, averageSpeed: CLLocationSpeed = 60) -> TimeInterval? {
        guard let distance = distance(from: destination) else { return nil }
        // Convert to hours (speed in km/h, distance in meters)
        let hours = (distance / 1000) / averageSpeed
        return hours * 3600 // Convert to seconds
    }
}

// MARK: - CLLocationManagerDelegate
extension LocationManager: CLLocationManagerDelegate {
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        handleLocationUpdate(location)
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("📍 Location error: \(error.localizedDescription)")
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        authorizationStatus = status
        
        switch status {
        case .authorizedAlways, .authorizedWhenInUse:
            if AuthManager.shared.currentUser?.role == .driver {
                startTracking()
            }
        case .denied, .restricted:
            stopTracking()
        case .notDetermined:
            requestAuthorization()
        @unknown default:
            break
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didEnterRegion region: CLRegion) {
        print("📍 Entered region: \(region.identifier)")
        // Handle geofence entry - e.g., arrived at pickup/delivery
        NotificationCenter.default.post(name: .didEnterGeofence, object: region.identifier)
    }
    
    func locationManager(_ manager: CLLocationManager, didExitRegion region: CLRegion) {
        print("📍 Exited region: \(region.identifier)")
        NotificationCenter.default.post(name: .didExitGeofence, object: region.identifier)
    }
}

// MARK: - Notifications
extension Notification.Name {
    static let didEnterGeofence = Notification.Name("didEnterGeofence")
    static let didExitGeofence = Notification.Name("didExitGeofence")
}
