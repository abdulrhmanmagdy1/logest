//
//  TrackingMapView.swift
//  Edham Logistics
//

import SwiftUI
import MapKit

struct TrackingMapView: View {
    @StateObject private var viewModel = TrackingMapViewModel()
    @State private var mapRegion = MKCoordinateRegion(
        center: CLLocationCoordinate2D(latitude: 24.7136, longitude: 46.6753), // Riyadh
        span: MKCoordinateSpan(latitudeDelta: 0.5, longitudeDelta: 0.5)
    )
    @State private var selectedShipment: Shipment?
    @State private var showRouteDetails = false
    
    var body: some View {
        ZStack {
            // Map
            Map(coordinateRegion: $mapRegion,
                showsUserLocation: true,
                annotationItems: viewModel.annotations) { annotation in
                MapAnnotation(coordinate: annotation.coordinate) {
                    ShipmentAnnotationView(
                        shipment: annotation.shipment,
                        isSelected: selectedShipment?.id == annotation.shipment.id
                    )
                    .onTapGesture {
                        selectedShipment = annotation.shipment
                    }
                }
            }
            .ignoresSafeArea()
            
            // Overlay UI
            VStack {
                // Search Bar
                searchBar
                
                Spacer()
                
                // Bottom Sheet
                if let shipment = selectedShipment {
                    ShipmentDetailCard(shipment: shipment) {
                        selectedShipment = nil
                    }
                    .transition(.move(edge: .bottom))
                }
            }
            .padding()
        }
        .sheet(isPresented: $showRouteDetails) {
            RouteDetailsView(shipment: selectedShipment)
        }
    }
    
    // MARK: - Search Bar
    private var searchBar: some View {
        HStack(spacing: 12) {
            Image(systemName: "magnifyingglass")
                .foregroundColor(ColorTheme.textSecondary)
            
            TextField("ابحث عن شحنة...", text: $viewModel.searchText)
                .foregroundColor(.white)
            
            if !viewModel.searchText.isEmpty {
                Button(action: {
                    viewModel.searchText = ""
                }) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(ColorTheme.textSecondary)
                }
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(ColorTheme.cardBackground)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(ColorTheme.border, lineWidth: 1)
        )
    }
}

// MARK: - Annotation View

struct ShipmentAnnotationView: View {
    let shipment: Shipment
    let isSelected: Bool
    
    var body: some View {
        ZStack {
            // Outer ring
            Circle()
                .fill(shipment.status.color.opacity(0.3))
                .frame(width: isSelected ? 60 : 50, height: isSelected ? 60 : 50)
            
            // Inner circle
            Circle()
                .fill(shipment.status.color)
                .frame(width: isSelected ? 50 : 40, height: isSelected ? 50 : 40)
            
            // Icon
            Image(systemName: "truck.box")
                .font(.system(size: isSelected ? 20 : 16))
                .foregroundColor(.white)
        }
        .scaleEffect(isSelected ? 1.1 : 1.0)
        .animation(.spring(), value: isSelected)
    }
}

// MARK: - Shipment Detail Card

struct ShipmentDetailCard: View {
    let shipment: Shipment
    let onClose: () -> Void
    
