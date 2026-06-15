/**
 * ============================================
 * ✅ Validation Middleware - نظام إدهام
 * Edham Logistics - Enhanced Input Validation
 * ============================================
 */

const { body, validationResult, query, param } = require('express-validator');
const { MESSAGES, HTTP_STATUS } = require('../config/constants');

const validate = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({
      success: false,
      message: 'خطأ في التحقق من البيانات',
      errors: errors.array().map(err => ({
        field: err.param,
        message: err.msg,
        value: err.value
      }))
    });
  }
  next();
};

// User validations
const validateUser = [
  body('email')
    .isEmail()
    .normalizeEmail()
    .withMessage('البريد الإلكتروني غير صحيح'),
  body('password')
    .isLength({ min: 6 })
    .withMessage('كلمة المرور يجب أن تكون 6 أحرف على الأقل'),
  body('name')
    .trim()
    .isLength({ min: 3 })
    .withMessage('الاسم يجب أن يكون 3 أحرف على الأقل'),
  body('phone')
    .optional()
    .matches(/^(\+966|0)(5|9)[0-9]{8}$/)
    .withMessage('رقم الهاتف غير صحيح'),
  validate
];

// Login validation
const validateLogin = [
  body('email')
    .trim()
    .notEmpty().withMessage('البريد الإلكتروني مطلوب')
    .isEmail().withMessage('البريد الإلكتروني غير صحيح'),
  body('password')
    .notEmpty().withMessage('كلمة المرور مطلوبة'),
  validate
];

// Shipment validations
const validateShipment = [
  body('description')
    .trim()
    .isLength({ min: 5 })
    .withMessage('وصف الشحنة يجب أن يكون 5 أحرف على الأقل'),
  body('weight')
    .isFloat({ min: 1 })
    .withMessage('الوزن يجب أن يكون أكبر من صفر'),
  body('pickupLocation.city')
    .notEmpty()
    .withMessage('مدينة الاستلام مطلوبة'),
  body('deliveryLocation.city')
    .notEmpty()
    .withMessage('مدينة التسليم مطلوبة'),
  validate
];

// Invoice validations
const validateInvoice = [
  body('subtotal')
    .isFloat({ min: 1 })
    .withMessage('المبلغ يجب أن يكون أكبر من صفر'),
  body('items')
    .isArray({ min: 1 })
    .withMessage('الفاتورة يجب أن تحتوي على بند واحد على الأقل'),
  validate
];

// Payment validation
const validatePayment = [
  body('amount')
    .isFloat({ min: 1 })
    .withMessage('المبلغ يجب أن يكون أكبر من صفر'),
  body('method')
    .isIn(['cash', 'bank_transfer', 'credit_card', 'stripe', 'check'])
    .withMessage('طريقة الدفع غير صحيحة'),
  validate
];

// Truck validations
const validateTruck = [
  body('plateNumber')
    .trim()
    .notEmpty()
    .withMessage('رقم اللوحة مطلوب'),
  body('model')
    .trim()
    .notEmpty()
    .withMessage('الموديل مطلوب'),
  body('capacity')
    .isFloat({ min: 1 })
    .withMessage('السعة يجب أن تكون أكبر من صفر'),
  body('year')
    .isInt({ min: 1990, max: new Date().getFullYear() + 1 })
    .withMessage('سنة الصنع غير صحيحة'),
  validate
];

