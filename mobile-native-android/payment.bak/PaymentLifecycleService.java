// // package com.edham.logistics.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Full Payment Lifecycle Management Service
 * Handles payment status tracking, partial payments, refunds, invoice reconciliation,
 * and accountant approval workflows
 */
@Service
@Transactional
public class PaymentLifecycleService {

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private AuditLogService auditLogService;

    /**
     * Payment Status Enum
     */
    public enum PaymentStatus {
        PENDING("قيد الانتظار", "#F59E0B"),
        PROCESSING("قيد المعالجة", "#3B82F6"),
        COMPLETED("مكتمل", "#10B981"),
        PARTIALLY_PAID("مدفوع جزئياً", "#8B5CF6"),
        FAILED("فشل", "#EF4444"),
        REFUNDED("مسترد", "#6B7280"),
        CANCELLED("ملغي", "#DC2626"),
        AWAITING_APPROVAL("في انتظار الموافقة", "#F97316"),
        APPROVED("م approved", "#10B981"),
        REJECTED("مرفوض", "#EF4444");

        private final String arabicName;
        private final String color;

        PaymentStatus(String arabicName, String color) {
            this.arabicName = arabicName;
            this.color = color;
        }

        public String getArabicName() { return arabicName; }
        public String getColor() { return color; }
    }

    /**
     * Payment Method Enum
     */
    public enum PaymentMethod {
        CASH("نقدي"),
        CREDIT_CARD("بطاقة ائتمان"),
        BANK_TRANSFER("تحويل بنكي"),
        DIGITAL_WALLET("محفظة رقمية"),
        CHECK("شيك"),
        CRYPTOCURRENCY("عملة مشفرة");

        private final String arabicName;

        PaymentMethod(String arabicName) {
            this.arabicName = arabicName;
        }

        public String getArabicName() { return arabicName; }
    }

    /**
     * Payment Transaction Data Class
     */
    public static class PaymentTransaction {
        private String paymentId;
        private String invoiceId;
        private String shipmentId;
        private String customerId;
        private Double amount;
        private Double paidAmount;
        private PaymentStatus status;
        private PaymentMethod method;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime dueDate;
        private String transactionReference;
        private String description;
        private List<PartialPayment> partialPayments;
        private List<Refund> refunds;
        private ApprovalInfo approvalInfo;
        private ReconciliationInfo reconciliationInfo;
        private Map<String, Object> metadata;

        // Constructors, getters, and setters
        public PaymentTransaction() {
            this.partialPayments = new ArrayList<>();
            this.refunds = new ArrayList<>();
            this.metadata = new HashMap<>();
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }

        // Getters and setters
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        
        public String getInvoiceId() { return invoiceId; }
        public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
        
        public String getShipmentId() { return shipmentId; }
        public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
        
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        
        public Double getPaidAmount() { return paidAmount; }
        public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }
        
        public PaymentStatus getStatus() { return status; }
        public void setStatus(PaymentStatus status) { this.status = status; }
        
