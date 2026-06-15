/**
 * ============================================
 * ⏰ Cron Jobs - نظام إدهام
 * Automated background tasks
 * ============================================
 */

const cron = require('node-cron');
const logger = require('../utils/logger');
const Shipment = require('../models/Shipment');
const Invoice = require('../models/Invoice');
const User = require('../models/User');
const Notification = require('../models/Notification');
const Survey = require('../models/Survey');
const emailService = require('../services/emailService');

class CronJobs {
  constructor() {
    this.jobs = [];
  }

  start() {
    // Mark overdue invoices (daily at 9 AM)
    this.jobs.push(cron.schedule('0 9 * * *', async () => {
      await this.markOverdueInvoices();
    }));

    // Check late shipments (every 2 hours)
    this.jobs.push(cron.schedule('0 */2 * * *', async () => {
      await this.checkLateShipments();
    }));

    // Send delivery surveys (every hour)
    this.jobs.push(cron.schedule('0 * * * *', async () => {
      await this.createDeliverySurveys();
    }));

    // Check truck maintenance due (daily at 8 AM)
    this.jobs.push(cron.schedule('0 8 * * *', async () => {
      await this.checkTruckMaintenance();
    }));

    // Send invoice reminders (daily at 10 AM)
    this.jobs.push(cron.schedule('0 10 * * *', async () => {
      await this.sendInvoiceReminders();
    }));

    // Cleanup old notifications (weekly on Sunday at 3 AM)
    this.jobs.push(cron.schedule('0 3 * * 0', async () => {
      await this.cleanupOldNotifications();
    }));

    // Generate daily reports (daily at 11:59 PM)
    this.jobs.push(cron.schedule('59 23 * * *', async () => {
      await this.generateDailyReport();
    }));

    logger.info('✅ Cron jobs started successfully');
  }

  // Mark overdue invoices
  async markOverdueInvoices() {
    try {
      const today = new Date();
      
      const overdueInvoices = await Invoice.find({
        status: { $in: ['sent', 'partial'] },
        dueDate: { $lt: today }
      });

      for (const invoice of overdueInvoices) {
        invoice.status = 'overdue';
        await invoice.save();

        // Create notification
        await Notification.create({
          user: invoice.client,
          type: 'invoice_overdue',
          title: 'فاتورة متأخرة',
          message: `الفاتورة ${invoice.invoiceNumber} أصبحت متأخرة. يرجى الدفع في أقرب وقت.`,
          priority: 'high',
          data: { invoiceId: invoice._id }
        });

        logger.info(`Invoice ${invoice.invoiceNumber} marked as overdue`);
      }

      logger.info(`Marked ${overdueInvoices.length} invoices as overdue`);
    } catch (error) {
      logger.error('Mark overdue invoices error:', error);
    }
  }

  // Check late shipments
  async checkLateShipments() {
    try {
      const now = new Date();
      
      const lateShipments = await Shipment.find({
        status: { $nin: ['delivered', 'completed', 'cancelled'] },
        'delivery.scheduledDate': { $lt: now }
      }).populate('client', 'firstName lastName email');

      for (const shipment of lateShipments) {
        // Check if already notified recently (within last 24 hours)
        const recentNotification = await Notification.findOne({
          user: shipment.client,
          type: 'shipment_delayed',
          'data.shipmentId': shipment._id,
          createdAt: { $gte: new Date(Date.now() - 24 * 60 * 60 * 1000) }
        });

        if (!recentNotification) {
          await Notification.create({
            user: shipment.client,
            type: 'shipment_delayed',
            title: 'تأخير في التسليم',
            message: `شحنتك ${shipment.trackingNumber} متأخرة عن الموعد المحدد. نعتذر عن الإزعاج ونعمل على حل الأمر.`,
            priority: 'high',
            data: { shipmentId: shipment._id, trackingNumber: shipment.trackingNumber }
          });

          logger.info(`Late shipment notification sent for ${shipment.trackingNumber}`);
        }
      }
    } catch (error) {
      logger.error('Check late shipments error:', error);
    }
  }

