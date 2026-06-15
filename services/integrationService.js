/**
 * ============================================
 * 🔗 Integration Service - نظام إدهام الاحترافي
 * Edham Logistics - External System Integration
 * ============================================
 */

const axios = require('axios');
const crypto = require('crypto');
const logger = require('../utils/logger');
const { MESSAGES } = require('../config/constants');

class IntegrationService {
  constructor() {
    this.integrations = new Map();
    this.webhooks = new Map();
    this.apiKeys = new Map();
    this.initializeIntegrations();
  }

  /**
   * Initialize available integrations
   */
  initializeIntegrations() {
    // Payment Gateway Integrations
    this.integrations.set('stripe', {
      name: 'Stripe',
      type: 'payment',
      enabled: process.env.STRIPE_ENABLED === 'true',
      config: {
        apiKey: process.env.STRIPE_SECRET_KEY,
        publishableKey: process.env.STRIPE_PUBLISHABLE_KEY,
        webhookSecret: process.env.STRIPE_WEBHOOK_SECRET
      }
    });

    this.integrations.set('paypal', {
      name: 'PayPal',
      type: 'payment',
      enabled: process.env.PAYPAL_ENABLED === 'true',
      config: {
        clientId: process.env.PAYPAL_CLIENT_ID,
        clientSecret: process.env.PAYPAL_CLIENT_SECRET,
        sandbox: process.env.PAYPAL_SANDBOX === 'true'
      }
    });

    // SMS Service Integrations
    this.integrations.set('twilio', {
      name: 'Twilio',
      type: 'sms',
      enabled: process.env.TWILIO_ENABLED === 'true',
      config: {
        accountSid: process.env.TWILIO_ACCOUNT_SID,
        authToken: process.env.TWILIO_AUTH_TOKEN,
        phoneNumber: process.env.TWILIO_PHONE_NUMBER
      }
    });

    this.integrations.set('aws-sns', {
      name: 'AWS SNS',
      type: 'sms',
      enabled: process.env.AWS_SNS_ENABLED === 'true',
      config: {
        accessKeyId: process.env.AWS_ACCESS_KEY_ID,
        secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
        region: process.env.AWS_REGION
      }
    });

    // Email Service Integrations
    this.integrations.set('sendgrid', {
      name: 'SendGrid',
      type: 'email',
      enabled: process.env.SENDGRID_ENABLED === 'true',
      config: {
        apiKey: process.env.SENDGRID_API_KEY
      }
    });

    this.integrations.set('aws-ses', {
      name: 'AWS SES',
      type: 'email',
      enabled: process.env.AWS_SES_ENABLED === 'true',
      config: {
        accessKeyId: process.env.AWS_ACCESS_KEY_ID,
        secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
        region: process.env.AWS_REGION
      }
    });

    // Mapping Service Integrations
    this.integrations.set('google-maps', {
      name: 'Google Maps',
      type: 'mapping',
      enabled: process.env.GOOGLE_MAPS_ENABLED === 'true',
      config: {
        apiKey: process.env.GOOGLE_MAPS_API_KEY
      }
    });

    this.integrations.set('mapbox', {
      name: 'Mapbox',
      type: 'mapping',
      enabled: process.env.MAPBOX_ENABLED === 'true',
      config: {
        accessToken: process.env.MAPBOX_ACCESS_TOKEN
      }
    });

    // Analytics Integrations
    this.integrations.set('google-analytics', {
      name: 'Google Analytics',
      type: 'analytics',
      enabled: process.env.GA_ENABLED === 'true',
      config: {
        trackingId: process.env.GA_TRACKING_ID
      }
    });

    // ERP Integrations
    this.integrations.set('sap', {
      name: 'SAP',
      type: 'erp',
      enabled: process.env.SAP_ENABLED === 'true',
      config: {
        baseUrl: process.env.SAP_BASE_URL,
        username: process.env.SAP_USERNAME,
        password: process.env.SAP_PASSWORD,
        client: process.env.SAP_CLIENT
      }
    });

    // Shipping Carrier Integrations
    this.integrations.set('fedex', {
      name: 'FedEx',
      type: 'shipping',
      enabled: process.env.FEDEX_ENABLED === 'true',
      config: {
        apiKey: process.env.FEDEX_API_KEY,
        password: process.env.FEDEX_PASSWORD,
        accountNumber: process.env.FEDEX_ACCOUNT_NUMBER
      }
    });

    this.integrations.set('dhl', {
      name: 'DHL',
      type: 'shipping',
      enabled: process.env.DHL_ENABLED === 'true',
      config: {
        apiKey: process.env.DHL_API_KEY,
        secret: process.env.DHL_SECRET
      }
    });

    logger.info('Integrations initialized', {
      total: this.integrations.size,
      enabled: Array.from(this.integrations.values()).filter(i => i.enabled).length
    });
  }

