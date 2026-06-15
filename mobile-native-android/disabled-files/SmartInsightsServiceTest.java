package com.edham.logistics.intelligence;

import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Smart Insights Service
 * Tests core business logic for predictions and recommendations
 */
@ExtendWith(MockitoExtension.class)
public class SmartInsightsServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private EmergencyRepository emergencyRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private SmartInsightsService smartInsightsService;

    private Shipment testShipment;
    private User testDriver;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        // Setup test shipment
        testShipment = new Shipment();
        testShipment.setId(1L);
        testShipment.setTrackingNumber("TEST001");
        testShipment.setOrigin("Riyadh");
        testShipment.setDestination("Jeddah");
        testShipment.setDriverId(1L);
        testShipment.setVehicleId(1L);
        testShipment.setExpectedDeliveryTime(LocalDateTime.now().plusHours(24));
        testShipment.setCreatedAt(LocalDateTime.now());

        // Setup test driver
        testDriver = new User();
        testDriver.setId(1L);
        testDriver.setName("Test Driver");
        testDriver.setEmail("driver@test.com");
        testDriver.setRole(UserRole.DRIVER);
        testDriver.setStatus("ACTIVE");
        testDriver.setVehicleId(1L);

        // Setup test vehicle
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setLicensePlate("TEST123");
        testVehicle.setStatus(VehicleStatus.AVAILABLE);
        testVehicle.setMileage(50000.0);
    }

    @Test
    void testPredictDelayedShipment_HighRisk() {
        // Given
        List<Shipment> historicalShipments = createHistoricalShipments(10, true); // High delay rate
        when(shipmentRepository.findByOriginAndDestination(eq("Riyadh"), eq("Jeddah")))
                .thenReturn(historicalShipments);
        when(shipmentRepository.findByDriverIdAndStatusIn(eq(1L), any()))
                .thenReturn(Arrays.asList(testShipment, testShipment)); // High workload
        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(testVehicle));

        // When
        SmartInsightsService.DelayPrediction prediction = smartInsightsService.predictDelayedShipment(testShipment);

        // Then
        assertNotNull(prediction);
        assertEquals("TEST001", prediction.getTrackingNumber());
        assertTrue(prediction.getDelayProbability() > 0.5, "Should predict high delay probability");
        assertNotNull(prediction.getPredictedReason());
        assertTrue(prediction.getEstimatedDelayHours() > 0);
        assertFalse(prediction.getFactors().isEmpty());
        assertNotNull(prediction.getRecommendation());
    }

    @Test
    void testPredictDelayedShipment_LowRisk() {
        // Given
        List<Shipment> historicalShipments = createHistoricalShipments(10, false); // Low delay rate
        when(shipmentRepository.findByOriginAndDestination(eq("Riyadh"), eq("Jeddah")))
                .thenReturn(historicalShipments);
        when(shipmentRepository.findByDriverIdAndStatusIn(eq(1L), any()))
                .thenReturn(Arrays.asList(testShipment)); // Low workload
        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(testVehicle));

        // When
        SmartInsightsService.DelayPrediction prediction = smartInsightsService.predictDelayedShipment(testShipment);

        // Then
        assertNotNull(prediction);
        assertEquals("TEST001", prediction.getTrackingNumber());
        assertTrue(prediction.getDelayProbability() < 0.5, "Should predict low delay probability");
        assertNotNull(prediction.getPredictedReason());
        assertTrue(prediction.getEstimatedDelayHours() >= 0);
        assertNotNull(prediction.getRecommendation());
    }

    @Test
    void testPredictDelayedShipment_ErrorHandling() {
        // Given
        when(shipmentRepository.findByOriginAndDestination(anyString(), anyString()))
                .thenThrow(new RuntimeException("Database error"));

        // When
        SmartInsightsService.DelayPrediction prediction = smartInsightsService.predictDelayedShipment(testShipment);

        // Then
        assertNotNull(prediction);
        assertEquals("TEST001", prediction.getTrackingNumber());
        assertEquals(0.0, prediction.getDelayProbability());
        assertEquals("ERROR", prediction.getPredictedReason());
        assertEquals(0, prediction.getEstimatedDelayHours());
        assertTrue(prediction.getFactors().isEmpty());
        assertEquals("Unable to predict due to system error", prediction.getRecommendation());
    }

    @Test
    void testSuggestDriverAllocation_Success() {
        // Given
        List<User> availableDrivers = Arrays.asList(testDriver);
        when(userRepository.findByRoleAndStatus(UserRole.DRIVER, "ACTIVE"))
                .thenReturn(availableDrivers);
        when(shipmentRepository.findByDriverId(anyLong()))
                .thenReturn(Arrays.asList(testShipment));
        when(vehicleRepository.findById(anyLong()))
                .thenReturn(Optional.of(testVehicle));

        // When
        SmartInsightsService.DriverAllocationSuggestion suggestion = 
                smartInsightsService.suggestDriverAllocation(testShipment);

        // Then
        assertNotNull(suggestion);
        assertEquals("TEST001", suggestion.getShipmentTrackingNumber());
        assertFalse(suggestion.getCandidates().isEmpty());
        assertNotNull(suggestion.getRecommendation());
        assertNotNull(suggestion.getGeneratedAt());

        // Verify top candidate
        SmartInsightsService.DriverCandidate topCandidate = suggestion.getCandidates().get(0);
        assertEquals(1L, topCandidate.getDriverId());
        assertEquals("Test Driver", topCandidate.getDriverName());
        assertTrue(topCandidate.getScore() >= 0.0);
        assertTrue(topCandidate.getScore() <= 1.0);
    }

    @Test
    void testSuggestDriverAllocation_NoAvailableDrivers() {
        // Given
        when(userRepository.findByRoleAndStatus(UserRole.DRIVER, "ACTIVE"))
                .thenReturn(Arrays.asList());

        // When
        SmartInsightsService.DriverAllocationSuggestion suggestion = 
                smartInsightsService.suggestDriverAllocation(testShipment);

        // Then
        assertNotNull(suggestion);
        assertEquals("TEST001", suggestion.getShipmentTrackingNumber());
        assertTrue(suggestion.getCandidates().isEmpty());
        assertEquals("No suitable drivers available", suggestion.getRecommendation());
    }

    @Test
    void testDetectInefficientRoutes_Success() {
        // Given
        List<Shipment> completedShipments = createCompletedShipments(20);
        when(shipmentRepository.findByStatusAndCreatedAtAfter(eq(ShipmentStatus.DELIVERED), any()))
                .thenReturn(completedShipments);
        when(shipmentRepository.findByOriginAndDestination(anyString(), anyString()))
                .thenReturn(completedShipments);

        // When
        List<SmartInsightsService.InefficientRoute> inefficientRoutes = 
                smartInsightsService.detectInefficientRoutes();

        // Then
        assertNotNull(inefficientRoutes);
        // Should detect some inefficient routes based on the test data
        verify(shipmentRepository, atLeastOnce()).findByStatusAndCreatedAtAfter(any(), any());
    }

    @Test
    void testDetectInefficientRoutes_ErrorHandling() {
        // Given
        when(shipmentRepository.findByStatusAndCreatedAtAfter(any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // When
        List<SmartInsightsService.InefficientRoute> inefficientRoutes = 
                smartInsightsService.detectInefficientRoutes();

        // Then
        assertNotNull(inefficientRoutes);
        assertTrue(inefficientRoutes.isEmpty()); // Should return empty list on error
    }

    @Test
    void testDetectSystemBottlenecks_Success() {
        // Given
        setupBottleneckTestData();

        // When
        SmartInsightsService.SystemBottlenecks bottlenecks = 
                smartInsightsService.detectSystemBottlenecks();

        // Then
        assertNotNull(bottlenecks);
        assertNotNull(bottlenecks.getBottlenecks());
        assertTrue(bottlenecks.getSystemHealthScore() >= 0.0);
        assertTrue(bottlenecks.getSystemHealthScore() <= 1.0);
        assertNotNull(bottlenecks.getAnalyzedAt());
    }

    @Test
    void testDetectSystemBottlenecks_DriverShortage() {
        // Given
        when(userRepository.countByRole(UserRole.DRIVER)).thenReturn(10L);
        when(userRepository.countByRoleAndStatus(UserRole.DRIVER, "ACTIVE")).thenReturn(5L);
        when(shipmentRepository.countByStatusIn(any()))
                .thenReturn(10L); // 2 shipments per driver (90% utilization)

        // When
        SmartInsightsService.SystemBottlenecks bottlenecks = 
                smartInsightsService.detectSystemBottlenecks();

        // Then
        assertNotNull(bottlenecks);
        boolean hasDriverShortage = bottlenecks.getBottlenecks().stream()
                .anyMatch(b -> "DRIVER_SHORTAGE".equals(b.getType()));
        assertTrue(hasDriverShortage, "Should detect driver shortage bottleneck");
    }

    @Test
    void testDetectSystemBottlenecks_MaintenanceBacklog() {
        // Given
        when(vehicleRepository.count()).thenReturn(20L);
        when(vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE)).thenReturn(3L);
        when(vehicleRepository.countByStatus(VehicleStatus.ISSUE_REPORTED)).thenReturn(2L);

        // When
        SmartInsightsService.SystemBottlenecks bottlenecks = 
                smartInsightsService.detectSystemBottlenecks();

        // Then
        assertNotNull(bottlenecks);
        boolean hasMaintenanceBacklog = bottlenecks.getBottlenecks().stream()
                .anyMatch(b -> "MAINTENANCE_BACKLOG".equals(b.getType()));
        assertTrue(hasMaintenanceBacklog, "Should detect maintenance backlog bottleneck");
    }

    @Test
    void testDelayFactor_Creation() {
        // Test DelayFactor data class
        SmartInsightsService.DelayFactor factor = 
                new SmartInsightsService.DelayFactor("TEST_FACTOR", 0.5, "Test description");

        assertEquals("TEST_FACTOR", factor.getFactor());
        assertEquals(0.5, factor.getImpact());
        assertEquals("Test description", factor.getDescription());
    }

    @Test
    void testDriverCandidate_Creation() {
        // Test DriverCandidate data class
        SmartInsightsService.DriverCandidate candidate = 
                new SmartInsightsService.DriverCandidate(
                        1L, 
                        "Test Driver", 
                        0.8, 
                        Arrays.asList("Good performance"),
                        Arrays.asList("High workload"),
                        "Recommended with considerations"
                );

        assertEquals(1L, candidate.getDriverId());
        assertEquals("Test Driver", candidate.getDriverName());
        assertEquals(0.8, candidate.getScore());
        assertEquals(1, candidate.getStrengths().size());
        assertEquals(1, candidate.getWeaknesses().size());
        assertEquals("Recommended with considerations", candidate.getJustification());
    }

    @Test
    void testInefficientRoute_Creation() {
        // Test InefficientRoute data class
        SmartInsightsService.InefficientRoute route = 
                new SmartInsightsService.InefficientRoute(
                        "Riyadh -> Jeddah",
                        10,
                        0.6,
                        45.0,
                        0.8,
                        Arrays.asList("Low average speed"),
                        "Optimize routing"
                );

        assertEquals("Riyadh -> Jeddah", route.getRoute());
        assertEquals(10, route.getShipmentCount());
        assertEquals(0.6, route.getEfficiencyScore());
        assertEquals(45.0, route.getAverageSpeed());
        assertEquals(0.8, route.getOnTimeRate());
        assertEquals(1, route.getIssues().size());
        assertEquals("Optimize routing", route.getRecommendation());
    }

    @Test
    void testBottleneck_Creation() {
        // Test Bottleneck data class
        SmartInsightsService.Bottleneck bottleneck = 
                new SmartInsightsService.Bottleneck(
                        "TEST_BOTTLENECK",
                        "Test bottleneck",
                        0.7,
                        "Test details",
                        "Test recommendation"
                );

        assertEquals("TEST_BOTTLENECK", bottleneck.getType());
        assertEquals("Test bottleneck", bottleneck.getDescription());
        assertEquals(0.7, bottleneck.getSeverity());
        assertEquals("Test details", bottleneck.getDetails());
        assertEquals("Test recommendation", bottleneck.getRecommendation());
    }

    @Test
    void testSystemBottlenecks_Creation() {
        // Test SystemBottlenecks data class
        List<SmartInsightsService.Bottleneck> bottlenecks = Arrays.asList(
                new SmartInsightsService.Bottleneck("TEST1", "Test 1", 0.5, "Details 1", "Rec 1"),
                new SmartInsightsService.Bottleneck("TEST2", "Test 2", 0.3, "Details 2", "Rec 2")
        );

        SmartInsightsService.SystemBottlenecks systemBottlenecks = 
                new SmartInsightsService.SystemBottlenecks(bottlenecks, 0.6, LocalDateTime.now());

        assertEquals(2, systemBottlenecks.getBottlenecks().size());
        assertEquals(0.6, systemBottlenecks.getSystemHealthScore());
        assertNotNull(systemBottlenecks.getAnalyzedAt());
    }

    @Test
    void testPredictDelayedShipment_EdgeCases() {
        // Test with null driver ID
        testShipment.setDriverId(null);
        when(shipmentRepository.findByOriginAndDestination(anyString(), anyString()))
                .thenReturn(Arrays.asList());

        SmartInsightsService.DelayPrediction prediction = smartInsightsService.predictDelayedShipment(testShipment);

        assertNotNull(prediction);
        assertEquals("TEST001", prediction.getTrackingNumber());

        // Test with null vehicle ID
        testShipment.setDriverId(1L);
        testShipment.setVehicleId(null);

        prediction = smartInsightsService.predictDelayedShipment(testShipment);

        assertNotNull(prediction);
        assertEquals("TEST001", prediction.getTrackingNumber());
    }

    @Test
    void testSuggestDriverAllocation_MultipleCandidates() {
        // Given
        User driver2 = new User();
        driver2.setId(2L);
        driver2.setName("Driver 2");
        driver2.setRole(UserRole.DRIVER);
        driver2.setStatus("ACTIVE");
        driver2.setVehicleId(2L);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setId(2L);
        vehicle2.setStatus(VehicleStatus.AVAILABLE);

        List<User> availableDrivers = Arrays.asList(testDriver, driver2);
        when(userRepository.findByRoleAndStatus(UserRole.DRIVER, "ACTIVE"))
                .thenReturn(availableDrivers);
        when(shipmentRepository.findByDriverId(anyLong()))
                .thenReturn(Arrays.asList(testShipment));
        when(vehicleRepository.findById(anyLong()))
                .thenReturn(Optional.of(testVehicle));

        // When
        SmartInsightsService.DriverAllocationSuggestion suggestion = 
                smartInsightsService.suggestDriverAllocation(testShipment);

        // Then
        assertNotNull(suggestion);
        assertEquals(2, suggestion.getCandidates().size());
        
        // Verify candidates are sorted by score (highest first)
        assertTrue(suggestion.getCandidates().get(0).getScore() >= suggestion.getCandidates().get(1).getScore());
    }

    // Helper methods
    private List<Shipment> createHistoricalShipments(int count, boolean delayed) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> {
                    Shipment shipment = new Shipment();
                    shipment.setId((long) i);
                    shipment.setTrackingNumber("HIST" + i);
                    shipment.setOrigin("Riyadh");
                    shipment.setDestination("Jeddah");
                    shipment.setCreatedAt(LocalDateTime.now().minusDays(i));
                    
                    if (delayed) {
                        shipment.setExpectedDeliveryTime(LocalDateTime.now().minusDays(i).plusHours(12));
                        shipment.setActualDeliveryTime(LocalDateTime.now().minusDays(i).plusHours(15));
                    } else {
                        shipment.setExpectedDeliveryTime(LocalDateTime.now().minusDays(i).plusHours(12));
                        shipment.setActualDeliveryTime(LocalDateTime.now().minusDays(i).plusHours(10));
                    }
                    
                    return shipment;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    private List<Shipment> createCompletedShipments(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> {
                    Shipment shipment = new Shipment();
                    shipment.setId((long) i);
                    shipment.setTrackingNumber("COMP" + i);
                    shipment.setOrigin("City" + (i % 5));
                    shipment.setDestination("City" + ((i + 1) % 5));
                    shipment.setDistance(100.0 + (i * 10));
                    shipment.setStatus(ShipmentStatus.DELIVERED);
                    shipment.setCreatedAt(LocalDateTime.now().minusDays(i));
                    shipment.setExpectedDeliveryTime(LocalDateTime.now().minusDays(i).plusHours(24));
                    shipment.setActualDeliveryTime(LocalDateTime.now().minusDays(i).plusHours(20 + (i % 10)));
                    return shipment;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    private void setupBottleneckTestData() {
        // Setup default repository responses for bottleneck testing
        when(userRepository.countByRole(UserRole.DRIVER)).thenReturn(20L);
        when(userRepository.countByRoleAndStatus(UserRole.DRIVER, "ACTIVE")).thenReturn(15L);
        when(shipmentRepository.countByStatusIn(any())).thenReturn(10L);
        when(vehicleRepository.count()).thenReturn(30L);
        when(vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE)).thenReturn(2L);
        when(vehicleRepository.countByStatus(VehicleStatus.ISSUE_REPORTED)).thenReturn(1L);
        when(shipmentRepository.findAll()).thenReturn(createCompletedShipments(50));
        when(invoiceRepository.findByStatus(InvoiceStatus.UNPAID)).thenReturn(Arrays.asList());
        when(emergencyRepository.findByCreatedAtAfter(any())).thenReturn(Arrays.asList());
    }
}
