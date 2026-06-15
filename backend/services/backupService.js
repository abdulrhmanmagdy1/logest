//
/**
 * ============================================
 * 💾 Backup & Disaster Recovery Service
 * خدمة النسخ الاحتياطي واستعادة البيانات
 * ============================================
 */

const { exec } = require('child_process');
const { promisify } = require('util');
const fs = require('fs').promises;
const path = require('path');
const archiver = require('archiver');
const AWS = require('aws-sdk');
const logger = require('../utils/logger');

const execAsync = promisify(exec);

class BackupService {
  constructor() {
    this.backupDir = path.join(__dirname, '../backups');
    this.retentionDays = 30;
    this.s3Enabled = process.env.AWS_S3_BUCKET ? true : false;
    
    if (this.s3Enabled) {
      this.s3 = new AWS.S3({
        accessKeyId: process.env.AWS_ACCESS_KEY_ID,
        secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
        region: process.env.AWS_REGION || 'us-east-1'
      });
      this.s3Bucket = process.env.AWS_S3_BUCKET;
    }

    this.initialize();
  }

  async initialize() {
    try {
      // Ensure backup directory exists
      await fs.mkdir(this.backupDir, { recursive: true });
      
      // Schedule automatic backups
      this.scheduleBackups();
      
      logger.info('Backup service initialized');
    } catch (error) {
      logger.error('Backup service initialization error:', error);
    }
  }

  /**
   * Create full backup
   */
  async createFullBackup(options = {}) {
    try {
      const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
      const backupName = `full_backup_${timestamp}`;
      const backupPath = path.join(this.backupDir, backupName);

      logger.info(`Starting full backup: ${backupName}`);

      // Create backup directory
      await fs.mkdir(backupPath, { recursive: true });

      const backupData = {
        name: backupName,
        type: 'full',
        startedAt: new Date(),
        components: []
      };

      // 1. Database backup
      const dbBackup = await this.backupDatabase(backupPath);
      backupData.components.push(dbBackup);

      // 2. Files backup
      const filesBackup = await this.backupFiles(backupPath);
      backupData.components.push(filesBackup);

      // 3. Configuration backup
      const configBackup = await this.backupConfiguration(backupPath);
      backupData.components.push(configBackup);

      // 4. Create manifest
      await this.createManifest(backupPath, backupData);

      // 5. Compress backup
      const compressedPath = await this.compressBackup(backupPath, backupName);

      // 6. Upload to S3 if enabled
      if (this.s3Enabled && options.uploadToS3 !== false) {
        await this.uploadToS3(compressedPath, backupName);
      }

      // 7. Clean up uncompressed backup
      await this.deleteDirectory(backupPath);

      backupData.completedAt = new Date();
      backupData.size = await this.getFileSize(compressedPath);
      backupData.path = compressedPath;
      backupData.s3Key = this.s3Enabled ? `backups/${backupName}.zip` : null;

      // Log backup
      await this.logBackup(backupData);

      logger.info(`Full backup completed: ${backupName}`);

      return {
        success: true,
        backup: backupData
      };

    } catch (error) {
      logger.error('Full backup error:', error);
      throw error;
    }
  }

  /**
   * Backup database
   */
  async backupDatabase(backupPath) {
    try {
      const dbName = process.env.DB_NAME || 'edham_logistics';
      const dbUri = process.env.MONGO_URI || 'mongodb://localhost:27017';
      
      const backupFile = path.join(backupPath, 'database.gz');
      
      // Use mongodump for MongoDB backup
      const command = `mongodump --uri="${dbUri}" --db=${dbName} --archive="${backupFile}" --gzip`;
      
      await execAsync(command);

      const stats = await fs.stat(backupFile);

      return {
        type: 'database',
        name: 'database.gz',
        path: backupFile,
        size: stats.size,
        status: 'success'
      };

    } catch (error) {
      logger.error('Database backup error:', error);
      return {
        type: 'database',
        status: 'failed',
        error: error.message
      };
    }
  }