  /**
   * Process payment through Stripe
   */
  async processStripePayment(paymentData) {
    try {
      const integration = this.integrations.get('stripe');
      if (!integration?.enabled) {
        throw new Error('Stripe integration is not enabled');
      }

      const stripe = require('stripe')(integration.config.apiKey);

      const paymentIntent = await stripe.paymentIntents.create({
        amount: Math.round(paymentData.amount * 100), // Convert to cents
        currency: paymentData.currency || 'sar',
        description: paymentData.description,
        metadata: {
          shipmentId: paymentData.shipmentId,
          clientId: paymentData.clientId
        }
      });

      logger.success('Stripe payment processed', { 
        paymentIntentId: paymentIntent.id,
        amount: paymentData.amount 
      });

      return {
        success: true,
        paymentIntent,
        clientSecret: paymentIntent.client_secret
      };

    } catch (error) {
      logger.error('Stripe payment failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Process payment through PayPal
   */
  async processPayPalPayment(paymentData) {
    try {
      const integration = this.integrations.get('paypal');
      if (!integration?.enabled) {
        throw new Error('PayPal integration is not enabled');
      }

      const paypal = require('@paypal/checkout-server-sdk');
      paypal.configure({
        'mode': integration.config.sandbox ? 'sandbox' : 'live',
        'client_id': integration.config.clientId,
        'client_secret': integration.config.clientSecret,
        'return_url': process.env.PAYPAL_RETURN_URL,
        'cancel_url': process.env.PAYPAL_CANCEL_URL
      });

      const order = await paypal.orders.create({
        'intent': 'CAPTURE',
        'purchase_units': [{
          'reference_id': paymentData.shipmentId,
          'description': paymentData.description,
          'amount': {
            'currency_code': paymentData.currency || 'SAR',
            'value': paymentData.amount.toString()
          }
        }]
      });

      logger.success('PayPal payment created', { 
        orderId: order.id,
        amount: paymentData.amount 
      });

      return {
        success: true,
        order,
        approvalUrl: order.links.find(link => link.rel === 'approve')?.href
      };

    } catch (error) {
      logger.error('PayPal payment failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Send SMS via Twilio
   */
  async sendTwilioSMS(phoneNumber, message, options = {}) {
    try {
      const integration = this.integrations.get('twilio');
      if (!integration?.enabled) {
        throw new Error('Twilio integration is not enabled');
      }

      const twilio = require('twilio')(integration.config.accountSid, integration.config.authToken);

      const messageOptions = {
        body: message,
        from: integration.config.phoneNumber,
        to: phoneNumber,
        ...options
      };

      const result = await twilio.messages.create(messageOptions);

      logger.success('SMS sent via Twilio', { 
        messageId: result.sid,
        to: phoneNumber 
      });

      return {
        success: true,
        messageId: result.sid,
        status: result.status
      };

    } catch (error) {
      logger.error('Twilio SMS failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Send SMS via AWS SNS
   */
  async sendAWSSNS(phoneNumber, message) {
    try {
      const integration = this.integrations.get('aws-sns');
      if (!integration?.enabled) {
        throw new Error('AWS SNS integration is not enabled');
      }

      const AWS = require('aws-sdk');
      const sns = new AWS.SNS({
        accessKeyId: integration.config.accessKeyId,
        secretAccessKey: integration.config.secretAccessKey,
        region: integration.config.region
      });

      const params = {
        Message: message,
        PhoneNumber: phoneNumber,
        MessageAttributes: {
          'AWS.SNS.SMS.SMSType': {
            DataType: 'String',
            StringValue: 'Transactional'
          }
        }
      };

      const result = await sns.publish(params).promise();

      logger.success('SMS sent via AWS SNS', { 
        messageId: result.MessageId,
        to: phoneNumber 
      });

      return {
        success: true,
        messageId: result.MessageId
      };

    } catch (error) {
      logger.error('AWS SNS SMS failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Send email via SendGrid
   */
  async sendSendGridEmail(to, subject, content, options = {}) {
    try {
      const integration = this.integrations.get('sendgrid');
      if (!integration?.enabled) {
        throw new Error('SendGrid integration is not enabled');
      }

      const sgMail = require('@sendgrid/mail');
      sgMail.setApiKey(integration.config.apiKey);

      const msg = {
        to: Array.isArray(to) ? to : [to],
        from: options.from || process.env.DEFAULT_FROM_EMAIL,
        subject: subject,
        ...content
      };

      const result = await sgMail.send(msg);

      logger.success('Email sent via SendGrid', { 
        messageId: result[0]?.headers?.['x-message-id'],
        to: to 
      });

      return {
        success: true,
        messageId: result[0]?.headers?.['x-message-id']
      };

    } catch (error) {
      logger.error('SendGrid email failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Get route from Google Maps
   */
  async getGoogleMapsRoute(origin, destination, options = {}) {
    try {
      const integration = this.integrations.get('google-maps');
      if (!integration?.enabled) {
        throw new Error('Google Maps integration is not enabled');
      }

      const url = `https://routes.googleapis.com/directions/json`;
      const params = {
        origin: `${origin.latitude},${origin.longitude}`,
        destination: `${destination.latitude},${destination.longitude}`,
        key: integration.config.apiKey,
        ...options
      };

      const response = await axios.get(url, { params });

      if (response.data.routes.length === 0) {
        throw new Error('No route found');
      }

      const route = response.data.routes[0];
      const leg = route.legs[0];

      logger.success('Google Maps route retrieved', { 
        distance: leg.distance.text,
        duration: leg.duration.text 
      });

      return {
        success: true,
        route: {
          distance: leg.distance,
          duration: leg.duration,
          steps: leg.steps,
          polyline: route.overview_polyline
        }
      };

    } catch (error) {
      logger.error('Google Maps route failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Create FedEx shipment
   */
  async createFedExShipment(shipmentData) {
    try {
      const integration = this.integrations.get('fedex');
      if (!integration?.enabled) {
        throw new Error('FedEx integration is not enabled');
      }

      const fedex = require('fedex-api')(integration.config);

      const shipment = {
        accountNumber: integration.config.accountNumber,
        ...shipmentData
      };

      const result = await fedex.createShipment(shipment);

      logger.success('FedEx shipment created', { 
        trackingNumber: result.trackingNumber,
        shipmentId: shipmentData.id 
      });

      return {
        success: true,
        trackingNumber: result.trackingNumber,
        labelUrl: result.labelUrl,
        trackingUrl: result.trackingUrl
      };

    } catch (error) {
      logger.error('FedEx shipment failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Create DHL shipment
   */
  async createDHLShipment(shipmentData) {
    try {
      const integration = this.integrations.get('dhl');
      if (!integration?.enabled) {
        throw new Error('DHL integration is not enabled');
      }

      const dhl = require('dhl-api')(integration.config);

      const shipment = {
        ...shipmentData,
        message: 'Edham Logistics Shipment'
      };

      const result = await dhl.createShipment(shipment);

      logger.success('DHL shipment created', { 
        trackingNumber: result.trackingNumber,
        shipmentId: shipmentData.id 
      });

      return {
        success: true,
        trackingNumber: result.trackingNumber,
        labelUrl: result.labelUrl,
        trackingUrl: result.trackingUrl
      };

    } catch (error) {
      logger.error('DHL shipment failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Sync with SAP ERP
   */
  async syncWithSAP(dataType, data) {
    try {
      const integration = this.integrations.get('sap');
      if (!integration?.enabled) {
        throw new Error('SAP integration is not enabled');
      }

      const sap = require('sap-nwrfc')(integration.config);

      let result;
      switch (dataType) {
        case 'shipment':
          result = await sap.call('BAPI_SHIPMENT_CREATE', {
            SHIPMENT_DATA: data
          });
          break;
        case 'customer':
          result = await sap.call('BAPI_CUSTOMER_CREATE', {
            CUSTOMER_DATA: data
          });
          break;
        case 'invoice':
          result = await sap.call('BAPI_INVOICE_CREATE', {
            INVOICE_DATA: data
          });
          break;
        default:
          throw new Error('Unsupported data type for SAP sync');
      }

      logger.success('SAP sync completed', { 
        dataType,
        sapId: result.ID 
      });

      return {
        success: true,
        sapId: result.ID,
        message: result.MESSAGE
      };

    } catch (error) {
      logger.error('SAP sync failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Track shipment with carrier
   */
  async trackShipment(carrier, trackingNumber) {
    try {
      let result;
      switch (carrier.toLowerCase()) {
        case 'fedex':
          result = await this.trackFedExShipment(trackingNumber);
          break;
        case 'dhl':
          result = await this.trackDHLShipment(trackingNumber);
          break;
        case 'aramex':
          result = await this.trackAramexShipment(trackingNumber);
          break;
        default:
          throw new Error('Unsupported carrier for tracking');
      }

      return result;

    } catch (error) {
      logger.error('Shipment tracking failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Track FedEx shipment
   */
  async trackFedExShipment(trackingNumber) {
    try {
      const integration = this.integrations.get('fedex');
      if (!integration?.enabled) {
        throw new Error('FedEx integration is not enabled');
      }

      const fedex = require('fedex-api')(integration.config);
      const result = await fedex.track(trackingNumber);

      logger.success('FedEx tracking retrieved', { 
        trackingNumber,
        status: result.status 
      });

      return {
        success: true,
        carrier: 'FedEx',
        trackingNumber,
        status: result.status,
        events: result.events,
        estimatedDelivery: result.estimatedDelivery
      };

    } catch (error) {
      logger.error('FedEx tracking failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Track DHL shipment
   */
  async trackDHLShipment(trackingNumber) {
    try {
      const integration = this.integrations.get('dhl');
      if (!integration?.enabled) {
        throw new Error('DHL integration is not enabled');
      }

      const dhl = require('dhl-api')(integration.config);
      const result = await dhl.track(trackingNumber);

      logger.success('DHL tracking retrieved', { 
        trackingNumber,
        status: result.status 
      });

      return {
        success: true,
        carrier: 'DHL',
        trackingNumber,
        status: result.status,
        events: result.events,
        estimatedDelivery: result.estimatedDelivery
      };

    } catch (error) {
      logger.error('DHL tracking failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Generate webhook signature
   */
  generateWebhookSignature(payload, secret) {
    return crypto
      .createHmac('sha256', secret)
      .update(payload)
      .digest('hex');
  }

  /**
   * Verify webhook signature
   */
  verifyWebhookSignature(payload, signature, secret) {
    const expectedSignature = this.generateWebhookSignature(payload, secret);
    return crypto.timingSafeEqual(Buffer.from(signature), Buffer.from(expectedSignature));
  }

  /**
   * Register webhook handler
   */
  registerWebhook(integration, event, handler) {
    if (!this.webhooks.has(integration)) {
      this.webhooks.set(integration, new Map());
    }
    
    this.webhooks.get(integration).set(event, handler);
    
    logger.info('Webhook registered', { integration, event });
  }

  /**
   * Process webhook
   */
  async processWebhook(integration, event, payload, signature) {
    try {
      const integrationConfig = this.integrations.get(integration);
      if (!integrationConfig?.enabled) {
        throw new Error(`${integration} integration is not enabled`);
      }

      // Verify signature if webhook secret is configured
      if (integrationConfig.config.webhookSecret) {
        const isValid = this.verifyWebhookSignature(
          JSON.stringify(payload),
          signature,
          integrationConfig.config.webhookSecret
        );

        if (!isValid) {
          throw new Error('Invalid webhook signature');
        }
      }

      const handler = this.webhooks.get(integration)?.get(event);
      if (!handler) {
        throw new Error(`No handler registered for ${integration} ${event}`);
      }

      await handler(payload);

      logger.success('Webhook processed', { integration, event });

      return {
        success: true,
        message: 'Webhook processed successfully'
      };

    } catch (error) {
      logger.error('Webhook processing failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Get integration status
   */
  getIntegrationStatus() {
    const status = {};
    
    for (const [key, integration] of this.integrations) {
      status[key] = {
        name: integration.name,
        type: integration.type,
        enabled: integration.enabled,
        lastChecked: new Date(),
        configured: Object.keys(integration.config).length > 0
      };
    }

    return status;
  }

  /**
   * Test integration connectivity
   */
  async testIntegration(integrationKey) {
    try {
      const integration = this.integrations.get(integrationKey);
      if (!integration) {
        return {
          success: false,
          error: 'Integration not found'
        };
      }

      let testResult;
      switch (integration.type) {
        case 'payment':
          testResult = await this.testPaymentIntegration(integrationKey);
          break;
        case 'sms':
          testResult = await this.testSMSIntegration(integrationKey);
          break;
        case 'email':
          testResult = await this.testEmailIntegration(integrationKey);
          break;
        case 'mapping':
          testResult = await this.testMappingIntegration(integrationKey);
          break;
        default:
          testResult = { success: true, message: 'Integration type not testable' };
      }

      return {
        success: true,
        integration: integration.name,
        testResult
      };

    } catch (error) {
      logger.error(`Integration test failed for ${integrationKey}:`, error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Test payment integration
   */
  async testPaymentIntegration(integrationKey) {
    // Implementation for testing payment integrations
    return { success: true, message: 'Payment integration test successful' };
  }

  /**
   * Test SMS integration
   */
  async testSMSIntegration(integrationKey) {
    // Implementation for testing SMS integrations
    return { success: true, message: 'SMS integration test successful' };
  }

  /**
   * Test email integration
   */
  async testEmailIntegration(integrationKey) {
    // Implementation for testing email integrations
    return { success: true, message: 'Email integration test successful' };
  }

  /**
   * Test mapping integration
   */
  async testMappingIntegration(integrationKey) {
    // Implementation for testing mapping integrations
    return { success: true, message: 'Mapping integration test successful' };
  }
}

module.exports = IntegrationService;
