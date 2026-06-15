package com.edham.logistics;

import com.edham.logistics.dto.ShipmentRequest;
import com.edham.logistics.dto.ShipmentResponse;
import com.edham.logistics.model.User;
import com.edham.logistics.model.UserRole;
import com.edham.logistics.repository.UserRepository;
import com.edham.logistics.service.ShipmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FlowIntegrationTest {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFullOrderFlow() {
        // 1. Setup Customer
        User customer = User.builder()
                .username("test_cust")
                .email("test@edham.co")
                .firstName("Test")
                .lastName("Customer")
                .role(UserRole.CUSTOMER)
                .active(true)
                .build();
        customer = userRepository.save(customer);

        // 2. Create Shipment Request (Simulating Android Wizard)
        ShipmentRequest request = new ShipmentRequest();
        request.setCustomerId(customer.getId());
        request.setOrigin("Riyadh");
        request.setDestination("Jeddah");
        request.setWeight(10.5);
        request.setPackageDescription("Frozen Goods");

        ShipmentResponse response = shipmentService.createShipment(request, null);

        // 3. Verify Result
        assertNotNull(response.getId());
        assertEquals("PENDING", response.getStatus());
        assertEquals("Riyadh", response.getOrigin());
        System.out.println("✅ Full Order Flow Verified: Shipment created with ID: " + response.getId());
    }
}
