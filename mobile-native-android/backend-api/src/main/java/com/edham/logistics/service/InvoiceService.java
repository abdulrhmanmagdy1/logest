package com.edham.logistics.service;

import com.edham.logistics.dto.InvoiceRequest;
import com.edham.logistics.dto.InvoiceResponse;
import com.edham.logistics.dto.InvoiceUpdateRequest;
import com.edham.logistics.model.Invoice;
import com.edham.logistics.model.InvoiceStatus;
import com.edham.logistics.model.Payment;
import com.edham.logistics.model.User;
import com.edham.logistics.repository.InvoiceRepository;
import com.edham.logistics.repository.PaymentRepository;
import com.edham.logistics.repository.UserRepository;
import com.edham.logistics.util.PaginatedResponse;
import com.edham.logistics.util.AuditLogger;
import com.edham.logistics.websocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public PaginatedResponse<InvoiceResponse> getAllInvoices(
            Pageable pageable, String status, Long customerId, String dateRange) {
        
        Page<Invoice> invoices = invoiceRepository.findAllWithFilters(
            pageable, status, customerId, dateRange);
        
        List<InvoiceResponse> invoiceResponses = invoices.getContent()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginatedResponse<>(
            invoiceResponses,
            invoices.getNumber(),
            invoices.getSize(),
            invoices.getTotalElements(),
            invoices.getTotalPages(),
            invoices.isLast()
        );
    }

    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        return convertToResponse(invoice);
    }

    public InvoiceResponse createInvoice(InvoiceRequest request, HttpServletRequest httpRequest) {
        User customer = userRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setCustomer(customer);
        invoice.setAmount(request.getAmount());
        invoice.setTax(request.getTax());
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setDueDate(request.getDueDate());
        invoice.setDescription(request.getDescription());
        invoice.setShipmentIds(request.getShipmentIds());
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setUpdatedAt(LocalDateTime.now());
        
        invoice = invoiceRepository.save(invoice);
        return convertToResponse(invoice);
    }

    public InvoiceResponse updateInvoice(Long id, InvoiceUpdateRequest request, 
                                     HttpServletRequest httpRequest) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        if (request.getAmount() != null) invoice.setAmount(request.getAmount());
        if (request.getTotalAmount() != null) invoice.setTotalAmount(request.getTotalAmount());
        if (request.getStatus() != null) invoice.setStatus(InvoiceStatus.valueOf(request.getStatus()));
        
        invoice.setUpdatedAt(LocalDateTime.now());
        invoice = invoiceRepository.save(invoice);
        return convertToResponse(invoice);
    }

    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    public PaginatedResponse<InvoiceResponse> getCustomerInvoices(Pageable pageable, String status) {
        Long customerId = 1L; // Placeholder
        Page<Invoice> invoices = invoiceRepository.findByCustomerId(customerId, status, pageable);
        
        List<InvoiceResponse> invoiceResponses = invoices.getContent()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginatedResponse<>(
            invoiceResponses,
            invoices.getNumber(),
            invoices.getSize(),
            invoices.getTotalElements(),
            invoices.getTotalPages(),
            invoices.isLast()
        );
    }

    public byte[] generateInvoicePdf(Long id) {
        return new byte[0]; // Placeholder
    }

    public InvoiceResponse processPayment(Long id, Map<String, Object> paymentData, 
                                       HttpServletRequest request) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount((Double) paymentData.get("amount"));
        payment.setPaymentMethod((String) paymentData.get("paymentMethod"));
        payment.setTransactionId((String) paymentData.get("transactionId"));
        payment.setStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());
        
        paymentRepository.save(payment);
        
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        invoice.setUpdatedAt(LocalDateTime.now());
        invoice = invoiceRepository.save(invoice);
        
        return convertToResponse(invoice);
    }

    public Map<String, Object> getInvoiceStatistics() {
        return java.util.Collections.emptyMap();
    }

    public List<InvoiceResponse> getOverdueInvoices() {
        return invoiceRepository.findByStatus(InvoiceStatus.OVERDUE).stream()
            .map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<InvoiceResponse> getPendingInvoices() {
        return invoiceRepository.findByStatus(InvoiceStatus.PENDING).stream()
            .map(this::convertToResponse).collect(Collectors.toList());
    }

    public PaginatedResponse<InvoiceResponse> searchInvoices(String query, Pageable pageable, 
                                                           String status) {
        Page<Invoice> invoices = invoiceRepository.searchInvoices(query, status, pageable);
        return new PaginatedResponse<>(
            invoices.getContent().stream().map(this::convertToResponse).collect(Collectors.toList()),
            invoices.getNumber(), invoices.getSize(), invoices.getTotalElements(), invoices.getTotalPages(), invoices.isLast()
        );
    }

    public void sendInvoiceReminder(Long id) {}

    public byte[] exportInvoices(String status, String dateRange, String format) {
        return new byte[0];
    }

    public List<Object> getPaymentHistory(Long invoiceId) {
        return java.util.Collections.emptyList();
    }

    public Object getRevenueAnalytics(String startDate, String endDate) {
        return null;
    }

    public List<InvoiceResponse> bulkInvoiceOperations(String operation, List<Long> invoiceIds, 
                                                     HttpServletRequest request) {
        return java.util.Collections.emptyList();
    }

    public Object getInvoiceSummary(String period) {
        return null;
    }

    private InvoiceResponse convertToResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setCustomerId(invoice.getCustomer().getId());
        response.setCustomerName(invoice.getCustomer().getFirstName() + " " + invoice.getCustomer().getLastName());
        response.setAmount(invoice.getAmount());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setStatus(invoice.getStatus().name());
        response.setCreatedAt(invoice.getCreatedAt());
        response.setUpdatedAt(invoice.getUpdatedAt());
        return response;
    }
}
