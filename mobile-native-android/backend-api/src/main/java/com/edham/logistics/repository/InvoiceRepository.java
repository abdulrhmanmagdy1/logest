package com.edham.logistics.repository;

import com.edham.logistics.model.Invoice;
import com.edham.logistics.model.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i WHERE " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:customerId IS NULL OR i.customer.id = :customerId)")
    Page<Invoice> findAllWithFilters(Pageable pageable, String status, Long customerId, String dateRange);

    Page<Invoice> findByCustomerId(Long customerId, String status, Pageable pageable);

    List<Invoice> findByStatus(InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.status = 'OVERDUE'")
    List<Invoice> findOverdueInvoices();

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :now AND i.status != 'PAID'")
    List<Invoice> findOverdue(LocalDateTime now);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceNumber LIKE %:query% OR i.customer.firstName LIKE %:query% OR i.customer.lastName LIKE %:query%")
    Page<Invoice> searchInvoices(String query, String status, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE (:status IS NULL OR i.status = :status)")
    List<Invoice> findAllForExport(String status, String dateRange);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID'")
    Double getTotalRevenue();

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PENDING'")
    Double getPendingAmount();

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'OVERDUE'")
    Double getOverdueAmount();

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.createdAt >= :start")
    Long countThisMonth(LocalDateTime start);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.createdAt >= :start AND i.status = 'PAID'")
    Double getRevenueThisMonth(LocalDateTime start);

    default Long countThisMonth() {
        return countThisMonth(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0));
    }

    default Double getRevenueThisMonth() {
        return getRevenueThisMonth(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0));
    }

    @Query("SELECT i.customer.id as customerId, SUM(i.totalAmount) as total FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end GROUP BY i.customer.id")
    List<Map<String, Object>> getRevenueByCustomer(LocalDateTime start, LocalDateTime end);

    @Query("SELECT CAST(i.createdAt AS date) as period, SUM(i.totalAmount) as total FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end GROUP BY CAST(i.createdAt AS date)")
    List<Map<String, Object>> getRevenueByPeriod(LocalDateTime start, LocalDateTime end);

    @Query("SELECT i.status as status, COUNT(i) as count FROM Invoice i GROUP BY i.status")
    List<Map<String, Object>> countByStatus();

    @Query("SELECT i.status as status, SUM(i.totalAmount) as total FROM Invoice i GROUP BY i.status")
    List<Map<String, Object>> getInvoiceTrends(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end")
    Long countByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end AND i.status = 'PAID'")
    Double getRevenueByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end AND i.status = 'PAID'")
    Long countPaidByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end AND i.status = 'PENDING'")
    Long countPendingByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end AND i.status = 'OVERDUE'")
    Long countOverdueByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT AVG(i.totalAmount) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end")
    Double getAverageInvoiceAmount(LocalDateTime start, LocalDateTime end);
}