    var body: some View {
        VStack(spacing: 16) {
            // Handle bar
            RoundedRectangle(cornerRadius: 2.5)
                .fill(ColorTheme.textSecondary)
                .frame(width: 40, height: 5)
            
            // Header
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(shipment.trackingNumber)
                        .font(.headline)
                        .foregroundColor(.white)
                    
                    Text(shipment.status.displayName)
                        .font(.caption)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(shipment.status.color.opacity(0.2))
                        .foregroundColor(shipment.status.color)
                        .cornerRadius(8)
                }
                
                Spacer()
                
                Button(action: onClose) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.title3)
                        .foregroundColor(ColorTheme.textSecondary)
                }
            }
            
            Divider()
                .background(ColorTheme.border)
            
            // Route Info
            HStack(spacing: 16) {
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Image(systemName: "mappin.circle.fill")
                            .foregroundColor(.green)
                        Text(shipment.origin)
                            .font(.subheadline)
                            .foregroundColor(.white)
                    }
                    
                    HStack {
                        Image(systemName: "arrow.down")
                            .foregroundColor(ColorTheme.textSecondary)
                        Text(shipment.distance)
                            .font(.caption)
                            .foregroundColor(ColorTheme.textSecondary)
                    }
                    
                    HStack {
                        Image(systemName: "flag.circle.fill")
                            .foregroundColor(.red)
                        Text(shipment.destination)
                            .font(.subheadline)
                            .foregroundColor(.white)
                    }
                }
                
                Spacer()
                
                // Driver info
                if let driver = shipment.driver {
                    VStack(spacing: 8) {
                        Circle()
                            .fill(ColorTheme.primary)
                            .frame(width: 50, height: 50)
                            .overlay(
                                Text(String(driver.name.prefix(1)))
                                    .font(.headline)
                                    .foregroundColor(.white)
                            )
                        
                        Text(driver.name)
                            .font(.caption)
                            .foregroundColor(.white)
                        
                        Button("اتصال") {
                            // Call driver
                        }
                        .font(.caption)
                        .foregroundColor(.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .background(Color.green)
                        .cornerRadius(8)
                    }
                }
            }
            
            // Progress bar
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Text("تقدم الشحنة")
                        .font(.caption)
                        .foregroundColor(ColorTheme.textSecondary)
                    
                    Spacer()
                    
                    Text("\(Int(shipment.progress * 100))%")
                        .font(.caption)
                        .foregroundColor(.white)
                }
                
                GeometryReader { geometry in
                    ZStack(alignment: .leading) {
                        Rectangle()
                            .fill(ColorTheme.textSecondary.opacity(0.3))
                            .frame(height: 6)
                            .cornerRadius(3)
                        
                        Rectangle()
                            .fill(ColorTheme.primary)
                            .frame(width: geometry.size.width * shipment.progress, height: 6)
                            .cornerRadius(3)
                    }
                }
                .frame(height: 6)
            }
            
            // Actions
            HStack(spacing: 12) {
                Button("عرض التفاصيل") {
                    // Show details
                }
                .font(.subheadline)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding()
                .background(ColorTheme.primary)
                .cornerRadius(12)
                
                Button(action: {
                    // Share location
                }) {
                    Image(systemName: "square.and.arrow.up")
                        .font(.title3)
                        .foregroundColor(.white)
                        .padding()
                        .background(ColorTheme.cardBackground)
                        .cornerRadius(12)
                }
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 20)
                .fill(ColorTheme.cardBackground)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 20)
                .stroke(ColorTheme.border, lineWidth: 1)
        )
    }
}

// MARK: - Route Details View

struct RouteDetailsView: View {
    let shipment: Shipment?
    @Environment(\.dismiss) var dismiss
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    if let shipment = shipment {
                        // Route map preview
                        RoutePreviewMap(shipment: shipment)
                            .frame(height: 200)
                            .cornerRadius(16)
                        
                        // Timeline
                        ShipmentTimeline(shipment: shipment)
                    }
                }
                .padding()
            }
            .navigationTitle("تفاصيل المسار")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("تم") {
                        dismiss()
                    }
                }
            }
        }
    }
}

struct RoutePreviewMap: View {
    let shipment: Shipment
    
    var body: some View {
        Map(coordinateRegion: .constant(MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 24.7136, longitude: 46.6753),
            span: MKCoordinateSpan(latitudeDelta: 1.0, longitudeDelta: 1.0)
        )), annotationItems: [
            Place(name: shipment.origin, coordinate: CLLocationCoordinate2D(latitude: 24.7136, longitude: 46.6753)),
            Place(name: shipment.destination, coordinate: CLLocationCoordinate2D(latitude: 21.4858, longitude: 39.1925))
        ]) { place in
            MapMarker(coordinate: place.coordinate)
        }
    }
}

struct Place: Identifiable {
    let id = UUID()
    let name: String
    let coordinate: CLLocationCoordinate2D
}

struct ShipmentTimeline: View {
    let shipment: Shipment
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            ForEach(shipment.timeline) { event in
                TimelineRow(event: event, isLast: event.id == shipment.timeline.last?.id)
            }
        }
    }
}

struct TimelineRow: View {
    let event: TimelineEvent
    let isLast: Bool
    
    var body: some View {
        HStack(alignment: .top, spacing: 16) {
            // Timeline indicator
            VStack(spacing: 0) {
                Circle()
                    .fill(event.isCompleted ? Color.green : ColorTheme.textSecondary)
                    .frame(width: 12, height: 12)
                
                if !isLast {
                    Rectangle()
                        .fill(event.isCompleted ? Color.green.opacity(0.3) : ColorTheme.textSecondary.opacity(0.3))
                        .frame(width: 2)
                        .frame(maxHeight: .infinity)
                }
            }
            
            // Event details
            VStack(alignment: .leading, spacing: 4) {
                Text(event.title)
                    .font(.subheadline)
                    .fontWeight(event.isCompleted ? .medium : .regular)
                    .foregroundColor(.white)
                
                Text(event.time)
                    .font(.caption)
                    .foregroundColor(ColorTheme.textSecondary)
                
                if let location = event.location {
                    Text(location)
                        .font(.caption)
                        .foregroundColor(ColorTheme.textSecondary)
                }
            }
            .padding(.bottom, 20)
            
            Spacer()
        }
    }
}

// MARK: - Preview
struct TrackingMapView_Previews: PreviewProvider {
    static var previews: some View {
        TrackingMapView()
            .preferredColorScheme(.dark)
    }
}
