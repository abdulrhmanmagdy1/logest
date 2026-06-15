/**
 * ============================================
 * 📧 Email Service - نظام إدهام
 * Professional email sending service
 * ============================================
 */

const nodemailer = require('nodemailer');
const logger = require('../utils/logger');

class EmailService {
  constructor() {
    this.transporter = nodemailer.createTransport({
      host: process.env.SMTP_HOST || 'smtp.gmail.com',
      port: process.env.SMTP_PORT || 587,
      secure: false,
      auth: {
        user: process.env.SMTP_USER,
        pass: process.env.SMTP_PASS
      }
    });

    this.templates = {
      welcome: this.getWelcomeTemplate,
      passwordReset: this.getPasswordResetTemplate,
      shipmentCreated: this.getShipmentCreatedTemplate,
      shipmentDelivered: this.getShipmentDeliveredTemplate,
      invoiceGenerated: this.getInvoiceGeneratedTemplate,
      paymentReceived: this.getPaymentReceivedTemplate
    };
  }

  async sendEmail({ to, subject, html, attachments = [] }) {
    try {
      const info = await this.transporter.sendMail({
        from: `"${process.env.FROM_NAME || 'Edham Logistics'}" <${process.env.FROM_EMAIL}>`,
        to,
        subject,
        html,
        attachments
      });

      logger.info(`Email sent to ${to}: ${info.messageId}`);
      return { success: true, messageId: info.messageId };
    } catch (error) {
      logger.error('Email sending failed:', error);
      return { success: false, error: error.message };
    }
  }

  // Welcome email for new users
  async sendWelcomeEmail(user) {
    const subject = 'مرحباً بك في نظام إدهام للنقل المبرد';
    const html = `
      <div dir="rtl" style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;">
          <h1 style="color: white; margin: 0;">🚛 نظام إدهام</h1>
        </div>
        <div style="padding: 30px; background: #f9f9f9;">
          <h2 style="color: #333;">مرحباً ${user.firstName}!</h2>
          <p style="color: #666; line-height: 1.6;">
            تم إنشاء حسابك بنجاح في نظام إدهام للنقل المبرد. يمكنك الآن:
          </p>
          <ul style="color: #666; line-height: 1.8;">
            <li>طلب خدمات النقل المبرد</li>
            <li>متابعة شحناتك في الوقت الفعلي</li>
            <li>إدارة الفواتير والمدفوعات</li>
            <li>التواصل المباشر مع السائقين</li>
          </ul>
          <div style="text-align: center; margin: 30px 0;">
            <a href="${process.env.CLIENT_URL}/dashboard" 
               style="background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
              ابدأ الآن
            </a>
          </div>
        </div>
        <div style="padding: 20px; text-align: center; background: #333; color: white;">
          <p style="margin: 0; font-size: 12px;">© 2024 شركة إدهام للنقل المبرد</p>
        </div>
      </div>
    `;

    return await this.sendEmail({ to: user.email, subject, html });
  }

  // Password reset email
  async sendPasswordResetEmail(user, resetToken) {
    const resetUrl = `${process.env.CLIENT_URL}/reset-password?token=${resetToken}`;
    const subject = 'إعادة تعيين كلمة المرور';
    const html = `
      <div dir="rtl" style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <div style="background: #f9f9f9; padding: 30px;">
          <h2 style="color: #333;">إعادة تعيين كلمة المرور</h2>
          <p style="color: #666;">مرحباً ${user.firstName}،</p>
          <p style="color: #666;">
            لقد طلبت إعادة تعيين كلمة المرور. انقر على الزر أدناه لإنشاء كلمة مرور جديدة:
          </p>
          <div style="text-align: center; margin: 30px 0;">
            <a href="${resetUrl}" 
               style="background: #e74c3c; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
              إعادة تعيين كلمة المرور
            </a>
          </div>
          <p style="color: #999; font-size: 12px;">
            هذا الرابط صالح لمدة 10 دقائق فقط. إذا لم تطلب إعادة تعيين كلمة المرور، يرجى تجاهل هذا البريد.
          </p>
        </div>
      </div>
    `;

    return await this.sendEmail({ to: user.email, subject, html });
  }