  // Create delivery surveys
  async function createDeliverySurveys() {
    try {
      // Find shipments delivered in the last hour but without survey
      const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);
      const now = new Date();

      const deliveredShipments = await Shipment.find({
        status: 'delivered',
        'delivery.actualDate': { $gte: oneHourAgo, $lte: now },
        'survey.completed': false
      });

      for (const shipment of deliveredShipments) {
        // Check if survey already exists
        const existingSurvey = await Survey.findOne({ shipment: shipment._id });
        
        if (!existingSurvey) {
          const survey = await Survey.create({
            shipment: shipment._id,
            driver: shipment.driver,
            client: shipment.client,
            status: 'pending',
            expiresAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000) // 7 days
          });

          // Notify client
          await Notification.create({
            user: shipment.client,
            type: 'survey_reminder',
            title: 'قيّم تجربتك',
            message: `شحنتك ${shipment.trackingNumber} تم تسليمها. يرجى تقييم الخدمة لمساعدتنا في التحسين.`,
            data: { surveyId: survey._id, shipmentId: shipment._id }
          });

          logger.info(`Survey created for shipment ${shipment.trackingNumber}`);
        }
      }
    } catch (error) {
      logger.error('Create delivery surveys error:', error);
    }
  }

  // Check truck maintenance
  async checkTruckMaintenance() {
    try {
      const Truck = require('../models/Truck');
      const threeDaysFromNow = new Date(Date.now() + 3 * 24 * 60 * 60 * 1000);

      const trucksNeedingMaintenance = await Truck.find({
        'maintenance.nextMaintenance': { $lte: threeDaysFromNow },
        status: { $in: ['active', 'on_trip'] }
      });

      for (const truck of trucksNeedingMaintenance) {
        // Notify admin/supervisor
        const admins = await User.find({ role: { $in: ['admin', 'supervisor'] } });
        
        for (const admin of admins) {
          await Notification.create({
            user: admin._id,
            type: 'maintenance_due',
            title: 'صيانة شاحنة مطلوبة',
            message: `الشاحنة ${truck.plateNumber} تحتاج صيانة قريباً. التاريخ: ${truck.maintenance.nextMaintenance.toLocaleDateString('ar-SA')}`,
            priority: 'high',
            data: { truckId: truck._id }
          });
        }

        logger.info(`Maintenance reminder sent for truck ${truck.plateNumber}`);
      }
    } catch (error) {
      logger.error('Check truck maintenance error:', error);
    }
  }

  // Send invoice reminders
  async sendInvoiceReminders() {
    try {
      const threeDaysFromNow = new Date(Date.now() + 3 * 24 * 60 * 60 * 1000);

      const upcomingInvoices = await Invoice.find({
        status: { $in: ['sent', 'partial'] },
        dueDate: { $lte: threeDaysFromNow, $gte: new Date() }
      }).populate('client', 'firstName lastName email');

      for (const invoice of upcomingInvoices) {
        // Check if already reminded
        const alreadyReminded = invoice.reminders?.sent?.some(
          r => r.type === 'before_due' && 
          new Date(r.date) > new Date(Date.now() - 24 * 60 * 60 * 1000)
        );

        if (!alreadyReminded) {
          // Send email reminder
          await emailService.sendInvoiceEmail(invoice.client, invoice);

          // Record reminder
          if (!invoice.reminders) invoice.reminders = { sent: [] };
          invoice.reminders.sent.push({
            type: 'before_due',
            date: new Date(),
            method: 'email'
          });
          await invoice.save();

          logger.info(`Invoice reminder sent for ${invoice.invoiceNumber}`);
        }
      }
    } catch (error) {
      logger.error('Send invoice reminders error:', error);
    }
  }

  // Cleanup old notifications
  async cleanupOldNotifications() {
    try {
      const thirtyDaysAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000);

      const result = await Notification.deleteMany({
        read: true,
        createdAt: { $lt: thirtyDaysAgo }
      });

      logger.info(`Cleaned up ${result.deletedCount} old notifications`);
    } catch (error) {
      logger.error('Cleanup notifications error:', error);
    }
  }

  // Generate daily report
  async generateDailyReport() {
    try {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const tomorrow = new Date(today);
      tomorrow.setDate(tomorrow.getDate() + 1);

      const stats = {
        date: today,
        newShipments: await Shipment.countDocuments({ createdAt: { $gte: today, $lt: tomorrow } }),
        completedShipments: await Shipment.countDocuments({ 
          status: 'completed',
          'delivery.actualDate': { $gte: today, $lt: tomorrow }
        }),
        newUsers: await User.countDocuments({ createdAt: { $gte: today, $lt: tomorrow } }),
        revenue: await Invoice.aggregate([
          { 
            $match: { 
              'payment.paidAt': { $gte: today, $lt: tomorrow },
              status: { $in: ['paid', 'partial'] }
            }
          },
          { $group: { _id: null, total: { $sum: '$payment.paidAmount' } } }
        ])
      };

      logger.info(`Daily report generated: ${JSON.stringify(stats)}`);
    } catch (error) {
      logger.error('Generate daily report error:', error);
    }
  }

  stop() {
    this.jobs.forEach(job => job.stop());
    logger.info('Cron jobs stopped');
  }
}

module.exports = new CronJobs();
