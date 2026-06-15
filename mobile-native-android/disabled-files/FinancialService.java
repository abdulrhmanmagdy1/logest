package com.edham.logistics.financial;

import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Financial service for invoice generation, payment tracking, and financial management
 */
@Slf4j
@Service
public class FinancialService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    // Invoice number generator
    private static final String INVOICE_PREFIX = "INV";
    private static final String PAYMENT_PREFIX = "PAY";
    
    // Financial constants
    private static final BigDecimal TAX_RATE = new BigDecimal("0.15"); // 15% tax
    private static final BigDecimal LATE_FEE_RATE = new BigDecimal("0.02"); // 2% late fee
    private static final int LATE_FEE_DAYS = 30; // Late fee after 30 days

    @Autowired
    public FinancialService(InvoiceRepository invoiceRepository,
                          PaymentRepository paymentRepository,
                          ShipmentRepository shipmentRepository,
                          UserRepository userRepository,
                          MongoTemplate mongoTemplate) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Generate invoice for shipment
     */
    @Transactional
    public CompletableFuture<Invoice> generateInvoice(Long shipmentId, String createdBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("Generating invoice for shipment: {}", shipmentId);
                
                // Get shipment
                Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
                if (shipmentOpt.isEmpty()) {
                    throw new IllegalArgumentException("Shipment not found: " + shipmentId);
                }
                
                Shipment shipment = shipmentOpt.get();
                
                // Check if invoice already exists
                Optional<Invoice> existingInvoice = invoiceRepository.findByShipmentId(shipmentId);
                if (existingInvoice.isPresent()) {
                    throw new IllegalStateException("Invoice already exists for shipment: " + shipmentId);
                }
                
                // Calculate invoice amounts
                InvoiceCalculation calculation = calculateInvoiceAmounts(shipment);
                
                // Generate invoice number
                String invoiceNumber = generateInvoiceNumber();
                
                // Create invoice
                Invoice invoice = Invoice.builder()
                        .invoiceNumber(invoiceNumber)
                        .shipmentId(shipmentId)
                        .customerId(shipment.getCustomerId())
                        .trackingNumber(shipment.getTrackingNumber())
                        .invoiceDate(LocalDateTime.now())
                        .dueDate(LocalDateTime.now().plusDays(30)) // 30 days due
                        .status(InvoiceStatus.DRAFT)
                        .subtotal(calculation.getSubtotal())
                        .taxAmount(calculation.getTaxAmount())
                        .lateFee(calculation.getLateFee())
                        .totalAmount(calculation.getTotalAmount())
                        .paidAmount(BigDecimal.ZERO)
                        .outstandingBalance(calculation.getTotalAmount())
                        .currency("SAR")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .items(calculation.getItems())
                        .billingAddress(getBillingAddress(shipment.getCustomerId()))
                        .shippingAddress(getShippingAddress(shipment))
                        .notes("Invoice for shipment " + shipment.getTrackingNumber())
                        .build();
                
                // Save invoice
                Invoice savedInvoice = invoiceRepository.save(invoice);
                
                // Update shipment financial status
                updateShipmentFinancialStatus(shipmentId, FinancialStatus.INVOICED);
                
                log.info("Invoice generated successfully: {} for shipment: {}", savedInvoice.getInvoiceNumber(), shipmentId);
                return savedInvoice;
                
            } catch (Exception e) {
                log.error("Error generating invoice for shipment {}: {}", shipmentId, e.getMessage(), e);
                throw new RuntimeException("Failed to generate invoice", e);
            }
        });
    }

    /**
     * Process payment
     */
    @Transactional
    public CompletableFuture<Payment> processPayment(PaymentRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("Processing payment for invoice: {}", request.getInvoiceId());
                
                // Get invoice
                Optional<Invoice> invoiceOpt = invoiceRepository.findById(request.getInvoiceId());
                if (invoiceOpt.isEmpty()) {
                    throw new IllegalArgumentException("Invoice not found: " + request.getInvoiceId());
                }
                
                Invoice invoice = invoiceOpt.get();
                
                // Validate payment amount
                if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Payment amount must be positive");
                }
                
                if (request.getAmount().compareTo(invoice.getOutstandingBalance()) > 0) {
                    throw new IllegalArgumentException("Payment amount exceeds outstanding balance");
                }
                
                // Generate payment number
                String paymentNumber = generatePaymentNumber();
                
                // Create payment
                Payment payment = Payment.builder()
                        .paymentNumber(paymentNumber)
                        .invoiceId(request.getInvoiceId())
                        .customerId(invoice.getCustomerId())
                        .amount(request.getAmount())
                        .paymentMethod(request.getPaymentMethod())
                        .paymentDate(LocalDateTime.now())
                        .status(PaymentStatus.PROCESSING)
                        .currency("SAR")
                        .transactionId(request.getTransactionId())
                        .notes(request.getNotes())
                        .processedBy(request.getProcessedBy())
                        .createdAt(LocalDateTime.now())
                        .build();
                
                // Save payment
                Payment savedPayment = paymentRepository.save(payment);
                
                // Update invoice
                updateInvoiceWithPayment(invoice, savedPayment);
                
                // Update payment status
                savedPayment.setStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(savedPayment);
                
                log.info("Payment processed successfully: {} for invoice: {}", savedPayment.getPaymentNumber(), request.getInvoiceId());
                return savedPayment;
                
            } catch (Exception e) {
                log.error("Error processing payment for invoice {}: {}", request.getInvoiceId(), e.getMessage(), e);
                throw new RuntimeException("Failed to process payment", e);
            }
        });
    }

    /**
     * Get invoice by ID
     */
    public CompletableFuture<Optional<Invoice>> getInvoice(String invoiceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return invoiceRepository.findById(invoiceId);
            } catch (Exception e) {
                log.error("Error getting invoice {}: {}", invoiceId, e.getMessage(), e);
                return Optional.empty();
            }
        });
    }

    /**
     * Get invoices for customer
     */
    public CompletableFuture<Page<Invoice>> getCustomerInvoices(Long customerId, Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return invoiceRepository.findByCustomerIdOrderByInvoiceDateDesc(customerId, pageable);
            } catch (Exception e) {
                log.error("Error getting invoices for customer {}: {}", customerId, e.getMessage(), e);
                return Page.empty();
            }
        });
    }

    /**
     * Get all invoices (accountant access only)
     */
    public CompletableFuture<Page<Invoice>> getAllInvoices(Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return invoiceRepository.findAllByOrderByInvoiceDateDesc(pageable);
            } catch (Exception e) {
                log.error("Error getting all invoices: {}", e.getMessage(), e);
                return Page.empty();
            }
        });
    }

    /**
     * Get payments for invoice
     */
    public CompletableFuture<List<Payment>> getInvoicePayments(String invoiceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return paymentRepository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId);
            } catch (Exception e) {
                log.error("Error getting payments for invoice {}: {}", invoiceId, e.getMessage(), e);
                return new ArrayList<>();
            }
        });
    }

    /**
     * Get outstanding balances
     */
    public CompletableFuture<List<OutstandingBalance>> getOutstandingBalances() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Invoice> outstandingInvoices = invoiceRepository.findByOutstandingBalanceGreaterThanOrderByDueDateAsc(BigDecimal.ZERO);
                
                Map<Long, OutstandingBalance> balanceMap = new HashMap<>();
                
                for (Invoice invoice : outstandingInvoices) {
                    Long customerId = invoice.getCustomerId();
                    
                    OutstandingBalance balance = balanceMap.computeIfAbsent(customerId, id -> 
                        OutstandingBalance.builder()
                                .customerId(id)
                                .customerName(getCustomerName(id))
                                .totalInvoices(0)
                                .totalAmount(BigDecimal.ZERO)
                                .paidAmount(BigDecimal.ZERO)
                                .outstandingBalance(BigDecimal.ZERO)
                                .overdueAmount(BigDecimal.ZERO)
                                .invoices(new ArrayList<>())
                                .build());
                    
                    // Update balance
                    balance.setTotalInvoices(balance.getTotalInvoices() + 1);
                    balance.setTotalAmount(balance.getTotalAmount().add(invoice.getTotalAmount()));
                    balance.setPaidAmount(balance.getPaidAmount().add(invoice.getPaidAmount()));
                    balance.setOutstandingBalance(balance.getOutstandingBalance().add(invoice.getOutstandingBalance()));
                    
                    // Check if overdue
                    if (invoice.getDueDate().isBefore(LocalDateTime.now()) && 
                        invoice.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
                        balance.setOverdueAmount(balance.getOverdueAmount().add(invoice.getOutstandingBalance()));
                    }
                    
                    balance.getInvoices().add(invoice);
                }
                
                return new ArrayList<>(balanceMap.values());
                
            } catch (Exception e) {
                log.error("Error getting outstanding balances: {}", e.getMessage(), e);
                return new ArrayList<>();
            }
        });
    }

    /**
     * Generate invoice PDF
     */
    public CompletableFuture<byte[]> generateInvoicePDF(String invoiceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("Generating PDF for invoice: {}", invoiceId);
                
                // Get invoice
                Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
                if (invoiceOpt.isEmpty()) {
                    throw new IllegalArgumentException("Invoice not found: " + invoiceId);
                }
                
                Invoice invoice = invoiceOpt.get();
                Shipment shipment = shipmentRepository.findById(invoice.getShipmentId()).orElse(null);
                
                // Create PDF document
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, outputStream);
                
                document.open();
                
                // Add content to PDF
                addInvoiceContent(document, invoice, shipment);
                
                document.close();
                
                byte[] pdfBytes = outputStream.toByteArray();
                log.info("PDF generated successfully for invoice: {}", invoiceId);
                
                return pdfBytes;
                
            } catch (Exception e) {
                log.error("Error generating PDF for invoice {}: {}", invoiceId, e.getMessage(), e);
                throw new RuntimeException("Failed to generate PDF", e);
            }
        });
    }

    /**
     * Get financial summary
     */
    public CompletableFuture<FinancialSummary> getFinancialSummary(LocalDateTime startDate, LocalDateTime endDate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get invoices in date range
                List<Invoice> invoices = invoiceRepository.findByInvoiceDateBetweenOrderByInvoiceDateDesc(startDate, endDate);
                
                // Get payments in date range
                List<Payment> payments = paymentRepository.findByPaymentDateBetweenOrderByPaymentDateDesc(startDate, endDate);
                
                // Calculate summary
                BigDecimal totalInvoiced = invoices.stream()
                        .map(Invoice::getTotalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                BigDecimal totalPaid = payments.stream()
                        .map(Payment::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                BigDecimal totalOutstanding = invoices.stream()
                        .map(Invoice::getOutstandingBalance)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                BigDecimal totalOverdue = invoices.stream()
                        .filter(invoice -> invoice.getDueDate().isBefore(LocalDateTime.now()))
                        .map(Invoice::getOutstandingBalance)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // Count by status
                long draftCount = invoices.stream().filter(i -> i.getStatus() == InvoiceStatus.DRAFT).count();
                long sentCount = invoices.stream().filter(i -> i.getStatus() == InvoiceStatus.SENT).count();
                long paidCount = invoices.stream().filter(i -> i.getStatus() == InvoiceStatus.PAID).count();
                long overdueCount = invoices.stream().filter(i -> i.getStatus() == InvoiceStatus.OVERDUE).count();
                
                return FinancialSummary.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .totalInvoices(invoices.size())
                        .totalInvoiced(totalInvoiced)
                        .totalPaid(totalPaid)
                        .totalOutstanding(totalOutstanding)
                        .totalOverdue(totalOverdue)
                        .draftInvoices((int) draftCount)
                        .sentInvoices((int) sentCount)
                        .paidInvoices((int) paidCount)
                        .overdueInvoices((int) overdueCount)
                        .currency("SAR")
                        .generatedAt(LocalDateTime.now())
                        .build();
                
            } catch (Exception e) {
                log.error("Error getting financial summary: {}", e.getMessage(), e);
                return null;
            }
        });
    }

    /**
     * Apply late fees to overdue invoices
     */
    @Transactional
    public CompletableFuture<Integer> applyLateFees() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Applying late fees to overdue invoices");
                
                // Get overdue invoices without late fees
                LocalDateTime cutoffDate = LocalDateTime.now().minusDays(LATE_FEE_DAYS);
                List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoicesWithoutLateFee(cutoffDate);
                
                int updatedCount = 0;
                
                for (Invoice invoice : overdueInvoices) {
                    // Calculate late fee
                    BigDecimal lateFee = invoice.getOutstandingBalance()
                            .multiply(LATE_FEE_RATE)
                            .setScale(2, RoundingMode.HALF_UP);
                    
                    // Update invoice
                    BigDecimal newTotalAmount = invoice.getTotalAmount().add(lateFee);
                    BigDecimal newOutstandingBalance = invoice.getOutstandingBalance().add(lateFee);
                    
                    Query query = new Query(Criteria.where("id").is(invoice.getId()));
                    Update update = Update.update("lateFee", invoice.getLateFee().add(lateFee))
                            .set("totalAmount", newTotalAmount)
                            .set("outstandingBalance", newOutstandingBalance)
                            .set("status", InvoiceStatus.OVERDUE);
                    
                    mongoTemplate.updateFirst(query, update, "invoices");
                    updatedCount++;
                }
                
                log.info("Applied late fees to {} overdue invoices", updatedCount);
                return updatedCount;
                
            } catch (Exception e) {
                log.error("Error applying late fees: {}", e.getMessage(), e);
                return 0;
            }
        });
    }

    // Helper methods
    private InvoiceCalculation calculateInvoiceAmounts(Shipment shipment) {
        List<InvoiceItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        
        // Base shipping cost
        BigDecimal shippingCost = shipment.getShippingCost() != null ? shipment.getShippingCost() : BigDecimal.valueOf(100.0);
        InvoiceItem shippingItem = InvoiceItem.builder()
                .description("Shipping Cost")
                .quantity(1)
                .unitPrice(shippingCost)
                .total(shippingCost)
                .build();
        items.add(shippingItem);
        subtotal = subtotal.add(shippingCost);
        
        // Additional services
        if (shipment.getAdditionalServices() != null) {
            for (String service : shipment.getAdditionalServices()) {
                BigDecimal serviceCost = getServiceCost(service);
                InvoiceItem serviceItem = InvoiceItem.builder()
                        .description(service)
                        .quantity(1)
                        .unitPrice(serviceCost)
                        .total(serviceCost)
                        .build();
                items.add(serviceItem);
                subtotal = subtotal.add(serviceCost);
            }
        }
        
        // Calculate tax
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        
        // Calculate late fee (if applicable)
        BigDecimal lateFee = BigDecimal.ZERO;
        
        // Calculate total
        BigDecimal totalAmount = subtotal.add(taxAmount).add(lateFee);
        
        return InvoiceCalculation.builder()
                .items(items)
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .lateFee(lateFee)
                .totalAmount(totalAmount)
                .build();
    }

    private BigDecimal getServiceCost(String serviceName) {
        // Service cost mapping
        switch (serviceName.toLowerCase()) {
            case "insurance": return BigDecimal.valueOf(50.0);
            case "express_delivery": return BigDecimal.valueOf(25.0);
            case "fragile_handling": return BigDecimal.valueOf(15.0);
            case "cold_chain": return BigDecimal.valueOf(75.0);
            default: return BigDecimal.valueOf(10.0);
        }
    }

    private String generateInvoiceNumber() {
        // Get last invoice number
        String lastNumber = invoiceRepository.findLastInvoiceNumber();
        int nextNumber = 1;
        
        if (lastNumber != null && lastNumber.startsWith(INVOICE_PREFIX)) {
            try {
                String numberPart = lastNumber.substring(INVOICE_PREFIX.length());
                nextNumber = Integer.parseInt(numberPart) + 1;
            } catch (NumberFormatException e) {
                // Use default if parsing fails
            }
        }
        
        return INVOICE_PREFIX + String.format("%06d", nextNumber);
    }

    private String generatePaymentNumber() {
        // Get last payment number
        String lastNumber = paymentRepository.findLastPaymentNumber();
        int nextNumber = 1;
        
        if (lastNumber != null && lastNumber.startsWith(PAYMENT_PREFIX)) {
            try {
                String numberPart = lastNumber.substring(PAYMENT_PREFIX.length());
                nextNumber = Integer.parseInt(numberPart) + 1;
            } catch (NumberFormatException e) {
                // Use default if parsing fails
            }
        }
        
        return PAYMENT_PREFIX + String.format("%06d", nextNumber);
    }

    private void updateInvoiceWithPayment(Invoice invoice, Payment payment) {
        BigDecimal newPaidAmount = invoice.getPaidAmount().add(payment.getAmount());
        BigDecimal newOutstandingBalance = invoice.getOutstandingBalance().subtract(payment.getAmount());
        
        InvoiceStatus newStatus = InvoiceStatus.PARTIALLY_PAID;
        if (newOutstandingBalance.compareTo(BigDecimal.ZERO) == 0) {
            newStatus = InvoiceStatus.PAID;
        } else if (invoice.getDueDate().isBefore(LocalDateTime.now())) {
            newStatus = InvoiceStatus.OVERDUE;
        }
        
        Query query = new Query(Criteria.where("id").is(invoice.getId()));
        Update update = Update.update("paidAmount", newPaidAmount)
                .set("outstandingBalance", newOutstandingBalance)
                .set("status", newStatus)
                .set("updatedAt", LocalDateTime.now());
        
        mongoTemplate.updateFirst(query, update, "invoices");
    }

    private void updateShipmentFinancialStatus(Long shipmentId, FinancialStatus status) {
        Query query = new Query(Criteria.where("id").is(shipmentId));
        Update update = Update.update("financialStatus", status);
        
        mongoTemplate.updateFirst(query, update, "shipments");
    }

    private String getBillingAddress(Long customerId) {
        Optional<User> customerOpt = userRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            User customer = customerOpt.get();
            return String.format("%s\n%s\n%s, %s\n%s", 
                    customer.getName(),
                    customer.getAddress(),
                    customer.getCity(),
                    customer.getCountry(),
                    customer.getPostalCode());
        }
        return "N/A";
    }

    private String getShippingAddress(Shipment shipment) {
        return String.format("%s\n%s\n%s, %s\n%s", 
                shipment.getRecipientName(),
                shipment.getDestinationAddress(),
                shipment.getDestinationCity(),
                shipment.getDestinationCountry(),
                shipment.getDestinationPostalCode());
    }

    private String getCustomerName(Long customerId) {
        return userRepository.findById(customerId)
                .map(User::getName)
                .orElse("Unknown Customer");
    }

    private void addInvoiceContent(Document document, Invoice invoice, Shipment shipment) throws DocumentException {
        // Add title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        // Add invoice details
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        
        // Company info
        Paragraph companyInfo = new Paragraph("Edham Logistics\nRiyadh, Saudi Arabia\nTax ID: 1234567890", normalFont);
        document.add(companyInfo);
        
        // Invoice details
        Paragraph invoiceDetails = new Paragraph();
        invoiceDetails.add(new Chunk("Invoice Number: " + invoice.getInvoiceNumber() + "\n", normalFont));
        invoiceDetails.add(new Chunk("Invoice Date: " + invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n", normalFont));
        invoiceDetails.add(new Chunk("Due Date: " + invoice.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n", normalFont));
        invoiceDetails.add(new Chunk("Status: " + invoice.getStatus() + "\n", normalFont));
        document.add(invoiceDetails);
        
        // Customer and shipping info
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        
        PdfPCell billingCell = new PdfPCell(new Paragraph("Billing Address:\n" + invoice.getBillingAddress(), normalFont));
        PdfPCell shippingCell = new PdfPCell(new Paragraph("Shipping Address:\n" + invoice.getShippingAddress(), normalFont));
        
        infoTable.addCell(billingCell);
        infoTable.addCell(shippingCell);
        document.add(infoTable);
        
        // Items table
        PdfPTable itemsTable = new PdfPTable(4);
        itemsTable.setWidthPercentage(100);
        itemsTable.setHeaderRows(1);
        
        itemsTable.addCell("Description");
        itemsTable.addCell("Quantity");
        itemsTable.addCell("Unit Price");
        itemsTable.addCell("Total");
        
        for (InvoiceItem item : invoice.getItems()) {
            itemsTable.addCell(item.getDescription());
            itemsTable.addCell(item.getQuantity().toString());
            itemsTable.addCell(item.getUnitPrice().toString());
            itemsTable.addCell(item.getTotal().toString());
        }
        
        document.add(itemsTable);
        
        // Totals
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(50);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        totalsTable.addCell("Subtotal:");
        totalsTable.addCell(invoice.getSubtotal().toString());
        totalsTable.addCell("Tax (15%):");
        totalsTable.addCell(invoice.getTaxAmount().toString());
        totalsTable.addCell("Late Fee:");
        totalsTable.addCell(invoice.getLateFee().toString());
        totalsTable.addCell("Total:");
        totalsTable.addCell(invoice.getTotalAmount().toString());
        
        document.add(totalsTable);
        
        // Payment info
        Paragraph paymentInfo = new Paragraph("\nPayment Information:\nBank: Saudi National Bank\nAccount: 1234567890\nIBAN: SA1234567890123456789012", normalFont);
        document.add(paymentInfo);
    }

    // Data classes
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PaymentRequest {
        private String invoiceId;
        private BigDecimal amount;
        private PaymentMethod paymentMethod;
        private String transactionId;
        private String notes;
        private String processedBy;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class InvoiceCalculation {
        private List<InvoiceItem> items;
        private BigDecimal subtotal;
        private BigDecimal taxAmount;
        private BigDecimal lateFee;
        private BigDecimal totalAmount;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OutstandingBalance {
        private Long customerId;
        private String customerName;
        private Integer totalInvoices;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal outstandingBalance;
        private BigDecimal overdueAmount;
        private List<Invoice> invoices;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FinancialSummary {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer totalInvoices;
        private BigDecimal totalInvoiced;
        private BigDecimal totalPaid;
        private BigDecimal totalOutstanding;
        private BigDecimal totalOverdue;
        private Integer draftInvoices;
        private Integer sentInvoices;
        private Integer paidInvoices;
        private Integer overdueInvoices;
        private String currency;
        private LocalDateTime generatedAt;
    }
}
