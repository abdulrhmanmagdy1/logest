//
/**
 * ============================================
 * 📄 Export Service - خدمة التصدير والتقارير
 * PDF & Excel Generation
 * ============================================
 */

const PDFDocument = require('pdfkit');
const ExcelJS = require('exceljs');
const fs = require('fs');
const path = require('path');
const logger = require('../utils/logger');

class ExportService {
  constructor() {
    this.templatesDir = path.join(__dirname, '../templates');
    this.exportsDir = path.join(__dirname, '../exports');
    
    // Ensure exports directory exists
    if (!fs.existsSync(this.exportsDir)) {
      fs.mkdirSync(this.exportsDir, { recursive: true });
    }
  }

  /**
   * Generate shipment PDF report
   */
  async generateShipmentPDF(shipmentId, language = 'ar') {
    try {
      const Shipment = require('../models/Shipment');
      const shipment = await Shipment.findById(shipmentId)
        .populate('driver', 'firstName lastName phone')
        .populate('createdBy', 'company.name email');

      if (!shipment) {
        throw new Error('Shipment not found');
      }

      const doc = new PDFDocument({ margin: 50 });
      const fileName = `shipment_${shipment.trackingNumber}_${Date.now()}.pdf`;
      const filePath = path.join(this.exportsDir, fileName);
      const stream = fs.createWriteStream(filePath);

      doc.pipe(stream);

      // Header
      this.addHeader(doc, language);

      // Title
      doc.fontSize(20)
         .fillColor('#1a1a2e')
         .text(language === 'ar' ? 'تفاصيل الشحنة' : 'Shipment Details', 50, 100);

      // Shipment Info
      doc.fontSize(12)
         .fillColor('#333');

      const info = [
        [language === 'ar' ? 'رقم التتبع:' : 'Tracking Number:', shipment.trackingNumber],
        [language === 'ar' ? 'الحالة:' : 'Status:', shipment.status],
        [language === 'ar' ? 'تاريخ الإنشاء:' : 'Created Date:', shipment.createdAt.toLocaleDateString()],
        [language === 'ar' ? 'نوع الشحنة:' : 'Cargo Type:', shipment.cargo?.type || 'N/A'],
        [language === 'ar' ? 'الوزن:' : 'Weight:', `${shipment.cargo?.weight?.value || 0} ${shipment.cargo?.weight?.unit || 'kg'}`]
      ];

      let y = 140;
      info.forEach(([label, value]) => {
        doc.text(`${label} ${value}`, 50, y);
        y += 25;
      });

      // Route Info
      doc.fontSize(16)
         .fillColor('#1a1a2e')
         .text(language === 'ar' ? 'معلومات المسار' : 'Route Information', 50, y + 20);

      y += 50;

      doc.fontSize(12)
         .text(`${language === 'ar' ? 'من:' : 'From:'} ${shipment.pickup?.address?.city || 'N/A'}`, 50, y);
      y += 25;
      doc.text(`${language === 'ar' ? 'إلى:' : 'To:'} ${shipment.delivery?.address?.city || 'N/A'}`, 50, y);

      // QR Code
      const QRCode = require('qrcode');
      const qrData = await QRCode.toDataURL(shipment.trackingNumber);
      const qrBuffer = Buffer.from(qrData.split(',')[1], 'base64');
      doc.image(qrBuffer, 400, 140, { width: 100 });

      // Footer
      this.addFooter(doc, language);

      doc.end();

      return new Promise((resolve, reject) => {
        stream.on('finish', () => {
          resolve({
            success: true,
            filePath,
            fileName,
            downloadUrl: `/exports/${fileName}`
          });
        });
        stream.on('error', reject);
      });

    } catch (error) {
      logger.error('PDF generation error:', error);
      throw error;
    }
  }