        public PaymentMethod getMethod() { return method; }
        public void setMethod(PaymentMethod method) { this.method = method; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        
        public LocalDateTime getDueDate() { return dueDate; }
        public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
        
        public String getTransactionReference() { return transactionReference; }
        public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<PartialPayment> getPartialPayments() { return partialPayments; }
        public void setPartialPayments(List<PartialPayment> partialPayments) { this.partialPayments = partialPayments; }
        
        public List<Refund> getRefunds() { return refunds; }
        public void setRefunds(List<Refund> refunds) { this.refunds = refunds; }
        
        public ApprovalInfo getApprovalInfo() { return approvalInfo; }
        public void setApprovalInfo(ApprovalInfo approvalInfo) { this.approvalInfo = approvalInfo; }
        
        public ReconciliationInfo getReconciliationInfo() { return reconciliationInfo; }
        public void setReconciliationInfo(ReconciliationInfo reconciliationInfo) { this.reconciliationInfo = reconciliationInfo; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public Double getRemainingAmount() {
            return amount - (paidAmount != null ? paidAmount : 0.0);
        }
        
        public boolean isFullyPaid() {
            return paidAmount != null && paidAmount >= amount;
        }
        
        public boolean isOverdue() {
            return dueDate != null && LocalDateTime.now().isAfter(dueDate) && !isFullyPaid();
        }
    }

    /**
     * Partial Payment Data Class
     */
    public static class PartialPayment {
        private String partialPaymentId;
        private Double amount;
        private LocalDateTime paymentDate;
        private PaymentMethod method;
        private String transactionReference;
        private String description;

        public PartialPayment(Double amount, PaymentMethod method) {
            this.partialPaymentId = UUID.randomUUID().toString();
            this.amount = amount;
            this.method = method;
            this.paymentDate = LocalDateTime.now();
        }

        // Getters and setters
        public String getPartialPaymentId() { return partialPaymentId; }
        public Double getAmount() { return amount; }
        public LocalDateTime getPaymentDate() { return paymentDate; }
        public PaymentMethod getMethod() { return method; }
        public String getTransactionReference() { return transactionReference; }
        public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * Refund Data Class
     */
    public static class Refund {
        private String refundId;
        private Double amount;
        private String reason;
        private LocalDateTime refundDate;
        private RefundStatus status;
        private String processedBy;
        private LocalDateTime processedDate;
        private String transactionReference;

        public enum RefundStatus {
            PENDING("قيد الانتظار"),
            PROCESSING("قيد المعالجة"),
            COMPLETED("مكتمل"),
            FAILED("فشل"),
            CANCELLED("ملغي");

            private final String arabicName;

            RefundStatus(String arabicName) {
                this.arabicName = arabicName;
            }

            public String getArabicName() { return arabicName; }
        }

        public Refund(Double amount, String reason) {
            this.refundId = UUID.randomUUID().toString();
            this.amount = amount;
            this.reason = reason;
            this.status = RefundStatus.PENDING;
            this.refundDate = LocalDateTime.now();
        }

        // Getters and setters
        public String getRefundId() { return refundId; }
        public Double getAmount() { return amount; }
        public String getReason() { return reason; }
        public LocalDateTime getRefundDate() { return refundDate; }
        public RefundStatus getStatus() { return status; }
        public void setStatus(RefundStatus status) { this.status = status; }
        public String getProcessedBy() { return processedBy; }
        public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
        public LocalDateTime getProcessedDate() { return processedDate; }
        public void setProcessedDate(LocalDateTime processedDate) { this.processedDate = processedDate; }
        public String getTransactionReference() { return transactionReference; }
        public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }
    }

    /**
     * Approval Info Data Class
     */
    public static class ApprovalInfo {
        private String approvalId;
        private String approverId;
        private String approverName;
        private ApprovalStatus status;
        private LocalDateTime requestedAt;
        private LocalDateTime reviewedAt;
        private String comments;
        private String rejectionReason;

        public enum ApprovalStatus {
            PENDING("قيد الانتظار"),
            APPROVED("موافق عليه"),
            REJECTED("مرفوض");

            private final String arabicName;

            ApprovalStatus(String arabicName) {
                this.arabicName = arabicName;
            }

            public String getArabicName() { return arabicName; }
        }

        public ApprovalInfo(String approverId) {
            this.approvalId = UUID.randomUUID().toString();
            this.approverId = approverId;
            this.status = ApprovalStatus.PENDING;
            this.requestedAt = LocalDateTime.now();
        }

        // Getters and setters
        public String getApprovalId() { return approvalId; }
        public String getApproverId() { return approverId; }
        public String getApproverName() { return approverName; }
        public void setApproverName(String approverName) { this.approverName = approverName; }
        public ApprovalStatus getStatus() { return status; }
        public void setStatus(ApprovalStatus status) { this.status = status; }
        public LocalDateTime getRequestedAt() { return requestedAt; }
        public LocalDateTime getReviewedAt() { return reviewedAt; }
        public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }
        public String getRejectionReason() { return rejectionReason; }
        public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    }

    /**
     * Reconciliation Info Data Class
     */
    public static class ReconciliationInfo {
        private String reconciliationId;
        private ReconciliationStatus status;
        private LocalDateTime reconciledAt;
        private String reconciledBy;
        private Double discrepancyAmount;
        private String discrepancyReason;
        private List<String> matchedTransactions;
        private List<String> unmatchedTransactions;

