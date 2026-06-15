//
/**
 * ============================================
 * 🔗 ERP Integration Service - خدمة التكامل مع أنظمة ERP
 * Integration with SAP, Oracle, Microsoft Dynamics, etc.
 * ============================================
 */

const axios = require('axios');
const crypto = require('crypto');
const logger = require('../utils/logger');

class ERPIntegrationService {
  constructor() {
    this.connections = new Map();
    this.webhookQueue = [];
  }

  /**
   * Connect to ERP system
   */
  async connect(companyId, config) {
    try {
      const { type, endpoint, credentials, options = {} } = config;

      let connection;

      switch (type.toLowerCase()) {
        case 'sap':
          connection = await this.connectSAP(endpoint, credentials, options);
          break;
        case 'oracle':
          connection = await this.connectOracle(endpoint, credentials, options);
          break;
        case 'dynamics':
          connection = await this.connectDynamics(endpoint, credentials, options);
          break;
        case 'salesforce':
          connection = await this.connectSalesforce(endpoint, credentials, options);
          break;
        case 'odoo':
          connection = await this.connectOdoo(endpoint, credentials, options);
          break;
        case 'quickbooks':
          connection = await this.connectQuickBooks(endpoint, credentials, options);
          break;
        default:
          throw new Error(`Unsupported ERP type: ${type}`);
      }

      // Store connection
      this.connections.set(companyId, {
        type,
        endpoint,
        connection,
        connectedAt: new Date(),
        config: options
      });

      logger.info(`ERP connection established for company ${companyId}: ${type}`);

      return {
        success: true,
        type,
        connected: true
      };

    } catch (error) {
      logger.error('ERP connection error:', error);
      throw error;
    }
  }

  /**
   * SAP Connection
   */
  async connectSAP(endpoint, credentials, options) {
    const { username, password, client, systemId } = credentials;

    // SAP OData or RFC connection
    return axios.create({
      baseURL: endpoint,
      auth: {
        username,
        password
      },
      headers: {
        'Content-Type': 'application/json',
        'x-csrf-token': 'fetch',
        'sap-client': client
      },
      timeout: 30000
    });
  }

  /**
   * Oracle Connection
   */
  async connectOracle(endpoint, credentials, options) {
    const { username, password } = credentials;

    return axios.create({
      baseURL: endpoint,
      auth: {
        username,
        password
      },
      headers: {
        'Content-Type': 'application/json'
      },
      timeout: 30000
    });
  }

  /**
   * Microsoft Dynamics Connection
   */
  async connectDynamics(endpoint, credentials, options) {
    const { clientId, clientSecret, tenantId } = credentials;

    // Get OAuth token
    const tokenResponse = await axios.post(
      `https://login.microsoftonline.com/${tenantId}/oauth2/token`,
      {
        grant_type: 'client_credentials',
        client_id: clientId,
        client_secret: clientSecret,
        resource: endpoint
      }
    );

    return axios.create({
      baseURL: endpoint,
      headers: {
        'Authorization': `Bearer ${tokenResponse.data.access_token}`,
        'Content-Type': 'application/json',
        'OData-MaxVersion': '4.0',
        'OData-Version': '4.0'
      },
      timeout: 30000
    });
  }

