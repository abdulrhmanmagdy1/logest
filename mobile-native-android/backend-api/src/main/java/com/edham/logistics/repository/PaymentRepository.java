package com.edham.logistics.repository;

import com.edham.logistics.model.Invoice;
import com.edham.logistics.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoice(Invoice invoice);

    @Query("SELECT p.paymentMethod as method, SUM(p.amount) as total FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end GROUP BY p.paymentMethod")
    List<Map<String, Object>> getPaymentMethodsAnalytics(LocalDateTime start, LocalDateTime end);
}