        public enum ReconciliationStatus {
            NOT_RECONCILED("غير موافق"),
            PARTIALLY_RECONCILED("موافق جزئياً"),
            FULLY_RECONCILED("موافق بالكامل"),
            DISCREPANCY_FOUND("تم العثور على اختلاف");

            private final String arabicName;

            ReconciliationStatus(String arabicName) {
                this.arabicName = arabicName;
            }

            public String getArabicName() { return arabicName; }
        }

        public ReconciliationInfo() {
            this.reconciliationId = UUID.randomUUID().toString();
            this.status = ReconciliationStatus.NOT_RECONCILED;
            this.matchedTransactions = new ArrayList<>();
            this.unmatchedTransactions = new ArrayList<>();
        }

        // Getters and setters
        public String getReconciliationId() { return reconciliationId; }
        public ReconciliationStatus getStatus() { return status; }
        public void setStatus(ReconciliationStatus status) { this.status = status; }
        public LocalDateTime getReconciledAt() { return reconciledAt; }
        public void setReconciledAt(LocalDateTime reconciledAt) { this.reconciledAt = reconciledAt; }
        public String getReconciledBy() { return reconciledBy; }
        public void setReconciledBy(String reconciledBy) { this.reconciledBy = reconciledBy; }
        public Double getDiscrepancyAmount() { return discrepancyAmount; }
        public void setDiscrepancyAmount(Double discrepancyAmount) { this.discrepancyAmount = discrepancyAmount; }
        public String getDiscrepancyReason() { return discrepancyReason; }
        public void setDiscrepancyReason(String discrepancyReason) { this.discrepancyReason = discrepancyReason; }
        public List<String> getMatchedTransactions() { return matchedTransactions; }
        public List<String> getUnmatchedTransactions() { return unmatchedTransactions; }
    }

    /**
     * Create new payment transaction
     */
    public PaymentTransaction createPayment(PaymentTransaction payment) {
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaidAmount(0.0);
        
        // Save to repository
        PaymentTransaction savedPayment = paymentRepository.save(payment);
        
        // Log creation
        auditLogService.logPaymentCreation(savedPayment.getPaymentId(), savedPayment);
        
        // Send notification
        notificationService.sendPaymentCreatedNotification(savedPayment);
        
        return savedPayment;
    }

    /**
     * Process payment transaction
     */
    public PaymentTransaction processPayment(String paymentId, Double amount, PaymentMethod method, String transactionReference) {
        PaymentTransaction payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        // Update payment
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setTransactionReference(transactionReference);
        
        // Add partial payment
        PartialPayment partialPayment = new PartialPayment(amount, method);
        partialPayment.setTransactionReference(transactionReference);
        payment.getPartialPayments().add(partialPayment);
        
        // Update paid amount
        Double newPaidAmount = (payment.getPaidAmount() != null ? payment.getPaidAmount() : 0.0) + amount;
        payment.setPaidAmount(newPaidAmount);
        
        // Check if fully paid
        if (newPaidAmount >= payment.getAmount()) {
            payment.setStatus(PaymentStatus.COMPLETED);
            
            // Check if approval is needed
            if (requiresAccountantApproval(payment)) {
                requestAccountantApproval(payment);
            }
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_PAID);
        }
        
        // Save and log
        PaymentTransaction updatedPayment = paymentRepository.save(payment);
        auditLogService.logPaymentProcessing(paymentId, amount, method);
        
        // Send notification
        notificationService.sendPaymentProcessedNotification(updatedPayment);
        
