# Implementation Plan - Supervisor System Migration

This plan outlines the steps to migrate the HTML-based Supervisor Dashboard into the Android application, ensuring full backend integration and functional "system" behavior.

## Proposed Changes

### 1. Backend Enhancements (Spring Boot)

#### [SupervisorController](file:///D:/logest/mobile-native-android/backend-api/src/main/java/com/edham/logistics/controller/SupervisorController.java)
- Add missing endpoints for Fleet, Loads, Maintenance, Parts, and Survey.
- Integrate with `AdminDashboardService` for complex analytics.

#### [AdminDashboardController](file:///D:/logest/mobile-native-android/backend-api/src/main/java/com/edham/logistics/controller/AdminDashboardController.java) [NEW]
- Move from `disabled-files` to main source and fix imports.
- Update `@PreAuthorize` to support `SUPERVISOR` role as well.

#### [AdminDashboardService](file:///D:/logest/mobile-native-android/backend-api/src/main/java/com/edham/logistics/service/AdminDashboardService.java) [NEW]
- Move from `disabled-files` to main source and fix imports.
- Ensure all repositories are correctly injected.

---

### 2. Android Infrastructure (Android)

#### [AdminApi](file:///D:/logest/mobile-native-android/app/src/main/java/com/edham/logistics/feature/admin/data/remote/AdminApi.kt)
- Restore from `.bak` and update to match the enhanced backend endpoints.
- Group endpoints logically (Dashboard, Fleet, Finance, etc.).

#### [Navigation Graph](file:///D:/logest/mobile-native-android/app/src/main/res/navigation/nav_graph.xml)
- Add destinations for all Supervisor modules:
    - `nav_supervisor_dashboard`
    - `nav_supervisor_fleet`
    - `nav_supervisor_tracking`
    - `nav_supervisor_loads`
    - `nav_supervisor_finance`
    - `nav_supervisor_maintenance`
    - `nav_supervisor_parts`
    - `nav_supervisor_reports`
    - `nav_supervisor_survey`

#### [SupervisorActivity](file:///D:/logest/mobile-native-android/app/src/main/java/com/edham/logistics/feature/admin/presentation/SupervisorActivity.kt) [NEW]
- Implement `DrawerLayout` with a custom `NavigationView` header and items matching the HTML sidebar.

---

### 3. UI Implementation (Android)

#### [DashboardFragment](file:///D:/logest/mobile-native-android/app/src/main/java/com/edham/logistics/feature/admin/presentation/ui/dashboard/AdminDashboardFragment.kt)
- Rebuild layout to include:
    - Dynamic stats cards with background gradients.
    - Integrated `SupportMapFragment` for live overview.
    - Alerts `RecyclerView`.
    - `MPAndroidChart` integration for Pie/Bar charts.

#### [LiveTrackingFragment] [NEW]
- **Professional Map Features**:
    - **Custom Markers**: High-resolution SVG truck icons that rotate based on `bearing` data from the backend.
    - **Real-time Pulse**: Smooth animation between location updates (Interpolation) to avoid "jumping" icons.
    - **Status Overlay**: Color-coded markers (Green: Moving, Red: Over-speeding/Delayed, Blue: Idling).
    - **Geofencing**: Visual boundaries for warehouse and delivery zones.
    - **Rich Info Windows**: Custom layouts showing Driver photo, current cargo temperature, and estimated time of arrival (ETA).

#### [FleetFragment](file:///D:/logest/mobile-native-android/app/src/main/java/com/edham/logistics/feature/admin/presentation/ui/fleet/FleetFragment.kt) [NEW]
- List view of all vehicles.
- Status badges (Active, Maintenance, Delayed).
- `FloatingActionButton` for "Add Vehicle".

#### [Other Fragments] [NEW]
- Create fragments for Loads, Finance, Maintenance, Parts, Reports, and Survey.
- Use a common base fragment for standard table views.

---

### 4. Custom Components

#### [StatCardView](file:///D:/logest/mobile-native-android/app/src/main/java/com/edham/logistics/ui/components/StatCardView.kt) [NEW]
- A custom view to encapsulate the style of stats cards (Icon, Value, Label, Change %).

---

## Verification Plan

### Automated Tests
- **Unit Tests**: Test `AdminDashboardService` calculations.
- **Integration Tests**: Verify `SupervisorController` endpoints return correct status codes and data structures.
- **Android UI Tests**: Use Espresso to verify navigation between sidebar items.

### Manual Verification
1. **Login Flow**: Log in as a Supervisor and verify the `SupervisorActivity` loads.
2. **Dashboard Data**: Verify stats cards match the backend `supervisor/stats` data.
3. **Map Interaction**: Verify truck markers appear on the map.
4. **Navigation**: Click every sidebar item and ensure it opens the correct fragment.
5. **Arabic Layout**: Ensure RTL layout is correct and text is properly translated.
6. **Form Submission**: Test "Add Vehicle" and "Add Load" modals.