  /**
   * Backup files
   */
  async backupFiles(backupPath) {
    try {
      const filesBackupPath = path.join(backupPath, 'files');
      await fs.mkdir(filesBackupPath, { recursive: true });

      // Directories to backup
      const directories = [
        { source: 'uploads', dest: 'uploads' },
        { source: 'exports', dest: 'exports' },
        { source: 'logs', dest: 'logs' }
      ];

      const baseDir = path.join(__dirname, '..');

      for (const dir of directories) {
        const sourcePath = path.join(baseDir, dir.source);
        const destPath = path.join(filesBackupPath, dir.dest);

        try {
          await fs.access(sourcePath);
          await this.copyDirectory(sourcePath, destPath);
        } catch (e) {
          logger.warn(`Directory ${dir.source} not found, skipping`);
        }
      }

      const stats = await this.getDirectorySize(filesBackupPath);

      return {
        type: 'files',
        path: filesBackupPath,
        size: stats,
        status: 'success'
      };

    } catch (error) {
      logger.error('Files backup error:', error);
      return {
        type: 'files',
        status: 'failed',
        error: error.message
      };
    }
  }

  /**
   * Backup configuration
   */
  async backupConfiguration(backupPath) {
    try {
      const configPath = path.join(backupPath, 'config');
      await fs.mkdir(configPath, { recursive: true });

      // Files to backup
      const configFiles = [
        '.env',
        'docker-compose.yml',
        'nginx.conf',
        'package.json',
        'ecosystem.config.js'
      ];

      const baseDir = path.join(__dirname, '..');

      for (const file of configFiles) {
        const sourcePath = path.join(baseDir, file);
        const destPath = path.join(configPath, file);

        try {
          await fs.copyFile(sourcePath, destPath);
        } catch (e) {
          logger.warn(`Config file ${file} not found, skipping`);
        }
      }

      // Save backup metadata
      const metadata = {
        version: process.env.npm_package_version || '1.0.0',
        nodeVersion: process.version,
        platform: process.platform,
        backupDate: new Date(),
        environment: process.env.NODE_ENV || 'development'
      };

      await fs.writeFile(
        path.join(configPath, 'backup-metadata.json'),
        JSON.stringify(metadata, null, 2)
      );

      return {
        type: 'configuration',
        path: configPath,
        status: 'success'
      };

    } catch (error) {
      logger.error('Configuration backup error:', error);
      return {
        type: 'configuration',
        status: 'failed',
        error: error.message
      };
    }
  }

  /**
   * Create manifest file
   */
  async createManifest(backupPath, backupData) {
    const manifestPath = path.join(backupPath, 'manifest.json');
    await fs.writeFile(manifestPath, JSON.stringify(backupData, null, 2));
  }

  /**
   * Compress backup
   */
  async compressBackup(sourcePath, backupName) {
    return new Promise((resolve, reject) => {
      const outputPath = path.join(this.backupDir, `${backupName}.zip`);
      const output = require('fs').createWriteStream(outputPath);
      const archive = archiver('zip', { zlib: { level: 9 } });

      output.on('close', () => resolve(outputPath));
      archive.on('error', reject);

      archive.pipe(output);
      archive.directory(sourcePath, false);
      archive.finalize();
    });
  }

  /**
   * Upload to S3
   */
  async uploadToS3(filePath, backupName) {
    try {
      const fileContent = await fs.readFile(filePath);
      const key = `backups/${backupName}.zip`;

      await this.s3.putObject({
        Bucket: this.s3Bucket,
        Key: key,
        Body: fileContent,
        StorageClass: 'STANDARD_IA' // Infrequent Access
      }).promise();

      logger.info(`Backup uploaded to S3: ${key}`);

      return { success: true, key };

    } catch (error) {
      logger.error('S3 upload error:', error);
      throw error;
    }
  }

  /**
   * Restore from backup
   */
  async restoreFromBackup(backupPath, options = {}) {
    try {
      logger.info(`Starting restore from: ${backupPath}`);

      // Extract if compressed
      let restoreDir = backupPath;
      if (backupPath.endsWith('.zip')) {
        restoreDir = await this.extractBackup(backupPath);
      }

      // Read manifest
      const manifestPath = path.join(restoreDir, 'manifest.json');
      const manifest = JSON.parse(await fs.readFile(manifestPath, 'utf8'));

      const restoreResults = [];

      // Restore database
      if (options.restoreDatabase !== false) {
        const dbResult = await this.restoreDatabase(restoreDir, options);
        restoreResults.push(dbResult);
      }

      // Restore files
      if (options.restoreFiles !== false) {
        const filesResult = await this.restoreFiles(restoreDir);
        restoreResults.push(filesResult);
      }

      // Restore configuration
      if (options.restoreConfig !== false) {
        const configResult = await this.restoreConfiguration(restoreDir);
        restoreResults.push(configResult);
      }

      // Cleanup extracted files if needed
      if (backupPath.endsWith('.zip') && options.keepExtracted !== true) {
        await this.deleteDirectory(restoreDir);
      }

      logger.info('Restore completed');

      return {
        success: true,
        backup: manifest,
        results: restoreResults
      };

    } catch (error) {
      logger.error('Restore error:', error);
      throw error;
    }
  }

