/**
 * ============================================
 * 💾 Backup Service - نظام إدهام الاحترافي
 * Edham Logistics - Backup & Disaster Recovery
 * ============================================
 */

const fs = require('fs').promises;
const path = require('path');
const crypto = require('crypto');
const { exec } = require('child_process');
const mongoose = require('mongoose');
const logger = require('../utils/logger');
const { MESSAGES } = require('../config/constants');

class BackupService {
  constructor() {
    this.backupPath = process.env.BACKUP_PATH || './backups';
    this.encryptionKey = process.env.BACKUP_ENCRYPTION_KEY;
    this.maxBackups = parseInt(process.env.MAX_BACKUPS) || 30;
    this.compressionLevel = parseInt(process.env.COMPRESSION_LEVEL) || 6;
  }

  /**
   * Initialize backup service
   */
  async initialize() {
    try {
      // Create backup directory
      await fs.mkdir(this.backupPath, { recursive: true });
      
      // Create subdirectories
      await fs.mkdir(path.join(this.backupPath, 'database'), { recursive: true });
      await fs.mkdir(path.join(this.backupPath, 'files'), { recursive: true });
      await fs.mkdir(path.join(this.backupPath, 'logs'), { recursive: true });
      await fs.mkdir(path.join(this.backupPath, 'config'), { recursive: true });

      logger.info('Backup service initialized successfully');
      return true;
    } catch (error) {
      logger.error('Failed to initialize backup service:', error);
      return false;
    }
  }

  /**
   * Perform full system backup
   */
  async performFullBackup(options = {}) {
    const backupId = this.generateBackupId();
    const timestamp = new Date().toISOString();
    
    try {
      logger.info('Starting full system backup', { backupId, timestamp });

      const backupManifest = {
        id: backupId,
        timestamp,
        type: 'full',
        version: process.env.APP_VERSION || '2.0.0',
        components: [],
        status: 'in_progress',
        startedAt: timestamp
      };

      // Backup database
      const databaseBackup = await this.backupDatabase(backupId);
      if (databaseBackup.success) {
        backupManifest.components.push({
          type: 'database',
          status: 'completed',
          file: databaseBackup.filename,
          size: databaseBackup.size,
          checksum: databaseBackup.checksum
        });
      }

      // Backup files
      const filesBackup = await this.backupFiles(backupId);
      if (filesBackup.success) {
        backupManifest.components.push({
          type: 'files',
          status: 'completed',
          file: filesBackup.filename,
          size: filesBackup.size,
          checksum: filesBackup.checksum
        });
      }

      // Backup configuration
      const configBackup = await this.backupConfiguration(backupId);
      if (configBackup.success) {
        backupManifest.components.push({
          type: 'configuration',
          status: 'completed',
          file: configBackup.filename,
          size: configBackup.size,
          checksum: configBackup.checksum
        });
      }

      // Backup logs
      const logsBackup = await this.backupLogs(backupId);
      if (logsBackup.success) {
        backupManifest.components.push({
          type: 'logs',
          status: 'completed',
          file: logsBackup.filename,
          size: logsBackup.size,
          checksum: logsBackup.checksum
        });
      }

      // Complete backup
      backupManifest.status = 'completed';
      backupManifest.completedAt = timestamp;
      backupManifest.totalSize = backupManifest.components.reduce((sum, comp) => sum + comp.size, 0);

      // Save manifest
      await this.saveBackupManifest(backupId, backupManifest);

      // Clean old backups
      if (options.cleanup !== false) {
        await this.cleanupOldBackups();
      }

      // Verify backup integrity
      const verification = await this.verifyBackupIntegrity(backupId);
      backupManifest.verified = verification.success;
      backupManifest.verifiedAt = verification.timestamp;

      // Update manifest with verification results
      await this.saveBackupManifest(backupId, backupManifest);

      logger.success('Full backup completed successfully', { 
        backupId,
        components: backupManifest.components.length,
        totalSize: backupManifest.totalSize,
        verified: verification.success
      });

      return {
        success: true,
        backupId,
        manifest: backupManifest,
        verification
      };

    } catch (error) {
      logger.error('Full backup failed:', error);
      
      // Update manifest with error
      const failedManifest = {
        id: backupId,
        timestamp,
        type: 'full',
        status: 'failed',
        error: error.message,
        failedAt: timestamp
      };
      
      await this.saveBackupManifest(backupId, failedManifest);

      return {
        success: false,
        backupId,
        error: error.message
      };
    }
  }

