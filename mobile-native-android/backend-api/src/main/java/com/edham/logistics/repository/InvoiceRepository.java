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
    Page<Invoice> findAllWithFilters(Pageable pageable, String status, Long customerId);

    default Page<Invoice> findAllWithFilters(Pageable pageable, String status, Long customerId, String dateRange) {
        return findAllWithFilters(pageable, status, customerId);
    }

    Page<Invoice> findByCustomerId(Long customerId, String status, Pageable pageable);

    List<Invoice> findByStatus(InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE " +
           "(i.invoiceNumber LIKE %:query% OR i.customer.firstName LIKE %:query% OR i.customer.lastName LIKE %:query%) AND " +
           "(:status IS NULL OR i.status = :status)")
    Page<Invoice> searchInvoices(String query, String status, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.createdAt BETWEEN :startDate AND :endDate")
    List<Invoice> findByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.createdAt BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumAmountByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
