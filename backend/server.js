/**
 * ============================================
 * 🚀 Edham Logistics Backend Server
 * Main entry point for the API
 * ============================================
 */

const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const compression = require('compression');
const morgan = require('morgan');
const { createServer } = require('http');
const { Server } = require('socket.io');
require('dotenv').config();

const connectDB = require('./config/database');
const { USE_MOCK_MODE } = require('./config/database');
const logger = require('./utils/logger');
const errorHandler = require('./middleware/errorHandler');
const mockMiddleware = require('./middleware/mockData');
const { specs, swaggerUi } = require('./config/swagger');

// Import routes (only if not in mock mode)
let authRoutes, userRoutes, shipmentRoutes, truckRoutes, driverRoutes, invoiceRoutes, trackingRoutes, notificationRoutes, surveyRoutes, reportRoutes, uploadRoutes, dashboardRoutes;
let accountantRoutes, workshopRoutes, chatRoutes, ticketRoutes, reviewRoutes, documentRoutes, sensorRoutes, ceoRoutes, analyticsRoutes, routeOptimizationRoutes, fuelRoutes, auditLogRoutes, subscriptionRoutes, paymentRoutes, webhookRoutes, apiKeyRoutes, aiRoutes, twoFARoutes, searchRoutes, exportRoutes, scannerRoutes;
let warehouseRoutes, crmRoutes, hrRoutes, contractRoutes, qualityRoutes, riskRoutes, projectRoutes;
let fleetRoutes, procurementRoutes, marketingRoutes;
let companyRoutes, workflowRoutes, slaRoutes, biRoutes, emailTemplateRoutes, formRoutes;
let ediRoutes, iotRoutes, blockchainRoutes;
let customerPortalRoutes, dataMigrationRoutes, apiGatewayRoutes, monitoringRoutes, regionsRoutes, knowledgeBaseRoutes, taskRoutes, auditRoutes, gamificationRoutes, calendarRoutes, cacheRoutes, aiAssistantRoutes, systemRoutes;

if (!USE_MOCK_MODE) {
  try {
    const User = require('./models/User');
    
    // Import routes
    authRoutes = require('./routes/auth');
    dashboardRoutes = require('./routes/dashboard');
    userRoutes = require('./routes/users');
    shipmentRoutes = require('./routes/shipments');
    truckRoutes = require('./routes/trucks');
    driverRoutes = require('./routes/drivers');
    invoiceRoutes = require('./routes/invoices');
    trackingRoutes = require('./routes/tracking');
    notificationRoutes = require('./routes/notifications');
    surveyRoutes = require('./routes/surveys');
    reportRoutes = require('./routes/reports');
    uploadRoutes = require('./routes/uploads');

    // Advanced Features Routes
    accountantRoutes = require('./routes/accountant');
    workshopRoutes = require('./routes/workshop');
    chatRoutes = require('./routes/chat');
    ticketRoutes = require('./routes/tickets');
    reviewRoutes = require('./routes/reviews');
    documentRoutes = require('./routes/documents');
    sensorRoutes = require('./routes/sensors');
    ceoRoutes = require('./routes/ceo');
    analyticsRoutes = require('./routes/analytics');
    routeOptimizationRoutes = require('./routes/routeOptimization');
    fuelRoutes = require('./routes/fuel');
    auditLogRoutes = require('./routes/auditLogs');
    subscriptionRoutes = require('./routes/subscriptions');
    paymentRoutes = require('./routes/payments');
    webhookRoutes = require('./routes/webhooks');
    apiKeyRoutes = require('./routes/apiKeys');
    aiRoutes = require('./routes/ai');
    twoFARoutes = require('./routes/twofa');
    searchRoutes = require('./routes/search');
    exportRoutes = require('./routes/exports');
    scannerRoutes = require('./routes/scanner');

    // Enterprise Routes
    warehouseRoutes = require('./routes/warehouse');
    crmRoutes = require('./routes/crm');
    hrRoutes = require('./routes/hr');
    contractRoutes = require('./routes/contracts');
    qualityRoutes = require('./routes/quality');
    riskRoutes = require('./routes/risk');
    projectRoutes = require('./routes/projects');

    // Operations Routes
    fleetRoutes = require('./routes/fleet');
    procurementRoutes = require('./routes/procurement');
    marketingRoutes = require('./routes/marketing');

    // Management Routes
    companyRoutes = require('./routes/companies');
    workflowRoutes = require('./routes/workflows');
    slaRoutes = require('./routes/sla');
    biRoutes = require('./routes/bi');
    emailTemplateRoutes = require('./routes/emailTemplates');
    formRoutes = require('./routes/forms');

    // Integration Routes
    ediRoutes = require('./routes/edi');
    iotRoutes = require('./routes/iot');
    blockchainRoutes = require('./routes/blockchain');

    // Portal Routes
    customerPortalRoutes = require('./routes/customerPortal');

    // Data Routes
    dataMigrationRoutes = require('./routes/dataMigration');

    // API Management Routes
    apiGatewayRoutes = require('./routes/apiGateway');

    // Monitoring Routes
    monitoringRoutes = require('./routes/monitoring');

    // Regions Routes
    regionsRoutes = require('./routes/regions');

    // Knowledge Base Routes
    knowledgeBaseRoutes = require('./routes/knowledgeBase');

    // Task Management Routes
    taskRoutes = require('./routes/tasks');

    // Audit Routes
    auditRoutes = require('./routes/audit');

    // Gamification Routes
    gamificationRoutes = require('./routes/gamification');

    // Calendar Routes
    calendarRoutes = require('./routes/calendar');

    // Cache Routes
    cacheRoutes = require('./routes/cache');

    // AI Assistant Routes
    aiAssistantRoutes = require('./routes/aiAssistant');

    // System Routes
    systemRoutes = require('./routes/system');
  } catch (error) {
    logger.warn('⚠️  Failed to load routes/models, falling back to mock mode');
    logger.warn(`Error: ${error.message}`);
  }
}

