// package com.edham.logistics.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller for Payment Lifecycle Management
 */
@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*")
public class PaymentLifecycleController {

    @Autowired
    private PaymentLifecycleService paymentLifecycleService;

    /**
     * Create new payment transaction
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CUSTOMER_SERVICE')")
    public ResponseEntity<PaymentLifecycleService.PaymentTransaction> createPayment(
            @RequestBody PaymentLifecycleService.PaymentTransaction payment) {
        try {
            PaymentLifecycleService.PaymentTransaction createdPayment = 
                paymentLifecycleService.createPayment(payment);
            return ResponseEntity.ok(createdPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CUSTOMER_SERVICE', 'CUSTOMER')")
    public ResponseEntity<PaymentLifecycleService.PaymentTransaction> getPayment(
            @PathVariable String paymentId) {
        try {
            PaymentLifecycleService.PaymentTransaction payment = 
                paymentLifecycleService.getPayment(paymentId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Process payment transaction
     */
    @PostMapping("/{paymentId}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CUSTOMER_SERVICE')")
    public ResponseEntity<PaymentLifecycleService.PaymentTransaction> processPayment(
            @PathVariable String paymentId,
            @RequestBody Map<String, Object> request) {
        try {
            Double amount = (Double) request.get("amount");
            String methodStr = (String) request.get("method");
            String transactionReference = (String) request.get("transactionReference");

            PaymentLifecycleService.PaymentMethod method = 
                PaymentLifecycleService.PaymentMethod.valueOf(methodStr.toUpperCase());

            PaymentLifecycleService.PaymentTransaction processedPayment = 
                paymentLifecycleService.processPayment(paymentId, amount, method, transactionReference);
            
            return ResponseEntity.ok(processedPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Process refund
     */
    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<PaymentLifecycleService.Refund> processRefund(
            @PathVariable String paymentId,
            @RequestBody Map<String, Object> request) {
        try {
            Double amount = (Double) request.get("amount");
            String reason = (String) request.get("reason");
            String processedBy = (String) request.get("processedBy");

            PaymentLifecycleService.Refund refund = 
                paymentLifecycleService.processRefund(paymentId, amount, reason, processedBy);
            
            return ResponseEntity.ok(refund);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Approve payment (Accountant only)
     */
    @PostMapping("/{paymentId}/approve")
    @PreAuthorize("hasRole('ACCOUNTANT')")
    public ResponseEntity<PaymentLifecycleService.PaymentTransaction> approvePayment(
            @PathVariable String paymentId,
            @RequestBody Map<String, String> request) {
        try {
            String approverId = request.get("approverId");
            String comments = request.get("comments");

            PaymentLifecycleService.PaymentTransaction approvedPayment = 
                paymentLifecycleService.approvePayment(paymentId, approverId, comments);
            
            return ResponseEntity.ok(approvedPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Reject payment (Accountant only)
     */
    @PostMapping("/{paymentId}/reject")
    @PreAuthorize("hasRole('ACCOUNTANT')")
    public ResponseEntity<PaymentLifecycleService.PaymentTransaction> rejectPayment(
            @PathVariable String paymentId,
            @RequestBody Map<String, String> request) {
        try {
            String approverId = request.get("approverId");
            String rejectionReason = request.get("rejectionReason");

            PaymentLifecycleService.PaymentTransaction rejectedPayment = 
                paymentLifecycleService.rejectPayment(paymentId, approverId, rejectionReason);
            
            return ResponseEntity.ok(rejectedPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Reconcile payment with invoice
     */
    @PostMapping("/{paymentId}/reconcile")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<PaymentLifecycleService.PaymentTransaction> reconcilePayment(
            @PathVariable String paymentId,
            @RequestBody Map<String, String> request) {
        try {
            String reconciledBy = request.get("reconciledBy");

            PaymentLifecycleService.PaymentTransaction reconciledPayment = 
                paymentLifecycleService.reconcilePayment(paymentId, reconciledBy);
            
            return ResponseEntity.ok(reconciledPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cancel payment
     */
    @PostMapping("/{paymentId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CUSTOMER_SERVICE')")
    public ResponseEntity<PaymentLifecycleService.PaymentTransaction> cancelPayment(
            @PathVariable String paymentId,
            @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");

            PaymentLifecycleService.PaymentTransaction cancelledPayment = 
                paymentLifecycleService.cancelPayment(paymentId, reason);
            
            return ResponseEntity.ok(cancelledPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update payment due date
     */
    @PutMapping("/{paymentId}/due-date")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CUSTOMER_SERVICE')")
    public ResponseEntity<PaymentLifecycleService.PaymentTransaction> updateDueDate(
            @PathVariable String paymentId,
            @RequestBody Map<String, Object> request) {
        try {
            String dueDateStr = (String) request.get("dueDate");
            LocalDateTime newDueDate = LocalDateTime.parse(dueDateStr);

            PaymentLifecycleService.PaymentTransaction updatedPayment = 
                paymentLifecycleService.updateDueDate(paymentId, newDueDate);
            
            return ResponseEntity.ok(updatedPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all payments for customer
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CUSTOMER_SERVICE', 'CUSTOMER')")
    public ResponseEntity<List<PaymentLifecycleService.PaymentTransaction>> getCustomerPayments(
            @PathVariable String customerId) {
        try {
            List<PaymentLifecycleService.PaymentTransaction> payments = 
                paymentLifecycleService.getCustomerPayments(customerId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get payments pending approval
     */
    @GetMapping("/pending-approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<List<PaymentLifecycleService.PaymentTransaction>> getPaymentsPendingApproval() {
        try {
            List<PaymentLifecycleService.PaymentTransaction> payments = 
                paymentLifecycleService.getPaymentsPendingApproval();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get overdue payments
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CUSTOMER_SERVICE')")
    public ResponseEntity<List<PaymentLifecycleService.PaymentTransaction>> getOverduePayments() {
        try {
            List<PaymentLifecycleService.PaymentTransaction> payments = 
                paymentLifecycleService.getOverduePayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get payment statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Map<String, Object>> getPaymentStatistics() {
        try {
            Map<String, Object> statistics = paymentLifecycleService.getPaymentStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get payment status information
     */
    @GetMapping("/statuses")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CUSTOMER_SERVICE', 'CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getPaymentStatuses() {
        Map<String, Object> statuses = Map.of(
            "PENDING", Map.of(
                "name", "قيد الانتظار",
                "color", "#F59E0B",
                "description", "تم إنشاء الدفع ولكن لم تتم معالجته بعد"
            ),
            "PROCESSING", Map.of(
                "name", "قيد المعالجة",
                "color", "#3B82F6",
                "description", "الدفع قيد المعالجة حالياً"
            ),
            "COMPLETED", Map.of(
                "name", "مكتمل",
                "color", "#10B981",
                "description", "تم الدفع بنجاح"
            ),
            "PARTIALLY_PAID", Map.of(
                "name", "مدفوع جزئياً",
                "color", "#8B5CF6",
                "description", "تم دفع جزء من المبلغ المطلوب"
            ),
            "FAILED", Map.of(
                "name", "فشل",
                "color", "#EF4444",
                "description", "فشلت عملية الدفع"
            ),
            "REFUNDED", Map.of(
                "name", "مسترد",
                "color", "#6B7280",
                "description", "تم استرداد الدفع"
            ),
            "CANCELLED", Map.of(
                "name", "ملغي",
                "color", "#DC2626",
                "description", "تم إلغاء الدفع"
            ),
            "AWAITING_APPROVAL", Map.of(
                "name", "في انتظار الموافقة",
                "color", "#F97316",
                "description", "الدفع في انتظار موافقة المحاسب"
            ),
            "APPROVED", Map.of(
                "name", "موافق عليه",
                "color", "#10B981",
                "description", "تمت الموافقة على الدفع من قبل المحاسب"
            ),
            "REJECTED", Map.of(
                "name", "مرفوض",
                "color", "#EF4444",
                "description", "تم رفض الدفع من قبل المحاسب"
            )
        );

        return ResponseEntity.ok(statuses);
    }

    /**
     * Get payment methods information
     */
    @GetMapping("/methods")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CUSTOMER_SERVICE', 'CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getPaymentMethods() {
        Map<String, Object> methods = Map.of(
            "CASH", Map.of(
                "name", "نقدي",
                "description", "الدفع النقدي المباشر"
            ),
            "CREDIT_CARD", Map.of(
                "name", "بطاقة ائتمان",
                "description", "الدفع باستخدام بطاقة ائتمان"
            ),
            "BANK_TRANSFER", Map.of(
                "name", "تحويل بنكي",
                "description", "التحويل البنكي المباشر"
            ),
            "DIGITAL_WALLET", Map.of(
                "name", "محفظة رقمية",
                "description", "الدفع باستخدام المحافظ الرقمية"
            ),
            "CHECK", Map.of(
                "name", "شيك",
                "description", "الدفع بالشيك"
            ),
            "CRYPTOCURRENCY", Map.of(
                "name", "عملة مشفرة",
                "description", "الدفع باستخدام العملات المشفرة"
            )
        );

        return ResponseEntity.ok(methods);
    }

    /**
     * Get dashboard summary
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        try {
            Map<String, Object> statistics = paymentLifecycleService.getPaymentStatistics();
            List<PaymentLifecycleService.PaymentTransaction> pendingApproval = 
                paymentLifecycleService.getPaymentsPendingApproval();
            List<PaymentLifecycleService.PaymentTransaction> overdue = 
                paymentLifecycleService.getOverduePayments();

            // Recent payments (last 10)
            List<PaymentLifecycleService.PaymentTransaction> recentPayments = 
                paymentLifecycleService.getCustomerPayments("").stream() // This would need implementation
                    .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                    .limit(10)
                    .toList();

            Map<String, Object> dashboard = Map.of(
                "statistics", statistics,
                "pendingApproval", pendingApproval,
                "overdue", overdue,
                "recentPayments", recentPayments,
                "lastUpdated", LocalDateTime.now().toString()
            );

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get system health status
     */
    @GetMapping("/health")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        try {
            Map<String, Object> statistics = paymentLifecycleService.getPaymentStatistics();
            
            // Calculate health metrics
            Long totalPayments = (Long) statistics.get("totalPayments");
            Long overdueCount = (Long) statistics.get("overdueCount");
            Long pendingApprovalCount = (Long) statistics.get("pendingApprovalCount");
            
            String healthStatus;
            if (overdueCount > 0) {
                healthStatus = "WARNING";
            } else if (pendingApprovalCount > 10) {
                healthStatus = "NORMAL";
            } else {
                healthStatus = "HEALTHY";
            }

            Map<String, Object> health = Map.of(
                "status", healthStatus,
                "totalPayments", totalPayments,
                "overduePayments", overdueCount,
                "pendingApproval", pendingApprovalCount,
                "lastUpdated", LocalDateTime.now().toString()
            );

            return ResponseEntity.ok(health);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
