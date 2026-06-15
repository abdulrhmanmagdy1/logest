const mongoose = require('mongoose');

const BackupLogSchema = new mongoose.Schema(
  {
    name: {
      type: String,
      trim: true,
    },
    type: {
      type: String,
      enum: ['full', 'incremental', 'differential'],
      default: 'full',
    },
    startedAt: Date,
    completedAt: Date,
    size: {
      type: Number, // bytes
      default: 0,
    },
    path: {
      type: String,
      trim: true,
    },
    s3Key: {
      type: String,
      trim: true,
    },
    components: [
      {
        type: { type: String },
        status: String,
        size: Number,
        path: String,
        error: String,
      },
    ],
  },
  {
    timestamps: true,
  }
);

BackupLogSchema.index({ createdAt: -1 });
BackupLogSchema.index({ type: 1, createdAt: -1 });

module.exports = mongoose.model('BackupLog', BackupLogSchema);