  /**
   * Generate Excel report
   */
  async generateExcelReport(reportType, data, options = {}) {
    try {
      const workbook = new ExcelJS.Workbook();
      workbook.creator = 'Edham Logistics';
      workbook.created = new Date();

      const worksheet = workbook.addWorksheet(reportType);

      // Define columns based on report type
      const columns = this.getColumnsForReportType(reportType, options.language);
      worksheet.columns = columns;

      // Style header
      worksheet.getRow(1).font = { bold: true, size: 12 };
      worksheet.getRow(1).fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: 'FF1A1A2E' }
      };
      worksheet.getRow(1).font = { color: { argb: 'FFFFFFFF' }, bold: true };

      // Add data
      data.forEach((item, index) => {
        const rowData = this.formatRowData(item, reportType);
        worksheet.addRow(rowData);
      });

      // Auto-fit columns
      worksheet.columns.forEach(column => {
        let maxLength = 0;
        column.eachCell({ includeEmpty: true }, cell => {
          const cellLength = cell.value ? cell.value.toString().length : 10;
          if (cellLength > maxLength) {
            maxLength = cellLength;
          }
        });
        column.width = Math.min(maxLength + 2, 50);
      });

      // Add conditional formatting
      if (reportType === 'shipments') {
        worksheet.addConditionalFormatting({
          ref: `E2:E${data.length + 1}`,
          rules: [
            {
              type: 'containsText',
              operator: 'containsText',
              text: 'delivered',
              style: { fill: { type: 'pattern', pattern: 'solid', bgColor: { argb: 'FF00FF00' } } }
            },
            {
              type: 'containsText',
              operator: 'containsText',
              text: 'delayed',
              style: { fill: { type: 'pattern', pattern: 'solid', bgColor: { argb: 'FFFF0000' } } }
            }
          ]
        });
      }

      const fileName = `${reportType}_report_${Date.now()}.xlsx`;
      const filePath = path.join(this.exportsDir, fileName);

      await workbook.xlsx.writeFile(filePath);

