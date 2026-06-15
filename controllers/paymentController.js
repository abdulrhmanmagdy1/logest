/**
 * ============================================
 * 💳 Payment Controller - نظام إدهام
 * Edham Logistics - Mobile Payment Processing Controller
 * ============================================
 */

const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY || 'sk_test_key');
const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');
const User = require('../models/User');
const Payment = require('../models/Payment');
const Transaction = require('../models/Transaction');
const Order = require('../models/Order');

class PaymentController {
  /**
   * Create payment intent for mobile apps
   */
  static async createPaymentIntent(req, res) {
    try {
      const { orderId, amount, currency = 'sar', paymentMethod = 'credit_card', metadata } = req.body;
      const userId = req.user.id;

      // Validate order if provided
      let order = null;
      if (orderId) {
        order = await Order.findById(orderId);
        if (!order) {
          return res.status(404).json({
            success: false,
            error: {
              code: 'ORDER_NOT_FOUND',
              message: 'Order not found'
            }
          });
        }
      }

      // Create payment intent with mobile-specific metadata
      const paymentIntent = await stripe.paymentIntents.create({
        amount: amount * 100, // Convert to cents/halalas
        currency,
        description: order ? `Payment for order ${order.order_number}` : 'Mobile payment',
        metadata: {
          userId: userId.toString(),
          orderId: orderId || '',
          paymentMethod,
          source: 'mobile_app',
          platform: metadata?.platform || 'unknown', // ios or android
          ...metadata
        },
        automatic_payment_methods: {
          enabled: true
        },
        // Enable Apple Pay and Google Pay
        payment_method_types: ['card'],
        // Add 3D Secure for mobile security
        confirmation_method: 'manual',
        confirm: false
      });

      // Create payment record
      const payment = new Payment({
        payment_intent_id: paymentIntent.id,
        user_id: userId,
        order_id: orderId || null,
        amount,
        currency,
        payment_method: paymentMethod,
        status: 'pending',
        source: 'mobile_app',
        metadata: {
          platform: metadata?.platform,
          device_id: metadata?.deviceId
        }
      });
      await payment.save();

      logger.success('Mobile payment intent created', { 
        paymentIntentId: paymentIntent.id,
        userId,
        orderId,
        amount,
        platform: metadata?.platform
      });
      
      res.json({
        success: true,
        data: {
          clientSecret: paymentIntent.client_secret,
          paymentIntentId: paymentIntent.id,
          paymentId: payment._id,
          amount,
          currency,
          paymentMethod,
          // Mobile-specific payment methods
          supportedMethods: {
            credit_card: true,
            apple_pay: metadata?.platform === 'ios',
            google_pay: metadata?.platform === 'android'
          }
        }
      });
    } catch (error) {
      logger.error('Create mobile payment intent error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        error: {
          code: 'PAYMENT_INTENT_FAILED',
          message: 'Failed to create payment intent',
          details: error.message
        }
      });
    }
  }

  /**
   * Confirm mobile payment
   */
  static async confirmPayment(req, res) {
    try {
      const { paymentIntentId, paymentMethodId } = req.body;
      const userId = req.user.id;

      // Retrieve payment intent
      const paymentIntent = await stripe.paymentIntents.retrieve(paymentIntentId);
      
      // Find payment record
      const payment = await Payment.findOne({ payment_intent_id: paymentIntentId });
      if (!payment) {
        return res.status(404).json({
          success: false,
          error: {
            code: 'PAYMENT_NOT_FOUND',
            message: 'Payment record not found'
          }
        });
      }

      // Confirm payment with payment method
      let confirmedPayment;
      if (paymentMethodId) {
        confirmedPayment = await stripe.paymentIntents.confirm(paymentIntentId, {
          payment_method: paymentMethodId
        });
      } else {
        confirmedPayment = paymentIntent;
      }

      // Update payment record
      payment.status = confirmedPayment.status === 'succeeded' ? 'completed' : 'failed';
      payment.completed_at = confirmedPayment.status === 'succeeded' ? new Date() : null;
      payment.stripe_response = confirmedPayment;
      await payment.save();

      // Create transaction record if payment succeeded
      if (confirmedPayment.status === 'succeeded') {
        const transaction = new Transaction({
          user_id: userId,
          payment_id: payment._id,
          order_id: payment.order_id,
          type: 'payment',
          amount: payment.amount,
          currency: payment.currency,
          description: `Mobile payment - ${payment.payment_method}`,
          status: 'completed',
          payment_method: payment.payment_method,
          source: 'mobile_app',
          metadata: payment.metadata
        });
        await transaction.save();

        // Update order status if payment is for an order
        if (payment.order_id) {
          await Order.findByIdAndUpdate(
            payment.order_id,
            { 
              'invoice.payment_status': 'paid',
              'invoice.paid_at': new Date(),
              'invoice.transaction_id': transaction._id
            }
          );
        }

        logger.success('Mobile payment confirmed successfully', { 
          paymentIntentId,
          userId,
          amount: payment.amount,
          orderId: payment.order_id
        });
      } else {
        logger.error('Mobile payment failed', { 
          paymentIntentId,
          userId,
          status: confirmedPayment.status,
          lastPaymentError: confirmedPayment.last_payment_error
        });
      }
      
      res.json({
        success: true,
        data: {
          status: confirmedPayment.status,
          amount: confirmedPayment.amount / 100,
          currency: confirmedPayment.currency,
          paymentId: payment._id,
          transactionId: confirmedPayment.status === 'succeeded' ? transaction._id : null,
          orderId: payment.order_id,
          // Mobile-specific response
          nextAction: confirmedPayment.status === 'succeeded' ? 'order_confirmed' : 'payment_failed',
          errorDetails: confirmedPayment.last_payment_error || null
        }
      });
    } catch (error) {
      logger.error('Confirm mobile payment error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        error: {
          code: 'PAYMENT_CONFIRMATION_FAILED',
          message: 'Failed to confirm payment',
          details: error.message
        }
      });
    }
  }

  /**
   * Create refund
   */
  static async createRefund(req, res) {
    try {
      const { paymentIntentId, amount, reason } = req.body;
      
      const refund = await stripe.refunds.create({
        payment_intent: paymentIntentId,
        amount: amount ? amount * 100 : undefined,
        reason: reason || 'requested_by_customer',
      });
      
      logger.success('Refund created', { refundId: refund.id });
      
      res.json({
        success: true,
        refundId: refund.id,
        status: refund.status,
        amount: refund.amount / 100,
      });
    } catch (error) {
      logger.error('Create refund error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: 'Error creating refund'
      });
    }
  }

  /**
   * Get mobile payment history
   */
  static async getPaymentHistory(req, res) {
    try {
      const { limit = 20, page = 1, status, startDate, endDate } = req.query;
      const userId = req.user.id;

      // Build filter
      const filter = { user_id: userId };
      if (status) filter.status = status;
      if (startDate || endDate) {
        filter.created_at = {};
        if (startDate) filter.created_at.$gte = new Date(startDate);
        if (endDate) filter.created_at.$lte = new Date(endDate);
      }

      // Get payments with pagination
      const payments = await Payment.find(filter)
        .populate('order_id', 'order_number status')
        .sort({ created_at: -1 })
        .limit(limit * 1)
        .skip((page - 1) * limit);

      const total = await Payment.countDocuments(filter);

      res.json({
        success: true,
        data: {
          payments: payments.map(payment => ({
            id: payment._id,
            amount: payment.amount,
            currency: payment.currency,
            status: payment.status,
            payment_method: payment.payment_method,
            created_at: payment.created_at,
            completed_at: payment.completed_at,
            order: payment.order_id,
            source: payment.source,
            metadata: payment.metadata
          })),
          pagination: {
            current: parseInt(page),
            total: Math.ceil(total / limit),
            count: payments.length,
            totalCount: total
          }
        }
      });
    } catch (error) {
      logger.error('Get mobile payment history error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        error: {
          code: 'FETCH_PAYMENT_HISTORY_FAILED',
          message: 'Failed to fetch payment history'
        }
      });
    }
  }

  /**
   * Webhook handler
   */
  static async webhook(req, res) {
    const sig = req.headers['stripe-signature'];
    const webhookSecret = process.env.STRIPE_WEBHOOK_SECRET;
    
    let event;
    
    try {
      event = stripe.webhooks.constructEvent(req.body, sig, webhookSecret);
    } catch (err) {
      logger.error('Webhook signature verification failed', err);
      return res.status(400).send('Webhook signature verification failed');
    }
    
    switch (event.type) {
      case 'payment_intent.succeeded':
        logger.success('Payment succeeded', { paymentIntent: event.data.object.id });
        break;
      case 'payment_intent.payment_failed':
        logger.error('Payment failed', { paymentIntent: event.data.object.id });
        break;
      default:
        logger.info(`Unhandled event type ${event.type}`);
    }
    
    res.json({ received: true });
  }

  /**
   * Get client balance
   */
  static async getBalance(req, res) {
    try {
      const user = await User.findById(req.user.id);
      
      if (!user) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.USER_NOT_FOUND
        });
      }

      res.json({
        success: true,
        balance: user.balance || 0,
        currency: 'SAR'
      });
    } catch (error) {
      logger.error('Error getting balance:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Add funds to client balance
   */
  static async addFunds(req, res) {
    try {
      const { amount, paymentMethodId } = req.body;

      if (!amount || amount <= 0) {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'المبلغ غير صحيح'
        });
      }

      const user = await User.findById(req.user.id);
      
      if (!user) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.USER_NOT_FOUND
        });
      }

      // Process payment with Stripe
      const paymentIntent = await stripe.paymentIntents.create({
        amount: amount * 100,
        currency: 'sar',
        payment_method: paymentMethodId,
        confirmation_method: 'manual',
        confirm: true,
        metadata: {
          userId: user._id.toString(),
          type: 'balance_topup'
        }
      });

      if (paymentIntent.status === 'succeeded') {
        // Update user balance
        user.balance = (user.balance || 0) + amount;
        await user.save();

        // Create transaction record
        const transaction = new Transaction({
          userId: user._id,
          type: 'credit',
          amount,
          description: 'إضافة رصيد',
          status: 'completed',
          paymentIntentId: paymentIntent.id,
          metadata: {
            type: 'balance_topup'
          }
        });
        await transaction.save();

        logger.success('Balance topped up successfully', { 
          userId: user._id, 
          amount,
          newBalance: user.balance 
        });

        res.json({
          success: true,
          message: 'تم إضافة الرصيد بنجاح',
          balance: user.balance,
          transactionId: transaction._id
        });
      } else {
        res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'فشل عملية الدفع',
          status: paymentIntent.status
        });
      }
    } catch (error) {
      logger.error('Error adding funds:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Process payment for order with multiple payment methods
   */
  static async processPayment(req, res) {
    try {
      const { orderId, amount, paymentMethod, paymentMethodId, items } = req.body;

      if (!amount || amount <= 0) {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'المبلغ غير صحيح'
        });
      }

      const user = await User.findById(req.user.id);
      
      if (!user) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.USER_NOT_FOUND
        });
      }

      let paymentResult;

      switch (paymentMethod) {
        case 'balance':
          // Check if user has sufficient balance
          if ((user.balance || 0) < amount) {
            return res.status(HTTP_STATUS.BAD_REQUEST).json({
              success: false,
              message: 'الرصيد غير كافي'
            });
          }

          // Deduct from balance
          user.balance -= amount;
          await user.save();

          // Create transaction record
          const transaction = new Transaction({
            userId: user._id,
            orderId,
            type: 'debit',
            amount,
            description: `دفع طلب #${orderId}`,
            status: 'completed',
            paymentMethod: 'balance',
            items
          });
          await transaction.save();

          paymentResult = {
            success: true,
            method: 'balance',
            transactionId: transaction._id,
            remainingBalance: user.balance
          };
          break;

        case 'card':
          // Process with Stripe
          const paymentIntent = await stripe.paymentIntents.create({
            amount: amount * 100,
            currency: 'sar',
            payment_method: paymentMethodId,
            confirmation_method: 'manual',
            confirm: true,
            metadata: {
              userId: user._id.toString(),
              orderId,
              type: 'order_payment'
            }
          });

          if (paymentIntent.status === 'succeeded') {
            const transaction = new Transaction({
              userId: user._id,
              orderId,
              type: 'payment',
              amount,
              description: `دفع طلب #${orderId}`,
              status: 'completed',
              paymentMethod: 'card',
              paymentIntentId: paymentIntent.id,
              items
            });
            await transaction.save();

            paymentResult = {
              success: true,
              method: 'card',
              transactionId: transaction._id,
              stripePaymentIntentId: paymentIntent.id
            };
          } else {
            return res.status(HTTP_STATUS.BAD_REQUEST).json({
              success: false,
              message: 'فشل عملية الدفع بالبطاقة',
              status: paymentIntent.status
            });
          }
          break;

        case 'cash':
          // Create pending transaction for cash on delivery
          const cashTransaction = new Transaction({
            userId: user._id,
            orderId,
            type: 'pending',
            amount,
            description: `دفع عند الاستلام طلب #${orderId}`,
            status: 'pending',
            paymentMethod: 'cash',
            items
          });
          await cashTransaction.save();

          paymentResult = {
            success: true,
            method: 'cash',
            transactionId: cashTransaction._id,
            status: 'pending'
          };
          break;

        default:
          return res.status(HTTP_STATUS.BAD_REQUEST).json({
            success: false,
            message: 'طريقة الدفع غير مدعومة'
          });
      }

      logger.success('Payment processed successfully', {
        userId: user._id,
        orderId,
        amount,
        paymentMethod,
        result: paymentResult
      });

      res.json({
        success: true,
        message: 'تمت معالجة الدفع بنجاح',
        payment: paymentResult
      });

    } catch (error) {
      logger.error('Error processing payment:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get available payment methods
   */
  static async getPaymentMethods(req, res) {
    try {
      const paymentMethods = [
        {
          id: 'balance',
          name: 'الرصيد',
          description: 'استخدام الرصيد المتاح',
          icon: 'wallet',
          enabled: true
        },
        {
          id: 'card',
          name: 'بطاقة ائتمانية',
          description: 'الدفع بالبطاقة الائتمانية',
          icon: 'credit-card',
          enabled: true
        },
        {
          id: 'cash',
          name: 'دفع عند الاستلام',
          description: 'الدفع نقداً عند استلام الخدمة',
          icon: 'cash',
          enabled: true
        }
      ];

      res.json({
        success: true,
        paymentMethods
      });
    } catch (error) {
      logger.error('Error getting payment methods:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get payment statistics (accountant/admin)
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
        totalRevenue,
        totalTransactions,
        paymentMethodStats,
        dailyStats
      ] = await Promise.all([
        Transaction.aggregate([
          { $match: { ...matchQuery, status: 'completed' } },
          { $group: { _id: null, total: { $sum: '$amount' } } }
        ]),
        Transaction.countDocuments(matchQuery),
        Transaction.aggregate([
          { $match: { ...matchQuery, status: 'completed' } },
          { $group: { _id: '$paymentMethod', count: { $sum: 1 }, total: { $sum: '$amount' } } }
        ]),
        Transaction.aggregate([
          { $match: { ...matchQuery, status: 'completed' } },
          {
            $group: {
              _id: {
                year: { $year: '$createdAt' },
                month: { $month: '$createdAt' },
                day: { $dayOfMonth: '$createdAt' }
              },
              total: { $sum: '$amount' },
              count: { $sum: 1 }
            }
          },
          { $sort: { '_id.year': 1, '_id.month': 1, '_id.day': 1 } }
        ])
      ]);

      res.json({
        success: true,
        statistics: {
          totalRevenue: totalRevenue[0]?.total || 0,
          totalTransactions,
          paymentMethods: paymentMethodStats,
          dailyStats,
          currency: 'SAR'
        }
      });

    } catch (error) {
      logger.error('Error getting payment statistics:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }
}

module.exports = PaymentController;
