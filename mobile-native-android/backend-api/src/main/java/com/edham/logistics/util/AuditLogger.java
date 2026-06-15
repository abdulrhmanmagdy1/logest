package com.edham.logistics.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogger.class);

    public void logShipmentCreation(Long shipmentId, Long userId, String message) {
        logger.info("AUDIT: Shipment Creation - ID: {}, User: {}, Message: {}", shipmentId, userId, message);
    }

    public void logShipmentUpdate(Long shipmentId, Long userId, String message) {
        logger.info("AUDIT: Shipment Update - ID: {}, User: {}, Message: {}", shipmentId, userId, message);
    }

    public void logShipmentDeletion(Long shipmentId, Long userId, String message) {
        logger.info("AUDIT: Shipment Deletion - ID: {}, User: {}, Message: {}", shipmentId, userId, message);
    }

    public void logShipmentStatusChange(Long shipmentId, Long userId, String oldStatus, String newStatus, String message) {
        logger.info("AUDIT: Status Change - ID: {}, User: {}, From: {}, To: {}, Message: {}", shipmentId, userId, oldStatus, newStatus, message);
    }

    public void logTrackingUpdate(Long shipmentId, Long userId, String message) {
        logger.info("AUDIT: Tracking Update - Shipment: {}, User: {}, Message: {}", shipmentId, userId, message);
    }

    public void logInvoiceCreation(Long invoiceId, Long userId, String message) {
        logger.info("AUDIT: Invoice Creation - ID: {}, User: {}, Message: {}", invoiceId, userId, message);
    }

    public void logInvoiceUpdate(Long invoiceId, Long userId, String message) {
        logger.info("AUDIT: Invoice Update - ID: {}, User: {}, Message: {}", invoiceId, userId, message);
    }

    public void logInvoiceDeletion(Long invoiceId, Long userId, String message) {
        logger.info("AUDIT: Invoice Deletion - ID: {}, User: {}, Message: {}", invoiceId, userId, message);
    }

    public void logPaymentProcessing(Long invoiceId, Long userId, String message) {
        logger.info("AUDIT: Payment Processing - Invoice: {}, User: {}, Message: {}", invoiceId, userId, message);
    }

    public void logInvoiceReminder(Long invoiceId, Long userId, String message) {
        logger.info("AUDIT: Invoice Reminder - ID: {}, User: {}, Message: {}", invoiceId, userId, message);
    }

    public void logBulkInvoiceOperation(String operation, Long userId, String message) {
        logger.info("AUDIT: Bulk Invoice Op - Op: {}, User: {}, Message: {}", operation, userId, message);
    }
}