  /**
   * Restore database
   */
  async restoreDatabase(restoreDir, options) {
    try {
      const dbName = process.env.DB_NAME || 'edham_logistics';
      const dbUri = process.env.MONGO_URI || 'mongodb://localhost:27017';
      
      const backupFile = path.join(restoreDir, 'database.gz');

      // Verify backup file exists
      await fs.access(backupFile);

      // Optional: Drop existing database
      if (options.dropBeforeRestore) {
        logger.warn('Dropping existing database before restore');
        const dropCommand = `mongo ${dbUri}/${dbName} --eval "db.dropDatabase()"`;
        await execAsync(dropCommand);
      }

      // Restore using mongorestore
      const command = `mongorestore --uri="${dbUri}" --nsInclude=${dbName}.* --archive="${backupFile}" --gzip`;
      
      await execAsync(command);

      return {
        type: 'database',
        status: 'success'
      };

    } catch (error) {
      logger.error('Database restore error:', error);
      return {
        type: 'database',
        status: 'failed',
        error: error.message
      };
    }
  }

  /**
   * Restore files
   */
  async restoreFiles(restoreDir) {
    try {
      const filesPath = path.join(restoreDir, 'files');
      const baseDir = path.join(__dirname, '..');

      // Verify files directory exists
      await fs.access(filesPath);

      // Copy directories back
      const directories = ['uploads', 'exports', 'logs'];

      for (const dir of directories) {
        const sourcePath = path.join(filesPath, dir);
        const destPath = path.join(baseDir, dir);

        try {
          await fs.access(sourcePath);
          
          // Create backup of current files
          const backupOld = `${destPath}_backup_${Date.now()}`;
          try {
            await fs.access(destPath);
            await fs.rename(destPath, backupOld);
          } catch (e) {
            // Directory doesn't exist, that's fine
          }

          // Restore from backup
          await this.copyDirectory(sourcePath, destPath);

        } catch (e) {
          logger.warn(`Could not restore ${dir}: ${e.message}`);
        }
      }

      return {
        type: 'files',
        status: 'success'
      };

    } catch (error) {
      logger.error('Files restore error:', error);
      return {
        type: 'files',
        status: 'failed',
        error: error.message
      };
    }
  }

  /**
   * Restore configuration
   */
  async restoreConfiguration(restoreDir) {
    try {
      const configPath = path.join(restoreDir, 'config');
      
      // Verify config directory exists
      await fs.access(configPath);

      // Just log the available config - don't auto-restore to avoid overwriting
      const files = await fs.readdir(configPath);
      
      return {
        type: 'configuration',
        status: 'success',
        availableFiles: files,
        note: 'Configuration files available for manual restore'
      };

    } catch (error) {
      logger.error('Configuration restore error:', error);
      return {
        type: 'configuration',
        status: 'failed',
        error: error.message
      };
    }
  }

  /**
   * List available backups
   */
  async listBackups(options = {}) {
    try {
      const backups = [];

      // Local backups
      const localFiles = await fs.readdir(this.backupDir);
      
      for (const file of localFiles) {
        if (file.endsWith('.zip')) {
          const stat = await fs.stat(path.join(this.backupDir, file));
          backups.push({
            name: file.replace('.zip', ''),
            type: 'local',
            path: path.join(this.backupDir, file),
            size: stat.size,
            createdAt: stat.birthtime
          });
        }
      }

      // S3 backups
      if (this.s3Enabled && options.includeS3 !== false) {
        const s3Objects = await this.s3.listObjectsV2({
          Bucket: this.s3Bucket,
          Prefix: 'backups/'
        }).promise();

        for (const obj of s3Objects.Contents || []) {
          if (obj.Key.endsWith('.zip')) {
            backups.push({
              name: path.basename(obj.Key, '.zip'),
              type: 's3',
              key: obj.Key,
              size: obj.Size,
              createdAt: obj.LastModified
            });
          }
        }
      }

      // Sort by date (newest first)
      backups.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

      return {
        success: true,
        count: backups.length,
        backups
      };

    } catch (error) {
      logger.error('List backups error:', error);
      throw error;
    }
  }

