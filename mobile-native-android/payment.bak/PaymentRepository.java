// package com.edham.logistics.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment Transaction entities
 */
@Repository
public interface PaymentRepository extends JpaRepository<PaymentLifecycleService.PaymentTransaction, String> {

    /**
     * Find payments by customer ID
     */
    List<PaymentLifecycleService.PaymentTransaction> findByCustomerId(String customerId);

    /**
     * Find payments by status
     */
    List<PaymentLifecycleService.PaymentTransaction> findByStatus(PaymentLifecycleService.PaymentStatus status);

    /**
     * Find payments by invoice ID
     */
    List<PaymentLifecycleService.PaymentTransaction> findByInvoiceId(String invoiceId);

    /**
     * Find payments by shipment ID
     */
    List<PaymentLifecycleService.PaymentTransaction> findByShipmentId(String shipmentId);

    /**
     * Find payments by payment method
     */
    List<PaymentLifecycleService.PaymentTransaction> findByMethod(PaymentLifecycleService.PaymentMethod method);

    /**
     * Find payments created between dates
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<PaymentLifecycleService.PaymentTransaction> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find payments due before date
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.dueDate < :dueDate AND p.status != 'COMPLETED' AND p.status != 'REFUNDED' AND p.status != 'CANCELLED'")
    List<PaymentLifecycleService.PaymentTransaction> findOverduePayments(@Param("dueDate") LocalDateTime dueDate);

    /**
     * Find payments with amount greater than specified value
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.amount > :amount")
    List<PaymentLifecycleService.PaymentTransaction> findByAmountGreaterThan(@Param("amount") Double amount);

    /**
     * Find payments with partial payments
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.status = 'PARTIALLY_PAID'")
    List<PaymentLifecycleService.PaymentTransaction> findPartiallyPaidPayments();

    /**
     * Find payments awaiting approval
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.status = 'AWAITING_APPROVAL'")
    List<PaymentLifecycleService.PaymentTransaction> findPaymentsAwaitingApproval();

    /**
     * Find payments by transaction reference
     */
    Optional<PaymentLifecycleService.PaymentTransaction> findByTransactionReference(String transactionReference);

    /**
     * Count payments by status
     */
    @Query("SELECT COUNT(p) FROM PaymentTransaction p WHERE p.status = :status")
    Long countByStatus(@Param("status") PaymentLifecycleService.PaymentStatus status);

    /**
     * Sum total amount by status
     */
    @Query("SELECT SUM(p.amount) FROM PaymentTransaction p WHERE p.status = :status")
    Double sumAmountByStatus(@Param("status") PaymentLifecycleService.PaymentStatus status);

    /**
     * Sum paid amount by customer
     */
    @Query("SELECT SUM(p.paidAmount) FROM PaymentTransaction p WHERE p.customerId = :customerId AND p.paidAmount IS NOT NULL")
    Double sumPaidAmountByCustomer(@Param("customerId") String customerId);

    /**
     * Find payments with refunds
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE SIZE(p.refunds) > 0")
    List<PaymentLifecycleService.PaymentTransaction> findPaymentsWithRefunds();

    /**
     * Find payments reconciled by user
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.reconciliationInfo.reconciledBy = :reconciledBy")
    List<PaymentLifecycleService.PaymentTransaction> findPaymentsReconciledBy(@Param("reconciledBy") String reconciledBy);

    /**
     * Find payments approved by user
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.approvalInfo.approverId = :approverId")
    List<PaymentLifecycleService.PaymentTransaction> findPaymentsApprovedBy(@Param("approverId") String approverId);

    /**
     * Find payments created today
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE DATE(p.createdAt) = CURRENT_DATE")
    List<PaymentLifecycleService.PaymentTransaction> findPaymentsCreatedToday();

    /**
     * Find payments with discrepancies
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.reconciliationInfo.status = 'DISCREPANCY_FOUND'")
    List<PaymentLifecycleService.PaymentTransaction> findPaymentsWithDiscrepancies();

    /**
     * Search payments by description
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    List<PaymentLifecycleService.PaymentTransaction> findByDescriptionContaining(@Param("description") String description);

    /**
     * Get payment statistics by customer
     */
    @Query("SELECT p.customerId, COUNT(p), SUM(p.amount), SUM(p.paidAmount) FROM PaymentTransaction p GROUP BY p.customerId")
    List<Object[]> getPaymentStatisticsByCustomer();

    /**
     * Get payment statistics by method
     */
    @Query("SELECT p.method, COUNT(p), SUM(p.amount) FROM PaymentTransaction p GROUP BY p.method")
    List<Object[]> getPaymentStatisticsByMethod();

    /**
     * Get payment statistics by status
     */
    @Query("SELECT p.status, COUNT(p), SUM(p.amount) FROM PaymentTransaction p GROUP BY p.status")
    List<Object[]> getPaymentStatisticsByStatus();

    /**
     * Find payments with multiple partial payments
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE SIZE(p.partialPayments) > 1")
    List<PaymentLifecycleService.PaymentTransaction> findPaymentsWithMultiplePartialPayments();

    /**
     * Find payments processed in date range
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.updatedAt BETWEEN :startDate AND :endDate AND p.status IN ('COMPLETED', 'PARTIALLY_PAID')")
    List<PaymentLifecycleService.PaymentTransaction> findProcessedPaymentsInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find payments by approval status
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.approvalInfo.status = :approvalStatus")
    List<PaymentLifecycleService.PaymentTransaction> findByApprovalStatus(@Param("approvalStatus") PaymentLifecycleService.ApprovalInfo.ApprovalStatus approvalStatus);

    /**
     * Find payments by reconciliation status
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.reconciliationInfo.status = :reconciliationStatus")
    List<PaymentLifecycleService.PaymentTransaction> findByReconciliationStatus(@Param("reconciliationStatus") PaymentLifecycleService.ReconciliationInfo.ReconciliationStatus reconciliationStatus);

    /**
     * Get overdue payment summary
     */
    @Query("SELECT p.customerId, p.paymentId, p.amount, p.paidAmount, p.dueDate FROM PaymentTransaction p WHERE p.dueDate < :currentDate AND p.status NOT IN ('COMPLETED', 'REFUNDED', 'CANCELLED')")
    List<Object[]> getOverduePaymentSummary(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find high-value payments (above threshold)
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.amount >= :threshold")
    List<PaymentLifecycleService.PaymentTransaction> findHighValuePayments(@Param("threshold") Double threshold);

    /**
     * Find payments with failed refunds
     */
    @Query("SELECT p FROM PaymentTransaction p JOIN p.refunds r WHERE r.status = 'FAILED'")
    List<PaymentLifecycleService.PaymentTransaction> findPaymentsWithFailedRefunds();

    /**
     * Get payment aging report
     */
    @Query("SELECT p.customerId, p.paymentId, p.amount, p.paidAmount, p.dueDate, p.status, " +
           "CASE WHEN p.dueDate < :currentDate THEN 'OVERDUE' ELSE 'CURRENT' END as agingStatus " +
           "FROM PaymentTransaction p WHERE p.status NOT IN ('COMPLETED', 'REFUNDED', 'CANCELLED')")
    List<Object[]> getPaymentAgingReport(@Param("currentDate") LocalDateTime currentDate);
}
