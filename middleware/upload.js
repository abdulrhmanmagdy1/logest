/**
 * ============================================
 * 📤 Upload Middleware - نظام إدهام
 * Edham Logistics - File Upload Handler
 * ============================================
 */

const multer = require('multer');
const path = require('path');
const fs = require('fs');
const logger = require('../utils/logger');

const createDir = (dir) => {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
    logger.success(`Created upload directory: ${dir}`);
  }
};

createDir('uploads/shipments');
createDir('uploads/profiles');
createDir('uploads/general');

const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    let folder = 'uploads/general';
    
    if (req.originalUrl.includes('/shipments')) folder = 'uploads/shipments';
    if (req.originalUrl.includes('/profile')) folder = 'uploads/profiles';
    
    cb(null, folder);
  },
  filename: (req, file, cb) => {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1e9);
    const ext = path.extname(file.originalname);
    const filename = file.fieldname + '-' + uniqueSuffix + ext;
    logger.info(`Uploading file: ${filename}`);
    cb(null, filename);
  }
});

const fileFilter = (req, file, cb) => {
  const allowedTypes = /jpeg|jpg|png|gif|pdf|doc|docx/;
  const extname = allowedTypes.test(path.extname(file.originalname).toLowerCase());
  const mimetype = allowedTypes.test(file.mimetype);

  if (extname && mimetype) {
    return cb(null, true);
  } else {
    logger.warning('Unsupported file type rejected', {
      filename: file.originalname,
      mimetype: file.mimetype
    });
    cb(new Error('نوع الملف غير مدعوم'));
  }
};

const upload = multer({
  storage,
  fileFilter,
  limits: { fileSize: 10 * 1024 * 1024 }
});

module.exports = upload;
