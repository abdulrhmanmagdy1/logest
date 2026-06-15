/**
 * ============================================
 * 📝 Logger Utility - نظام إدهام
 * Edham Logistics - Logging System
 * ============================================
 */

const fs = require('fs');
const path = require('path');

const logDir = path.join(__dirname, '../logs');

// Create logs directory if it doesn't exist
if (!fs.existsSync(logDir)) {
  fs.mkdirSync(logDir, { recursive: true });
}

class Logger {
  constructor() {
    this.levels = {
      ERROR: 0,
      WARN: 1,
      INFO: 2,
      SUCCESS: 3,
      DEBUG: 4
    };
    
    this.colors = {
      ERROR: '\x1b[31m',    // Red
      WARN: '\x1b[33m',     // Yellow
      INFO: '\x1b[36m',     // Cyan
      SUCCESS: '\x1b[32m',  // Green
      DEBUG: '\x1b[35m',    // Magenta
      RESET: '\x1b[0m'
    };

    this.logFile = path.join(logDir, `${new Date().toISOString().split('T')[0]}.log`);
  }

  getTimestamp() {
    return new Date().toISOString();
  }

  log(level, message, data = {}) {
    const timestamp = this.getTimestamp();
    const logMessage = `[${timestamp}] [${level}] ${message}\n${JSON.stringify(data, null, 2)}\n\n`;

    // Write to file
    fs.appendFileSync(this.logFile, logMessage);

    // Console output
    const color = this.colors[level] || this.colors.INFO;
    console.log(`${color}[${level}] ${message}${this.colors.RESET}`, data);
  }

  error(message, data) {
    this.log('ERROR', message, data);
  }

  success(message, data) {
    this.log('SUCCESS', message, data);
  }

  info(message, data) {
    this.log('INFO', message, data);
  }

  warn(message, data) {
    this.log('WARN', message, data);
  }

  debug(message, data) {
    if (process.env.NODE_ENV === 'development') {
      this.log('DEBUG', message, data);
    }
  }
}

module.exports = new Logger();
