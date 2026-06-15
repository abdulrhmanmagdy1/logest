/**
 * ============================================
 * 📊 Reporting Controller - نظام إدهام الاحترافي
 * Edham Logistics - Advanced Reporting System
 * ============================================
 */

const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');
const Report = require('../models/Report');
const Shipment = require('../models/Shipment');
const Client = require('../models/Client');
const Driver = require('../models/Driver');
const Transaction = require('../models/Transaction');
const Maintenance = require('../models/Maintenance');
const ExcelJS = require('exceljs');
const PDFDocument = require('pdfkit');

class ReportingController {
  /**
   * Get available report templates
   */
  static async getReportTemplates(req, res) {
    try {
      const templates = [
        {
          id: 'revenue_report',
          name: 'تقرير الإيرادات',
          description: 'تحليل شامل للإيرادات والأداء المالي',
          category: 'financial',
          parameters: [
            { name: 'startDate', type: 'date', required: true, label: 'تاريخ البداية' },
            { name: 'endDate', type: 'date', required: true, label: 'تاريخ النهاية' },
            { name: 'groupBy', type: 'select', options: ['day', 'week', 'month', 'quarter'], label: 'تجميع حسب' },
            { name: 'includeTaxes', type: 'boolean', default: false, label: 'تضمين الضرائب' }
          ]
        },
        {
          id: 'shipment_performance',
          name: 'تقرير أداء الشحنات',
          description: 'تحليل أداء الشحنات ومؤشرات التسليم',
          category: 'operational',
          parameters: [
            { name: 'startDate', type: 'date', required: true, label: 'تاريخ البداية' },
            { name: 'endDate', type: 'date', required: true, label: 'تاريخ النهاية' },
            { name: 'status', type: 'select', options: ['all', 'delivered', 'cancelled'], label: 'حالة الشحنة' },
            { name: 'groupBy', type: 'select', options: ['client', 'driver', 'route'], label: 'تجميع حسب' }
          ]
        },
        {
          id: 'fleet_utilization',
          name: 'تقرير استغلال الأسطول',
          description: 'تحليل استغلال المركبات والسائقين',
          category: 'fleet',
          parameters: [
            { name: 'startDate', type: 'date', required: true, label: 'تاريخ البداية' },
            { name: 'endDate', type: 'date', required: true, label: 'تاريخ النهاية' },
            { name: 'vehicleType', type: 'select', options: ['all', 'truck', 'van', 'trailer'], label: 'نوع المركبة' },
            { name: 'includeMaintenance', type: 'boolean', default: true, label: 'تضمين بيانات الصيانة' }
          ]
        },
        {
          id: 'customer_analysis',
          name: 'تحليل العملاء',
          description: 'تحليل شامل للعملاء والرضا',
          category: 'customer',
          parameters: [
            { name: 'startDate', type: 'date', required: true, label: 'تاريخ البداية' },
            { name: 'endDate', type: 'date', required: true, label: 'تاريخ النهاية' },
            { name: 'customerType', type: 'select', options: ['all', 'active', 'inactive'], label: 'نوع العميل' },
            { name: 'includeSatisfaction', type: 'boolean', default: true, label: 'تضمين بيانات الرضا' }
          ]
        },
        {
          id: 'financial_summary',
          name: 'الملخص المالي',
          description: 'تقرير مالي شامل مع جميع المعاملات',
          category: 'financial',
          parameters: [
            { name: 'startDate', type: 'date', required: true, label: 'تاريخ البداية' },
            { name: 'endDate', type: 'date', required: true, label: 'تاريخ النهاية' },
            { name: 'transactionType', type: 'select', options: ['all', 'income', 'expense'], label: 'نوع المعاملة' },
            { name: 'currency', type: 'select', options: ['SAR', 'USD', 'EUR'], label: 'العملة' }
          ]
        },
        {
          id: 'driver_performance',
          name: 'أداء السائقين',
          description: 'تقييم أداء السائقين ومؤشرات الكفاءة',
          category: 'hr',
          parameters: [
            { name: 'startDate', type: 'date', required: true, label: 'تاريخ البداية' },
            { name: 'endDate', type: 'date', required: true, label: 'تاريخ النهاية' },
            { name: 'groupBy', type: 'select', options: ['driver', 'team', 'region'], label: 'تجميع حسب' },
            { name: 'includeLocation', type: 'boolean', default: true, label: 'تضمين بيانات الموقع' }
          ]
        }
      ];

      res.json({
        success: true,
        templates
      });
    } catch (error) {
      logger.error('Error getting report templates:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Generate custom report
   */
  static async generateReport(req, res) {
    try {
      const { templateId, parameters, format = 'json' } = req.body;

      // Validate template
      const template = await this.getTemplateById(templateId);
      if (!template) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'قالب التقرير غير موجود'
        });
      }

      // Validate parameters
      const validationResult = this.validateParameters(template.parameters, parameters);
      if (!validationResult.valid) {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'معلمات غير صالحة',
          errors: validationResult.errors
        });
      }

