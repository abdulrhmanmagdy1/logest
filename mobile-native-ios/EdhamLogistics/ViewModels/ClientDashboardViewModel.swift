// ============================================
// 🏠 Client Dashboard ViewModel - Swift
// ============================================

import Foundation
import Combine

@MainActor
class ClientDashboardViewModel: ObservableObject {
    @Published var shipments: [Shipment] = []
    @Published var activeCount = 0
    @Published var completedCount = 0
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    private let apiService = APIService.shared
    
    func loadDashboard() async {
        isLoading = true
        
        do {
            let allShipments = try await apiService.getShipments()
            self.shipments = Array(allShipments.prefix(5))
            self.activeCount = allShipments.filter { $0.isActive }.count
            self.completedCount = allShipments.filter { $0.isCompleted }.count
        } catch {
            // Load demo data
            loadDemoData()
        }
        
        isLoading = false
    }
    
    private func loadDemoData() {
        activeCount = 12
        completedCount = 48
        
        // Demo shipments
        shipments = [
            Shipment(
                id: "1",
                trackingNumber: "EDH-1001",
                cargo: Cargo(
                    type: .general,
                    description: "بضاعة عامة",
                    weight: Weight(value: 500, unit: "كجم"),
                    temperature: nil,
                    specialInstructions: nil
                ),
                pickup: Pickup(
                    address: Address(street: nil, city: "الرياض", region: "الرياض", zipCode: nil, country: nil, coordinates: nil),
                    scheduledDate: Date(),
                    actualDate: nil,
                    timeWindow: nil
                ),
                delivery: Delivery(
                    address: Address(street: nil, city: "جدة", region: "مكة", zipCode: nil, country: nil, coordinates: nil),
                    scheduledDate: Date(),
                    actualDate: nil,
                    timeWindow: nil,
                    contactName: nil,
                    contactPhone: nil,
                    instructions: nil
                ),
                status: .inTransit,
                createdBy: "1",
                assignedDriver: "driver1",
                assignedTruck: "truck1",
                pricing: nil,
                createdAt: Date(),
                updatedAt: Date()
            ),
            Shipment(
                id: "2",
                trackingNumber: "EDH-1002",
                cargo: Cargo(
                    type: .frozen,
                    description: "مواد مجمدة",
                    weight: Weight(value: 1200, unit: "كجم"),
                    temperature: Temperature(min: -18, max: -12, critical: true),
                    specialInstructions: nil
                ),
                pickup: Pickup(
                    address: Address(street: nil, city: "الدمام", region: "الشرقية", zipCode: nil, country: nil, coordinates: nil),
                    scheduledDate: Date(),
                    actualDate: nil,
                    timeWindow: nil
                ),
                delivery: Delivery(
                    address: Address(street: nil, city: "الرياض", region: "الرياض", zipCode: nil, country: nil, coordinates: nil),
                    scheduledDate: Date(),
                    actualDate: nil,
                    timeWindow: nil,
                    contactName: nil,
                    contactPhone: nil,
                    instructions: nil
                ),
                status: .pending,
                createdBy: "1",
                assignedDriver: nil,
                assignedTruck: nil,
                pricing: nil,
                createdAt: Date(),
                updatedAt: Date()
            )
        ]
    }
}