  /**
   * Salesforce Connection
   */
  async connectSalesforce(endpoint, credentials, options) {
    const { username, password, securityToken, clientId, clientSecret } = credentials;

    // Login to Salesforce
    const loginResponse = await axios.post(
      `${endpoint}/services/oauth2/token`,
      {
        grant_type: 'password',
        client_id: clientId,
        client_secret: clientSecret,
        username: username,
        password: password + securityToken
      }
    );

    const instanceUrl = loginResponse.data.instance_url;
    const accessToken = loginResponse.data.access_token;

    return axios.create({
      baseURL: instanceUrl,
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      },
      timeout: 30000
    });
  }

  /**
   * Odoo Connection
   */
  async connectOdoo(endpoint, credentials, options) {
    const { database, username, apiKey } = credentials;

    // Odoo XML-RPC or JSON-RPC
    return {
      endpoint,
      database,
      username,
      apiKey,
      uid: null // Will be set after authentication
    };
  }

  /**
   * QuickBooks Connection
   */
  async connectQuickBooks(endpoint, credentials, options) {
    const { clientId, clientSecret, refreshToken, realmId } = credentials;

    // Refresh access token
    const tokenResponse = await axios.post(
      'https://oauth.platform.intuit.com/oauth2/v1/tokens/bearer',
      {
        grant_type: 'refresh_token',
        refresh_token: refreshToken
      },
      {
        auth: {
          username: clientId,
          password: clientSecret
        }
      }
    );

    return axios.create({
      baseURL: `${endpoint}/v3/company/${realmId}`,
      headers: {
        'Authorization': `Bearer ${tokenResponse.data.access_token}`,
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      timeout: 30000
    });
  }

  /**
   * Sync shipment to ERP
   */
  async syncShipment(companyId, shipment, operation = 'create') {
    try {
      const conn = this.connections.get(companyId);
      if (!conn) {
        throw new Error('ERP not connected');
      }

      const { type, connection } = conn;

      let result;

      switch (type.toLowerCase()) {
        case 'sap':
          result = await this.syncShipmentToSAP(connection, shipment, operation);
          break;
        case 'oracle':
          result = await this.syncShipmentToOracle(connection, shipment, operation);
          break;
        case 'dynamics':
          result = await this.syncShipmentToDynamics(connection, shipment, operation);
          break;
        case 'salesforce':
          result = await this.syncShipmentToSalesforce(connection, shipment, operation);
          break;
        default:
          throw new Error(`Sync not implemented for ${type}`);
      }

      // Log sync
      await this.logSync(companyId, 'shipment', shipment._id, operation, result);

      return {
        success: true,
        erpId: result.id,
        syncedAt: new Date()
      };

    } catch (error) {
      logger.error('ERP sync shipment error:', error);
      
      // Queue for retry
      this.queueSync(companyId, 'shipment', shipment, operation);
      
      throw error;
    }
  }

  /**
   * Sync shipment to SAP
   */
  async syncShipmentToSAP(connection, shipment, operation) {
    const data = this.transformShipmentForSAP(shipment);

    if (operation === 'create') {
      const response = await connection.post('/ShipmentSet', data);
      return { id: response.data.d.ShipmentID };
    } else {
      const response = await connection.patch(
        `/ShipmentSet('${shipment.erpId}')`,
        data
      );
      return { id: shipment.erpId };
    }
  }

  /**
   * Sync shipment to Dynamics
   */
  async syncShipmentToDynamics(connection, shipment, operation) {
    const data = this.transformShipmentForDynamics(shipment);

    if (operation === 'create') {
      const response = await connection.post('/shipments', data);
      return { id: response.data.shipmentid };
    } else {
      const response = await connection.patch(
        `/shipments(${shipment.erpId})`,
        data
      );
      return { id: shipment.erpId };
    }
  }

  /**
   * Sync invoice to ERP
   */
  async syncInvoice(companyId, invoice, operation = 'create') {
    try {
      const conn = this.connections.get(companyId);
      if (!conn) {
        throw new Error('ERP not connected');
      }

      const { type, connection } = conn;

      let result;

      switch (type.toLowerCase()) {
        case 'sap':
          result = await this.syncInvoiceToSAP(connection, invoice, operation);
          break;
        case 'quickbooks':
          result = await this.syncInvoiceToQuickBooks(connection, invoice, operation);
          break;
        default:
          throw new Error(`Invoice sync not implemented for ${type}`);
      }

      // Log sync
      await this.logSync(companyId, 'invoice', invoice._id, operation, result);

      return {
        success: true,
        erpId: result.id,
        syncedAt: new Date()
      };

    } catch (error) {
      logger.error('ERP sync invoice error:', error);
      this.queueSync(companyId, 'invoice', invoice, operation);
      throw error;
    }
  }

  /**
   * Sync invoice to QuickBooks
   */
  async syncInvoiceToQuickBooks(connection, invoice, operation) {
    const data = {
      Line: invoice.items.map(item => ({
        DetailType: 'SalesItemLineDetail',
        Amount: item.total,
        SalesItemLineDetail: {
          ItemRef: {
            name: item.description,
            value: item.itemId || '1'
          },
          Qty: item.quantity,
          UnitPrice: item.unitPrice
        }
      })),
      CustomerRef: {
        value: invoice.client.erpId
      },
      TxnDate: invoice.createdAt.toISOString().split('T')[0],
      DueDate: invoice.dueDate.toISOString().split('T')[0],
      TotalAmt: invoice.total
    };

    if (operation === 'create') {
      const response = await connection.post('/invoice', data);
      return { id: response.data.Invoice.Id };
    } else {
      const response = await connection.post(`/invoice?operation=update`, {
        ...data,
        Id: invoice.erpId
      });
      return { id: invoice.erpId };
    }
  }

  /**
   * Fetch customers from ERP
   */
  async syncCustomers(companyId, options = {}) {
    try {
      const conn = this.connections.get(companyId);
      if (!conn) {
        throw new Error('ERP not connected');
      }

      const { type, connection } = conn;
      let customers = [];

      switch (type.toLowerCase()) {
        case 'sap':
          const sapResponse = await connection.get('/CustomerSet');
          customers = sapResponse.data.d.results.map(c => this.transformSAPCustomer(c));
          break;

        case 'dynamics':
          const dynResponse = await connection.get('/accounts');
          customers = dynResponse.data.value.map(c => this.transformDynamicsCustomer(c));
          break;

        case 'quickbooks':
          const qbResponse = await connection.get('/query?query=select * from Customer');
          customers = qbResponse.data.QueryResponse.Customer.map(c => this.transformQuickBooksCustomer(c));
          break;
      }

      // Update local customer records
      await this.updateLocalCustomers(companyId, customers);

      return {
        success: true,
        count: customers.length,
        customers
      };

    } catch (error) {
      logger.error('ERP sync customers error:', error);
      throw error;
    }
  }

  /**
   * Queue sync for retry
   */
  queueSync(companyId, entityType, entity, operation) {
    this.webhookQueue.push({
      companyId,
      entityType,
      entity,
      operation,
      retryCount: 0,
      createdAt: new Date()
    });

    logger.info(`Queued ERP sync for ${entityType} ${entity._id}`);
  }

  /**
   * Process retry queue
   */
  async processRetryQueue() {
    const now = new Date();
    const toProcess = this.webhookQueue.filter(item => {
      const retryDelay = Math.pow(2, item.retryCount) * 60000; // Exponential backoff
      return now - item.createdAt >= retryDelay;
    });

    for (const item of toProcess) {
      try {
        if (item.entityType === 'shipment') {
          await this.syncShipment(item.companyId, item.entity, item.operation);
        } else if (item.entityType === 'invoice') {
          await this.syncInvoice(item.companyId, item.entity, item.operation);
        }

        // Remove from queue
        this.webhookQueue = this.webhookQueue.filter(i => i !== item);

      } catch (error) {
        item.retryCount++;
        
        if (item.retryCount >= 5) {
          // Max retries reached, log failure
          await this.logSyncFailure(item, error);
          this.webhookQueue = this.webhookQueue.filter(i => i !== item);
        }
      }
    }
  }

  /**
   * Transform shipment for SAP
   */
  transformShipmentForSAP(shipment) {
    return {
      TrackingNumber: shipment.trackingNumber,
      OriginCity: shipment.pickup?.address?.city,
      DestinationCity: shipment.delivery?.address?.city,
      Weight: shipment.cargo?.weight?.value,
      WeightUnit: shipment.cargo?.weight?.unit,
      Status: this.mapStatusToSAP(shipment.status),
      ShipmentDate: shipment.createdAt
    };
  }

  /**
   * Transform shipment for Dynamics
   */
  transformShipmentForDynamics(shipment) {
    return {
      trackingnumber: shipment.trackingNumber,
      origin: shipment.pickup?.address?.city,
      destination: shipment.delivery?.address?.city,
      weight: shipment.cargo?.weight?.value,
      statuscode: this.mapStatusToDynamics(shipment.status),
      createdon: shipment.createdAt
    };
  }

  /**
   * Map status codes
   */
  mapStatusToSAP(status) {
    const mapping = {
      'pending': 'PENDING',
      'picked_up': 'PICKED',
      'in_transit': 'IN_TRANSIT',
      'delivered': 'DELIVERED',
      'cancelled': 'CANCELLED'
    };
    return mapping[status] || 'PENDING';
  }

  mapStatusToDynamics(status) {
    const mapping = {
      'pending': 1,
      'picked_up': 2,
      'in_transit': 3,
      'delivered': 4,
      'cancelled': 5
    };
    return mapping[status] || 1;
  }

  /**
   * Log sync operation
   */
  async logSync(companyId, entityType, entityId, operation, result) {
    try {
      const ERPIntegrationLog = require('../models/ERPIntegrationLog');
      
      await ERPIntegrationLog.create({
        company: companyId,
        entityType,
        entityId,
        operation,
        status: 'success',
        erpId: result.id,
        timestamp: new Date()
      });

    } catch (error) {
      logger.error('Log sync error:', error);
    }
  }

  /**
   * Log sync failure
   */
  async logSyncFailure(queueItem, error) {
    try {
      const ERPIntegrationLog = require('../models/ERPIntegrationLog');
      
      await ERPIntegrationLog.create({
        company: queueItem.companyId,
        entityType: queueItem.entityType,
        entityId: queueItem.entity._id,
        operation: queueItem.operation,
        status: 'failed',
        error: error.message,
        retryCount: queueItem.retryCount,
        timestamp: new Date()
      });

    } catch (logError) {
      logger.error('Log sync failure error:', logError);
    }
  }

  /**
   * Get sync status
   */
  async getSyncStatus(companyId, startDate, endDate) {
    try {
      const ERPIntegrationLog = require('../models/ERPIntegrationLog');
      
      const stats = await ERPIntegrationLog.aggregate([
        {
          $match: {
            company: mongoose.Types.ObjectId(companyId),
            timestamp: { $gte: startDate, $lte: endDate }
          }
        },
        {
          $group: {
            _id: '$status',
            count: { $sum: 1 }
          }
        }
      ]);

      const recentFailures = await ERPIntegrationLog.find({
        company: companyId,
        status: 'failed',
        timestamp: { $gte: new Date(Date.now() - 24 * 60 * 60 * 1000) }
      })
        .sort({ timestamp: -1 })
        .limit(10);

      return {
        success: true,
        stats,
        recentFailures,
        pendingSync: this.webhookQueue.filter(i => i.companyId === companyId).length
      };

    } catch (error) {
      logger.error('Get sync status error:', error);
      throw error;
    }
  }

  /**
   * Disconnect from ERP
   */
  async disconnect(companyId) {
    const conn = this.connections.get(companyId);
    if (conn) {
      this.connections.delete(companyId);
      logger.info(`ERP disconnected for company ${companyId}`);
    }

    return { success: true, disconnected: true };
  }
}

module.exports = new ERPIntegrationService();