      return {
        success: true,
        filePath,
        fileName,
        downloadUrl: `/exports/${fileName}`,
        recordCount: data.length
      };

    } catch (error) {
      logger.error('Excel generation error:', error);
      throw error;
    }
  }

  /**
   * Generate comprehensive analytics report
   */
  async generateAnalyticsReport(startDate, endDate, options = {}) {
    try {
      const Analytics = require('../models/Analytics');
      const data = await Analytics.getReportData(startDate, endDate);

      const workbook = new ExcelJS.Workbook();

      // Summary Sheet
      const summarySheet = workbook.addWorksheet('Summary');
      summarySheet.columns = [
        { header: 'Metric', key: 'metric', width: 30 },
        { header: 'Value', key: 'value', width: 20 },
        { header: 'Change', key: 'change', width: 15 }
      ];

      summarySheet.addRow({ metric: 'Total Shipments', value: data.totalShipments, change: data.shipmentsChange });
      summarySheet.addRow({ metric: 'Revenue', value: data.revenue, change: data.revenueChange });
      summarySheet.addRow({ metric: 'On-Time Delivery', value: `${data.onTimeRate}%`, change: data.onTimeChange });

      // Shipments Sheet
      const shipmentsSheet = workbook.addWorksheet('Shipments');
      shipmentsSheet.columns = [
        { header: 'Date', key: 'date', width: 15 },
        { header: 'Count', key: 'count', width: 12 },
        { header: 'Revenue', key: 'revenue', width: 15 },
        { header: 'Avg Delivery Time', key: 'avgTime', width: 20 }
      ];

      data.dailyStats.forEach(day => {
        shipmentsSheet.addRow(day);
      });

      // Add charts
      this.addChartsToWorkbook(workbook, data);

      const fileName = `analytics_report_${startDate}_${endDate}.xlsx`;
      const filePath = path.join(this.exportsDir, fileName);

      await workbook.xlsx.writeFile(filePath);

      return {
        success: true,
        filePath,
        fileName,
        downloadUrl: `/exports/${fileName}`
      };

    } catch (error) {
      logger.error('Analytics report error:', error);
      throw error;
    }
  }

  /**
   * Generate invoice PDF
   */
  async generateInvoicePDF(invoiceId) {
    try {
      const Invoice = require('../models/Invoice');
      const invoice = await Invoice.findById(invoiceId)
        .populate('client', 'company.name email')
        .populate('shipment', 'trackingNumber');

      if (!invoice) {
        throw new Error('Invoice not found');
      }

      const doc = new PDFDocument({ margin: 50 });
      const fileName = `invoice_${invoice.invoiceNumber}.pdf`;
      const filePath = path.join(this.exportsDir, fileName);
      const stream = fs.createWriteStream(filePath);

      doc.pipe(stream);

      // Company Header
      doc.fontSize(24)
         .fillColor('#1a1a2e')
         .text('إدهام لوجستيكس', 50, 50);

      doc.fontSize(14)
         .fillColor('#666')
         .text('نظام النقل المبرد الذكي', 50, 80);

      // Invoice Title
      doc.fontSize(20)
         .fillColor('#1a1a2e')
         .text('فاتورة ضريبية مبسطة', 400, 50, { align: 'right' });

      // Invoice Details
      doc.fontSize(12)
         .fillColor('#333');

      const details = [
        ['رقم الفاتورة:', invoice.invoiceNumber],
        ['التاريخ:', invoice.createdAt.toLocaleDateString('ar-SA')],
        ['حالة الدفع:', invoice.status === 'paid' ? 'مدفوعة' : 'غير مدفوعة']
      ];

      let y = 120;
      details.forEach(([label, value]) => {
        doc.text(`${label} ${value}`, 400, y, { align: 'right' });
        y += 25;
      });

      // Client Info
      doc.fontSize(14)
         .fillColor('#1a1a2e')
         .text('معلومات العميل', 50, 220);

      doc.fontSize(12)
         .fillColor('#333');

      doc.text(`الشركة: ${invoice.client?.company?.name || 'N/A'}`, 50, 250);
      doc.text(`البريد الإلكتروني: ${invoice.client?.email || 'N/A'}`, 50, 275);

      // Items Table
      doc.fontSize(14)
         .fillColor('#1a1a2e')
         .text('تفاصيل الفاتورة', 50, 320);

      // Table header
      const tableTop = 350;
      doc.rect(50, tableTop, 500, 30).fill('#1a1a2e').stroke();
      doc.fillColor('#fff')
         .text('البيان', 60, tableTop + 10)
         .text('الكمية', 300, tableTop + 10)
         .text('السعر', 400, tableTop + 10)
         .text('الإجمالي', 480, tableTop + 10);

      // Table rows
      let rowY = tableTop + 30;
      invoice.items.forEach((item, index) => {
        const bgColor = index % 2 === 0 ? '#f5f5f5' : '#ffffff';
        doc.rect(50, rowY, 500, 30).fill(bgColor).stroke('#ddd');
        
        doc.fillColor('#333')
           .text(item.description, 60, rowY + 10, { width: 220 })
           .text(item.quantity.toString(), 300, rowY + 10)
           .text(item.unitPrice.toFixed(2), 400, rowY + 10)
           .text(item.total.toFixed(2), 480, rowY + 10);
        
        rowY += 30;
      });

      // Totals
      const totalsY = rowY + 20;
      doc.text(`المجموع الفرعي: ${invoice.subtotal.toFixed(2)} ر.س`, 400, totalsY, { align: 'right' });
      doc.text(`الضريبة (15%): ${invoice.tax.toFixed(2)} ر.س`, 400, totalsY + 25, { align: 'right' });
      
      doc.fontSize(16)
         .fillColor('#1a1a2e')
         .text(`الإجمالي: ${invoice.total.toFixed(2)} ر.س`, 400, totalsY + 55, { align: 'right' });

      // Footer
      doc.fontSize(10)
         .fillColor('#666')
         .text('شكراً لثقتكم بإدهام لوجستيكس', 300, 750, { align: 'center' });

      doc.end();

      return new Promise((resolve, reject) => {
        stream.on('finish', () => {
          resolve({
            success: true,
            filePath,
            fileName,
            downloadUrl: `/exports/${fileName}`
          });
        });
        stream.on('error', reject);
      });

    } catch (error) {
      logger.error('Invoice PDF error:', error);
      throw error;
    }
  }

  // Helper methods
  addHeader(doc, language) {
    doc.fontSize(24)
       .fillColor('#1a1a2e')
       .text(language === 'ar' ? 'إدهام لوجستيكس' : 'Edham Logistics', 50, 50);

    doc.fontSize(14)
       .fillColor('#666')
       .text(language === 'ar' ? 'نظام النقل المبرد الذكي' : 'Smart Cold Chain Logistics', 50, 80);

    doc.moveTo(50, 95)
       .lineTo(550, 95)
       .stroke('#1a1a2e');
  }

  addFooter(doc, language) {
    const footerText = language === 'ar' 
      ? 'تم إنشاء هذا التقرير بواسطة نظام إدهام لوجستيكس'
      : 'Generated by Edham Logistics System';

    doc.fontSize(10)
       .fillColor('#666')
       .text(footerText, 300, 750, { align: 'center' });
  }

  getColumnsForReportType(reportType, language) {
    const isArabic = language === 'ar';

    switch (reportType) {
      case 'shipments':
        return [
          { header: isArabic ? 'رقم التتبع' : 'Tracking #', key: 'trackingNumber', width: 20 },
          { header: isArabic ? 'الحالة' : 'Status', key: 'status', width: 15 },
          { header: isArabic ? 'المن' : 'From', key: 'origin', width: 20 },
          { header: isArabic ? 'الى' : 'To', key: 'destination', width: 20 },
          { header: isArabic ? 'التاريخ' : 'Date', key: 'createdAt', width: 15 },
          { header: isArabic ? 'السائق' : 'Driver', key: 'driver', width: 20 }
        ];

      case 'invoices':
        return [
          { header: isArabic ? 'رقم الفاتورة' : 'Invoice #', key: 'invoiceNumber', width: 20 },
          { header: isArabic ? 'العميل' : 'Client', key: 'client', width: 25 },
          { header: isArabic ? 'المبلغ' : 'Amount', key: 'total', width: 15 },
          { header: isArabic ? 'الحالة' : 'Status', key: 'status', width: 15 },
          { header: isArabic ? 'تاريخ الاستحقاق' : 'Due Date', key: 'dueDate', width: 15 }
        ];

      case 'drivers':
        return [
          { header: isArabic ? 'الاسم' : 'Name', key: 'name', width: 25 },
          { header: isArabic ? 'رقم الهاتف' : 'Phone', key: 'phone', width: 15 },
          { header: isArabic ? 'الحالة' : 'Status', key: 'status', width: 15 },
          { header: isArabic ? 'الشحنات' : 'Shipments', key: 'shipmentCount', width: 12 },
          { header: isArabic ? 'التقييم' : 'Rating', key: 'rating', width: 12 }
        ];

      default:
        return [];
    }
  }

  formatRowData(item, reportType) {
    switch (reportType) {
      case 'shipments':
        return {
          trackingNumber: item.trackingNumber,
          status: item.status,
          origin: item.pickup?.address?.city || 'N/A',
          destination: item.delivery?.address?.city || 'N/A',
          createdAt: item.createdAt?.toLocaleDateString(),
          driver: item.driver ? `${item.driver.firstName} ${item.driver.lastName}` : 'Not Assigned'
        };

      case 'invoices':
        return {
          invoiceNumber: item.invoiceNumber,
          client: item.client?.company?.name || item.client?.email || 'N/A',
          total: item.total,
          status: item.status,
          dueDate: item.dueDate?.toLocaleDateString()
        };

      case 'drivers':
        return {
          name: `${item.firstName} ${item.lastName}`,
          phone: item.phone,
          status: item.status,
          shipmentCount: item.shipmentCount || 0,
          rating: item.rating || 0
        };

      default:
        return item;
    }
  }

  addChartsToWorkbook(workbook, data) {
    // Charts would be added here using ExcelJS chart capabilities
    // This is a placeholder for chart implementation
  }
}

module.exports = new ExportService();
