/**
 * ============================================
 * 💰 Invoice Controller - نظام إدهام
 * Edham Logistics - Invoice Management Controller
 * ============================================
 */

const Invoice = require('../models/Invoice');
const Shipment = require('../models/Shipment');
const Helpers = require('../utils/helpers');
const { MESSAGES, HTTP_STATUS, INVOICE_STATUS } = require('../config/constants');
const logger = require('../utils/logger');

class InvoiceController {
  /**
   * Get all invoices
   */
  static async getAll(req, res) {
    try {
      const { page = 1, limit = 20, status, clientId } = req.query;
      
      let query = { deletedAt: null };
      if (status) query.status = status;
      if (clientId) query.client = clientId;

      const invoices = await Invoice.find(query)
        .populate('client', 'name email phone')
        .populate('shipment', 'shipmentNumber')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ createdAt: -1 });

      const total = await Invoice.countDocuments(query);

      res.json({
        success: true,
        data: invoices,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get invoices error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Create invoice
   */
  static async create(req, res) {
    try {
      const { shipmentId, items, tax, notes } = req.body;

      const shipment = await Shipment.findById(shipmentId);
      if (!shipment) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'Shipment not found'
        });
      }

      const invoiceNumber = Helpers.generateInvoiceNumber();
      
      // Calculate totals
      const subtotal = items.reduce((sum, item) => sum + (item.quantity * item.price), 0);
      const taxAmount = subtotal * (tax || 0.15);
      const total = subtotal + taxAmount;

      const invoice = new Invoice({
        invoiceNumber,
        client: shipment.client,
        shipment: shipmentId,
        items,
        subtotal,
        tax: taxAmount,
        total,
        notes,
        status: INVOICE_STATUS.PENDING,
        issueDate: new Date(),
        dueDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000) // 30 days
      });

      await invoice.save();
      await invoice.populate(['client', 'shipment']);

      logger.success('Invoice created', { invoiceNumber });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: MESSAGES.CREATED,
        data: invoice
      });
    } catch (error) {
      logger.error('Create invoice error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Record payment
   */
  static async recordPayment(req, res) {
    try {
      const { amount, method, reference } = req.body;
      
      const invoice = await Invoice.findById(req.params.id);
      if (!invoice) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      invoice.paidAmount += amount;
      
      if (invoice.paidAmount >= invoice.total) {
        invoice.status = INVOICE_STATUS.PAID;
        invoice.balanceDue = 0;
      } else {
        invoice.status = INVOICE_STATUS.PARTIAL;
        invoice.balanceDue = invoice.total - invoice.paidAmount;
      }

      invoice.payments.push({
        amount,
        method,
        reference,
        date: new Date()
      });

      await invoice.save();

      logger.success('Payment recorded', { invoiceId: req.params.id, amount });

      res.json({
        success: true,
        message: 'Payment recorded successfully',
        data: invoice
      });
    } catch (error) {
      logger.error('Record payment error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get invoice statistics
   */
  static async getStatistics(req, res) {
    try {
      const [total, paid, pending, overdue, totalRevenue] = await Promise.all([
        Invoice.countDocuments({ deletedAt: null }),
        Invoice.countDocuments({ status: INVOICE_STATUS.PAID }),
        Invoice.countDocuments({ status: { $in: [INVOICE_STATUS.PENDING, INVOICE_STATUS.SENT] } }),
        Invoice.countDocuments({ status: INVOICE_STATUS.OVERDUE }),
        Invoice.aggregate([
          { $match: { status: INVOICE_STATUS.PAID } },
          { $group: { _id: null, total: { $sum: '$paidAmount' } } }
        ])
      ]);

      res.json({
        success: true,
        data: {
          total,
          paid,
          pending,
          overdue,
          totalRevenue: totalRevenue[0]?.total || 0
        }
      });
    } catch (error) {
      logger.error('Get invoice statistics error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Verify invoice by serial number or verification code
   */
  static async verify(req, res) {
    try {
      const { serialNumber, verificationCode } = req.body;
      
      let invoice;
      if (serialNumber) {
        invoice = await Invoice.findOne({ serialNumber })
          .populate('client', 'name email phone');
      } else if (verificationCode) {
        invoice = await Invoice.findOne({ verificationCode })
          .populate('client', 'name email phone');
      } else {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'Serial number or verification code required'
        });
      }

      if (!invoice) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      // Update verification tracking
      invoice.verificationAttempts = (invoice.verificationAttempts || 0) + 1;
      invoice.lastVerifiedAt = new Date();
      await invoice.save();

      res.json({
        success: true,
        valid: true,
        data: {
          invoiceNumber: invoice.invoiceNumber,
          serialNumber: invoice.serialNumber,
          amount: invoice.total,
          status: invoice.status,
          client: invoice.client?.name,
          createdAt: invoice.createdAt,
          isVerified: true
        }
      });
    } catch (error) {
      logger.error('Verify invoice error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = InvoiceController;
