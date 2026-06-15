//
/**
 * ============================================
 * 🪝 Webhook Service - خدمة إرسال الويب هوك
 * ============================================
 */

const axios = require('axios');
const crypto = require('crypto');
const { Webhook, WebhookDelivery } = require('../models/Webhook');
const logger = require('../utils/logger');

class WebhookService {
  constructor() {
    this.retryQueue = [];
    this.processing = false;
  }

  /**
   * Trigger a webhook for a specific event
   */
  async triggerEvent(event, data, companyId) {
    try {
      // Find all webhooks subscribed to this event (or all events)
      const webhooks = await Webhook.find({
        company: companyId,
        status: 'active',
        events: { $in: [event, '*'] }
      });

      logger.info(`Triggering ${event} for ${webhooks.length} webhooks`);

      // Send to all matching webhooks
      const deliveries = await Promise.all(
        webhooks.map(webhook => this.triggerWebhook(webhook, {
          event,
          timestamp: new Date().toISOString(),
          data
        }))
      );

      return deliveries;
    } catch (error) {
      logger.error('Trigger event error:', error);
      throw error;
    }
  }

  /**
   * Send webhook to a specific endpoint
   */
  async triggerWebhook(webhook, payload) {
    try {
      const startTime = Date.now();

      // Generate signature
      const signature = webhook.generateSignature(payload);

      // Prepare headers
      const headers = {
        'Content-Type': 'application/json',
        'X-Webhook-Signature': signature,
        'X-Webhook-ID': webhook._id.toString(),
        'X-Webhook-Event': payload.event,
        'User-Agent': 'Edham-Logistics-Webhook/1.0'
      };

      // Add custom headers
      if (webhook.headers && webhook.headers.length > 0) {
        webhook.headers.forEach(h => {
          headers[h.key] = h.value;
        });
      }

      // Create delivery record
      const delivery = await WebhookDelivery.create({
        webhook: webhook._id,
        event: payload.event,
        payload,
        request: {
          method: 'POST',
          headers,
          body: payload,
          timestamp: new Date()
        }
      });

      // Send request
      let response;
      try {
        response = await axios.post(webhook.url, payload, {
          headers,
          timeout: 30000, // 30 seconds
          maxRedirects: 5
        });

        // Update delivery success
        delivery.status = 'delivered';
        delivery.response = {
          statusCode: response.status,
          headers: response.headers,
          body: JSON.stringify(response.data).substring(0, 10000), // Limit size
          responseTime: Date.now() - startTime,
          timestamp: new Date()
        };

        // Update webhook stats
        webhook.stats.totalDeliveries += 1;
        webhook.stats.successfulDeliveries += 1;
        webhook.stats.lastDelivery = new Date();

      } catch (error) {
        // Handle failure
        delivery.status = 'failed';
        delivery.error = {
          message: error.message,
          code: error.code,
          stack: error.stack
        };

        if (error.response) {
          delivery.response = {
            statusCode: error.response.status,
            headers: error.response.headers,
            body: JSON.stringify(error.response.data).substring(0, 10000),
            responseTime: Date.now() - startTime,
            timestamp: new Date()
          };
        }

        // Update webhook stats
        webhook.stats.totalDeliveries += 1;
        webhook.stats.failedDeliveries += 1;
        webhook.stats.lastFailure = new Date();
        webhook.stats.lastError = error.message;

        // Add to retry queue if configured
        if (webhook.retryConfig.maxRetries > 0) {
          this.scheduleRetry(delivery, webhook);
        }
      }

      await delivery.save();
      await webhook.save();

      return delivery;
    } catch (error) {
      logger.error('Trigger webhook error:', error);
      throw error;
    }
  }

  /**
   * Schedule a webhook retry
   */
  scheduleRetry(delivery, webhook) {
    const attemptCount = delivery.attempts.length;
    
    if (attemptCount >= webhook.retryConfig.maxRetries) {
      logger.warn(`Max retries reached for webhook ${webhook._id}`);
      return;
    }

    const delay = webhook.retryConfig.exponentialBackoff
      ? webhook.retryConfig.retryDelay * Math.pow(2, attemptCount)
      : webhook.retryConfig.retryDelay;

    setTimeout(async () => {
      try {
        logger.info(`Retrying webhook ${webhook._id}, attempt ${attemptCount + 1}`);
        
        delivery.status = 'retrying';
        delivery.attempts.push({
          timestamp: new Date(),
          statusCode: delivery.response?.statusCode,
          error: delivery.error?.message
        });

        await this.triggerWebhook(webhook, delivery.payload);
      } catch (error) {
        logger.error('Retry error:', error);
      }
    }, delay);
  }

  /**
   * Retry a specific failed delivery
   */
  async retryDelivery(deliveryId) {
    try {
      const delivery = await WebhookDelivery.findById(deliveryId);
      if (!delivery || delivery.status === 'delivered') {
        throw new Error('Delivery not found or already delivered');
      }

      const webhook = await Webhook.findById(delivery.webhook);
      if (!webhook || webhook.status !== 'active') {
        throw new Error('Webhook not found or inactive');
      }

      return await this.triggerWebhook(webhook, delivery.payload);
    } catch (error) {
      logger.error('Manual retry error:', error);
      throw error;
    }
  }

  /**
   * Verify webhook signature from incoming request
   */
  verifySignature(payload, signature, secret) {
    try {
      const expectedSignature = crypto
        .createHmac('sha256', secret)
        .update(JSON.stringify(payload))
        .digest('hex');

      return crypto.timingSafeEqual(
        Buffer.from(signature),
        Buffer.from(expectedSignature)
      );
    } catch (error) {
      return false;
    }
  }

  /**
   * Get webhook health status
   */
  async getWebhookHealth(webhookId) {
    try {
      const deliveries = await WebhookDelivery.find({
        webhook: webhookId,
        createdAt: { $gte: new Date(Date.now() - 24 * 60 * 60 * 1000) } // Last 24 hours
      });

      const total = deliveries.length;
      const successful = deliveries.filter(d => d.status === 'delivered').length;
      const failed = deliveries.filter(d => d.status === 'failed').length;

      return {
        total,
        successful,
        failed,
        successRate: total > 0 ? (successful / total) * 100 : 0,
        avgResponseTime: total > 0
          ? deliveries.reduce((sum, d) => sum + (d.response?.responseTime || 0), 0) / total
          : 0,
        status: failed > successful ? 'unhealthy' : 'healthy'
      };
    } catch (error) {
      logger.error('Get webhook health error:', error);
      throw error;
    }
  }

  /**
   * Test webhook endpoint
   */
  async testEndpoint(url) {
    try {
      const testPayload = {
        event: 'test.webhook',
        timestamp: new Date().toISOString(),
        data: { message: 'Webhook test event' }
      };

      const response = await axios.post(url, testPayload, {
        headers: { 'Content-Type': 'application/json' },
        timeout: 10000,
        validateStatus: () => true // Accept any status code
      });

      return {
        success: response.status >= 200 && response.status < 300,
        statusCode: response.status,
        responseTime: response.headers['x-response-time'],
        message: response.status >= 200 && response.status < 300
          ? 'Webhook endpoint is reachable'
          : `Endpoint returned status ${response.status}`
      };
    } catch (error) {
      return {
        success: false,
        statusCode: null,
        error: error.message,
        message: 'Failed to reach webhook endpoint'
      };
    }
  }
}

module.exports = new WebhookService();
