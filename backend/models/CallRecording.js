const mongoose = require('mongoose');

const CallRecordingSchema = new mongoose.Schema(
  {
    roomId: {
      type: String,
      required: true,
      trim: true,
    },
    startedAt: Date,
    startedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
    },
    endedAt: Date,
    endedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
    },
    duration: {
      type: Number, // milliseconds
      default: 0,
    },
    participants: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
      },
    ],
  },
  {
    timestamps: true,
  }
);

CallRecordingSchema.index({ roomId: 1 });
CallRecordingSchema.index({ startedBy: 1, createdAt: -1 });

module.exports = mongoose.model('CallRecording', CallRecordingSchema);