// Initialize Express app
const app = express();
const httpServer = createServer(app);
const io = new Server(httpServer, {
  cors: {
    // origin: true reflects the requesting origin back — works with credentials
    // and is equivalent to "*" for all practical purposes.
    // Once you have your Vercel URL, tighten this to:
    //   origin: process.env.CLIENT_URL || 'https://your-app.vercel.app'
    origin: true,
    methods: ['GET', 'POST'],
    credentials: true,
  },
});

// Connect to database
connectDB();

// Middleware
app.use(helmet());
app.use(cors({
  // Open to all origins for initial deployment.
  // After confirming Vercel URL, set CLIENT_URL env var on Render
  // and change this to: origin: process.env.CLIENT_URL
  origin: true,
  credentials: true,
}));
app.use(compression());
app.use(morgan('combined', { stream: { write: msg => logger.info(msg.trim()) } }));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// Mock Data Middleware (when MongoDB is not available)
app.use('/api/v1', mockMiddleware);

// Swagger API Documentation — disabled in production
if (process.env.NODE_ENV !== 'production') {
  app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(specs, { explorer: true }));
  logger.info('Swagger UI available at /api-docs (development only)');
}

// API Routes (only if not in mock mode)
if (!USE_MOCK_MODE && authRoutes) {
  app.use('/api/v1/auth', authRoutes);
  app.use('/api/v1/dashboard', dashboardRoutes);
  app.use('/api/v1/users', userRoutes);
  app.use('/api/v1/shipments', shipmentRoutes);
  app.use('/api/v1/trucks', truckRoutes);
  app.use('/api/v1/drivers', driverRoutes);
  app.use('/api/v1/invoices', invoiceRoutes);
  app.use('/api/v1/tracking', trackingRoutes);
  app.use('/api/v1/notifications', notificationRoutes);
  app.use('/api/v1/surveys', surveyRoutes);
  app.use('/api/v1/reports', reportRoutes);
  app.use('/api/v1/uploads', uploadRoutes);

  // Advanced Features Routes
  app.use('/api/v1/accountant', accountantRoutes);
  app.use('/api/v1/workshop', workshopRoutes);
  app.use('/api/v1/chat', chatRoutes);
  app.use('/api/v1/tickets', ticketRoutes);
  app.use('/api/v1/reviews', reviewRoutes);
  app.use('/api/v1/documents', documentRoutes);
  app.use('/api/v1/sensors', sensorRoutes);
  app.use('/api/v1/ceo', ceoRoutes);
  app.use('/api/v1/analytics', analyticsRoutes);
  app.use('/api/v1/route-optimization', routeOptimizationRoutes);
  app.use('/api/v1/fuel', fuelRoutes);
  app.use('/api/v1/audit-logs', auditLogRoutes);
  app.use('/api/v1/subscriptions', subscriptionRoutes);
  app.use('/api/v1/payments', paymentRoutes);
  app.use('/api/v1/webhooks', webhookRoutes);
  app.use('/api/v1/api-keys', apiKeyRoutes);
  app.use('/api/v1/ai', aiRoutes);
  app.use('/api/v1/2fa', twoFARoutes);
  app.use('/api/v1/search', searchRoutes);
  app.use('/api/v1/exports', exportRoutes);
  app.use('/api/v1/scanner', scannerRoutes);

  // Enterprise Routes
  app.use('/api/v1/warehouses', warehouseRoutes);
  app.use('/api/v1/crm', crmRoutes);
  app.use('/api/v1/hr', hrRoutes);
  app.use('/api/v1/contracts', contractRoutes);
  app.use('/api/v1/quality', qualityRoutes);
  app.use('/api/v1/risks', riskRoutes);
  app.use('/api/v1/projects', projectRoutes);

  // Operations Routes
  app.use('/api/v1/fleet', fleetRoutes);
  app.use('/api/v1/procurement', procurementRoutes);
  app.use('/api/v1/marketing', marketingRoutes);

  // Management Routes
  app.use('/api/v1/companies', companyRoutes);
  app.use('/api/v1/workflows', workflowRoutes);
  app.use('/api/v1/sla', slaRoutes);
  app.use('/api/v1/bi', biRoutes);
  app.use('/api/v1/email-templates', emailTemplateRoutes);
  app.use('/api/v1/forms', formRoutes);

  // Integration Routes
  app.use('/api/v1/edi', ediRoutes);
  app.use('/api/v1/iot', iotRoutes);
  app.use('/api/v1/blockchain', blockchainRoutes);

  // Portal Routes
  app.use('/api/v1/portal', customerPortalRoutes);

  // Data Routes
  app.use('/api/v1/migration', dataMigrationRoutes);

  // API Management Routes
  app.use('/api/v1/gateway', apiGatewayRoutes);

  // Monitoring Routes
  app.use('/api/v1/monitoring', monitoringRoutes);

  // Regions Routes
  app.use('/api/v1/regions', regionsRoutes);

  // Search Routes
  app.use('/api/v1/search', searchRoutes);

  // Knowledge Base Routes
  app.use('/api/v1/kb', knowledgeBaseRoutes);

  // Task Management Routes
  app.use('/api/v1/tasks', taskRoutes);

  // Audit Routes
  app.use('/api/v1/audit', auditRoutes);

  // Gamification Routes
  app.use('/api/v1/gamification', gamificationRoutes);

  // Calendar Routes
  app.use('/api/v1/calendar', calendarRoutes);

  // Cache Routes
  app.use('/api/v1/cache', cacheRoutes);

  // AI Assistant Routes
  app.use('/api/v1/ai', aiAssistantRoutes);

  // System Routes
  app.use('/api/v1/system', systemRoutes);
}

