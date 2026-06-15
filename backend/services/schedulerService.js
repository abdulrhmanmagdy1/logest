//
/**
 * ============================================
 * ⏰ Scheduler Service - خدمة الجدولة
 * ============================================
 */

const cron = require('node-cron');
const logger = require('../utils/logger');

class SchedulerService {
  constructor() {
    this.jobs = new Map();
    this.running = false;
  }

  // Initialize scheduler
  init() {
    logger.info('Scheduler service initialized');
    this.running = true;
  }

  // Schedule a recurring job
  schedule(name, cronExpression, task, options = {}) {
    try {
      if (!cron.validate(cronExpression)) {
        throw new Error(`Invalid cron expression: ${cronExpression}`);
      }

      // Cancel existing job with same name
      if (this.jobs.has(name)) {
        this.jobs.get(name).stop();
      }

      const job = cron.schedule(cronExpression, async () => {
        try {
          logger.info(`Executing scheduled job: ${name}`);
          await task();
          logger.info(`Scheduled job completed: ${name}`);
        } catch (error) {
          logger.error(`Scheduled job failed: ${name}`, error);
          if (options.onError) {
            options.onError(error);
          }
        }
      }, {
        scheduled: options.startImmediately !== false,
        timezone: options.timezone || 'Asia/Riyadh'
      });

      this.jobs.set(name, {
        cron: job,
        expression: cronExpression,
        options,
        createdAt: new Date()
      });

      logger.info(`Job scheduled: ${name} (${cronExpression})`);
      return true;

    } catch (error) {
      logger.error(`Failed to schedule job: ${name}`, error);
      return false;
    }
  }

  // Schedule one-time job
  scheduleOnce(name, date, task) {
    const now = new Date();
    const delay = date.getTime() - now.getTime();

    if (delay <= 0) {
      logger.warn(`Schedule date is in the past: ${name}`);
      return false;
    }

    const timeout = setTimeout(async () => {
      try {
        logger.info(`Executing one-time job: ${name}`);
        await task();
        logger.info(`One-time job completed: ${name}`);
      } catch (error) {
        logger.error(`One-time job failed: ${name}`, error);
      } finally {
        this.jobs.delete(name);
      }
    }, delay);

    this.jobs.set(name, {
      type: 'once',
      timeout,
      scheduledFor: date,
      createdAt: new Date()
    });

    logger.info(`One-time job scheduled: ${name} for ${date.toISOString()}`);
    return true;
  }

  // Cancel a scheduled job
  cancel(name) {
    if (!this.jobs.has(name)) {
      return false;
    }

    const job = this.jobs.get(name);
    
    if (job.cron) {
      job.cron.stop();
    } else if (job.timeout) {
      clearTimeout(job.timeout);
    }

    this.jobs.delete(name);
    logger.info(`Job cancelled: ${name}`);
    return true;
  }

  // Get all scheduled jobs
  getJobs() {
    const jobs = [];
    this.jobs.forEach((job, name) => {
      jobs.push({
        name,
        type: job.cron ? 'recurring' : 'once',
        expression: job.expression,
        scheduledFor: job.scheduledFor,
        createdAt: job.createdAt
      });
    });
    return jobs;
  }

  // Check if job exists
  hasJob(name) {
    return this.jobs.has(name);
  }

  // Stop all jobs
  stopAll() {
    this.jobs.forEach((job, name) => {
      if (job.cron) {
        job.cron.stop();
      } else if (job.timeout) {
        clearTimeout(job.timeout);
      }
      logger.info(`Job stopped: ${name}`);
    });
    this.jobs.clear();
    this.running = false;
    logger.info('All jobs stopped');
  }

  // Common scheduled tasks
  setupCommonTasks() {
    // Daily cleanup at 3 AM
    this.schedule('daily-cleanup', '0 3 * * *', async () => {
      // Cleanup old logs, temp files, etc.
      logger.info('Running daily cleanup');
    });

    // Weekly report generation (Sundays at 8 AM)
    this.schedule('weekly-reports', '0 8 * * 0', async () => {
      logger.info('Generating weekly reports');
    });

    // Monthly billing (1st of month at 6 AM)
    this.schedule('monthly-billing', '0 6 1 * *', async () => {
      logger.info('Processing monthly billing');
    });

    // Reset API usage (1st of month at midnight)
    this.schedule('reset-api-usage', '0 0 1 * *', async () => {
      logger.info('Resetting API usage counters');
    });

    // Health check every 5 minutes
    this.schedule('health-check', '*/5 * * * *', async () => {
      logger.info('Health check ping');
    });
  }
}

module.exports = new SchedulerService();