  // Shipment created notification
  async sendShipmentCreatedEmail(user, shipment) {
    const subject = `تم إنشاء طلب النقل - ${shipment.trackingNumber}`;
    const html = `
      <div dir="rtl" style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;">
          <h1 style="color: white; margin: 0;">📦 طلب نقل جديد</h1>
        </div>
        <div style="padding: 30px; background: #f9f9f9;">
          <p style="color: #666;">مرحباً ${user.firstName}،</p>
          <p style="color: #666;">تم إنشاء طلب النقل الخاص بك بنجاح!</p>
          
          <div style="background: white; padding: 20px; border-radius: 8px; margin: 20px 0;">
            <h3 style="color: #667eea; margin-top: 0;">تفاصيل الطلب</h3>
            <table style="width: 100%; color: #666;">
              <tr><td style="padding: 8px 0;"><strong>رقم التتبع:</strong></td><td>${shipment.trackingNumber}</td></tr>
              <tr><td style="padding: 8px 0;"><strong>نوع الشحنة:</strong></td><td>${shipment.cargo.type}</td></tr>
              <tr><td style="padding: 8px 0;"><strong>من:</strong></td><td>${shipment.pickup.address.city}</td></tr>
              <tr><td style="padding: 8px 0;"><strong>إلى:</strong></td><td>${shipment.delivery.address.city}</td></tr>
              <tr><td style="padding: 8px 0;"><strong>الحالة:</strong></td><td><span style="background: #f1c40f; color: white; padding: 4px 12px; border-radius: 12px; font-size: 12px;">قيد الانتظار</span></td></tr>
            </table>
          </div>
          
          <div style="text-align: center;">
            <a href="${process.env.CLIENT_URL}/tracking/${shipment.trackingNumber}" 
               style="background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
              تتبع الشحنة
            </a>
          </div>
        </div>
      </div>
    `;

    return await this.sendEmail({ to: user.email, subject, html });
  }

  // Shipment delivered notification
  async sendShipmentDeliveredEmail(user, shipment) {
    const subject = `✅ تم تسليم الشحنة - ${shipment.trackingNumber}`;
    const html = `
      <div dir="rtl" style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <div style="background: #27ae60; padding: 30px; text-align: center;">
          <h1 style="color: white; margin: 0;">✅ تم التسليم بنجاح</h1>
        </div>
        <div style="padding: 30px; background: #f9f9f9;">
          <p style="color: #666;">مرحباً ${user.firstName}،</p>
          <p style="color: #666;">تم تسليم شحنتك بنجاح!</p>
          
          <div style="background: white; padding: 20px; border-radius: 8px; margin: 20px 0;">
            <p><strong>رقم التتبع:</strong> ${shipment.trackingNumber}</p>
            <p><strong>تاريخ التسليم:</strong> ${new Date().toLocaleDateString('ar-SA')}</p>
          </div>
          
          <div style="text-align: center; margin: 30px 0;">
            <a href="${process.env.CLIENT_URL}/survey/${shipment._id}" 
               style="background: #f39c12; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
              قيّم الخدمة
            </a>
          </div>
        </div>
      </div>
    `;

    return await this.sendEmail({ to: user.email, subject, html });
  }

  // Invoice generated notification
  async sendInvoiceEmail(user, invoice) {
    const subject = `🧾 فاتورة جديدة - ${invoice.invoiceNumber}`;
    const html = `
      <div dir="rtl" style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <div style="background: #2c3e50; padding: 30px; text-align: center;">
          <h1 style="color: white; margin: 0;">🧾 فاتورة جديدة</h1>
        </div>
        <div style="padding: 30px; background: #f9f9f9;">
          <p style="color: #666;">مرحباً ${user.firstName}،</p>
          <p style="color: #666;">تم إنشاء فاتورة جديدة لحسابك.</p>
          
          <div style="background: white; padding: 20px; border-radius: 8px; margin: 20px 0;">
            <h3 style="color: #2c3e50; margin-top: 0;">${invoice.invoiceNumber}</h3>
            <p style="font-size: 24px; color: #27ae60; font-weight: bold; margin: 10px 0;">
              ${invoice.total.toLocaleString()} ر.س
            </p>
            <p style="color: #666;">تاريخ الاستحقاق: ${new Date(invoice.dueDate).toLocaleDateString('ar-SA')}</p>
          </div>
          
          <div style="text-align: center;">
            <a href="${process.env.CLIENT_URL}/invoices/${invoice._id}" 
               style="background: #27ae60; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
              عرض الفاتورة
            </a>
          </div>
        </div>
      </div>
    `;

    return await this.sendEmail({ to: user.email, subject, html });
  }
}

module.exports = new EmailService();
