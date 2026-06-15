/**
 * ============================================
 * 🚛 نظام إدهام - الخادم الرئيسي
 * Edham Logistics - Main Server
 * ============================================
 * Refrigerated Transport Management System
 * ============================================
 */

require('./config/environment');
const express = require("express");
const http = require("http");
const cors = require("cors");
const helmet = require("helmet");
const morgan = require("morgan");
const compression = require("compression");
const { Server } = require("socket.io");
const mongoose = require("mongoose");
const mongoSanitize = require("express-mongo-sanitize");
const xss = require("xss-clean");
const logger = require("./utils/logger");
const connectDB = require("./config/database");
const { generalLimiter, authLimiter } = require("./middleware/rateLimiter");
const { errorHandler, notFound } = require("./middleware/errorHandler");
const requestContext = require("./middleware/requestContext");
const tenantContext = require("./middleware/tenantContext");
const { registerModules } = require("./modules/core/moduleRegistry");
const legacyRouteModules = require("./modules/legacy/legacyRouteModules");

const app = express();
const server = http.createServer(app);

// ============================================
// �️ DATABASE CONNECTION
// ============================================
if (process.env.NODE_ENV !== "test") {
  connectDB();
}

// ============================================
// �️ SECURITY MIDDLEWARE
// ============================================

// Helmet - Security Headers
app.use(helmet());

// CORS Configuration
app.use(cors({
  origin: process.env.CORS_ORIGIN || "*",
  credentials: true
}));

// Compression
app.use(compression());

// Body Parser
app.use(express.json({ limit: "10kb" }));
app.use(express.urlencoded({ extended: true, limit: "10kb" }));

// Data Sanitization - NoSQL Injection
app.use(mongoSanitize());

// XSS Protection
app.use(xss());

// Request context and tenant context (SaaS foundation)
app.use(requestContext);
app.use(tenantContext);

// Logging
if (process.env.NODE_ENV === "development") {
  app.use(morgan("dev"));
} else {
  app.use(morgan("combined"));
}

// Rate Limiting
app.use('/api/', generalLimiter);
app.use('/api/auth/login', authLimiter);
app.use('/api/auth/register', authLimiter);

// ============================================
// 🏥 HEALTH CHECK
// ============================================

app.get("/", (req, res) => {
  res.json({
    success: true,
    message: "🚛 Edham Logistics API is running",
    version: "1.0.0",
    environment: process.env.NODE_ENV,
    timestamp: new Date().toISOString()
  });
});

app.get("/api/health", (req, res) => {
  res.json({
    success: true,
    status: "healthy",
    database: mongoose.connection.readyState === 1 ? "connected" : "disconnected",
    uptime: process.uptime()
  });
});

// ============================================
// 🛣️ ROUTES
// ============================================

registerModules(app, legacyRouteModules);

// ============================================
// 🌐 WEBSOCKET SERVER (Socket.io)
// ============================================

const io = new Server(server, {
  cors: {
    origin: process.env.CORS_ORIGIN || "*",
    methods: ["GET", "POST"],
    credentials: true
  }
});

// Socket Event Handlers
require("./sockets")(io);

// ============================================
// ⚠️ GLOBAL ERROR HANDLER
// ============================================

app.use(notFound);
app.use(errorHandler);

// ============================================
// 🚀 START SERVER
// ============================================

const PORT = process.env.PORT || 5000;

if (process.env.NODE_ENV !== "test") {
  server.listen(PORT, () => {
    logger.info("=".repeat(50));
    logger.success("Edham Logistics Server Started");
    logger.info("=".repeat(50));
    logger.info(`Server running on port ${PORT}`);
    logger.info(`Environment: ${process.env.NODE_ENV}`);
    logger.info(`URL: http://localhost:${PORT}`);
    logger.info("=".repeat(50));
  });
}

// Graceful Shutdown
if (process.env.NODE_ENV !== "test") {
  process.on("SIGTERM", () => {
    logger.info("SIGTERM received. Shutting down gracefully...");
    server.close(() => {
      logger.info("Server closed");
      mongoose.connection.close(false, () => {
        logger.info("MongoDB connection closed");
        process.exit(0);
      });
    });
  });
}

module.exports = app;