  /**
   * Schedule automatic backups
   */
  scheduleBackups() {
    const cron = require('node-cron');

    // Daily backup at 2 AM
    cron.schedule('0 2 * * *', async () => {
      logger.info('Running scheduled daily backup');
      try {
        await this.createFullBackup();
        await this.cleanOldBackups();
      } catch (error) {
        logger.error('Scheduled backup error:', error);
      }
    });

    logger.info('Backup schedule configured: Daily at 2:00 AM');
  }

  /**
   * Clean old backups
   */
  async cleanOldBackups() {
    try {
      const cutoffDate = new Date();
      cutoffDate.setDate(cutoffDate.getDate() - this.retentionDays);

      const backups = await this.listBackups();

      for (const backup of backups.backups) {
        if (backup.type === 'local' && new Date(backup.createdAt) < cutoffDate) {
          await fs.unlink(backup.path);
          logger.info(`Deleted old backup: ${backup.name}`);
        }
      }

    } catch (error) {
      logger.error('Clean old backups error:', error);
    }
  }

  /**
   * Helper: Copy directory recursively
   */
  async copyDirectory(src, dest) {
    await fs.mkdir(dest, { recursive: true });
    const entries = await fs.readdir(src, { withFileTypes: true });

    for (const entry of entries) {
      const srcPath = path.join(src, entry.name);
      const destPath = path.join(dest, entry.name);

      if (entry.isDirectory()) {
        await this.copyDirectory(srcPath, destPath);
      } else {
        await fs.copyFile(srcPath, destPath);
      }
    }
  }

  /**
   * Helper: Delete directory
   */
  async deleteDirectory(dir) {
    try {
      const entries = await fs.readdir(dir, { withFileTypes: true });

      for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);
        if (entry.isDirectory()) {
          await this.deleteDirectory(fullPath);
        } else {
          await fs.unlink(fullPath);
        }
      }

      await fs.rmdir(dir);
    } catch (error) {
      logger.error(`Delete directory error: ${dir}`, error);
    }
  }

  /**
   * Helper: Get file size
   */
  async getFileSize(filePath) {
    const stats = await fs.stat(filePath);
    return stats.size;
  }

  /**
   * Helper: Get directory size
   */
  async getDirectorySize(dirPath) {
    let size = 0;

    const entries = await fs.readdir(dirPath, { withFileTypes: true });

    for (const entry of entries) {
      const fullPath = path.join(dirPath, entry.name);
      if (entry.isDirectory()) {
        size += await this.getDirectorySize(fullPath);
      } else {
        const stats = await fs.stat(fullPath);
        size += stats.size;
      }
    }

    return size;
  }

  /**
   * Helper: Extract backup
   */
  async extractBackup(zipPath) {
    const extractDir = zipPath.replace('.zip', '_extracted');
    
    return new Promise((resolve, reject) => {
      const extract = require('extract-zip');
      extract(zipPath, { dir: extractDir })
        .then(() => resolve(extractDir))
        .catch(reject);
    });
  }

  /**
   * Log backup operation
   */
  async logBackup(backupData) {
    try {
      const BackupLog = require('../models/BackupLog');
      await BackupLog.create(backupData);
    } catch (error) {
      logger.error('Log backup error:', error);
    }
  }

  /**
   * Get backup status
   */
  async getBackupStatus() {
    try {
      const backups = await this.listBackups();
      const lastBackup = backups.backups[0];
      
      const now = new Date();
      const lastBackupTime = lastBackup ? new Date(lastBackup.createdAt) : null;
      const hoursSinceLastBackup = lastBackupTime 
        ? (now - lastBackupTime) / (1000 * 60 * 60)
        : null;

      return {
        success: true,
        totalBackups: backups.count,
        lastBackup: lastBackup ? {
          name: lastBackup.name,
          date: lastBackup.createdAt,
          size: lastBackup.size,
          type: lastBackup.type
        } : null,
        hoursSinceLastBackup,
        storageUsed: backups.backups.reduce((sum, b) => sum + (b.size || 0), 0),
        s3Enabled: this.s3Enabled,
        retentionDays: this.retentionDays,
        status: hoursSinceLastBackup && hoursSinceLastBackup < 25 ? 'healthy' : 'warning'
      };

    } catch (error) {
      logger.error('Get backup status error:', error);
      throw error;
    }
  }
}

module.exports = new BackupService();