        return updatedPayment;
    }

    /**
     * Process refund
     */
    public Refund processRefund(String paymentId, Double amount, String reason, String processedBy) {
        PaymentTransaction payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        // Create refund
        Refund refund = new Refund(amount, reason);
        refund.setProcessedBy(processedBy);
        
        // Add to payment
        payment.getRefunds().add(refund);
        
        // Update payment status if fully refunded
        Double totalRefunded = payment.getRefunds().stream()
            .mapToDouble(Refund::getAmount)
            .sum();
        
        if (totalRefunded >= payment.getPaidAmount()) {
            payment.setStatus(PaymentStatus.REFUNDED);
        }
        
        // Save and log
        paymentRepository.save(payment);
        auditLogService.logRefundProcessing(paymentId, refund.getRefundId(), amount, reason);
        
        // Send notification
        notificationService.sendRefundProcessedNotification(payment, refund);
        
        return refund;
    }

    /**
     * Request accountant approval
     */
    public void requestAccountantApproval(PaymentTransaction payment) {
        // Find accountant users
        List<User> accountants = userRepository.findByRole("ACCOUNTANT");
        
        if (accountants.isEmpty()) {
            throw new RuntimeException("No accountant users found");
        }
        
        // Create approval info for first available accountant
        ApprovalInfo approvalInfo = new ApprovalInfo(accountants.get(0).getUserId());
        approvalInfo.setApproverName(accountants.get(0).getName());
        
        payment.setApprovalInfo(approvalInfo);
        payment.setStatus(PaymentStatus.AWAITING_APPROVAL);
        
        // Save and log
        paymentRepository.save(payment);
        auditLogService.logApprovalRequest(payment.getPaymentId(), approvalInfo.getApprovalId());
        
        // Send notification to accountant
        notificationService.sendApprovalRequestNotification(payment, approvalInfo);
    }

    /**
     * Approve payment
     */
    public PaymentTransaction approvePayment(String paymentId, String approverId, String comments) {
        PaymentTransaction payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        ApprovalInfo approvalInfo = payment.getApprovalInfo();
        if (approvalInfo == null || !approvalInfo.getApproverId().equals(approverId)) {
            throw new RuntimeException("Invalid approver or no approval request found");
        }
        
        // Update approval
        approvalInfo.setStatus(ApprovalInfo.ApprovalStatus.APPROVED);
        approvalInfo.setReviewedAt(LocalDateTime.now());
        approvalInfo.setComments(comments);
        
        // Update payment status
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setUpdatedAt(LocalDateTime.now());
        
        // Save and log
        PaymentTransaction updatedPayment = paymentRepository.save(payment);
        auditLogService.logPaymentApproval(paymentId, approverId, comments);
        
        // Send notification
        notificationService.sendPaymentApprovedNotification(updatedPayment);
        
        return updatedPayment;
    }

    /**
     * Reject payment
     */
    public PaymentTransaction rejectPayment(String paymentId, String approverId, String rejectionReason) {
        PaymentTransaction payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        ApprovalInfo approvalInfo = payment.getApprovalInfo();
        if (approvalInfo == null || !approvalInfo.getApproverId().equals(approverId)) {
            throw new RuntimeException("Invalid approver or no approval request found");
        }
        
        // Update approval
        approvalInfo.setStatus(ApprovalInfo.ApprovalStatus.REJECTED);
        approvalInfo.setReviewedAt(LocalDateTime.now());
        approvalInfo.setRejectionReason(rejectionReason);
        
        // Update payment status
        payment.setStatus(PaymentStatus.REJECTED);
        payment.setUpdatedAt(LocalDateTime.now());
        
        // Save and log
        PaymentTransaction updatedPayment = paymentRepository.save(payment);
        auditLogService.logPaymentRejection(paymentId, approverId, rejectionReason);
        
        // Send notification
        notificationService.sendPaymentRejectedNotification(updatedPayment);
        
        return updatedPayment;
    }

    /**
     * Reconcile payment with invoice
     */
    public PaymentTransaction reconcilePayment(String paymentId, String reconciledBy) {
        PaymentTransaction payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        // Get invoice
        Invoice invoice = invoiceRepository.findById(payment.getInvoiceId())
            .orElseThrow(() -> new RuntimeException("Invoice not found: " + payment.getInvoiceId()));
        
        // Create reconciliation info
        ReconciliationInfo reconciliationInfo = new ReconciliationInfo();
        reconciliationInfo.setReconciledBy(reconciledBy);
        reconciliationInfo.setReconciledAt(LocalDateTime.now());
        
        // Check for discrepancies
        Double paymentAmount = payment.getPaidAmount();
        Double invoiceAmount = invoice.getAmount();
        
        if (paymentAmount.equals(invoiceAmount)) {
            reconciliationInfo.setStatus(ReconciliationInfo.ReconciliationStatus.FULLY_RECONCILED);
        } else {
            reconciliationInfo.setStatus(ReconciliationInfo.ReconciliationStatus.DISCREPANCY_FOUND);
            reconciliationInfo.setDiscrepancyAmount(Math.abs(paymentAmount - invoiceAmount));
            reconciliationInfo.setDiscrepancyReason("Payment amount doesn't match invoice amount");
        }
        
        // Add matched transactions
        reconciliationInfo.getMatchedTransactions().add(payment.getTransactionReference());
        
        payment.setReconciliationInfo(reconciliationInfo);
        
        // Save and log
        PaymentTransaction updatedPayment = paymentRepository.save(payment);
        auditLogService.logPaymentReconciliation(paymentId, reconciliationInfo.getReconciliationId());
        
        // Send notification
        notificationService.sendPaymentReconciledNotification(updatedPayment);
        
        return updatedPayment;
    }

    /**
     * Get payment by ID
     */
    public PaymentTransaction getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
    }

    /**
     * Get all payments for customer
     */
    public List<PaymentTransaction> getCustomerPayments(String customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    /**
     * Get payments pending approval
     */
    public List<PaymentTransaction> getPaymentsPendingApproval() {
        return paymentRepository.findByStatus(PaymentStatus.AWAITING_APPROVAL);
    }

    /**
     * Get overdue payments
     */
    public List<PaymentTransaction> getOverduePayments() {
        return paymentRepository.findAll().stream()
            .filter(PaymentTransaction::isOverdue)
            .collect(Collectors.toList());
    }

    /**
     * Get payment statistics
     */
    public Map<String, Object> getPaymentStatistics() {
        List<PaymentTransaction> allPayments = paymentRepository.findAll();
        
        Map<PaymentStatus, Long> statusCounts = allPayments.stream()
            .collect(Collectors.groupingBy(PaymentTransaction::getStatus, Collectors.counting()));
        
        Double totalAmount = allPayments.stream()
            .mapToDouble(PaymentTransaction::getAmount)
            .sum();
        
        Double totalPaid = allPayments.stream()
            .mapToDouble(p -> p.getPaidAmount() != null ? p.getPaidAmount() : 0.0)
            .sum();
        
        Long overdueCount = allPayments.stream()
            .filter(PaymentTransaction::isOverdue)
            .count();
        
        return Map.of(
            "totalPayments", allPayments.size(),
            "totalAmount", totalAmount,
            "totalPaid", totalPaid,
            "totalOutstanding", totalAmount - totalPaid,
            "statusDistribution", statusCounts,
            "overdueCount", overdueCount,
            "pendingApprovalCount", statusCounts.getOrDefault(PaymentStatus.AWAITING_APPROVAL, 0L)
        );
    }

    /**
     * Check if payment requires accountant approval
     */
    private boolean requiresAccountantApproval(PaymentTransaction payment) {
        // Business logic for when approval is needed
        return payment.getAmount() > 10000.0 || // Large amounts
               payment.getMethod() == PaymentMethod.CHECK || // Check payments
               payment.getPartialPayments().size() > 3; // Many partial payments
    }

    /**
     * Cancel payment
     */
    public PaymentTransaction cancelPayment(String paymentId, String reason) {
        PaymentTransaction payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        if (payment.getStatus() == PaymentStatus.COMPLETED || 
            payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RuntimeException("Cannot cancel completed or refunded payment");
        }
        
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setUpdatedAt(LocalDateTime.now());
        payment.getMetadata().put("cancellationReason", reason);
        
        // Save and log
        PaymentTransaction updatedPayment = paymentRepository.save(payment);
        auditLogService.logPaymentCancellation(paymentId, reason);
        
        // Send notification
        notificationService.sendPaymentCancelledNotification(updatedPayment);
        
        return updatedPayment;
    }

    /**
     * Update payment due date
     */
    public PaymentTransaction updateDueDate(String paymentId, LocalDateTime newDueDate) {
        PaymentTransaction payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        LocalDateTime oldDueDate = payment.getDueDate();
        payment.setDueDate(newDueDate);
        payment.setUpdatedAt(LocalDateTime.now());
        
        // Save and log
        PaymentTransaction updatedPayment = paymentRepository.save(payment);
        auditLogService.logDueDateUpdate(paymentId, oldDueDate, newDueDate);
        
        // Send notification
        notificationService.sendDueDateUpdatedNotification(updatedPayment);
        
        return updatedPayment;
    }
}