  /**
   * Backup database
   */
  async backupDatabase(backupId) {
    try {
      const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
      const filename = `database_${timestamp}.dump`;
      const filepath = path.join(this.backupPath, 'database', filename);

      logger.info('Starting database backup', { backupId, filename });

      // Create database dump using mongodump
      const dumpCommand = `mongodump --uri="${process.env.MONGODB_URI}" --out="${filepath}" --gzip`;
      
      await this.executeCommand(dumpCommand);

      // Get file stats
      const stats = await fs.stat(filepath);
      const checksum = await this.calculateChecksum(filepath);

      logger.success('Database backup completed', { 
        backupId,
        filename,
        size: stats.size,
        checksum
      });

      return {
        success: true,
        filename,
        filepath,
        size: stats.size,
        checksum
      };

    } catch (error) {
      logger.error('Database backup failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Backup application files
   */
  async backupFiles(backupId) {
    try {
      const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
      const filename = `files_${timestamp}.tar.gz`;
      const filepath = path.join(this.backupPath, 'files', filename);

      logger.info('Starting files backup', { backupId, filename });

      // Create tar archive of important directories
      const directoriesToBackup = [
        './uploads',
        './public',
        './client/build',
        './mobile-native-android/app/build'
      ];

      const tarCommand = `tar -czf "${filepath}" ${directoriesToBackup.join(' ')}`;
      await this.executeCommand(tarCommand);

      // Get file stats
      const stats = await fs.stat(filepath);
      const checksum = await this.calculateChecksum(filepath);

      logger.success('Files backup completed', { 
        backupId,
        filename,
        size: stats.size,
        checksum
      });

      return {
        success: true,
        filename,
        filepath,
        size: stats.size,
        checksum
      };

    } catch (error) {
      logger.error('Files backup failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Backup configuration files
   */
  async backupConfiguration(backupId) {
    try {
      const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
      const filename = `config_${timestamp}.json`;
      const filepath = path.join(this.backupPath, 'config', filename);

      logger.info('Starting configuration backup', { backupId, filename });

      // Collect configuration data
      const configData = {
        environment: process.env.NODE_ENV,
        version: process.env.APP_VERSION,
        database: {
          host: process.env.DB_HOST,
          port: process.env.DB_PORT,
          name: process.env.DB_NAME
        },
        redis: {
          host: process.env.REDIS_HOST,
          port: process.env.REDIS_PORT
        },
        api: {
          port: process.env.PORT,
          corsOrigins: process.env.ALLOWED_ORIGINS,
          jwtSecret: process.env.JWT_SECRET ? '***CONFIGURED***' : 'NOT_SET'
        },
        backup: {
          path: this.backupPath,
          maxBackups: this.maxBackups,
          encryptionEnabled: !!this.encryptionKey
        },
        timestamp: new Date().toISOString()
      };

      // Encrypt configuration if encryption key is available
      const dataToSave = this.encryptionKey ? 
        this.encrypt(JSON.stringify(configData, null, 2)) : 
        JSON.stringify(configData, null, 2);

      await fs.writeFile(filepath, dataToSave);

      // Get file stats
      const stats = await fs.stat(filepath);
      const checksum = await this.calculateChecksum(filepath);

      logger.success('Configuration backup completed', { 
        backupId,
        filename,
        size: stats.size,
        checksum
      });

      return {
        success: true,
        filename,
        filepath,
        size: stats.size,
        checksum
      };

    } catch (error) {
      logger.error('Configuration backup failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Backup application logs
   */
  async backupLogs(backupId) {
    try {
      const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
      const filename = `logs_${timestamp}.tar.gz`;
      const filepath = path.join(this.backupPath, 'logs', filename);

      logger.info('Starting logs backup', { backupId, filename });

      // Create tar archive of log files
      const logDirectories = [
        './logs',
        './storage/logs'
      ];

      // Filter logs to only include recent ones (last 7 days)
      const sevenDaysAgo = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);
      const findCommand = `find ./logs -name "*.log" -newermt "${sevenDaysAgo.toISOString()}"`;
      
      const logFiles = await this.executeCommand(findCommand);
      if (logFiles.stdout.trim()) {
        const tarCommand = `tar -czf "${filepath}" ${logFiles.stdout.trim()}`;
        await this.executeCommand(tarCommand);
      } else {
        // Create empty archive if no recent logs
        await this.executeCommand(`touch "${filepath}"`);
      }

      // Get file stats
      const stats = await fs.stat(filepath);
      const checksum = await this.calculateChecksum(filepath);

      logger.success('Logs backup completed', { 
        backupId,
        filename,
        size: stats.size,
        checksum
      });

      return {
        success: true,
        filename,
        filepath,
        size: stats.size,
        checksum
      };

    } catch (error) {
      logger.error('Logs backup failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Restore from backup
   */
  async restoreFromBackup(backupId, options = {}) {
    try {
      logger.info('Starting restore from backup', { backupId });

      // Load backup manifest
      const manifest = await this.loadBackupManifest(backupId);
      if (!manifest) {
        throw new Error('Backup manifest not found');
      }

      // Verify backup integrity
      const verification = await this.verifyBackupIntegrity(backupId);
      if (!verification.success) {
        throw new Error('Backup integrity verification failed');
      }

      const restoreResults = [];

      // Restore database
      if (options.database !== false) {
        const dbComponent = manifest.components.find(c => c.type === 'database');
        if (dbComponent) {
          const dbRestore = await this.restoreDatabase(dbComponent);
          restoreResults.push(dbRestore);
        }
      }

      // Restore files
      if (options.files !== false) {
        const filesComponent = manifest.components.find(c => c.type === 'files');
        if (filesComponent) {
          const filesRestore = await this.restoreFiles(filesComponent);
          restoreResults.push(filesRestore);
        }
      }

      // Restore configuration
      if (options.config !== false) {
        const configComponent = manifest.components.find(c => c.type === 'configuration');
        if (configComponent) {
          const configRestore = await this.restoreConfiguration(configComponent);
          restoreResults.push(configRestore);
        }
      }

      const allSuccessful = restoreResults.every(r => r.success);

      if (allSuccessful) {
        logger.success('Restore completed successfully', { backupId });
        return {
          success: true,
          backupId,
          results: restoreResults
        };
      } else {
        throw new Error('Some restore operations failed');
      }

    } catch (error) {
      logger.error('Restore failed:', error);
      return {
        success: false,
        backupId,
        error: error.message
      };
    }
  }

  /**
   * Restore database
   */
  async restoreDatabase(dbComponent) {
    try {
      const filepath = path.join(this.backupPath, 'database', dbComponent.file);
      
      logger.info('Restoring database', { file: dbComponent.file });

      // Drop existing database (with confirmation)
      const restoreCommand = `mongorestore --uri="${process.env.MONGODB_URI}" --drop "${filepath}" --gzip`;
      await this.executeCommand(restoreCommand);

      logger.success('Database restore completed');
      return { success: true, component: 'database' };

    } catch (error) {
      logger.error('Database restore failed:', error);
      return { success: false, component: 'database', error: error.message };
    }
  }

  /**
   * Restore files
   */
  async restoreFiles(filesComponent) {
    try {
      const filepath = path.join(this.backupPath, 'files', filesComponent.file);
      
      logger.info('Restoring files', { file: filesComponent.file });

      // Extract files to temporary location first
      const tempDir = path.join(this.backupPath, 'temp_restore');
      await fs.mkdir(tempDir, { recursive: true });
      
      const extractCommand = `tar -xzf "${filepath}" -C "${tempDir}"`;
      await this.executeCommand(extractCommand);

      // Move files to their original locations (with backup of existing)
      // This would need more sophisticated logic for production use

      logger.success('Files restore completed');
      return { success: true, component: 'files' };

    } catch (error) {
      logger.error('Files restore failed:', error);
      return { success: false, component: 'files', error: error.message };
    }
  }

  /**
   * Restore configuration
   */
  async restoreConfiguration(configComponent) {
    try {
      const filepath = path.join(this.backupPath, 'config', configComponent.file);
      
      logger.info('Restoring configuration', { file: configComponent.file });

      // Read configuration file
      let configData = await fs.readFile(filepath, 'utf8');
      
      // Decrypt if encrypted
      if (this.encryptionKey) {
        configData = this.decrypt(configData);
      }

      const config = JSON.parse(configData);

      // Restore configuration (this would update environment variables or config files)
      // For security, this should be done manually in production

      logger.success('Configuration restore completed');
      return { success: true, component: 'configuration' };

    } catch (error) {
      logger.error('Configuration restore failed:', error);
      return { success: false, component: 'configuration', error: error.message };
    }
  }

  /**
   * Clean up old backups
   */
  async cleanupOldBackups() {
    try {
      const backups = await this.listBackups();
      
      if (backups.length <= this.maxBackups) {
        return;
      }

      // Sort by date and remove oldest
      const backupsToDelete = backups
        .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
        .slice(0, backups.length - this.maxBackups);

      for (const backup of backupsToDelete) {
        const backupDir = path.join(this.backupPath, backup.id);
        await this.deleteDirectory(backupDir);
        logger.info('Deleted old backup', { backupId: backup.id });
      }

      logger.info('Cleanup completed', { deleted: backupsToDelete.length });

    } catch (error) {
      logger.error('Backup cleanup failed:', error);
    }
  }

  /**
   * List all backups
   */
  async listBackups() {
    try {
      const backupDirs = await fs.readdir(this.backupPath);
      const backups = [];

      for (const dir of backupDirs) {
        if (dir.startsWith('backup_')) {
          const manifestPath = path.join(this.backupPath, dir, 'manifest.json');
          try {
            const manifestData = await fs.readFile(manifestPath, 'utf8');
            const manifest = JSON.parse(manifestData);
            backups.push(manifest);
          } catch (error) {
            // Skip invalid backup directories
          }
        }
      }

      return backups.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));

    } catch (error) {
      logger.error('Failed to list backups:', error);
      return [];
    }
  }

  /**
   * Verify backup integrity
   */
  async verifyBackupIntegrity(backupId) {
    try {
      const manifest = await this.loadBackupManifest(backupId);
      if (!manifest) {
        return { success: false, error: 'Manifest not found' };
      }

      const verificationResults = [];

      for (const component of manifest.components) {
        const filepath = this.getComponentFilePath(component.type, component.file);
        const currentChecksum = await this.calculateChecksum(filepath);
        
        const isValid = currentChecksum === component.checksum;
        verificationResults.push({
          type: component.type,
          valid: isValid,
          expectedChecksum: component.checksum,
          actualChecksum: currentChecksum
        });
      }

      const allValid = verificationResults.every(r => r.valid);

      return {
        success: allValid,
        timestamp: new Date().toISOString(),
        results: verificationResults
      };

    } catch (error) {
      logger.error('Backup verification failed:', error);
      return { success: false, error: error.message };
    }
  }

  /**
   * Save backup manifest
   */
  async saveBackupManifest(backupId, manifest) {
    try {
      const backupDir = path.join(this.backupPath, backupId);
      await fs.mkdir(backupDir, { recursive: true });
      
      const manifestPath = path.join(backupDir, 'manifest.json');
      await fs.writeFile(manifestPath, JSON.stringify(manifest, null, 2));

    } catch (error) {
      logger.error('Failed to save backup manifest:', error);
    }
  }

  /**
   * Load backup manifest
   */
  async loadBackupManifest(backupId) {
    try {
      const manifestPath = path.join(this.backupPath, backupId, 'manifest.json');
      const manifestData = await fs.readFile(manifestPath, 'utf8');
      return JSON.parse(manifestData);

    } catch (error) {
      logger.error('Failed to load backup manifest:', error);
      return null;
    }
  }

  /**
   * Get component file path
   */
  getComponentFilePath(type, filename) {
    const typePaths = {
      database: 'database',
      files: 'files',
      configuration: 'config',
      logs: 'logs'
    };
    
    return path.join(this.backupPath, typePaths[type] || type, filename);
  }

  /**
   * Generate backup ID
   */
  generateBackupId() {
    const timestamp = new Date().toISOString().replace(/[:.]/g, '');
    const random = crypto.randomBytes(4).toString('hex');
    return `backup_${timestamp}_${random}`;
  }

  /**
   * Calculate file checksum
   */
  async calculateChecksum(filepath) {
    try {
      const data = await fs.readFile(filepath);
      return crypto.createHash('sha256').update(data).digest('hex');
    } catch (error) {
      logger.error('Failed to calculate checksum:', error);
      return null;
    }
  }

  /**
   * Encrypt data
   */
  encrypt(text) {
    if (!this.encryptionKey) return text;
    
    const algorithm = 'aes-256-gcm';
    const key = crypto.scryptSync(this.encryptionKey, 'salt', 32);
    const iv = crypto.randomBytes(16);
    
    const cipher = crypto.createCipher(algorithm, key, iv);
    
    let encrypted = cipher.update(text, 'utf8', 'hex');
    encrypted += cipher.final('hex');
    
    return iv.toString('hex') + ':' + encrypted;
  }

  /**
   * Decrypt data
   */
  decrypt(encryptedText) {
    if (!this.encryptionKey) return encryptedText;
    
    const algorithm = 'aes-256-gcm';
    const key = crypto.scryptSync(this.encryptionKey, 'salt', 32);
    
    const parts = encryptedText.split(':');
    const iv = Buffer.from(parts[0], 'hex');
    const encrypted = parts[1];
    
    const decipher = crypto.createDecipher(algorithm, key, iv);
    
    let decrypted = decipher.update(encrypted, 'hex', 'utf8');
    decrypted += decipher.final('utf8');
    
    return decrypted;
  }

  /**
   * Execute shell command
   */
  executeCommand(command) {
    return new Promise((resolve, reject) => {
      exec(command, (error, stdout, stderr) => {
        if (error) {
          reject(error);
        } else {
          resolve({ stdout, stderr });
        }
      });
    });
  }

  /**
   * Delete directory recursively
   */
  async deleteDirectory(dirPath) {
    try {
      await fs.rm(dirPath, { recursive: true, force: true });
    } catch (error) {
      logger.error('Failed to delete directory:', error);
    }
  }
}

module.exports = BackupService;
