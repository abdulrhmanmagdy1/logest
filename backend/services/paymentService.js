//
/**
 * ============================================
 * 💳 Payment Service - نظام الدفع والفوترة
 * ============================================
 */

const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
const logger = require('../utils/logger');

class PaymentService {
  constructor() {
    this.supportedMethods = ['card', 'bank_transfer', 'paypal', 'apple_pay', 'stc_pay'];
  }

  /**
   * Process payment for invoice or subscription
   */
  async processPayment(data) {
    try {
      const { amount, currency = 'SAR', method, customer, metadata = {} } = data;

      let paymentResult;

      switch (method) {
        case 'card':
          paymentResult = await this.processCardPayment(data);
          break;
        case 'bank_transfer':
          paymentResult = await this.processBankTransfer(data);
          break;
        case 'paypal':
          paymentResult = await this.processPayPalPayment(data);
          break;
        case 'apple_pay':
          paymentResult = await this.processApplePay(data);
          break;
        case 'stc_pay':
          paymentResult = await this.processSTCPay(data);
          break;
        default:
          throw new Error(`Unsupported payment method: ${method}`);
      }

      // Log successful payment
      logger.info('Payment processed successfully', {
        paymentId: paymentResult.id,
        amount,
        currency,
        method,
        customer: customer.id
      });

      return {
        success: true,
        paymentId: paymentResult.id,
        status: paymentResult.status,
        amount,
        currency,
        method,
        receiptUrl: paymentResult.receiptUrl,
        timestamp: new Date()
      };
    } catch (error) {
      logger.error('Payment processing error:', error);
      throw error;
    }
  }

  /**
   * Process card payment via Stripe
   */
  async processCardPayment(data) {
    const { amount, currency, token, customer, saveCard = false } = data;

    // Create or retrieve customer
    let stripeCustomer;
    if (customer.stripeCustomerId) {
      stripeCustomer = await stripe.customers.retrieve(customer.stripeCustomerId);
    } else {
      stripeCustomer = await stripe.customers.create({
        email: customer.email,
        name: customer.name,
        phone: customer.phone
      });
    }

    // Create payment intent
    const paymentIntent = await stripe.paymentIntents.create({
      amount: Math.round(amount * 100), // Convert to cents/halalas
      currency: currency.toLowerCase(),
      customer: stripeCustomer.id,
      payment_method: token,
      confirm: true,
      setup_future_usage: saveCard ? 'off_session' : undefined,
      metadata: {
        companyId: customer.companyId,
        invoiceId: data.invoiceId
      }
    });

    return {
      id: paymentIntent.id,
      status: paymentIntent.status,
      receiptUrl: paymentIntent.charges?.data[0]?.receipt_url
    };
  }

  /**
   * Create subscription
   */
  async createSubscription(data) {
    try {
      const { customer, plan, billingCycle } = data;

      // Get or create Stripe customer
      let stripeCustomerId = customer.stripeCustomerId;
      if (!stripeCustomerId) {
        const stripeCustomer = await stripe.customers.create({
          email: customer.email,
          name: customer.name,
          phone: customer.phone,
          metadata: {
            companyId: customer.companyId
          }
        });
        stripeCustomerId = stripeCustomer.id;
      }

      // Create subscription
      const priceId = billingCycle === 'yearly' ? plan.stripeYearlyPriceId : plan.stripeMonthlyPriceId;
      
      const subscription = await stripe.subscriptions.create({
        customer: stripeCustomerId,
        items: [{ price: priceId }],
        payment_behavior: 'default_incomplete',
        expand: ['latest_invoice.payment_intent']
      });

      return {
        success: true,
        subscriptionId: subscription.id,
        status: subscription.status,
        clientSecret: subscription.latest_invoice?.payment_intent?.client_secret,
        stripeCustomerId
      };
    } catch (error) {
      logger.error('Subscription creation error:', error);
      throw error;
    }
  }

  /**
   * Cancel subscription
   */
  async cancelSubscription(subscriptionId, immediate = false) {
    try {
      if (immediate) {
        const subscription = await stripe.subscriptions.cancel(subscriptionId);
        return { success: true, status: subscription.status };
      } else {
        // Cancel at period end
        const subscription = await stripe.subscriptions.update(subscriptionId, {
          cancel_at_period_end: true
        });
        return { 
          success: true, 
          status: subscription.status,
          cancelAt: new Date(subscription.cancel_at * 1000)
        };
      }
    } catch (error) {
      logger.error('Subscription cancellation error:', error);
      throw error;
    }
  }