      // Generate report data
      const reportData = await this.generateReportData(template, parameters);

      // Save report to database
      const report = new Report({
        templateId,
        templateName: template.name,
        parameters,
        data: reportData,
        format,
        generatedBy: req.user.id,
        status: 'completed'
      });

      await report.save();

      // Return report in requested format
      if (format === 'excel') {
        const buffer = await this.generateExcelReport(reportData, template);
        res.setHeader('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        res.setHeader('Content-Disposition', `attachment; filename="${template.name}_${Date.now()}.xlsx"`);
        res.send(buffer);
      } else if (format === 'pdf') {
        const buffer = await this.generatePDFReport(reportData, template);
        res.setHeader('Content-Type', 'application/pdf');
        res.setHeader('Content-Disposition', `attachment; filename="${template.name}_${Date.now()}.pdf"`);
        res.send(buffer);
      } else {
        res.json({
          success: true,
          report: {
            id: report._id,
            templateName: template.name,
            data: reportData,
            generatedAt: report.createdAt,
            format
          }
        });
      }

      logger.success('Report generated', { 
        templateId, 
        reportId: report._id,
        userId: req.user.id 
      });

    } catch (error) {
      logger.error('Error generating report:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get saved reports
   */
  static async getReports(req, res) {
    try {
      const {
        page = 1,
        limit = 20,
        templateId,
        startDate,
        endDate,
        status
      } = req.query;

      const query = { generatedBy: req.user.id };
      
      if (templateId) query.templateId = templateId;
      if (startDate || endDate) {
        query.createdAt = {};
        if (startDate) query.createdAt.$gte = new Date(startDate);
        if (endDate) query.createdAt.$lte = new Date(endDate);
      }
      if (status) query.status = status;

      const skip = (page - 1) * limit;

      const [reports, total] = await Promise.all([
        Report.find(query)
          .sort({ createdAt: -1 })
          .skip(skip)
          .limit(parseInt(limit))
          .populate('templateId', 'name category'),
        Report.countDocuments(query)
      ]);

      res.json({
        success: true,
        reports,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / limit)
        }
      });
    } catch (error) {
      logger.error('Error getting reports:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get report details
   */
  static async getReportDetails(req, res) {
    try {
      const { id } = req.params;

      const report = await Report.findById(id)
        .populate('templateId', 'name category parameters')
        .populate('generatedBy', 'name email');

      if (!report) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'التقرير غير موجود'
        });
      }

      // Check permissions
      if (report.generatedBy._id.toString() !== req.user.id && req.user.role !== 'admin') {
        return res.status(HTTP_STATUS.FORBIDDEN).json({
          success: false,
          message: 'غير مصرح بالوصول إلى هذا التقرير'
        });
      }

      res.json({
        success: true,
        report
      });
    } catch (error) {
      logger.error('Error getting report details:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Create custom report template
   */
  static async createTemplate(req, res) {
    try {
      const {
        name,
        description,
        category,
        parameters,
        query,
        format
      } = req.body;

      // Validate template data
      const templateData = {
        name,
        description,
        category,
        parameters,
        query,
        format,
        isCustom: true,
        createdBy: req.user.id
      };

      // Save custom template
      const ReportTemplate = require('../models/ReportTemplate');
      const template = new ReportTemplate(templateData);
      await template.save();

      logger.success('Custom template created', { 
        templateId: template._id,
        userId: req.user.id 
      });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'تم إنشاء القالب بنجاح',
        template
      });
    } catch (error) {
      logger.error('Error creating template:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Schedule recurring report
   */
  static async scheduleRecurringReport(req, res) {
    try {
      const {
        templateId,
        parameters,
        schedule,
        recipients,
        format
      } = req.body;

      const scheduleData = {
        templateId,
        parameters,
        schedule: {
          frequency: schedule.frequency, // daily, weekly, monthly
          dayOfWeek: schedule.dayOfWeek, // 0-6 for weekly
          dayOfMonth: schedule.dayOfMonth, // 1-31 for monthly
          time: schedule.time // HH:MM format
        },
        recipients,
        format,
        isActive: true,
        createdBy: req.user.id
      };

      const ScheduledReport = require('../models/ScheduledReport');
      const scheduledReport = new ScheduledReport(scheduleData);
      await scheduledReport.save();

      logger.success('Recurring report scheduled', { 
        scheduleId: scheduledReport._id,
        userId: req.user.id 
      });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'تم جدولة التقرير بنجاح',
        scheduledReport
      });
    } catch (error) {
      logger.error('Error scheduling report:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get scheduled reports
   */
  static async getScheduledReports(req, res) {
    try {
      const scheduledReports = await require('../models/ScheduledReport')
        .find({ createdBy: req.user.id, isActive: true })
        .populate('templateId', 'name category')
        .sort({ createdAt: -1 });

      res.json({
        success: true,
        scheduledReports
      });
    } catch (error) {
      logger.error('Error getting scheduled reports:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get template by ID
   */
  static async getTemplateById(templateId) {
    const templates = await this.getReportTemplates();
    return templates.find(t => t.id === templateId);
  }

  /**
   * Validate parameters against template requirements
   */
  static validateParameters(templateParameters, providedParameters) {
    const errors = [];

    for (const param of templateParameters) {
      if (param.required && !providedParameters[param.name]) {
        errors.push(`المعلمة ${param.label} مطلوبة`);
      }

      if (providedParameters[param.name]) {
        // Type validation
        if (param.type === 'date') {
          const date = new Date(providedParameters[param.name]);
          if (isNaN(date.getTime())) {
            errors.push(`المعلمة ${param.label} يجب أن تكون تاريخ صالح`);
          }
        }

        if (param.type === 'select' && param.options) {
          if (!param.options.includes(providedParameters[param.name])) {
            errors.push(`المعلمة ${param.label} يجب أن تكون من الخيارات المتاحة`);
          }
        }
      }
    }

    return {
      valid: errors.length === 0,
      errors
    };
  }

  /**
   * Generate report data based on template
   */
  static async generateReportData(template, parameters) {
    switch (template.id) {
      case 'revenue_report':
        return await this.generateRevenueReport(parameters);
      case 'shipment_performance':
        return await this.generateShipmentPerformanceReport(parameters);
      case 'fleet_utilization':
        return await this.generateFleetUtilizationReport(parameters);
      case 'customer_analysis':
        return await this.generateCustomerAnalysisReport(parameters);
      case 'financial_summary':
        return await this.generateFinancialSummaryReport(parameters);
      case 'driver_performance':
        return await this.generateDriverPerformanceReport(parameters);
      default:
        throw new Error('قالب التقرير غير مدعوم');
    }
  }

  /**
   * Generate revenue report data
   */
  static async generateRevenueReport(parameters) {
    const { startDate, endDate, groupBy = 'month', includeTaxes = false } = parameters;

    const matchQuery = {
      createdAt: {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      },
      status: 'completed'
    };

    const revenueData = await Transaction.aggregate([
      { $match: matchQuery },
      {
        $group: this.getGroupByExpression(groupBy),
        revenue: { $sum: '$amount' },
        count: { $sum: 1 },
        average: { $avg: '$amount' }
      }
    },
      { $sort: { '_id': 1 } }
    ]);

    return {
      title: 'تقرير الإيرادات',
      period: { startDate, endDate },
      groupBy,
      summary: {
        totalRevenue: revenueData.reduce((sum, item) => sum + item.revenue, 0),
        totalTransactions: revenueData.reduce((sum, item) => sum + item.count, 0),
        averageTransaction: revenueData.reduce((sum, item) => sum + item.average, 0) / revenueData.length
      },
      data: revenueData,
      includeTaxes
    };
  }

  /**
   * Generate shipment performance report
   */
  static async generateShipmentPerformanceReport(parameters) {
    const { startDate, endDate, status = 'all', groupBy = 'client' } = parameters;

    const matchQuery = {
      createdAt: {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      }
    };

    if (status !== 'all') {
      matchQuery.status = status;
    }

    const performanceData = await Shipment.aggregate([
      { $match: matchQuery },
      {
        $group: this.getGroupByExpression(groupBy),
        totalShipments: { $sum: 1 },
        deliveredShipments: {
          $sum: { $cond: [{ $eq: ['$status', 'delivered'] }, 1, 0] }
        },
        averageDeliveryTime: {
          $avg: {
            $cond: [
              { $and: [
                { $eq: ['$status', 'delivered'] },
                { $ne: ['$actualDelivery', null] },
                { $ne: ['$estimatedDelivery', null] }
              ]},
              { $subtract: ['$actualDelivery', '$estimatedDelivery'] },
              null
            ]
          }
        },
        totalRevenue: { $sum: '$amount' }
      }
    },
      { $sort: { totalRevenue: -1 } }
    ]);

    return {
      title: 'تقرير أداء الشحنات',
      period: { startDate, endDate },
      status,
      groupBy,
      summary: {
        totalShipments: performanceData.reduce((sum, item) => sum + item.totalShipments, 0),
        deliveredShipments: performanceData.reduce((sum, item) => sum + item.deliveredShipments, 0),
        deliveryRate: performanceData.length > 0 ? 
          (performanceData.reduce((sum, item) => sum + item.deliveredShipments, 0) / 
           performanceData.reduce((sum, item) => sum + item.totalShipments, 0) * 100) : 0
      },
      data: performanceData
    };
  }

  /**
   * Generate fleet utilization report
   */
  static async generateFleetUtilizationReport(parameters) {
    const { startDate, endDate, vehicleType = 'all', includeMaintenance = true } = parameters;

    const matchQuery = {
      createdAt: {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      }
    };

    if (vehicleType !== 'all') {
      matchQuery['vehicle.type'] = vehicleType;
    }

    const utilizationData = await Shipment.aggregate([
      { $match: matchQuery },
      {
        $lookup: {
          from: 'trucks',
          localField: 'truckId',
          foreignField: '_id',
          as: 'truck'
        }
      },
      { $unwind: '$truck' },
      {
        $group: {
          _id: '$truck._id',
          plateNumber: { $first: '$truck.plateNumber' },
          vehicleType: { $first: '$truck.type' },
          totalShipments: { $sum: 1 },
          totalDistance: { $sum: '$distance' },
          totalRevenue: { $sum: '$amount' },
          averageRevenuePerShipment: { $avg: '$amount' }
        }
      }
    ]);

    return {
      title: 'تقرير استغلال الأسطول',
      period: { startDate, endDate },
      vehicleType,
      includeMaintenance,
      summary: {
        totalVehicles: utilizationData.length,
        averageShipmentsPerVehicle: utilizationData.length > 0 ? 
          utilizationData.reduce((sum, item) => sum + item.totalShipments, 0) / utilizationData.length : 0,
        totalRevenue: utilizationData.reduce((sum, item) => sum + item.totalRevenue, 0)
      },
      data: utilizationData
    };
  }

  /**
   * Generate customer analysis report
   */
  static async generateCustomerAnalysisReport(parameters) {
    const { startDate, endDate, customerType = 'all', includeSatisfaction = true } = parameters;

    const matchQuery = {
      createdAt: {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      }
    };

    if (customerType !== 'all') {
      matchQuery.status = customerType === 'active' ? 'active' : { $ne: 'active' };
    }

    const customerData = await Client.aggregate([
      { $match: matchQuery },
      {
        $lookup: {
          from: 'shipments',
          localField: '_id',
          foreignField: 'clientId',
          as: 'shipments'
        }
      },
      {
        $group: {
          _id: '$_id',
          name: { $first: '$name' },
          email: { $first: '$email' },
          totalShipments: { $size: '$shipments' },
          totalRevenue: { $sum: '$shipments.amount' },
          averageOrderValue: { $avg: '$shipments.amount' },
          lastShipmentDate: { $max: '$shipments.createdAt' }
        }
      },
      { $sort: { totalRevenue: -1 } }
    ]);

    return {
      title: 'تحليل العملاء',
      period: { startDate, endDate },
      customerType,
      includeSatisfaction,
      summary: {
        totalCustomers: customerData.length,
        totalRevenue: customerData.reduce((sum, item) => sum + item.totalRevenue, 0),
        averageOrderValue: customerData.reduce((sum, item) => sum + item.averageOrderValue, 0) / customerData.length
      },
      data: customerData
    };
  }

  /**
   * Generate financial summary report
   */
  static async generateFinancialSummaryReport(parameters) {
    const { startDate, endDate, transactionType = 'all', currency = 'SAR' } = parameters;

    const matchQuery = {
      createdAt: {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      },
      currency
    };

    if (transactionType !== 'all') {
      matchQuery.type = transactionType === 'income' ? 'credit' : 'debit';
    }

    const financialData = await Transaction.aggregate([
      { $match: matchQuery },
      {
        $group: {
          _id: {
            year: { $year: '$createdAt' },
            month: { $month: '$createdAt' }
          },
          income: {
            $sum: { $cond: [{ $eq: ['$type', 'credit'] }, '$amount', 0] }
          },
          expenses: {
            $sum: { $cond: [{ $eq: ['$type', 'debit'] }, '$amount', 0] }
          },
          net: { $sum: '$amount' },
          count: { $sum: 1 }
        }
      },
      { $sort: { '_id': 1 } }
    ]);

    return {
      title: 'الملخص المالي',
      period: { startDate, endDate },
      transactionType,
      currency,
      summary: {
        totalIncome: financialData.reduce((sum, item) => sum + item.income, 0),
        totalExpenses: financialData.reduce((sum, item) => sum + item.expenses, 0),
        netProfit: financialData.reduce((sum, item) => sum + item.net, 0),
        totalTransactions: financialData.reduce((sum, item) => sum + item.count, 0)
      },
      data: financialData
    };
  }

  /**
   * Generate driver performance report
   */
  static async generateDriverPerformanceReport(parameters) {
    const { startDate, endDate, groupBy = 'driver', includeLocation = true } = parameters;

    const matchQuery = {
      createdAt: {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      }
    };

    const performanceData = await Shipment.aggregate([
      { $match: matchQuery },
      {
        $lookup: {
          from: 'drivers',
          localField: 'driverId',
          foreignField: '_id',
          as: 'driver'
        }
      },
      { $unwind: '$driver' },
      {
        $group: this.getGroupByExpression(groupBy),
        totalShipments: { $sum: 1 },
        deliveredShipments: {
          $sum: { $cond: [{ $eq: ['$status', 'delivered'] }, 1, 0] }
        },
        averageDeliveryTime: {
          $avg: {
            $cond: [
              { $and: [
                { $eq: ['$status', 'delivered'] },
                { $ne: ['$actualDelivery', null] },
                { $ne: ['$estimatedDelivery', null] }
              ]},
              { $subtract: ['$actualDelivery', '$estimatedDelivery'] },
              null
            ]
          }
        },
        totalDistance: { $sum: '$distance' },
        totalRevenue: { $sum: '$amount' }
      }
    },
      { $sort: { totalRevenue: -1 } }
    ]);

    return {
      title: 'أداء السائقين',
      period: { startDate, endDate },
      groupBy,
      includeLocation,
      summary: {
        totalDrivers: performanceData.length,
        averageShipmentsPerDriver: performanceData.length > 0 ? 
          performanceData.reduce((sum, item) => sum + item.totalShipments, 0) / performanceData.length : 0,
        totalRevenue: performanceData.reduce((sum, item) => sum + item.totalRevenue, 0)
      },
      data: performanceData
    };
  }

  /**
   * Get group by expression for aggregation
   */
  static getGroupByExpression(groupBy) {
    switch (groupBy) {
      case 'day':
        return {
          _id: {
            year: { $year: '$createdAt' },
            month: { $month: '$createdAt' },
            day: { $dayOfMonth: '$createdAt' }
          }
        };
      case 'week':
        return {
          _id: {
            year: { $year: '$createdAt' },
            week: { $week: '$createdAt' }
          }
        };
      case 'month':
        return {
          _id: {
            year: { $year: '$createdAt' },
            month: { $month: '$createdAt' }
          }
        };
      case 'quarter':
        return {
          _id: {
            year: { $year: '$createdAt' },
            quarter: { $ceil: { $divide: [{ $month: '$createdAt' }, 3] } }
          }
        };
      case 'client':
        return { _id: '$clientId' };
      case 'driver':
        return { _id: '$driverId' };
      case 'route':
        return { _id: '$routeId' };
      case 'team':
        return { _id: '$teamId' };
      default:
        return { _id: { $dateToString: { format: "%Y-%m-%d", date: "$createdAt" } } };
    }
  }

  /**
   * Generate Excel report
   */
  static async generateExcelReport(data, template) {
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet(template.name);

    // Add headers
    const headers = Object.keys(data.data[0] || {});
    worksheet.columns = headers.map(header => ({
      header: this.getHeaderLabel(header),
      key: header,
      width: 20
    }));

    // Add data
    worksheet.addRows(data.data);

    // Add summary section
    if (data.summary) {
      worksheet.addRow({});
      worksheet.addRow(['ملخص']);
      Object.entries(data.summary).forEach(([key, value]) => {
        worksheet.addRow([this.getHeaderLabel(key), value]);
      });
    }

    return await workbook.xlsx.writeBuffer();
  }

  /**
   * Generate PDF report
   */
  static async generatePDFReport(data, template) {
    return new Promise((resolve, reject) => {
      try {
        const doc = new PDFDocument();
        
        // Set up fonts
        doc.font('Helvetica-Bold');
        doc.font('Helvetica');

        // Title
        doc.fontSize(20).text(template.name, { align: 'center' });
        doc.moveDown();

        // Period
        if (data.period) {
          doc.fontSize(12).text(`الفترة: ${data.period.startDate} إلى ${data.period.endDate}`, { align: 'center' });
          doc.moveDown();
        }

        // Summary table
        if (data.summary) {
          doc.fontSize(14).text('الملخص', { underline: true });
          doc.moveDown();

          Object.entries(data.summary).forEach(([key, value]) => {
            doc.fontSize(10).text(`${this.getHeaderLabel(key)}: ${value}`);
            doc.moveDown();
          });
        }

        // Data table
        if (data.data && data.data.length > 0) {
          doc.moveDown();
          doc.fontSize(14).text('التفاصيل', { underline: true });
          doc.moveDown();

          const headers = Object.keys(data.data[0]);
          const tableTop = doc.y;

          // Headers
          headers.forEach((header, index) => {
            const x = 50 + (index * 100);
            doc.fontSize(10).text(this.getHeaderLabel(header), x, tableTop, { width: 90 });
          });

          // Data rows
          doc.y = tableTop + 20;
          data.data.forEach((row, rowIndex) => {
            headers.forEach((header, colIndex) => {
              const x = 50 + (colIndex * 100);
              doc.fontSize(9).text(String(row[header] || ''), x, doc.y, { width: 90 });
            });
            doc.y += 15;
          });
        }

        doc.end();
        resolve(Buffer.concat([]));
      } catch (error) {
        reject(error);
      }
    });
  }

  /**
   * Get header label in Arabic
   */
  static getHeaderLabel(key) {
    const labels = {
      revenue: 'الإيرادات',
      count: 'العدد',
      average: 'المتوسط',
      totalShipments: 'إجمالي الشحنات',
      deliveredShipments: 'الشحنات المسلمة',
      deliveryRate: 'معدل التسليم',
      totalRevenue: 'إجمالي الإيرادات',
      totalCustomers: 'إجمالي العملاء',
      averageOrderValue: 'متوسط قيمة الطلب',
      income: 'الدخل',
      expenses: 'المصروفات',
      netProfit: 'صافي الربح',
      totalTransactions: 'إجمالي المعاملات',
      totalDistance: 'إجمالي المسافة',
      averageDeliveryTime: 'متوسط وقت التسليم',
      name: 'الاسم',
      email: 'البريد الإلكتروني',
      lastShipmentDate: 'تاريخ آخر شحنة'
    };

    return labels[key] || key;
  }
}

module.exports = ReportingController;
