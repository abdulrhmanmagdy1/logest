/**
 * ============================================
 * 🧾 Voucher Controller - نظام إدهام
 * Edham Logistics - Receipt & Voucher Management Controller
 * ============================================
 */

const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');
const Voucher = require('../models/Voucher');
const User = require('../models/User');
const Transaction = require('../models/Transaction');
const PDFDocument = require('pdfkit');
const ExcelJS = require('exceljs');

class VoucherController {
  /**
   * Get all vouchers with filtering
   */
  static async getAll(req, res) {
    try {
      const {
        page = 1,
        limit = 20,
        type,
        status,
        startDate,
        endDate,
        clientId,
        serialNumber
      } = req.query;

      const query = {};
      
      if (type) query.type = type;
      if (status) query.status = status;
      if (clientId) query.clientId = clientId;
      if (serialNumber) query.serialNumber = { $regex: serialNumber, $options: 'i' };
      
      if (startDate || endDate) {
        query.createdAt = {};
        if (startDate) query.createdAt.$gte = new Date(startDate);
        if (endDate) query.createdAt.$lte = new Date(endDate);
      }

      const skip = (page - 1) * limit;

      const [vouchers, total] = await Promise.all([
        Voucher.find(query)
          .populate('clientId', 'name email phone')
          .populate('createdBy', 'name email')
          .populate('approvedBy', 'name email')
          .sort({ createdAt: -1 })
          .skip(skip)
          .limit(limit),
        Voucher.countDocuments(query)
      ]);

      res.json({
        success: true,
        vouchers,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / limit)
        }
      });
    } catch (error) {
      logger.error('Error getting vouchers:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get voucher by serial number
   */
  static async getBySerialNumber(req, res) {
    try {
      const { serialNumber } = req.params;

      const voucher = await Voucher.findOne({ serialNumber })
        .populate('clientId', 'name email phone')
        .populate('createdBy', 'name email')
        .populate('approvedBy', 'name email');

      if (!voucher) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'السند غير موجود'
        });
      }

      res.json({
        success: true,
        voucher
      });
    } catch (error) {
      logger.error('Error getting voucher by serial number:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Create receipt voucher (سند قبض)
   */
  static async createReceiptVoucher(req, res) {
    try {
      const {
        clientId,
        amount,
        description,
        paymentMethod,
        referenceNumber,
        items,
        notes
      } = req.body;

      // Validate client exists
      const client = await User.findById(clientId);
      if (!client) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'العميل غير موجود'
        });
      }

      // Generate unique serial number
      const serialNumber = await Voucher.generateSerialNumber('receipt');

      const voucher = new Voucher({
        serialNumber,
        type: 'receipt',
        clientId,
        amount,
        description,
        paymentMethod,
        referenceNumber,
        items: items || [],
        notes,
        createdBy: req.user.id,
        status: 'completed'
      });

      await voucher.save();

      // Create corresponding transaction
      const transaction = new Transaction({
        userId: clientId,
        type: 'credit',
        amount,
        description: `سند قبض #${serialNumber}: ${description}`,
        status: 'completed',
        paymentMethod,
        voucherId: voucher._id,
        metadata: {
          voucherSerial: serialNumber,
          type: 'receipt'
        }
      });

      await transaction.save();

      // Update client balance if applicable
      if (paymentMethod === 'balance') {
        client.balance = (client.balance || 0) + amount;
        await client.save();
      }

      logger.success('Receipt voucher created', {
        voucherId: voucher._id,
        serialNumber,
        amount,
        clientId
      });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'تم إنشاء سند القبض بنجاح',
        voucher: await voucher.populate('clientId', 'name email')
      });
    } catch (error) {
      logger.error('Error creating receipt voucher:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Create payment voucher (سند صرف)
   */
  static async createPaymentVoucher(req, res) {
    try {
      const {
        clientId,
        amount,
        description,
        paymentMethod,
        referenceNumber,
        items,
        notes,
        requiresApproval = false
      } = req.body;

      // Validate client exists
      const client = await User.findById(clientId);
      if (!client) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'العميل غير موجود'
        });
      }

      // Generate unique serial number
      const serialNumber = await Voucher.generateSerialNumber('payment');

      const voucher = new Voucher({
        serialNumber,
        type: 'payment',
        clientId,
        amount,
        description,
        paymentMethod,
        referenceNumber,
        items: items || [],
        notes,
        createdBy: req.user.id,
        status: requiresApproval ? 'pending' : 'completed'
      });

      await voucher.save();

      // Create corresponding transaction
      const transaction = new Transaction({
        userId: clientId,
        type: 'debit',
        amount,
        description: `سند صرف #${serialNumber}: ${description}`,
        status: voucher.status,
        paymentMethod,
        voucherId: voucher._id,
        metadata: {
          voucherSerial: serialNumber,
          type: 'payment'
        }
      });

      await transaction.save();

      // Update client balance if applicable and approved
      if (paymentMethod === 'balance' && voucher.status === 'completed') {
        const currentBalance = client.balance || 0;
        if (currentBalance >= amount) {
          client.balance -= amount;
          await client.save();
        } else {
          // Insufficient balance
          voucher.status = 'failed';
          transaction.status = 'failed';
          await Promise.all([voucher.save(), transaction.save()]);
          
          return res.status(HTTP_STATUS.BAD_REQUEST).json({
            success: false,
            message: 'رصيد العميل غير كافي'
          });
        }
      }

      logger.success('Payment voucher created', {
        voucherId: voucher._id,
        serialNumber,
        amount,
        clientId,
        status: voucher.status
      });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'تم إنشاء سند الصرف بنجاح',
        voucher: await voucher.populate('clientId', 'name email')
      });
    } catch (error) {
      logger.error('Error creating payment voucher:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Create transfer voucher (حولات)
   */
  static async createTransferVoucher(req, res) {
    try {
      const {
        fromClientId,
        toClientId,
        amount,
        description,
        referenceNumber,
        notes
      } = req.body;

      // Validate clients exist
      const [fromClient, toClient] = await Promise.all([
        User.findById(fromClientId),
        User.findById(toClientId)
      ]);

      if (!fromClient || !toClient) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'أحد العملاء غير موجود'
        });
      }

      // Check from client balance
      const fromBalance = fromClient.balance || 0;
      if (fromBalance < amount) {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'رصيد العميل المحول غير كافي'
        });
      }

      // Generate unique serial number
      const serialNumber = await Voucher.generateSerialNumber('transfer');

      const voucher = new Voucher({
        serialNumber,
        type: 'transfer',
        clientId: fromClientId,
        amount,
        description,
        referenceNumber,
        notes,
        createdBy: req.user.id,
        status: 'completed',
        metadata: {
          fromClientId,
          toClientId
        }
      });

      await voucher.save();

      // Create transactions for both clients
      const debitTransaction = new Transaction({
        userId: fromClientId,
        type: 'debit',
        amount,
        description: `تحويل #${serialNumber}: ${description}`,
        status: 'completed',
        paymentMethod: 'transfer',
        voucherId: voucher._id,
        metadata: {
          voucherSerial: serialNumber,
          type: 'transfer',
          toClientId
        }
      });

      const creditTransaction = new Transaction({
        userId: toClientId,
        type: 'credit',
        amount,
        description: `استلام تحويل #${serialNumber}: ${description}`,
        status: 'completed',
        paymentMethod: 'transfer',
        voucherId: voucher._id,
        metadata: {
          voucherSerial: serialNumber,
          type: 'transfer',
          fromClientId
        }
      });

      await Promise.all([debitTransaction.save(), creditTransaction.save()]);

      // Update client balances
      fromClient.balance -= amount;
      toClient.balance = (toClient.balance || 0) + amount;
      await Promise.all([fromClient.save(), toClient.save()]);

      logger.success('Transfer voucher created', {
        voucherId: voucher._id,
        serialNumber,
        amount,
        fromClientId,
        toClientId
      });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'تم إنشاء الحوالة بنجاح',
        voucher: await voucher.populate('clientId', 'name email')
      });
    } catch (error) {
      logger.error('Error creating transfer voucher:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Update voucher status
   */
  static async updateStatus(req, res) {
    try {
      const { id } = req.params;
      const { status, notes } = req.body;

      const voucher = await Voucher.findById(id);
      if (!voucher) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'السند غير موجود'
        });
      }

      if (voucher.status === 'completed') {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'لا يمكن تعديل سند مكتمل'
        });
      }

      voucher.status = status;
      voucher.approvedBy = req.user.id;
      if (notes) voucher.notes = notes;

      await voucher.save();

      // Update corresponding transaction
      await Transaction.findOneAndUpdate(
        { voucherId: voucher._id },
        { status, approvedBy: req.user.id }
      );

      logger.info('Voucher status updated', {
        voucherId: voucher._id,
        oldStatus: voucher.status,
        newStatus: status,
        updatedBy: req.user.id
      });

      res.json({
        success: true,
        message: 'تم تحديث حالة السند بنجاح',
        voucher
      });
    } catch (error) {
      logger.error('Error updating voucher status:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Cancel voucher
   */
  static async cancelVoucher(req, res) {
    try {
      const { id } = req.params;
      const { reason } = req.body;

      const voucher = await Voucher.findById(id);
      if (!voucher) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'السند غير موجود'
        });
      }

      if (voucher.status === 'cancelled') {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'السند ملغي بالفعل'
        });
      }

      voucher.status = 'cancelled';
      voucher.cancelledBy = req.user.id;
      voucher.cancelledAt = new Date();
      voucher.metadata.cancellationReason = reason;

      await voucher.save();

      // Update corresponding transaction
      await Transaction.findOneAndUpdate(
        { voucherId: voucher._id },
        { 
          status: 'cancelled',
          metadata: { ...voucher.metadata, cancellationReason: reason }
        }
      );

      logger.info('Voucher cancelled', {
        voucherId: voucher._id,
        reason,
        cancelledBy: req.user.id
      });

      res.json({
        success: true,
        message: 'تم إلغاء السند بنجاح',
        voucher
      });
    } catch (error) {
      logger.error('Error cancelling voucher:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get voucher statistics
   */
  static async getStatistics(req, res) {
    try {
      const { startDate, endDate } = req.query;

      const matchQuery = {};
      if (startDate || endDate) {
        matchQuery.createdAt = {};
        if (startDate) matchQuery.createdAt.$gte = new Date(startDate);
        if (endDate) matchQuery.createdAt.$lte = new Date(endDate);
      }

      const [
        totalReceipts,
        totalPayments,
        totalTransfers,
        typeStats,
        statusStats,
        monthlyStats
      ] = await Promise.all([
        Voucher.aggregate([
          { $match: { ...matchQuery, type: 'receipt', status: 'completed' } },
          { $group: { _id: null, total: { $sum: '$amount' }, count: { $sum: 1 } } }
        ]),
        Voucher.aggregate([
          { $match: { ...matchQuery, type: 'payment', status: 'completed' } },
          { $group: { _id: null, total: { $sum: '$amount' }, count: { $sum: 1 } } }
        ]),
        Voucher.aggregate([
          { $match: { ...matchQuery, type: 'transfer', status: 'completed' } },
          { $group: { _id: null, total: { $sum: '$amount' }, count: { $sum: 1 } } }
        ]),
        Voucher.aggregate([
          { $match: matchQuery },
          { $group: { _id: '$type', count: { $sum: 1 }, total: { $sum: '$amount' } } }
        ]),
        Voucher.aggregate([
          { $match: matchQuery },
          { $group: { _id: '$status', count: { $sum: 1 } } }
        ]),
        Voucher.aggregate([
          { $match: { ...matchQuery, status: 'completed' } },
          {
            $group: {
              _id: {
                year: { $year: '$createdAt' },
                month: { $month: '$createdAt' }
              },
              total: { $sum: '$amount' },
              count: { $sum: 1 }
            }
          },
          { $sort: { '_id.year': 1, '_id.month': 1 } }
        ])
      ]);

      res.json({
        success: true,
        statistics: {
          receipts: totalReceipts[0] || { total: 0, count: 0 },
          payments: totalPayments[0] || { total: 0, count: 0 },
          transfers: totalTransfers[0] || { total: 0, count: 0 },
          byType: typeStats,
          byStatus: statusStats,
          monthly: monthlyStats,
          currency: 'SAR'
        }
      });
    } catch (error) {
      logger.error('Error getting voucher statistics:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get client debts and receivables
   */
  static async getClientDebts(req, res) {
    try {
      const { clientId } = req.params;

      const client = await User.findById(clientId);
      if (!client) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'العميل غير موجود'
        });
      }

      const [
        totalDebits,
        totalCredits,
        pendingVouchers,
        recentVouchers
      ] = await Promise.all([
        Transaction.aggregate([
          { $match: { userId: client._id, type: { $in: ['debit', 'payment'] }, status: 'completed' } },
          { $group: { _id: null, total: { $sum: '$amount' } } }
        ]),
        Transaction.aggregate([
          { $match: { userId: client._id, type: { $in: ['credit', 'refund'] }, status: 'completed' } },
          { $group: { _id: null, total: { $sum: '$amount' } } }
        ]),
        Voucher.find({ clientId: client._id, status: 'pending' })
          .sort({ createdAt: -1 }),
        Voucher.find({ clientId: client._id })
          .sort({ createdAt: -1 })
          .limit(10)
          .populate('createdBy', 'name')
      ]);

      const totalDebitAmount = totalDebits[0]?.total || 0;
      const totalCreditAmount = totalCredits[0]?.total || 0;
      const currentBalance = totalCreditAmount - totalDebitAmount;

      res.json({
        success: true,
        client: {
          id: client._id,
          name: client.name,
          email: client.email,
          phone: client.phone
        },
        financials: {
          totalDebits: totalDebitAmount,
          totalCredits: totalCreditAmount,
          currentBalance,
          pendingVouchers: pendingVouchers.length,
          status: currentBalance >= 0 ? 'مستقر' : 'مدين'
        },
        pendingVouchers,
        recentVouchers
      });
    } catch (error) {
      logger.error('Error getting client debts:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Verify voucher authenticity (anti-fraud)
   */
  static async verifyVoucher(req, res) {
    try {
      const { serialNumber, verificationCode } = req.body;

      const voucher = await Voucher.findOne({ serialNumber })
        .populate('clientId', 'name email')
        .populate('createdBy', 'name email');

      if (!voucher) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'السند غير موجود',
          verified: false
        });
      }

      // Generate verification hash (anti-fraud)
      const verificationHash = voucher.generateVerificationHash();

      if (verificationCode && verificationCode !== verificationHash) {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'رمز التحقق غير صحيح',
          verified: false
        });
      }

      res.json({
        success: true,
        message: 'السند صحيح وموثوق',
        verified: true,
        voucher: {
          serialNumber,
          type: voucher.type,
          amount: voucher.amount,
          status: voucher.status,
          createdAt: voucher.createdAt,
          clientName: voucher.clientId?.name,
          verificationHash
        }
      });
    } catch (error) {
      logger.error('Error verifying voucher:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Export vouchers to PDF/Excel
   */
  static async exportVouchers(req, res) {
    try {
      const { format } = req.params;
      const { startDate, endDate, type, status } = req.query;

      const query = {};
      if (type) query.type = type;
      if (status) query.status = status;
      if (startDate || endDate) {
        query.createdAt = {};
        if (startDate) query.createdAt.$gte = new Date(startDate);
        if (endDate) query.createdAt.$lte = new Date(endDate);
      }

      const vouchers = await Voucher.find(query)
        .populate('clientId', 'name email phone')
        .populate('createdBy', 'name email')
        .sort({ createdAt: -1 });

      if (format === 'pdf') {
        // Generate PDF
        const doc = new PDFDocument();
        res.setHeader('Content-Type', 'application/pdf');
        res.setHeader('Content-Disposition', `attachment; filename="vouchers-${Date.now()}.pdf"`);
        doc.pipe(res);

        // PDF content
        doc.fontSize(20).text('تقرير السندات', { align: 'right' });
        doc.moveDown();
        
        vouchers.forEach(voucher => {
          doc.fontSize(12).text(`الرقم التسلسلي: ${voucher.serialNumber}`);
          doc.text(`النوع: ${voucher.type}`);
          doc.text(`المبلغ: ${voucher.amount} ريال`);
          doc.text(`العميل: ${voucher.clientId?.name || 'N/A'}`);
          doc.text(`الحالة: ${voucher.status}`);
          doc.text(`التاريخ: ${voucher.createdAt.toLocaleDateString('ar-SA')}`);
          doc.moveDown();
        });

        doc.end();
      } else if (format === 'excel') {
        // Generate Excel
        const workbook = new ExcelJS.Workbook();
        const worksheet = workbook.addWorksheet('السندات');

        worksheet.columns = [
          { header: 'الرقم التسلسلي', key: 'serialNumber', width: 20 },
          { header: 'النوع', key: 'type', width: 15 },
          { header: 'المبلغ', key: 'amount', width: 15 },
          { header: 'العميل', key: 'clientName', width: 20 },
          { header: 'الحالة', key: 'status', width: 15 },
          { header: 'التاريخ', key: 'createdAt', width: 20 },
          { header: 'الملاحظات', key: 'description', width: 30 }
        ];

        vouchers.forEach(voucher => {
          worksheet.addRow({
            serialNumber: voucher.serialNumber,
            type: voucher.type,
            amount: voucher.amount,
            clientName: voucher.clientId?.name || 'N/A',
            status: voucher.status,
            createdAt: voucher.createdAt.toLocaleDateString('ar-SA'),
            description: voucher.description
          });
        });

        res.setHeader('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        res.setHeader('Content-Disposition', `attachment; filename="vouchers-${Date.now()}.xlsx"`);
        
        await workbook.xlsx.write(res);
        res.end();
      } else {
        res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'تنسيق التصدير غير مدعوم'
        });
      }
    } catch (error) {
      logger.error('Error exporting vouchers:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get voucher template
   */
  static async getTemplate(req, res) {
    try {
      const { type } = req.params;

      const templates = {
        receipt: {
          title: 'سند قبض',
          fields: [
            { name: 'clientId', label: 'العميل', type: 'select', required: true },
            { name: 'amount', label: 'المبلغ', type: 'number', required: true },
            { name: 'description', label: 'الوصف', type: 'text', required: true },
            { name: 'paymentMethod', label: 'طريقة الدفع', type: 'select', required: true },
            { name: 'referenceNumber', label: 'رقم المرجع', type: 'text', required: false },
            { name: 'notes', label: 'ملاحظات', type: 'textarea', required: false }
          ]
        },
        payment: {
          title: 'سند صرف',
          fields: [
            { name: 'clientId', label: 'العميل', type: 'select', required: true },
            { name: 'amount', label: 'المبلغ', type: 'number', required: true },
            { name: 'description', label: 'الوصف', type: 'text', required: true },
            { name: 'paymentMethod', label: 'طريقة الدفع', type: 'select', required: true },
            { name: 'referenceNumber', label: 'رقم المرجع', type: 'text', required: false },
            { name: 'notes', label: 'ملاحظات', type: 'textarea', required: false }
          ]
        },
        transfer: {
          title: 'حوالة',
          fields: [
            { name: 'fromClientId', label: 'العميل المحول', type: 'select', required: true },
            { name: 'toClientId', label: 'العميل المستلم', type: 'select', required: true },
            { name: 'amount', label: 'المبلغ', type: 'number', required: true },
            { name: 'description', label: 'الوصف', type: 'text', required: true },
            { name: 'referenceNumber', label: 'رقم المرجع', type: 'text', required: false },
            { name: 'notes', label: 'ملاحظات', type: 'textarea', required: false }
          ]
        }
      };

      const template = templates[type];
      if (!template) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'قالب غير موجود'
        });
      }

      res.json({
        success: true,
        template
      });
    } catch (error) {
      logger.error('Error getting voucher template:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }
}

module.exports = VoucherController;
