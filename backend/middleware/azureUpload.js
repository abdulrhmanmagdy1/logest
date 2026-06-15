/**
 * ============================================
 * Azure Blob Storage Upload Middleware
 * Replaces Multer disk storage with memory buffer → Azure Blob
 *
 * Required env vars:
 *   AZURE_STORAGE_CONNECTION_STRING — Azure Storage account connection string
 *   AZURE_STORAGE_CONTAINER_NAME    — Blob container name (default: "uploads")
 * ============================================
 */

const multer = require('multer');
const { BlobServiceClient } = require('@azure/storage-blob');
const path = require('path');
const logger = require('../utils/logger');

const ALLOWED_MIME_TYPES = new Set([
  'image/jpeg',
  'image/png',
  'image/gif',
  'image/webp',
  'application/pdf',
]);

const MAX_FILE_SIZE = parseInt(process.env.MAX_FILE_SIZE, 10) || 5 * 1024 * 1024; // 5 MB

// ─── Multer: memory storage (no local disk writes) ───────────────────────────
const fileFilter = (req, file, cb) => {
  if (ALLOWED_MIME_TYPES.has(file.mimetype)) {
    cb(null, true);
  } else {
    cb(new Error(`Invalid file type "${file.mimetype}". Allowed: JPEG, PNG, GIF, WEBP, PDF.`), false);
  }
};

const upload = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: MAX_FILE_SIZE },
  fileFilter,
});

// ─── Azure Blob client (lazy singleton) ──────────────────────────────────────
let _blobServiceClient = null;

function getBlobServiceClient() {
  if (_blobServiceClient) return _blobServiceClient;

  const connStr = process.env.AZURE_STORAGE_CONNECTION_STRING;
  if (!connStr) {
    throw new Error('AZURE_STORAGE_CONNECTION_STRING is not set. Configure it in Azure App Settings.');
  }
  _blobServiceClient = BlobServiceClient.fromConnectionString(connStr);
  return _blobServiceClient;
}

// ─── Upload a single buffer to Azure Blob Storage ────────────────────────────
async function uploadToAzureBlob(fileBuffer, originalName, mimetype) {
  const containerName = process.env.AZURE_STORAGE_CONTAINER_NAME || 'uploads';
  const ext = path.extname(originalName).toLowerCase();
  const blobName = `${Date.now()}-${Math.random().toString(36).slice(2, 9)}${ext}`;

  const containerClient = getBlobServiceClient().getContainerClient(containerName);

  // Create container if it doesn't exist (idempotent)
  await containerClient.createIfNotExists({ access: 'blob' });

  const blockBlobClient = containerClient.getBlockBlobClient(blobName);

  await blockBlobClient.uploadData(fileBuffer, {
    blobHTTPHeaders: { blobContentType: mimetype },
  });

  return {
    blobName,
    url: blockBlobClient.url,
    size: fileBuffer.length,
    mimetype,
  };
}

// ─── Express middleware: multer + auto-upload to Azure ────────────────────────
/**
 * Returns a middleware stack that:
 *  1. Parses the multipart body into memory buffers via multer
 *  2. Uploads the buffer(s) to Azure Blob Storage
 *  3. Attaches upload result(s) to req.azureFiles / req.azureFile
 *
 * Usage:
 *   router.post('/image', azureUpload.single('image'), handler)
 *   router.post('/docs',  azureUpload.array('files', 10), handler)
 */
const azureUpload = {
  single(fieldName) {
    const multerMiddleware = upload.single(fieldName);
    return [
      multerMiddleware,
      async (req, res, next) => {
        if (!req.file) return next();
        try {
          req.azureFile = await uploadToAzureBlob(req.file.buffer, req.file.originalname, req.file.mimetype);
          next();
        } catch (err) {
          logger.error('Azure Blob upload error:', err.message);
          next(err);
        }
      },
    ];
  },

  array(fieldName, maxCount = 10) {
    const multerMiddleware = upload.array(fieldName, maxCount);
    return [
      multerMiddleware,
      async (req, res, next) => {
        if (!req.files || req.files.length === 0) return next();
        try {
          req.azureFiles = await Promise.all(
            req.files.map((f) => uploadToAzureBlob(f.buffer, f.originalname, f.mimetype))
          );
          next();
        } catch (err) {
          logger.error('Azure Blob multi-upload error:', err.message);
          next(err);
        }
      },
    ];
  },
};

module.exports = { azureUpload, uploadToAzureBlob };
