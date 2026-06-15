package com.edham.logistics.controller;

import com.edham.logistics.dto.InvoiceRequest;
import com.edham.logistics.dto.InvoiceResponse;
import com.edham.logistics.dto.InvoiceUpdateRequest;
import com.edham.logistics.service.InvoiceService;
import com.edham.logistics.util.ApiResponse;
import com.edham.logistics.util.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Invoice Controller
 * Handles invoice and billing operations
 */
@RestController
@RequestMapping("/api/v1/invoices")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    /**
     * Get all invoices (paginated)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PaginatedResponse<InvoiceResponse>>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String dateRange) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<InvoiceResponse> response = invoiceService.getAllInvoices(
                pageable, status, customerId, dateRange);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<InvoiceResponse>>builder()
                    .success(true)
                    .message("Invoices retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<InvoiceResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve invoices: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get invoice by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable Long id) {
        try {
            InvoiceResponse response = invoiceService.getInvoiceById(id);
            
            return ResponseEntity.ok(
                ApiResponse.<InvoiceResponse>builder()
                    .success(true)
                    .message("Invoice retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<InvoiceResponse>builder()
                    .success(false)
                    .message("Invoice not found: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Create new invoice
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceRequest invoiceRequest,
            HttpServletRequest request) {
        
        try {
            InvoiceResponse response = invoiceService.createInvoice(invoiceRequest, request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<InvoiceResponse>builder()
                    .success(true)
                    .message("Invoice created successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<InvoiceResponse>builder()
                    .success(false)
                    .message("Failed to create invoice: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Update invoice
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceUpdateRequest updateRequest,
            HttpServletRequest request) {
        
        try {
            InvoiceResponse response = invoiceService.updateInvoice(id, updateRequest, request);
            
            return ResponseEntity.ok(
                ApiResponse.<InvoiceResponse>builder()
                    .success(true)
                    .message("Invoice updated successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<InvoiceResponse>builder()
                    .success(false)
                    .message("Failed to update invoice: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Delete invoice
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(@PathVariable Long id) {
        try {
            invoiceService.deleteInvoice(id);
            
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .message("Invoice deleted successfully")
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete invoice: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get customer invoices
     */
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<InvoiceResponse>>> getCustomerInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<InvoiceResponse> response = invoiceService.getCustomerInvoices(
                pageable, status);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<InvoiceResponse>>builder()
                    .success(true)
                    .message("Customer invoices retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<InvoiceResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve customer invoices: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Generate invoice PDF
     */
    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT', 'CUSTOMER')")
    public ResponseEntity<byte[]> generateInvoicePdf(@PathVariable Long id) {
        try {
            byte[] pdfData = invoiceService.generateInvoicePdf(id);
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice_" + id + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdfData);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Process payment
     */
    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> processPayment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> paymentData,
            HttpServletRequest request) {
        
        try {
            InvoiceResponse response = invoiceService.processPayment(id, paymentData, request);
            
            return ResponseEntity.ok(
                ApiResponse.<InvoiceResponse>builder()
                    .success(true)
                    .message("Payment processed successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<InvoiceResponse>builder()
                    .success(false)
                    .message("Payment processing failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get invoice statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<Object>> getInvoiceStats() {
        try {
            Object stats = invoiceService.getInvoiceStatistics();
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("Invoice statistics retrieved successfully")
                    .data(stats)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Failed to retrieve statistics: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get overdue invoices
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getOverdueInvoices() {
        try {
            List<InvoiceResponse> invoices = invoiceService.getOverdueInvoices();
            
            return ResponseEntity.ok(
                ApiResponse.<List<InvoiceResponse>>builder()
                    .success(true)
                    .message("Overdue invoices retrieved successfully")
                    .data(invoices)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<List<InvoiceResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve overdue invoices: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get pending invoices
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getPendingInvoices() {
        try {
            List<InvoiceResponse> invoices = invoiceService.getPendingInvoices();
            
            return ResponseEntity.ok(
                ApiResponse.<List<InvoiceResponse>>builder()
                    .success(true)
                    .message("Pending invoices retrieved successfully")
                    .data(invoices)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<List<InvoiceResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve pending invoices: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Search invoices
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<InvoiceResponse>>> searchInvoices(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<InvoiceResponse> response = invoiceService.searchInvoices(
                query, pageable, status);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<InvoiceResponse>>builder()
                    .success(true)
                    .message("Search completed successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<InvoiceResponse>>builder()
                    .success(false)
                    .message("Search failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Send invoice reminder
     */
    @PostMapping("/{id}/reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<Void>> sendInvoiceReminder(@PathVariable Long id) {
        try {
            invoiceService.sendInvoiceReminder(id);
            
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .message("Invoice reminder sent successfully")
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to send reminder: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Export invoices to CSV
     */
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<byte[]> exportInvoices(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateRange,
            @RequestParam(required = false) String format) {
        
        try {
            byte[] data = invoiceService.exportInvoices(status, dateRange, format);
            
            String filename = "invoices." + (format != null ? format : "csv");
            String contentType = format != null && format.equals("json") ? 
                "application/json" : "text/csv";
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Content-Type", contentType)
                .body(data);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Get payment history
     */
    @GetMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<List<Object>>> getPaymentHistory(@PathVariable Long id) {
        try {
            List<Object> payments = invoiceService.getPaymentHistory(id);
            
            return ResponseEntity.ok(
                ApiResponse.<List<Object>>builder()
                    .success(true)
                    .message("Payment history retrieved successfully")
                    .data(payments)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<List<Object>>builder()
                    .success(false)
                    .message("Payment history not found: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get revenue analytics
     */
    @GetMapping("/analytics/revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<Object>> getRevenueAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        try {
            Object analytics = invoiceService.getRevenueAnalytics(startDate, endDate);
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("Revenue analytics retrieved successfully")
                    .data(analytics)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Failed to retrieve revenue analytics: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Bulk invoice operations
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> bulkInvoiceOperations(
            @RequestBody Map<String, Object> bulkRequest,
            HttpServletRequest request) {
        
        try {
            String operation = (String) bulkRequest.get("operation");
            @SuppressWarnings("unchecked")
            List<Long> invoiceIds = (List<Long>) bulkRequest.get("invoiceIds");
            
            List<InvoiceResponse> response = invoiceService.bulkInvoiceOperations(
                operation, invoiceIds, request);
            
            return ResponseEntity.ok(
                ApiResponse.<List<InvoiceResponse>>builder()
                    .success(true)
                    .message("Bulk operation completed successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<List<InvoiceResponse>>builder()
                    .success(false)
                    .message("Bulk operation failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get invoice summary
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<Object>> getInvoiceSummary(
            @RequestParam(required = false) String period) {
        
        try {
            Object summary = invoiceService.getInvoiceSummary(period);
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("Invoice summary retrieved successfully")
                    .data(summary)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Failed to retrieve invoice summary: " + e.getMessage())
                    .build()
                );
        }
    }
}
