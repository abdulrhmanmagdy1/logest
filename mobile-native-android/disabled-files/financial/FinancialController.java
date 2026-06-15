package com.edham.logistics.financial;

import com.edham.logistics.dto.UnifiedResponseDTO;
import com.edham.logistics.model.*;
import com.edham.logistics.financial.FinancialService.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Financial controller with accountant-only access
 * Provides invoice generation, payment tracking, and financial management
 */
@RestController
@RequestMapping("/api/v1/financial")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class FinancialController {

    private final FinancialService financialService;
    private final UserRepository userRepository;

    @Autowired
    public FinancialController(FinancialService financialService,
                           UserRepository userRepository) {
        this.financialService = financialService;
        this.userRepository = userRepository;
    }

    /**
     * Generate invoice for shipment
     */
    @PostMapping("/invoices/generate/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Invoice>>> generateInvoice(
            @PathVariable Long shipmentId,
            @RequestBody Map<String, String> request) {
        
        String createdBy = request.getOrDefault("createdBy", getCurrentUserId().toString());
        
        return financialService.generateInvoice(shipmentId, createdBy)
                .thenApply(invoice -> {
                    log.info("Invoice generated successfully: {} for shipment: {}", invoice.getInvoiceNumber(), shipmentId);
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<Invoice>builder()
                                    .success(true)
                                    .data(invoice)
                                    .message("Invoice generated successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error generating invoice for shipment {}: {}", shipmentId, throwable.getMessage(), throwable);
                    return ResponseEntity.badRequest().body(
                            UnifiedResponseDTO.<Invoice>builder()
                                    .success(false)
                                    .error("Failed to generate invoice: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get invoice by ID
     */
    @GetMapping("/invoices/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CLIENT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Invoice>>> getInvoice(
            @PathVariable String invoiceId) {
        
        return financialService.getInvoice(invoiceId)
                .thenApply(invoiceOpt -> {
                    if (invoiceOpt.isPresent()) {
                        Invoice invoice = invoiceOpt.get();
                        
                        // Check access permissions
                        if (!canAccessInvoice(invoice)) {
                            return ResponseEntity.status(403).body(
                                    UnifiedResponseDTO.<Invoice>builder()
                                            .success(false)
                                            .error("Access denied for invoice: " + invoiceId)
                                            .timestamp(LocalDateTime.now())
                                            .build()
                            );
                        }
                        
                        log.debug("Invoice retrieved: {}", invoiceId);
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<Invoice>builder()
                                        .success(true)
                                        .data(invoice)
                                        .message("Invoice retrieved successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error getting invoice {}: {}", invoiceId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<Invoice>builder()
                                    .success(false)
                                    .error("Failed to get invoice: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get invoices for customer
     */
    @GetMapping("/invoices/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CLIENT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Page<Invoice>>>> getCustomerInvoices(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "invoiceDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        // Check access permissions
        Long currentUserId = getCurrentUserId();
        UserRole currentUserRole = getCurrentUserRole();
        
        if (!currentUserRole.equals(UserRole.ADMIN) && !currentUserRole.equals(UserRole.ACCOUNTANT) && !currentUserId.equals(customerId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<Page<Invoice>>builder()
                                    .success(false)
                                    .error("Access denied: Cannot view other customer's invoices")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        return financialService.getCustomerInvoices(customerId, pageable)
                .thenApply(invoices -> {
                    log.debug("Retrieved {} invoices for customer: {}", invoices.getTotalElements(), customerId);
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<Page<Invoice>>builder()
                                    .success(true)
                                    .data(invoices)
                                    .message("Customer invoices retrieved successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error getting customer invoices for {}: {}", customerId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<Page<Invoice>>builder()
                                    .success(false)
                                    .error("Failed to get customer invoices: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get all invoices (accountant/admin only)
     */
    @GetMapping("/invoices/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Page<Invoice>>>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "invoiceDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) InvoiceStatus status) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        return financialService.getAllInvoices(pageable)
                .thenApply(invoices -> {
                    log.debug("Retrieved {} all invoices", invoices.getTotalElements());
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<Page<Invoice>>builder()
                                    .success(true)
                                    .data(invoices)
                                    .message("All invoices retrieved successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error getting all invoices: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<Page<Invoice>>builder()
                                    .success(false)
                                    .error("Failed to get all invoices: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Process payment
     */
    @PostMapping("/payments/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Payment>>> processPayment(
            @RequestBody PaymentRequest request) {
        
        // Set processed by if not provided
        if (request.getProcessedBy() == null) {
            request.setProcessedBy(getCurrentUserId().toString());
        }
        
        return financialService.processPayment(request)
                .thenApply(payment -> {
                    log.info("Payment processed successfully: {} for invoice: {}", payment.getPaymentNumber(), request.getInvoiceId());
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<Payment>builder()
                                    .success(true)
                                    .data(payment)
                                    .message("Payment processed successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error processing payment for invoice {}: {}", request.getInvoiceId(), throwable.getMessage(), throwable);
                    return ResponseEntity.badRequest().body(
                            UnifiedResponseDTO.<Payment>builder()
                                    .success(false)
                                    .error("Failed to process payment: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get payments for invoice
     */
    @GetMapping("/payments/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CLIENT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<Payment>>>> getInvoicePayments(
            @PathVariable String invoiceId) {
        
        // Check access permissions
        return financialService.getInvoice(invoiceId)
                .thenCompose(invoiceOpt -> {
                    if (invoiceOpt.isPresent()) {
                        Invoice invoice = invoiceOpt.get();
                        
                        if (!canAccessInvoice(invoice)) {
                            return CompletableFuture.completedFuture(
                                    ResponseEntity.status(403).body(
                                            UnifiedResponseDTO.<List<Payment>>builder()
                                                    .success(false)
                                                    .error("Access denied for invoice: " + invoiceId)
                                                    .timestamp(LocalDateTime.now())
                                                    .build()
                                    )
                            );
                        }
                        
                        return financialService.getInvoicePayments(invoiceId)
                                .thenApply(payments -> {
                                    log.debug("Retrieved {} payments for invoice: {}", payments.size(), invoiceId);
                                    return ResponseEntity.ok(
                                            UnifiedResponseDTO.<List<Payment>>builder()
                                                    .success(true)
                                                    .data(payments)
                                                    .message("Invoice payments retrieved successfully")
                                                    .timestamp(LocalDateTime.now())
                                                    .build()
                                    );
                                });
                    } else {
                        return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error getting payments for invoice {}: {}", invoiceId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<List<Payment>>builder()
                                    .success(false)
                                    .error("Failed to get payments: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get outstanding balances
     */
    @GetMapping("/balances/outstanding")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<OutstandingBalance>>>> getOutstandingBalances() {
        
        return financialService.getOutstandingBalances()
                .thenApply(balances -> {
                    log.debug("Retrieved {} outstanding balances", balances.size());
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<List<OutstandingBalance>>builder()
                                    .success(true)
                                    .data(balances)
                                    .message("Outstanding balances retrieved successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error getting outstanding balances: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<List<OutstandingBalance>>builder()
                                    .success(false)
                                    .error("Failed to get outstanding balances: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get customer outstanding balance
     */
    @GetMapping("/balances/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CLIENT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<OutstandingBalance>>> getCustomerBalance(
            @PathVariable Long customerId) {
        
        // Check access permissions
        Long currentUserId = getCurrentUserId();
        UserRole currentUserRole = getCurrentUserRole();
        
        if (!currentUserRole.equals(UserRole.ADMIN) && !currentUserRole.equals(UserRole.ACCOUNTANT) && !currentUserId.equals(customerId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<OutstandingBalance>builder()
                                    .success(false)
                                    .error("Access denied: Cannot view other customer's balance")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return financialService.getOutstandingBalances()
                .thenApply(balances -> {
                    Optional<OutstandingBalance> customerBalance = balances.stream()
                            .filter(balance -> balance.getCustomerId().equals(customerId))
                            .findFirst();
                    
                    if (customerBalance.isPresent()) {
                        log.debug("Retrieved balance for customer: {}", customerId);
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<OutstandingBalance>builder()
                                        .success(true)
                                        .data(customerBalance.get())
                                        .message("Customer balance retrieved successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<OutstandingBalance>builder()
                                        .success(true)
                                        .data(OutstandingBalance.builder()
                                                .customerId(customerId)
                                                .customerName(getCustomerName(customerId))
                                                .totalInvoices(0)
                                                .totalAmount(java.math.BigDecimal.ZERO)
                                                .paidAmount(java.math.BigDecimal.ZERO)
                                                .outstandingBalance(java.math.BigDecimal.ZERO)
                                                .overdueAmount(java.math.BigDecimal.ZERO)
                                                .invoices(new ArrayList<>())
                                                .build())
                                        .message("No outstanding balance found")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error getting customer balance for {}: {}", customerId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<OutstandingBalance>builder()
                                    .success(false)
                                    .error("Failed to get customer balance: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Generate invoice PDF
     */
    @GetMapping("/invoices/{invoiceId}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'CLIENT')")
    public CompletableFuture<ResponseEntity<ByteArrayResource>> generateInvoicePDF(
            @PathVariable String invoiceId) {
        
        // Check access permissions
        return financialService.getInvoice(invoiceId)
                .thenCompose(invoiceOpt -> {
                    if (invoiceOpt.isPresent()) {
                        Invoice invoice = invoiceOpt.get();
                        
                        if (!canAccessInvoice(invoice)) {
                            return CompletableFuture.completedFuture(
                                    ResponseEntity.status(403).build()
                            );
                        }
                        
                        return financialService.generateInvoicePDF(invoiceId)
                                .thenApply(pdfBytes -> {
                                    log.debug("Generated PDF for invoice: {}", invoiceId);
                                    
                                    ByteArrayResource resource = new ByteArrayResource(pdfBytes);
                                    
                                    return ResponseEntity.ok()
                                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + invoiceId + ".pdf")
                                            .contentType(MediaType.APPLICATION_PDF)
                                            .contentLength(pdfBytes.length)
                                            .body(resource);
                                });
                    } else {
                        return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error generating PDF for invoice {}: {}", invoiceId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().build();
                });
    }

    /**
     * Get financial summary
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<FinancialSummary>>> getFinancialSummary(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        // Default to last 30 days if not provided
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        return financialService.getFinancialSummary(startDate, endDate)
                .thenApply(summary -> {
                    if (summary != null) {
                        log.debug("Retrieved financial summary for period: {} to {}", startDate, endDate);
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<FinancialSummary>builder()
                                        .success(true)
                                        .data(summary)
                                        .message("Financial summary retrieved successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<FinancialSummary>builder()
                                        .success(true)
                                        .data(null)
                                        .message("No financial data available")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error getting financial summary: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<FinancialSummary>builder()
                                    .success(false)
                                    .error("Failed to get financial summary: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Apply late fees to overdue invoices
     */
    @PostMapping("/invoices/apply-late-fees")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Integer>>> applyLateFees() {
        
        return financialService.applyLateFees()
                .thenApply(updatedCount -> {
                    log.info("Applied late fees to {} overdue invoices", updatedCount);
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<Integer>builder()
                                    .success(true)
                                    .data(updatedCount)
                                    .message("Late fees applied successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error applying late fees: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<Integer>builder()
                                    .success(false)
                                    .error("Failed to apply late fees: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get invoice statistics
     */
    @GetMapping("/invoices/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Map<String, Object>>>> getInvoiceStatistics() {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> statistics = new HashMap<>();
                
                // Get recent invoices (last 30 days)
                LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
                List<Invoice> recentInvoices = financialService.getAllInvoices(
                        PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, "invoiceDate")))
                        .join()
                        .getContent();
                
                // Calculate statistics
                long totalInvoices = recentInvoices.size();
                long draftCount = recentInvoices.stream().filter(i -> i.getStatus() == InvoiceStatus.DRAFT).count();
                long sentCount = recentInvoices.stream().filter(i -> i.getStatus() == InvoiceStatus.SENT).count();
                long paidCount = recentInvoices.stream().filter(i -> i.getStatus() == InvoiceStatus.PAID).count();
                long overdueCount = recentInvoices.stream().filter(i -> i.getStatus() == InvoiceStatus.OVERDUE).count();
                
                java.math.BigDecimal totalAmount = recentInvoices.stream()
                        .filter(i -> i.getStatus() != InvoiceStatus.DRAFT)
                        .map(Invoice::getTotalAmount)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                
                java.math.BigDecimal totalPaid = recentInvoices.stream()
                        .map(Invoice::getPaidAmount)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                
                java.math.BigDecimal totalOutstanding = recentInvoices.stream()
                        .map(Invoice::getOutstandingBalance)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                
                statistics.put("totalInvoices", totalInvoices);
                statistics.put("draftInvoices", draftCount);
                statistics.put("sentInvoices", sentCount);
                statistics.put("paidInvoices", paidCount);
                statistics.put("overdueInvoices", overdueCount);
                statistics.put("totalAmount", totalAmount);
                statistics.put("totalPaid", totalPaid);
                statistics.put("totalOutstanding", totalOutstanding);
                statistics.put("collectionRate", totalAmount.compareTo(java.math.BigDecimal.ZERO) > 0 ? 
                        totalPaid.divide(totalAmount, 4, java.math.BigDecimal.ROUND_HALF_UP).multiply(java.math.BigDecimal.valueOf(100)) : 
                        java.math.BigDecimal.ZERO);
                statistics.put("period", "Last 30 days");
                statistics.put("generatedAt", LocalDateTime.now());
                
                log.debug("Retrieved invoice statistics: {}", statistics);
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(true)
                                .data(statistics)
                                .message("Invoice statistics retrieved successfully")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error getting invoice statistics: {}", e.getMessage(), e);
                return ResponseEntity.internalServerError().body(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(false)
                                .error("Failed to get invoice statistics: " + e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        });
    }

    // Helper methods
    private Long getCurrentUserId() {
        // Implementation to get current user ID from security context
        return 1L; // Placeholder
    }

    private UserRole getCurrentUserRole() {
        // Implementation to get current user role from security context
        return UserRole.CLIENT; // Placeholder
    }

    private boolean canAccessInvoice(Invoice invoice) {
        Long currentUserId = getCurrentUserId();
        UserRole currentUserRole = getCurrentUserRole();
        
        // Admin and Accountant can access all invoices
        if (currentUserRole.equals(UserRole.ADMIN) || currentUserRole.equals(UserRole.ACCOUNTANT)) {
            return true;
        }
        
        // Client can only access their own invoices
        if (currentUserRole.equals(UserRole.CLIENT) && currentUserId.equals(invoice.getCustomerId())) {
            return true;
        }
        
        return false;
    }

    private String getCustomerName(Long customerId) {
        return userRepository.findById(customerId)
                .map(User::getName)
                .orElse("Unknown Customer");
    }
}