  /**
   * Process refund
   */
  async processRefund(paymentId, amount = null, reason = null) {
    try {
      const refundData = {
        payment_intent: paymentId,
        reason: reason || 'requested_by_customer'
      };

      if (amount) {
        refundData.amount = Math.round(amount * 100);
      }

      const refund = await stripe.refunds.create(refundData);

      return {
        success: true,
        refundId: refund.id,
        amount: refund.amount / 100,
        status: refund.status,
        timestamp: new Date()
      };
    } catch (error) {
      logger.error('Refund processing error:', error);
      throw error;
    }
  }

  /**
   * Get payment history
   */
  async getPaymentHistory(customerId, limit = 10) {
    try {
      const charges = await stripe.charges.list({
        customer: customerId,
        limit
      });

      return charges.data.map(charge => ({
        id: charge.id,
        amount: charge.amount / 100,
        currency: charge.currency,
        status: charge.status,
        description: charge.description,
        receiptUrl: charge.receipt_url,
        createdAt: new Date(charge.created * 1000)
      }));
    } catch (error) {
      logger.error('Get payment history error:', error);
      throw error;
    }
  }

  /**
   * Webhook handler for Stripe events
   */
  async handleWebhook(event) {
    try {
      switch (event.type) {
        case 'payment_intent.succeeded':
          await this.handlePaymentSuccess(event.data.object);
          break;
        
        case 'payment_intent.payment_failed':
          await this.handlePaymentFailure(event.data.object);
          break;
        
        case 'invoice.payment_succeeded':
          await this.handleInvoicePaymentSuccess(event.data.object);
          break;
        
        case 'customer.subscription.updated':
          await this.handleSubscriptionUpdate(event.data.object);
          break;
        
        case 'customer.subscription.deleted':
          await this.handleSubscriptionCancellation(event.data.object);
          break;
        
        default:
          logger.info(`Unhandled webhook event: ${event.type}`);
      }

      return { received: true };
    } catch (error) {
      logger.error('Webhook handling error:', error);
      throw error;
    }
  }

  async handlePaymentSuccess(paymentIntent) {
    // Update invoice status, send confirmation email, etc.
    logger.info('Payment succeeded', { paymentIntentId: paymentIntent.id });
    
    // Update database records
    const Invoice = require('../models/Invoice');
    await Invoice.findOneAndUpdate(
      { 'payment.stripePaymentIntentId': paymentIntent.id },
      {
        status: 'paid',
        'payment.paidAt': new Date(),
        'payment.status': 'succeeded'
      }
    );
  }

  async handlePaymentFailure(paymentIntent) {
    logger.error('Payment failed', { 
      paymentIntentId: paymentIntent.id,
      error: paymentIntent.last_payment_error 
    });
    
    // Notify customer, update records
    const Invoice = require('../models/Invoice');
    await Invoice.findOneAndUpdate(
      { 'payment.stripePaymentIntentId': paymentIntent.id },
      {
        'payment.status': 'failed',
        'payment.failureMessage': paymentIntent.last_payment_error?.message
      }
    );
  }

  async handleInvoicePaymentSuccess(invoice) {
    logger.info('Invoice payment succeeded', { invoiceId: invoice.id });
  }

  async handleSubscriptionUpdate(subscription) {
    logger.info('Subscription updated', { subscriptionId: subscription.id });
    
    // Update subscription in database
    const Subscription = require('../models/Subscription').Subscription;
    await Subscription.findOneAndUpdate(
      { stripeSubscriptionId: subscription.id },
      {
        status: subscription.status,
        currentPeriodEnd: new Date(subscription.current_period_end * 1000)
      }
    );
  }

  async handleSubscriptionCancellation(subscription) {
    logger.info('Subscription cancelled', { subscriptionId: subscription.id });
    
    const Subscription = require('../models/Subscription').Subscription;
    await Subscription.findOneAndUpdate(
      { stripeSubscriptionId: subscription.id },
      {
        status: 'cancelled',
        'cancellation.cancelledAt': new Date()
      }
    );
  }
}

module.exports = new PaymentService();
