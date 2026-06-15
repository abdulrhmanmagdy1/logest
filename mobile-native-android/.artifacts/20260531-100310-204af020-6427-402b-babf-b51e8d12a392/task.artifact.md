# Task List - Supervisor System Migration

## Phase 1: Zero-Mock Backend (The Foundation)
- [ ] Create `Part` and `Survey` Entities, Repositories, and Controllers in Spring Boot
- [ ] Implement database-backed calculations for all Dashboard Stats (Revenue Sum, Fleet Count)
- [ ] Refactor `TrackingService` to use a dedicated simulation loop that persists to DB
- [ ] Create missing endpoints for Parts Inventory and Driver Surveys

## Phase 2: Professional Live Map (Final Polish)
- [ ] Replace Polling with real WebSocket events for Map Markers
- [ ] Implement "Smooth Transition" animation for moving trucks (no jumps)
- [ ] Connect `BottomSheetTruckInfo` to real current `Shipment` data from the database
- [ ] Implement Geofencing alerts (Riyadh Hub, Jeddah Port)

## Phase 3: Operations & Dispatch
- [ ] Fully integrate "Assign Driver" flow with backend database updates
- [ ] Implement "Add Vehicle" and "Update Mileage" forms with API calls
- [ ] Build the "Invoices Management" full lifecycle (Create -> Pay -> Archive)

## Phase 4: Data Tables & BI
- [ ] Connect `FleetFragment` to real `VehicleRepository`
- [ ] Connect `InventoryFragment` to new `PartRepository`
- [ ] Connect `SurveyFragment` to real driver feedback data
- [ ] Implement "Export to Excel" feature for Financial Reports (Invoices)

## Phase 5: Verification & Production Readiness
- [ ] Performance profiling for the Map with 50+ concurrent vehicles
- [ ] RTL/Arabic Layout audit for all screens
- [ ] Security audit for Supervisor endpoints