// Order validations
const validateOrder = [
  body('pickup_address')
    .trim()
    .notEmpty()
    .withMessage('عنوان الاستلام مطلوب'),
  body('pickup_location.lat')
    .isFloat({ min: -90, max: 90 })
    .withMessage('خط عرض الاستلام غير صحيح'),
  body('pickup_location.lng')
    .isFloat({ min: -180, max: 180 })
    .withMessage('خط طول الاستلام غير صحيح'),
  body('dropoff_address')
    .trim()
    .notEmpty()
    .withMessage('عنوان التسليم مطلوب'),
  body('dropoff_location.lat')
    .isFloat({ min: -90, max: 90 })
    .withMessage('خط عرض التسليم غير صحيح'),
  body('dropoff_location.lng')
    .isFloat({ min: -180, max: 180 })
    .withMessage('خط طول التسليم غير صحيح'),
  body('pickup_date')
    .isISO8601()
    .withMessage('تاريخ الاستلام غير صحيح'),
  body('pickup_time')
    .matches(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/)
    .withMessage('وقت الاستلام غير صحيح (HH:MM)'),
  body('vehicle_type')
    .isIn(['CARGO_TRUCK', 'PICKUP_VAN', 'LIGHT_TRUCK', 'HEAVY_TRUCK', 'MOTORCYCLE'])
    .withMessage('نوع المركبة غير صحيح'),
  body('cargo_details.weight_kg')
    .isFloat({ min: 0.1 })
    .withMessage('وزن البضاعة يجب أن يكون أكبر من 0.1 كجم'),
  body('cargo_details.package_count')
    .isInt({ min: 1 })
    .withMessage('عدد الطرود يجب أن يكون على الأقل 1'),
  body('payment_method')
    .isIn(['CREDIT_CARD', 'BANK_TRANSFER', 'CASH', 'WALLET'])
    .withMessage('طريقة الدفع غير صحيحة'),
  validate
];

// Price calculation validation
const validatePriceCalculation = [
  body('pickup_location.lat')
    .isFloat({ min: -90, max: 90 })
    .withMessage('خط عرض الاستلام غير صحيح'),
  body('pickup_location.lng')
    .isFloat({ min: -180, max: 180 })
    .withMessage('خط طول الاستلام غير صحيح'),
  body('dropoff_location.lat')
    .isFloat({ min: -90, max: 90 })
    .withMessage('خط عرض التسليم غير صحيح'),
  body('dropoff_location.lng')
    .isFloat({ min: -180, max: 180 })
    .withMessage('خط طول التسليم غير صحيح'),
  body('vehicle_type')
    .isIn(['CARGO_TRUCK', 'PICKUP_VAN', 'LIGHT_TRUCK', 'HEAVY_TRUCK', 'MOTORCYCLE'])
    .withMessage('نوع المركبة غير صحيح'),
  body('helpers_count')
    .optional()
    .isInt({ min: 0, max: 10 })
    .withMessage('عدد العمال يجب أن يكون بين 0 و 10'),
  body('insurance_enabled')
    .optional()
    .isBoolean()
    .withMessage('قيمة التأمين يجب أن تكون true أو false'),
  validate
];

const validateOrderAssignment = [
  body('orderId')
    .trim()
    .notEmpty()
    .withMessage('معرف الطلب مطلوب'),
  body('driverId')
    .trim()
    .notEmpty()
    .withMessage('معرف السائق مطلوب'),
  body('assignment_notes')
    .optional()
    .trim()
    .isLength({ max: 500 })
    .withMessage('ملاحظات التعيين يجب أن تكون أقل من 500 حرف'),
  validate
];

const validateMaintenance = [
  body('truckId')
    .trim()
    .notEmpty()
    .withMessage('معرف الشاحنة مطلوب'),
  body('maintenance_type')
    .trim()
    .notEmpty()
    .withMessage('نوع الصيانة مطلوب'),
  body('scheduled_date')
    .isISO8601()
    .withMessage('تاريخ الصيانة غير صحيح'),
  body('description')
    .optional()
    .trim()
    .isLength({ max: 1000 })
    .withMessage('الوصف يجب أن يكون أقل من 1000 حرف'),
  body('estimated_cost')
    .optional()
    .isFloat({ min: 0 })
    .withMessage('تكلفة الصيانة يجب أن تكون رقمًا موجبًا'),
  validate
];

module.exports = {
  validate,
  validateUser,
  validateLogin,
  validateShipment,
  validateInvoice,
  validatePayment,
  validateTruck,
  validateOrder,
  validateOrderAssignment,
  validatePriceCalculation,
  validateMaintenance
};
