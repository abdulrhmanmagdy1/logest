/**
 * ============================================
 * 📊 Formatters - نظام إدهام
 * Data formatting utilities for client app
 * ============================================
 */

/**
 * Format currency (SAR)
 * @param {number} amount - Amount to format
 * @param {number} decimals - Decimal places
 * @returns {string} Formatted currency string
 */
export const formatCurrency = (amount, decimals = 2) => {
  if (amount === null || amount === undefined) return '-';
  
  return new Intl.NumberFormat('ar-SA', {
    style: 'currency',
    currency: 'SAR',
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  }).format(amount);
};

/**
 * Format number with Arabic numerals
 * @param {number} num - Number to format
 * @returns {string} Formatted number
 */
export const formatNumber = (num) => {
  if (num === null || num === undefined) return '-';
  
  return new Intl.NumberFormat('ar-SA').format(num);
};

/**
 * Format date to Arabic
 * @param {string|Date} date - Date to format
 * @param {Object} options - Formatting options
 * @returns {string} Formatted date
 */
export const formatDate = (date, options = {}) => {
  if (!date) return '-';
  
  const defaultOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    ...options,
  };
  
  return new Intl.DateTimeFormat('ar-SA', defaultOptions).format(new Date(date));
};

/**
 * Format date and time
 * @param {string|Date} date - Date to format
 * @returns {string} Formatted date and time
 */
export const formatDateTime = (date) => {
  if (!date) return '-';
  
  return new Intl.DateTimeFormat('ar-SA', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(date));
};

/**
 * Format relative time (e.g., "منذ ساعتين")
 * @param {string|Date} date - Date to format
 * @returns {string} Relative time string
 */
export const formatRelativeTime = (date) => {
  if (!date) return '-';
  
  const now = new Date();
  const then = new Date(date);
  const diffInSeconds = Math.floor((now - then) / 1000);
  
  if (diffInSeconds < 60) return 'الآن';
  if (diffInSeconds < 3600) return `منذ ${Math.floor(diffInSeconds / 60)} دقيقة`;
  if (diffInSeconds < 86400) return `منذ ${Math.floor(diffInSeconds / 3600)} ساعة`;
  if (diffInSeconds < 604800) return `منذ ${Math.floor(diffInSeconds / 86400)} يوم`;
  if (diffInSeconds < 2592000) return `منذ ${Math.floor(diffInSeconds / 604800)} أسبوع`;
  if (diffInSeconds < 31536000) return `منذ ${Math.floor(diffInSeconds / 2592000)} شهر`;
  
  return `منذ ${Math.floor(diffInSeconds / 31536000)} سنة`;
};

/**
 * Format phone number (Saudi format)
 * @param {string} phone - Phone number
 * @returns {string} Formatted phone number
 */
export const formatPhone = (phone) => {
  if (!phone) return '-';
  
  const cleaned = phone.replace(/\D/g, '');
  
  if (cleaned.length === 10 && cleaned.startsWith('05')) {
    return cleaned.replace(/(\d{4})(\d{3})(\d{3})/, '$1 $2 $3');
  }
  
  return phone;
};

/**
 * Format distance
 * @param {number} meters - Distance in meters
 * @returns {string} Formatted distance
 */
export const formatDistance = (meters) => {
  if (meters === null || meters === undefined) return '-';
  
  if (meters < 1000) {
    return `${Math.round(meters)} م`;
  }
  
  return `${(meters / 1000).toFixed(1)} كم`;
};

/**
 * Format duration
 * @param {number} minutes - Duration in minutes
 * @returns {string} Formatted duration
 */
export const formatDuration = (minutes) => {
  if (minutes === null || minutes === undefined) return '-';
  
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  
  if (hours === 0) return `${mins} دقيقة`;
  if (mins === 0) return `${hours} ساعة`;
  
  return `${hours} ساعة ${mins} دقيقة`;
};

/**
 * Format percentage
 * @param {number} value - Percentage value (0-100)
 * @param {number} decimals - Decimal places
 * @returns {string} Formatted percentage
 */
export const formatPercentage = (value, decimals = 1) => {
  if (value === null || value === undefined) return '-';
  
  return `${value.toFixed(decimals)}%`;
};

/**
 * Format weight
 * @param {number} kg - Weight in kilograms
 * @returns {string} Formatted weight
 */
export const formatWeight = (kg) => {
  if (kg === null || kg === undefined) return '-';
  
  if (kg >= 1000) {
    return `${(kg / 1000).toFixed(2)} طن`;
  }
  
  return `${kg.toFixed(1)} كجم`;
};

/**
 * Format file size
 * @param {number} bytes - Size in bytes
 * @returns {string} Formatted file size
 */
export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 بايت';
  
  const k = 1024;
  const sizes = ['بايت', 'كيلوبايت', 'ميغابايت', 'جيغابايت'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`;
};

/**
 * Truncate text with ellipsis
 * @param {string} text - Text to truncate
 * @param {number} maxLength - Maximum length
 * @returns {string} Truncated text
 */
export const truncateText = (text, maxLength = 50) => {
  if (!text || text.length <= maxLength) return text;
  
  return `${text.substring(0, maxLength)}...`;
};

/**
 * Format status to Arabic
 * @param {string} status - Status code
 * @returns {string} Arabic status
 */
export const formatStatus = (status) => {
  const statuses = {
    pending: 'معلق',
    processing: 'قيد المعالجة',
    shipped: 'تم الشحن',
    in_transit: 'في الطريق',
    delivered: 'تم التسليم',
    cancelled: 'ملغي',
    completed: 'مكتمل',
    active: 'نشط',
    inactive: 'غير نشط',
  };
  
  return statuses[status] || status;
};

/**
 * Format tracking number
 * @param {string} trackingNumber - Tracking number
 * @returns {string} Formatted tracking number
 */
export const formatTrackingNumber = (trackingNumber) => {
  if (!trackingNumber) return '-';
  
  return trackingNumber.toUpperCase();
};

/**
 * Get status color
 * @param {string} status - Status code
 * @returns {string} Color class
 */
export const getStatusColor = (status) => {
  const colors = {
    pending: 'text-yellow-500',
    processing: 'text-blue-500',
    shipped: 'text-purple-500',
    in_transit: 'text-indigo-500',
    delivered: 'text-green-500',
    completed: 'text-green-500',
    cancelled: 'text-red-500',
    active: 'text-green-500',
    inactive: 'text-gray-500',
  };
  
  return colors[status] || 'text-gray-500';
};

/**
 * Get status background color
 * @param {string} status - Status code
 * @returns {string} Background color class
 */
export const getStatusBgColor = (status) => {
  const colors = {
    pending: 'bg-yellow-100 text-yellow-800',
    processing: 'bg-blue-100 text-blue-800',
    shipped: 'bg-purple-100 text-purple-800',
    in_transit: 'bg-indigo-100 text-indigo-800',
    delivered: 'bg-green-100 text-green-800',
    completed: 'bg-green-100 text-green-800',
    cancelled: 'bg-red-100 text-red-800',
    active: 'bg-green-100 text-green-800',
    inactive: 'bg-gray-100 text-gray-800',
  };
  
  return colors[status] || 'bg-gray-100 text-gray-800';
};