// Health check endpoint
app.get('/api/v1/health', (req, res) => {
  res.json({
    status: 'success',
    message: 'Edham Logistics API is running',
    timestamp: new Date().toISOString(),
    version: '1.0.0'
  });
});

// Initialize WebRTC Service
const WebRTCService = require('./services/webrtcService');
const webrtcService = new WebRTCService(io);

// Socket.io connection handling
io.on('connection', (socket) => {
  logger.info(`Client connected: ${socket.id}`);
  
  // Join room based on user role
  socket.on('join', (data) => {
    const { userId, role } = data;
    socket.join(`user:${userId}`);
    socket.join(`role:${role}`);
    logger.info(`User ${userId} joined rooms`);
  });
  
  // Location tracking
  socket.on('location_update', (data) => {
    const { driverId, shipmentId, location } = data;
    // Broadcast to clients tracking this shipment
    io.to(`shipment:${shipmentId}`).emit('location_update', {
      driverId,
      shipmentId,
      location,
      timestamp: new Date().toISOString()
    });
  });
  
  // Chat messages
  socket.on('chat_message', (data) => {
    const { roomId, message, sender } = data;
    io.to(roomId).emit('new_message', {
      message,
      sender,
      timestamp: new Date().toISOString()
    });
  });
  
  // Notifications
  socket.on('notification', (data) => {
    const { userId, notification } = data;
    io.to(`user:${userId}`).emit('new_notification', notification);
  });
  
  // Disconnect handling
  socket.on('disconnect', () => {
    logger.info(`Client disconnected: ${socket.id}`);
  });
});

// Error handling
app.use(errorHandler);

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    status: 'error',
    message: 'Route not found'
  });
});

// Start server
const PORT = process.env.PORT || 5000;
httpServer.listen(PORT, () => {
  logger.info(`🚀 Server running on port ${PORT}`);
  logger.info(`📡 Environment: ${process.env.NODE_ENV || 'development'}`);
});

// Handle unhandled promise rejections
process.on('unhandledRejection', (err) => {
  logger.error('UNHANDLED REJECTION! 💥 Shutting down...');
  logger.error(err.name, err.message);
  server.close(() => {
    process.exit(1);
  });
});

// Handle uncaught exceptions
process.on('uncaughtException', (err) => {
  logger.error('UNCAUGHT EXCEPTION! 💥 Shutting down...');
  logger.error(err.name, err.message);
  process.exit(1);
});

module.exports = { io };
