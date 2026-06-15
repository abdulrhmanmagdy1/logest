package com.edham.logistics.controller;

import com.edham.logistics.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer")
@CrossOrigin(origins = "*")
public class CustomerController {

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", 3);
        stats.put("active", 1);
        stats.put("completed", 12);
        stats.put("wallet_balance", 4821.0);

        return ResponseEntity.ok(new ApiResponse<>(true, "Customer stats retrieved", stats));
    }

    @PostMapping("/recharge-wallet")
    public ResponseEntity<ApiResponse<Double>> rechargeWallet(@RequestParam Double amount) {
        // Mock recharge logic
        return ResponseEntity.ok(new ApiResponse<>(true, "Wallet recharged successfully", amount));
    }
}
